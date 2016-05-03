package com.nosad.sample.entity;

/**
 * Created by Andriy on 4/27/2016.
 */
public class Message {

    private int mMessageID = -1;
    private String mUserId;
    private String mMessage;
    private String mTimestamp;
    private String mUserName;

    /*timestamp should be in format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"*/
    public Message(){};

    public int getMessageID() {return this.mMessageID; };
    public void setMessageID(int messageID) {this.mMessageID = messageID; };

    public String getMessage() {return this.mMessage; };
    public void   setMessage(String message) {this.mMessage = message; };

    public String getUserID() {return this.mUserId; };
    public void   setUserID(String userID) {this.mUserId = userID;};

    public String getTimestamp() {return this.mTimestamp; };
    public void   setTimestamp(String timestamp) {this.mTimestamp = timestamp;};

    public String getUserName() {return this.mUserName; };
    public void   setUserName(String username) {this.mUserName = username;};

    public int getMessageHASH() {
        String str = mUserId + mMessage + mTimestamp + mUserName;
        return str.hashCode();
    }

}
