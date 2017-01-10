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


public class Sensor1Activity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    JSONObject measurement;
    JSONObject measurementMap;
    String sensor1Name;

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ListView listView;

    private  String url;

    ArrayList<HashMap<String, Object>> measurementList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getString(R.string.backend_url);
        setContentView(R.layout.activity_sensor1);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor= sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        measurementList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.listSensor1);
        new GetMeasurementsSensor1().execute();

        sensor1Name = sensor.getName();
        Float sensor1Value = sensor.getPower();
        Log.d("SENSOR:"+ sensor1Name , Float.toString(sensor1Value) );
        measurement = new JSONObject();
        measurementMap = new JSONObject();

        try {
            Long timestamp = System.currentTimeMillis()/1000;
            measurement.put("timestamp",  timestamp);
            measurement.put("value", sensor1Value);
            measurementMap.put(sensor1Name, measurement);
        } catch (JSONException je) {
            je.printStackTrace();
        }

        new PostMeasurement().execute();

        TextView sensorNameValue = (TextView) findViewById(R.id.sensorNameValue);
        sensorNameValue.setText(sensor1Name);

        TextView sensorValue = (TextView) findViewById(R.id.sensorValue);
        sensorValue.setText(Float.toString(sensor1Value));

    }

    private class GetMeasurementsSensor1 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(Sensor1Activity.this);
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
                    JSONArray measurements = jsonObject.getJSONArray(sensor1Name);

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
                    Sensor1Activity.this, measurementList,
                    R.layout.list_item, new String[]{"timestamp", "value"}, new int[]{R.id.content, R.id.id});

            listView.setAdapter(adapter);
        }

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
            String jsonResponse = httpHandler.doPostRequest(url, measurementMap);
            Log.e(TAG, "Response from url: " + jsonResponse);

            return null;
        }


    }

}

