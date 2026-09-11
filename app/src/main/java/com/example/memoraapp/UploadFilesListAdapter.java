package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UploadFilesListAdapter extends RecyclerView.Adapter<UploadFilesListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<UploadFilesListModel> dataModelArrayList;
    private Context c;
    // URLs
    String url_delete = config.BaseUrl + "upload_file_delete.php";
    String url_share = config.BaseUrl + "upload_file_share.php";

    public UploadFilesListAdapter(Context ctx, ArrayList<UploadFilesListModel> dataModelArrayList) {
        c = ctx;
        inflater = LayoutInflater.from(c);
        this.dataModelArrayList = dataModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.upload_files_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final UploadFilesListModel omodel = dataModelArrayList.get(position);

        // 1. Load Image
        Picasso.get().load(config.files + omodel.getImage()).into(holder.file_image);

        // 2. Set Text
        holder.note.setText(omodel.getNote());

        // 3. Set Share Button Text (Fixes scrolling issue)
        if ("yes".equalsIgnoreCase(omodel.getShare())) {
            holder.share.setText("Unshare"); // Already shared, so button allows unsharing
        } else {
            holder.share.setText("Share");
        }

        // 4. Share Button Click
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog(omodel.getShare(), position);
            }
        });

        // 5. Delete Button Click
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog(position);
            }
        });

        // 6. Download Button Click
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImage(omodel.getImage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataModelArrayList.size();
    }

    // --- DIALOGS & LOGIC ---

    private void shareDialog(final String currentShareStatus, final int position) {
        String title = "no".equals(currentShareStatus) ? "Share File" : "Unshare File";
        String msg = "no".equals(currentShareStatus) ? "Are you sure you want to share this file?" : "Stop sharing this file?";

        new AlertDialog.Builder(c)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = dataModelArrayList.get(position).getId();

                        if ("no".equals(currentShareStatus)) {
                            // Currently not shared -> Call Share
                            performShareAction(id, position, "share");
                        } else {
                            // Currently shared -> Call Unshare
                            performShareAction(id, position, "unshare");
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Consolidated method for both Share and Unshare to reduce code duplication
    private void performShareAction(final String id, final int position, final String actionType) {
        StringRequest str = new StringRequest(Request.Method.POST, url_share,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsnb = new JSONObject(response);
                            String status = jsnb.getString("status");
                            String message = jsnb.getString("message");

                            if ("1".equals(status) || "success".equals(status)) {
                                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();

                                // Update Local Model
                                String newStatus = actionType.equals("share") ? "yes" : "no";
                                dataModelArrayList.get(position).setShare(newStatus);

                                // Refresh UI for this item
                                notifyItemChanged(position);

                            } else {
                                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(c, "JSON Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(c, "Network Error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                // Send "share" or "unshare" to your PHP
                params.put("type", actionType);
                return params;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(c);
        rq.add(str);
    }

    private void deleteDialog(final int position) {
        new AlertDialog.Builder(c)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this file?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFile(dataModelArrayList.get(position).getId(), position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteFile(final String id, final int position) {
        StringRequest str = new StringRequest(Request.Method.POST, url_delete,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsnb = new JSONObject(response);
                            String status = jsnb.getString("status");
                            String message = jsnb.getString("message");

                            if ("1".equals(status) || "success".equals(status)) {
                                Toast.makeText(c, "Deleted Successfully", Toast.LENGTH_SHORT).show();

                                // Remove from list and animate
                                dataModelArrayList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, dataModelArrayList.size());
                            } else {
                                Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("id", id);
                return params;
            }
        };
        RequestQueue rq = Volley.newRequestQueue(c);
        rq.add(str);
    }

    private void downloadImage(String imageUrl) {
        String downloadUrl = config.files + imageUrl;

        DownloadManager downloadManager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            try {
                Uri downloadUri = Uri.parse(downloadUrl);
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setTitle("Downloading Image");
                request.setDescription("Downloading " + imageUrl);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                String fileName = "IMG_" + System.currentTimeMillis() + ".png";
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                downloadManager.enqueue(request);
                Toast.makeText(c, "Download started...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(c, "Error starting download.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(c, "Download manager not available", Toast.LENGTH_SHORT).show();
        }
    }

    public void filterList(ArrayList<UploadFilesListModel> filteredList) {
        this.dataModelArrayList = filteredList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView note;
        ImageView file_image;
        Button delete, download, share;

        public MyViewHolder(View itemView) {
            super(itemView);
            file_image = itemView.findViewById(R.id.file_image);
            delete = itemView.findViewById(R.id.file_delete);
            note = itemView.findViewById(R.id.file_note);
            download = itemView.findViewById(R.id.file_download);
            share = itemView.findViewById(R.id.file_share);
        }
    }
}