package com.namekart.amp2;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ComponentScan()
public class ThreadpoolTaskconfig {
    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(22);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        threadPoolTaskScheduler.setBeanName("threadPoolTaskScheduler");
        //threadPoolTaskScheduler.
        threadPoolTaskScheduler.setRemoveOnCancelPolicy(true);
        //threadPoolTaskScheduler.scheduleAtFixedRate(new yoi(),3000);
        return threadPoolTaskScheduler;
    }

    @Bean(name = "workStealingPool")
    public ForkJoinPool threadPoolExecutor() {

        ForkJoinPool threadPoolExecutor = (ForkJoinPool) Executors.newWorkStealingPool(10);
        return threadPoolExecutor;

    }

//    @Bean(name = "taskExecutor")
//    public ThreadPoolTaskExecutor threadPoolTaskExecutor()
//    {
//        ThreadPoolTaskExecutor threadPoolTaskExecutor= new ThreadPoolTaskExecutor();
//        threadPoolTaskExecutor.setCorePoolSize(7);
//        return threadPoolTaskExecutor;
//    }

}
