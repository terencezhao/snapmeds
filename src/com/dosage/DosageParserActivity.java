package com.dosage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.snapmeds.Constants;
import com.snapmeds.R;
import com.utilities.Dosage;
import com.utilities.DosageParser;

public class DosageParserActivity extends Activity {
    private Dosage dosage;
    private String setId;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dosage_parser );

        Intent intent = getIntent();
        setId = intent.getStringExtra( Constants.DRUG_SET_ID );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.dosage_parser, menu );
        return true;
    }

    /**
     * Handles action when parse button is pressed.
     * 
     * @param view - enter button
     */
    public void onParse( View view ) {
        String dosageText = ( (EditText) findViewById( R.id.dosageText ) ).getText().toString();
        ( (EditText) findViewById( R.id.dosageText ) ).setText( "" );
        ( (EditText) findViewById( R.id.dosageText ) ).clearFocus();
        dosage = DosageParser.parseDosageString( dosageText );

        // Parse fails
        if ( dosage == null ) {
            Toast toast = Toast.makeText( this.getApplicationContext(), R.string.dosage_parse_failed, Toast.LENGTH_SHORT );
            toast.show();
            return;
        }
        
        // Parse succeeds, start next activity
        Intent intent = new Intent( this, DosageParserFieldsActivity.class );
        intent.putExtra( Constants.DOSAGE_PARSE, true );
        intent.putExtra( Constants.DRUG_SET_ID, setId );
        putParsedDosage( intent );
        startActivity( intent );
    }
    
    /**
     * Handles action when skip button is pressed.
     * 
     * @param view - skip button
     */
    public void onSkip( View view ) {
        Intent intent = new Intent( this, DosageParserFieldsActivity.class );
        intent.putExtra( Constants.DOSAGE_PARSE, false );
        intent.putExtra( Constants.DRUG_SET_ID, setId );
        startActivity( intent );
    }
    
    /**
     * Puts dosage fields in intent.
     * 
     * @param intent
     */
    private void putParsedDosage( Intent intent) {
        intent.putExtra( Constants.DOSAGE_QUANTITY, dosage.getDose() );
        intent.putExtra( Constants.DOSAGE_FREQUENCY, dosage.getFrequencyString() );
        intent.putExtra( Constants.DOSAGE_ROUTE, dosage.getRoute() );
        intent.putExtra( Constants.DOSAGE_INSTRUCTIONS, dosage.getInstructions() );
        intent.putExtra( Constants.DOSAGE_DURATION, dosage.getDuration() );
        intent.putExtra( Constants.DOSAGE_REASON, dosage.getReason() );
        intent.putExtra( Constants.DOSAGE_WARNINGS, dosage.getWarnings() );
    }
}
