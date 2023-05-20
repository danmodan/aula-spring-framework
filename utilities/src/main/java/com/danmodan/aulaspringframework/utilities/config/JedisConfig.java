package com.danmodan.aulaspringframework.utilities.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import redis.clients.jedis.JedisPooled;

@Profile("jedis")
@Configuration
public class JedisConfig {

    @Bean
    public JedisPooled jedisClient(
        @Value("${jedis.hostname}") String hostname, 
        @Value("${jedis.port}") int port
    ) {

        return new JedisPooled(hostname, port);
    }
}
