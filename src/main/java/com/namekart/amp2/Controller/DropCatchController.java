package com.namekart.amp2.Controller;

import com.namekart.amp2.DCEntity.*;
import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.Entity.LiveMap;
import com.namekart.amp2.Entity.Notification;
import com.namekart.amp2.Entity.Response_PlaceBid;
import com.namekart.amp2.Feign.DropCatchFeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class DropCatchController {

    @Autowired
    LiveDCrepo liveDCrepo;
    @Autowired
    DropCatchFeign dropCatchFeign;

    @Autowired
    MapWraprepo mapwraprepo;

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    MyRepo repo;

    @Autowired
    Bidhisrepo bidhisrepo;

    @Autowired
    NotifRepo notifRepo;

    @Autowired
    LiveMaprepo liveMaprepo;
    String token="eyJhbGciOiJSUzI1NiIsImtpZCI6IkI2ODhCNTVDMUNFMDI5OUEwNjRCQjYyNzM5MkMxQkYyQjE1OEU0NjBSUzI1NiIsInR5cCI6ImF0K2p3dCIsIng1dCI6InRvaTFYQnpnS1pvR1M3WW5PU3diOHJGWTVHQSJ9.eyJuYmYiOjE2Njc2NTM3NjcsImV4cCI6MTY2NzY1NTU2NywiaXNzIjoiaHR0cHM6Ly9hdXRoLnRjZGV2b3BzLmNvbSIsImF1ZCI6WyJjbGllbnQiLCJkcm9wY2F0Y2hDbGllbnQiXSwiY2xpZW50X2lkIjoiYmFieXlvZGE6aGF3ayIsImp0aSI6Ijg3NjkwREY3RTY4MUYyMDIwQzEzNzNENkE5N0Q3MDM4IiwiaWF0IjoxNjY3NjUzNzY3LCJzY29wZSI6WyJjbGllbnQ6cmVhZC5hY2NvdW50SWQiLCJkcm9wY2F0Y2g6YXBpLnJlYWQiXX0.bmRjUSDnSkxDEO9VRurKceNBzNiaU6IGCpn8P_ZGtJk_ji22UhStbb8izZAYewMzw8tIoR-UbKTb5sPDmxpQDA9OfciYrcLzozr3sipKe904T08bkEDWSwBnAEWG18SIX03INXbNDrbMUMknsenekdwejk0gdqnnljMvp42LpwUcJRcmkv8vTcOQ1IsRDA7KP8bw6ciaEEYG8r9WZY2F2FskXq8i52gTXH9Yt_Lgj8U4_LEMWpHw9G5ByrwmzjVl8qmxnjIQFo_HEbztGGpC5MhjViQykkXAxc7OnJlI4Qclnj6FZQmHTr2C5T3IOHonRv71upvNWbjTFyx0_2Tf6g";

    String bearer= "Bearer "+ token;

    Logger logger =Logger.getLogger("DropCatch Yash");

    @GetMapping("/getauctiondc")
    Object getAuctiondc(@RequestParam int id)
    {
        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;
        ResponseEntity<Object> r= dropCatchFeign.getAuctionDetail1(bearer,id);
        System.out.println(r.getStatusCodeValue());
Object acdc= r.getBody();
        return acdc;
    }

    @GetMapping("/getauctionsdc")
    ResponseAuctionList getAuctionsdc() {
        try{
            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign.authorise(auth).getBody().getToken();
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


    @GetMapping("/refreshdcmap")
    List<Map> refresh1() {
        try{
        List<Map> list= new ArrayList<>();
        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;
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

    @PostMapping("/bulkbidscheduledc")
    List<String> mainmain(@RequestBody List<ArrayList<String>> ddlist)
    {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        MapWrap mw =  mapwraprepo.getById(1);
        Map<String,Long> map= mw.getMap();
        Map<Long,String> rm= new HashMap<>();

        List<String> failure= new ArrayList<>();
        int n= ddlist.size();
        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;
        Long test= map.get(ddlist.get(0).get(0).toLowerCase());
        AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, test.intValue()).getBody();
        String endTime = ad.getEndTime().substring(0,ad.getEndTime().length()-1);

        logger.info(endTime);
        Date date= new Date();
        try{
            date = parser.parse(endTime);
            date.setHours(date.getHours()+5);
            date.setMinutes(date.getMinutes()+26);
            System.out.println(date);
        }
        catch(ParseException p)
        {logger.info(p.getMessage());}
        List<Biddc> bids= new ArrayList<>();
        for(int i=0;i<n;i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();

            Long amount= Long.parseLong(ddlist.get(i).get(1));
            Long auctionId= map.get(domain);
            rm.put(auctionId,domain);
            Biddc bid = new Biddc(auctionId,amount);
            bids.add(bid);
        }
       // Date date1 = new Date();
       // date1.setMinutes(date1.getMinutes()+2);
        taskScheduler.schedule(new PlaceBiddc(bids),date);
        return failure;
    }

    @PostMapping("/bulkbidscheduledc1")
    List<Integer> mainmain1(@RequestBody List<ArrayList<String>> ddlist)
    {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

        MapWrap mw =  mapwraprepo.getById(1);
        Map<String,Long> map= mw.getMap();
        int n= ddlist.size();
        int d=0;
        Map<String,List<Biddc>> m = new HashMap<>();
        Map<Long,String> rm= mw.getRm();
        Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
        String token = dropCatchFeign.authorise(auth).getBody().getToken();
        String bearer= "Bearer "+token;
        for(int i=0;i<n;i++)
        {
            String domain = ddlist.get(i).get(0).toLowerCase();
            try {

                Long bid = Long.parseLong(ddlist.get(i).get(1));
                Long auctionId = map.get(domain);
                // rm.put(auctionId,domain);
                AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();

                String endTime = ad.getEndTime().substring(0, ad.getEndTime().length() - 1);
                Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(auctionId));
                if (!op.isPresent()) {
                    DBdetails db = new DBdetails(domain, auctionId, "Dropcatch", String.valueOf(ad.getHighBid()), ad.getNumberOfBidders(), ad.getType(), "", endTime, "", "", false);
                    repo.save(db);
                } else {
                    DBdetails db = op.get();
                    db.setCurrbid(String.valueOf(ad.getHighBid()));
                    db.setEndTimepst(ad.getEndTime());
                    db.setBidders(ad.getNumberOfBidders());
                }
                logger.info(endTime);
                if (m.containsKey(endTime)) {
                    m.get(endTime).add(new Biddc(auctionId, bid));
                } else {
                    m.put(endTime, new ArrayList<Biddc>());
                    m.get(endTime).add(new Biddc(auctionId, bid));
                }
            }
            catch(Exception E)
            {
                logger.info(E.getMessage());
                notifRepo.save( new Notification("Dropcatch: Domain with name: "+ domain+" not found. See log for further info."));
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
                date.setHours(date.getHours()+5);
                date.setMinutes(date.getMinutes()+30);
                time_left= relTime(date);
                endTimeist=ft1.format(date);
                date.setMinutes(date.getMinutes()-4);
                bidplacetime=ft1.format(date);
                System.out.println(date);
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
                notifRepo.save(new Notification("Dropcatch: BID SCHEDULED for domain: "+db.getDomain()+ " for price: "+db.getBidAmount()+" at "+db.getEndTimeist()));
                logger.info("Dropcatch: BID SCHEDULED for domain: "+db.getDomain()+ " for price: "+db.getBidAmount()+" at "+db.getEndTimeist());

            }
        }
List<Integer> list= new ArrayList<>();
        list.add(d);
        list.add(n);
        return list;
        }

        @PostMapping("/bulkbiddc")
        List<Integer> mainmaininstant(@RequestBody List<ArrayList<String>> ddlist)
        {
            List<Integer> l = new ArrayList<>();
            List<String> succ= new ArrayList<>();
            int n = ddlist.size();
            MapWrap mw =  mapwraprepo.getById(1);
            Map<String,Long> map= mw.getMap();
            Map<Long,String> rm = mw.getRm();
            Map<Long,Long> rb= new HashMap<>();
            Map<String,List<String>> m = new HashMap<>();
            List<Biddc> bids = new ArrayList<>();
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
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
                    logger.info(E.getMessage());
                    notifRepo.save( new Notification("Dropcatch: Domain with name: "+ domain+" not found. See log for further info."));

                }
            }

            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign.authorise(auth).getBody().getToken();
            String bearer= "Bearer "+token;
            ResponsePlaceBiddc pb = dropCatchFeign.placeBiddc(bearer,bids).getBody();
            List<BidPlacedFailure> failures = pb.getFailures();
            List<BidPlacedSuccess> successes= pb.getSuccesses();

            for(int i=0;i< successes.size();i++)
            {

                BidPlacedSuccess s=successes.get(i);
                Long auctionId= s.getAuctionId();
                String domain = rm.get(auctionId);

                succ.add(domain);
                logger.info("Dropcatch: Instant Bid PLACED for domain: "+domain+ " at price: "+rb.get(s.getAuctionId()));
                notifRepo.save(new Notification("Dropcatch: Instant Bid PLACED for domain: "+domain+ " at price: "+rb.get(s.getAuctionId())));
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
                    date.setHours(date.getHours()+5);
                    date.setMinutes(date.getMinutes()+30);
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
                    DBdetails db = new DBdetails(domain,auctionId,"Dropcatch",String.valueOf(s.getHighBid()), ad.getNumberOfBidders(), ad.getType(),"Bid Placed",endTime,endTimeist,bidplacetime,true);
                    db.setTime_left(time_left);
                    db.setAuctiontype(ad.getType());
                    repo.save(db);
                }
            }

            for(int i=0;i< failures.size();i++)
            {
                BidPlacedFailure f=failures.get(i);
                Long auctionId= f.getAuctionId();
                String domain = rm.get(f.getAuctionId());
                logger.info("Dropcatch: Instant Bid NOT PLACED for domain: "+domain+ " for price: "+rb.get(f.getAuctionId())+" with error: "+f.getError().getDescription());
                notifRepo.save(new Notification("Dropcatch: Instant Bid NOT PLACED for domain: "+domain+ " for price: "+rb.get(f.getAuctionId())+" with error: "+f.getError().getDescription()));
                Optional<DBdetails> op= Optional.ofNullable(repo.findByAuctionId(auctionId));
                if(op.isPresent())
                {
                    DBdetails db= op.get();
                    db.setResult("Bid Not Placed");
                    repo.save(db);
                }

            }
            for (Map.Entry<String, List<String>> set : m.entrySet())
            {
                String endTime=set.getKey();
                Date date=new Date();
                try{
                    date = parser.parse(endTime);
                    date.setHours(date.getHours()+6);
                    date.setMinutes(date.getMinutes()+15);

                    // System.out.println(date);
                }
                catch(ParseException p)
                {logger.info(p.getMessage());}
                taskScheduler.schedule(new GetResultdc(set.getValue()),date);
            }
            l.add(successes.size());
            l.add(ddlist.size());
            return l;

        }


    @PostMapping("/bulkbidd")
    List<Integer> mainmaininsta(@RequestBody List<ArrayList<String>> ddlist)
    {
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(4);
        return list;
    }

        @PostMapping("/fetchdetailsdc")
        List<DBdetails> fetchDetails(@RequestBody List<String> list)
        {
            List<DBdetails> l= new ArrayList<>();
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
            MapWrap mw= mapwraprepo.getReferenceById(1);
            Map<String,Long> map = mw.getMap();
            int n= list.size();
            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign.authorise(auth).getBody().getToken();
            String bearer= "Bearer "+token;
            for(int i=0;i<n;i++)
            {
                String domain= list.get(i).toLowerCase();
                try {
                    Long auctionId = map.get(domain);
                    AuctionDetailDC ad = dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByAuctionId(auctionId));
                    DBdetails db = null;
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

                    if (op.isPresent()) {
                        db = op.get();
                        db.setCurrbid(String.valueOf(ad.getHighBid()));
                        db.setBidders(ad.getNumberOfBidders());
                        //db.setTime_left(ad.);
                        //db.setAge(aj.getAge());
                        //db.setEstibot(aj.getEstibot_appraisal());

                       // db.setAuctiontype(ad.getType());
                        db.setEndTimepst(endTime);
                        db.setEndTimeist(endTimeist);
                        db.setTime_left(time_left);
                        db.setFetched(true);
                        repo.save(db);
                    } else {
                        //AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                        db = new DBdetails(domain, auctionId, "Dropcatch", String.valueOf(ad.getHighBid()), ad.getNumberOfBidders(), ad.getType(), "", endTime, endTimeist, "", false);
                        db.setTime_left(time_left);
                        db.setAuctiontype(ad.getType());
                        db.setFetched(true);
                        repo.save(db);
                    }

                    l.add(db);
                }
                catch(Exception e)
                {
                    logger.info(domain+" "+e.getMessage());
                }
            }
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

          s=h+"h "+s;

      long d = TimeUnit.MILLISECONDS.toDays(diff)%365;
          s=d+"d "+s;

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

        s=h+"h "+s;

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

        public class GetResultdc implements Runnable
        {
            List<String> list;

            GetResultdc(List<String> list)
            {
                this.list= list;
            }

            @Override
            public void run()
            {
                Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
                String token = dropCatchFeign.authorise(auth).getBody().getToken();
                String bearer= "Bearer "+token;
                List<String> succ=new ArrayList<>();
                int n= list.size();
                for(int i=0;i<n;i++) {
                    try {
                        String domain = list.get(i);
                        DBdetails db = repo.findByDomain(domain);
                        AuctionResultdc r = dropCatchFeign.getAuctionResult(bearer, domain, 10).getBody().getItems().get(0);
                        String domain1 = r.getDomain().toLowerCase();
                        if (domain.equals(domain1))
                        {
                            if (r.getResult().equals("AuctionWon"))
                                db.setResult("Won");
                            else db.setResult("Loss");
                        }
                        else
                        {
                            succ.add(domain);
                        }


                        if (succ.size() >= 1) {
                            Date d = new Date();
                            d.setMinutes(d.getMinutes() + 30);
                            taskScheduler.schedule(new GetResultdc(succ), d);
                        }
                    }
                      catch(Exception e)
                      {
                          logger.info("Inside Get Result Scheduled Service: "+e.getMessage());
                      }
                }

            }

        }

        @GetMapping("/startlivedc")
        ResponseAuctionList startlivedc()
        {
            logger.info("Starting Live Service");
            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign.authorise(auth).getBody().getToken();
            String bearer= "Bearer "+token;
            liveDCrepo.deleteAll();
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date= new Date();
            date.setMinutes(date.getMinutes()-205);
            String end= parser.format(date)+"Z";
            ResponseEntity<ResponseAuctionList> o = dropCatchFeign.getAuctionDetailslive(bearer, 350, true, "Dropped", "EndTimeAsc",end,true);
           // LiveMap lm= liveMaprepo.findById(1).get();
            LiveMap lm=null;

            if(liveMaprepo.findById(1).isPresent())
                lm = liveMaprepo.findById(1).get();
            else
            {
                lm= new LiveMap();
                liveMaprepo.save(lm);
            }
            Map<Long,String> map= lm.getMapdc();
            map.clear();
            ResponseAuctionList rl= o.getBody();
            List<AuctionDetailDC> items = rl.getItems();
            for(int i=0;i<items.size();i++)
            {
                AuctionDetailDC item= items.get(i);
                map.put(item.getAuctionId(),item.getName().toLowerCase());
                liveDCrepo.save(item);
            }
            liveMaprepo.save(lm);

            ScheduledFuture scheduledFuture=taskScheduler.scheduleWithFixedDelay(new DetectLivedc(end),20000);
            Date d= new Date();
            d.setMinutes(d.getMinutes()+130);
            taskScheduler.schedule(new Stoplivedc(scheduledFuture),d);
            logger.info("Started live service");
            return rl;
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
                Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
                String token = dropCatchFeign.authorise(auth).getBody().getToken();
                String bearer= "Bearer "+token;
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                LiveMap lm= liveMaprepo.findById(1).get();
                Map<Long,String> map= lm.getMapdc();
                List<AuctionDetailDC> items = dropCatchFeign.getAuctionDetailslive(bearer,350,true,"Dropped","EndTimeAsc",endTime,true).getBody().getItems();
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
                        String endTime= item.getEndTime().substring(0,item.getEndTime().length()-1);
                        Date end=null;
                        String relTime="";
                        try{
                            end=parser.parse(endTime);
                            end.setHours(end.getHours()+5);
                            end.setMinutes(end.getMinutes()+30);
                            relTime=relTimelive(end);
                        }
                        catch(ParseException p)
                        {
                            logger.info(p.getMessage());
                        }
                        Date now= new Date();
                        String addTime= ft1.format(now);
                        Long price= item.getHighBid();
                        String text = "Dropcatch Live Detect \n \n" + domain + "\n \nTime Left: " + relTime + "\nCurrent Bid: " + price +  " \n\nLink: " + "https://www.dropcatch.com/domain/" + domain;
                        telegram.sendAlert(-1001706842871L,text);
                        item.setAddTime(addTime);
                        item.setTimeLeft(relTime);
                        item.setLive(true);
                        liveDCrepo.save(item);
                    }}
                    catch(Exception e)
                    {
                        logger.info(e.getMessage());
                    }
                }
                liveMaprepo.save(lm);
            }
        }

    public class PlaceBiddc implements Runnable{

        List<Biddc> bids;
        //Map<Long,String> rm;
        Map<Long,Long> rb;
       // String bearer;
        public PlaceBiddc(List<Biddc> bids) {
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
            MapWrap mw = mapwraprepo.getReferenceById(1);
            Map<Long,String> rm = mw.getRm();
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
            Authorise auth = new Authorise("babyyoda:hawk",":pvN|?'Sb4.Ah2N0t+7M");
            String token = dropCatchFeign.authorise(auth).getBody().getToken();
            token= "Bearer "+token;

           // Long id= bids.get(0).getAuctionId();
            //int num1 = Math.toIntExact(id);
           // AuctionDetailDC ad= dropCatchFeign.getAuctionDetail1(token, num1).getBody();
            //logger.info(ad.getName());

           ResponseEntity<ResponsePlaceBiddc> re= dropCatchFeign.placeBiddc(token,bids);
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
                    date.setHours(date.getHours()+5);
                    date.setMinutes(date.getMinutes()+30);
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
                    repo.save(db);
                }
                else
                {
                    date = new Date();
                   String bidplacetime=ft1.format(date);
                    AuctionDetailDC ad= dropCatchFeign.getAuctionDetail(bearer, auctionId.intValue()).getBody();
                    DBdetails db = new DBdetails(domain,auctionId,"Dropcatch",String.valueOf(s.getHighBid()), ad.getNumberOfBidders(), ad.getType(),"Bid Placed",endTime,endTimeist,bidplacetime,true);
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

