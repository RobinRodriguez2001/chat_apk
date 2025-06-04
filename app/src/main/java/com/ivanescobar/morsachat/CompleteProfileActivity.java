package com.ivanescobar.morsachat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ivanescobar.morsachat.R;
import com.ivanescobar.morsachat.models.User;

import java.util.HashMap;
import java.util.Map;



public class CompleteProfileActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    Button mButtonRegister;

    TextInputEditText mTextInputPhone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        mTextInputUsername = findViewById(R.id.textInputUsername);
        mButtonRegister = findViewById(R.id.btnRegister);

        mTextInputPhone = findViewById(R.id.textInputPhone);



        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {
        String username = mTextInputUsername.getText().toString();
        String phone = mTextInputPhone.getText().toString();

        if (!username.isEmpty()) {
            updateUser(username, phone);
        }
        else {
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUser(final String username, final String phone) {
//        String id = mAuthProvider.getUid();
        User user = new User();
        user.setUsername(username);
//        user.setId(id);
        //user.setPhone(phone);
        //uset.setTimestamp(new Date().getTime());
//        mDialog.show();
//        mUsersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//                if (task.isSuccessful()) {
//                    Intent intent = new Intent(CompleteProfileActivity.this, HomeActivity.class);
//                    startActivity(intent);
//                }
//                else {
//                    Toast.makeText(CompleteProfileActivity.this, "No se pudo almacenar el usuario en la base de datos", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

}
