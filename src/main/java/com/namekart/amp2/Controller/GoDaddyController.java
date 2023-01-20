package com.namekart.amp2.Controller;

import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.Entity.LiveMap;
import com.namekart.amp2.Entity.Notification;
import com.namekart.amp2.Feign.GoDaddyFeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.GoDaddyEntities.*;
import com.namekart.amp2.GoDaddySoapClient;
import com.namekart.amp2.NamecheapEntity.Livencdb;
import com.namekart.amp2.Repository.*;
import com.namekart.amp2.stub.GetAuctionDetailsResponse;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class GoDaddyController {
    Logger logger= Logger.getLogger("GoDaddy");

    SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    String Authorization= "sso-key eoBX9S5CMVCy_BtxuibgTTSw5rVT2dwZWd9:EqNYRpNbEvuY6ATi2UNpUm";

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
        for(int i=0;i<n;i++)
        {
            String domain= domains.get(i).toLowerCase();
            try
            {
                GDAppraisalResp resp= goDaddyFeign.getGDV(Authorization,domain);
                list.add(resp);
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

    {taskScheduler.scheduleAtFixedRate(asyncCalss::try2,1000);
    }

    @GetMapping("/getlivegd")
    List<Lauction> getLive()
    {
        return liveGDrepo.findByLiveTrueOrderByIddDesc();
    }

    @GetMapping("/purchase")
    PlaceBid purchase(@RequestParam String domain, @RequestParam String price) {
        PlaceBid p = goDaddySoapClient.purchase(domain, price);
        return p;
    }

  void refreshscheduledbids()
  {
     List<DBdetails> list= myRepo.findByPlatformAndResult("GoDaddy","Bid Scheduled");
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
             d.setHours(d.getHours() + 13);
             d.setMinutes(d.getMinutes() + 30);
             String timeLeft = relTime(d);
             String endTimeist = ft1.format(d);

             dBdetails.setTime_left(timeLeft);
             dBdetails.setEndTimepst(endTimepst);
             dBdetails.setEndTimeist(endTimeist);
             dBdetails.setCurrbid(res.getPrice());
             dBdetails.setAuctiontype(res.getAuctionModel());
             myRepo.save(dBdetails);
         } else {
             logger.info("Could not fetch scheduled bid info for domain: " + domain+" with message: "+res.getIsValid());
             telegram.sendAlert(-834797664L, "Could not fetch scheduled bid info for domain: " + domain);

         }
     }
     catch(Exception e)
     {
         logger.info("Could not fetch scheduled bid info for domain: " + domain+" with exception: "+e.getMessage());
         telegram.sendAlert(-834797664L, "Could not fetch scheduled bid info for domain: " + domain+" with exception: "+e.getMessage());

     }
     }

  }

  @PostMapping("/bulkfetchgodaddy")
  List<DBdetails> bulkfetch(@RequestBody List<String> ddlist)
  {
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
                  d.setHours(d.getHours() + 13);
                  d.setMinutes(d.getMinutes() + 30);
                  String timeLeft = relTime(d);
                  String endTimeist = ft1.format(d);
                  Optional<DBdetails> op = Optional.ofNullable(myRepo.findByPlatformAndDomain("GoDaddy", domain));
                  DBdetails dBdetails = null;
                  if (!op.isPresent()) {
                      dBdetails = new DBdetails(domain, "GoDaddy", res.getPrice(), null, timeLeft, null, null, res.getAuctionModel(), "", "", endTimepst, endTimeist, "", false);
                  } else {
                      dBdetails = op.get();
                      dBdetails.setTime_left(timeLeft);
                      dBdetails.setEndTimepst(endTimepst);
                      dBdetails.setCurrbid(res.getPrice());
                      dBdetails.setEndTimeist(endTimeist);
                      dBdetails.setAuctiontype(res.getAuctionModel());

                  }
                  myRepo.save(dBdetails);
                  list.add(dBdetails);
              } else {
                  notifRepo.save(new Notification("GoDaddy: Domain details not fetched for domain: " + domain + " with error: " + res.getMessage()));
                  logger.info("GoDaddy: Domain details not fetched for domain: " + domain + " with error: " + res.getMessage());
              }
          }
          catch(Exception e)
          {
              notifRepo.save(new Notification("GoDaddy: Domain details not fetched for domain: " + domain + " with error: " + e.getMessage()));
              logger.info("GoDaddy: Domain details not fetched for domain: " + domain + " with error: " + e.getMessage());

          }
      }
      return list;
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
                d.setHours(d.getHours()+13);
                d.setMinutes(d.getMinutes()+30);
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
                    dBdetails.setCurrbid(res.getPrice());
                    dBdetails.setEndTimeist(endTimeist);
                    dBdetails.setAuctiontype(res.getAuctionModel());
                    dBdetails.setBidplacetime(bidplacetime);
                }
                if(place.getIsValid().equals("True"))
                {
                    a++;
                    dBdetails.setResult("Bid Placed");
                    dBdetails.setIsBidPlaced(true);
                    notifRepo.save(new Notification("GoDaddy: Instant Bid PLACED for " + domain + " at price " + price + " USD at " + new Date()));
                    logger.info("GoDaddy: Instant Bid Placed of domain: " + domain);
                    telegram.sendAlert(-834797664L,"GoDaddy: Instant Bid Placed of domain: " + domain);
                }
                else
                {
                    dBdetails.setResult("Bid Not Placed");
                    dBdetails.setIsBidPlaced(false);
                    notifRepo.save(new Notification("GoDaddy: Instant Bid NOT PLACED for " + domain + " at price " + price + " USD at " + new Date()));
                    logger.info("GoDaddy: Instant Bid Not Placed of domain: " + domain);
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

@GetMapping("/startlivegd")
List<Lauction> startlivegd()
{
    try {
       // logger.info("Starting live service");
        int p=1; int l=4; String ID="",PID="";
       // logger.info("1");

           // LiveMap lm= liveMaprepo.findById(1).get();
       // logger.info("2");
        LiveMap lm=null;

        if(liveMaprepo.findById(1).isPresent())
            lm = liveMaprepo.findById(1).get();
        else
        {
            lm= new LiveMap();
            liveMaprepo.save(lm);
        }
        Map<String,String> map= lm.getMapgd();
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
                if(!map.containsKey(id)) {
                    map.put(id, auction.getName().toLowerCase());
                    String timeLeft = auction.getTimeLeft().toLowerCase();
                    String[] arr= timeLeft.split(" ");
                    String a=arr[0];
                    int a1 = Integer.valueOf(a.substring(0,a.length()-1));

                    char a2 = timeLeft.charAt(a.length()-1);
                    auction.setTimeLeft(timeLeft);
                    ID=auction.getID();
                    liveGDrepo.save(auction);
                    if (a2=='h' && a1==l+1) {
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
            liveMaprepo.save(lm);
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

            LiveMap lm= liveMaprepo.findById(1).get();
            Map<String,String> map= lm.getMapgd();
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
                    if(!map.containsKey(id)) {
                        String domain=auction.getName().toLowerCase();
                        map.put(id,domain);
                        String timeLeft = auction.getTimeLeft().toLowerCase();
                        auction.setTimeLeft(timeLeft);
                        String addTime= ft2.format(new Date());
                        auction.setAddTime(addTime); auction.setLive(true);
                        liveGDrepo.save(auction);
                        domain = domain.replace('.','-');
                        String text= "GoDaddy Live Detect \n \n"+auction.getName()+"\n \nTime Left: "+timeLeft+"\nCurrent Bid: "+auction.getPrice()+" \n\nGDV: "+auction.getValuationPrice()+" \n\nLink: "+"https://in.godaddy.com/domain-auctions/"+domain+"-"+id;
                        try {
                            Object obj = telegram.sendAlert(-1001833712484L, text);
                        }
                        catch(RetryableException re)
                        {
                            logger.info(re.getMessage());
                            try {
                                Thread.sleep(30000);
                                Object obj = telegram.sendAlert(-1001833712484L, text);
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

                    }
                }
                p++;

            }
            liveMaprepo.save(lm);
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
                    continue;
                }
                d.setHours(d.getHours()+13);
                d.setMinutes(d.getMinutes()+30);
                String timeLeft= relTime(d);
                String endTimeist= ft1.format(d);
                d.setMinutes(d.getMinutes()-4);
                String bidplacetime= ft1.format(d);
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
                    dBdetails.setEndTimeist(endTimeist);
                    dBdetails.setCurrbid(res.getPrice());
                    dBdetails.setAuctiontype(res.getAuctionModel());
                }
                taskScheduler.schedule(new Schedulebid(domain,price), d);
                notifRepo.save(new Notification("GoDaddy: Bid SCHEDULED for " + domain + " at price " + price + " at time " + bidplacetime));
                telegram.sendAlert(-834797664L,"GoDaddy: Bid SCHEDULED for " + domain + " at price " + price + " at time " + bidplacetime);

                logger.info("GoDaddy: Bid SCHEDULED for " + domain + " at price " + price + " time " + bidplacetime);
                a++;
                dBdetails.setResult("Bid Scheduled");
                myRepo.save(dBdetails);

            }
            catch (JAXBException e)
            {

                notifRepo.save(new Notification("GoDaddy: Bid NOT SCHEDULED for " + domain + " at price " + price+". See log for detail."));
                telegram.sendAlert(-834797664L,"GoDaddy: Bid NOT SCHEDULED for " + domain + " at price " + price+". See log for detail.");
                telegram.sendAlert(-834797664L,e.getMessage());
                logger.info(e.getMessage());

            }
            catch(Exception e1)
            {
                telegram.sendAlert(-834797664L,e1.getMessage());
                logger.info(e1.getMessage());
            }
        }
        list.add(a);
        list.add(n);
        return null;
    }

    public class Schedulebid implements Runnable
    {
        String domain,price;

        public Schedulebid(String domain, String price) {
            this.domain = domain.toLowerCase();
            this.price = price;
        }

        @Override
        public void run()
        {
            try {
                PlaceBid place = goDaddySoapClient.purchase(domain, price);
                GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                DBdetails dBdetails= myRepo.findByPlatformAndDomain("GoDaddy",domain);
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
                d.setHours(d.getHours()+13);
                d.setMinutes(d.getMinutes()+30);
                String timeLeft= relTime(d);
                String endTimeist= ft1.format(d);
                dBdetails.setTime_left(timeLeft);
                dBdetails.setEndTimepst(endTimepst);
                dBdetails.setEndTimeist(endTimeist);
                dBdetails.setCurrbid(res.getPrice());
                dBdetails.setAuctiontype(res.getAuctionModel());
                if(place.getIsValid().equals("True"))
                {
                    dBdetails.setResult("Bid Placed");
                    dBdetails.setIsBidPlaced(true);
                    notifRepo.save(new Notification("GoDaddy: Scheduled Bid PLACED for " + domain + " at price " + price + " USD at " + new Date()));
                    logger.info("GoDaddy: Scheduled Bid Placed of domain: " + domain);
                    telegram.sendAlert(-834797664L,"GoDaddy: Scheduled Bid Placed of domain: " + domain);
                }
                else
                {
                    dBdetails.setResult("Bid Not Placed");
                    dBdetails.setIsBidPlaced(false);
                    notifRepo.save(new Notification("GoDaddy: Scheduled Bid NOT PLACED for " + domain + " at price " + price + " USD at " + new Date()));
                    logger.info("GoDaddy: Scheduled Bid Not Placed of domain: " + domain);
                    telegram.sendAlert(-834797664L,"GoDaddy: Scheduled Bid Not Placed of domain: " + domain);

                }
                myRepo.save(dBdetails);
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
                telegram.sendAlert(-834797664L,e.getMessage());
            }
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

                PlaceBid res = goDaddySoapClient.purchasecloseout(domain, price);
                if (res.getIsValid().equals("True")) {
                    logger.info("GoDaddy: Instant Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price);
                    notifRepo.save(new Notification("GoDaddy: Instant Closeout SUCCESSFULLY BOUGHT for domain: " + domain + " at price: " + price));
                    a++;
                    String endTime= res.getAuditDateTime().substring(0,19);
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
                    d.setHours(d.getHours()+13);
                    d.setMinutes(d.getMinutes()+30);
                    String endTimeist= ft1.format(d);
                    Optional<Closeoutdb> o= Optional.ofNullable(closeoutrepo.findByDomain(domain));
                    Closeoutdb db= null;
                    if(!o.isPresent())
                    {
                        db=new Closeoutdb("GoDaddy",domain,"",endTime,endTimeist,"","","","Bought");
                    }
                    else
                    {
                        db=o.get();
                        db.setEndTimeist(endTimeist);
                        db.setEndTime(endTime);
                       db.setOurPrice(price);
                       db.setStatus("Bought");
                    }

                    closeoutrepo.save(db);

                } else {
                    notifRepo.save(new Notification("Could not bought closeout: " + domain + " at price: " + price + " with message: " + res.getMessage()));
                    logger.info("Could not bought closeout: " + domain + " at price: " + price + " with message: " + res.getMessage());
                }
            }
            catch(Exception e)
            {
                notifRepo.save(new Notification("Could not bought closeout: " + domain + " at price: " + price + " with message: " + e.getMessage()));
                logger.info("Could not bought closeout: " + domain + " at price: " + price + " with message: " + e.getMessage());
            }
        }
        List<Integer> l= new ArrayList<>();
        l.add(a);
        l.add(n);
        return l;
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
                    d.setHours(d.getHours()+13);
                    d.setMinutes(d.getMinutes()+30);
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
                  logger.info("Could not fetch detail of Closeout: "+domain+" with message: "+ res.getMessage());
                  notifRepo.save(new Notification("GoDaddy: Could not fetch detail of Closeout: "+domain+" with message: "+ res.getMessage()));
                }
            }
            catch(Exception e)
            {
                logger.info("Could not fetch detail of Closeout: "+domain+" with message: "+ e.getMessage());
                notifRepo.save(new Notification("GoDaddy: Could not fetch detail of Closeout: "+domain+" with message: "+ e.getMessage()));
                logger.info(e.getMessage());
            }
        }
        return list;
    }

    @GetMapping("/getcompletedcloseouts")
    List<Closeoutdb> getcompletedcloseouts()
    {
        logger.info("getting completed closeouts");
        return closeoutrepo.findByStatusOrStatus("Bought","Lost");
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
                        String endTime = res.getAuctionEndTime().substring(0, 19);
                        // logger.info(endTime);
                        Date d = null;
                        try {
                            d = ft.parse(endTime);

                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                            continue;
                        }
                        d.setHours(d.getHours() + 13);
                        d.setMinutes(d.getMinutes() + 30);
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
        return list;
    }

    @PostMapping ("/schedulegodaddycloseouts")
    List<Integer> scheduleCloseouts(@RequestBody Closeouts closeouts)
    {
        List<Integer> list= new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("50","40");
        map.put("40","30");
        map.put("30","11");
        map.put("11","5");

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
                        d.setHours(d.getHours() + 13);
                        d.setMinutes(d.getMinutes() + 30);
                        String endTimeist = ft1.format(d);
                        String timeLeft = relTime(d);
                        Optional<Closeoutdb> o = Optional.ofNullable(closeoutrepo.findByDomain(domain));
                        Closeoutdb db = null;
                        if (!o.isPresent()) {
                            db = new Closeoutdb("GoDaddy", domain, res.getPrice(), endTime, endTimeist, timeLeft, price, res.getValuationPrice(), res.getAuctionModel(), "");
                        } else {
                            db = o.get();
                            db.setAuctype(res.getAuctionModel());
                            db.setEndTimeist(endTimeist);
                            db.setEndTime(endTime);
                            db.setCurrPrice(res.getPrice());
                            db.setTimeLeft(timeLeft);
                            db.setOurPrice(price);
                        }
                        Cron cron = new Cron(domain, price);

                        ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(cron, d, 1000);
                        db.setStatus("Closeout Scheduled");
                        logger.info("GoDaddy: Closeout scheduled for domain: "+domain+" with price: "+price+" at time: "+endTimeist);
                        notifRepo.save(new Notification("GoDaddy: Closeout scheduled for domain: "+domain+" with price: "+price+" at time: "+endTimeist));
                        a++;
                        cron.setScheduledFuture(scheduledFuture);
                        d.setMinutes(d.getMinutes() + 6);
                        ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture,domain), d);
                        d.setMinutes(d.getMinutes() - 8);
                        taskScheduler.schedule(new CancelCron(domain, scheduledFuture, scheduledFuture1), d);

                        closeoutrepo.save(db);
                    }
                    else
                    {
                        logger.info("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());
                        notifRepo.save(new Notification("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage()));
                    }
                }
                catch(Exception e)
                {
                    logger.info("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());
                    notifRepo.save(new Notification("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage()));

                }

            }
        }
        else
        {
            for(int i=0;i< closeout.size();i++)
            {
                String domain=closeout.get(i);

                try
                {
                  GetAuctionsDetailRes res= goDaddySoapClient.getAuctionDetail(domain);
                  if(res.getIsValid().equals("True")) {
                      String endTime = res.getAuctionEndTime().substring(0, 19);
                      String auctype = res.getAuctionModel();
                      logger.info(endTime);
                      Date d = null;
                      try {
                          d = ft.parse(endTime);
                          System.out.println(d);
                      } catch (ParseException p) {
                          logger.info(p.getMessage());
                          continue;
                      }
                      d.setHours(d.getHours() + 13);
                      d.setMinutes(d.getMinutes() + 30);
                      String endTimeist = ft1.format(d);
                      String timeLeft = relTime(d);
                      Optional<Closeoutdb> o = Optional.ofNullable(closeoutrepo.findByDomain(domain));
                      Closeoutdb db = null;
                      if (!o.isPresent()) {
                          db = new Closeoutdb("GoDaddy", domain, res.getPrice(), endTime, endTimeist, timeLeft, price, res.getValuationPrice(), res.getAuctionModel(), "");
                      } else {
                          db = o.get();
                          db.setAuctype(res.getAuctionModel());
                          db.setEndTimeist(endTimeist);
                          db.setEndTime(endTime);
                          db.setCurrPrice(res.getPrice());
                          db.setTimeLeft(timeLeft);
                          db.setOurPrice(price);
                      }
                      if (!auctype.equals("BuyNow")) {
                          d.setMinutes(d.getMinutes() + 15);
                          taskScheduler.schedule(new Try(domain, price, map), d);
                          logger.info("GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                          notifRepo.save(new Notification("GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                          db.setStatus("Closeout Recheck Scheduled");
                          a++;

                      } else {

                          String price1 = res.getPrice().substring(1, res.getPrice().length());
                          if (map.get(price1).equals(price)) {
                              Cron cron = new Cron(domain, price);

                              ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(cron, d, 1000);
                              cron.setScheduledFuture(scheduledFuture);
                              a++;
                              db.setStatus("Closeout Scheduled");
                              logger.info("GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                              notifRepo.save(new Notification("GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));

                              d.setMinutes(d.getMinutes() + 6);
                              ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture,domain), d);
                              d.setMinutes(d.getMinutes() - 8);
                              taskScheduler.schedule(new CancelCron(domain, scheduledFuture, scheduledFuture1), d);

                          } else {
                              d.setMinutes(d.getMinutes() + 11);
                              taskScheduler.schedule(new Try(domain, price, map), d);
                              db.setStatus("Closeout Recheck Scheduled");
                              logger.info("GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                              notifRepo.save(new Notification("GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                              a++;
                          }
                      }
                      closeoutrepo.save(db);

                  }
                  else
                  {
                      logger.info("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());
                      notifRepo.save(new Notification("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage()));

                  }
                }
                catch (Exception e)
                {
                    logger.info("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());
                    notifRepo.save(new Notification("GoDaddy: Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage()));

                }

            }
        }
        list.add(a);
        list.add(n);
        return list;
    }


    void schedulealgo(String domain, String price, Map<String,String> map)
    {
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
            d.setHours(d.getHours() + 13);
            d.setMinutes(d.getMinutes() + 30);
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
                taskScheduler.schedule(new Try(domain,price,map),d);
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
asyncCalss.cron(domain,price,scheduledFuture);
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
Map<String,String> map;
//Date d;

        public Try(String domain, String price, Map<String, String> map) {
            this.domain = domain;
            this.price = price;
            this.map = map;
           // this.d = d;
        }

        @Override
        public void run()
        {
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
                    d.setHours(d.getHours() + 13);
                    d.setMinutes(d.getMinutes() + 30);
                    String endTimeist = ft1.format(d);
                    String timeLeft = relTime(d);

                    Closeoutdb db = closeoutrepo.findByDomain(domain);

                    db.setAuctype(res.getAuctionModel());
                    db.setEndTimeist(endTimeist);
                    db.setEndTime(endTime);
                    db.setCurrPrice(res.getPrice());
                    db.setTimeLeft(timeLeft);
                    db.setOurPrice(price);

                    if (map.get(price1).equals(price)) {
                        Cron cron = new Cron(domain, price);

                        ScheduledFuture scheduledFuture = taskScheduler.scheduleAtFixedRate(cron, d, 1000);
                        cron.setScheduledFuture(scheduledFuture);
                        db.setStatus("Closeout Scheduled");
                        logger.info("GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                        notifRepo.save(new Notification("GoDaddy: Closeout scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));
                        d.setMinutes(d.getMinutes() + 6);
                        ScheduledFuture scheduledFuture1 = taskScheduler.schedule(new StopCron(scheduledFuture,domain), d);
                        d.setMinutes(d.getMinutes() - 8);
                        taskScheduler.schedule(new CancelCron(domain, scheduledFuture, scheduledFuture1), d);

                    } else {
                        d.setMinutes(d.getMinutes() + 10);
                        taskScheduler.schedule(new Try(domain, price, map), d);
                        db.setStatus("Closeout Recheck Scheduled");
                        logger.info("GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist);
                        notifRepo.save(new Notification("GoDaddy: Closeout RECHECK scheduled for domain: " + domain + " with price: " + price + " at time: " + endTimeist));


                    }

                    closeoutrepo.save(db);
                }
                else
                {
                    logger.info("GoDaddy: While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage());
                    notifRepo.save(new Notification("GoDaddy: While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+res.getMessage()));

                }
            }
            catch(Exception e)
            {
                logger.info("GoDaddy: While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage());
                notifRepo.save(new Notification("GoDaddy: While Rechecking Closeout not scheduled for domain: "+domain+" with reason: "+e.getMessage()));

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
scheduledFuture.cancel(false);
            Closeoutdb db= closeoutrepo.findByDomain(domain);
            db.setStatus("Lost");
            closeoutrepo.save(db);
        }
    }
}
