package nl.hva.erik.sensor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class Sensor1Activity extends Activity implements SensorEventListener {

    private String TAG = Sensor1Activity.class.getSimpleName();
    private ProgressDialog progressDialog;
    private SensorManager sensorManager;
    private Sensor sensor;
    private  String url;
    JSONObject measurement;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getString(R.string.backend_url);
        setContentView(R.layout.activity_temperature);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor= sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        Log.d("TEMP", Float.toString(sensor.getPower()) );
        measurement = new JSONObject();

        try {
            Long timestamp = System.currentTimeMillis()/1000;
            measurement.put("timestamp",  timestamp);
            measurement.put("value", 3);
        } catch (JSONException je) {
            je.printStackTrace();
        }

        new PostMeasurement().execute();

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



    private class PostMeasurement extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler httpHandler = new HttpHandler();
            httpHandler.doPostRequest(url, measurement);
            String jsonResponse = httpHandler.doPostRequest(url, measurement);
            Log.e(TAG, "Response from url: " + jsonResponse);

            return null;
        }


    }

}

