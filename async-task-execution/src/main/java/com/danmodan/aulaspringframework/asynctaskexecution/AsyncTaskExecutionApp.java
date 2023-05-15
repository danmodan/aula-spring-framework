package com.danmodan.aulaspringframework.asynctaskexecution;

import com.danmodan.aulaspringframework.utilities.Util;
import com.danmodan.aulaspringframework.asynctaskexecution.service.AsyncTaskService;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class AsyncTaskExecutionApp {

    public static void main(String[] args) throws Exception {

        var context = new AnnotationConfigApplicationContext(AsyncTaskExecutionApp.class);
        context.registerShutdownHook();

        AsyncTaskService service = context.getBean(AsyncTaskService.class);

        long millis = 2_000;

        for (int i = 0; i < 3; i++) {

            service.processAsync(millis);
            service.processFailedAsync(millis);
            service.processAsyncInSpecificExecutor(millis);
        }

        // Util.log("✅ resultado = " + service.processAsyncAndGet(millis).get());

        Util.log("🏁 Thread 'main' terminou");
    }
}
