package com.example.seckill.utils;

import com.example.seckill.pojo.User;
import com.example.seckill.vo.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 * 生成用户工具类
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/26 10:49
 */
public class UserUtil {
    public static void main(String[] args) throws Exception {
        createUser(500);
    }
    public static void createUser(int count) throws Exception {
        List<User> userList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user" + i);
            user.setSalt("favourite");
            user.setPassword(MD5Util.inputToDBPass("123456", user.getSalt()));
            userList.add(user);
        }
        System.out.println("create user finish");
        Connection connection = getConnection();
        String sql = "insert into t_user(id, nickname, password, salt) values (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);//预编译sql语句
        for (int i = 0; i < count; i++) {
            User user = userList.get(i);
            ps.setLong(1, user.getId());
            ps.setString(2, user.getNickname());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getSalt());
            ps.addBatch(); //将一组参数添加到此PreparedStatement 对象的批处理命令中。
        }
        ps.executeBatch(); //执行批处理命令
        ps.clearParameters();
        ps.close();
        connection.close();
        System.out.println("insert to db");
        //登陆，生成userTicket
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("/Users/hourui/Desktop/config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(0); //写的指针为0
        for (int i = 0; i < count; i++) {
            User user = userList.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection(); //获取HTTP连接
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream os = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.passToServerPass("123456");
            os.write(params.getBytes(StandardCharsets.UTF_8));
            os.flush();
            InputStream is = co.getInputStream(); //请求的响应结果
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = is.read(buf)) != -1){
                bout.write(buf, 0, len);
            }
            os.close();
            is.close();
            bout.close();
            String response = new String(bout.toByteArray()); //返回一个respBean对象转化的字符串
            ObjectMapper objectMapper = new ObjectMapper();
            RespBean respBean = objectMapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length()); //将文件指针指向文件最后，实现追加
            raf.write(row.getBytes(StandardCharsets.UTF_8));
            raf.write("\r\n".getBytes(StandardCharsets.UTF_8));
            System.out.println("write to file :" + user.getId());
        }
        raf.close();
        System.out.println("over");
    }

    private static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/seckill?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8";
        String username = "root";
        String password = "1021";
        String driverClassName = "com.mysql.cj.jdbc.Driver";
        Class.forName(driverClassName); //DriverManager.registerDriver(new Driver()); 加载这个类的同时会将驱动注册到DriverManager中
        return DriverManager.getConnection(url, username, password);
    }
}
