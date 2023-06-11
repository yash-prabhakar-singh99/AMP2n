package com.namekart.amp2.Controller;

import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.EstibotEntity.Estibot_Response;
import com.namekart.amp2.Feign.Estibot;
import com.namekart.amp2.Repository.MyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class AllController {
    @Autowired
    NamecheapController namecheapController;

    @Autowired
    NamesiloController namesiloController;

    @Autowired
    Controller dynadotController;

    Logger logger= Logger.getLogger("Common");
    @Autowired
    GoDaddyController goDaddyController;

    @Autowired
    DropCatchController dropCatchController;

    @Autowired
    MyRepo repo;

    @Autowired
    Estibot estibot;

    @GetMapping("/getscheduledbids")
    List<DBdetails> getScheduledBids()
    {
      /*CompletableFuture<Boolean> dd= dynadotController.refreshScheduled();
        CompletableFuture<Boolean> dc=dropCatchController.refreshScheduled();
        CompletableFuture<Boolean> gd=goDaddyController.refreshscheduledbids();
        CompletableFuture<Boolean> nc=namecheapController.refreshScheduled();
        CompletableFuture<Boolean> ns=namesiloController.refreshScheduled();

        CompletableFuture.allOf(dd,dc,gd,nc,ns).join();
        logger.info("1");
        */

        List<DBdetails> list= repo.findByResultOrResultOrResultOrResultOrderByEndTimeist("Bid Scheduled", "Bid Placed","Bid Placed And Scheduled","Outbid");
        return list;
    }

    @Scheduled(cron = "0 30 22 ? * *", zone = "UTC")
    void refreshDB()
    {
        List<DBdetails> list= repo.findByWatchlistFalseAndWasWatchlistedFalseAndTrackFalseAndResult("");
        repo.deleteAll(list);
    }

    @GetMapping("/getestibot")
    Estibot_Response getEstibot(@RequestParam String t, @RequestParam String d)
    {
        return estibot.getEstibot("D74OFQ3pN0pszbfpGK8GjV2vM","appraise",t,d);
    }

    @GetMapping("/getallasync")
    void test()
    {
        logger.info("1");
        CompletableFuture.runAsync(()->{
            try {
                Thread.sleep(2000);
                logger.info("3");
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
        });
        logger.info("2");
    }

}
