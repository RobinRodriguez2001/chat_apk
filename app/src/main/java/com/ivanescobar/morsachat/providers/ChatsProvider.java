package com.ivanescobar.morsachat.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ivanescobar.morsachat.models.Chat;

public class ChatsProvider {

    CollectionReference mCollection;

    public ChatsProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    // Método para crear un chat
    public Task<Void> create(Chat chat) {
        // Guardar el chat en la colección "Chats" con un ID único
        return mCollection.document().set(chat);
    }
}