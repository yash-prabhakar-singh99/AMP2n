package com.namekart.amp2.TelegramEntities;

public class ReplyKeyboardButton {
    String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ReplyKeyboardButton(String text) {
        this.text = text;
    }

    public ReplyKeyboardButton() {
    }
}
