package com.nosad.sample.engine.managers.data;

import android.content.Context;

import com.nosad.sample.entity.Message;

/**
 * Created by Andriy on 4/27/2016.
 */
public class MessageManager extends DataManager {

    public MessageManager(Context context) {
        super(context);
    }

    public boolean StoreMessage(Message msg) {

        return false;
    };
}
