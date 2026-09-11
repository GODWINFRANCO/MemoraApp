package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ReminderModel> reminderList;
    // Replace with your actual delete URL
    private String deleteUrl = config.BaseUrl + "reminder_delete.php";

    public ReminderAdapter(Context context, ArrayList<ReminderModel> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Make sure the XML file name matches step 3 (row_reminder.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.row_reminder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReminderModel model = reminderList.get(position);

        holder.tvReminderContent.setText(model.getReminder());
        holder.tvDate.setText(model.getDate());
        holder.tvTime.setText(model.getTime());
        holder.tvCaregiverName.setText(model.getCaregiverName());

    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvReminderContent, tvDate, tvTime, tvCaregiverName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReminderContent = itemView.findViewById(R.id.tvReminderContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCaregiverName = itemView.findViewById(R.id.tvCaregiverName);
        }
    }
}