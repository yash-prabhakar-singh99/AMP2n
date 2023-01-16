package com.namekart.amp2.TelegramEntities;

public class Update {
    Long update_id;
    Message message;

    CallbackQuery callback_query;

    public CallbackQuery getCallback_query() {
        return callback_query;
    }

    public boolean isCallback()
    {
        if (this.callback_query!=null)
            return true;
        else
            return false;
    }

    public void setCallback_query(CallbackQuery callback_query) {
        this.callback_query = callback_query;
    }

    public Update() {
    }

    public Long getUpdate_id() {
        return update_id;
    }

    public void setUpdate_id(Long update_id) {
        this.update_id = update_id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
