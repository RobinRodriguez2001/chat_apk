package com.ivanescobar.morsachat.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ivanescobar.morsachat.ChatActivity;
import com.ivanescobar.morsachat.R;
import com.ivanescobar.morsachat.models.User2;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends FirestoreRecyclerAdapter<User2, UsersAdapter.ViewHolder> {

    private Context mContext;

    // Constructor
    public UsersAdapter(FirestoreRecyclerOptions<User2> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User2 user) {
        // Depuración: Verifica que los datos se estén recuperando correctamente
        Log.d("USER_DEBUG", "User: " + user.getUsername() + ", Image Profile: " + user.getImage_profile());

        // Asignar el nombre de usuario
        holder.textViewUsername.setText(user.getUsername());



        // Cargar la imagen de perfil con Picasso
        if (user.getImage_profile() != null && !user.getImage_profile().isEmpty()) {
            Picasso.get()
                    .load(user.getImage_profile()) // Usa el campo image_profile
                    .placeholder(R.drawable.cover_image) // Imagen predeterminada mientras se carga
                    .error(R.drawable.cover_image) // Imagen predeterminada si hay un error
                    .into(holder.circleImageChat);
        } else {
            // Imagen predeterminada si no hay URL
            holder.circleImageChat.setImageResource(R.drawable.cover_image);
            Log.e("USER_DEBUG", "La imagen de perfil es nula o está vacía para el usuario: " + user.getUsername());
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de cada elemento de la lista
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_user, parent, false);
        return new ViewHolder(view);
    }

    // Clase ViewHolder para manejar las vistas
    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageChat;
        TextView textViewUsername;
        TextView textViewLastMessage;
        View view; // Referencia al layout principal

        public ViewHolder(View view) {
            super(view);
            this.view = view; // Asignar la vista principal
            circleImageChat = view.findViewById(R.id.circleImageChat);
            textViewUsername = view.findViewById(R.id.textViewUsernameChat);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessageChat);

            // Agregar OnClickListener al layout principal
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        User2 user = getItem(position); // Obtener el usuario seleccionado
                        if (user != null) {
                            // Navegar al Activity de chat
                            navigateToChat(user);
                        }
                    }
                }
            });
        }

        private void navigateToChat(User2 user) {
            Context context = view.getContext();

            if (user.getIdUser() == null || user.getIdUser().isEmpty()) {
                Log.e("CHAT_DEBUG", "Error: El ID del usuario es nulo o vacío");
                Toast.makeText(context, "Error: No se pudo identificar al destinatario2", Toast.LENGTH_SHORT).show();
                return;
            }

            // Depuración
            Log.d("CHAT_DEBUG", "Iniciando chat con usuario: " + user.getUsername());
            Log.d("CHAT_DEBUG", "ID del usuario: " + user.getIdUser());

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("userId", user.getIdUser()); // Pasar el ID del usuario
            intent.putExtra("username", user.getUsername()); // Pasar el nombre del usuario
            context.startActivity(intent);
        }

    }
}