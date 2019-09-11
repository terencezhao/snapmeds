package com.utilities;

import java.util.ArrayList;
import java.util.List;

public class Frequency {
	// every x 
    private int interval;
    
    // times to take, from time pickers
    private List<Long> times;
    
    // number of times to take
    private int numTimes;
    
    // daily, weekly, monthly
    private FrequencyUnit unit;

    public Frequency() {
        numTimes = 0;
        interval = 0;
        unit = FrequencyUnit.INVALID;
        times = new ArrayList<Long>();
    }
    
    public Frequency( int numTimes, FrequencyUnit unit, int interval) {
        this.times = new ArrayList<Long>();
        this.numTimes = numTimes;
        this.unit = unit;
        this.interval = interval;
    }

    public Frequency( List<Long> times, FrequencyUnit unit, int interval ) {
        this.times = times;
        this.numTimes = times.size();
        this.unit = unit;
        this.interval = interval;
    }

    public void parseString( String unparsedFrequency ) {
        parseNumTimes( unparsedFrequency );
        parseUnit( unparsedFrequency );
        parseInterval( unparsedFrequency );
    }

    private void parseNumTimes( String unparsedFrequency ) {
        if ( unparsedFrequency.matches( "(?i).*(1 TIME|ONE TIME|ONCE).*" ) )
            setNumTimes( 1 );
        else if ( unparsedFrequency.matches( "(?i).*(2 TIMES|TWO TIMES|TWICE).*" ) )
            setNumTimes( 2 );
        else if ( unparsedFrequency.matches( "(?i).*(3 TIMES|THREE TIMES).*" ) )
            setNumTimes( 3 );
        else if ( unparsedFrequency.matches( "(?i).*(4 TIMES|FOUR TIMES).*" ) )
            setNumTimes( 4 );
        else if ( unparsedFrequency.matches( "(?i).*(5 TIMES|FIVE TIMES).*" ) )
            setNumTimes( 5 );
        else if ( unparsedFrequency.matches( "(?i).*(6 TIMES|SIX TIMES).*" ) )
            setNumTimes( 6 );
        else if ( unparsedFrequency.matches( "(?i).*(7 TIMES|SEVEN TIMES).*" ) )
            setNumTimes( 7 );
        else if ( unparsedFrequency.matches( "(?i).*(8 TIMES|EIGHT TIMES).*" ) )
            setNumTimes( 8 );
        else if ( unparsedFrequency.matches( "(?i).*(9 TIMES|NINE TIMES).*" ) )
            setNumTimes( 9 );
        else
            setNumTimes( 1 );
    }

    private void parseUnit( String unparsedFrequency ) {
        if ( unparsedFrequency.matches( "(?i).*(HOURLY|HOUR|HOURS).*" ) )
            setUnit( FrequencyUnit.HOURLY );
        else if ( unparsedFrequency.matches( "(?i).*(DAILY|DAY|DAYS).*" ) )
            setUnit( FrequencyUnit.DAILY );
        else if ( unparsedFrequency.matches( "(?i).*(WEEKLY|WEEK|WEEKS).*" ) )
            setUnit( FrequencyUnit.WEEKLY );
        else if ( unparsedFrequency.matches( "(?i).*(MONTHLY|MONTH|MONTHS).*" ) )
            setUnit( FrequencyUnit.MONTHLY );
        else if ( unparsedFrequency.matches( "(?i).*(MINUTELY|MINUTE|MINUTES).*" ) )
            setUnit( FrequencyUnit.MINUTELY);
        else
            setUnit( FrequencyUnit.DAILY );
    }

    private void parseInterval( String unparsedFrequency ) {
        if ( unparsedFrequency.matches( "(?i).*(EVERY 1|EVERY ONE).*" ) )
            setInterval( 1 );
        else if ( unparsedFrequency.matches( "(?i).*(EVERY 2|EVERY TWO|EVERY OTHER).*" ) )
            setInterval( 2 );
        else if ( unparsedFrequency.matches( "(?i).*(EVERY 3|EVERY THREE).*" ) )
            setInterval( 3 );
        else if ( unparsedFrequency.matches( "(?i).*(EVERY 4|EVERY FOUR).*" ) )
            setInterval( 4 );
        else if ( unparsedFrequency.matches( "(?i).*(EVERY 5|EVERY FIVE).*" ) )
            setInterval( 5 );
        else if ( unparsedFrequency.matches( "(?i).*(EVERY 6|EVERY SIX).*" ) )
            setInterval( 6 );
        else if ( unparsedFrequency.matches( "(?i).*(EVERY 7|EVERY SEVEN).*" ) )
            setInterval( 7 );
        else if ( unparsedFrequency.matches( "(?i).*(EVERY 8|EVERY EIGHT).*" ) )
            setInterval( 8 );
        else if ( unparsedFrequency.matches( "(?i).*(EVERY 9|EVERY NINE).*" ) )
            setInterval( 9 );
        else
            setInterval( 1 );
    }

    public void addTime( Long time ) {
        this.times.add( time );
    }

    public List<Long> getTimes() {
        return times;
    }

    public int getNumTimes() {
        return numTimes;
    }

    public FrequencyUnit getUnit() {
        return unit;
    }

    public int getInterval() {
        return interval;
    }

    public void setNumTimes( int numTimes ) {
        this.numTimes = numTimes;
    }

    public void setUnit( FrequencyUnit unit ) {
        this.unit = unit;
    }

    public void setInterval( int interval ) {
        this.interval = interval;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + interval;
        result = prime * result + numTimes;
        result = prime * result + ( ( times == null ) ? 0 : times.hashCode() );
        result = prime * result + ( ( unit == null ) ? 0 : unit.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( !( obj instanceof Frequency ) ) return false;
        Frequency other = (Frequency) obj;
        if ( interval != other.interval ) return false;
        if ( numTimes != other.numTimes ) return false;
        if ( times == null ) {
            if ( other.times != null ) return false;
        }
        else if ( !times.equals( other.times ) ) return false;
        if ( unit != other.unit ) return false;
        return true;
    }

}
