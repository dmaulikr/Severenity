package com.severenity.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Andriy on 4/27/2016.
 */
public class Message extends RealmObject {

    @Required
    @PrimaryKey
    private String messageId;

    @Required
    private String senderId;

    @Required
    private String text;

    @Required
    private String timestamp;

    @Required
    private String senderName;

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getText() {return this.text; }

    public void setText(String text) {this.text = text; }

    public String getSenderId() {return this.senderId; }

    public void setSenderId(String senderId) {this.senderId = senderId;}

    public String getTimestamp() {return this.timestamp; }

    /**
     * Timestamp should be in format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     */
    public void   setTimestamp(String timestamp) {this.timestamp = timestamp;}

    public String getSenderName() {return this.senderName; }
    public void setSenderName(String senderName) {this.senderName = senderName;}
}
