package com.namekart.amp2.Controller;

import com.azure.spring.aad.AADOAuth2AuthenticatedPrincipal;
import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.Entity.ResponseLive;
import com.namekart.amp2.EstibotEntity.Estibot_Data;
import com.namekart.amp2.EstibotEntity.Estibot_Response;
import com.namekart.amp2.Feign.Estibot;
import com.namekart.amp2.GoDaddyEntities.PurchaseResp;
import com.namekart.amp2.Repository.LiveFilterSettingsRepo;
import com.namekart.amp2.Repository.MyRepo;
import com.namekart.amp2.Repository.UserRepository;
import com.namekart.amp2.SettingsEntity.LiveFilterSettings;
import com.namekart.amp2.SettingsEntity.LiveFiltersWrapper;
import com.namekart.amp2.Status;
import com.namekart.amp2.TelegramEntities.Tsession;
import com.namekart.amp2.UserEntities.User;
import com.nimbusds.jose.shaded.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NonUniqueResultException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class AllController {
  /*  @Autowired
    NamecheapController namecheapController;

    @Autowired
    NamesiloController namesiloController;

    @Autowired
    Controller dynadotController;*/

    Logger logger= Logger.getLogger("Common");

    @Lazy
  @Autowired
    GoDaddyController goDaddyController;
/*
    @Autowired
    DropCatchController dropCatchController;*/
  Random random= new Random();
    @Autowired
    MyRepo repo;

    @Autowired
    @Qualifier(value = "workStealingPool")
    ForkJoinPool threadPoolExecutor;

    @Autowired
    Estibot estibot;

    String estKey="D74OFQ3pN0pszbfpGK8GjV2vM";
    ConcurrentMap<String, Status> taskmap;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;
    SimpleDateFormat ft;
    SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Lazy
    @Autowired
    BotController botController;
    public AllController() {
        taskmap=new ConcurrentHashMap<>();
        ft = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        ft.setTimeZone(TimeZone.getTimeZone("IST"));
    }



    @GetMapping("/syncuser")
    User syncUser()
    {
        AADOAuth2AuthenticatedPrincipal ad= getToken();
        Optional<User> op= Optional.ofNullable(userRepository.findByEmail(ad.getClaim("unique_name")+""));
        User user=null;
        if(op.isPresent())
        {
            user=op.get();
            user.setFirstName(ad.getClaim("given_name")+"");user.setLastName(ad.getClaim("family_name")+"");
            JSONArray array= (JSONArray) ad.getClaim("roles");
            user.getRoles().clear();
            for (int i=0;i<array.size();i++) {

                user.getRoles().add(array.get(i)+"");
                if(array.get(i).toString().startsWith("Watch"))
                {user.getRoles().add("Watch_GD");user.getRoles().add("Watch_DD");user.getRoles().add("Watch_DC");user.getRoles().add("Watch_NC");user.getRoles().add("Watch_NS");}
                if(array.get(i).toString().startsWith("Live_Watch"))
                {user.getRoles().add("Live_Watch_GD");user.getRoles().add("Live_Watch_DD");user.getRoles().add("Live_Watch_DC");user.getRoles().add("Live_Watch_NC");user.getRoles().add("Live_Watch_NS");}/*if(array.get(i).toString().startsWith("Bid"))
                    user.getRoles().add("Bid");
                else if(array.get(i).toString().startsWith("Watch"))
                    user.getRoles().add("Watch");
                else if(array.get(i).toString().startsWith("Live_Watch"))
                    user.getRoles().add("Live_Watch");
                else if(array.get(i).toString().startsWith("Live_Bid"))
                    user.getRoles().add("Live_Bid");
                else if(array.get(i).toString().startsWith("Report"))
                    user.getRoles().add("Report");*/
            }
            if(user.getRoles().contains("Telegram"))
            {
                ConcurrentMap<Long,Tsession> users1=botController.getUsers1(),users= botController.getUsers();
                if(!users1.containsKey(user.getTgUserId()))
                {
                    users1.put(user.getTgUserId(),new Tsession(user.getRoles()));
                    users.remove(user.getTgUserId());
                }
                else
                {
                    users1.get(user.getTgUserId()).setRoles(user.getRoles());
                }
            }
        }
        else
        {

            user=new User(random.nextInt(999,10000),ad.getClaim("given_name")+"",ad.getClaim("family_name")+"",ad.getClaim("unique_name")+"");
            JSONArray array= (JSONArray) ad.getClaim("roles");
            for (int i=0;i<array.size();i++) {
                user.getRoles().add(array.get(i)+"");
                if(array.get(i).toString().startsWith("Watch"))
                {user.getRoles().add("Watch_GD");user.getRoles().add("Watch_DD");user.getRoles().add("Watch_DC");user.getRoles().add("Watch_NC");user.getRoles().add("Watch_NS");}
                if(array.get(i).toString().startsWith("Live_Watch"))
                {user.getRoles().add("Live_Watch_GD");user.getRoles().add("Live_Watch_DD");user.getRoles().add("Live_Watch_DC");user.getRoles().add("Live_Watch_NC");user.getRoles().add("Live_Watch_NS");}
            }
        }
        userRepository.save(user);
        return user;
    }

    @GetMapping("/users")
    List<User> getUsers()
    {
       return userRepository.findAll();
    }

    @GetMapping("/deleteuser")
    List<User> deleteUser(@RequestParam Integer id)
    {
        userRepository.deleteById(id);
        return userRepository.findAll();
    }

    @GetMapping("/getuser")
    User getUser()
    {
       return userRepository.findByEmail(getToken().getClaim("unique_name")+"");
    }

    AADOAuth2AuthenticatedPrincipal getToken()
    {
        return (AADOAuth2AuthenticatedPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
    String getUserName()
    {
        return (getToken().getClaim("unique_name")+"");
    }

    @GetMapping("/getotp")
    int getOtp()
    {
        return getUser().getOtp();
    }

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

        List<DBdetails> list= repo.findByScheduledTrueOrderByEndTimeistAsc();//repo.findByResultOrResultOrResultOrResultOrderByEndTimeist("Bid Scheduled", "Bid Placed","Bid Placed And Scheduled","Outbid");
        return list;
    }

    @Autowired
    LiveFilterSettingsRepo livesettingsrepo;




    @GetMapping("/getlivefilters")
    LiveFiltersWrapper getLiveFilters()
    {
        LiveFilterSettings live=null;
        Optional<LiveFilterSettings> op= livesettingsrepo.findById(1);
        if(op.isEmpty())
        {live= new LiveFilterSettings();
        livesettingsrepo.save(live);}
        else live=op.get();
        Map<String,Integer> extests= live.getExtnEst();
        LiveFiltersWrapper wrapper= new LiveFiltersWrapper(live.getNoHyphens(),live.getNoNumbers(),new int[]{live.getLowLength(),live.getUpLength()},new int[]{extests.get("com"),extests.get("net"),extests.get("co"),extests.get("ai"),live.getNewExtEsts(),live.getElseEsts()},live.getNewExtns(),live.getRestrictedExtns());
        return wrapper;
    }

    @PostMapping("/postlivefilters")
    void getLiveFilters(@RequestBody LiveFiltersWrapper wrapper)
    {
        LiveFilterSettings live=null;
        Optional<LiveFilterSettings> op= livesettingsrepo.findById(1);
        if(op.isEmpty())
        {live= new LiveFilterSettings();
            livesettingsrepo.save(live);}
        else live=op.get();
        Map<String,Integer> extnEst= live.getExtnEst();
        live.setNoHyphens(wrapper.getNoHyphens());live.setNoNumbers(wrapper.getNoNumbers());
        live.setUpLength(wrapper.getDomainLength()[1]);live.setLowLength(wrapper.getDomainLength()[0]);
        live.setRestrictedExtns(wrapper.getRestrictedExts());
        live.setNewExtns(wrapper.getNew_ests());
        extnEst.put("com",wrapper.getDiff_exts_ests()[0]);extnEst.put("net",wrapper.getDiff_exts_ests()[1]);extnEst.put("org",wrapper.getDiff_exts_ests()[1]);extnEst.put("info",wrapper.getDiff_exts_ests()[1]);extnEst.put("co",wrapper.getDiff_exts_ests()[2]);
        extnEst.put("me",wrapper.getDiff_exts_ests()[2]);extnEst.put("tv",wrapper.getDiff_exts_ests()[2]);extnEst.put("ai",wrapper.getDiff_exts_ests()[3]);extnEst.put("io",wrapper.getDiff_exts_ests()[4]);
        live.setElseEsts(wrapper.getDiff_exts_ests()[5]);live.setNewExtEsts(wrapper.getDiff_exts_ests()[4]);
        livesettingsrepo.save(live);
    }

    @PostMapping("/fetchwithest")
    List<DBdetails> getEstDatabase(@RequestBody List<Long> ids)
    {
        List<DBdetails> list= new ArrayList<>();
        for(int i=0;i< ids.size();i++)
        {
            list.add(repo.findById(ids.get(i)).get());
        }
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

    List<Estibot_Data> getEstibotsSync(String s)
    {
        Estibot_Response estibot_response =null;
        try {
            estibot_response = estibot.getEstibot(estKey, "appraise", "cache", s);
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
            String[] sl=s.split(">>");
            List<Estibot_Data> esl= new ArrayList<>();
            for(int i=0;i< sl.length-1;i++)
            {
                esl.add(new Estibot_Data(sl[i].toLowerCase(),-1));
            }
            return esl;
        }
        List<Estibot_Data> list = estibot_response.getResults().getData();

        List<String> notFound = estibot_response.getNot_found();
        if(notFound!=null&&(!notFound.isEmpty())) {
            s = "";
            for (int i = 0; i < notFound.size(); i++) {
                String domain = notFound.get(i);
                s = s + domain + ">>";
            }
            Estibot_Response estibot_response1 = estibot.getEstibot(estKey, "appraise", "live", s);
            list.addAll(estibot_response1.getResults().getData());
        }
        return list;
    }
    Estibot_Data getEstibotsSyncsingle(String s)
    {
        Estibot_Response estibot_response =null;
        try {
            estibot_response = estibot.getEstibot(estKey, "appraise", "cache", s);
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
            String[] sl=s.split(">>");
            Estibot_Data data= new Estibot_Data(s,-1);
           return data;
        }
        Estibot_Data data = estibot_response.getResults().getData().get(0);

        List<String> notFound = estibot_response.getNot_found();
        if(notFound!=null&&(!notFound.isEmpty())) {
            Estibot_Response estibot_response1 = estibot.getEstibot(estKey, "appraise", "live", s);
            data=estibot_response1.getResults().getData().get(0);
        }
        return data;
    }

    List<Estibot_Data> getEstibotsSync1(List<String> domains)
    {
        if(domains.isEmpty()||domains.size()==0||domains==null)
            return null;
        String s="";
        for(int i=0;i< domains.size();i++)
        {

            String domain = domains.get(i);
            domain=domain.trim();
            s=s+domain+">>";
        }
        Estibot_Response estibot_response =null;
        try {
            estibot_response = estibot.getEstibot(estKey, "appraise", "cache", s);
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
            String[] sl=s.split(">>");
            List<Estibot_Data> esl= new ArrayList<>();
            for(int i=0;i< sl.length-1;i++)
            {
                esl.add(new Estibot_Data(sl[i].toLowerCase(),-1));
            }
            return esl;
        }
        List<Estibot_Data> list = estibot_response.getResults().getData();

        List<String> notFound = estibot_response.getNot_found();
        if(notFound!=null&&(!notFound.isEmpty()))
        {
            s = "";
            for (int i = 0; i < notFound.size(); i++) {
                String domain = notFound.get(i);
                s = s + domain + ">>";
            }
            Estibot_Response estibot_response1 = estibot.getEstibot(estKey, "appraise", "live", s);


            list.addAll(estibot_response1.getResults().getData());
        }
        return list;
    }

    public ConcurrentMap<String, Status> getTaskmap() {
        return taskmap;
    }

    @PostMapping("/bulkfetchest")
    Estibot_Data[] getEstibotListWeb(@RequestBody LinkedHashSet<String> domains)
    {
        if(domains.isEmpty()||domains.size()==0||domains==null)
            return null;
        String s="";
        Map<String,Integer> map= new HashMap<>();
        Iterator<String> itr = domains.iterator();

        int j=0;
        while (itr.hasNext()){
            String domain=itr.next().trim().toLowerCase();
            map.put(domain,j);
            s=s+domain+">>";
            j++;
        }

        Estibot_Data[] arr= new Estibot_Data[domains.size()];
        if(!s.equals("")) {
            Estibot_Response estibot_response = estibot.getEstibot(estKey, "appraise", "cache", s);
            List<Estibot_Data> list = estibot_response.getResults().getData();
            List<String> notFound = estibot_response.getNot_found();
            if(notFound!=null&&(!notFound.isEmpty())) {
                s = "";
                for (int i = 0; i < notFound.size(); i++) {
                    String domain = notFound.get(i);
                    s = s + domain + ">>";
                }
                Estibot_Response estibot_response1 = estibot.getEstibot(estKey, "appraise", "live", s);
                list.addAll(estibot_response1.getResults().getData());
            }
            for(int i=0;i< list.size();i++)
            {
                Estibot_Data data= list.get(i);
                String domain=data.getDomain().trim().toLowerCase();
                arr[map.get(domain)]=data;
                map.remove(domain);
            }
            for (Map.Entry<String,Integer> ml : map.entrySet())
            {
                arr[ml.getValue()]=new Estibot_Data(ml.getKey(), 0);
            }
            return arr;
        }
        else
            return null;
    }

    CompletableFuture<List<Estibot_Data>> getEstibotList(List<String> domains)
    {
        return CompletableFuture.supplyAsync(()->{
            String s="";
            for(int i=0;i< domains.size();i++)
            {
                String domain=domains.get(i);
                try {
                    Optional<DBdetails> o = Optional.ofNullable(repo.findByDomain(domain.toLowerCase()));
                    if (o.isEmpty())
                        s = s + domain + ">>";
                    else {
                        if (!o.get().isEstFlag())
                            s = s + domain + ">>";
                    }
                }
                catch(NonUniqueResultException e)
                {
                    List<DBdetails> l=repo.findAllByDomain(domain.toLowerCase());
                    if(!l.get(l.size()-1).isEstFlag())
                        s = s + domain + ">>";

                }
                catch(IncorrectResultSizeDataAccessException e)
                {
                    List<DBdetails> l=repo.findAllByDomain(domain.toLowerCase());
                    if(!l.get(l.size()-1).isEstFlag())
                        s = s + domain + ">>";

                }
                catch(Exception e)
                {
                    logger.info(domain+e.getMessage());
                }
            }
            if(!s.equals("")) {
                Estibot_Response estibot_response = estibot.getEstibot(estKey, "appraise", "cache", s);
                List<Estibot_Data> list = estibot_response.getResults().getData();

                List<String> notFound = estibot_response.getNot_found();
                if(notFound!=null&&(!notFound.isEmpty())) {
                    s = "";
                    for (int i = 0; i < notFound.size(); i++) {
                        String domain = notFound.get(i);
                        s = s + domain + ">>";
                    }
                    Estibot_Response estibot_response1 = estibot.getEstibot(estKey, "appraise", "live", s);


                    list.addAll(estibot_response1.getResults().getData());
                }
                return list;
            }
            else
                return null;

        },threadPoolExecutor);
    }


    CompletableFuture<List<Estibot_Data>> getEstibotList1(List<List<String>> domains)
    {
        return CompletableFuture.supplyAsync(()->{
            String s="";
            for(int i=0;i< domains.size();i++)
            {
                String domain=domains.get(i).get(0);
                try {
                Optional<DBdetails> o= Optional.ofNullable(repo.findByDomain(domain.toLowerCase()));
                if(o.isEmpty())
                    s= s+domain+">>";
                else
                {
                    if(!o.get().isEstFlag())
                        s= s+domain+">>";
                }
                }
                catch(NonUniqueResultException e)
                {
                    List<DBdetails> l=repo.findAllByDomain(domain.toLowerCase());
                    if(!l.get(l.size()-1).isEstFlag())
                        s = s + domain + ">>";

                }
                catch(IncorrectResultSizeDataAccessException e)
                {
                    List<DBdetails> l=repo.findAllByDomain(domain.toLowerCase());
                    if(!l.get(l.size()-1).isEstFlag())
                        s = s + domain + ">>";

                }
                catch(Exception e)
                {
                    logger.info(domain+e.getMessage());
                }
            }
            if(!s.equals("")) {


                Estibot_Response estibot_response = estibot.getEstibot(estKey, "appraise", "cache", s);
                List<Estibot_Data> list = estibot_response.getResults().getData();
                List<String> notFound = estibot_response.getNot_found();
                if(notFound!=null&&(!notFound.isEmpty())) {
                    s = "";
                    for (int i = 0; i < notFound.size(); i++) {
                        String domain = notFound.get(i);
                        s = s + domain + ">>";
                    }
                    Estibot_Response estibot_response1 = estibot.getEstibot(estKey, "appraise", "live", s);


                    list.addAll(estibot_response1.getResults().getData());
                }
                return list;
            }
            else return null;

        },threadPoolExecutor);
    }

    @Async
    void putESTinDB(CompletableFuture<List<Estibot_Data>> cf)
    {

            List<Estibot_Data> list=null;
            try {
                 list = cf.get();
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
            if(list!=null)
            for(int i=0;i<list.size();i++)
            {
                Estibot_Data ed = list.get(i);
                String domain = ed.getDomain();
                try {
                    Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
                    if (op.isPresent()) {
                        DBdetails db= op.get();
                        db.setEstValues(ed.getAppraised_value(), ed.getKeyword_exact_local_search_volume(), ed.getKeyword_exact_cpc(), ed.getWhois_create_date(), ed.getWhois_registrar(), ed.getEnd_user_buyers(), ed.getWayback_age(), ed.getAppraised_wholesale_value(), ed.getNum_words(), ed.getIs_cctld(), ed.getIs_ntld(), ed.getIs_adult(), ed.getIs_reversed(), ed.getNum_numbers(), ed.getSld_length(), ed.getSearch_ads_phrase(), ed.getHas_trademark(), ed.getBacklinks(), ed.getWayback_records(), ed.getPronounceability_score(), ed.getLanguage(), ed.getLanguage_probability(), ed.getCategory(), ed.getCategory_root(), ed.getFirst_word(), ed.getSecond_word(),ed.getExtensions_taken());
                        repo.save(db);
                    }

                }
                catch (IncorrectResultSizeDataAccessException ef)
                {
                    List<DBdetails> l=repo.findAllByDomain(domain.toLowerCase());
                    for(int j=0;j<l.size();j++)
                    {
                        DBdetails db= l.get(j);
                        db.setEstValues(ed.getAppraised_value(), ed.getKeyword_exact_local_search_volume(), ed.getKeyword_exact_cpc(), ed.getWhois_create_date(), ed.getWhois_registrar(), ed.getEnd_user_buyers(), ed.getWayback_age(), ed.getAppraised_wholesale_value(), ed.getNum_words(), ed.getIs_cctld(), ed.getIs_ntld(), ed.getIs_adult(), ed.getIs_reversed(), ed.getNum_numbers(), ed.getSld_length(), ed.getSearch_ads_phrase(), ed.getHas_trademark(), ed.getBacklinks(), ed.getWayback_records(), ed.getPronounceability_score(), ed.getLanguage(), ed.getLanguage_probability(), ed.getCategory(), ed.getCategory_root(), ed.getFirst_word(), ed.getSecond_word(),ed.getExtensions_taken());
                        repo.save(db);
                    }

                }
                catch (NonUniqueResultException ef)
                {
                    List<DBdetails> l=repo.findAllByDomain(domain.toLowerCase());
                    for(int j=0;j<l.size();j++)
                    {
                        DBdetails db= l.get(j);
                        db.setEstValues(ed.getAppraised_value(), ed.getKeyword_exact_local_search_volume(), ed.getKeyword_exact_cpc(), ed.getWhois_create_date(), ed.getWhois_registrar(), ed.getEnd_user_buyers(), ed.getWayback_age(), ed.getAppraised_wholesale_value(), ed.getNum_words(), ed.getIs_cctld(), ed.getIs_ntld(), ed.getIs_adult(), ed.getIs_reversed(), ed.getNum_numbers(), ed.getSld_length(), ed.getSearch_ads_phrase(), ed.getHas_trademark(), ed.getBacklinks(), ed.getWayback_records(), ed.getPronounceability_score(), ed.getLanguage(), ed.getLanguage_probability(), ed.getCategory(), ed.getCategory_root(), ed.getFirst_word(), ed.getSecond_word(),ed.getExtensions_taken());
                        repo.save(db);
                    }
                }
                catch(Exception e)
                {
                    logger.info(e.getMessage()+domain);
                }
            }
    }

    Estibot_Data getEstibotSync(String domain)
    {
        Estibot_Response estibot_response =null;
        try {
            estibot_response = estibot.getEstibot(estKey, "appraise", "cache", domain);
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
            return new Estibot_Data(domain,-1);
        }


        List<String> notFound = estibot_response.getNot_found();
        if(notFound!=null&&(!notFound.isEmpty()))
            estibot_response = estibot.getEstibot(estKey, "appraise", "live", domain);

        if(estibot_response==null||estibot_response.getResults().getData()==null)
            return new Estibot_Data(domain,-1);
        return estibot_response.getResults().getData().get(0);

    }

    CompletableFuture<Estibot_Data> getEstibotDomain(String domain)
    {
        return CompletableFuture.supplyAsync(()->{
            Estibot_Response estibot_response=null;
                try {
                    Optional<DBdetails> o= Optional.ofNullable(repo.findByDomain(domain.toLowerCase()));
                    if(o.isEmpty())
                    {
                         estibot_response = estibot.getEstibot(estKey, "appraise", "cache", domain);
                    }
                    else
                    {
                        if(!o.get().isEstFlag())
                            estibot_response = estibot.getEstibot(estKey, "appraise", "cache", domain);
                    }
                }
                catch(NonUniqueResultException e)
                {
                    List<DBdetails> l=repo.findAllByDomain(domain.toLowerCase());
                    if(!l.get(l.size()-1).isEstFlag())
                        estibot_response = estibot.getEstibot(estKey, "appraise", "cache", domain);


                }
                catch(IncorrectResultSizeDataAccessException e)
                {
                    List<DBdetails> l=repo.findAllByDomain(domain.toLowerCase());
                    if(!l.get(l.size()-1).isEstFlag())
                        estibot_response = estibot.getEstibot(estKey, "appraise", "cache", domain);

                }
                catch(Exception e)
                {
                    logger.info(domain+e.getMessage());
                }

            if(estibot_response!=null) {
                List<String> notFound = estibot_response.getNot_found();
                if(notFound!=null&&(!notFound.isEmpty()))
                 estibot_response = estibot.getEstibot(estKey, "appraise", "live", domain);

                return estibot_response.getResults().getData().get(0);
            }
            else return null;

        },threadPoolExecutor);
    }

    @Async
    void putESTinDBSingle(CompletableFuture<Estibot_Data> cf)
    {

        Estibot_Data ed=null;
        try {
            ed = cf.get();
        }
        catch(Exception e)
        {
            logger.info(e.getMessage());
        }
        if(ed!=null) {
            String domain = ed.getDomain();
            try {
                Optional<DBdetails> op = Optional.ofNullable(repo.findByDomain(domain));
                if (op.isPresent()) {
                    DBdetails db = op.get();
                    db.setEstValues(ed.getAppraised_value(), ed.getKeyword_exact_local_search_volume(), ed.getKeyword_exact_cpc(), ed.getWhois_create_date(), ed.getWhois_registrar(), ed.getEnd_user_buyers(), ed.getWayback_age(), ed.getAppraised_wholesale_value(), ed.getNum_words(), ed.getIs_cctld(), ed.getIs_ntld(), ed.getIs_adult(), ed.getIs_reversed(), ed.getNum_numbers(), ed.getSld_length(), ed.getSearch_ads_phrase(), ed.getHas_trademark(), ed.getBacklinks(), ed.getWayback_records(), ed.getPronounceability_score(), ed.getLanguage(), ed.getLanguage_probability(), ed.getCategory(), ed.getCategory_root(), ed.getFirst_word(), ed.getSecond_word(),ed.getExtensions_taken());
                    repo.save(db);
                }

            } catch (IncorrectResultSizeDataAccessException ef) {
                List<DBdetails> l = repo.findAllByDomain(domain.toLowerCase());
                for (int j = 0; j < l.size(); j++) {
                    DBdetails db = l.get(j);
                    db.setEstValues(ed.getAppraised_value(), ed.getKeyword_exact_local_search_volume(), ed.getKeyword_exact_cpc(), ed.getWhois_create_date(), ed.getWhois_registrar(), ed.getEnd_user_buyers(), ed.getWayback_age(), ed.getAppraised_wholesale_value(), ed.getNum_words(), ed.getIs_cctld(), ed.getIs_ntld(), ed.getIs_adult(), ed.getIs_reversed(), ed.getNum_numbers(), ed.getSld_length(), ed.getSearch_ads_phrase(), ed.getHas_trademark(), ed.getBacklinks(), ed.getWayback_records(), ed.getPronounceability_score(), ed.getLanguage(), ed.getLanguage_probability(), ed.getCategory(), ed.getCategory_root(), ed.getFirst_word(), ed.getSecond_word(),ed.getExtensions_taken());
                    repo.save(db);
                }

            } catch (NonUniqueResultException ef) {
                List<DBdetails> l = repo.findAllByDomain(domain.toLowerCase());
                for (int j = 0; j < l.size(); j++) {
                    DBdetails db = l.get(j);
                    db.setEstValues(ed.getAppraised_value(), ed.getKeyword_exact_local_search_volume(), ed.getKeyword_exact_cpc(), ed.getWhois_create_date(), ed.getWhois_registrar(), ed.getEnd_user_buyers(), ed.getWayback_age(), ed.getAppraised_wholesale_value(), ed.getNum_words(), ed.getIs_cctld(), ed.getIs_ntld(), ed.getIs_adult(), ed.getIs_reversed(), ed.getNum_numbers(), ed.getSld_length(), ed.getSearch_ads_phrase(), ed.getHas_trademark(), ed.getBacklinks(), ed.getWayback_records(), ed.getPronounceability_score(), ed.getLanguage(), ed.getLanguage_probability(), ed.getCategory(), ed.getCategory_root(), ed.getFirst_word(), ed.getSecond_word(),ed.getExtensions_taken());
                    repo.save(db);
                }
            } catch (Exception e) {
                logger.info(e.getMessage() + domain);
            }
        }
    }

    @GetMapping("/gettargetshighest")
    List<DBdetails> getTargetshighest()
    {
        DBdetails dd= repo.findTopByPlatformAndScheduledTrueOrderByBidAmountDesc("Dynadot");
        DBdetails dc= repo.findTopByPlatformAndScheduledTrueOrderByBidAmountDesc("Dropcatch");
        DBdetails nc= repo.findTopByPlatformAndScheduledTrueOrderByBidAmountDesc("Namecheap");
        DBdetails gd= repo.findTopByPlatformAndScheduledTrueOrderByBidAmountDesc("GoDaddy");
        DBdetails ns= repo.findTopByPlatformAndScheduledTrueOrderByBidAmountDesc("Namesilo");

        List<DBdetails> list=new ArrayList<>();
        list.add(dd);list.add(dc);list.add(nc);list.add(ns);list.add(gd);
        return list;
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

    @GetMapping("/registercron")
    void registerDomain(@RequestParam String domain,@RequestParam String date){
        Date d=null;
        try {
            d = ft.parse(date+" 23:30");
        }
        catch(ParseException p)
        {
           logger.info(p.getMessage());
        }
        String d1= parser.format(d)+"Z";
        RegisterDomain registerDomain=new RegisterDomain(domain,d1);
        ScheduledFuture scheduledFuture=taskScheduler.scheduleAtFixedRate(registerDomain,d,1000);
        d.setHours(d.getHours()+1);
        ScheduledFuture scheduledFuture1= taskScheduler.schedule(new StopRegisterDomain(scheduledFuture),d);
        registerDomain.setScheduledFuture(scheduledFuture);registerDomain.setScheduledFuture1(scheduledFuture1);
    }

    class RegisterDomain implements Runnable
    {
        String domain;String d;

        ScheduledFuture scheduledFuture;ScheduledFuture scheduledFuture1;

        public ScheduledFuture getScheduledFuture() {
            return scheduledFuture;
        }

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }
        public ScheduledFuture getScheduledFuture1() {
            return scheduledFuture1;
        }

        public void setScheduledFuture1(ScheduledFuture scheduledFuture1) {
            this.scheduledFuture1 = scheduledFuture1;
        }

        public RegisterDomain(String domain,String d) {
            this.domain = domain;
            this.d=d;
        }

        public void run()
        {
            try
            {
                PurchaseResp resp= goDaddyController.registerDomain(domain,d);
                scheduledFuture.cancel(true);scheduledFuture1.cancel(true);
                logger.info("Purchased domain "+domain);
            }
            catch(Exception e)
            {
                logger.info(e.getMessage());
            }
        }
    }

    class StopRegisterDomain implements Runnable
    {
        ScheduledFuture scheduledFuture;

        public ScheduledFuture getScheduledFuture() {
            return scheduledFuture;
        }

        public void setScheduledFuture(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public StopRegisterDomain(ScheduledFuture scheduledFuture) {
            this.scheduledFuture = scheduledFuture;
        }

        public void run()
        {
            scheduledFuture.cancel(true);
            logger.info("Not able to purchase");
        }
    }

}
