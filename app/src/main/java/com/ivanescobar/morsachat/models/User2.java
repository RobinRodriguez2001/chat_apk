package com.ivanescobar.morsachat.models;

public class User2 {
    private String idUser; // Cambiado a idUser
    private String email;
    private String username;
    private String phone;
    private long timestamp;
    private String image_profile;
    private String image_cover;

    public User2() {
    }

    public User2(String idUser, String email, String username, String phone, long timestamp, String image_profile, String image_cover) {
        this.idUser = idUser;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.timestamp = timestamp;
        this.image_profile = image_profile;
        this.image_cover = image_cover;
    }

    // Getters y setters
    public String getIdUser() { // Cambiado a getIdUser
        return idUser;
    }

    public void setIdUser(String idUser) { // Cambiado a setIdUser
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage_profile() {
        return image_profile;
    }

    public void setImage_profile(String image_profile) {
        this.image_profile = image_profile;
    }

    public String getImage_cover() {
        return image_cover;
    }

    public void setImage_cover(String image_cover) {
        this.image_cover = image_cover;
    }
}