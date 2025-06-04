package com.ivanescobar.morsachat.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;


public class TokenProvider {

    CollectionReference mCollection;

    public TokenProvider(){
        mCollection = FirebaseFirestore.getInstance().collection("Tokens");
    }

    public void create(String idUser){
        if(idUser == null){ // Corregido: debes usar == en lugar de =
            return;
        }

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                // Aquí puedes guardar el token en Firestore o hacer lo que necesites con él

                mCollection.document(idUser).set(new Token(token));
            }
        });
    }



    // Clase Token para almacenar el token en Firestore
    public static class Token {
        private String token;

        public Token(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}