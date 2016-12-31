package nl.hva.erik.sensor.models;

/**
 * Created by erik on 14/12/2016.
 */
public class Sensor {

    private long id;
    private String content;

    public Sensor() {

    }
    public Sensor(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id =  id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content =  content;
    }



}