package com.nosad.sample.engine.managers.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.nosad.sample.App;
import com.nosad.sample.utils.common.Constants;

/**
 * Created by Novosad on 3/30/16.
 */
public class StepManager {
    private Context context;

    public StepManager(Context context) {
        this.context = context;

        if (!isKitkatWithStepSensor()) {
            Log.e(Constants.TAG, "Cannot register step sensor to count steps made.");
        } else {
            registerEventListener();
        }
    }

    /**
     * Returns true if this device is supported. It needs to be running Android KitKat (4.4) or
     * higher and has a step counter and step detector sensor.
     * This check is useful when an app provides an alternative implementation or different
     * functionality if the step sensors are not available or this code runs on a platform version
     * below Android KitKat. If this functionality is required, then the minSDK parameter should
     * be specified appropriately in the AndroidManifest.
     *
     * @return True iff the device can run this sample
     */
    private boolean isKitkatWithStepSensor() {
        // Require at least Android KitKat
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        // Check that the device supports the step counter and detector sensors
        PackageManager packageManager = context.getPackageManager();
        return currentApiVersion >= android.os.Build.VERSION_CODES.KITKAT
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
    }

    /**
     * Listener that handles step sensor events for step detector and step counter sensors.
     */
    private final SensorEventListener mListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // A step detector event is received for each step.
            // This means we need to count steps ourselves
            Log.i(Constants.TAG, "New step detected by STEP_DETECTOR sensor.");

            Intent stepsCount = new Intent(Constants.INTENT_FILTER_STEPS);
            App.getLocalBroadcastManager().sendBroadcast(stepsCount);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * Register a {@link android.hardware.SensorEventListener} for the step detector sensor.
     */
    private void registerEventListener() {
        // Get the default sensor for the sensor type from the SenorManager
        SensorManager sensorManager =
                (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        final boolean registered =
            sensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        if (registered) {
            Log.i(Constants.TAG, "Sensor listener successfully registered.");
        } else {
            Log.i(Constants.TAG, "Sensor listener was not registered.");
        }
    }

    /**
     * Unregisters the sensor listener if it is registered.
     */
    private void unregisterListeners() {
        SensorManager sensorManager =
                (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
        sensorManager.unregisterListener(mListener);
        Log.i(Constants.TAG, "Sensor listener unregistered.");
    }

    public void stop() {
        unregisterListeners();
    }
}
