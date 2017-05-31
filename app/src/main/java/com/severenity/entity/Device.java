package com.severenity.entity;

import io.realm.RealmObject;

/**
 * Created by Novosad on 5/5/16.
 */
public class Device extends RealmObject {
    private String deviceName;
    private String deviceId;
    private String registrationId;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
