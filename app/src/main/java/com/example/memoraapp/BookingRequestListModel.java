package com.example.memoraapp;

public class BookingRequestListModel {
    String id, patientid, patientname, patientphone, patientpfp, caregiverid, caregivername, caregiverphone, caregiverpfp, caregiverfees, bookingdate, patientnotes, created_at, status, fee, feedate;

    public BookingRequestListModel(String id, String patientid, String patientname, String patientphone, String patientpfp, String caregiverid, String caregivername, String caregiverphone, String caregiverpfp, String caregiverfees, String bookingdate, String patientnotes, String created_at, String status, String fee, String feedate) {
        this.id = id;
        this.patientid = patientid;
        this.patientname = patientname;
        this.patientphone = patientphone;
        this.patientpfp = patientpfp;
        this.caregiverid = caregiverid;
        this.caregivername = caregivername;
        this.caregiverphone = caregiverphone;
        this.caregiverpfp = caregiverpfp;
        this.caregiverfees = caregiverfees;
        this.bookingdate = bookingdate;
        this.patientnotes = patientnotes;
        this.created_at = created_at;
        this.status = status;
        this.fee = fee;
        this.feedate = feedate;

    }

    public String getId() {
        return id;
    }

    public String getPatientid() {
        return patientid;
    }

    public String getPatientname() {
        return patientname;
    }

    public String getPatientphone() {
        return patientphone;
    }

    public String getPatientpfp() {
        return patientpfp;
    }

    public String getCaregiverid() {
        return caregiverid;
    }

    public String getCaregivername() {
        return caregivername;
    }

    public String getCaregiverphone() {
        return caregiverphone;
    }

    public String getCaregiverpfp() {
        return caregiverpfp;
    }

    public String getCaregiverfees() {
        return caregiverfees;
    }

    public String getBookingdate() {
        return bookingdate;
    }

    public String getPatientnotes() {
        return patientnotes;
    }

    public String getCreated_at() {
        return created_at;
    }
    public String getStatus() {
        return status;
    }
    public String getFee() {
        return fee;
    }
    public String getFeedate() {
        return feedate;
    }
}
