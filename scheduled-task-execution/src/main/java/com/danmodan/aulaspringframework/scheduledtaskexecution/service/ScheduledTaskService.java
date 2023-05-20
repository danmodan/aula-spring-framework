package com.danmodan.aulaspringframework.scheduledtaskexecution.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.danmodan.aulaspringframework.utilities.Util;

@Service
public class ScheduledTaskService {

    public static final int DELAY = 2_000;

    public int process(long millis) {
        Util.log("👉 fazendo tarefa");
        Util.sleep(millis);
        return new Random().nextInt(5) + 1;
    }

    @Scheduled(
        cron = "*/1 * * * * *", // cron = Scheduled.CRON_DISABLED
        zone = "America/Sao_Paulo")
    public void processEvery1Sec() {
        var result = process(DELAY);
        Util.log("✅ resultado processEvery1Sec ⏰ = " + result);
    }

    @Scheduled(
        initialDelay = 1, 
        fixedRate = 1, 
        timeUnit = TimeUnit.SECONDS)
    public void processAt1SecRate() {
        var result = process(DELAY);
        Util.log("✅ resultado processAt1SecRate ⏰ = " + result);
    }

    @Scheduled(
        fixedDelay = 1, 
        timeUnit = TimeUnit.SECONDS)
    public void processAt1SecDelay() {
        var result = process(DELAY);
        Util.log("✅ resultado processAt1SecDelay ⏰ = " + result);
    }
}