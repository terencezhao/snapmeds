package com.dosage;

import com.snapmeds.Constants;
import com.snapmeds.R;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NotificationReminderService extends IntentService {

    public NotificationReminderService() {
        super( "ReminderService" );
    }

    @Override
    protected void onHandleIntent( Intent intent ) {

        // get setId, title and detail from intent
        String setId = intent.getStringExtra( Constants.DRUG_SET_ID );
        String title = intent.getStringExtra( Constants.TITLE );
        String detail = intent.getStringExtra( Constants.DETAIL );
        Integer alarmId = intent.getIntExtra( Constants.ALARM_ID, 0 );

        // creates a notification object, with three mandatory fields set, small
        // icon, title, detail
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder( this.getApplicationContext() )
                .setSmallIcon( R.drawable.ic_launcher ).setContentTitle( title ).setContentText( detail );

        // creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent( this, com.snapmeds.PrescriptionDetailActivity.class );
        resultIntent.putExtra( Constants.DRUG_SET_ID, setId );

        // stack builder object contains artificial back stack for activity
        // ensures backward navigation from app to home screen
        TaskStackBuilder stackBuilder = TaskStackBuilder.create( this.getApplicationContext() );

        // adds the back stack for the intent, but not the intent itself
        // (i.e. the parent stack of the Activity)
        stackBuilder.addParentStack( com.snapmeds.PrescriptionDetailActivity.class );

        // adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent( resultIntent );

        // create and set PendingIntent
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( alarmId, PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent( resultPendingIntent );

        // id allows update of notification later on
        NotificationManager mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        mNotificationManager.notify( alarmId, mBuilder.build() );
    }

}
