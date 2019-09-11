package com.utilities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.dosage.Reminder;

public class Dosage {

    private String dose;
    private String frequencyString;
    private Frequency frequency;
    private String route;
    private String warnings;
    private String instructions;
    private String reason;
    private String duration;
    private List<Reminder> reminders;

    public Dosage() {
        dose = "";
        frequencyString = "";
        frequency = new Frequency();
        route = "";
        warnings = "";
        instructions = "";
        reason = "";
        duration = "";
        reminders = new ArrayList<Reminder>();
    }

    public Dosage( String dose, String frequency ) {
        setDose( dose );
        setFrequencyString( frequency );
    }

    public String getDose() {
        return dose;
    }

    public void setDose( String dose ) {
        this.dose = dose;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public String getFrequencyString() {
        return frequencyString;
    }

    public void setFrequencyString( String frequencyString ) {
        this.frequencyString = frequencyString;
        this.frequency = new Frequency();
        this.frequency.parseString( frequencyString );
    }

    public void setFrequency( Frequency frequency ) {
        this.frequency = frequency;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute( String route ) {
        this.route = route;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings( String warnings ) {
        this.warnings = warnings;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions( String instructions ) {
        this.instructions = instructions;
    }

    public String getReason() {
        return reason;
    }

    public void setReason( String reason ) {
        this.reason = reason;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration( String duration ) {
        this.duration = duration;
    }

    public List<Reminder> getReminders() {
        return this.reminders;
    }

    public void setReminders( List<Reminder> reminders ) {
        this.reminders = reminders;
    }

    public void addReminder( Reminder reminder ) {
        this.reminders.add( reminder );
    }

    public void setupReminders( Context context ) {
        for ( Reminder reminder : reminders ) {
            reminder.setupReminder( context );
        }
    }

    public void cancelReminders( Context context ) {
        for ( Reminder reminder : reminders ) {
            reminder.cancelReminder( context );
        }
    }

    @Override
    public String toString() {
        return "Dosage []";
    }

}
