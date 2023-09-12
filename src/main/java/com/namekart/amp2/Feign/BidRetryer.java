package com.namekart.amp2.Feign;

import com.namekart.amp2.Controller.AllController;
import com.namekart.amp2.Status;
import feign.RetryableException;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

@Component
public class BidRetryer implements Retryer {

    Logger logger= Logger.getLogger("Bid Retryer");

    private int attempt = 1;

    private long message_id=0l;

    ConcurrentMap<String, Status> taskmap;

    AllController controller;
    public BidRetryer(AllController controller) {
        this.controller=controller;
        this.taskmap=controller.getTaskmap();
    }

    @Autowired
    Telegram telegram;
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
            retryInterval= 1000l;
        logger.info("Retrying after "+retryInterval);
        if(attempt++ == 4)
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
            else if(url.contains("https://api.dropcatch.com"))
        {
            domain=e.request().requestTemplate().queries().get("searchTerm").toArray()[0]+"";
            platform="Dropcatch";
            method=taskmap.get(domain.toLowerCase()).getFutureTask();
            if(url.contains("bids"))
            {
                action="bid";
            }
            else action="fetch";
        }
            else if(url.contains("https://api.dropcatch.com"))
            {
                domain=e.request().requestTemplate().queries().get("searchTerm").toArray()[0]+"";
                platform="Dropcatch";
                method=taskmap.get(domain.toLowerCase()).getFutureTask();
                if(url.contains("bids"))
                {
                    action="bid";
                }
                else action="fetch";
            }
            else if(url.contains("https://api.dynadot.com/api3.json"))
            {
                domain=e.request().requestTemplate().queries().get("domain").toArray()[0]+"";
                platform="Dynadot";
                method=taskmap.get(domain.toLowerCase()).getFutureTask();
                if(String.valueOf(e.request().requestTemplate().queries().get("domain").toArray()[0]).equals("get_auction_details"))
                {
                    action="Fetch";
                }
                else action="Bid";
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
        return new BidRetryer(controller);
    }
}
