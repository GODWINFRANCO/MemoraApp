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

public class CaregiverProfile extends AppCompatActivity {
    TextView username,mail,dob,address,phone,name,pass,edit,fee,logout;
    ImageView backbutton,pfp;
    String susername,smail,sdob,saddress,sphone,sfee,sname,sid,spass,spfp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_caregiver_profile);

        mail=findViewById(R.id.careprofilemail);
        dob=findViewById(R.id.careprofiledob);
        name=findViewById(R.id.careprofilename);
        address=findViewById(R.id.careprofileaddress);
        fee=findViewById(R.id.careprofilefee);
        phone=findViewById(R.id.careprofilephone);
        username=findViewById(R.id.careprofileusername);
        pass=findViewById(R.id.careprofilepass);
        backbutton=findViewById(R.id.carebackbutton);
        edit=findViewById(R.id.careedit);
        logout=findViewById(R.id.carelogout);
        pfp=findViewById(R.id.careprofileimage);

        HashMap<String ,String> data=new CaregiverSessionManager(CaregiverProfile.this).getUserDetails();
        sid=data.get("id");
        sname=data.get("caregivername");
        sphone=data.get("caregiverphone");
        saddress=data.get("caregiveraddress");
        sfee=data.get("caregiverfee");
        sdob=data.get("caregiverdob");
        smail=data.get("caregivermail");
        spass=data.get("caregiverpass");

        spfp=data.get("caregiverpfp");
        Picasso.get().load(config.CaretakerprofileUrl + spfp).into(pfp);

        username.setText(sname);
        name.setText(sname);
        phone.setText(sphone);
        address.setText(saddress);
        fee.setText(sfee);
        dob.setText(sdob);
        mail.setText(smail);
        pass.setText(spass);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Example: show message
                Toast.makeText(getApplicationContext(),
                        "Logged out successfully",
                        Toast.LENGTH_SHORT).show();

                // Example: go to LoginActivity
                Intent intent = new Intent(CaregiverProfile.this, FirstPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                // Close current screen
                finish();
            }
        });
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(CaregiverProfile.this, CaregiverEditProfile.class);
                in.putExtra("id",sid);
                in.putExtra("caregivername",sname);
                in.putExtra("caregiverphone",sphone);
                in.putExtra("caregiveraddress",saddress);
                in.putExtra("caregiverfee",sfee);
                in.putExtra("caregiverdob",sdob);
                in.putExtra("caregivermail",smail);
                in.putExtra("caregiverpass",spass);
                in.putExtra("caregiverpfp",spfp);


                startActivity(in);
            }
        });
    }
}