package com.example.memoraapp;

public class ReminderModel {
    String id, caregiver_id, caregivername, patient_id, reminder, date, time;

    public ReminderModel(String id, String caregiver_id, String caregivername, String patient_id, String reminder, String date, String time) {
        this.id = id;
        this.caregiver_id = caregiver_id;
        this.caregivername = caregivername;
        this.patient_id = patient_id;
        this.reminder = reminder;
        this.date = date;
        this.time = time;
    }

    public String getId() { return id; }
    public String getCaregiverId() { return caregiver_id; }
    public String getCaregiverName() { return caregivername; }
    public String getPatientId() { return patient_id; }
    public String getReminder() { return reminder; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}