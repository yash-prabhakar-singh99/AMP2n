package com.namekart.amp2.Controller;

import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.Entity.Notification;
import com.namekart.amp2.Feign.GoDaddyFeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.GoDaddyEntities.*;
import com.namekart.amp2.GoDaddySoapClient;
import com.namekart.amp2.Repository.Closeoutrepo;
import com.namekart.amp2.Repository.MyRepo;
import com.namekart.amp2.Repository.NotifRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

@Component
public class AsyncCalss {
    @Autowired
    GoDaddySoapClient goDaddySoapClient;

    String Authorization= "sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm";

    @Autowired
    Telegram telegram;
    @Autowired
    Closeoutrepo closeoutrepo;

    @Autowired
    MyRepo repo;
    @Autowired
    NotifRepo notifRepo;

    @Autowired
    GoDaddyFeign goDaddyFeign;

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    SimpleDateFormat timeft ;
    TimeZone ist ;

    String a="yoyo";

    public AsyncCalss()
    {
        timeft = new SimpleDateFormat("dd/MM HH:mm");
        ist= TimeZone.getTimeZone("IST");
        timeft.setTimeZone(ist);
    }

    Logger logger= Logger.getLogger("Async Class");
    @Async
    public void try2()
    {

        Long l= System.currentTimeMillis();
        Object obj= new Object();

        logger.info(""+obj.hashCode());
        /*try{
            Thread.sleep(2000);}
        catch(InterruptedException ie)
        {logger.info(ie.getMessage());}*/
    }

    @Async
    void getGDVs(@RequestBody List<Long> ids)
    {
        int n= ids.size();
        for(int i=0;i<n;i++)
        {
            DBdetails db= repo.findById(ids.get(i)).get();
            String domain= db.getDomain();
            try
            {
                GDAppraisalResp resp= goDaddyFeign.getGDV(Authorization,domain);
                db.setGdv(resp.getGovalue());
                repo.save(db);
                //logger.info(""+(i+1));
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
        }
    }

    @Async
    void getGDV( Long id)
    {

            DBdetails db= repo.findById(id).get();
            String domain= db.getDomain();
            try
            {
                GDAppraisalResp resp= goDaddyFeign.getGDV(Authorization,domain);
                db.setGdv(resp.getGovalue());
                repo.save(db);
                //logger.info(""+(i+1));
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }

    }

    void getGDVSync( Long id)
    {

        DBdetails db= repo.findById(id).get();
        String domain= db.getDomain();
        try
        {
            GDAppraisalResp resp= goDaddyFeign.getGDV(Authorization,domain);
            db.setGdv(resp.getGovalue());
            repo.save(db);
            //logger.info(""+(i+1));
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }

    }

    void getGDVSync( DBdetails db)
    {


        String domain= db.getDomain();
        try
        {
            GDAppraisalResp resp= goDaddyFeign.getGDV(Authorization,domain);
            db.setGdv(resp.getGovalue());
            repo.save(db);
            //logger.info(""+(i+1));
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }

    }

    @Async
    void getGDVV(String domain)
    {


        try
        {
            GDAppraisalResp resp= goDaddyFeign.getGDV(Authorization,domain);
            logger.info(""+resp.getDomain()+" "+resp.getGovalue());
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }

    }
int i=0;
    @Async
    public void cron1(String domain, String price, ScheduledFuture scheduledFuture, ScheduledFuture scheduledFuture1)
    {
        try {
            i++;
            logger.info(""+i);
            EstimateCloseoutPrice est= goDaddySoapClient.estimateCloseoutPrice(domain);
            if(est.getResult().equals("Success")) {
                String key= est.getCloseoutDomainPriceKey();
                InstantPurchaseCloseout p = null;

                p = goDaddySoapClient.instantPurchaseCloseout(domain,key);


                if (p.getResult().equals("Success"))
                {
                    Date now= new Date();
                    String time= timeft.format(now);
                    logger.info("GoDaddy: Scheduled Closeout CHECKED OUT from cart for domain: " + domain + " at price: " + price);
                    notifRepo.save(new Notification("GoDaddy",time,"Scheduled Closeout CHECKED OUT from cart for domain: " + domain + " at price: " + price));
                    telegram.sendAlert(-1001763199668l,842l, "GoDaddy: Scheduled Closeout CHECKED OUT from cart for domain: " + domain + " at price: " + price);
                    Closeoutdb db = closeoutrepo.findByDomain(domain);
                    db.setCurrPrice(price);
                    db.setStatus("Bought");
                    closeoutrepo.save(db);
                    scheduledFuture.cancel(true);
                    scheduledFuture1.cancel(true);
                    i=0;
                }

            }
            else
            {
                if(est.getMessage().equals("ERROR"))
                {
                    Date now= new Date();
                    String time= timeft.format(now);
                    logger.info("GoDaddy: Scheduled Closeout NOT CHECKED OUT from cart for domain: " + domain + " at price: " + price+" because of ERROR, most probably you're OUT OF FUND");
                    notifRepo.save(new Notification("GoDaddy",time,"Scheduled Closeout NOT CHECKED OUT from cart for domain: " + domain + " at price: " + price+" because of ERROR, most probably you're OUT OF FUND"));
                    telegram.sendAlert(-930742733l, "GoDaddy: Scheduled Closeout NOT CHECKED OUT from cart for domain: " + domain + " at price: " + price+" because of ERROR, most probably you're OUT OF FUND");
                    Closeoutdb db = closeoutrepo.findByDomain(domain);
                    db.setStatus("ERROR");
                    closeoutrepo.save(db);
                    scheduledFuture.cancel(true);
                    scheduledFuture1.cancel(true);
                    i=0;
                }
            }

        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }

    }

    class CheckOut implements Runnable
    {
        String domain,price;
        ScheduledFuture scheduledFuture;ScheduledFuture scheduledFuture1;

        public void setScheduledFuture(ScheduledFuture scheduledFuture, ScheduledFuture scheduledFuture1) {
            this.scheduledFuture = scheduledFuture;
            this.scheduledFuture1=scheduledFuture1;
        }

        public CheckOut(String domain, String price) {
            this.domain = domain;
            this.price = price;
        }

        @Override
        public void run() {
            cron1(domain,price,scheduledFuture,scheduledFuture1);
        }
    }

    @Async
    public void cron(String domain, String price, ScheduledFuture scheduledFuture,ScheduledFuture scheduledFuture1)
    {
        try {
            PlaceBid p=null;
            //logger.info("1");
            p = goDaddySoapClient.purchasecloseout(domain, price);

            if (p.getIsValid().equals("True"))
            {
                Date now= new Date();
                String time= timeft.format(now);
                logger.info("GoDaddy: Scheduled Closeout SUCCESSFULLY SNIPED for domain: " + domain + " at price: " + price+" and is in the cart, Will be automatically checked out after 2 hours");
                notifRepo.save(new Notification("GoDaddy",time,"Scheduled Closeout SUCCESSFULLY SNIPED for domain: " + domain + " at price: " + price+" and is in the cart, Will be automatically checked out after 2 hours"));
                telegram.sendAlert(-1001763199668l,842l, "GoDaddy: Scheduled Closeout SUCCESSFULLY SNIPED for domain: " + domain + " at price: " + price+" and is in the cart, Will be automatically checked out after 2 hours");
                Closeoutdb db = closeoutrepo.findByDomain(domain);
                db.setCurrPrice(price);
                db.setStatus("In Cart");
                closeoutrepo.save(db);
                now.setMinutes(now.getMinutes()+120);
                CheckOut checkOut= new CheckOut(domain,price);

                ScheduledFuture s=taskScheduler.scheduleAtFixedRate(checkOut,now,30000);
                now.setMinutes(now.getMinutes()+20);
                ScheduledFuture s1=taskScheduler.schedule(new CancelCron1(s,domain),now);
                checkOut.setScheduledFuture(s,s1);
                scheduledFuture.cancel(true);
                scheduledFuture1.cancel(true);

            }

        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }

    }

    class CancelCron1 implements Runnable
    {
        ScheduledFuture scheduledFuture;

        String domain;
        public CancelCron1(ScheduledFuture scheduledFuture, String domain) {
            this.scheduledFuture = scheduledFuture;
            this.domain=domain;
        }

        @Override
        public void run() {
            if((!scheduledFuture.isDone())||(!scheduledFuture.isCancelled()))
                scheduledFuture.cancel(true);

            Date now= new Date();
            String time= timeft.format(now);
            logger.info("GoDaddy: Scheduled Closeout NOT CHECKED OUT from cart for domain: " + domain + " after trying for 20 mins, Someone else may have sniped it.");
            notifRepo.save(new Notification("GoDaddy",time,"Scheduled Closeout NOT CHECKED OUT from cart for domain: " + domain +  " after trying for 20 mins, Someone else may have sniped it."));
            telegram.sendAlert(-930742733l,"GoDaddy: Scheduled Closeout NOT CHECKED OUT from cart for domain: " + domain + " after trying for 20 mins, Someone else may have sniped it.");
            Closeoutdb db = closeoutrepo.findByDomain(domain);
            db.setStatus("Not Checked Out");
            closeoutrepo.save(db);
            i=0;
        }
    }

    @Async
    void dynaNotFetchedNotif(Set<String> set)
    {
        for(String domain: set)
        {

            Date now= new Date();
            String time= timeft.format(now);
            notifRepo.save(new Notification("Dynadot",time,"Domain details NOT FETCHED for " + domain ));
            logger.info(time+": Domain details NOT FETCHED for " + domain);
        }
    }

}
