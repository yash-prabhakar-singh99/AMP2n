package com.namekart.amp2.Controller;
//import com.orgyflame.springtelegrambotapi.bot.mapping.BotController;


import com.namekart.amp2.Entity.DBdetails;
import com.namekart.amp2.Feign.Namecheapfeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.NamecheapEntity.Bidnc;
import com.namekart.amp2.NamecheapEntity.ResponsePlaceBidNc;
import com.namekart.amp2.Repository.MyRepo;
import com.namekart.amp2.TelegramEntities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    DropCatchController dropCatchController;
    String bearernc = "Bearer ef7b03f63d8a94e2f083b991a74dd5852s5DuDtyOc9Ft1QZ5u0plxLpA0vlYdHFxEccAez6lh/wUyQNkOTCfqcOgrYMcvG4";

    @Autowired
    Namecheapfeign namecheapfeign;

    @Autowired
    MyRepo repo;

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
            String data= callbackQuery.getData();
            Long message_thread_id= callbackQuery.getMessage().getMessage_thread_id();
                // telegram.answerCallback(update.getCallback_query().getId(),"Pressed "+update.getCallback_query().getData(),true);
                         //-1001653862522L
            //if(chat_id == -1001653862522L|| chat_id== -1001814695777L|| chat_id==-856441586L||chat_id==-1001706842871L||chat_id==-1001833712484L)
                if(chat_id==-1001763199668l||chat_id==-1001887754426l)
                {
                 /*   String[] arr = data.split(" ");
                    String p=arr[0];
                    String plat=arr[1];
                    String id = arr[2];
                    String domain = arr[3];
                    if(p.equals("b")) {
                        if(arr.length==5)
                        {
                            //namecheapController.schedulesingle(domain,ncid,p);
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                            List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();

                            row.add(new InlineKeyboardButton("+10", data + " 10"));
                            row.add(new InlineKeyboardButton("+25", data + " 25"));
                            row.add(new InlineKeyboardButton("+50", data + " 50"));
                            row1.add(new InlineKeyboardButton("Custom", data.replaceFirst("b", "c")));
                            row2.add(new InlineKeyboardButton("Watch", data.replaceFirst("b", "w")));
                            row2.add(new InlineKeyboardButton("Track", data.replaceFirst("b", "t")));

                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            rows.add(row);
                            rows.add(row1);
                            rows.add(row2);
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                            EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), inlineKeyboardMarkup);
                            telegram.editMessage(edit);
                        }
                        //Bid buttons pressed:
                        else if(arr.length==6)
                        {
                            if(chat_id == -1001653862522L)
                            {
                                Float bid=Float.valueOf(arr[4])+Float.valueOf(arr[5]);
                                Float f = namecheapController.schedulesingleoutbid(domain, id, bid);
                                if(f==0)
                                    telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " Scheduled for " + domain, false);
                                else if(f==1)
                                    telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                                else
                                    telegram.answerCallback(callbackQuery.getId(), "Namecheap: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);
                            }
                            else if(chat_id==-1001814695777L)
                            {
                                String bid= String.valueOf(Math.ceil(Float.valueOf(arr[4])+Float.valueOf(arr[5])));
                                Float f= dynadotController.mainmainsingle(domain, Long.valueOf(id),bid);
                                if(f==0)
                                    telegram.answerCallback(callbackQuery.getId(), "Dynadot: Max bid of " + bid + " Scheduled for " + domain, false);
                                else if(f==1)
                                    telegram.answerCallback(callbackQuery.getId(), "Dynadot: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                                else
                                    telegram.answerCallback(callbackQuery.getId(), "Dynadot: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);
                            }

                            else if(chat_id==-856441586L)
                            {
                                Float bid=Float.valueOf(arr[4])+Float.valueOf(arr[5]);
                                float f= namesiloController.scheduleSingleoutbid(Long.valueOf(id),domain,bid);
                                if(f==0)
                                    telegram.answerCallback(callbackQuery.getId(), "Namesilo: Max bid of " + bid + " Scheduled for " + domain, false);
                                else if(f==1)
                                    telegram.answerCallback(callbackQuery.getId(), "Namesilo: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                                else
                                    telegram.answerCallback(callbackQuery.getId(), "Namesilo: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);
                            }
                            else if(chat_id==-1001706842871L)
                            {
                                Long bid=Long.valueOf(arr[4])+Long.valueOf(arr[5]);
                                Long f= dropCatchController.scheduleSingle(domain,Long.valueOf(id),bid);
                                if(f==0)
                                    telegram.answerCallback(callbackQuery.getId(), "Dropcatch: Max bid of " + bid + " Scheduled for " + domain, false);
                                else if(f==1)
                                    telegram.answerCallback(callbackQuery.getId(), "Dropcatch: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                                else
                                    telegram.answerCallback(callbackQuery.getId(), "Dropcatch: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);
                            }
                            else if(chat_id==-1001833712484L)
                            {
                                String bid= String.valueOf(Integer.valueOf(arr[4])+Integer.valueOf(arr[5]));
                                Float f= goDaddyController.scheduleSingleOutbid(domain,id,bid);
                                if(f==0)
                                    telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Max bid of " + bid + " Scheduled for " + domain, false);
                                else if(f==1)
                                    telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                                else
                                    telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);

                            }
                        }
                        //telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " +p+ " Scheduled for " + domain, false);
                    }
                    else if(p.equals("c"))
                    {
                        //Float price= Float.valueOf(arr[]);
                       // Float bid=0f;
                        List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row2= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row3= new ArrayList<InlineKeyboardButton>();

                        data= data.replaceFirst("c","b");
                        row.add(new InlineKeyboardButton("+10",data+" 10"));
                        row.add(new InlineKeyboardButton("+25", data+" 25"));
                        row.add(new InlineKeyboardButton("+50", data+" 50"));
                        row1.add(new InlineKeyboardButton("+75",data+" 75"));
                        row1.add(new InlineKeyboardButton("+100", data+" 100"));
                        row1.add(new InlineKeyboardButton("+150", data+" 150"));
                        row2.add(new InlineKeyboardButton("+200",data+" 200"));
                        row2.add(new InlineKeyboardButton("+300", data+" 300"));
                        row2.add(new InlineKeyboardButton("+500", data+" 500"));
                        row3.add(new InlineKeyboardButton("Watch", data.replaceFirst("b", "w")));
                        row3.add(new InlineKeyboardButton("Track", data.replaceFirst("b", "t")));

                        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                        rows.add(row);
                        rows.add(row1);
                        rows.add(row2);
                        rows.add(row3);
                        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                        EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), inlineKeyboardMarkup);
                        telegram.editMessage(edit);
                    }
                    else if(p.equals("w"))
                    {
                        telegram.answerCallback(callbackQuery.getId(), domain+" added to watchlist", false);

                        //watchlist logic
                        if(chat_id == -1001653862522L)
                        {
                            namecheapController.watchlistLive(domain,id,false);

                        }
                        else if(chat_id==-1001814695777L)
                        {
                            dynadotController.watchListLive(domain,Long.valueOf(id),false);
                        }
                        else if(chat_id==-856441586L)
                        {

                            namesiloController.watchlistLive(Long.valueOf(id),domain,false);
                        }
                        else if(chat_id==-1001706842871L)
                        {
                            dropCatchController.watchlistLive(domain,Long.valueOf(id),false);
                        }
                        else if(chat_id==-1001833712484L)
                        {
                            goDaddyController.watchlistLive(domain,id,false);
                        }

                    }
                    else if(p.equals("t"))
                    {
                        telegram.answerCallback(callbackQuery.getId(), "Tracking "+domain, false);
                        //watchlist logic
                        if(chat_id == -1001653862522L)
                        {
                            namecheapController.watchlistLive(domain,id,true);

                        }
                        else if(chat_id==-1001814695777L)
                        {
                            dynadotController.watchListLive(domain,Long.valueOf(id),true);
                        }
                        else if(chat_id==-856441586L)
                        {

                            namesiloController.watchlistLive(Long.valueOf(id),domain,true);
                        }
                        else if(chat_id==-1001706842871L)
                        {
                            dropCatchController.watchlistLive(domain,Long.valueOf(id),true);
                        }
                        else if(chat_id==-1001833712484L)
                        {
                            goDaddyController.watchlistLive(domain,id,true);
                        }


                    }*/


                    String[] arr = data.split(" ");
                    String p=arr[0];
                    String plat=arr[1];
                    String id = arr[2];
                    String domain = arr[3];
                    if(p.equals("b"))
                    {
                        if(arr.length==5)
                        {
                            //namecheapController.schedulesingle(domain,ncid,p);
                            List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                            List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                            List<InlineKeyboardButton> row2 = new ArrayList<InlineKeyboardButton>();

                            row.add(new InlineKeyboardButton("+10", data + " 10"));
                            row.add(new InlineKeyboardButton("+25", data + " 25"));
                            row.add(new InlineKeyboardButton("+50", data + " 50"));
                            row1.add(new InlineKeyboardButton("Custom", data.replaceFirst("b", "c")));
                            row2.add(new InlineKeyboardButton("Watch", data.replaceFirst("b", "w")));
                            row2.add(new InlineKeyboardButton("Track", data.replaceFirst("b", "t")));

                            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                            rows.add(row);
                            rows.add(row1);
                            rows.add(row2);
                            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                            EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), inlineKeyboardMarkup);
                            telegram.editMessage(edit);
                        }
                        //Bid buttons pressed:
                        else if(arr.length==6)
                        {
                            if(plat.equals("nc"))
                            {
                                Float bid = Float.valueOf(arr[4]) + Float.valueOf(arr[5]);
                               Float f= namecheapController.schedulesingleoutbid(domain, id, bid);
                               if(f==0)
                                   telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " Scheduled for " + domain, false);
                               else if(f==1)
                                   telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                               else
                                   telegram.answerCallback(callbackQuery.getId(), "Namecheap: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);

                            }
                            else if(plat.equals("dd"))
                            {
                                String bid= String.valueOf(Math.ceil(Float.valueOf(arr[4])+Float.valueOf(arr[5])));
                              Float f= message_thread_id==1014?dynadotController.mainmainsingle(domain, Long.valueOf(id),bid):dynadotController.mainmainsingleoutbid(domain, Long.valueOf(id),bid);
                              if(f==0)
                                telegram.answerCallback(callbackQuery.getId(), "Dynadot: Max bid of " + bid + " Scheduled for " + domain, false);
                              else if(f==1)
                                  telegram.answerCallback(callbackQuery.getId(), "Dynadot: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                              else
                                  telegram.answerCallback(callbackQuery.getId(), "Dynadot: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);

                            }
                            else if(plat.equals("ns"))
                            {
                                Float bid=Float.valueOf(arr[4])+Float.valueOf(arr[5]);
                               float f= namesiloController.scheduleSingleoutbid(Long.valueOf(id),domain,bid);
                                if(f==0)
                                telegram.answerCallback(callbackQuery.getId(), "Namesilo: Max bid of " + bid + " Scheduled for " + domain, false);
                                else if(f==1)
                                    telegram.answerCallback(callbackQuery.getId(), "Namesilo: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                                else
                                    telegram.answerCallback(callbackQuery.getId(), "Namesilo: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);

                            }
                            else if(plat.equals("gd"))
                            {
                                String bid= String.valueOf(Integer.valueOf(arr[4])+Integer.valueOf(arr[5]));
                               Float f= goDaddyController.scheduleSingleOutbid(domain,id,bid);
                               if(f==0)
                                telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Max bid of " + bid + " Scheduled for " + domain, false);
                               else if(f==1)
                                   telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Max bid of " + bid + " NOT SCHEDULED for " + domain, false);
                               else
                                   telegram.answerCallback(callbackQuery.getId(), "GoDaddy: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+f, false);

                            }
                            if(plat.equals("dc"))
                            {
                                Long bid= Long.valueOf(arr[4])+Long.valueOf(arr[5]);
                              long l=  dropCatchController.scheduleSingle(domain,Long.valueOf(id),bid);
                              if(l==0)
                              telegram.answerCallback(callbackQuery.getId(), "Dropcatch: Max bid of " + bid + " Scheduled for " + domain, false);
                              else
                                  telegram.answerCallback(callbackQuery.getId(), "Dropcatch: Bid NOT SCHEDULED for "+domain+" as bid value is lower than accepted bid of "+l, false);

                            }

                        }
                        //telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " +p+ " Scheduled for " + domain, false);
                    }
                    else if(p.equals("c"))
                    {
                        //Float price= Float.valueOf(arr[]);
                        // Float bid=0f;
                        List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row2= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row3= new ArrayList<InlineKeyboardButton>();

                        data= data.replaceFirst("c","b");
                        row.add(new InlineKeyboardButton("+10",data+" 10"));
                        row.add(new InlineKeyboardButton("+25", data+" 25"));
                        row.add(new InlineKeyboardButton("+50", data+" 50"));
                        row1.add(new InlineKeyboardButton("+75",data+" 75"));
                        row1.add(new InlineKeyboardButton("+100", data+" 100"));
                        row1.add(new InlineKeyboardButton("+150", data+" 150"));
                        row2.add(new InlineKeyboardButton("+200",data+" 200"));
                        row2.add(new InlineKeyboardButton("+300", data+" 300"));
                        row2.add(new InlineKeyboardButton("+500", data+" 500"));
                        row3.add(new InlineKeyboardButton("Watch", data.replaceFirst("b", "w")));
                        row3.add(new InlineKeyboardButton("Track", data.replaceFirst("b", "t")));

                        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                        rows.add(row);
                        rows.add(row1);
                        rows.add(row2);
                        rows.add(row3);
                        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                        EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), inlineKeyboardMarkup);
                        telegram.editMessage(edit);
                    }
                    else if(p.equals("w"))
                    {
                        telegram.answerCallback(callbackQuery.getId(), domain+" added to watchlist", false);

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
                }
            return callbackQuery;
        }
        else return update.getMessage();
    }

    @GetMapping("/sendkeyboard")
    Message sendkeyboard()
    {
        List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
        List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("One","1"));
        row.add(new InlineKeyboardButton("two", "2"));
        row1.add(new InlineKeyboardButton("custom", "custom"));

        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
       return telegram.sendKeyboard(new SendMessage(-1001763199668l,1004l,"Your keyboard",inlineKeyboardMarkup));
    }
}
