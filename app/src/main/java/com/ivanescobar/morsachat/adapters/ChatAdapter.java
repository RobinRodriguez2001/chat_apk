package com.ivanescobar.morsachat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ivanescobar.morsachat.R;
import com.ivanescobar.morsachat.models.Message; // Asegúrate de que esta importación sea correcta

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Constantes para los tipos de vista
    private static final int TYPE_SENT = 0;
    private static final int TYPE_RECEIVED = 1;

    private List<Message> mMessages;
    private String currentUserId; // El ID del usuario actual (el que está usando la app)

    public ChatAdapter(List<Message> messages, String currentUserId) {
        this.mMessages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        // Si el senderId del mensaje es igual al currentUserId, es un mensaje enviado por mí
        if (message.getSenderId().equals(currentUserId)) {
            return TYPE_SENT;
        } else {
            // Si no, es un mensaje recibido de otra persona
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_SENT) {
            // Inflar el layout para mensajes enviados
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            // Inflar el layout para mensajes recibidos
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessages.get(position);

        // Comprobar el tipo de ViewHolder para asignar el texto correctamente
        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).textViewMessage.setText(message.getMessage());
        } else if (holder instanceof ReceivedViewHolder) {
            ((ReceivedViewHolder) holder).textViewMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    // ViewHolder para mensajes enviados (alineados a la derecha)
    static class SentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        SentViewHolder(View view) {
            super(view);
            // Asegúrate de que el ID del TextView sea el mismo en item_message_sent.xml
            textViewMessage = view.findViewById(R.id.textViewMessage);
        }
    }

    // ViewHolder para mensajes recibidos (alineados a la izquierda)
    static class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        ReceivedViewHolder(View view) {
            super(view);
            // Asegúrate de que el ID del TextView sea el mismo en item_message_received.xml
            textViewMessage = view.findViewById(R.id.textViewMessage);
        }
    }
}