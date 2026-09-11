package com.example.memoraapp;

public class CaregiverPatientAiChatsListModel {
    String id, patient_id, session_summary, created_at;

    public CaregiverPatientAiChatsListModel(String id, String patient_id, String session_summary, String created_at) {
        this.id = id;
        this.patient_id = patient_id;
        this.session_summary = session_summary;
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getSession_summary() {
        return session_summary;
    }

    public String getCreated_at() {
        return created_at;
    }
}
