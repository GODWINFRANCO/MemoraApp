package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class PaymentSuccess extends AppCompatActivity {

    TextView t1,t2,t3,t4;

    String st1,st2,st3,st4;

    MaterialButton backtohome;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_success);
        t1=findViewById(R.id.psdoctor);
        t2=findViewById(R.id.psdate);
        t3=findViewById(R.id.pspatient);
        backtohome=findViewById(R.id.psbacktohome);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backtohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(PaymentSuccess.this, Home.class);
                startActivity(in);
                finish();
            }
        });
        Intent intent = getIntent();

        st1=intent.getStringExtra("caregivername");
        t1.setText(st1);
        st2=intent.getStringExtra("date");
        t2.setText(st2);
        st3=intent.getStringExtra("username");
        t3.setText(st3);
//        st4=intent.getStringExtra("time");
//        t4.setText(st4);

    }
}
