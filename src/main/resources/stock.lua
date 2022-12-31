if (redis.call("exists", KEYS[1]) == 1) then
    --字符串不能跟数字进行比对,需要使用tonumber转为数字
    local stock = tonumber(redis.call("get", KEYS[1]));
    if (stock > 0) then
        redis.call("incrby", KEYS[1], -1);
        return stock;
    else
        return 0;
    end
else
    return -1;
end