package com.danmodan.aulaspringframework.scheduledtaskexecution;

import com.danmodan.aulaspringframework.utilities.Util;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class ScheduledTaskExecutionApp {

    public static void main(String[] args) throws Exception {

        var context = new AnnotationConfigApplicationContext(ScheduledTaskExecutionApp.class);
        context.registerShutdownHook();

        Util.log("🏁 Thread 'main' terminou");
    }
}
