package com.dosage;

import java.io.IOException;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;

import com.storage.SimpleStorage;
import com.utilities.Prescription;

/**
 * Service that sets up all prescription notifications. Notifications are
 * canceled when the phone turns off, so we must set them up again on reboot.
 */
public class SetupNotificationsService extends IntentService {

    public SetupNotificationsService() {
        super( "SetupNotificationService" );
    }

    @Override
    protected void onHandleIntent( Intent intent ) {
        List<Prescription> prescriptions;
        try {
            prescriptions = SimpleStorage.loadPrescriptions( this.getApplicationContext() );
        } catch ( IOException e ) {
            e.printStackTrace();
            return;
        }

        // Setup all prescription notification reminders
        for ( Prescription prescription : prescriptions ) {
            List<Reminder> reminders = prescription.getDosage().getReminders();
            for ( Reminder reminder : reminders ) {
                if ( reminder instanceof NotificationReminder ) {
                    ( (NotificationReminder) reminder ).setupReminder( this.getApplicationContext() );
                }
            }
        }

    }

}
