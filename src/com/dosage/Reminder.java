package com.dosage;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import android.content.Context;

import com.utilities.Frequency;

@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Reminder {
    protected String title;
    protected String detail;
    protected Frequency frequency;
    protected boolean isSetup;
    
    public Reminder( Frequency frequency, String title, String detail ) {
        this.frequency = frequency;
        this.title = title;
        this.detail = detail;
        this.isSetup = false;
    }

    public abstract void setupReminder( Context context );

    public abstract void cancelReminder( Context context );
    
    /* ----------- Functions only for Jackson serialization ------------- */
    
    public Reminder() {}

    public boolean isSetup() {
        return isSetup;
    }

    public void setSetup( boolean isSetup ) {
        this.isSetup = isSetup;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail( String detail ) {
        this.detail = detail;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency( Frequency frequency ) {
        this.frequency = frequency;
    }
}
