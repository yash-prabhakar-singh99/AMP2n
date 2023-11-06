package com.namekart.amp2.Feign;

import com.namekart.amp2.Controller.AllController;
import com.namekart.amp2.Status;
import feign.RetryableException;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

@Component
public class BidRetryerUnreliable implements Retryer {

    Logger logger= Logger.getLogger("Bid Retryer Unreliable");

    private int attempt = 1;

    private long message_id=0l;

    ConcurrentMap<String, Status> taskmap;

    AllController controller;

    Telegram telegram;
    public BidRetryerUnreliable(AllController controller, Telegram telegram) {
        this.controller=controller;
        this.telegram=telegram;
        this.taskmap=controller.getTaskmap();
    }

    @Override
    public void continueOrPropagate(RetryableException e)
    {
        Long chat_id=-930742733l;
        Date now= null;

        Date date= e.retryAfter();
        Long retryInterval;
        if(date!=null)
             retryInterval= date.getTime()- now.getTime()+1000l;
        else
            retryInterval= 15000l;
        logger.info("Retrying after "+retryInterval);
        if(attempt++ == 3)
        {
            String url= e.request().url().split("\\?")[0];
            String platform="",action="",method="",domain="";
            if(url.contains("https://aftermarketapi.namecheap.com/client/api"))
            {
                domain=e.request().requestTemplate().queries().get("name").toArray()[0]+"";
                platform="Namecheap";
                method=taskmap.get(domain.toLowerCase()).getFutureTask();
                if(url.contains("bids"))
                {
                    action="bid";
                }
                else action="fetch";
            }
            logger.info("API failure in while performing "+action+" in method "+method+" of domain "+domain);
            telegram.sendAlert(chat_id,platform+": API failure in while performing "+action+" in method "+method+" of domain "+domain);
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
        return new BidRetryerUnreliable(controller,telegram);
    }
}
