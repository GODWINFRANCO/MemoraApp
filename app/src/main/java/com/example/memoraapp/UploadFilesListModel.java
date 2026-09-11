package com.example.memoraapp;

public class UploadFilesListModel {
    String id, user_id, note, image, share;

    public UploadFilesListModel(String id, String user_id, String note, String image, String share) {
        this.id = id;
        this.user_id = user_id;
        this.note = note;
        this.image = image;
        this.share = share;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getNote() {
        return note;
    }

    public String getImage() {
        return image;
    }

    public String getShare() {
        return share;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setShare(String share) {
        this.share = share;
    }
}
