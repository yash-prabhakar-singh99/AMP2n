package com.namekart.amp2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableAsync
public class Amp2Application {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext=SpringApplication.run(Amp2Application.class, args);
       // displayAllBeans();
    }

    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
           if(beanName.contains("Security")||beanName.contains("Decode"))
            System.out.println(beanName);
        }
    }
}
