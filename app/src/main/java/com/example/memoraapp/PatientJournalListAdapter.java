package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatientJournalListAdapter extends RecyclerView.Adapter<PatientJournalListAdapter.MyViewHolder> {

    private Context c;
    private ArrayList<PatientJournalListModel> list;

    public PatientJournalListAdapter(Context ctx, ArrayList<PatientJournalListModel> list) {
        this.c = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_journal, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        PatientJournalListModel model = list.get(position);

//        holder.name.setText("Name: " + model.getUsername());
        holder.date.setText(model.getDate());
        holder.dairy.setText(model.getJournal());
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    public void filterList(ArrayList<PatientJournalListModel> filteredList) {
        this.list = filteredList;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, day, date, dairy;

        MyViewHolder(View v) {
            super(v);
//            name = v.findViewById(R.id.tvUsername);
//            day = v.findViewById(R.id.tvDay);
            date = v.findViewById(R.id.tvDate);
            dairy = v.findViewById(R.id.tvDiary);
        }
    }
}
