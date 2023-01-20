package com.namekart.amp2.Controller;
//import com.orgyflame.springtelegrambotapi.bot.mapping.BotController;


import com.namekart.amp2.Feign.Namecheapfeign;
import com.namekart.amp2.Feign.Telegram;
import com.namekart.amp2.NamecheapEntity.Bidnc;
import com.namekart.amp2.NamecheapEntity.ResponsePlaceBidNc;
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
    Telegram telegram;
    Logger logger = Logger.getLogger("Telegram");

    @PostMapping("/callback/amp")
    Object callbackamp(@RequestBody Update update)
    {
        logger.info(""+update.getUpdate_id());
        if(update.isCallback())
        {logger.info(update.getCallback_query().getData());
            CallbackQuery callbackQuery=update.getCallback_query();
            Long chat_id= update.getCallback_query().getMessage().getChat().getId();
            String data= callbackQuery.getData();

                // telegram.answerCallback(update.getCallback_query().getId(),"Pressed "+update.getCallback_query().getData(),true);
                         //-1001653862522L
            if (chat_id == -1001653862522L) {

                    String[] arr = data.split(" ");
                    String p=arr[0];
                    String ncid = arr[1];
                    String domain = arr[2];
                    if(!p.equals("c")) {

                        //namecheapController.schedulesingle(domain,ncid,p);
                        telegram.answerCallback(callbackQuery.getId(), "Namecheap: Max bid of " +p+ " Scheduled for " + domain, false);
                    }
                    else
                    {
                        Float price= Float.valueOf(arr[3]);
                        Float bid=0f;
                        List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row1= new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row2= new ArrayList<InlineKeyboardButton>();

                        bid=price+10;
                        row.add(new InlineKeyboardButton("+10",bid+" "+ncid+" "+domain));
                        bid=price+25;
                        row.add(new InlineKeyboardButton("+25", bid+" "+ncid+" "+domain));
                        bid=price+50;
                        row.add(new InlineKeyboardButton("+50", bid+" "+ncid+" "+domain));
                        bid=price+75;
                        row1.add(new InlineKeyboardButton("+75",bid+" "+ncid+" "+domain));
                        bid=price+100;
                        row1.add(new InlineKeyboardButton("+100", bid+" "+ncid+" "+domain));
                        bid=price+150;
                        row1.add(new InlineKeyboardButton("+150", bid+" "+ncid+" "+domain));
                        bid=price+200;
                        row2.add(new InlineKeyboardButton("+200",bid+" "+ncid+" "+domain));
                        bid=price+300;
                        row2.add(new InlineKeyboardButton("+300", bid+" "+ncid+" "+domain));
                        bid=price+500;
                        row2.add(new InlineKeyboardButton("+500", bid+" "+ncid+" "+domain));
                        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
                        rows.add(row);
                        rows.add(row1);
                        rows.add(row2);
                        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
                        EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), inlineKeyboardMarkup);
                        telegram.editMessage(edit);
                    }
                } else if (chat_id ==-834797664) {
                    if (data.equals("custom")) {
                        List<InlineKeyboardButton> row = new ArrayList<InlineKeyboardButton>();
                        List<InlineKeyboardButton> row1 = new ArrayList<InlineKeyboardButton>();
                        row.add(new InlineKeyboardButton("One", "1"));
                        row.add(new InlineKeyboardButton("two", "2"));
                        row1.add(new InlineKeyboardButton("three", "3"));
                        row1.add(new InlineKeyboardButton("four", "4"));
                        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                        rows.add(row);
                        rows.add(row1);
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);
                        EditMessageReplyMarkup edit = new EditMessageReplyMarkup(chat_id, update.getCallback_query().getMessage().getMessage_id(), inlineKeyboardMarkup);
                        telegram.editMessage(edit);
                    }
                }
            return callbackQuery;}
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
       return telegram.sendKeyboard(new SendMessage(-834797664L,"Your keyboard",inlineKeyboardMarkup));
    }
}
