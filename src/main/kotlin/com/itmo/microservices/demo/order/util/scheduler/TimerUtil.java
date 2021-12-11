package com.itmo.microservices.demo.order.util.scheduler;

import org.quartz.*;

import java.util.Date;

public class TimerUtil {
    private TimerUtil() {}

    public static JobDetail buildJobDetail(Class<? extends Job> jobClass, final TimerInfo info) {
        final JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(jobClass.getSimpleName(), info);

        return JobBuilder
                .newJob(jobClass)
                .withIdentity(jobClass.getSimpleName())
                .setJobData(jobDataMap)
                .build();
    }

    public static Trigger buildTrigger(Class<? extends Job> jobClass, final TimerInfo info) {
        SimpleScheduleBuilder  builder = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(info.getRepeatInterval());

        if (info.isRunForever()) {
            builder = builder.repeatForever();
        } else {
            builder = builder.withRepeatCount(info.getTotalFireCount() - 1);
        }

        return TriggerBuilder
                .newTrigger()
                .withIdentity(jobClass.getSimpleName())
                .withSchedule(builder)
                .startAt(new Date(System.currentTimeMillis() + info.getInitialOffset()))
                .build();
    }
}
