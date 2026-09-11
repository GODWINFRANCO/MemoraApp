package com.example.memoraapp;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

//import com.mashood.kaudisorders.R;
//import com.mashood.kaudisorders.disorder.DisorderListActivity;
//import com.squareup.picasso.Picasso;

//import com.example.wecan.ui.dashboard.DashboardFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CaregiverPatientFilesListAdapter extends RecyclerView.Adapter<CaregiverPatientFilesListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<UploadFilesListModel> dataModelArrayList;
    private Context c;
//    String status, message, url = config.BaseUrl + "upload_file_delete.php";

    public CaregiverPatientFilesListAdapter(Context ctx, ArrayList<UploadFilesListModel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_patient_files, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final UploadFilesListModel omodel = dataModelArrayList.get(position);
        Picasso.get().load(config.files + omodel.getImage()).into(holder.file_image);

        holder.note.setText( dataModelArrayList.get(position).getNote());

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(dataModelArrayList.get(position).getImage());
            }
            private void downloadImage(String imageUrl1) {
                String resumeUrl = config.files + imageUrl1;

                DownloadManager downloadManager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    try {
                        Uri downloadUri = Uri.parse(resumeUrl);
                        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                        request.setTitle("Downloading Image");
                        request.setDescription("Downloading " + imageUrl1);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        String fileName = imageUrl1 + "_image_" + System.currentTimeMillis() + ".png";
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                        downloadManager.enqueue(request);
                        Toast.makeText(c, "Download started for: " + imageUrl1, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(c, "Error downloading image. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(c, "Download manager not available", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }


    public void filterList(ArrayList<UploadFilesListModel> filteredSongs) {
        this.dataModelArrayList = filteredSongs;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {


        public

        TextView note;
        ImageView file_image;
        Button download;


        public MyViewHolder(View itemView) {
            super(itemView);
            file_image= itemView.findViewById(R.id.file_image);
            note = itemView.findViewById(R.id.file_note);
            download = itemView.findViewById(R.id.file_download);
        }
    }
}