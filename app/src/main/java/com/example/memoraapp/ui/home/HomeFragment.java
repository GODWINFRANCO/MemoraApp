package com.example.memoraapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.memoraapp.Add_Memory_album;
import com.example.memoraapp.CaregiverListActivity;
import com.example.memoraapp.ChatBotActivity;
import com.example.memoraapp.PatientFamiliarPhotoUpload;
import com.example.memoraapp.PatientJournal;
import com.example.memoraapp.PatientSideBookingsListActivity;
import com.example.memoraapp.R;
import com.example.memoraapp.ReminderListActivity;
import com.example.memoraapp.SOS_Activity;
import com.example.memoraapp.UploadFilesGetOTP;
import com.example.memoraapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    CardView list, bookings, journal, digilocker, sos, chatbot,famphoto, reminder_list;


    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        list=root.findViewById(R.id.listview);
        bookings=root.findViewById(R.id.bookings);
//        journal = root.findViewById(R.id.journal);
        digilocker = root.findViewById(R.id.digilocker);
        sos = root.findViewById(R.id.sos);
        chatbot = root.findViewById(R.id.chabot);
        famphoto = root.findViewById(R.id.famphoto);
//        reminder_list = root.findViewById(R.id.reminder_list);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent n=new Intent(getActivity(), CaregiverListActivity.class);
                startActivity(n);
            }
        });

        bookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n=new Intent(getActivity(), PatientSideBookingsListActivity.class);
                startActivity(n);
            }
        });
//        journal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent n=new Intent(getActivity(), PatientJournal.class);
//                startActivity(n);
//            }
//        });
        digilocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n=new Intent(getActivity(), UploadFilesGetOTP.class);
                startActivity(n);
            }
        });
        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n=new Intent(getActivity(), SOS_Activity.class);
                startActivity(n);
            }
        });
        chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n=new Intent(getActivity(), ChatBotActivity.class);
                startActivity(n);
            }
        });
        famphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent n=new Intent(getActivity(), PatientFamiliarPhotoUpload.class);
                startActivity(n);
            }
        });
//        reminder_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent n=new Intent(getActivity(), ReminderListActivity.class);
//                startActivity(n);
//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}