package com.namekart.amp2.Controller;

import com.namekart.amp2.DCEntity.AuctionDetailDC;
import com.namekart.amp2.DCEntity.AuctionResultdc;
import com.namekart.amp2.DCEntity.Authorise;
import com.namekart.amp2.DCEntity.MapWrap;
import com.namekart.amp2.Entity.*;
import com.namekart.amp2.Feign.DropCatchFeign;
import com.namekart.amp2.Feign.FeignSample;
import com.namekart.amp2.Feign.MyFeignClient;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.Repository.*;
import com.namekart.amp2.Service.Service;
//import com.namekart.amp2.Tasks.PlaceBid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
    LiveMaprepo liveMaprepo;

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

    static String key="8B8Y70UXd7o7D58A8rh7N829B629L9H8W9G7e7q9W8d";
    TimeZone istTime = TimeZone.getTimeZone("IST");

    ScheduledFuture scheduledFuture;
    String token="eyJhbGciOiJSUzI1NiIsImtpZCI6IkI2ODhCNTVDMUNFMDI5OUEwNjRCQjYyNzM5MkMxQkYyQjE1OEU0NjBSUzI1NiIsInR5cCI6ImF0K2p3dCIsIng1dCI6InRvaTFYQnpnS1pvR1M3WW5PU3diOHJGWTVHQSJ9.eyJuYmYiOjE2NjczODI0NjksImV4cCI6MTY2NzM4NDI2OSwiaXNzIjoiaHR0cHM6Ly9hdXRoLnRjZGV2b3BzLmNvbSIsImF1ZCI6WyJjbGllbnQiLCJkcm9wY2F0Y2hDbGllbnQiXSwiY2xpZW50X2lkIjoiYmFieXlvZGE6aGF3ayIsImp0aSI6IjQ3QkExMkY1RjQ5NkZBOTE3QzA3NTRGQTJEQkE1Q0RFIiwiaWF0IjoxNjY3MzgyNDY5LCJzY29wZSI6WyJjbGllbnQ6cmVhZC5hY2NvdW50SWQiLCJkcm9wY2F0Y2g6YXBpLnJlYWQiXX0.CVhCAoIJiKmwjluLxeBxLcoffsAVhaCtJj4ZfI40fOhDog_E0K3KSsYyMU-Bq6Y4ctJ6axDRC7QwbgNbYeDPIRRNLUNC9Jn7S4qr-00cjx17pRsB-WzNfrqhTEPi7n0wTNpsTBDWoPSbRq7S2-jyNXQ0IfEhc6r0PEWF-ebEiv_V3qmiTVu_XQ0jrT7pHxnrzHsCzNeXfGPUlIVx38lg8QZNeSNJsfkD78N0QJhgHTlGvJNnjZpKvXbyGJozldps2iTGpyh0-LoRn0b-ua78CQgY3mqAIcGQ7MZ-bR9KFEuZ0vRxNQtGBE_fY7iUf5R89tlSmeslkyC-_Hp8D9D7Kg";

    String bearer= "Bearer "+ token;
    //ft1.setTimeZone(istTime);

    @GetMapping("/getauctiondc1")
    AuctionDetailDC getAuctiondc1(@RequestParam int id)
    {
        AuctionDetailDC acdc= dropCatchFeign.getAuctionDetail(bearer, id).getBody();
        logger.info(acdc.toString());
        logger.info(acdc.getName());
        return acdc;
    }



 @PostMapping("/postDomains")
    List<Integer> mainmain(@RequestBody List<ArrayList<String>> ddlist)
    {

        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm z");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        List<Integer> l= new ArrayList<>();
        int a=0;
        int n= ddlist.size();
for(int i=0;i< ddlist.size();i++)
{
    String domain= ddlist.get(i).get(0).toLowerCase();
    String bid=ddlist.get(i).get(1);
    try {
        logger.info("no");
        Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
        logger.info(ra.getStatus());
        if (ra.getStatus().equals("success")) {

            Date date = null;
            logger.info("yes1");
            String pst = ra.getAuction_det().getAuction_json().getEnd_time();
            String pst1=pst.substring(0,17)+"IST";
            logger.info("yes2");
            try {
                date = ft.parse(pst1);
            } catch (ParseException p) {
                //str.add(domain);
                logger.info("Dynadot: Bid of domain " + domain + " not scheduled because of date parse exception");
                notifRepo.save(new Notification("Dynadot: Bid of domain " + domain + " not scheduled because of date parse exception"));
                continue;
            }
            date.setHours(date.getHours() + 13);
            date.setMinutes(date.getMinutes() + 30);
            String ist = ft1.format(date);
            System.out.println(ist);
String time_left= relTime(date);
            date.setMinutes(date.getMinutes() - 4);
            String bidist = ft1.format(date);
            System.out.println(date);
            taskScheduler.schedule(placeBid(domain, bid, key), date);

            a++;
            notifRepo.save(new Notification("Dynadot: Bid SCHEDULED for " + domain + " at price " + bid + " at time " + bidist));
            logger.info("Dynadot: Bid SCHEDULED for " + domain + " at price " + bid + " time " + date);
            Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
            DBdetails dBdetails = null;
            List<Bid_details> bd = ra.getAuction_details().get(0).getBid_history();
            Auction_json aj = ra.getAuction_det().getAuction_json();
            if (!op.isPresent()) {
                dBdetails = new DBdetails(domain, "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(),time_left, aj.getAge(), aj.getEstibot_appraisal(), aj.getAuction_type(), bid, "Bid Scheduled", pst, ist, bidist, false);
            } else {
                dBdetails = op.get();
                dBdetails.setResult("Bid Scheduled");
               // dBdetails.setIsBidPlaced(false);
                dBdetails.setBidAmount(bid);
                dBdetails.setBidplacetime(bidist);
                dBdetails.setCurrbid(aj.getCurrent_bid_price());
                dBdetails.setBidders(aj.getBidders());
                dBdetails.setTime_left(time_left);
                dBdetails.setAge(aj.getAge());
                dBdetails.setEstibot(aj.getEstibot_appraisal());
                dBdetails.setAuctiontype(aj.getAuction_type());
                dBdetails.setEndTimepst(pst);
                dBdetails.setEndTimeist(ist);
            }
            repo.save(dBdetails);
            /*if(bd!=null)
            for (int j = 0; j < bd.size(); j++) {
                DB_Bid_Details dbd = new DB_Bid_Details(dBdetails, bd.get(j));
                dBdetails.getBidhistory().add(dbd);
                bidhisrepo.save(dbd);
            }*/

        } else {
            //str.add(domain);
            String content = ra.getCont();
            notifRepo.save(new Notification("Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
            logger.info("Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);

        }
    }
    catch(Exception E)
    {
       String content= myFeignClient.getAuctionDetailstr(key,"get_auction_details",domain,"usd");
        logger.info("Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
        logger.info(E.getMessage());
       // str.add(domain);
        try {
            notifRepo.save(new Notification("Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
        }
        catch(Exception A)
        {
            notifRepo.save(new Notification("Dynadot: Bid NOT SCHEDULED for " + domain + " at price " + bid+". See log for reason"));

        }
    }
}
l.add(a);
l.add(n);
return l;
    }


    @PostMapping("/postDomainsinstant")
    List<Integer> mainmaininstant(@RequestBody List<ArrayList<String>> ddlist)
    {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm z");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
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

                    Date date = null;
                    String pst = ra.getAuction_details().getAuction_json().getEnd_time();
                    String pst1=pst.substring(0,17)+"IST";
                    try {
                        date = ft.parse(pst1);
                    } catch (ParseException p) {
                      //  str.add(domain);
                        logger.info("Dynadot: Bid of domain " + domain + " not placed because of date parse exception");
                        notifRepo.save(new Notification("Dynadot: Bid of domain " + domain + " not placed because of date parse exception"));
                        continue;
                    }
                    date.setHours(date.getHours() + 13);
                    date.setMinutes(date.getMinutes() + 30);

                    String time_left= relTime(date);
                    String ist = ft1.format(date);
                    System.out.println(ist);
                    date.setTime(date.getMinutes()+45);
                    taskScheduler.schedule(new GetResultdyna(domain),date);
                   Date date1= new Date();
                    String bidist = ft1.format(date1);
                    System.out.println(date);
                    notifRepo.save(new Notification("Dynadot: Instant Bid Placed for " + domain + " at price " + bid + " time " + bidist));
                    logger.info("Dynadot: Instant Bid Placed for " + domain + " at price " + bid + " time " + bidist);
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
                    DBdetails dBdetails = null;
                    List<Bid_details> bd = ra.getAuction_details().getBid_history();
                    Auction_json aj = ra.getAuction_details().getAuction_json();
                    if (!op.isPresent()) {
                        dBdetails = new DBdetails(domain, "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), aj.getEstibot_appraisal(), aj.getAuction_type(), bid, "Bid Placed", pst, ist, bidist, true);
                    } else {
                        dBdetails = op.get();
                        dBdetails.setResult("Bid Placed");
                        dBdetails.setIsBidPlaced(true);
                        dBdetails.setBidAmount(bid);
                        dBdetails.setBidplacetime(bidist);
                        dBdetails.setCurrbid(aj.getCurrent_bid_price());
                        dBdetails.setBidders(aj.getBidders());
                        dBdetails.setTime_left(time_left);
                        dBdetails.setAge(aj.getAge());
                        dBdetails.setEstibot(aj.getEstibot_appraisal());
                       // dBdetails.setAuctiontype(aj.getAuction_type());
                        dBdetails.setEndTimepst(pst);
                        dBdetails.setEndTimeist(ist);
                    }
                    repo.save(dBdetails);
                    /*if(bd!=null)
                    for (int j = 0; j < bd.size(); j++) {
                        DB_Bid_Details dbd = new DB_Bid_Details(dBdetails, bd.get(j));
                        dBdetails.getBidhistory().add(dbd);
                        bidhisrepo.save(dbd);
                    }*/

                } else {
                  //  str.add(domain);
                    String content = ra.getContent();
                    notifRepo.save(new Notification("Dynadot: Instant Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content));
                    logger.info("Dynadot: Instant Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content);

                }
            }
            catch(Exception E)
            {
                String content= myFeignClient.placeAuctionBidstr(key,"get_auction_details",domain,bid,"usd");
                notifRepo.save(new Notification("Dynadot: Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content));
                logger.info("Dynadot: Bid NOT PLACED for " + domain + " at price " + bid + " with error: " + content);
                //str.add(domain);
            }
        }
        l.add(a);
        l.add(n);
        return l;


    }

    @PostMapping("/postDomainsingle")
    boolean mainmainsingle(@RequestBody List<String> ddlist)
    {
            String domain= ddlist.get(0);
            String bid=ddlist.get(1);
            try {
                Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
                if (ra.getStatus().equals("success")) {
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm z");
                    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

                    Date date = null;
                    String pst = ra.getAuction_det().getAuction_json().getEnd_time();
                    String pst1= pst.substring(0,17)+"IST";

                    try {
                        date = ft.parse(pst1);
                    } catch (ParseException p) {
                        logger.info("Bid of domain " + domain + " not scheduled because of date parse exception");
                        notifRepo.save(new Notification("Bid of domain " + domain + " not scheduled because of date parse exception"));
                        return false;

                    }
                    date.setHours(date.getHours() + 12);
                    date.setMinutes(date.getMinutes() + 30);
                    String ist = ft1.format(date);
                    System.out.println(ist);
                    date.setMinutes(date.getMinutes() - 4);
                    String bidist = ft1.format(date);
                    System.out.println(date);
                    taskScheduler.schedule(placeBid(domain, bid, key), date);
                    notifRepo.save(new Notification("Bid SCHEDULED for " + domain + " at price " + bid + " time " + bidist));
                    logger.info("Bid SCHEDULED for " + domain + " at price " + bid + " time " + bidist);
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
                    DBdetails dBdetails = null;
                    List<Bid_details> bd = ra.getAuction_details().get(0).getBid_history();
                    Auction_json aj = ra.getAuction_det().getAuction_json();
                    if (!op.isPresent()) {
                        dBdetails = new DBdetails(domain, "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), aj.getTime_left(), aj.getAge(), aj.getEstibot_appraisal(), aj.getAuction_type(), bid, "Bid Scheduled", pst, ist, bidist, false);
                    } else {
                        dBdetails = op.get();
                        dBdetails.setResult("Bid Scheduled");
                        // dBdetails.setIsBidPlaced(false);
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
                    for (int j = 0; j < bd.size(); j++) {
                        DB_Bid_Details dbd = new DB_Bid_Details(dBdetails, bd.get(j));
                        dBdetails.getBidhistory().add(dbd);
                        bidhisrepo.save(dbd);
                    }
                      return false;
                } else {

                    String content = ra.getCont();
                    notifRepo.save(new Notification("Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
                    logger.info("Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                    return true;
                }
            }
            catch(Exception E)
            {
                String content= myFeignClient.getAuctionDetailstr(key,"get_auction_details",domain,"usd");
                notifRepo.save(new Notification("Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content));
                logger.info("Bid NOT SCHEDULED for " + domain + " at price " + bid + " with error: " + content);
                return true;
            }


    }

@GetMapping("/geterror")
Error2 getError(String domain)
{
   return myFeignClient.getAuctionError2(key,"get_auction_details",domain,"usd");
}


    @PostMapping("/postDomainsingleinstant")
    boolean mainmainsingleinstant(@RequestBody List<String> ddlist)
    {


            String domain= ddlist.get(0);
            String bid=ddlist.get(1);
            try {
                Response_PlaceBid ra = myFeignClient.placeAuctionBids(key, "place_auction_bid", domain, bid,"usd");
                if (ra.getStatus().equals("success")) {
                    SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm z");
                    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy/MM/dd HH:mm z");

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

    }

@PostMapping("/fetchdetailsdyna")
List<DBdetails> fetch(@RequestBody List<String> domains)
{
    int n= domains.size();
    List<DBdetails> l= new ArrayList<>();
    for(int i=0;i<n;i++) {
        String domain= domains.get(i).toLowerCase();
        try {
            Response_AuctionDetails rn = myFeignClient.getAuctionDetails(key, "get_auction_details", domain, "usd");
            logger.info(rn.toString());
            if (rn.getStatus().equals("success")) {
                logger.info("yes");
                Auction_json aj = rn.getAuction_details().get(0).getAuction_json();
                List<Bid_details> bd = rn.getAuction_details().get(0).getBid_history();
                SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm z");
                Date date;
                String pst = rn.getAuction_details().get(0).getAuction_json().getEnd_time();
                // String pst="2022/10/28 11:10 IST";
                String ist = pst.substring(0, 17) + "IST";

                date = ft.parse(ist);
                date.setHours(date.getHours() + 13);
                date.setMinutes(date.getMinutes() + 30);
                String time_left= relTime(date);
                ist = ft.format(date);
                System.out.println(ist);
                Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
                DBdetails dBdetails = null;
                if (!op.isPresent()) {
                    dBdetails = new DBdetails(domain, "Dynadot", aj.getCurrent_bid_price(), aj.getBidders(), time_left, aj.getAge(), aj.getEstibot_appraisal(), aj.getAuction_type(), "", "", pst, ist, "", false);
                } else {
                    dBdetails = op.get();
                    // dBdetails.setResult("Bid Placed");
                    //dBdetails.setIsBidPlaced(true);
                    //dBdetails.setBidAmount(bid);
                    //dBdetails.setBidplacetime(bidist);
                    dBdetails.setCurrbid(aj.getCurrent_bid_price());
                    dBdetails.setBidders(aj.getBidders());
                    dBdetails.setTime_left(time_left);
                    dBdetails.setAge(aj.getAge());
                    dBdetails.setEstibot(aj.getEstibot_appraisal());
                    dBdetails.setAuctiontype(aj.getAuction_type());
                    dBdetails.setEndTimepst(pst);
                    dBdetails.setEndTimeist(ist);
                }
                dBdetails.setFetched(true);
                repo.save(dBdetails);
               /* if(bd!=null)
                { for (int j = 0; j < bd.size(); j++) {
                    DB_Bid_Details dbd = new DB_Bid_Details(dBdetails, bd.get(j));
                    dBdetails.getBidhistory().add(dbd);
                    bidhisrepo.save(dbd);
                }}*/
                l.add(dBdetails);
            } else {
                String content = rn.getCont();
                logger.info("no");
                notifRepo.save(new Notification("Domain details NOT FETCHED for " + domain + " with error: " + content));
                logger.info("Domain details NOT FETCHED for " + domain + " with error: " + content);

            }
        } catch (Exception E) {
            logger.info(E.getMessage());
            String content = myFeignClient.getAuctionDetailstr(key, "get_auction_details", domain, "usd");
            notifRepo.save(new Notification("Domain details NOT FETCHED for " + domain + " with error: " + content));
            logger.info("Domain Details NOT FETCHED for " + domain + " at price " + " with error: " + content);

        }
    }
    return l;
}

    @PostMapping("/postdb")
    boolean addDb(@RequestBody DBdetails db)
    {
        repo.save(db);
        return true;
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
    {Date date = new Date();
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

        List<DBdetails> list = repo.findByResultOrResult("Won","Loss");

        return list;
    }

    @GetMapping("/getwatchlist")
    List<DBdetails> getWatchlist()
    {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        List<DBdetails> list= repo.findByWatchlistTrue();
        int n=list.size(); Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;
       /* MapWrap mw = mapwraprepo.getById(1);
        Map<String, Long> map = mw.getMap();
        Map<Long,String> rm= mw.getRm();*/
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
                    AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
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
            { db.setWatchlist(false);
                repo.save(db);
                logger.info("Watchlist rendering exception: "+db.getPlatform()+" " +domain+" "+e.getMessage());}
        }
        return repo.findByWatchlistTrue();
    }

    @PutMapping("/watchlisted/{domain}")
    Boolean watchlisted(@PathVariable String domain)
    {
        DBdetails db= repo.findByDomain(domain);
        db.setWatchlist(true);
        return db.isWatchlist();
    }

    @PutMapping("/watchlisted")
    Boolean watchlisting(@RequestBody List<List<Long>> all)
    {
        List<Long> ids = all.get(0);
        List<Long> nids=all.get(1);

        for(int i=0;i<ids.size();i++)
        {
            DBdetails db= repo.findById(ids.get(i)).get();
            db.setWatchlist(true);
            logger.info(db.getDomain());
            if(db.isWatchlist())
           logger.info("true");
            else
                logger.info("false");
            repo.save(db);
        }
        for(int i=0;i<nids.size();i++)
        {
            DBdetails db= repo.findById(nids.get(i)).get();
            db.setWatchlist(false);
            logger.info(db.getDomain());
            if(db.isWatchlist())
                logger.info("true");
            else
                logger.info("false");

            repo.save(db);
        }

        return true;
    }

    @GetMapping("/getnotifications")
    List<Notification> getnotif()
    {
        List<Notification> db = notifRepo.findByOrderByIDDesc();
        logger.info("notif");
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

    @GetMapping("/startlive")
    Boolean liveStart()
    {
        logger.info("Starting Dynadot Live Service");
        try {
            scheduledFuture.cancel(true);
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }
        LiveMap lm=null;

        if(liveMaprepo.findById(1).isPresent())
        lm = liveMaprepo.findById(1).get();
        else
        {
            lm= new LiveMap();
            liveMaprepo.save(lm);
        }
        Map<String,Long> map= lm.getMap();
        map.clear();
        liveMaprepo.save(lm);
       /* List<LiveDetails> live= liveRepo.findByLiveTrueOrderByIdDesc();
        int n1= live.size();
        for(int i=0;i<n1;i++)
        {
            LiveDetails ld = live.get(i);
            ld.setLive(false);
            liveRepo.save(ld);
        }*/
        liveRepo.deleteAll();
        Date date= new Date();
        date.setHours(date.getHours()+4);
        Long time= date.getTime();
        ResponseLive rl = myFeignClient.getLiveDetails(key,"get_open_auctions","usd");
        List<LiveDetails> list = rl.getAuction_list();
        int n= list.size();

        for(int i=0;i<n;i++)
        {
            LiveDetails ld= list.get(i);
            Long auction_id= ld.getAuction_id();
            int bids = ld.getBids();
            Long endtime= ld.getEnd_time_stamp();
            if(bids>0&&endtime<=time)
            {
                map.put(ld.getDomain().toLowerCase(),ld.getAuction_id());
                ld.setPlatform("Dynadot");
                liveRepo.save(ld);
               /* Optional<LiveDetails> op = Optional.ofNullable(liveRepo.findByAuctionid(auction_id));
                if(!op.isPresent()) {
                    ld.setPlatform("Dynadot");
                    liveRepo.save(ld);
                }*/
            }
        }
        liveMaprepo.save(lm);
        logger.info("Started Dynadot Live Service");
        scheduledFuture= taskScheduler.scheduleWithFixedDelay(new DetectLive(time),18000);
taskScheduler.schedule(new StopLiveDyna(),date);
        return true;
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
        return liveRepo.findByLiveTrueOrderByIdDesc();
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
logger.info(s);
        return s;
    }

    String relTime(Long t2)
    {
       Long t1= System.currentTimeMillis();
        long diff = t2 - t1;
        String s="";
        long min = TimeUnit.MILLISECONDS.toMinutes(diff)%60;

            s=min+"m"+s;

        long h = TimeUnit.MILLISECONDS.toHours(diff)%24;
            s=h+"h, "+s;

        long d = TimeUnit.MILLISECONDS.toDays(diff)%365;
            s=d+"d, "+s;

        logger.info(s);
        return s;
    }

    String relTimelive(Long t2)
    {
        Long t1= System.currentTimeMillis();
        long diff = t2 - t1;
        String s="";
        long min = TimeUnit.MILLISECONDS.toMinutes(diff)%60;

        s=min+"m"+s;

        long h = TimeUnit.MILLISECONDS.toHours(diff)%24;
        s=h+"h, "+s;

        logger.info(s);
        return s;
    }
    //@Bean
    PlaceBid placeBid(String domain, String bid, String key)
    {
       return new PlaceBid(domain,bid,key);
    }

    @GetMapping("/getscheduledbids")
    List<DBdetails> getScheduled()
    {List<DBdetails> list = repo.findByResult("Bid Scheduled");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        int n=list.size(); Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;

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
                    AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
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

    @GetMapping("/getplacedbids")
    List<DBdetails> getPlaced()
    {
        logger.info("Returning placed bids, refreshing");
        List<DBdetails> list= repo.findByResult("Bid Placed");
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
        SimpleDateFormat ft2 = new SimpleDateFormat("yyyy-MM-dd");
        int n=list.size(); Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;

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
    }

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
        scheduledFuture = taskScheduler.scheduleWithFixedDelay(new DetectLive(date.getTime()),2000);
    }

    @GetMapping("/stop")
    void stop()
    {
        scheduledFuture.cancel(true);
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
    public class DetectLive implements Runnable
    {
        Long time;

        public DetectLive(Long time) {
            this.time = time;
        }

        @Override
        public void run()
        {
            SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            LiveMap lm = liveMaprepo.findById(1).get();
            Map<String,Long> map= lm.getMap();
            ResponseLive rl = myFeignClient.getLiveDetails(key,"get_open_auctions","usd");

            List<LiveDetails> list = rl.getAuction_list();
            int n= list.size();
            Date date= new Date();

            logger.info("Detect Live running "+ft.format(date));
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
                        String time_left= relTimelive(ld.getEnd_time_stamp());
                        String currbid = ld.getCurrent_bid_price();
                        int age= ld.getAge();
                        String est=ld.getEstibot_appraisal();
                        String text= "Dynadot Live Detect \n \n"+domain+"\n \nTime Left: "+time_left+"\nCurrent Bid: "+currbid+"\n \nAge: "+age+" \nEST: "+est+" \n\nLink: "+"https://www.dynadot.com/market/auction/"+domain;
                        Object obj= telegram.sendAlert(-1001814695777L,text);
                        ld.setLive(true);
                        ld.setPlatform("Dynadot");
                        String addtime= ft1.format(date);
                        ld.setAddtime(addtime);
                        ld.setTime_left(time_left);
                        map.put(domain,ld.getAuction_id());
                        WasLive wasLive= new WasLive(time_left,addtime,"Dynadot",ld.getAuction_id(),domain, ld.getCurrent_bid_price(), ld.getEnd_time(), ld.getEstibot_appraisal(),ld.getUtf_name(),ld.getBids(),ld.getBidders(),ld.getAge(),ld.getEnd_time_stamp());
                        wasLiveRepo.save(wasLive);
                        liveRepo.save(ld);
                    }
                }

            }
            liveMaprepo.save(lm);
        }
    }

    public class StopLiveDyna implements Runnable
    {
        @Override
        public void run()
        {
            scheduledFuture.cancel(true);
        }
    }

    public class GetResultdyna implements Runnable
    {
        String domain;
         GetResultdyna(String domain)
         {
             this.domain=domain;
         }

         @Override
         public void run()
         {
             DBdetails db=repo.findByDomain(domain);
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
                 Date d= new Date();
                 d.setMinutes(d.getMinutes()+30);
                 taskScheduler.schedule(new GetResultdyna(domain), d);
             }
         }
    }
    public class PlaceBid implements Runnable{



        String domain;
        private String bid;
        private String key;





        public PlaceBid(String domain, String bid, String key)
        {
            this.domain= domain;
            this.bid=bid;
            this.key=key;
            //this.service= new Service();
        }
        @Override
        public void run() {
            try {
//myFeignClient.placeAuctionBid(key,"place_auction_bid",domain,bid,"usd");
                // Response_AuctionDetails ra = myFeignClient.getAuctionDetails(key,"get_auction_details",domain,"usd");
                //logger.info(ra.getAuction_details().get(0).getAuction_json().getDomain());
                //Object obj = myFeignClient.placeAuctionBid(key, "place_auction_bid", domain, bid, "usd");
                //Optional<DBdetails> op=  Optional.ofNullable(repo.findByDomain(domain));
                Response_PlaceBid map = myFeignClient.placeAuctionBids(key, "place_auction_bid", domain, bid,"usd");

                //LinkedHashMap map = (LinkedHashMap) obj;
                String status =  map.getStatus();
                if (status.equals("success")) {
                    Auction_json aj= map.getAuction_details().getAuction_json();
                    DBdetails db = repo.findByDomain(domain);
                    db.setIsBidPlaced(true);
                    db.setResult("Bid Placed");
                    db.setCurrbid(aj.getCurrent_bid_price());
                    db.setEndTimepst(aj.getEnd_time());
                    db.setTime_left(relTime(aj.getEnd_time_stamp()));

                    repo.save(db);
                    notifRepo.save(new Notification("Dynadot: Scheduled Bid PLACED for " + domain + " at price " + bid + " USD at " + new Date()));
                    logger.info("Dynadot: Scheduled Bid Placed of domain: " + domain);
                    Date d=new Date();
                    d.setMinutes(d.getMinutes()+59);
                    taskScheduler.schedule(new GetResultdyna(domain),d);
                } else {
                    String content = map.getContent();
                    notifRepo.save(new Notification("Dynadot: Scheduled Bid NOT PLACED for " + domain + " at price " + bid + " USD with Error Message: " + content + " at " + new Date()));
                    logger.info("Dynadot: Bid not placed of domain: " + domain+" at price " + bid + " USD with Error Message: " + content + " at " + new Date());
                }
            }
            catch(Exception E)
            {

                String content = myFeignClient.placeAuctionBidstr(key, "place_auction_bid", domain, bid, "usd");
                notifRepo.save(new Notification("Dynadot: Scheduled Bid NOT PLACED for " + domain + " at price " + bid + " USD with Error Message: " + content + " at " + new Date()));
                logger.info("Dynadot: Scheduled Bid not placed of domain: " + domain+" at price " + bid + " USD with Error Message: " + content + " at " + new Date());

            }
        }
    }

}