package com.severenity.entity;

/**
 * Created by Andriy on 4/27/2016.
 */
public class Message {

    private int mMessageID = -1;
    private String mUserId;
    private String mMessage;
    private String mTimestamp;
    private String mUsername;

    /*timestamp should be in format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"*/
    public Message() {}

    public int getMessageID() {return this.mMessageID; }
    public void setMessageID(int messageID) {this.mMessageID = messageID; }

    public String getMessage() {return this.mMessage; }
    public void   setMessage(String message) {this.mMessage = message; }

    public String getUserID() {return this.mUserId; }
    public void   setUserID(String userID) {this.mUserId = userID;}

    public String getTimestamp() {return this.mTimestamp; }
    public void   setTimestamp(String timestamp) {this.mTimestamp = timestamp;}

    public String getUsername() {return this.mUsername; }
    public void setUsername(String username) {this.mUsername = username;}

    public int getMessageHASH() {
        String str = mUserId + mMessage + mTimestamp + mUsername;
        return str.hashCode();
    }

}
