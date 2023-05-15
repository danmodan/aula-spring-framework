package com.danmodan.aulaspringframework.scheduledtaskexecution.service;

import com.danmodan.aulaspringframework.utilities.Util;

import java.util.Random;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTaskService {

    private int process(long millis) {
        Util.log("👉 fazendo tarefa");
        Util.sleep(millis);
        return new Random().nextInt(100);
    }

    @Async
    public void processScheduled() {
        var result = process(2_000);
        Util.log("✅ resultado processScheduled 🔀 = " + result);
    }
}