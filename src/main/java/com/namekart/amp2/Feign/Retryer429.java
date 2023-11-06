package com.namekart.amp2.Feign;

import feign.RetryableException;
import feign.Retryer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

@Primary
@Component
public class Retryer429 implements Retryer {

    Logger logger= Logger.getLogger("Retryer 429");


    private int attempt = 1;


    /*public Retryer429(int retryMaxAttempt, Long retryInterval) {
        this.retryMaxAttempt = retryMaxAttempt;
        this.retryInterval = retryInterval;
    }*/

    public Retryer429() {
    }

    @Override
    public void continueOrPropagate(RetryableException e) {

        Date now= new Date();

        Date date= e.retryAfter();
        Long retryInterval;
        if(date!=null)
             retryInterval= date.getTime()- now.getTime()+1000l;
        else
            retryInterval= 30000l;
        logger.info("Retrying after "+retryInterval);
        if(attempt++ == 3){
            throw e;
        }
        try {
            Thread.sleep(retryInterval);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

    }

    @Override
    public Retryer clone() {
        return new Retryer429();
    }
}
