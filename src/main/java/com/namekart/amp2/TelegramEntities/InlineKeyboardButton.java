package com.namekart.amp2.TelegramEntities;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InlineKeyboardButton {
    String text;
    String callback_data;
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;

    public InlineKeyboardButton(String text, String callback_data) {
        this.text = text;
        this.callback_data = callback_data;
       // this.url="";
    }

    public InlineKeyboardButton(String text) {
        this.text = text;
        //this.url="";

    }
    public InlineKeyboardButton() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCallback_data() {
        return callback_data;
    }

    public void setCallback_data(String callback_data) {
        this.callback_data = callback_data;
    }
}
