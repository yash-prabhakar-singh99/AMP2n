package com.namekart.amp2.Controller;
//import com.orgyflame.springtelegrambotapi.bot.mapping.BotController;


import com.namekart.amp2.Entity.BulkScheduleResponse;
import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.Entity.FetchReq;
import com.namekart.amp2.EstibotEntity.Estibot_Data;
import com.namekart.amp2.Feign.Estibot;
import com.namekart.amp2.Feign.Namecheapfeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.GoDaddyEntities.Closeoutdb;
import com.namekart.amp2.Repository.Closeoutrepo;
import com.namekart.amp2.Repository.MyRepo;
import com.namekart.amp2.Status;
import com.namekart.amp2.TelegramEntities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

@RestController
@CrossOrigin
public class BotController {

    @Autowired
    NamesiloController namesiloController;
    @Autowired
    NamecheapController namecheapController;
    @Autowired
    Controller dynadotController;
    @Autowired
    GoDaddyController goDaddyController;

    String filler="";
    @Autowired
    DropCatchController dropCatchController;
    String bearernc = "Bearer ef7b03f63d8a94e2f083b991a74dd5852s5DuDtyOc9Ft1QZ5u0plxLpA0vlYdHFxEccAez6lh/wUyQNkOTCfqcOgrYMcvG4";

    @Autowired
    Namecheapfeign namecheapfeign;

    @Autowired
    MyRepo repo;

    SimpleDateFormat ft1 = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

    //SimpleDateFormat timeft = new SimpleDateFormat("dd/MM HH:mm");
    TimeZone ist = TimeZone.getTimeZone("IST");
    String bidCmd,fetchCmd,watchCmd,scheduledCmd,watchlistCmd,wonCmd,lostCmd,escCmd,upcomingCmd,resultsCmd,toolsCmd,backorderCmd,leadCmd,liveCmd;
    Map<Long,Tsession> users;

    ReplyKeyboardMarkup keyboardMarkup;

    SendMessage sendPlatformKeyboard,sendPlatformKeyboardbid,sendUpcKeyboard,sendToolsKeyboard,sendResultsKeyboard,sendPlatformKeyboardbo,sendlivelistkeyboard;

    Map<String,SendMessage> sendPlatOps= new HashMap<>();

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    ConcurrentMap<String, Status> taskmap;

    void deleteTaskMap(String domain)
    {
        domain= domain.toLowerCase();
        if(taskmap.containsKey(domain))
        {   taskmap.get(domain).getFuture().cancel(false);
            taskmap.remove(domain);}
    }
    void cancelBid(String domain)
    {
        taskmap.get(domain).getFuture().cancel(false);
        taskmap.remove(domain);
        DBdetails db= repo.findByDomainIgnoreCaseAndScheduledTrue(domain);
        db.setResult("Bid Cancelled");
        db.setScheduled(false);
        repo.save(db);
    }
    AllController controller;
    public BotController(AllController controller) {
        this.controller=controller;
        this.taskmap=controller.getTaskmap();
        upcomingCmd="⏳Upcoming";resultsCmd="\uD83D\uDCDDResults";toolsCmd="⚙Tools";
        bidCmd="\uD83D\uDCB8Bid";watchCmd="⭐Watch";watchlistCmd="⏰Watchlist";lostCmd="\uD83D\uDE35\u200D\uD83D\uDCABLost";
        fetchCmd="\uD83D\uDD0EFetch";scheduledCmd="⏳Scheduled";wonCmd="\uD83E\uDD11Won";escCmd="❌Esc";
        backorderCmd="\uD83D\uDCB0Backorder";leadCmd="\uD83D\uDC40Leads";liveCmd="⚡️Live";
        ReplyKeyboardButton button1= new ReplyKeyboardButton(watchCmd);
        ReplyKeyboardButton button2= new ReplyKeyboardButton(bidCmd);
        ReplyKeyboardButton button3= new ReplyKeyboardButton(upcomingCmd);
        ReplyKeyboardButton button4= new ReplyKeyboardButton(toolsCmd);
        ReplyKeyboardButton button5= new ReplyKeyboardButton(resultsCmd);
        ReplyKeyboardButton button8= new ReplyKeyboardButton(escCmd);
        ReplyKeyboardButton button6= new ReplyKeyboardButton(backorderCmd);
        ReplyKeyboardButton button7= new ReplyKeyboardButton(leadCmd);
        //ReplyKeyboardButton button7= new ReplyKeyboardButton(lostCmd);
        //ReplyKeyboardButton button8= new ReplyKeyboardButton(cancelCmd);

        List<ReplyKeyboardButton> list1= new ArrayList<>();
        List<ReplyKeyboardButton> list2= new ArrayList<>();
        List<ReplyKeyboardButton> list3= new ArrayList<>();
        List<ReplyKeyboardButton> list4= new ArrayList<>();

        list1.add(button1);list1.add(button2);list2.add(button3);list2.add(button4);list3.add(button5);list3.add(button6);list4.add(button7);list4.add(button8);
        List<List<ReplyKeyboardButton>> keyboard= new ArrayList<>();
        keyboard.add(list1);keyboard.add(list2);keyboard.add(list3);keyboard.add(list4);
        keyboardMarkup= new ReplyKeyboardMarkup(keyboard,true,true);
        users= new HashMap<>();
        //Prakhar                  Yash
        users.put(851358113l,new Tsession(3,"","",""));users.put(1210466480l,new Tsession(3,"","",""));
        //users.put(1886622310l,new Tsession(3,"","",""));
        users.put(6087300016l,new Tsession(3,"","",""));users.put(6131686452l,new Tsession(3,"","",""));
        users.put(706865236l,new Tsession(2,"","",""));users.put(276002250l,new Tsession(2,"","",""));
        users.put(5575459640l,new Tsession(3,"","",""));
        InlineKeyboardButton dynadot=new InlineKeyboardButton("Dynadot","w Dynadot");
        InlineKeyboardButton dropcatch=new InlineKeyboardButton("Dropcatch","w Dropcatch");
        InlineKeyboardButton namecheap=new InlineKeyboardButton("Namecheap","w Namecheap");
        InlineKeyboardButton godaddy=new InlineKeyboardButton("GoDaddy","w GoDaddy");
        InlineKeyboardButton namesilo=new InlineKeyboardButton("Namesilo","w Namesilo");
        List<InlineKeyboardButton> row1= new ArrayList<>();
        List<InlineKeyboardButton> row2= new ArrayList<>();
        List<InlineKeyboardButton> row3= new ArrayList<>();

        row1.add(dynadot);row1.add(dropcatch);row2.add(namecheap);row2.add(namesilo);row3.add(godaddy);;
        List<List<InlineKeyboardButton>> platKeyboard= new ArrayList<>();
        platKeyboard.add(row1);platKeyboard.add(row2);platKeyboard.add(row3);
        InlineKeyboardMarkup platformMarkup= new InlineKeyboardMarkup(platKeyboard);
        sendPlatformKeyboard=new SendMessage(null,"Choose the platform for watchlisting:",platformMarkup);

        InlineKeyboardButton ddlive=new InlineKeyboardButton("DD Live","ll1 Dynadot");
        InlineKeyboardButton dclive=new InlineKeyboardButton("DC Live","ll1 Dropcatch");
        InlineKeyboardButton nclive=new InlineKeyboardButton("NC Live","ll1 Namecheap");
        //InlineKeyboardButton gdlive=new InlineKeyboardButton("GD Live","ll1 GoDaddy");
        InlineKeyboardButton nslive=new InlineKeyboardButton("NS Live","ll1 Namesilo");
        List<InlineKeyboardButton> row1l= new ArrayList<>();
        List<InlineKeyboardButton> row2l= new ArrayList<>();
        //List<InlineKeyboardButton> row3l= new ArrayList<>();

        row1l.add(ddlive);row1l.add(dclive);row2l.add(nclive);row2l.add(nslive);//row3.add(godaddy);;
        List<List<InlineKeyboardButton>> livelistKeyboard= new ArrayList<>();
        livelistKeyboard.add(row1l);livelistKeyboard.add(row2l);//livelistKeyboard.add(row3l);
        InlineKeyboardMarkup llMarkup= new InlineKeyboardMarkup(livelistKeyboard);
        sendlivelistkeyboard=new SendMessage(null,"Choose the Live List:",llMarkup);



        InlineKeyboardButton dynadotb=new InlineKeyboardButton("Dynadot","b Dynadot");
        InlineKeyboardButton dropcatchb=new InlineKeyboardButton("Dropcatch","b Dropcatch");
        InlineKeyboardButton namecheapb=new InlineKeyboardButton("Namecheap","b Namecheap");
        InlineKeyboardButton godaddyb=new InlineKeyboardButton("GoDaddy","b GoDaddy");
        InlineKeyboardButton namesilob=new InlineKeyboardButton("Namesilo","b Namesilo");
        InlineKeyboardButton gcb=new InlineKeyboardButton("GD Closeouts","b GDCloseouts");

        List<InlineKeyboardButton> row1b= new ArrayList<>();
        List<InlineKeyboardButton> row2b= new ArrayList<>();
        List<InlineKeyboardButton> row3b= new ArrayList<>();
      //  List<InlineKeyboardButton> row4b= new ArrayList<>();

        row1b.add(dynadotb);row1b.add(dropcatchb);row2b.add(namecheapb);row2b.add(namesilob);row3b.add(godaddyb);row3b.add(gcb);
        List<List<InlineKeyboardButton>> platKeyboardb= new ArrayList<>();
        platKeyboardb.add(row1b);platKeyboardb.add(row2b);platKeyboardb.add(row3b);
        InlineKeyboardMarkup platformMarkupb= new InlineKeyboardMarkup(platKeyboardb);
        sendPlatformKeyboardbid=new SendMessage(null,"Choose the platform for bidding:",platformMarkupb);
        String[] plats={"Dynadot","Dropcatch","Namecheap","Namesilo","GoDaddy","GDCloseouts"};

        for(int i=0;i<plats.length;i++)
        {
            InlineKeyboardButton main= new InlineKeyboardButton("Main","b1 "+plats[i]+" 1");
            InlineKeyboardButton alt= new InlineKeyboardButton("Alt","b1 "+plats[i]+" 2");
            InlineKeyboardButton both= new InlineKeyboardButton("Both","b1 "+plats[i]+" 3");

            List<InlineKeyboardButton> row1op= new ArrayList<>();
            List<InlineKeyboardButton> row2op= new ArrayList<>();
            row1op.add(main); row1op.add(alt); row2op.add(both);
            List<List<InlineKeyboardButton>> platops= new ArrayList<>();
            platops.add(row1op);platops.add(row2op);
            InlineKeyboardMarkup platopsm= new InlineKeyboardMarkup(platops);
            sendPlatOps.put(plats[i],new SendMessage(null,"Select account for "+plats[i],platopsm));

        }

        InlineKeyboardButton bo1=new InlineKeyboardButton("Level 1","bo 1");
        InlineKeyboardButton bo2=new InlineKeyboardButton("Level 2","bo 2");
        InlineKeyboardButton bo3=new InlineKeyboardButton("Level 3","bo 3");
        InlineKeyboardButton bo4=new InlineKeyboardButton("Level 4","bo 4");


        List<InlineKeyboardButton> row1bo= new ArrayList<>();
        List<InlineKeyboardButton> row2bo= new ArrayList<>();

        row1bo.add(bo1);row1bo.add(bo2); row2bo.add(bo3);row2bo.add(bo4);
        List<List<InlineKeyboardButton>> bokb= new ArrayList<>();
        bokb.add(row1bo);bokb.add(row2bo);
        InlineKeyboardMarkup platformMarkupbo= new InlineKeyboardMarkup(bokb);
        sendPlatformKeyboardbo=new SendMessage(null,"Choose BackOrder Level:",platformMarkupbo);

        InlineKeyboardButton cb=new InlineKeyboardButton("Cancel Bids","t cb");
        InlineKeyboardButton cc=new InlineKeyboardButton("Cancel Closeouts","t cc");
        InlineKeyboardButton be=new InlineKeyboardButton("Bulk EST","t be");
        InlineKeyboardButton fs=new InlineKeyboardButton("Full Stats","t fs");
        InlineKeyboardButton wi=new InlineKeyboardButton("Whois","t wi");
        InlineKeyboardButton oc=new InlineKeyboardButton("Own-Check","t oc");

        List<InlineKeyboardButton> row111= new ArrayList<>();
        List<InlineKeyboardButton> row222= new ArrayList<>();
        List<InlineKeyboardButton> row333= new ArrayList<>();
        row111.add(cb);row111.add(cc);row222.add(be);row222.add(fs);row333.add(wi);row333.add(oc);
        List<List<InlineKeyboardButton>> toolsKeyboard= new ArrayList<>();
        toolsKeyboard.add(row111);toolsKeyboard.add(row222);toolsKeyboard.add(row333);
        InlineKeyboardMarkup toolsMarkup= new InlineKeyboardMarkup(toolsKeyboard);
        sendToolsKeyboard=new SendMessage(null,"Select the tool you wanna use:",toolsMarkup);

        InlineKeyboardButton w4=new InlineKeyboardButton("Wins 4H","r Won 4");
        InlineKeyboardButton w12=new InlineKeyboardButton("Wins 12H","r Won 12");
        InlineKeyboardButton w24=new InlineKeyboardButton("Wins 24H","r Won 24");
        InlineKeyboardButton l4=new InlineKeyboardButton("Lost 4H","r Loss 4");
        InlineKeyboardButton l12=new InlineKeyboardButton("Lost 12H","r Loss 12");
        InlineKeyboardButton l24=new InlineKeyboardButton("Lost 24H","r Loss 24");
        InlineKeyboardButton byname=new InlineKeyboardButton("Search By Name","rn");
        InlineKeyboardButton bydate=new InlineKeyboardButton("Search By Date","rd");

        List<InlineKeyboardButton> roww1= new ArrayList<>();
        List<InlineKeyboardButton> roww2= new ArrayList<>();
        List<InlineKeyboardButton> roww3= new ArrayList<>();

        roww1.add(w4);roww1.add(w12);roww1.add(w24);roww2.add(l4);roww2.add(l12);roww2.add(l24);roww3.add(byname);roww3.add(bydate);
        List<List<InlineKeyboardButton>> resultsKeyboard= new ArrayList<>();
        resultsKeyboard.add(roww1);resultsKeyboard.add(roww2);resultsKeyboard.add(roww3);
        InlineKeyboardMarkup resultsMarkup= new InlineKeyboardMarkup(resultsKeyboard);
        sendResultsKeyboard=new SendMessage(null,"Select Winnings/Losings in the given timeslots:",resultsMarkup);

        InlineKeyboardButton gs=new InlineKeyboardButton("Scheduled","u gs");
        InlineKeyboardButton gw=new InlineKeyboardButton("Watchlist","u gw");
        InlineKeyboardButton gc=new InlineKeyboardButton("Closeouts","u gc");

        List<InlineKeyboardButton> row11= new ArrayList<>();
        List<InlineKeyboardButton> row22= new ArrayList<>();
        List<InlineKeyboardButton> row33= new ArrayList<>();
        row11.add(gs);row22.add(gw);row33.add(gc);
        List<List<InlineKeyboardButton>> upcKeyboard= new ArrayList<>();
        upcKeyboard.add(row11);upcKeyboard.add(row22);upcKeyboard.add(row33);
        InlineKeyboardMarkup upcMarkup= new InlineKeyboardMarkup(upcKeyboard);
        sendUpcKeyboard=new SendMessage(null,"Select the upcoming you wanna see:",upcMarkup);
        for(int i=0;i<66;i++)
            filler=filler+"_";
        ft1.setTimeZone(ist);
    }

    @Autowired
    Telegram telegram;
    Logger logger = Logger.getLogger("Telegram");

    @PostMapping("/callback/amp")
    Object callbackamp(@RequestBody Update update)
    {
        logger.info(""+update.getUpdate_id());
        if(update.isCallback())
        {
            logger.info(update.getCallback_query().getData());
            CallbackQuery callbackQuery=update.getCallback_query();
            Long chat_id= callbackQuery.getMessage().getChat().getId();
            Long message_id=callbackQuery.getMessage().getMessage_id();
            String chatType= callbackQuery.getMessage().getChat().getType();
            Long user_id= callbackQuery.getFrom().getId();
            String data= callbackQuery.getData();
            InlineKeyboardMarkup reply_markup=callbackQuery.getMessage().getReply_markup();
            Long message_thread_id= callbackQuery.getMessage().getMessage_thread_id();
                // telegram.answerCallback(update.getCallback_query().getId(),"Pressed "+update.getCallback_query().getData(),true);
                         //-1001653862522L
            //if(chat_id == -1001653862522L|| chat_id== -1001814695777L|| chat_id==-856441586L||chat_id==-1001706842871L||chat_id==-1001833712484L)
                if(chat_id==-1001763199668l||chat_id==-1001887754426l||chat_id==-1001866615838L)
                {
                    String[] arr = data.split(" ");
                    String p=arr[0];
                    String plat=arr[1];
                    String id = arr[2];
                    String domain = arr[3];
                    if(p.equals("b"))
                    {
                        if(users.containsKey(user_id)&&users.get(user_id).getPermission()>=3) {
                            if (arr.length == 5) {
                                List<List<InlineKeyboardButton>> inlinekeyboard=reply_markup.getInline_keyboard();
                                List<InlineKeyboardButton> r=inlinekeyboard.get(0);
                                if(r.size()==3)
                                {r.remove(0);r.remove(0);}
                                else inlinekeyboard.remove(0);
                                //namecheapController.schedulesingle(domain,ncid,p);
                                List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                                List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();


                                row.add(new InlineKeyboardButton("+10", data + " 10"));
                                row.add(new InlineKeyboardButton("+25", data + " 25"));
                                row.add(new InlineKeyboardButton("+50", data + " 50"));
                                row1.add(new InlineKeyboardButton("Custom", data.replaceFirst("b", "c")));
                               inlinekeyboard.add(0,row);inlinekeyboard.add(1,row1);
                                EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), reply_markup);
                                telegram.editMessage(edit);
                            }


                            //Bid buttons pressed:
                            else if (arr.length == 6) {
                                if (plat.equals("nc")) {
                                    Float bid = Float.valueOf(arr[4]) + Float.valueOf(arr[5]);
                                    Float f = namecheapController.schedulesingleoutbid(domain, id, bid,false);
                                    if (f == 0)
                                        telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " Scheduled for " + domain, false);
                                    else if (f == 1)
                                        telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " NOT SCHEDULED for " + domain, true);
                                    else if (f == 2)
                                        telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " NOT SCHEDULED for " + domain + " as auction has ended.", true);
                                    else
                                        telegram.answerCallback(callbackQuery.getId(), "Namecheap: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + f, true);

                                } else if (plat.equals("dd")) {
                                    String bid = String.valueOf(Math.ceil(Float.valueOf(arr[4]) + Float.valueOf(arr[5])));
                                    //Float f= message_thread_id!=null&&message_thread_id?dynadotController.mainmainsingle(domain, Long.valueOf(id),bid):dynadotController.mainmainsingleoutbid(domain, Long.valueOf(id),bid);
                                    Float f = dynadotController.mainmainsingle(domain, Long.valueOf(id), bid);

                                    if (f == 0)
                                        telegram.answerCallback(callbackQuery.getId(), "Dynadot: Max bid of " + bid + " Scheduled for " + domain, false);
                                    else if (f == 1)
                                        telegram.answerCallback(callbackQuery.getId(), "Dynadot: Max bid of " + bid + " NOT SCHEDULED for " + domain, true);
                                    else if (f == 2)
                                        telegram.answerCallback(callbackQuery.getId(), "Dynadot: Max bid of " + bid + " NOT SCHEDULED for " + domain + " as auction has ended.", true);
                                    else
                                        telegram.answerCallback(callbackQuery.getId(), "Dynadot: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + f, true);

                                } else if (plat.equals("ns")) {
                                    Float bid = Float.valueOf(arr[4]) + Float.valueOf(arr[5]);
                                    float f = namesiloController.scheduleSingleoutbid(Long.valueOf(id), domain, bid);
                                    if (f == 0)
                                        telegram.answerCallback(callbackQuery.getId(), "Namesilo: Max bid of " + bid + " Scheduled for " + domain, false);
                                    else if (f == 1)
                                        telegram.answerCallback(callbackQuery.getId(), "Namesilo: Max bid of " + bid + " NOT SCHEDULED for " + domain, true);
                                    else if (f == 2)
                                        telegram.answerCallback(callbackQuery.getId(), "Namesilo: Max bid of " + bid + " NOT SCHEDULED for " + domain + " as auction has ended.", true);
                                    else
                                        telegram.answerCallback(callbackQuery.getId(), "Namesilo: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + f, true);

                                } else if (plat.equals("gd")) {
                                    String bid = String.valueOf(Integer.valueOf(arr[4]) + Integer.valueOf(arr[5]));
                                    Float f = goDaddyController.scheduleSingleOutbid(domain, id, bid);
                                    if (f == 0)
                                        telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Max bid of " + bid + " Scheduled for " + domain, false);
                                    else if (f == 1)
                                        telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Max bid of " + bid + " NOT SCHEDULED for " + domain, true);
                                    else if (f == 2)
                                        telegram.answerCallback(callbackQuery.getId(), "Namesilo: Max bid of " + bid + " NOT SCHEDULED for " + domain + " as auction has ended.", true);
                                    else
                                        telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + f, true);

                                }
                                if (plat.equals("dc")) {
                                    Long bid = Long.valueOf(arr[4]) + Long.valueOf(arr[5]);
                                    long l = dropCatchController.scheduleSingle(domain, Long.valueOf(id), bid);
                                    if (l == 0)
                                        telegram.answerCallback(callbackQuery.getId(), "Dropcatch: Max bid of " + bid + " Scheduled for " + domain, false);
                                    else if (l == 2)
                                        telegram.answerCallback(callbackQuery.getId(), "Dropcatch: Max bid of " + bid + " NOT SCHEDULED for " + domain + " as auction has ended.", true);
                                    else
                                        telegram.answerCallback(callbackQuery.getId(), "Dropcatch: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + l, true);

                                }

                            }
                        }
                        else {
                            telegram.answerCallback(callbackQuery.getId(), "Not Authorised", false);
                        }
                        //telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " +p+ " Scheduled for " + domain, false);
                    }
                    if(p.equals("-b"))
                    {
                        if (plat.equals("nc")) {
                            Float bid = Float.valueOf(arr[4]) + Float.valueOf(arr[5]);
                            Float f = namecheapController.schedulesingleoutbid(domain, id, bid,true);
                            if (f == 0)
                                telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " Scheduled for " + domain, false);
                            else if (f == 1)
                                telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " NOT SCHEDULED for " + domain, true);
                            else if (f == 2)
                                telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " NOT SCHEDULED for " + domain + " as auction has ended.", true);
                            else
                                telegram.answerCallback(callbackQuery.getId(), "Namecheap: Bid NOT SCHEDULED for " + domain + " as bid value is lower than accepted bid of " + f, true);

                        }
                    }
                    else if(p.equals("c"))
                    {
                        List<List<InlineKeyboardButton>> inlinekeyboard=reply_markup.getInline_keyboard();
                        List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row2= new ArrayList<InlineKeyboardButton>();


                        data= data.replaceFirst("c","b");

                        row1.add(new InlineKeyboardButton("+75",data+" 75"));
                        row1.add(new InlineKeyboardButton("+100", data+" 100"));
                        row1.add(new InlineKeyboardButton("+150", data+" 150"));
                        row2.add(new InlineKeyboardButton("+200",data+" 200"));
                        row2.add(new InlineKeyboardButton("+300", data+" 300"));
                        row2.add(new InlineKeyboardButton("+500", data+" 500"));

                        inlinekeyboard.remove(1);
                        inlinekeyboard.add(1,row1);inlinekeyboard.add(2,row2);
                        EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), reply_markup);
                        telegram.editMessage(edit);
                    }
                    else if(p.equals("cn"))
                    {
                        List<List<InlineKeyboardButton>> inlinekeyboard=reply_markup.getInline_keyboard();
                        List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row2= new ArrayList<InlineKeyboardButton>();


                        data= data.replaceFirst("cn","b");

                        row1.add(new InlineKeyboardButton("+100",data+" 100"));
                        row1.add(new InlineKeyboardButton("+250", data+" 250"));
                        row1.add(new InlineKeyboardButton("+500", data+" 500"));
                        row2.add(new InlineKeyboardButton("+100 Alt","-"+data+" 100"));
                        row2.add(new InlineKeyboardButton("+250 Alt", "-"+data+" 250"));
                        row2.add(new InlineKeyboardButton("+500 Alt", "-"+data+" 500"));

                        inlinekeyboard.remove(0);
                        inlinekeyboard.add(0,row1);inlinekeyboard.add(1,row2);
                        EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), reply_markup);
                        telegram.editMessage(edit);
                    }
                    else if(p.equals("w"))
                    {
                        if(plat.equals("nc"))
                        {
                            namecheapController.watchlistLive(domain,id,false);
                        }
                        else if(plat.equals("dd"))
                        {
                            dynadotController.watchListLive(domain,Long.valueOf(id),false);
                        }
                        else if(plat.equals("ns"))
                        {
                            namesiloController.watchlistLive(Long.valueOf(id),domain,false);
                        }
                        else if(plat.equals("gd"))
                        {
                            goDaddyController.watchlistLive(domain,id,false);
                        }
                        else if(plat.equals("dc"))
                        {
                            dropCatchController.watchlistLive(domain,Long.valueOf(id),false);
                        }
                        telegram.answerCallback(callbackQuery.getId(), domain+" added to watchlist", false);

                    }
                    else if(p.equals("r"))
                    {
                        //telegram.answerCallback(callbackQuery.getId(), "", false);

                        if(plat.equals("nc"))
                        {
                            namecheapController.refreshBot(domain,id,chat_id,message_thread_id,reply_markup);
                        }
                        else if(plat.equals("dd"))
                        {
                            dynadotController.refreshBot(domain,Long.valueOf(id),chat_id,message_thread_id,reply_markup);
                        }
                        else if(plat.equals("ns"))
                        {
                            namesiloController.refreshBot(domain,Long.valueOf(id),chat_id,message_thread_id,reply_markup);
                        }
                        else if(plat.equals("gd"))
                        {
                            goDaddyController.refreshBot(domain,chat_id,message_thread_id,reply_markup);
                        }
                        else if(plat.equals("dc"))
                        {
                            dropCatchController.refreshBot(domain,Long.valueOf(id),chat_id,message_thread_id,reply_markup);
                        }
                        telegram.answerCallback(callbackQuery.getId(), "", false);
                    }
                    else if(p.equals("t"))
                    {
                        telegram.answerCallback(callbackQuery.getId(), "Tracking "+domain, false);

                        if(plat.equals("nc"))
                        {
                            namecheapController.watchlistLive(domain,id,true);
                        }
                        else if(plat.equals("dd"))
                        {
                            dynadotController.watchListLive(domain,Long.valueOf(id),true);
                        }
                        else if(plat.equals("ns"))
                        {
                            namesiloController.watchlistLive(Long.valueOf(id),domain,true);
                        }
                        else if(plat.equals("gd"))
                        {
                            goDaddyController.watchlistLive(domain,id,true);
                        }
                        else if(plat.equals("dc"))
                        {
                            dropCatchController.watchlistLive(domain,Long.valueOf(id),true);
                        }
                        telegram.answerCallback(callbackQuery.getId(), domain+" added to watchlist", false);
                    }
                    else if(p.equals("rw"))
                    {
                        telegram.answerCallback(callbackQuery.getId(), domain+" : Stopped Tracking",false);

                        if(plat.equals("nc"))
                        {
                           DBdetails db=repo.findByPlatformAndNamecheapid("Namecheap",id);
                           //db.setWatchlist(false);
                           db.setTrack(false);
                           repo.save(db);
                        }
                        else if(plat.equals("dd"))
                        {
                            DBdetails db=repo.findByPlatformAndAuctionId("Dynadot",Long.valueOf(id));
                            //db.setWatchlist(false);
                            db.setTrack(false);
                            repo.save(db);
                        }
                        else if(plat.equals("dc"))
                        {
                            DBdetails db=repo.findByPlatformAndAuctionId("Dropcatch",Long.valueOf(id));
                            //db.setWatchlist(false);
                            db.setTrack(false);
                            repo.save(db);
                        }
                        else if(plat.equals("ns"))
                        {
                            DBdetails db=repo.findByPlatformAndAuctionId("Namesilo",Long.valueOf(id));
                            //db.setWatchlist(false);
                            db.setTrack(false);
                            repo.save(db);
                        }
                        else if(plat.equals("gd"))
                        {
                            DBdetails db=repo.findByPlatformAndDomain("GoDaddy",domain.toLowerCase());
                            //db.setWatchlist(false);
                            db.setTrack(false);
                            repo.save(db);
                        }
                    }
                    else if(p.equals("m"))
                    {
                        DBdetails db= repo.findByWatchlistTrueAndDomainIgnoreCase(domain);
                        if(db!=null) {
                            db.setMute(!db.getMute());
                            repo.save(db);
                            telegram.answerCallback(callbackQuery.getId(), domain + (db.getMute() ? " Muted" : " Un-Muted"), false);
                        }
                        else telegram.answerCallback(callbackQuery.getId(), domain + " not found in Watchlist", false);

                    }
                }
                else if (chatType.equals("private"))
                {
                    telegram.answerCallback(callbackQuery.getId());
                    Tsession tsession= users.get(user_id);
                    String[] cdata=callbackQuery.getData().split(" ");
                    String cmd=cdata[0];
                    if(cmd.equals("b1"))
                    {
                        telegram.sendAlert(chat_id,"Enter domains with their prices in a format:\nabc.com,155\nxyz.com,255\npqr.net,113");
                        tsession.setPrevCmd("b3");
                        String platform= cdata[1];
                        tsession.setPlatform(platform);
                        tsession.setData(cdata[2]);
                    }
                    else if(cmd.equals("b"))
                    {
                        String platform= cdata[1];
                        SendMessage sendMessage= sendPlatOps.get(platform);
                        tsession.setPrevCmd("b2");
                        sendMessage.setChat_id(chat_id);
                        telegram.sendKeyboard(sendMessage);
                        tsession.setPlatform(platform);
                    }
                    else if(tsession.getPrevCmd().equals("f1"))
                    {
                        telegram.sendAlert(chat_id,"Enter domains in a format:\nabc.com\nxyz.com\npqr.net");
                        tsession.setPrevCmd("f2");
                        String platform= cdata[1];
                        tsession.setPlatform(platform);
                    }
                    else if(cmd.equals("w"))
                    {
                        tsession.setPrevCmd("w2");
                        String platform= cdata[1];
                        tsession.setPlatform(platform);
                    }
                    else if(cmd.equals("bo"))
                    {
                        tsession.setPrevCmd("bo2");
                        String level= cdata[1];
                        if(level.equals("1"))
                        {
                            telegram.sendAlert(chat_id,"Enter domains in a format:\nabc.com\nxyz.com\npqr.net");

                        }
                        else if(level.equals("2"))
                        {
                            telegram.sendAlert(chat_id,"Enter domains with their prices in a format( If value is 11, you may skip writing it):\nabc.com,13\nxyz.com\npqr.net,16");

                        }
                        tsession.setPlatform(level);
                    }
                    else if(cmd.equals("t"))
                    {
                        String cdata1= cdata[1];
                        switch(cdata1)
                        {
                            case "cb": telegram.sendAlert(chat_id,"Enter domains in a format whose bids are to be cancelled:\nabc.com\nxyz.com\npqr.net");
                                tsession.setPrevCmd(cdata1);
                                break;
                            case "cc": telegram.sendAlert(chat_id,"Feature soon to be added.");
                                tsession.setPrevCmd("");
                                break;
                            case "be": telegram.sendAlert(chat_id,"Enter domains in a format:\nabc.com\nxyz.com\npqr.net");
                                tsession.setPrevCmd(cdata1);
                                break;
                            case "fs": telegram.sendAlert(chat_id,"Enter domains in a format:\nabc.com\nxyz.com\npqr.net");
                                tsession.setPrevCmd(cdata1);
                                break;
                            case "wi": telegram.sendAlert(chat_id,"Feature soon to be added.");
                                tsession.setPrevCmd("");
                                break;
                            case "oc":telegram.sendAlert(chat_id,"Feature soon to be added.");
                                tsession.setPrevCmd("");
                                break;
                        }
                    }
                    else if(cmd.equals("r"))
                    {
                        if(cdata[1].equals("Won"))
                        {
                            int h= Integer.valueOf(cdata[2]);
                            sendWonList(h,chat_id);
                        }
                        else
                        {
                            int h= Integer.valueOf(cdata[2]);
                            sendLostList(h,chat_id);
                        }
                        tsession.setPrevCmd("");
                    }else if(cmd.equals("rn"))
                    {
                        telegram.sendAlert(chat_id,"Enter domains to be searched in a format:\nabc.com\nxyz.com\npqr.net");
                        tsession.setPrevCmd("rn");
                    }else if(cmd.equals("rd"))
                    {
                        telegram.sendAlert(chat_id,"Enter date in a format: yyyy-MM-dd");
                        tsession.setPrevCmd("rd");
                    }
                    else if(cmd.equals("u"))
                    {
                        String cmdd=cdata[1];
                        switch(cmdd)
                        {
                            case "gs": getScheduled(chat_id);
                                break;
                            case "gw": getWatchlisted(chat_id);
                                break;
                            case "gc": getCloseouts(chat_id);
                                break;
                        }
                        tsession.setPrevCmd("");
                    }
                    else if(cmd.equals("ll1"))
                    {
                        String plat=cdata[1];
                        switch(plat)
                        {
                            case "Namecheap": namecheapController.sendHighlights(chat_id);
                                break;
                            case "Namesilo": namesiloController.sendHighlights(chat_id);
                                break;
                            case "Dynadot": dynadotController.sendHighlights(chat_id);
                                break;
                            case "Dropcatch": dropCatchController.sendList(chat_id);
                                break;
                        }
                        tsession.setPrevCmd("");
                    }

                }
            return callbackQuery;
        }
        else if(update.getMessage()!=null)
        {
            Message message= update.getMessage();
            Long userId= message.getFrom().getId();
            String text= message.getText();
            String chatType= message.getChat().getType();
            Long chatId= message.getChat().getId();
            if(chatType.equals("private"))
            {
                if(text.equals("/start"))
                {
                    telegram.sendReplyKeyboard(new SendReplyKeyboard(chatId,"Hello!!\nAmazing Auction Hacker\uD83D\uDE0E \nUse my keyboard for various commands and follow the instructions.\nHave a great day ahead!\uD83E\uDEF6",keyboardMarkup));
                }
                else if(text.equals(bidCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()==3)
                    {
                        sendPlatformKeyboardbid.setChat_id(chatId);
                        telegram.sendKeyboard(sendPlatformKeyboardbid);
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("b1");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else if(text.equals(fetchCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()>=1)
                    {
                        sendPlatformKeyboard.setChat_id(chatId);
                        telegram.sendKeyboard(sendPlatformKeyboard);
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("f1");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else if(text.equals(watchCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()>=2)
                    {
                        sendPlatformKeyboard.setChat_id(chatId);
                        telegram.sendKeyboard(sendPlatformKeyboard);
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("w1");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else if(text.equals(toolsCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()>=2)
                    {
                        sendToolsKeyboard.setChat_id(chatId);
                        telegram.sendKeyboard(sendToolsKeyboard);
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("t1");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else if(text.equals(upcomingCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()>=2)
                    {
                        sendUpcKeyboard.setChat_id(chatId);
                        telegram.sendKeyboard(sendUpcKeyboard);
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("u1");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else if(text.equals(resultsCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()>=2)
                    {
                        sendResultsKeyboard.setChat_id(chatId);
                        telegram.sendKeyboard(sendResultsKeyboard);
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("r1");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else if(text.equals(backorderCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()>=3)
                    {
                        sendPlatformKeyboardbo.setChat_id(chatId);
                        telegram.sendKeyboard(sendPlatformKeyboardbo);
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("bo1");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else if(text.equals(leadCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()>=1)
                    {
                        telegram.sendAlert(chatId,"Enter a domain for the leads");
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("l1");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else if(text.equals(liveCmd))
                {
                    if(users.containsKey(userId)&&users.get(userId).getPermission()>=2)
                    {
                        sendlivelistkeyboard.setChat_id(chatId);
                        telegram.sendKeyboard(sendlivelistkeyboard);
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("ll");
                    }
                    else telegram.sendAlert(chatId,"Not authorised to use this command");
                }
                else if(text.equals(escCmd))
                {
                    if(users.containsKey(userId))
                    {
                        Tsession tsession= users.get(userId);
                        tsession.setPrevCmd("");
                        tsession.setData("");
                        tsession.setPlatform("");
                    }
                    else
                    {
                        telegram.sendAlert(chatId,"Not authorised to use this command");
                    }
                }
                else
                {
                    if(users.containsKey(userId))
                    {
                        Tsession tsession= users.get(userId);
                        String platform= tsession.getPlatform();
                        String data=tsession.getData();
                        String prevCmd= tsession.getPrevCmd();
                        logger.info(prevCmd);
                        switch(prevCmd)
                        {
                            case "b3": telegram.sendAlert(chatId,"Placing bid(s) please wait.. Check status of each bid in User Activity of AMP");
                                List<String> ds= Arrays.asList(text.trim().split("\\s*\n\\s*"));
                                List<List<String>> ddlist= new ArrayList<>();
                                for(int i=0;i<ds.size();i++)
                                {
                                    String d1=ds.get(i).trim();
                                    List<String> ds1= Arrays.asList(d1.split(",|  | "));
                                    int l1=ds1.size();
                                    logger.info(ds1.get(l1-2)+","+ds1.get(l1-1));
                                    ddlist.add(ds1);
                                }
                                List<Integer> res=null;
                                BulkScheduleResponse bs=null;
                                String s="";
                                if(platform.equals("Dynadot"))
                                {
                                     bs=dynadotController.mainmainbot(ddlist);
                                     res=bs.getL();s= bs.getStr();
                                }
                                else if(platform.equals("Dropcatch"))
                                {
                                    bs=dropCatchController.mainmain1bot(ddlist);
                                    res=bs.getL();s= bs.getStr();
                                }
                                else if(platform.equals("GoDaddy"))
                                {
                                    bs=goDaddyController.bulkbidschedulebot(ddlist);
                                    res=bs.getL();s= bs.getStr();
                                }
                                else if(platform.equals("Namecheap"))
                                {
                                    if(data.equals("1"))
                                    bs=namecheapController.bulkschedulebot(ddlist,false);
                                    else bs=namecheapController.bulkschedulebot(ddlist,true);

                                    res=bs.getL();s= bs.getStr();
                                }
                               /* else if(platform.equals("Namecheap1"))
                                {
                                    bs=namecheapController.bulkschedulebot1(ddlist);
                                    res=bs.getL();s= bs.getStr();
                                }*/
                                else if(platform.equals("Namesilo"))
                                {
                                    bs=namesiloController.bulkschedulensbot(ddlist);
                                    res=bs.getL();s= bs.getStr();
                                }
                                else if(platform.equals("GDCloseouts"))
                                {
                                    bs=goDaddyController.scheduleCloseoutsbot(ddlist);
                                    res=bs.getL();s= bs.getStr();
                                }
                                if(s!=""&&s!=null)
                                {
                                    telegram.sendAlert(chatId,"Bid Placed on "+res.get(0)+"/"+res.get(1)+" domains\n\n"+s);
                                }
                                else
                                telegram.sendAlert(chatId,"Bid Placed on "+res.get(0)+"/"+res.get(1)+" domains");
                                break;
                            case "f2": //telegram.sendAlert(chatId,"You will be able to fetch tomorrow ;)");
                                telegram.sendAlert(chatId,"Fetching...");
                                List<String> fl= Arrays.asList(text.trim().split("\\s*\n\\s*"));
                                FetchReq fetchReq= new FetchReq(fl,false);
                                List<DBdetails> re=null;
                                if(platform.equals("Dynadot"))
                                {
                                    re=dynadotController.fetch1(fetchReq);
                                }
                                else if(platform.equals("Dropcatch"))
                                {
                                    re=dropCatchController.fetchDetails(fetchReq);
                                }
                                else if(platform.equals("GoDaddy"))
                                {
                                    re=goDaddyController.bulkfetch(fetchReq);
                                }
                                else if(platform.equals("Namecheap"))
                                {
                                    re=namecheapController.fetchdetailsnc(fetchReq);
                                }
                                else if(platform.equals("Namesilo"))
                                {
                                    re=namesiloController.bulkfetchns(fetchReq);
                                }
                                if(re.isEmpty()||re==null)
                                {
                                    telegram.sendAlert(chatId,"No details found.");
                                }
                                else
                                {
                                    if(platform.equals("Dynadot")||platform.equals("Namecheap"))
                                        fetchFormat(re,chatId);
                                    else if (platform.equals("GoDaddy")) {
                                        fetchFormatgd(re,chatId);
                                    }else if (platform.equals("Dropcatch")) {
                                        fetchFormatdc(re,chatId);
                                    }
                                    else if (platform.equals("Namesilo")) {
                                        fetchFormatns(re,chatId);
                                    }
                                }
                                break;
                            case "w2": //telegram.sendAlert(chatId,"You will be able to fetch tomorrow ;)");
                                telegram.sendAlert(chatId,"Fetching and watching...");
                                List<String> fl1= Arrays.asList(text.trim().split("\\s*\n\\s*"));
                                FetchReq fetchReq1= new FetchReq(fl1,true);
                                List<DBdetails> re1=null;
                                if(platform.equals("Dynadot"))
                                {
                                    re1=dynadotController.fetch1(fetchReq1);
                                }
                                else if(platform.equals("Dropcatch"))
                                {
                                    re1=dropCatchController.fetchDetails(fetchReq1);
                                }
                                else if(platform.equals("GoDaddy"))
                                {
                                    re1=goDaddyController.bulkfetch(fetchReq1);
                                }
                                else if(platform.equals("Namecheap"))
                                {
                                    re1=namecheapController.fetchdetailsnc(fetchReq1);
                                }
                                else if(platform.equals("Namesilo"))
                                {
                                    re1=namesiloController.bulkfetchns(fetchReq1);
                                }
                                if(re1.isEmpty()||re1==null)
                                {
                                    telegram.sendAlert(chatId,"No details found.");
                                }
                                else
                                {
                                    if(platform.equals("Dynadot")||platform.equals("Namecheap"))
                                        fetchFormat(re1,chatId);
                                    else if (platform.equals("GoDaddy")) {
                                        fetchFormatgd(re1,chatId);
                                    }else if (platform.equals("Dropcatch")) {
                                        fetchFormatdc(re1,chatId);
                                    }
                                    else if (platform.equals("Namesilo")) {
                                        fetchFormatns(re1,chatId);
                                    }
                                }
                                break;
                            case "bo2": //telegram.sendAlert(chatId,"You will be able to fetch tomorrow ;)");
                                telegram.sendAlert(chatId,"Placing Orders...");
                                List<Integer> re1o=null;
                                if(platform.equals("1"))
                                {
                                    List<String> fl1o= Arrays.asList(text.trim().split("\\s*\n\\s*"));
                                    re1o=dropCatchController.placeBackorderStandard(fl1o);
                                }
                                else if(platform.equals("2"))
                                {
                                    List<String> dso= Arrays.asList(text.trim().split("\\s*\n\\s*"));
                                    List<List<String>> ddlisto= new ArrayList<>();
                                    for(int i=0;i<dso.size();i++)
                                    {
                                        String d1=dso.get(i).trim();
                                        List<String> ds1= Arrays.asList(d1.split(","));
                                        ddlisto.add(ds1);
                                    }
                                    re1o=dropCatchController.placeBackorderDiscount(ddlisto);
                                }
                                telegram.sendAlert(chatId,"Orders Placed on "+re1o.get(0)+"/"+re1o.get(1)+" domains");

                                break;
                            case "cb": telegram.sendAlert(chatId,"Cancelling...");
                                List<String> c= Arrays.asList(text.trim().split("\\s*\n\\s*"));
                            String a="Cancelled the bid on domain(s): ",b="Couldn't find domain(s): ";boolean a1=false,b1=false;
                            for(int i=0;i<c.size();i++)
                            {
                                String domain= c.get(i).trim().toLowerCase();
                                if(taskmap.containsKey(domain))
                                {
                                    cancelBid(domain);
                                    a=a+domain+" ";
                                    a1=true;
                                }
                                else
                                {
                                    b=b+domain+" ";
                                    b1=true;
                                }
                            }

                            String c1= (a1?a:"") + "\n"+(b1?b:"");
                                telegram.sendAlert(chatId,c1);
                                break;
                            case "be": telegram.sendAlert(chatId,"Fetching Estibot Appraisals..");
                                sendEST(new LinkedHashSet(Arrays.asList(text.trim().split("\\s*\n\\s*"))),chatId);
                                break;
                            case "fs": telegram.sendAlert(chatId,"Fetching Estibot Stats..");
                                getFullStats(Arrays.asList(text.trim().split("\\s*\n\\s*")),chatId);
                                break;
                            case "rn": //telegram.sendAlert(chatId,"Fetching details about these domains..");
                            searchByName(Arrays.asList(text.trim().split("\\s*\n\\s*")),chatId);
                            break;
                            case"rd": //telegram.sendAlert(chatId,"Fetching Results of this date..");
                            searchResultsByDate(text.trim(),chatId);
                            break;
                            case "l1": String leads=dynadotController.getLeads(text.trim().split("\\.",2)[0]);
                            if(leads.equals(""))
                                telegram.sendAlert(chatId,"No active leads found");
                            else telegram.sendAlert(chatId,"Leads: "+leads);
                        }
                        tsession.setPrevCmd("");
                    }
                }
            }

        }
        else
        {
            logger.info("Neither message, nor callback received but received a update to our webhook");
        }
        return null;
    }

    void fetchFormat(List<DBdetails> list,Long chat_id)
    {
        int n=25;
        //        time left   currbid,    bids,  est, separators, space around separators
        int t= n+    13+         7  +       4+  6  +  4   +      8;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-13s | %-7s | %-4s | %6s%n","Domain","Time Left", "Price","Bids","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                DBdetails lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-13s  | %-7s | %-4d | %6d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrbid(), lnc.getBids(),lnc.getEstibot());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }
    void fetchFormatgd(List<DBdetails> list,Long chat_id)
    {
        int n=25;
        //        time left   currbid,    bids,  separators, space around separators
        int t= n+    13+         7  +       4  +  3   +      6;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-13s | %-7s | %4s%n","Domain","Time Left", "Price","Bids");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                DBdetails lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-13s  | %-7s | %4d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrbid(), lnc.getBids());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void fetchFormatdc(List<DBdetails> list,Long chat_id)
    {
        int n=25;
        //        time left   currbid,    bidders,  separators, space around separators
        int t= n+    13+         7  +       7  +  3   +      6;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-13s | %-7s | %7s%n","Domain","Time Left", "Price","Bidders");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                DBdetails lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-13s  | %-7s | %7d%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrbid(), lnc.getBidders());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }
SimpleDateFormat format= new SimpleDateFormat("yyyy-MM-dd");
    void searchResultsByDate(String ft, Long chat_id)
    {
        try
        {
            format.parse(ft);
        }
        catch(Exception e)
        {
            telegram.sendAlert(chat_id,"Wrong Date Format.");
            return;
        }
        if(ft.length()!=10)
        {
            telegram.sendAlert(chat_id,"Wrong Date Format.");
            return;

        }
       // List<DBdetails> list= repo.findAllByEndTimeistStartsWithOrderByEndTimeistDesc(ft);
        List<DBdetails> list= repo.getResultListbyDate(ft+"%");

        if(list==null||list.isEmpty())
        {
            telegram.sendAlert(chat_id,"No auction ended on this date.");
            return;
        }

            int n = 25;
//        Plat   Result  High Bid  Our Max Bid  separators spaces
            int t = 9 + n + 6 + 8 + 11 + 4 + 8;
            int d = 4096 / t;
            d = d - 6;
            String s = "";
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
                telegram.sendAlert(chat_id, "<pre>" + s + "</pre>", "HTML");
                l = l - d;
                s = "";
            }

    }

    void searchByName(List<String> list, Long chat_id)
    {
        List<DBdetails> dbs= repo.findByDomainIgnoreCaseIn(list);
        if(dbs==null||dbs.isEmpty())
        {
            telegram.sendAlert(chat_id,"Couldn't find the auction by given name(s).");
            return;
        }
        logger.info(""+dbs.size());
        int n=25;
        //        Platform time left   currbid,Our BidStatus,EST  separators, space around separators
        int t= n+   9+        13+         7 +       7+    8+    7  +  5   +      10;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-9s | %-13s | %-7s | %-7s | %-8s | %7s%n","Domain","Platform","Time Left", "Price","Our Bid","Status","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=dbs.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                DBdetails lnc = dbs.get(j);
                j++;
                String bid="",sw="";
                if(lnc.getScheduled()||lnc.getResult().equals("Won")||lnc.getResult().equals("Loss"))
                {
                    bid=lnc.getBidAmount();
                    if(lnc.getScheduled())
                        sw=Float.valueOf(lnc.getBidAmount())>Float.valueOf(lnc.getCurrbid())?"Winning":"Losing";
                    else if(lnc.getResult().equals("Won"))
                        sw="Won";
                    else sw="Lost";
                }
                else
                {
                    bid="NaN";
                    if(lnc.isWatchlist())
                        sw="Watching";
                    else sw="NaN";
                }



                s = s + String.format("%-"+n+"s | %-9s | %-13s | %-7s | %-7s | %-8s | %7d%n", lnc.getDomain(),lnc.getPlatform(), lnc.getTime_left(),lnc.getCurrbid(),bid,sw,lnc.getEstibot());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    @PostMapping("/fi")
    List<DBdetails> findIn(@RequestBody List<String> domains)
    {
        return repo.findByDomainIgnoreCaseIn(domains);
    }

    void getScheduled(Long chat_id)
    {
        List<DBdetails> list= repo.findByScheduledTrueOrderByEndTimeistAsc();
        if(list==null||list.isEmpty())
        {
            telegram.sendAlert(chat_id,"No upcoming scheduled auctions right now.");
            return;
        }
        int n=25;
        //        Platform time left   currbid,Our BidStatus,EST  separators, space around separators
        int t= n+   9+        13+         7 +       7+    6+    7  +  5   +      10;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-9s | %-13s | %-7s | %-7s | %-6s | %7s%n","Domain","Platform","Time Left", "Price","Our Bid","Status","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                DBdetails lnc = list.get(j);

                j++;

                String sw=Float.valueOf(lnc.getBidAmount())>Float.valueOf(lnc.getCurrbid())?"Yay":"Nay";

                s = s + String.format("%-"+n+"s | %-9s | %-13s | %-7s | %-7s | %-6s | %7d%n", lnc.getDomain(),lnc.getPlatform(), lnc.getTime_left(),lnc.getCurrbid(),lnc.getBidAmount(),sw,lnc.getEstibot());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }
@Autowired
    Closeoutrepo closeoutrepo;
    void getCloseouts(Long chat_id)
    {
        List<Closeoutdb> list = closeoutrepo.findByStatusOrStatus("Closeout Scheduled","Closeout Recheck Scheduled");

        if(list==null||list.isEmpty())
        {
            telegram.sendAlert(chat_id,"No upcoming scheduled closeouts right now.");
            return;
        }
        int n=25;
        //        time left   currbid,Target,  separators, space around separators
        int t= n+    13+         7 +    6+     +  3  +      6;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-13s | %-7s | %7s%n","Domain","Time Left", "Price","Target");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Closeoutdb lnc = list.get(j);

                j++;

                s = s + String.format("%-"+n+"s | %-13s | %-7s | %7s%n", lnc.getDomain(), lnc.getTimeLeft(),lnc.getCurrPrice(),lnc.getOurPrice());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void getWatchlisted(Long chat_id)
    {
        List<DBdetails> list= repo.findByWatchlistTrueOrderByEndTimeist();
        if(list==null||list.isEmpty())
        {
            telegram.sendAlert(chat_id,"No upcoming watchlisted auctions right now.");
            return;
        }
        int n=25;
        //           platform, time left   currbid, EST  separators, space around separators
        int t= n+      +9+       13+         7 +    8  +  4   +      8;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-13s | %-7s | %-8s | %7s%n","Domain","Platform","Time Left", "Price","EST");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                DBdetails lnc = list.get(j);

                j++;

                s = s + String.format("%-"+n+"s | %-9s | %-13s | %-7s | %7d%n", lnc.getDomain(),lnc.getPlatform(), lnc.getTime_left(),lnc.getCurrbid(),lnc.getEstibot());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }
    void fetchFormatns(List<DBdetails> list,Long chat_id)
    {
        int n=25;
        //        time left   currbid,    separators, space around separators
        int t= n+    13+         7   +  2   +      4;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-13s | %7s%n","Domain","Time Left", "Price");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                DBdetails lnc = list.get(j);

                j++;
                s = s + String.format("%-"+n+"s | %-13s  | %7s%n", lnc.getDomain(), lnc.getTime_left(),lnc.getCurrbid());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }



    void sendEST(LinkedHashSet<String> domains, Long chat_id)
    {
        Estibot_Data[] list= controller.getEstibotListWeb(domains);
        int n=25;
        //        EST   separator U+0009
        int t= n+    7+ 1;
        String s="";
        int d= 4096/t;
        d=d-6;
        int l=list.length;
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Estibot_Data lnc = list[i];

                j++;
                s = s + lnc.getDomain()+" "+lnc.getAppraised_value()+"\n";

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,s);
            l=l-d;
            s="";
        }

    }

    void getFullStats(List<String> domains,Long chat_id)
    {
        List<Estibot_Data> list= controller.getEstibotsSync1(domains);
        int n=25;
        //           EST, Extns   SV, CPC   registrar+AWB+EUB separators, space around separators
        int t= n+   +6+    5+      4 +  4  +  15   +  3+    3+    8+16;
        int d= 4096/t;
        d=d-6;

        String s=String.format("%-"+n+"s | %-6s | %-5s | %-4s | %-4s | %-10s | %-3s | %3s%n","Domain","EST","Extns", "SV","CPC","Registrar","ABY","EUB");
        for(int i=0;i<t;i++)
        {
            s=s+"-";

        }
        s=s+"\n\n";
        int l=list.size();
        int j=0;
        while(l>0) {
            for (int i = 0; i < l && i < d; i++) {
                Estibot_Data lnc = list.get(j);

                j++;

                s = s + String.format("%-"+n+"s | %-6d | %-5d | %-4d | %-3.1f | %-10s | %-3d | %3d%n", lnc.getDomain(),lnc.getAppraised_value(), lnc.getExtensions_taken(),lnc.getKeyword_exact_local_search_volume(),lnc.getKeyword_exact_cpc(),lnc.getWhois_registrar(),lnc.getWayback_age(),lnc.getEnd_user_buyers());

            }
            // System.out.println(s);
            telegram.sendAlert(chat_id,"<pre>"+s+"</pre>","HTML");
            l=l-d;
            s="";
        }

    }

    void sendWonList(int h,Long chat_id)
    {
        Date date= new Date();
        date.setHours(date.getHours()-h);
        String d1= ft1.format(date);
        //int l= repo.findLargestResultLength(d1,d2);
        List<DBdetails> list= repo.getWonList(d1);
        if(list==null||list.isEmpty())
        {
            telegram.sendAlert(chat_id,"No Winnings in Last "+h+" hours.");
            return;
        }
        if(list.size()!=0) {
            int n = 0;
            for (int i = 0; i < list.size(); i++) {
                n = Math.max(n, list.get(i).getDomain().length());
            }
//        Plat     High Bid  Our Max Bid  separators spaces
            int t = 9 + n  + 8 + 11 + 3 + 6;
            int d = 4096 / t;
            d = d - 6;
            String s = String.format("| %-" + (t - 4) + "s |%n", "Winnings of Last "+h+" hours:");
            for (int i = 0; i < t; i++) {
                s = s + "-";
            }
            s = s + "\n\n";
            s = s + String.format("%-9s | %-" + n + "s | %-8s | %11s%n", "Platform", "Domain", "High Bid", "Our Max Bid");
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
                        s = s + String.format("%-9s | %-" + n + "s | %-8s | %11s%n", lnc.getPlatform(), lnc.getDomain(), lnc.getCurrbid(), lnc.getBidAmount());
                    }
                    catch(Exception e)
                    {
                        logger.info(e.getMessage());
                    }

                }
                // System.out.println(s);
                telegram.sendAlert(chat_id, "<pre>" + s + "</pre>", "HTML");
                l = l - d;
                s = "";
            }
        }
    }

    void sendLostList(int h,Long chat_id)
    {
        Date date= new Date();
        date.setHours(date.getHours()-h);
        String d1= ft1.format(date);
        //int l= repo.findLargestResultLength(d1,d2);
        List<DBdetails> list= repo.getLostList(d1);
        if(list==null||list.isEmpty())
        {
            telegram.sendAlert(chat_id,"No Losings in Last "+h+" hours.");
            return;
        }
        if(list.size()!=0) {
            int n = 0;
            for (int i = 0; i < list.size(); i++) {
                n = Math.max(n, list.get(i).getDomain().length());
            }
//        Plat     High Bid  Our Max Bid  separators spaces
            int t = 9 + n  + 8 + 11 + 3 + 6;
            int d = 4096 / t;
            d = d - 6;
            String s = String.format("| %-" + (t - 4) + "s |%n", "Losings of Last "+h+" hours:");
            for (int i = 0; i < t; i++) {
                s = s + "-";
            }
            s = s + "\n\n";
            s = s + String.format("%-9s | %-" + n + "s | %-8s | %11s%n", "Platform", "Domain", "High Bid", "Our Max Bid");
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
                        s = s + String.format("%-9s | %-" + n + "s | %-8s | %11s%n", lnc.getPlatform(), lnc.getDomain(), lnc.getCurrbid(), lnc.getBidAmount());
                    }
                    catch(Exception e)
                    {
                        logger.info(e.getMessage());
                    }

                }
                // System.out.println(s);
                telegram.sendAlert(chat_id, "<pre>" + s + "</pre>", "HTML");
                l = l - d;
                s = "";
            }
        }
    }

    @GetMapping("/sendkeyboard")
    Message sendkeyboard()
    {
        List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("One","1"));
        row.add(new InlineKeyboardButton("two", "2"));
        row.add(new InlineKeyboardButton("three", "3"));

        row1.add(new InlineKeyboardButton("custom", "custom"));

        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        String text = "Updated\uD83D\uDFE2\n\nDynadot Auction LOSING/OUTBID!"+filler +"\nabc.com\u0009111" + "\n \nTime Left: 12h, 5m"  + "\nCurrent Bid: 122" + "\nMin Next Bid: 137" + "\nOur Max Bid: 123" +  "\n \nAge: 23"  + " \nEST: 1234";

        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
       return telegram.sendKeyboard(new SendMessage(-834797664l,text,inlineKeyboardMarkup));
    }
}
