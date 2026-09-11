package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class PatientProfile extends AppCompatActivity {

    TextView username,mail,dob,address,phone,name,pass,edit,logout;

    ImageView backbutton,pfp;
    String spfp,smail,sdob,saddress,sphone,sname,sid,spass;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_profile);
        mail=findViewById(R.id.profilemail);
        dob=findViewById(R.id.profiledob);
        name=findViewById(R.id.profilename);
        address=findViewById(R.id.profileaddress);
        phone=findViewById(R.id.profilephone);
        username=findViewById(R.id.profileusername);
        pass=findViewById(R.id.profilepass);
        backbutton=findViewById(R.id.backbutton);
        edit=findViewById(R.id.pedit);
        logout=findViewById(R.id.plogout);
        pfp=findViewById(R.id.patientprofileimage);

        HashMap<String ,String> data=new SessionManager(PatientProfile.this).getUserDetails();
        sid=data.get("id");
        sname=data.get("patientname");
        sphone=data.get("patientphone");
        saddress=data.get("patientaddress");
        sdob=data.get("patientdob");
        smail=data.get("patientmail");
        spass=data.get("patientpass");
        spfp=data.get("patientpfp");

        username.setText(sname);
        name.setText(sname);
        phone.setText(sphone);
        address.setText(saddress);
        dob.setText(sdob);
        mail.setText(smail);
        pass.setText(spass);

        spfp=data.get("patientpfp");
        Picasso.get().load(config.PatientprofileUrl + spfp).into(pfp);








        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PatientProfile.this, Home.class);
                startActivity(in);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Example: show message
                Toast.makeText(getApplicationContext(),
                        "Logged out successfully",
                        Toast.LENGTH_SHORT).show();

                // Example: go to LoginActivity
                Intent intent = new Intent(PatientProfile.this, FirstPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                // Close current screen
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PatientProfile.this, PatientEditProfile.class);
                in.putExtra("id",sid);
                in.putExtra("patientname",sname);
                in.putExtra("patientphone",sphone);
                in.putExtra("patientaddress",saddress);
                in.putExtra("patientdob",sdob);
                in.putExtra("patientmail",smail);
                in.putExtra("patientpass",spass);
                in.putExtra("patientpfp",spfp);


                startActivity(in);
            }
        });
    }
}