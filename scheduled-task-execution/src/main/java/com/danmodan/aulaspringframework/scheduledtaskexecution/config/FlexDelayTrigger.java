package com.danmodan.aulaspringframework.scheduledtaskexecution.config;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.PeriodicTrigger;

public class FlexDelayTrigger extends PeriodicTrigger {

    int counter;
    int lastResult;

    public FlexDelayTrigger(Duration period) {
        super(period);
        setFixedRate(false);
    }

    @Override
    public Instant nextExecution(TriggerContext triggerContext) {

        if(counter >= 4) {
            return null;
        }

        Instant nextExecution = super.nextExecution(triggerContext);

        if(lastResult < 3) {

            return nextExecution;
        }

        return nextExecution.plusSeconds(2);

        // Clock clock = triggerContext.getClock();
        // return clock
        //     .instant()
        //     .atZone(clock.getZone())
        //     .plusDays(1)
        //     .toInstant();
    }
}
