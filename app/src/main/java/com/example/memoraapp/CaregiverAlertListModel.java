package com.example.memoraapp;

public class CaregiverAlertListModel {
    String id, user_id, caregiver_id, patientname, date, trigger_word, timestamp, patientpfp;

    public CaregiverAlertListModel(String id, String user_id, String caregiver_id, String patientname, String date, String trigger_word, String timestamp, String patientpfp) {
        this.id = id;
        this.user_id = user_id;
        this.caregiver_id = caregiver_id;
        this.patientname = patientname;
        this.date = date;
        this.trigger_word = trigger_word;
        this.timestamp = timestamp;
        this.patientpfp = patientpfp;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getCaregiver_id() {
        return caregiver_id;
    }

    public String getPatientname() {
        return patientname;
    }

    public String getDate() {
        return date;
    }

    public String getTrigger_word() {
        return trigger_word;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPatientpfp() {
        return patientpfp;
    }
}
