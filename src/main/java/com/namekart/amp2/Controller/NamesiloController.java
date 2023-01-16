package com.namekart.amp2.Controller;

import com.namekart.amp2.Entity.LiveMap;
import com.namekart.amp2.Feign.NamesiloFeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.NamesiloEntities.SiloAuctionDetails;
import com.namekart.amp2.NamesiloEntities.SiloRespAucList;
import com.namekart.amp2.NamesiloEntities.SiloRespDomList;
import com.namekart.amp2.NamesiloEntities.SiloRespRenew;
import com.namekart.amp2.Repository.LiveMaprepo;
import com.namekart.amp2.Repository.Siloliverepo;
import feign.RetryableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@CrossOrigin
@RequestMapping("/namesilo")
public class NamesiloController {
    String key="7fcf313ace746555cff70389";

    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    Logger logger = Logger.getLogger("Namesilo");
    @Autowired
    NamesiloFeign namesiloFeign;

    @Autowired
    Telegram telegram;

    @Autowired
    LiveMaprepo liveMaprepo;

    @Autowired
    Siloliverepo siloliverepo;
    RestTemplate rest = new RestTemplate();

    @GetMapping("/getlist")
SiloRespAucList getList()
{
    //SiloRespAucList l= namesiloFeign.getList(1,"xml",key);
    SiloRespAucList l=null;
    ResponseEntity<SiloRespAucList> res= rest.getForEntity("https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389", SiloRespAucList.class);
    l= res.getBody();
    logger.info(l.getReply().getBody().get(0).getAuctionEndsOn());
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

    Boolean startlivesilo()
    {
        logger.info("Starting Namesilo Live");
      LiveMap live= liveMaprepo.findById(1).get();
        Map<String,String> regmap= live.getMapnsregistered();
        regmap.clear();
        ResponseEntity<SiloRespDomList> res= rest.getForEntity("https://www.namesilo.com/api/listDomains?version=1&type=xml&key=7fcf313ace746555cff70389", SiloRespDomList.class);
        SiloRespDomList l= res.getBody();
        List<String> listreg= l.getReply().getDomains();
        for(int i=0;i<listreg.size();i++)
        {
            regmap.put(listreg.get(i).toLowerCase(), listreg.get(i).toLowerCase());
        }
        Map<Long,String> livens= live.getMapns();
        livens.clear();

        ResponseEntity<SiloRespAucList> ress= rest.getForEntity("https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389", SiloRespAucList.class);
        Date date = new Date();
        date.setHours(date.getHours()+2);
        List<SiloAuctionDetails> al=ress.getBody().getReply().getBody();
        for(int i=0;i<al.size();i++)
        {
            SiloAuctionDetails details= al.get(i);
            String domain= details.getDomain().toLowerCase();
            String endTime= details.getAuctionEndsOn();
            Float bid= details.getCurrentBid();
            if(bid>0.0)
            {
                Date end=null;
                try
                {
                    end=parser.parse(endTime);
                   // System.out.println(d);
                }
                catch(ParseException p)
                {logger.info(p.getMessage());
                continue;}
                if(end.before(date))
                {
                    livens.put(details.getId(), domain);
                    if(regmap.containsKey(domain))
                    {
                        logger.info("Found domain for renewal: "+domain);
                        String url="https://www.namesilo.com/api/renewDomain?version=1&type=xml&key=7fcf313ace746555cff70389&domain="+domain+"&years=1";
                        SiloRespRenew respRenew= rest.getForEntity(url,SiloRespRenew.class).getBody();
                        if(respRenew.getReply().getCode()==300)
                            logger.info("Renewed domain: "+domain);
                    }
                }
            }

        }
        liveMaprepo.save(live);
        logger.info("Started Namesilo Live");
        return true;
    }

    String relTimelive(Date d)
    {
        Date date= new Date();
        String s="";
        int h=d.getHours()-date.getHours();
        int m= d.getMinutes()-date.getMinutes();
        s= h+"h, "+m+"m";
        return s;
    }



    public class DetectLiveNs implements Runnable
    {
        Date date;

        public DetectLiveNs(Date date) {
            this.date = date;
        }

        @Override
        public void run()
        {
            logger.info("Live Detect Service Ran");
            SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

            LiveMap live= liveMaprepo.findById(1).get();
            Map<String,String> regmap= live.getMapnsregistered();
            Map<Long,String> livens= live.getMapns();
            ResponseEntity<SiloRespAucList> ress= rest.getForEntity("https://www.namesilo.com/public/api/listAuctions?version=1&type=xml&key=7fcf313ace746555cff70389", SiloRespAucList.class);
            List<SiloAuctionDetails> al=ress.getBody().getReply().getBody();
            for(int i=0;i<al.size();i++) {
                SiloAuctionDetails details = al.get(i);
                String domain = details.getDomain().toLowerCase();
                String endTime = details.getAuctionEndsOn();
                Float bid = details.getCurrentBid();
                if(!livens.containsKey(details.getId()))
                {
                    if(bid>0.0)
                    {
                        Date end = null;
                        try {
                            end = parser.parse(endTime);
                            // System.out.println(d);
                        } catch (ParseException p) {
                            logger.info(p.getMessage());
                            continue;
                        }
                        if (end.before(date)) {
                            livens.put(details.getId(), domain);
                            details.setLive(true);
                            String time_left= relTimelive(end);
                            String addTime= ft1.format(new Date());
                            if (regmap.containsKey(domain)) {
                                logger.info("Found domain for renewal: " + domain);
                                String url = "https://www.namesilo.com/api/renewDomain?version=1&type=xml&key=7fcf313ace746555cff70389&domain=" + domain + "&years=1";
                                SiloRespRenew respRenew = rest.getForEntity(url, SiloRespRenew.class).getBody();
                                if (respRenew.getReply().getCode() == 300)
                                    logger.info("Renewed domain: " + domain);
                            }
                            else
                            {
                                String text = "Namesilo Live Detect \n \n" + domain + "\n \nTime Left: " + time_left + "\nCurrent Bid: " + bid + " \n\nLink: " + "https://www.namecheap.com/market/" + domain;
                                try {
                                    Object obj = telegram.sendAlert(-1001814695777L, text);
                                }
                                catch(RetryableException re)
                                {
                                    logger.info(re.getMessage());
                                    try {
                                        Thread.sleep(5000);
                                        Object obj = telegram.sendAlert(-1001814695777L, text);
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
                    }
            }

            }
            liveMaprepo.save(live);
        }
    }
}

