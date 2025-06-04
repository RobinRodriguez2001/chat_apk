package com.ivanescobar.morsachat.providers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class UsersProvider2 {

    CollectionReference mCollection;

    public UsersProvider2() {
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    // Método para obtener todos los usuarios
    public Query getAllUsers() {
        return mCollection;
    }
}