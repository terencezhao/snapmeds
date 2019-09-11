package com.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.utilities.Prescription;

public class SimpleStorage {
    private static final String PRESCRIPTION_KEY = "key";

    /**
     * Loads a Prescription from persistence
     * 
     * @param activity
     *            - the activity storing the Prescription
     * @return Prescription - the Prescription to load
     * @throws IOException
     */
    public static Prescription loadPrescription( Context context, String setId ) throws IOException {
        List<Prescription> prescriptions = loadPrescriptions( context );

        if ( prescriptions == null ) {
            return null;
        }

        for ( Prescription prescription : prescriptions ) {
            String prescriptionSetId = prescription.getDrug().getSetid();
            if ( prescriptionSetId.equals( setId ) ) {
                return prescription;
            }
        }

        return null;
    }

    /**
     * Loads List of Prescriptions from persistence
     * 
     * @param activity
     *            - the activity storing the Prescription
     * @return List<Prescription> - list of Prescriptions persisted
     * @throws IOException
     */
    public static List<Prescription> loadPrescriptions( Context context ) throws IOException {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        String prescriptionSerialized = settings.getString( PRESCRIPTION_KEY, null );

        List<Prescription> prescriptions = new ArrayList<Prescription>();

        if ( prescriptionSerialized == null ) {
            return prescriptions;
        }

        ObjectMapper serializer = new ObjectMapper();
        JavaType prescriptionListType = serializer.getTypeFactory().constructCollectionType( List.class,
                Prescription.class );
        prescriptions = serializer.readValue( prescriptionSerialized, prescriptionListType );
        
        try
        {
          prescriptions = serializer.readValue( prescriptionSerialized, prescriptionListType );
          prescriptions = prescriptions != null ? prescriptions : new ArrayList<Prescription>();
        }
        catch(JsonMappingException e)
        {
          // Wipe the objects in an outdated schema.
          clearPrescriptions(context);
        }
        return prescriptions;
    }

    /**
     * Adds Prescription to persistence
     * 
     * @param activity
     *            - the activity storing the Prescription
     * @param prescription
     *            - the Prescription object being stored
     * @throws IOException
     */
    public static void addPrescription( Context context, Prescription prescription ) throws IOException {
        List<Prescription> prescriptions = loadPrescriptions( context );
        prescriptions.add( prescription );

        write( context, prescriptions );
    }

    /**
     * Adds List of Prescriptions to persistence
     * 
     * @param activity
     *            - the activity storing the Prescription
     * @param prescriptions
     *            - the List of Prescription objects being stored
     * @throws IOException
     */
    public static void addPrescriptions( Context context, List<Prescription> addedPrescriptions ) throws IOException {
        List<Prescription> prescriptions = loadPrescriptions( context );
        prescriptions.addAll( addedPrescriptions );

        write( context, prescriptions );

    }

    /**
     * Removes [first instance of] the Prescription in storage
     * 
     * @param activity
     *            - the activity which stored the Prescription
     * @param prescription
     *            - the Prescription to remove from persistence
     * @throws IOException
     */
    public static void removePrescription( Context context, Prescription prescription ) throws IOException {
        List<Prescription> prescriptions = loadPrescriptions( context );
        prescriptions.remove( prescription );

        write( context, prescriptions );
    }

    /**
     * Edits the Prescription in persistence
     * 
     * @param activity
     *            - the activity storing the Prescription being edited
     * @param prescription
     *            - the Prescription being edited
     * @throws IOException
     */
    public static void editPrescription( Context context, Prescription prescription ) throws IOException {
        List<Prescription> prescriptions = loadPrescriptions( context );
        for ( int i = 0; i < prescriptions.size(); i++ ) {
            if ( prescriptions.get( i ).equals( prescription ) ) {
                prescriptions.set( i, prescription );
            }
        }

        write( context, prescriptions );
    }

    /**
     * Clears all the Prescriptions from persistence
     * 
     * @param activity
     *            - the activity wishing to clear its Prescriptions
     * @throws IOException
     */
    public static void clearPrescriptions( Context context ) throws IOException {
        write( context, new ArrayList<Prescription>() );
    }

    /**
     * Writes list of Prescriptions to the activity's persistence
     * 
     * @param activity
     *            - the activity to write the Prescriptions to
     * @param prescriptions
     *            - the list of Prescriptions to write
     * @throws IOException
     */
    private static void write( Context context, List<Prescription> prescriptions ) throws IOException {
        ObjectMapper serializer = new ObjectMapper();
        String serializedList = serializer.writeValueAsString( prescriptions );

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString( PRESCRIPTION_KEY, serializedList );
        editor.commit();
    }

}
