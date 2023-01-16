package com.namekart.amp2.TelegramEntities;

import java.util.List;

public class InlineKeyboardMarkup {
    List<List<InlineKeyboardButton>> inline_keyboard;

    public InlineKeyboardMarkup() {
    }

    public InlineKeyboardMarkup(List<List<InlineKeyboardButton>> inline_keyboard) {
        this.inline_keyboard = inline_keyboard;
    }

    public List<List<InlineKeyboardButton>> getInline_keyboard() {
        return inline_keyboard;
    }

    public void setInline_keyboard(List<List<InlineKeyboardButton>> inline_keyboard) {
        this.inline_keyboard = inline_keyboard;
    }
}
