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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CaregiverPatientListAdapter extends RecyclerView.Adapter<CaregiverPatientListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<BookingRequestListModel> dataModelArrayList;
    private Context c;

    public CaregiverPatientListAdapter(Context ctx, ArrayList<BookingRequestListModel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_patient, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        BookingRequestListModel model = dataModelArrayList.get(position);

        // LOAD PATIENT IMAGE ONLY
        Picasso.get()
                .load(config.PatientprofileUrl + model.getPatientpfp())
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.proff);

        holder.name.setText(model.getPatientname());
        holder.phone.setText(model.getPatientphone());

        holder.documents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, CaregiverPatientFilesListActivity.class);
                intent.putExtra("patientid", model.getPatientid());
                c.startActivity(intent);
            }
        });
        holder.ai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, CaregiverPatientAiChatsListActivity.class);
                intent.putExtra("patientid", model.getPatientid());
                c.startActivity(intent);
            }
        });
        holder.reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, CaregiverReminder.class);
                intent.putExtra("patient_id", model.getPatientid());
                c.startActivity(intent);
            }
        });


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

    public void filterList(ArrayList<BookingRequestListModel> filteredSongs) {
        this.dataModelArrayList = filteredSongs;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone;
        Button documents, ai, reminder;
        ImageView proff;
        CardView patient_card;

        public MyViewHolder(View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.patphone);
            name = itemView.findViewById(R.id.patname);
            proff = itemView.findViewById(R.id.patientphoto);
            documents = itemView.findViewById(R.id.pat_documents);
            ai = itemView.findViewById(R.id.pat_ai);
            patient_card = itemView.findViewById(R.id.patient_card);
            reminder = itemView.findViewById(R.id.pat_reminder);
        }
    }
}
