package nl.hva.erik.sensor.models;

import java.sql.Timestamp;

/**
 * Created by erik on 14/12/2016.
 */
public class Measurement {

    private Timestamp timestamp;
    private long value;

    public Measurement() {

    }
    public Measurement(Timestamp timestamp,  long value) {
        this.timestamp = timestamp ;
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long id) {
        this.timestamp =  timestamp;
    }

    public long getValue() {
        return value;
    }

    public void setValue(String content) {
        this.value =  value;
    }



}