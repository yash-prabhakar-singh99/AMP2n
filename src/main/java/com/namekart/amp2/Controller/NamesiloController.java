package com.namekart.amp2.Controller;

import com.namekart.amp2.DotDBEntity.DotDbResponse;
import com.namekart.amp2.Entity.*;
import com.namekart.amp2.EstibotEntity.Estibot_Data;
import com.namekart.amp2.Feign.DotDBFeign;
import com.namekart.amp2.Feign.GoDaddyFeign;
import com.namekart.amp2.Feign.NamesiloFeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.NamesiloEntities.*;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class NamesiloController {
    String key="7fcf313ace746555cff70389";

    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

    SimpleDateFormat timeft = new SimpleDateFormat("dd/MM HH:mm");

    String filler="\n",text1,textl,textli,textob,summary="";
    StopWatch stopWatch;
    public NamesiloController(AllController controller)
    {
        TimeZone utc= TimeZone.getTimeZone("UTC");
        TimeZone ist= TimeZone.getTimeZone("IST");
        parser.setTimeZone(utc);
        ft1.setTimeZone(ist);
        timeft.setTimeZone(ist);
        this.controller=controller;
        this.taskmap=controller.getTaskmap();
        //taskmap= new ConcurrentHashMap<>();
        regmap=new HashSet<>();
        livens=new HashMap<>();
        rest = new RestTemplate();
        for(int i=0;i<66;i++)
            filler=filler+"_";
        text1="Namesilo"+filler+"\n";textob="Namesilo OUTBID!!"+filler+"\n";
        textl="Namesilo Live Detect"+filler+"\n";textli="Namesilo Initial List Detect"+filler+"\n";
        stopWatch=new StopWatch();
    }
    Logger logger = Logger.getLogger("Namesilo");
    @Autowired
    NamesiloFeign namesiloFeign;

    @Autowired
    @Qualifier(value = "workStealingPool")
    ForkJoinPool threadPoolExecutor;

    @Autowired
    NotifRepo notifRepo;
    @Autowired
    MyRepo repo;
    @Autowired
    Telegram telegram;

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    LiveMaprepo liveMaprepo;

    Set<String> regmap;
    Map<Long,String> livens;
    @Autowired
    Siloliverepo siloliverepo;
    RestTemplate rest;

    int t=60;
    String mute_unmute="\uD83D\uDD08/\uD83D\uDD07";

    String liveFormata(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, Integer EST)
    {
        if(ourMaxBid==null||ourMaxBid.isEmpty())
        {
            ourMaxBid="0";
        }
        String text="NS "+status+" - "+timeLeft+"\n"+domain+"\n"+"Price: "+minBid+" | Our Limit: "+ourMaxBid+"\n"+"EST: "+EST;
        return text;
    }
    String liveFormata(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, String EST)
    {
        if(ourMaxBid==null||ourMaxBid.isEmpty())
        {
            ourMaxBid="0";
        }
        String text="NS "+status+" - "+timeLeft+"\n"+domain+"\n"+"Price: "+minBid+" | Our Limit: "+ourMaxBid+"EST: "+EST;
        return text;
    }

    InlineKeyboardMarkup getKeyboardWatch(String domain,Long auctionId,Float currbid,String url)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " ns "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " ns "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton(mute_unmute, "m" + " ns "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " ns "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl(url);
        row1.add(link);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getKeyboardOb(String domain,Long auctionId, Float currbid,String url)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " ns "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " ns "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " ns "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl(url);
        row1.add(link);
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getKeyboardLive(String domain, Long auctionId,Float currbid, String url)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " ns "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " ns "+auctionId+" " + domain + " " + currbid));
        row.add(new InlineKeyboardButton("Watch", "w" + " ns " +auctionId+" "+ domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Track", "t" + " ns " +auctionId+" "+ domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " ns "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.dynadot.com/market/auction/" + domain);
        row1.add(link);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }

    void sendOutbid(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, Integer EST,Long auctionId, String url)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001866615838L,text,getKeyboardOb(domain,auctionId,minBid,url)));
    }

    void sendWatchlist(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, Integer EST,Long auctionId, String url)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001887754426L,text,getKeyboardWatch(domain,auctionId,minBid,url)));
    }
    void sendLive(String timeLeft, String domain, Float minBid,Integer EST,Long auctionId, String url)
    {
        String text=liveFormata("Live Detect",timeLeft,domain,minBid,"",EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1016l,text,getKeyboardLive(domain,auctionId,minBid,url)));
    }
    void sendLive(String timeLeft, String domain, Float minBid,Integer EST,Long auctionId, String url,String leads)
    {
        String text=liveFormata("Live Detect",timeLeft,domain,minBid,"",EST);
        if(leads!=null&&!leads.equals(""))
            text=text+"\n"+leads;
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1016l,text,getKeyboardLive(domain,auctionId,minBid,url)));
    }
    void sendLiveI(String timeLeft, String domain, Float minBid,Integer EST,Long auctionId, String url)
    {
        String text=liveFormata("Initial Detect",timeLeft,domain,minBid,"",EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,24112l,text,getKeyboardLive(domain,auctionId,minBid,url)));
    }

    void sendOutbid(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, String EST,Long auctionId, String url)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001866615838L,text,getKeyboardOb(domain,auctionId,minBid,url)));
    }

    void sendWatchlist(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, String EST,Long auctionId, String url)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001887754426L,text,getKeyboardWatch(domain,auctionId,minBid,url)));
    }
    void sendLive(String status,String timeLeft, String domain, Float minBid, String EST,Long auctionId, String url)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,"",EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1016l,text,getKeyboardLive(domain,auctionId,minBid,url)));
    }

    ConcurrentMap<String, Status> taskmap;
    @Scheduled(cron = "0 05 08 ? * *", zone = "IST")
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

    @GetMapping("/testrate")
    void testRate()
    {
        for(int i=0;i<100;i++)
        {

            String url = "https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389&statusId=2&typeId=3&pageSize=500&page=1";

            getResponseRest(url,3,"auclist");
            logger.info(""+i);
        }
    }
    ResponseEntity getResponseRest(String url, int n, String command) {
        try {
            if(n==-1)
                return null;
            if (command.equals("auclist"))
            {
            ResponseEntity<SiloRespAucList> res = rest.getForEntity(url, SiloRespAucList.class);
            //SiloRespAucList l = res.getBody();
            return res;
            }
            else if(command.equals("domlist"))
            {
                ResponseEntity<SiloRespDomList> res = rest.getForEntity(url, SiloRespDomList.class);
                return res;
            }
            else if(command.equals("auc"))
            {
                ResponseEntity<SiloRespAuc> res = rest.getForEntity(url, SiloRespAuc.class);
                return res;
            }
            else if(command.equals("bid"))
            {
                ResponseEntity<SiloRespPlaceBid> res = rest.getForEntity(url, SiloRespPlaceBid.class);
                return res;
            }
            else if(command.equals("renew"))
            {
                ResponseEntity<SiloRespRenew> res = rest.getForEntity(url, SiloRespRenew.class);
                return res;
            }
        }
        catch(HttpClientErrorException.TooManyRequests e)
        {
            Long retryAfter=Long.valueOf(e.getResponseHeaders().get("Retry-After").get(0));
            //RetryableException f;
            try {
                logger.info("Error 429 handled..Retrying after "+retryAfter);
                Thread.sleep((retryAfter+1) * 1000);
                return getResponseRest(url,n-1,command);
            }
            catch(InterruptedException i)
            {
                logger.info(e.getMessage());
            }
        }
        return null;
    }


    ResponseEntity getResponseRestB(String url, int n, String command, String domain, String method,DBdetails db) {
        try {
            if(n==-1)
            {
                String action=command.equals("auc")?"fetch":"bid";
                logger.info("API failure in while performing "+action+" in method "+method+" of domain "+domain);
                telegram.sendAlert(-930742733l,"Namecheap: API failure in while performing "+action+" in method "+method+" of domain "+domain);
                db.setResult("API Error "+action+" "+method);
                return null;
            }
            if (command.equals("auclist"))
            {
                ResponseEntity<SiloRespAucList> res = rest.getForEntity(url, SiloRespAucList.class);
                //SiloRespAucList l = res.getBody();
                return res;
            }
            else if(command.equals("domlist"))
            {
                ResponseEntity<SiloRespDomList> res = rest.getForEntity(url, SiloRespDomList.class);
                return res;
            }
            else if(command.equals("auc"))
            {
                ResponseEntity<SiloRespAuc> res = rest.getForEntity(url, SiloRespAuc.class);
                return res;
            }
            else if(command.equals("bid"))
            {
                ResponseEntity<SiloRespPlaceBid> res = rest.getForEntity(url, SiloRespPlaceBid.class);
                return res;
            }
            else if(command.equals("renew"))
            {
                ResponseEntity<SiloRespRenew> res = rest.getForEntity(url, SiloRespRenew.class);
                return res;
            }
        }
        catch(HttpClientErrorException.TooManyRequests e)
        {
            Long retryAfter=Long.valueOf(e.getResponseHeaders().get("Retry-After").get(0));
            //RetryableException f;
            try {
                logger.info("Error 429 handled..Retrying after "+retryAfter);
                Thread.sleep((retryAfter+1) * 1000);
                return getResponseRest(url,n-1,command);
            }
            catch(InterruptedException i)
            {
                logger.info(e.getMessage());
            }
        }
        catch(Exception e)
        {
            logger.info(e.getMessage()+", now retrying after 1 sec");
            Long retryAfter=0l;
            //RetryableException f;
            try {
                logger.info("Error 429 handled..Retrying after "+retryAfter);
                Thread.sleep((retryAfter+1) * 1000);
                return getResponseRest(url,n-1,command);
            }
            catch(InterruptedException i)
            {
                logger.info(e.getMessage());
            }
        }
        return null;
    }


    @GetMapping("/getlist")
SiloRespAucList getList()
{
    //SiloRespAucList l= namesiloFeign.getList(1,"xml",key);
    SiloRespAucList l=null;
    String url = "https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389&statusId=2&typeId=3&pageSize=500&page=15";
    ResponseEntity<SiloRespAucList> res= rest.getForEntity(url, SiloRespAucList.class);
    siloliverepo.save(res.getBody().getReply().getBody().get(0));
    l= res.getBody();
    logger.info(l.getReply().getBody().get(0).getDomain()+l.getReply().getBody().get(0).getNsid());
    Date d=null;
    try
    {
        d=parser.parse(l.getReply().getBody().get(0).getAuctionEndsOn());

        System.out.println(d);
    }
    catch(ParseException p)
    {logger.info(p.getMessage());}
    return l;
}

@Async
CompletableFuture<Boolean> refreshScheduled()
{
    List<DBdetails> list= repo.findByPlatformAndResultOrResultOrResultOrResult("Namesilo", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
    if(list==null||list.size()==0)
        return CompletableFuture.completedFuture(true);
    for (int i = 0; i < list.size(); i++)
    {
        DBdetails db = list.get(i);
        String domain= db.getDomain();
        Long id= db.getAuctionId();
        ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
        SiloAucReply reply= res.getBody().getReply();
        if(reply.getCode()==300) {
            SiloAuctionDetails details = reply.getBody();

            String endTime = details.getAuctionEndsOn();
            Float currbid = details.getCurrentBid();

            Date end = null;
            try {
                end = parser.parse(endTime);
                end.setHours(end.getHours() + 7);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            String endTimeist = ft1.format(end);
            String time_left = relTime(end);
           if(currbid>Float.valueOf(db.getBidAmount()))
               db.setResult("Outbid");

            db.setCurrbid(String.valueOf(currbid));
            db.setTime_left(time_left);
            db.setEndTimepst(endTime);
            db.setEndTimeist(endTimeist);

            if(details.getStatusId()!=2)
            {
                Date now= new Date();
                String time=timeft.format(now);
                notifRepo.save(new Notification("Namesilo",time,"Scheduled auction ended of domain: "+domain+" at price: "+currbid+" at time: "+endTimeist));
            }
            repo.save(db);
        }

    }
    return CompletableFuture.completedFuture(true);
}

Boolean b=true;
    @Scheduled(fixedDelay = 120000)
    void refreshnswatchlist() {
        List<DBdetails> list = repo.findByPlatformAndWatchlistIsTrue("Namesilo");
        if (list!=null&&!list.isEmpty())
        for (int i = 0; i < list.size(); i++)
        {
            if(i!=0)
            {
                try{
                    Thread.sleep(2000);
                }
                catch(InterruptedException e)
                {
                    logger.info(e.getMessage());
                }
            }
            DBdetails db = list.get(i);
            String domain= db.getDomain();
            Long id= db.getAuctionId();
            //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
            String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
            ResponseEntity<SiloRespAuc> res= getResponseRest(url,3,"auc");
            SiloAucReply reply= res.getBody().getReply();
            if(reply.getCode()==300) {
                SiloAuctionDetails details = reply.getBody();

                    String endTime = details.getAuctionEndsOn();
                    Float currbid = details.getCurrentBid();
                    Float prevBid= Float.valueOf(db.getCurrbid());
                    Date end = null;
                    try {
                        end = parser.parse(endTime);
                       // end.setHours(end.getHours() + 7);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                    String endTimeist = ft1.format(end);
                    String time_left = relTime(end);
                    Date now = new Date();
                float minNextBid = minNextBid(currbid);
                    if(db.getScheduled())
                    {
                        if(currbid>Float.valueOf(db.getBidAmount()))
                        {
                            if(!db.getResult().equals("Outbid"))
                            {
                                sendOutbid("Outbid",time_left,domain,minNextBid,db.getBidAmount(),db.getEstibot(),id,db.getUrl());
                                db.setResult("Outbid");
                            }

                        }
                        else if(b)
                        {

                            if (end.getTime() - now.getTime() > 300000) {
                                Date d = new Date(end.getTime() - 280000);
                                ScheduledFuture task = taskScheduler.schedule(new PreCheck(domain, id, Float.valueOf(db.getBidAmount())), d);
                                enterTaskMap(domain, task, "pc");

                            } else {
                                end.setSeconds(end.getSeconds() - 12);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBid(domain, id, Float.valueOf(db.getBidAmount()), endTime), end);
                                enterTaskMap(domain, task, "pb");

                            }
                            telegram.sendAlert(-1001763199668l,1004l, "Namesilo: Bid SCHEDULED for " + domain + " at price " + db.getBidAmount() + " time " + endTime);

                            String time= timeft.format(now);
                            logger.info(time + ": Bid SCHEDULED for " + domain + " at price " + db.getBidAmount() + " time " + endTime);
                            notifRepo.save(new Notification("Namesilo", time, "Bid SCHEDULED for " + domain + " at price " + db.getBidAmount() + " at time " + endTime));

                        }
                        else if(currbid>0.85*Float.valueOf(db.getBidAmount())&&db.isApproachWarn())
                        {
                            sendOutbid("Approaching Our Bid",time_left,domain,minNextBid,db.getBidAmount(),db.getEstibot(),id,db.getUrl());
                            db.setApproachWarn(false);
                        }
                    }
                    else if(!db.getMute()) {
                        if (prevBid < currbid) {
                            sendWatchlist("New Bid Placed",time_left,domain,minNextBid,db.getBidAmount(),db.getEstibot(),id,db.getUrl());

                        }
                        int nw = db.getNw();
                        if (nw == 0) {
                            if (end.getTime() - now.getTime() > 86400000)
                                nw = 4;
                            else if (end.getTime() - now.getTime() > 3600000)
                                nw = 3;
                            else if (end.getTime() - now.getTime() > 600000)
                                nw = 2;
                            else if (end.getTime() - now.getTime() > 240000)
                                nw = 1;
                            db.setNw(nw);
                        }
                        if (end.getTime() - now.getTime() < 86400002 && end.getTime() - now.getTime() > 86280000 && nw >= 4) {

                            sendWatchlist("<24 hrs LEFT",time_left,domain,minNextBid,db.getBidAmount(),db.getEstibot(),id,db.getUrl());

                            nw = 3;
                            db.setNw(nw);
                        } else if (end.getTime() - now.getTime() < 3600002 && end.getTime() - now.getTime() > 3480000 && nw >= 3) {

                            sendWatchlist("<1 hr LEFT",time_left,domain,minNextBid,db.getBidAmount(),db.getEstibot(),id,db.getUrl());

                            nw = 2;
                            db.setNw(nw);
                        } else if (end.getTime() - now.getTime() < 600002 && end.getTime() - now.getTime() > 480000 && nw >= 2) {

                            sendWatchlist("<10 mins LEFT",time_left,domain,minNextBid,db.getBidAmount(),db.getEstibot(),id,db.getUrl());

                            nw = 1;
                            db.setNw(nw);
                        } else if (end.getTime() - now.getTime() < 240002 && end.getTime() - now.getTime() > 120000 && nw >= 1) {

                            sendWatchlist("<4 mins LEFT",time_left,domain,minNextBid,db.getBidAmount(),db.getEstibot(),id,db.getUrl());

                            nw = -1;
                            db.setNw(nw);
                        }
                    }

                        db.setCurrbid(String.valueOf(currbid));
                        db.setTime_left(time_left);
                        db.setEndTimepst(endTime);
                        db.setEndTimeist(endTimeist);

                if(details.getStatusId()!=2||end.before(now))
                {
                    db.setWatchlist(false);
                    db.setWasWatchlisted(true);

                    String time=timeft.format(now);
                    if(db.getScheduled())
                    {
                        if(details.getLeaderUserId()==292467)
                        {
                            telegram.sendAlert(-1001763199668l,842l, "Namesilo: Yippee!! Won auction of " + domain + " at price: " + details.getCurrentBid());
                            notifRepo.save(new Notification("Namesilo",time,"Namesilo: Yippee!! Won auction of " + domain + " at price: " + details.getCurrentBid()));
                            logger.info(time+": Won auction of " + domain + " at price: " + details.getCurrentBid());
                            db.setScheduled(false);
                            db.setResult("Won");
                        }
                        else
                        {
                            if(details.getStatusId()==6&&details.getLeaderUserId()!=292467)
                            {
                                telegram.sendAlert(-1001763199668l, 841l, "Namesilo: Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid()+" because owner renewed the domain");
                                notifRepo.save(new Notification("Namesilo", time, "Namesilo: Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid()+" because owner renewed the domain"));
                                logger.info(time + ": Lost auction of " + domain + " at price: " + details.getCurrentBid()+" because owner renewed the domain");
                            }
                            else {
                                telegram.sendAlert(-1001763199668l, 841l, "Namesilo: Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid());
                                notifRepo.save(new Notification("Namesilo", time, "Namesilo: Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid()));
                                logger.info(time + ": Lost auction of " + domain + " at price: " + details.getCurrentBid());
                            }db.setScheduled(false);
                            db.setResult("Loss");
                        }
                        deleteTaskMap(domain);
                    }
                    if(details.getStatusId()==6&&details.getLeaderUserId()==292467)
                    {
                        telegram.sendAlert(-1001887754426l, "Namesilo: Renewed the domain " + domain + " at price: " + details.getCurrentBid());
                        notifRepo.save(new Notification("Namesilo", time, "Renewed the domain " + domain + " at price: " +details.getCurrentBid()));
                        logger.info(time + ": Renewed the domain " + domain + " at price: " + details.getCurrentBid());
                        db.setResult("Won"); db.setScheduled(false);
                        repo.save(db);
                        deleteTaskMap(domain);
                    }
                    notifRepo.save(new Notification("Namesilo",time,"Watchlisted auction ended of domain: "+domain+" at price: "+currbid+" at time: "+endTimeist));
                }
                repo.save(db);
            }

        }
        //scheduled

        //List<DBdetails> slist= repo.findByPlatformAndResultOrResultOrResultOrResult("Namesilo", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
        //List<DBdetails> slist= repo.findScheduledNS();

        b=false;
    }
    InlineKeyboardMarkup refreshMarkup(InlineKeyboardMarkup markup,float currbid)
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
        domain= domain.toLowerCase();
        Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Namesilo", id));
        DBdetails db=null;
        boolean b=op.isPresent();
        if(b)
            db=op.get();
        String text="Updated\uD83D\uDFE2\n\n";
        ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
        SiloAucReply reply= res.getBody().getReply();
        SiloAuctionDetails details = reply.getBody();
        String endTime = details.getAuctionEndsOn();
        Float currbid = details.getCurrentBid();
        EditMessage editMessage=null;
        Date end = null;
        try {
            end = parser.parse(endTime);
        } catch (ParseException p) {
            logger.info(p.getMessage());
        }
        /*markup.getInline_keyboard().get(0).get(0).setCallback_data("b ns "+id+" "+domain+" "+currbid+" 50");
        markup.getInline_keyboard().get(0).get(0).setCallback_data("b ns "+id+" "+domain+" "+currbid);*/
        Date now= new Date();
        if(reply.getCode()==300&&end.after(now)) {

            String endTimeist = ft1.format(end);
            String time_left = relTime(end);
            if(b&&db.getScheduled())
            {
                float minNextBid = minNextBid(currbid);
                if(currbid>Float.valueOf(db.getBidAmount()))
                {
                        text = "Namesilo AUCTION LOSING/OUTBID!!"+filler+ "\n" + domain + "\n \nTime Left: " + time_left + "\n\nCurrent Bid: " + currbid +"\nMin Next Bid: "+ minNextBid+"\nOur Max Bid: "+db.getBidAmount();

                }
                else
                {
                    text = "Namesilo AUCTION WINNING!!"+filler+ "\n" + domain + "\n \nTime Left: " + time_left + "\n\nCurrent Bid: " + currbid +"\nMin Next Bid: "+ minNextBid+"\nOur Max Bid: "+db.getBidAmount();
                }
            }
            else
            {
                text = "Namesilo AUCTION"+filler+"\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + currbid;
            }
            //editMessage=new EditMessage(text,chat_id,message_id,markup);
            SendMessage sendMessage= new SendMessage(chat_id,message_thread_id,text,refreshMarkup(markup,currbid));
            telegram.sendKeyboard(sendMessage);
        }
        else
        {
            if(b)
            {
                if(db.getScheduled())
                {
                    if(details.getLeaderUserId()==292467)
                    {
                        text = "Namesilo AUCTION WON!!"+filler+ "\n" + domain  + "\n\nLast Bid: " + currbid+"\nOur Max Bid: "+db.getBidAmount();

                    }
                    else
                    {
                        text = "Namesilo AUCTION WON!!"+filler+ "\n" + domain  + "\n\nLast Bid: " + currbid+"\nOur Max Bid: "+db.getBidAmount();

                    }
                }
                else if(db.getResult().equals("Won"))
                {
                    text = "Namesilo AUCTION WON!!"+filler+ "\n" + domain  + "\n\nLast Bid: " + currbid+"\nOur Max Bid: "+db.getBidAmount();

                } else if(db.getResult().equals("Loss"))
                {
                    text = "Namesilo AUCTION LOST!!"+filler+ "\n" + domain  + "\n\nLast Bid: " + currbid+"\nOur Max Bid: "+db.getBidAmount();

                }
            }
            else
            {
                text = "Namesilo AUCTION ENDED"+filler+ "\n" + domain  + "\n\nLast Bid: " + currbid;

            }
           /* editMessage=new EditMessage(text,chat_id,message_id);
            telegram.editMessageText(editMessage);*/
            telegram.sendAlert(chat_id,message_thread_id,text);
        }
    }

    void watchlistLive(long id, String domain, Boolean track)
    {
        CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
        domain= domain.toLowerCase();
        ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
        SiloAucReply reply= res.getBody().getReply();
        if(reply.getCode()==300)
        {
            SiloAuctionDetails details= reply.getBody();
            String endTime = details.getAuctionEndsOn();
            Float currbid = details.getCurrentBid();

            Date end = null;
            try {
                end = parser.parse(endTime);
                // if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                // end.setHours(end.getHours()+7);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            String endTimeist= ft1.format(end);
            Integer gdv=0;
            Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Namesilo",id));
            /*if(op.isPresent())
            {
                gdv= op.get().getGdv();
                if(gdv==null||gdv==0)
                {
                    Optional<SiloAuctionDetails> ad= Optional.ofNullable(siloliverepo.findByNsid(id));
                    if(ad.isPresent())
                    {
                        gdv=ad.get().getGdv();
                    }
                }
            }
            else
                gdv= siloliverepo.findByNsid(id).getGdv();
*/

            Optional<SiloAuctionDetails> op1=Optional.ofNullable(siloliverepo.findByNsid(id));
            String url=op1.isEmpty()?"https://namesilo.com/marketplace":op1.get().getUrl();
            String time_left= relTime(end);

            Float minNextBid=minNextBid(currbid);
            sendWatchlist("Watchlist",time_left,domain,minNextBid,"",details.getEST(),id,url);

            DBdetails dBdetails = null;

            if (!op.isPresent()) {
                dBdetails = new DBdetails(domain,gdv, id,"Namesilo", String.valueOf(currbid),time_left, "expired", "", "", endTime, endTimeist,false,url);
            } else {
                dBdetails = op.get();
                dBdetails.setCurrbid(String.valueOf(currbid));
                dBdetails.setTime_left(time_left);
                dBdetails.setEndTimepst(endTime);
                dBdetails.setEndTimeist(endTimeist);
                // dBdetails.setGdv(gdv);
            }
            dBdetails.setEstibot(details.getEST());
            dBdetails.setWatchlist(true);
            if(track)
                dBdetails.setTrack(true);
            repo.save(dBdetails);
            controller.putESTinDBSingle(cf);
        }
    }
    @GetMapping("/cancel/ns")
    void cancelBid(@RequestParam String domain, @RequestParam Long auctionId)
    {
        logger.info(domain);
        deleteTaskMap(domain);
        DBdetails db= repo.findByPlatformAndAuctionId("Namesilo",auctionId);
        db.setResult("Bid Cancelled");
        db.setScheduled(false);
        repo.save(db);
    }
   /* @Scheduled(fixedRate = 120000)
    void refreshnstrack() {
        List<DBdetails> list = repo.findByPlatformAndTrackIsTrue("Namesilo");
        if (list.isEmpty())
            return;
        for (int i = 0; i < list.size(); i++)
        {
            DBdetails db = list.get(i);
            String domain= db.getDomain();
            Long id= db.getAuctionId();
            if(i!=0)
            {
                try{
                    Thread.sleep(2000);
                }
                catch(InterruptedException e)
                {
                    logger.info(e.getMessage());
                }
            }
            //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
            String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
            ResponseEntity<SiloRespAuc> res= getResponseRest(url,3,"auc");
            SiloAucReply reply= res.getBody().getReply();
            if(reply.getCode()==300) {
                SiloAuctionDetails details = reply.getBody();

                String endTime = details.getAuctionEndsOn();
                Float currbid = details.getCurrentBid();
                Date end = null;
                try {
                    end = parser.parse(endTime);
                   // end.setHours(end.getHours() + 7);
                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                String endTimeist = ft1.format(end);
                String time_left = relTime(end);
                Date now = new Date();
                String text = textl + domain  + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + currbid;
                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton("Bid 50", "b" + " ns " + details.getNsid() + " " + domain + " " + currbid+" 50"));
                row.add(new InlineKeyboardButton("Bid", "b" + " ns " + details.getNsid() + " " + domain + " " + currbid));
                row1.add(new InlineKeyboardButton("Remove", "rw ns " + details.getNsid() + " " + domain));
                row1.add(new InlineKeyboardButton("Refresh", "r ns " + details.getNsid() + " " + domain));
                InlineKeyboardButton link= new InlineKeyboardButton("Link");
                link.setUrl(db.getUrl());
                row1.add(link);

                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                rows.add(row);rows.add(row1);
                InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                        ,text,inlineKeyboardMarkup));

                db.setCurrbid(String.valueOf(currbid));
                db.setTime_left(time_left);
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);

                if(details.getStatusId()!=2)
                {
                    db.setTrack(false);
                    db.setWatchlist(false);
                    db.setWasWatchlisted(true);

                    String time=timeft.format(now);
                    notifRepo.save(new Notification("Namesilo",time,"Watchlisted auction ended of domain: "+domain+" at price: "+currbid+" at time: "+endTimeist));
                }
                repo.save(db);
            }

        }
    }*/


    @GetMapping("/getregisteredlist")
    SiloRespDomList getRegList()
    {
        //SiloRespAucList l= namesiloFeign.getList(1,"xml",key);
        SiloRespDomList l=null;
        ResponseEntity<SiloRespDomList> res= rest.getForEntity("https://www.namesilo.com/api/listDomains?version=1&type=xml&key=7fcf313ace746555cff70389", SiloRespDomList.class);
        l= res.getBody();
        logger.info(l.getReply().getDetail());
        return l;
    }

    @GetMapping("/getauction")
    SiloRespAuc getAuction(@RequestParam Long id)
    {
        ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
        return res.getBody();
    }
    @GetMapping("/getauctionlist")
    SiloRespAucList getAuctionList()
    {
        ResponseEntity<SiloRespAucList> res= rest.getForEntity("https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389&statusId=2&typeId=3&pageSize=500&page="+1, SiloRespAucList.class);
        return res.getBody();
    }


    @PostMapping("/bulkfetchns")
    List<DBdetails> bulkfetchns(@RequestBody FetchReq fetchReq)
    {
        List<String> ddlist= fetchReq.getDomains();
        int a=0;
        List<DBdetails> list= new ArrayList<>();
        for(int i=0;i< ddlist.size();i++)
        {
            String domain= ddlist.get(i);
            Optional<SiloAuctionDetails> op1= Optional.ofNullable(siloliverepo.findByDomainIgnoreCase(domain));
            if(op1.isEmpty())
            {
                Date now=new Date();
                String time = timeft.format(now);
                telegram.sendAlert(-1001763199668l, 1005l, "Namesilo: "+ domain + " not fetched as no domain found in our database.");
                notifRepo.save(new Notification("Namesilo", time,  domain + " not fetched as no domain found in our database."));
                logger.info(time + domain + " not fetched as no domain found in our database.");

            }
            else
            {
                SiloAuctionDetails details1=op1.get();
                Long id= details1.getNsid();
                ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
                SiloAucReply reply= res.getBody().getReply();
                if(reply.getCode()==300)
                {
                    SiloAuctionDetails details= reply.getBody();
                    String endTime = details.getAuctionEndsOn();
                    Float currbid = details.getCurrentBid();

                    Date end = null;
                    try {
                        end = parser.parse(endTime);
                        // if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                        // end.setHours(end.getHours()+7);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                    String endTimeist= ft1.format(end);
                    Integer gdv=0;
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Namesilo",id));
            /*if(op.isPresent())
            {
                gdv= op.get().getGdv();
                if(gdv==null||gdv==0)
                {
                    Optional<SiloAuctionDetails> ad= Optional.ofNullable(siloliverepo.findByNsid(id));
                    if(ad.isPresent())
                    {
                        gdv=ad.get().getGdv();
                    }
                }
            }
            else
                gdv= siloliverepo.findByNsid(id).getGdv();
*/

                    String time_left= relTime(end);
                    if(fetchReq.getWatch())
                    {
                        String text = text1 + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + currbid;
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid 50", "b" + " ns " + details.getNsid() + " " + domain + " " + currbid+" 50"));
                        row.add(new InlineKeyboardButton("Bid", "b" + " ns " + details.getNsid() + " " + domain + " " + currbid));
                        row1.add(new InlineKeyboardButton("Track", "t ns " + details.getNsid() + " " + domain));
                        row1.add(new InlineKeyboardButton("Refresh", "r ns " + details.getNsid() + " " + domain));
                        InlineKeyboardButton link= new InlineKeyboardButton("Link");
                        link.setUrl(details1.getUrl());
                        row1.add(link);

                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                        rows.add(row);rows.add(row1);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                        Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                , text, inlineKeyboardMarkup));
                        Float minNextBid=minNextBid(currbid);
                        String url=op1.isEmpty()?"https://namesilo.com/marketplace":op1.get().getUrl();
                        sendWatchlist("Watchlist",time_left,domain,minNextBid,"",details.getEST(),id,url);

                    }
                    DBdetails dBdetails = null;
                    if (!op.isPresent()) {
                        dBdetails = new DBdetails(domain,gdv, id,"Namesilo", String.valueOf(currbid),time_left, "expired", "", "", endTime, endTimeist,false,details1.getUrl());
                    } else {
                        dBdetails = op.get();
                        dBdetails.setCurrbid(String.valueOf(currbid));
                        dBdetails.setTime_left(time_left);
                        dBdetails.setEndTimepst(endTime);
                        dBdetails.setEndTimeist(endTimeist);
                        // dBdetails.setGdv(gdv);
                    }
                    dBdetails.setEstibot(details.getEST());
                    if(fetchReq.getWatch())
                    dBdetails.setWatchlist(true);
                    list.add(dBdetails);
                    repo.save(dBdetails);
                }
                else
                {
                    String finalDomain2 = domain;
                    CompletableFuture.runAsync(()->{
                        Date now= new Date();
                        String time= timeft.format(now);
                        //telegram.sendAlert(-1001763199668l,1005l,"Namesilo: Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail());
                        logger.info(time+": Domain " + finalDomain2 + " not fetched with error: "+reply.getDetail());
                        if(!reply.getBody().getErrors().isEmpty())
                        {
                            logger.info(time+": "+reply.getBody().getErrors().get(0).getMessage());
                        }
                        notifRepo.save(new Notification("Namesilo",time,"Domain " + finalDomain2 + " not fetched with error: "+reply.getDetail()));
                    },threadPoolExecutor);
                }
            }
        }
        return list;

    }

    //@Autowired
    AllController controller;

    @PostMapping("/bulkschedulens")
    List<Integer> bulkschedulens(@RequestBody List<List<String>> ddlist)
    {
        List<Integer> arr= new ArrayList<>();
        int a=0;
        for(int i=0;i< ddlist.size();i++)
        {
            String domain= ddlist.get(i).get(0).toLowerCase();String price= ddlist.get(i).get(1);
            Optional<SiloAuctionDetails> op1= Optional.ofNullable(siloliverepo.findByDomainIgnoreCase(domain));
            if(op1.isEmpty())
            {
                Date now=new Date();
                String time = timeft.format(now);
                telegram.sendAlert(-930742733l, "Namesilo: Bid NOT SCHEDULED for " + domain + " as no domain found in our database.");
                notifRepo.save(new Notification("Namesilo", time, "Bid NOT SCHEDULED for "  + domain + " as no domain found in our database."));
                logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as no domain found in our database.");

            }
            else
            {
                SiloAuctionDetails details1=op1.get();
                Long id= details1.getNsid();
                Float bid= Float.valueOf(price);
                String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
                ResponseEntity<SiloRespAuc> res= getResponseRest(url,3,"auc");

                //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
                SiloAucReply reply= res.getBody().getReply();
                if(reply.getCode()==300) {
                    SiloAuctionDetails details = reply.getBody();
                    String endTime = details.getAuctionEndsOn();
                    Date end = null;
                    try {
                        end = parser.parse(endTime);
                        //if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                        //end.setHours(end.getHours() + 7);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                    Date now = new Date();
                    if (details.getStatusId() != 3 && end.after(now)) {
                        Float currbid = details.getCurrentBid();
                        float minNextBid = minNextBid(currbid);
                        if (minNextBid <= bid) {

                            if (end.getTime() - now.getTime() > 300000) {
                                Date d = new Date(end.getTime() - 280000);
                                ScheduledFuture task = taskScheduler.schedule(new PreCheck(domain, id, bid), d);
                                enterTaskMap(domain, task, "pc");

                            } else {
                                end.setSeconds(end.getSeconds() - t);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBid(domain, id, bid, endTime), end);
                                enterTaskMap(domain, task, "pb");

                            }
                            a++;

                            Date finalEnd = end;
                            String finalDomain = domain;
                            CompletableFuture.runAsync(() -> {
                                String endTimeist = ft1.format(finalEnd);
                                telegram.sendAlert(-1001763199668l, 1005l, "Namesilo: Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + endTime);
                                String time = timeft.format(now);
                                String time_left = relTime(finalEnd);
                                logger.info(time + ": Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + endTime);
                                notifRepo.save(new Notification("Namesilo", time, "Bid SCHEDULED for " + finalDomain + " at price " + bid + " at time " + endTime));
                                Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Namesilo", id));
                                DBdetails dBdetails = null;

                                if (!op.isPresent()) {
                                    dBdetails = new DBdetails(finalDomain, 0, id, "Namesilo", String.valueOf(currbid), time_left, "expired", String.valueOf(bid), "Bid Scheduled", endTime, endTimeist, false,details1.getUrl());
                                } else {
                                    dBdetails = op.get();
                                    dBdetails.setResult("Bid Scheduled");
                                    dBdetails.setBidAmount(String.valueOf(bid));
                                    dBdetails.setBidplacetime(endTimeist);
                                    dBdetails.setCurrbid(String.valueOf(currbid));
                                    dBdetails.setTime_left(time_left);
                                    dBdetails.setEndTimepst(endTime);
                                    dBdetails.setEndTimeist(endTimeist);
                                    //dBdetails.setGdv(gdv);
                                }
                                dBdetails.setEstibot(details.getEST());
                                dBdetails.setScheduled(true);

                                repo.save(dBdetails);


                            }, threadPoolExecutor);


                        } else {
                            String finalDomain1 = domain;
                            CompletableFuture.runAsync(() ->
                            {
                                //Date now = new Date();
                                String time = timeft.format(now);
                                telegram.sendAlert(-930742733l, "Namesilo: Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid);
                                notifRepo.save(new Notification("Namesilo", time, "Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid));
                                logger.info(time + ": Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid);
                            }, threadPoolExecutor);

                        }
                    }
                    else
                    {
                        String finalDomain3 = domain;
                        CompletableFuture.runAsync(() ->
                        {
                            //Date now = new Date();
                            String time = timeft.format(now);
                            telegram.sendAlert(-930742733l, "Namesilo: Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended.");
                            notifRepo.save(new Notification("Namesilo", time, "Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended."));
                            logger.info(time + ": Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended.");
                        }, threadPoolExecutor);

                    }
                }
                else
                {
                    String finalDomain2 = domain;
                    CompletableFuture.runAsync(()->{
                        Date now= new Date();
                        String time= timeft.format(now);
                        telegram.sendAlert(-930742733l,"Namesilo: Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail());
                        logger.info(time+": Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid + "with error: "+reply.getDetail());
                        if(!reply.getBody().getErrors().isEmpty())
                        {
                            logger.info(time+": "+reply.getBody().getErrors().get(0).getMessage());
                        }
                        notifRepo.save(new Notification("Namesilo",time,"Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail()));
                    },threadPoolExecutor);

                }
            }
        }
        arr.add(a);
        arr.add(ddlist.size());
        return arr;
    }

    BulkScheduleResponse bulkschedulensbot(@RequestBody List<List<String>> ddlist)
    {
        List<Integer> arr= new ArrayList<>();
        BulkScheduleResponse bs=null;
        String s="";
        int a=0;
        for(int i=0;i< ddlist.size();i++)
        {
            int l1=ddlist.get(i).size();
            String domain= ddlist.get(i).get(l1-2).toLowerCase();String price= ddlist.get(i).get(l1-1);
            Optional<SiloAuctionDetails> op1= Optional.ofNullable(siloliverepo.findByDomainIgnoreCase(domain));
            if(op1.isEmpty())
            {
                Date now=new Date();
                String time = timeft.format(now);
                telegram.sendAlert(-1001763199668l, 1005l, "Namesilo: Bid NOT SCHEDULED for " + domain + " as no domain found in our database.");
                notifRepo.save(new Notification("Namesilo", time, "Bid NOT SCHEDULED for "  + domain + " as no domain found in our database."));
                logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as no domain found in our database.");

            }
            else
            {
                SiloAuctionDetails details1=op1.get();
                Long id= details1.getNsid();
                Float bid= Float.valueOf(price);
                String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
                ResponseEntity<SiloRespAuc> res= getResponseRest(url,3,"auc");

                //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
                SiloAucReply reply= res.getBody().getReply();
                if(reply.getCode()==300) {
                    SiloAuctionDetails details = reply.getBody();
                    String endTime = details.getAuctionEndsOn();
                    Date end = null;
                    try {
                        end = parser.parse(endTime);
                        //if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                        //end.setHours(end.getHours() + 7);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                    Date now = new Date();
                    if (details.getStatusId() != 3 && end.after(now)) {
                        Float currbid = details.getCurrentBid();
                        float minNextBid = minNextBid(currbid);
                        if (minNextBid <= bid) {

                            if (end.getTime() - now.getTime() > 300000) {
                                Date d = new Date(end.getTime() - 280000);
                                ScheduledFuture task = taskScheduler.schedule(new PreCheck(domain, id, bid), d);
                                enterTaskMap(domain, task, "pc");

                            } else {
                                end.setSeconds(end.getSeconds() - t);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBid(domain, id, bid, endTime), end);
                                enterTaskMap(domain, task, "pb");

                            }
                            a++;

                            Date finalEnd = end;
                            String finalDomain = domain;
                            CompletableFuture.runAsync(() -> {
                                String endTimeist = ft1.format(finalEnd);
                                telegram.sendAlert(-1001763199668l, 1005l, "Namesilo: Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + endTime);
                                String time = timeft.format(now);
                                String time_left = relTime(finalEnd);
                                logger.info(time + ": Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + endTime);
                                notifRepo.save(new Notification("Namesilo", time, "Bid SCHEDULED for " + finalDomain + " at price " + bid + " at time " + endTime));
                                Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Namesilo", id));
                                DBdetails dBdetails = null;

                                if (!op.isPresent()) {
                                    dBdetails = new DBdetails(finalDomain, 0, id, "Namesilo", String.valueOf(currbid), time_left, "expired", String.valueOf(bid), "Bid Scheduled", endTime, endTimeist, false,details1.getUrl());
                                } else {
                                    dBdetails = op.get();
                                    dBdetails.setResult("Bid Scheduled");
                                    dBdetails.setBidAmount(String.valueOf(bid));
                                    dBdetails.setBidplacetime(endTimeist);
                                    dBdetails.setCurrbid(String.valueOf(currbid));
                                    dBdetails.setTime_left(time_left);
                                    dBdetails.setEndTimepst(endTime);
                                    dBdetails.setEndTimeist(endTimeist);
                                    //dBdetails.setGdv(gdv);
                                }
                                dBdetails.setEstibot(details.getEST());
                                repo.save(dBdetails);


                            }, threadPoolExecutor);


                        } else {
                            String text="Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid;
                            s=s+text+"\n";
                            String finalDomain1 = domain;
                            CompletableFuture.runAsync(() ->
                            {
                                //Date now = new Date();
                                String time = timeft.format(now);
                                telegram.sendAlert(-930742733l, "Namesilo: Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid);
                                notifRepo.save(new Notification("Namesilo", time, "Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid));
                                logger.info(time + ": Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid);
                            }, threadPoolExecutor);

                        }
                    }
                    else
                    {
                        String text="Bid NOT SCHEDULED for " + domain + " as auction has ended";
                        s=s+text+"\n";
                        String finalDomain3 = domain;
                        CompletableFuture.runAsync(() ->
                        {
                            //Date now = new Date();
                            String time = timeft.format(now);
                            telegram.sendAlert(-930742733l, "Namesilo: Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended.");
                            notifRepo.save(new Notification("Namesilo", time, "Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended."));
                            logger.info(time + ": Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended.");
                        }, threadPoolExecutor);

                    }
                }
                else
                {
                    String finalDomain2 = domain;
                    CompletableFuture.runAsync(()->{
                        Date now= new Date();
                        String time= timeft.format(now);
                        telegram.sendAlert(-930742733l,"Namesilo: Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail());
                        logger.info(time+": Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid + "with error: "+reply.getDetail());
                        if(!reply.getBody().getErrors().isEmpty())
                        {
                            logger.info(time+": "+reply.getBody().getErrors().get(0).getMessage());
                        }
                        notifRepo.save(new Notification("Namesilo",time,"Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail()));
                    },threadPoolExecutor);

                }
            }
        }
        arr.add(a);
        arr.add(ddlist.size());
        bs= new BulkScheduleResponse(arr,s);
        return bs;
    }


    float scheduleSingle(Long id, String domain, Float bid)
    {
        CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
        domain= domain.toLowerCase();
        String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
        ResponseEntity<SiloRespAuc> res= getResponseRest(url,3,"auc");

        //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
        SiloAucReply reply= res.getBody().getReply();
        if(reply.getCode()==300)
        {
          SiloAuctionDetails details= reply.getBody();

            Float currbid = details.getCurrentBid();
            Float minNextBid= minNextBid(currbid);
            if(minNextBid<=bid) {
                String endTime = details.getAuctionEndsOn();
                Date end = null;
                try {
                    end = parser.parse(endTime);
                    //end.setHours(end.getHours() + 7);
                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                Date now = new Date();
                if (end.getTime() - now.getTime() > 300000) {
                    Date d = new Date(end.getTime() - 280000);
                    ScheduledFuture task = taskScheduler.schedule(new PreCheck(domain, id, bid), d);
                    enterTaskMap(domain, task, "pc");
                } else {
                    end.setSeconds(end.getSeconds() - t);
                    ScheduledFuture task = taskScheduler.schedule(new PlaceBid(domain, id, bid, endTime), end);
                    enterTaskMap(domain, task, "pb");

                }

                Date finalEnd = end;
                String finalDomain = domain;
                CompletableFuture.runAsync(()->{

                String endTimeist = ft1.format(finalEnd);
                telegram.sendAlert(-1001763199668l,1005l, "Namesilo: Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + endTime);

                String time = timeft.format(now);
                String time_left = relTime(finalEnd);
                logger.info(time + ": Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + endTime);
                notifRepo.save(new Notification("Namesilo", time, "Bid SCHEDULED for " + finalDomain + " at price " + bid + " at time " + endTime));
                //Integer gdv = siloliverepo.findByNsid(id).getGdv();
                    Optional<SiloAuctionDetails> op1=Optional.ofNullable(siloliverepo.findByNsid(id));
                    String urld=op1.isEmpty()?"https://namesilo.com/marketplace":op1.get().getUrl();
                Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Namesilo", id));
                DBdetails dBdetails = null;
                if (!op.isPresent())
                {
                    dBdetails = new DBdetails(finalDomain,0, id, "Namesilo", String.valueOf(currbid), time_left, "expired", String.valueOf(bid), "Bid Scheduled", endTime, endTimeist, false,urld);
                } else {
                    dBdetails = op.get();
                    dBdetails.setResult("Bid Scheduled");
                    dBdetails.setBidAmount(String.valueOf(bid));
                    dBdetails.setBidplacetime(endTimeist);
                    dBdetails.setCurrbid(String.valueOf(currbid));
                    dBdetails.setTime_left(time_left);
                    dBdetails.setEndTimepst(endTime);
                    dBdetails.setEndTimeist(endTimeist);
                    //dBdetails.setGdv(gdv);
                }
                    dBdetails.setEstibot(details.getEST());
                    dBdetails.setScheduled(true);

                    repo.save(dBdetails);
                });
                controller.putESTinDBSingle(cf);
                return 0;
            }
            else
            {
                String finalDomain1 = domain;
                CompletableFuture.runAsync(()->
                {
                    Date now= new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-930742733l,"Namesilo: Bid NOT SCHEDULED for "+ finalDomain1 +" as bid value is lower than accepted bid of "+minNextBid);
                    notifRepo.save(new Notification("Namesilo",time,"Bid NOT SCHEDULED for "+ finalDomain1 +" as bid value is lower than accepted bid of "+minNextBid));
                    logger.info(time+": Bid NOT SCHEDULED for "+ finalDomain1 +" as bid value is lower than accepted bid of "+minNextBid);
                },threadPoolExecutor);
                return minNextBid;
            }
        }
        else
        {
            String finalDomain2 = domain;
            CompletableFuture.runAsync(()->{
                Date now= new Date();
                String time= timeft.format(now);
                telegram.sendAlert(-930742733l,"Namesilo: Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail());
                logger.info(time+": Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid + "with error: "+reply.getDetail());
                if(!reply.getBody().getErrors().isEmpty())
                {
                    logger.info(time+": "+reply.getBody().getErrors().get(0).getMessage());
                }
                notifRepo.save(new Notification("Namesilo",time,"Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail()));
            },threadPoolExecutor);
            return 1;
        }

    }

    @GetMapping("/datens")
    void datens1()
    {
        Date date= new Date();
        logger.info(""+date.getTimezoneOffset());
    }

    @GetMapping("/schedulesinglens")
    float scheduleSingleoutbid(@RequestParam Long id,@RequestParam String domain,@RequestParam Float bid)
    {

        CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);

        domain= domain.toLowerCase();
        String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
        ResponseEntity<SiloRespAuc> res= getResponseRest(url,3,"auc");

        //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
        SiloAucReply reply= res.getBody().getReply();
        if(reply.getCode()==300) {
            SiloAuctionDetails details = reply.getBody();
            String endTime = details.getAuctionEndsOn();
            Date end = null;
            try {
                end = parser.parse(endTime);
                //if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                //end.setHours(end.getHours() + 7);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            Date now = new Date();
            if (details.getStatusId() != 3 && end.after(now)) {
                Float currbid = details.getCurrentBid();
                float minNextBid = minNextBid(currbid);
                if (minNextBid <= bid) {

                    if (end.getTime() - now.getTime() > 300000)
                    {
                        Date d = new Date(end.getTime() - 280000);
                        ScheduledFuture task = taskScheduler.schedule(new PreCheck(domain, id, bid), d);
                        enterTaskMap(domain, task, "pc");
                    }
                    else
                    {
                        end.setSeconds(end.getSeconds() - t);
                        ScheduledFuture task = taskScheduler.schedule(new PlaceBid(domain, id, bid, endTime), end);
                        enterTaskMap(domain, task, "pb");
                    }
                    Date finalEnd = end;
                    String finalDomain = domain;
                    CompletableFuture.runAsync(() -> {
                        String endTimeist = ft1.format(finalEnd);
                        telegram.sendAlert(-1001763199668l, 1005l, "Namesilo: Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + endTime);
                        String time = timeft.format(now);
                        String time_left = relTime(finalEnd);
                        logger.info(time + ": Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + endTime);
                        notifRepo.save(new Notification("Namesilo", time, "Bid SCHEDULED for " + finalDomain + " at price " + bid + " at time " + endTime));
                        Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Namesilo", id));
                        DBdetails dBdetails = null;
                        Optional<SiloAuctionDetails> op1=Optional.ofNullable(siloliverepo.findByNsid(id));
                        String urld=op1.isEmpty()?"https://namesilo.com/marketplace":op1.get().getUrl();
                        if (!op.isPresent()) {
                            dBdetails = new DBdetails(finalDomain, 0, id, "Namesilo", String.valueOf(currbid), time_left, "expired", String.valueOf(bid), "Bid Scheduled", endTime, endTimeist, false,urld);
                        } else {
                            dBdetails = op.get();
                            dBdetails.setResult("Bid Scheduled");
                            dBdetails.setBidAmount(String.valueOf(bid));
                            dBdetails.setBidplacetime(endTimeist);
                            dBdetails.setCurrbid(String.valueOf(currbid));
                            dBdetails.setTime_left(time_left);
                            dBdetails.setEndTimepst(endTime);
                            dBdetails.setEndTimeist(endTimeist);
                            //dBdetails.setGdv(gdv);
                        }
                        dBdetails.setEstibot(details.getEST());
                        dBdetails.setScheduled(true);

                        repo.save(dBdetails);


                    }, threadPoolExecutor);
                    controller.putESTinDBSingle(cf);
                    return 0;

                } else {
                    String finalDomain1 = domain;
                    CompletableFuture.runAsync(() ->
                    {
                        //Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Namesilo: Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid);
                        notifRepo.save(new Notification("Namesilo", time, "Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid));
                        logger.info(time + ": Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid);
                    }, threadPoolExecutor);
                    return minNextBid;
                }
            }
            else
            {
                String finalDomain3 = domain;
                CompletableFuture.runAsync(() ->
                {
                    //Date now = new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-930742733l, "Namesilo: Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended.");
                    notifRepo.save(new Notification("Namesilo", time, "Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended."));
                    logger.info(time + ": Bid NOT SCHEDULED for " + finalDomain3 + " as auction has ended.");
                }, threadPoolExecutor);
                return 2;
            }
        }
        else
        {
            String finalDomain2 = domain;
            CompletableFuture.runAsync(()->{
            Date now= new Date();
            String time= timeft.format(now);
            telegram.sendAlert(-930742733l,"Namesilo: Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail());
            logger.info(time+": Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid + "with error: "+reply.getDetail());
            if(!reply.getBody().getErrors().isEmpty())
            {
                logger.info(time+": "+reply.getBody().getErrors().get(0).getMessage());
            }
            notifRepo.save(new Notification("Namesilo",time,"Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid+ "with error: "+reply.getDetail()));
            },threadPoolExecutor);

            return 1;
        }
    }

    public class PreCheck implements Runnable
    {
        String domain;
        Long id;
        Float maxbid;

        public PreCheck(String domain, Long id, Float maxbid) {
            this.domain = domain;
            this.id = id;
            this.maxbid = maxbid;
        }

        @Override
        public void run()
        {
            //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
            String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
            DBdetails db = repo.findByPlatformAndAuctionId("Namesilo", id);

            ResponseEntity<SiloRespAuc> res= getResponseRestB(url,3,"auc",domain,"pc",db);
            if(res==null)
                return;
           // SiloAucReply reply= res.getBody().getReply();
            Float currbid= res.getBody().getReply().getBody().getCurrentBid();
            Float minbid= minNextBid(currbid);
            if(currbid>maxbid)
            {
                //notify
                SiloAuctionDetails details= res.getBody().getReply().getBody();
                String endTime= details.getAuctionEndsOn();
                String time_left="";
                String endTimeist="";
                Date end=null;
                try
                {
                    end=parser.parse(endTime);
                    //if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                      //  end.setHours(end.getHours()+7);
                    end.setSeconds(end.getSeconds()-t);
                }
                catch(ParseException p)
                {
                    logger.info(p.getMessage());
                }
                time_left=relTime(end);

                sendOutbid("Outbid",time_left,domain,minbid,db.getBidAmount(),details.getEST(),id,db.getUrl());

                db.setCurrbid(String.valueOf(currbid));
                db.setTime_left(time_left);
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setResult("Outbid");
                repo.save(db);
                Date now= new Date();
                now.setMinutes(now.getMinutes()+30);
               ScheduledFuture task= taskScheduler.schedule(new GetResultNs(domain,id), now);
                updateTaskMap(domain,task,"gr");

            }
            else
            {
                String endTimeist="";
                String endTime= res.getBody().getReply().getBody().getAuctionEndsOn();
                Date end=null;
                try
                {
                    end=parser.parse(endTime);
                   // if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                    //   end.setHours(end.getHours()+7);
                    end.setSeconds(end.getSeconds()-12);
                }
                catch(ParseException p)
                {
                    logger.info(p.getMessage());
                }
                endTimeist=ft1.format(end);
               ScheduledFuture task= taskScheduler.schedule(new PlaceBid(domain,id,maxbid,endTime),end);
                enterTaskMap(domain,task,"pb");

                Date now= new Date();
                String time= timeft.format(now);
                telegram.sendAlert(-1001763199668l,1004l,"Namesilo: Prechecking, Bid SCHEDULED for " + domain + " at price " + minbid+ " time " + endTimeist);
                notifRepo.save(new Notification("Namesilo",time,"Prechecking, Bid SCHEDULED for " + domain + " at price " + minbid+ " time " + endTimeist));
                logger.info(time+": Prechecking, Bid SCHEDULED for " + domain + " at price " + minbid + " time " + endTimeist);
            }
        }
    }
    public class PlaceBid implements Runnable
    {
        String domain;
        Long id;
        Float maxbid;

        String timeid;

        public PlaceBid(String domain, Long id, Float maxbid, String timeid)
        {
            this.domain = domain;
            this.id = id;
            this.maxbid = maxbid;
            this.timeid=timeid;
        }

        @Override
        public void run() {
            DBdetails db = repo.findByPlatformAndAuctionId("Namesilo", id);
            String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
            ResponseEntity<SiloRespAuc> res= getResponseRestB(url,4,"auc",domain,"pb",db);
            if(res==null)
                return;
            //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
            SiloAucReply reply= res.getBody().getReply();

            if(reply.getCode()==300)
            {
                SiloAuctionDetails details = reply.getBody();
                String endTime = details.getAuctionEndsOn();
                Float currbid = details.getCurrentBid();
                Float minNextBid = minNextBid(currbid);

                if(currbid>maxbid)
                {
                    //notify
                    String time_left="";
                    String endTimeist="";
                    Date end=null;
                    try
                    {
                        end=parser.parse(endTime);
                       // if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                          //  end.setHours(end.getHours()+7);
                        end.setSeconds(end.getSeconds()-12);
                    }
                    catch(ParseException p)
                    {
                        logger.info(p.getMessage());
                    }
                    time_left=relTime(end);
                    sendOutbid("Outbid",time_left,domain,minNextBid,db.getBidAmount(),details.getEST(),id,db.getUrl());

                    db.setCurrbid(String.valueOf(currbid));
                    db.setTime_left(time_left);
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setResult("Outbid");
                    repo.save(db);
                    Date now= new Date();
                    now.setMinutes(now.getMinutes());
                    ScheduledFuture task= taskScheduler.schedule(new GetResultNs(domain,id),now);
                    updateTaskMap(domain,task,"gr");

                }
                else
                {
                    if(timeid.equals(endTime))
                    {
                        String urlp="https://www.namesilo.com/public/api/bidAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id+"&bid="+minNextBid;
                        ResponseEntity<SiloRespPlaceBid> resp= getResponseRestB(urlp,3,"bid",domain,"pb",db);
                        //SiloAucReply reply= res.getBody().getReply();
                        //ResponseEntity<SiloRespPlaceBid> resp= rest.getForEntity("https://www.namesilo.com/public/api/bidAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id+"&bid="+minNextBid,SiloRespPlaceBid.class);
                        if(resp.getBody().getReply().getCode()==300)
                        {

                            Date d=new Date();
                            String time= timeft.format(d);
                            telegram.sendAlert(-1001763199668l,1004l, "Namesilo: Scheduled Bid PLACED for " + domain + " at price " + minNextBid + " USD");
                            notifRepo.save(new Notification("Namesilo",time,"Scheduled Bid PLACED for " + domain + " at price " + minNextBid + " USD"));
                            logger.info(time+": Scheduled Bid Placed of domain: " + domain+ " at price " + minNextBid + " USD");
                            d.setSeconds(d.getSeconds()+30);
                            CheckOutbid checkOutbid= new CheckOutbid(domain,id,maxbid,minNextBid);

                            ScheduledFuture scheduledFuture= taskScheduler.scheduleAtFixedRate(checkOutbid,d,30000);
                            checkOutbid.setScheduledFuture(scheduledFuture);
                            if(resp.getBody().getReply().getBody().getBid()>maxbid)
                                db.setBidAmount(String.valueOf(resp.getBody().getReply().getBody().getBid()));
                            db.setIsBidPlaced(true);
                            db.setResult("Bid Placed");
                            db.setCurrbid(String.valueOf(minNextBid));
                            repo.save(db);
                            updateTaskMap(domain,scheduledFuture,"co");

                        }
                        else
                        {
                            Date d=new Date();
                            String time= timeft.format(d);
                            String content = resp.getBody().getReply().getDetail()+" "+resp.getBody().getReply().getBody().getMessage();
                            logger.info(time+": Bid not placed of domain: " + domain + " at price " + minNextBid + " USD with Error Message: " + content);
                            try {
                                notifRepo.save(new Notification("Namesilo", time, "Scheduled Bid NOT PLACED for " + domain + " at price " + minNextBid + " USD with Error Message: " + content));
                            }
                            catch(Exception e)
                            {
                                logger.info(e.getMessage());
                            }
                            db.setIsBidPlaced(true);
                            db.setScheduled(false);
                            db.setResult("Bid Not Placed");
                            db.setCurrbid(String.valueOf(minNextBid));
                            repo.save(db);
                            deleteTaskMap(domain);
                        }
                    }
                    else
                    {
                        String endTimeist="";
                        Date end=null;
                        try
                        {
                            end=parser.parse(endTime);
                          //  if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                             //   end.setHours(end.getHours()+7);
                            end.setSeconds(end.getSeconds()-t);
                        }
                        catch(ParseException p)
                        {
                            logger.info(p.getMessage());
                        }
                        endTimeist=ft1.format(end);
                       ScheduledFuture task= taskScheduler.schedule(new PlaceBid(domain,id,maxbid,endTime),end);
                        updateTaskMap(domain,task,"pb");
                        Date now= new Date();
                        String time= timeft.format(now);
                        telegram.sendAlert(-1001763199668l,1004l,"Namesilo: Prechecking, Bid SCHEDULED for " + domain + " at price " + minNextBid+ " time " + endTimeist);
                        notifRepo.save(new Notification("Namesilo",time,"Prechecking, Bid SCHEDULED for " + domain + " at price " + minNextBid+ " time " + endTimeist));
                        logger.info(time+": Prechecking, Bid SCHEDULED for " + domain + " at price " + minNextBid + " time " + endTimeist);
                    }
                }
            }
        }
    }
    public class CheckOutbid implements Runnable
    {
        String domain;
        Long id;
        Float maxbid;
        Float prevbid;

        ScheduledFuture scheduledFuture;

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public CheckOutbid(String domain, Long id, Float maxbid, Float prevbid) {
            this.domain = domain;
            this.id = id;
            this.maxbid = maxbid;
            this.prevbid=prevbid;
        }

        @Override
        public void run() {
            DBdetails db = repo.findByPlatformAndAuctionId("Namesilo", id);
            String url="https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id;
            ResponseEntity<SiloRespAuc> res= getResponseRestB(url,3,"auc",domain,"co",db);
            if(res==null)
                return;
            //ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
            SiloAucReply reply= res.getBody().getReply();
            if(reply.getCode()==300)
            {
                SiloAuctionDetails details= reply.getBody();
                String endTime= details.getAuctionEndsOn();
                String time_left="";
                String endTimeist="";
                Date end=null;
                try
                {
                    end=parser.parse(endTime);
                    //if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                    //  end.setHours(end.getHours()+7);
                    end.setSeconds(end.getSeconds()-12);
                }
                catch(ParseException p)
                {
                    logger.info(p.getMessage());
                }

                logger.info("NS User Id: "+details.getLeaderUserId());
                Date now = new Date();
                if(details.getStatusId()==2&&end.after(now))
                {
                    float currbid = details.getCurrentBid();
                    if (currbid > prevbid)
                    {
                        float minNextBid = minNextBid(currbid);
                        if (currbid > maxbid)
                        {
                            //notify
                            time_left=relTime(end);
                            sendOutbid("Outbid",time_left,domain,minNextBid,db.getBidAmount(),details.getEST(),id,db.getUrl());

                            db.setCurrbid(String.valueOf(currbid));
                            db.setTime_left(time_left);
                            db.setEndTimepst(endTime);
                            db.setEndTimeist(endTimeist);
                            db.setResult("Outbid");
                            repo.save(db);
                            now.setMinutes(now.getMinutes()+30);
                            ScheduledFuture task= taskScheduler.schedule(new GetResultNs(domain,id),now);
                            updateTaskMap(domain,task,"gr");

                        }
                        else
                        {
                            try
                            {
                                end=parser.parse(endTime);
                                end.setSeconds(end.getSeconds()-t);
                            }
                            catch(ParseException p)
                            {
                                logger.info(p.getMessage());
                            }
                            endTimeist=ft1.format(end);
                          ScheduledFuture task=  taskScheduler.schedule(new PlaceBid(domain,id,maxbid,endTime),end);
                            updateTaskMap(domain,task,"pb");

                            logger.info("Rescheduled");
                            db.setResult("Bid Placed And Scheduled");
                            repo.save(db);
                            String time= timeft.format(now);
                            telegram.sendAlert(-1001763199668l,1004L, "Namesilo: Outbid, Bid SCHEDULED for " + domain + " at price " + minNextBid + " at time: " + endTimeist);
                            notifRepo.save(new Notification("Namesilo",time,"Outbid, Bid SCHEDULED for " + domain + " at price " + minNextBid + " at time: " + endTimeist));
                            logger.info(time+": Outbid, Bid SCHEDULED for " + domain + " at price " + minNextBid + " time " + endTimeist);

                        }
                        scheduledFuture.cancel(false);
                    }
                }
                else if(details.getStatusId()==6)
                {   String time = timeft.format(now);
                    if(details.getLeaderUserId()==292467)
                    {
                        telegram.sendAlert(-1001763199668l,842L, "Namesilo: Yippee!! Won auction of " + domain + " at price: " + prevbid+ " because we renewed the domain");
                        notifRepo.save(new Notification("Namesilo", time, "Yippee!! Won auction of " + domain + " at price: " + prevbid+ " because we renewed the domain"));
                        logger.info(time + ": Won auction of " + domain + " at price: " + prevbid+ " because we renewed the domain");
                        db.setResult("Won"); db.setScheduled(false);

                        deleteTaskMap(domain);
                        repo.save(db);
                    }
                    else {
                        telegram.sendAlert(-1001763199668l, 841l, "Namesilo: Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid() + " because owner renewed the domain");
                        notifRepo.save(new Notification("Namesilo", time, "Namesilo: Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid() + " because owner renewed the domain"));
                        logger.info(time + ": Lost auction of " + domain + " at price: " + details.getCurrentBid() + " because owner renewed the domain");
                        db.setResult("Loss");
                        db.setScheduled(false);

                        repo.save(db);
                        deleteTaskMap(domain);}
                }
                else if(details.getStatusId()==3||end.before(now))
                {
                    logger.info(""+details.getLeaderUserId());
                    if(details.getCurrentBid().equals(prevbid))
                    {
                        String time = timeft.format(now);
                        telegram.sendAlert(-1001763199668l,842L, "Namesilo: Yippee!! Won auction of " + domain + " at price: " + prevbid);
                        notifRepo.save(new Notification("Namesilo", time, "Yippee!! Won auction of " + domain + " at price: " + prevbid));
                        logger.info(time + ": Won auction of " + domain + " at price: " + prevbid);
                        db.setResult("Won"); db.setScheduled(false);

                        deleteTaskMap(domain);
                        repo.save(db);
                    }
                    else
                    {
                        String time = timeft.format(now);
                        telegram.sendAlert(-1001763199668l,841L, "Namesilo: Hush!! Lost auction of " + domain + " at price: " + prevbid);
                        notifRepo.save(new Notification("Namesilo", time, "Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid()));
                        logger.info(time + ": Lost auction of " + domain + " at price: " + details.getCurrentBid());
                        db.setResult("Loss");
                        db.setScheduled(false);

                        repo.save(db);
                        deleteTaskMap(domain);
                    }
                    scheduledFuture.cancel(false);
                }
            }
            else
            {
                logger.info(domain+" "+reply.getDetail()+" "+reply.getCode());
            }
        }
    }

    float minNextBid(float currbid)
    {
        float minNextBid=0f;
        if(currbid<50)
        {
            minNextBid=currbid+1;
        }
        else if(currbid<250)
        {
            minNextBid=currbid+5;
        }
        else
        {
            minNextBid=currbid+10;
        }
        return minNextBid;
    }
int i=0;
    //@Scheduled(fixedDelay = 2000)




   /* String relTimelive(Date d)
    {
        Date date= new Date();
        String s="";
        int h=d.getHours()-date.getHours();
        int m= d.getMinutes()-date.getMinutes();
        s= h+"h, "+m+"m";
        return s;
    }*/
   public class GetResultNs implements Runnable
   {
       String domain;
       Long id;

       public GetResultNs(String domain, Long id)
       {
           this.domain = domain;
           this.id = id;
       }

       @Override
       public void run()
       {
           ResponseEntity<SiloRespAuc> res= rest.getForEntity("https://www.namesilo.com/public/api/viewAuction?version=1&type=xml&key=7fcf313ace746555cff70389&auctionId="+id, SiloRespAuc.class);
           SiloAucReply reply= res.getBody().getReply();
           if(reply.getCode()==300)
           {
               SiloAuctionDetails details = reply.getBody();
               if (details.getStatusId()==3)
               {
                   DBdetails db=repo.findByPlatformAndAuctionId("Namesilo",id);
                   String endTime= details.getAuctionEndsOn();
                   String endTimeist="";
                   Date end=null;
                   try
                   {
                       end=parser.parse(endTime);
                       //if((end.getTimezoneOffset()==0&&Math.abs(end.getHours()-7)>2)||(end.getTimezoneOffset()==330&&Math.abs(end.getHours()-12)>2))
                       //    end.setHours(end.getHours()+7);
                       end.setSeconds(end.getSeconds()-12);
                   }
                   catch(ParseException p)
                   {
                       logger.info(p.getMessage());
                   }
                   endTimeist=ft1.format(end);
                   db.setEndTimepst(endTime);
                   db.setEndTimeist(endTimeist);
                   db.setCurrbid(String.valueOf(details.getCurrentBid()));
                   //need user id;
                   Date now= new Date();
                   String time= timeft.format(now);
                   if(details.getLeaderUserId()==0)
                   {
                       telegram.sendAlert(-1001763199668l,842l, "Namesilo: Yippee!! Won auction of " + domain + " at price: " + details.getCurrentBid());
                       notifRepo.save(new Notification("Namesilo",time,"Namesilo: Yippee!! Won auction of " + domain + " at price: " + details.getCurrentBid()));
                       logger.info(time+": Won auction of " + domain + " at price: " + details.getCurrentBid());

                       db.setResult("Won");
                   }
                   else
                   {
                       telegram.sendAlert(-1001763199668l, 841l,"Namesilo: Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid());
                       notifRepo.save(new Notification("Namesilo",time,"Namesilo: Hush!! Lost auction of " + domain + " at price: " + details.getCurrentBid()));
                       logger.info(time+": Lost auction of " + domain + " at price: " + details.getCurrentBid());
                       db.setResult("Loss");
                   }
                   deleteTaskMap(domain);
                   db.setScheduled(false);

                   repo.save(db);
               }
               else if(details.getStatusId()==2)
               {
                   Date now= new Date();
                   now.setMinutes(now.getMinutes()+30);
                  ScheduledFuture task= taskScheduler.schedule(new GetResultNs(domain,id),now);
                   updateTaskMap(domain,task,"gr");

               }
           }
           else
           {

           }
       }
   }

    String formdigit(long a)
    {
        if(a<10)
            return "0"+a;
        else return ""+a;
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


        logger.info(s);
        return s;
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
        logger.info(s);
        return s;
    }


    boolean isHighlight(String domain, Estibot_Data data, LiveFilterSettings settings)
    {
        Integer EST= data.getAppraised_value();
        if(EST==null)
            EST=0;
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
        if(EST==null)
            EST=0;
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

    @Autowired
    LiveFilterSettingsRepo settingsRepo;
    public class StopLiveNs implements Runnable
    {
        ScheduledFuture scheduledFuture;

        public StopLiveNs(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        @Override
        public void run() {
            scheduledFuture.cancel(false);
            livens.clear();
            regmap.clear();
            Optional<LiveMap> o= liveMaprepo.findById(4);
            LiveMap lm;
            if(o.isEmpty())
            {
                lm= new LiveMap(4);
            }
            else
                lm=o.get();
            LiveFilterSettings settings=settingsRepo.findById(1).get();
            Map<Long,String> map1= lm.getMapns();

            ResponseEntity<SiloRespDomList> res= getResponseRest("https://www.namesilo.com/api/listDomains?version=1&type=xml&key=7fcf313ace746555cff70389",3,"domlist");
            //ResponseEntity<SiloRespDomList> res= rest.getForEntity("https://www.namesilo.com/api/listDomains?version=1&type=xml&key=7fcf313ace746555cff70389", SiloRespDomList.class);
            SiloRespDomList l= res.getBody();
            List<String> listreg= l.getReply().getDomains();
            for(int i=0;i<listreg.size();i++)
            {
                regmap.add(listreg.get(i).toLowerCase());
            }
            Long lnsid=0l;
            siloliverepo.deleteAll();
            int page=0;
            int n=0;
            boolean b=true;
            Date date = new Date();
            date.setHours(date.getHours() + 24);
            String domain1="";
            int n1=0;
            List<String> ests= new ArrayList<>();
            String est= new String("");
            int estm=0;
            while(b)
            {
                page++;
                String url = "https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389&statusId=2&typeId=3&pageSize=500&page=";
                //ResponseEntity<SiloRespAucList> ress = rest.getForEntity(url+page, SiloRespAucList.class);
                ResponseEntity<SiloRespAucList> ress= getResponseRest(url+page,3,"auclist");
                List<SiloAuctionDetails> al = ress.getBody().getReply().getBody();
                for (int i = 0; i < al.size(); i++) {
                    SiloAuctionDetails details = al.get(i);
                    String domain = details.getDomain().toLowerCase();
                    String endTime = details.getAuctionEndsOn();
                    Float bid = details.getCurrentBid();
                    Float openingbid= details.getOpeningBid();


                    Date end = null;
                    try {
                        end = parser.parse(endTime);
                        end.setHours(end.getHours()+7);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    if (end.before(date))
                    {
                        lnsid=details.getNsid();
                        if (bid > openingbid) {
                            map1.put(details.getNsid(), domain);
                            n1=Math.max(n1,domain.length());
                            details.setEndList(true);
                            if(estm>=200)
                            {
                                ests.add(est);
                                est= new String("");
                                estm=0;
                            }
                            est=est+domain+">>";
                            estm++;
                            //logger.info(details.getNsid()+" "+ domain);
                            try {
                                siloliverepo.save(details);
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                            if (regmap.contains(domain))
                            {
                                logger.info("Found domain for renewal: " + domain);
                                String url1 = "https://www.namesilo.com/api/renewDomain?version=1&type=xml&key=7fcf313ace746555cff70389&domain=" + domain + "&years=1";
                                SiloRespRenew respRenew = rest.getForEntity(url1, SiloRespRenew.class).getBody();
                                if (respRenew.getReply().getCode() == 300)
                                    logger.info("Renewed domain: " + domain);
                                String text = "Namesilo Own-Check\nRenewed \n\n" + domain;
                                try
                                {
                                    Object obj = telegram.sendAlert(//-856441586L
                                            -1001763199668l,1016l, text);
                                }
                                catch (Exception e)
                                {
                                    logger.info(e.getMessage());
                                }
                            }

                        }
                        domain1=domain;
                    }
                    else
                    {
                        b=false;
                        n=i;
                        logger.info(domain1+""+n);
                        break;
                    }
                }
                try {
                    Thread.sleep(2000);
                }
                catch(InterruptedException e)
                {
                    logger.info(e.getMessage());
                }
            }
            ests.add(est);
            List<Estibot_Data> estList= new ArrayList<>();
            for(int i=0;i<ests.size();i++)
            {
                String str= ests.get(i);
                estList.addAll(controller.getEstibotsSync(str));
            }
            for(int i=0;i<estList.size();i++)
            {
                Estibot_Data data= estList.get(i);
                SiloAuctionDetails details= siloliverepo.findByDomainIgnoreCase(data.getDomain());
                details.setEST(data.getAppraised_value());
                details.setHighlight(isHighlight(data.getDomain(),data,settings));
                siloliverepo.save(details);
            }
            liveMaprepo.save(lm);
            regmap.clear();
            sendEndHighlights(n1);
            stopWatch.reset();
            summary="";
        }
    }
    @GetMapping("/startlivesilo")
    @Scheduled(cron = "0 00 11 ? * *", zone = "IST")
    Boolean startlivesilo()
    {
        logger.info("Starting Namesilo Live");
        Optional<LiveMap> o= liveMaprepo.findById(4);
        LiveMap lm;
        if(o.isEmpty())
        {
            lm= new LiveMap(4);
        }
        else
            lm=o.get();
        regmap.clear();
        LiveFilterSettings settings= settingsRepo.findById(1).get();
        ResponseEntity<SiloRespDomList> res= getResponseRest("https://www.namesilo.com/api/listDomains?version=1&type=xml&key=7fcf313ace746555cff70389",3,"domlist");
        //ResponseEntity<SiloRespDomList> res= rest.getForEntity("https://www.namesilo.com/api/listDomains?version=1&type=xml&key=7fcf313ace746555cff70389", SiloRespDomList.class);
        SiloRespDomList l= res.getBody();
        List<String> listreg= l.getReply().getDomains();
        for(int i=0;i<listreg.size();i++)
        {
            regmap.add(listreg.get(i).toLowerCase());
        }
        Long lnsid=0l;
        livens.clear();
        livens=lm.getMapns();
        int page=0;
        int n=0;
        boolean b=true;
        Date date = new Date();
        date.setHours(date.getHours() + 2);
        String domain1="";
        int n1=0;
        List<String> ests= new ArrayList<>();
        String est= new String("");
        int estm=0;
        while(b)
        {
            page++;
            String url = "https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389&statusId=2&typeId=3&pageSize=500&page=";
            //ResponseEntity<SiloRespAucList> ress = rest.getForEntity(url+page, SiloRespAucList.class);
            ResponseEntity<SiloRespAucList> ress= getResponseRest(url+page,3,"auclist");
            List<SiloAuctionDetails> al = ress.getBody().getReply().getBody();
            for (int i = 0; i < al.size(); i++) {
                SiloAuctionDetails details = al.get(i);
                String domain = details.getDomain().toLowerCase();
                String endTime = details.getAuctionEndsOn();
                Float bid = details.getCurrentBid();
                Float openingbid= details.getOpeningBid();

                Date end = null;
                try {
                    end = parser.parse(endTime);
                    end.setHours(end.getHours()+7);
                } catch (ParseException p) {
                    logger.info(p.getMessage());
                    continue;
                }
                if (end.before(date))
                {lnsid=details.getNsid();
                    if (bid > openingbid) {
                        if (!livens.containsKey(details.getNsid())) {
                            n1=Math.max(n1,domain.length());
                            livens.put(details.getNsid(), domain);
                            if(estm>=200)
                            {
                                ests.add(est);
                                est= new String("");
                                estm=0;
                            }
                            est=est+domain+">>";
                            estm++;
                            details.setInitialList(true);
                            //logger.info(details.getNsid()+" "+ domain);
                            try {
                                siloliverepo.save(details);
                            }
                            catch(Exception e)
                            {
                               e.printStackTrace();
                            }

                            if (regmap.contains(domain)) {
                                logger.info("Found domain for renewal: " + domain);
                                String url1 = "https://www.namesilo.com/api/renewDomain?version=1&type=xml&key=7fcf313ace746555cff70389&domain=" + domain + "&years=1";
                                SiloRespRenew respRenew = rest.getForEntity(url1, SiloRespRenew.class).getBody();
                                if (respRenew.getReply().getCode() == 300)
                                {logger.info("Renewed domain: " + domain);
                                String text = "Namesilo Own-Check\nRenewed \n\n" + domain;
                                try {
                                    Object obj = telegram.sendAlert(//-856441586L
                                            -1001763199668l, 1016l, text);
                                } catch (Exception e) {
                                    logger.info(e.getMessage());
                                }}
                            }

                        }
                        domain1 = domain;
                    }
                }
                else
                {
                    b=false;
                    n=i;
                    logger.info(domain1+""+n);
                    break;
                }
            }
            try {
                Thread.sleep(2000);
            }
            catch(InterruptedException e)
            {
                logger.info(e.getMessage());
            }
        }
        ests.add(est);
        try {
            List<Estibot_Data> estList = new ArrayList<>();
            for (int i = 0; i < ests.size(); i++) {
                String str = ests.get(i);
                estList.addAll(controller.getEstibotsSync(str));
            }
            for (int i = 0; i < estList.size(); i++) {
                Estibot_Data data = estList.get(i);
                SiloAuctionDetails details = siloliverepo.findByDomainIgnoreCase(data.getDomain());
                details.setEST(data.getAppraised_value());
                boolean highlight = isHighlight(data.getDomain(), data, settings);
                details.setHighlight(highlight);
                if (highlight) {
                    String domain = details.getDomain();
                    String endTime = details.getAuctionEndsOn();
                    Float bid = details.getCurrentBid();
                    Float openingbid = details.getOpeningBid();
                    Date end = null;
                    try {
                        end = parser.parse(endTime);
                        end.setHours(end.getHours() + 7);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    int EST = data.getAppraised_value();
                    String time_left = relTimelive(end);
                    Float minNextBid = minNextBid(bid);
                    sendLiveI(time_left, domain, minNextBid, EST, details.getNsid(), details.getUrl());

                }
                siloliverepo.save(details);
            }

        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }
        stopWatch.start();
        summary="";

        //liveMaprepo.save(live);
        ScheduledFuture scheduledFuture= taskScheduler.scheduleWithFixedDelay(new DetectLiveNs(date,page,n,lnsid),40000);
        taskScheduler.schedule(new StopLiveNs(scheduledFuture),date);
        logger.info("Started Namesilo Live");
     /*   for (Map.Entry<Long,String> entry : livens.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());*/
        sendHighlights(n1);
        return true;
    }
    @Autowired
    GoDaddyFeign goDaddyFeign;

    void sendSummary()
    {
        if(stopWatch.isStarted())
        {
            stopWatch.split();
            if(stopWatch.getSplitTime()>240000)
            {
                if(summary!=null&&!summary.equals(""))
                    telegram.sendAlert(-1001763199668l, 1016l,"Live Domains in Last 4-5 Minutes:\n\n"+summary);
                summary="";
                stopWatch.reset();stopWatch.start();
            }
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

    public class DetectLiveNs implements Runnable {
        Date date;
        int page, n;
        Long lnsid;

        public DetectLiveNs(Date date, int page, int n, Long lnsid) {
            this.date = date;
            this.page = page;
            this.n = n;
            this.lnsid = lnsid;
        }

        @Override
        public void run() {

            logger.info("Live Detect Service Ran");
            LiveFilterSettings settings = settingsRepo.findById(1).get();
            boolean b = true;
            for (int j = 1; j < page; j++) {

                String url = "https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389&statusId=2&typeId=3&pageSize=500&page=";
                //ResponseEntity<SiloRespAucList> ress = rest.getForEntity(url+j, SiloRespAucList.class);
                ResponseEntity<SiloRespAucList> ress = getResponseRest(url + j, 3, "auclist");

                List<SiloAuctionDetails> al = ress.getBody().getReply().getBody();
                for (int i = 0; i < al.size(); i++) {
                    SiloAuctionDetails details = al.get(i);
                    String domain = details.getDomain().toLowerCase();
                    String endTime = details.getAuctionEndsOn();
                    Float bid = details.getCurrentBid();
                    Float openingbid = details.getOpeningBid();
                    Date end = null;
                    try {
                        end = parser.parse(endTime);
                        end.setHours(end.getHours() + 7);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    Date now = new Date();
                    if (end.getTime() - now.getTime() >= 10800000) {
                        b = false;
                        break;
                    }
                    if (bid > openingbid) {
                        if (!livens.containsKey(details.getNsid())) {
                            livens.put(details.getNsid(), domain);
                            // logger.info(details.getNsid()+" "+ domain);

                            if (regmap.contains(domain)) {
                                logger.info("Found domain for renewal: " + domain);
                                String url1 = "https://www.namesilo.com/api/renewDomain?version=1&type=xml&key=7fcf313ace746555cff70389&domain=" + domain + "&years=1";
                                // SiloRespRenew respRenew = rest.getForEntity(url1, SiloRespRenew.class).getBody();
                                ResponseEntity<SiloRespRenew> resren = getResponseRest(url1, 3, "renew");
                                SiloRespRenew respRenew = resren.getBody();
                                if (respRenew.getReply().getCode() == 300)
                                {
                                    logger.info("Renewed domain: " + domain);
                                String text = "Namesilo Own-Check\nRenewed \n\n" + domain;
                                try {
                                    Object obj = telegram.sendAlert(//-856441586L
                                            -1001763199668l, 1016l, text);
                                } catch (Exception e) {
                                    logger.info(e.getMessage());
                                }
                                }
                            } else {
                                String time_left = relTimelive(end);
                                //Integer gdv= goDaddyFeign.getGDV("sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm",domain).getGovalue();
                                //details.setGdv(gdv);
                                Estibot_Data data = controller.getEstibotSync(domain);
                                Integer EST = 0;
                                if (data != null) {
                                    EST = data.getAppraised_value();
                                    details.setEST(EST);
                                    summary = summary + domain + "\n";

                                    String[] dom = domain.split("\\.", 2);
                                    boolean highlight = isHighlight(data.getDomain(), data, settings, dom);
                                    details.setHighlight(highlight);
                                    if (highlight) {
                                        Float minNextBid = minNextBid(bid);
                                        if (dom[1].equalsIgnoreCase("com")) {
                                            String leads = getLeads(dom[0]);
                                            sendLive(time_left, domain, minNextBid, details.getEST(), details.getNsid(), details.getUrl(), leads);
                                        } else
                                            sendLive(time_left, domain, minNextBid, details.getEST(), details.getNsid(), details.getUrl());
                                    }
                                }
                            }
                            try {
                                siloliverepo.save(details);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }
                    if (lnsid == details.getNsid()) {
                        b = false;
                        break;
                    }

                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.info(e.getMessage());
                }
                if (!b)
                    break;
            }
            if (!b) {
                String url1 = "https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389&statusId=2&typeId=3&pageSize=500&page=";
                //ResponseEntity<SiloRespAucList> ress1 = rest.getForEntity(url1+page, SiloRespAucList.class);
                ResponseEntity<SiloRespAucList> ress1 = getResponseRest(url1 + page, 3, "auclist");

                List<SiloAuctionDetails> al1 = ress1.getBody().getReply().getBody();
                for (int i = 0; i < n; i++) {
                    SiloAuctionDetails details = al1.get(i);
                    String domain = details.getDomain().toLowerCase();
                    String endTime = details.getAuctionEndsOn();
                    Float bid = details.getCurrentBid();
                    Float openingbid = details.getOpeningBid();
                    if (i == n - 1)
                        logger.info(domain);

                    Date end = null;
                    try {
                        end = parser.parse(endTime);
                        end.setHours(end.getHours() + 7);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    Date now = new Date();
                    if (end.getTime() - now.getTime() >= 10800000) {
                        b = false;
                        break;
                    }
                    if (bid > openingbid) {
                        if (!livens.containsKey(details.getNsid())) {
                            //logger.info(details.getNsid()+" "+ domain);
                            livens.put(details.getNsid(), domain);
                            if (regmap.contains(domain)) {
                                logger.info("Found domain for renewal: " + domain);
                                String url2 = "https://www.namesilo.com/api/renewDomain?version=1&type=xml&key=7fcf313ace746555cff70389&domain=" + domain + "&years=1";
                                ResponseEntity<SiloRespRenew> resren = getResponseRest(url2, 3, "renew");
                                SiloRespRenew respRenew = resren.getBody();
                                //SiloRespRenew respRenew = rest.getForEntity(url2, SiloRespRenew.class).getBody();
                                if (respRenew.getReply().getCode() == 300)
                                    logger.info("Renewed domain: " + domain);
                                String text = "Namesilo Own-Check\nRenewed \n\n" + domain;
                                try {
                                    Object obj = telegram.sendAlert(//-856441586L
                                            -1001763199668l, 1016l, text);
                                } catch (Exception e) {
                                    logger.info(e.getMessage());
                                }
                            } else {
                                String time_left = relTimelive(end);
                                Estibot_Data data = controller.getEstibotSync(domain);
                                Integer EST = 0;
                                if (data != null) {
                                    EST = data.getAppraised_value();
                                    details.setEST(EST);
                                    summary = summary + domain + "\n";

                                    String[] dom = domain.split("\\.", 2);
                                    boolean highlight = isHighlight(data.getDomain(), data, settings, dom);
                                    details.setHighlight(highlight);
                                    if (highlight) {
                                        Float minNextBid = minNextBid(bid);
                                        if (dom[1].equalsIgnoreCase("com")) {
                                            String leads = getLeads(dom[0]);
                                            sendLive(time_left, domain, minNextBid, details.getEST(), details.getNsid(), details.getUrl(), leads);
                                        } else
                                            sendLive(time_left, domain, minNextBid, details.getEST(), details.getNsid(), details.getUrl());
                                    }
                                }
                                try {
                                    siloliverepo.save(details);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        if (lnsid == details.getNsid()) {
                            b = false;
                            break;
                        }
                    }
                }
            /*for (Map.Entry<Long,String> entry : livens.entrySet())
                System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());*/
                // liveMaprepo.save(live);
                sendSummary();
            }
        }
    }

    void sendInitialList(int n)
    {
        //int n=32;
        //           currbid,  EST , separators, space around separators
        int t= n+    6  +6 +  2   +      4;
        int d= 4096/t;
        d=d-10;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namesilo Initial List");
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
        List<SiloAuctionDetails> list=siloliverepo.findByInitialListTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                SiloAuctionDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getDomain(), lnc.getCurrentBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,1016l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendList(int n)
    {
        //int n=32;
        //           currbid,  EST , separators, space around separators
        int t= n+    6  +6 +  2   +      4;
        int d= 4096/t;
        d=d-10;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namesilo Initial List");
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
        List<SiloAuctionDetails> list=siloliverepo.findAllByOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                SiloAuctionDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getDomain(), lnc.getCurrentBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,1016l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendList(long chat_id)
    {
        int n=25;
        //           currbid,  EST , separators, space around separators
        int t= n+    6  +6 +  2   +      4;
        int d= 4096/t;
        d=d-10;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namesilo Live List");
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
        List<SiloAuctionDetails> list=siloliverepo.findAllByOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                SiloAuctionDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getDomain(), lnc.getCurrentBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendEndList(int n)
    {
        //           currbid,EST, separators, space around separators
        int t= n+    6 +6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namesilo Next Day List");
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
        List<SiloAuctionDetails> list=siloliverepo.findByEndListTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                SiloAuctionDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getDomain(), lnc.getCurrentBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,1016l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }
    }
    void sendHighlights(long chat_id)
    {
        int n=25;
        //           currbid,  EST , separators, space around separators
        int t= n+    6  +6 +  2   +      4;
        int d= 4096/t;
        d=d-10;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namesilo Highlights");
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
        List<SiloAuctionDetails> list=siloliverepo.findByHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                SiloAuctionDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getDomain(), lnc.getCurrentBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }
    void sendInitialHighlights(int n)
    {
        //int n=32;
        //           currbid,  EST , separators, space around separators
        int t= n+    6  +6 +  2   +      4;
        int d= 4096/t;
        d=d-10;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namesilo Initial Highlights");
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
        List<SiloAuctionDetails> list=siloliverepo.findByInitialListTrueAndHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                SiloAuctionDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getDomain(), lnc.getCurrentBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,1016l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendHighlights(int n)
    {
        //int n=32;
        //           currbid,  EST , separators, space around separators
        int t= n+    6  +6 +  2   +      4;
        int d= 4096/t;
        d=d-10;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namesilo Initial Highlights");
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
        List<SiloAuctionDetails> list=siloliverepo.findByHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                SiloAuctionDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getDomain(), lnc.getCurrentBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,1016l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }


    void sendEndHighlights(int n)
    {
        //           currbid,EST, separators, space around separators
        int t= n+    6 +6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namesilo Next Day Highlights");
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
        List<SiloAuctionDetails> list=siloliverepo.findByEndListTrueAndHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                SiloAuctionDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getDomain(), lnc.getCurrentBid(),lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,1016l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }
    }

    }


//    String time_left= relTimelive(end);
//    String addTime= ft1.format(new Date());
//                            if (regmap.containsKey(domain)) {
//                                    logger.info("Found domain for renewal: " + domain);
//                                    String url = "https://www.namesilo.com/api/renewDomain?version=1&type=xml&key=7fcf313ace746555cff70389&domain=" + domain + "&years=1";
//                                    SiloRespRenew respRenew = rest.getForEntity(url, SiloRespRenew.class).getBody();
//        if (respRenew.getReply().getCode() == 300)
//        logger.info("Renewed domain: " + domain);
//        }
//        else
//        {
//        String text = "Namesilo Live Detect \n \n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + bid + " \n\nLink: " + "https://www.namecheap.com/market/" + domain;
//        try {
//        Object obj = telegram.sendAlert(-1001814695777L, text);
//        }
//        catch(RetryableException re)
//        {
//        logger.info(re.getMessage());
//        try {
//        Thread.sleep(5000);
//        Object obj = telegram.sendAlert(-1001814695777L, text);
//        }
//        catch(InterruptedException ie)
//        {
//        logger.info(ie.getMessage());
//        Thread.currentThread().interrupt();
//        }
//        }
//        catch (Exception e)
//        {
//        logger.info(e.getMessage());
//        }
//        }