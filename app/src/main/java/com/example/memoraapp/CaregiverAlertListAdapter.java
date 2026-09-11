package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CaregiverAlertListAdapter extends RecyclerView.Adapter<CaregiverAlertListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<CaregiverAlertListModel> dataModelArrayList;
    private Context c;

    public CaregiverAlertListAdapter(Context ctx, ArrayList<CaregiverAlertListModel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_alert, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        CaregiverAlertListModel model = dataModelArrayList.get(position);

//         LOAD PATIENT IMAGE ONLY
        Picasso.get()
                .load(config.PatientprofileUrl + model.getPatientpfp())
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.iv_user_avatar);

        holder.tv_notification_text.setText(model.getPatientname() +
                " has the word "+ model.getTrigger_word() +
                " in their journal("+ model.getDate() +
                ")");

//        holder.proff.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(c, BookingRequest_Full_activity.class);
//
//                intent.putExtra("id", model.getId());
//                intent.putExtra("patientid", model.getPatientid());
//                intent.putExtra("patientname", model.getPatientname());
//                intent.putExtra("patientphone", model.getPatientphone());
//                intent.putExtra("patientpfp", model.getPatientpfp());
//                intent.putExtra("caregiverid", model.getCaregiverid());
//                intent.putExtra("caregivername", model.getCaregivername());
//                intent.putExtra("caregiverphone", model.getCaregiverphone());
//                intent.putExtra("caregiverpfp", model.getCaregiverpfp());
//                intent.putExtra("caregiverfees", model.getCaregiverfees());
//                intent.putExtra("bookingdate", model.getBookingdate());
//                intent.putExtra("patientnotes", model.getPatientnotes());
//                intent.putExtra("created_at", model.getCreated_at());
//                intent.putExtra("status", model.getStatus());
//                intent.putExtra("fee", model.getFee());
//                intent.putExtra("feedate", model.getFeedate());
//
//                c.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    public void filterList(ArrayList<CaregiverAlertListModel> filteredSongs) {
        this.dataModelArrayList = filteredSongs;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_notification_text;
//        Button accept;
        ImageView iv_user_avatar;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_notification_text = itemView.findViewById(R.id.tv_notification_text);
//            name = itemView.findViewById(R.id.patname);
            iv_user_avatar = itemView.findViewById(R.id.iv_user_avatar);
//            status=itemView.findViewById(R.id.status);
        }
    }
}
