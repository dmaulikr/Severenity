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
    //private Date   _timestamp; /*"yyyy-MM-dd'T'HH:mm:ss.SSSZ"*/

    /*timestamp should be in format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"*/
    public Message(String userId, String message/*, String timestamp*/){
        this._userId  = userId;
        this._message = message;
/*
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        try {
            _timestamp = formatter.parse(timestamp);
        } catch (ParseException e) {
            _timestamp = null;
        }
*/
    };

    public String getMessage() {return this._message; };
    public void   steMessage(String message) {this._message = message; };

    public String getUserID() {return this._userId; };
    public void   setUserID(String userID) {this._userId = userID;};

}
