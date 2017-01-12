package nl.hva.erik.sensor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nl.hva.erik.sensor.R;
import nl.hva.erik.sensor.models.Measurement;

public class MeasurementAdapter extends ArrayAdapter<Measurement> {

    public MeasurementAdapter(Context context, ArrayList<Measurement> measurements) {

        super(context, 0, measurements);

    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        Measurement measurement = getItem(position);

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        }


        TextView timestampView = (TextView) convertView.findViewById(R.id.timestamp);

        TextView valueView = (TextView) convertView.findViewById(R.id.value);

        timestampView.setText(measurement.getTimestamp().toString());

        valueView.setText(Float.toString(measurement.getValue()));

        return convertView;

    }

}