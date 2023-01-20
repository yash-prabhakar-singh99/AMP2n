package com.namekart.amp2.Controller;

import com.namekart.amp2.Entity.*;
import com.namekart.amp2.Feign.Namecheapfeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.NamecheapEntity.*;
import com.namekart.amp2.Repository.*;
import com.namekart.amp2.TelegramEntities.InlineKeyboardButton;
import com.namekart.amp2.TelegramEntities.InlineKeyboardMarkup;
import com.namekart.amp2.TelegramEntities.SendMessage;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class NamecheapController {

    String bearer = "Bearer ef7b03f63d8a94e2f083b991a74dd5852s5DuDtyOc9Ft1QZ5u0plxLpA0vlYdHFxEccAez6lh/wUyQNkOTCfqcOgrYMcvG4";

    @Autowired
    LiveMaprepo liveMaprepo;

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
    NotifRepo notifRepo;



    Logger logger =Logger.getLogger("Namecheap Yash");

    @PostMapping("/fetchdetailsnc")
    List<DBdetails> fetchdetailsnc(@RequestBody List<String> ddlist)
    {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone istTimeZone = TimeZone.getTimeZone("IST");
        parser.setTimeZone(utcTimeZone);
        ft1.setTimeZone(istTimeZone);
        List<DBdetails> list = new ArrayList<>();
        int n= ddlist.size();
        for(int i=0;i<n;i++) {
            String domain= ddlist.get(i).toLowerCase();
            ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
            AuctionDetailNC nc= rn.getItems().get(0);
            String endTime = nc.getEndDate();
            endTime = endTime.substring(0, endTime.length() - 5);
            Date d = new Date();
            String endTimeist="";
            String time_left;
            try {
                d = parser.parse(endTime);
                time_left= relTime(d);
            } catch (ParseException p) {
                logger.info(p.getMessage());
                continue;
            }
            endTimeist= ft1.format(d);
            logger.info(endTimeist);
            Float currbid = nc.getPrice();
            String ncid = nc.getId();
            Optional<DBdetails> op= Optional.ofNullable(repo.findByNamecheapid(ncid));
            DBdetails db=null;


            if(op.isPresent())
            {
                db= op.get();
                db.setCurrbid(String.valueOf(currbid));
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setTime_left(time_left);
                db.setEstibot(String.valueOf(nc.getEstibotValue()));
                db.setFetched(true);

                repo.save(db);
            }
            else
            {
                //AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                db = new DBdetails(domain,null,"Namecheap",String.valueOf(currbid),null,nc.getAuctionType(),"",endTime,endTimeist,"",false,ncid);
                db.setTime_left(time_left);
                db.setEstibot(String.valueOf(nc.getEstibotValue()));
                db.setFetched(true);
                repo.save(db);
            }

            list.add(db);
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

    void schedulesingle(String domain, String ncid, String bid)
    {

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone istTimeZone = TimeZone.getTimeZone("IST");
        parser.setTimeZone(utcTimeZone);
        ft1.setTimeZone(istTimeZone);


           Float maxprice= Float.valueOf(bid);
            try {
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
                AuctionDetailNC nc = rn.getItems().get(0);

                String endTime = nc.getEndDate();
                endTime = endTime.substring(0,endTime.length() - 5);
                Date d = new Date();
                String endTimeist="", bidplacetime="";
                String time_left="";
                try {
                    d = parser.parse(endTime);
                    endTimeist = ft1.format(d);
                    time_left = relTime(d);
                  //  d.setMinutes(d.getMinutes() - 4);
                   // bidplacetime = ft1.format(d);
                } catch (ParseException p) {
                    logger.info(p.getMessage());
                    return;
                }
                Date now= new Date();
                if(d.getTime()-now.getTime()<300000)
                {
                    d.setSeconds(d.getSeconds()-10);
                taskScheduler.schedule(new PlaceBid(ncid, maxprice,domain,endTime),d);
                }
                else
                {
                    d.setMinutes(d.getMinutes()-4);
                    taskScheduler.schedule(new PreCheck(ncid,domain,maxprice),d);
                }

                logger.info(endTimeist);
                Float currbid = nc.getPrice();


                Optional<DBdetails> op = Optional.ofNullable(repo.findByNamecheapid(ncid));
                DBdetails db = null;

                if (op.isPresent()) {
                    db = op.get();
                    db.setCurrbid(String.valueOf(currbid));
                    //db.setBidders(nc.get);
                    //db.setTime_left(ad.);
                    //db.setAge(aj.getAge());
                    //db.setEstibot(aj.getEstibot_appraisal());
                    //db.setAuctiontype();
                    db.setBidAmount(bid);
                    db.setResult("Bid Scheduled");
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    db.setBidplacetime(bidplacetime);
                    db.setEstibot(String.valueOf(nc.getEstibotValue()));

                    repo.save(db);
                } else {
                    db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", endTime, endTimeist, bidplacetime, false, ncid);
                    db.setTime_left(time_left);
                    db.setBidAmount(bid);
                    db.setEstibot(String.valueOf(nc.getEstibotValue()));

                    repo.save(db);
                }
                notifRepo.save(new Notification("Namecheap: BID SCHEDULED for domain: "+db.getDomain()+ " for price: "+db.getBidAmount()+" at "+db.getBidplacetime()));
                logger.info("Namecheap: BID SCHEDULED for domain: "+db.getDomain()+ " for price: "+db.getBidAmount()+" at "+db.getBidplacetime());


            }
            catch(Exception e)
            {
                notifRepo.save(new Notification("Namecheap: BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice));
                logger.info("Namecheap: BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice);

                logger.info(e.getMessage());
            }



    }

    List<Integer> bulkschedule(@RequestBody List<ArrayList<String>> ddlist)
    {

        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone istTimeZone = TimeZone.getTimeZone("IST");
        parser.setTimeZone(utcTimeZone);
        ft1.setTimeZone(istTimeZone);
        List<Integer> l= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();
            Float maxprice= Float.valueOf(ddlist.get(i).get(1));
            try {
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
                AuctionDetailNC nc = rn.getItems().get(0);
                String ncid = nc.getId();
                String endTime = nc.getEndDate();
                endTime = endTime.substring(0, endTime.length() - 5);
                Date d = new Date();
                String endTimeist="", bidplacetime="";
                String time_left;
                try {
                    d = parser.parse(endTime);
                    endTimeist = ft1.format(d);
                    time_left = relTime(d);
                    //  d.setMinutes(d.getMinutes() - 4);
                    // bidplacetime = ft1.format(d);
                } catch (ParseException p) {
                    logger.info(p.getMessage());
                    continue;
                }
                Date now= new Date();
                if(d.getTime()-now.getTime()<300000)
                {
                    d.setSeconds(d.getSeconds()-10);
                    taskScheduler.schedule(new PlaceBid(ncid, maxprice,domain,endTime),d);
                }
                else
                {
                    d.setMinutes(d.getMinutes()-4);
                    taskScheduler.schedule(new PreCheck(ncid,domain,maxprice),d);
                }

                logger.info(endTimeist);
                Float currbid = nc.getPrice();


                Optional<DBdetails> op = Optional.ofNullable(repo.findByNamecheapid(ncid));
                DBdetails db = null;

                if (op.isPresent()) {
                    db = op.get();
                    db.setCurrbid(String.valueOf(currbid));
                    //db.setBidders(nc.get);
                    //db.setTime_left(ad.);
                    //db.setAge(aj.getAge());
                    //db.setEstibot(aj.getEstibot_appraisal());
                    //db.setAuctiontype();
                    db.setBidAmount(ddlist.get(i).get(1));
                    db.setResult("Bid Scheduled");
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    db.setBidplacetime(bidplacetime);
                    db.setEstibot(String.valueOf(nc.getEstibotValue()));

                    repo.save(db);
                } else {
                    db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", endTime, endTimeist, bidplacetime, false, ncid);
                    db.setTime_left(time_left);
                    db.setBidAmount(ddlist.get(i).get(1));
                    db.setEstibot(String.valueOf(nc.getEstibotValue()));

                    repo.save(db);
                }
                notifRepo.save(new Notification("Namecheap: BID SCHEDULED for domain: "+db.getDomain()+ " for price: "+db.getBidAmount()+" at "+db.getBidplacetime()));
                logger.info("Namecheap: BID SCHEDULED for domain: "+db.getDomain()+ " for price: "+db.getBidAmount()+" at "+db.getBidplacetime());

                a++;
            }
            catch(Exception e)
            {
                notifRepo.save(new Notification("Namecheap: BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice));
                logger.info("Namecheap: BID NOT SCHEDULED for domain: "+domain+ " for price: "+maxprice);

                logger.info(e.getMessage());
            }

        }
        l.add(a);
        l.add(n);
        return l;
    }

    @PostMapping("/bulkbidnc")
    List<Integer> bulkbid(@RequestBody List<ArrayList<String>> ddlist)
    {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        TimeZone istTimeZone = TimeZone.getTimeZone("IST");
        parser.setTimeZone(utcTimeZone);
        ft1.setTimeZone(istTimeZone);
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
                        db.setEstibot(String.valueOf(nc.getEstibotValue()));

                        db.setIsBidPlaced(true);
                        db.setBidplacetime(bidplacetime);
                        repo.save(db);
                    } else {
                        db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Placed", endTime, endTimeist, bidplacetime, true, ncid);
                        db.setTime_left(time_left);
                        db.setEstibot(String.valueOf(nc.getEstibotValue()));

                        db.setBidAmount(ddlist.get(i).get(1));
                        repo.save(db);
                    }
                    a++;
                    notifRepo.save(new Notification("Namecheap: INSTANT BID PLACED for " + domain + " at price " + bid.getMaxAmount() + " USD at " + new Date()));

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
                        db.setEstibot(String.valueOf(nc.getEstibotValue()));

                        db.setBidplacetime(bidplacetime);
                        repo.save(db);
                    } else {
                        db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Not Placed", endTime, endTimeist, bidplacetime, false, ncid);
                        db.setTime_left(time_left);
                        db.setBidAmount(ddlist.get(i).get(1));
                        db.setEstibot(String.valueOf(nc.getEstibotValue()));

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
       // LiveMap lm = liveMaprepo.findById(1).get();
        LiveMap lm=null;

        if(liveMaprepo.findById(1).isPresent())
            lm = liveMaprepo.findById(1).get();
        else
        {
            lm= new LiveMap();
            liveMaprepo.save(lm);
        }

        //logger.info("yes");
        Map<String,String> map= lm.getMapnc();
        //logger.info("yes1");
        map.clear();
        //logger.info("yes2");
        liveMaprepo.save(lm);

        liveNcRepo.deleteAll();
        String bidCount="1_";
        Long t1= System.currentTimeMillis()/1000;
        Long t2=t1+7200;
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
        for(int i=0;i<n;i++)
        {
            liveNcRepo.save(items.get(i));
            map.put(items.get(i).getId(),items.get(i).getName().toLowerCase());
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
                        liveNcRepo.save(items1.get(i));
                        map.put(items1.get(i).getId(),items1.get(i).getName().toLowerCase());
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

                            liveNcRepo.save(items1.get(i));
                            map.put(items1.get(i).getId(),items1.get(i).getName().toLowerCase());
                        }
                    }
                }
            }

        liveMaprepo.save(lm);
        logger.info("Started Namecheap Live Service");
        scheduledFuture= taskScheduler.scheduleWithFixedDelay(new DetectLiveNc(t),18000);

        return rl;
    }

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

             SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
             SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
             TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
             TimeZone istTimeZone = TimeZone.getTimeZone("IST");
             parser.setTimeZone(utcTimeZone);
             ft1.setTimeZone(istTimeZone);
             logger.info("Namecheap Detect Live Service Ran");
             LiveMap lm = liveMaprepo.findById(1).get();
             //LiveMap lm = liveMaprepo.getReferenceById(1);

            Map<String,String> map= lm.getMapnc();
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

                     Float currbid = item.getPrice();
                     Float bid=0.0f;
                     //int age= item.get;
                     Float est=item.getEstibotValue();
                     List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                     List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
                     bid=currbid+10;
                     row.add(new InlineKeyboardButton("+10",bid+" "+item.getId()+" "+domain));
                     bid=currbid+25;
                     row.add(new InlineKeyboardButton("+25", bid+" "+item.getId()+" "+domain));
                     bid=currbid+50;
                     row.add(new InlineKeyboardButton("+50", bid+" "+item.getId()+" "+domain));
                     row1.add(new InlineKeyboardButton("custom", "c "+item.getId()+" "+domain+" "+currbid));
                     List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                     rows.add(row);
                     rows.add(row1);
                     InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);

                     String text= "Namecheap Live Detect \n \n"+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+currbid+" \n\nEST: "+est+" \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                     try {
                         Object obj = telegram.sendKeyboard(new SendMessage(-1001653862522L
                                 ,text,inlineKeyboardMarkup));
                     }
                     catch(RetryableException re)
                     {
                         liveMaprepo.save(lm);
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

                                 Float currbid = item1.getPrice();
                                 Float bid=0.0f;
                                 Float est = item1.getEstibotValue();
                                 List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                                 List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
                                 bid=currbid+10;
                                 row.add(new InlineKeyboardButton("+10",bid+" "+item1.getId()+" "+domain));
                                 bid=currbid+25;
                                 row.add(new InlineKeyboardButton("+25", bid+" "+item1.getId()+" "+domain));
                                 bid=currbid+50;
                                 row.add(new InlineKeyboardButton("+50", bid+" "+item1.getId()+" "+domain));
                                 row1.add(new InlineKeyboardButton("custom", "c "+item1.getId()+" "+domain+" "+currbid));
                                 List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                                 rows.add(row);
                                 rows.add(row1);
                                 InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                                 String text= "Namecheap Live Detect \n \n"+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+currbid+" \n\nEST: "+est+" \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                                 try {
                                     Object obj = telegram.sendKeyboard(new SendMessage(-1001653862522L
                                             ,text,inlineKeyboardMarkup));
                                 }
                                 catch(RetryableException re)
                                 {
                                     liveMaprepo.save(lm);
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

                                 Float currbid = item1.getPrice();

                                 Float bid=0.0f;
                                 Float est = item1.getEstibotValue();
                                 List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                                 List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
                                 bid=currbid+10;
                                 row.add(new InlineKeyboardButton("+10",bid+" "+item1.getId()+" "+domain));
                                 bid=currbid+25;
                                 row.add(new InlineKeyboardButton("+25", bid+" "+item1.getId()+" "+domain));
                                 bid=currbid+50;
                                 row.add(new InlineKeyboardButton("+50", bid+" "+item1.getId()+" "+domain));
                                 row1.add(new InlineKeyboardButton("custom", "c "+item1.getId()+" "+domain+" "+currbid));
                                 List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                                 rows.add(row);
                                 rows.add(row1);

                                 InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                                 String text= "Namecheap Live Detect \n \n"+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+currbid+" \n\nEST: "+est+" \n\nLink: "+"https://www.namecheap.com/market/"+domain;
                                 try {
                                     Object obj = telegram.sendKeyboard(new SendMessage(-1001653862522L
                                             ,text,inlineKeyboardMarkup));
                                 }
                                 catch(RetryableException re)
                                 {
                                     liveMaprepo.save(lm);
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
                                 catch(Exception e)
                                 {
                                     logger.info(e.getMessage());
                                 }
                                 Date date = new Date();
                                 String addtime = ft1.format(date);
                                 item1.setAddtime(addtime);
                                 item1.setTime_left(time_left);
                                 liveNcRepo.save(item1);
                             }
                         }
                     }
                 }
             }
             liveMaprepo.save(lm);
         }
    }

    public class PreCheck implements Runnable
    {
        String ncid,domain;
        Float maxprice;

        public PreCheck(String ncid, String domain, Float maxprice) {
            this.ncid = ncid;
            this.domain = domain;
            this.maxprice = maxprice;
        }

        @Override
        public void run()
        {
            AuctionDetailNC detail= namecheapfeign.getAuctionDetailbyId(bearer,ncid);
            Float minbid= detail.getMinBid();
            if(maxprice<minbid)
            {
               //notify
            }
            else
            {
                String endTime= detail.getEndDate().substring(0,detail.getEndDate().length()-5);
                try
                {
                    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    //SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
                    TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
                    //TimeZone istTimeZone = TimeZone.getTimeZone("IST");
                    parser.setTimeZone(utcTimeZone);
                    Date d= parser.parse(endTime);
                    d.setSeconds(d.getSeconds()-10);
                    taskScheduler.schedule(new PlaceBid(ncid,maxprice,domain,endTime),d);
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
        Float price,maxprice;
        String ncid;
        String domain;
        ScheduledFuture scheduledFuture;

        public CheckOutbid(Float price, Float maxprice, String ncid, String domain) {
            this.price = price;
            this.maxprice = maxprice;
            this.ncid = ncid;
            this.domain=domain;
        }

        @Override
        public void run()
        {
            AuctionDetailNC detail= namecheapfeign.getAuctionDetailbyId(bearer,ncid);
            Float pricenow = detail.getPrice();
            Float minbid   = detail.getMinBid();
            String endTime = detail.getEndDate();
            String status= detail.getStatus();

            if(status.equals("active")) {
                if (pricenow > price) {
                    if (minbid > maxprice)
                    {
                        //notify
                        scheduledFuture.cancel(true);
                    }
                    else {
                        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        //SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
                        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
                        //TimeZone istTimeZone = TimeZone.getTimeZone("IST");
                        parser.setTimeZone(utcTimeZone);
                        //ft1.setTimeZone(istTimeZone);
                        endTime = endTime.substring(0,endTime.length()-5);
                        try
                        {
                            Date d = parser.parse(endTime);
                            d.setSeconds(d.getSeconds() - 10);
                            taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime), d);
                            DBdetails dBdetails= repo.findByNamecheapid(ncid);
                            dBdetails.setResult("Bid Placed And Scheduled");
                            repo.save(dBdetails);
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
                if(detail.getPrice()==price)
                {
                    DBdetails dBdetails= repo.findByNamecheapid(ncid);
                    dBdetails.setResult("Won");
                    repo.save(dBdetails);
                }
                else
                {
                    DBdetails dBdetails= repo.findByNamecheapid(ncid);
                    dBdetails.setResult("Loss");
                    repo.save(dBdetails);
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
            AuctionDetailNC detail= namecheapfeign.getAuctionDetailbyId(bearer,ncid);
            String timeId1= detail.getEndDate().substring(0,detail.getEndDate().length()-5);
            Float price= detail.getMinBid();
            if(!timeId.equals(timeId1))
            {
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
                parser.setTimeZone(utcTimeZone);
                try
                {
                    Date d = parser.parse(timeId1);
                    d.setSeconds(d.getSeconds() - 10);
                    taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, timeId1), d);
                    //DBdetails dBdetails= repo.findByNamecheapid(ncid);
                    //dBdetails.setResult("Bid Placed And Scheduled");
                    //repo.save(dBdetails);
                }
                catch (ParseException p)
                {
                    logger.info(p.getMessage());
                }
            }
            else {

                if(price< maxprice) {
                    Bidnc bid=new Bidnc(price);
                    ResponsePlaceBidNc pb = namecheapfeign.placeBidnc(bearer, ncid, bid);
                    //String domain= repo.findByNamecheapid(ncid).getDomain();
                    if (pb.getStatus().equals("processed")) {
                        DBdetails db = repo.findByNamecheapid(ncid);
                        //db.setTime_left("0d, 0h, 9m");
                        db.setIsBidPlaced(true);
                        db.setCurrbid(String.valueOf(pb.getAmount()));
                        //db.setBidAmount();
                        db.setResult("Bid Placed");
                        notifRepo.save(new Notification("Namecheap: SCHEDULED BID PLACED for " + domain + " at price " + bid.getMaxAmount() + " USD at " + new Date()));

                        logger.info("Namecheap: scheduled bid placed of domain: " + domain);
                        repo.save(db);
                    } else {
                        DBdetails db = repo.findByNamecheapid(ncid);
                        db.setIsBidPlaced(false);
                        db.setCurrbid(String.valueOf(pb.getAmount()));
                        //db.setBidAmount();
                        db.setResult("Bid Not Placed");
                        notifRepo.save(new Notification("Namecheap: SCHEDULED BID NOT PLACED for " + domain + " at price " + bid.getMaxAmount() + " USD at " + new Date()));

                        logger.info("Namecheap: scheduled bid not placed of domain: " + domain);
                        repo.save(db);
                    }
                }
                else
                {
                    //notify
                }
            }
        }
    }

}
