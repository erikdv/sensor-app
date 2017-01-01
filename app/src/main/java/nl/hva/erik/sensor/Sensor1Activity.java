package nl.hva.erik.sensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;


public class Sensor1Activity extends Activity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor sensor;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor= sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        Log.d("TEMP", Float.toString(sensor.getPower()) );

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
    }

}
