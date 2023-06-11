package com.namekart.amp2.Controller;

import com.namekart.amp2.Entity.*;
import com.namekart.amp2.Feign.GoDaddyFeign;
import com.namekart.amp2.Feign.Namecheapfeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.NamecheapEntity.*;
import com.namekart.amp2.NamesiloEntities.SiloAuctionDetails;
import com.namekart.amp2.Repository.*;
import com.namekart.amp2.Status;
import com.namekart.amp2.TelegramEntities.InlineKeyboardButton;
import com.namekart.amp2.TelegramEntities.InlineKeyboardMarkup;
import com.namekart.amp2.TelegramEntities.SendMessage;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.StringUtils;
//import org.apache.commons.lang3.StringUtils;

import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class NamecheapController {

    String bearer = "Bearer ef7b03f63d8a94e2f083b991a74dd5852s5DuDtyOc9Ft1QZ5u0plxLpA0vlYdHFxEccAez6lh/wUyQNkOTCfqcOgrYMcvG4";

    @Autowired
    LiveMaprepo liveMaprepo;

    @Autowired
    AsyncCalss asyncCalss;
    @Autowired
    LiveNcRepo liveNcRepo;
    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    Telegram telegram;
    ScheduledFuture scheduledFuture=null;
    @Autowired
    Namecheapfeign namecheapfeign;

    @Autowired
    MyRepo repo;

    @Autowired
    Bidhisrepo bidhisrepo;

    @Autowired
    @Qualifier(value = "workStealingPool")
    ForkJoinPool threadPoolExecutor;

    @Autowired
    NotifRepo notifRepo;

    Boolean b= true;

    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
    TimeZone istTimeZone = TimeZone.getTimeZone("IST");

    SimpleDateFormat timeft = new SimpleDateFormat("dd/MM HH:mm");

    Map<String,String> map;
    public NamecheapController()
    {
        parser.setTimeZone(utcTimeZone);
        ft1.setTimeZone(istTimeZone);
        timeft.setTimeZone(istTimeZone);
        taskmap= new HashMap<>();
        map=new HashMap<>();
    }

    Logger logger =Logger.getLogger("Namecheap Yash");

    Map<String, Status> taskmap;

    SimpleDateFormat parser()
    {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        parser.setTimeZone(utcTimeZone);
        return parser;
    }

    SimpleDateFormat ft1()
    {
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        ft1.setTimeZone(istTimeZone);
        return ft1;
    }

    @Scheduled(cron = "0 15 08 ? * *", zone = "IST")
    void refreshTaskMap()
    {
        Iterator<Map.Entry<String, Status> >
                iterator = taskmap.entrySet().iterator();


        while (iterator.hasNext()) {

            Map.Entry<String,Status> entry = iterator.next();
            ScheduledFuture future= entry.getValue().getFuture();
            if(future.isCancelled()||future.isDone())
            {
                logger.info("Removing from taskmap, inactive scheduled domain:"+entry.getKey());
                iterator.remove();
            }

        }
    }
    void enterTaskMap(String domain, ScheduledFuture scheduledFuture, String futureTask)
    {
        domain=domain.toLowerCase();
        if(taskmap.containsKey(domain))
        {
            Status status=taskmap.get(domain);
            status.getFuture().cancel(true);
            status.setFuture(scheduledFuture);
            status.setFutureTask(futureTask);
        }
        else
        {
            Status status= new Status(scheduledFuture,futureTask);
            taskmap.put(domain,status);
        }
    }

    void updateTaskMap(String domain, ScheduledFuture scheduledFuture, String futureTask)
    {
        domain= domain.toLowerCase();
        if(taskmap.containsKey(domain)) {
            Status status = taskmap.get(domain);
            status.setFuture(scheduledFuture);
            status.setFutureTask(futureTask);
        }
    }
    void deleteTaskMap(String domain)
    {
        domain= domain.toLowerCase();
        if(taskmap.containsKey(domain)) {
            taskmap.get(domain).getFuture().cancel(false);
            taskmap.remove(domain);
        }
    }


   /* @PostMapping("/webhooknc")
    void webhookNC(@RequestBody NCEventWrapper eventWrapper)
    {
        NCEvent event= eventWrapper.getEvent();
        String type= event.getType();
        String domain= event.getData().getSale().getName();
        String id= event.getData().getSale().getId();
        DBdetails db= repo.findByNamecheapid(id);
        NCEventData data= event.getData();
        Date now= new Date();
        String time= timeft.format(now);
        if(type.equals("AUCTION_OUTBID"))
        {
            logger.info("AUCTION_OUTBID");
            db.setResult("Outbid");
            String endTime = event.getData().getSale().getEndDate();
            endTime = endTime.substring(0, endTime.length() - 5);
            Date d = null;
            String time_left="";
            try {
                d = parser.parse(endTime);
                time_left = relTime(d);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            String text = "Namecheap Auction OUTBID \n \n" + domain + "\n \nTime Left: " + time_left  + "\nCurrent Bid: " + data.getNextBid().getAmount() +"\nMin Next Bid: " + data.getSale().getMinBid() + "\nOur Max Bid: " + db.getBidAmount()  + " \nEST: " + db.getEstibot() + " \nGDV: " + db.getGdv() + " \n\nLink: " + "https://www.namecheap.com/market/" + domain;
            //-1001814695777L
            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
            row.add(new InlineKeyboardButton("Bid", "b nc " + id + " " + domain + " " + data.getNextBid().getAmount()));
            row.add(new InlineKeyboardButton("Watch", "w nc " + id + " " + domain));
            row.add(new InlineKeyboardButton("Track", "t nc " + id + " " + domain));
            List<List<InlineKeyboardButton>> rows= new ArrayList<>();
            rows.add(row);
            InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
            Object obj = telegram.sendKeyboard(new SendMessage(-1001763199668l
                    ,text,inlineKeyboardMarkup));

            notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+db.getBidAmount()+" OUTBID at price " + data.getNextBid().getAmount() ));
            logger.info(time+": Domain: "+domain+" with our max price "+db.getBidAmount()+" Outbid at price " + data.getNextBid().getAmount() );
            db.setResult("Outbid");
        }
        else if(type.equals("AUCTION_WINNER"))
        {
            logger.info("AUCTION_WINNER");
            telegram.sendAlert(-1001763199668l,"Namecheap: Yippee!! Won auction of "+domain+" at price: "+data.getSale().getPrice());
            notifRepo.save(new Notification("Namecheap",time,"Yippee!! Won auction of "+domain+" at price: "+data.getSale().getPrice()));
            logger.info(time+": Won auction of "+domain+" at price: "+data.getSale().getPrice());
            db.setResult("Won");

        }
        else if(type.equals("AUCTION_WINNER_RUNNERUP"))
        {
            logger.info("AUCTION_WINNER_RUNNERUP");
            telegram.sendAlert(-1001763199668l,"Namecheap: Hush!! Lost auction of "+domain+" at price: "+data.getSale().getPrice());
            notifRepo.save(new Notification("Namecheap",time,"Hush!! Lost auction of "+domain+" at price: "+data.getSale().getPrice()));
            logger.info(time+": Lost auction of "+domain+" at price: "+data.getSale().getPrice());
            db.setResult("Loss");

        }
        else if(type.equals("AUCTION_ENDED"))
        {
            logger.info("AUCTION_ENDED");
            logger.info("AUCTION_WINNER_RUNNERUP");
            telegram.sendAlert(-1001763199668l,"Namecheap: Hush!! Lost auction of "+domain+" at price: "+data.getSale().getPrice());
            notifRepo.save(new Notification("Namecheap",time,"Hush!! Lost auction of "+domain+" at price: "+data.getSale().getPrice()));
            logger.info(time+": Lost auction of "+domain+" at price: "+data.getSale().getPrice());
            db.setResult("Loss");

        }
        else if(type.equals("AUCTION_CLOSED"))
        {
            logger.info("AUCTION_CLOSED");
            logger.info("AUCTION_WINNER_RUNNERUP");
            telegram.sendAlert(-1001763199668l,"Namecheap: Hush!! Lost auction of "+domain+" at price: "+data.getSale().getPrice());
            notifRepo.save(new Notification("Namecheap",time,"Hush!! Lost auction of "+domain+" at price: "+data.getSale().getPrice()));
            logger.info(time+": Lost auction of "+domain+" at price: "+data.getSale().getPrice());
            db.setResult("Loss");


        }
        logger.info(data.getSale().getPrice()+" "+ data.getSale().getName());
        repo.save(db);


    }
*/
    @GetMapping("/nct")
    AuctionDetailNC nc()
    {
        AuctionDetailNC nc= namecheapfeign.getAuctionDetailbyId(bearer,"4hv4XEyQSiupYEJLnNErw");
        logger.info("yo");
        return nc;
    }

    CompletableFuture<Boolean> refreshScheduled()
    {
        List<DBdetails> list= repo.findByPlatformAndResultOrResultOrResultOrResult("Namecheap", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
        if(list==null||list.size()==0)
            return CompletableFuture.completedFuture(true);
        for(int i=0;i< list.size();i++)
        {
            DBdetails db= list.get(i);
            String ncid= db.getNamecheapid();
            String domain= db.getDomain();

            try {
                AuctionDetailNC nc = namecheapfeign.getAuctionDetailbyId(bearer, ncid);
                if (nc.getStatus().equals("active"))
                {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = null;
                    String endTimeist = "";
                    Integer est = nc.getEstibotValue();
                    String time_left;
                    try {
                        d = parser.parse(endTime);
                        time_left = relTime(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    endTimeist = ft1.format(d);
                    logger.info(endTimeist);
                    Float currbid = nc.getPrice();
                    if(currbid>Float.valueOf(db.getBidAmount()))
                        db.setResult("Outbid");
                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    //db.setEstibot(String.valueOf(nc.getEstibotValue()));
                    db.setBids(nc.getBidCount());
                    repo.save(db);
                } else {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = null;
                    String endTimeist = "";
                    Integer est = nc.getEstibotValue();
                    String time_left;
                    try {
                        d = parser.parse(endTime);
                        time_left = relTime(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    endTimeist = ft1.format(d);
                    logger.info(endTimeist);
                    Float currbid = nc.getPrice();
                    Date now= new Date();
                    String time=timeft.format(now);
                    notifRepo.save(new Notification("Namecheap",time,"Scheduled auction ended of domain: "+domain+" at price: "+currbid+" at time: "+endTimeist));
                    if(currbid<=Float.valueOf(db.getBidAmount()))
                    db.setResult("Won");
                    else
                        db.setResult("Loss");
                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    // db.setEstibot(String.valueOf(est));
                    db.setBids(nc.getBidCount());
                    repo.save(db);
                }
            }
            catch(FeignException e)
            {
                if(e.status()==400)
                {
                    db.setWatchlist(false);
                    repo.save(db);
                }
                logger.info(e.getMessage());
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
        }

        return CompletableFuture.completedFuture(true);
    }

    @Scheduled(fixedRate = 120000)
    void refreshncwatchlist()
    {
        List<DBdetails> list= repo.findByPlatformAndWatchlistIsTrueAndTrackIsFalse("Namecheap");
        if(!list.isEmpty())
        for(int i=0;i< list.size();i++)
        {
            DBdetails db= list.get(i);
            String ncid= db.getNamecheapid();
            String domain= db.getDomain();

            try {
                AuctionDetailNC nc = namecheapfeign.getAuctionDetailbyId(bearer, ncid);
                if (nc.getStatus().equals("active"))
                {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = null;
                    String endTimeist = "";
                    Integer est = nc.getEstibotValue();
                    String time_left;
                    try {
                        d = parser.parse(endTime);
                        time_left = relTime(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }

                    endTimeist = ft1.format(d);
                    logger.info(endTimeist);
                    float prevBid= Float.valueOf(db.getCurrbid());
                    float currbid = nc.getPrice();
                    Date now= new Date();
                    if(prevBid<currbid) {
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
                        row.add(new InlineKeyboardButton("Track", "t nc " + ncid + " " + domain));
                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                        String text = "Namecheap\n\n" + domain  + "\nNEW BID PLACED"+ "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + est + " \nGDV: " + db.getGdv()  +" \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                        try {
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }
                    int nw= db.getNw();
                    if(nw==0)
                    {
                        if(d.getTime()-now.getTime()>86400000)
                            nw=4;
                        else if(d.getTime()-now.getTime()>3600000)
                            nw=3;
                        else if(d.getTime()-now.getTime()>600000)
                            nw=2;
                        else if(d.getTime()-now.getTime()>240000)
                            nw=1;
                        db.setNw(nw);
                    }
                    if(d.getTime()-now.getTime()<86400002&&d.getTime()-now.getTime()>86280000&&nw>=4)
                    {
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
                        row.add(new InlineKeyboardButton("Track", "t nc " + ncid + " " + domain));
                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                        String text = "Namecheap\n\n" + domain +"\n<24 hrs LEFT"+ "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + est + " \nGDV: " + db.getGdv()  +" \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                        try {
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                        nw=3;
                        db.setNw(nw);
                    }
                    else if(d.getTime()-now.getTime()<3600002&&d.getTime()-now.getTime()>3480000&&nw>=3)
                    {
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
                        row.add(new InlineKeyboardButton("Track", "t nc " + ncid + " " + domain));
                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                        String text = "Namecheap\n\n" + domain+"\n<1 hr LEFT" + "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + est + " \nGDV: " + db.getGdv()  +" \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                        try {
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                        nw=2;
                        db.setNw(nw);
                    }

                    else if(d.getTime()-now.getTime()<600002&&d.getTime()-now.getTime()>480000&&nw>=2)
                    {
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
                        row.add(new InlineKeyboardButton("Track", "t nc " + ncid + " " + domain));
                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                        String text = "Namecheap\n\n" + domain +"\n<10 mins LEFT"+ "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + est + " \nGDV: " + db.getGdv()  +" \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                        try {
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                        nw=1;
                        db.setNw(nw);
                    }
                    else if(d.getTime()-now.getTime()<240002&&d.getTime()-now.getTime()>120000&&nw>=1)
                    {
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
                        row.add(new InlineKeyboardButton("Track", "t nc " + ncid + " " + domain));
                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                        String text = "Namecheap\n\n" + domain+"\n<4 mins LEFT" + "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + est + " \nGDV: " + db.getGdv()  +" \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                        try {
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                        nw=-1;
                        db.setNw(nw);
                    }

                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    db.setBids(nc.getBidCount());
                    repo.save(db);
                }
                else
                {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = null;
                    String endTimeist = "";
                    Integer est = nc.getEstibotValue();
                    String time_left;
                    try {
                        d = parser.parse(endTime);
                        time_left = relTime(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    endTimeist = ft1.format(d);
                    logger.info(endTimeist);
                    Float currbid = nc.getPrice();
                    Date now= new Date();
                    String time=timeft.format(now);
                    notifRepo.save(new Notification("Namecheap",time,"Watchlisted auction ended of domain: "+domain+" at price: "+currbid+" at time: "+endTimeist));
                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                   // db.setEstibot(String.valueOf(est));
                    db.setBids(nc.getBidCount());
                    db.setWatchlist(false);
                    db.setWasWatchlisted(true);
                    repo.save(db);
                }
            }
            catch(FeignException e)
            {
                if(e.status()==400)
                {
                    db.setWatchlist(false);
                    repo.save(db);
                }
                logger.info(e.getMessage());
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
        }
        //scheduled

        //List<DBdetails> slist= repo.findByPlatformAndResultOrResultOrResultOrResult("Namecheap", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
        List<DBdetails> slist= repo.findScheduledNC();


        if(slist==null||slist.size()==0)
            return;


        for(int i=0;i< slist.size();i++)
        {
            DBdetails db= slist.get(i);
            String ncid= db.getNamecheapid();
            String domain= db.getDomain();

            try {
                AuctionDetailNC nc = namecheapfeign.getAuctionDetailbyId(bearer, ncid);
                if (nc.getStatus().equals("active"))
                {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = null;
                    String endTimeist = "";
                    Integer est = nc.getEstibotValue();
                    String time_left;
                    try {
                        d = parser.parse(endTime);
                        time_left = relTime(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }

                    endTimeist = ft1.format(d);
                    logger.info(endTimeist);
                    float currbid = nc.getPrice();
                    if(currbid>Float.valueOf(db.getBidAmount())&&(!db.getResult().equals("Outbid")))
                    {
                        String text = "Namecheap Auction OUTBID \n \n" + domain + "\n \nTime Left: " + time_left  + "\nCurrent Bid: " + nc.getPrice() +"\nMin Next Bid: " + nc.getMinBid() + "\nOur Max Bid: " + db.getBidAmount()  + " \nEST: " + db.getEstibot() + " \nGDV: " + db.getGdv() + " \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();

                        row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + nc.getPrice()));
                        row.add(new InlineKeyboardButton("Watch", "w nc " + ncid + " " + domain));
                        row.add(new InlineKeyboardButton("Track", "t nc " + ncid + " " + domain));
                        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                        Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                                ,text,inlineKeyboardMarkup));
                        Date now= new Date();
                        String time= timeft.format(now);

                        notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+db.getBidAmount()+" OUTBID at price " + nc.getPrice() ));
                        logger.info(time+": Domain: "+domain+" with our max price "+db.getBidAmount()+" Outbid at price " + nc.getPrice() );
                        db.setResult("Outbid");}
                    else if(b&&nc.getMinBid()<=Float.valueOf(db.getBidAmount()))
                    {
                        Date now = new Date();
                        if (d.getTime() - now.getTime() < 300000) {
                            d.setSeconds(d.getSeconds() - 10);
                            ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid,Float.valueOf(db.getBidAmount()) , domain, endTime), d);
                            enterTaskMap(domain, task, "pb");

                        } else {
                            //d.setMinutes(d.getMinutes()-4);
                            Date d1 = new Date(d.getTime() - 270000);
                            ScheduledFuture task = taskScheduler.schedule(new PreCheck(ncid, domain, Float.valueOf(db.getBidAmount())), d1);
                            enterTaskMap(domain, task, "pc");

                        }
                    }
                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    db.setBids(nc.getBidCount());
                    repo.save(db);
                } else {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = null;
                    String endTimeist = "";
                    Integer est = nc.getEstibotValue();
                    String time_left;
                    try {
                        d = parser.parse(endTime);
                        time_left = relTime(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    endTimeist = ft1.format(d);
                    logger.info(endTimeist);
                    Float currbid = nc.getPrice();
                    Date now= new Date();
                    String time=timeft.format(now);
                    //notifRepo.save(new Notification("Namecheap",time,"Scheduled auction ended of domain: "+domain+" at price: "+currbid+" at time: "+endTimeist));
                    if(currbid.equals(db.getMyLastBid())) {
                        telegram.sendAlert(-1001763199668l,842l, "Namecheap: Yippee!! Won auction of "+domain+" at price: "+currbid);
                        notifRepo.save(new Notification("Namecheap",time,"Yippee!! Won auction of "+domain+" at price: "+currbid));
                        logger.info(time+": Won auction of "+domain+" at price: "+currbid);
                        deleteTaskMap(domain);
                        db.setResult("Won");
                        repo.save(db);

                    }
                    else
                    {
                        telegram.sendAlert(-1001763199668l,841l, "Namecheap: Hush!! Lost auction of "+domain+" at price: "+currbid);
                        notifRepo.save(new Notification("Namecheap",time,"Hush!! Lost auction of "+domain+" at price: "+currbid));
                        logger.info(time+": Lost auction of "+domain+" at price: "+currbid);
                        deleteTaskMap(domain);
                        db.setResult("Loss");
                        repo.save(db);
                    }
                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    // db.setEstibot(String.valueOf(est));
                    db.setBids(nc.getBidCount());
                    repo.save(db);

                }
            }
            catch(FeignException e)
            {
                if(e.status()==400)
                {
                    db.setWatchlist(false);
                    repo.save(db);
                }
                logger.info(e.getMessage());
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
            b=false;
        }
    }

    @Scheduled(fixedRate = 120000)
    void refreshncTrack()
    {
        List<DBdetails> list= repo.findByPlatformAndTrackIsTrue("Namecheap");
        if(list.isEmpty())
            return;
        for(int i=0;i< list.size();i++)
        {
            DBdetails db= list.get(i);
            String ncid= db.getNamecheapid();
            String domain= db.getDomain();

            try {
                AuctionDetailNC nc = namecheapfeign.getAuctionDetailbyId(bearer, ncid);
                if (nc.getStatus().equals("active"))
                {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = null;
                    String endTimeist = "";
                    Integer est = nc.getEstibotValue();
                    String time_left;
                    try {
                        d = parser.parse(endTime);
                        time_left = relTime(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }

                    endTimeist = ft1.format(d);
                    logger.info(endTimeist);
                    Float prevBid= Float.valueOf(db.getCurrbid());
                    Float currbid = nc.getPrice();
                    Date now= new Date();
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
                    row.add(new InlineKeyboardButton("Remove", "rw nc " + ncid + " " + domain));
                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    rows.add(row);
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                    String text = "Namecheap Live Track\n\n" + domain + "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + est + " \nGDV: " + db.getGdv()  +" \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                    try {
                        Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                , text, inlineKeyboardMarkup));
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }


                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    //db.setEstibot(String.valueOf(nc.getEstibotValue()));
                    db.setBids(nc.getBidCount());
                    repo.save(db);
                } else {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = null;
                    String endTimeist = "";
                    Integer est = nc.getEstibotValue();
                    String time_left;
                    try {
                        d = parser.parse(endTime);
                        time_left = relTime(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    endTimeist = ft1.format(d);
                    logger.info(endTimeist);
                    Float currbid = nc.getPrice();
                    Date now= new Date();
                    String time=timeft.format(now);
                    notifRepo.save(new Notification("Namecheap",time,"Tracked auction ended of domain: "+domain+" at price: "+currbid+" at time: "+endTimeist));
                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    // db.setEstibot(String.valueOf(est));
                    db.setBids(nc.getBidCount());
                    db.setWatchlist(false);
                    db.setTrack(false);
                    db.setWasWatchlisted(true);

                    repo.save(db);
                }
            }
            catch(FeignException e)
            {
                if(e.status()==400)
                {
                    db.setTrack(false);
                    repo.save(db);
                }
                logger.info(e.getMessage());
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
        }
    }

    void instantUpdateWatchlist(DBdetails db)
    {
        String domain= db.getDomain();
        String ncid= db.getNamecheapid();
        try {

            AuctionDetailNC nc = namecheapfeign.getAuctionDetailbyId(bearer,ncid);
            String endTime = nc.getEndDate();
            endTime = endTime.substring(0, endTime.length() - 5);
            Date d = null;
            String endTimeist = "";
            String time_left="";
            try {
                d = parser.parse(endTime);
                time_left = relTime(d);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            endTimeist = ft1.format(d);
            logger.info(endTimeist);
            Float currbid = nc.getPrice();
            // Integer gdv=0;
            Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndNamecheapid("Namecheap",ncid));

            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
            row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
            row.add(new InlineKeyboardButton("Remove", "rw nc " + ncid + " " + domain));
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            rows.add(row);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
            String text = "Namecheap\n\n" + domain + "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + nc.getEstibotValue()//+ "\nGDV: " + gdv
                    + " \n\nLink: " + "https://www.namecheap.com/market/" + domain;
            try {
                Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                        , text, inlineKeyboardMarkup));
            } catch (Exception e) {
                logger.info(e.getMessage());
            }


                db.setCurrbid(String.valueOf(currbid));
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setTime_left(time_left);
                db.setEstibot(nc.getEstibotValue());
                //db.setGdv(gdv);
            db.setWatchlist(true);
            /*if(track)
                db.setTrack(true);*/
            repo.save(db);
        }
        catch(Exception e)
        {
            Date now= new Date();
            String time = timeft.format(now);
            notifRepo.save(new Notification("Namecheap",time,"Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage()));
            logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
        }
    }
    void watchlistLive(String domain, String ncid, Boolean track)
    {
        try {
            domain=domain.toLowerCase();
            AuctionDetailNC nc = namecheapfeign.getAuctionDetailbyId(bearer,ncid);
            String endTime = nc.getEndDate();
            endTime = endTime.substring(0, endTime.length() - 5);
            Date d = null;
            String endTimeist = "";
            String time_left="";
            try {
                d = parser.parse(endTime);
                time_left = relTime(d);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            endTimeist = ft1.format(d);
            logger.info(endTimeist);
            Float currbid = nc.getPrice();
           // Integer gdv=0;
            Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndNamecheapid("Namecheap",ncid));
            /*if(op.isPresent())
            {
                gdv= op.get().getGdv();
                if(gdv==null||gdv==0)
                {
                    Optional<Livencdb> ad= Optional.ofNullable(liveNcRepo.findById(ncid));
                    if(ad.isPresent())
                    {
                        gdv=ad.get().getGdv();
                    }
                }
            }
            else
                gdv= liveNcRepo.findById(ncid).getGdv();*/

            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
            row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
            row.add(new InlineKeyboardButton("Remove", "rw nc " + ncid + " " + domain));
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            rows.add(row);
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
            String text = "Namecheap\n\n" + domain + "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + nc.getEstibotValue()//+ "\nGDV: " + gdv
                    + " \n\nLink: " + "https://www.namecheap.com/market/" + domain;
            try {
                Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                        , text, inlineKeyboardMarkup));
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            DBdetails db = null;


            if (op.isPresent()) {
                db = op.get();
                db.setCurrbid(String.valueOf(currbid));
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setTime_left(time_left);
                db.setEstibot(nc.getEstibotValue());
                //db.setGdv(gdv);
                db.setFetched(true);
            } else {
                //AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "", endTime, endTimeist, "", false, ncid);
                db.setTime_left(time_left);
                db.setEstibot(nc.getEstibotValue());
               // db.setGdv(gdv);
                db.setFetched(true);
            }
            db.setWatchlist(true);
            if(track)
            db.setTrack(true);
            repo.save(db);
        }
        catch(Exception e)
        {
            Date now= new Date();
            String time = timeft.format(now);
            notifRepo.save(new Notification("Namecheap",time,"Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage()));
            logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
        }
    }

    @PostMapping("/fetchdetailsnc")
    List<DBdetails> fetchdetailsnc(@RequestBody FetchReq body)
    {
        List<String> ddlist= body.getDomains();
        Boolean watch= body.getWatch();
        List<DBdetails> list = new ArrayList<>();
        int n= ddlist.size();
        for(int i=0;i<n;i++) {
            String domain= ddlist.get(i).toLowerCase();
            try
            {
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
                AuctionDetailNC nc = rn.getItems().get(0);
                String endTime = nc.getEndDate();
                endTime = endTime.substring(0, endTime.length() - 5);
                Date d = null;
                String endTimeist = "";
                String time_left;
                try {
                    d = parser.parse(endTime);
                    time_left = relTime(d);
                } catch (ParseException p) {
                    logger.info(p.getMessage());
                    continue;
                }
                endTimeist = ft1.format(d);
                logger.info(endTimeist);
                Float currbid = nc.getPrice();
                String ncid = nc.getId();
                Optional<DBdetails> op = Optional.ofNullable(repo.findByNamecheapid(ncid));
                DBdetails db = null;


                if (op.isPresent()) {
                    db = op.get();
                    db.setCurrbid(String.valueOf(currbid));
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    db.setEstibot(nc.getEstibotValue());
                    db.setFetched(true);


                } else {
                    //AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                    db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "", endTime, endTimeist, "", false, ncid);
                    db.setTime_left(time_left);
                    db.setEstibot(nc.getEstibotValue());
                    db.setFetched(true);

                }
                if(watch)
                {db.setWatchlist(true);
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid", "b nc " + ncid + " " + domain + " " + currbid));
                    row.add(new InlineKeyboardButton("Remove", "rw nc " + ncid + " " + domain));
                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    rows.add(row);
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                    String text = "Namecheap\n\n" + domain + "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + nc.getEstibotValue()//+ "\nGDV: " + gdv
                            + " \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                    try {
                        Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                , text, inlineKeyboardMarkup));
                    } catch (Exception e) {
                        logger.info(e.getMessage());
                    }
                }
                repo.save(db);
                list.add(db);
            }
            catch(Exception e)
            {
                Date now= new Date();
                String time = timeft.format(now);
                notifRepo.save(new Notification("Namecheap",time,"Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage()));
                logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
            }
            //System.out.println(d);
        }
        return list;
    }

    @GetMapping("/fetchnc")
    ResponseLivedb fetchlive()
    {
        return namecheapfeign.getAuctionDetailslive1(bearer,"1_","1669908629_1669912229","end_time",100);
    }

    @GetMapping("/timezone")
    void timezone(@RequestParam String time)
    {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone istTimeZone = TimeZone.getTimeZone("IST");
        parser.setTimeZone(utcTimeZone);
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        ft1.setTimeZone(istTimeZone);
        time=time.substring(0,time.length()-5);

        try {
            Date d = parser.parse(time);
            System.out.println(d);
            logger.info(ft1.format(d));
        }
        catch(ParseException p)
        {
            logger.info(p.getMessage());
        }
    }

    float schedulesingle(String domain, String ncid, Float bid)
    {
           Float maxprice= bid;
            try {
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
                AuctionDetailNC nc = rn.getItems().get(0);

                float minNextBid= nc.getMinBid();
                if(maxprice>=minNextBid) {
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = new Date();

                    try {
                        d = parser.parse(endTime);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        return 1;
                    }
                    Date now = new Date();
                    if (d.getTime() - now.getTime() < 300000) {
                        d.setSeconds(d.getSeconds() - 10);
                        ScheduledFuture place = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime), d);
                        enterTaskMap(domain, place, "pb");
                    } else {
                        d.setMinutes(d.getMinutes() - 4);
                        ScheduledFuture pre = taskScheduler.schedule(new PreCheck(ncid, domain, maxprice), d);
                        enterTaskMap(domain, pre, "pc");

                    }
                    Date finalD = d;
                    String finalEndTime = endTime;
                    CompletableFuture.runAsync(()->{
                    String endTimeist = ft1.format(finalD);
                    String time_left = relTime(finalD);
                    telegram.sendAlert(-1001763199668l,1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
                    logger.info(endTimeist);
                    Float currbid = nc.getPrice();

                    //Integer gdv = liveNcRepo.findById(nc.getId()).getGdv();
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByNamecheapid(ncid));
                    DBdetails db = null;

                    if (op.isPresent()) {
                        db = op.get();
                        db.setCurrbid(String.valueOf(currbid));
                        db.setBidAmount(String.valueOf(bid));
                        db.setResult("Bid Scheduled");
                        db.setEndTimepst(finalEndTime);
                        db.setEndTimeist(endTimeist);
                        db.setTime_left(time_left);
                        db.setEstibot(nc.getEstibotValue());


                    } else {
                        db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", finalEndTime, endTimeist, endTimeist, false, ncid);
                        db.setTime_left(time_left);
                        db.setBidAmount(String.valueOf(bid));
                        db.setEstibot(nc.getEstibotValue());


                    }
                    //db.setGdv(gdv);
                    repo.save(db);
                    String time = timeft.format(now);
                    notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist));
                    logger.info("BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
},threadPoolExecutor);
                return 0;}
                else
                {
                    CompletableFuture.runAsync(()->{
                        Date now = new Date();
                        String time = ft1.format(now);
                        telegram.sendAlert(-1001763199668l, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                        notifRepo.save(new Notification("GoDaddy", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);
                    },threadPoolExecutor);
                    return minNextBid;
                }
            }
            catch(Exception e)
            {
                Date now= new Date();
                String time= timeft.format(now);
                notifRepo.save(new Notification("Namecheap",time,"BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice));
                logger.info("BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice);

                logger.info(e.getMessage());
            }
    return 0;
    }

    @GetMapping("/schedulesinglenc")
    float schedulesingleoutbid(@RequestParam String domain, @RequestParam String ncid, @RequestParam Float bid)
    {
        Float maxprice= bid;
        try {
            ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
            AuctionDetailNC nc = rn.getItems().get(0);
            float minNextBid= nc.getMinBid();

            if(minNextBid<=maxprice) {
                String endTime = nc.getEndDate();
                endTime = endTime.substring(0, endTime.length() - 5);
                Date d = new Date();

                try {
                    d = parser.parse(endTime);
                } catch (ParseException p) {
                    logger.info(p.getMessage());
                    return 1;
                }
                Date now = new Date();
                if (d.getTime() - now.getTime() < 300000) {
                    d.setSeconds(d.getSeconds() - 10);
                    ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime), d);
                    enterTaskMap(domain, task, "pb");

                } else {
                    d.setMinutes(d.getMinutes() - 4);
                    ScheduledFuture task = taskScheduler.schedule(new PreCheck(ncid, domain, maxprice), d);
                    enterTaskMap(domain, task, "pc");

                }
                Date finalD = d;
                String finalEndTime = endTime;
                CompletableFuture.runAsync(() -> {
                    String endTimeist = ft1.format(finalD);
                    String time_left = relTime(finalD);
                    Float currbid = nc.getPrice();
                    telegram.sendAlert(-1001763199668l,1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByNamecheapid(ncid));
                    DBdetails db = null;

                    if (op.isPresent()) {
                        db = op.get();
                        db.setCurrbid(String.valueOf(currbid));
                        db.setBidAmount(String.valueOf(bid));
                        db.setResult("Bid Scheduled");
                        db.setEndTimepst(finalEndTime);
                        db.setEndTimeist(endTimeist);
                        db.setTime_left(time_left);

                        db.setEstibot(nc.getEstibotValue());


                    } else {
                        db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", finalEndTime, endTimeist, endTimeist, false, ncid);
                        db.setTime_left(time_left);
                        db.setBidAmount(String.valueOf(bid));
                        db.setEstibot(nc.getEstibotValue());


                    }
                    repo.save(db);
                    String time = timeft.format(now);
                    notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist));
                    logger.info("BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
                }, threadPoolExecutor);
                return 0;
            }
            else
            {
                CompletableFuture.runAsync(()->{
                    Date now = new Date();
                    String time = ft1.format(now);
                    telegram.sendAlert(-1001763199668l,1005l, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                    notifRepo.save(new Notification("GoDaddy", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);
                },threadPoolExecutor);
                return minNextBid;
            }
        }
        catch(Exception e)
        {
            Date now = new Date();
            String time= timeft.format(now);
            notifRepo.save(new Notification("Namecheap",time,"BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice));
            logger.info("Namecheap: BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice);

            logger.info(e.getMessage());
        }
        return 0;
    }


    @PostMapping("/bulkschedulenc")
    List<Integer> bulkschedule(@RequestBody List<ArrayList<String>> ddlist)
    {

        List<Integer> l= new ArrayList<>();
        List<Long> ids= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();
            Float maxprice= Float.valueOf(ddlist.get(i).get(1));
            try {
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
                AuctionDetailNC nc = rn.getItems().get(0);
                float minNextBid=nc.getMinBid();
                if(minNextBid<=maxprice)
                {
                    String ncid = nc.getId();
                    String endTime = nc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = new Date();

                    try {
                        d = parser.parse(endTime);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    Date now = new Date();
                    if (d.getTime() - now.getTime() < 300000) {
                        d.setSeconds(d.getSeconds() - 10);
                        ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime), d);
                        enterTaskMap(domain, task, "pb");

                    } else {
                        //d.setMinutes(d.getMinutes()-4);
                        Date d1 = new Date(d.getTime() - 270000);
                        ScheduledFuture task = taskScheduler.schedule(new PreCheck(ncid, domain, maxprice), d1);
                        enterTaskMap(domain, task, "pc");

                    }
                    a++;
                    String endTimeist = ft1.format(d);
                    String time_left = relTime(d);
                    telegram.sendAlert(-1001763199668l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
                    logger.info(endTimeist);
                    Float currbid = nc.getPrice();
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByNamecheapid(ncid));
                    DBdetails db = null;

                    if (op.isPresent()) {
                        db = op.get();
                        db.setCurrbid(String.valueOf(currbid));
                        db.setBidAmount(ddlist.get(i).get(1));
                        db.setResult("Bid Scheduled");
                        db.setEndTimepst(endTime);
                        db.setEndTimeist(endTimeist);
                        db.setTime_left(time_left);
                        db.setEstibot(nc.getEstibotValue());

                        repo.save(db);
                    } else {
                        db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", endTime, endTimeist, "", false, ncid);
                        db.setTime_left(time_left);
                        db.setBidAmount(ddlist.get(i).get(1));
                        db.setEstibot(nc.getEstibotValue());

                        repo.save(db);
                    }
                    ids.add(db.getId());
                    String time = timeft.format(now);
                    notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime()));
                    logger.info("BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime());
                }
                else
                {
                    Date now = new Date();
                    String time = ft1.format(now);
                    //telegram.sendAlert(-1001763199668l, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                    notifRepo.save(new Notification("GoDaddy", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);

                }
            }
            catch(Exception e)
            {
                Date now= new Date();
                String time= timeft.format(now);
                notifRepo.save(new Notification("Namecheap",time,"BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice));
                logger.info("BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice);
                logger.info(e.getMessage());
            }

        }
        asyncCalss.getGDVs(ids);
        l.add(a);
        l.add(n);
        return l;
    }

    @PostMapping("/bulkbidnc")
    List<Integer> bulkbid(@RequestBody List<ArrayList<String>> ddlist)
    {
        List<Integer> l= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            try {
                String domain = ddlist.get(i).get(0).toLowerCase();
                Bidnc bid = new Bidnc(Float.valueOf(ddlist.get(i).get(1)));

                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);

                AuctionDetailNC nc = rn.getItems().get(0);
                String ncid = nc.getId();
                ResponsePlaceBidNc pb= namecheapfeign.placeBidnc(bearer,ncid,bid);
                String endTime = nc.getEndDate();
                endTime = endTime.substring(0, endTime.length() - 5);
                Date d = new Date();

                String endTimeist = "", bidplacetime = "";
                String time_left;
                try {
                    d = parser.parse(endTime);
                    endTimeist = ft1.format(d);
                    time_left = relTime(d);

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                    continue;
                }
                Date d1= new Date();
                bidplacetime= ft1.format(d1);


                logger.info(endTimeist);
                Float currbid = nc.getPrice();


                Optional<DBdetails> op = Optional.ofNullable(repo.findByNamecheapid(ncid));
                DBdetails db = null;

                if(pb.getStatus().equals("processed")) {
                    currbid=pb.getAmount();
                    if (op.isPresent()) {
                        db = op.get();
                        db.setCurrbid(String.valueOf(currbid));
                        //db.setBidders(nc.get);
                        //db.setTime_left(ad.);
                        //db.setAge(aj.getAge());
                        //db.setEstibot(aj.getEstibot_appraisal());
                        //db.setAuctiontype();
                        db.setBidAmount(ddlist.get(i).get(1));
                        db.setEndTimepst(endTime);
                        db.setEndTimeist(endTimeist);
                        db.setTime_left(time_left);
                        db.setResult("Bid Placed");
                        db.setEstibot(nc.getEstibotValue());
                        db.setIsBidPlaced(true);
                        db.setBidplacetime(bidplacetime);
                        db.setMyLastBid(bid.getMaxAmount());
                        repo.save(db);
                    } else {
                        db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Placed", endTime, endTimeist, bidplacetime, true, ncid);
                        db.setTime_left(time_left);
                        db.setEstibot(nc.getEstibotValue());

                        db.setBidAmount(ddlist.get(i).get(1));
                        repo.save(db);
                    }
                    a++;
                    Date now= new Date();
                    String time= timeft.format(now);
                    telegram.sendAlert(-1001763199668l,1005l, "Namecheap: INSTANT BID PLACED for " + domain + " at price " + bid.getMaxAmount() + " USD");
                    notifRepo.save(new Notification("Namecheap",time,"INSTANT BID PLACED for " + domain + " at price " + bid.getMaxAmount() + " USD"));

                    logger.info("Namecheap: instant bid placed of domain: " + domain);
                }else
                {
                    if (op.isPresent()) {
                        db = op.get();
                        db.setCurrbid(String.valueOf(currbid));
                        //db.setBidders(nc.get);
                        //db.setTime_left(ad.);
                        //db.setAge(aj.getAge());
                        //db.setEstibot(aj.getEstibot_appraisal());
                        //db.setAuctiontype();
                        db.setBidAmount(ddlist.get(i).get(1));
                        db.setEndTimepst(endTime);
                        db.setEndTimeist(endTimeist);
                        db.setTime_left(time_left);
                        db.setResult("Bid Not Placed");
                        db.setIsBidPlaced(false);
                        db.setEstibot(nc.getEstibotValue());

                        db.setBidplacetime(bidplacetime);
                        repo.save(db);
                    } else {
                        db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Not Placed", endTime, endTimeist, bidplacetime, false, ncid);
                        db.setTime_left(time_left);
                        db.setBidAmount(ddlist.get(i).get(1));
                        db.setEstibot(nc.getEstibotValue());

                        notifRepo.save(new Notification("Namecheap: INSTANT BID NOT PLACED for " + domain + " at price " + bid.getMaxAmount() + " USD at " + new Date()));

                        logger.info("Namecheap: instant bid not placed of domain: " + domain);
                        repo.save(db);
                    }
                }
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }

        }
        l.add(a);
        l.add(n);
        return l;
    }


    String relTime(Date d2)
    {
        Date d1 = new Date();
        long diff = d2.getTime() - d1.getTime();
        String s="";
        long min = TimeUnit.MILLISECONDS.toMinutes(diff)%60;

            s=min+"m"+s;

        long h = TimeUnit.MILLISECONDS.toHours(diff)%24;

            s=h+"h, "+s;

        long d = TimeUnit.MILLISECONDS.toDays(diff)%365;

            s=d+"d, "+s;

        return s;
    }

    String relTimelive(Date d2)
    {
        Date d1 = new Date();
        long diff = d2.getTime() - d1.getTime();
        String s="";
        long min = TimeUnit.MILLISECONDS.toMinutes(diff)%60;

        s=min+"m"+s;

        long h = TimeUnit.MILLISECONDS.toHours(diff)%24;

        s=h+"h, "+s;

        return s;
    }

    @GetMapping("/getlivenc")
    List<Livencdb> getLive()
    {
        return liveNcRepo.findByLiveTrueOrderByIddDesc();
    }

    @GetMapping("/getplacenc/{id}")
    ResponsePlaceBidNc getplace(@PathVariable String id, @RequestParam Float bid1)
    {
        Bidnc bid= new Bidnc(bid1);
      return  namecheapfeign.placeBidnc(bearer,id,bid);
    }

   @Scheduled(cron = "0 00 20 ? * *", zone = "IST")
    @GetMapping("/startlivenc")
    ResponseLivedb startLivenc()
    {

        logger.info("Starting NameCheap Live Service");
        try {
            scheduledFuture.cancel(true);
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }

        //map.clear();
        Optional<LiveMap> o= liveMaprepo.findById(1);
        LiveMap lm;
        if(o.isEmpty())
        {
            lm= new LiveMap(1);
        }
        else
            lm=o.get();

        map= lm.getMapnc();
        //liveNcRepo.deleteAll();
        String bidCount="1_";
        Date now= new Date();
        Long t1= now.getTime()/1000;
        Float hours=2f;
        Long t2=t1+hours.longValue()*3600;
        //Long t2=1669905052l;
        String t=String.valueOf(t1)+"_"+String.valueOf(t2);
        //String t="1669908629_1669912229";
        logger.info(t);

        ResponseLivedb rl= namecheapfeign.getAuctionDetailslive(bearer,bidCount,t,"end_time");
        int total= rl.getPages().getTotal();
        List<Livencdb> items= rl.getItems();
        int r=total/100;
        int d=total%100;
        int n= items.size();
        int l=0;
        for(int i=0;i<n;i++)
        {
           Livencdb lnc= items.get(i);
            if(!map.containsKey(lnc.getId()))
            {
                lnc.setInitialList(true);
                l=Math.max(l,lnc.getName().length());
                liveNcRepo.save(lnc);
                map.put(lnc.getId(), lnc.getName().toLowerCase());
            }
        }
        if(r>=1)
        {
            if(d==0)
            {
                for(int k=1;k<r;k++)
                {
                    ResponseLivedb rl1= namecheapfeign.getAuctionDetailslive1(bearer,bidCount,t,"end_time",k+1);
                    List<Livencdb> items1= rl1.getItems();
                    int n1= items1.size();
                    for(int i=0;i<n1;i++)
                    {
                        Livencdb lnc = items1.get(i);
                        if(!map.containsKey(lnc.getId())) {
                            l=Math.max(l,lnc.getName().length());
                            lnc.setInitialList(true);
                            liveNcRepo.save(lnc);
                            map.put(lnc.getId(), lnc.getName().toLowerCase());
                        }
                    }
                }
            }
                else
                {
                    for(int k=1;k<=r;k++)
                    {
                        ResponseLivedb rl1= namecheapfeign.getAuctionDetailslive1(bearer,bidCount,t,"end_time",k+1);
                        List<Livencdb> items1= rl1.getItems();
                        int n1= items1.size();
                        for(int i=0;i<n1;i++)
                        {
                            Livencdb lnc = items1.get(i);
                            if(!map.containsKey(lnc.getId())) {

                                l=Math.max(l,lnc.getName().length());
                                lnc.setInitialList(true);
                                liveNcRepo.save(lnc);
                                map.put(lnc.getId(), lnc.getName().toLowerCase());
                            }
                        }
                    }
                }
            }
        sendInitialList(l);
        //liveMaprepo.save(lm);
        logger.info("Started Namecheap Live Service");
        scheduledFuture= taskScheduler.scheduleWithFixedDelay(new DetectLiveNc(t),28000);

        now.setMinutes(now.getMinutes()+hours.intValue()*60);
        taskScheduler.schedule(new StopLive(scheduledFuture),now);
        return rl;
    }

    @GetMapping("/sendncinitiallist")
    void sendInitialList(int n)
    {
        //int n=32;
        //           currbid, est, separators, space around separators
        int t= n+    6  +   6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namecheap Initial List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-6s | %6s%n","Domain", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<Livencdb> list=liveNcRepo.findByInitialListTrueOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6.0f%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
           // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendEndList(int n)
    {
        //int n=32;
        //           currbid, est, separators, space around separators
        int t= n+    6  +   6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namecheap Next Day List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-6s | %6s%n","Domain", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<Livencdb> list=liveNcRepo.findByEndListTrueOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6.0f%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    @GetMapping("/cancel/nc")
    void cancelBid(@RequestParam String domain,@RequestParam String ncid)
    {
        logger.info(domain+ncid);
        deleteTaskMap(domain);
        DBdetails db= repo.findByNamecheapid(ncid);
        db.setResult("Bid Cancelled");
        repo.save(db);
    }

  //  @Autowired
   // GoDaddyFeign goDaddyFeign;
    public class DetectLiveNc implements Runnable
    {
        String time;
         DetectLiveNc(String time)
         {
             this.time=time;
         }

         @Override
         public void run()
         {
             logger.info("Namecheap Detect Live Service Ran");
             //LiveMap lm = liveMaprepo.findById(1).get();
             //LiveMap lm = liveMaprepo.getReferenceById(1);

            //Map<String,String> map= lm.getMapnc();
            // Map<String,String> map=liveMaprepo.getReferenceById(1).getMapnc();
             String bidCount="1_";
             ResponseLivedb rl= namecheapfeign.getAuctionDetailslive(bearer,bidCount,time,"end_time");
             List<Livencdb> items= rl.getItems();
             int total= rl.getPages().getTotal();
             int r=total/100;
             int d1=total%100;
             int n= items.size();
             for(int i=0;i<n;i++)
             {
                 Livencdb item = items.get(i);
                 String domain= item.getName();
                 String id= item.getId();
                 if(!map.containsKey(id))
                 {
                     logger.info("Detected Live Domain Namecheap: "+domain);
                 map.put(items.get(i).getId(),items.get(i).getName().toLowerCase());
                 item.setLive(true);
                     String endTime = item.getEndDate();
                     endTime = endTime.substring(0, endTime.length() - 5);
                     Date d = new Date();


                     //String endTimeist = "";
                     String time_left;
                     try {
                         d = parser.parse(endTime);
                         //endTimeist = ft1.format(d);
                         time_left = relTime(d);

                     } catch (ParseException p) {
                         logger.info(p.getMessage());
                         continue;
                     }

                    // Integer gdv= goDaddyFeign.getGDV("sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm",domain).getGovalue();

                     Float currbid = item.getPrice();
                     Float bid=0.0f;
                     //int age= item.get;
                     Float est=item.getEstibotValue();

                     List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                     row.add(new InlineKeyboardButton("Bid","b"+" nc "+item.getId()+" "+domain+" "+currbid));
                     row.add(new InlineKeyboardButton("Watch","w nc "+item.getId()+" "+domain));
                     row.add(new InlineKeyboardButton("Track","t nc "+item.getId()+" "+domain));

                     List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                     rows.add(row);

                     InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);

                     String text= "Namecheap Live Detect \n \n"+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+currbid+" \n\nEST: "+est+//" \nGDV: "+gdv+
                             " \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                     try {
                         Object obj = telegram.sendKeyboard(new SendMessage(//-1001653862522L
                                 -1001763199668l,1017l,text,inlineKeyboardMarkup));
                     }
                     catch(RetryableException re)
                     {
                         //liveMaprepo.save(lm);
                         liveNcRepo.save(item);
                         logger.info(re.getMessage());
                         try {
                             Thread.sleep(30000);
                             Object obj = telegram.sendKeyboard(new SendMessage(-1001653862522L
                                     ,text,inlineKeyboardMarkup));
                         }
                         catch(InterruptedException ie)
                         {
                             logger.info(ie.getMessage());
                             Thread.currentThread().interrupt();
                         }
                     }
                     catch (Exception e)
                     {
                         logger.info(e.getMessage());
                     }

                     Date date= new Date();
                     String addtime= ft1.format(date);
                     item.setAddtime(addtime);
                     item.setTime_left(time_left);
                    // item.setGdv(gdv);
                     //WasLive wasLive= new WasLive(time_left,addtime,"Dynadot",ld.getAuction_id(),domain, ld.getCurrent_bid_price(), ld.getEnd_time(), ld.getEstibot_appraisal(),ld.getUtf_name(),ld.getBids(),ld.getBidders(),ld.getAge(),ld.getEnd_time_stamp());
                    // wasLiveRepo.save(wasLive);

                 liveNcRepo.save(item);
                 }
             }
             if(r>=1)
             {
                 if(d1==0)
                 {
                     for(int k=1;k<r;k++)
                     {
                         ResponseLivedb rl1= namecheapfeign.getAuctionDetailslive1(bearer,bidCount,time,"end_time",k+1);
                         List<Livencdb> items1= rl1.getItems();
                         int n1= items1.size();
                         for(int i=0;i<n1;i++)
                         {
                             Livencdb item1=items1.get(i);
                             String domain= item1.getName().toLowerCase();
                             String id= item1.getId();
                             if(!map.containsKey(id)) {
                                 logger.info("Detected Live Domain Namecheap: " + domain);
                                 map.put(items1.get(i).getId(), items1.get(i).getName().toLowerCase());
                                 item1.setLive(true);
                                 String endTime = item1.getEndDate();
                                 endTime = endTime.substring(0, endTime.length() - 5);
                                 Date d = new Date();

                                 //String endTimeist = "";
                                 String time_left;
                                 try {
                                     d = parser.parse(endTime);
                                     //endTimeist = ft1.format(d);
                                     time_left = relTime(d);

                                 } catch (ParseException p) {
                                     logger.info(p.getMessage());
                                     continue;
                                 }

                                 //Integer gdv= goDaddyFeign.getGDV("sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm",domain).getGovalue();
                                 Float currbid = item1.getPrice();
                                 Float bid=0.0f;
                                 Float est = item1.getEstibotValue();
                                 List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                                 row.add(new InlineKeyboardButton("Bid","b"+" nc "+item1.getId()+" "+domain+" "+currbid));
                                 row.add(new InlineKeyboardButton("Watch","w nc "+item1.getId()+" "+domain));
                                 row.add(new InlineKeyboardButton("Track","t nc "+item1.getId()+" "+domain));

                                 List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                                 rows.add(row);
                                 InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);

                                 String text= "Namecheap Live Detect \n \n"+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+currbid+" \n\nEST: "+est+//" \nGDV: "+gdv+
                                         " \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                                 try {
                                     Object obj = telegram.sendKeyboard(new SendMessage(//-1001653862522L
                                             -1001763199668l,1017l ,text,inlineKeyboardMarkup));
                                 }
                                 catch(RetryableException re)
                                 {
                                     //liveMaprepo.save(lm);
                                     liveNcRepo.save(item1);
                                     logger.info(re.getMessage());
                                     try {
                                         Thread.sleep(30000);
                                         Object obj = telegram.sendKeyboard(new SendMessage(-1001653862522L
                                                 ,text,inlineKeyboardMarkup));
                                     }
                                     catch(InterruptedException ie)
                                     {
                                         logger.info(ie.getMessage());
                                         Thread.currentThread().interrupt();
                                     }
                                 }
                                 catch (Exception e)
                                 {
                                     logger.info(e.getMessage());
                                 }
                                 Date date = new Date();
                                 String addtime = ft1.format(date);
                                 item1.setAddtime(addtime);
                                 item1.setTime_left(time_left);
                                // item1.setGdv(gdv);
                                 //WasLive wasLive= new WasLive(time_left,addtime,"Dynadot",ld.getAuction_id(),domain, ld.getCurrent_bid_price(), ld.getEnd_time(), ld.getEstibot_appraisal(),ld.getUtf_name(),ld.getBids(),ld.getBidders(),ld.getAge(),ld.getEnd_time_stamp());
                                 // wasLiveRepo.save(wasLive);

                                 liveNcRepo.save(item1);

                             }
                         }
                     }
                 }
                 else
                 {
                     for(int k=1;k<=r;k++)
                     {
                         ResponseLivedb rl1= namecheapfeign.getAuctionDetailslive1(bearer,bidCount,time,"end_time",k+1);
                         List<Livencdb> items1= rl1.getItems();
                         int n1= items1.size();
                         for(int i=0;i<n1;i++)
                         {
                             Livencdb item1=items1.get(i);
                             String domain= item1.getName();
                             String id= item1.getId();
                             if(!map.containsKey(id)) {
                                 logger.info("Detected Live Domain Namecheap: " + domain);
                                 map.put(items1.get(i).getId(), items1.get(i).getName().toLowerCase());
                                 item1.setLive(true);
                                 String endTime = item1.getEndDate();
                                 endTime = endTime.substring(0, endTime.length() - 5);
                                 Date d = new Date();

                                 //String endTimeist = "";
                                 String time_left;
                                 try {
                                     d = parser.parse(endTime);
                                     //endTimeist = ft1.format(d);
                                     time_left = relTime(d);

                                 } catch (ParseException p) {
                                     logger.info(p.getMessage());
                                     continue;
                                 }

                                 //Integer gdv= goDaddyFeign.getGDV("sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm",domain).getGovalue();
                                 Float currbid = item1.getPrice();
                                 Float bid=0.0f;
                                 Float est = item1.getEstibotValue();
                                 List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                                 row.add(new InlineKeyboardButton("Bid","b"+" nc "+item1.getId()+" "+domain+" "+currbid));
                                 row.add(new InlineKeyboardButton("Watch","w nc "+item1.getId()+" "+domain));
                                 row.add(new InlineKeyboardButton("Track","t nc "+item1.getId()+" "+domain));

                                 List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                                 rows.add(row);
                                 InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                                 String text= "Namecheap Live Detect \n \n"+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+currbid+" \n\nEST: "+est+//" \nGDV: "+gdv+
                                         " \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                                 try {
                                     Object obj = telegram.sendKeyboard(new SendMessage(//-1001653862522L
                                             -1001763199668l,1017l ,text,inlineKeyboardMarkup));
                                 }
                                 catch(RetryableException re)
                                 {
                                     //liveMaprepo.save(lm);
                                     liveNcRepo.save(item1);
                                     logger.info(re.getMessage());
                                     try {
                                         Thread.sleep(30000);
                                         Object obj = telegram.sendKeyboard(new SendMessage(//-1001653862522L
                                                 -1001763199668l,1017l,text,inlineKeyboardMarkup));
                                     }
                                     catch(InterruptedException ie)
                                     {
                                         logger.info(ie.getMessage());
                                         Thread.currentThread().interrupt();
                                     }
                                 }
                                 catch(Exception e)
                                 {
                                     logger.info(e.getMessage());
                                 }
                                 Date date = new Date();
                                 String addtime = ft1.format(date);
                                 item1.setAddtime(addtime);
                                 item1.setTime_left(time_left);
                                 //item1.setGdv(gdv);
                                 liveNcRepo.save(item1);
                             }
                         }
                     }
                 }
             }
             //liveMaprepo.save(lm);
         }
    }

    public class StopLive implements Runnable
    {
        ScheduledFuture scheduledFuture;

        public StopLive(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        @Override
        public void run()
        {
            scheduledFuture.cancel(false);
            map.clear();
            Optional<LiveMap> o= liveMaprepo.findById(1);
            LiveMap lm;
            if(o.isEmpty())
            {
                 lm= new LiveMap(1);
            }
            else
                lm=o.get();

            Map<String,String> map1= lm.getMapnc();
            map1.clear();
            String bidCount="1_";
            Date now= new Date();
            Long t1= now.getTime()/1000;
            Float hours=24f;
            Long t2=t1+hours.longValue()*3600;
            //Long t2=1669905052l;
            String t=String.valueOf(t1)+"_"+String.valueOf(t2);
            //String t="1669908629_1669912229";
            logger.info(t);
            liveNcRepo.deleteAll();
            ResponseLivedb rl= namecheapfeign.getAuctionDetailslive(bearer,bidCount,t,"end_time");
            int total= rl.getPages().getTotal();
            List<Livencdb> items= rl.getItems();
            int r=total/100;
            int d=total%100;
            int n= items.size();
            int l=0;
            for(int i=0;i<n;i++)
            {
                Livencdb lnc=items.get(i);
                lnc.setEndList(true);
                l=Math.max(l,lnc.getName().length());
                liveNcRepo.save(lnc);
                map1.put(items.get(i).getId(),items.get(i).getName().toLowerCase());
            }
            if(r>=1)
            {
                if(d==0)
                {
                    for(int k=1;k<r;k++)
                    {
                        ResponseLivedb rl1= namecheapfeign.getAuctionDetailslive1(bearer,bidCount,t,"end_time",k+1);
                        List<Livencdb> items1= rl1.getItems();
                        int n1= items1.size();
                        for(int i=0;i<n1;i++)
                        {
                            Livencdb lnc=items.get(i);
                            l=Math.max(l,lnc.getName().length());
                            lnc.setEndList(true);
                            liveNcRepo.save(lnc);
                            map1.put(items1.get(i).getId(),items1.get(i).getName().toLowerCase());
                        }
                    }
                }
                else
                {
                    for(int k=1;k<=r;k++)
                    {
                        ResponseLivedb rl1= namecheapfeign.getAuctionDetailslive1(bearer,bidCount,t,"end_time",k+1);
                        List<Livencdb> items1= rl1.getItems();
                        int n1= items1.size();
                        for(int i=0;i<n1;i++)
                        {
                            Livencdb lnc=items.get(i);
                            lnc.setEndList(true);
                            l=Math.max(l,lnc.getName().length());
                            liveNcRepo.save(lnc);
                            map1.put(items1.get(i).getId(),items1.get(i).getName().toLowerCase());
                        }
                    }
                }
            }

            liveMaprepo.save(lm);
            sendEndList(l);
        }
    }

    @GetMapping("/listbidsnc")
    ResponseListBids listBids()
    {
        return namecheapfeign.getBidList(bearer);
    }
    public class PreCheck implements Runnable
    {
        String ncid,domain;
        float maxprice;

        public PreCheck(String ncid, String domain, Float maxprice)
        {
            this.ncid = ncid;
            this.domain = domain;
            this.maxprice = maxprice;
        }

        @Override
        public void run()
        {
            AuctionDetailNC detail= namecheapfeign.getAuctionDetailbyId(bearer,ncid);
            float minbid= detail.getMinBid();
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            Float currbid = detail.getPrice();

            if(maxprice<currbid)
            {
               //notify
                DBdetails db= repo.findByNamecheapid(detail.getId());
                db.setResult("Outbid");
                repo.save(db);
                String endTime = detail.getEndDate();
                endTime = endTime.substring(0, endTime.length() - 5);
                Date d = new Date();

                //String endTimeist = "";
                String time_left="";
                try {
                    d = parser.parse(endTime);
                    //endTimeist = ft1.format(d);
                    time_left = relTime(d);

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }

                Integer est=detail.getEstibotValue();
                List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton("Bid","b nc "+detail.getId()+" "+domain+" "+currbid));
                row.add(new InlineKeyboardButton("Watch","w nc "+detail.getId()+" "+domain));
                List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                rows.add(row);
                InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                String text= "Namecheap Auction OUTBID\n\n"+domain+"\n\nTime Left: "+time_left+"\nCurrent Bid: "+currbid+"\nMin Next Bid: "+minbid+"\nOur Max Bid: "+maxprice+" \n\nEST: "+est+" \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                try {
                    Object obj = telegram.sendKeyboard(new SendMessage(-1001763199668L
                            ,text,inlineKeyboardMarkup));
                }
                catch (Exception e)
                {
                    logger.info(e.getMessage());
                }
                Date now= new Date();
                String time= timeft.format(now);
                notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + minbid ));
                logger.info(time+": Namecheap: Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + minbid );



            }
            else
            {
                String endTime= detail.getEndDate().substring(0,detail.getEndDate().length()-5);
                try
                {
                    Date d= parser.parse(endTime);
                    d.setSeconds(d.getSeconds()-10);
                   ScheduledFuture task= taskScheduler.schedule(new PlaceBid(ncid,maxprice,domain,endTime),d);
                    updateTaskMap(domain,task,"pb");

                    Date now= new Date();
                    String time= timeft.format(now);
                    String bidist= ft1.format(d);
                    notifRepo.save(new Notification("Namecheap",time,"Prechecking, Bid SCHEDULED for " + domain + " at price " + maxprice + " at time " + bidist));
                    logger.info(time+": Prechecking, Bid SCHEDULED for " + domain + " at price " + maxprice + " time " + bidist+" i.e. "+bidist);


                }
                catch(ParseException p)
                {
                    logger.info(p.getMessage());
                }
            }
        }
    }
    public class CheckOutbid implements Runnable
    {
        float price,maxprice;
        String ncid;
        String domain;
        ScheduledFuture scheduledFuture;

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public CheckOutbid(Float price, Float maxprice, String ncid, String domain) {
            this.price = price;
            this.maxprice = maxprice;
            this.ncid = ncid;
            this.domain=domain;
        }

        @Override
        public void run()
        {
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            AuctionDetailNC detail= namecheapfeign.getAuctionDetailbyId(bearer,ncid);
            float pricenow = detail.getPrice();
            float minbid   = detail.getMinBid();
            String endTime = detail.getEndDate();
            String status= detail.getStatus();

            if(status.equals("active")) {

                if (pricenow > price) {
                    if (pricenow > maxprice)
                    {
                        //notify
                        DBdetails db= repo.findByNamecheapid(detail.getId());
                        db.setResult("Outbid");
                        repo.save(db);
                        endTime = endTime.substring(0, endTime.length() - 5);
                        Date d = new Date();

                        //String endTimeist = "";
                        String time_left="";
                        try {
                            d = parser.parse(endTime);
                            //endTimeist = ft1.format(d);
                            time_left = relTime(d);

                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                        }

                        Float currbid = detail.getPrice();
                        Integer est=detail.getEstibotValue();
                        List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid","b nc "+detail.getId()+" "+domain+" "+currbid));
                        row.add(new InlineKeyboardButton("Watch","w nc "+detail.getId()+" "+domain));
                        row.add(new InlineKeyboardButton("Track","t nc "+detail.getId()+" "+domain));

                        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                        String text= "Namecheap Auction OUTBID\n\n"+domain+"\n\nTime Left: "+time_left+"\nCurrent Bid: "+currbid+"\nMin Next Bid: "+minbid+"\nOur Max Bid: "+maxprice+" \n\nEST: "+est+" \n\nLink: "+"https://www.namecheap.com/market/"+domain;

                        try {
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                                    ,text,inlineKeyboardMarkup));
                        }
                        catch (Exception e)
                        {
                            logger.info(e.getMessage());
                        }
                        Date now= new Date();
                        String time= timeft.format(now);
                        notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + minbid ));
                        logger.info(time+": Namecheap: Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + minbid );


                        scheduledFuture.cancel(true);
                    }
                    else {
                        endTime = endTime.substring(0,endTime.length()-5);
                        try
                        {
                            Date d = parser.parse(endTime);
                            d.setSeconds(d.getSeconds() - 10);
                           ScheduledFuture task= taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime), d);
                            updateTaskMap(domain,task,"pb");
                            DBdetails dBdetails= repo.findByNamecheapid(ncid);
                            dBdetails.setResult("Bid Placed And Scheduled");
                            repo.save(dBdetails);
                            Date now= new Date();
                            String time= timeft.format(now);
                            String bidist= ft1.format(d);
                            telegram.sendAlert(-1001763199668l,1004l, "Namecheap: Outbid, BID SCHEDULED for domain: "+domain+ " for max price: "+minbid+" at "+bidist);

                            notifRepo.save(new Notification("Namecheap",time,"Outbid, Bid SCHEDULED for " + domain + " at price " + minbid + " at time: " + bidist));
                            logger.info(time+": Outbid, Bid SCHEDULED for " + domain + " at price " + minbid + " time " + bidist);

                        }
                        catch (ParseException p)
                        {
                            logger.info(p.getMessage());
                        }
                    }
                    scheduledFuture.cancel(true);
                }
            }
            else
            {
                logger.info(""+price);
                logger.info(""+pricenow);
                if(pricenow==price)
                {
                    DBdetails dBdetails= repo.findByNamecheapid(ncid);
                    dBdetails.setResult("Won");
                    repo.save(dBdetails);
                    Date now= new Date();
                    String time= timeft.format(now);
                    telegram.sendAlert(-1001763199668l,842l, "Namecheap: Yippee!! Won auction of "+domain+" at price: "+price);
                    notifRepo.save(new Notification("Namecheap",time,"Yippee!! Won auction of "+domain+" at price: "+price));
                    logger.info(time+": Won auction of "+domain+" at price: "+price);
                    deleteTaskMap(domain);
                }
                else
                {
                    DBdetails dBdetails= repo.findByNamecheapid(ncid);
                    dBdetails.setResult("Loss");
                    repo.save(dBdetails);
                    Date now= new Date();
                    String time= timeft.format(now);
                    telegram.sendAlert(-1001763199668l, 841l,"Namecheap: Hush!! Lost auction of "+domain+" at price: "+price);
                    notifRepo.save(new Notification("Namecheap",time,"Hush!! Lost auction of "+domain+" at price: "+pricenow));
                    logger.info(time+": Lost auction of "+domain+" at price: "+pricenow);
                    deleteTaskMap(domain);
                }
                scheduledFuture.cancel(true);
            }
        }
    }
    public class PlaceBid implements Runnable{



        String ncid,domain, timeId;
        Float maxprice;





        public PlaceBid(String ncid, Float maxprice,String domain, String timeId)
        {
            this.ncid=ncid;
            this.maxprice=maxprice;
            this.domain=domain;
            this.timeId=timeId;
            //this.service= new Service();
        }
        @Override
        public void run() {
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            AuctionDetailNC detail= namecheapfeign.getAuctionDetailbyId(bearer,ncid);
            String timeId1= detail.getEndDate().substring(0,detail.getEndDate().length()-5);
            Float price= detail.getMinBid();
            Float pricee= detail.getPrice();

            if(!timeId.equals(timeId1))
            {
                if(pricee<=maxprice) {

                    try {
                        Date d = parser.parse(timeId1);
                        d.setSeconds(d.getSeconds() - 10);
                        ScheduledFuture task=taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, timeId1), d);
                        Date now= new Date();
                        String time= timeft.format(now);
                        String bidist= ft1.format(d);
                        telegram.sendAlert(-1001763199668l,1004l, "Prechecking, Bid SCHEDULED for " + domain + " at price " + price + " at time " + bidist);
                        notifRepo.save(new Notification("Namecheap",time,"Prechecking, Bid SCHEDULED for " + domain + " at price " + price + " at time " + bidist));
                        logger.info(time+": Prechecking, Bid SCHEDULED for " + domain + " at price " + price + " time " + bidist);
                        updateTaskMap(domain,task,"pb");

                        //DBdetails dBdetails= repo.findByNamecheapid(ncid);
                        //dBdetails.setResult("Bid Placed And Scheduled");
                        //repo.save(dBdetails);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                }
                else
                {
                    //notify
                    String endTime= detail.getEndDate();
                    DBdetails db= repo.findByNamecheapid(detail.getId());
                    db.setResult("Outbid");
                    repo.save(db);
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = new Date();

                    //String endTimeist = "";
                    String time_left="";
                    try {
                        d = parser.parse(endTime);
                        //endTimeist = ft1.format(d);
                        time_left = relTime(d);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }

                    Float currbid = detail.getPrice();
                    Integer est=detail.getEstibotValue();
                    List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid","b nc "+detail.getId()+" "+domain+" "+currbid));
                    row.add(new InlineKeyboardButton("Watch","w nc "+detail.getId()+" "+domain));
                    row.add(new InlineKeyboardButton("Track","t nc "+detail.getId()+" "+domain));

                    List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                    rows.add(row);
                    InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                    String text= "Namecheap Auction OUTBID\n\n"+domain+"\n\nTime Left: "+time_left+"\nCurrent Bid: "+currbid+"\nMin Next Bid: "+price+"\nOur Max Bid: "+maxprice+" \n\nEST: "+est+" \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                    try {
                        Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                                ,text,inlineKeyboardMarkup));
                    }
                    catch (Exception e)
                    {
                        logger.info(e.getMessage());
                    }
                    Date now= new Date();
                    String time= timeft.format(now);
                    notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + price ));
                    logger.info(time+": Namecheap: Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + price );

                }
            }
            else {

                if(pricee<=maxprice) {
                    Bidnc bid=new Bidnc(price);
                    ResponsePlaceBidNc pb = namecheapfeign.placeBidnc(bearer, ncid, bid);

                    //String domain= repo.findByNamecheapid(ncid).getDomain();
                    if (pb.getStatus().equals("processed")) {
                        Date d=new Date();
                        String time= timeft.format(d);
                        telegram.sendAlert(-1001763199668l,1004l, "Namecheap: Scheduled Bid PLACED for " + domain + " at price " + price + " USD");
                        notifRepo.save(new Notification("Namecheap",time,"Scheduled Bid PLACED for " + domain + " at price " + price + " USD"));
                        logger.info(time+": Scheduled Bid Placed of domain: " + domain+ " at price " + price + " USD");
                        logger.info(""+price);
                       if(pb.getLeadingBid())
                        {
                            Date now= d;
                        now.setSeconds(now.getSeconds()+45);
                        CheckOutbid checkOutbid= new CheckOutbid(price,maxprice,ncid,domain);
                        ScheduledFuture scheduledFuture= taskScheduler.scheduleAtFixedRate(checkOutbid,now,30000);
                        checkOutbid.setScheduledFuture(scheduledFuture);
                        updateTaskMap(domain,scheduledFuture,"co");
                        DBdetails db = repo.findByNamecheapid(ncid);
                        db.setMyLastBid(price);
                        db.setIsBidPlaced(true);
                        db.setCurrbid(String.valueOf(pb.getAmount()));
                        //db.setBidAmount();
                        db.setResult("Bid Placed");
                            repo.save(db);
                        }
                        else {
                            AuctionDetailNC detail1= namecheapfeign.getAuctionDetailbyId(bearer,ncid);
                            String timeId2= detail1.getEndDate().substring(0,detail.getEndDate().length()-5);
                            Float price1= detail1.getMinBid();
                           Float pricee1= detail1.getPrice();

                           if(pricee1<=maxprice) {

                                try {
                                    Date d1 = parser.parse(timeId2);
                                    d1.setSeconds(d1.getSeconds() - 10);
                                    ScheduledFuture task=taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, timeId2), d1);
                                    Date now= new Date();
                                     time= timeft.format(now);
                                    String bidist= ft1.format(d1);
                                    telegram.sendAlert(-1001763199668l,1004l,"Namecheap: Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " time " + bidist);
                                    notifRepo.save(new Notification("Namecheap",time,"Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " at time " + bidist));
                                    logger.info(time+": Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " time " + bidist);
                                    updateTaskMap(domain,task,"pb");
                                    DBdetails db= repo.findByNamecheapid(detail.getId());
                                    db.setResult("Bid Placed And Scheduled");
                                    repo.save(db);
                                    //DBdetails dBdetails= repo.findByNamecheapid(ncid);
                                    //dBdetails.setResult("Bid Placed And Scheduled");
                                    //repo.save(dBdetails);
                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                }
                            }
                            else
                            {
                                //notify
                                String endTime= detail1.getEndDate();
                                DBdetails db= repo.findByNamecheapid(detail1.getId());
                                db.setResult("Outbid");
                                repo.save(db);
                                endTime = endTime.substring(0, endTime.length() - 5);
                                 d = new Date();

                                //String endTimeist = "";
                                String time_left="";
                                try {
                                    d = parser.parse(endTime);
                                    //endTimeist = ft1.format(d);
                                    time_left = relTime(d);

                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                }

                                Float currbid = detail1.getPrice();
                                Integer est=detail1.getEstibotValue();
                                List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                                row.add(new InlineKeyboardButton("Bid","b nc "+detail1.getId()+" "+domain+" "+currbid));
                                row.add(new InlineKeyboardButton("Watch","w nc "+detail1.getId()+" "+domain));
                                row.add(new InlineKeyboardButton("Track","t nc "+detail1.getId()+" "+domain));

                                List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                                rows.add(row);
                                InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                                String text= "Namecheap Auction OUTBID\n\n"+domain+"\n\nTime Left: "+time_left+"\nCurrent Bid: "+currbid+"\nMin Next Bid: "+price1+"\nOur Max Bid: "+maxprice+" \n\nEST: "+est+" \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                                try {
                                    Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                                            ,text,inlineKeyboardMarkup));
                                }
                                catch (Exception e)
                                {
                                    logger.info(e.getMessage());
                                }
                                Date now= new Date();
                                 time= timeft.format(now);
                                notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + currbid ));
                                logger.info(time+": Namecheap: Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + currbid );

                            }

                        }
                    }
                        else {
                        DBdetails db = repo.findByNamecheapid(ncid);
                        db.setIsBidPlaced(false);
                        db.setCurrbid(String.valueOf(pb.getAmount()));
                        //db.setBidAmount();
                        db.setResult("Bid Not Placed");
                        Date d=new Date();
                        String time= timeft.format(d);
                        deleteTaskMap(domain);
                        notifRepo.save(new Notification("Namecheap",time,"Scheduled Bid NOT PLACED for " + domain + " at price " + price ));
                        logger.info(time+": Bid not placed of domain: " + domain + " at price " + price);

                        repo.save(db);
                    }
                }
                else
                {
                    //notify
                    String endTime= detail.getEndDate();
                    DBdetails db= repo.findByNamecheapid(detail.getId());
                    db.setResult("Outbid");
                    repo.save(db);
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = new Date();

                    //String endTimeist = "";
                    String time_left="";
                    try {
                        d = parser.parse(endTime);
                        //endTimeist = ft1.format(d);
                        time_left = relTime(d);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }

                    Float currbid = detail.getPrice();
                    Integer est=detail.getEstibotValue();
                    List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid","b nc "+detail.getId()+" "+domain+" "+currbid));
                    row.add(new InlineKeyboardButton("Watch","w nc "+detail.getId()+" "+domain));
                    row.add(new InlineKeyboardButton("Track","t nc "+detail.getId()+" "+domain));
                    List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                    rows.add(row);
                    InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                    String text= "Namecheap Auction OUTBID\n\n"+domain+"\n\nTime Left: "+time_left+"\nCurrent Bid: "+currbid+"\nMin Next Bid: "+price+"\nOur Max Bid: "+maxprice+" \n\nEST: "+est+" \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                    try {
                        Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                                ,text,inlineKeyboardMarkup));
                    }
                    catch (Exception e)
                    {
                        logger.info(e.getMessage());
                    }
                    Date now= new Date();
                    String time= timeft.format(now);
                    notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + price ));
                    logger.info(time+": Namecheap: Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + price );

                }
            }
        }
    }

}
