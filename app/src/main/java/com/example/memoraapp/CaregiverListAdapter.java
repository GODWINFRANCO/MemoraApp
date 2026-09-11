package com.example.memoraapp;


import static androidx.core.content.ContextCompat.startActivity;

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

//import com.mashood.kaudisorders.R;
//import com.mashood.kaudisorders.disorder.DisorderListActivity;
import com.squareup.picasso.Picasso;

//import com.example.wecan.ui.dashboard.DashboardFragment;

//import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CaregiverListAdapter extends RecyclerView.Adapter<CaregiverListAdapter.MyViewHolder> {


    private LayoutInflater inflater;
    private ArrayList<CaregiverListModel> dataModelArrayList;
    private Context c;

    public CaregiverListAdapter(Context ctx, ArrayList<CaregiverListModel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.caregiverlist, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final CaregiverListModel omodel = dataModelArrayList.get(position);
        Picasso.get().load(config.CaretakerprofileUrl + omodel.getCaregiverpfp()).into(holder.prof);

        holder.name.setText(""+ dataModelArrayList.get(position).getCaregivername());
        holder.phone.setText(""+dataModelArrayList.get(position).getCaregiverfee());


        holder.booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, PatientCaregiverBooking.class);
                intent.putExtra("id",dataModelArrayList.get(position).getId());
                intent.putExtra("caregivername",dataModelArrayList.get(position).getCaregivername() );
                intent.putExtra("caregiverphone", dataModelArrayList.get(position).getCaregiverphone());
                intent.putExtra("caregiveraddress",dataModelArrayList.get(position).getCaregiveraddress());
                intent.putExtra("caregiverfee", dataModelArrayList.get(position).getCaregiverfee());
                intent.putExtra("caregiverdob",dataModelArrayList.get(position).getCaregiverdob());
                intent.putExtra("caregivermail",dataModelArrayList.get(position).getCaregivermail());
                intent.putExtra("caregiverpass",dataModelArrayList.get(position).getCaregiverpass());
                intent.putExtra("caregivergender", dataModelArrayList.get(position).getCaregivergender());
                intent.putExtra("caregiverpfp",dataModelArrayList.get(position).getCaregiverpfp());
                intent.putExtra("certificate",dataModelArrayList.get(position).getCertificate());

//
                c.startActivity(intent);

//                        if (!dataModelArrayList.get(position).getImage().equals("")) {
//            Picasso.get.load(config.imgurl+dataModelArrayList.get(position).getImage()).into(holder.image);
            }
//
        });
//
    }


        @Override
        public int getItemCount() {
        return dataModelArrayList.size();
    }


    public void filterList(ArrayList<CaregiverListModel> filteredSongs) {
        this.dataModelArrayList = filteredSongs;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        public

        TextView name,phone ;
        Button booking;
        ImageView prof;
//        Button bookbtn;


        public MyViewHolder(View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.cgphone);
            name= itemView.findViewById(R.id.cgname);
            prof= itemView.findViewById(R.id.profileImage);
            booking = itemView.findViewById(R.id.bookppp);


        }

    }
}