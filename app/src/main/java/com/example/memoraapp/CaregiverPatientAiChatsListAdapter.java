package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CaregiverPatientAiChatsListAdapter extends RecyclerView.Adapter<CaregiverPatientAiChatsListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    // CHANGED: Use the PatientLog model instead of CaregiverListModel
    private ArrayList<CaregiverPatientAiChatsListModel> dataModelArrayList;
    private Context c;

    public CaregiverPatientAiChatsListAdapter(Context ctx, ArrayList<CaregiverPatientAiChatsListModel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CHANGED: Inflate the log card layout (item_patient_log.xml)
        View view = inflater.inflate(R.layout.item_patient_log, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final CaregiverPatientAiChatsListModel model = dataModelArrayList.get(position);

        // 1. Set Text Data
        holder.txtPatientId.setText("Patient ID: " + model.getPatient_id());
        holder.txtDate.setText(model.getCreated_at());
        holder.txtSummary.setText(model.getSession_summary());

        // 2. SMART COLOR LOGIC
        // This detects keywords in the summary to change the color strip
        String text = model.getSession_summary().toLowerCase();

        if (text.contains("pain") || text.contains("critical") || text.contains("suicidal") || text.contains("emergency")) {
            // RED for Danger
            holder.viewStatusColor.setBackgroundColor(Color.parseColor("#E53935"));
        } else if (text.contains("sad") || text.contains("anxious") || text.contains("depressed") || text.contains("worry")) {
            // ORANGE for Warning
            holder.viewStatusColor.setBackgroundColor(Color.parseColor("#FFB300"));
        } else {
            // GREEN for Stable
            holder.viewStatusColor.setBackgroundColor(Color.parseColor("#43A047"));
        }

        // Optional: Make the whole card clickable if you want to open details
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // You can open a detailed activity here if needed
                // Intent intent = new Intent(c, LogDetailActivity.class);
                // intent.putExtra("summary", model.getSummary());
                // c.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    // Helper to filter list (for search functionality)
    public void filterList(ArrayList<CaregiverPatientAiChatsListModel> filteredList) {
        this.dataModelArrayList = filteredList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        // CHANGED: Updated to match IDs in item_patient_log.xml
        TextView txtPatientId, txtDate, txtSummary;
        View viewStatusColor;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtPatientId = itemView.findViewById(R.id.txtPatientId);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtSummary = itemView.findViewById(R.id.txtSummary);
            viewStatusColor = itemView.findViewById(R.id.viewStatusColor);
        }
    }
}