package com.stirling.senpino.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stirling.senpino.Measurement;
import com.stirling.senpino.R;

import java.util.List;

public class MeasurementDataAdapter extends RecyclerView.Adapter<MeasurementDataAdapter.MeasurementViewHolder> {
    private List<Measurement> measurements;

    public class MeasurementViewHolder extends RecyclerView.ViewHolder {
        private TextView user, weight, timestamp;

        public MeasurementViewHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.user);
            weight = (TextView) view.findViewById(R.id.weight);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
        }
    }

    public MeasurementDataAdapter(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    @Override
    public MeasurementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.measurement_row, parent, false);

        return new MeasurementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MeasurementViewHolder holder, int position) {
        Measurement measurement = measurements.get(position);
        holder.user.setText(measurement.getUser());
        holder.weight.setText(measurement.getWeight());
        holder.timestamp.setText(measurement.getTimestamp());

    }

    @Override
    public int getItemCount() {
//        if(measurements != null){
            return measurements.size();
//        }
//        return 0;
    }
}