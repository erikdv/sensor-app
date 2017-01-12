package nl.hva.erik.sensor.acitivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.HashMap;

import nl.hva.erik.sensor.R;

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

        Button sensor1Button = (Button) findViewById(R.id.sensor1_button);
        final Intent sensor1Intent = new Intent(this, SensorDetailsActivity.class);
        sensor1Intent.putExtra("sensor", Sensor.TYPE_GRAVITY);

        sensor1Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(sensor1Intent);
            }
        });


        Button sensor2Button = (Button) findViewById(R.id.sensor2_button);
        final Intent sensor2Intent = new Intent(this, SensorDetailsActivity.class);
        sensor2Intent.putExtra("sensor", Sensor.TYPE_LIGHT);

        sensor2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(sensor2Intent);
            }
        });
    }

}