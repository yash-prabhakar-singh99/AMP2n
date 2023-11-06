package com.namekart.amp2.Controller;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import com.namekart.amp2.DotDBEntity.DdMatch;
import com.namekart.amp2.DotDBEntity.DotDbResponse;
import com.namekart.amp2.Entity.*;
import com.namekart.amp2.EstibotEntity.Estibot_Data;
import com.namekart.amp2.Feign.*;
import com.namekart.amp2.Repository.*;
import com.namekart.amp2.Service.Service;
//import com.namekart.amp2.Tasks.PlaceBid;
import com.namekart.amp2.SettingsEntity.FastBidSetting;
import com.namekart.amp2.SettingsEntity.LiveFilterSettings;
import com.namekart.amp2.Status;
import com.namekart.amp2.TelegramEntities.*;
import com.namekart.amp2.UserEntities.Action;
import com.namekart.amp2.UserEntities.User;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@CrossOrigin
@RestController
public class Controller {
    Logger logger =Logger.getLogger("Dynadot Yash");
    @Autowired
    DropCatchFeign dropCatchFeign;
    @Autowired
    GoDaddyController goDaddyController;
    @Autowired
    AsyncCalss asyncCalss;
    @Autowired
    Telegram telegram;
    @Autowired
    MyFeignClient myFeignClient;
    @Autowired
    FeignSample feignSample;
    @Autowired
    NamecheapController namecheapController;
    @Autowired
    DropCatchController dropCatchController;
    @Autowired
    MyRepo repo;


    @Autowired
    LiveRepo liveRepo;
    @Autowired
    NotifRepo notifRepo;
    @Autowired
    Bidhisrepo bidhisrepo;
    @Autowired
    WasLiveRepo wasLiveRepo;

    @Autowired
    MapWraprepo mapwraprepo;
    @Autowired
    Service service;
    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    //@Autowired
    AllController controller;

   /* @Autowired
    @Qualifier(value = "taskExecutor")
    ThreadPoolTaskExecutor threadPoolTaskExecutor;*/
   Map<String,Long> map;
   String summary="";
   StopWatch stopWatch;
    @Autowired
    @Qualifier(value = "workStealingPool")
    ForkJoinPool threadPoolExecutor;

    static String key="6QA7t7bx6I7Yjk7Sk8X6f8E7dq6n9R9F6YRa7E7X";
    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    SimpleDateFormat timeft = new SimpleDateFormat("dd/MM HH:mm");
    TimeZone ist = TimeZone.getTimeZone("IST");

    String cronExpression="0 00 23 ? * *";

    String filler="\n";
    Boolean b =true;

    String mute_unmute="\uD83D\uDD08/\uD83D\uDD07";

    @Autowired
    LiveMaprepo liveMaprepo;

    public Controller(AllController controller)
    {
        this.controller=controller;
       // taskmap= new ConcurrentHashMap<>();
        this.taskmap=controller.getTaskmap();
        ft1.setTimeZone(ist);
        timeft.setTimeZone(ist);
        map=new HashMap<>();
        for(int i=0;i<66;i++)
            filler=filler+"_";
        text1="Dynadot" +filler+"\n";
        textob="Dynadot Auction OUTBID!!" +filler+"\n";
        stopWatch=new StopWatch();
        /*this.liveMaprepo=liveMaprepo;
        LiveMap lm=null;
        lm = liveMaprepo.findById(1).get();*/
    }
    //ScheduledFuture scheduledFuture;
    @Autowired
    FastSettingsRepo fastSettingsRepo;

    FastBidSetting fastBidSetting;

    @PostConstruct
    void postConstruct()
    {
        Optional<FastBidSetting> op= fastSettingsRepo.findById("Dynadot");
        if(!op.isPresent())
        {
            fastBidSetting=new FastBidSetting("Dynadot",4,1000);
            fastSettingsRepo.save(fastBidSetting);
        }
    }

    void setFastBidSetting(int n, int amount)
    {
        fastBidSetting.setFastBidAmount(amount);fastBidSetting.setFastN(n);
        fastBidSetting=fastSettingsRepo.save(fastBidSetting);
    }

    @Autowired
    UserRepository userRepository;

    User getUser()
    {
        return userRepository.findByEmail(getToken().getClaim("unique_name")+"");
    }

    User getUser(Long tg_id)
    {
        return userRepository.findByTgUserId(tg_id);
    }

    AADOAuth2AuthenticatedPrincipal getToken()
    {
        return (AADOAuth2AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    String getUserName()
    {
        return (getToken().getClaim("unique_name")+"");
    }
    @Autowired
    ActionRepository actionRepository;

    void saveAction(String action, String medium, User user, DBdetails dbdetails, Notification notification, boolean success, String domain, String userName)
    {
        Action action1=new Action(action,medium,user,dbdetails,notification,success,domain,userName);
        actionRepository.save(action1);
    }
    void saveAction(String action, String medium, String telegramGroup, DBdetails dbdetails, Notification notification, boolean success, String domain,Long tg_id)
    {

        User user=userRepository.findByTgUserId(tg_id);
        String userName=user.getEmail();
        Action action1=new Action(action,medium,telegramGroup,user,dbdetails,notification,success,domain,userName);
        actionRepository.save(action1);
    }

    void saveAction(String action, String medium, DBdetails dbdetails, Notification notification, boolean success, String domain, Long tg_id)
    {
        User user=userRepository.findByTgUserId(tg_id);
        String userName=user.getEmail();
        Action action1=new Action(action,medium,user,dbdetails,notification,success,domain,userName);
        actionRepository.save(action1);
    }

    String liveFormata(String status, String timeLeft, String domain, String minBid, String ourMaxBid, long age, Integer EST)
    {
        if(ourMaxBid==null||ourMaxBid.isEmpty())
        {
            ourMaxBid="0";
        }
        String text="DD "+status+" - "+timeLeft+"\n"+domain+"\n"+"Price: "+minBid+" | Our Limit: "+ourMaxBid+"\n" +"Age: "+age+" | EST: "+EST;
        return text;
    }
    String liveFormata(String status, String timeLeft, String domain, String minBid, String ourMaxBid, long age, String EST)
    {
        if(ourMaxBid==null||ourMaxBid.isEmpty())
        {
            ourMaxBid="0";
        }
        String text="DD "+status+" - "+timeLeft+"\n"+domain+"\n"+"Price: "+minBid+" | Our Limit: "+ourMaxBid+"\n" +"Age: "+age+" | EST: "+EST;
        return text;
    }
    InlineKeyboardMarkup getKeyboardWatch(String domain,Long auctionId, String currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " dd "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " dd "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton(mute_unmute, "m" + " dd "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " dd "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.dynadot.com/market/auction/" + domain);
        row1.add(link);
        row2.add(new InlineKeyboardButton("Leads", "l" + " dd "+auctionId+" " + domain + " " + currbid));
        row2.add(new InlineKeyboardButton("Stats", "s" + " dd "+auctionId+" " + domain + " " + currbid));
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        rows.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getKeyboardOb(String domain,Long auctionId, String currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row2=new ArrayList<>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " dd "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " dd "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " dd "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.dynadot.com/market/auction/" + domain);
        row1.add(link);
        row2.add(new InlineKeyboardButton("Leads", "l" + " dd "+auctionId+" " + domain + " " + currbid));
        row2.add(new InlineKeyboardButton("Stats", "s" + " dd "+auctionId+" " + domain + " " + currbid));

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        rows.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getKeyboardLive(String domain, Long auctionId,String currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " dd "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid", "b" + " dd "+auctionId+" " + domain + " " + currbid));
        row.add(new InlineKeyboardButton("Watch", "w" + " dd " +auctionId+" "+ domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Track", "t" + " dd " +auctionId+" "+ domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " dd "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.dynadot.com/market/auction/" + domain);
        row1.add(link);
        row2.add(new InlineKeyboardButton("Leads", "l" + " dd "+auctionId+" " + domain + " " + currbid));
        row2.add(new InlineKeyboardButton("Stats", "s" + " dd "+auctionId+" " + domain + " " + currbid));

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        rows.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }

    void sendOutbid(String status, String timeLeft, String domain, String minBid, String ourMaxBid, long age, Integer EST,Long auctionId)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,ourMaxBid,age,EST);
        telegram.sendKeyboard(new SendMessage(-1001866615838L,text,getKeyboardOb(domain,auctionId,minBid)));
    }

    void sendWatchlist(String status, String timeLeft, String domain, String minBid, String ourMaxBid, long age, Integer EST,Long auctionId)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,ourMaxBid,age,EST);
        telegram.sendKeyboard(new SendMessage(-1001887754426L,text,getKeyboardWatch(domain,auctionId,minBid)));
    }

    void sendLive(String timeLeft, String domain, String minBid, long age,Integer EST,String ourMaxBid,Long auctionId)
    {
        String text=liveFormata("Live Detect",timeLeft,domain,minBid,ourMaxBid,age,EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1014l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, String minBid, long age,Integer EST,Long auctionId)
    {
        String text=liveFormata("Live Detect",timeLeft,domain,minBid,"",age,EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1014l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, String minBid, long age,Integer EST,Long auctionId,String leads)
    {
        String text=liveFormata("Live Detect",timeLeft,domain,minBid,"",age,EST);
        if(leads!=null&&!leads.equals(""))
            text=text+"\n"+leads;
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1014l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, String minBid, long age,Integer EST,Long auctionId,String leads,String ourMaxBid)
    {
        String text=liveFormata("Live Detect",timeLeft,domain,minBid,ourMaxBid,age,EST);
        if(leads!=null&&!leads.equals(""))
            text=text+"\n"+leads;
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1014l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLiveI(String timeLeft, String domain, String minBid, long age,Integer EST,Long auctionId, String ourMaxBid)
    {
        String text=liveFormata("Initial Detect",timeLeft,domain,minBid,ourMaxBid,age,EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,24112l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLiveI(String timeLeft, String domain, String minBid, long age,Integer EST,Long auctionId)
    {
        String text=liveFormata("Initial Detect",timeLeft,domain,minBid,"",age,EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,24112l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendOutbid(String status, String timeLeft, String domain, String minBid, String ourMaxBid, long age, String EST,Long auctionId)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,ourMaxBid,age,EST);
        telegram.sendKeyboard(new SendMessage(-1001866615838L,text,getKeyboardOb(domain,auctionId,minBid)));
    }

    void sendWatchlist(String status, String timeLeft, String domain, String minBid, String ourMaxBid, long age, String EST,Long auctionId)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,ourMaxBid,age,EST);
        telegram.sendKeyboard(new SendMessage(-1001887754426L,text,getKeyboardWatch(domain,auctionId,minBid)));
    }
    void sendLive(String status,String timeLeft, String domain, String minBid, long age, String EST,Long auctionId)
    {
        String text=liveFormata(status,timeLeft,domain,minBid,"",age,EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1014l,text,getKeyboardLive(domain,auctionId,minBid)));
    }

    @GetMapping("/gettimedyna")
    void getAuctiondc1()
    {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        TimeZone pst=TimeZone.getTimeZone("PST");
        ft.setTimeZone(pst);
        Long l=1674237779837l;
        String[] s1=TimeZone.getAvailableIDs();
System.out.println(Arrays.toString(s1));
        String s="2023/01/20 13:30";
        try{
            Date d= new Date(l);
            System.out.println(d);
            logger.info(ft1.format(d));
        }
        catch(Exception p)
        {
            logger.info(p.getMessage());
        }
    }
    ConcurrentMap<String, Status> taskmap;
    @Scheduled(cron = "0 00 09 ? * *", zone = "IST")
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
    sendResultList();
    sendScheduledList();


}
    @Scheduled(cron = "0 00 18 ? * *", zone = "IST")
    void
    healthCheckfinal()
    {
        int t=8+3+6;
        String s = String.format("| %-" + (t - 4) + "s |%n", "Health Check");
        for (int i = 0; i < t; i++) {
            s = s + "-";
        }
        s = s + "\n\n";
        s = s + String.format("%-2s | %-2s | %-2s | %2s%n", "DD", "DC", "NC", "GD");
        for (int i = 0; i < t; i++) {
            s = s + "-";
        }
        s = s + "\n\n";
        s = s + String.format("%-2s | %-2s | %-2s | %2s%n",healthCheck()?"OK":"NO",dropCatchController.healthCheck()?"OK":"NO", namecheapController.healthCheck()?"OK":"NO", goDaddyController.healthCheck()?"OK":"NO");
        telegram.sendAlert(-1001763199668l,44167l, "<pre>" + s + "</pre>", "HTML");

    }
@GetMapping("/checkddd")
void sendResultList()
{
    Date date= new Date();
    String d2= ft1.format(date);
    date.setDate(date.getDate()-1);
    String d1= ft1.format(date);
    //int l= repo.findLargestResultLength(d1,d2);
    List<DBdetails> list= repo.getResultList(d1,d2);
    if(list.size()!=0) {
        int n = 0;
        for (int i = 0; i < list.size(); i++) {
            n = Math.max(n, list.get(i).getDomain().length());
        }
//        Plat   Result  High Bid  Our Max Bid  separators spaces
        int t = 9 + n + 6 + 8 + 11 + 4 + 8;
        int d = 4096 / t;
        d = d - 6;
        String s = String.format("| %-" + (t - 4) + "s |%n", "Results of Last Day");
        for (int i = 0; i < t; i++) {
            s = s + "-";
        }
        s = s + "\n\n";
        s = s + String.format("%-9s | %-" + n + "s | %-6s | %-8s | %11s%n", "Platform", "Domain", "Result", "High Bid", "Our Max Bid");
        for (int i = 0; i < t; i++) {
            s = s + "-";
        }
        s = s + "\n\n";

        int l = list.size();
        int j = 0;
        while (l > 0) {
            for (int i = 0; i < l && i < d; i++) {
                DBdetails lnc = list.get(j);
                j++;
                try {
                s = s + String.format("%-9s | %-" + n + "s | %-6s | %-8s | %11s%n", lnc.getPlatform(), lnc.getDomain(), lnc.getResult(), lnc.getCurrbid(), lnc.getBidAmount());
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage());
                }

            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l, "<pre>" + s + "</pre>", "HTML");
            l = l - d;
            s = "";
        }
    }
}

    @GetMapping("/checkddd1")
    void sendScheduledList()
    {
        Date date= new Date();
        String d2= ft1.format(date);
        date.setDate(date.getDate()+1);
        String d1= ft1.format(date);
        //int l= repo.findLargestResultLength(d1,d2);
        List<DBdetails> list= repo.getScheduledList(d2,d1);
        if(list.size()!=0) {
            int n = 0;
            for (int i = 0; i < list.size(); i++) {
                n = Math.max(n, list.get(i).getDomain().length());
            }
//             Plat   Status  High Bid  Our Max Bid  separators spaces
            int t = 9 + n + 7 + 8 + 11 + 4 + 8;
            int d = 4096 / t;
            d = d - 6;
            String s = String.format("| %-" + (t - 4) + "s |%n", "Targets for Next Day");
            for (int i = 0; i < t; i++) {
                s = s + "-";
            }
            s = s + "\n\n";
            s = s + String.format("%-9s | %-" + n + "s | %-7s | %-8s | %11s%n", "Platform", "Domain", "Status", "High Bid", "Our Max Bid");
            for (int i = 0; i < t; i++) {
                s = s + "-";
            }
            s = s + "\n\n";

            int l = list.size();
            int j = 0;
            while (l > 0) {
                for (int i = 0; i < l && i < d; i++) {
                    DBdetails lnc = list.get(j);
                    j++;
                    try {
                        s = s + String.format("%-9s | %-" + n + "s | %-7s | %-8s | %11s%n", lnc.getPlatform(), lnc.getDomain(), Float.valueOf(lnc.getBidAmount()) > Float.valueOf(lnc.getCurrbid()) ? "Winning" : "Losing", lnc.getCurrbid(), lnc.getBidAmount());
                    }
                    catch(Exception e)
                    {
                        logger.info(e.getMessage());
                    }

                }
                // System.out.println(s);
                telegram.sendAlert(-1001763199668l,845l, "<pre>" + s + "</pre>", "HTML");
                l = l - d;
                s = "";
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
            try {
                status.getFuture().cancel(false);
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
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

    @PreAuthorize("hasAuthority('APPROLE_Bid_DD')")
 @PostMapping("/postDomains")
    List<Integer> mainmain(@RequestBody List<List<String>> ddlist) {

        CompletableFuture<List<Estibot_Data>> cf= controller.getEstibotList1(ddlist);
     List<Integer> l = new ArrayList<>();
     int a = 0;
     int n = ddlist.size();
    // List<Long> ids = new ArrayList<>();
     Map<String, String> map = new HashMap<>();
     String domainss = "";
     for (int i = 0; i < n; i++) {
         String domain = ddlist.get(i).get(0).toLowerCase();
         map.put(domain, ddlist.get(i).get(1));
         domainss = domainss + domain + ",";
     }


     Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domainss, "usd");
     logger.info(ra.getStatus());

     if (ra!=null&&ra.getStatus()!=null) {
         if (ra.getStatus() != null && ra.getStatus().equals("success")) {

             List<Auction_details> AuctionDetails = ra.getAuction_details();
             for (int i = 0; i < AuctionDetails.size(); i++) {

                 Auction_json aj = AuctionDetails.get(i).getAuction_json();
                 String domain = aj.getDomain().toLowerCase();
                 String bid = map.get(domain);


                 try {
                     if (!ra.getAuction_det().getAuction_json().getAuction_ended())
                     {
                     Long timestamp = aj.getEnd_time_stamp();
                     Date date = new Date(timestamp);
                     String pst = aj.getEnd_time();
                     Date now = new Date();
                     if (Float.valueOf(bid) >= Float.valueOf(aj.getAccepted_bid_price())) {
                         if (timestamp - now.getTime() > 300000) {
                             Date date1 = new Date(date.getTime() - 300000);
                             ScheduledFuture pre = taskScheduler.schedule(new PreCheck(domain, bid), date1);
                             enterTaskMap(domain, pre, "pc");
                         } else {
                             date.setSeconds(date.getSeconds() - 10);
                             ScheduledFuture place = taskScheduler.schedule(new PlaceBid(domain, bid, pst), date);
                             enterTaskMap(domain, place, "pb");
                         }
                         a++;

                         placebidnotifanddb(domain, bid, aj, date, now);
                     } else {
                         CompletableFuture.runAsync(() -> {

                             String time = ft1.format(now);
                             synchronized (this) {
                                 telegram.sendAlert(-930742733l, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + aj.getAccepted_bid_price());
                                 Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + aj.getAccepted_bid_price()));
                                 saveAction("Bid Scheduled","UI",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                                 logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + aj.getAccepted_bid_price());
                             }
                         }, threadPoolExecutor);
                     }
                 }
                     else {
                         CompletableFuture.runAsync(() ->
                         {
                             Date now = new Date();
                             String time = timeft.format(now);
                             telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                             Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                             saveAction("Bid Scheduled","UI",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                             logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                         }, threadPoolExecutor);
                     }

                 } catch (Exception E) {
                     Date date = new Date();
                     String time = ft1.format(date);
                     String content = myFeignClient.getAuctionDetailstr(key, "get_auction_details", domain, "usd");
                     logger.info(time + ": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                     logger.info(time + ": " + E.getMessage());
                     //str.add(domain);
                     try {
                         notifRepo.save(new Notification(time + ": Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
                     } catch (Exception A) {
                         notifRepo.save(new Notification(time + ": Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + ". See log for reason"));

                     }
                 }
             }
             // asyncCalss.getGDVs(ids);
             l.add(a);
             l.add(n);

         } else {
             Date date = new Date();
             String time = ft1.format(date);
             Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid not scheduled for domains as: " + ra.getCont()));
             logger.info(time + ": Bid not scheduled for domains as: " + ra.getCont());
         }
     }else {
         Date date = new Date();
         String time = ft1.format(date);
         notifRepo.save(new Notification("Dynadot", time, "Bid not scheduled for domains"));
         logger.info(time + ": Bid not scheduled for domains");
     }
     controller.putESTinDB(cf);
     return l;
 }

    BulkScheduleResponse mainmainbot(@RequestBody List<List<String>> ddlist,Long tg_id) {

        CompletableFuture<List<Estibot_Data>> cf= controller.getEstibotList1(ddlist);
        List<Integer> l = new ArrayList<>();
        int a = 0;
        int n = ddlist.size();
        BulkScheduleResponse res=null;
        // List<Long> ids = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        String domainss = "";
        for (int i = 0; i < n; i++) {
            int l1=ddlist.get(i).size();
            String domain = ddlist.get(i).get(0).toLowerCase();
            map.put(domain, ddlist.get(i).subList(1,l1));
            domainss = domainss + domain + ",";
        }
        String s="";

        Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domainss, "usd");
        logger.info(ra.getStatus());

        if (ra!=null&&ra.getStatus()!=null) {
            if (ra.getStatus() != null && ra.getStatus().equals("success")) {

                List<Auction_details> AuctionDetails = ra.getAuction_details();
                for (int i = 0; i < AuctionDetails.size(); i++) {

                    Auction_json aj = AuctionDetails.get(i).getAuction_json();
                    String domain = aj.getDomain().toLowerCase();
                    String bid = map.get(domain).get(0);


                    try {
                        if (!ra.getAuction_det().getAuction_json().getAuction_ended())
                        {
                            Long timestamp = aj.getEnd_time_stamp();
                            Date date = new Date(timestamp);
                            String pst = aj.getEnd_time();
                            Date now = new Date();
                            if (Float.valueOf(bid) >= Float.valueOf(aj.getAccepted_bid_price())) {
                                if (timestamp - now.getTime() > 300000) {
                                    Date date1 = new Date(date.getTime() - 300000);
                                    ScheduledFuture pre = taskScheduler.schedule(new PreCheck(domain, bid), date1);
                                    enterTaskMap(domain, pre, "pc");
                                } else {
                                    date.setSeconds(date.getSeconds() - 10);
                                    ScheduledFuture place = taskScheduler.schedule(new PlaceBid(domain, bid, pst), date);
                                    enterTaskMap(domain, place, "pb");
                                }
                                a++;

                                placebidnotifanddbtg(domain, map.get(domain), aj, date, now,tg_id);
                            } else {
                                String text="Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + aj.getAccepted_bid_price();
                                s=s+text+"\n";
                                CompletableFuture.runAsync(() -> {

                                    String time = ft1.format(now);
                                    synchronized (this) {
                                        telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + aj.getAccepted_bid_price());

                                        Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + aj.getAccepted_bid_price()));
                                        saveAction("Bid Scheduled","CPanel",repo.findTopByDomain(domain),notification,false,domain,tg_id);
                                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + aj.getAccepted_bid_price());
                                    }
                                }, threadPoolExecutor);
                            }
                        }
                        else {
                            String text="Bid NOT SCHEDULED for" + domain + " as auction has ended";
                            s=s+text+"\n";
                            CompletableFuture.runAsync(() ->
                            {
                                Date now = new Date();
                                String time = timeft.format(now);
                                telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                                saveAction("Bid Scheduled","CPanel",repo.findTopByDomain(domain),notification,false,domain,tg_id);
                                logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                            }, threadPoolExecutor);
                        }

                    } catch (Exception E) {
                        Date date = new Date();
                        String time = ft1.format(date);
                        String content = myFeignClient.getAuctionDetailstr(key, "get_auction_details", domain, "usd");
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                        logger.info(time + ": " + E.getMessage());
                        //str.add(domain);
                        try {
                            notifRepo.save(new Notification(time + ": Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
                        } catch (Exception A) {
                            notifRepo.save(new Notification(time + ": Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + ". See log for reason"));

                        }
                    }
                }
                // asyncCalss.getGDVs(ids);
                l.add(a);
                l.add(n);
                res= new BulkScheduleResponse(l,s);
            } else {
                Date date = new Date();
                String time = ft1.format(date);
                notifRepo.save(new Notification("Dynadot", time, "Bid not scheduled for domains as: " + ra.getCont()));
                logger.info(time + ": Bid not scheduled for domains as: " + ra.getCont());
            }
        }else {
            Date date = new Date();
            String time = ft1.format(date);
            notifRepo.save(new Notification("Dynadot", time, "Bid not scheduled for domains"));
            logger.info(time + ": Bid not scheduled for domains");
        }
        controller.putESTinDB(cf);
        return res;
    }

CompletableFuture placebidnotifanddb(String domain, String bid, Auction_json aj, Date date, Date now)
{
   return CompletableFuture.runAsync(() ->
    {
    String ist = ft1.format(date);
    String time_left = relTime(date);
    String bidist = ft1.format(date);
    String time = timeft.format(now);
    String pst = aj.getEnd_time();
        Optional<DBdetails> op =null;

    Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist));
    logger.info(time + ": Bid SCHEDULED for " + domain + " at price " + bid + " time " + bidist + " i.e. " + date);
    telegram.sendAlert(-1001763199668l,1005l, "Dynadot: Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist);

     op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Dynadot", aj.getAuction_id()));

DBdetails dBdetails = null;

    if (!op.isPresent()) {
        dBdetails = new DBdetails(domain, aj.getAuction_id(), "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), bid, "Bid Scheduled", pst, ist,false);
    } else {
        dBdetails = op.get();
        dBdetails.setResult("Bid Scheduled");
        dBdetails.setBidAmount(bid);
        dBdetails.setBidplacetime(bidist);
        dBdetails.setCurrbid(aj.getCurrent_bid_price());
        dBdetails.setBidders(aj.getBidders());
        dBdetails.setTime_left(time_left);
        dBdetails.setEndTimepst(pst);
        dBdetails.setEndTimeist(ist);
    }
        dBdetails.setScheduled(true);

        synchronized (this) {
        repo.save(dBdetails);
    }
        saveAction("Bid Scheduled","UI",getUser(),dBdetails,notification,true,domain,getUserName());
        //asyncCalss.getGDVSync(dBdetails);
        },threadPoolExecutor);

}
    CompletableFuture placebidnotifanddbtg(String domain, List<String> list, Auction_json aj, Date date, Date now, Long tg_id)
    {
        return CompletableFuture.runAsync(() ->
        {
            String bid=list.get(0);
            String ist = ft1.format(date);
            String time_left = relTime(date);
            String bidist = ft1.format(date);
            String time = timeft.format(now);
            String pst = aj.getEnd_time();
            Optional<DBdetails> op =null;

            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist));
            logger.info(time + ": Bid SCHEDULED for " + domain + " at price " + bid + " time " + bidist + " i.e. " + date);
            telegram.sendAlert(-1001763199668l,1005l, "Dynadot: Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist);

            op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Dynadot", aj.getAuction_id()));

            DBdetails dBdetails = null;

            if (!op.isPresent()) {
                dBdetails = new DBdetails(domain, aj.getAuction_id(), "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), bid, "Bid Scheduled", pst, ist,false);
            } else {
                dBdetails = op.get();
                dBdetails.setResult("Bid Scheduled");
                dBdetails.setBidAmount(bid);
                dBdetails.setBidplacetime(bidist);
                dBdetails.setCurrbid(aj.getCurrent_bid_price());
                dBdetails.setBidders(aj.getBidders());
                dBdetails.setTime_left(time_left);
                dBdetails.setEndTimepst(pst);
                dBdetails.setEndTimeist(ist);
            }
            dBdetails.setScheduled(true);
            if(list.size()>1)
            {
                if(list.size()==3)
                {
                    int fast=Integer.valueOf(list.get(2));
                    if(fast>10)
                    {
                        dBdetails.setFastBidAmount(list.get(2));
                        dBdetails.setFast_n(fastBidSetting.getFastN());
                    }
                    else {
                        dBdetails.setFast_n(fast);
                        dBdetails.setFastBidAmount(String.valueOf(fastBidSetting.getFastBidAmount()));
                    }
                }
                else if(list.size()==4)
                {
                    int fast=Integer.valueOf(list.get(2));
                    dBdetails.setFastBidAmount(list.get(3));
                    dBdetails.setFast_n(fast);
                }
                else if(list.size()==2)
                {
                    dBdetails.setFast_n(fastBidSetting.getFastN());
                    dBdetails.setFastBidAmount(String.valueOf(fastBidSetting.getFastBidAmount()));
                }
            }

            synchronized (this) {
                repo.save(dBdetails);
            }
            saveAction("Bid Scheduled","CPanel",dBdetails,notification,true,domain,tg_id);
            //asyncCalss.getGDVSync(dBdetails);
        },threadPoolExecutor);

    }

@GetMapping("/trygdv")
void try2()
{
    taskScheduler.scheduleAtFixedRate(new Try(),1000);
}

void try1()
{
    CompletableFuture.runAsync(()->{
        logger.info("1");

        try {
            Thread.sleep(2000);
        }
        catch(InterruptedException i)
        {
            logger.info(i.getMessage());
        }
    },threadPoolExecutor);
}

class Try implements Runnable
{
    @Override
    public void run() {
        try1();
    }
}


    @PreAuthorize("hasAuthority('APPROLE_Bid_DD')") @PostMapping("/postDomainsinstant")
    List<Integer> mainmaininstant(@RequestBody List<List<String>> ddlist)
    {
        //SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm z");

        CompletableFuture<List<Estibot_Data>> cf= controller.getEstibotList1(ddlist);

        List<Integer> l= new ArrayList<>();

        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            String domain= ddlist.get(i).get(0).toLowerCase();
            String bid=ddlist.get(i).get(1);
            try {
                Response_PlaceBid ra = myFeignClient.placeAuctionBids(key, "place_auction_bid", domain, bid,"usd");
                if (ra.getStatus().equals("success")) {

                    a++;

                    Date date = new Date(ra.getAuction_details().getAuction_json().getEnd_time_stamp());
                    String pst = ra.getAuction_details().getAuction_json().getEnd_time();
                    //String pst1=pst.substring(0,17)+"IST";

                    String time_left= relTime(date);
                    String ist = ft1.format(date);
                    System.out.println(ist);
                    date.setTime(date.getMinutes()+30);
                   ScheduledFuture gr= taskScheduler.schedule(new GetResultdyna(domain),date);
                    enterTaskMap(domain,gr,"gr");
                   Date date1= new Date();
                   // String bidist = ft1.format(date1);
                    String time= timeft.format(date1);
                   //System.out.println(date);
                    Notification notification=notifRepo.save(new Notification("Dynadot",time,"Instant Bid Placed for " + domain + " at price " + bid ));
                    telegram.sendAlert(-1001763199668l,1005l,"Dynadot: Instant Bid Placed for " + domain + " at price " + bid );

                    logger.info(time+": Instant Bid Placed for " + domain + " at price " + bid );
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
                    DBdetails dBdetails = null;
                    List<Bid_details> bd = ra.getAuction_details().getBid_history();
                    Auction_json aj = ra.getAuction_details().getAuction_json();
                    if (!op.isPresent()) {
                        dBdetails = new DBdetails(domain, aj.getAuction_id(),"Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), bid, "Bid Placed", pst, ist, "", true);
                    } else {
                        dBdetails = op.get();
                        dBdetails.setResult("Bid Placed");
                        dBdetails.setIsBidPlaced(true);
                        dBdetails.setBidAmount(bid);
                        //dBdetails.setBidplacetime(bidist);
                        dBdetails.setCurrbid(aj.getCurrent_bid_price());
                        dBdetails.setBidders(aj.getBidders());
                        dBdetails.setTime_left(time_left);
                        dBdetails.setAge(aj.getAge());
                        dBdetails.setEstibot(Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)));
                        //dBdetails.setAuctiontype(aj.getAuction_type());
                        dBdetails.setEndTimepst(pst);
                        dBdetails.setEndTimeist(ist);
                    }
                    repo.save(dBdetails);
                    saveAction("Bid Instant","UI",getUser(),dBdetails,notification,true,domain,getUserName());

                    /*if(bd!=null)
                    for (int j = 0; j < bd.size(); j++) {
                        DB_Bid_Details dbd = new DB_Bid_Details(dBdetails, bd.get(j));
                        dBdetails.getBidhistory().add(dbd);
                        bidhisrepo.save(dbd);
                    }*/

                } else {
                  //  str.add(domain);
                    String content = ra.getContent();
                    Date now= new Date();
                    String time = ft1.format(now);
                    logger.info(time+": Dynadot: Instant Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content);
                    try {
                        Notification notification=notifRepo.save(new Notification("Dynadot", time, "Instant Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content));
                        saveAction("Bid Scheduled","UI",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                    }
                    catch(Exception e)
                    {
                        logger.info(e.getMessage());
                    }
                }
            }
            catch(Exception E) {
                Date now = new Date();
                String time = ft1.format(now);
                //String content= myFeignClient.placeAuctionBidstr(key,"get_auction_details",domain,bid,"usd");
                logger.info(time + ": Dynadot: Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + E.getMessage());
                try {
                    notifRepo.save(new Notification(time + ": Dynadot: Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + E.getMessage()));
                    //str.add(domain);
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage());
                }
            }
        }
        l.add(a);
        l.add(n);
        controller.putESTinDB(cf);
        return l;


    }

    @PreAuthorize("hasAnyAuthority('APPROLE_Bid_DD','APPROLE_Live_Bid_DD')")
    @GetMapping("/schedulesingledynalive")
    float mainmainsingle1live(@RequestParam String domain,@RequestParam Long auctionId,@RequestParam String bid)
    {

        try {
            CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
            //logger.info("no");
            Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
            if(ra.getStatus()!=null)
            {
                logger.info(ra.getStatus());
                if (ra.getStatus().equals("success")) {
                    if (!ra.getAuction_det().getAuction_json().getAuction_ended()) {
                        Date now = new Date();
                        float currbidf = Float.valueOf(ra.getAuction_det().getAuction_json().getAccepted_bid_price());
                        if (currbidf <= Float.valueOf(bid)) {
                            Long timestamp = ra.getAuction_det().getAuction_json().getEnd_time_stamp();
                            Date date = new Date(timestamp);
                            //logger.info("yes1");
                            String pst = ra.getAuction_det().getAuction_json().getEnd_time();
                            if (timestamp - now.getTime() > 300000) {
                                date.setMinutes(date.getMinutes() - 5);
                                ScheduledFuture pre = taskScheduler.schedule(new PreCheck(domain, bid), date);
                                enterTaskMap(domain, pre, "pc");
                            } else {
                                date.setSeconds(date.getSeconds() - 10);
                                ScheduledFuture place = taskScheduler.schedule(new PlaceBid(domain, bid, pst), date);
                                enterTaskMap(domain, place, "pb");
                            }

                            CompletableFuture.runAsync(() ->
                            {
                                String ist = ft1.format(date);
                                String time_left = relTime(date);
                                String bidist = ft1.format(date);
                                String time = timeft.format(now);
                                // Integer GDV = liveRepo.findByAuctionid(ra.getAuction_det().getAuction_json().getAuction_id()).getGdv();
                                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist));
                                telegram.sendAlert(-1001763199668l, 1005l, "Dynadot: Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist);
                                logger.info(time + ": Bid SCHEDULED for " + domain + " at price " + bid + " time " + date);
                                Auction_json aj = ra.getAuction_det().getAuction_json();
                                Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(aj.getAuction_id()));
                                DBdetails dBdetails = null;
                                List<Bid_details> bd = ra.getAuction_details().get(0).getBid_history();

                                if (!op.isPresent()) {
                                    dBdetails = new DBdetails(domain, aj.getAuction_id(), "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), bid, "Bid Scheduled", pst, ist, false);
                                } else {
                                    dBdetails = op.get();
                                    dBdetails.setResult("Bid Scheduled");
                                    dBdetails.setBidAmount(bid);
                                    dBdetails.setBidplacetime(bidist);
                                    dBdetails.setCurrbid(aj.getCurrent_bid_price());
                                    dBdetails.setBidders(aj.getBidders());
                                    dBdetails.setTime_left(time_left);
                                    dBdetails.setEndTimepst(pst);
                                    dBdetails.setEndTimeist(ist);
                                }
                                dBdetails.setScheduled(true);
                                saveAction("Bid Scheduled","UI List",getUser(),dBdetails,notification,true,domain,getUserName());

                                // dBdetails.setGdv(GDV);
                                repo.save(dBdetails);
                            }, threadPoolExecutor);
                            controller.putESTinDBSingle(cf);
                            return 0;
                        } else {
                            CompletableFuture.runAsync(() ->
                            {
                                String time = timeft.format(now);
                                telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf);
                                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf));
                                saveAction("Bid Scheduled","UI List",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                                logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf);
                            }, threadPoolExecutor);
                            return currbidf;
                        }
                    } else {
                        CompletableFuture.runAsync(() ->
                        {
                            Date now = new Date();
                            String time = timeft.format(now);
                            telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                            saveAction("Bid Scheduled","UI List",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                        }, threadPoolExecutor);
                        return 2;
                    }
                } else {
                    //str.add(domain);
                    CompletableFuture.runAsync(() ->
                    {
                        String content = ra.getCont();
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                        try {
                            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
                            saveAction("Bid Scheduled","UI List",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }, threadPoolExecutor);
                    return 1;
                }
            }
            else
            {
                CompletableFuture.runAsync(() ->
                {
                    Date now = new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                    saveAction("Bid Scheduled","UI List",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                }, threadPoolExecutor);
                return 2;
            }
        }
        catch(Exception E)
        {

            Date now = new Date();
            String time= timeft.format(now);
            logger.info(time+": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + E.getMessage());
            try {
                notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + E.getMessage()));
            }
            catch(Exception A)
            {
                notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for " + domain + " at price " + bid+". See log for reason"));

            }
        }
        return 0;
    }

    @PreAuthorize("hasAuthority('APPROLE_Bid_DD')")
    @GetMapping("/schedulesingledyna")
    float mainmainsingle1(@RequestParam String domain,@RequestParam Long auctionId,@RequestParam String bid)
    {
        try {
            CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
            //logger.info("no");
            Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
            if(ra.getStatus()!=null)
            {
                logger.info(ra.getStatus());
                if (ra.getStatus().equals("success")) {
                    if (!ra.getAuction_det().getAuction_json().getAuction_ended()) {
                        Date now = new Date();
                        float currbidf = Float.valueOf(ra.getAuction_det().getAuction_json().getAccepted_bid_price());
                        if (currbidf <= Float.valueOf(bid)) {
                            Long timestamp = ra.getAuction_det().getAuction_json().getEnd_time_stamp();
                            Date date = new Date(timestamp);
                            //logger.info("yes1");
                            String pst = ra.getAuction_det().getAuction_json().getEnd_time();
                            if (timestamp - now.getTime() > 300000) {
                                date.setMinutes(date.getMinutes() - 5);
                                ScheduledFuture pre = taskScheduler.schedule(new PreCheck(domain, bid), date);
                                enterTaskMap(domain, pre, "pc");
                            } else {
                                date.setSeconds(date.getSeconds() - 10);
                                ScheduledFuture place = taskScheduler.schedule(new PlaceBid(domain, bid, pst), date);
                                enterTaskMap(domain, place, "pb");
                            }

                            CompletableFuture.runAsync(() ->
                            {
                                String ist = ft1.format(date);
                                String time_left = relTime(date);
                                String bidist = ft1.format(date);
                                String time = timeft.format(now);
                                // Integer GDV = liveRepo.findByAuctionid(ra.getAuction_det().getAuction_json().getAuction_id()).getGdv();
                                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist));
                                telegram.sendAlert(-1001763199668l, 1005l, "Dynadot: Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist);
                                logger.info(time + ": Bid SCHEDULED for " + domain + " at price " + bid + " time " + date);
                                Auction_json aj = ra.getAuction_det().getAuction_json();
                                Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(aj.getAuction_id()));
                                DBdetails dBdetails = null;
                                List<Bid_details> bd = ra.getAuction_details().get(0).getBid_history();

                                if (!op.isPresent()) {
                                    dBdetails = new DBdetails(domain, aj.getAuction_id(), "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), bid, "Bid Scheduled", pst, ist, false);
                                } else {
                                    dBdetails = op.get();
                                    dBdetails.setResult("Bid Scheduled");
                                    dBdetails.setBidAmount(bid);
                                    dBdetails.setBidplacetime(bidist);
                                    dBdetails.setCurrbid(aj.getCurrent_bid_price());
                                    dBdetails.setBidders(aj.getBidders());
                                    dBdetails.setTime_left(time_left);
                                    dBdetails.setEndTimepst(pst);
                                    dBdetails.setEndTimeist(ist);
                                }
                                dBdetails.setScheduled(true);
                                saveAction("Bid Scheduled","UI List",getUser(),dBdetails,notification,true,domain,getUserName());

                                // dBdetails.setGdv(GDV);
                                repo.save(dBdetails);
                            }, threadPoolExecutor);
                            controller.putESTinDBSingle(cf);
                            return 0;
                        } else {
                            CompletableFuture.runAsync(() ->
                            {
                                String time = timeft.format(now);
                                telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf);
                                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf));
                                saveAction("Bid Scheduled","UI List",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                                logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf);
                            }, threadPoolExecutor);
                            return currbidf;
                        }
                    } else {
                        CompletableFuture.runAsync(() ->
                        {
                            Date now = new Date();
                            String time = timeft.format(now);
                            telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                            saveAction("Bid Scheduled","UI List",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                        }, threadPoolExecutor);
                        return 2;
                    }
                } else {
                    //str.add(domain);
                    CompletableFuture.runAsync(() ->
                    {
                        String content = ra.getCont();
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                        try {
                            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
                            saveAction("Bid Scheduled","UI List",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }, threadPoolExecutor);
                    return 1;
                }
            }
            else
            {
                CompletableFuture.runAsync(() ->
                {
                    Date now = new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                    saveAction("Bid Scheduled","UI List",getUser(),repo.findTopByDomain(domain),notification,false,domain,getUserName());
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                }, threadPoolExecutor);
                return 2;
            }
        }
        catch(Exception E)
        {

            Date now = new Date();
            String time= timeft.format(now);
            logger.info(time+": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + E.getMessage());
            try {
                notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + E.getMessage()));
            }
            catch(Exception A)
            {
                notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for " + domain + " at price " + bid+". See log for reason"));

            }
        }
        return 0;
    }
    float mainmainsingle(@RequestParam String domain,@RequestParam Long auctionId,@RequestParam String bid,String chat_title,Long tg_id)
    {
        try {
            CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
            //logger.info("no");
            Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
            if(ra.getStatus()!=null)
            {
                logger.info(ra.getStatus());
                if (ra.getStatus().equals("success")) {
                    if (!ra.getAuction_det().getAuction_json().getAuction_ended()) {
                        Date now = new Date();
                        float currbidf = Float.valueOf(ra.getAuction_det().getAuction_json().getAccepted_bid_price());
                        if (currbidf <= Float.valueOf(bid)) {
                            Long timestamp = ra.getAuction_det().getAuction_json().getEnd_time_stamp();
                            Date date = new Date(timestamp);
                            //logger.info("yes1");
                            String pst = ra.getAuction_det().getAuction_json().getEnd_time();
                            if (timestamp - now.getTime() > 300000) {
                                date.setMinutes(date.getMinutes() - 5);
                                ScheduledFuture pre = taskScheduler.schedule(new PreCheck(domain, bid), date);
                                enterTaskMap(domain, pre, "pc");
                            } else {
                                date.setSeconds(date.getSeconds() - 10);
                                ScheduledFuture place = taskScheduler.schedule(new PlaceBid(domain, bid, pst), date);
                                enterTaskMap(domain, place, "pb");
                            }

                            CompletableFuture.runAsync(() ->
                            {
                                String ist = ft1.format(date);
                                String time_left = relTime(date);
                                String bidist = ft1.format(date);
                                String time = timeft.format(now);
                                // Integer GDV = liveRepo.findByAuctionid(ra.getAuction_det().getAuction_json().getAuction_id()).getGdv();
                                telegram.sendAlert(-1001763199668l, 1005l, "Dynadot: Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist);
                                logger.info(time + ": Bid SCHEDULED for " + domain + " at price " + bid + " time " + date);
                                Auction_json aj = ra.getAuction_det().getAuction_json();
                                Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(aj.getAuction_id()));
                                DBdetails dBdetails = null;
                                List<Bid_details> bd = ra.getAuction_details().get(0).getBid_history();

                                if (!op.isPresent()) {
                                    dBdetails = new DBdetails(domain, aj.getAuction_id(), "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), bid, "Bid Scheduled", pst, ist, false);
                                } else {
                                    dBdetails = op.get();
                                    dBdetails.setResult("Bid Scheduled");
                                    dBdetails.setBidAmount(bid);
                                    dBdetails.setBidplacetime(bidist);
                                    dBdetails.setCurrbid(aj.getCurrent_bid_price());
                                    dBdetails.setBidders(aj.getBidders());
                                    dBdetails.setTime_left(time_left);
                                    dBdetails.setEndTimepst(pst);
                                    dBdetails.setEndTimeist(ist);
                                }
                                dBdetails.setScheduled(true);

                                // dBdetails.setGdv(GDV);
                                repo.save(dBdetails);
                                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist));
                                saveAction("Bid Scheduled","Bubble",chat_title,dBdetails,notification,true,domain,tg_id);

                            }, threadPoolExecutor);
                            controller.putESTinDBSingle(cf);
                            return 0;
                        } else {
                            CompletableFuture.runAsync(() ->
                            {
                                String time = timeft.format(now);
                                telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf);
                                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf));
                                saveAction("Bid Scheduled","Bubble",chat_title,repo.findByAuctionId(auctionId),notification,false,domain,tg_id);

                                logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + currbidf);
                            }, threadPoolExecutor);
                            return currbidf;
                        }
                    } else {
                        CompletableFuture.runAsync(() ->
                        {
                            Date now = new Date();
                            String time = timeft.format(now);
                            telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " as auction has ended."));
                            saveAction("Bid Scheduled","Bubble",chat_title,repo.findByAuctionId(auctionId),notification,false,domain,tg_id);
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended.");
                        }, threadPoolExecutor);
                        return 2;
                    }
                } else {
                    //str.add(domain);
                    CompletableFuture.runAsync(() ->
                    {
                        String content = ra.getCont();
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                        try {
                            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
                            saveAction("Bid Scheduled","Bubble",chat_title,repo.findByAuctionId(auctionId),notification,false,domain,tg_id);
                        } catch (Exception e) {
                            logger.info(e.getMessage());
                        }
                    }, threadPoolExecutor);
                    return 1;
                }
            }
            else
            {
                CompletableFuture.runAsync(() ->
                {
                    Date now = new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-930742733l, "Dynadot: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                    saveAction("Bid Scheduled","Bubble",chat_title,repo.findByAuctionId(auctionId),notification,false,domain,tg_id);
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                }, threadPoolExecutor);
                return 2;
            }
        }
        catch(Exception E)
        {

            Date now = new Date();
            String time= timeft.format(now);
            logger.info(time+": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + E.getMessage());
            try {
                notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + E.getMessage()));
            }
            catch(Exception A)
            {
                notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for " + domain + " at price " + bid+". See log for reason"));

            }
        }
        return 0;
    }
    float mainmainsingleoutbid(@RequestParam String domain, @RequestParam Long auctionId,@RequestParam String bid)
    {
        try
        {
            domain=domain.toLowerCase();
            Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
            logger.info(ra.getStatus());
            if (ra.getStatus().equals("success"))
            {
                Date now = new Date();
                float currbidf= Float.valueOf(ra.getAuction_det().getAuction_json().getAccepted_bid_price());
                if(currbidf<=Float.valueOf(bid))
                {
                    Long timestamp = ra.getAuction_det().getAuction_json().getEnd_time_stamp();
                    Date date = new Date(timestamp);
                    String pst = ra.getAuction_det().getAuction_json().getEnd_time();
                    if (timestamp - now.getTime() > 300000) {
                        date.setMinutes(date.getMinutes() - 5);
                        ScheduledFuture pre = taskScheduler.schedule(new PreCheck(domain, bid), date);
                        enterTaskMap(domain, pre, "pc");
                    } else {
                        date.setSeconds(date.getSeconds() - 10);
                        ScheduledFuture place = taskScheduler.schedule(new PlaceBid(domain, bid, pst), date);

                        enterTaskMap(domain, place, "pb");
                    }
                    String finalDomain = domain;
                    CompletableFuture.runAsync(() ->
                    {
                        String bidist = ft1.format(date);
                        String ist = ft1.format(date);
                        String time_left = relTime(date);
                        String time = timeft.format(now);
                        notifRepo.save(new Notification("Dynadot", time, "Bid SCHEDULED for " + finalDomain + " at price " + bid + " at time " + bidist));
                        telegram.sendAlert(-1001763199668l,1005l, "Dynadot: Bid SCHEDULED for " + finalDomain + " at price " + bid + " at time " + bidist);
                        logger.info(time + ": Bid SCHEDULED for " + finalDomain + " at price " + bid + " time " + date);
                        Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Dynadot", auctionId));
                        DBdetails dBdetails = null;
                        List<Bid_details> bd = ra.getAuction_details().get(0).getBid_history();
                        Auction_json aj = ra.getAuction_det().getAuction_json();
                        if (!op.isPresent()) {
                            dBdetails = new DBdetails(finalDomain, aj.getAuction_id(), "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), bid, "Bid Scheduled", pst, ist, false);
                        } else {
                            dBdetails = op.get();
                            dBdetails.setResult("Bid Scheduled");
                            dBdetails.setBidAmount(bid);
                            dBdetails.setBidplacetime(bidist);
                            dBdetails.setCurrbid(aj.getCurrent_bid_price());
                            dBdetails.setBidders(aj.getBidders());
                            dBdetails.setTime_left(time_left);
                            dBdetails.setEndTimepst(pst);
                            dBdetails.setEndTimeist(ist);
                        }
                        dBdetails.setScheduled(true);

                        repo.save(dBdetails);
                    }, threadPoolExecutor);
                    return 0;
                }
                else
                {
                    String finalDomain1 = domain;
                    CompletableFuture.runAsync(()->
                    {
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l,"Dynadot: Bid NOT SCHEDULED for "+ finalDomain1 +" as bid value is lower than accepted bid of "+currbidf);
                        notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for "+ finalDomain1 +" as bid value is lower than accepted bid of "+currbidf));
                        logger.info(time+": Bid NOT SCHEDULED for "+ finalDomain1 +" as bid value is lower than accepted bid of "+currbidf);
                    },threadPoolExecutor);
                    return currbidf;
                }
            } else {
                //str.add(domain);
                String finalDomain2 = domain;
                CompletableFuture.runAsync(()->
                { String content = ra.getCont();
                    telegram.sendAlert(-930742733l,"Dynadot: Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid + " with error: " + content);
                    Date now = new Date();
                String time= timeft.format(now);
                logger.info(time+": Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid + " with error: " + content);
                try {
                    notifRepo.save(new Notification("Dynadot", time, "Bid NOT SCHEDULED for " + finalDomain2 + " at price " + bid + " with error: " + content));
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage());
                }
            },threadPoolExecutor);
                return 1;
            }
        }
        catch(Exception E)
        {

            Date now = new Date();
            String time= timeft.format(now);
            logger.info(time+": Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + E.getMessage());
            try {
                notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + E.getMessage()));
            }
            catch(Exception A)
            {
                notifRepo.save(new Notification("Dynadot",time,"Bid NOT SCHEDULED for " + domain + " at price " + bid+". See log for reason"));

            }
        }
        return 0;
    }

    @GetMapping("/geterror")
Error2 getError(String domain)
{
   return myFeignClient.getAuctionError2(key,"get_auction_details",domain,"usd");
}


   /* @PostMapping("/postDomainsingleinstant")
    boolean mainmainsingleinstant(@RequestBody List<String> ddlist)
    {


            String domain= ddlist.get(0);
            String bid=ddlist.get(1);
            try {
                Response_PlaceBid ra = myFeignClient.placeAuctionBids(key, "place_auction_bid", domain, bid,"usd");
                if (ra.getStatus().equals("success")) {

                    Date date = null;
                    String pst = ra.getAuction_details().getAuction_json().getEnd_time();
                   String pst1=pst.substring(0,17)+"IST";
                    try {
                        date = ft.parse(pst1);
                    } catch (ParseException p) {

                        logger.info("Bid of domain " + domain + " not placed because of date parse exception");
                        notifRepo.save(new Notification("Bid of domain " + domain + " not placed because of date parse exception"));
                        return true;
                    }
                    date.setHours(date.getHours() + 13);
                    date.setMinutes(date.getMinutes() + 30);
                    String ist = ft1.format(date);
                    System.out.println(ist);
                    Date date1= new Date();
                    String bidist = ft1.format(date1);
                    System.out.println(date);

                    notifRepo.save(new Notification("Bid Placed for " + domain + " at price " + bid + " time " + bidist));
                    logger.info("Bid Placed for " + domain + " at price " + bid + " time " + bidist);
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
                    DBdetails dBdetails = null;
                    List<Bid_details> bd = ra.getAuction_details().getBid_history();
                    Auction_json aj = ra.getAuction_details().getAuction_json();
                    if (!op.isPresent()) {
                        dBdetails = new DBdetails(domain, "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), aj.getTime_left(), aj.getAge(), aj.getEstibot_appraisal(), aj.getAuction_type(), bid, "Bid Placed", pst, ist, bidist, true);
                    } else {
                        dBdetails = op.get();
                        dBdetails.setResult("Bid Placed");
                        dBdetails.setIsBidPlaced(true);
                        dBdetails.setBidAmount(bid);
                        dBdetails.setBidplacetime(bidist);
                        dBdetails.setCurrbid(aj.getCurrent_bid_price());
                        dBdetails.setBidders(aj.getBidders());
                        dBdetails.setTime_left(aj.getTime_left());
                        dBdetails.setAge(aj.getAge());
                        dBdetails.setEstibot(aj.getEstibot_appraisal());
                        dBdetails.setAuctiontype(aj.getAuction_type());
                        dBdetails.setEndTimepst(pst);
                        dBdetails.setEndTimeist(ist);
                    }
                    repo.save(dBdetails);
                    if(bd!=null)
                    {for (int j = 0; j < bd.size(); j++) {
                        DB_Bid_Details dbd = new DB_Bid_Details(dBdetails, bd.get(j));
                        dBdetails.getBidhistory().add(dbd);
                        bidhisrepo.save(dbd);
                    }}
                    return false;
                } else {

                    String content = ra.getContent();
                    notifRepo.save(new Notification("Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content));
                    logger.info("Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content);
                    return true;
                }
            }
            catch(Exception E)
            {
                String content= myFeignClient.placeAuctionBidstr(key,"get_auction_details",domain,bid,"usd");
                notifRepo.save(new Notification("Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content));
                logger.info("Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content);
                return true;
            }

    }*/
    @GetMapping("/getforkpool")
    void getForkpool()
    {
        logger.info(""+threadPoolExecutor.getParallelism()+" "+threadPoolExecutor.getActiveThreadCount());
    }

    @PreAuthorize("hasAuthority('APPROLE_Watch')")
    @PostMapping("/fetchdetailsdyna")
    List<DBdetails> fetch1(@RequestBody FetchReq body)
    {

        try {
            List<String> domains = body.getDomains();
            CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList(domains);
            List<DBdetails> list = Collections.synchronizedList(new ArrayList<>());
            List<Long> ids = Collections.synchronizedList(new ArrayList<>());

            Boolean watch = body.getWatch();
            int n = domains.size();
            String domainss = "";

            Set<String> set = new HashSet<>();
            for (int i = 0; i < n; i++) {
                String domain = domains.get(i).toLowerCase();
                set.add(domain);
                domainss = domainss + domain + ",";
            }
            Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domainss, "usd");
            if (ra.getStatus() == null)
            {
                set.addAll(domains);
                //return null;
            }
            else
            {
                logger.info("" + threadPoolExecutor.getParallelism() + " " + " " + taskScheduler.getPoolSize());
                // logger.info("1 "+ForkJoinPool.commonPool().getParallelism());
                List<Auction_details> AuctionDetails = ra.getAuction_details();
                CompletableFuture[] arr = new CompletableFuture[AuctionDetails.size()];
                for (int i = 0; i < AuctionDetails.size(); i++) {

                    int finalI = i;

                    arr[i] = CompletableFuture.runAsync(() -> {
                        Auction_json aj = AuctionDetails.get(finalI).getAuction_json();
                        Long id = aj.getAuction_id();
                        String domain = aj.getDomain().toLowerCase();
                        set.remove(domain);

                        String pst = aj.getEnd_time();
                        Date date = new Date(aj.getEnd_time_stamp());
                        String time_left = relTime(date);
                        String ist = ft1.format(date);
                        String currbid = aj.getCurrent_bid_price();
                        logger.info(domain);
                        Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Dynadot", aj.getAuction_id()));
                        DBdetails db = null;
                        if (!op.isPresent()) {
                            db = new DBdetails(domain, aj.getAuction_id(), "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), "", "", pst, ist, false);
                        } else {
                            db = op.get();
                            db.setCurrbid(currbid);
                            db.setBidders(aj.getBidders());
                            db.setTime_left(time_left);
                            db.setAuctiontype(aj.getAuction_type());
                            db.setEndTimepst(pst);
                            db.setEndTimeist(ist);
                        }
                        if (watch)
                        {db.setWatchlist(true);
                            sendWatchlist("Watchlisted",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),aj.getEstibot_appraisal(), aj.getAuction_id());
                            Date now=new Date();
                            String time = timeft.format(now);
                            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Domain Watchlisted " + domain ));
                            saveAction("Watchlisted","UI",getUser(),db,notification,true,domain,getUserName());}
                        list.add(db);

                        //logger.info(domain);
                        synchronized (this) {
                            repo.save(db);
                        }

                    }, threadPoolExecutor);
                }
                CompletableFuture.allOf(arr).join();

                /*if(watch)
                asyncCalss.getGDVs(ids);*/
                 logger.info(""+threadPoolExecutor.getPoolSize()+" "+taskScheduler.getPoolSize());

            }
            asyncCalss.dynaNotFetchedNotif(set);
            controller.putESTinDB(cf);

       /* logger.info("2 "+ForkJoinPool.commonPool().getPoolSize());
        System.out.println("CPU Core: " + Runtime.getRuntime().availableProcessors());
        System.out.println("CommonPool Parallelism: " + ForkJoinPool.commonPool().getParallelism());
        System.out.println("CommonPool Common Parallelism: " + ForkJoinPool.getCommonPoolParallelism());*/

            return list;
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }
        return null;
    }

    List<DBdetails> fetch1bot( FetchReq body,Long tg_id)
    {

        try {
            List<String> domains = body.getDomains();
            CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList(domains);
            List<DBdetails> list = Collections.synchronizedList(new ArrayList<>());
            List<Long> ids = Collections.synchronizedList(new ArrayList<>());

            Boolean watch = body.getWatch();
            int n = domains.size();
            String domainss = "";

            Set<String> set = new HashSet<>();
            for (int i = 0; i < n; i++) {
                String domain = domains.get(i).toLowerCase();
                set.add(domain);
                domainss = domainss + domain + ",";
            }
            Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domainss, "usd");
            if (ra.getStatus() == null)
            {
                set.addAll(domains);
                //return null;
            }
            else
            {
                logger.info("" + threadPoolExecutor.getParallelism() + " " + " " + taskScheduler.getPoolSize());
                // logger.info("1 "+ForkJoinPool.commonPool().getParallelism());
                List<Auction_details> AuctionDetails = ra.getAuction_details();
                CompletableFuture[] arr = new CompletableFuture[AuctionDetails.size()];
                for (int i = 0; i < AuctionDetails.size(); i++) {

                    int finalI = i;

                    arr[i] = CompletableFuture.runAsync(() -> {
                        Auction_json aj = AuctionDetails.get(finalI).getAuction_json();
                        Long id = aj.getAuction_id();
                        String domain = aj.getDomain().toLowerCase();
                        set.remove(domain);

                        String pst = aj.getEnd_time();
                        Date date = new Date(aj.getEnd_time_stamp());
                        String time_left = relTime(date);
                        String ist = ft1.format(date);
                        String currbid = aj.getCurrent_bid_price();
                        logger.info(domain);
                        Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndAuctionId("Dynadot", aj.getAuction_id()));
                        DBdetails db = null;
                        if (!op.isPresent()) {
                            db = new DBdetails(domain, aj.getAuction_id(), "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), "", "", pst, ist, false);
                        } else {
                            db = op.get();
                            db.setCurrbid(currbid);
                            db.setBidders(aj.getBidders());
                            db.setTime_left(time_left);
                            db.setAuctiontype(aj.getAuction_type());
                            db.setEndTimepst(pst);
                            db.setEndTimeist(ist);
                        }
                        if (watch)
                        {db.setWatchlist(true);
                         sendWatchlist("Watchlisted",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),aj.getEstibot_appraisal(), aj.getAuction_id());
                            Date now=new Date();
                            String time = timeft.format(now);
                            Notification notification=notifRepo.save(new Notification("Dynadot", time, "Domain Watchlisted " + domain ));
                            saveAction("Watchlisted","CPanel",db,notification,true,domain,tg_id);
                        }
                        list.add(db);

                        //logger.info(domain);
                        synchronized (this) {
                            repo.save(db);
                        }

                    }, threadPoolExecutor);
                }
                CompletableFuture.allOf(arr).join();

                /*if(watch)
                asyncCalss.getGDVs(ids);*/
                logger.info(""+threadPoolExecutor.getPoolSize()+" "+taskScheduler.getPoolSize());

            }
            asyncCalss.dynaNotFetchedNotif(set);
            controller.putESTinDB(cf);

       /* logger.info("2 "+ForkJoinPool.commonPool().getPoolSize());
        System.out.println("CPU Core: " + Runtime.getRuntime().availableProcessors());
        System.out.println("CommonPool Parallelism: " + ForkJoinPool.commonPool().getParallelism());
        System.out.println("CommonPool Common Parallelism: " + ForkJoinPool.getCommonPoolParallelism());*/

            return list;
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }
        return null;
    }



List<DBdetails> fetch(@RequestBody FetchReq body)
{
    List<String> domains=body.getDomains();
    Boolean watch= body.getWatch();
    int n= domains.size();
    List<DBdetails> l= new ArrayList<>();
    for(int i=0;i<n;i++) {
        String domain= domains.get(i).toLowerCase();
        try {
            Response_AuctionDetails rn = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
            logger.info(rn.getAuction_det().getAuction_json().getDomain());
            if (rn.getStatus().equals("success"))
            {
                Auction_json aj = rn.getAuction_details().get(0).getAuction_json();
                //List<Bid_details> bd = rn.getAuction_details().get(0).getBid_history();
                String pst=rn.getAuction_det().getAuction_json().getEnd_time();
                Date date = new Date(rn.getAuction_det().getAuction_json().getEnd_time_stamp());
                String time_left= relTime(date);
                String ist = ft1.format(date);
                Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
                DBdetails dBdetails = null;
                if (!op.isPresent()) {
                    dBdetails = new DBdetails(domain, aj.getAuction_id(),"Dynadot", aj.getCurrent_bid_price(), aj.getBidders(),time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), "", "", pst, ist,false);
                } else {
                    dBdetails = op.get();
                    dBdetails.setCurrbid(aj.getCurrent_bid_price());
                    dBdetails.setBidders(aj.getBidders());
                    dBdetails.setTime_left(time_left);
                    dBdetails.setAge(aj.getAge());
                    dBdetails.setEstibot(Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)));
                    dBdetails.setAuctiontype(aj.getAuction_type());
                    dBdetails.setEndTimepst(pst);
                    dBdetails.setEndTimeist(ist);
                }
                dBdetails.setFetched(true);
                if(watch)
                    dBdetails.setWatchlist(true);
                repo.save(dBdetails);
               /*
               if(bd!=null)
                { for (int j = 0; j < bd.size(); j++) {
                    DB_Bid_Details dbd = new DB_Bid_Details(dBdetails, bd.get(j));
                    dBdetails.getBidhistory().add(dbd);
                    bidhisrepo.save(dbd);
                }}
                */
                l.add(dBdetails);
            } else {
                String content = rn.getCont();
                Date now= new Date();
                String time = timeft.format(now);
                notifRepo.save(new Notification("Dynadot",time,"Domain details NOT FETCHED for " + domain + " with error: " + content));
                logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + content);

            }
        }
        catch (Exception E)
        {
            Date now= new Date();
            String time = timeft.format(now);
            //logger.info(time+": "+E.getMessage());
            //String content = myFeignClient.getAuctionDetailstr(key, "get_auction_details", domain, "usd");
            logger.info(time+": Domain Details NOT FETCHED for " + domain + " at price " + " with error: " + E.getMessage());
            try
            {
                notifRepo.save(new Notification("Dynadot",time, "Domain details NOT FETCHED for " + domain + " with error: " + E.getMessage()));
            }
            catch(Exception e)
            {
                notifRepo.save(new Notification("Dynadot",time,"Domain details NOT FETCHED for " + domain + ". See log for error"));
            }
        }
    }
    return l;
}

    @PostMapping("/postdb")
    List<Integer> addDb(@RequestBody List<ArrayList<String>> ddlist)
    {
        List<Integer> list = new ArrayList<>();
        list.add(4);
        list.add(5);
        return list;
    }

    @GetMapping("/getsample")
    Response_PlaceBid getSample(@RequestParam String dname, @RequestParam String bid)
    {
        try {
            Response_PlaceBid ra = myFeignClient.placeAuctionBids(key, "place_auction_bid", dname, bid, "usd");
            //Auction_json aj = (Auction_json)(ra.getAuction_details().get(0));
            //String domain = aj.getDomain();
            //LinkedHashMap aj= (LinkedHashMap) ra.getAuction_details().get(0);
            //aj.get("domain");
            //System.out.println(domain);

            //String domain = ra.getAuction_details().getAuction_json().getDomain();
            String status= ra.getStatus();
            System.out.println(ra);

            return ra;
        }
        catch(Exception E)
        {
            //Error1 er = myFeignClient.getAuctionError(key, "place_auction_bid", dname, bid, "usd");
        logger.info(E.getMessage());
        return null;
        }
    }

    @GetMapping("/sample")
            String sample()
    {
        Date date = new Date();
        SimpleDateFormat ft1 = new SimpleDateFormat("HH:mm:ss");
        logger.info(ft1.format(date));
        return ft1.format(date);
    }

    @GetMapping("/getdetail/{domain}")
    DBdetails getDBdetail(@PathVariable String domain)
    {
        DBdetails db= repo.findByDomain(domain);
        return db;
    }


    @GetMapping("/getdetail")
    List<DBdetails> getDBdetails()
    {
       List<DBdetails> db = repo.findAll();
       return db;
    }

    @GetMapping("/getcurrauctions")
    List<DBdetails> getcurrDBdetails()
    {
        List<DBdetails> db = repo.findByResult("Bid Placed");
        return db;
    }

    @GetMapping("/getcompletedauctions")
    List<DBdetails> getcompDBdetails()
    {

        List<DBdetails> list = repo.findByResultOrResultOrderByEndTimeistDesc("Won","Loss");

        return list;
    }

    //@Scheduled(fixedRate = 5000)
    void yooo()
    {
        logger.info("1");
    }

    //@Scheduled(fixedRate = 5000)
    void yooo1()
    {
        logger.info("2");
    }
    String text1;
    String textob;

    @Scheduled(fixedRate = 120000)
    void refreshddwatchlist()
    {
        List<DBdetails> list = repo.findByPlatformAndWatchlistIsTrue("Dynadot");
       // List<DBdetails> slist = repo.findByPlatformAndResultOrResultOrResultOrResult("Dynadot", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
       // List<DBdetails> slist = repo.findScheduledDD();

        String domains="";
        Map<String,DBdetails> map= new HashMap<>();
        Set<String> set = new HashSet<>();

        Response_AuctionDetails ra;
        if(list!=null&&!list.isEmpty())
        {
            for (int i = 0; i < list.size(); i++) {
                String domain = list.get(i).getDomain();
                map.put(domain, list.get(i));
                set.add(domain);
                domains = domains + domain + ",";
            }
             ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domains, "usd");
            if (ra.getStatus() == null) {
                for (int i = 0; i < list.size(); i++) {
                    DBdetails db = list.get(i);
                    String domain=db.getDomain();
                    Date now= new Date();
                    String time= timeft.format(now);
                    logger.info(time+": Watchlisted or Scheduled Auction not refreshed of domain: "+domain);
                    telegram.sendAlert(-834797664L,"Watchlisted or Scheduled Auction not refreshed of domain: "+domain);

                    /*db.setWatchlist(false);
                    db.setResult("");
                    repo.save(db);*/
                }
            } else {
                List<Auction_details> AuctionDetails = ra.getAuction_details();
                for (int i = 0; i < AuctionDetails.size(); i++) {
                    Auction_json aj = AuctionDetails.get(i).getAuction_json();
                    String domain = aj.getDomain().toLowerCase();
                    set.remove(domain);
                    Date now = new Date();
                    Long id = aj.getAuction_id();
                    DBdetails db = map.get(domain);
                    String pst = aj.getEnd_time();
                    Date date = new Date(aj.getEnd_time_stamp());
                    String time_left = relTime(date);
                    String ist = ft1.format(date);
                    float prevBid = Float.valueOf(db.getCurrbid());
                    String currbid = aj.getCurrent_bid_price();
                    Long age = aj.getAge();
                    int nw = db.getNw();
                    Integer est = Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3));
                    String bid= aj.getAccepted_bid_price();
                    String maxbid= db.getBidAmount();
                    if (!aj.getAuction_ended()) {


                        if(db.getScheduled())
                        {
                            if(Float.valueOf(aj.getCurrent_bid_price())>Float.valueOf(db.getBidAmount())&&!aj.getIs_high_bidder())
                            {
                                if(!db.getResult().equals("Outbid"))
                                {
                                    sendOutbid("Outbid",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),est, aj.getAuction_id());

                                    String time= timeft.format(now);
                                    notifRepo.save(new Notification("Dynadot",time,"Domain: "+domain+" with our max price "+maxbid+" OUTBID at price " + currbid ));
                                    logger.info(time+": Dynadot: Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + currbid );
                                    db.setResult("Outbid");
                                }
                            }
                            else if(b)
                            {
                                Long timestamp = ra.getAuction_det().getAuction_json().getEnd_time_stamp();
                                if (timestamp - now.getTime() > 300000) {
                                    date.setMinutes(date.getMinutes() - 5);
                                    ScheduledFuture pre = taskScheduler.schedule(new PreCheck(domain, db.getBidAmount()), date);
                                    enterTaskMap(domain, pre, "pc");
                                } else {
                                    date.setSeconds(date.getSeconds() - 10);
                                    ScheduledFuture place = taskScheduler.schedule(new PlaceBid(domain, db.getBidAmount(), pst), date);

                                    enterTaskMap(domain, place, "pb");
                                }
                                String time= timeft.format(now);
                                notifRepo.save(new Notification("Dynadot", time, "Bid SCHEDULED for " + domain + " at price " + bid + " at time " + ist));
                                telegram.sendAlert(-1001763199668l,1004l, "Dynadot: Bid SCHEDULED for " + domain + " at price " + bid + " at time " + ist);
                                logger.info(time + ": Bid SCHEDULED for " + domain + " at price " + bid + " time " + date);
                                db.setScheduled(true);

                            }
                            else if(Float.valueOf(aj.getCurrent_bid_price())>0.85*Float.valueOf(db.getBidAmount())&&db.isApproachWarn())
                            {
                                sendOutbid("Approaching Our Bid",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),est, aj.getAuction_id());
                                db.setApproachWarn(false);
                            }
                        }
                        else if(!db.getMute())
                        {
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
                        if (prevBid < Float.valueOf(currbid)&&(!aj.getIs_high_bidder())) {
                            sendWatchlist("NEW BID PLACED",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),est, aj.getAuction_id());

                        }

                        if (date.getTime() - now.getTime() < 86400002 && date.getTime() - now.getTime() > 86280000 && nw >= 4) {
                            sendWatchlist("<24 hrs LEFT",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),est, aj.getAuction_id());

                            nw = 3;
                            db.setNw(nw);
                        } else if (date.getTime() - now.getTime() < 3600002 && date.getTime() - now.getTime() > 3480000 && nw >= 3) {
                            sendWatchlist("<1 hr LEFT",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),est, aj.getAuction_id());

                            nw = 2;
                            db.setNw(nw);
                        } else if (date.getTime() - now.getTime() < 600002 && date.getTime() - now.getTime() > 480000 && nw >= 2) {
                            sendWatchlist("<10 mins LEFT",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),est, aj.getAuction_id());

                            nw = 1;
                            db.setNw(nw);
                        } else if (date.getTime() - now.getTime() < 240002 && date.getTime() - now.getTime() > 120000 && nw >= 1) {
                            sendWatchlist("<4 mins LEFT",time_left,domain,aj.getAccepted_bid_price(),db.getBidAmount(),aj.getAge(),est, aj.getAuction_id());

                            nw = -1;
                            db.setNw(nw);
                        }}


                    } else {
                        String time = timeft.format(now);
                        notifRepo.save(new Notification("Dynadot", time, "Watchlisted auction ended of domain: " + domain + " at price: " + currbid + " at time: " + ist));
                        db.setWatchlist(false);
                        db.setWasWatchlisted(true);
                        if(db.getScheduled())
                        {
                            if (aj.getIs_high_bidder())
                            {
                                telegram.sendAlert(-1001763199668l,842l,"Dynadot: Yippee!! Won auction of "+domain+" at price: "+currbid);
                                notifRepo.save(new Notification("Dynadot",time,"Yippee!! Won auction of "+domain+" at price: "+currbid));
                                logger.info(time+": Won auction of "+domain+" at price: "+currbid);
                                db.setResult("Won");
                            }
                            else
                            {
                                telegram.sendAlert(-1001763199668l,841l,"Dynadot: Hush!! Lost auction of "+domain+" at price: "+currbid);
                                notifRepo.save(new Notification("Dynadot",time,"Hush!! Lost auction of "+domain+" at price: "+currbid));
                                logger.info(time+": Lost auction of "+domain+" at price: "+currbid);
                                db.setResult("Loss");
                            }
                            db.setScheduled(false);
                            deleteTaskMap(domain);

                        }
                    }
                    db.setCurrbid(currbid);
                    db.setBidders(aj.getBidders());
                    db.setTime_left(time_left);
                    db.setEndTimepst(pst);
                    db.setEndTimeist(ist);
                    repo.save(db);
                }
            }
            for(String domain: set)
            {
                /*DBdetails db = map.get(domain);
                db.setResult("");
                repo.save(db);*/
                Date now= new Date();
                String time= timeft.format(now);
                logger.info(time+": Watchlisted or Scheduled Auction not refreshed of domain: "+domain);
                telegram.sendAlert(-834797664L,"Watchlisted or Scheduled Auction not refreshed of domain: "+domain);
              //  notifRepo.save(new Notification("Dynadot",time,"Watchlisted or Scheduled Auction not refreshed of domain: "+domain));
            }
        }
        b=false;
    }

    /*@Scheduled(fixedRate = 120000)
    void refreshddtracker()
    {
        List<DBdetails> list = repo.findByPlatformAndTrackIsTrue("Dynadot");
        if(list.isEmpty())
            return;
        String domains="";
        Map<String,DBdetails> map= new HashMap<>();
        for(int i=0;i<list.size();i++)
        {
            String domain= list.get(i).getDomain();
            map.put(domain,list.get(i));
            domains=domains+domain+",";
        }
        Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domains, "usd");
        if(ra.getStatus()==null)
        {
            for(int i=0;i<list.size();i++)
            {
                DBdetails db= list.get(i);
                db.setWatchlist(false);
                repo.save(db);
            }
        }
        else
        {
            List<Auction_details> AuctionDetails = ra.getAuction_details();
            for (int i = 0; i < AuctionDetails.size(); i++) {
                Auction_json aj = AuctionDetails.get(i).getAuction_json();
                if (!aj.getAuction_ended()) {
                    Date now= new Date();
                    Long id = aj.getAuction_id();
                    String domain = aj.getDomain().toLowerCase();
                    DBdetails db = map.get(domain);
                    String pst = aj.getEnd_time();
                    Date date = new Date(aj.getEnd_time_stamp());
                    String time_left = relTime(date);
                    String ist = ft1.format(date);
                    String currbid = aj.getCurrent_bid_price();
                    Long age = aj.getAge();

                    Integer est = Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3));

                    String text = "Dynadot Live Track" +filler+"\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\n \nAge: " + age + " \nEST: " + est + " \nGDV: " + db.getGdv() + " \n\nLink: " + "https://www.dynadot.com/market/auction/" + domain;
                    //-1001814695777L
                    List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                    List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid 50", "b" + " dd " + aj.getAuction_id() + " " + domain + " " + currbid+" 50"));
                    row.add(new InlineKeyboardButton("Bid", "b" + " dd " + aj.getAuction_id() + " " + domain + " " + currbid));
                    row1.add(new InlineKeyboardButton("Remove", "rw dd " + aj.getAuction_id() + " " + domain));
                    row1.add(new InlineKeyboardButton("Refresh", "r dd " + aj.getAuction_id() + " " + domain));
                    InlineKeyboardButton link= new InlineKeyboardButton("Link");
                    link.setUrl("https://www.dynadot.com/market/auction/" + domain);
                    row1.add(link);

                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    rows.add(row);rows.add(row1);
                    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                    Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                            , text, inlineKeyboardMarkup));

                    db.setCurrbid(currbid);
                    db.setBidders(aj.getBidders());
                    db.setTime_left(time_left);
                    db.setAge(age);
                    db.setEstibot(est);
                    db.setAuctiontype(aj.getAuction_type());
                    db.setEndTimepst(pst);
                    db.setEndTimeist(ist);
                    repo.save(db);
                } else
                {

                    Date now= new Date();
                    String time=timeft.format(now);
                    Long id = aj.getAuction_id();
                    String domain = aj.getDomain().toLowerCase();
                    DBdetails db = map.get(domain);
                    String pst = aj.getEnd_time();
                    Date date = new Date(aj.getEnd_time_stamp());
                    String time_left = relTime(date);
                    String ist = ft1.format(date);
                    String currbid = aj.getCurrent_bid_price();
                    notifRepo.save(new Notification("Dynadot",time,"Tracked auction ended of domain: "+domain+" at price: "+currbid+" at time: "+ist));

                    db.setCurrbid(currbid);
                    db.setBidders(aj.getBidders());
                    db.setTime_left(time_left);
                    db.setAuctiontype(aj.getAuction_type());
                    db.setEndTimepst(pst);
                    db.setEndTimeist(ist);
                    db.setWatchlist(false);
                    db.setTrack(false);
                    db.setWasWatchlisted(true);
                    repo.save(db);
                }

            }
        }
    }*/

    InlineKeyboardMarkup refreshMarkup(InlineKeyboardMarkup markup,String currbid)
    {
        for(int i=0;i<markup.getInline_keyboard().size();i++)
        {
            List<InlineKeyboardButton> list= markup.getInline_keyboard().get(i);
            for(int j=0;j<list.size();i++)
            {
                InlineKeyboardButton button= list.get(j);
                String data= button.getCallback_data();
                String[] arr = data.split(" ");
                if(arr.length>3) {
                    arr[4] = currbid;
                    data = "";
                    for (int k = 0; k < arr.length; k++) {
                        data = arr[i] + " ";
                    }
                    button.setCallback_data(data);
                }
            }
        }
        return markup;
    }

    void refreshBot(String domain, Long id, Long chat_id, Long message_thread_id, InlineKeyboardMarkup markup)
    {
        Response_AuctionDetails rn = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");

        if (rn.getStatus().equals("success")&&rn!=null)
        {
            Optional<DBdetails> op= Optional.ofNullable(repo.findByPlatformAndAuctionId("Dynadot",id));
            Auction_json aj = rn.getAuction_details().get(0).getAuction_json();
            DBdetails dBdetails=null;
            boolean b=op.isPresent();
            if(b)
                dBdetails=op.get();
            String text = "";
            if (!aj.getAuction_ended()) {
                String pst = rn.getAuction_det().getAuction_json().getEnd_time();
                Date date = new Date(rn.getAuction_det().getAuction_json().getEnd_time_stamp());
                String time_left = relTime(date);
                String ist = ft1.format(date);
                String currbid = aj.getCurrent_bid_price();

                Long age = aj.getAge();
                String est = aj.getEstibot_appraisal();
                String bid= aj.getAccepted_bid_price();
                markup.getInline_keyboard().get(0).get(0).setCallback_data("b dd "+id+" "+domain+" "+currbid+" 50");
                markup.getInline_keyboard().get(0).get(0).setCallback_data("b dd "+id+" "+domain+" "+currbid);
                if (b&&dBdetails.getScheduled()) {
                    String maxbid= dBdetails.getBidAmount();

                    if(Float.valueOf(aj.getCurrent_bid_price())>Float.valueOf(dBdetails.getBidAmount())&&!aj.getIs_high_bidder())
                    {
                        text = "Updated\uD83D\uDFE2\n\nDynadot Auction LOSING/OUTBID!"+filler+" \n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nMin Next Bid: " + bid + "\nOur Max Bid: " + maxbid + "\n \nAge: " + age + " \nEST: " + est;

                    }
                    else
                    {
                        text = "Updated\uD83D\uDFE2\n\nDynadot Auction WINNING"+filler+"\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nMin Next Bid: " + bid + "\nOur Max Bid: " + maxbid + "\n \nAge: " + age + " \nEST: " + est;

                    }
                }
                else {
                    text = "Updated\uD83D\uDFE2\n\nDynadot" +filler+"\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\n \nAge: " + age + " \nEST: " + est;
                }


               // EditMessage editMessage= new EditMessage(text,chat_id,message_id,markup);
               // telegram.editMessageText(editMessage);
                SendMessage sendMessage= new SendMessage(chat_id,message_thread_id,text,refreshMarkup(markup,currbid));
                telegram.sendKeyboard(sendMessage);

            }
            else
            {
                if(b)
                {if(dBdetails.getResult().equals("Won"))
                    text = "Updated\uD83D\uDFE2\n\nDynadot " +filler+"\n"+ domain + "\n\nWON!! "  + "\nLast Bid: " +  aj.getCurrent_bid_price() + "\nOur Max Bid: "+dBdetails.getBidAmount();
                else if(dBdetails.getResult().equals("Loss"))
                    text = "Updated\uD83D\uDFE2\n\nDynadot " +filler+"\n" + domain + "\n\nLOST!! "  + "\nLast Bid: " +  aj.getCurrent_bid_price() + "\nOur Max Bid: "+dBdetails.getBidAmount();
                else if(dBdetails.getScheduled())
                {
                    if(aj.getIs_high_bidder())
                        text = "Updated\uD83D\uDFE2\n\nDynadot" +filler+"\n" + domain + "\n\nWON!! "  + "\nLast Bid: " +  aj.getCurrent_bid_price() + "\nOur Max Bid: "+dBdetails.getBidAmount();
                    else
                        text = "Updated\uD83D\uDFE2\n\nDynadot" +filler+"\n" + domain + "\n\nLOST!! "  + "\nLast Bid: " +  aj.getCurrent_bid_price() + "\nOur Max Bid: "+dBdetails.getBidAmount();

                }}
                else
                {
                    text = "Updated\uD83D\uDFE2\n\nDynadot"+filler+ "\n" + domain + "\n\nAuction Ended!! "  + "\nLast Bid: " + aj.getCurrent_bid_price();

                }
               /* EditMessage editMessage= new EditMessage(text,chat_id,message_id);
                telegram.editMessageText(editMessage);*/
                telegram.sendAlert(chat_id,message_thread_id,text);
            }

        } else {
            String content = rn.getCont();
            Date now= new Date();
            String time = timeft.format(now);
            logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + content);
            String text = "Updated\uD83D\uDFE2\n\nDynadot" +filler+"\n Auction couldn't refreshed with error: "+rn.getCont();
           /* EditMessage editMessage= new EditMessage(text,chat_id,message_id,markup);
            telegram.editMessageText(editMessage);*/

            telegram.sendAlert(chat_id,message_thread_id,text);
        }
    }

    void watchListLive(String domain, Long id, Boolean track, String chat_title, Long tg_id)
    {
        try {
            CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
            Response_AuctionDetails rn = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");

            if (rn.getStatus().equals("success"))
            {
                Auction_json aj = rn.getAuction_details().get(0).getAuction_json();
                //List<Bid_details> bd = rn.getAuction_details().get(0).getBid_history();
                String pst=rn.getAuction_det().getAuction_json().getEnd_time();
                Date date = new Date(rn.getAuction_det().getAuction_json().getEnd_time_stamp());
                String time_left= relTime(date);
                String ist = ft1.format(date);
                String currbid = aj.getCurrent_bid_price();
                Long age = aj.getAge();
                String est = aj.getEstibot_appraisal();
               // Integer gdv = 0;
                Optional<DBdetails> op= Optional.ofNullable(repo.findByPlatformAndAuctionId("Dynadot",id));
                /*if(op.isPresent())
                {
                    gdv= op.get().getGdv();
                    if(gdv==null||gdv==0)
                    {
                        Optional<LiveDetails> ad= Optional.ofNullable(liveRepo.findByAuctionid(aj.getAuction_id()));
                        if(ad.isPresent())
                        {
                            gdv=ad.get().getGdv();
                        }
                    }
                }
                else
                    gdv= liveRepo.findByAuctionid(aj.getAuction_id()).getGdv();*/

                sendWatchlist("Watchlisted",time_left,domain,aj.getAccepted_bid_price(),"",aj.getAge(),est, aj.getAuction_id());

                DBdetails dBdetails = null;
                if (!op.isPresent())
                {
                    dBdetails = new DBdetails(domain, aj.getAuction_id(),"Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)), aj.getAuction_type(), "", "", pst, ist, "", false);
                }
                else
                {
                    dBdetails = op.get();
                    dBdetails.setCurrbid(aj.getCurrent_bid_price());
                    dBdetails.setBidders(aj.getBidders());
                    dBdetails.setTime_left(time_left);
                    dBdetails.setAge(aj.getAge());
                    dBdetails.setEstibot(Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)));
                    dBdetails.setAuctiontype(aj.getAuction_type());
                    dBdetails.setEndTimepst(pst);
                    dBdetails.setEndTimeist(ist);
                }
               // dBdetails.setGdv(gdv);
                dBdetails.setWatchlist(true);
                if(track)
                dBdetails.setTrack(true);
                repo.save(dBdetails);
                Date now=new Date();
                String time = timeft.format(now);
                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Domain Watchlisted " + domain ));
                saveAction("Watchlisted","Bubble",chat_title,dBdetails,notification,true,domain,tg_id);

                controller.putESTinDBSingle(cf);
            } else {
                String content = rn.getCont();
                Date now= new Date();
                String time = timeft.format(now);
                notifRepo.save(new Notification("Dynadot",time,"Domain details NOT FETCHED for " + domain + " with error: " + content));
                logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + content);

            }
        }
        catch (Exception E)
        {
            Date now= new Date();
            String time = timeft.format(now);

            logger.info(time+": Domain Details NOT FETCHED for " + domain + " at price " + " with error: " + E.getMessage());
            try
            {
                notifRepo.save(new Notification("Dynadot",time, "Domain details NOT FETCHED for " + domain + " with error: " + E.getMessage()));
            }
            catch(Exception e)
            {
                notifRepo.save(new Notification("Dynadot",time,"Domain details NOT FETCHED for " + domain + ". See log for error"));
            }
        }
    }

    void instantUpdateWatchlist(DBdetails dBdetails)
    {
        String domain= dBdetails.getDomain();
        try {
            Response_AuctionDetails rn = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");

            if (rn.getStatus().equals("success"))
            {
                Auction_json aj = rn.getAuction_details().get(0).getAuction_json();
                //List<Bid_details> bd = rn.getAuction_details().get(0).getBid_history();
                String pst=rn.getAuction_det().getAuction_json().getEnd_time();
                Date date = new Date(rn.getAuction_det().getAuction_json().getEnd_time_stamp());
                String time_left= relTime(date);
                String ist = ft1.format(date);
                String currbid = aj.getCurrent_bid_price();
                Long age = aj.getAge();
                String est = aj.getEstibot_appraisal();

                sendWatchlist("Watchlist",time_left,domain,aj.getAccepted_bid_price(),dBdetails.getBidAmount(),aj.getAge(),est, aj.getAuction_id());

                dBdetails.setCurrbid(aj.getCurrent_bid_price());
                    dBdetails.setBidders(aj.getBidders());
                    dBdetails.setTime_left(time_left);
                    dBdetails.setAge(aj.getAge());
                    dBdetails.setEstibot(Integer.valueOf(aj.getEstibot_appraisal().substring(1, aj.getEstibot_appraisal().length() - 3)));
                    dBdetails.setAuctiontype(aj.getAuction_type());
                    dBdetails.setEndTimepst(pst);
                    dBdetails.setEndTimeist(ist);

                dBdetails.setWatchlist(true);
               /* if(track)
                    dBdetails.setTrack(true);*/
                repo.save(dBdetails);
                Date now=new Date();
                String time = timeft.format(now);
                Notification notification=notifRepo.save(new Notification("Dynadot", time, "Domain Watchlisted " + domain ));
                saveAction("Watchlisted","UI",getUser(),dBdetails,notification,true,domain,getUserName());

        } else {
                String content = rn.getCont();
                Date now= new Date();
                String time = timeft.format(now);
                notifRepo.save(new Notification("Dynadot",time,"Domain details NOT FETCHED for " + domain + " with error: " + content));
                logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + content);

            }
        }
        catch (Exception E)
        {
            Date now= new Date();
            String time = timeft.format(now);

            logger.info(time+": Domain Details NOT FETCHED for " + domain + " at price " + " with error: " + E.getMessage());
            try
            {
                notifRepo.save(new Notification("Dynadot",time, "Domain details NOT FETCHED for " + domain + " with error: " + E.getMessage()));
            }
            catch(Exception e)
            {
                notifRepo.save(new Notification("Dynadot",time,"Domain details NOT FETCHED for " + domain + ". See log for error"));
            }
        }
    }

    @GetMapping("/getwatchlist")
    List<DBdetails> getWatchlist()
    {
        return repo.findByWatchlistTrueOrderByEndTimeist();
    }

    @PutMapping("/watchlisted/{domain}")
    Boolean watchlisted(@PathVariable String domain)
    {
        DBdetails db= repo.findByDomain(domain);
        db.setWatchlist(true);
        return db.isWatchlist();
    }

    @GetMapping("/watchlistsingle")
    void watchlisted(@RequestParam Long id)
    {
        DBdetails db= repo.findById(id).get();
        db.setWatchlist(true);
        repo.save(db);
       /* String plat= db.getPlatform();
        if(plat.equals("Dynadot"))
            instantUpdateWatchlist(db);
        else if(plat.equals("Dropcatch"))
            dropCatchController.instantUpdateWatchlist(db);
        else if(plat.equals("GoDaddy"))
            goDaddyController.instantUpdateWatchlist(db);
        else if(plat.equals("Namecheap"))
            namecheapController.instantUpdateWatchlist(db);*/
    }

    @GetMapping("/removewatchlistsingle")
    void removewatchlisted(@RequestParam Long id)
    {
        DBdetails db= repo.findById(id).get();
        db.setWatchlist(false);
        repo.save(db);
    }
    @PutMapping("/watchlisted")
    Boolean watchlisting(@RequestBody List<List<Long>> all)
    {
        List<Long> ids = all.get(0);
        List<Long> nids=all.get(1);

        //asyncCalss.getGDVs(ids);
        for(int i=0;i<ids.size();i++)
        {
            DBdetails db= repo.findById(ids.get(i)).get();
            String plat= db.getPlatform();
            if(plat.equals("Dynadot"))
            instantUpdateWatchlist(db);
            else if(plat.equals("Dropcatch"))
                dropCatchController.instantUpdateWatchlist(db);
            else if(plat.equals("GoDaddy"))
                goDaddyController.instantUpdateWatchlist(db);
            else if(plat.equals("Namecheap"))
                namecheapController.instantUpdateWatchlist(db);
                //db.setWatchlist(true);
            logger.info(db.getDomain());
            repo.save(db);
        }
        for(int i=0;i<nids.size();i++)
        {
            DBdetails db= repo.findById(nids.get(i)).get();
            db.setWatchlist(false);
            logger.info(db.getDomain());
            repo.save(db);
        }
        return true;
    }

    @PutMapping("/removewatchlist")
    void removeWatchlist(@RequestBody List<Long> ids)
    {

        for(int i=0;i<ids.size();i++)
        {
            DBdetails db= repo.findById(ids.get(i)).get();
            db.setWatchlist(false);
            repo.save(db);
        }
    }

    @GetMapping("/getnotifications")
    List<Notification> getnotif()
    {
        List<Notification> db = notifRepo.findTop100ByOrderByIDDesc();
        logger.info("notif");
        return db;
    }

    @GetMapping("/getnotificationstoday")
    List<Notification> getnotiftoday()
    {
        Date date= new Date();
        List<Notification> db = notifRepo.findAllByDateOrderByIDDesc(date);
        return db;
    }

    @GetMapping("/getsample1")
    LinkedHashMap getSample1(@RequestParam String dname)
    {
       /* ObjectMapper obj = new ObjectMapper();
        try{
ErrorResponse er= obj.readValue( myFeignClient.getAuctionDetail(key, "get_auction_details", dname,"usd"),ErrorResponse.class);
        return er;}
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;*/

        LinkedHashMap mp = myFeignClient.getAuctionDetail(key, "get_auction_details", dname,"usd");
        System.out.println(mp.getClass());
        System.out.println(mp);
        System.out.println(mp.get("status"));
        System.out.println(mp.get("status").getClass());
        System.out.println(mp.get("auction_details"));
        ArrayList al=(ArrayList)mp.get("auction_details");
        System.out.println(mp.get("auction_details").getClass());
        System.out.println(al.get(0).getClass());
        return mp;
    }



    @GetMapping("/get1")
    void get()
    {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm z");
        Date d;
        String s="2022/10/28 11:10 PST";
        s=s.substring(0,17)+"IST";
        try{
            System.out.println(s);
            d=ft.parse(s);
            System.out.println(d);
            d.setHours(d.getHours()+12);
            d.setMinutes(d.getMinutes()+30);
            System.out.println(d);
        }

        catch(ParseException p)
        {System.out.println();}

       // long l1= 1669084598541;
        long l= System.currentTimeMillis();

    }

    @GetMapping("/geto")
    ResponseLive geto()
    {
        //Domaindetails d= new Domaindetails("domain.com",78);
      // ResponseLive rl = myFeignClient.getLiveDetails(key,"get_open_auctions","usd");
//long l = rl.getAuction_list().get(0).getEnd_time_stamp();
//long l1=1669084598541;
//System.out.println(l);
String s= "2022/11/26 11:00 IST";
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm z");
        Date d= null;
        try{
            d= ft.parse(s);
            System.out.println(d);
            System.out.println(d.getTime());
            d.setHours(d.getHours()+13);
            d.setMinutes(d.getMinutes()+30);
            System.out.println(d);
        }
        catch(ParseException p)
        {
            logger.info(p.getMessage());
        }

long l1= d.getTime();
//long diff= l-l1;
        System.out.println(l1);
       return null;
    }


    @GetMapping("/cancel/dd")
    @PreAuthorize("hasAuthority('APPROLE_Bid_DD')")
    void cancelBidWeb(@RequestParam String domain,@RequestParam Long auctionId)
    {
        deleteTaskMap(domain);
      DBdetails db= repo.findByPlatformAndAuctionId("Dynadot",auctionId);
      db.setResult("Bid Cancelled");
      db.setScheduled(false);
      repo.save(db);
        Date now=new Date();
        String time = timeft.format(now);
        Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bidding Cancelled of " + domain ));
        saveAction("Bid Cancelled","UI List",getUser(),db,notification,true,domain,getUserName());

    }
    void cancelBidAction(DBdetails db,Long tg_id)
    {
        Date now=new Date();
        String time = timeft.format(now);
        Notification notification=notifRepo.save(new Notification(db.getPlatform(), time, "Bidding Cancelled of " + db.getDomain() ));
        saveAction("Bid Cancelled","CPanel",db,notification,true,db.getDomain(),tg_id);

    }

    @GetMapping("/returnalllive")
    List<LiveDetails> allLive()
    {
        return liveRepo.findAll();
    }

    @GetMapping("/waslive")
    List<WasLive> wasLive()
    {
        return wasLiveRepo.findByOrderByIdDesc();
    }

    @GetMapping("/detectlive")
    List<LiveDetails> detectLive()
    {
        return liveRepo.findAllByOrderByESTDesc(); //liveRepo.findByLiveTrueOrderByIdDesc();
    }

    @GetMapping("/detectliveupdated")
    List<LiveDetails> detectLiveUpdated()
    {
        refreshDDLive();
        return liveRepo.findAllByOrderByESTDesc(); //liveRepo.findByLiveTrueOrderByIdDesc();
    }


    @GetMapping("/set")
    LiveMap setWrap()
    {
       LiveMap lm = new LiveMap();
       Map<String,Long> map= lm.getMap();
       map.put("yash",234L);
       liveMaprepo.save(lm);
       return liveMaprepo.getReferenceById(1);
    }

    @GetMapping("/getlivemap")
    LiveMap getLiveMap()
    {
        Optional<LiveMap> o = liveMaprepo.findById(1);
        if(o.isPresent())
            return o.get();
        else {logger.info("no"); return null;}
    }

    @GetMapping("livemapclear")
    Map clear()
    {
       LiveMap lm = liveMaprepo.getById(1);
        Map<String,Long> map= lm.getMap();
        logger.info("y");
        map.clear();
        logger.info("n");
        liveMaprepo.save(lm);
        logger.info("n1");
        return map;

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
//logger.info(s);
        return s;
    }

    String relTime(Long t2)
    {
       Long t1= System.currentTimeMillis();
        long diff = t2 - t1;
        String s="";
        long min = TimeUnit.MILLISECONDS.toMinutes(diff)%60;

            s=formdigit(min)+"m"+s;

        long h = TimeUnit.MILLISECONDS.toHours(diff)%24;
            s=formdigit(h)+"h "+s;

        long d = TimeUnit.MILLISECONDS.toDays(diff)%365;
            s=formdigit(d)+"d "+s;

       // logger.info(s);
        return s;
    }

    String relTimelive(Long t2)
    {
        Long t1= System.currentTimeMillis();
        long diff = t2 - t1;
        String s="";
        long min = TimeUnit.MILLISECONDS.toMinutes(diff)%60;

        s=formdigit(min)+"m"+s;

        long h = TimeUnit.MILLISECONDS.toHours(diff)%24;
        s=formdigit(h)+"h "+s;

        //logger.info(s);
        return s;
    }
    //@Bean
    PlaceBid placeBid(String domain, String bid, String key)
    {
       return new PlaceBid(domain,bid,key);
    }

    @Async
    CompletableFuture<Boolean> refreshScheduled() {
        List<DBdetails> list = repo.findByPlatformAndResultOrResultOrResultOrResult("Dynadot", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
        if (list.isEmpty())
            return CompletableFuture.completedFuture(true);
        String domains = "";
        Map<String, DBdetails> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            String domain = list.get(i).getDomain();
            map.put(domain, list.get(i));
            set.add(domain);
            domains = domains + domain + ",";
        }
        Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domains, "usd");
        if(ra.getStatus()==null)
        {
            for(int i=0;i<list.size();i++)
            {
                DBdetails db= list.get(i);
                db.setResult("");
                repo.save(db);
            }
        }
        else
        {
            List<Auction_details> AuctionDetails = ra.getAuction_details();
            for (int i = 0; i < AuctionDetails.size(); i++) {

                Auction_json aj = AuctionDetails.get(i).getAuction_json();
                Long id = aj.getAuction_id();
                String domain = aj.getDomain().toLowerCase();
                set.remove(domain);
                if (!aj.getAuction_ended()) {

                    DBdetails db = map.get(domain);
                    String pst = aj.getEnd_time();
                    Date date = new Date(aj.getEnd_time_stamp());
                    String time_left = relTime(date);
                    String ist = ft1.format(date);
                    String currbid = aj.getCurrent_bid_price();

                    if(Float.valueOf(aj.getAccepted_bid_price())>Float.valueOf(db.getBidAmount()))
                        db.setResult("Outbid");

                    db.setCurrbid(currbid);
                    db.setBidders(aj.getBidders());
                    db.setTime_left(time_left);
                    db.setAuctiontype(aj.getAuction_type());
                    db.setEndTimepst(pst);
                    db.setEndTimeist(ist);
                    repo.save(db);
                } else
                {

                    Date now= new Date();
                    DBdetails db = map.get(domain);
                    String pst = aj.getEnd_time();
                    Date date = new Date(aj.getEnd_time_stamp());
                    String time_left = relTime(date);
                    String ist = ft1.format(date);
                    String currbid = aj.getCurrent_bid_price();

                    db.setCurrbid(currbid);
                    db.setBidders(aj.getBidders());
                    db.setTime_left(time_left);
                    db.setAuctiontype(aj.getAuction_type());
                    db.setEndTimepst(pst);
                    db.setEndTimeist(ist);
                   if(aj.getIs_high_bidder())
                       db.setResult("Won");
                   else
                       db.setResult("Loss");
                    repo.save(db);
                }

            }
    }
        for(String domain: set)
        {
            DBdetails db = map.get(domain);
            db.setResult("");
            repo.save(db);
            Date now= new Date();
            String time= timeft.format(now);
            logger.info(time+": Scheduled Auction not refreshed of domain: "+domain);
            notifRepo.save(new Notification("Dynadot",time,"Scheduled Auction not refreshed of domain: "+domain));
        }

       return CompletableFuture.completedFuture(true);
    }

   /*List<DBdetails> getScheduled()
    {List<DBdetails> list = repo.findByResult("Bid Scheduled");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        int n=list.size(); Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");


        for(int i=0;i<n;i++)
        {
            DBdetails db= list.get(i);
            String domain= db.getDomain();
            try {
                if (db.getPlatform().equals("Dynadot")) {


                    Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
                    Auction_json aj = ra.getAuction_det().getAuction_json();
                    db.setCurrbid(aj.getCurrent_bid_price());
                    db.setBidders(aj.getBidders());
                    db.setEndTimepst(aj.getEnd_time());
                    db.setTime_left(relTime(aj.getEnd_time_stamp()));
                } else if (db.getPlatform().equals("Dropcatch")) {
                    Long auctionId = db.getAuctionId();
                    AuctionDetailDC ad = dropCatchFeign.getAuctionDetail("", auctionId.intValue()).getBody();
                    String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                    Date date = new Date();
                    String endTimeist = "";
                    String time_left = "";
                    try {
                        date = parser.parse(endTime);
                        date.setHours(date.getHours() + 5);
                        date.setMinutes(date.getMinutes() + 30);
                        endTimeist = ft1.format(date);
                        time_left = relTime(date);
                        // System.out.println(date);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    db.setCurrbid(String.valueOf(ad.getHighBid()));
                    db.setBidders(ad.getNumberOfBidders());
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                }
                repo.save(db);
            }
            catch(Exception e)
            { db.setResult("Not Placed");
                repo.save(db);
               // notifRepo.save(new Notification(db.getPlatform()+": Scheduled bid not placed for domain: "+domain));
                logger.info("Scheduled Bid List rendering exception: "+db.getPlatform()+" " +domain+" "+e.getMessage());}
        }
        goDaddyController.refreshscheduledbids();
       return repo.findByResult("Bid Scheduled");
    }
*/
    /*@GetMapping("/getplacedbids")
    List<DBdetails> getPlaced()
    {
        logger.info("Returning placed bids, refreshing");
        List<DBdetails> list= repo.findByResult("Bid Placed");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd");
        int n=list.size(); Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");

        for(int i=0;i<n;i++)
        {
            DBdetails db= list.get(i);
            String domain= db.getDomain();
            try {
                if (db.getPlatform().equals("Dynadot")) {

                    try {
                        Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
                        Auction_json aj = ra.getAuction_det().getAuction_json();
                        if (aj.getAuction_ended()) {
                            if (aj.getIs_high_bidder())
                                db.setResult("Won");
                            else
                                db.setResult("Loss");
                            db.setCurrbid(aj.getCurrent_bid_price());
                            db.setBidders(aj.getBidders());
                            db.setEndTimepst(aj.getEnd_time());
                            db.setTime_left("0m");
                        } else {
                            db.setCurrbid(aj.getCurrent_bid_price());
                            db.setBidders(aj.getBidders());
                            db.setEndTimepst(aj.getEnd_time());
                            db.setTime_left(relTime(aj.getEnd_time_stamp()));
                        }
                    }
                    catch(Exception e)
                    {
                        logger.info("Placed Bid List rendering exception: "+db.getPlatform()+" " +domain+" "+e.getMessage());}
                    Date d= new Date();
                    d.setDate(d.getDate()+1);
                    String end= ft2.format(d);
                    d.setDate(d.getDate()-90);
                    String start= ft2.format(d);
                    ResponseClosedAuction r= myFeignClient.getClosedAuctionDetails(key,"get_closed_auctions",start,end,"usd");
                    List<ClosedAuctionsDets> list1 = r.getGetClosedAuctionsResponse().getAuctions();
                    boolean b=false;
                    for(int j=0;j< list1.size();j++)
                    {
                        String domain1= list1.get(j).getDomain();
                        if(domain.equals(domain1.toLowerCase()));
                        {b=true;
                            if(list1.get(i).getAuctionWonStatus().equals("won"))
                            {
                                db.setResult("Won");
                                db.setCurrbid(list1.get(i).getBidPrice());
                            }
break;
                        }
                    }
                    if(!b)
                    {
                        db.setResult("Not Placed");
                    }

                }


                else if (db.getPlatform().equals("Dropcatch")) {

                    Long auctionId = db.getAuctionId();
                    AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                    if(!(ad==null))
                    {
                    String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                    Date date = new Date();
                    String endTimeist = "";
                    String time_left = "";
                    try {
                        date = parser.parse(endTime);
                        date.setHours(date.getHours() + 5);
                        date.setMinutes(date.getMinutes() + 30);
                        endTimeist = ft1.format(date);
                        time_left = relTime(date);
                        // System.out.println(date);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    db.setCurrbid(String.valueOf(ad.getHighBid()));
                    db.setBidders(ad.getNumberOfBidders());
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setTime_left(time_left);
                }
                    else
                    {
                        AuctionResultdc ar= dropCatchFeign.getAuctionResult(bearer,domain,10).getBody().getItems().get(0);
                        if(domain.equals(ar.getDomain().toLowerCase()))
                        {logger.info("While Placed Bid Table Rendering Checking Result for dropcatch domain: "+domain);
                            if(ar.getResult().equals("AuctionWon"))
                                db.setResult("Won");
                            else db.setResult("Loss");
                        }
                        else
                        {
                            db.setResult("Not Placed");
                        }
                    }
                }
                else {
                    db.setResult("Not Placed");
                }
                repo.save(db);
            }
            catch(Exception e)
            { db.setResult("Not Placed");
                repo.save(db);
                // notifRepo.save(new Notification(db.getPlatform()+": Scheduled bid not placed for domain: "+domain));
                logger.info("Scheduled Bid List rendering exception: "+db.getPlatform()+" " +domain+" "+e.getMessage());}
        }
        logger.info("returned all placed bids");
        return repo.findByResult("Bid Placed");
    }*/

    @GetMapping("/closedauction")
    ResponseClosedAuction closedAuction()
    {
        SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd");
        Date d= new Date();
        String d1= ft2.format(d);
        logger.info(d1);
        d.setDate(d.getDate()+1);
        String end= ft2.format(d);
        logger.info(end);
        d.setDate(d.getDate()-30);
        String start= ft2.format(d);
        logger.info(start);

        logger.info( myFeignClient.getClosedAuctionDetailstr(key,"get_closed_auctions",start,end,"usd"));
        ResponseClosedAuction r= myFeignClient.getClosedAuctionDetails(key,"get_closed_auctions",start,end,"usd");
        //logger.info(r.getGetClosedAuctionsResponse().getStatus());
        ResponseClosedAuction r1= new ResponseClosedAuction();

        return r;
    }

    @GetMapping("/db")
        DBdetails db()
        {
            return new DBdetails();
        }

    @GetMapping("/start")
    void start1()
    {
        Date date = new Date();
        date.setHours(date.getHours()+24);
        //scheduledFuture = taskScheduler.scheduleWithFixedDelay(new DetectLive(date.getTime()),2000);
    }

    @GetMapping("/stop")
    void stop()
    {
        //scheduledFuture.cancel(true);
    }
    @GetMapping("/telegram")
    void telegram1()
    {
       String domain ="eiew.com";
        String time_left= "0h, 29m";
        String currbid = "14.99";
        int age= 1;
        String est="$1300.00";
        String text= "Dynadot Live Detect \n \n"+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+currbid+"\n \nAge: "+age+" \nEST: "+est+" \n\nLink: "+"https://www.dynadot.com/market/auction/"+domain;

        Object obj= telegram.sendAlert(-834797664L,text);
        logger.info("yes");
    }

    boolean isHighlight(String domain, Integer EST, LiveFilterSettings settings)
    {
        domain=domain.toLowerCase();
        String[] dom=domain.split("\\.",2);
        String sld=dom[0];String tld= dom[1];
        int l= domain.length();
        if(settings.getNoHyphens()&&domain.contains("-"))
            return false;
        if(settings.getNoNumbers()&&domain.matches(".*\\d.*"))
            return false;
        if(l<settings.getLowLength()||l>settings.getUpLength())
            return false;
        Map<String,Integer> extest=settings.getExtnEst();
        if(settings.getRestrictedExtns().contains(tld))
            return false;
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
    boolean isHighlight(String domain, Integer EST, LiveFilterSettings settings,String[] dom)
    {
        domain=domain.toLowerCase();
        String sld=dom[0];String tld= dom[1];
        int l= domain.length();
        if(settings.getNoHyphens()&&domain.contains("-"))
            return false;
        if(settings.getNoNumbers()&&domain.matches(".*\\d.*"))
            return false;
        if(l<settings.getLowLength()||l>settings.getUpLength())
            return false;
        Map<String,Integer> extest=settings.getExtnEst();
        if(settings.getRestrictedExtns().contains(tld))
            return false;
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

    @Scheduled(cron = "0 00 20 ? * *", zone = "IST")
    // @Scheduled(cron = cronExpression, zone = "IST")
    @GetMapping("/startlive")
    void liveStart()
    {
        logger.info("Starting Dynadot Live Service");
        LiveFilterSettings settings= settingsRepo.findById(1).get();
        map.clear();
       Optional<LiveMap> o= liveMaprepo.findById(2);
        LiveMap lm;
        if(o.isEmpty())
        {
            lm= new LiveMap(2);
            liveMaprepo.save(lm);
            lm=liveMaprepo.findById(2).get();
        }
        else
            lm=o.get();
        liveRepo.deleteAll();
        map=lm.getMap();

        Date date= new Date();
        String addtime= ft1.format(date);
        date.setHours(date.getHours()+6);
        Long time= date.getTime();
        ResponseLive rl = myFeignClient.getLiveDetails(key,"get_open_auctions","usd","expired,user,backorder,registrar,registry_expired");
        List<LiveDetails> list = rl.getAuction_list();
        int n= list.size();
        int l=0;
        for(int i=0;i<n;i++)
        {
            LiveDetails ld= list.get(i);
            Long auction_id= ld.getAuction_id();
            int bids = ld.getBids();
            Long endtime= ld.getEnd_time_stamp();
            if(bids>0&&endtime<=time)
            {
                if(!map.containsKey(ld.getDomain().toLowerCase()))
                {
                    try {

                        map.put(ld.getDomain().toLowerCase(), auction_id);
                        String time_left = relTimelive(ld.getEnd_time_stamp());

                        ld.setAddtime(addtime);
                        String est = ld.getEstibot_appraisal();
                        Integer EST = Integer.valueOf(est.substring(1, est.length() - 3));
                        ld.setEST(EST);
                        ld.setTime_left(time_left);
                        l = Math.max(l, ld.getDomain().length());
                        ld.setInitialList(true);
                        boolean highlight=isHighlight(ld.getDomain(),EST,settings);
                        ld.setHighlight(highlight);
                        if(highlight) {
                            String domain=ld.getDomain().toLowerCase(); String currbid=ld.getCurrent_bid_price(); int age=ld.getAge();
                            // Integer gdv= goDaddyFeign.getGDV("sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm",domain).getGovalue();
                            if(!taskmap.containsKey(domain))
                            sendLiveI(time_left,domain,ld.getCurrent_bid_price(),ld.getAge(),EST, ld.getAuction_id());
                            else
                            {
                                sendLiveI(time_left,domain,ld.getCurrent_bid_price(),ld.getAge(),EST, ld.getAuction_id(),repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount());
                            }

                        }
                        liveRepo.save(ld);
                    }
                    catch (Exception e)
                    {
                        logger.info(e.getMessage()+" "+ ld.getDomain());
                    }
                }

            }
        }
        summary="";
        //liveMaprepo.save(lm);
        sendHighlights(l);
        sendList(l);
        stopWatch.start();
        logger.info("Started Dynadot Live Service");
        ScheduledFuture scheduledFuture= taskScheduler.scheduleWithFixedDelay(new DetectLive(time),38000);
        taskScheduler.schedule(new StopLiveDyna(scheduledFuture),date);
    }

    boolean healthCheck()
    {
        try {
            ResponseLive rl = myFeignClient.getLiveDetails(key, "get_open_auctions", "usd", "expired,user,backorder");
            if (rl.getStatus().equals("success"))
                return true;
            else return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    void sendSummary()
    {
        if(stopWatch.isStarted())
        {
            stopWatch.split();
            if(stopWatch.getSplitTime()>240000)
            {
                if(summary!=null&&!summary.equals(""))
                    telegram.sendAlert(-1001763199668l, 1014l,"Live Domains in Last 4-5 Minutes:\n\n"+summary);
                summary="";
                stopWatch.reset();stopWatch.start();
            }
        }
    }
    @Autowired
    GoDaddyFeign goDaddyFeign;

    String dotdbkey="Token 6c2753c5bac47cd06cc087368fae3376";
    @Autowired
    DotDBFeign dotDBFeign;

    @GetMapping("/getleads")
    String getLeads(@RequestParam String keyword)
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
    public class DetectLive implements Runnable
    {
        Long time;

        public DetectLive(Long time) {
            this.time = time;
        }

        @Override
        public void run()
        {
           /* LiveMap lm = liveMaprepo.findById(1).get();
            Map<String,Long> map= lm.getMap();*/
            ResponseLive rl = myFeignClient.getLiveDetails(key,"get_open_auctions","usd","expired,user,backorder,registrar,registry_expired");
            LiveFilterSettings settings= settingsRepo.findById(1).get();

            List<LiveDetails> list = rl.getAuction_list();
            int n= list.size();
            Date date= new Date();
            Map<String,Long> map1= new HashMap<>(map);
            logger.info("Detect Live running "+ft1.format(date));
            for(int i=0;i<n;i++)
            {
                LiveDetails ld= list.get(i);
                int bids = ld.getBids();
                Long endtime= ld.getEnd_time_stamp();
                if(bids>0&&endtime<=time)
                {
                    String domain = ld.getDomain().toLowerCase();
                    if(!map.containsKey(domain))
                    {
                        try{
                            String time_left= relTimelive(ld.getEnd_time_stamp());
                        String currbid = ld.getCurrent_bid_price();
                        int age= ld.getAge();
                        String est=ld.getEstibot_appraisal();
                        int EST=(est!=null)?Integer.valueOf(est.substring(1,est.length()-3)):0;
                        summary=summary+domain+"\n";
                        ld.setEST(EST);
                            String[] dom=domain.split("\\.",2);
                            boolean highlight=isHighlight(ld.getDomain(),EST,settings,dom);
                            ld.setHighlight(highlight);
                            if(highlight) {
                                // Integer gdv= goDaddyFeign.getGDV("sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm",domain).getGovalue();
                                String tld=dom[0],sld=dom[1];
                                if(sld.equalsIgnoreCase("com")) {
                                    String leads = getLeads(tld);
                                    if(!taskmap.containsKey(domain))
                                    sendLive(time_left, domain, ld.getCurrent_bid_price(), ld.getAge(), EST, ld.getAuction_id(), leads);
                                else
                                    {
                                        sendLive(time_left, domain, ld.getCurrent_bid_price(), ld.getAge(), EST, ld.getAuction_id(), leads,repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount());
                                    }
                                }
                            else {
                                    if(!taskmap.containsKey(domain))
                                        sendLive(time_left, domain, ld.getCurrent_bid_price(), ld.getAge(), EST, ld.getAuction_id());
                                    else {
                                        sendLive(time_left, domain, ld.getCurrent_bid_price(), ld.getAge(), EST,repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount(), ld.getAuction_id());
                                    }
                            }

                            }
                        //Object obj= telegram.sendAlert(-1001814695777L,text);
                        ld.setLive(true);
                       // ld.setPlatform("Dynadot");
                        String addtime= ft1.format(date);
                        ld.setAddtime(addtime);
                        ld.setTime_left(time_left);
                        //ld.setGdv(gdv);
                        map.put(domain,ld.getAuction_id());
                       // WasLive wasLive= new WasLive(time_left,addtime,"Dynadot",ld.getAuction_id(),domain, ld.getCurrent_bid_price(), ld.getEnd_time(), ld.getEstibot_appraisal(),ld.getUtf_name(),ld.getBids(),ld.getBidders(),ld.getAge(),ld.getEnd_time_stamp());
                       // wasLiveRepo.save(wasLive);
                        liveRepo.save(ld);
                        }
                        catch (Exception e)
                        {
                            logger.info(e.getMessage()+" "+ ld.getDomain());
                        }
                    }
                    else {map1.remove(domain);}
                }

            }
            sendSummary();

            if(!map1.isEmpty())
            for (Map.Entry<String,Long> mapElement : map1.entrySet())
            {
                Long value = mapElement.getValue();
                try {
                    liveRepo.deleteByAuctionid(value);
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage());
                }
            }
           // liveMaprepo.save(lm);
        }
    }


    @GetMapping("/stoplivedyna")
    void stoplivedyna()
    {
        LiveFilterSettings settings= settingsRepo.findById(1).get();
        map.clear();
        Optional<LiveMap> o= liveMaprepo.findById(2);
        LiveMap lm;
        if(o.isEmpty())
        {
            lm= new LiveMap(2);
        }
        else
            lm=o.get();
        Map<String,Long> map1= lm.getMap();
        map1.clear();
        Date date= new Date();
        String addtime=ft1.format(date);


        date.setHours(date.getHours()+24);
        liveRepo.deleteAll();
        Long time= date.getTime();
        date.setHours(date.getHours()-6);
        Long time1= date.getTime();
        ResponseLive rl = myFeignClient.getLiveDetails(key,"get_open_auctions","usd","expired,user,backorder,registrar,registry_expired"
        );
        List<LiveDetails> list = rl.getAuction_list();
        int n= list.size();
        int l=0;
        for(int i=0;i<n;i++)
        {
            LiveDetails ld= list.get(i);
            Long auction_id= ld.getAuction_id();
            int bids = ld.getBids();
            Long endtime= ld.getEnd_time_stamp();
            if(bids>0&&endtime<=time&&endtime>=time1)
            {
                if(!map1.containsKey(ld.getDomain().toLowerCase()))
                {
                    try {
                        map1.put(ld.getDomain().toLowerCase(), auction_id);
                        String time_left = relTimelive(ld.getEnd_time_stamp());
                        Integer EST = Integer.valueOf(ld.getEstibot_appraisal().substring(1, ld.getEstibot_appraisal().length() - 3));
                        ld.setEST(EST);
                        ld.setTime_left(time_left);ld.setAddtime(addtime);
                        l = Math.max(l, ld.getDomain().length());
                        ld.setHighlight(isHighlight(ld.getDomain(),EST,settings));
                        ld.setEndList(true);
                        liveRepo.save(ld);
                    }
                    catch(Exception e)
                    {
                        logger.info(e.getMessage());
                    }
                }

            }
        }
        stopWatch.reset();
        liveMaprepo.save(lm);
        sendEndHighlights(l);
        sendEndList(l);
    }

    public class StopLiveDyna implements Runnable {
        ScheduledFuture scheduledFuture;

        public StopLiveDyna(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        @Override
        public void run() {
            scheduledFuture.cancel(true);
            LiveFilterSettings settings= settingsRepo.findById(1).get();
            map.clear();
            Optional<LiveMap> o= liveMaprepo.findById(2);
            LiveMap lm;
            if(o.isEmpty())
            {
                lm= new LiveMap(2);
            }
            else
                lm=o.get();
            Map<String,Long> map1= lm.getMap();
            map1.clear();
            Date date= new Date();
            String addtime=ft1.format(date);


            date.setHours(date.getHours()+24);
            liveRepo.deleteAll();
            Long time= date.getTime();
            date.setHours(date.getHours()-6);
            Long time1= date.getTime();
            ResponseLive rl = myFeignClient.getLiveDetails(key,"get_open_auctions","usd","expired,user,backorder,registrar,registry_expired"
            );
            List<LiveDetails> list = rl.getAuction_list();
            int n= list.size();
            int l=0;
            for(int i=0;i<n;i++)
            {
                LiveDetails ld= list.get(i);
                Long auction_id= ld.getAuction_id();
                int bids = ld.getBids();
                Long endtime= ld.getEnd_time_stamp();
                if(bids>0&&endtime<=time&&endtime>=time1)
                {
                    if(!map1.containsKey(ld.getDomain().toLowerCase()))
                    {
                        try {
                            map1.put(ld.getDomain().toLowerCase(), auction_id);
                            String time_left = relTimelive(ld.getEnd_time_stamp());
                            Integer EST = Integer.valueOf(ld.getEstibot_appraisal().substring(1, ld.getEstibot_appraisal().length() - 3));
                            ld.setEST(EST);
                            ld.setTime_left(time_left);ld.setAddtime(addtime);
                            l = Math.max(l, ld.getDomain().length());
                            ld.setHighlight(isHighlight(ld.getDomain(),EST,settings));
                            ld.setEndList(true);
                            liveRepo.save(ld);
                        }
                        catch(Exception e)
                        {
                            logger.info(e.getMessage());
                        }
                    }

                }
            }
            stopWatch.reset();
            liveMaprepo.save(lm);
            sendEndHighlights(l);
            sendEndList(l);
        }
    }

    void sendInitialList(int n)
    {
        //int n=32;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dynadot Initial List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";

        List<LiveDetails> list=liveRepo.findByInitialListTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());

            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendList(int n)
    {
        //int n=32;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dynadot Initial List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";

        List<LiveDetails> list=liveRepo.findAllByOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());

            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }
    void sendList(long chat_id)
    {
        int n=25;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dynadot List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";

        List<LiveDetails> list=liveRepo.findAllByOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    @GetMapping("/testlive")
    void testttttt()
    {
        liveRepo.deleteByAuctionid(18668426l);
    }
    void refreshDDLive()
    {
        Map<String,Long> map1=new HashMap<>(map);
        Date date= new Date();
        String addtime= ft1.format(date);
        //if(date.getTimezoneOffset()==0)
        {
            if(date.getHours()<=20&&date.getMinutes()<=30)
            {
                date.setHours(20);date.setMinutes(30);
            }
            else
            {
                date.setDate(date.getDate()+1);date.setHours(20);date.setMinutes(30);
            }
        }
        Long time= date.getTime();
        ResponseLive rl = myFeignClient.getLiveDetails(key,"get_open_auctions","usd","expired,user,backorder,registrar,registry_expired");
        List<LiveDetails> list = rl.getAuction_list();
        int n= list.size();
        int l=0;
        logger.info(""+n);
        for(int i=0;i<n;i++)
        {
            LiveDetails ld= list.get(i);
            Long auction_id= ld.getAuction_id();
            int bids = ld.getBids();
            Long endtime= ld.getEnd_time_stamp();
            if(bids>0&&endtime<=time)
            {
                    try {
                        map1.remove(ld.getDomain().toLowerCase());
                        String time_left = relTimelive(ld.getEnd_time_stamp());
                        ld.setAddtime(addtime);
                        String est = ld.getEstibot_appraisal();
                        Integer EST = Integer.valueOf(est.substring(1, est.length() - 3));
                        ld.setEST(EST);
                        ld.setTime_left(time_left);
                        ld.setLive(true);
                        liveRepo.save(ld);
                    }
                    catch (Exception e)
                    {
                        logger.info(e.getMessage()+" "+ ld.getDomain());
                    }
            }
        }

        if(!map1.isEmpty())
        for (Map.Entry<String,Long> mapElement : map1.entrySet())
        {
            Long value = mapElement.getValue();
            try {
                liveRepo.deleteByAuctionid(value);
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
        }
        }

    void sendListCurrent(long chat_id)
    {
        refreshDDLive();
        int n=25;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dynadot List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";

        List<LiveDetails> list=liveRepo.findAllByOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendHighlights(long chat_id)
    {
        int n=25;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s1=String.format("| %-"+(t-4)+"s |%n", "Dynadot Highlights");
        for(int i=0;i<t;i++)
        {
            s1=s1+"-";
        }
        s1=s1+"\n\n";
        s1=s1+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s1=s1+"-";

        }
        s1=s1+"\n\n";

        List<LiveDetails> list=liveRepo.findByHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        boolean b=true;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);

                j++;
                s1 = s1 + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s1+"</pre>","HTML");

            l=l-d;
            s1="";
        }

    }
    void sendInitialHighlights(int n)
    {
        //int n=32;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s1=String.format("| %-"+(t-4)+"s |%n", "Dynadot Initial Highlights");
        for(int i=0;i<t;i++)
        {
            s1=s1+"-";
        }
        s1=s1+"\n\n";
        s1=s1+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s1=s1+"-";

        }
        s1=s1+"\n\n";

        List<LiveDetails> list=liveRepo.findByInitialListTrueAndHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        boolean b=true;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);

                j++;
                s1 = s1 + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());

            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,1014l,"<pre>"+s1+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s1+"</pre>","HTML");
            l=l-d;
            s1="";
        }

    }

    void sendHighlights(int n)
    {
        //int n=32;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s1=String.format("| %-"+(t-4)+"s |%n", "Dynadot Initial Highlights");
        for(int i=0;i<t;i++)
        {
            s1=s1+"-";
        }
        s1=s1+"\n\n";
        s1=s1+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s1=s1+"-";

        }
        s1=s1+"\n\n";

        List<LiveDetails> list=liveRepo.findByHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        boolean b=true;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);

                j++;
                s1 = s1 + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());

            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,1014l,"<pre>"+s1+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s1+"</pre>","HTML");
            l=l-d;
            s1="";
        }

    }
    void sendEndHighlights(int n)
    {
        //int n=32;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s1=String.format("| %-"+(t-4)+"s |%n", "Dynadot Next Day Highlights");
        for(int i=0;i<t;i++)
        {
            s1=s1+"-";
        }
        s1=s1+"\n\n";
        s1=s1+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s1=s1+"-";

        }
        s1=s1+"\n\n";

        List<LiveDetails> list=liveRepo.findByEndListTrueAndHighlightTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        boolean b=true;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);

                j++;
                s1 = s1 + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());

            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,1014l,"<pre>"+s1+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s1+"</pre>","HTML");
            l=l-d;
            s1="";
        }

    }
    void sendEndList(int n)
    {
        //int n=32;
        //        time left   currbid, est, separators, space around separators
        int t= n+    10+         7  +   6  +  3   +      6;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Dynadot Next Day List");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        s=s+ String.format("%-"+n+"s | %-10s | %-7s | %6s%n","Domain","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";
        }
        s=s+"\n\n";
        List<LiveDetails> list=liveRepo.findByEndListTrueOrderByESTDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                LiveDetails lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-10s  | %-7s | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrent_bid_price(), lnc.getEST());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    public class GetResultdyna implements Runnable
    {
        String domain;
         GetResultdyna(String domain)
         {this.domain=domain;
         }

         @Override
         public void run()
         {
             DBdetails db=repo.findByDomain(domain);
             Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
             Auction_json aj = ra.getAuction_det().getAuction_json();

             if (aj.getAuction_ended()) {
                 if (aj.getIs_high_bidder())
                 {
                     Date now= new Date();
                     String time= timeft.format(now);
                     String bid= aj.getCurrent_bid_price();
                     telegram.sendAlert(-1001763199668l,842l,"Dynadot: Yippee!! Won auction of "+domain+" at price: "+bid);
                     notifRepo.save(new Notification("Dynadot",time,"Yippee!! Won auction of "+domain+" at price: "+bid));
                     logger.info(time+": Won auction of "+domain+" at price: "+bid);
                     db.setResult("Won");
                 }
                 else
                 {
                     Date now= new Date();
                     String time= timeft.format(now);
                     String bid= aj.getCurrent_bid_price();
                     telegram.sendAlert(-1001763199668l,841l,"Dynadot: Hush!! Lost auction of "+domain+" at price: "+bid);
                     notifRepo.save(new Notification("Dynadot",time,"Hush!! Lost auction of "+domain+" at price: "+bid));
                     logger.info(time+": Lost auction of "+domain+" at price: "+bid);
                     db.setResult("Loss");
                 }
                 db.setScheduled(false);

                 deleteTaskMap(domain);
                 db.setCurrbid(aj.getCurrent_bid_price());
                 db.setBidders(aj.getBidders());
                 db.setEndTimepst(aj.getEnd_time());
                 repo.save(db);
                 //db.setTime_left("0m");
             } else {
                 Date d= new Date();
                 d.setMinutes(d.getMinutes()+30);
                ScheduledFuture res= taskScheduler.schedule(new GetResultdyna(domain), d);
                 updateTaskMap(domain,res,"gr");


             }
         }
    }

    public class PreCheck implements Runnable
    {
        String domain, maxbid;

        public PreCheck(String domain, String maxbid) {
            this.domain = domain;
            this.maxbid = maxbid;
        }


        @Override
        public void run()
        {
            try {
                DBdetails db= repo.findByDomainIgnoreCaseAndScheduledTrue(domain);
                Response_AuctionDetails ra =null;
                try{
                     ra = myFeignClientB.getAuctionDetails(key, "get_auction_details", domain, "usd");
            }
            catch(Exception e)
            {
                db.setResult("API Error Fetch pc");
                repo.save(db);
                return;
            }
                Auction_json aj = ra.getAuction_det().getAuction_json();
                String bid= aj.getAccepted_bid_price();
                String currbid = aj.getCurrent_bid_price();
                if(Float.parseFloat(currbid)>Float.parseFloat(maxbid))
                {
                    //notify

                    db.setResult("Outbid");
                    repo.save(db);
                    String time_left= relTimelive(aj.getEnd_time_stamp());
                    long age= aj.getAge();
                    String est=aj.getEstibot_appraisal();
                    sendOutbid("Outbid",time_left,domain,bid,maxbid,age,est, db.getAuctionId());

                    Date now= new Date();
                    String time= timeft.format(now);
                    notifRepo.save(new Notification("Dynadot",time,"Domain: "+domain+" with our max price "+maxbid+" OUTBID at price " + bid ));
                    logger.info(time+": Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + bid );

                    now.setMinutes(now.getMinutes() + 59);
                    ScheduledFuture res=taskScheduler.schedule(new GetResultdyna(domain), now);
                    updateTaskMap(domain,res,"gr");
                }
                else
                {
                    Date date= new Date(aj.getEnd_time_stamp()-10000);
                   ScheduledFuture place= taskScheduler.schedule(new PlaceBid(domain,maxbid,aj.getEnd_time()),date);
                    updateTaskMap(domain,place,"pb");

                    Date now= new Date();
                    String time= timeft.format(now);
                    String bidist= ft1.format(date);
                    notifRepo.save(new Notification("Dynadot",time,"Prechecking, Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist));
                    logger.info(time+": Prechecking, Bid SCHEDULED for " + domain + " at price " + bid + " time " + bidist+" i.e. "+date);

                }
            }
            catch(Exception e)
            {
                Date now= new Date();
                String time= timeft.format(now);
                logger.info(time+": Prechecking exception: "+e.getMessage());
            }
        }
    }
    public class CheckOutbid implements Runnable
    {

        String domain, bid, maxbid;
        ScheduledFuture scheduledFuture;

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public CheckOutbid(String domain, String bid, String maxbid)
        {
            this.domain = domain;
            this.bid = bid;
            this.maxbid = maxbid;
        }

        @Override
        public void run()
        {
            DBdetails db= repo.findByDomainIgnoreCaseAndScheduledTrue(domain);
            Response_AuctionDetails ra =null;
            try{
                ra = myFeignClientB.getAuctionDetails(key, "get_auction_details", domain, "usd");
            }
            catch(Exception e)
            {
                db.setResult("API Error Fetch co");
                repo.save(db);
                return;
            }
            Auction_json aj = ra.getAuction_det().getAuction_json();
            if(aj.getAuction_ended())
            {
                if(aj.getIs_high_bidder())
                {
                    db.setResult("Won");
                    db.setScheduled(false);
                    repo.save(db);
                    deleteTaskMap(domain);
                    Date now= new Date();
                    String time= timeft.format(now);
                    telegram.sendAlert(-1001763199668l,842l,"Dynadot: Yippee!! Won auction of "+domain+" at price: "+bid);
                    notifRepo.save(new Notification("Dynadot",time,"Yippee!! Won auction of "+domain+" at price: "+bid));
                    logger.info(time+": Won auction of "+domain+" at price: "+bid);

                }
                else
                {
                    db.setResult("Loss");
                    db.setCurrbid(aj.getCurrent_bid_price());
                    db.setScheduled(false);

                    repo.save(db);
                    deleteTaskMap(domain);
                    Date now= new Date();
                    String time= timeft.format(now);
                    telegram.sendAlert(-1001763199668l,841l,"Dynadot: Hush!! Lost auction of "+domain+" at price: "+bid);
                    notifRepo.save(new Notification("Dynadot",time,"Hush!! Lost auction of "+domain+" at price: "+aj.getCurrent_bid_price()));
                    logger.info(time+": Lost auction of "+domain+" at price: "+aj.getCurrent_bid_price());

                }
                scheduledFuture.cancel(true);
            }
            else
            {
                if(!aj.getIs_high_bidder())
                {
                    String minbid=aj.getAccepted_bid_price();
                    String currbid = aj.getCurrent_bid_price();

                    if(Float.parseFloat(currbid)>Float.parseFloat(maxbid))
                    {
                        //notify
                        db.setResult("Outbid");
                        db.setCurrbid(currbid);
                        repo.save(db);
                        String time_left= relTimelive(aj.getEnd_time_stamp());

                        long age= aj.getAge();
                        String est=aj.getEstibot_appraisal();
                        sendOutbid("Outbid",time_left,domain,bid,maxbid,age,est, db.getAuctionId());

                        Date now= new Date();
                        String time= timeft.format(now);
                        notifRepo.save(new Notification("Dynadot",time,"Domain: "+domain+" with our max price "+maxbid+" OUTBID at price " + minbid ));
                        logger.info(time+": Dynadot: Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + minbid );

                        now.setMinutes(now.getMinutes() + 29);
                       ScheduledFuture res= taskScheduler.schedule(new GetResultdyna(domain), now);
                        updateTaskMap(domain,res,"gr");
                    }
                    else
                    {
                        Date d= new Date(aj.getEnd_time_stamp()-10000);
                        ScheduledFuture place=taskScheduler.schedule(new PlaceBid(domain,maxbid,aj.getEnd_time()),d);
                        updateTaskMap(domain,place,"pb");


                        logger.info("Rescheduled");
                        db.setResult("Bid Placed And Scheduled");
                        db.setCurrbid(aj.getCurrent_bid_price());
                        repo.save(db);
                        Date now= new Date();
                        String time= timeft.format(now);
                        String bidist= ft1.format(d);
                        telegram.sendAlert(-1001763199668l,1004l,"Dynadot: Outbid, Bid SCHEDULED for " + domain + " at price " + minbid + " at time " + bidist);
                        notifRepo.save(new Notification("Dynadot",time,"Outbid, Bid SCHEDULED for " + domain + " at price " + minbid + " at time: " + bidist));
                        logger.info(time+": Outbid, Bid SCHEDULED for " + domain + " at price " + bid + " time " + bidist+" i.e. "+bidist);

                    }
                    scheduledFuture.cancel(true);
                }
            }
        }
    }

    @Autowired
    MyFeignClientB myFeignClientB;
    public class PlaceBid implements Runnable{
        private String domain;
        private String maxbid;
        private String timeid;

        public PlaceBid(String domain, String maxbid, String timeid)
        {
            this.domain= domain;
            this.maxbid=maxbid;
            this.timeid=timeid;
            //this.service= new Service();
        }
        @Override
        public void run() {
            try {
                DBdetails db= repo.findByDomainIgnoreCaseAndScheduledTrue(domain);
                Response_AuctionDetails ra =null;
                try{
                    ra = myFeignClientB.getAuctionDetails(key, "get_auction_details", domain, "usd");
                }
                catch(Exception e)
                {
                    db.setResult("API Error Fetch pb");
                    repo.save(db);
                    return;
                }                    String timeId1= ra.getAuction_det().getAuction_json().getEnd_time();
                String bid=ra.getAuction_det().getAuction_json().getAccepted_bid_price();
                String currbid=ra.getAuction_det().getAuction_json().getCurrent_bid_price();
                if(timeId1.equals(timeid))
                {
                    if(Float.parseFloat(currbid)<=Float.parseFloat(maxbid))
                    {Response_PlaceBid map;
                        try{
                        map = myFeignClientB.placeAuctionBids(key, "place_auction_bid", domain, bid, "usd");
                        }
                        catch(Exception e)
                        {
                            db.setResult("API Error Bid pb");
                            repo.save(db);
                            return;
                        }   //LinkedHashMap map = (LinkedHashMap) obj;
                        String status = map.getStatus();
                        if (status.equals("success"))
                        {
                            Date d=new Date();
                            String time= timeft.format(d);
                            telegram.sendAlert(-1001763199668l,1004l,"Dynadot: Scheduled Bid PLACED for " + domain + " at price " + bid + " USD");
                            notifRepo.save(new Notification("Dynadot",time,"Scheduled Bid PLACED for " + domain + " at price " + bid + " USD"));
                            logger.info(time+": Scheduled Bid Placed of domain: " + domain+ " at price " + bid + " USD");

                            boolean scheduleCO=true;
                            Auction_json aj = map.getAuction_details().getAuction_json();
                            if(Float.parseFloat(bid)>Float.parseFloat(maxbid))
                                db.setBidAmount(bid);
                            db.setIsBidPlaced(true);
                            db.setResult("Bid Placed");
                            db.setCurrbid(aj.getCurrent_bid_price());
                            db.setEndTimepst(aj.getEnd_time());
                            db.setTime_left(relTime(aj.getEnd_time_stamp()));
                            sendWatchlist("Our Bid Placed",db.getTime_left(),domain,aj.getAccepted_bid_price(),maxbid,aj.getAge(),db.getEstibot(),aj.getAuction_id());
                            if(db.getFastBid()&&(Float.valueOf(db.getFastBidAmount())>=Float.parseFloat(currbid))) {
                                if (map.getAuction_details().getAuction_json().getIs_high_bidder() == false) {
                                    db.setFast_i(db.getFast_i() + 1);
                                    if(db.getFast_i()>db.getFast_n())
                                    {
                                        //fastBid
                                        telegram.sendAlert(-1001763199668l,1004l,"Dynadot: Started Fast Bidding on " + domain);
                                        notifRepo.save(new Notification("Dynadot",time,"Started Fast Bidding on " + domain));
                                        logger.info(time+": Started Fast Bidding on domain: " + domain);
                                        while(true)
                                        {
                                            bid=map.getAuction_details().getAuction_json().getAccepted_bid_price();
                                            currbid=map.getAuction_details().getAuction_json().getCurrent_bid_price();
                                            if(Float.parseFloat(currbid)<=Float.parseFloat(maxbid))
                                            {
                                                try{
                                                    map = myFeignClientB.placeAuctionBids(key, "place_auction_bid", domain, bid, "usd");
                                                }
                                                catch(Exception e)
                                                {
                                                    db.setResult("API Error Bid pb Fast");
                                                    db.setFast_i(0);
                                                    repo.save(db);
                                                    return;
                                                }
                                                status = map.getStatus();
                                                if (status.equals("success")) {
                                                    aj = map.getAuction_details().getAuction_json();
                                                    if (Float.parseFloat(bid) > Float.parseFloat(maxbid))
                                                        db.setBidAmount(bid);
                                                    db.setCurrbid(aj.getCurrent_bid_price());
                                                    if (aj.getIs_high_bidder()) {
                                                        telegram.sendAlert(-1001763199668l, 1004l, "Dynadot: Stopped Fast Bidding on " + domain + " as we surpassed proxy at price: " + aj.getCurrent_bid_price());
                                                        notifRepo.save(new Notification("Dynadot", time, "Stopped Fast Bidding on " + domain + " as we surpassed proxy at price: " + aj.getCurrent_bid_price()));
                                                        logger.info(time + ": Stopped  Fast Bidding on domain: " + domain + " as we surpassed proxy at price: " + aj.getCurrent_bid_price());
                                                        db.setFast_i(0);
                                                        break;
                                                    }
                                                }
                                                else
                                                {
                                                    String content = map.getContent();
                                                    deleteTaskMap(domain);
                                                    scheduleCO=false;
                                                    db.setFast_i(0);
                                                    telegram.sendAlert(-930742733l,"Dynadot: Scheduled Bid NOT PLACED for " + domain + " at price " + bid + " USD with Error Message: " + content);
                                                    logger.info(time+": Bid not placed of domain: " + domain + " at price " + bid + " USD with Error Message: " + content);
                                                    try {
                                                        notifRepo.save(new Notification("Dynadot", time, "Scheduled Bid NOT PLACED for " + domain + " at price " + bid + " USD with Error Message: " + content));
                                                    }
                                                    catch(Exception e)
                                                    {
                                                        logger.info(e.getMessage());
                                                    }
                                                    break;
                                                }
                                            }
                                            else
                                            {
                                                db.setCurrbid(currbid);
                                                db.setResult("Outbid");
                                                String time_left= relTimelive(aj.getEnd_time_stamp());

                                                long age= aj.getAge();
                                                String est=aj.getEstibot_appraisal();
                                                sendOutbid("Outbid",time_left,domain,bid,maxbid,age,est, db.getAuctionId());

                                                Date now= new Date();
                                                time= timeft.format(now);
                                                logger.info(time+": Dynadot: Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + bid );
                                                notifRepo.save(new Notification("Dynadot",time,"Domain: "+domain+" with our max price "+maxbid+" OUTBID at price " + bid ));

                                                now.setMinutes(now.getMinutes() + 59);
                                                ScheduledFuture res=taskScheduler.schedule(new GetResultdyna(domain), now);
                                                updateTaskMap(domain,res,"gr");
                                                db.setFast_i(0);
                                                scheduleCO=false;
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    db.setFast_i(0);
                                }
                            }

                            repo.save(db);
                            if(scheduleCO)
                            {
                                d=new Date();
                                d.setSeconds(d.getSeconds()+30);
                                CheckOutbid checkOutbid= new CheckOutbid(domain,bid,maxbid);
                                ScheduledFuture scheduledFuture= taskScheduler.scheduleAtFixedRate(checkOutbid,d,30000);
                                checkOutbid.setScheduledFuture(scheduledFuture);
                                updateTaskMap(domain,scheduledFuture,"co");
                            }
                        }
                        else
                        {
                            Date d=new Date();
                            String time= timeft.format(d);
                            String content = map.getContent();
                            deleteTaskMap(domain);
                            telegram.sendAlert(-930742733l,"Dynadot: Scheduled Bid NOT PLACED for " + domain + " at price " + bid + " USD with Error Message: " + content);
                            logger.info(time+": Bid not placed of domain: " + domain + " at price " + bid + " USD with Error Message: " + content);
                            try {
                                notifRepo.save(new Notification("Dynadot", time, "Scheduled Bid NOT PLACED for " + domain + " at price " + bid + " USD with Error Message: " + content));
                            }
                            catch(Exception e)
                            {
                                logger.info(e.getMessage());
                            }
                        }
                    }
                    else
                    {
                        //notify
                        Auction_json aj= ra.getAuction_det().getAuction_json();
                        //String currbid = aj.getCurrent_bid_price();
                        db.setCurrbid(currbid);
                        db.setResult("Outbid");
                        repo.save(db);
                        String time_left= relTimelive(aj.getEnd_time_stamp());

                        long age= aj.getAge();
                        String est=aj.getEstibot_appraisal();
                        sendOutbid("Outbid",time_left,domain,bid,maxbid,age,est, db.getAuctionId());

                        Date now= new Date();
                        String time= timeft.format(now);
                        logger.info(time+": Dynadot: Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + bid );
                        notifRepo.save(new Notification("Dynadot",time,"Domain: "+domain+" with our max price "+maxbid+" OUTBID at price " + bid ));

                        now.setMinutes(now.getMinutes() + 59);
                        ScheduledFuture res=taskScheduler.schedule(new GetResultdyna(domain), now);
                        updateTaskMap(domain,res,"gr");

                    }
                }
                else
                {
                    if(Float.parseFloat(currbid)>Float.parseFloat(maxbid))
                    {
                        //notify
                        Auction_json aj= ra.getAuction_det().getAuction_json();
                        db.setResult("Outbid");
                        repo.save(db);
                        String time_left= relTimelive(aj.getEnd_time_stamp());
                       // String currbid = aj.getCurrent_bid_price();
                        long age= aj.getAge();
                        String est=aj.getEstibot_appraisal();
                        sendOutbid("Outbid",time_left,domain,bid,maxbid,age,est, db.getAuctionId());

                        Date now= new Date();
                        String time= timeft.format(now);
                        notifRepo.save(new Notification("Dynadot",time,"Domain: "+domain+" with our max price "+maxbid+" OUTBID at price " + bid ));
                        logger.info(time+": Dynadot: Domain: "+domain+" with our max price "+maxbid+" Outbid at price " + bid );

                        now.setMinutes(now.getMinutes() + 59);
                       ScheduledFuture res= taskScheduler.schedule(new GetResultdyna(domain), now);
                        updateTaskMap(domain,res,"gr");

                    }
                    else
                    {
                        Date date= new Date(ra.getAuction_det().getAuction_json().getEnd_time_stamp()-10000);
                       ScheduledFuture place= taskScheduler.schedule(new PlaceBid(domain,maxbid,timeId1),date);
                        updateTaskMap(domain,place,"pb");

                        Date now= new Date();
                        String time= timeft.format(now);
                        String bidist= ft1.format(date);
                        notifRepo.save(new Notification("Dynadot",time,"Prechecking, Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist));
                        logger.info(time+": Prechecking, Bid SCHEDULED for " + domain + " at price " + bid + " time " + bidist+" i.e. "+date);

                    }
                }
            }
            catch(Exception E)
            {

                //String content = myFeignClient.placeAuctionBidstr(key, "place_auction_bid", domain, maxbid, "usd");

                Date now= new Date();
                String time= timeft.format(now);
                logger.info(time+": Scheduled Bid not placed of domain: " + domain+" at max price " + maxbid + " USD with Error Message: " + E.getMessage());
                try {
                    notifRepo.save(new Notification("Dynadot", time, "Scheduled Bid NOT PLACED for " + domain + " at max price " + maxbid + " USD with Error Message: " + E.getMessage()));
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage());
                }
            }
        }
    }

}