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
           // telegram.answerCallback(update.getCallback_query().getId(),"Pressed "+update.getCallback_query().getData(),true);
            if(chat_id==-1001653862522L)
            {
                String data= callbackQuery.getData();
                String[] arr= data.split(" ");
                Bidnc bidnc= new Bidnc(Float.valueOf(arr[0]));
                String ncid= arr[1];
                String domain= arr[2];
               ResponsePlaceBidNc pb= namecheapfeign.placeBidnc(bearernc,ncid,bidnc);
               if(pb.getStatus().equals("processed"))
               {
                   telegram.answerCallback(callbackQuery.getId(),"Namecheap: Bid of "+bidnc.getMaxAmount()+" Placed on "+domain,false);
               }
               else {
                   telegram.answerCallback(callbackQuery.getId(),"Namecheap: Bid of "+bidnc.getMaxAmount()+" Not Placed on "+domain,false);
               }
            }
            return callbackQuery;}
        else return update.getMessage();
    }

    @GetMapping("/sendkeyboard")
    Message sendkeyboard()
    {
        List<InlineKeyboardButton> row= new ArrayList<InlineKeyboardButton>();
        row.add(new InlineKeyboardButton("One","1"));
        row.add(new InlineKeyboardButton("two", "2"));
        List<List<InlineKeyboardButton>> rows= new ArrayList<>();
        rows.add(row);
        InlineKeyboardMarkup inlineKeyboardMarkup= new InlineKeyboardMarkup(rows);
       return telegram.sendKeyboard(new SendMessage(-834797664L,"Your keyboard",inlineKeyboardMarkup));
    }
}
