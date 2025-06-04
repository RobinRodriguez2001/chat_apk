package com.ivanescobar.morsachat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.ivanescobar.morsachat.R;
import com.ivanescobar.morsachat.adapters.UsersAdapter;
import com.ivanescobar.morsachat.models.User;
import com.ivanescobar.morsachat.models.User2;
import com.ivanescobar.morsachat.providers.UsersProvider2;

public class ChatsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UsersAdapter mAdapter;
    private UsersProvider2 mUsersProvider2; // Cambia a UsersProvider

    public ChatsFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerViewChats);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsersProvider2 = new UsersProvider2(); // Usa UsersProvider
        loadUsers();

        return view;
    }

    private void loadUsers() {
        // Obtener todos los usuarios
        Query query = mUsersProvider2.getAllUsers();

        // Configurar opciones para el adaptador
        FirestoreRecyclerOptions<User2> options = new FirestoreRecyclerOptions.Builder<User2>()
                .setQuery(query, User2.class)
                .build();

        // Inicializar el adaptador
        mAdapter = new UsersAdapter(options, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
}