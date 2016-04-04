package com.nosad.sample.engine.exceptions;

/**
 * Created by Novosad on 4/4/16.
 */
public class NotAuthenticatedException extends Exception {
    public NotAuthenticatedException(String detailMessage) {
        super(detailMessage);
    }
}
