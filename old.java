package com.dosage;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.TimePicker;
import android.widget.Toast;

import com.snapmeds.Constants;
import com.snapmeds.R;
import com.storage.SimpleStorage;
import com.utilities.Dosage;
import com.utilities.DosageParser;
import com.utilities.Frequency;
import com.utilities.Prescription;

public class DosageParserActivity extends Activity {
    private Prescription prescription;
    private Dosage dosage;
    private String setId;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dosage_parser );

        Intent intent = getIntent();
        setId = intent.getStringExtra( Constants.SET_ID );
        try {
            prescription = SimpleStorage.loadPrescription( this, setId );
            dosage = prescription.getDosage();
            if ( dosage != null ) {
            	setFieldsForDosage( dosage );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        // Configures what the dosage text does
        EditText dosageText = (EditText) findViewById( R.id.dosageText );
        dosageText.setOnEditorActionListener( new OnEditorActionListener() {
            public boolean onEditorAction( TextView v, int actionId, KeyEvent event ) {
                if ( actionId == EditorInfo.IME_ACTION_DONE ) {
                    parseDosageText( v );
                }
                return false;
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.dosage_parser, menu );
        return true;
    }

    /**
     * Parse full dosage text into components and display the result.
     * 
     * @param view
     *            - Enter button
     */
    public void parseDosageText( View view ) {
        String dosageText = ( (EditText) findViewById( R.id.dosageText ) ).getText().toString();
        ( (EditText) findViewById( R.id.dosageText ) ).setText( "" );
        ( (EditText) findViewById( R.id.dosageText ) ).clearFocus();
        dosage = DosageParser.parseDosageString( dosageText );

        if ( dosage == null ) {
            Toast toast = Toast.makeText( this.getApplicationContext(), R.string.dosage_parse_failed, Toast.LENGTH_SHORT );
            toast.show();
            return;
        }
        setFieldsForDosage( dosage );
    }

    private void setFieldsForDosage( Dosage dosage ) {
        ( (EditText) findViewById( R.id.dosageQuantity ) ).setText( dosage.getDose() );
        ( (EditText) findViewById( R.id.dosageFrequency ) ).setText( dosage.getFrequencyString() );
        ( (EditText) findViewById( R.id.dosageRoute ) ).setText( dosage.getRoute() );
        ( (EditText) findViewById( R.id.dosageInstructions ) ).setText( dosage.getInstructions() );
        ( (EditText) findViewById( R.id.dosageDuration ) ).setText( dosage.getDuration() );
        ( (EditText) findViewById( R.id.dosageReason ) ).setText( dosage.getReason() );
        ( (EditText) findViewById( R.id.dosageWarnings ) ).setText( dosage.getWarnings() );
    }

    private Dosage createDosageFromFields() {
        Dosage dosage = new Dosage();
        dosage.setDose( ( (EditText) findViewById( R.id.dosageQuantity ) ).getText().toString() );
        dosage.setFrequencyString( ( (EditText) findViewById( R.id.dosageFrequency ) ).getText().toString() );
        dosage.setRoute( ( (EditText) findViewById( R.id.dosageRoute ) ).getText().toString() );
        dosage.setInstructions( ( (EditText) findViewById( R.id.dosageInstructions ) ).getText().toString() );
        dosage.setDuration( ( (EditText) findViewById( R.id.dosageDuration ) ).getText().toString() );
        dosage.setReason( ( (EditText) findViewById( R.id.dosageReason ) ).getText().toString() );
        dosage.setWarnings( ( (EditText) findViewById( R.id.dosageWarnings ) ).getText().toString() );
        return dosage;
    }

    /**
     * Handles action when user presses the Save button.
     * 
     * @param view - Save button
     */
    public void onSavePressed( View view ) {
    	new AlertDialog.Builder(this)
    		.setIcon(android.R.drawable.ic_dialog_alert)
    		.setTitle(R.string.save_title)
    		.setMessage(R.string.save_message)
    		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveDosage();
				}
			})
			.setNegativeButton(R.string.no, null)
			.show();
    }
    
    /**
     * Save dosage information to persistent storage.
     */
    public void saveDosage() {
        // Cancel any previously set reminders
        if ( dosage != null ) {
        	dosage.cancelReminders( this );
        }

        // Begin setting new dosage value
        dosage = createDosageFromFields();
        Frequency frequency = dosage.getFrequency();

        CalendarReminder calReminder = new CalendarReminder( frequency, prescription.getDrug().getName(), dosage.getDose() );
        NotificationReminder notifyReminder = new NotificationReminder( setId, dosage.getFrequency(), prescription.getDrug().getName(), dosage.getDose() );

        //dosage.addReminder( calReminder );
        dosage.addReminder( notifyReminder );

        // Open timepickers to set the times value in the dosage frequency
        TimePickerListener listener = new TimePickerListener( dosage.getFrequency(), null );
        TimePickerFragment timePicker = new TimePickerFragment();

        // Bind listener to fragment
        timePicker.setOnTimeSetListener( listener );
        timePicker.show( getFragmentManager(), "timePicker" );
    }

    /**
     * This is called after reminder times have been received from the user
     */
    private void saveDosageToStorage() {
        prescription.setDosage( dosage );
        dosage.setupReminders( this );
        try {
            SimpleStorage.editPrescription( this, prescription );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public class TimePickerListener implements TimePickerDialog.OnTimeSetListener {
        private Frequency frequency;
        private boolean isTimeSet;
        private int popupsRemaining;

        public TimePickerListener( Frequency frequency, Integer reserved ) {
            this.frequency = frequency;
            isTimeSet = false;
            if ( reserved == null ) {
                this.popupsRemaining = frequency.getNumTimes() - 1;
            }
            else {
                this.popupsRemaining = reserved;
            }
        }

        @Override
        public void onTimeSet( TimePicker view, int hourOfDay, int minute ) {
            // There is an android bug where onTimeSet is called twice per time
            // set. So we have use a flag to ensure we only respond once
            if ( isTimeSet ) return;
            isTimeSet = true;

            long milliseconds = 0;
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set( Calendar.HOUR_OF_DAY, hourOfDay );
            selectedTime.set( Calendar.MINUTE, minute );
            milliseconds = selectedTime.getTimeInMillis();
            frequency.addTime( milliseconds );

            if ( popupsRemaining > 0 ) {
                // if more pop ups needed, show another time picker
                TimePickerListener listener = new TimePickerListener( frequency, popupsRemaining - 1 );
                TimePickerFragment timePicker = new TimePickerFragment();
                timePicker.setOnTimeSetListener( listener );
                timePicker.show( getFragmentManager(), "timePicker" );
            }
            else {
                saveDosageToStorage();
            }
        }

    }
}
