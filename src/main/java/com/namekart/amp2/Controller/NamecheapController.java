package com.namekart.amp2.Controller;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import com.namekart.amp2.APIKeySetting.APIKeySettings;
import com.namekart.amp2.DotDBEntity.DotDbResponse;
import com.namekart.amp2.Entity.*;
import com.namekart.amp2.EstibotEntity.Estibot_Data;
import com.namekart.amp2.Feign.*;
import com.namekart.amp2.NamecheapEntity.*;
import com.namekart.amp2.Repository.*;
import com.namekart.amp2.SettingsEntity.FastBidSetting;
import com.namekart.amp2.SettingsEntity.LiveFilterSettings;
import com.namekart.amp2.Status;
import com.namekart.amp2.TelegramEntities.EditMessage;
import com.namekart.amp2.TelegramEntities.InlineKeyboardButton;
import com.namekart.amp2.TelegramEntities.InlineKeyboardMarkup;
import com.namekart.amp2.TelegramEntities.SendMessage;
import com.namekart.amp2.UserEntities.Action;
import com.namekart.amp2.UserEntities.Authentication;
import com.namekart.amp2.UserEntities.User;
import com.nimbusds.jose.shaded.json.JSONArray;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
//import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

//import org.springframework.util.StopWatch;
import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*")
public class NamecheapController {

    //String bearer = "Bearer 4aa4b0e5ecc2cd5f20b41259603a550cl9wE+5ze225OhGZGHciyoHUGJiErCOc7S42HVyoMLsacyU7d7auWRON41tJVPg+w";


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
    String filler="\n";
    Boolean b= true;

    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
    TimeZone istTimeZone = TimeZone.getTimeZone("IST");

    int buffer=50,bufferQ=10;

    SimpleDateFormat timeft = new SimpleDateFormat("dd/MM HH:mm");

    StopWatch stopWatch;

    @Value("${platforms}")
    String[] platforms;
    String summary="",textli;
    Map<String,String> map;

    ConcurrentMap<Boolean,String> accounts;
    public NamecheapController(AllController controller)
    {
        parser.setTimeZone(utcTimeZone);
        ft1.setTimeZone(istTimeZone);
        timeft.setTimeZone(istTimeZone);
        this.controller=controller;
        this.taskmap=controller.getTaskmap();
        //taskmap= new HashMap<>();
        map=new HashMap<>();
        for(int i=0;i<66;i++)
            filler=filler+"_";
        text1="Namecheap"+filler+"\n";textob="Namecheap OUTBID!!"+filler+"\n";
        textl="Namecheap Live Detect"+filler+"\n";
        textli="Namecheap Initial List Detect"+filler+"\n";
        stopWatch=new StopWatch();
        //stopWatch.start();
        accounts=new ConcurrentHashMap<>();
    }

    FastBidSetting fastBidSetting;

    @Autowired
    APIKeysRepo apiKeysRepo;

    @Autowired
    FastSettingsRepo fastSettingsRepo;
    @PostConstruct
    void loadAccount()
    {
        APIKeySettings settings=apiKeysRepo.findById(1).get();APIKeySettings settings1=apiKeysRepo.findById(2).get();
        accounts.put(false,"Bearer "+settings.getNcKey());accounts.put(true,"Bearer "+settings1.getNcKey());

        /*
        for(String platform : platforms)
        {
           Optional<FastBidSetting> op= fastSettingsRepo.findById(platform);
           if(!op.isPresent())
           {
               FastBidSetting fastBidSetting=new FastBidSetting(platform,4,1000);
               fastSettingsRepo.save(fastBidSetting);
           }
        }
        */
        Optional<FastBidSetting> op= fastSettingsRepo.findById("Namecheap");
        if(!op.isPresent())
        {
            fastBidSetting=new FastBidSetting("Namecheap",4,1000);
            fastSettingsRepo.save(fastBidSetting);
        }
    }
    void setFastBidSetting(int n, int amount)
    {
        fastBidSetting.setFastBidAmount(amount);fastBidSetting.setFastN(n);
        fastBidSetting=fastSettingsRepo.save(fastBidSetting);
    }
    @GetMapping("/postAPIkeys")
    void postSettings()
    {
        APIKeySettings settings= new APIKeySettings(1,"7fcf313ace746555cff70389","babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M","8B8Y70UXd7o7D58A8rh7N829B629L9H8W9G7e7q9W8d","cab3a5f74eee3c7a90027fa7a3081cd9CcawjwyrjfmyhQ5c+PfCADp9wDYnfd2ni6AozrVwtT93rjRaabhDbfp+mYQUhPCy","9jbXdb1mjhS_QcMKNez5VGsuKqjy8zwFe7","KfM7V6dqvRfgYf7KkSPuin");//namecheap aria account
        APIKeySettings settings1= new APIKeySettings(2,"7fcf313ace746555cff70389","babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M","8B8Y70UXd7o7D58A8rh7N829B629L9H8W9G7e7q9W8d","cab3a5f74eee3c7a90027fa7a3081cd9CcawjwyrjfmyhQ5c+PfCADp9wDYnfd2ni6AozrVwtT93rjRaabhDbfp+mYQUhPCy","9jbXdb1mjhS_QcMKNez5VGsuKqjy8zwFe7","KfM7V6dqvRfgYf7KkSPuin");
        apiKeysRepo.save(settings);apiKeysRepo.save(settings1);
        //APIKeySettings settings=apiKeysRepo.findById(1).get();APIKeySettings settings1=apiKeysRepo.findById(2).get();
        accounts.put(false,"Bearer "+settings.getNcKey());accounts.put(true,"Bearer "+settings1.getNcKey());
    }
    Logger logger =Logger.getLogger("Namecheap Yash");

    ConcurrentMap<String, Status> taskmap;

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
    String getAccount(String domain)
    {
        domain=domain.toLowerCase();
        if(taskmap.containsKey(domain))
        return accounts.get(taskmap.get(domain).isAccount());
        else return accounts.get(false);
    }

    boolean getAccountBoolean(String domain)
    {
        domain=domain.toLowerCase();
        if(taskmap.containsKey(domain))
            return taskmap.get(domain).isAccount();
        else return false;
    }
    String getTask(String domain)
    {
        domain=domain.toLowerCase();
        if(taskmap.containsKey(domain))
            return taskmap.get(domain).getFutureTask();
        else return "no";
    }

    void saveAction(String action, String medium, String telegramGroup, User user, DBdetails dbdetails, Notification notification, boolean success, String domain, String userName)
    {
        Action action1=new Action(action,medium,telegramGroup,user,dbdetails,notification,success,domain,userName);
        try {
            actionRepository.save(action1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


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
    String getAccount(DBdetails db)
    {
        return accounts.get(db.getAccount());
    }



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
            Status status= new Status(scheduledFuture,futureTask,false);
            taskmap.put(domain,status);
        }
    }

    void enterTaskMap(String domain, ScheduledFuture scheduledFuture, String futureTask, boolean account)
    {
        domain=domain.toLowerCase();
        if(taskmap.containsKey(domain))
        {
            Status status=taskmap.get(domain);
            status.getFuture().cancel(true);
            status.setFuture(scheduledFuture);
            status.setFutureTask(futureTask);
            status.setAccount(account);
        }
        else
        {
            Status status= new Status(scheduledFuture,futureTask,account);
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

    @Autowired
    ActionRepository actionRepository;

    @GetMapping("/testmapping")
    Object testMapping()
    {
        /*User user= new User("abc","abc","abc");
        userRepository.save(user);*/
        DBdetails dBdetails=new DBdetails("abc1.com");
        repo.save(dBdetails);
        Notification notification= new Notification("abc");
        notifRepo.save(notification);
        //Notification notification=notifRepo.findById(1l).get();
        //Action action= actionRepository.findById(1l).get();
        Action action= new Action();
        action.setDbdetails(dBdetails);action.setNotification(notification);action.setUser(userRepository.findById(3).get());
        actionRepository.save(action);

        return repo.findById(415l).get();
    }

    @GetMapping("/testmapping1")
    Object testMapping1()
    {
        /*User user= new User("abc","abc","abc");
        userRepository.save(user);
        DBdetails dBdetails=new DBdetails("abc.com");
        repo.save(dBdetails);
        Notification notification= new Notification("abc");
        notifRepo.save(notification);
        //Notification notification=notifRepo.findById(1l).get();
        //Action action= actionRepository.findById(1l).get();
        Action action= new Action();
        action.setDbdetails(dBdetails);action.setNotification(notification);action.setUser(user);
        actionRepository.save(action);*/

        return userRepository.findById(3).get();
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
    /*@GetMapping("/nct")
    AuctionDetailNC nc()
    {
        AuctionDetailNC nc= namecheapfeign.getAuctionDetailbyId(bearer,"4hv4XEyQSiupYEJLnNErw");
        logger.info("yo");
        return nc;
    }*/

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
                String bearer= getAccount(db);
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

    String text1; String textob;
    String textl;

    @GetMapping("/startwatch")
   void startwatch()
    {
        stopWatch.start();
        logger.info(""+stopWatch.isStopped());

        //stopWatch.ge
    }

    @GetMapping("/getwatch")
    void getWatchtime()
    {
        stopWatch.split();
        logger.info(""+stopWatch.getSplitTime());
    }

    @GetMapping("/stopwatch")
    void stopwatch()
    {
        stopWatch.stop();
        logger.info(""+stopWatch.getTime());

    }
    @GetMapping("/resetwatch")
    void resetwatch()
    {
        stopWatch.reset();
        logger.info(""+stopWatch.getTime()+stopWatch.isStarted());

    }

    String liveFormat(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, Integer EST)
    {
        if(ourMaxBid==null||ourMaxBid.isEmpty())
        {
            ourMaxBid="0";
        }
        String text="NC "+status+" - "+timeLeft+"\n"+domain+"\n"+"Price: "+minBid+" | Our Limit: "+ourMaxBid+"\n"+"EST: "+EST;
        return text;
    }
    String liveFormat(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, Integer EST, boolean account)
    {
        if(ourMaxBid==null||ourMaxBid.isEmpty())
        {
            ourMaxBid="0";
        }
        String a=account?"S":"P";
        String text="NC "+status+" - "+timeLeft+"\n"+domain+"\n"+"Price: "+minBid+" | Our Limit: "+ourMaxBid+"\n"+"EST: "+EST+" "+a;
        return text;
    }
    String mute_unmute="\uD83D\uDD08/\uD83D\uDD07";

    InlineKeyboardMarkup getKeyboardWatch(String domain,String auctionId, Float currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();

        row.add(new InlineKeyboardButton("Bid 50", "b" + " nc "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid 50 Alt", "-b" + " nc "+auctionId+" " + domain + " " + currbid+" 50"));
        row.add(new InlineKeyboardButton("Custom", "cn" + " nc "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton(mute_unmute, "m" + " nc "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " nc "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.namecheap.com/market/" + domain);
        row1.add(link);
        row2.add(new InlineKeyboardButton("Leads", "l" + " nc "+auctionId+" " + domain + " " + currbid));
        row2.add(new InlineKeyboardButton("Stats", "s" + " nc "+auctionId+" " + domain + " " + currbid));

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        rows.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getKeyboardOb(String domain,String auctionId, Float currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();

        row.add(new InlineKeyboardButton("Bid 50", "b" + " nc "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid 50 Alt", "-b" + " nc "+auctionId+" " + domain + " " + currbid+" 50"));
        row.add(new InlineKeyboardButton("Custom", "cn" + " nc "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " nc "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.namecheap.com/market/" + domain);
        row1.add(link);
        row2.add(new InlineKeyboardButton("Leads", "l" + " nc "+auctionId+" " + domain + " " + currbid));
        row2.add(new InlineKeyboardButton("Stats", "s" + " nc "+auctionId+" " + domain + " " + currbid));

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        rows.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }
    InlineKeyboardMarkup getKeyboardLive(String domain, String auctionId,Float currbid)
    {
        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("Bid 50", "b" + " nc "+auctionId+" " + domain + " " + currbid + " 50"));
        row.add(new InlineKeyboardButton("Bid 50 Alt", "-b" + " nc "+auctionId+" " + domain + " " + currbid+" 50"));
        row.add(new InlineKeyboardButton("Custom", "cn" + " nc "+auctionId+" " + domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Watch", "w" + " nc " +auctionId+" "+ domain + " " + currbid));
        row1.add(new InlineKeyboardButton("Refresh", "r" + " nc "+auctionId+" " + domain + " " + currbid));
        row2.add(new InlineKeyboardButton("Leads", "l" + " nc "+auctionId+" " + domain + " " + currbid));
        row2.add(new InlineKeyboardButton("Stats", "s" + " nc "+auctionId+" " + domain + " " + currbid));
        InlineKeyboardButton link = new InlineKeyboardButton("Link");
        link.setUrl("https://www.namecheap.com/market/" + domain);
        row1.add(link);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        rows.add(row2);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
        return inlineKeyboardMarkup;
    }

    void sendOutbid(String status, String timeLeft, String domain, Float minBid, String ourMaxBid, Integer EST,String auctionId,boolean account)
    {
        String text=liveFormat(status,timeLeft,domain,minBid,ourMaxBid,EST,account);
        telegram.sendKeyboard(new SendMessage(-1001866615838L,text,getKeyboardOb(domain,auctionId,minBid)));
    }

    void sendWatchlist(String status, String timeLeft, String domain,Float minBid, String ourMaxBid, Integer EST,String auctionId)
    {
        String text=liveFormat(status,timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001887754426L,text,getKeyboardWatch(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, Float minBid, String ourMaxBid,Integer EST,String auctionId)
    {
        String text=liveFormat("Live Detect",timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1017l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, Float minBid, Integer EST,String auctionId,String leads,String ourMaxBid)
    {
        String text=liveFormat("Live Detect",timeLeft,domain,minBid,ourMaxBid,EST);
        if(leads!=null&&!leads.equals(""))
            text=text+"\n"+leads;
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1017l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, Float minBid, Integer EST,String auctionId)
    {
        String text=liveFormat("Live Detect",timeLeft,domain,minBid,"",EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1017l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLive(String timeLeft, String domain, Float minBid, Integer EST,String auctionId,String leads)
    {
        String text=liveFormat("Live Detect",timeLeft,domain,minBid,"",EST);
        if(leads!=null&&!leads.equals(""))
            text=text+"\n"+leads;
        telegram.sendKeyboard(new SendMessage(-1001763199668l,1017l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLiveI(String timeLeft, String domain, Float minBid,Integer EST,String auctionId)
    {
        String text=liveFormat("Initial Detect",timeLeft,domain,minBid,"",EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,24112l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    void sendLiveI(String timeLeft, String domain, Float minBid,Integer EST,String auctionId, String ourMaxBid)
    {
        String text=liveFormat("Initial Detect",timeLeft,domain,minBid,ourMaxBid,EST);
        telegram.sendKeyboard(new SendMessage(-1001763199668l,24112l,text,getKeyboardLive(domain,auctionId,minBid)));
    }
    @Scheduled(fixedRate = 120000)
    void refreshncwatchlist()
    {

        /*long ti=stopWatch.getLastTaskTimeMillis();
        logger.info(""+ti);
        if(ti>80000)
        {
            stopWatch.stop();stopWatch.start();
        }*/
        List<DBdetails> list= repo.findByPlatformAndWatchlistIsTrue("Namecheap");
        if(!list.isEmpty())
        for(int i=0;i< list.size();i++)
        {
            DBdetails db= list.get(i);
            String ncid= db.getNamecheapid();
            String domain= db.getDomain();

            try {
                String bearer=getAccount(db);
                AuctionDetailNC nc = namecheapfeign.getAuctionDetailbyId(bearer, ncid);
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
                Float currbid = nc.getPrice();
                Date now= new Date();
                if (nc.getStatus().equals("active"))
                {
                    if(db.getScheduled())
                    {
                        if(currbid>Float.valueOf(db.getBidAmount())&&(!db.getResult().equals("Outbid")))
                        {
                            sendOutbid("Outbid",time_left,domain,nc.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid,db.getAccount());

                            String time= timeft.format(now);

                            notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+db.getBidAmount()+" OUTBID at price " + nc.getPrice() ));
                            logger.info(time+": Domain: "+domain+" with our max price "+db.getBidAmount()+" Outbid at price " + nc.getPrice() );
                            db.setResult("Outbid");}
                        else if(b&&nc.getMinBid()<=Float.valueOf(db.getBidAmount()))
                        {
                            if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                                d.setSeconds(d.getSeconds() - bufferQ);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, Float.valueOf(db.getBidAmount()), domain, endTime,true), d);
                                enterTaskMap(domain, task, "pb");
                            }
                            else if (d.getTime() - now.getTime() < 300000) {
                                d.setSeconds(d.getSeconds() - buffer);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid,Float.valueOf(db.getBidAmount()) , domain, endTime,false), d);
                                enterTaskMap(domain, task, "pb");

                            } else {
                                //d.setMinutes(d.getMinutes()-4);
                                Date d1 = new Date(d.getTime() - 270000);
                                ScheduledFuture task = taskScheduler.schedule(new PreCheck(ncid, domain, Float.valueOf(db.getBidAmount())), d1);
                                enterTaskMap(domain, task, "pc");

                            }
                        } else if (currbid>0.85*Float.valueOf(db.getBidAmount())&&(!db.getResult().equals("Outbid"))&&(db.isApproachWarn())) {
                            sendOutbid("Approaching Our Bid",time_left,domain,nc.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid,db.getAccount());
                            db.setApproachWarn(false);
                        }
                    }
                    else if(!db.getMute()) {
                        if (prevBid < currbid&&(nc.getPrice()!=db.getMyLastBid())) {
                            sendWatchlist("New Bid Placed",time_left,domain,nc.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid);

                        }
                        int nw = db.getNw();
                        if (nw == 0) {
                            if (d.getTime() - now.getTime() > 86400000)
                                nw = 4;
                            else if (d.getTime() - now.getTime() > 3600000)
                                nw = 3;
                            else if (d.getTime() - now.getTime() > 600000)
                                nw = 2;
                            else if (d.getTime() - now.getTime() > 240000)
                                nw = 1;
                            db.setNw(nw);
                        }
                        if (d.getTime() - now.getTime() < 86400002 && d.getTime() - now.getTime() > 86280000 && nw >= 4) {
                            sendWatchlist("<24 hrs LEFT",time_left,domain,nc.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid);

                            nw = 3;
                            db.setNw(nw);
                        } else if (d.getTime() - now.getTime() < 3600002 && d.getTime() - now.getTime() > 3480000 && nw >= 3) {
                            sendWatchlist("<1 hr LEFT",time_left,domain,nc.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid);

                            nw = 2;
                            db.setNw(nw);
                        } else if (d.getTime() - now.getTime() < 600002 && d.getTime() - now.getTime() > 480000 && nw >= 2) {
                            sendWatchlist("<10 mins LEFT",time_left,domain,nc.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid);

                            nw = 1;
                            db.setNw(nw);
                        } else if (d.getTime() - now.getTime() < 240002 && d.getTime() - now.getTime() > 120000 && nw >= 1) {
                            sendWatchlist("<4 mins LEFT",time_left,domain,nc.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid);

                            nw = -1;
                            db.setNw(nw);
                        }
                    }

                }
                else
                {
                    String time=timeft.format(now);
                    notifRepo.save(new Notification("Namecheap",time,"Watchlisted auction ended of domain: "+domain+" at price: "+currbid+" at time: "+endTimeist));
                    if(db.getScheduled()) {

                        if (currbid.equals(db.getMyLastBid())||currbid.floatValue()<db.getMyLastBid().floatValue()) {
                            telegram.sendAlert(-1001763199668l, 842l, "Namecheap: Yippee!! Won auction of " + domain + " at price: " + currbid);
                            notifRepo.save(new Notification("Namecheap", time, "Yippee!! Won auction of " + domain + " at price: " + currbid));
                            logger.info(time + ": Won auction of " + domain + " at price: " + currbid);
                            deleteTaskMap(domain);
                            db.setResult("Won");

                        } else {
                            telegram.sendAlert(-1001763199668l, 841l, "Namecheap: Hush!! Lost auction of " + domain + " at price: " + currbid);
                            notifRepo.save(new Notification("Namecheap", time, "Hush!! Lost auction of " + domain + " at price: " + currbid));
                            logger.info(time + ": Lost auction of " + domain + " at price: " + currbid);
                            deleteTaskMap(domain);
                            db.setResult("Loss");
                        }

                        db.setScheduled(false);
                    }
                    db.setWatchlist(false);
                    db.setWasWatchlisted(true);
                    repo.save(db);
                }
                db.setCurrbid(String.valueOf(currbid));
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setTime_left(time_left);
                db.setBids(nc.getBidCount());
                repo.save(db);
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
       // List<DBdetails> slist= repo.findScheduledNC();

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
                if(arr.length>3) {
                    arr[4] = currbid + "";
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

    String getPrimaryAccount()
    {
        return accounts.get(false);
    }

    void refreshBot(String domain, String ncid, Long chat_id, Long message_thread_id, InlineKeyboardMarkup markup)
    {
        domain=domain.toLowerCase();
        String bearer="";
        Optional<DBdetails> op = Optional.ofNullable(repo.findByPlatformAndNamecheapid("Namecheap",ncid));
        DBdetails db=null;
        boolean b=op.isPresent();
        if(b)
        {db=op.get(); bearer=getAccount(db);}
        else bearer=getPrimaryAccount();
        AuctionDetailNC nc = namecheapfeign.getAuctionDetailbyId(bearer,ncid);
        String text="Updated\uD83D\uDFE2\n\n";
        Float currbid = nc.getPrice();EditMessage editMessage=null;
        if (nc.getStatus().equals("active")) {
            String endTime = nc.getEndDate();
            endTime = endTime.substring(0, endTime.length() - 5);
            Date d = null;
            String endTimeist = "";
            String time_left = "";
            try {
                d = parser.parse(endTime);
                time_left = relTime(d);
            } catch (ParseException p) {
                logger.info(p.getMessage());
            }
            endTimeist = ft1.format(d);
            logger.info(endTimeist);
            /*markup.getInline_keyboard().get(0).get(0).setCallback_data("b nc "+ncid+" "+domain+" "+currbid+" 50");
            markup.getInline_keyboard().get(0).get(0).setCallback_data("b nc "+ncid+" "+domain+" "+currbid);*/
            if (b&&db.getScheduled()) {
                if (currbid > Float.valueOf(db.getBidAmount())) {
                    text = text + "Namecheap Auction LOSING/OUTBID" + filler + "\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + nc.getPrice() + "\nMin Next Bid: " + nc.getMinBid() + "\nOur Max Bid: " + db.getBidAmount() + " \nEST: " + db.getEstibot();
                } else
                    text = text + "Namecheap Auction WINNING" + filler + "\n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + nc.getPrice() + "\nMin Next Bid: " + nc.getMinBid() + "\nOur Max Bid: " + db.getBidAmount() + " \nEST: " + db.getEstibot();

            } else {
                text =text+ "Namecheap Auction"+filler+ "\n" + domain + "\n\nTime Left: " + time_left + "\nCurrent Bid: " + currbid + "\nBids: " + nc.getBidCount() + " \n\nEST: " + nc.getEstibotValue();
            }
            //editMessage= new EditMessage(text,chat_id,message_id,markup);
            SendMessage sendMessage= new SendMessage(chat_id,message_thread_id,text,refreshMarkup(markup,currbid));
            telegram.sendKeyboard(sendMessage);
        }
        else {
            if(b){
            if (db.getResult().equals("Won")) {
                text = text + "Namecheap Auction WON!!" + filler + "\n" + domain + "\nLast Bid: " + nc.getPrice() + "\nOur Max Bid: " + db.getBidAmount() + " \nEST: " + db.getEstibot();
            } else if (db.getResult().equals("Loss"))
                text = text + "Namecheap Auction LOST!!" + filler + "\n" + domain + "\nLast Bid: " + nc.getPrice() + "\nOur Max Bid: " + db.getBidAmount() + " \nEST: " + db.getEstibot();
            else if (db.getScheduled()) {
                if (currbid.equals(db.getMyLastBid())) {
                    text = text + "Namecheap Auction WON!!" + filler + "\n" + domain + "\nLast Bid: " + nc.getPrice() + "\nOur Max Bid: " + db.getBidAmount() + " \nEST: " + db.getEstibot();
                } else {
                    text = text + "Namecheap Auction LOST!!" + filler + "\n" + domain + "\nLast Bid: " + nc.getPrice() + "\nOur Max Bid: " + db.getBidAmount() + " \nEST: " + db.getEstibot();
                }
            }
        }
            else {
                text = text + "Namecheap Auction ENDED" + filler + "\n" + domain + "\nLast Bid: " + nc.getPrice()  + " \nEST: " + nc.getEstibotValue();
            }
            telegram.sendAlert(chat_id,message_thread_id,text);

        }
        //telegram.editMessageText(editMessage);
    }

    void watchlistLive(String domain, String ncid, Boolean track, String chat_title, Long tg_id)
    {
        try {
            CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
            domain=domain.toLowerCase();
            String bearer= getPrimaryAccount();
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
            sendWatchlist("Watchlist",time_left,domain,nc.getMinBid(),db.getBidAmount(),nc.getEstibotValue(),ncid);
            Date now=new Date();
            String time = timeft.format(now);
            Notification notification=notifRepo.save(new Notification("Namecheap", time, "Domain Watchlisted " + domain ));
            saveAction("Watchlisted","Bubble",chat_title,db,notification,true,domain,tg_id);

            controller.putESTinDBSingle(cf);
        }
        catch(Exception e)
        {
            Date now= new Date();
            String time = timeft.format(now);
            notifRepo.save(new Notification("Namecheap",time,"Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage()));
            logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
        }
    }
   /* @Scheduled(fixedRate = 120000)
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
                    List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid 50", "b" + " nc " + ncid + " " + domain + " " + currbid+" 50"));
                    row.add(new InlineKeyboardButton("Bid", "b" + " nc " + ncid + " " + domain + " " + currbid));
                    row1.add(new InlineKeyboardButton("Remove", "rw nc " + ncid + " " + domain));
                    row1.add(new InlineKeyboardButton("Refresh", "r nc " + ncid + " " + domain));
                    InlineKeyboardButton link= new InlineKeyboardButton("Link");
                    link.setUrl("https://www.namecheap.com/market/" + domain);
                    row1.add(link);

                    List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                    rows.add(row);rows.add(row1);
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
*/
    void instantUpdateWatchlist(DBdetails db)
    {
        String domain= db.getDomain();
        String ncid= db.getNamecheapid();
        try {
            String bearer= getAccount(db);
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


                db.setCurrbid(String.valueOf(currbid));
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setTime_left(time_left);
                db.setEstibot(nc.getEstibotValue());
                //db.setGdv(gdv);
            db.setWatchlist(true);
            /*if(track)
                db.setTrack(true);*/
            sendWatchlist("Watchlist",time_left,domain,nc.getMinBid(),db.getBidAmount(),nc.getEstibotValue(),ncid);

            repo.save(db);
            Date now=new Date();
            String time = timeft.format(now);
            Notification notification=notifRepo.save(new Notification("Namecheap", time, "Domain Watchlisted " + domain ));
            saveAction("Watchlisted","UI",getUser(),db,notification,true,domain,getUserName());

        }
        catch(Exception e)
        {
            Date now= new Date();
            String time = timeft.format(now);
            notifRepo.save(new Notification("Namecheap",time,"Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage()));
            logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
        }
    }
    //@Autowired
    AllController controller;

    @PreAuthorize("hasAuthority('APPROLE_Watch')")
    @PostMapping("/fetchdetailsnc")
    List<DBdetails> fetchdetailsnc(@RequestBody FetchReq body)
    {
        List<String> ddlist= body.getDomains();
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList(ddlist);
        Boolean watch= body.getWatch();
        String bearer=getPrimaryAccount();
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
                    sendWatchlist("Watchlist",time_left,domain,nc.getMinBid(),db.getBidAmount(),nc.getEstibotValue(),ncid);
                    Date now=new Date();
                    String time = timeft.format(now);
                    Notification notification=notifRepo.save(new Notification("Namecheap", time, "Domain Watchlisted " + domain ));
                    saveAction("Watchlisted","UI",getUser(),db,notification,true,domain,getUserName());
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
        controller.putESTinDB(cf);
        return list;
    }

    List<DBdetails> fetchdetailsncbot(FetchReq body,Long tg_id)
    {
        List<String> ddlist= body.getDomains();
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList(ddlist);
        Boolean watch= body.getWatch();
        String bearer=getPrimaryAccount();
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
                    Date now=new Date();
                    String time = timeft.format(now);
                    Notification notification=notifRepo.save(new Notification("Namecheap", time, "Domain Watchlisted " + domain ));
                    saveAction("Watchlisted","CPanel",db,notification,true,domain,tg_id);

                    sendWatchlist("Watchlist",time_left,domain,nc.getMinBid(),db.getBidAmount(),nc.getEstibotValue(),ncid);

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
        controller.putESTinDB(cf);
        return list;
    }

    @GetMapping("/fetchnc")
    ResponseLivedb fetchlive()
    {
        String bearer= getPrimaryAccount();
        return namecheapfeign.getAuctionDetailslive1(bearer,"1_","1669908629_1669912229","end_time",100);
    }

    @GetMapping("/fetch123")
   // @PreAuthorize("hasAuthority('APPROLE_Bid_GD')")
    Object fetchlive22()
    {

      // String accessToken = jwt.getTokenValue();
        logger.info(SecurityContextHolder.getContext().getAuthentication().getName());

        //AADOAuth2AuthenticatedPrincipal ad= (AADOAuth2AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       /* logger.info(((JSONArray)ad.getClaim("roles")).size()+"");
        Set<String> set=new HashSet<>();
        JSONArray array= (JSONArray) ad.getClaim("roles");
        for (int i=0;i<array.size();i++) {
            set.add(array.get(i)+"");
        }
        return set;*/
        return null;
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

    float schedulesingle(String domain, String ncid, Float bid,boolean changeAccount )
    {
        domain=domain.toLowerCase();
        boolean oldAccount=getAccountBoolean(domain);
        String bearer=getAccount(domain);
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
                    if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                        d.setSeconds(d.getSeconds() - bufferQ);
                        ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,true), d);
                        enterTaskMap(domain, task, "pb");
                    }
                    else if (d.getTime() - now.getTime() < 300000) {
                        d.setSeconds(d.getSeconds() - buffer);
                        ScheduledFuture place = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,false), d);

                        enterTaskMap(domain, place, "pb",changeAccount?!oldAccount:oldAccount);
                    } else {
                        d.setMinutes(d.getMinutes() - 4);
                        ScheduledFuture pre = taskScheduler.schedule(new PreCheck(ncid, domain, maxprice), d);
                        enterTaskMap(domain, pre, "pc",changeAccount?!oldAccount:oldAccount);

                    }
                    Date finalD = d;
                    String finalEndTime = endTime;
                    String finalDomain = domain;
                    CompletableFuture.runAsync(()->{
                    String endTimeist = ft1.format(finalD);
                    String time_left = relTime(finalD);
                    telegram.sendAlert(-1001763199668l,1005l, "Namecheap: BID SCHEDULED for domain: " + finalDomain + " for max price: " + maxprice + " at " + endTimeist);
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
                        if(db.getMyLastBid().equals(currbid))
                        {
                            db.setResult("Bid Placed");
                            Date now1 = new Date();
                            now1.setSeconds(now1.getSeconds() + 45);
                            CheckOutbid checkOutbid = new CheckOutbid(currbid, maxprice, ncid, finalDomain,oldAccount);
                            ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now1, 30000);
                            checkOutbid.setScheduledFuture(scheduledFuture);
                            enterTaskMap(finalDomain, scheduledFuture, "co");
                        }

                    } else {
                        db = new DBdetails(finalDomain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", finalEndTime, endTimeist, endTimeist, false, ncid);
                        db.setTime_left(time_left);
                        db.setBidAmount(String.valueOf(bid));
                        db.setEstibot(nc.getEstibotValue());
                    }
                        db.setScheduled(true);
                        db.setAccount(changeAccount?!oldAccount:oldAccount);
                        //db.setGdv(gdv);
                    repo.save(db);
                    String time = timeft.format(now);
                    notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + finalDomain + " for max price: " + maxprice + " at " + endTimeist));
                    logger.info("BID SCHEDULED for domain: " + finalDomain + " for max price: " + maxprice + " at " + endTimeist);
},threadPoolExecutor);
                return 0;}
                else
                {
                    String finalDomain1 = domain;
                    CompletableFuture.runAsync(()->{
                        Date now = new Date();
                        String time = ft1.format(now);
                        telegram.sendAlert(-1001763199668l, "Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid);
                        notifRepo.save(new Notification("GoDaddy", time, "Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid));
                        logger.info(time + ": Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextBid);
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

    @Autowired
    NamecheapfeignBQ namecheapfeignBQ;
    @GetMapping("/schedulesinglenc")@PreAuthorize("hasAuthority('APPROLE_Bid_NC')")
    float schedulesingleweb(@RequestParam String domain, @RequestParam String ncid, @RequestParam Float bid)
    {
        CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
        Float maxprice= bid;
        try {
            String bearer=getAccount(domain);
            boolean oldAccount=getAccountBoolean(domain);
            ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
            if(rn.getItems()!=null&&rn.getItems().get(0)!=null)
            {AuctionDetailNC nc = rn.getItems().get(0);
            if (nc.getStatus().equals("active")) {
                float minNextBid = nc.getMinBid();

                if (minNextBid <= maxprice) {
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
                    //boolean q=false;

                    if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                        d.setSeconds(d.getSeconds() - bufferQ);
                        ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,true), d);
                        enterTaskMap(domain, task, "pb");
                    }
                    else if (d.getTime() - now.getTime() < 300000) {
                        d.setSeconds(d.getSeconds() - buffer);
                        ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,false), d);
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
                        telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
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
                            if(db.getMyLastBid().equals(currbid))
                            {
                                Date now1 = new Date();
                                now1.setSeconds(now1.getSeconds() + 45);
                                db.setResult("Bid Placed");
                                CheckOutbid checkOutbid = new CheckOutbid(currbid, maxprice, ncid, domain,oldAccount);
                                ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now1, 30000);
                                checkOutbid.setScheduledFuture(scheduledFuture);
                                enterTaskMap(domain, scheduledFuture, "co");
                            }

                        } else {
                            db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", finalEndTime, endTimeist, endTimeist, false, ncid);
                            db.setTime_left(time_left);
                            db.setBidAmount(String.valueOf(bid));
                            db.setEstibot(nc.getEstibotValue());


                        }
                        db.setScheduled(true);

                        repo.save(db);
                        String time = timeft.format(now);
                        Notification notif=notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist));
                        saveAction("Bid Scheduled","UI List",getUser(),db,notif,true,domain,getUserName());
                        logger.info("BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
                    }, threadPoolExecutor);
                    controller.putESTinDBSingle(cf);
                    return 0;
                } else {
                    CompletableFuture.runAsync(() -> {
                        Date now = new Date();
                        String time = ft1.format(now);
                        telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                        Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                        saveAction("Bid Scheduled","UI List",getUser(),repo.findByNamecheapid(ncid),notif,false,domain,getUserName());

                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);
                    }, threadPoolExecutor);
                    return minNextBid;
                }
            } else {
                CompletableFuture.runAsync(() -> {
                    Date now = new Date();
                    String time = ft1.format(now);
                    telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as auction has ended");
                    Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as auction has ended"));
                    saveAction("Bid Scheduled","UI List",getUser(),repo.findByNamecheapid(ncid),notif,false,domain,getUserName());
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended");
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
                telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                saveAction("Bid Scheduled","UI List",getUser(),repo.findByNamecheapid(ncid),notif,false,domain,getUserName());
                logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
            }, threadPoolExecutor);
            return 2;
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
    @GetMapping("/schedulesinglenclive")@PreAuthorize("hasAnyAuthority('APPROLE_Bid_NC','APPROLE_Live_Bid_NC')")
    float schedulesinglewebLive(@RequestParam String domain, @RequestParam String ncid, @RequestParam Float bid)
    {
        CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
        Float maxprice= bid;
        try {
            String bearer=getAccount(domain);
            boolean oldAccount=getAccountBoolean(domain);
            ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
            if(rn.getItems()!=null&&rn.getItems().get(0)!=null)
            {AuctionDetailNC nc = rn.getItems().get(0);
                if (nc.getStatus().equals("active")) {
                    float minNextBid = nc.getMinBid();

                    if (minNextBid <= maxprice) {
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
                        //boolean q=false;

                        if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                            d.setSeconds(d.getSeconds() - bufferQ);
                            ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,true), d);
                            enterTaskMap(domain, task, "pb");
                        }
                        else if (d.getTime() - now.getTime() < 300000) {
                            d.setSeconds(d.getSeconds() - buffer);
                            ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,false), d);
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
                            telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
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
                                if(db.getMyLastBid().equals(currbid))
                                {
                                    Date now1 = new Date();
                                    now1.setSeconds(now1.getSeconds() + 45);
                                    db.setResult("Bid Placed");
                                    CheckOutbid checkOutbid = new CheckOutbid(currbid, maxprice, ncid, domain,oldAccount);
                                    ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now1, 30000);
                                    checkOutbid.setScheduledFuture(scheduledFuture);
                                    enterTaskMap(domain, scheduledFuture, "co");
                                }

                            } else {
                                db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", finalEndTime, endTimeist, endTimeist, false, ncid);
                                db.setTime_left(time_left);
                                db.setBidAmount(String.valueOf(bid));
                                db.setEstibot(nc.getEstibotValue());


                            }
                            db.setScheduled(true);

                            repo.save(db);
                            String time = timeft.format(now);
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist));
                            saveAction("Bid Scheduled","UI List",getUser(),db,notif,true,domain,getUserName());
                            logger.info("BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
                        }, threadPoolExecutor);
                        controller.putESTinDBSingle(cf);
                        return 0;
                    } else {
                        CompletableFuture.runAsync(() -> {
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                            saveAction("Bid Scheduled","UI List",getUser(),repo.findByNamecheapid(ncid),notif,false,domain,getUserName());

                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);
                        }, threadPoolExecutor);
                        return minNextBid;
                    }
                } else {
                    CompletableFuture.runAsync(() -> {
                        Date now = new Date();
                        String time = ft1.format(now);
                        telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as auction has ended");
                        Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as auction has ended"));
                        saveAction("Bid Scheduled","UI List",getUser(),repo.findByNamecheapid(ncid),notif,false,domain,getUserName());
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended");
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
                    telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                    saveAction("Bid Scheduled","UI List",getUser(),repo.findByNamecheapid(ncid),notif,false,domain,getUserName());
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                }, threadPoolExecutor);
                return 2;
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

    @GetMapping("/testbid")
    float schedulesingleoutbid(@RequestParam String domain, @RequestParam String ncid, @RequestParam Float bid,@RequestParam boolean changeAccount, @RequestParam String chat_title,@RequestParam Long user_id)
    {
        CompletableFuture<Estibot_Data> cf=controller.getEstibotDomain(domain);
        Float maxprice= bid;
        String bearer=getAccount(domain);
        boolean oldAccount=getAccountBoolean(domain);
        try {
            ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
            if(rn.getItems()!=null&&rn.getItems().get(0)!=null)
            {
                AuctionDetailNC nc = rn.getItems().get(0);
                if (nc.getStatus().equals("active")) {
                    float minNextBid = nc.getMinBid();

                    if (minNextBid <= maxprice) {
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
                        if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                            d.setSeconds(d.getSeconds() - bufferQ);
                            ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,true), d);
                            enterTaskMap(domain, task, "pb");
                        }
                        else if (d.getTime() - now.getTime() < 300000) {
                            d.setSeconds(d.getSeconds() - buffer);
                            ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,false), d);
                            enterTaskMap(domain, task, "pb",changeAccount?!oldAccount:oldAccount);

                        } else {
                            d.setMinutes(d.getMinutes() - 4);
                            ScheduledFuture task = taskScheduler.schedule(new PreCheck(ncid, domain, maxprice), d);
                            enterTaskMap(domain, task, "pc",changeAccount?!oldAccount:oldAccount);

                        }
                        Date finalD = d;
                        String finalEndTime = endTime;
                        CompletableFuture.runAsync(() -> {
                            String endTimeist = ft1.format(finalD);
                            String time_left = relTime(finalD);
                            Float currbid = nc.getPrice();
                            telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
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
                                db.setAccount(changeAccount?!oldAccount:oldAccount);
                                db.setEstibot(nc.getEstibotValue());
                                if(db.getMyLastBid().equals(currbid))
                                {
                                    Date now1 = new Date();
                                    now1.setSeconds(now1.getSeconds() + 45);
                                    db.setResult("Bid Placed");
                                    CheckOutbid checkOutbid = new CheckOutbid(currbid, maxprice, ncid, domain,oldAccount);
                                    ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now1, 30000);
                                    checkOutbid.setScheduledFuture(scheduledFuture);
                                    enterTaskMap(domain, scheduledFuture, "co");
                                }


                            } else {
                                db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", finalEndTime, endTimeist, endTimeist, false, ncid);
                                db.setTime_left(time_left);
                                db.setBidAmount(String.valueOf(bid));
                                db.setEstibot(nc.getEstibotValue());
                                db.setAccount(changeAccount?!oldAccount:oldAccount);

                            }
                            db.setScheduled(true);

                            repo.save(db);
                            String time = timeft.format(now);
                            Notification notif= notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist));
                            User user=getUser(user_id);
                            String userName=user.getEmail();
                            saveAction("Bid Scheduled","Bubble",chat_title,user,db,notif,true,domain,userName);
                            logger.info("BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
                        }, threadPoolExecutor);
                        controller.putESTinDBSingle(cf);
                        return 0;
                    } else {
                        CompletableFuture.runAsync(() -> {
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                            User user=getUser(user_id);
                            String userName=user.getEmail();
                            saveAction("Bid Scheduled","Bubble",chat_title,user,repo.findByNamecheapid(ncid),notif,false,domain,userName);

                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);
                        }, threadPoolExecutor);
                        return minNextBid;
                    }
                } else {
                    CompletableFuture.runAsync(() -> {
                        Date now = new Date();
                        String time = ft1.format(now);
                        telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as auction has ended");
                        Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as auction has ended"));
                        User user=getUser(user_id);
                        String userName=user.getEmail();
                        saveAction("Bid Scheduled","Bubble",chat_title,user,repo.findByNamecheapid(ncid),notif,false,domain,userName);
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended");
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
                    telegram.sendAlert(-1001763199668l, 1005l, "Namecheap: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                    User user=getUser(user_id);
                    String userName=user.getEmail();
                    saveAction("Bid Scheduled","Bubble",chat_title,user,repo.findByNamecheapid(ncid),notif,false,domain,userName);
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                }, threadPoolExecutor);
                return 2;
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



    @PostMapping("/bulkschedulenc")@PreAuthorize("hasAuthority('APPROLE_Bid_NC')")
    List<Integer> bulkschedule(@RequestBody List<List<String>> ddlist)
    {
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList1(ddlist);
        List<Integer> l= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();
            Float maxprice= Float.valueOf(ddlist.get(i).get(1));
            try {
                String bearer=getAccount(domain);
                boolean oldAccount=getAccountBoolean(domain);
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);

                if(rn.getItems()!=null&&rn.getItems().get(0)!=null)
                {AuctionDetailNC nc = rn.getItems().get(0);
                    if (nc.getStatus().equals("active")) {
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
                    Date now = new Date(); if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                    d.setSeconds(d.getSeconds() - bufferQ);
                    ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,true), d);
                    enterTaskMap(domain, task, "pb");
                }
                else if (d.getTime() - now.getTime() < 300000) {
                        d.setSeconds(d.getSeconds() - buffer);
                        ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,false), d);
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
                    telegram.sendAlert(-1001763199668l,1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
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
                        db.setScheduled(true);
                        if(db.getMyLastBid().equals(currbid))
                        {
                            Date now1 = new Date();
                            now1.setSeconds(now1.getSeconds() + 45);
                            db.setResult("Bid Placed");
                            CheckOutbid checkOutbid = new CheckOutbid(currbid, maxprice, ncid, domain,oldAccount);
                            ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now1, 30000);
                            checkOutbid.setScheduledFuture(scheduledFuture);
                            enterTaskMap(domain, scheduledFuture, "co");
                        }
                        repo.save(db);
                    } else {
                        db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", endTime, endTimeist, "", false, ncid);
                        db.setTime_left(time_left);
                        db.setBidAmount(ddlist.get(i).get(1));
                        db.setEstibot(nc.getEstibotValue());
                        db.setScheduled(true);

                        repo.save(db);
                    }
                    String time = timeft.format(now);
                    Notification notif=notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime()));
                    saveAction("Bid Scheduled","UI",getUser(),db,notif,true,domain,getUserName());
                    logger.info("BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime());
                }
                else
                {
                    Date now = new Date();
                    String time = ft1.format(now);
                    telegram.sendAlert(-930742733l,"Namecheap: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                    Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                    saveAction("Bid Scheduled","UI",getUser(),repo.findTopByDomain(domain),notif,false,domain,getUserName());
                    logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);

                }
                    } else {
                        CompletableFuture.runAsync(() -> {
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as auction has ended");
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as auction has ended"));
                            saveAction("Bid Scheduled","UI",getUser(),repo.findTopByDomain(domain),notif,false,domain,getUserName());
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended");
                        }, threadPoolExecutor);

                    }
                }
                else
                {
                    CompletableFuture.runAsync(() ->
                    {
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                        Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                        saveAction("Bid Scheduled","UI",getUser(),repo.findTopByDomain(domain),notif,false,domain,getUserName());
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    }, threadPoolExecutor);

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
        controller.putESTinDB(cf);
        l.add(a);
        l.add(n);
        return l;
    }
    /*@PostMapping("/bulkschedulenc1")
    List<Integer> bulkschedule1(@RequestBody List<List<String>> ddlist)
    {
        String bearer1="bearer cab3a5f74eee3c7a90027fa7a3081cd9CcawjwyrjfmyhQ5c+PfCADp9wDYnfd2ni6AozrVwtT93rjRaabhDbfp+mYQUhPCy";
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList1(ddlist);
        List<Integer> l= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();
            Float maxprice= Float.valueOf(ddlist.get(i).get(1));
            try {
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer1, domain);
                AuctionDetailNC nc = rn.getItems().get(0);
                if(nc!=null&&nc.getStatus()!=null)
                {
                    if (nc.getStatus().equals("active")) {
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
                            telegram.sendAlert(-1001763199668l,1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
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
                                db.setScheduled(true);

                                repo.save(db);
                            } else {
                                db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", endTime, endTimeist, "", false, ncid);
                                db.setTime_left(time_left);
                                db.setBidAmount(ddlist.get(i).get(1));
                                db.setEstibot(nc.getEstibotValue());
                                db.setScheduled(true);

                                repo.save(db);
                            }
                            String time = timeft.format(now);
                            notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime()));
                            logger.info("BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime());
                        }
                        else
                        {
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l,"Namecheap: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                            notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);

                        }
                    } else {
                        CompletableFuture.runAsync(() -> {
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as auction has ended");
                            notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as auction has ended"));
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended");
                        }, threadPoolExecutor);

                    }
                }
                else
                {
                    CompletableFuture.runAsync(() ->
                    {
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                        notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    }, threadPoolExecutor);

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
        controller.putESTinDB(cf);
        l.add(a);
        l.add(n);
        return l;
    }
*/
    BulkScheduleResponse bulkschedulebot(@RequestBody List<List<String>> ddlist, boolean account,Long user_id)
    {
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList1(ddlist);
        List<Integer> l= new ArrayList<>();
        BulkScheduleResponse res=null;
        String s="";
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            int l1=ddlist.get(i).size();
            String domain = ddlist.get(i).get(0).toLowerCase();
            Float maxprice= Float.valueOf(ddlist.get(i).get(1));
            try {
                String bearer=getAccount(domain);
                boolean oldAccount= getAccountBoolean(domain);
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
                if(rn.getItems()!=null&&rn.getItems().get(0)!=null)
                {AuctionDetailNC nc = rn.getItems().get(0);
                    if (nc.getStatus().equals("active")) {
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
                            if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                                d.setSeconds(d.getSeconds() - bufferQ);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,true), d);
                                enterTaskMap(domain, task, "pb");
                            }
                            else if (d.getTime() - now.getTime() < 300000) {
                                d.setSeconds(d.getSeconds() - buffer);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,false), d);
                                enterTaskMap(domain, task, "pb",account);

                            } else {
                                //d.setMinutes(d.getMinutes()-4);
                                Date d1 = new Date(d.getTime() - 270000);
                                ScheduledFuture task = taskScheduler.schedule(new PreCheck(ncid, domain, maxprice), d1);
                                enterTaskMap(domain, task, "pc",account);

                            }
                            a++;
                            String endTimeist = ft1.format(d);
                            String time_left = relTime(d);
                            telegram.sendAlert(-1001763199668l,1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
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
                                db.setScheduled(true);
                                db.setAccount(account);
                                if(db.getMyLastBid().equals(currbid))
                                {
                                    Date now1 = new Date();
                                    now1.setSeconds(now1.getSeconds() + 45);
                                    db.setResult("Bid Placed");
                                    CheckOutbid checkOutbid = new CheckOutbid(currbid, maxprice, ncid, domain,oldAccount);
                                    ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now1, 30000);
                                    checkOutbid.setScheduledFuture(scheduledFuture);
                                    enterTaskMap(domain, scheduledFuture, "co");
                                }
                            } else {
                                db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", endTime, endTimeist, "", false, ncid);
                                db.setTime_left(time_left);
                                db.setBidAmount(ddlist.get(i).get(1));
                                db.setEstibot(nc.getEstibotValue());
                                db.setScheduled(true);
                                db.setAccount(account);
                            }
                            List<String> list=ddlist.get(i);
                            if(list.size()>2)
                            {
                                if(list.size()==4)
                                {
                                    int fast=Integer.valueOf(list.get(3));
                                    if(fast>10)
                                    {
                                        db.setFastBidAmount(list.get(3));
                                        db.setFast_n(fastBidSetting.getFastN());
                                    }
                                    else {
                                        db.setFast_n(fast);
                                        db.setFastBidAmount(String.valueOf(fastBidSetting.getFastBidAmount()));
                                    }
                                }
                                else if(list.size()==5)
                                {
                                    int fast=Integer.valueOf(list.get(3));
                                    db.setFastBidAmount(list.get(4));
                                    db.setFast_n(fast);
                                }
                                else if(list.size()==2)
                                {
                                    db.setFast_n(fastBidSetting.getFastN());
                                    db.setFastBidAmount(String.valueOf(fastBidSetting.getFastBidAmount()));
                                }
                            }
                            repo.save(db);
                            String time = timeft.format(now);
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime()));
                            User user=getUser(user_id);
                            String userName=user.getEmail();
                            saveAction("Bid Scheduled","CPanel",user,db,notif,true,domain,userName);
                            logger.info("BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime());
                        }
                        else
                        {
                            String text="Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid;
                            s=s+text+"\n";
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l,"Namecheap: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                            User user=getUser(user_id);
                            String userName=user.getEmail();
                            saveAction("Bid Scheduled","CPanel",user,repo.findTopByDomain(domain),notif,false,domain,userName);
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);

                        }
                    } else {
                        String text="Bid NOT SCHEDULED for" + domain + " as auction has ended";
                        s=s+text+"\n";
                        CompletableFuture.runAsync(() -> {
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as auction has ended");
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as auction has ended"));
                            User user=getUser(user_id);
                            String userName=user.getEmail();
                            saveAction("Bid Scheduled","CPanel",user,repo.findTopByDomain(domain),notif,false,domain,userName);
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended");
                        }, threadPoolExecutor);

                    }
                }
                else
                {
                    CompletableFuture.runAsync(() ->
                    {
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                        Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                        User user=getUser(user_id);
                        String userName=user.getEmail();
                        saveAction("Bid Scheduled","CPanel",user,repo.findTopByDomain(domain),notif,false,domain,userName);
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    }, threadPoolExecutor);

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
        controller.putESTinDB(cf);
        l.add(a);
        l.add(n);
        res= new BulkScheduleResponse(l,s);
        return res;
    }

    BulkScheduleResponse bulkschedulebot2Accounts(List<List<String>> ddlist, Long user_id)
    {
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList1(ddlist);
        List<Integer> l= new ArrayList<>();
        BulkScheduleResponse res=null;
        String s="";
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            int l1=ddlist.get(i).size();
            boolean account=false;
            String domain = ddlist.get(i).get(0).toLowerCase();
            Float maxprice1= Float.valueOf(ddlist.get(i).get(1));
            Float maxprice=Float.valueOf(ddlist.get(i).get(2));
            int ba=1;
             if(maxprice1>maxprice)
            {
                float m=maxprice1;
                maxprice1=maxprice;
                maxprice=m;
                account=true;
                ba=2;
            }
            try {
                String bearer=getAccount(domain);
                boolean oldAccount= getAccountBoolean(domain);
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer, domain);
                if(rn.getItems()!=null&&rn.getItems().get(0)!=null)
                {AuctionDetailNC nc = rn.getItems().get(0);
                    if (nc.getStatus().equals("active")) {
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
                            if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                                d.setSeconds(d.getSeconds() - bufferQ);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, endTime,true), d);
                                enterTaskMap(domain, task, "pb");
                            }
                            else if (d.getTime() - now.getTime() < 300000) {
                                d.setSeconds(d.getSeconds() - buffer);
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, endTime,false), d);
                                enterTaskMap(domain, task, "pb",account);

                            } else {
                                //d.setMinutes(d.getMinutes()-4);
                                Date d1 = new Date(d.getTime() - 270000);
                                ScheduledFuture task = taskScheduler.schedule(new PreCheckB(ncid, domain, maxprice), d1);
                                enterTaskMap(domain, task, "pc",account);

                            }
                            a++;
                            String endTimeist = ft1.format(d);
                            String time_left = relTime(d);
                            telegram.sendAlert(-1001763199668l,1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
                            logger.info(endTimeist);
                            Float currbid = nc.getPrice();
                            Optional<DBdetails> op = Optional.ofNullable(repo.findByNamecheapid(ncid));
                            DBdetails db = null;

                            if (op.isPresent()) {
                                db = op.get();
                                db.setCurrbid(String.valueOf(currbid));
                                db.setBidAmount(String.valueOf(maxprice));
                                db.setResult("Bid Scheduled");
                                db.setEndTimepst(endTime);
                                db.setEndTimeist(endTimeist);
                                db.setTime_left(time_left);
                                db.setEstibot(nc.getEstibotValue());
                                db.setScheduled(true);
                                db.setAccount(account);
                                if(db.getMyLastBid().equals(currbid))
                                {
                                    Date now1 = new Date();
                                    now1.setSeconds(now1.getSeconds() + 45);
                                    db.setResult("Bid Placed");
                                    CheckOutbid checkOutbid = new CheckOutbid(currbid, maxprice, ncid, domain,oldAccount);
                                    ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now1, 30000);
                                    checkOutbid.setScheduledFuture(scheduledFuture);
                                    enterTaskMap(domain, scheduledFuture, "co");
                                }

                            } else {
                                db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", endTime, endTimeist, "", false, ncid);
                                db.setTime_left(time_left);

                                db.setBidAmount(String.valueOf(maxprice));
                                db.setEstibot(nc.getEstibotValue());
                                db.setScheduled(true);
                                db.setAccount(account);

                            }
                            db.setBothAccount(ba);
                            db.setPreBidAmount(String.valueOf(maxprice1));
                            List<String> list=ddlist.get(i);
                            if(list.size()>3)
                            {
                                if(list.size()==5)
                                {
                                    int fast=Integer.valueOf(list.get(4));
                                    if(fast>10)
                                    {
                                        db.setFastBidAmount(list.get(4));
                                        db.setFast_n(fastBidSetting.getFastN());
                                    }
                                    else {
                                        db.setFast_n(fast);
                                        db.setFastBidAmount(String.valueOf(fastBidSetting.getFastBidAmount()));
                                    }
                                }
                                else if(list.size()==6)
                                {
                                    int fast=Integer.valueOf(list.get(4));
                                    db.setFastBidAmount(list.get(5));
                                    db.setFast_n(fast);
                                }
                                else if(list.size()==3)
                                {
                                    db.setFast_n(fastBidSetting.getFastN());
                                    db.setFastBidAmount(String.valueOf(fastBidSetting.getFastBidAmount()));
                                }
                            }
                            repo.save(db);
                            String time = timeft.format(now);
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + db.getDomain() + " for price: " + maxprice1+","+maxprice + " at " + db.getBidplacetime()));
                            User user=getUser(user_id);String userName= user.getEmail();
                            saveAction("Bid Scheduled","CPanel",user,db,notif,true,domain,userName);
                            logger.info("BID SCHEDULED for domain: " + db.getDomain() + " for price: " + maxprice1+","+maxprice + " at " + db.getBidplacetime());
                        }
                        else
                        {
                            String text="Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid;
                            s=s+text+"\n";
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l,"Namecheap: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                            User user=getUser(user_id);String userName= user.getEmail();
                            saveAction("Bid Scheduled","CPanel",user,repo.findTopByDomain(domain),notif,false,domain,userName);
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);

                        }
                    } else {
                        String text="Bid NOT SCHEDULED for" + domain + " as auction has ended";
                        s=s+text+"\n";
                        CompletableFuture.runAsync(() -> {
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as auction has ended");
                            Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as auction has ended"));
                            User user=getUser(user_id);String userName= user.getEmail();
                            saveAction("Bid Scheduled","CPanel",user,repo.findTopByDomain(domain),notif,false,domain,userName);
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended");
                        }, threadPoolExecutor);

                    }
                }
                else
                {
                    CompletableFuture.runAsync(() ->
                    {
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                        Notification notif=notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                        User user=getUser(user_id);String userName= user.getEmail();
                        saveAction("Bid Scheduled","CPanel",user,repo.findTopByDomain(domain),notif,false,domain,userName);
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    }, threadPoolExecutor);

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
        controller.putESTinDB(cf);
        l.add(a);
        l.add(n);
        res= new BulkScheduleResponse(l,s);
        return res;
    }

    // String bearer1="bearer cab3a5f74eee3c7a90027fa7a3081cd9CcawjwyrjfmyhQ5c+PfCADp9wDYnfd2ni6AozrVwtT93rjRaabhDbfp+mYQUhPCy";

    /*BulkScheduleResponse bulkschedulebot1(@RequestBody List<List<String>> ddlist)
    {
        CompletableFuture<List<Estibot_Data>> cf=controller.getEstibotList1(ddlist);
        List<Integer> l= new ArrayList<>();
        BulkScheduleResponse res=null;
        String s="";
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            int l1=ddlist.get(i).size();
            String domain = ddlist.get(i).get(l1-2).toLowerCase();
            Float maxprice= Float.valueOf(ddlist.get(i).get(l1-1));
            try {
                ResponseAuctionDetailsNC rn = namecheapfeign.getAuctionDetails(bearer1, domain);
                AuctionDetailNC nc = rn.getItems().get(0);
                if(nc!=null&&nc.getStatus()!=null)
                {
                    if (nc.getStatus().equals("active")) {
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
                                ScheduledFuture task = taskScheduler.schedule(new PlaceBid1(ncid, maxprice, domain, endTime), d);
                                enterTaskMap(domain, task, "pb");

                            } else {
                                //d.setMinutes(d.getMinutes()-4);
                                Date d1 = new Date(d.getTime() - 270000);
                                ScheduledFuture task = taskScheduler.schedule(new PreCheck1(ncid, domain, maxprice), d1);
                                enterTaskMap(domain, task, "pc");

                            }
                            a++;
                            String endTimeist = ft1.format(d);
                            String time_left = relTime(d);
                            telegram.sendAlert(-1001763199668l,1005l, "Namecheap: BID SCHEDULED for domain: " + domain + " for max price: " + maxprice + " at " + endTimeist);
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
                                db.setScheduled(true);

                                repo.save(db);
                            } else {
                                db = new DBdetails(domain, null, "Namecheap", String.valueOf(currbid), null, nc.getAuctionType(), "Bid Scheduled", endTime, endTimeist, "", false, ncid);
                                db.setTime_left(time_left);
                                db.setBidAmount(ddlist.get(i).get(1));
                                db.setEstibot(nc.getEstibotValue());
                                db.setScheduled(true);

                                repo.save(db);
                            }
                            String time = timeft.format(now);
                            notifRepo.save(new Notification("Namecheap", time, "BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime()));
                            logger.info("BID SCHEDULED for domain: " + db.getDomain() + " for price: " + db.getBidAmount() + " at " + db.getBidplacetime());
                        }
                        else
                        {
                            String text="Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid;
                            s=s+text+"\n";
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l,"Namecheap: Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid);
                            notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as bid value is lower than accepted bid of " + minNextBid));
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextBid);

                        }
                    } else {
                        String text="Bid NOT SCHEDULED for" + domain + " as auction has ended";
                        s=s+text+"\n";
                        CompletableFuture.runAsync(() -> {
                            Date now = new Date();
                            String time = ft1.format(now);
                            telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for" + domain + " as auction has ended");
                            notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for" + domain + " as auction has ended"));
                            logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as auction has ended");
                        }, threadPoolExecutor);

                    }
                }
                else
                {
                    CompletableFuture.runAsync(() ->
                    {
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-930742733l, "Namecheap: Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                        notifRepo.save(new Notification("Namecheap", time, "Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended."));
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " because response is null, maybe auction has ended.");
                    }, threadPoolExecutor);

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
        controller.putESTinDB(cf);
        l.add(a);
        l.add(n);
        res= new BulkScheduleResponse(l,s);
        return res;
    }*/


    @PostMapping("/bulkbidnc")
    @PreAuthorize("hasAuthority('APPROLE_Bid_NC')")
    List<Integer> bulkbid(@RequestBody List<List<String>> ddlist)
    {
        CompletableFuture<List<Estibot_Data>> cf= controller.getEstibotList1(ddlist);
        List<Integer> l= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
        for(int i=0;i< ddlist.size();i++)
        {
            try {
                String domain = ddlist.get(i).get(0).toLowerCase();
                String bearer=getAccount(domain);
                boolean oldAccount=getAccountBoolean(domain);
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
                    Notification notif=notifRepo.save(new Notification("Namecheap",time,"INSTANT BID PLACED for " + domain + " at price " + bid.getMaxAmount() + " USD"));
                    saveAction("Bid Scheduled","UI",getUser(),db,notif,true,domain,getUserName());

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

                        Notification notif=notifRepo.save(new Notification("Namecheap: INSTANT BID NOT PLACED for " + domain + " at price " + bid.getMaxAmount() + " USD at " + new Date()));
                        saveAction("Bid Scheduled","UI",getUser(),db,notif,false,domain,getUserName());

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
        controller.putESTinDB(cf);
        l.add(a);
        l.add(n);
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

    @GetMapping("/getlivenc")
    List<Livencdb> getLive()
    {
        return liveNcRepo.findAllByOrderByEstibotValueDesc();//liveNcRepo.findByLiveTrueOrderByIddDesc();
    }

    @GetMapping("/getlivencupdated")
    List<Livencdb> getLiveUpdated()
    {
        refreshNCLive();
        return liveNcRepo.findAllByOrderByEstibotValueDesc();//liveNcRepo.findByLiveTrueOrderByIddDesc();
    }

    /*@GetMapping("/getplacenc/{id}")
    ResponsePlaceBidNc getplace(@PathVariable String id, @RequestParam Float bid1)
    {
        Bidnc bid= new Bidnc(bid1);
      return  namecheapfeign.placeBidnc(bearer,id,bid);
    }*/

    boolean healthCheck()
    {
        try
        {
        String bidCount="1_";
        Date now= new Date();
        Long t1= now.getTime()/1000;
        Float hours=4f;
       // Long t2=t1+hours.longValue()*3600;
        now.setHours(22);
        Long t2=now.getTime()/1000;
        //Long t2=1669905052l;
        String t=String.valueOf(t1)+"_"+String.valueOf(t2);
        //String t="1669908629_1669912229";
        logger.info(t);

        String bearer=getPrimaryAccount();
        ResponseLivedb rl= namecheapfeign.getAuctionDetailslive(bearer,bidCount,t,"end_time");
        if(rl.getItems().size()>0)
            return true;
        else return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    void refreshNCLive()
    {
        String bidCount="1_";
        Date now= new Date();
        String addtime=ft1.format(now);
        Long t1= now.getTime()/1000;
        Map<String,String> map1=new HashMap<>(map);
        if(now.getHours()<=16&&now.getMinutes()<=30)
        {
            now.setHours(16);now.setMinutes(30);
        }
        else
        {
            now.setDate(now.getDate()+1);now.setHours(16);now.setMinutes(30);
        }
        Long t2=now.getTime()/1000;
        //Long t2=1669905052l;
        String t=String.valueOf(t1)+"_"+String.valueOf(t2);
        //String t="1669908629_1669912229";
        logger.info(t);

        String bearer=getPrimaryAccount();
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

                    String id=lnc.getId();
                    String domain=lnc.getName();
                    float currbid=lnc.getPrice();
                    String endTime = lnc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d1 = null;
                    String time_left;
                    int est=lnc.getEstibotValue();
                    try {
                        d1 = parser.parse(endTime);
                        //endTimeist = ft1.format(d);
                        time_left = relTime(d1);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    lnc.setTime_left(time_left);
            lnc.setLive(true);
                lnc.setAddtime(addtime);
                logger.info(lnc.getIdd()+"");
                liveNcRepo.save(lnc);
                map1.remove(lnc.getId());

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
                                String id=lnc.getId();
                                String domain=lnc.getName();
                                float currbid=lnc.getPrice();
                                String endTime = lnc.getEndDate();
                                endTime = endTime.substring(0, endTime.length() - 5);
                                Date d1 = null;
                                String time_left;
                                int est=lnc.getEstibotValue();
                                try {
                                    d1 = parser.parse(endTime);
                                    //endTimeist = ft1.format(d);
                                    time_left = relTime(d1);

                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                    continue;
                                }
                        lnc.setTime_left(time_left);
                               lnc.setAddtime(addtime);
                        lnc.setLive(true);
                            liveNcRepo.save(lnc);
                            map1.remove(lnc.getId());
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
                                String id=lnc.getId();
                                String domain=lnc.getName();
                                float currbid=lnc.getPrice();
                                String endTime = lnc.getEndDate();
                                endTime = endTime.substring(0, endTime.length() - 5);
                                Date d1 = null;
                                String time_left;
                                int est=lnc.getEstibotValue();
                                try {
                                    d1 = parser.parse(endTime);
                                    //endTimeist = ft1.format(d);
                                    time_left = relTime(d1);

                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                    continue;
                                }
                                lnc.setTime_left(time_left);
                            lnc.setAddtime(addtime);
                        lnc.setLive(true);
                            liveNcRepo.save(lnc);
                            map1.remove(lnc.getId());

                    }
                }
            }
        }

        if(!map1.isEmpty())
            for (Map.Entry<String,String> mapElement : map1.entrySet())
            {
                String value = mapElement.getKey();
                try {
                    liveNcRepo.deleteByAuctionid(value);
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage());
                }
            }
    }

   @Scheduled(cron = "0 09 20 ? * *", zone = "IST")
    @GetMapping("/startlivenc")
    ResponseLivedb startLivenc()
    {
        logger.info("Starting NameCheap Live Service");
        LiveFilterSettings settings= settingsRepo.findById(1).get();
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
        String addtime=ft1.format(now);
        Long t1= now.getTime()/1000;
        Float hours=2f;
        Long t2=t1+hours.longValue()*3600;
        //Long t2=1669905052l;
        String t=String.valueOf(t1)+"_"+String.valueOf(t2);
        //String t="1669908629_1669912229";
        logger.info(t);

        String bearer=getPrimaryAccount();
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
                boolean highlight=isHighlight(lnc.getName(),lnc.getEstibotValue().intValue(),settings);
                lnc.setHighlight(highlight);
                if(highlight) {
                    String id=lnc.getId();
                    String domain=lnc.getName();
                    float currbid=lnc.getPrice();
                    String endTime = lnc.getEndDate();
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d1 = null;
                    String time_left;
                    int est=lnc.getEstibotValue();
                    try {
                        d1 = parser.parse(endTime);
                        //endTimeist = ft1.format(d);
                        time_left = relTime(d1);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        continue;
                    }
                    if(!taskmap.containsKey(domain.toLowerCase()))
                    sendLiveI(time_left,domain,lnc.getMinBid(),est,id);
                    else
                        sendLiveI(time_left,domain,lnc.getMinBid(),est,id,repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount());

                }
                lnc.setAddtime(addtime);
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
                            boolean highlight=isHighlight(lnc.getName(),lnc.getEstibotValue().intValue(),settings);
                            lnc.setHighlight(highlight);
                            if(highlight) {
                                String id=lnc.getId();
                                String domain=lnc.getName();
                                float currbid=lnc.getPrice();
                                String endTime = lnc.getEndDate();
                                endTime = endTime.substring(0, endTime.length() - 5);
                                Date d1 = null;
                                String time_left;
                                int est=lnc.getEstibotValue();
                                try {
                                    d1 = parser.parse(endTime);
                                    //endTimeist = ft1.format(d);
                                    time_left = relTime(d1);

                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                    continue;
                                }
                                if(!taskmap.containsKey(domain.toLowerCase()))
                                    sendLiveI(time_left,domain,lnc.getMinBid(),est,id);
                                else
                                    sendLiveI(time_left,domain,lnc.getMinBid(),est,id,repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount());


                            }
                            lnc.setAddtime(addtime);

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
                                boolean highlight=isHighlight(lnc.getName(),lnc.getEstibotValue().intValue(),settings);
                                lnc.setHighlight(highlight);
                                if(highlight) {
                                    String id=lnc.getId();
                                    String domain=lnc.getName();
                                    float currbid=lnc.getPrice();
                                    String endTime = lnc.getEndDate();
                                    endTime = endTime.substring(0, endTime.length() - 5);
                                    Date d1 = null;
                                    String time_left;
                                    int est=lnc.getEstibotValue();
                                    try {
                                        d1 = parser.parse(endTime);
                                        //endTimeist = ft1.format(d);
                                        time_left = relTime(d1);

                                    } catch (ParseException p) {
                                        logger.info(p.getMessage());
                                        continue;
                                    }

                                    if(!taskmap.containsKey(domain.toLowerCase()))
                                        sendLiveI(time_left,domain,lnc.getMinBid(),est,id);
                                    else
                                        sendLiveI(time_left,domain,lnc.getMinBid(),est,id,repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount());


                                }
                                lnc.setAddtime(addtime);

                                liveNcRepo.save(lnc);
                                map.put(lnc.getId(), lnc.getName().toLowerCase());
                            }
                        }
                    }
                }
            }
        try
        {
            sendHighlights(l);
            sendList(l);
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }
        //liveMaprepo.save(lm);
        logger.info("Started Namecheap Live Service");
        summary="";
        stopWatch.start();

        scheduledFuture= taskScheduler.scheduleWithFixedDelay(new DetectLiveNc(t),40000);

        now.setMinutes(now.getMinutes()+hours.intValue()*60);
        taskScheduler.schedule(new StopLive(scheduledFuture),now);
        return rl;
    }

    @GetMapping("/ch")
    boolean isHighlighcheck(@RequestParam String domain, @RequestParam Integer EST)
    {
        return isHighlight(domain,EST,settingsRepo.findById(1).get());
    }

    boolean isHighlight(String domain, Integer EST, LiveFilterSettings settings)
    {
        domain=domain.toLowerCase();
        String[] dom=domain.split("\\.",2);
        String sld=dom[0];String tld= dom[1];
        logger.info(tld);logger.info(sld);
        if(settings.getNoHyphens()&&domain.contains("-"))
            return false;

        if(settings.getNoNumbers()&&domain.matches(".*\\d.*"))
            return false;
        int l= domain.length();
        if(l<settings.getLowLength()||l>settings.getUpLength())
            return false;
        Map<String,Integer> extest=settings.getExtnEst();
        if(settings.getRestrictedExtns().contains(tld))
            return false;
        if(extest.containsKey(tld))
        {
            logger.info("true");
            if(EST< extest.get(tld))
                return false;
        }
        else if(settings.getNewExtnsSet().contains(tld)&&settings.getNewExtEsts()>EST)
        {return false;}
        else if(settings.getElseEsts()>EST)
        {return false;}
        return true;
    }
    boolean isHighlight(String domain, Integer EST, LiveFilterSettings settings,String[] dom)
    {
        domain=domain.toLowerCase();

        String sld=dom[0];String tld= dom[1];
        logger.info(tld);logger.info(sld);
        if(settings.getNoHyphens()&&domain.contains("-"))
            return false;

        if(settings.getNoNumbers()&&domain.matches(".*\\d.*"))
            return false;
        int l= domain.length();
        if(l<settings.getLowLength()||l>settings.getUpLength())
            return false;
        Map<String,Integer> extest=settings.getExtnEst();
        if(settings.getRestrictedExtns().contains(tld))
            return false;
        if(extest.containsKey(tld))
        {
            logger.info("true");
            if(EST< extest.get(tld))
                return false;
        }
        else if(settings.getNewExtnsSet().contains(tld)&&settings.getNewExtEsts()>EST)
        {return false;}
        else if(settings.getElseEsts()>EST)
        {return false;}
        return true;
    }

    @Autowired
    LiveFilterSettingsRepo settingsRepo;
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
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


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
        List<Livencdb> list=liveNcRepo.findAllByOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


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
        //           currbid, est, separators, space around separators
        int t= n+    6  +   6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namecheap Live List");
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
        List<Livencdb> list=liveNcRepo.findAllByOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
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
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendHighlights(int n)
    {
        //int n=32;
        //           currbid, est, separators, space around separators
        int t= n+    6  +   6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namecheap Initial Highlights");
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
        List<Livencdb> list=liveNcRepo.findByHighlightTrueOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,1017l,"<pre>"+s+"</pre>","HTML");

            l=l-d;
            s="";
        }

    }

    void sendInitialHighlights(int n)
    {
        //int n=32;
        //           currbid, est, separators, space around separators
        int t= n+    6  +   6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namecheap Initial Highlights");
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
        List<Livencdb> list=liveNcRepo.findByInitialListTrueAndHighlightTrueOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,1017l,"<pre>"+s+"</pre>","HTML");

            l=l-d;
            s="";
        }

    }



    void sendEndHighlights(int n)
    {
        //int n=32;
        //           currbid, est, separators, space around separators
        int t= n+    6  +   6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namecheap Next Day Highlights");
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
        List<Livencdb> list=liveNcRepo.findByEndListTrueAndHighlightTrueOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
            // System.out.println(s);
            telegram.sendAlert(-1001763199668l,1017l,"<pre>"+s+"</pre>","HTML");
            telegram.sendAlert(-1001763199668l,845l,"<pre>"+s+"</pre>","HTML");

            l=l-d;
            s="";
        }

    }
    void sendHighlights(long chat_id)
    {
        int n=25;
        //           currbid, est, separators, space around separators
        int t= n+    6  +   6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namecheap Highlights");
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
        List<Livencdb> list=liveNcRepo.findByHighlightTrueOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            //telegram.sendAlert(-1001763199668l,1017l,"<pre>"+s+"</pre>","HTML");

            l=l-d;
            s="";
        }

    }


    void sendListCurrent(long chat_id)
    {

        refreshNCLive();
        int n=25;
        //           currbid, est, separators, space around separators
        int t= n+    6  +   6  +  2   +      4;
        int d= 4096/t;
        d=d-6;
        String s=String.format("| %-"+(t-4)+"s |%n", "Namecheap Highlights");
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
        List<Livencdb> list=liveNcRepo.findByHighlightTrueOrderByEstibotValueDesc();
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Livencdb lnc = list.get(j);
                j++;
                s = s + String.format("%-"+n+"s | %-6.0f | %6d%n", lnc.getName(), lnc.getPrice(), lnc.getEstibotValue());


            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            //telegram.sendAlert(-1001763199668l,1017l,"<pre>"+s+"</pre>","HTML");

            l=l-d;
            s="";
        }

    }
    @GetMapping("/cancel/nc")@PreAuthorize("hasAuthority('APPROLE_Bid_NC')")
    void cancelBidweb(@RequestParam String domain,@RequestParam String ncid)
    {
        logger.info(domain+ncid);
        deleteTaskMap(domain);
        DBdetails db= repo.findByNamecheapid(ncid);
        db.setResult("Bid Cancelled");
        db.setScheduled(false);
        repo.save(db);
        Date now=new Date();
        String time = timeft.format(now);
        Notification notification=notifRepo.save(new Notification("Dynadot", time, "Bidding Cancelled of " + domain ));
        saveAction("Bid Cancelled","UI List",getUser(),db,notification,true,domain,getUserName());

    }

    void cancelBid(@RequestParam String domain,@RequestParam String ncid)
    {
        logger.info(domain+ncid);
        deleteTaskMap(domain);
        DBdetails db= repo.findByNamecheapid(ncid);
        db.setResult("Bid Cancelled");
        db.setScheduled(false);
        repo.save(db);

    }

  //  @Autowired
   // GoDaddyFeign goDaddyFeign;
    void sendSummary()
    {
        if(stopWatch.isStarted())
        {
            stopWatch.split();
            //long sp=stopWatch.getSplitTime()
        if(stopWatch.getSplitTime()>240000l)
        {
            if(summary!=null&&!summary.equals(""))
                telegram.sendAlert(-1001763199668l, 1017l,"Live Domains in Last 4-5 Minutes:\n\n"+summary);
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
             LiveFilterSettings settings= settingsRepo.findById(1).get();
            //Map<String,String> map= lm.getMapnc();
            // Map<String,String> map=liveMaprepo.getReferenceById(1).getMapnc();
             String bearer=getPrimaryAccount();
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
                     summary=summary+domain+"\n";

                     String endTime = item.getEndDate();
                     endTime = endTime.substring(0, endTime.length() - 5);
                     Date d = null;
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
                     Integer est=item.getEstibotValue();
                     String[] dom=domain.split("\\.",2);
                     boolean isHighlight=isHighlight(item.getName(),item.getEstibotValue().intValue(),settings,dom);
                     item.setHighlight(isHighlight);
                     if(isHighlight) {
                         if(dom[1].equalsIgnoreCase("com"))
                         {
                             String leads=getLeads(dom[0]);
                             if(!taskmap.containsKey(domain.toLowerCase()))
                             sendLive(time_left,domain,item.getMinBid(),est,id,leads);
                             else sendLive(time_left,domain,item.getMinBid(),est,id,leads,repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount());
                         }
                         else {
                             if(!taskmap.containsKey(domain.toLowerCase()))
                             sendLive(time_left,domain,item.getMinBid(),est,id);
                             else sendLive(time_left,domain,item.getMinBid(),repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount(),est,id);
                         }
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
                                 summary=summary+domain+"\n";

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
                                 int est = item1.getEstibotValue();
                                 String[] dom=domain.split("\\.",2);
                                 boolean isHighlight=isHighlight(item1.getName(),item1.getEstibotValue().intValue(),settings,dom);
                                 item1.setHighlight(isHighlight);
                                 if(isHighlight) {
                                     if(dom[1].equalsIgnoreCase("com"))
                                     {
                                         String leads=getLeads(dom[0]);
                                         if(!taskmap.containsKey(domain.toLowerCase()))
                                             sendLive(time_left,domain,item1.getMinBid(),est,id,leads);
                                         else sendLive(time_left,domain,item1.getMinBid(),est,id,leads,repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount());
                                     }
                                     else {
                                         if(!taskmap.containsKey(domain.toLowerCase()))
                                             sendLive(time_left,domain,item1.getMinBid(),est,id);
                                         else sendLive(time_left,domain,item1.getMinBid(),repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount(),est,id);
                                     }
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
                                 summary=summary+domain+"\n";

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
                                 int est = item1.getEstibotValue();
                                 String[] dom=domain.split("\\.",2);
                                 boolean isHighlight=isHighlight(item1.getName(),item1.getEstibotValue().intValue(),settings,dom);
                                 item1.setHighlight(isHighlight);
                                 if(isHighlight) {
                                     if(dom[1].equalsIgnoreCase("com"))
                                     {
                                         String leads=getLeads(dom[0]);
                                         if(!taskmap.containsKey(domain.toLowerCase()))
                                             sendLive(time_left,domain,item1.getMinBid(),est,id,leads);
                                         else sendLive(time_left,domain,item1.getMinBid(),est,id,leads,repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount());
                                     }
                                     else {
                                         if(!taskmap.containsKey(domain.toLowerCase()))
                                             sendLive(time_left,domain,item1.getMinBid(),est,id);
                                         else sendLive(time_left,domain,item1.getMinBid(),repo.findByDomainIgnoreCaseAndScheduledTrue(domain).getBidAmount(),est,id);
                                     }
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
             sendSummary();
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
            String bearer=getPrimaryAccount();
            scheduledFuture.cancel(false);
            map.clear();
            Optional<LiveMap> o= liveMaprepo.findById(1);
            LiveFilterSettings settings= settingsRepo.findById(1).get();
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
            String addtime= ft1.format(now);

            Float hours=24f;
            Long t1= now.getTime()/1000+22*3600;

            Long t2=t1+2*3600;
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
                lnc.setHighlight(isHighlight(lnc.getName(),lnc.getEstibotValue().intValue(),settings));
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
                            lnc.setHighlight(isHighlight(lnc.getName(),lnc.getEstibotValue().intValue(),settings));
                            lnc.setAddtime(addtime);
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
                            lnc.setHighlight(isHighlight(lnc.getName(),lnc.getEstibotValue().intValue(),settings));
                            lnc.setAddtime(addtime);

                            l=Math.max(l,lnc.getName().length());
                            liveNcRepo.save(lnc);
                            map1.put(items1.get(i).getId(),items1.get(i).getName().toLowerCase());
                        }
                    }
                }
            }
            stopWatch.reset();
            summary="";
            liveMaprepo.save(lm);
            sendEndHighlights(l);
            sendEndList(l);
        }
    }

    /*@GetMapping("/listbidsnc")
    ResponseListBids listBids()
    {
        return namecheapfeign.getBidList(bearer);
    }*/
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
            AuctionDetailNC detail=null;
            DBdetails db= repo.findByNamecheapid(ncid);
            String bearer=getAccount(db);
            try {
                 detail = namecheapfeignB.getAuctionDetailbyId(bearer, ncid, domain);
            }
            catch(Exception e)
            {
                db.setResult("API Error Fetch pc");
                repo.save(db);
                return;
            }
            float minbid= detail.getMinBid();
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            Float currbid = detail.getPrice();

            if(maxprice<currbid)
            {
               //notify

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
                sendOutbid("Outbid",time_left,domain,detail.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid,db.getAccount());

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
                    Date now= new Date();
                    ScheduledFuture task;
                    if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                        d.setSeconds(d.getSeconds() - bufferQ);
                        task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,true), d);
                    }
                    else {
                        d.setSeconds(d.getSeconds() - buffer);
                        task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,false), d);
                    }

                    updateTaskMap(domain,task,"pb");

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

        boolean account;
        ScheduledFuture scheduledFuture;

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public CheckOutbid(Float price, Float maxprice, String ncid, String domain, boolean account) {
            this.price = price;
            this.maxprice = maxprice;
            this.ncid = ncid;
            this.domain=domain;
            this.account=account;
        }
        public CheckOutbid(Float price, Float maxprice, String ncid, String domain) {
            this.price = price;
            this.maxprice = maxprice;
            this.ncid = ncid;
            this.domain=domain;
            this.account=taskmap.get(domain).isAccount();
        }

        @Override
        public void run()
        {
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            AuctionDetailNC detail=null;
            String bearer= accounts.get(account);
            DBdetails db= repo.findByNamecheapid(ncid);
            try{
             detail= namecheapfeignB.getAuctionDetailbyId(bearer,ncid,domain);
            }
            catch(Exception e)
            {
                db.setResult("API Error Fetch co");
                repo.save(db);
                return;
            }
            float pricenow = detail.getPrice();
            float minbid   = detail.getMinBid();
            String endTime = detail.getEndDate();
            String status= detail.getStatus();

            if(status.equals("active")) {

                if (pricenow > price) {
                    if (pricenow > maxprice)
                    {
                        //notify

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
                        sendOutbid("Outbid",time_left,domain,detail.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid,db.getAccount());

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
                            Date now= new Date();
                            ScheduledFuture task;
                            if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                                d.setSeconds(d.getSeconds() - bufferQ);
                                task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,true), d);
                            }
                            else {
                                d.setSeconds(d.getSeconds() - buffer);
                                task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, endTime,false), d);
                            }
                            updateTaskMap(domain,task,"pb");
                            DBdetails dBdetails= repo.findByNamecheapid(ncid);
                            dBdetails.setResult("Bid Placed And Scheduled");
                            repo.save(dBdetails);
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

                if(pricenow==price)
                {
                    DBdetails dBdetails= repo.findByNamecheapid(ncid);
                    dBdetails.setResult("Won");
                    dBdetails.setScheduled(false);
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
                    dBdetails.setScheduled(false);

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

    DBdetails getDBinTime(String ncid)
    {
       DBdetails db= repo.findByNamecheapid(ncid);
       if(db!=null)
           return db;
       else
       {
           try {
               Thread.sleep(500);
           }
           catch(InterruptedException i)
           {
               logger.info(i.getMessage());
           }
           return getDBinTime(ncid);
       }
    }


    @Autowired
    NamecheapfeignB namecheapfeignB;
    public class PlaceBid implements Runnable
    {
        String ncid,domain, timeId;
        Float maxprice;
        boolean q;

        public PlaceBid(String ncid, Float maxprice, String domain, String timeId, boolean q) {
            this.ncid = ncid;
            this.domain = domain;
            this.timeId = timeId;
            this.maxprice = maxprice;
            this.q = q;
        }

    /*    public PlaceBid(String ncid, Float maxprice, String domain, String timeId)
        {
            this.ncid=ncid;
            this.maxprice=maxprice;
            this.domain=domain;
            this.timeId=timeId;
            //this.service= new Service();
        }*/
        @Override
        public void run() {
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            DBdetails db= getDBinTime(ncid);
            AuctionDetailNC detail=null;
            String bearer=db!=null?getAccount(db):accounts.get(false);

            try{
                if(q)
                    detail= namecheapfeignBQ.getAuctionDetailbyId(bearer,ncid,domain.toLowerCase());
                else detail= namecheapfeignB.getAuctionDetailbyId(bearer,ncid,domain.toLowerCase());
            }
            catch(Exception e)
            {
                try {
                    bearer = accounts.get(!taskmap.get(domain).isAccount());
                    if(q)
                        detail= namecheapfeignBQ.getAuctionDetailbyId(bearer,ncid,domain.toLowerCase());
                    else detail= namecheapfeignB.getAuctionDetailbyId(bearer,ncid,domain.toLowerCase());
                    taskmap.get(domain).setAccount(!taskmap.get(domain).isAccount());
                    db.setAccount(!db.getAccount());
                    repo.save(db);
                }
                catch(Exception e1)
                {
                    db.setResult("API Error Fetch pb");
                    repo.save(db);
                    return;
                }
            }
            String timeId1= detail.getEndDate().substring(0,detail.getEndDate().length()-5);
            Float price= detail.getMinBid();
            Float pricee= detail.getPrice();

            if(!timeId.equals(timeId1))
            {
                if(pricee<=maxprice) {

                    try {
                        Date d = parser.parse(timeId1);
                        Date now= new Date();
                        ScheduledFuture task;
                        if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                            d.setSeconds(d.getSeconds() - bufferQ);
                            task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, timeId1,true), d);
                        }
                        else {
                            d.setSeconds(d.getSeconds() - buffer);
                            task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, timeId1,false), d);
                        }

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
                    sendOutbid("Outbid",time_left,domain,detail.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid,db.getAccount());

                    Date now= new Date();
                    String time= timeft.format(now);
                    notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + price ));
                    logger.info(time+": Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + price );

                }
            }
            else {

              /*  if(pricee.equals(db.getMyLastBid()))
                {

                }
                else
                {*/
                if (pricee <= maxprice) {
                    Bidnc bid = new Bidnc(price);
                    ResponsePlaceBidNc pb =null;
                    try{
                        if(q)
                            pb = namecheapfeignBQ.placeBidnc(bearer, ncid, bid,domain);
                        else
                     pb = namecheapfeignB.placeBidnc(bearer, ncid, bid,domain);
                }
            catch(Exception e)
                {
                    try {
                        bearer = accounts.get(!taskmap.get(domain).isAccount());
                        if(q)
                            pb = namecheapfeignBQ.placeBidnc(bearer, ncid, bid,domain);
                        else
                            pb = namecheapfeignB.placeBidnc(bearer, ncid, bid,domain);                        taskmap.get(domain).setAccount(!taskmap.get(domain).isAccount());
                        db.setAccount(!db.getAccount());
                        repo.save(db);
                    }
                    catch(Exception e1) {
                        db.setResult("API Error Bid pb");
                        repo.save(db);
                        return;
                    }
                }
                    //String domain= repo.findByNamecheapid(ncid).getDomain();
                    if (pb.getStatus().equals("processed")) {
                        Date d = new Date();
                        String time = timeft.format(d);
                        boolean scheduleCO=true;
                        telegram.sendAlert(-1001763199668l, 1004l, "Namecheap: Scheduled Bid PLACED for " + domain + " at price " + price + " USD");
                        notifRepo.save(new Notification("Namecheap", time, "Scheduled Bid PLACED for " + domain + " at price " + price + " USD"));
                        logger.info(time + ": Scheduled Bid Placed of domain: " + domain + " at price " + price + " USD");
                        db.setMyLastBid(price);
                        db.setIsBidPlaced(true);
                        if (pb.getLeadingBid()) {
                            Date now = d;
                            now.setSeconds(now.getSeconds() + 45);
                            CheckOutbid checkOutbid = new CheckOutbid(price, maxprice, ncid, domain);
                            ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now, 30000);
                            checkOutbid.setScheduledFuture(scheduledFuture);
                            updateTaskMap(domain, scheduledFuture, "co");
                            //DBdetails db = repo.findByNamecheapid(ncid);

                            db.setCurrbid(String.valueOf(pb.getAmount()));
                            if (pb.getAmount() > maxprice)
                                db.setBidAmount(String.valueOf(pb.getAmount()));
                            db.setResult("Bid Placed");
                            db.setFast_i(0);
                            sendWatchlist("Our Bid Placed","05m",domain,pb.getAmount(),db.getBidAmount(),db.getEstibot(),ncid);

                        } else {

                            if(db.getFastBid()&&(Float.valueOf(db.getFastBidAmount())>=price))
                            {
                                db.setFast_i(db.getFast_i()+1);
                                if(db.getFast_i()>db.getFast_n())
                                {
                                    telegram.sendAlert(-1001763199668l,1004l,"Namecheap: Started Fast Bidding on " + domain);
                                    notifRepo.save(new Notification("Namecheap",time,"Started Fast Bidding on " + domain));
                                    logger.info(time+": Started Fast Bidding on domain: " + domain);
                                    while(true)
                                    {
                                        try
                                        {
                                            Thread.sleep(1250);
                                        }
                                        catch(InterruptedException ie)
                                        {
                                            logger.info(ie.getMessage());
                                        }

                                        detail = namecheapfeignB.getAuctionDetailbyId(bearer, ncid,domain);
                                        price= detail.getMinBid();
                                        pricee= detail.getPrice();
                                        if (pricee <= maxprice) {
                                            bid = new Bidnc(price);
                                            try
                                            {
                                                Thread.sleep(1250);
                                            }
                                            catch(InterruptedException ie)
                                            {
                                                logger.info(ie.getMessage());
                                            }
                                            try{
                                                if(q)
                                                    pb = namecheapfeignBQ.placeBidnc(bearer, ncid, bid,domain);
                                                else
                                                    pb = namecheapfeignB.placeBidnc(bearer, ncid, bid,domain);
                                            }
                                            catch(Exception e)
                                            {
                                                try {
                                                    bearer = accounts.get(!taskmap.get(domain).isAccount());
                                                    if(q)
                                                        pb = namecheapfeignBQ.placeBidnc(bearer, ncid, bid,domain);
                                                    else
                                                        pb = namecheapfeignB.placeBidnc(bearer, ncid, bid,domain);                                                    taskmap.get(domain).setAccount(!taskmap.get(domain).isAccount());
                                                    db.setAccount(!db.getAccount());
                                                }
                                                catch(Exception e1) {
                                                    db.setResult("API Error Bid pb Fast");
                                                    db.setFast_i(0);
                                                    repo.save(db);
                                                    return;
                                                }
                                            }
                                            //String domain= repo.findByNamecheapid(ncid).getDomain();
                                            if (pb.getStatus().equals("processed")) {
                                                db.setMyLastBid(price);
                                                db.setIsBidPlaced(true);
                                                db.setCurrbid(String.valueOf(pb.getAmount()));
                                                if (pb.getAmount() > maxprice)
                                                    db.setBidAmount(String.valueOf(pb.getAmount()));
                                                db.setResult("Bid Placed");
                                                if (pb.getLeadingBid()) {
                                                    telegram.sendAlert(-1001763199668l, 1004l, "Namecheap: Stopped Fast Bidding on " + domain + " as we surpassed proxy at price: " + price);
                                                    notifRepo.save(new Notification("Namecheap", time, "Stopped Fast Bidding on " + domain + " as we surpassed proxy at price: " + price));
                                                    logger.info(time + ": Stopped  Fast Bidding on domain: " + domain + " as we surpassed proxy at price: " + price);
                                                    db.setFast_i(0);
                                                    break;
                                                }
                                            }
                                            else {

                                                db.setIsBidPlaced(false);
                                                db.setCurrbid(String.valueOf(pb.getAmount()));
                                                db.setFast_i(0);
                                                scheduleCO=false;
                                                //db.setBidAmount();
                                                db.setResult("Bid Not Placed");
                                                time = timeft.format(d);
                                                deleteTaskMap(domain);
                                                notifRepo.save(new Notification("Namecheap", time, "Scheduled Bid NOT PLACED for " + domain + " at price " + price));
                                                logger.info(time + ": Bid not placed of domain: " + domain + " at price " + price);
                                                break;
                                            }
                                        }
                                        else
                                        {
                                            String endTime = detail.getEndDate();
                                            db.setFast_i(0);
                                            db.setResult("Outbid");
                                            endTime = endTime.substring(0, endTime.length() - 5);
                                            d = new Date();

                                            //String endTimeist = "";
                                            String time_left = "";
                                            try {
                                                d = parser.parse(endTime);
                                                //endTimeist = ft1.format(d);
                                                time_left = relTime(d);

                                            } catch (ParseException p) {
                                                logger.info(p.getMessage());
                                            }

                                            Float currbid = detail.getPrice();
                                            Integer est = detail.getEstibotValue();
                                            sendOutbid("Outbid", time_left, domain, detail.getMinBid(), db.getBidAmount(), db.getEstibot(), ncid,db.getAccount());

                                            Date now = new Date();
                                            time = timeft.format(now);
                                            notifRepo.save(new Notification("Namecheap", time, "Domain: " + domain + " with our max price " + maxprice + " OUTBID at price " + currbid));
                                            logger.info(time + ": Namecheap: Domain: " + domain + " with our max price " + maxprice + " Outbid at price " + currbid);
                                            scheduleCO=false;
                                            break;
                                        }
                                    }
                                }
                            }
                            else{
                                AuctionDetailNC detail1 = namecheapfeignB.getAuctionDetailbyId(bearer, ncid,domain);
                            String timeId2 = detail1.getEndDate().substring(0, detail.getEndDate().length() - 5);
                            Float price1 = detail1.getMinBid();
                            Float pricee1 = detail1.getPrice();

                            if (pricee1 <= maxprice) {

                                try {
                                    Date d1 = parser.parse(timeId2);
                                    Date now= new Date();
                                    ScheduledFuture task;
                                    if (d1.getTime() - now.getTime() < (buffer+10)*1000) {
                                        d1.setSeconds(d1.getSeconds() - bufferQ);
                                         task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, timeId2,true), d);
                                    }
                                    else {
                                        d1.setSeconds(d1.getSeconds() - buffer);
                                         task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, timeId2,false), d);
                                    }
                                    /*d1.setSeconds(d1.getSeconds() - buffer);
                                    ScheduledFuture task = taskScheduler.schedule(new PlaceBid(ncid, maxprice, domain, timeId2), d1);
                                    Date now = new Date();*/
                                    time = timeft.format(now);
                                    String bidist = ft1.format(d1);
                                    telegram.sendAlert(-1001763199668l, 1004l, "Namecheap: Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " time " + bidist);
                                    notifRepo.save(new Notification("Namecheap", time, "Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " at time " + bidist));
                                    logger.info(time + ": Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " time " + bidist);
                                    updateTaskMap(domain, task, "pb");
                                   // DBdetails db = repo.findByNamecheapid(detail.getId());
                                    db.setResult("Bid Placed And Scheduled");
                                    repo.save(db);
                                    //DBdetails dBdetails= repo.findByNamecheapid(ncid);
                                    //dBdetails.setResult("Bid Placed And Scheduled");
                                    //repo.save(dBdetails);

                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                }
                            } else {
                                //notify
                                String endTime = detail1.getEndDate();

                                db.setResult("Outbid");
                                repo.save(db);
                                endTime = endTime.substring(0, endTime.length() - 5);
                                d = new Date();

                                //String endTimeist = "";
                                String time_left = "";
                                try {
                                    d = parser.parse(endTime);
                                    //endTimeist = ft1.format(d);
                                    time_left = relTime(d);

                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                }

                                Float currbid = detail1.getPrice();
                                Integer est = detail1.getEstibotValue();
                                sendOutbid("Outbid", time_left, domain, detail1.getMinBid(), db.getBidAmount(), db.getEstibot(), ncid,db.getAccount());

                                Date now = new Date();
                                time = timeft.format(now);
                                notifRepo.save(new Notification("Namecheap", time, "Domain: " + domain + " with our max price " + maxprice + " OUTBID at price " + currbid));
                                logger.info(time + ": Namecheap: Domain: " + domain + " with our max price " + maxprice + " Outbid at price " + currbid);

                            }
                            scheduleCO=false;
                            sendWatchlist("Our Bid Placed","05m",domain,price1,db.getBidAmount(),db.getEstibot(),ncid);

                            }
                        }
                        repo.save(db);
                        if(scheduleCO)
                        {
                            Date now = d;
                            now.setSeconds(now.getSeconds() + 45);
                            CheckOutbid checkOutbid = new CheckOutbid(price, maxprice, ncid, domain);
                            ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now, 30000);
                            checkOutbid.setScheduledFuture(scheduledFuture);
                            updateTaskMap(domain, scheduledFuture, "co");
                        }
                    } else {

                        db.setIsBidPlaced(false);
                        db.setCurrbid(String.valueOf(pb.getAmount()));
                        //db.setBidAmount();
                        db.setResult("Bid Not Placed");
                        Date d = new Date();
                        String time = timeft.format(d);
                        deleteTaskMap(domain);
                        notifRepo.save(new Notification("Namecheap", time, "Scheduled Bid NOT PLACED for " + domain + " at price " + price));
                        logger.info(time + ": Bid not placed of domain: " + domain + " at price " + price);

                        repo.save(db);
                    }
                } else {
                    //notify
                    String endTime = detail.getEndDate();
                    db.setResult("Outbid");
                    repo.save(db);
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = new Date();

                    //String endTimeist = "";
                    String time_left = "";
                    try {
                        d = parser.parse(endTime);
                        //endTimeist = ft1.format(d);
                        time_left = relTime(d);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }

                    Float currbid = detail.getPrice();
                    Integer est = detail.getEstibotValue();
                    sendOutbid("Outbid", time_left, domain, detail.getMinBid(), db.getBidAmount(), db.getEstibot(), ncid,db.getAccount());

                    Date now = new Date();
                    String time = timeft.format(now);
                    notifRepo.save(new Notification("Namecheap", time, "Domain: " + domain + " with our max price " + maxprice + " OUTBID at price " + price));
                    logger.info(time + ": Namecheap: Domain: " + domain + " with our max price " + maxprice + " Outbid at price " + price);

                }

            }
        }
    }
    public class PreCheckB implements Runnable
    {

        String ncid,domain;
        float maxprice;

        public PreCheckB(String ncid, String domain, Float maxprice)
        {
            this.ncid = ncid;
            this.domain = domain;
            this.maxprice = maxprice;
        }

        @Override
        public void run()
        {
            AuctionDetailNC detail=null;
            DBdetails db= repo.findByNamecheapid(ncid);
            String bearer=getAccount(db);
            try {
                detail = namecheapfeignB.getAuctionDetailbyId(bearer, ncid, domain);
            }
            catch(Exception e)
            {
                db.setResult("API Error Fetch pc");
                repo.save(db);
                return;
            }
            float minbid= detail.getMinBid();
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            Float currbid = detail.getPrice();

            if(maxprice<currbid)
            {
                //notify

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
                sendOutbid("Outbid",time_left,domain,detail.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid,db.getAccount());

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
                    Date now= new Date();
                    ScheduledFuture task;
                    if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                        d.setSeconds(d.getSeconds() - bufferQ);
                        task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, endTime,true), d);
                    }
                    else {
                        d.setSeconds(d.getSeconds() - buffer);
                        task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, endTime,false), d);
                    }

                    updateTaskMap(domain,task,"pb");

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
    public class CheckOutbidB implements Runnable
    {
        float price,maxprice;
        String ncid;
        String domain;

        boolean account;
        ScheduledFuture scheduledFuture;

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public CheckOutbidB(Float price, Float maxprice, String ncid, String domain, boolean account) {
            this.price = price;
            this.maxprice = maxprice;
            this.ncid = ncid;
            this.domain=domain;
            this.account=account;
        }
        public CheckOutbidB(Float price, Float maxprice, String ncid, String domain) {
            this.price = price;
            this.maxprice = maxprice;
            this.ncid = ncid;
            this.domain=domain;
            this.account=taskmap.get(domain).isAccount();
        }

        @Override
        public void run()
        {
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            AuctionDetailNC detail=null;
            String bearer= accounts.get(account);
            DBdetails db= repo.findByNamecheapid(ncid);
            try{
                detail= namecheapfeignB.getAuctionDetailbyId(bearer,ncid,domain);
            }
            catch(Exception e)
            {
                db.setResult("API Error Fetch co");
                repo.save(db);
                return;
            }
            float pricenow = detail.getPrice();
            float minbid   = detail.getMinBid();
            String endTime = detail.getEndDate();
            String status= detail.getStatus();

            if(status.equals("active")) {

                if (pricenow > price) {
                    if (pricenow > maxprice)
                    {
                        //notify

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
                        sendOutbid("Outbid",time_left,domain,detail.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid,db.getAccount());

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
                            Date now= new Date();
                            ScheduledFuture task;
                            if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                                d.setSeconds(d.getSeconds() - bufferQ);
                                task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, endTime,true), d);
                            }
                            else {
                                d.setSeconds(d.getSeconds() - buffer);
                                task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, endTime,false), d);
                            }

                            updateTaskMap(domain,task,"pb");
                            DBdetails dBdetails= repo.findByNamecheapid(ncid);
                            dBdetails.setResult("Bid Placed And Scheduled");
                            repo.save(dBdetails);

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

                if(pricenow==price)
                {
                    DBdetails dBdetails= repo.findByNamecheapid(ncid);
                    dBdetails.setResult("Won");
                    dBdetails.setScheduled(false);
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
                    dBdetails.setScheduled(false);

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


    public class PlaceBidB implements Runnable
    {



        String ncid,domain, timeId;
        Float maxprice;
        boolean q;
        public PlaceBidB(String ncid, Float maxprice,String domain, String timeId,boolean q)
        {
            this.ncid=ncid;
            this.maxprice=maxprice;
            this.domain=domain;
            this.timeId=timeId;
            this.q=q;
            //this.service= new Service();
        }
        @Override
        public void run() {
            SimpleDateFormat parser= parser();
            SimpleDateFormat ft1=ft1();
            DBdetails db= repo.findByNamecheapid(ncid);
            AuctionDetailNC detail=null;
            String bearer=getAccount(db);

            try{
                if(q)
                    detail= namecheapfeignBQ.getAuctionDetailbyId(bearer,ncid,domain.toLowerCase());
                else
                detail= namecheapfeignB.getAuctionDetailbyId(bearer,ncid,domain.toLowerCase());
            }
            catch(Exception e)
            {
                try {
                    bearer = accounts.get(!taskmap.get(domain).isAccount());
                    if(q)
                        detail= namecheapfeignBQ.getAuctionDetailbyId(bearer,ncid,domain.toLowerCase());
                    else
                        detail= namecheapfeignB.getAuctionDetailbyId(bearer,ncid,domain.toLowerCase());                    taskmap.get(domain).setAccount(!taskmap.get(domain).isAccount());
                    db.setAccount(!db.getAccount());
                    repo.save(db);
                }
                catch(Exception e1) {
                    db.setResult("API Error Fetch pb");
                    repo.save(db);
                    return;
                }
            }
            String timeId1= detail.getEndDate().substring(0,detail.getEndDate().length()-5);
            Float price= detail.getMinBid();
            Float pricee= detail.getPrice();

            if(!db.getAccountSwitched()&&price>Float.valueOf(db.getPreBidAmount()))
            {
                if(db.getBothAccount()==1)
                {
                    if (!db.getAccount())
                    {
                        taskmap.get(domain).setAccount(true);
                        db.setAccount(true);
                        bearer=accounts.get(true);
                    }
                }
                else if(db.getBothAccount()==2)
                {
                    if (db.getAccount())
                    {
                        taskmap.get(domain).setAccount(false);
                        db.setAccount(false);
                        bearer=accounts.get(false);
                    }
                }
                db.setAccountSwitched(true);
                repo.save(db);
            }

            if(!timeId.equals(timeId1))
            {
                if(pricee<=maxprice) {

                    try {
                        Date d = parser.parse(timeId1);
                        Date now= new Date();
                        ScheduledFuture task;
                        if (d.getTime() - now.getTime() < (buffer+10)*1000) {
                            d.setSeconds(d.getSeconds() - bufferQ);
                            task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, timeId1,true), d);
                        }
                        else {
                            d.setSeconds(d.getSeconds() - buffer);
                            task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, timeId1,false), d);
                        }
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
                    sendOutbid("Outbid",time_left,domain,detail.getMinBid(),db.getBidAmount(),db.getEstibot(),ncid,db.getAccount());

                    Date now= new Date();
                    String time= timeft.format(now);
                    notifRepo.save(new Notification("Namecheap",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + price ));
                    logger.info(time+": Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + price );

                }
            }
            else {

              /*  if(pricee.equals(db.getMyLastBid()))
                {

                }
                else
                {*/
                if (pricee <= maxprice) {
                    Bidnc bid = new Bidnc(price);
                    ResponsePlaceBidNc pb =null;
                    try{
                        if(q)
                            pb = namecheapfeignBQ.placeBidnc(bearer, ncid, bid,domain);
                        else
                        pb = namecheapfeignB.placeBidnc(bearer, ncid, bid,domain);
                    }
                    catch(Exception e)
                    {
                        try {
                            bearer = accounts.get(!taskmap.get(domain).isAccount());
                            pb = namecheapfeignB.placeBidnc(bearer, ncid, bid, domain);
                            taskmap.get(domain).setAccount(!taskmap.get(domain).isAccount());
                            db.setAccount(!db.getAccount());
                            repo.save(db);
                        }
                        catch(Exception e1) {
                            db.setResult("API Error Bid pb");
                            repo.save(db);
                            return;
                        }
                    }
                    //String domain= repo.findByNamecheapid(ncid).getDomain();
                    if (pb.getStatus().equals("processed"))
                    {
                        Date d = new Date();
                        String time = timeft.format(d);
                        telegram.sendAlert(-1001763199668l, 1004l, "Namecheap: Scheduled Bid PLACED for " + domain + " at price " + price + " USD");
                        notifRepo.save(new Notification("Namecheap", time, "Scheduled Bid PLACED for " + domain + " at price " + price + " USD"));
                        logger.info(time + ": Scheduled Bid Placed of domain: " + domain + " at price " + price + " USD");
                        db.setMyLastBid(price);
                        db.setIsBidPlaced(true);
                        boolean scheduleCO=true;
                        if (pb.getLeadingBid()) {
                            Date now = d;

                            //DBdetails db = repo.findByNamecheapid(ncid);

                            db.setCurrbid(String.valueOf(pb.getAmount()));
                            if (pb.getAmount() > maxprice)
                                db.setBidAmount(String.valueOf(pb.getAmount()));
                            db.setResult("Bid Placed");
                            db.setFast_i(0);
                        } else {
                            if(db.getFastBid()&&(Float.valueOf(db.getFastBidAmount())<=price))
                            {
                                db.setFast_i(db.getFast_i()+1);
                                if(db.getFast_i()>db.getFast_n())
                                {
                                    telegram.sendAlert(-1001763199668l,1004l,"Namecheap: Started Fast Bidding on " + domain);
                                    notifRepo.save(new Notification("Namecheap",time,"Started Fast Bidding on " + domain));
                                    logger.info(time+": Started Fast Bidding on domain: " + domain);
                                    while(true)
                                    {
                                        try
                                        {
                                            Thread.sleep(1250);
                                        }
                                        catch(InterruptedException ie)
                                        {
                                            logger.info(ie.getMessage());
                                        }

                                        detail = namecheapfeignB.getAuctionDetailbyId(bearer, ncid,domain);
                                        price= detail.getMinBid();
                                        pricee= detail.getPrice();
                                        if (pricee <= maxprice) {
                                            if(!db.getAccountSwitched()&&price>Float.valueOf(db.getPreBidAmount()))
                                            {
                                                if(db.getBothAccount()==1)
                                                {
                                                    if (!db.getAccount())
                                                    {
                                                        taskmap.get(domain).setAccount(true);
                                                        db.setAccount(true);
                                                        bearer=accounts.get(true);
                                                    }
                                                }
                                                else if(db.getBothAccount()==2)
                                                {
                                                    if (db.getAccount())
                                                    {
                                                        taskmap.get(domain).setAccount(false);
                                                        db.setAccount(false);
                                                        bearer=accounts.get(false);
                                                    }
                                                }
                                                db.setAccountSwitched(true);
                                            }
                                            bid = new Bidnc(price);
                                            try
                                            {
                                                Thread.sleep(1250);
                                            }
                                            catch(InterruptedException ie)
                                            {
                                                logger.info(ie.getMessage());
                                            }
                                            try{
                                                if(q)
                                                    pb = namecheapfeignBQ.placeBidnc(bearer, ncid, bid,domain);
                                                else
                                                    pb = namecheapfeignB.placeBidnc(bearer, ncid, bid,domain);
                                            }
                                            catch(Exception e)
                                            {
                                                try {
                                                    bearer = accounts.get(!taskmap.get(domain).isAccount());
                                                    if(q)
                                                        pb = namecheapfeignBQ.placeBidnc(bearer, ncid, bid,domain);
                                                    else
                                                        pb = namecheapfeignB.placeBidnc(bearer, ncid, bid,domain);                                                    taskmap.get(domain).setAccount(!taskmap.get(domain).isAccount());
                                                    db.setAccount(!db.getAccount());
                                                }
                                                catch(Exception e1) {
                                                    db.setResult("API Error Bid pb Fast");
                                                    db.setFast_i(0);
                                                    repo.save(db);
                                                    return;
                                                }
                                            }
                                            //String domain= repo.findByNamecheapid(ncid).getDomain();
                                            if (pb.getStatus().equals("processed")) {
                                                db.setMyLastBid(price);
                                                db.setIsBidPlaced(true);
                                                db.setCurrbid(String.valueOf(pb.getAmount()));
                                                if (pb.getAmount() > maxprice)
                                                    db.setBidAmount(String.valueOf(pb.getAmount()));
                                                db.setResult("Bid Placed");
                                                if (pb.getLeadingBid()) {
                                                    telegram.sendAlert(-1001763199668l, 1004l, "Namecheap: Stopped Fast Bidding on " + domain + " as we surpassed proxy at price: " + price);
                                                    notifRepo.save(new Notification("Namecheap", time, "Stopped Fast Bidding on " + domain + " as we surpassed proxy at price: " + price));
                                                    logger.info(time + ": Stopped  Fast Bidding on domain: " + domain + " as we surpassed proxy at price: " + price);
                                                    db.setFast_i(0);
                                                    break;
                                                }
                                            }
                                            else {

                                                db.setIsBidPlaced(false);
                                                db.setCurrbid(String.valueOf(pb.getAmount()));
                                                db.setFast_i(0);
                                                scheduleCO=false;
                                                //db.setBidAmount();
                                                db.setResult("Bid Not Placed");
                                                time = timeft.format(d);
                                                deleteTaskMap(domain);
                                                notifRepo.save(new Notification("Namecheap", time, "Scheduled Bid NOT PLACED for " + domain + " at price " + price));
                                                logger.info(time + ": Bid not placed of domain: " + domain + " at price " + price);
                                                break;
                                            }
                                        }
                                        else
                                        {
                                            String endTime = detail.getEndDate();
                                            db.setFast_i(0);
                                            db.setResult("Outbid");
                                            endTime = endTime.substring(0, endTime.length() - 5);
                                            d = new Date();

                                            //String endTimeist = "";
                                            String time_left = "";
                                            try {
                                                d = parser.parse(endTime);
                                                //endTimeist = ft1.format(d);
                                                time_left = relTime(d);

                                            } catch (ParseException p) {
                                                logger.info(p.getMessage());
                                            }

                                            Float currbid = detail.getPrice();
                                            Integer est = detail.getEstibotValue();
                                            sendOutbid("Outbid", time_left, domain, detail.getMinBid(), db.getBidAmount(), db.getEstibot(), ncid,db.getAccount());

                                            Date now = new Date();
                                            time = timeft.format(now);
                                            notifRepo.save(new Notification("Namecheap", time, "Domain: " + domain + " with our max price " + maxprice + " OUTBID at price " + currbid));
                                            logger.info(time + ": Namecheap: Domain: " + domain + " with our max price " + maxprice + " Outbid at price " + currbid);
                                            scheduleCO=false;
                                            break;
                                        }
                                    }
                                }
                            }
                            else{
                            AuctionDetailNC detail1 = namecheapfeignB.getAuctionDetailbyId(bearer, ncid, domain);
                            String timeId2 = detail1.getEndDate().substring(0, detail.getEndDate().length() - 5);
                            Float price1 = detail1.getMinBid();
                            Float pricee1 = detail1.getPrice();

                            if (pricee1 <= maxprice) {

                                try {
                                    Date d1 = parser.parse(timeId2);
                                    Date now = new Date();
                                    ScheduledFuture task;
                                    if (d.getTime() - now.getTime() < (buffer + 10) * 1000) {
                                        d.setSeconds(d.getSeconds() - bufferQ);
                                        task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, timeId1, true), d);
                                    } else {
                                        d.setSeconds(d.getSeconds() - buffer);
                                        task = taskScheduler.schedule(new PlaceBidB(ncid, maxprice, domain, timeId1, false), d);
                                    }
                                    time = timeft.format(now);
                                    String bidist = ft1.format(d1);
                                    telegram.sendAlert(-1001763199668l, 1004l, "Namecheap: Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " time " + bidist);
                                    notifRepo.save(new Notification("Namecheap", time, "Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " at time " + bidist));
                                    logger.info(time + ": Outbid by Proxy, Bid SCHEDULED for " + domain + " at price " + detail1.getPrice() + " time " + bidist);
                                    updateTaskMap(domain, task, "pb");
                                    // DBdetails db = repo.findByNamecheapid(detail.getId());
                                    db.setResult("Bid Placed And Scheduled");
                                    //DBdetails dBdetails= repo.findByNamecheapid(ncid);
                                    //dBdetails.setResult("Bid Placed And Scheduled");
                                    //repo.save(dBdetails);
                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                }
                            } else {
                                //notify
                                String endTime = detail1.getEndDate();

                                db.setResult("Outbid");
                                endTime = endTime.substring(0, endTime.length() - 5);
                                d = new Date();

                                //String endTimeist = "";
                                String time_left = "";
                                try {
                                    d = parser.parse(endTime);
                                    //endTimeist = ft1.format(d);
                                    time_left = relTime(d);

                                } catch (ParseException p) {
                                    logger.info(p.getMessage());
                                }

                                Float currbid = detail1.getPrice();
                                Integer est = detail1.getEstibotValue();
                                sendOutbid("Outbid", time_left, domain, detail1.getMinBid(), db.getBidAmount(), db.getEstibot(), ncid, db.getAccount());

                                Date now = new Date();
                                time = timeft.format(now);
                                notifRepo.save(new Notification("Namecheap", time, "Domain: " + domain + " with our max price " + maxprice + " OUTBID at price " + currbid));
                                logger.info(time + ": Namecheap: Domain: " + domain + " with our max price " + maxprice + " Outbid at price " + currbid);

                            }
                            scheduleCO=false;
                        }
                        }
                        if(scheduleCO)
                        {
                            Date now=new Date();
                            now.setSeconds(now.getSeconds() + 45);
                            CheckOutbidB checkOutbid = new CheckOutbidB(price, maxprice, ncid, domain);
                            ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now, 30000);
                            checkOutbid.setScheduledFuture(scheduledFuture);
                            updateTaskMap(domain, scheduledFuture, "co");
                        }
                        repo.save(db);
                    } else {

                        db.setIsBidPlaced(false);
                        db.setCurrbid(String.valueOf(pb.getAmount()));
                        //db.setBidAmount();
                        db.setResult("Bid Not Placed");
                        Date d = new Date();
                        String time = timeft.format(d);
                        deleteTaskMap(domain);
                        notifRepo.save(new Notification("Namecheap", time, "Scheduled Bid NOT PLACED for " + domain + " at price " + price));
                        logger.info(time + ": Bid not placed of domain: " + domain + " at price " + price);

                        repo.save(db);
                    }
                } else {
                    //notify
                    String endTime = detail.getEndDate();
                    db.setResult("Outbid");
                    repo.save(db);
                    endTime = endTime.substring(0, endTime.length() - 5);
                    Date d = new Date();

                    //String endTimeist = "";
                    String time_left = "";
                    try {
                        d = parser.parse(endTime);
                        //endTimeist = ft1.format(d);
                        time_left = relTime(d);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }

                    Float currbid = detail.getPrice();
                    Integer est = detail.getEstibotValue();
                    sendOutbid("Outbid", time_left, domain, detail.getMinBid(), db.getBidAmount(), db.getEstibot(), ncid,db.getAccount());

                    Date now = new Date();
                    String time = timeft.format(now);
                    notifRepo.save(new Notification("Namecheap", time, "Domain: " + domain + " with our max price " + maxprice + " OUTBID at price " + price));
                    logger.info(time + ": Namecheap: Domain: " + domain + " with our max price " + maxprice + " Outbid at price " + price);

                }

            }
        }
    }

}
