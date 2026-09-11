package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PatientSideBookingsAdapter extends RecyclerView.Adapter<PatientSideBookingsAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<BookingRequestListModel> dataModelArrayList;
    private Context c;
    String status, message;

    public PatientSideBookingsAdapter(Context ctx, ArrayList<BookingRequestListModel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.psbookings, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        BookingRequestListModel model = dataModelArrayList.get(position);

        // LOAD CAREGIVER IMAGE
        Picasso.get()
                .load(config.CaretakerprofileUrl + model.getCaregiverpfp())
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.proff);

        holder.name.setText(model.getCaregivername());
        holder.fee.setText(model.getCaregiverfees());
        holder.status.setText(model.getStatus());

        if ("approved".equalsIgnoreCase(model.getStatus())) {
            holder.payment.setVisibility(View.VISIBLE);
        } else {
            holder.payment.setVisibility(View.GONE);
        }
        if ("paid".equalsIgnoreCase(model.getStatus())) {
            holder.cancel.setVisibility(View.VISIBLE);
        } else {
            holder.cancel.setVisibility(View.GONE);
        }

        // --- DATE CALCULATION & COLOR LOGIC ---
        String bookingDateStr = model.getBookingdate();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            Date startDate = sdf.parse(bookingDateStr);

            if (startDate != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                // 1. Calculate End Date
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                cal.add(Calendar.MONTH, 1);
                Date endDate = cal.getTime();

                // 2. Display the dates
                holder.start_date.setText(displayFormat.format(startDate));
                holder.end_date.setText(displayFormat.format(endDate));

                // 3. Compare with Today's Date
                Date today = new Date(); // Gets current system time

                if (today.before(endDate) || today.equals(endDate)) {
                    // Within range - Green background (using hex for light green)
                    holder.start_date.setBackgroundColor(android.graphics.Color.parseColor("#C8E6C9"));
                    holder.end_date.setBackgroundColor(android.graphics.Color.parseColor("#C8E6C9"));
                } else {
                    // Expired - Red background (using hex for light red)
                    holder.start_date.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"));
                    holder.end_date.setBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.start_date.setText(bookingDateStr);
            holder.end_date.setText("---");
        }

        holder.payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStatus();
            }

            private void checkStatus() {
                StringRequest str = new StringRequest(Request.Method.POST, config.BaseUrl+"booking_pay_check.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

//                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsnb = new JSONObject(response);
                            status = jsnb.getString("status");
                            message = jsnb.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if ("0".equals(status)) {
                            Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(c, PatientSidePaymentFullActivity.class);

                            intent.putExtra("id", model.getId());
                            intent.putExtra("patientid", model.getPatientid());
                            intent.putExtra("patientname", model.getPatientname());
                            intent.putExtra("patientphone", model.getPatientphone());
                            intent.putExtra("patientpfp", model.getPatientpfp());
                            intent.putExtra("caregiverid", model.getCaregiverid());
                            intent.putExtra("caregivername", model.getCaregivername());
                            intent.putExtra("caregiverphone", model.getCaregiverphone());
                            intent.putExtra("caregiverpfp", model.getCaregiverpfp());
                            intent.putExtra("caregiverfees", model.getCaregiverfees());
                            intent.putExtra("bookingdate", model.getBookingdate());
                            intent.putExtra("patientnotes", model.getPatientnotes());
                            intent.putExtra("created_at", model.getCreated_at());
                            intent.putExtra("status", model.getStatus());
                            intent.putExtra("fee", model.getFee());
                            intent.putExtra("feedate", model.getFeedate());

                            c.startActivity(intent);
//                            Toast.makeText(c, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(c, error.toString(), Toast.LENGTH_SHORT).show();

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("id", dataModelArrayList.get(position).getId());

                        return params;
                    }
                };

                RequestQueue rq = Volley.newRequestQueue(c);
                rq.add(str);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                re();
            }

            private void re() {
                new AlertDialog.Builder(c)
                        .setTitle("Cancel")
                        .setMessage("Are you sure you want to cancel this booking?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dele(dataModelArrayList.get(position).getId());

                            }

                            private void dele(String id) {
                                StringRequest str = new StringRequest(Request.Method.POST, config.BaseUrl+"booking_cancel.php", new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

//                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                                        try {
                                            JSONObject jsnb = new JSONObject(response);
                                            status = jsnb.getString("status");
                                            message = jsnb.getString("message");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        if ("0".equals(status)) {
                                            Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(c, "Cancelled", Toast.LENGTH_SHORT).show();
                                            holder.cancel.setVisibility(View.GONE);
                                            holder.status.setText("Cancelled");

//                                            dataModelArrayList.remove(position);
//
//                                            // 2. Notify the adapter that an item was removed to update the UI
//                                            notifyItemRemoved(position);
//                                            notifyItemRangeChanged(position, dataModelArrayList.size());

                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Toast.makeText(c, error.toString(), Toast.LENGTH_SHORT).show();

                                    }
                                }) {

                                    @Override
                                    protected Map<String, String> getParams() {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("id", id );

                                        return params;
                                    }
                                };

                                RequestQueue rq = Volley.newRequestQueue(c);
                                rq.add(str);


                            }

                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(c, ReminderListActivity.class);
                                c.startActivity(intent);
                            }
                        })
                        .show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public void filterList(ArrayList<BookingRequestListModel> filteredSongs) {
        this.dataModelArrayList = filteredSongs;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, fee, status, start_date, end_date;
        Button payment, cancel;
        ImageView proff;

        public MyViewHolder(View itemView) {
            super(itemView);
            fee = itemView.findViewById(R.id.carefee);
            name = itemView.findViewById(R.id.patname);
            proff = itemView.findViewById(R.id.patientphoto);
            status = itemView.findViewById(R.id.status);
            payment = itemView.findViewById(R.id.pay);
            cancel = itemView.findViewById(R.id.cancel);

            start_date = itemView.findViewById(R.id.start_date);
            end_date = itemView.findViewById(R.id.end_date);
        }
    }
}