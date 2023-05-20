package com.danmodan.aulaspringframework.scheduledtaskexecution.config;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import com.danmodan.aulaspringframework.scheduledtaskexecution.service.ScheduledTaskService;
import com.danmodan.aulaspringframework.utilities.Util;

import redis.clients.jedis.JedisPooled;

@EnableScheduling
@Configuration
public class ScheduledConfig implements SchedulingConfigurer {

    @Autowired
    private ScheduledTaskService service;

    @Autowired
    private JedisPooled jedisClient;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        /** Configurar TaskScheduler/Executor */

        // configExecutor(taskRegistrar);

        /** 
         * Adicionar/agenda tarefas
         * (org.springframework.scheduling.config.Task)
         * e seus respectivos gatilhos
         * (org.springframework.scheduling.Trigger)
         */

        // setupCronTasks(taskRegistrar);
        // setupFixedRateTasks(taskRegistrar);
        // setupFixedDelayTasks(taskRegistrar);
        // setupCustomTriggerTasks(taskRegistrar);
    }

    /** Configurar TaskScheduler/Executor */

    private void configExecutor(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar
            .setTaskScheduler(taskScheduler());

        // taskRegistrar
        //     .setScheduler(scheduledExecutor());
    }

    // @Bean
    public TaskScheduler taskScheduler() {

        // Default 1 Thread só
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(20);
        executor.setThreadNamePrefix("taskScheduler-");
        executor.setErrorHandler(t -> Util.log(String.format("❌ [ERROR] %s", t.getMessage())));
        executor.afterPropertiesSet();
        return executor;
    }

    // @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService scheduledExecutor() {
        return Executors.newScheduledThreadPool(20);
    }




    /** 
     * Adicionar/agendar tarefas de cadência cron
     * (org.springframework.scheduling.config.CronTask/TriggerTask)
     * e seus respectivos gatilhos
     * (org.springframework.scheduling.support.CronTrigger)
     */

    private void setupCronTasks(ScheduledTaskRegistrar taskRegistrar) {

        // ScheduledTaskRegistrar.CRON_DISABLED
        String cron = "*/1 * * * * *";

        taskRegistrar
            .addCronTask(new CronTask(service::processEvery1Sec, cron));

        taskRegistrar
            .addTriggerTask(
                new TriggerTask(
                    service::processEvery1Sec, 
                    new CronTrigger(cron, ZoneId.of("America/Sao_Paulo"))
                )
            );

        ScheduledTask scheduledCronTask = taskRegistrar
            .scheduleCronTask(new CronTask(service::processEvery1Sec, cron));

        scheduledCronTask = taskRegistrar
            .scheduleTriggerTask(
                new TriggerTask(
                    service::processEvery1Sec, 
                    new CronTrigger(cron, ZoneId.of("America/Sao_Paulo"))
                )
            );
    }




    /** 
     * Adicionar/agendar tarefas de cadência fixed rate
     * (org.springframework.scheduling.config.IntervalTask/FixedRateTask/TriggerTask)
     * e seus respectivos gatilhos
     * (org.springframework.scheduling.support.PeriodicTrigger)
     */

    private void setupFixedRateTasks(ScheduledTaskRegistrar taskRegistrar) {

        Duration oneSecondDuration = Duration.ofSeconds(1);

        taskRegistrar
            .addFixedRateTask(
                new IntervalTask(
                    service::processAt1SecRate, 
                    oneSecondDuration, 
                    oneSecondDuration
                )
            );

        PeriodicTrigger rateTrigger = new PeriodicTrigger(oneSecondDuration);
        rateTrigger.setFixedRate(true);
        rateTrigger.setInitialDelay(oneSecondDuration);
        taskRegistrar
            .addTriggerTask(new TriggerTask(service::processAt1SecRate, rateTrigger));

        ScheduledTask fixedRateTask = taskRegistrar
            .scheduleFixedRateTask(
                new FixedRateTask(
                    service::processAt1SecRate, 
                    oneSecondDuration, 
                    oneSecondDuration
                )
            );

        taskRegistrar
            .addTriggerTask(
                new TriggerTask(
                    service::processAt1SecRate, 
                    (TriggerContext triggerContext) -> {

                        Instant lastActualExecution = triggerContext.lastActualExecution();

                        if(lastActualExecution == null) {
                            return triggerContext
                                .getClock()
                                .instant()
                                .plusSeconds(1); // initial delay
                        }

                        return lastActualExecution.plusSeconds(1);
                    }
                )
            );
    }




    /** 
     * Adicionar/agendar tarefas de cadência fixed delay
     * (org.springframework.scheduling.config.IntervalTask/FixedDelayTask)
     * e seus respectivos gatilhos
     * (org.springframework.scheduling.support.PeriodicTrigger)
     */

    private void setupFixedDelayTasks(ScheduledTaskRegistrar taskRegistrar) {

        Duration oneSecondDuration = Duration.ofSeconds(1);

        taskRegistrar
            .addFixedDelayTask(new IntervalTask(service::processAt1SecDelay, oneSecondDuration));

        PeriodicTrigger delayTrigger = new PeriodicTrigger(oneSecondDuration);
        delayTrigger.setFixedRate(false);
        taskRegistrar
            .addTriggerTask(new TriggerTask(service::processAt1SecDelay, delayTrigger));

        ScheduledTask fixedDelayTask = taskRegistrar
            .scheduleFixedDelayTask(
                new FixedDelayTask(
                    service::processAt1SecDelay, 
                    oneSecondDuration, 
                    Duration.ofSeconds(0)
                )
            );

        taskRegistrar
            .addTriggerTask(
                new TriggerTask(
                    service::processAt1SecDelay, 
                    (TriggerContext triggerContext) -> {

                        Instant lastCompletion = triggerContext.lastCompletion();

                        if(lastCompletion == null) {
                            return triggerContext
                                .getClock()
                                .instant();
                        }

                        return lastCompletion.plusSeconds(1);
                    }
                )
            );
    }




    /** 
     * Adicionar/agendar tarefas de cadência customizada
     * (org.springframework.scheduling.config.TriggerTask)
     * e seus respectivos gatilhos customizados
     * (org.springframework.scheduling.Trigger)
     */

    private void setupCustomTriggerTasks(ScheduledTaskRegistrar taskRegistrar) {

        Duration oneSecondDuration = Duration.ofSeconds(1);

        FlexDelayTrigger flexDelayTrigger = new FlexDelayTrigger(oneSecondDuration);

        taskRegistrar
            .addTriggerTask(
                new TriggerTask(
                    () -> {

                        // obtem resultado anterior

                        Integer lastResult = flexDelayTrigger.getLastResult();
                        int millis = lastResult == null ? ScheduledTaskService.DELAY : lastResult;

                        // execução da tarefa

                        int result = service.process(millis);
                        Util.log("✅ resultado flexPeriodicTrigger ⏰ = " + result);

                        // guarda resultado atual

                        flexDelayTrigger.setLastResult(result * 1000); // segundos
                        flexDelayTrigger.incrementExecutionCounter();
                    },
                    flexDelayTrigger
                )
            );

        // LockCustomTrigger lockCustomTrigger = new LockCustomTrigger(
        //         "*/3 * * * * *", 
        //         ZoneId.of("America/Sao_Paulo"),
        //         jedisClient);

        // taskRegistrar
        //     .addTriggerTask(
        //         new TriggerTask(
        //             () -> {

        //                 try {

        //                     // acquire lock

        //                     if(!lockCustomTrigger.tryLock(10)) {
        //                         return;
        //                     }

        //                     // execução da tarefa

        //                     int result = service.process(5_000);
        //                     Util.log("✅ resultado lockCustomTrigger ⏰ = " + result);

        //                 } finally {

        //                     // release lock

        //                     lockCustomTrigger.unlock();
        //                 }
        //             },
        //             lockCustomTrigger
        //         )
        //     );
    }
}
