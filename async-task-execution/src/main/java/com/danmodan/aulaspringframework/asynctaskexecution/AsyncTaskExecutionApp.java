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

            service.processAsyncInSpecificExecutor(millis);
            service.processAsync(millis);
            service.processFailedAsync(millis);
        }

        Util.log("✅ resultado = " + service.processAsyncAndGet(millis).get());

        service.processAsyncAndGet(millis)
            .thenAccept(result -> Util.log("✅ resultado = " + result));

        Util.log("🏁 Thread 'main' terminou");
    }
}
