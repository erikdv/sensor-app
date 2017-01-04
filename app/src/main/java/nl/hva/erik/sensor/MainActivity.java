package nl.hva.erik.sensor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ListView listView;

    private  String url;

    ArrayList<HashMap<String, Object>> measurementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        url = getString(R.string.backend_url);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        measurementList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list);
        new GetSensors().execute();

        Button button = (Button) findViewById(R.id.available_sensors_button);
        final Intent intent = new Intent(this, SensorOverviewActivity.class);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        Button temperatureButton = (Button) findViewById(R.id.temp_sensor_button);
        final Intent temperatureIntent = new Intent(this, Sensor1Activity.class);
        temperatureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(temperatureIntent);
            }
        });


    }

    private class GetSensors extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(MainActivity.this);
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
                    JSONArray measurements = jsonObject.getJSONArray("measurements");

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
                    MainActivity.this, measurementList,
                    R.layout.list_item, new String[]{"timestamp", "value"}, new int[]{R.id.content, R.id.id});

            listView.setAdapter(adapter);
        }

    }
}