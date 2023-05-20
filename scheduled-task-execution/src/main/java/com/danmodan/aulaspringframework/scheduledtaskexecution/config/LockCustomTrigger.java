package com.danmodan.aulaspringframework.scheduledtaskexecution.config;

import java.net.InetAddress;
import java.time.ZoneId;

import org.springframework.scheduling.support.CronTrigger;

import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.params.SetParams;

public class LockCustomTrigger extends CronTrigger {

    private final JedisPooled jedisClient;

    public LockCustomTrigger(
        String expression, 
        ZoneId zoneId, 
        JedisPooled jedisClient
    ) {

        super(expression, zoneId);
        this.jedisClient = jedisClient;
    }

    public void unlock() {

        try {

            jedisClient.eval(
                """
                    if redis.call(\"get\",KEYS[1]) == ARGV[1]
                    then
                        return redis.call(\"del\",KEYS[1])
                    else
                        return 0
                    end
                """,
                1,
                "custom-task-locked-by",
                InetAddress.getLocalHost().getHostName()
            );
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean tryLock(int expiration) {

        try {

            SetParams params = new SetParams();
            params.ex(expiration);
            params.nx();

            String lockResult = jedisClient
                .set(
                    "custom-task-locked-by", 
                    InetAddress.getLocalHost().getHostName(), 
                    params);

            return lockResult != null;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
