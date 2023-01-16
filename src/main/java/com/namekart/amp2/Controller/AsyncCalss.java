package com.namekart.amp2.Controller;

import com.namekart.amp2.Entity.Notification;
import com.namekart.amp2.GoDaddyEntities.Closeoutdb;
import com.namekart.amp2.GoDaddyEntities.PlaceBid;
import com.namekart.amp2.GoDaddySoapClient;
import com.namekart.amp2.Repository.Closeoutrepo;
import com.namekart.amp2.Repository.NotifRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

@Component
public class AsyncCalss {
    @Autowired
    GoDaddySoapClient goDaddySoapClient;

    @Autowired
    Closeoutrepo closeoutrepo;

    @Autowired
    NotifRepo notifRepo;
    Logger logger= Logger.getLogger("Async Class GoDaddy");
    @Async
    public void try2()
    {
        logger.info(String.valueOf(System.currentTimeMillis()));
        try{
            Thread.sleep(2000);}
        catch(InterruptedException ie)
        {logger.info(ie.getMessage());}
    }

    @Async
    public void cron(String domain, String price, ScheduledFuture scheduledFuture)
    {
        try {
            PlaceBid p = goDaddySoapClient.purchasecloseout(domain, price);
            if (p.getIsValid().equals("True")) ;
            {
                scheduledFuture.cancel(false);
                logger.info("GoDaddy: Scheduled Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price);
                notifRepo.save(new Notification("GoDaddy: Scheduled Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price));
                Closeoutdb db= closeoutrepo.findByDomain(domain);
                db.setStatus("Bought");
                closeoutrepo.save(db);
            }
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }

    }
}
