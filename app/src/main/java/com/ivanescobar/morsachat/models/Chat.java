package com.ivanescobar.morsachat.models;

import java.util.Date;

public class Chat {
    private String idUser1;
    private String idUser2;
    private boolean writing;
    private long timestamp;

    // Constructor vacío (necesario para Firestore)
    public Chat() {
    }

    // Getters y setters
    public String getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(String idUser1) {
        this.idUser1 = idUser1;
    }

    public String getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(String idUser2) {
        this.idUser2 = idUser2;
    }

    public boolean isWriting() {
        return writing;
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}