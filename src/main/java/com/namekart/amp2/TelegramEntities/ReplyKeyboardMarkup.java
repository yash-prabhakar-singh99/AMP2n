package com.namekart.amp2.TelegramEntities;

import java.util.List;

public class ReplyKeyboardMarkup {
    List<List<ReplyKeyboardButton>> keyboard;

    Boolean is_persistent;

    public ReplyKeyboardMarkup(List<List<ReplyKeyboardButton>> keyboard, Boolean is_persistent, Boolean resize_keyboard) {
        this.keyboard = keyboard;
        this.is_persistent = is_persistent;
        this.resize_keyboard = resize_keyboard;
    }

    public Boolean getResize_keyboard() {
        return resize_keyboard;
    }

    public void setResize_keyboard(Boolean resize_keyboard) {
        this.resize_keyboard = resize_keyboard;
    }

    Boolean resize_keyboard;

    public ReplyKeyboardMarkup(List<List<ReplyKeyboardButton>> keyboard, Boolean is_persistent) {
        this.keyboard = keyboard;
        this.is_persistent = is_persistent;
    }

    public ReplyKeyboardMarkup() {
    }

    public List<List<ReplyKeyboardButton>> getKeyboard() {
        return keyboard;
    }

    public void setKeyboard(List<List<ReplyKeyboardButton>> keyboard) {
        this.keyboard = keyboard;
    }

    public Boolean getIs_persistent() {
        return is_persistent;
    }

    public void setIs_persistent(Boolean is_persistent) {
        this.is_persistent = is_persistent;
    }
}
