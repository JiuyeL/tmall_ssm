package com.how2java.tmall.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 */
public class JedisPoolUtil {
    private static JedisPool jedisPool;

    static {
        //读取配置文件
        InputStream is = JedisPoolUtil.class.getClassLoader().getResourceAsStream("jedis.properties");
        //创建properties文件
        Properties pro = new Properties();
        //关联文件
        try {
            pro.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取配置文件信息
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(Integer.parseInt(pro.getProperty("maxIdle")));
        config.setMaxTotal(Integer.parseInt(pro.getProperty("maxTotal")));

        //
        jedisPool = new JedisPool(config, (pro.getProperty("host")), Integer.parseInt(pro.getProperty("port")));
    }

    public static Jedis getJedis() {
        return jedisPool.getResource();
    }
}
