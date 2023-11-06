package com.namekart.amp2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.FilterChainProxy;

import javax.servlet.Filter;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableAsync
public class Amp2Application {
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext=SpringApplication.run(Amp2Application.class, args);
       // displayAllBeans();
       /* FilterChainProxy filterChainProxy = (FilterChainProxy) applicationContext.getBean("filterChainProxy");

        List<Filter> filters = filterChainProxy.getFilters();

        Filter lastFilter = filters.get(filters.size() - 1);*/
    }

    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
           if(beanName.contains("Security")||beanName.contains("Decode"))
            System.out.println(beanName);
        }
    }
}
