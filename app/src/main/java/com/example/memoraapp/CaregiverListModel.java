package com.example.memoraapp;

public class CaregiverListModel {
    String id,caregivername,caregiverphone,caregiveraddress,caregiverfee,caregiverdob,caregivermail,caregiverpass,caregivergender,caregiverpfp,certificate;

    public CaregiverListModel(String id, String caregivername, String caregiverphone, String caregiveraddress, String caregiverfee, String caregiverdob, String caregivermail, String caregiverpass, String caregivergender, String caregiverpfp, String certificate) {
        this.id = id;
        this.caregivername = caregivername;
        this.caregiverphone = caregiverphone;
        this.caregiveraddress = caregiveraddress;
        this.caregiverfee = caregiverfee;
        this.caregiverdob = caregiverdob;
        this.caregivermail = caregivermail;
        this.caregiverpass = caregiverpass;
        this.caregivergender = caregivergender;
        this.caregiverpfp = caregiverpfp;
        this.certificate = certificate;
    }

    public String getId() {
        return id;
    }

    public String getCaregivername() {
        return caregivername;
    }

    public String getCaregiverphone() {
        return caregiverphone;
    }

    public String getCaregiveraddress() {
        return caregiveraddress;
    }

    public String getCaregiverfee() {
        return caregiverfee;
    }

    public String getCaregiverdob() {
        return caregiverdob;
    }

    public String getCaregivermail() {
        return caregivermail;
    }

    public String getCaregiverpass() {
        return caregiverpass;
    }

    public String getCaregivergender() {
        return caregivergender;
    }

    public String getCaregiverpfp() {
        return caregiverpfp;
    }

    public String getCertificate() {
        return certificate;
    }
}

