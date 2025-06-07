package com.ivanescobar.morsachat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ivanescobar.morsachat.adapters.ChatAdapter; // Asegúrate de que esta importación sea correcta
import com.ivanescobar.morsachat.models.Message; // Asegúrate de que esta importación sea correcta

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private EditText mEditTextMessage;
    private Button mButtonSend;
    private ChatAdapter mAdapter;
    private List<Message> mMessages;

    private String mUserId; // ID del usuario con el que se está chateando (receiverId)
    private String mUsername; // Nombre del usuario con el que se está chateando
    private FirebaseFirestore mFirestore;
    private String currentUserId; // Este es el ID del usuario actual (senderId)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Obtener el ID y el nombre de usuario del Intent
        mUserId = getIntent().getStringExtra("userId");
        mUsername = getIntent().getStringExtra("username");

        // Verificar que mUserId no sea nulo
        if (mUserId == null) {
            Log.e("CHAT_DEBUG", "Error: receiverId es nulo");
            Toast.makeText(this, "Error: No se pudo identificar al destinatario", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar la actividad si no hay un receiverId válido
        } else {
            Log.d("CHAT_DEBUG", "Receiver ID: " + mUserId);
        }

        // Inicializar Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Obtener el ID del usuario actual DESPUÉS de que FirebaseAuth esté inicializado
        // Esto es crucial para que el adaptador identifique correctamente los mensajes enviados
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("CHAT_DEBUG", "Current User ID: " + currentUserId);
        } else {
            // Manejar el caso en que el usuario actual no esté logueado (ej. redirigir a login)
            Log.e("CHAT_DEBUG", "Error: Usuario actual es nulo. ¿Usuario no logueado?");
            Toast.makeText(this, "No has iniciado sesión.", Toast.LENGTH_SHORT).show();
            finish();
            return; // Detener la ejecución adicional
        }

        // Configurar RecyclerView
        mRecyclerView = findViewById(R.id.recyclerViewChat);
        // Usar LayoutManager para mantener la posición al agregar nuevos elementos
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mMessages = new ArrayList<>();
        // Pasar currentUserId al adaptador aquí
        mAdapter = new ChatAdapter(mMessages, currentUserId);
        mRecyclerView.setAdapter(mAdapter);

        // Configurar EditText y Button
        mEditTextMessage = findViewById(R.id.editTextMessage);
        mButtonSend = findViewById(R.id.buttonSend);

        // Cargar mensajes existentes
        loadMessages();

        // Enviar mensaje
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Redirigir al Home
        Intent intent = new Intent(this, HomeActivity.class); // Reemplaza "HomeActivity" con el nombre de tu actividad principal
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Limpia la pila de actividades
        startActivity(intent);
        finish(); // Cierra la actividad actual

        // Animación personalizada al regresar (opcional)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void loadMessages() {
        // Asegurarse de que currentUserId esté disponible antes de la consulta
        if (currentUserId == null) {
            Log.e("CHAT_DEBUG", "Error: currentUserId es nulo en loadMessages.");
            return;
        }

        Log.d("CHAT_DEBUG", "Sender ID (Usuario Actual): " + currentUserId);
        Log.d("CHAT_DEBUG", "Receiver ID (Chateando Con): " + mUserId);

        // Consultar los mensajes entre los dos usuarios
        mFirestore.collection("Messages")
                // Filtra mensajes donde el senderId es el usuario actual O el usuario con el que se chatea
                .whereIn("senderId", Arrays.asList(currentUserId, mUserId))
                // Filtra mensajes donde el receiverId es el usuario actual O el usuario con el que se chatea
                .whereIn("receiverId", Arrays.asList(currentUserId, mUserId))
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("CHAT_DEBUG", "Error al escuchar mensajes: " + e.getMessage());
                            Toast.makeText(ChatActivity.this, "Error al cargar mensajes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        mMessages.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Convertir el documento a un objeto Message
                            Message message = document.toObject(Message.class);
                            mMessages.add(message);
                        }
                        mAdapter.notifyDataSetChanged();
                        // Desplazarse al último mensaje si hay alguno
                        if (!mMessages.isEmpty()) {
                            mRecyclerView.scrollToPosition(mMessages.size() - 1);
                        }
                    }
                });
    }

    private void sendMessage() {
        String messageText = mEditTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Crear un nuevo mensaje
            Map<String, Object> message = new HashMap<>();
            message.put("senderId", currentUserId); // Usar el currentUserId ya obtenido
            message.put("receiverId", mUserId);
            message.put("message", messageText);
            message.put("timestamp", new Date().getTime()); // Usar timestamp para ordenar

            Log.d("CHAT_DEBUG", "Enviando mensaje: " + messageText);
            Log.d("CHAT_DEBUG", "Sender ID: " + currentUserId);
            Log.d("CHAT_DEBUG", "Receiver ID: " + mUserId);

            // Guardar el mensaje en Firestore
            mFirestore.collection("Messages")
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("CHAT_DEBUG", "Mensaje enviado con ID: " + documentReference.getId());
                            mEditTextMessage.setText(""); // Limpiar el EditText
                            // NO ES NECESARIO llamar a loadMessages() aquí.
                            // El addSnapshotListener en loadMessages() se encargará de actualizar el RecyclerView automáticamente.
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("CHAT_DEBUG", "Error al enviar mensaje: " + e.getMessage());
                            Toast.makeText(ChatActivity.this, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}