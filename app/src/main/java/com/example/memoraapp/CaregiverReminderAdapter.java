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

public class CaregiverReminderAdapter extends RecyclerView.Adapter<CaregiverReminderAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<ReminderModel> reminderList;
    // Replace with your actual delete URL
    private String deleteUrl = config.BaseUrl + "reminder_delete.php";

    public CaregiverReminderAdapter(Context context, ArrayList<ReminderModel> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Make sure the XML file name matches step 3 (row_reminder.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.reminder_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReminderModel model = reminderList.get(position);

        holder.tvReminderContent.setText(model.getReminder());
        holder.tvDate.setText(model.getDate());
        holder.tvTime.setText(model.getTime());
        holder.tvCaregiverName.setText(model.getCaregiverName());

        holder.btnRemove.setOnClickListener(v -> confirmDelete(position, model.getId()));

    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    private void confirmDelete(int position, String id) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Reminder")
                .setMessage("Are you sure you want to remove this reminder?")
                .setPositiveButton("Yes", (dialog, which) -> deleteReminder(position, id))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReminder(int position, String id) {
        StringRequest request = new StringRequest(Request.Method.POST, deleteUrl,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if ("1".equals(status) || status.equals("success")) {
                            Toast.makeText(context, "Reminder Removed", Toast.LENGTH_SHORT).show();
                            reminderList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, reminderList.size());
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvReminderContent, tvDate, tvTime, tvCaregiverName;
        Button btnRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReminderContent = itemView.findViewById(R.id.tvReminderContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCaregiverName = itemView.findViewById(R.id.tvCaregiverName);

            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}