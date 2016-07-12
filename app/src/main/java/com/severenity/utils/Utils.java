package com.severenity.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.severenity.App;
import com.severenity.R;
import com.severenity.entity.User;
import com.severenity.utils.common.Constants;
import com.severenity.view.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;

/**
 * Created by Novosad on 8/29/2015.
 */
public class Utils {
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Returns {@link LatLng} representation from the given {@link Location} object.
     *
     * @param location - location object
     * @return latLng representation of the given location.
     */
    public static @Nullable LatLng latLngFromLocation(Location location) {
        if (location == null) {
            return null;
        }
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    /**
     * Retrieves IMEI of the device.
     *
     * @param context context for system service.
     * @return device's IMEI
     */
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    /**
     * Creates user in db
     *
     * @param response - JSON response to create user from.
     */
    public static User createUserFromJSON(JSONObject response) {
        User user = new User();
        try {
            user.setCreatedDate(response.getString("createdDate"));
            user.setId(response.getString("userId"));
            user.setName(response.getString("name"));
            user.setEmail(response.getString("email"));

            JSONObject profileObject = response.getJSONObject("profile");
            user.setDistance(profileObject.getInt("distance"));
            user.setExperience(profileObject.getInt("experience"));
            user.setImmunity(profileObject.getInt("immunity"));
            user.setEnergy(profileObject.getInt("energy"));
            user.setCredits(profileObject.getInt("credits"));
            user.setImplantHP(profileObject.getInt("implantHP"));
            user.setMaxImplantHP(profileObject.getInt("maxImplantHP"));
            user.setLevel(profileObject.getInt("level"));
            user.setMaxImmunity(profileObject.getInt("maxImmunity"));
            user.setMaxEnergy(profileObject.getInt("maxEnergy"));
            user.setViewRadius(profileObject.getInt("viewRadius") * 1.0);
            user.setActionRadius(profileObject.getInt("actionRadius") * 1.0);

            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Combines device model and manufacturer into device name.
     *
     * E.g. LGE Nexus 5.
     * @return combined device name.
     */
    public static String getDeviceName() {
        String deviceName = Build.MODEL;
        String deviceMan = Build.MANUFACTURER;
        return deviceMan + " " + deviceName;
    }

    /**
     * Shows alert dialog with given message on given context.
     *
     * @param message - message to be displayed.
     * @param context - context where to show message.
     */
    public static void showAlertDialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.severenity_notification));
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * Shows alert dialog with given message on given context.
     *
     * @param message - message to be displayed.
     * @param context - context where to show message.
     */
    public static void showPromptDialog(String message, Context context, final PromptCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.severenity_notification));
        builder.setMessage(message);
        builder.setPositiveButton(context.getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onAccept();
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.decline), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callback.onDecline();
            }
        });

        builder.show();
    }

    public static Bitmap getScaledMarker(int resourceId, Context context) {
        int height = 192;
        int width = 192;
        BitmapDrawable bitmapDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(resourceId, context.getTheme());
        } else {
            bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(resourceId);
        }
        return Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), width, height, false);
    }

    public interface PromptCallback {
        void onAccept();
        void onDecline();
    }

    /**
     * Sends notification to the {@link MainActivity} so notification can be displayed inside
     * of the activity.
     *
     * @param message - text of the message.
     */
    public static void sendNotification(String message, Context context, Intent intent, int notificationId) {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getResources().getString(R.string.severenity_notification))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    //1 minute = 60 seconds
    //1 hour = 60 x 60 = 3600
    //1 day = 3600 x 24 = 86400
    public static String dateDifference(Date startDate, Date endDate) {

        // Milliseconds
        long difference = endDate.getTime() - startDate.getTime();

        Log.d(Constants.TAG, "startDate : " + startDate);
        Log.d(Constants.TAG, "endDate : " + endDate);
        Log.d(Constants.TAG, "difference : " + startDate);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = difference / daysInMilli;
        difference = difference % daysInMilli;

        long elapsedHours = difference / hoursInMilli;
        difference = difference % hoursInMilli;

        long elapsedMinutes = difference / minutesInMilli;
        difference = difference % minutesInMilli;

        long elapsedSeconds = difference / secondsInMilli;

        return String.format(Locale.US, "%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
    }

    /**
     * Returns new LatLong from current by adding x meters
     *
     * @param currentPos - start position to move from
     * @param meters     - distance to new position in meters.
     * @param direction  - new position direction: South, North, East, West.
     */
    public static LatLng getPositionInMeter(LatLng currentPos, double meters, int direction) {

        double verticalShift   = 360 * ( meters / 1000) / Constants.EARTH_CIRCUMFERENCE;
        double horizontalShift = verticalShift / Math.cos(currentPos.latitude * Math.PI/180);

        switch (direction) {

            case Constants.SOUTH_DIRECTION: {
                double shift = currentPos.latitude - verticalShift;
                return new LatLng(shift, currentPos.longitude);
            }

            case Constants.NORTH_DIRECTION: {
                double shift = currentPos.latitude + verticalShift;
                return new LatLng(shift, currentPos.longitude);
            }

            case Constants.WEST_DIRECTION: {
                double shift = currentPos.longitude - horizontalShift;
                return new LatLng(currentPos.latitude, shift);
            }

            case Constants.EAST_DIRECTION: {
                double shift = currentPos.longitude + horizontalShift;
                return new LatLng(currentPos.latitude, shift);
            }

            case Constants.WS_DIRECTION: {
                double shiftV = currentPos.latitude - verticalShift;
                double shiftH = currentPos.longitude - horizontalShift;

                return new LatLng(shiftV, shiftH);
            }

            case Constants.WN_DIRECTION: {
                double shiftV = currentPos.latitude + verticalShift;
                double shiftH = currentPos.longitude - horizontalShift;

                return new LatLng(shiftV, shiftH);
            }

            case Constants.EN_DIRECTION: {
                double shiftV = currentPos.latitude  + verticalShift;
                double shiftH = currentPos.longitude + horizontalShift;

                return new LatLng(shiftV, shiftH);
            }

            case Constants.ES_DIRECTION: {
                double shiftV = currentPos.latitude  - verticalShift;
                double shiftH = currentPos.longitude + horizontalShift;

                return new LatLng(shiftV, shiftH);
            }
        }

        return null;
    }


    /**
     * calculate the distance between two places
     *
     * @param startPos - the position of start point
     * @param endPos   - the position of destination point
     * @return distance in meters
     */
    public static float distanceBetweenLocations(LatLng startPos, LatLng endPos) {

        Location locationStart = new Location("Start location");
        locationStart.setLatitude(startPos.latitude);
        locationStart.setLongitude(startPos.longitude);

        Location locationDest = new Location("Dest location");
        locationDest.setLatitude(endPos.latitude);
        locationDest.setLongitude(endPos.longitude);

        return locationStart.distanceTo(locationDest);
    }

    /**
     * Converts drawable to bitmap.
     *
     * @param drawable {@link Drawable} to convert
     * @return new {@link Bitmap} object
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
