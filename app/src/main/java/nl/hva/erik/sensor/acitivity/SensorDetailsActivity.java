package nl.hva.erik.sensor.acitivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import nl.hva.erik.sensor.helper.HttpHandler;
import nl.hva.erik.sensor.R;

public class SensorDetailsActivity extends Activity {

    TextView label;
    TextView status;
    String sensor2Name;
    JSONObject measurement;
    JSONObject measurementMap;
    Float sensor2Value;
    int sensorType;

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ListView listView;
    Sensor sensor;

    private  String url;

    ArrayList<HashMap<String, Object>> measurementList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        url = getString(R.string.backend_url);

        listView = (ListView) findViewById(R.id.listSensorDetails);

        measurementList = new ArrayList<>();
        measurement = new JSONObject();
        measurementMap = new JSONObject();

        label = (TextView)findViewById(R.id.sensorAvailable);
        status = (TextView)findViewById(R.id.sensorValue);

        new GetMeasurementsSensor().execute();

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Intent intent = getIntent();
        sensorType = intent.getIntExtra("sensor", -1);
        sensor = sensorManager.getDefaultSensor(sensorType);
        sensor2Name = sensor.getName();
        if(sensor != null){
            label.setText(sensor.getName());
            sensorManager.registerListener(
                    sensorEventListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }else{
            label.setText("Sensor not found");
        }

        final Button button = (Button) findViewById(R.id.storeValueSensor);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Long timestamp = System.currentTimeMillis()/1000;
                    measurement.put("timestamp",  timestamp);
                    measurement.put("value", sensor2Value);
                    measurementMap.put(sensor2Name, measurement);
                } catch (JSONException je) {
                    je.printStackTrace();
                }
                new SensorDetailsActivity.PostMeasurement().execute();
            }

        });

    }

    private final SensorEventListener sensorEventListener
            = new SensorEventListener(){

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == sensorType){
                sensor2Value = event.values[0];
                status.setText( Float.toString(event.values[0]));
            }
        }
    };

    private class GetMeasurementsSensor extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(SensorDetailsActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler httpHandler = new HttpHandler();

            // Making a request to url and getting response
            String jsonResponse = httpHandler.doGetRequest(url);

            Log.e(TAG, "Response from url: " + jsonResponse);

            if (jsonResponse != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    // Getting JSON Array node
                   JSONArray measurements = jsonObject.getJSONArray(sensor2Name);
                 //   JSONArray measurements = jsonObject.getJSONArray("Gravity");

                    // looping through All Contacts
                    for (int i = 0; i < measurements.length(); i++) {
                        JSONObject measurement = measurements.getJSONObject(i);

                        int id =  measurement.getInt("timestamp");
                        long name = measurement.getLong("value");


                        // tmp hash map for single contact
                        HashMap<String, Object> measurementMap = new HashMap<>();

                        // adding each child node to HashMap key => value
                        measurementMap.put("timestamp", id);
                        measurementMap.put("value", name);


                        // adding contact to contact list
                        measurementList.add(measurementMap);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    SensorDetailsActivity.this, measurementList,
                    R.layout.list_item, new String[]{"timestamp", "value"}, new int[]{R.id.content, R.id.id});

            listView.setAdapter(adapter);
        }

    }
    private class PostMeasurement extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler httpHandler = new HttpHandler();
            String jsonResponse = httpHandler.doPostRequest(url, measurementMap);
            Log.e(TAG, "Response from url: " + jsonResponse);

            return null;
        }
    }

}