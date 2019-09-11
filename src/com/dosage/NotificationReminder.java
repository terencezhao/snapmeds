package com.dosage;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.snapmeds.Constants;
import com.utilities.Frequency;

public class NotificationReminder extends Reminder {

    private static Integer incrementingId = 0;
    private Integer notificationId;
    private String setId;

    public NotificationReminder( String setId, Frequency frequency, String title, String detail ) {
        super( frequency, title, detail );
        this.setId = setId;
    }

    @Override
    public void setupReminder( Context context ) {
        // increment id to ensure uniqueness
        notificationId = incrementingId++;

        // create intent for notification service
        Intent serviceIntent = new Intent( context, NotificationReminderService.class );
        serviceIntent.putExtra( Constants.DRUG_SET_ID, setId );
        serviceIntent.putExtra( Constants.TITLE, title );
        serviceIntent.putExtra( Constants.DETAIL, detail );
        serviceIntent.putExtra( Constants.ALARM_ID, notificationId );
        long intervalMillis = getIntervalMilliseconds( frequency );

        // create alarm manager to wake up notification service
        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        PendingIntent pendingIntent = PendingIntent.getService( context, notificationId, serviceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT );
        alarmManager.cancel( pendingIntent );

        // schedule a repeating alarm
        for ( long time : frequency.getTimes() ) {
            alarmManager.setRepeating( AlarmManager.RTC_WAKEUP, time, intervalMillis, pendingIntent );
        }

        this.isSetup = true;
    }

    @Override
    public void cancelReminder( Context context ) {
        if ( !this.isSetup ) return;
        // Remove recurring alarm for notification
        Intent serviceIntent = new Intent( context, NotificationReminderService.class );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
        PendingIntent pendingIntent = PendingIntent.getService( context, notificationId, serviceIntent,
                PendingIntent.FLAG_NO_CREATE );
        if ( pendingIntent != null ) alarmManager.cancel( pendingIntent );

        // Remove notification if already in notification bar
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService( Context.NOTIFICATION_SERVICE );
        mNotificationManager.cancel( notificationId );
    }

    private long getIntervalMilliseconds( Frequency frequency ) {
        long millis = 1000;
        switch ( frequency.getUnit() ) {
            case MINUTELY:
                millis *= 60;
                break;
            case HOURLY:
                millis *= 60 * 60;
                break;
            case DAILY:
                millis *= 60 * 60 * 24;
                break;
            case WEEKLY:
                millis *= 60 * 60 * 24 * 7;
                break;
            case MONTHLY:
                millis *= 60 * 60 * 24 * 7 * 30;
                break;
            default:
                break;
        }
        return millis;
    }

    /* ----------- Functions only for Jackson serialization ------------- */

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId( Integer id ) {
        this.notificationId = id;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId( String setId ) {
        this.setId = setId;
    }
    
    public NotificationReminder() {}
}
