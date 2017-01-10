package nl.hva.erik.sensor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    ArrayList<HashMap<String, Object>> measurementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.available_sensors_button);
        final Intent intent = new Intent(this, SensorOverviewActivity.class);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        Button temperatureButton = (Button) findViewById(R.id.sensor1_button);
        final Intent sensor1Intent = new Intent(this, Sensor1Activity.class);
        temperatureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(sensor1Intent);
            }
        });
    }

}