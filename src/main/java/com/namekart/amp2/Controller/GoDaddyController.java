package com.namekart.amp2.Controller;

import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.Entity.FetchReq;
import com.namekart.amp2.Entity.LiveMap;
import com.namekart.amp2.Entity.Notification;
import com.namekart.amp2.Feign.GoDaddyFeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.GoDaddyEntities.*;
import com.namekart.amp2.GoDaddySoapClient;
import com.namekart.amp2.GoDaddySoapClient1;
import com.namekart.amp2.NamecheapEntity.Livencdb;
import com.namekart.amp2.Repository.*;
import com.namekart.amp2.Status;
import com.namekart.amp2.TelegramEntities.InlineKeyboardButton;
import com.namekart.amp2.TelegramEntities.InlineKeyboardMarkup;
import com.namekart.amp2.TelegramEntities.SendMessage;
import com.namekart.amp2.stub.GetAuctionDetailsResponse;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class GoDaddyController {
    Logger logger= Logger.getLogger("GoDaddy");

    SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    SimpleDateFormat ftr = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    TimeZone pst = TimeZone.getTimeZone("PST");
    TimeZone ist = TimeZone.getTimeZone("IST");
    String Authorization= "sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm";
    SimpleDateFormat timeft = new SimpleDateFormat("dd/MM HH:mm");

    Set<String> map;
    Map<String,String> mapc ;

    Map<String,Integer> mapt ;

    public GoDaddyController()
    {
        ft.setTimeZone(pst);
        ft1.setTimeZone(ist);
        timeft.setTimeZone(ist);
        taskmap=new ConcurrentHashMap<>();
        map= new HashSet<>();
        mapc = new HashMap<>();
        mapt = new HashMap<>();
        mapc.put("50","40");
        mapc.put("40","30");
        mapc.put("30","11");
        mapc.put("11","5");
        mapt.put("50",5);
        mapt.put("40",4);
        mapt.put("30",3);
        mapt.put("11",2);
        mapt.put("5",1);

    }

    @Autowired
    MyRepo myRepo;

    @Autowired
    LiveGDrepo liveGDrepo;

    @Autowired
    LiveMaprepo liveMaprepo;
    @Autowired
    Telegram telegram;
    @Autowired
    ThreadPoolTaskScheduler taskScheduler;
    @Autowired
    AsyncCalss asyncCalss;

    @Autowired
    NotifRepo notifRepo;

    @Autowired
    GoDaddyFeign goDaddyFeign;

    @Autowired
    Closeoutrepo closeoutrepo;
    @Autowired
    GoDaddySoapClient goDaddySoapClient;

    @Autowired
    @Qualifier(value = "workStealingPool")
    ForkJoinPool threadPoolExecutor;

    @Autowired
    GoDaddySoapClient1 goDaddySoapClient1;
    Map<String, Status> taskmap;

    Map<String, Status> taskmapc;


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

    @PostConstruct
    void scheduleCloseOutsonRestart()
    {
        List<Closeoutdb> list= closeoutrepo.findByStatusOrStatus("Closeout Scheduled","Closeout Recheck Scheduled");
        for(int i=0;i<list.size();i++)
        {
            Closeoutdb db= list.get(i);
            scheduleCloseoutSingle(db.getDomain(),db.getOurPrice());
        }
    }

    class GDrComparator implements Comparator<DBdetails>{

        // Overriding compare()method of Comparator
        // for descending order of cgpa
        public int compare(DBdetails d1, DBdetails d2) {
            String e1=d1.getEndTimeist(), e2=d2.getEndTimeist();
            if (e1.compareTo(e2)<0)
                return 1;
            else if (e1.compareTo(e2)>0)
                return -1;
            return 0;
        }
    }

    @GetMapping("/getgdv/{domain}")
    GDAppraisalResp getGDV(@PathVariable String domain)
    {
        return goDaddyFeign.getGDV(Authorization,domain);
    }

    @PostMapping("/getgdvs")
    List<GDAppraisalResp> getGDVs(@RequestBody List<String> domains)
    {
        List<GDAppraisalResp> list= new ArrayList<>();
        int n= domains.size();
        logger.info(""+n);
       // if(n<2100)
        for(int i=0;i<n;i++)
        {

            String domain= domains.get(i).toLowerCase();
            try
            {
                GDAppraisalResp resp= goDaddyFeign.getGDV(Authorization,domain);

                list.add(resp);
                logger.info(""+(i+1));
            }
            catch(Exception e)
            {
               logger.info(e.getMessage());

            }
        }
        return list;
    }

    @GetMapping("/getsoap")
    GetAuctionsDetailRes getAuc(@RequestParam String domain)
    {
        GetAuctionDetailsResponse g= goDaddySoapClient.getAuctionDetails(domain);
        String xmlString= g.getGetAuctionDetailsResult();
        JAXBContext jaxbContext;
        GetAuctionsDetailRes res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

             res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
logger.info(res.getAuctionEndTime());
            //System.out.println(employee);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return res;
    }



    @GetMapping("/try")
   // @Async("threadPoolTaskScheduler")
    public void try1()

    {taskScheduler.scheduleAtFixedRate(asyncCalss::try2,100);
    }

    @GetMapping("/getlivegd")
    List<Lauction> getLive()
    {
        return liveGDrepo.findByLiveTrueOrderByIddDesc();
    }

    @GetMapping("/purchase")
    PlaceBid purchase(@RequestParam String domain, @RequestParam String price)
    {
        PlaceBid p = goDaddySoapClient.purchase(domain, price);
        return p;
    }

    @Async
    CompletableFuture<Boolean> refreshscheduledbids()
  {
      List<DBdetails> list= myRepo.findByPlatformAndResultOrResultOrResultOrResult("GoDaddy", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
      if(list==null||list.size()==0)
          return CompletableFuture.completedFuture(true);
      for(int i=0;i< list.size();i++)
     {
         DBdetails dBdetails = list.get(i);
         String domain = dBdetails.getDomain();
         try {
         GetAuctionsDetailRes res = goDaddySoapClient.getAuctionDetail(domain);
         if (res.getIsValid().equals("True")) {
             String endTimepst = res.getAuctionEndTime();
             String endTime = endTimepst.substring(0, 19);
             logger.info(endTime);
             Date d = null;
             try {
                 d = ft.parse(endTime);
                 System.out.println(d);
             } catch (ParseException p) {
                 logger.info(p.getMessage());
                 //continue;
             }
             String timeLeft = relTime(d);
             String endTimeist = ft1.format(d);
             String price1= res.getPrice().substring(1,res.getPrice().length());
             price1= price1.replace(",","");
             int pricei= Integer.valueOf(price1);
             if(pricei>Integer.valueOf(dBdetails.getBidAmount()))
                 dBdetails.setResult("Outbid");
             dBdetails.setTime_left(timeLeft);
             dBdetails.setEndTimepst(endTimepst);
             dBdetails.setEndTimeist(endTimeist);
             dBdetails.setCurrbid(price1);
             dBdetails.setAuctiontype(res.getAuctionModel());
             myRepo.save(dBdetails);
         } else {
             logger.info("Could not fetch scheduled bid info for domain: " + domain+" with message: "+res.getIsValid());
             telegram.sendAlert(-834797664L, "Could not fetch scheduled bid info for domain: " + domain);
             dBdetails.setResult("");
             myRepo.save(dBdetails);
         }
     }
     catch(Exception e)
     {
         logger.info("Could not fetch scheduled bid info for domain: " + domain+" with exception: "+e.getMessage());
         telegram.sendAlert(-834797664L, "Could not fetch scheduled bid info for domain: " + domain+" with exception: "+e.getMessage());
     }
     }
return CompletableFuture.completedFuture(true);
  }

  @PostMapping("/bulkfetchgodaddy")
  List<DBdetails> bulkfetch(@RequestBody FetchReq body)
  {
List<String> ddlist= body.getDomains();
Boolean watch= body.getWatch();
      List<DBdetails> list= new ArrayList<>();
      int n= ddlist.size();
      for(int i=0;i<n;i++)
      {
          String domain= ddlist.get(i).toLowerCase();
          try {
              GetAuctionsDetailRes res = goDaddySoapClient.getAuctionDetail(domain);
              if (res.getIsValid().equals("True")) {
                  String endTimepst = res.getAuctionEndTime();
                  String endTime = endTimepst.substring(0, 19);

                  logger.info(endTime);
                  Date d = null;
                  try {
                      d = ft.parse(endTime);
                      System.out.println(d);
                  } catch (ParseException p) {
                      logger.info(p.getMessage());
                      //continue;
                  }

                  String timeLeft = relTime(d);
                  String endTimeist = ft1.format(d);
                  Optional<DBdetails> op = Optional.ofNullable(myRepo.findByPlatformAndDomain("GoDaddy", domain));
                  DBdetails dBdetails = null;
                  if (!op.isPresent()) {
                      dBdetails = new DBdetails(domain, "GoDaddy", formatPrice(res.getPrice()), null, timeLeft, null, null, res.getAuctionModel(), "", "", endTimepst, endTimeist, "", false);
                  } else {
                      dBdetails = op.get();
                      dBdetails.setTime_left(timeLeft);
                      dBdetails.setEndTimepst(endTimepst);
                      dBdetails.setCurrbid(formatPrice(res.getPrice()));
                      dBdetails.setEndTimeist(endTimeist);
                      dBdetails.setAuctiontype(res.getAuctionModel());
                     // dBdetails.setGdv(res.getValuationPrice());

                  }
                  //logger.info(res.getPrice());

                  //logger.info(dBdetails.getCurrbid());
                  if(watch)
                  {dBdetails.setWatchlist(true);
                      String text = "GoDaddy\n\n" + domain + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + dBdetails.getCurrbid() //+ "\n \nGDV: " +gdv
                              +" \n\nLink: " +"https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck="+domain ;
                      List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                      row.add(new InlineKeyboardButton("Bid","b gd"+" "+domain+" "+domain+" "+dBdetails.getCurrbid()));
                      row.add(new InlineKeyboardButton("Remove","rw gd "+domain+" "+domain));
                      List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                      rows.add(row);
                      InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                      Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                              ,text,inlineKeyboardMarkup));}
                  myRepo.save(dBdetails);
                  list.add(dBdetails);
              } else {
                  Date now= new Date();
                  String time = timeft.format(now);
                  notifRepo.save(new Notification("GoDaddy",time,"Domain details NOT FETCHED for " + domain + " with error: " + res.getMessage()));
                  logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + res.getMessage());
              }
          }
          catch(Exception e)
          {
              Date now= new Date();
              String time = timeft.format(now);
              notifRepo.save(new Notification("GoDaddy",time,"Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage()));
              logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
          }
      }
      return list;
  }

  @GetMapping("/testgd1")
    void testgd()
    {
        for(int i=0;i<75;i++)
        {
           GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail("099.cc");
            logger.info(res.getIsValid()+ res.getMessage()+i);
        }
    }

    @PostMapping("/bulkbidgodaddy")
    List<Integer> bulkbid(@RequestBody List<ArrayList<String>> ddlist)
    {

        List<Integer> list= new ArrayList<>();
        int n=ddlist.size();
        int a=0;
        for(int i=0;i<n;i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();
            String price = ddlist.get(i).get(1);
            try {
                PlaceBid place = goDaddySoapClient.purchase(domain, price);
                GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);

                String endTimepst= res.getAuctionEndTime();
                String endTime= endTimepst.substring(0,19);

                logger.info(endTime);
                Date d=null;
                try {
                    d=ft.parse(endTime);
                    System.out.println(d);
                }
                catch(ParseException p)
                {
                    logger.info(p.getMessage());
                    //continue;
                }
                String timeLeft= relTime(d);
                String endTimeist= ft1.format(d);
                Date now = new Date();
                String bidplacetime = ft1.format(now);
                Optional<DBdetails> op = Optional.ofNullable(myRepo.findByPlatformAndDomain("GoDaddy",domain));
                DBdetails dBdetails=null;
                if(!op.isPresent())
                {
                    dBdetails= new DBdetails(domain,"GoDaddy",res.getPrice(),null,timeLeft,null,null,res.getAuctionModel(),price,"",endTimepst,endTimeist,bidplacetime,false);
                }
                else
                {
                    dBdetails=op.get();
                    dBdetails.setBidAmount(price);
                    dBdetails.setTime_left(timeLeft);
                    dBdetails.setEndTimepst(endTimepst);
                    dBdetails.setCurrbid(formatPrice(res.getPrice()));
                    dBdetails.setEndTimeist(endTimeist);
                    dBdetails.setAuctiontype(res.getAuctionModel());
                    dBdetails.setBidplacetime(bidplacetime);
                }
                String time= timeft.format(now);
                if(place.getIsValid().equals("True"))
                {
                    a++;
                    dBdetails.setResult("Bid Placed");
                    dBdetails.setIsBidPlaced(true);
                    //telegram.sendAlert(-1001763199668l,"Dropcatch: BID SCHEDULED for domain: "+domain+ " for max price: "+bid+" at "+endTimeist);

                    notifRepo.save(new Notification("GoDaddy",time,"Instant Bid PLACED for " + domain + " at price " + price + " USD at " + new Date()));
                    logger.info("Instant Bid Placed of domain: " + domain);
                    telegram.sendAlert(-1001763199668l,1005l,"GoDaddy: Instant Bid Placed of domain: " + domain);
                }
                else
                {
                    dBdetails.setResult("Bid Not Placed");
                    dBdetails.setIsBidPlaced(false);
                    notifRepo.save(new Notification("GoDaddy",time,"Instant Bid NOT PLACED for " + domain + " at price " + price + " USD at " + new Date()));
                    logger.info("Instant Bid Not Placed of domain: " + domain);
                    telegram.sendAlert(-834797664L,"GoDaddy: Instant Bid Not Placed of domain: " + domain);
                }
                myRepo.save(dBdetails);
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
                telegram.sendAlert(-834797664L,e.getMessage());
            }

        }
        list.add(a);
        list.add(n);
        return list;
    }
@GetMapping("/getauctionlistgd")
AuctionList getlistgd()
{
    return goDaddySoapClient.getList(1);
}


@Scheduled(cron = "0 11 20 ? * *", zone = "IST")
@GetMapping("/startlivegd")
List<Lauction> startlivegd()
{
    try {
       // logger.info("Starting live service");
        int p=1; int l=4; String ID="",PID="";
       // logger.info("1");

           // LiveMap lm= liveMaprepo.findById(1).get();
       // logger.info("2");
        //logger.info("3");
        map.clear();
        //logger.info("4");
        liveGDrepo.deleteAll();
        //logger.info("5");
        boolean b=true;
        //logger.info("6");
        while(b)
        {//logger.info("yes");
            AuctionList al = goDaddySoapClient.getList(p);
           // logger.info("yes1");
            List<Lauction> list = al.getLauctionList();
            int n = list.size();
            for (int i = 0; i < n; i++) {
                Lauction auction = list.get(i);
                String id=auction.getID();
                if(!map.contains(id)) {
                    map.add(id);
                    String timeLeft = auction.getTimeLeft().toLowerCase();
                    String[] arr= timeLeft.split(" ");
                    String a=arr[0];
                    int a1 = Integer.valueOf(a.substring(0,a.length()-1));
                    char a2 = timeLeft.charAt(a.length()-1);
                    auction.setTimeLeft(timeLeft);
                    ID=auction.getID();
                    liveGDrepo.save(auction);
                    if(a2=='h'&&a1==l+1)
                    {
                        logger.info("1"+a2+a1);
                        b = false;
                        break;
                    }
                    else  if (a2=='h' && a1>l+1) {
                        logger.info("2"+a2+a1);
                        b = false;
                        logger.info(ID);
                        ID=PID;
                        break;
                    }
                    else if(a2=='d')
                    {
                        b = false;
                        ID=PID;
                        break;
                    }
                    PID=ID;
                }
            }
            p++;
        }

       ScheduledFuture scheduledFuture= taskScheduler.scheduleWithFixedDelay(new DetectLiveGD(ID),20000);
       Date date= new Date();
       date.setHours(date.getHours()+5);
       taskScheduler.schedule(new StopLive(scheduledFuture),date);
       logger.info("Started live service");
       return liveGDrepo.findAll();
    }
    catch(Exception e)
    {
       logger.info(e.getMessage());
        return null;
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
        scheduledFuture.cancel(true);
        map.clear();
    }
}
public class DetectLiveGD implements Runnable
{
    String ID;
    public DetectLiveGD(String ID) {
        this.ID = ID;
    }

    @Override
    public void run()
    {
        try {
            logger.info("Detect Live Service Running");
            logger.info(ID);

            int p=1; int l=4;

            boolean b=true;
            while(b)
            {
                AuctionList al = goDaddySoapClient.getList(p);
                List<Lauction> list = al.getLauctionList();
                int n = list.size();
                for (int i = 0; i < n; i++) {
                    Lauction auction = list.get(i);
                    String id=auction.getID();
                    if(ID.equals(id))
                    {
                        logger.info("break "+ID);
                        b=false;
                        break;
                    }
                    if(!map.contains(id)) {
                        String domain=auction.getName().toLowerCase();
                        map.add(id);
                        String timeLeft = auction.getTimeLeft().toLowerCase();
                        auction.setTimeLeft(timeLeft);
                        String addTime= ft2.format(new Date());
                        auction.setAddTime(addTime); auction.setLive(true);
                        liveGDrepo.save(auction);
                        Integer gdv= Integer.valueOf(formatPriceLive(auction.getValuationPrice()));
                        if(Integer.valueOf(auction.getBidCount())>1||gdv>1000) {
                            String domain1 = domain.replace('.', '-');
                            String currbid = formatPriceLive(auction.getPrice());
                            String text = "GoDaddy Live Detect \n \n" + auction.getName() + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + auction.getPrice() + " \n\nGDV: " + auction.getValuationPrice() + " \n\nLink: " + "https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck=" + domain1;
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            row.add(new InlineKeyboardButton("Bid", "b gd" + " " + id + " " + domain + " " + currbid));
                            row.add(new InlineKeyboardButton("Watch", "w gd " + id + " " + domain));
                            row.add(new InlineKeyboardButton("Track", "t gd " + id + " " + domain));

                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            rows.add(row);
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                            Object obj = telegram.sendKeyboard(new SendMessage(//-1001833712484L
                                    -1001763199668l, 1013l, text, inlineKeyboardMarkup));
                        }
                    }
                }
                p++;

            }
            //liveMaprepo.save(lm);
            logger.info("Detect live service ran");
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());

        }
    }
}

    @PostMapping("/bulkbidschedulegodaddy")
    List<Integer> bulkbidschedule(@RequestBody List<ArrayList<String>> ddlist)
    {
        List<Long> ids= new ArrayList<>();
        List<Integer> list= new ArrayList<>();
        int n=ddlist.size();
        int a=0;
        for(int i=0;i<n;i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();
            String price = ddlist.get(i).get(1);

            GetAuctionDetailsResponse g= goDaddySoapClient.getAuctionDetails(domain);
            String xmlString= g.getGetAuctionDetailsResult();
            JAXBContext jaxbContext;
            GetAuctionsDetailRes res=null;
            try
            {
                jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
                //System.out.println(employee);
                if(res.getIsValid().equals("True")) {
                    String currbid= formatPrice(res.getPrice());
                    Float minNextpricef= Float.valueOf(currbid)+Float.valueOf(formatPrice(res.getBidIncrementAmount()));
                    Float pricef=Float.valueOf(price);
                    if(pricef>=minNextpricef) {
                        String endTimepst = res.getAuctionEndTime();
                        String endTime = endTimepst.substring(0, 19);

                        Date d = null;
                        try {
                            d = ft.parse(endTime);
                            System.out.println(d);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                            continue;
                        }

                        Date now = new Date();
                        if (d.getTime() - now.getTime() < 270000) {
                            d.setSeconds(d.getSeconds() - 12);
                            ScheduledFuture place = taskScheduler.schedule(new Schedulebid(domain, price, endTime), d);
                            enterTaskMap(domain, place, "pb");
                        } else {
                            Date d1 = new Date(d.getTime() - 270000);
                            ScheduledFuture pre = taskScheduler.schedule(new Precheck(domain, price), d1);
                            enterTaskMap(domain, pre, "pc");

                        }
                        a++;
                        String time = timeft.format(now);
                        String timeLeft = relTime(d);
                        String endTimeist = ft1.format(d);
                        //d.setMinutes(d.getMinutes() - 4);
                        String bidplacetime = ft1.format(d);
                        notifRepo.save(new Notification("GoDaddy", time, "Bid SCHEDULED for " + domain + " at price " + price + " at time " + bidplacetime));
                        telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Bid SCHEDULED for " + domain + " at price " + price + " at time " + bidplacetime);

                        logger.info("GoDaddy: Bid SCHEDULED for " + domain + " at price " + price + " time " + bidplacetime);


                        Optional<DBdetails> op = Optional.ofNullable(myRepo.findByPlatformAndDomain("GoDaddy", domain));
                        DBdetails dBdetails = null;
                        if (!op.isPresent()) {
                            dBdetails = new DBdetails(domain, "GoDaddy", currbid, null, timeLeft, null, null, res.getAuctionModel(), price, "Bid Scheduled", endTimepst, endTimeist, bidplacetime, false);
                        } else {
                            dBdetails = op.get();
                            dBdetails.setBidAmount(price);
                            dBdetails.setTime_left(timeLeft);
                            dBdetails.setEndTimepst(endTimepst);
                            dBdetails.setEndTimeist(endTimeist);
                            dBdetails.setCurrbid(currbid);
                            dBdetails.setAuctiontype(res.getAuctionModel());
                            dBdetails.setResult("Bid Scheduled");
                        }
                       // ids.add(dBdetails.getId());
                        myRepo.save(dBdetails);
                    }
                    else {
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-1001763199668l,1005l, "Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextpricef);
                        notifRepo.save(new Notification("GoDaddy", time, "Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextpricef));
                        logger.info(time + ": Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + minNextpricef);

                    }

                }
                else
                {
                    Date now= new Date();
                    String time= timeft.format(now);
                    telegram.sendAlert(-1001763199668l,1005l, "Bid not scheduled for domain: " + domain + " with error: " + res.getMessage());
                    notifRepo.save(new Notification("GoDaddy",time,"Bid not scheduled for domain: " + domain + " with error: " + res.getMessage()));
                    logger.info("Bid not scheduled for domain: " + domain + " with error: " + res.getMessage());
                }

            }
            catch (JAXBException e)
            {
                Date now= new Date();
                String time= timeft.format(now);
                notifRepo.save(new Notification("GoDaddy",time,"Bid NOT SCHEDULED for " + domain + " at price " + price+". See log for detail."));
                telegram.sendAlert(-834797664L,"GoDaddy: Bid NOT SCHEDULED for " + domain + " at price " + price+". See log for detail.");
                telegram.sendAlert(-834797664L,e.getMessage());
                logger.info(e.getMessage());

            }
            catch(Exception e1)
            {
                Date now= new Date();
                String time= timeft.format(now);
                telegram.sendAlert(-834797664L,e1.getMessage());
                notifRepo.save(new Notification("GoDaddy",time,"Bid NOT SCHEDULED for " + domain + " at price " + price+" with error: "+e1.getMessage()));
                logger.info(e1.getMessage());
            }
        }
       // asyncCalss.getGDVs(ids);
        list.add(a);
        list.add(n);

        return list;
    }

    Boolean b=true;
    @Scheduled(fixedRate = 120000)
    void refreshgdwatchlist() {
        List<DBdetails> list = myRepo.findByPlatformAndWatchlistIsTrueAndTrackIsFalse("GoDaddy");
        if (!list.isEmpty())
            for (int i = 0; i < list.size(); i++) {
                DBdetails db = list.get(i);
                String domain = db.getDomain();
                GetAuctionDetailsResponse g = goDaddySoapClient.getAuctionDetails(domain);
                String xmlString = g.getGetAuctionDetailsResult();
                JAXBContext jaxbContext;
                GetAuctionsDetailRes res = null;
                try {
                    jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();


                    res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

                    //System.out.println(employee);
                    if (res.getIsValid().equals("True")) {
                        Date now = new Date();

                        String endTimepst = res.getAuctionEndTime();
                        String endTime = endTimepst.substring(0, 19);
                        //logger.info(db.getCurrbid());
                        float prevBid = Float.valueOf(db.getCurrbid());
                        logger.info(endTime);
                        Date d = null;
                        try {
                            d = ft.parse(endTime);
                            System.out.println(d);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());

                        }
                        String timeLeft = relTime(d);
                        String endTimeist = ft1.format(d);
                        String currbid = formatPrice(res.getPrice());

                        if (prevBid < Float.valueOf(currbid)) {
                            String text = "GoDaddy\n\n" + domain + "\nNEW BID PLACED" + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + currbid + "\n \nGDV: " + db.getGdv() + " \n\nLink: " + "https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck=" + domain;
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            row.add(new InlineKeyboardButton("Bid", "b gd" + " " + "gd" + " " + domain + " " + currbid));
                            row.add(new InlineKeyboardButton("Track", "t gd " + "gd" + " " + domain));
                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            rows.add(row);
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
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
                            String text = "GoDaddy\n\n" + domain + "\n<24 hrs LEFT" + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + currbid + "\n \nGDV: " + db.getGdv() + " \n\nLink: " + "https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck=" + domain;
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            row.add(new InlineKeyboardButton("Bid", "b gd" + " " + "gd" + " " + domain + " " + currbid));
                            row.add(new InlineKeyboardButton("Track", "t gd " + "gd" + " " + domain));
                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            rows.add(row);
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                            nw = 3;
                            db.setNw(nw);
                        } else if (d.getTime() - now.getTime() < 3600002 && d.getTime() - now.getTime() > 3480000 && nw >= 3) {
                            String text = "GoDaddy\n\n" + domain + "\n<1 hr LEFT" + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + currbid + "\n \nGDV: " + db.getGdv() + " \n\nLink: " + "https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck=" + domain;
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            row.add(new InlineKeyboardButton("Bid", "b gd" + " " + "gd" + " " + domain + " " + currbid));
                            row.add(new InlineKeyboardButton("Track", "t gd " + "gd" + " " + domain));
                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            rows.add(row);
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                            nw = 2;
                            db.setNw(nw);
                        } else if (d.getTime() - now.getTime() < 600002 && d.getTime() - now.getTime() > 480000 && nw >= 2) {
                            String text = "GoDaddy\n\n" + domain + "\n<10 mins LEFT" + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + currbid + "\n \nGDV: " + db.getGdv() + " \n\nLink: " + "https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck=" + domain;
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            row.add(new InlineKeyboardButton("Bid", "b gd" + " " + "gd" + " " + domain + " " + currbid));
                            row.add(new InlineKeyboardButton("Track", "t gd " + "gd" + " " + domain));
                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            rows.add(row);
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                            nw = 1;
                            db.setNw(nw);
                        } else if (d.getTime() - now.getTime() < 240002 && d.getTime() - now.getTime() > 120000 && nw >= 1) {
                            String text = "GoDaddy\n\n" + domain + "\n<4 mins LEFT" + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + currbid + "\n \nGDV: " + db.getGdv() + " \n\nLink: " + "https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck=" + domain;
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            row.add(new InlineKeyboardButton("Bid", "b gd" + " " + "gd" + " " + domain + " " + currbid));
                            row.add(new InlineKeyboardButton("Track", "t gd " + "gd" + " " + domain));
                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            rows.add(row);
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                                    , text, inlineKeyboardMarkup));
                            nw = -1;
                            db.setNw(nw);
                        }


                        db.setTime_left(timeLeft);
                        db.setEndTimepst(endTimepst);
                        db.setEndTimeist(endTimeist);
                        db.setCurrbid(currbid);
                        db.setAuctiontype(res.getAuctionModel());
                    } else {
                        db.setWatchlist(false);
                        db.setWasWatchlisted(true);
                        Date now = new Date();
                        String time = timeft.format(now);
                        notifRepo.save(new Notification("GoDaddy", time, "Watchlisted auction ended of domain: " + domain));
                    }
                    myRepo.save(db);
                } catch (JAXBException e)
                {
                    Date now = new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-834797664L, e.getMessage()+"gd "+xmlString);
                    logger.info(e.getMessage());

                }
                catch (Exception e1)
                {
                    Date now = new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-834797664L, e1.getMessage()+"1gd "+domain);
                    logger.info(e1.getMessage());
                }
            }

        //scheduled

        //List<DBdetails> slist = myRepo.findByPlatformAndResultOrResultOrResultOrResult("GoDaddy", "Bid Scheduled", "Bid Placed", "Bid Placed And Scheduled", "Outbid");
        List<DBdetails> slist= myRepo.findScheduledGD();

        if (slist == null || slist.size() == 0)
            return;
        PriorityQueue<DBdetails> pq = new PriorityQueue<>(slist.size(), new GDrComparator());
        for (int i = 0; i < slist.size(); i++) {
            DBdetails dBdetails = slist.get(i);
            String domain = dBdetails.getDomain();
            try {
                GetAuctionsDetailRes res = goDaddySoapClient.getAuctionDetail(domain);
                if (res.getIsValid().equals("True")) {
                    String endTimepst = res.getAuctionEndTime();
                    String endTime = endTimepst.substring(0, 19);
                    logger.info(endTime);
                    Date d = null;
                    try {
                        d = ft.parse(endTime);
                        System.out.println(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        //continue;
                    }
                    String timeLeft = relTime(d);
                    String endTimeist = ft1.format(d);

                    String price1 = formatPrice(res.getPrice());
                    int pricec = Integer.valueOf(price1);
                    int pricei = pricec+Integer.valueOf(formatPrice(res.getBidIncrementAmount()));
                    if (pricec > Integer.valueOf(dBdetails.getBidAmount()))
                    {
                        if(!dBdetails.getResult().equals("Outbid"))
                        {
                            String text= "GoDaddy Auction OUTBID\n\n"+domain+"\n \nTime Left: "+timeLeft+"\nCurrent Bid: "+price1+"\nMin Next Bid: "+ pricei+"\nOur Max Bid: "+dBdetails.getBidAmount()+"\n \nGDV: "+dBdetails.getGdv()+" \n\nLink: "+"https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck="+domain;
                            //-1001814695777L
                            List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                            row.add(new InlineKeyboardButton("Bid","b gd "+"gd"+" "+domain+" "+pricei));
                            row.add(new InlineKeyboardButton("Watch","w gd "+"gd"+" "+domain));
                            row.add(new InlineKeyboardButton("Track","t gd "+"gd"+" "+domain));

                            List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                            rows.add(row);
                            InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                            Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                                    ,text,inlineKeyboardMarkup));
                            dBdetails.setResult("Outbid");
                        }
                    }
                    else if(b)
                    {
                        Date now= new Date();
                        if (d.getTime() - now.getTime() < 270000) {
                            d.setSeconds(d.getSeconds() - 12);
                            ScheduledFuture place = taskScheduler.schedule(new Schedulebid(domain, dBdetails.getBidAmount(), endTime), d);
                            enterTaskMap(domain, place, "pb");
                        } else {
                            Date d1 = new Date(d.getTime() - 270000);
                            ScheduledFuture pre = taskScheduler.schedule(new Precheck(domain, dBdetails.getBidAmount()), d1);
                            enterTaskMap(domain, pre, "pc");

                        }
                        String time = timeft.format(now);
                        String bidplacetime = ft1.format(d);
                        notifRepo.save(new Notification("GoDaddy", time, "Bid SCHEDULED for " + domain + " at price " + dBdetails.getBidAmount() + " at time " + bidplacetime));
                        telegram.sendAlert(-1001763199668l,1004l, "GoDaddy: Bid SCHEDULED for " + domain + " at price " + dBdetails.getBidAmount() + " at time " + bidplacetime);

                        logger.info("GoDaddy: Bid SCHEDULED for " + domain + " at price " + dBdetails.getBidAmount() + " time " + bidplacetime);

                    }

                    dBdetails.setTime_left(timeLeft);
                    dBdetails.setEndTimepst(endTimepst);
                    dBdetails.setEndTimeist(endTimeist);
                    dBdetails.setCurrbid(price1);
                    dBdetails.setAuctiontype(res.getAuctionModel());
                    myRepo.save(dBdetails);
                } else {
                    /*logger.info("Could not fetch scheduled bid info for domain: " + domain+" with message: "+res.getIsValid());
                    telegram.sendAlert(-834797664L, "Could not fetch scheduled bid info for domain: " + domain);
                    dBdetails.setResult("");
                    myRepo.save(dBdetails);*/
                    if(!dBdetails.isBidPlaced())
                    {
                        Date now = new Date();
                        String time = timeft.format(now);
                        telegram.sendAlert(-1001763199668l,841l, "GoDaddy: Hush!! Lost auction of " + domain + " at price: " + dBdetails.getCurrbid());
                        notifRepo.save(new Notification("GoDaddy", time, "Hushh!! Lost auction of " + domain + " at price: " + dBdetails.getCurrbid()));
                        logger.info(time + ": Lost auction of " + domain + " at price: " + dBdetails.getCurrbid());
                        dBdetails.setResult("Loss");
                        myRepo.save(dBdetails);
                        deleteTaskMap(domain);
                    }
                    else
                    pq.add(dBdetails);

                }
            } catch (Exception e) {
                logger.info("Could not fetch scheduled bid info for domain: " + domain + " with exception: " + e.getMessage());
                telegram.sendAlert(-834797664L, "Could not fetch scheduled bid info for domain: " + domain + " with exception: " + e.getMessage());

            }
        }
        PriorityQueue<DBdetails> pq1 = new PriorityQueue<>(slist.size(), new GDrComparator());
        if (!pq.isEmpty()) {
            List<Lauction> losings = goDaddySoapClient1.getLosings().getLauctionList();

            for (int i = 0; i < losings.size(); i++) {
                Lauction auction = losings.get(i);
                DBdetails db = pq.peek();
                String domain = db.getDomain();
                Date d1 = null;
                try {
                    d1 = ft1.parse(db.getEndTimeist());

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                String endTime = auction.getAuctionEndTime();
                Date d = null;
                try {
                    d = ftr.parse(endTime);

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                while ((!domain.equalsIgnoreCase(auction.getName())) && d1.after(d)) {
                    pq1.add(pq.poll());
                    if (pq.isEmpty())
                        break;
                    db = pq.peek();
                    domain = db.getDomain();

                    try {
                        d1 = ft1.parse(db.getEndTimeist());

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                }
                if (pq.isEmpty())
                    break;
                if (domain.equalsIgnoreCase(auction.getName())) {
                    pq.poll();
                    String price = auction.getPrice().substring(1, auction.getPrice().length() - 4);
                    Date now = new Date();
                    String time = timeft.format(now);
                    telegram.sendAlert(-1001763199668l,841l, "GoDaddy: Hush!! Lost auction of " + domain + " at price: " + price);
                    notifRepo.save(new Notification("GoDaddy", time, "Hushh!! Lost auction of " + domain + " at price: " + price));
                    logger.info(time + ": Lost auction of " + domain + " at price: " + price);
                    db.setResult("Loss");
                    String endTimeist = ft1.format(d);
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setCurrbid(price);
                    myRepo.save(db);
                    deleteTaskMap(domain);

                }
                if (pq.isEmpty())
                    break;
            }
        }
        if (!pq1.isEmpty()) {
            List<Lauction> winnings = goDaddySoapClient1.getWinnings().getLauctionList();

            Date now=new Date();
            String time=timeft.format(now);
            for (int i = 0; i < winnings.size(); i++) {
                Lauction auction = winnings.get(i);
                DBdetails db = pq1.peek();
                String domain = db.getDomain();
                Date d1 = null;
                try {
                    d1 = ft1.parse(db.getEndTimeist());

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                String endTime = auction.getAuctionEndTime();
                Date d = null;
                try {
                    d = ftr.parse(endTime);

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }

                while (!domain.equalsIgnoreCase(auction.getName()) && d1.after(d)) {
                    pq1.poll();

                    telegram.sendAlert(-834797664L, "GoDaddy: Not refreshed scheduled domain: "+domain);
                    notifRepo.save(new Notification("GoDaddy", time, "Not refreshed scheduled domain: "+domain));
                    logger.info(time + ": Not refreshed scheduled domain: "+domain);
                    db.setResult("Loss");
                    myRepo.save(db);
                    deleteTaskMap(domain);
                    if (pq1.isEmpty())
                        break;
                    db = pq1.peek();
                    domain = db.getDomain();

                    try {
                        d1 = ft1.parse(db.getEndTimeist());

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                    }
                }
                if (pq1.isEmpty())
                    break;
                if (domain.equalsIgnoreCase(auction.getName())) {
                    pq1.poll();
                    String price = auction.getPrice().substring(1, auction.getPrice().length() - 4);
                    telegram.sendAlert(-1001763199668l,842l, "GoDaddy: Yippee!! Won auction of " + domain + " at price: " + price);
                    notifRepo.save(new Notification("GoDaddy",time,"Yippee!! Won auction of "+domain+" at price: "+price));
                    logger.info(time+": Won auction of "+domain+" at price: "+price);

                    db.setResult("Won");
                    String endTimeist = ft1.format(d);
                    db.setEndTimepst(endTime);
                    db.setEndTimeist(endTimeist);
                    db.setCurrbid(price);
                    myRepo.save(db);
                    deleteTaskMap(domain);
                }
                if (pq1.isEmpty())
                    break;
            }
        }
        b=false;
    }

    @GetMapping("/cancel/gd")
    void cancelBid(@RequestParam String domain)
    {
        deleteTaskMap(domain);
        DBdetails db= myRepo.findByPlatformAndDomain("GoDaddy",domain.toLowerCase());
        db.setResult("Bid Cancelled");
        myRepo.save(db);
    }
    @Scheduled(fixedRate = 120000)
    void refreshgdtrack()
    {
        List<DBdetails> list = myRepo.findByPlatformAndTrackIsTrue("GoDaddy");
        if (list.isEmpty())
            return;
        for (int i = 0; i < list.size(); i++) {
            DBdetails db = list.get(i);
            String domain = db.getDomain();
            GetAuctionDetailsResponse g= goDaddySoapClient.getAuctionDetails(domain);
            String xmlString= g.getGetAuctionDetailsResult();
            JAXBContext jaxbContext;
            GetAuctionsDetailRes res=null;
            try
            {
                jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();



                res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

                //System.out.println(employee);
                if(res.getIsValid().equals("True"))
                {
                    Date now= new Date();

                    String endTimepst = res.getAuctionEndTime();
                    String endTime = endTimepst.substring(0, 19);

                    float prevBid= Float.valueOf(db.getCurrbid());
                    logger.info(endTime);
                    Date d = null;
                    try {
                        d = ft.parse(endTime);
                        System.out.println(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());

                    }
                    String timeLeft = relTime(d);
                    String endTimeist = ft1.format(d);
                    String currbid= formatPrice(res.getPrice());

                    String text = "GoDaddy Live Track\n\n" + domain +"\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + currbid + "\n \nGDV: " + db.getGdv() +" \n\nLink: " +"https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck="+domain ;
                    List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                    row.add(new InlineKeyboardButton("Bid","b gd"+" "+"gd"+" "+domain+" "+currbid));
                    row.add(new InlineKeyboardButton("Remove","rw gd "+"gd"+" "+domain));
                    List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                    rows.add(row);
                    InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                    Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                            ,text,inlineKeyboardMarkup));


                    db.setTime_left(timeLeft);
                    db.setEndTimepst(endTimepst);
                    db.setEndTimeist(endTimeist);
                    db.setCurrbid(currbid);
                    db.setAuctiontype(res.getAuctionModel());
                }
                else
                {
                    db.setTrack(false);
                    db.setWatchlist(false);
                    db.setWasWatchlisted(true);
                    Date now= new Date();
                    String time=timeft.format(now);
                    notifRepo.save(new Notification("GoDaddy",time,"Tracked auction ended of domain: "+domain));
                }
                myRepo.save(db);
            }
            catch (JAXBException e)
            {
                Date now= new Date();
                String time= timeft.format(now);
                telegram.sendAlert(-834797664L,e.getMessage());
                logger.info(e.getMessage());

            }
            catch(Exception e1)
            {
                Date now= new Date();
                String time= timeft.format(now);
                telegram.sendAlert(-834797664L,e1.getMessage());
                logger.info(e1.getMessage());
            }
        }
    }


    @GetMapping("/getWinnings")
    MyWonSummary getMyWonSummary()
    {
       MyWonSummary m= goDaddySoapClient1.getWinnings();
       return m;
    }


    void instantUpdateWatchlist(DBdetails db)
    {
        String domain=db.getDomain().toLowerCase();
        GetAuctionDetailsResponse g= goDaddySoapClient.getAuctionDetails(domain);
        String xmlString= g.getGetAuctionDetailsResult();
        JAXBContext jaxbContext;
        GetAuctionsDetailRes res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();



            res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

            //System.out.println(employee);
            if(res.getIsValid().equals("True"))
            {
                String endTimepst = res.getAuctionEndTime();
                String endTime = endTimepst.substring(0, 19);

                logger.info(endTime);
                Date d = null;
                try {
                    d = ft.parse(endTime);
                    System.out.println(d);
                } catch (ParseException p) {
                    logger.info(p.getMessage());

                }
                String timeLeft = relTime(d);
                String endTimeist = ft1.format(d);
                String currbid= formatPrice(res.getPrice());
                String bidplacetime = ft1.format(d);
                Date now= new Date();
                String time=timeft.format(now);
                String text = "GoDaddy\n\n" + domain + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + currbid// +"\n \nGDV: " +gdv
                        +" \n\nLink: " +"https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck="+domain ;
                List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton("Bid","b gd"+" "+"gd"+" "+domain+" "+currbid));
                row.add(new InlineKeyboardButton("Remove","rw gd "+"gd"+" "+domain));
                List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                rows.add(row);
                InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                        ,text,inlineKeyboardMarkup));
                //Optional<DBdetails> op= Optional.ofNullable(myRepo.findByPlatformAndDomain("GoDaddy",domain));
                    db.setTime_left(timeLeft);
                    db.setEndTimepst(endTimepst);
                    db.setEndTimeist(endTimeist);
                    db.setCurrbid(currbid);
                    db.setAuctiontype(res.getAuctionModel());

                db.setWatchlist(true);
                /*if(track)
                    db.setTrack(true);*/
                myRepo.save(db);
            }
            else
            {

                Date now= new Date();
                String time=timeft.format(now);
                //notifRepo.save(new Notification("Namesilo",time,"Watchlisted auction ended of domain: "+domain));
            }

        }
        catch (JAXBException e)
        {
            Date now= new Date();
            String time= timeft.format(now);
            telegram.sendAlert(-834797664L,e.getMessage());
            logger.info(e.getMessage());

        }
        catch(Exception e1)
        {
            Date now= new Date();
            String time= timeft.format(now);
            telegram.sendAlert(-834797664L,e1.getMessage());
            logger.info(e1.getMessage());
        }
    }


    void watchlistLive(String domain,String id, Boolean track)
    {
        domain=domain.toLowerCase();
        GetAuctionDetailsResponse g= goDaddySoapClient.getAuctionDetails(domain);
        String xmlString= g.getGetAuctionDetailsResult();
        JAXBContext jaxbContext;
        GetAuctionsDetailRes res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();



            res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

            //System.out.println(employee);
            if(res.getIsValid().equals("True"))
            {
                String endTimepst = res.getAuctionEndTime();
                String endTime = endTimepst.substring(0, 19);

                logger.info(endTime);
                Date d = null;
                try {
                    d = ft.parse(endTime);
                    System.out.println(d);
                } catch (ParseException p) {
                    logger.info(p.getMessage());

                }
                String timeLeft = relTime(d);
                String endTimeist = ft1.format(d);
                String currbid= formatPrice(res.getPrice());
                String bidplacetime = ft1.format(d);
                Date now= new Date();
                String time=timeft.format(now);
                Optional<DBdetails> op= Optional.ofNullable(myRepo.findByPlatformAndDomain("GoDaddy",domain));
            /*   Integer gdv=0;
                if(op.isPresent())
                {
                    gdv= op.get().getGdv();
                    if(gdv==null||gdv==0)
                    {
                       Optional<Lauction> lauctiono= Optional.ofNullable(liveGDrepo.findByName(domain));
                        if(lauctiono.isPresent())
                        {
                            gdv=lauctiono.get().getGDV();
                        }
                    }
                }
                else {  //gdv= liveGDrepo.findByName(domain).getGDV();
                }*/
                String text = "GoDaddy\n\n" + domain + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + currbid //+ "\n \nGDV: " +gdv
                        +" \n\nLink: " +"https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck="+domain ;
                List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton("Bid","b gd"+" "+"gd"+" "+domain+" "+currbid));
                row.add(new InlineKeyboardButton("Remove","rw gd "+"gd"+" "+domain));
                List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                rows.add(row);
                InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                Object obj = telegram.sendKeyboard(new SendMessage(-1001887754426l
                        ,text,inlineKeyboardMarkup));
                //Optional<DBdetails> op= Optional.ofNullable(myRepo.findByPlatformAndDomain("GoDaddy",domain));
                DBdetails db= null;
                if(op.isPresent()) {
                    db=op.get();
                    db.setTime_left(timeLeft);
                    db.setEndTimepst(endTimepst);
                    db.setEndTimeist(endTimeist);
                    db.setCurrbid(currbid);
                    db.setAuctiontype(res.getAuctionModel());
                }
                else
                {
                    db = new DBdetails(domain, "GoDaddy", res.getPrice(), null, timeLeft, null, null, res.getAuctionModel(), "", "", endTimepst, endTimeist, bidplacetime, false);
                }
                db.setWatchlist(true);
                if(track)
                db.setTrack(true);
                myRepo.save(db);
            }
            else
            {

                Date now= new Date();
                String time=timeft.format(now);
                //notifRepo.save(new Notification("Namesilo",time,"Watchlisted auction ended of domain: "+domain));
            }

        }
        catch (JAXBException e)
        {
            Date now= new Date();
            String time= timeft.format(now);
            telegram.sendAlert(-834797664L,e.getMessage());
            logger.info(e.getMessage());

        }
        catch(Exception e1)
        {
            Date now= new Date();
            String time= timeft.format(now);
            telegram.sendAlert(-834797664L,e1.getMessage());
            logger.info(e1.getMessage());
        }
    }

    @GetMapping("/schedulesinglegd")
    float scheduleSingleOutbid(@RequestParam String domain,@RequestParam String id,@RequestParam String price)
    {
        domain=domain.toLowerCase();
        GetAuctionDetailsResponse g= goDaddySoapClient.getAuctionDetails(domain);
        String xmlString= g.getGetAuctionDetailsResult();
        JAXBContext jaxbContext;
        GetAuctionsDetailRes res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();



            res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));

            //System.out.println(employee);
            if(res.getIsValid().equals("True"))
            {
                String currbid= formatPrice(res.getPrice());
                Float minNextpricef= Float.valueOf(currbid)+Float.valueOf(formatPrice(res.getBidIncrementAmount()));
                Float pricef=Float.valueOf(price);
                if(pricef>=minNextpricef) {
                    String endTimepst = res.getAuctionEndTime();
                    String endTime = endTimepst.substring(0, 19);
                    logger.info(endTime);
                    Date d = null;
                    try {
                        d = ft.parse(endTime);
                        System.out.println(d);
                    } catch (ParseException p) {
                        logger.info(p.getMessage());

                    }

                    Date now = new Date();
                    if (d.getTime() - now.getTime() < 270000) {
                        d.setSeconds(d.getSeconds() - 12);
                        ScheduledFuture place = taskScheduler.schedule(new Schedulebid(domain, price, endTime), d);
                        enterTaskMap(domain, place, "pb");

                    } else {
                        Date d1 = new Date(d.getTime() - 270000);
                        ScheduledFuture pre = taskScheduler.schedule(new Precheck(domain, price), d1);
                        enterTaskMap(domain, pre, "pc");

                    }
                    Date finalD = d;
                    String finalDomain = domain;
                    GetAuctionsDetailRes finalRes = res;
                    CompletableFuture.runAsync(()->
                    {
                        String timeLeft = relTime(finalD);
                        String endTimeist = ft1.format(finalD);
                        //d.setMinutes(d.getMinutes() - 4);
                        String bidplacetime = ft1.format(finalD);
                        String time = timeft.format(now);
                        notifRepo.save(new Notification("GoDaddy", time, "Bid SCHEDULED for " + finalDomain + " at price " + price + " at time " + bidplacetime));
                        telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Bid SCHEDULED for " + finalDomain + " at price " + price + " at time " + bidplacetime);

                        logger.info("GoDaddy: Bid SCHEDULED for " + finalDomain + " at price " + price + " time " + bidplacetime);


                        Optional<DBdetails> op = Optional.ofNullable(myRepo.findByPlatformAndDomain("GoDaddy", finalDomain));
                        DBdetails dBdetails = null;
                        if (!op.isPresent()) {
                            dBdetails = new DBdetails(finalDomain, "GoDaddy", finalRes.getPrice(), null, timeLeft, null, null, finalRes.getAuctionModel(), price, "Bid Scheduled", endTimepst, endTimeist, bidplacetime, false);
                        } else {
                            dBdetails = op.get();
                            dBdetails.setBidAmount(price);
                            dBdetails.setTime_left(timeLeft);
                            dBdetails.setEndTimepst(endTimepst);
                            dBdetails.setEndTimeist(endTimeist);
                            dBdetails.setCurrbid(formatPrice(finalRes.getPrice()));
                            dBdetails.setAuctiontype(finalRes.getAuctionModel());
                            dBdetails.setResult("Bid Scheduled");
                        }
                        myRepo.save(dBdetails);
                    });
                    return 0;
                }
                else {
                    String finalDomain1 = domain;
                    CompletableFuture.runAsync(()->{
                    Date now = new Date();
                    String time = ft1.format(now);
                    telegram.sendAlert(-1001763199668l,1005l,"GoDaddy: Bid NOT SCHEDULED for" + finalDomain1 + " as bid value is lower than accepted bid of " + minNextpricef);
                    notifRepo.save(new Notification("GoDaddy", time, "Bid NOT SCHEDULED for" + finalDomain1 + " as bid value is lower than accepted bid of " + minNextpricef));
                    logger.info(time + ": Bid NOT SCHEDULED for " + finalDomain1 + " as bid value is lower than accepted bid of " + minNextpricef);
                    },threadPoolExecutor);
                    return minNextpricef;
                }

            }
            else
            {
                String finalDomain2 = domain;
                GetAuctionsDetailRes finalRes1 = res;
                CompletableFuture.runAsync(()->{
                Date now= new Date();
                String time= timeft.format(now);
                    telegram.sendAlert(-1001763199668l,1005l,"Bid not scheduled for domain: " + finalDomain2 + " with error: " + finalRes1.getMessage());
                    notifRepo.save(new Notification("GoDaddy",time,"Bid not scheduled for domain: " + finalDomain2 + " with error: " + finalRes1.getMessage()));
                logger.info("Bid not scheduled for domain: " + finalDomain2 + " with error: " + finalRes1.getMessage());
                },threadPoolExecutor);
                }
            return 1;

        }
        catch (JAXBException e)
        {
            Date now= new Date();
            String time= timeft.format(now);
            notifRepo.save(new Notification("GoDaddy",time,"Bid NOT SCHEDULED for " + domain + " at price " + price+". See log for detail."));
            telegram.sendAlert(-834797664L,"GoDaddy: Bid NOT SCHEDULED for " + domain + " at price " + price+". See log for detail.");
            telegram.sendAlert(-834797664L,e.getMessage());
            logger.info(e.getMessage());

        }
        catch(Exception e1)
        {
            Date now= new Date();
            String time= timeft.format(now);
            telegram.sendAlert(-834797664L,e1.getMessage());
            notifRepo.save(new Notification("GoDaddy",time,"Bid NOT SCHEDULED for " + domain + " at price " + price+" with error: "+e1.getMessage()));
            logger.info(e1.getMessage());
        }
        return 0;
    }

    String formatPrice(String price)
    {
        String price1= price.substring(1,price.length());
        price1= price1.replace(",","");
        return price1;
    }

    String formatPriceLive(String price)
    {
        String price1= price.substring(1,price.length()-4);
        price1= price1.replace(",","");
        return price1;
    }

    public class CheckOutbid implements Runnable
    {
        String domain,maxprice,price;
        ScheduledFuture scheduledFuture;

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public CheckOutbid(String domain, String maxprice, String price) {
            this.domain = domain;
            this.maxprice = maxprice;
            this.price=price;
        }

        @Override
        public void run()
        {
            GetAuctionsDetailRes res = goDaddySoapClient.getAuctionDetail(domain);
            if(res.getIsValid().equals("False"))
            {
                //won
                DBdetails dBdetails= myRepo.findByPlatformAndDomain("GoDaddy",domain);
                dBdetails.setResult("Won");
                myRepo.save(dBdetails);
                Date now= new Date();
                String time= timeft.format(now);
                telegram.sendAlert(-1001763199668l,842l, "GoDaddy: Yippee!! Won auction of "+domain+" at price: "+price);
                notifRepo.save(new Notification("GoDaddy",time,"Yippee!! Won auction of "+domain+" at price: "+price));
                logger.info(time+": Won auction of "+domain+" at price: "+price);
                deleteTaskMap(domain);
                scheduledFuture.cancel(false);
            }
            else
            {
                if(res.getIsHighestBidder().equals("False"))
                {
                    String price1= res.getPrice().substring(1,res.getPrice().length());
                    price1= price1.replace(",","");
                    int pricec= Integer.valueOf(price1);
                    String bidincrement= res.getBidIncrementAmount().substring(1,res.getBidIncrementAmount().length());
                    bidincrement= bidincrement.replace(",","");
                    int inc= Integer.valueOf(bidincrement);
                    int pricei= pricec+inc;
                    String price= String.valueOf(pricei);
                    if(pricec<=Integer.valueOf(maxprice))
                    {
                        String endTimepst= res.getAuctionEndTime();
                        String endTime= endTimepst.substring(0,19);
                        Date d = null;
                        try {
                            d = ft.parse(endTime);
                            d.setSeconds(d.getSeconds()-10);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                        }
                        ScheduledFuture place=  taskScheduler.schedule(new Schedulebid(domain, maxprice, endTime), d);
                        updateTaskMap(domain,place,"pb");

                        Date now= new Date();
                        String time=timeft.format(now);
                        String endTimeist = ft1.format(d);

                        notifRepo.save(new Notification("GoDaddy",time,"Outbid, Bid SCHEDULED for " + domain + " at price " + price + " at time " + endTimeist));
                        telegram.sendAlert(-1001763199668l,1004l, "GoDaddy: Outbid, Bid SCHEDULED for " + domain + " at price " + price + " at time " + endTimeist);
                        logger.info(time+": Outbid, Bid SCHEDULED for " + domain + " at price " + price + " time " + endTimeist);

                    }
                    else
                    {
                        //notify
                        String endTimepst = res.getAuctionEndTime();
                        String endTime = endTimepst.substring(0, 19);
                        logger.info(endTime);
                        Date d = null;
                        try {
                            d = ft.parse(endTime);
                            System.out.println(d);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                            //continue;
                        }

                        String timeLeft = relTime(d);
                        DBdetails db= myRepo.findByPlatformAndDomain("GoDaddy",domain);
                        String text= "GoDaddy Auction OUTBID\n\n"+domain+"\n \nTime Left: "+timeLeft+"\nCurrent Bid: "+price1+"\nMin Next Bid: "+ pricei+"\nOur Max Bid: "+maxprice+"\n \nGDV: "+db.getGdv()+" \n\nLink: "+"https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck="+domain;
                        //-1001814695777L
                        List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid","b gd "+"gd"+" "+domain+" "+price));
                        row.add(new InlineKeyboardButton("Watch","w gd "+"gd"+" "+domain));
                        row.add(new InlineKeyboardButton("Track","t gd "+"gd"+" "+domain));

                        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                        Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                                ,text,inlineKeyboardMarkup));
                        Date now= new Date();
                        String time= timeft.format(now);
                        notifRepo.save(new Notification("GoDaddy",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + price1 ));
                        logger.info(time+":Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + price1 );

                        db.setResult("Outbid");
                        myRepo.save(db);
                        now.setMinutes(now.getMinutes()+30);
                       ScheduledFuture res1= taskScheduler.schedule(new GetResultGD(domain,domain),now);
                        updateTaskMap(domain,res1,"gr");

                    }
                    scheduledFuture.cancel(false);
                }
            }
        }
    }

    public class Precheck implements Runnable
    {
        String domain,maxprice;

        public Precheck(String domain, String maxprice)
        {
            this.domain = domain;
            this.maxprice = maxprice;
        }

        @Override
        public void run()
        {
            GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
            String endTimepst= res.getAuctionEndTime();
            String endTime= endTimepst.substring(0,19);
            String price1= res.getPrice().substring(1,res.getPrice().length());
            price1= price1.replace(",","");
            int pricec= Integer.valueOf(price1);
            String bidincrement= res.getBidIncrementAmount().substring(1,res.getBidIncrementAmount().length());
            bidincrement= bidincrement.replace(",","");
            int inc= Integer.valueOf(bidincrement);
            int pricei= pricec+inc;
            String price= String.valueOf(pricei);
            if(pricec<=Integer.valueOf(maxprice))
            {
                Date d = null;
                try {
                    d = ft.parse(endTime);
                    d.setSeconds(d.getSeconds()-10);
                } catch (ParseException p) {
                    logger.info(p.getMessage());

                }
                //String timeLeft= relTime(d);

               ScheduledFuture place= taskScheduler.schedule(new Schedulebid(domain, maxprice, endTime), d);
                updateTaskMap(domain,place,"pb");

                Date now= new Date();
                String time=timeft.format(now);
                String endTimeist = ft1.format(d);
                notifRepo.save(new Notification("GoDaddy",time,"Bid SCHEDULED for " + domain + " at price " + price + " at time " + endTimeist));
                telegram.sendAlert(-1001763199668l,1004l, time+": GoDaddy: Bid SCHEDULED for " + domain + " at price " + price + " at time " + endTimeist);
                logger.info(time+": Bid SCHEDULED for " + domain + " at price " + price + " time " + endTimeist);

            }
            else
            {
                //notify

                logger.info(endTime);
                Date d = null;
                try {
                    d = ft.parse(endTime);
                    System.out.println(d);
                } catch (ParseException p) {
                    logger.info(p.getMessage());
                    //continue;
                }

                String timeLeft = relTime(d);
                DBdetails db= myRepo.findByPlatformAndDomain("GoDaddy",domain);
                String text= "GoDaddy Auction OUTBID\n\n"+domain+"\n \nTime Left: "+timeLeft+"\nCurrent Bid: "+price1+"\nMin Next Bid: "+ pricei+"\nOur Max Bid: "+maxprice+"\n \nGDV: "+db.getGdv()+" \n\nLink: "+"https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck="+domain;
                //-1001814695777L
                List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                row.add(new InlineKeyboardButton("Bid","b gd "+"gd"+" "+domain+" "+price));
                row.add(new InlineKeyboardButton("Watch","w gd "+"gd"+" "+domain));
                row.add(new InlineKeyboardButton("Track","t gd "+"gd"+" "+domain));
                List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                rows.add(row);
                InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                        ,text,inlineKeyboardMarkup));
                Date now= new Date();
                String time= timeft.format(now);
                notifRepo.save(new Notification("GoDaddy",time,"Domain: "+domain+" with our max price "+maxprice+" OUTBID at price " + price1 ));
                logger.info(time+":Domain: "+domain+" with our max price "+maxprice+" Outbid at price " + price1 );

                db.setResult("Outbid");
                myRepo.save(db);
                now.setMinutes(now.getMinutes()+30);
                ScheduledFuture res1= taskScheduler.schedule(new GetResultGD(domain,domain),now);
                updateTaskMap(domain,res1,"gr");

            }
        }
    }
    public class Schedulebid implements Runnable
    {
        String domain,maxprice,timeId;

        public Schedulebid(String domain, String price,String timeId)
        {
            this.domain = domain.toLowerCase();
            this.maxprice = price;
            this.timeId=timeId;
        }

        @Override
        public void run()
        {
            try {
                GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                if (res.getIsValid().equals("True")) {
                    String endTimepst = res.getAuctionEndTime();
                    String endTime = endTimepst.substring(0, 19);
                    String price1 = formatPrice(res.getPrice());
                    int pricec = Integer.valueOf(price1);
                    String bidincrement =formatPrice(res.getBidIncrementAmount());
                    int inc = Integer.valueOf(bidincrement);
                    int pricei = pricec + inc;
                    String price = String.valueOf(pricei);
                    if (pricec <= Integer.valueOf(maxprice)) {
                        if (endTime.equals(timeId)) {

                            PlaceBid place = goDaddySoapClient.purchase(domain, price);

                            DBdetails dBdetails = myRepo.findByPlatformAndDomain("GoDaddy", domain);

                            logger.info(endTime);
                       /* Date d = null;
                        try {
                            d = ft.parse(endTime);

                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                        }*/
                            Date now = new Date();
                            String time = timeft.format(now);
                            if (place.getIsValid().equals("True")) {
                                now.setSeconds(now.getSeconds() + 30);
                                CheckOutbid checkOutbid = new CheckOutbid(domain, maxprice, price);
                                ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(checkOutbid, now, 40000);
                                checkOutbid.setScheduledFuture(scheduledFuture);
                                dBdetails.setResult("Bid Placed");
                                dBdetails.setIsBidPlaced(true);
                                notifRepo.save(new Notification("GoDaddy", time, "Scheduled Bid PLACED for " + domain + " at price " + price));
                                logger.info(time + ": Scheduled Bid Placed of domain: " + domain + " at price " + price);
                                telegram.sendAlert(-1001763199668l,1004l, "GoDaddy: Scheduled Bid Placed of domain: " + domain + " at price " + price);
                                updateTaskMap(domain,scheduledFuture,"cb");

                            } else {
                                dBdetails.setResult("Bid Not Placed");
                                dBdetails.setIsBidPlaced(false);
                                notifRepo.save(new Notification("GoDaddy", time, "Scheduled Bid NOT PLACED for " + domain + " at price " + price));
                                logger.info(time + ": Scheduled Bid Not Placed of domain: " + domain + " at price " + price);
                                telegram.sendAlert(-1001763199668l,1004l, "GoDaddy: Scheduled Bid Not Placed of domain: " + domain + " at price " + price);
                                deleteTaskMap(domain);
                            }
                        /*dBdetails.setTime_left(timeLeft);
                        dBdetails.setEndTimepst(endTimepst);
                        dBdetails.setEndTimeist(endTimeist);
                        dBdetails.setCurrbid(res.getPrice());
                        dBdetails.setAuctiontype(res.getAuctionModel());*/

                            myRepo.save(dBdetails);
                        } else {
                            Date d = null;
                            try {
                                d = ft.parse(endTime);
                                d.setSeconds(d.getSeconds()-10);
                            } catch (ParseException p) {
                                logger.info(p.getMessage());

                            }
                            //String timeLeft= relTime(d);

                          ScheduledFuture place=  taskScheduler.schedule(new Schedulebid(domain, maxprice, endTime), d);
                            Date now = new Date();
                            String time = timeft.format(now);
                            String endTimeist = ft1.format(d);
                            notifRepo.save(new Notification("GoDaddy", time, "Prechecking.. Bid SCHEDULED for " + domain + " at price " + price + " at time " + endTimeist));
                            telegram.sendAlert(-1001763199668l,1004l, time + ": GoDaddy: Prechecking.. Bid SCHEDULED for " + domain + " at price " + price + " at time " + endTimeist);
                            logger.info(time + ": Prechecking.. Bid SCHEDULED for " + domain + " at price " + price + " time " + endTimeist);
                            updateTaskMap(domain,place,"pb");

                        }
                    } else {
                        //notify
                        logger.info(endTime);
                        Date d = null;
                        try {
                            d = ft.parse(endTime);
                            System.out.println(d);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                            //continue;
                        }

                        String timeLeft = relTime(d);
                        DBdetails db = myRepo.findByPlatformAndDomain("GoDaddy", domain);
                        String text = "GoDaddy Auction OUTBID\n\n" + domain + "\n \nTime Left: " + timeLeft + "\nCurrent Bid: " + price1 + "\nMin Next Bid: " + pricei + "\nOur Max Bid: " + maxprice + "\n \nGDV: " + db.getGdv() + " \n\nLink: " + "https://in.godaddy.com/domainsearch/find?checkAvail=1&domainToCheck=" + domain;
                        //-1001814695777L
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("Bid", "b gd " + "gd" + " " + domain + " " + price));
                        row.add(new InlineKeyboardButton("Watch", "w gd " + "gd" + " " + domain));
                        row.add(new InlineKeyboardButton("Track","t gd "+"gd"+" "+domain));
                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                        rows.add(row);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                        Object obj = telegram.sendKeyboard(new SendMessage(-1001866615838L
                                , text, inlineKeyboardMarkup));
                        Date now = new Date();
                        String time = timeft.format(now);
                        notifRepo.save(new Notification("GoDaddy", time, "Domain: " + domain + " with our max price " + maxprice + " OUTBID at price " + price1));
                        logger.info(time + ":Domain: " + domain + " with our max price " + maxprice + " Outbid at price " + price1);

                        db.setResult("Outbid");
                        myRepo.save(db);
                        now.setMinutes(now.getMinutes() + 30);
                       ScheduledFuture res1= taskScheduler.schedule(new GetResultGD(domain, domain), now);
                        updateTaskMap(domain,res1,"gr");

                    }
                }
                else
                {
                    DBdetails dBdetails = myRepo.findByPlatformAndDomain("GoDaddy", domain);
                    Date now = new Date();
                    String time = timeft.format(now);
                    dBdetails.setResult("Bid Not Placed");
                    dBdetails.setIsBidPlaced(false);
                    notifRepo.save(new Notification("GoDaddy", time, "Scheduled Bid NOT PLACED for " + domain + " with message: "+res.getMessage()));
                    logger.info(time + ": Scheduled Bid Not Placed of domain: " + domain + " with message: "+res.getMessage());
                    telegram.sendAlert(-834797664L, "GoDaddy: Scheduled Bid Not Placed of domain: " + domain + " with message: "+res.getMessage());
                    deleteTaskMap(domain);
                }
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
                telegram.sendAlert(-834797664L,e.getMessage());
            }
        }
    }

public class GetResultGD implements Runnable
{
    String domain;
    String id;

    public GetResultGD(String domain, String id) {
        this.domain = domain;
        this.id = id;
    }

    @Override
    public void run() {
        List<Lauction> losings= goDaddySoapClient1.getLosings().getLauctionList();
        for(int i=0;i< losings.size();i++)
        {
            Lauction auction= losings.get(i);
            if(domain.equalsIgnoreCase(auction.getName()))
            {
                String price= auction.getPrice().substring(1,auction.getPrice().length()-4);
                Date now= new Date();
                String time= timeft.format(now);
                telegram.sendAlert(-1001763199668l,841l, "GoDaddy: Hush!! Lost auction of "+domain+" at price: "+price);
                notifRepo.save(new Notification("GoDaddy",time,"Hushh!! Lost auction of "+domain+" at price: "+price));
                logger.info(time+": Lost auction of "+domain+" at price: "+price);

                DBdetails db= myRepo.findByPlatformAndDomain("GoDaddy",domain);
               db.setResult("Loss");
               String endTime=auction.getAuctionEndTime();
                Date d = null;
                try {
                    d = ftr.parse(endTime);

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                String endTimeist= ft1.format(d);
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setCurrbid(price);
                myRepo.save(db);
                deleteTaskMap(domain);
                return;
            }
        }
        List<Lauction> winnings= goDaddySoapClient1.getWinnings().getLauctionList();
        for(int i=0;i< winnings.size();i++)
        {
            Lauction auction= winnings.get(i);
            if(domain.equalsIgnoreCase(auction.getName()))
            {
                String price= auction.getPrice().substring(1,auction.getPrice().length()-4);
              /*  Date now= new Date();
                String time= timeft.format(now);
                notifRepo.save(new Notification("GoDaddy",time,"Yippee!! Won auction of "+domain+" at price: "+price));
               logger.info(time+": Won auction of "+domain+" at price: "+price);
*/
                DBdetails db= myRepo.findByPlatformAndDomain("GoDaddy",domain);
                db.setResult("Won");
                String endTime=auction.getAuctionEndTime();
                Date d = null;
                try {
                    d = ftr.parse(endTime);

                } catch (ParseException p) {
                    logger.info(p.getMessage());
                }
                String endTimeist= ft1.format(d);
                db.setEndTimepst(endTime);
                db.setEndTimeist(endTimeist);
                db.setCurrbid(price);
                myRepo.save(db);
                deleteTaskMap(domain);
                return;
            }
        }
        Date now= new Date();
        now.setMinutes(now.getMinutes()+30);
       ScheduledFuture task= taskScheduler.schedule(new GetResultGD(domain,id), now);
       updateTaskMap(domain,task,"gr");
    }

}

    @PostMapping("/buygodaddycloseouts")
    List<Integer> buycloseouts(@RequestBody Closeouts closeouts)
    {

        List<String> list= closeouts.getCloseout();
        String price= closeouts.getPrice();
        int a=0;
        int n=list.size();
        for(int i=0;i<n;i++)
        {
            String domain= list.get(i).toLowerCase();
            try {
                EstimateCloseoutPrice est= goDaddySoapClient.estimateCloseoutPrice(domain);
                if(est.getResult().equals("Success")) {
                    //PlaceBid res = goDaddySoapClient.purchasecloseout(domain, price);
                    InstantPurchaseCloseout res= goDaddySoapClient.instantPurchaseCloseout(domain,est.getCloseoutDomainPriceKey());
                    if (res.getResult().equals("Success")) {
                        Date d = new Date();
                        String time = timeft.format(d);
                        logger.info(time + ": Instant Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price);
                        notifRepo.save(new Notification("GoDaddy", time, "Instant Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price));
                        telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Instant Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price);
                        a++;
                        //String endTime= res.getAuditDateTime().substring(0,19);
                        //logger.info(endTime);

                        ft1.setTimeZone(pst);
                        String endTime = ft1.format(d);
                        //d.setHours(d.getHours()+13);
                        //d.setMinutes(d.getMinutes()+30);
                        ft1.setTimeZone(ist);
                        String endTimeist = ft1.format(d);
                        Optional<Closeoutdb> o = Optional.ofNullable(closeoutrepo.findByDomain(domain));
                        Closeoutdb db = null;
                        if (!o.isPresent()) {
                            db = new Closeoutdb("GoDaddy", domain, price, endTime, endTimeist, "", "", "", "Bought");
                            db.setOurPrice(price);
                        } else {
                            db = o.get();
                            db.setEndTimeist(endTimeist);
                            db.setEndTime(endTime);
                            db.setCurrPrice(price);
                            db.setOurPrice(price);
                            db.setStatus("Bought");
                        }

                        closeoutrepo.save(db);

                    } else {
                        Date d = new Date();
                        String time = timeft.format(d);
                        notifRepo.save(new Notification("GoDaddy", time, "Could not bought closeout: " + domain + " at price: " + price + " with message: " + res.getMessage()));
                        logger.info(time + ": Could not bought closeout: " + domain + " at price: " + price + " with message: " + res.getMessage());
                        telegram.sendAlert(-1001763199668l,1005l, "Could not bought closeout: " + domain + " at price: " + price + " with message: " + res.getMessage());

                    }
                }
                else {
                    Date d = new Date();
                    String time = timeft.format(d);
                    notifRepo.save(new Notification("GoDaddy", time, "Could not bought closeout: " + domain + " at price: " + price + " with message: " + est.getMessage()));
                    logger.info(time + ": Could not bought closeout: " + domain + " at price: " + price + " with message: " + est.getMessage());
                    telegram.sendAlert(-1001763199668l,1005l, "Could not bought closeout: " + domain + " at price: " + price + " with message: " + est.getMessage());

                }
            }
            catch(Exception e)
            {
                Date d= new Date();
                String time= timeft.format(d);
                notifRepo.save(new Notification("GoDaddy",time,"Could not bought closeout: " + domain + " at price: " + price + " with message: " + e.getMessage()));
                logger.info(time+": Could not bought closeout: " + domain + " at price: " + price + " with message: " + e.getMessage());
                telegram.sendAlert(-834797664L,"Could not bought closeout: " + domain + " at price: " + price + " with message: " + e.getMessage());

            }
        }
        List<Integer> l= new ArrayList<>();
        l.add(a);
        l.add(n);
        return l;
    }

    @PutMapping("/watchlistedcloseout")
    Boolean watchlistingcloseouts(@RequestBody List<List<Long>> all)
    {
        List<Long> ids = all.get(0);
        List<Long> nids=all.get(1);

        for(int i=0;i<ids.size();i++)
        {
            Closeoutdb db= closeoutrepo.findById(ids.get(i)).get();
            db.setWatchlisted(true);
            closeoutrepo.save(db);
        }
        for(int i=0;i<nids.size();i++)
        {
            Closeoutdb db= closeoutrepo.findById(nids.get(i)).get();
            db.setWatchlisted(false);
            closeoutrepo.save(db);
        }

        return true;
    }

    @PutMapping("/removecloseoutwatchlist")
    void removeCloseoutWatchlist(@RequestBody List<Long> ids)
    {
        for(int i=0;i<ids.size();i++)
        {
            Closeoutdb db= closeoutrepo.findById(ids.get(i)).get();
            db.setWatchlisted(false);
            logger.info(db.getDomain());
            closeoutrepo.save(db);
        }
    }

    @GetMapping("/getWatchlistCloseouts")
    List<Closeoutdb> getWatchlistCloseouts()
    {
        List<Closeoutdb>  list= closeoutrepo.findByWatchlistedTrue();
        for(int i=0;i<list.size();i++)
        {
            Closeoutdb db= list.get(i);
            String domain= db.getDomain();
            try{
            GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
            if(res.getIsValid().equals("True"))
            {
            String endTime= res.getAuctionEndTime().substring(0,19);
            // logger.info(endTime);
            Date d=null;
            try {
                d=ft.parse(endTime);

            }
            catch(ParseException p)
            {
                logger.info(p.getMessage());
                continue;
            }

            String endTimeist= ft1.format(d);
            String timeLeft= relTime(d);
                db.setAuctype(res.getAuctionModel());
                db.setEndTimeist(endTimeist);
                db.setEndTime(endTime);
                db.setCurrPrice(res.getPrice());
                db.setTimeLeft(timeLeft);
            closeoutrepo.save(db);
        }
            else
        {

            Date now= new Date();
            String time = timeft.format(now);
            db.setWatchlisted(false);
            list.remove(i);
            closeoutrepo.save(db);
            notifRepo.save(new Notification("GoDaddy",time,"Watchlisted Closeout: " + domain + " details not fetched with error: " + res.getMessage()));
            logger.info(time+": Watchlisted Closeout: " + domain + " details not fetched with error: " + res.getMessage());
            telegram.sendAlert(-834797664L,"GoDaddy: Watchlisted Closeout: "+domain+" details not fetched with message: "+ res.getMessage());

        }
    }
            catch(Exception e)
    {
        Date now= new Date();
        String time = timeft.format(now);
        notifRepo.save(new Notification("GoDaddy",time,"Watchlisted Closeout: " + domain + " details not fetched with error: " + e.getMessage()));
        logger.info(time+": Watchlisted Closeout: " + domain + " details not fetched with error: " + e.getMessage());
        telegram.sendAlert(-834797664L,"GoDaddy: Watchlisted Closeout: "+domain+" details not fetched with message: "+ e.getMessage());
    }
        }
        return closeoutrepo.findByWatchlistedTrueOrderByEndTimeist();
    }

    @PostMapping("/bulkfetchcloseoutsgodaddy")
    List<Closeoutdb> fetchcloseouts(@RequestBody List<String> dlist)
    {

        List<Closeoutdb> list= new ArrayList<>();
        for(int i=0;i<dlist.size();i++)
        {
            String domain= dlist.get(i).toLowerCase();
            try {
                GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                if(res.getIsValid().equals("True"))
                {
                    String endTime= res.getAuctionEndTime().substring(0,19);
                   // logger.info(endTime);
                    Date d=null;
                    try {
                        d=ft.parse(endTime);

                    }
                    catch(ParseException p)
                    {
                        logger.info(p.getMessage());
                        continue;
                    }

                    String endTimeist= ft1.format(d);
                    String timeLeft= relTime(d);
                    Optional<Closeoutdb> o= Optional.ofNullable(closeoutrepo.findByDomain(domain));
                    Closeoutdb db= null;
                    if(!o.isPresent())
                    {
                        db=new Closeoutdb("GoDaddy",domain,res.getPrice(),endTime,endTimeist,timeLeft,res.getValuationPrice(),res.getAuctionModel(),"");
                    }
                    else
                    {
                        db=o.get();
                        db.setAuctype(res.getAuctionModel());
                        db.setEndTimeist(endTimeist);
                        db.setEndTime(endTime);
                        db.setCurrPrice(res.getPrice());
                        db.setTimeLeft(timeLeft);
                    }
                    list.add(db);
                    closeoutrepo.save(db);
                }
                else
                {

                    Date now= new Date();
                    String time = timeft.format(now);
                    notifRepo.save(new Notification("GoDaddy",time,"Domain details NOT FETCHED for " + domain + " with error: " + res.getMessage()));
                    logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + res.getMessage());
                    telegram.sendAlert(-834797664L,"GoDaddy: Could not fetch detail of Closeout: "+domain+" with message: "+ res.getMessage());

                }
            }
            catch(Exception e)
            {
                Date now= new Date();
                String time = timeft.format(now);
                notifRepo.save(new Notification("GoDaddy",time,"Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage()));
                logger.info(time+": Domain details NOT FETCHED for " + domain + " with error: " + e.getMessage());
                telegram.sendAlert(-834797664L,"GoDaddy: Could not fetch detail of Closeout: "+domain+" with message: "+ e.getMessage());
            }
        }
        return list;
    }

    @GetMapping("/getcompletedcloseouts")
    List<Closeoutdb> getcompletedcloseouts()
    {
        logger.info("getting completed closeouts");
        return closeoutrepo.findByStatusOrStatusOrStatusOrderByEndTimeistDesc("Bought","Lost","In Cart");
    }


    @GetMapping("/getscheduledcloseouts")
    List<Closeoutdb> getscheduledcloseouts()
    { logger.info("getting scheduled closeouts");
        List<Closeoutdb> list = closeoutrepo.findByStatusOrStatus("Closeout Scheduled","Closeout Recheck Scheduled");
        int n= list.size();
        for(int i=0;i<n;i++)
        {
            Closeoutdb db= list.get(i);
            String domain= db.getDomain();
            try{
                GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                if(res.getIsValid().equals("True"))
                {
                    if(!res.getAuctionModel().equals("BuyNow"))
                    { String bidCount= res.getBidCount();
                        logger.info(bidCount);
                        if (!bidCount.equals("0")) {
                            db.setStatus("Lost");
                            closeoutrepo.save(db);
                        }
                    }
                    else {
                        String currprice=res.getPrice().substring(1, res.getPrice().length());
                        String endTime = res.getAuctionEndTime().substring(0, 19);
                        // logger.info(endTime);
                        int dif= mapt.getOrDefault(currprice,6)-mapt.get(db.getOurPrice())-1;
                        //int dif= mapt.get(currprice)-mapt.get(db.getOurPrice());
                        Date d = null;
                        try {
                            d = ft.parse(endTime);
                            d.setDate(d.getDate()+dif);

                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                            continue;
                        }
                        String endTimeist = ft1.format(d);
                        String timeLeft = relTime(d);
                        db.setAuctype(res.getAuctionModel());
                        db.setEndTimeist(endTimeist);
                        db.setEndTime(endTime);
                        db.setCurrPrice(res.getPrice());
                        db.setTimeLeft(timeLeft);
                        closeoutrepo.save(db);
                    }
                }
                else {
                    db.setStatus("Lost");
                    closeoutrepo.save(db);
                }
            }
            catch (Exception e)
            {
                logger.info("While getting scheduled closeouts: "+e.getMessage());
            }
        }
        return closeoutrepo.findByStatusOrStatusOrderByEndTimeist("Closeout Scheduled","Closeout Recheck Scheduled");
    }

    void scheduleCloseoutSingle(String domain, String price)
    {
        if(price.equals("50"))
        {
                //System.out.println(employee);
                try {
                    GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                    if(res.getIsValid().equals("True"))
                    {

                        String currprice=res.getPrice().substring(1, res.getPrice().length());
                        if(!res.getAuctionModel().equals("BuyNow")) {
                            String endTime = res.getAuctionEndTime().substring(0, 19);
                            logger.info(endTime);
                            Date d = null;
                            try {
                                d = ft.parse(endTime);
                                System.out.println(d);
                            } catch (ParseException p) {
                                logger.info(p.getMessage());
                            }
                            String endTimeist = ft1.format(d);
                            String timeLeft = relTime(d);
                            Optional<Closeoutdb> o = Optional.ofNullable(closeoutrepo.findByDomain(domain));

                            Closeoutdb db = null;
                            if (!o.isPresent()) {
                                db = new Closeoutdb("GoDaddy", domain, currprice, endTime, endTimeist, timeLeft, price, res.getValuationPrice(), res.getAuctionModel(), "");
                            } else {
                                db = o.get();
                                db.setAuctype(res.getAuctionModel());
                                db.setEndTimeist(endTimeist);
                                db.setEndTime(endTime);
                                db.setCurrPrice(currprice);
                                db.setTimeLeft(timeLeft);
                                db.setOurPrice(price);
                            }
                            Cron cron = new Cron(domain, price);

                            ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(cron, d, 1000);
                            db.setStatus("Closeout Scheduled");
                            Date now = new Date();
                            String time = timeft.format(now);
                            logger.info("Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                            notifRepo.save(new Notification("GoDaddy", time, "Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                            telegram.sendAlert(-834797664L, "GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);

                            cron.setScheduledFuture(scheduledFuture);
                            d.setMinutes(d.getMinutes() + 6);
                            ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture, domain), d);
                            cron.setScheduledFuture1(scheduledFuture1);
                            d.setMinutes(d.getMinutes() - 8);
                            taskScheduler.schedule(new CancelCron(domain, scheduledFuture, scheduledFuture1), d);

                            closeoutrepo.save(db);
                        }
                        else {
                            Date now = new Date();
                            String time = timeft.format(now);
                            logger.info("Closeout not scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice);
                            notifRepo.save(new Notification("GoDaddy", time, "Closeout scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice));
                            telegram.sendAlert(-834797664L, "GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice);

                        }
                    }
                    else
                    {
                        Date now= new Date();
                        String time=timeft.format(now);
                        logger.info("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());
                        notifRepo.save(new Notification("GoDaddy",time,"Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage()));
                        telegram.sendAlert(-834797664L,"GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());

                    }
                }
                catch(Exception e)
                {
                    Date now= new Date();
                    String time=timeft.format(now);
                    logger.info("Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());
                    notifRepo.save(new Notification("GoDaddy",time,"Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage()));
                    telegram.sendAlert(-834797664L,"GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());


                }


        }
        else
        {
            try
                {
                    GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                    if(res.getIsValid().equals("True")) {
                        String currprice=res.getPrice().substring(1, res.getPrice().length());
                        String auctype = res.getAuctionModel();

                        if((!auctype.equals("BuyNow"))||Integer.valueOf(price)<Integer.valueOf(currprice)) {

                        String endTime = res.getAuctionEndTime().substring(0, 19);
                        logger.info(endTime);
                        Date d = null;
                        try {
                            d = ft.parse(endTime);
                            System.out.println(d);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());

                        }
                        String endTimeist = ft1.format(d);
                        String timeLeft = relTime(d);
                        Optional<Closeoutdb> o = Optional.ofNullable(closeoutrepo.findByDomain(domain));
                        Closeoutdb db = null;
                        if (!o.isPresent()) {
                            db = new Closeoutdb("GoDaddy", domain, currprice, endTime, endTimeist, timeLeft, price, res.getValuationPrice(), res.getAuctionModel(), "");
                        } else {
                            db = o.get();
                            db.setAuctype(res.getAuctionModel());
                            db.setEndTimeist(endTimeist);
                            db.setEndTime(endTime);
                            db.setCurrPrice(currprice);
                            db.setTimeLeft(timeLeft);
                            db.setOurPrice(price);
                        }
                        if (!auctype.equals("BuyNow")) {
                            d.setMinutes(d.getMinutes() + 15);
                            taskScheduler.schedule(new Try(domain, price), d);
                            Date now= new Date();
                            String time=timeft.format(now);
                            logger.info("Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                            notifRepo.save(new Notification("GoDaddy",time,"Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                            telegram.sendAlert(-834797664L,"GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);

                            db.setStatus("Closeout Recheck Scheduled");


                        } else {

                            String price1 = currprice;
                            if (mapc.get(price1).equals(price)) {
                                Cron cron = new Cron(domain,price);

                                ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(cron, d, 1000);
                                cron.setScheduledFuture(scheduledFuture);
                                db.setStatus("Closeout Scheduled");
                                Date now= new Date();
                                String time=timeft.format(now);
                                logger.info("Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                                notifRepo.save(new Notification("GoDaddy",time,"Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                                telegram.sendAlert(-834797664L,"GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);

                                d.setMinutes(d.getMinutes() + 6);
                                ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture,domain), d);
                                d.setMinutes(d.getMinutes() - 8);
                                taskScheduler.schedule(new CancelCron(domain, scheduledFuture, scheduledFuture1), d);

                            } else {
                                d.setMinutes(d.getMinutes() + 11);
                                taskScheduler.schedule(new Try(domain, price), d);
                                db.setStatus("Closeout Recheck Scheduled");
                                logger.info("Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                                Date now= new Date();
                                String time=timeft.format(now);
                                notifRepo.save(new Notification("GoDaddy",time,"Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                                telegram.sendAlert(-834797664L,"GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);

                            }
                        }
                        closeoutrepo.save(db);
                        }
                        else
                        {
                            Date now = new Date();
                            String time = timeft.format(now);
                            logger.info("Closeout not scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice);
                            notifRepo.save(new Notification("GoDaddy", time, "Closeout scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice));
                            telegram.sendAlert(-834797664L, "GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice);

                        }

                    }
                    else
                    {
                        Date now= new Date();
                        String time=timeft.format(now);
                        logger.info("Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());
                        notifRepo.save(new Notification("GoDaddy",time,"Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage()));
                        telegram.sendAlert(-834797664L,"GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());


                    }
                }
                catch (Exception e)
                {
                    Date now= new Date();
                    String time=timeft.format(now);
                    logger.info("Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());
                    notifRepo.save(new Notification("GoDaddy",time,"Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage()));
                    telegram.sendAlert(-834797664L,"GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());


                }


        }
    }

    @PostMapping ("/schedulegodaddycloseouts")
    List<Integer> scheduleCloseouts(@RequestBody Closeouts closeouts)
    {
        List<Integer> list= new ArrayList<>();
        String price = closeouts.getPrice();
        List<String> closeout= closeouts.getCloseout();
        int n= closeout.size();
        int a=0;
        logger.info(closeout.get(0));
        if(price.equals("50"))
        {
            for(int i=0;i< closeout.size();i++)
            {
                String domain= closeout.get(i).toLowerCase();
                //System.out.println(employee);
                try {
                    GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                    if(res.getIsValid().equals("True"))
                    {
                        String currprice=res.getPrice().substring(1, res.getPrice().length());
                        if(!res.getAuctionModel().equals("BuyNow")) {
                            String endTime = res.getAuctionEndTime().substring(0, 19);
                            logger.info(endTime);
                            Date d = null;
                            try {
                                d = ft.parse(endTime);
                                System.out.println(d);
                            } catch (ParseException p) {
                                logger.info(p.getMessage());
                                continue;
                            }
                            String endTimeist = ft1.format(d);
                            String timeLeft = relTime(d);
                            Optional<Closeoutdb> o = Optional.ofNullable(closeoutrepo.findByDomain(domain));
                            Closeoutdb db = null;
                            if (!o.isPresent()) {
                                db = new Closeoutdb("GoDaddy", domain, currprice, endTime, endTimeist, timeLeft, price, res.getValuationPrice(), res.getAuctionModel(), "");
                            } else {
                                db = o.get();
                                db.setAuctype(res.getAuctionModel());
                                db.setEndTimeist(endTimeist);
                                db.setEndTime(endTime);
                                db.setCurrPrice(currprice);
                                db.setTimeLeft(timeLeft);
                                db.setOurPrice(price);
                            }
                            Cron cron = new Cron(domain, price);

                            ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(cron, d, 1000);
                            db.setStatus("Closeout Scheduled");
                            Date now = new Date();
                            String time = timeft.format(now);
                            logger.info("Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                            notifRepo.save(new Notification("GoDaddy", time, "Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                            telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);

                            a++;
                            cron.setScheduledFuture(scheduledFuture);
                            d.setMinutes(d.getMinutes() + 6);
                            ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture, domain), d);
                            cron.setScheduledFuture1(scheduledFuture1);
                            d.setMinutes(d.getMinutes() - 8);
                            taskScheduler.schedule(new CancelCron(domain, scheduledFuture, scheduledFuture1), d);

                            closeoutrepo.save(db);
                        }
                        else
                        {
                            Date now = new Date();
                            String time = timeft.format(now);
                            logger.info("Closeout not scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice);
                            notifRepo.save(new Notification("GoDaddy", time, "Closeout scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice));
                            telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice);

                        }
                    }
                    else
                    {
                        Date now= new Date();
                        String time=timeft.format(now);
                        logger.info("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());
                        notifRepo.save(new Notification("GoDaddy",time,"Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage()));
                        telegram.sendAlert(-1001763199668l,1005l,"GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());

                    }
                }
                catch(Exception e)
                {
                    Date now= new Date();
                    String time=timeft.format(now);
                    logger.info("Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());
                    notifRepo.save(new Notification("GoDaddy",time,"Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage()));
                    telegram.sendAlert(-834797664L,"GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());


                }

            }
        }
        else
        {
            for(int i=0;i< closeout.size();i++)
            {
                String domain=closeout.get(i).toLowerCase();

                try
                {
                  GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                  if(res.getIsValid().equals("True")) {
                      String currprice=res.getPrice().substring(1, res.getPrice().length());
                      String auctype = res.getAuctionModel();

                      if((!auctype.equals("BuyNow"))||Integer.valueOf(price)<Integer.valueOf(currprice)) {
                          String endTime = res.getAuctionEndTime().substring(0, 19);
                          logger.info(endTime);
                          Date d = null,d1=null;
                          try {
                              d = ft.parse(endTime);
                              int dif= mapt.getOrDefault(currprice,6)-mapt.get(price)-1;
                              d1= new Date(d.getTime()+dif*24*60*60*1000);

                          } catch (ParseException p) {
                              logger.info(p.getMessage());
                              continue;
                          }
                          String endTimeist = ft1.format(d1);
                          String timeLeft = relTime(d1);
                          Optional<Closeoutdb> o = Optional.ofNullable(closeoutrepo.findByDomain(domain));
                          Closeoutdb db = null;
                          if (!o.isPresent()) {
                              db = new Closeoutdb("GoDaddy", domain, currprice, endTime, endTimeist, timeLeft, price, res.getValuationPrice(), res.getAuctionModel(), "");
                          } else {
                              db = o.get();
                              db.setAuctype(res.getAuctionModel());
                              db.setEndTimeist(endTimeist);
                              db.setEndTime(endTime);
                              db.setCurrPrice(currprice);
                              db.setTimeLeft(timeLeft);
                              db.setOurPrice(price);
                          }
                          if (!auctype.equals("BuyNow")) {
                              d.setMinutes(d.getMinutes() + 15);
                              taskScheduler.schedule(new Try(domain, price), d);
                              Date now = new Date();
                              String time = timeft.format(now);
                              logger.info("Closeout SCHEDULED for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                              notifRepo.save(new Notification("GoDaddy", time, "Closeout SCHEDULED for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                              telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Closeout SCHEDULED for domain: " + domain + " with price: " + price + " at time: " + endTimeist);

                              db.setStatus("Closeout Recheck Scheduled");
                              a++;

                          } else {

                              String price1 = currprice;
                              if (mapc.get(price1).equals(price)) {
                                  Cron cron = new Cron(domain, price);

                                  ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(cron, d, 1000);
                                  cron.setScheduledFuture(scheduledFuture);
                                  a++;
                                  db.setStatus("Closeout Scheduled");
                                  Date now = new Date();
                                  String time = timeft.format(now);
                                  logger.info("Closeout SCHEDULED for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                                  notifRepo.save(new Notification("GoDaddy", time, "Closeout SCHEDULED for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                                  telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Closeout SCHEDULED for domain: " + domain + " with price: " + price + " at time: " + endTimeist);

                                  d.setMinutes(d.getMinutes() + 6);
                                  ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture, domain), d);

                                  cron.setScheduledFuture1(scheduledFuture1);
                                  d.setMinutes(d.getMinutes() - 8);
                                  taskScheduler.schedule(new CancelCron(domain, scheduledFuture, scheduledFuture1), d);

                              } else {
                                  d.setMinutes(d.getMinutes() + 11);
                                  taskScheduler.schedule(new Try(domain, price), d);
                                  db.setStatus("Closeout Recheck Scheduled");
                                  logger.info("Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                                  Date now = new Date();
                                  String time = timeft.format(now);
                                  notifRepo.save(new Notification("GoDaddy", time, "Closeout SCHEDULED for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                                  telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Closeout SCHEDULED for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                                  a++;
                              }
                          }
                          closeoutrepo.save(db);
                      }
                      else
                      {
                          Date now = new Date();
                          String time = timeft.format(now);
                          logger.info("Closeout not scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice);
                          notifRepo.save(new Notification("GoDaddy", time, "Closeout scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice));
                          telegram.sendAlert(-1001763199668l,1005l, "GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " as current price is "+currprice);

                      }
                  }
                  else
                  {
                      Date now= new Date();
                      String time=timeft.format(now);
                      logger.info("Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());
                      notifRepo.save(new Notification("GoDaddy",time,"Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage()));
                      telegram.sendAlert(-1001763199668l,1005l,"GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());


                  }
                }
                catch (Exception e)
                {
                    Date now= new Date();
                    String time=timeft.format(now);
                    logger.info("Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());
                    notifRepo.save(new Notification("GoDaddy",time,"Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage()));
                    telegram.sendAlert(-834797664L,"GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());


                }

            }
        }
        list.add(a);
        list.add(n);
        return list;
    }


    void schedulealgo(String domain, String price, Map<String,String> map)
    {
        TimeZone pst = TimeZone.getTimeZone("PST");
        TimeZone ist = TimeZone.getTimeZone("IST");
        ft.setTimeZone(pst);
        ft1.setTimeZone(ist);
        GetAuctionDetailsResponse g= goDaddySoapClient.getAuctionDetails(domain);
        String xmlString= g.getGetAuctionDetailsResult();
        JAXBContext jaxbContext;
        GetAuctionsDetailRes res=null;
        try {
            jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            //System.out.println(employee);
            String endTime = res.getAuctionEndTime().substring(0, 19);
            String price1 = res.getPrice().substring(1,res.getPrice().length());

            logger.info(endTime);
            Date d = null;
            try {
                d = ft.parse(endTime);
                System.out.println(d);
            } catch (ParseException p) {
                logger.info(p.getMessage());
                //continue;
            }
            if(map.get(price1).equals(price))
            {
                Cron cron =new Cron(domain,price);

                ScheduledFuture scheduledFuture= taskScheduler.scheduleAtFixedRate(cron,d,1000);
                cron.setScheduledFuture(scheduledFuture);
                d.setMinutes(d.getMinutes()+6);
                ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture,domain),d);
                d.setMinutes(d.getMinutes()-8);
                taskScheduler.schedule(new CancelCron(domain,scheduledFuture,scheduledFuture1),d);

            }
            else
            {
                d.setMinutes(d.getMinutes()+11);
                taskScheduler.schedule(new Try(domain,price),d);
            }

        }
        catch(JAXBException jb)
        {
           jb.printStackTrace();
        }
        }




    public class CancelCron implements Runnable
    {
        String domain;
        ScheduledFuture scheduledFuture,scheduledFuture1;
        public CancelCron(String domain, ScheduledFuture scheduledFuture, ScheduledFuture scheduledFuture1) {
            this.domain = domain;
            this.scheduledFuture = scheduledFuture;
            this.scheduledFuture1=scheduledFuture1;
        }

        @Override
        public void run()
        {
            GetAuctionDetailsResponse g= goDaddySoapClient.getAuctionDetails(domain);
            String xmlString= g.getGetAuctionDetailsResult();
            JAXBContext jaxbContext;
            GetAuctionsDetailRes res=null;

            try
            {
                jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

                res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
                if(res.getIsValid().equals("True"))
                {
                if(!res.getAuctionModel().equals("BuyNow"))
                    { String bidCount= res.getBidCount();
                        logger.info(bidCount);
                        if (!bidCount.equals("0")) {
                            scheduledFuture.cancel(true);
                            scheduledFuture1.cancel(true);
                            Closeoutdb db= closeoutrepo.findByDomain(domain);
                            db.setStatus("Lost");
                            closeoutrepo.save(db);
                        }
                    }
                }
                else
                {
                    scheduledFuture.cancel(true);
                    scheduledFuture1.cancel(true);
                    Closeoutdb db= closeoutrepo.findByDomain(domain);
                    db.setStatus("Lost");
                    closeoutrepo.save(db);
                }


                //System.out.println(employee);
            }
            catch (JAXBException e)
            {
                logger.info(e.getMessage());
            }
        }
    }

    public class Cron implements Runnable
    {
        String domain;
        String price;

        ScheduledFuture scheduledFuture;

        public ScheduledFuture getScheduledFuture1() {
            return scheduledFuture1;
        }

        public void setScheduledFuture1(ScheduledFuture scheduledFuture1) {
            this.scheduledFuture1 = scheduledFuture1;
        }

        ScheduledFuture scheduledFuture1;

        public ScheduledFuture getScheduledFuture() {
            return scheduledFuture;
        }

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public Cron(String domain, String price) {
            this.domain = domain;
            this.price = price;
        }

        @Override
        @Async
        public void run()
        {
asyncCalss.cron(domain,price,scheduledFuture,scheduledFuture1);
        }
    }


    String relTime(Date d1)
    {
        Date date = new Date();
        Long diff= d1.getTime()- date.getTime();

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



    public class Try implements Runnable
    {

String domain,price;

//Date d;

        public Try(String domain, String price) {
            this.domain = domain;
            this.price = price;

           // this.d = d;
        }

        @Override
        public void run()
        {
            TimeZone pst= TimeZone.getTimeZone("PST");
            TimeZone ist= TimeZone.getTimeZone("IST");
            ft.setTimeZone(pst);
            ft1.setTimeZone(ist);

            logger.info("Running Try Task");

            try{
                GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                if(res.getIsValid().equals("True")) {
                    String endTime = res.getAuctionEndTime().substring(0, 19);
                    String price1 = res.getPrice().substring(1, res.getPrice().length());

                    logger.info(endTime);
                    Date d = null;
                    try {
                        d = ft.parse(endTime);

                    } catch (ParseException p) {
                        logger.info(p.getMessage());
                        //continue;
                    }
                    String endTimeist = ft1.format(d);
                    String timeLeft = relTime(d);

                    Closeoutdb db = closeoutrepo.findByDomain(domain);

                    db.setAuctype(res.getAuctionModel());
                    db.setEndTimeist(endTimeist);
                    db.setEndTime(endTime);
                    db.setCurrPrice(price1);
                    db.setTimeLeft(timeLeft);
                    db.setOurPrice(price);

                    if (mapc.get(price1).equals(price)) {
                        Cron cron = new Cron(domain, price);

                        ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(cron, d, 1000);
                        cron.setScheduledFuture(scheduledFuture);
                        db.setStatus("Closeout Scheduled");
                        Date now= new Date();
                        String time=timeft.format(now);
                        logger.info("Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                        notifRepo.save(new Notification("GoDaddy",time,"Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                        telegram.sendAlert(-1001763199668l,1004l,"GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);

                        d.setMinutes(d.getMinutes() + 6);
                        ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture,domain), d);
                        cron.setScheduledFuture1(scheduledFuture1);
                        d.setMinutes(d.getMinutes() - 8);
                        taskScheduler.schedule(new CancelCron(domain, scheduledFuture, scheduledFuture1), d);

                    } else {
                        d.setMinutes(d.getMinutes() + 10);
                        taskScheduler.schedule(new Try(domain, price), d);
                        db.setStatus("Closeout Recheck Scheduled");
                        Date now= new Date();
                        String time=timeft.format(now);
                        logger.info("Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                        telegram.sendAlert(-1001763199668l,1004l,"GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                        notifRepo.save(new Notification("GoDaddy",time,"Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));


                    }

                    closeoutrepo.save(db);
                }
                else
                {
                    Date now= new Date();
                    String time=timeft.format(now);
                    logger.info("While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());
                    notifRepo.save(new Notification("GoDaddy",time,"While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage()));
                    telegram.sendAlert(-834797664L,"GoDaddy: While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());

                }
            }
            catch(Exception e)
            {
                Date now= new Date();
                String time=timeft.format(now);
                logger.info("GoDaddy: While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());
                notifRepo.save(new Notification("GoDaddy",time,"While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage()));
                telegram.sendAlert(-834797664L,"GoDaddy: While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());

            }
        }
    }



    public class StopCron implements Runnable
    {
        ScheduledFuture scheduledFuture;
        String domain;
        public StopCron(ScheduledFuture scheduledFuture, String domain) {
            this.scheduledFuture = scheduledFuture; this.domain= domain;
        }

        @Override
        public void run()
        {
            if(!scheduledFuture.isCancelled())
            {
                scheduledFuture.cancel(false);
            Closeoutdb db= closeoutrepo.findByDomain(domain);
            db.setStatus("Lost");
            closeoutrepo.save(db);
            }
        }
    }


    ScheduledFuture t,f;

    @GetMapping("/start1")
    void start1(@RequestParam Boolean b)
    {
        if(b)
        t=taskScheduler.scheduleAtFixedRate(new Cron1("abc.com","5"),1000);
        else
            f= taskScheduler.scheduleAtFixedRate(new Cron2("abc.com","5"),1000);

    }

    @GetMapping("/stop1")

    void stop1(@RequestParam Boolean b)
    {
        if(b)
        t.cancel(true);
        else f.cancel(true);
    }

    public class Cron1 implements Runnable
    {
        String domain;
        String price;

        ScheduledFuture scheduledFuture;

        public ScheduledFuture getScheduledFuture1() {
            return scheduledFuture1;
        }

        public void setScheduledFuture1(ScheduledFuture scheduledFuture1) {
            this.scheduledFuture1 = scheduledFuture1;
        }

        ScheduledFuture scheduledFuture1;

        public ScheduledFuture getScheduledFuture() {
            return scheduledFuture;
        }

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public Cron1(String domain, String price) {
            this.domain = domain;
            this.price = price;
        }

        @Override
        @Async
        public void run()
        {
            asyncCalss.cron(domain,price,scheduledFuture,scheduledFuture1);
        }
    }

    public class Cron2 implements Runnable
    {
        String domain;
        String price;

        public Cron2(String domain, String price) {
            this.domain = domain;
            this.price = price;
        }

        @Override
        @Async
        public void run()
        {
            cron1(domain,price);
        }
    }

    public void cron1(String domain, String price)
    {
        CompletableFuture.runAsync(()->{
        try {
            PlaceBid p=null;

            p = goDaddySoapClient.purchasecloseout(domain, price);
logger.info("1");
            if (p.getIsValid().equals("True"))
            {
                logger.info("GoDaddy: Scheduled Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price);
                notifRepo.save(new Notification("GoDaddy: Scheduled Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price));
                telegram.sendAlert(-834797664L, "GoDaddy: Scheduled Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price);
                Closeoutdb db = closeoutrepo.findByDomain(domain);
                db.setCurrPrice(price);
                db.setStatus("Bought");
                closeoutrepo.save(db);
                //scheduledFuture.cancel(true);
            }
            else
            {
                logger.info(p.getIsValid());
            }

        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }},threadPoolExecutor);

    }


}
