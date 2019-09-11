package com.dosage;

import java.io.IOException;
import java.util.Calendar;

import com.snapmeds.Constants;
import com.snapmeds.R;
import com.storage.SimpleStorage;
import com.utilities.Dosage;
import com.utilities.Frequency;
import com.utilities.Prescription;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class DosageParserFieldsActivity extends Activity {
    private Prescription prescription;
    private Dosage dosage;
    private String setId;
	
	@Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dosage_fields_parser );

        Intent intent = getIntent();
        boolean isParsed = intent.getBooleanExtra( Constants.DOSAGE_PARSE, false );
        setId = intent.getStringExtra( Constants.DRUG_SET_ID );
        
        // Get prescription
        try {
			prescription = SimpleStorage.loadPrescription( this, setId );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        // If parsed, create dosage from intent and set text fields
        if (isParsed) {
        	dosage = createDosageFromIntent( intent );
        	setFieldsForDosage( dosage );
        }
        
        // If skipped, get dosage from persistent storage and set text fields 
        else {
            dosage = prescription.getDosage();
			if ( dosage != null ) {
				setFieldsForDosage( dosage );
			}
        }
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
     * Save dosage to persistent storage.
     */
    private void saveDosage() {
        // Cancel any previously set reminders
        if ( prescription.getDosage() != null ) {
        	prescription.getDosage().cancelReminders( this );
        }

        // Create new dosage from text fields
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
     * Saves dosage to persistent storage.
     * Called after reminder times have been received from the user.
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
	
    /**
     * Sets the text fields with the parsed information.
     * 
     * @param dosage
     */
    private void setFieldsForDosage( Dosage dosage ) {
        ( (EditText) findViewById( R.id.dosageQuantity ) ).setText( dosage.getDose() );
        ( (EditText) findViewById( R.id.dosageFrequency ) ).setText( dosage.getFrequencyString() );
        ( (EditText) findViewById( R.id.dosageRoute ) ).setText( dosage.getRoute() );
        ( (EditText) findViewById( R.id.dosageInstructions ) ).setText( dosage.getInstructions() );
        ( (EditText) findViewById( R.id.dosageDuration ) ).setText( dosage.getDuration() );
        ( (EditText) findViewById( R.id.dosageReason ) ).setText( dosage.getReason() );
        ( (EditText) findViewById( R.id.dosageWarnings ) ).setText( dosage.getWarnings() );
    }
    
    /**
     * Creates a dosage object from intent.
     * 
     * @param intent
     * @return
     */
    private Dosage createDosageFromIntent( Intent intent ) {
        Dosage dosage = new Dosage();
        dosage.setDose( intent.getStringExtra(Constants.DOSAGE_QUANTITY) );
        dosage.setFrequencyString( intent.getStringExtra(Constants.DOSAGE_FREQUENCY) );
        dosage.setRoute( intent.getStringExtra(Constants.DOSAGE_ROUTE) );
        dosage.setInstructions( intent.getStringExtra(Constants.DOSAGE_INSTRUCTIONS) );
        dosage.setDuration( intent.getStringExtra(Constants.DOSAGE_DURATION) );
        dosage.setReason( intent.getStringExtra(Constants.DOSAGE_REASON) );
        dosage.setWarnings( intent.getStringExtra(Constants.DOSAGE_WARNINGS) );
        return dosage;
    }
    
    /**
     * Creates a dosage object from text fields.
     * 
     * @return
     */
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
}