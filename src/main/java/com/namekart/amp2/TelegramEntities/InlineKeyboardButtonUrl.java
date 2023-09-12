package com.namekart.amp2.TelegramEntities;

public class InlineKeyboardButtonUrl {
    String text;
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;

    public InlineKeyboardButtonUrl(String text, String url) {
        this.text = text;
        this.url = url;
    }

    public InlineKeyboardButtonUrl(String text) {
        this.text = text;
    }
    public InlineKeyboardButtonUrl() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
