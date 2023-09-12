package com.namekart.amp2.Feign;

import feign.RetryableException;
import feign.Retryer;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.logging.Logger;

@Component
public class NcRetryer implements Retryer {

    Logger logger= Logger.getLogger("Retryer Nc");

    private int attempt = 1;

    public NcRetryer() {
    }

    @Override
    public void continueOrPropagate(RetryableException e)
    {
        Date now= new Date();

        Date date= e.retryAfter();
        Long retryInterval;
        if(date!=null)
             retryInterval= date.getTime()- now.getTime()+1000l;
        else
            retryInterval= 1000l;
        logger.info("Retrying after "+retryInterval);
        if(attempt++ == 4)
        {
            throw e;
        }
        try
        {
            Thread.sleep(retryInterval);
        }
        catch(InterruptedException ignored)
        {
            Thread.currentThread().interrupt();
        }
        logger.info("Re-performing action "+(attempt-1)+" time.");

    }

    @Override
    public Retryer clone()
    {
        return new NcRetryer();
    }
}
