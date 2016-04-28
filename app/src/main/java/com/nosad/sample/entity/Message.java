package com.nosad.sample.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Andriy on 4/27/2016.
 */
public class Message {
    private String _userId;
    private String _message;
    private String _timestamp;
    private String _userName;

    /*timestamp should be in format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"*/
    public Message(){};

    public String getMessage() {return this._message; };
    public void   setMessage(String message) {this._message = message; };

    public String getUserID() {return this._userId; };
    public void   setUserID(String userID) {this._userId = userID;};

    public String getTimestamp() {return this._timestamp; };
    public void   setTimestamp(String timestamp) {this._timestamp = timestamp;};

    public String getUserName() {return this._userName; };
    public void   setUserName(String username) {this._userName = username;};

}
