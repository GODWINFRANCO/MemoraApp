package com.example.memoraapp;

public class PatientJournalListModel {
    String id, user_id, date, journal, timestamp;

    public PatientJournalListModel(String id, String user_id, String date, String journal, String timestamp) {
        this.id = id;
        this.user_id = user_id;
        this.date = date;
        this.journal = journal;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getDate() {
        return date;
    }

    public String getJournal() {
        return journal;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
