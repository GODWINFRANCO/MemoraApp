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

public class BookingRequestListAdapter extends RecyclerView.Adapter<BookingRequestListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<BookingRequestListModel> dataModelArrayList;
    private Context c;

    public BookingRequestListAdapter(Context ctx, ArrayList<BookingRequestListModel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bookinglist, parent, false);
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
        holder.phone.setText(model.getCreated_at());
        holder.status.setText(model.getStatus());

        holder.profile_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, BookingRequest_Full_activity.class);

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

        TextView name, phone, status;
        Button accept;
        ImageView proff;
        CardView profile_card;

        public MyViewHolder(View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.patphone);
            name = itemView.findViewById(R.id.patname);
            proff = itemView.findViewById(R.id.patientphoto);
            status=itemView.findViewById(R.id.status);
            profile_card=itemView.findViewById(R.id.card_profile);
        }
    }
}
