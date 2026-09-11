package com.example.memoraapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;

public class PatientSidePaymentFullActivity extends AppCompatActivity {


    TextView name,date,permonth,tobepaid,phone;
    String sid,sname,sdate,sper,stobe,sphone,spfp,spatientname,stime, spatientphone;
    Button proceed;
    ImageView pfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_side_payment_full);
        name=findViewById(R.id.fname);
        date=findViewById(R.id.fdate);
        permonth=findViewById(R.id.fpermonth);
        tobepaid=findViewById(R.id.ftobepaid);
        phone=findViewById(R.id.fphone);
        proceed=findViewById(R.id.fproceed);
        pfp=findViewById(R.id.fpfp);

        Intent in = getIntent();
        sid = in.getStringExtra("id");
        sname=in.getStringExtra("caregivername");
        spatientname=in.getStringExtra("patientname");
        spatientphone=in.getStringExtra("patientphone");
        stime=in.getStringExtra("time");
        sdate=in.getStringExtra("bookingdate");
        sper=in.getStringExtra("caregiverfees");
        stobe=in.getStringExtra("caregiverfees");
        sphone=in.getStringExtra("caregiverphone");
        spfp=in.getStringExtra("caregiverpfp");

        if (spfp != null && !spfp.isEmpty()) {
            Picasso.get().load(config.CaretakerprofileUrl + spfp).into(pfp);
        }

        name.setText(sname);
        phone.setText(sphone);
        date.setText(sdate);
        permonth.setText(sper);
        tobepaid.setText(stobe);






        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PatientSidePaymentFullActivity.this, PaymentActivity.class);
                in.putExtra("id", sid);
                in.putExtra("caregivername", sname);
                in.putExtra("userphone", spatientphone);
                in.putExtra("username", spatientname);
                in.putExtra("fee", stobe);
                in.putExtra("date", sdate);
//                in.putExtra("time", stime);
                startActivity(in);
                finish();
            }
        });

    }
}