package com.danmodan.aulaspringframework.asynctaskexecution.service;

import com.danmodan.aulaspringframework.utilities.Util;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskService {

    private int process(long millis) {
        Util.log("👉 fazendo tarefa");
        Util.sleep(millis);
        return new Random().nextInt(100) + 1;
    }

    @Async
    public void processAsync(long millis) {
        var result = process(millis);
        Util.log("✅ resultado processAsync 🔀 = " + result);
    }

    @Async("specificExecutor")
    public void processAsyncInSpecificExecutor(long millis) {
        var result = process(millis);
        Util.log("✅ resultado processAsyncInSpecificExecutor 🔀 = " + result);
    }

    @Async
    public CompletableFuture<Integer> processAsyncAndGet(long millis) {
        return CompletableFuture.completedFuture(process(millis));
    }

    @Async
    public void processFailedAsync(long millis) {
        process(millis);
        throw new RuntimeException("deu ruim!");
    }
}