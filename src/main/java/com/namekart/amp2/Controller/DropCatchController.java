package com.namekart.amp2.Controller;

import com.namekart.amp2.DCEntity.*;
import com.namekart.amp2.DotDBEntity.DotDbResponse;
import com.namekart.amp2.Entity.*;
import com.namekart.amp2.EstibotEntity.Estibot_Data;
import com.namekart.amp2.Feign.*;
import com.namekart.amp2.Repository.*;
import com.namekart.amp2.SettingsEntity.LiveFilterSettings;
import com.namekart.amp2.Status;
import com.namekart.amp2.TelegramEntities.EditMessage;
import com.namekart.amp2.TelegramEntities.InlineKeyboardButton;
import com.namekart.amp2.TelegramEntities.InlineKeyboardMarkup;
import com.namekart.amp2.TelegramEntities.SendMessage;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class DropCatchController {

    @Autowired
    LiveDCrepo liveDCrepo;
    @Autowired
    DropCatchFeign dropCatchFeign;

    @Autowired
    DropcatchFeign1 dropCatchFeign1;

    @Autowired
    MapWraprepo mapwraprepo;

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    MyRepo repo;

    @Autowired
    AsyncCalss asyncCalss;

    @Autowired
    Bidhisrepo bidhisrepo;

    @Autowired
    NotifRepo notifRepo;

    @Autowired
    LiveMaprepo liveMaprepo;

    Boolean b=true;
    Map<Long,String> map;
    Logger logger =Logger.getLogger("DropCatch Yash");

    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

    SimpleDateFormat timeft = new SimpleDateFormat("dd/MM HH:mm");

    TimeZone utc= TimeZone.getTimeZone("UTC");
    TimeZone ist = TimeZone.getTimeZone("IST");

    String filler="\n";
    String bearer;


    @Autowired
    @Qualifier(value = "workStealingPool")
    ForkJoinPool threadPoolExecutor;

    String text1,textob,textl,textli,summary="";
    StopWatch stopWatch;
    public DropCatchController(AllController controller)
    {
        parser.setTimeZone(utc);
        timeft.setTimeZone(ist);
        ft1.setTimeZone(ist);
        this.controller=controller;
        this.taskmap=controller.getTaskmap();
        //taskmap= new ConcurrentHashMap<>();
        map=new HashMap<>();
        for(int i=0;i<66;i++)
            filler=filler+"_";

        text1="Dropcatch"+filler+"\n";textob="Dropcatch OUTBID!!"+filler+"\n";
        textl="Dropcatch Live Detect"+filler+"\n";
        textli="Dropcatch Initial List Detect"+filler+"\n";

        stopWatch=new StopWatch();
    }

    @Scheduled(fixedRate = 1200000)
    void refreshBearer()
    {
        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign1.authorise(auth).getBody().getToken();
        bearer= "Bearer "+token;
    }

    ConcurrentMap<String, Status> taskmap;
    @Scheduled(cron = "0 30 08 ? * *", zone = "IST")
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
        if(taskmap.containsKey(domain))
        {   taskmap.get(domain).getFuture().cancel(false);
            taskmap.remove(domain);}
    }

    String liveFormat(String status, String timeLeft, String domain, Long minBid, String ourMaxBid, Integer EST)
    {
        if(ourMaxBid==null||ourMaxBid.isEmpty())
        {
            ourMaxBid="0";
        }
        String text="DC "+status+" - "+timeLeft+"\n"+domain+"\n"+"Price: "+minBid+" | Our Limit: "+ourMaxBid+"\n"+"EST: "+EST;
        return text;
    }
    String mute_unmute="\uD83D\uDD08/\uD83D\uDD07";
    InlineKeyboardMarkup getKeyboardWatch(String domain,Long auctionId, Long currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " dc "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " dc "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton(mute_unmute, "m" + " dc "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " dc "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.dropcatch.com/domain/" + domain);
        row1.add(link);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getKeyboardOb(String domain,Long auctionId, Long currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " dc "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " dc "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " dc "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.dropcatch.com/domain/" + domain);
        row1.add(link);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getKeyboardLive(String domain, Long auctionId,Long currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " dc "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " dc "+auctionId+" " + domain + " " + currbid));
        row.add(new InlineKeyboardButton("Watch", "w" + " dc " +auctionId+" "+ domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Track", "t" + " dc " +auctionId+" "+ domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " dc "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.dropcatch.com/domain/" + domain);
        row1.add(link);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }

    void sendOutbid(String status, String timeLeft, String domain, Long minBid, String ourMaxBid, Integer EST,Long auctionId)
    {
        String text=liveFormat(status,timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001866615838L,text,getKeyboardOb(domain,auctionId,minBid)));
    }

    void sendWatchlist(String status, String timeLeft, String domain, Long minBid, String ourMaxBid, Integer EST,Long auctionId)
    {
        String text=liveFormat(status,timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001887754426L,text,getKeyboardWatch(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, Long minBid, Integer EST,Long auctionId)
    {
        String text=liveFormat("Live Detect",timeLeft,domain,minBid,"",EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,22012l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, Long minBid, Integer EST,Long auctionId, String leads)
    {
        String text=liveFormat("Live Detect",timeLeft,domain,minBid,"",EST);
        if(leads!=null&&!leads.equals(""))
            text=text+"\n"+leads;
        telegram.sendKeyboard(new SendMessage(-1001763199668l,22012l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLiveI(String timeLeft, String domain, Long minBid,Integer EST,Long auctionId)
    {
        String text=liveFormat("Initial Detect",timeLeft,domain,minBid,"",EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,24112l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    @GetMapping("/getauctiondc")
    Object getAuctiondc(@RequestParam int id)
    {
//        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
//        String token = dropCatchFeign.authorise(auth).getBody().getToken();
//        String bearer= "Bearer "+token;
        ResponseEntity<AuctionDetailDC> r= dropCatchFeign.getAuctionDetail(bearer,id);
        System.out.println(r.getStatusCodeValue());
        AuctionDetailDC acdc= r.getBody();
        return acdc;
    }

    @GetMapping("/cancel/dc")
    void cancelBid(@RequestParam String domain,@RequestParam Long auctionId)
    {
        deleteTaskMap(domain);
        DBdetails db= repo.findByPlatformAndAuctionId("Dropcatch",auctionId);
        db.setResult("Bid Cancelled");
        db.setScheduled(false);
        repo.save(db);
    }
    @Scheduled(fixedRate = 120000)
    void refreshWatchlistDC()
    {
        if(b)
        {
            try{
                Thread.sleep(30000);
            }
            catch(InterruptedException io)
            {
                logger.info(io.getMessage());
            }
        }

        List<DBdetails> list= repo.findByPlatformAndWatchlistIsTrueAndTrackIsFalse("Dropcatch");
        //List<DBdetails> slist= repo.findByPlatformAndResultOrResultOrResultOrResult("Dropcatch", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
        //List<DBdetails> slist= repo.findScheduledDC();
        if(list==null||list.isEmpty())
            return;
//        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
//        String token = dropCatchFeign.authorise(auth).getBody().getToken();
//        String bearer= "Bearer "+token;
        if(list!=null&&list.size()!=0) {
            for (int i = 0; i < list.size(); i++) {
                DBdetails db = list.get(i);
                Long auctionId = db.getAuctionId();
                String domain = db.getDomain();

                try {

                    AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                    if (ad != null) {
                        String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                        Date date = null;
                        String endTimeist = "";
                        String time_left = "";
                        long currbid = ad.getHighBid();
                        long prevBid = Long.valueOf(db.getCurrbid());
                        try {
                            date = parser.parse(endTime);
                            endTimeist = ft1.format(date);
                            time_left = relTime(date);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                            continue;
                        }
                        Date now = new Date();
                        if (date.before(now)) {
                            db.setWatchlist(false);
                            if(db.getScheduled())
                            {
                                if (ad.isWinning())
                                    db.setResult("Won");
                                else
                                    db.setResult("Loss");
                            }
                            repo.save(db);
                            continue;
                        }
                        if(db.getScheduled())
                        {
                            if(ad.getHighBid()>Long.valueOf(db.getBidAmount()))
                            {
                                if(!db.getResult().equals("Outbid"))
                                {
                                    sendOutbid("Outbid",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

                                    db.setResult("Outbid");
                                }
                                //db.setResult("Outbid");
                            }
                            else if(b)
                            {
                                String time= timeft.format(now);
                                String bid= db.getBidAmount();
                                if (date.getTime() - now.getTime() < 270000) {
                                    ScheduledFuture place = taskScheduler.schedule(new PlaceBiddc1(domain, auctionId,Long.valueOf(db.getBidAmount()) , endTime), date);
                                    enterTaskMap(domain, place, "pb");

                                    telegram.sendAlert(-1001763199668l,1004l, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist);
                                    notifRepo.save(new Notification("Dropcatch", time, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist));
                                    logger.info(time + ": BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist + " i.e. " + date);

                                } else {
                                    Date date1 = new Date(date.getTime() - 270000);
                                    ScheduledFuture pre = taskScheduler.schedule(new Precheck(domain, auctionId, Long.valueOf(db.getBidAmount())), date1);
                                    enterTaskMap(domain, pre, "pc");
                                    telegram.sendAlert(-1001763199668l,1004l, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist);
                                    notifRepo.save(new Notification("Dropcatch", time, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist));
                                    logger.info(time + ": BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist + " i.e. " + date);
                                }
                            } else if (ad.getHighBid()>Long.valueOf(db.getBidAmount())&&db.isApproachWarn()) {
                                sendOutbid("Approaching Our Bid",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);
                                db.setApproachWarn(false);
                            }
                        }
                        else if(!db.getMute()) {
                            int nw = db.getNw();
                            if (nw == 0) {
                                if (date.getTime() - now.getTime() > 86400000)
                                    nw = 4;
                                else if (date.getTime() - now.getTime() > 3600000)
                                    nw = 3;
                                else if (date.getTime() - now.getTime() > 600000)
                                    nw = 2;
                                else if (date.getTime() - now.getTime() > 240000)
                                    nw = 1;
                                db.setNw(nw);
                            }
                            if (prevBid < currbid&&(!ad.isWinning())) {
                                sendWatchlist("New Bid Placed",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

                            }

                            if (date.getTime() - now.getTime() < 86400002 && date.getTime() - now.getTime() > 86280000 && nw >= 4) {
                                sendWatchlist("<24 hrs LEFT",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

                                nw = 3;
                                db.setNw(nw);
                            } else if (date.getTime() - now.getTime() < 3600002 && date.getTime() - now.getTime() > 3480000 && nw >= 3) {
                                sendWatchlist("<1 hr LEFT",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

                                nw = 2;
                                db.setNw(nw);
                            } else if (date.getTime() - now.getTime() < 600002 && date.getTime() - now.getTime() > 480000 && nw >= 2) {
                                sendWatchlist("<10 mins LEFT",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

                                nw = 1;
                                db.setNw(nw);
                            } else if (date.getTime() - now.getTime() < 240002 && date.getTime() - now.getTime() > 120000 && nw >= 1) {
                                sendWatchlist("<4 mins LEFT",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

                                nw = -1;
                                db.setNw(nw);
                            }
                        }

                        db.setCurrbid(String.valueOf(currbid));
                        db.setBidders(ad.getNumberOfBidders());
                        db.setEndTimepst(endTime);
                        db.setEndTimeist(endTimeist);
                        db.setTime_left(time_left);

                    } else {
                        db.setWatchlist(false);
                        db.setWasWatchlisted(true);
                        if (db.getScheduled())
                        {
                            if (!db.isBidPlaced()) {
                                Date now = new Date();
                                String time = timeft.format(now);
                                db.setResult("Loss");
                                telegram.sendAlert(-1001763199668l, 841l, "Dropcatch: Hush!! Lost auction of " + domain + " at price: " + db.getCurrbid());
                                notifRepo.save(new Notification("Dropcatch", time, "Hush!! Lost auction of " + domain + " at price: " + db.getCurrbid()));
                                logger.info(time + ": Hush!! Lost auction of " + domain + " at price: " + db.getCurrbid());
                                deleteTaskMap(domain);

                            } else {
                                AuctionResultdc r = dropCatchFeign.getAuctionResult(bearer, domain, 10).getBody().getItems().get(0);
                                String domain1 = r.getDomain().toLowerCase();
                                if (domain.equals(domain1)) {
                                    Date now = new Date();
                                    String time = timeft.format(now);
                                    if (r.getResult().equals("AuctionWon")) {
                                        telegram.sendAlert(-1001763199668l, 842l, "Dropcatch: Yippee!! Won auction of " + domain + " at price: " + r.getAmount());
                                        notifRepo.save(new Notification("Dropcatch", time, "Yippee!! Won auction of " + domain + " at price: " + r.getAmount()));
                                        db.setResult("Won");
                                        logger.info(time + ": Yippee!! Won auction of " + domain + " at price: " + r.getAmount());
                                    } else {
                                        db.setResult("Loss");
                                        telegram.sendAlert(-1001763199668l, "Dropcatch: Hush!! Lost auction of " + domain + " at price: " + r.getAmount());
                                        notifRepo.save(new Notification("Dropcatch", time, "Hush!! Lost auction of " + domain + " at price: " + r.getAmount()));
                                        logger.info(time + ": Hush!! Lost auction of " + domain + " at price: " + r.getAmount());

                                    }
                                    db.setScheduled(false);
                                    deleteTaskMap(domain);
                                }

                            }

                        }
                    }
                    repo.save(db);
                } catch (Exception e) {
                    Date now = new Date();
                    String time = timeft.format(now);
                    logger.info(time + ": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
                }

            }
        }
        //scheduled
        b=false;
    }
    InlineKeyboardMarkup refreshMarkup(InlineKeyboardMarkup markup,long currbid)
    {
        for(int i=0;i<markup.getInline_keyboard().size();i++)
        {
            List<InlineKeyboardButton> list= markup.getInline_keyboard().get(i);
            for(int j=0;j<list.size();i++)
            {
                InlineKeyboardButton button= list.get(j);
                String data= button.getCallback_data();
                String[] arr = data.split(" ");
                arr[4]=currbid+"";
                data="";
                for(int k=0;k<arr.length;k++)
                {
                    data=arr[i]+" ";
                }
                button.setCallback_data(data);
            }
        }
        return markup;
    }

    void refreshBot(String domain, Long id, Long chat_id, Long message_thread_id, InlineKeyboardMarkup markup)
    {
        Optional<DBdetails> op= Optional.ofNullable(repo.findByPlatformAndAuctionId("Dropcatch",id));
        DBdetails db=null;
        boolean b=op.isPresent();
        if(b)
            db=op.get();
        AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, id.intValue()).getBody();
        String text="Updated\uD83D\uDFE2\n\n";
        EditMessage editMessage= null;
        if(ad!=null) {
            String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
            Date date = null;
            String endTimeist = "";
            String time_left = "";
            long currbid = ad.getHighBid();
            try {
                date = parser.parse(endTime);
                endTimeist = ft1.format(date);
                time_left = relTime(date);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
           /* markup.getInline_keyboard().get(0).get(0).setCallback_data("b dc "+id+" "+domain+" "+currbid+" 50");
            markup.getInline_keyboard().get(0).get(0).setCallback_data("b dc "+id+" "+domain+" "+currbid);*/
            Date now= new Date();
            if(date.before(now))
            {
                if(b&&db.getScheduled())
                {
                    if (ad.isWinning())
                        text = text + "Dropcatch Auction WON!!\n" + filler + "\n" + domain + "\n\nLast Bid: " + ad.getHighBid()  + "\nOur Max Bid: " + db.getBidAmount()  ;
                    else
                        text = text + "Dropcatch Auction LOST!!\n" + filler + "\n" + domain + "\n\nLast Bid: " + ad.getHighBid()  + "\nOur Max Bid: " + db.getBidAmount()  ;
                }
                 else {
                    text = text + "Dropcatch Auction ENDED" + filler + "\n" + domain+ "\n\nLast Bid: " + ad.getHighBid() ;
                }
                //editMessage= new EditMessage(text,chat_id,message_id);
                telegram.sendAlert(chat_id,message_thread_id,text);
            }
           else if(b&&db.getScheduled()) {
                if (ad.getHighBid() > Long.valueOf(db.getBidAmount())) {
                    text = text + "Dropcatch Auction LOSING/OUTBID\n" + filler + "\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + ad.getHighBid() + "\nMin Next Bid: " + ad.getMinimumNextBid() + "\nOur Max Bid: " + db.getBidAmount()  ;
                }
                else {
                    text = text + "Dropcatch Auction WINNING\n" + filler + "\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + ad.getHighBid() + "\nMin Next Bid: " + ad.getMinimumNextBid() + "\nOur Max Bid: " + db.getBidAmount()  ;
                }
                //editMessage= new EditMessage(text,chat_id,message_id,markup);
                SendMessage sendMessage= new SendMessage(chat_id,text,refreshMarkup(markup,currbid));
                telegram.sendKeyboard(sendMessage);
            }
            else
            {
                text = text + "Dropcatch Auction\n" + filler + "\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + ad.getHighBid() + "\nMin Next Bid: " + ad.getMinimumNextBid();
               // editMessage= new EditMessage(text,chat_id,message_id,markup);
                SendMessage sendMessage= new SendMessage(chat_id,message_thread_id,text,refreshMarkup(markup,currbid));
                telegram.sendKeyboard(sendMessage);
            }
        }
        else
        {
            if(b)
            {if(db.getResult().equals("Won"))
            {
                text = text + "Dropcatch Auction WON!!\n" + filler + "\n" + domain + "\n\nLast Bid: " + db.getCurrbid()  + "\nOur Max Bid: " + db.getBidAmount()  ;
            }
            else if (db.getResult().equals("Loss"))
            {
                text = text + "Dropcatch Auction LOST!!\n" + filler + "\n" + domain + "\n\nLast Bid: " + db.getCurrbid()  + "\nOur Max Bid: " + db.getBidAmount()  ;

            }
            else if(db.getScheduled())
            {
                AuctionResultdc r = dropCatchFeign.getAuctionResult(bearer, domain, 10).getBody().getItems().get(0);
                String domain1 = r.getDomain().toLowerCase();
                if (domain.equals(domain1)) {

                    if (r.getResult().equals("AuctionWon")) {
                        text = text + "Dropcatch Auction WON!!\n" + filler + "\n" + domain + "\n\nLast Bid: " + db.getCurrbid()  + "\nOur Max Bid: " + db.getBidAmount()  ;
                    } else {
                        text = text + "Dropcatch Auction LOST!!\n" + filler + "\n" + domain + "\n\nLast Bid: " + db.getCurrbid()  + "\nOur Max Bid: " + db.getBidAmount()  ;
                    }
                }
            }}
            else
            {
                text = text + "Dropcatch Auction ENDED\n" + filler + "\n" + domain;
            }
           // editMessage= new EditMessage(text,chat_id,message_id);
            telegram.sendAlert(chat_id,message_thread_id,text);
        }

    }

    void watchlistLive(String domain, Long auctionId, boolean track)
    {
        try
        {
            CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);

        /*Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;*/

            AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
            String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
            Date date = new Date();
            String endTimeist = "";
            String time_left = "";
            long currbid=ad.getHighBid();
            try {
                date = parser.parse(endTime);
                endTimeist = ft1.format(date);
                time_left = relTime(date);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            Optional<DBdetails> op= Optional.of(repo.findByPlatformAndAuctionId("Dropcatch",auctionId));
            /*Integer gdv=0;
            if(op.isPresent())
            {
                gdv= op.get().getGdv();
                if(gdv==null||gdv==0)
                {
                    Optional<AuctionDetailDC> lauctiono= Optional.ofNullable(liveDCrepo.findByAuctionId(auctionId));
                    if(lauctiono.isPresent())
                    {
                        gdv=lauctiono.get().getGDV();
                    }
                }
            }*/

            DBdetails db=null;
            if(op.isPresent())
            {
                db.setCurrbid(String.valueOf(currbid));
                db.setBidders(ad.getNumberOfBidders());
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setTime_left(time_left);

            }
            else {
                //AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                db = new DBdetails(domain, auctionId, "Dropcatch", String.valueOf(ad.getHighBid()), ad.getNumberOfBidders(),time_left, ad.getType(), "", endTime, endTimeist, "", false);
                db.setFetched(true);
            }
            //db.setGdv(gdv);
            db.setWatchlist(true);
            if(track)
                db.setTrack(true);
            sendWatchlist("Watchlist",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

            controller.putESTinDBSingle(cf);
            repo.save(db);

        } catch (Exception e) {
            Date now = new Date();
            String time = timeft.format(now);
            logger.info(time + ": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
        }
    }
   /* @Scheduled(fixedRate = 120000)
    void refreshTrackDC()
    {
        List<DBdetails> list= repo.findByPlatformAndTrackIsTrue("Dropcatch");
        if(list==null||list.size()==0)
            return;
        *//*Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;*//*
        for(int i=0;i<list.size();i++) {
            DBdetails db = list.get(i);
            Long auctionId = db.getAuctionId();
            String domain = db.getDomain();

            try {

                AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                if(ad!=null) {
                    String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                    Date date = null;
                    String endTimeist = "";
                    String time_left = "";
                    long currbid = ad.getHighBid();
                    long prevBid=Long.valueOf(db.getCurrbid());
                    try {
                        date = parser.parse(endTime);
                        endTimeist = ft1.format(date);
                        time_left = relTime(date);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    Date now= new Date();
                    if(date.before(now))
                    {
                        db.setWatchlist(false);
                        repo.save(db);
                        continue;
                    }
                    String text =textl + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + currbid; //+ "\n \nGDV: " //+ gdv;
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid 50", "b" + " dc " +ad.getAuctionId() + " " + domain + " " + currbid+" 50"));
                    row.add(new InlineKeyboardButton("Bid", "b" + " dc " + ad.getAuctionId() + " " + domain + " " + currbid));
                    row1.add(new InlineKeyboardButton("Remove", "rw dc " + ad.getAuctionId() + " " + domain));
                    row1.add(new InlineKeyboardButton("Refresh", "r dc " + ad.getAuctionId() + " " + domain));
                    InlineKeyboardButton link= new InlineKeyboardButton("Link");
                    link.setUrl("https://www.dropcatch.com/domain/" + domain);
                    row1.add(link);

                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    rows.add(row);rows.add(row1);
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                    Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                            , text, inlineKeyboardMarkup));


                    db.setCurrbid(String.valueOf(currbid));
                    db.setBidders(ad.getNumberOfBidders());
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    repo.save(db);
                }
                else
                {
                    db.setWatchlist(false);
                    db.setTrack(false);
                    db.setWasWatchlisted(true);
                    repo.save(db);
                }

            } catch (Exception e) {
                Date now = new Date();
                String time = timeft.format(now);
                logger.info(time + ": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
            }

        }

    }
*/
    @Async
    CompletableFuture<Boolean> refreshScheduled()
    {

        List<DBdetails> list= repo.findByPlatformAndResultOrResultOrResultOrResult("Dropcatch", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
        if(list==null||list.size()==0)
            return CompletableFuture.completedFuture(true);
        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign1.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;
        for(int i=0;i<list.size();i++) {
            DBdetails db = list.get(i);
            Long auctionId = db.getAuctionId();
            String domain = db.getDomain();

            try {

                AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                if(ad!=null) {
                    String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                    Date date = null;
                    String endTimeist = "";
                    String time_left = "";
                    long currbid = ad.getHighBid();
                    try {
                        date = parser.parse(endTime);
                        endTimeist = ft1.format(date);
                        time_left = relTime(date);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    Date now= new Date();
                    if(date.before(now))
                    {
                        if(ad.isWinning())
                            db.setResult("Won");
                        else
                            db.setResult("Loss");

                        repo.save(db);
                        continue;
                    }
                    if(ad.getMinimumNextBid()>Long.valueOf(db.getBidAmount()))
                    db.setResult("Outbid");
                    db.setCurrbid(String.valueOf(currbid));
                    db.setBidders(ad.getNumberOfBidders());
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    repo.save(db);
                }
                else
                {
                    db.setResult("");
                    repo.save(db);
                    Date now= new Date();
                    String time= timeft.format(now);
                    logger.info(time+": Scheduled Auction not refreshed of domain: "+domain);
                    notifRepo.save(new Notification("Dropcatch",time,"Scheduled Auction not refreshed of domain: "+domain));

                }

            } catch (Exception e) {
                Date now = new Date();
                String time = timeft.format(now);
                logger.info(time + ": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
            }

        }
        return CompletableFuture.completedFuture(true);
    }



    void instantUpdateWatchlist(DBdetails db)
    {
        String domain= db.getDomain();
        Long auctionId= db.getAuctionId();
        try
        {
            AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
            String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
            Date date = new Date();
            String endTimeist = "";
            String time_left = "";
            long currbid=ad.getHighBid();
            try {
                date = parser.parse(endTime);
                endTimeist = ft1.format(date);
                time_left = relTime(date);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            sendWatchlist("Watchlist",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);


            db.setCurrbid(String.valueOf(currbid));
                db.setBidders(ad.getNumberOfBidders());
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setTime_left(time_left);

            //db.setGdv(gdv);
            db.setWatchlist(true);
           /* if(track)
                db.setTrack(true);*/
            repo.save(db);

        } catch (Exception e) {
            Date now = new Date();
            String time = timeft.format(now);
            logger.info(time + ": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
        }
    }
    @GetMapping("/getauctionsdc")
    ResponseAuctionList getAuctionsdc() {
        try{
            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign1.authorise(auth).getBody().getToken();
            String bearer= "Bearer "+token;
        ResponseEntity<ResponseAuctionList> re = dropCatchFeign.getAuctionDetails(bearer, 350, true, "Dropped", "EndTimeAsc");
      return re.getBody();
    }
       catch(Exception E)
            {
                //ResponseEntity<ResponseAuctionList> re = dropCatchFeign.getAuctionDetails(bearer, 350, true, "Dropped", "EndTimeAsc");
                logger.info(E.getMessage());
                //NotifRepo.save("")
                return null;
            }

    }

    @GetMapping("/postmapwrap")
            MapWrap postmapwrap()
    {
       return mapwraprepo.save(new MapWrap());
    }


    @GetMapping("/postmap")
    Map postmap(@RequestParam String domain, @RequestParam Long auctionId)
    {
      MapWrap mw =  mapwraprepo.getById(1);
      Map<String,Long> map= mw.getMap();
      map.put(domain,auctionId);
      mapwraprepo.save(mw);
      return map;

    }

    /*  @GetMapping("/refreshdcdropped")
    Map refresh()
    {
        MapWrap mw =  mapwraprepo.getById(1);
        Map<String,Long> map= mw.getMap();
        ResponseEntity<Object> r=  dropCatchFeign.getAuctionDetails(bearer,350,true,"Dropped" ,"EndTimeAsc" );
        if(r.getStatusCodeValue()==200) {
            ResponseAuctionList ral =(ResponseAuctionList) r.getBody();
            List<AuctionDetailDC> items = ral.getItems();
            int n= items.size();
            int c=0;
            Iterator<Map.Entry<String, Long>> it = map.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry<String, Long> entry = it.next();
                String s= entry.getKey();
                String st= items.get(0).getName();
                st=st.toLowerCase();
                if(!s.equals(st))
                {
                    it.remove();
                }
                else
                    break;
            }
            int n1= map.size();
            for(int i=n1;i<n;i++)
            {
                AuctionDetailDC ad=items.get(i);
                map.put(ad.getName().toLowerCase(),ad.getAuctionId());
            }
            mapwraprepo.save(mw);
            return map;
        }
        else{
            logger.info(r.getStatusCode().getReasonPhrase());
            return null;
        }
        }
*/


    @Scheduled(cron = "0 05 20 ? * *", zone = "UTC")
    @GetMapping("/refreshdcmap")
    List<Map> refresh1()
    {
        try{
        List<Map> list= new ArrayList<>();
//        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
//        String token = dropCatchFeign.authorise(auth).getBody().getToken();
//        String bearer= "Bearer "+token;
            MapWrap mw=null;
            logger.info("yo1");

            if(mapwraprepo.findById(1).isPresent())
        {
            logger.info("present");
            mw = mapwraprepo.findById(1).get();}
        else
        {
            mw= new MapWrap();
            mapwraprepo.save(mw);
        }
            //MapWrap mw = mapwraprepo.findById(1).get();

            Map<String, Long> map = mw.getMap();
        Map<Long,String> rm= mw.getRm();
        ResponseEntity<ResponseAuctionList> o = dropCatchFeign.getAuctionDetails(bearer, 350, true, "Dropped", "EndTimeAsc");

     map.clear();
     rm.clear();
     ResponseAuctionList r= o.getBody();
     List<AuctionDetailDC> items= r.getItems();
     int n= items.size();
     for(int i=0;i<n;i++)
     {AuctionDetailDC item= items.get(i);
         map.put(item.getName().toLowerCase(),item.getAuctionId() );
         rm.put(item.getAuctionId(),item.getName().toLowerCase());

     }
            list.add(map);
            list.add(rm);
            mapwraprepo.save(mw);
            return list;
        } catch(Exception E) {
            logger.info(E.getMessage());
            return null;
        }
    }
    @GetMapping("/clearmap")
    MapWrap clearmap()
    {
        MapWrap mw =  mapwraprepo.getById(1);
        Map<String,Long> map= mw.getMap();
        Map<Long,String> rm= mw.getRm();
        map.clear();
        rm.clear();
        mapwraprepo.save(mw);
        return mapwraprepo.getById(1);

    }

    @GetMapping("/getmap")
    MapWrap getmap()
    {
        MapWrap mw = mapwraprepo.getReferenceById(1);
        Map<String,Long> map = mw.getMap();
        Map<Long,String> rm= mw.getRm();
        //logger.info(rm.get(map.get("jxhbhome.com")));
        return mw;

    }

    @PostMapping("/bulkbidscheduledc1")
    List<Integer> mainmain1(@RequestBody List<List<String>> ddlist)
    {
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList1(ddlist);
        List<Long> ids= new ArrayList<>();
        MapWrap mw =  mapwraprepo.getById(1);
       // Map<String,Long> map= mw.getMap();
       // Map<Long,String> rm= mw.getRm();
        List<Integer> result= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
//        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
//        String token = dropCatchFeign.authorise(auth).getBody().getToken();
//        String bearer= "Bearer "+token;
        for(int i=0;i<n;i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();

            try {

                String maxbid= ddlist.get(i).get(1);
                Long bid = Long.parseLong(maxbid);
                //Long auctionId = map.get(domain);
                //AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                AuctionDetailDC ad = dropCatchFeign.getAuctionDetail1(bearer, domain,true).getBody().getItems().get(0);
                Long auctionId= ad.getAuctionId();
                Long minNextbid= ad.getMinimumNextBid();

                Date now = new Date();

                    String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);

                    Date date= new Date();
                    try {
                        date = parser.parse(endTime);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                if (date.after(now)) {
                    if(bid>=minNextbid) {
                    String endTimeist = ft1.format(date);
                    date.setSeconds(date.getSeconds() - 15);
                    String time = timeft.format(now);
                    if (date.getTime() - now.getTime() < 270000) {
                        ScheduledFuture place = taskScheduler.schedule(new PlaceBiddc1(domain, auctionId, bid, endTime), date);
                        enterTaskMap(domain, place, "pb");
                        a++;
                        telegram.sendAlert(-1001763199668l,1005l, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist);
                        notifRepo.save(new Notification("Dropcatch", time, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist));
                        logger.info(time + ": BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist + " i.e. " + date);

                    } else {
                        Date date1 = new Date(date.getTime() - 270000);
                        ScheduledFuture pre = taskScheduler.schedule(new Precheck(domain, auctionId, bid), date1);
                        enterTaskMap(domain, pre, "pc");

                        a++;
                        telegram.sendAlert(-1001763199668l,1005l,"Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist);
                        notifRepo.save(new Notification("Dropcatch", time, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist));
                        logger.info(time + ": BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist + " i.e. " + date);
                    }
                    String time_left = relTime(date);
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(auctionId));
                    if (!op.isPresent()) {
                        DBdetails db = new DBdetails(domain, auctionId, "Dropcatch", String.valueOf(ad.getHighBid()), maxbid, ad.getNumberOfBidders(), time_left, ad.getType(), "Bid Scheduled", endTime, endTimeist, "", false);
                        db.setScheduled(true);

                        repo.save(db);
                        ids.add(db.getId());
                    } else {
                        DBdetails db = op.get();
                        db.setCurrbid(String.valueOf(ad.getHighBid()));
                        db.setEndTimepst(ad.getEndTime());
                        db.setEndTimeist(endTimeist);
                        db.setResult("Bid Scheduled");
                        db.setTime_left(time_left);
                        db.setScheduled(true);
                        db.setBidAmount(maxbid);
                        db.setBidders(ad.getNumberOfBidders());
                        repo.save(db);
                        ids.add(db.getId());
                    }
                }
                else
                {
                    String time = ft1.format(now);
                    telegram.sendAlert(-930742733l,"Dropcatch: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextbid);
                    notifRepo.save(new Notification("Dropcatch", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextbid));
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextbid);

                }
            } else {
            CompletableFuture.runAsync(() ->
            {
                String time = timeft.format(now);
                telegram.sendAlert(-930742733l, "Dropcatch: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                notifRepo.save(new Notification("Dropcatch", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
            }, threadPoolExecutor);

        }
            }
            catch(Exception E)
            {
               Date now= new Date();
               String time= timeft.format(now);
                logger.info(time+": "+E.getMessage());
                notifRepo.save( new Notification("Dropcatch",time,"Bid not scheduled for domain: "+ domain+" with reason: "+E.getMessage()));
            }
        }
        controller.putESTinDB(cf);
       // asyncCalss.getGDVs(ids);
        result.add(a); result.add(n);
        return result;

    }

    BulkScheduleResponse mainmain1bot(@RequestBody List<List<String>> ddlist)
    {
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList1(ddlist);
        List<Long> ids= new ArrayList<>();
        BulkScheduleResponse res=null;
        String s="";
        //MapWrap mw =  mapwraprepo.getById(1);
        // Map<String,Long> map= mw.getMap();
        // Map<Long,String> rm= mw.getRm();
        List<Integer> result= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
//        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
//        String token = dropCatchFeign.authorise(auth).getBody().getToken();
//        String bearer= "Bearer "+token;
        for(int i=0;i<n;i++)
        {
            int l1=ddlist.get(i).size();
            String domain = ddlist.get(i).get(l1-2).toLowerCase();

            try {

                String maxbid= ddlist.get(i).get(l1-1);
                Long bid = Long.parseLong(maxbid);
                //Long auctionId = map.get(domain);
                //AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                AuctionDetailDC ad = dropCatchFeign.getAuctionDetail1(bearer, domain,true).getBody().getItems().get(0);
                Long auctionId= ad.getAuctionId();
                Long minNextbid= ad.getMinimumNextBid();

                Date now = new Date();

                String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);

                Date date= new Date();
                try {
                    date = parser.parse(endTime);

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                if (date.after(now)) {
                    if(bid>=minNextbid) {
                        String endTimeist = ft1.format(date);
                        date.setSeconds(date.getSeconds() - 15);
                        String time = timeft.format(now);
                        if (date.getTime() - now.getTime() < 270000) {
                            ScheduledFuture place = taskScheduler.schedule(new PlaceBiddc1(domain, auctionId, bid, endTime), date);
                            enterTaskMap(domain, place, "pb");
                            a++;
                            telegram.sendAlert(-1001763199668l,1005l, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist);
                            notifRepo.save(new Notification("Dropcatch", time, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist));
                            logger.info(time + ": BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist + " i.e. " + date);

                        } else {
                            Date date1 = new Date(date.getTime() - 270000);
                            ScheduledFuture pre = taskScheduler.schedule(new Precheck(domain, auctionId, bid), date1);
                            enterTaskMap(domain, pre, "pc");

                            a++;
                            telegram.sendAlert(-1001763199668l,1005l,"Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist);
                            notifRepo.save(new Notification("Dropcatch", time, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist));
                            logger.info(time + ": BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist + " i.e. " + date);
                        }
                        String time_left = relTime(date);
                        Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(auctionId));
                        if (!op.isPresent()) {
                            DBdetails db = new DBdetails(domain, auctionId, "Dropcatch", String.valueOf(ad.getHighBid()), maxbid, ad.getNumberOfBidders(), time_left, ad.getType(), "Bid Scheduled", endTime, endTimeist, "", false);
                            db.setScheduled(true);
                            repo.save(db);
                            ids.add(db.getId());
                        } else {
                            DBdetails db = op.get();
                            db.setCurrbid(String.valueOf(ad.getHighBid()));
                            db.setEndTimepst(ad.getEndTime());
                            db.setEndTimeist(endTimeist);
                            db.setResult("Bid Scheduled");
                            db.setTime_left(time_left);
                            db.setScheduled(true);
                            db.setBidAmount(maxbid);
                            db.setBidders(ad.getNumberOfBidders());
                            repo.save(db);
                            ids.add(db.getId());
                        }
                    }
                    else
                    {
                        String text="Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextbid;
                        s=s+text+"\n";
                        String time = ft1.format(now);
                        telegram.sendAlert(-930742733l,"Dropcatch: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextbid);
                        notifRepo.save(new Notification("Dropcatch", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextbid));
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextbid);

                    }
                } else {
                    String text="Bid NOT SCHEDULED for" + domain + " as auction has ended";
                    s=s+text+"\n";
                    CompletableFuture.runAsync(() ->
                    {
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Dropcatch: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                        notifRepo.save(new Notification("Dropcatch", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                    }, threadPoolExecutor);

                }
            }
            catch(Exception E)
            {
                Date now= new Date();
                String time= timeft.format(now);
                logger.info(time+": "+E.getMessage());
                notifRepo.save( new Notification("Dropcatch",time,"Bid not scheduled for domain: "+ domain+" with reason: "+E.getMessage()));
            }
        }
        controller.putESTinDB(cf);
        // asyncCalss.getGDVs(ids);
        result.add(a); result.add(n);
        res= new BulkScheduleResponse(result,s);
        return res;

    }

    @PostMapping("/placebackorderstandard")
    List<Integer> placeBackorderStandard(@RequestBody List<String> ddlist)
    {
        List<Integer> res= new ArrayList<>();
        int n=ddlist.size();
        List<BackOrderform> backorders=new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            String domain=ddlist.get(i).toLowerCase();
            backorders.add(new BackOrderform(domain,"Standard",59));
        }

       BackOrderResp resp= dropCatchFeign.placeBackorders(bearer,backorders).getBody();
        int a=resp.getSuccesses()!=null?resp.getSuccesses().size():0;
        res.add(a);res.add(n);
        return res;
    }

    @GetMapping("/testbo")
   BackOrderResp testbo()
    {
        List<BackOrderSuccess> successes= new ArrayList<>();
        successes.add(new BackOrderSuccess("abc.com","type","abc",10,9));
        return new BackOrderResp(successes,new ArrayList<BackOrderFailure>());
    }
    @PostMapping("/placebackorderdiscount")
    List<Integer> placeBackorderDiscount(@RequestBody List<List<String>> ddlist)
    {
        List<Integer> res= new ArrayList<>();
        int n=ddlist.size();
        List<BackOrderform> backorders=new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            String domain=ddlist.get(i).get(0).toLowerCase();
            int price= ddlist.get(i).size()==2?Integer.valueOf(ddlist.get(i).get(1).trim()):11;
            backorders.add(new BackOrderform(domain,"DiscountClub",price));
        }

        BackOrderResp resp= dropCatchFeign.placeBackorders(bearer,backorders).getBody();
        int a=resp.getSuccesses()!=null?resp.getSuccesses().size():0;
        res.add(a);res.add(n);
        return res;
    }

    @GetMapping("/schedulesingledc")
    long scheduleSingle(@RequestParam String domain,@RequestParam Long auctionId, @RequestParam Long bid)
    {
        try {
            CompletableFuture<Estibot_Data> cf = controller.getEstibotDomain(domain);

//            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
//            String token = dropCatchFeign.authorise(auth).getBody().getToken();
//            String bearer= "Bearer "+token;
            String domainf = domain.toLowerCase();
            AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
            if(ad!=null)
            {

            Long minNextBid = ad.getMinimumNextBid();

                String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                Date date = new Date();
                try {
                    date = parser.parse(endTime);

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                Date now = new Date();
                if (date.after(now)) {
                    if (minNextBid <= bid) {
                    date.setSeconds(date.getSeconds() - 15);

                    String time = timeft.format(now);
                    if (date.getTime() - now.getTime() < 270000) {
                        ScheduledFuture place = taskScheduler.schedule(new PlaceBiddc1(domain, auctionId, bid, endTime), date);
                        enterTaskMap(domain, place, "pb");

                    } else {
                        Date date1 = new Date(date.getTime() - 270000);
                        ScheduledFuture pre = taskScheduler.schedule(new Precheck(domain, auctionId, bid), date1);
                        enterTaskMap(domain, pre, "pc");

                    }
                    Date finalDate = date;
                    CompletableFuture.runAsync(() ->
                    {
                        String time_left = relTime(finalDate);
                        String endTimeist = ft1.format(finalDate);
                        telegram.sendAlert(-1001763199668l, 1005l, "Dropcatch: BID SCHEDULED for domain: " + domainf + " for max price: " + bid + " at " + endTimeist);
                        notifRepo.save(new Notification("Dropcatch", time, "Dropcatch: BID SCHEDULED for domain: " + domain + " for max price: " + bid + " at " + endTimeist));
                        logger.info(time + ": BID SCHEDULED for domain: " + domainf + " for max price: " + bid + " at " + endTimeist + " i.e. " + finalDate);

                        Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(auctionId));
                        DBdetails db = null;
                        if (!op.isPresent()) {
                            db = new DBdetails(domainf, auctionId, "Dropcatch", String.valueOf(ad.getHighBid()), String.valueOf(bid), ad.getNumberOfBidders(), time_left, ad.getType(), "Scheduled", endTime, endTimeist, "", false);


                        } else {
                            db = op.get();
                            db.setCurrbid(String.valueOf(ad.getHighBid()));
                            db.setEndTimepst(ad.getEndTime());
                            db.setEndTimeist(endTimeist);
                            db.setTime_left(time_left);
                            db.setBidAmount(String.valueOf(bid));
                            db.setBidders(ad.getNumberOfBidders());
                        }
                        db.setScheduled(true);

   /* Integer gdv = db.getGdv();
    if (gdv == null || gdv == 0) {
        gdv = liveDCrepo.findByAuctionId(auctionId).getGDV();
        db.setGdv(gdv);
    }*/
                        repo.save(db);
                    }, threadPoolExecutor);
                    controller.putESTinDBSingle(cf);
                    return 0;
                } else {
                    CompletableFuture.runAsync(
                            () ->
                            {
                                //Date now = new Date();
                                String time = ft1.format(now);
                                telegram.sendAlert(-930742733l, "Bid NOT SCHEDULED for" + domainf + " as bid value is lower than accepted bid of " + minNextBid);
                                notifRepo.save(new Notification("Dropcatch", time, "Bid NOT SCHEDULED for" + domainf + " as bid value is lower than accepted bid of " + minNextBid));
                                logger.info(time + ": Bid NOT SCHEDULED for " + domainf + " as bid value is lower than accepted bid of " + minNextBid);

                            }, threadPoolExecutor
                    );
                    return minNextBid;
                }
            } else {
                CompletableFuture.runAsync(() ->
                {
                    String time = timeft.format(now);
                    telegram.sendAlert(-930742733l, "Dropcatch: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                    notifRepo.save(new Notification("Dropcatch", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                }, threadPoolExecutor);
                return 2;
            }
        }
        else
            {
                CompletableFuture.runAsync(() ->
                {
                    Date now = new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-930742733l, "Dropcatch: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                    notifRepo.save(new Notification("Dropcatch", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                }, threadPoolExecutor);
                return 2;
            }
    }

            catch(Exception E)
    {
        Date now= new Date();
        String time= timeft.format(now);
        logger.info(time+": "+E.getMessage());
        notifRepo.save( new Notification("Dropcatch",time,"Bid not scheduled for domain: "+ domain+" with reason: "+E.getMessage()));
    }
    return 0;
    }
    public class Precheck implements Runnable
    {
        String domain;
        Long id;
        Long maxbid;

        public Precheck(String domain, Long id, Long maxbid) {
            this.domain = domain;
            this.id = id;
            this.maxbid = maxbid;
        }

        @Override
        public void run() {
            try {
//                Authorise auth = new Authorise("babyyoda:hawk", ":pvN|?'Sb4.Ah2N0t+7M");
//                String token = dropCatchFeign.authorise(auth).getBody().getToken();
//                String bearer = "Bearer " + token;
                DBdetails db= repo.findByPlatformAndAuctionId("Dropcatch",id);
                AuctionDetailDC ad =null;
                try{
                ad = dropCatchFeignB.getAuctionDetail(bearer, id.intValue()).getBody();
            }
            catch(Exception e)
            {
                db.setResult("API Error Fetch pc");
                repo.save(db);
                return;
            }
            Long minbid = ad.getMinimumNextBid();
                if (ad.getHighBid() > maxbid)
                {
                    String endTime = ad.getEndTime().substring(0,ad.getEndTime().length()-1);
                    Date date= null;
                    String endTimeist="";
                    String time_left="";
                    try
                    {
                        date = parser.parse(endTime);
                        time_left=relTime(date);
                        endTimeist=ft1.format(date);
                        // System.out.println(date);
                    }
                    catch(ParseException p)
                    {
                        logger.info(p.getMessage());}
                    db.setResult("Outbid");
                    db.setEndTimepst(endTime);
                    db.setTime_left(time_left);
                    db.setEndTimeist(endTimeist);
                    db.setCurrbid(String.valueOf(ad.getHighBid()));
                    repo.save(db);
                    //-1001814695777L
                    String text= textob+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+ad.getHighBid()+"\nMin Next Bid: "+ ad.getMinimumNextBid()+"\nOur Max Bid: "+db.getBidAmount();
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid 50", "b" + " dc " +ad.getAuctionId() + " " + domain + " " + ad.getHighBid()+" 50"));
                    row.add(new InlineKeyboardButton("Bid", "b" + " dc " + ad.getAuctionId() + " " + domain + " " + ad.getHighBid()));
                    row.add(new InlineKeyboardButton("Watch", "w" + " dc " + ad.getAuctionId() + " " + domain + " " + ad.getHighBid()));
                    row1.add(new InlineKeyboardButton("Track", "t dc " + ad.getAuctionId() + " " + domain));
                    row1.add(new InlineKeyboardButton("Refresh", "r dc " + ad.getAuctionId() + " " + domain));
                    InlineKeyboardButton link= new InlineKeyboardButton("Link");
                    link.setUrl("https://www.dropcatch.com/domain/" + domain);
                    row1.add(link);

                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    rows.add(row);rows.add(row1);
                    InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                    Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                            ,text,inlineKeyboardMarkup));
                    Date now= new Date();
                    String time= timeft.format(now);
                    notifRepo.save(new Notification("Dropcatch",time,"Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + minbid ));
                    logger.info(time+" : Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + minbid );

                    //notify

                    date.setMinutes(date.getMinutes()+30);
                    ScheduledFuture res= taskScheduler.schedule(new GetResultdc(domain,id),date);
                    updateTaskMap(domain,res,"gr");


                } else {

                    String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                    Date date = new Date();
                    try {
                        date = parser.parse(endTime);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                    date.setSeconds(date.getSeconds() - 15);
                    Date now= new Date();
                    String time= timeft.format(now);
                    String endTimeist= ft1.format(date);
                   ScheduledFuture place= taskScheduler.schedule(new PlaceBiddc1(domain, id, maxbid, endTime), date);
                    updateTaskMap(domain,place,"pb");

                    notifRepo.save(new Notification("Dropcatch",time,"Prechecking BID SCHEDULED for domain: "+domain+ " for max price: "+minbid+" at "+endTimeist));
                    logger.info(time+": Prechecking BID SCHEDULED for domain: "+domain+ " for max price: "+minbid+" at "+endTimeist+" i.e. "+date);

                }
            }
            catch(Exception e)
            {

                Date now= new Date();
                String time= timeft.format(now);
                logger.info(time+": "+e.getMessage());
                notifRepo.save(new Notification("Dropcatch",time,"Prechecking BID NOT SCHEDULED for domain: "+domain+ " with error: "+e.getMessage()));
            }
        }

    }

    @PostMapping("/bulkbidscheduledc")
    List<Integer> mainmain(@RequestBody List<List<String>> ddlist)
    {
        controller.getEstibotList1(ddlist);
        MapWrap mw =  mapwraprepo.getById(1);
        Map<String,Long> map= mw.getMap();
        int n= ddlist.size();
        int d=0;
        Map<String,List<Biddc>> m = new HashMap<>();
        Map<Long,String> rm= mw.getRm();
//        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
//        String token = dropCatchFeign.authorise(auth).getBody().getToken();
//        String bearer= "Bearer "+token;
        for(int i=0;i<n;i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();
            try {
                Long bid = Long.parseLong(ddlist.get(i).get(1));
                Long auctionId = map.get(domain);
                AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                Date date= new Date();
                String endTimeist="";
                String time_left="";
                try
                {
                    date = parser.parse(endTime);
                    time_left= relTime(date);
                    endTimeist=ft1.format(date);
                }
                catch(ParseException p)
                {logger.info(p.getMessage());}
               //taskScheduler.schedule(new PlaceBiddc(set.getValue()),date);
                Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(auctionId));

                if (!op.isPresent())
                {
                    DBdetails db = new DBdetails(domain, auctionId, "Dropcatch", String.valueOf(ad.getHighBid()), ad.getNumberOfBidders(),time_left ,ad.getType(), "", endTime, "", "", false);
                    repo.save(db);
                }
                else
                {
                    DBdetails db = op.get();
                    db.setCurrbid(String.valueOf(ad.getHighBid()));
                    db.setEndTimepst(ad.getEndTime());
                    db.setBidders(ad.getNumberOfBidders());
                }
                //logger.info(endTime);
                if (m.containsKey(endTime)) {
                    m.get(endTime).add(new Biddc(auctionId, bid));
                } else {
                    m.put(endTime, new ArrayList<Biddc>());
                    m.get(endTime).add(new Biddc(auctionId, bid));
                }
            }
            catch(Exception E)
            {
                Date now= new Date();
                String time= timeft.format(now);
                logger.info(E.getMessage());
                notifRepo.save( new Notification("Dropcatch",time,"Domain with name: "+ domain+" not found. See log for further info."));
            }
        }
        for (Map.Entry<String, List<Biddc>> set : m.entrySet())
        {
            String endTime = set.getKey();
            logger.info(endTime);
            Date date= new Date();
            String endTimeist="";
            String bidplacetime="";
            String time_left="";
            try{
                date = parser.parse(endTime);
                //date.setHours(date.getHours()+5);
                //date.setMinutes(date.getMinutes()+30);
                time_left= relTime(date);
                endTimeist=ft1.format(date);
                date.setMinutes(date.getMinutes()-4);
                bidplacetime=ft1.format(date);
                //System.out.println(date);
            }
            catch(ParseException p)
            {logger.info(p.getMessage());}
            taskScheduler.schedule(new PlaceBiddc(set.getValue()),date);
            int n1= set.getValue().size();
            d=d+n1;
            for(int i=0;i<n1;i++)
            {
                Biddc bid = set.getValue().get(i);
                Long auctionId = bid.getAuctionId();
                DBdetails db = repo.findByAuctionId(auctionId);
                db.setEndTimeist(endTimeist);
                db.setTime_left(time_left);
                db.setBidAmount(String.valueOf(bid.getAmount()));
                db.setBidplacetime(bidplacetime);
                db.setResult("Bid Scheduled");
                repo.save(db);
                Date now= new Date();
                String time= timeft.format(now);
                notifRepo.save(new Notification("Dropcatch",time,"BID SCHEDULED for domain: "+db.getDomain()+ " for price: "+db.getBidAmount()+" at "+db.getEndTimeist()));
                logger.info("Dropcatch: BID SCHEDULED for domain: "+db.getDomain()+ " for price: "+db.getBidAmount()+" at "+db.getEndTimeist());

            }
        }
List<Integer> list= new ArrayList<>();
        list.add(d);
        list.add(n);
        return list;
        }

        @PostMapping("/bulkbiddc")
        List<Integer> mainmaininstant(@RequestBody List<List<String>> ddlist)
        {
            CompletableFuture<List<Estibot_Data>> cf= controller.getEstibotList1(ddlist);
            List<Integer> l = new ArrayList<>();
            List<String> succ= new ArrayList<>();
            int n = ddlist.size();
            MapWrap mw =  mapwraprepo.getById(1);
            Map<String,Long> map= mw.getMap();
            Map<Long,String> rm = mw.getRm();
            Map<Long,Long> rb= new HashMap<>();
            Map<String,List<String>> m = new HashMap<>();
            List<Biddc> bids = new ArrayList<>();

            for(int i=0;i<n;i++)
            {
                String domain = ddlist.get(i).get(0).toLowerCase();
                try {
                    Long amount = Long.parseLong(ddlist.get(i).get(1));
                    Long auctionId = map.get(domain);
                    Biddc bid = new Biddc(auctionId, amount);
                    rb.put(auctionId, amount);
                    bids.add(bid);

                }
                catch(Exception E)
                {
                    Date now= new Date();
                    String time= timeft.format(now);
                    logger.info(E.getMessage());
                    notifRepo.save( new Notification("Dropcatch",time,"Domain with name: "+ domain+" not found. See log for further info."));

                }
            }

            /*Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign.authorise(auth).getBody().getToken();
            String bearer= "Bearer "+token;*/
            ResponsePlaceBiddc pb = dropCatchFeign.placeBiddc(bearer,bids).getBody();
            List<BidPlacedFailure> failures = pb.getFailures();
            List<BidPlacedSuccess> successes= pb.getSuccesses();

            for(int i=0;i< successes.size();i++)
            {

                BidPlacedSuccess s=successes.get(i);
                Long auctionId= s.getAuctionId();
                String domain = rm.get(auctionId);

                succ.add(domain);
                Date now= new Date();
                String time= timeft.format(now);
                telegram.sendAlert(-1001763199668l,1005l,"Dropcatch: Instant Bid PLACED for domain: "+domain+ " at price: "+rb.get(s.getAuctionId()));
                logger.info("Instant Bid PLACED for domain: "+domain+ " at price: "+rb.get(s.getAuctionId()));
                notifRepo.save(new Notification("Dropcatch",time,"Instant Bid PLACED for domain: "+domain+ " at price: "+rb.get(s.getAuctionId())));
                String endTime = s.getEndTime().substring(0,s.getEndTime().length()-1);
                if(m.containsKey(endTime))
                {
                    List<String> ml=m.get(endTime);
                    ml.add(domain);
                }
                else
                {
                    List<String> ml=new ArrayList<>();
                    ml.add(domain);
                    m.put(endTime,ml);

                }
                Date date= new Date();
                String endTimeist="";
                String time_left="";
                try{
                    date = parser.parse(endTime);
                    time_left= relTime(date);
                    endTimeist=ft1.format(date);
                    // System.out.println(date);
                }
                catch(ParseException p)
                {logger.info(p.getMessage());}
                Optional<DBdetails> op= Optional.ofNullable(repo.findByAuctionId(auctionId));
                if(op.isPresent())
                {
                    DBdetails db= op.get();
                    db.setResult("Bid Placed");
                    db.setIsBidPlaced(true);
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                    db.setCurrbid(String.valueOf(s.getHighBid()));
                    date= new Date();
                    String bidplacetime= ft1.format(date);
                    db.setBidplacetime(bidplacetime);
                    repo.save(db);
                }
                else
                {
                    date= new Date();
                    String bidplacetime=ft1.format(date);
                    AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                    DBdetails db = new DBdetails(domain,auctionId,"Dropcatch",String.valueOf(s.getHighBid()), ad.getNumberOfBidders(),time_left, ad.getType(),"Bid Placed",endTime,endTimeist,bidplacetime,true);


                    repo.save(db);
                }
            }

            for(int i=0;i< failures.size();i++)
            {
                BidPlacedFailure f=failures.get(i);
                Long auctionId= f.getAuctionId();
                String domain = rm.get(f.getAuctionId());
                Date now= new Date();
                String time= timeft.format(now);
                logger.info("Instant Bid NOT PLACED for domain: "+domain+ " for price: "+rb.get(f.getAuctionId())+" with error: "+f.getError().getDescription());
                notifRepo.save(new Notification("Dropcatch",time,"Instant Bid NOT PLACED for domain: "+domain+ " for price: "+rb.get(f.getAuctionId())+" with error: "+f.getError().getDescription()));
                Optional<DBdetails> op= Optional.ofNullable(repo.findByAuctionId(auctionId));
                if(op.isPresent())
                {
                    DBdetails db= op.get();
                    db.setResult("Bid Not Placed");
                    repo.save(db);
                }

            }
           /* for (Map.Entry<String, List<String>> set : m.entrySet())
            {
                String endTime=set.getKey();
                Date date=new Date();
                try{
                    date = parser.parse(endTime);

                    date.setMinutes(date.getMinutes()+45);

                    // System.out.println(date);
                }
                catch(ParseException p)
                {logger.info(p.getMessage());}
               ScheduledFuture task= taskScheduler.schedule(new GetResultdc(),date);

            }*/
            l.add(successes.size());
            l.add(ddlist.size());
            controller.putESTinDB(cf);
            return l;

        }
//@Autowired
AllController controller;

    @PostMapping("/bulkbidd")
    List<Integer> mainmaininsta(@RequestBody List<ArrayList<String>> ddlist)
    {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        return list;
    }

        @PostMapping("/fetchdetailsdc")
        List<DBdetails> fetchDetails(@RequestBody FetchReq body)
        {
            List<String> list=body.getDomains();
            Boolean watch= body.getWatch();
            List<DBdetails> l= new ArrayList<>();
            CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList(list);
//            MapWrap mw= mapwraprepo.getReferenceById(1);
//            Map<String,Long> map = mw.getMap();
            int n= list.size();
//            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
//            String token = dropCatchFeign.authorise(auth).getBody().getToken();
//            String bearer= "Bearer "+token;
            for(int i=0;i<n;i++)
            {

                String domain= list.get(i).toLowerCase();
                try {
                   // Long auctionId = map.get(domain);
                    AuctionDetailDC ad = dropCatchFeign.getAuctionDetail1(bearer, domain,true).getBody().getItems().get(0);
                    if(ad!=null&&ad.getName().equalsIgnoreCase(domain)) {
                        long auctionId= ad.getAuctionId();
                        Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(auctionId));
                        DBdetails db = null;
                        String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                        Date date = new Date();
                        String endTimeist = "";
                        String time_left = "";
                        try {
                            date = parser.parse(endTime);
                            endTimeist = ft1.format(date);
                            time_left = relTime(date);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                            continue;
                        }

                        if (op.isPresent()) {
                            db = op.get();
                            db.setCurrbid(String.valueOf(ad.getHighBid()));
                            db.setBidders(ad.getNumberOfBidders());
                            db.setEndTimepst(endTime);
                            db.setEndTimeist(endTimeist);
                            db.setTime_left(time_left);
                            db.setFetched(true);

                        } else {
                            //AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                            db = new DBdetails(domain, auctionId, "Dropcatch", String.valueOf(ad.getHighBid()), ad.getNumberOfBidders(), time_left, ad.getType(), "", endTime, endTimeist, "", false);
                            db.setFetched(true);

                        }
                        if (watch)
                        { db.setWatchlist(true);
                            Long currbid=ad.getHighBid();
                            sendWatchlist("Watchlist",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);
                        }
                        repo.save(db);
                        l.add(db);
                    }
                    else
                    {
                        Date now= new Date();
                        String time = timeft.format(now);
                        notifRepo.save(new Notification("Dropcatch",time,"Domain "+domain+" not found"));
                        logger.info(time+": Domain "+domain+" not found");

                    }
                }
                catch(Exception e)
                {
                    Date now= new Date();
                    String time = timeft.format(now);
                    notifRepo.save(new Notification("Dropcatch",time,"Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage()));
                    logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
                }
            }
            controller.putESTinDB(cf);
            return l;
        }

       String formdigit(long a)
       {
           if(a<10)
               return "0"+a;
           else return ""+a;
       }
  String relTime(Date d2)
  {
      Date d1 = new Date();
      long diff = d2.getTime() - d1.getTime();
      String s="";
      long min = TimeUnit.MILLISECONDS.toMinutes(diff)%60;

          s=formdigit(min)+"m"+s;

      long h = TimeUnit.MILLISECONDS.toHours(diff)%24;

          s=formdigit(h)+"h "+s;

      long d = TimeUnit.MILLISECONDS.toDays(diff)%365;
          s=formdigit(d)+"d "+s;

      return s;
  }

    String relTimelive(Date d2)
    {
        Date d1 = new Date();
        long diff = d2.getTime() - d1.getTime();
        String s="";
        long min = TimeUnit.MILLISECONDS.toMinutes(diff)%60;

        s=formdigit(min)+"m"+s;

        long h = TimeUnit.MILLISECONDS.toHours(diff)%24;

        s=formdigit(h)+"h "+s;

        return s;
    }



  @GetMapping("/date")
  void datet()
  {
          /*  Date d= new Date();
            Date d1= new Date();
            d.setMinutes(d.getMinutes() + 4410);

            long difference_In_Time
                    = d.getTime() - d1.getTime();

            System.out.println(difference_In_Time);

            long difference_In_Minutes
                    = TimeUnit
                    .MILLISECONDS
                    .toMinutes(difference_In_Time)
                    % 60;
            System.out.println(difference_In_Minutes);

            long difference_In_Hours
                    = TimeUnit
                    .MILLISECONDS
                    .toHours(difference_In_Time)
                    % 24;
            System.out.println(difference_In_Hours);

            long difference_In_Days
                    = TimeUnit
                    .MILLISECONDS
                    .toDays(difference_In_Time)
                    % 365;
            System.out.println(difference_In_Days);*/
      SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      String end= "2022-12-21T20:00:00";
      Date d= new Date();
      try{
         // d=parser.parse(end);
          d.setMinutes(d.getMinutes()-205);
          logger.info(parser.format(d)+"Z");
      }
      catch(Exception p)
      {
          logger.info(p.getMessage());
      }

        }

        public class GetResultdc implements Runnable {
            String domain;
            Long auctionId;

            public GetResultdc(String domain, Long auctionId) {
                this.domain = domain;
                this.auctionId = auctionId;
            }

            @Override
            public void run() {
                /*Authorise auth = new Authorise("babyyoda:hawk", ":pvN|?'Sb4.Ah2N0t+7M");
                String token = dropCatchFeign.authorise(auth).getBody().getToken();
                String bearer = "Bearer " + token;*/

                try {


                    DBdetails db = repo.findByDomain(domain);
                    AuctionResultdc r = dropCatchFeign.getAuctionResult(bearer, domain, 10).getBody().getItems().get(0);
                    String domain1 = r.getDomain().toLowerCase();
                    if (domain.equals(domain1)) {
                        if (r.getResult().equals("AuctionWon")) {
                            telegram.sendAlert(-1001763199668l,842l, "Dropcatch: Yippee!! Won auction of " + domain + " at price: " + db.getCurrbid());
                            db.setResult("Won");
                        } else db.setResult("Loss");
                        {
                            telegram.sendAlert(-1001763199668l,841l, "Dropcatch: Hush!! Lost auction of " + domain + " at price: " + db.getCurrbid());
                            repo.save(db);
                        }
                        db.setScheduled(false);
                        deleteTaskMap(domain);
                    } else {
                        Date d = new Date();
                        d.setMinutes(d.getMinutes() + 30);
                        ScheduledFuture res = taskScheduler.schedule(new GetResultdc(domain, db.getAuctionId()), d);
                        updateTaskMap(domain, res, "gr");

                    }


                } catch (Exception e) {
                    logger.info("Inside Get Result Scheduled Service: " + e.getMessage());
                }


            }
        }

    boolean isHighlight(String domain, Estibot_Data data, LiveFilterSettings settings)
    {
        Integer EST= data.getAppraised_value();
        domain=domain.toLowerCase();
        String[] dom=domain.split("\\.",2);
        String sld=dom[0];String tld= dom[1];
        if(settings.getNoHyphens()&&data.getNum_hyphens()>0)
            return false;
        if(settings.getNoNumbers()&&data.getHas_numbers()==1)
            return false;
        int l= domain.length();
        if(l<settings.getLowLength()||l>settings.getUpLength())
            return false;
        Map<String,Integer> extest=settings.getExtnEst();
        if(settings.getRestrictedExtns().contains(tld))
            return false;
        if(data.getAppraised_value()==-1)
            return true;
        if(extest.containsKey(tld))
        {
            if(EST< extest.get(tld))
                return false;
        }
        else if(settings.getNewExtnsSet().contains(tld)&&settings.getNewExtEsts()>EST)
            return false;
        else if(settings.getElseEsts()>EST)
            return false;
        return true;
    }

    boolean isHighlight(String domain, Estibot_Data data, LiveFilterSettings settings,String[] dom)
    {
        Integer EST= data.getAppraised_value();
        domain=domain.toLowerCase();

        String sld=dom[0];String tld= dom[1];
        if(settings.getNoHyphens()&&data.getNum_hyphens()>0)
            return false;
        if(settings.getNoNumbers()&&data.getHas_numbers()==1)
            return false;
        int l= domain.length();
        if(l<settings.getLowLength()||l>settings.getUpLength())
            return false;
        Map<String,Integer> extest=settings.getExtnEst();
        if(settings.getRestrictedExtns().contains(tld))
            return false;
        if(data.getAppraised_value()==-1)
            return true;
        if(extest.containsKey(tld))
        {
            if(EST< extest.get(tld))
                return false;
        }
        else if(settings.getNewExtnsSet().contains(tld)&&settings.getNewExtEsts()>EST)
            return false;
        else if(settings.getElseEsts()>EST)
            return false;
        return true;
    }

    void sendSummary()
    {
        if(stopWatch.isStarted())
        {
            stopWatch.split();
            if(stopWatch.getSplitTime()>240000)
            {
                if(summary!=null&&!summary.equals(""))
                telegram.sendAlert(-1001763199668l, 22012l,"Live Domains in Last 4-5 Minutes:\n\n"+summary);
                summary="";
                stopWatch.reset();stopWatch.start();
            }
        }
    }

    boolean healthCheck()
    {
        try {

        Date date= new Date();
        // date.setMinutes(date.getMinutes()-205);
        date.setDate(date.getDate()+1);
        date.setHours(2);
        String end= parser.format(date)+"Z";
        ResponseEntity<ResponseAuctionList> o = dropCatchFeign.getAuctionDetailslive1(bearer, 250, true, "EndTimeAsc",end,true);
        return o.getBody().getItems().size()>0;
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
            return false;
        }
    }

    @Autowired
    LiveFilterSettingsRepo settingsRepo;
    @Scheduled(cron = "0 00 23 ? * *", zone = "IST")
    @GetMapping("/startlivedc")
        ResponseAuctionList startlivedc()
        {
            int l=0;
            logger.info("Starting Live Service");
            Optional<LiveMap> o1= liveMaprepo.findById(3);
            LiveMap lm;
            if(o1.isEmpty())
            {
                lm= new LiveMap(3);
                liveMaprepo.save(lm);
                lm=liveMaprepo.findById(3).get();
            }
            else
                lm=o1.get();
            //liveDCrepo.deleteAll();
            LiveFilterSettings settings= settingsRepo.findById(1).get();
            Date date= new Date();
           // date.setMinutes(date.getMinutes()-205);
            date.setHours(date.getHours()+2);
            String end= parser.format(date)+"Z";
            ResponseEntity<ResponseAuctionList> o = dropCatchFeign.getAuctionDetailslive1(bearer, 250, true, "EndTimeAsc",end,true);
           // LiveMap lm= liveMaprepo.findById(1).get();
           // map.clear();
            map=lm.getMapdc();
            String est="";
            ResponseAuctionList rl= o.getBody();
            List<AuctionDetailDC> items = rl.getItems();
            for(int i=0;i<items.size();i++)
            {
                AuctionDetailDC item= items.get(i);
                if(!map.containsKey(item.getAuctionId())) {
                    //logger.info(item.getName());
                    item.setInitialList(true);
                    try {
                        liveDCrepo.save(item);
                        //logger.info(item.getName() + i);
                    } catch (Exception e) {
                        continue;
                    }
                    l = Math.max(l, item.getName().length());
                    map.put(item.getAuctionId(), item.getName().toLowerCase());
                    est = est + item.getName() + ">>";
                }

            }
           List<Estibot_Data> estibot_dataList=  controller.getEstibotsSync(est);
            for(int i=0;i<estibot_dataList.size();i++)
            {
                Estibot_Data data= estibot_dataList.get(i);
                AuctionDetailDC dc= liveDCrepo.findByNameIgnoreCase(data.getDomain());
                dc.setEST(data.getAppraised_value());
                boolean highlight=isHighlight(data.getDomain(),data,settings);
                dc.setHighlight(highlight);

                if(highlight) {
                    String domain=data.getDomain();
                    Long auctionId=dc.getAuctionId();
                    String endTime= dc.getEndTime().substring(0,dc.getEndTime().length()-1);
                    Date end1=null;
                    String relTime="";
                    try{
                        end1=parser.parse(endTime);
                        relTime=relTimelive(end1);
                    }
                    catch(ParseException p)
                    {
                        logger.info(p.getMessage());
                    }
                    Long price= dc.getHighBid();
                    sendLiveI(relTime,domain,dc.getMinimumNextBid(), data.getAppraised_value(),auctionId);


                }
                liveDCrepo.save(dc);
            }
            summary="";
            stopWatch.start();
            ScheduledFuture scheduledFuture=taskScheduler.scheduleWithFixedDelay(new DetectLivedc(end),60000);
            Date d= new Date();
            d.setMinutes(d.getMinutes()+130);
            taskScheduler.schedule(new Stoplivedc(scheduledFuture),d);
            try {
               // sendInitialList(l);
                sendHighlights(l);
            }
            catch (Exception e)
            {
                logger.info(e.getMessage());
            }
            logger.info("Started live service");
            return rl;
        }

        @GetMapping("/getdclivee")
        ResponseAuctionList getListLivee()
        {
            ResponseEntity<ResponseAuctionList> rl= dropCatchFeign.getAuctionDetailslive1(bearer,350,true,"EndTimeAsc","2023-05-25T20:00:00Z",true);

            return rl.getBody();
        }
@Autowired
    Telegram telegram;
    public class Stoplivedc implements Runnable
    {
        ScheduledFuture scheduledFuture;
        public Stoplivedc(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        @Override
        public void run()
        {
            scheduledFuture.cancel(false);
            map.clear();
            int l=0;
            Optional<LiveMap> o= liveMaprepo.findById(3);
            LiveMap lm;
            if(o.isEmpty())
            {
                lm= new LiveMap(3);
            }
            else
                lm=o.get();

            LiveFilterSettings settings= settingsRepo.findById(1).get();
            Map<Long,String> map1= lm.getMapdc();
            map1.clear();
            liveDCrepo.deleteAll();
            Date date= new Date();
            // date.setMinutes(date.getMinutes()-205);
            date.setHours(date.getHours()+24);
            String end= parser.format(date)+"Z";
            ResponseEntity<ResponseAuctionList> o1 = dropCatchFeign.getAuctionDetailslive1(bearer, 250, true, "EndTimeAsc",end,true);
            // LiveMap lm= liveMaprepo.findById(1).get();
            ResponseAuctionList rl= o1.getBody();
            String est="";
            List<AuctionDetailDC> items = rl.getItems();
            for(int i=0;i<items.size();i++)
            {
                AuctionDetailDC item= items.get(i);
                est=est+item.getName()+">>";
                l=Math.max(l,item.getName().length());
                item.setEndList(true);
                map1.put(item.getAuctionId(),item.getName().toLowerCase());
                try {
                    liveDCrepo.save(item);
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage());
                }
            }

            liveMaprepo.save(lm);
            List<Estibot_Data> estibot_dataList=  controller.getEstibotsSync(est);
            for(int i=0;i<estibot_dataList.size();i++)
            {
                Estibot_Data data= estibot_dataList.get(i);
                AuctionDetailDC dc= liveDCrepo.findByNameIgnoreCase(data.getDomain());
                dc.setEST(data.getAppraised_value());
                dc.setHighlight(isHighlight(data.getDomain(),data,settings));
                liveDCrepo.save(dc);
            }
            try {
                //sendEndList(l);
                sendEndHighlights(l);
            }
            catch (Exception e)
            {
                logger.info(e.getMessage());
            }
            stopWatch.reset();summary="";
        }
    }
    void sendList(long chat_id)
    {
        int n=25;
        //           currbid,EST,separators,space around separators
        int t= n+    6  +6  +  2   +     4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dropcatch Initial List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %6s | %6s%n","Domain","Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<AuctionDetailDC> list=liveDCrepo.findByInitialListTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                AuctionDetailDC lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %6d | %6d%n", lnc.getName(), lnc.getHighBid(),lnc.getEST());


            }
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }


    void sendInitialList(int n)
    {
        //int n=32;
        //           currbid,EST,separators,space around separators
        int t= n+    6  +6  +  2   +     4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dropcatch Initial List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %6s | %6s%n","Domain","Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<AuctionDetailDC> list=liveDCrepo.findByInitialListTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                AuctionDetailDC lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %6d | %6d%n", lnc.getName(), lnc.getHighBid(),lnc.getEST());


            }
            telegram.sendAlert(-1001763199668l,22012l,"<pre>"+s+"</pre>","HTML");
            // System.out.println(s);
           telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }
    void sendList(int n)
    {
        //int n=32;
        //           currbid,EST,separators,space around separators
        int t= n+    6  +6  +  2   +     4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dropcatch Initial List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %6s | %6s%n","Domain","Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<AuctionDetailDC> list=liveDCrepo.findAllByOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                AuctionDetailDC lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %6d | %6d%n", lnc.getName(), lnc.getHighBid(),lnc.getEST());


            }
            telegram.sendAlert(-1001763199668l,22012l,"<pre>"+s+"</pre>","HTML");
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendEndList(int n)
    {
        int t= n+    6 +6   +  2   +     4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dropcatch Next Day List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-6s | %6s%n","Domain","Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<AuctionDetailDC> list=liveDCrepo.findByEndListTrue();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                AuctionDetailDC lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6d | %6d%n", lnc.getName(), lnc.getHighBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,22012l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");

            l=l-d;
            s="";
        }

    }

    void sendInitialHighlights(int n)
    {
        //int n=32;
        //           currbid,EST,separators,space around separators
        int t= n+    6  +6  +  2   +     4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dropcatch Initial Highlights");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %6s | %6s%n","Domain","Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<AuctionDetailDC> list=liveDCrepo.findByInitialListTrueAndHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                AuctionDetailDC lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %6d | %6d%n", lnc.getName(), lnc.getHighBid(),lnc.getEST());


            }
            telegram.sendAlert(-1001763199668l,22012l,"<pre>"+s+"</pre>","HTML");
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendHighlights(int n)
    {
        //int n=32;
        //           currbid,EST,separators,space around separators
        int t= n+    6  +6  +  2   +     4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dropcatch Initial Highlights");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %6s | %6s%n","Domain","Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<AuctionDetailDC> list=liveDCrepo.findByHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                AuctionDetailDC lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %6d | %6d%n", lnc.getName(), lnc.getHighBid(),lnc.getEST());


            }
            telegram.sendAlert(-1001763199668l,22012l,"<pre>"+s+"</pre>","HTML");
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendEndHighlights(int n)
    {
        int t= n+    6 +6   +  2   +     4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dropcatch Next Day Highlights");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-6s | %6s%n","Domain","Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<AuctionDetailDC> list=liveDCrepo.findByEndListTrueAndHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                AuctionDetailDC lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6d | %6d%n", lnc.getName(), lnc.getHighBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,22012l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");

            l=l-d;
            s="";
        }

    }

    String dotdbkey="Token 6c2753c5bac47cd06cc087368fae3376";
    @Autowired
    DotDBFeign dotDBFeign;
    String getLeads(String keyword)
    {
        try {
            String s = "";
            DotDbResponse res = dotDBFeign.getLeads(dotdbkey, keyword, "active");
            if (res.getMatches() != null && res.getMatches().size() != 0 && res.getMatches().get(0).getName().equalsIgnoreCase(keyword)) {
                List<String> suff = res.getMatches().get(0).getSuffixes();
                for (int i = 0; i < suff.size(); i++)
                    s = s + keyword + suff.get(i) + ",";
            } else {
                keyword = "the" + keyword;
                DotDbResponse res1 = dotDBFeign.getLeads(dotdbkey, keyword, "active");
                if (res1.getMatches() != null && res1.getMatches().size() != 0 && res1.getMatches().get(0).getName().equalsIgnoreCase(keyword)) {
                    List<String> suff = res1.getMatches().get(0).getSuffixes();
                    for (int i = 0; i < suff.size(); i++)
                        s = s + keyword + suff.get(i) + ",";
                }
            }
            return s;
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
            return "";
        }
    }

    public class DetectLivedc implements Runnable
        {

            String endTime;

            public DetectLivedc(String endTime) {

                this.endTime = endTime;
            }

            @Override
            public void run()
            {

                logger.info("Live detect service running");
               /* Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
                String token = dropCatchFeign.authorise(auth).getBody().getToken();
                String bearer= "Bearer "+token;*/

                LiveFilterSettings settings= settingsRepo.findById(1).get();
                List<AuctionDetailDC> items = dropCatchFeign.getAuctionDetailslive1(bearer,350,true,"EndTimeAsc",endTime,true).getBody().getItems();
                for(int i=0;i< items.size();i++)
                {
                    try{
                    AuctionDetailDC item= items.get(i);
                    Long auctionId= item.getAuctionId();
                    String domain= item.getName().toLowerCase();
                    if(!map.containsKey(auctionId))
                    {
                        map.put(auctionId,domain);
                        logger.info("Detected Live Domain: "+domain);
                        summary=summary+domain+"\n";
                        Estibot_Data estibot_data= controller.getEstibotSync(domain);
                        item.setEST(estibot_data.getAppraised_value());
                        String endTime= item.getEndTime().substring(0,item.getEndTime().length()-1);
                        Date end=null;
                        String relTime="";
                        try{
                            end=parser.parse(endTime);
                            relTime=relTimelive(end);
                        }
                        catch(ParseException p)
                        {
                            logger.info(p.getMessage());
                        }
                        Date now= new Date();
                        String addTime= ft1.format(now);
                        Long price= item.getHighBid();
                        String[] dom=domain.split("\\.",2);
                        boolean highlight=isHighlight(domain,estibot_data,settings,dom);
                        item.setHighlight(highlight);
                        if(highlight) {
                            if(dom[1].equalsIgnoreCase("com"))
                            {
                                String leads= getLeads(dom[0]);
                                sendLive(relTime,domain,price,estibot_data.getAppraised_value(),auctionId,leads);
                            }
                            else sendLive(relTime,domain,price,estibot_data.getAppraised_value(),auctionId);
                        }
                        item.setAddTime(addTime);
                        item.setTimeLeft(relTime);
                        item.setLive(true);
                        try {
                            liveDCrepo.save(item);
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }}
                    catch(Exception e)
                    {
                        logger.info(e.getMessage());
                    }
                }
                sendSummary();
            }
        }

        @GetMapping("/tryschedule")
        Boolean trysc()
        {
            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign1.authorise(auth).getBody().getToken();
            String bearer= "Bearer "+token;
            Date now= new Date();
            now.setMinutes(now.getMinutes()+1);
            Try try1= new Try(bearer);
            logger.info("1");
           ScheduledFuture scheduledFuture= taskScheduler.scheduleAtFixedRate(try1,now,120000);
           try1.setScheduledFuture(scheduledFuture);
            return true;
        }

        public class Try implements Runnable
        {
            ScheduledFuture scheduledFuture;

            String bearer;

            public Try(String bearer) {
                this.bearer = bearer;
            }

            public void setScheduledFuture(ScheduledFuture scheduledFuture) {
                this.scheduledFuture = scheduledFuture;
            }

            @Override
            public void run() {
                try {
                    AuctionDetailDC dc= dropCatchFeign.getAuctionDetail(bearer,3615601).getBody();
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage());
                    scheduledFuture.cancel(false);
                }
            }
        }

        public class CheckOutBid implements Runnable
        {
            String domain;
            Long auctionId;
            Long maxbid,price;

            ScheduledFuture scheduledFuture;

            public void setScheduledFuture(ScheduledFuture scheduledFuture) {
                this.scheduledFuture = scheduledFuture;
            }

            public CheckOutBid(String domain, Long auctionId, Long maxbid, Long price)
            {
                this.domain = domain;
                this.auctionId = auctionId;
                this.maxbid = maxbid;
                this.price=price;
            }

            @Override
            public void run() {
                /*Authorise auth = new Authorise("babyyoda:hawk", ":pvN|?'Sb4.Ah2N0t+7M");
                String token = dropCatchFeign.authorise(auth).getBody().getToken();
                String bearer = "Bearer " + token;*/
                DBdetails db= repo.findByPlatformAndAuctionId("Dropcatch",auctionId);
                AuctionDetailDC ad =null;
                try{
                    ad = dropCatchFeignB.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                }
                catch(Exception e)
                {
                    db.setResult("API Error Fetch co");
                    repo.save(db);
                    return;
                }
                if (ad!=null)
                {
                    Boolean winning = ad.isWinning();
                    Long minbid = ad.getMinimumNextBid();


                    if (!winning)
                    {
                        if (ad.getHighBid() > maxbid)
                        {
                            //notify
                            String endTime = ad.getEndTime().substring(0,ad.getEndTime().length()-1);
                            Date date= null;
                            String endTimeist="";
                            String time_left="";
                            try
                            {
                                date = parser.parse(endTime);
                                time_left=relTime(date);
                                endTimeist=ft1.format(date);
                                // System.out.println(date);
                            }
                            catch(ParseException p)
                            {
                                logger.info(p.getMessage());}
                            db.setResult("Outbid");
                            db.setEndTimepst(endTime);
                            db.setTime_left(time_left);
                            db.setEndTimeist(endTimeist);
                            db.setCurrbid(String.valueOf(ad.getHighBid()));
                            repo.save(db);
                            //-1001814695777L
                            sendOutbid("Outbid",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

                            Date now= new Date();
                            String time= timeft.format(now);
                            notifRepo.save(new Notification("Dropcatch",time,"Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + minbid ));
                            logger.info(time+" : Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + minbid );
                            date.setMinutes(date.getMinutes()+30);
                            ScheduledFuture sh=taskScheduler.schedule(new GetResultdc(domain,auctionId),date);
                            updateTaskMap(domain,sh,"gr");

                        } else
                        {
                            String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                            Date date = null;
                            try {
                                date = parser.parse(endTime);
                            } catch (ParseException p) {
                                logger.info(p.getMessage());
                            }
                            date.setSeconds(date.getSeconds() - 15);
                           ScheduledFuture place= taskScheduler.schedule(new PlaceBiddc1(domain, auctionId, maxbid, endTime), date);
                            updateTaskMap(domain,place,"pb");

                            db.setResult("Bid Placed And Scheduled");
                            repo.save(db);
                            Date now= new Date();
                            String time= timeft.format(now);
                            String bidist= ft1.format(date);
                            telegram.sendAlert(-1001763199668l,1004l,"Dropcatch: Outbid, Bid SCHEDULED for " + domain + " at price " + minbid + " at time " + bidist);
                            notifRepo.save(new Notification("Dropcatch",time,"Outbid, Bid SCHEDULED for " + domain + " at price " + minbid + " at time " + bidist));
                            logger.info(time+": Outbid, Bid SCHEDULED for " + domain + " at price " + minbid + " time " + bidist+" i.e. "+bidist);

                        }
                        scheduledFuture.cancel(true);
                    }
                }
                else
                {
                    db.setResult("Won");
                    db.setScheduled(false);
                    repo.save(db);
                    deleteTaskMap(domain);
                    Date now= new Date();
                    String time= timeft.format(now);
                    telegram.sendAlert(-1001763199668l,842l,"Dropcatch: Yippee!! Won auction of "+domain+" at price: "+price);
                    notifRepo.save(new Notification("Dropcatch",time,"Yippee!! Won auction of "+domain+" at price: "+price));
                    logger.info(time+": Won auction of "+domain+" at price: "+price);
                    AuctionResultdc r = dropCatchFeign.getAuctionResult(bearer, domain, 10).getBody().getItems().get(0);
                    String domain1 = r.getDomain().toLowerCase();
                    if (domain.equalsIgnoreCase(domain1))
                    {
                        if (r.getResult().equals("AuctionWon"))
                            //db.setResult("Won");
                        telegram.sendAlert(-834797664L,r.getResult());
                        //else
                           // db.setResult("Loss");
                    }

                }
            }

        }

        @Autowired
        DropCatchFeignB dropCatchFeignB;
        public class PlaceBiddc1 implements Runnable
        {
            String domain;
            Long auctionId;
            Long maxbid;
            String timeId;

            public PlaceBiddc1(String domain, Long auctionId, Long maxbid, String timeId)
            {
                this.domain = domain;
                this.auctionId = auctionId;
                this.maxbid = maxbid;
                this.timeId = timeId;
            }

            @Override
            public void run()
            {
                /*Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
                String token = dropCatchFeign.authorise(auth).getBody().getToken();
                String bearer= "Bearer "+token;
*/
                DBdetails db= repo.findByPlatformAndAuctionId("Dropcatch",auctionId);
                AuctionDetailDC ad =null;
                try{
                    ad = dropCatchFeignB.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                }
                catch(Exception e)
                {
                    db.setResult("API Error Fetch pb");
                    repo.save(db);
                    return;
                }
                String endTime= ad.getEndTime().substring(0,ad.getEndTime().length()-1);
                Long minbid=ad.getMinimumNextBid();
                if(ad.getHighBid()>maxbid)
                {
                    //notify
                    Date date= null;
                    String endTimeist="";
                    String time_left="";
                    try
                    {
                        date = parser.parse(endTime);
                        time_left=relTime(date);
                        endTimeist=ft1.format(date);
                        // System.out.println(date);
                    }
                    catch(ParseException p)
                    {
                        logger.info(p.getMessage());}
                    db.setResult("Outbid");
                    db.setEndTimepst(endTime);
                    db.setTime_left(time_left);
                    db.setEndTimeist(endTimeist);
                    db.setCurrbid(String.valueOf(ad.getHighBid()));
                    repo.save(db);
                    sendOutbid("Outbid",time_left,domain,ad.getMinimumNextBid(), db.getBidAmount(), db.getEstibot(),auctionId);

                    Date now= new Date();
                    String time= timeft.format(now);
                    notifRepo.save(new Notification("Dropcatch",time,"Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + minbid ));
                    logger.info(time+" : Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + minbid );
                    date.setMinutes(date.getMinutes()+30);
                   ScheduledFuture res= taskScheduler.schedule(new GetResultdc(domain,auctionId),date);
                    updateTaskMap(domain,res,"gr");

                }
                else
                {
                    if (timeId.equals(endTime))
                    {


                            Biddc biddc = new Biddc(auctionId, minbid);
                            List<Biddc> bids= new ArrayList<>();
                            bids.add(biddc);
                        ResponseEntity<ResponsePlaceBiddc> re=null;
                            try {
                                re= dropCatchFeignB.placeBiddc(bearer,bids,domain);
                            }
                            catch(Exception e)
                            {
                                db.setResult("API Error Bid pb");
                                repo.save(db);
                                return;
                            }
                            ResponsePlaceBiddc rpb= re.getBody();
                            List<BidPlacedFailure> failures = rpb.getFailures();
                            List<BidPlacedSuccess> successes= rpb.getSuccesses();

                            if(successes.size()==1)
                            {
                                BidPlacedSuccess s= successes.get(0);
                                Date now = new Date();
                                String time= timeft.format(now);
                                telegram.sendAlert(-1001763199668l,1004l,"Dropcatch: Scheduled Bid PLACED for domain: "+domain+ " at price: "+minbid);
                                logger.info(time+": Scheduled Bid PLACED for domain: "+domain+ " at price: "+minbid);
                                notifRepo.save(new Notification("Dropcatch",time,"Scheduled Bid PLACED for domain: "+domain+ " at price: "+minbid));

                                now.setSeconds(now.getSeconds()+30);
                                CheckOutBid checkOutBid= new CheckOutBid(domain,auctionId,maxbid,minbid);
                                ScheduledFuture scheduledFuture=taskScheduler.scheduleAtFixedRate(checkOutBid,now,40000);
                                checkOutBid.setScheduledFuture(scheduledFuture);
                                updateTaskMap(domain,scheduledFuture,"co");

                                String endTime1 = s.getEndTime().substring(0,s.getEndTime().length()-1);
                                Date date= null;
                                String endTimeist="";
                                String time_left="";
                                try
                                {
                                    date = parser.parse(endTime1);
                                    time_left=relTime(date);
                                    endTimeist=ft1.format(date);
                                    // System.out.println(date);
                                }
                                catch(ParseException p)
                                {
                                    logger.info(p.getMessage());}
                                if(minbid>maxbid)
                                    db.setBidAmount(String.valueOf(minbid));
                                db.setResult("Bid Placed");
                                db.setIsBidPlaced(true);
                                db.setEndTimepst(endTime);
                                db.setTime_left(time_left);
                                db.setEndTimeist(endTimeist);
                                db.setCurrbid(String.valueOf(s.getHighBid()));
                                repo.save(db);
                            }
                            else
                            {
                                BidPlacedFailure f= failures.get(0);
                                Date now = new Date();
                                String time= timeft.format(now);
                                logger.info(time+": Scheduled Bid NOT PLACED for domain: "+domain+ " for price: "+minbid+" with error: "+f.getError().getDescription());
                                notifRepo.save(new Notification("Dropcatch",time,"Scheduled Bid NOT PLACED for domain: "+domain+ " for price: "+minbid+" with error: "+f.getError().getDescription()));
                                    db.setResult("Bid Not Placed");
                                    repo.save(db);

                                deleteTaskMap(domain);
                            }

                            //taskScheduler.schedule(new CheckOutbid(),)

                    }
                    else
                    {
                        Date date = new Date();
                        try {
                            date = parser.parse(endTime);
                        } catch (ParseException p)
                        {
                            logger.info(p.getMessage());
                        }
                        String endTimeist= ft1.format(date);
                        date.setSeconds(date.getSeconds() - 15);
                        Date now= new Date();
                        String time=timeft.format(now);
                       ScheduledFuture place= taskScheduler.schedule(new PlaceBiddc1(domain, auctionId, maxbid, endTime), date);
                        updateTaskMap(domain,place,"pb");
                        telegram.sendAlert(-1001763199668l,1004l,"Prechecking BID SCHEDULED for domain: "+domain+ " for max price: "+minbid+" at "+endTimeist);

                        notifRepo.save(new Notification("Dropcatch",time,"Prechecking BID SCHEDULED for domain: "+domain+ " for max price: "+minbid+" at "+endTimeist));
                        logger.info(time+": Prechecking BID SCHEDULED for domain: "+domain+ " for max price: "+minbid+" at "+endTimeist+" i.e. "+date);

                    }
                }

            }
        }

    public class PlaceBiddc implements Runnable
    {

        List<Biddc> bids;
        Map<Long,Long> rb;
        public PlaceBiddc(List<Biddc> bids)
        {
            this.bids = bids;
            this.rb= new HashMap<Long,Long>();
            //this.rm=rm;
            for(int i=0;i<bids.size();i++)
            {
                rb.put(bids.get(i).getAuctionId(),bids.get(i).getAmount());
            }
            //this.bearer = bearer;
        }




        @Override
        public void run() {
            List<String> succ= new ArrayList<>();
            MapWrap mw = mapwraprepo.findById(1).get();
            Map<Long,String> rm = mw.getRm();
           /* Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign.authorise(auth).getBody().getToken();
            String bearer= "Bearer "+token;
*/
           // Long id= bids.get(0).getAuctionId();
            //int num1 = Math.toIntExact(id);
           // AuctionDetailDC ad= dropCatchFeign.getAuctionDetail1(token, num1).getBody();
            //logger.info(ad.getName());

           ResponseEntity<ResponsePlaceBiddc> re= dropCatchFeign.placeBiddc(bearer,bids);
           ResponsePlaceBiddc rpb= re.getBody();
           List<BidPlacedFailure> failures = rpb.getFailures();
           List<BidPlacedSuccess> successes= rpb.getSuccesses();

           for(int i=0;i< failures.size();i++)
           {
               BidPlacedFailure f=failures.get(i);
               Long auctionId= f.getAuctionId();
               String domain = rm.get(f.getAuctionId());
               logger.info("Dropcatch: Scheduled Bid NOT PLACED for domain: "+domain+ " for price: "+rb.get(f.getAuctionId())+" with error: "+f.getError().getDescription());
               notifRepo.save(new Notification("Dropcatch: Scheduled Bid NOT PLACED for domain: "+domain+ " for price: "+rb.get(f.getAuctionId())+" with error: "+f.getError().getDescription()));
               Optional<DBdetails> op= Optional.ofNullable(repo.findByAuctionId(auctionId));
               if(op.isPresent())
               {
                   DBdetails db= op.get();
                   db.setResult("Bid Not Placed");
                   repo.save(db);
               }
           }

            for(int i=0;i<successes.size();i++)
            {
                BidPlacedSuccess s=successes.get(i);
                Long auctionId= s.getAuctionId();
                String domain = rm.get(s.getAuctionId());
                succ.add(domain);
                logger.info("Dropcatch: Scheduled Bid PLACED for domain: "+domain+ " at price: "+rb.get(s.getAuctionId()));
                notifRepo.save(new Notification("Dropcatch: Scheduled Bid PLACED for domain: "+domain+ " at price: "+rb.get(s.getAuctionId())));
                String endTime = s.getEndTime().substring(0,s.getEndTime().length()-1);
                Date date= new Date();
                String endTimeist="";
                String time_left="";
                try{
                    date = parser.parse(endTime);
                    time_left=relTime(date);
                    endTimeist=ft1.format(date);
                   // System.out.println(date);
                }
                catch(ParseException p)
                {logger.info(p.getMessage());}
                Optional<DBdetails> op= Optional.ofNullable(repo.findByAuctionId(auctionId));
                if(op.isPresent())
                {
                    DBdetails db= op.get();
                    db.setResult("Bid Placed");
                    db.setIsBidPlaced(true);
                    db.setEndTimepst(endTime);
                    db.setTime_left(time_left);
                    db.setEndTimeist(endTimeist);
                    db.setCurrbid(String.valueOf(s.getHighBid()));
                    repo.save(db);
                }
                else
                {
                    date = new Date();
                   String bidplacetime=ft1.format(date);
                    AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                    DBdetails db = new DBdetails(domain,auctionId,"Dropcatch",String.valueOf(s.getHighBid()), ad.getNumberOfBidders(), time_left,ad.getType(),"Bid Placed",endTime,endTimeist,bidplacetime,true);
                    db.setTime_left(time_left);
                    db.setAuctiontype(ad.getType());
                    repo.save(db);
                }
            }

          //notifRepo.save(new Notification("Scheduled Bid NOT PLACED for " + domain + " at price " + bid + " USD with Error Message: " + content + " at " + new Date()));
            //logger.info("bid not placed of domain: " + domain+" at price " + bid + " USD with Error Message: " + content + " at " + new Date());
        }
    }

}

