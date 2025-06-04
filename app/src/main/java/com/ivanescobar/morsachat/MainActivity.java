package com.ivanescobar.morsachat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

//necesario para que funcione el boton registrar y redirija a RegisterAcivity
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ivanescobar.morsachat.R;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;






public class MainActivity extends AppCompatActivity {

//    definir varibale
    TextView mTextViewRegister;

    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;

    Button mButtonLogin;

    FirebaseAuth mAuth;

    //Google Sign in
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;


    private LoadingDialog loadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//    definir id de boton 'registrar'

        // Inicializar el LoadingDialog
        loadingDialog = new LoadingDialog(this);

        mTextViewRegister = findViewById(R.id.textViewRegister);

        mTextInputEmail = findViewById(R.id.textInputEmail);

        mTextInputPassword = findViewById(R.id.textInputPassword);

        mButtonLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();

        // Verificar si hay una sesión activa
        if (mAuth.getCurrentUser() != null) {
            // Si hay un usuario autenticado, redirigir a HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Cerrar MainActivity para que el usuario no pueda volver atrás
        }


        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


//        Realizar accion y redirigir
                mTextViewRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(intent);
                    }
                });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;


        });


    }


    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        // Mostrar el cuadro de carga
        loadingDialog.startLoadingAlertDialog();

        // Verificación de credenciales en Cloud Firestore
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                // Ocultar el cuadro de carga cuando se complete la autenticación
                loadingDialog.dismissDialog();

                if (task.isSuccessful()) {
                    // Obtener el UID del usuario actual
                    String userId = mAuth.getCurrentUser().getUid();
                    Log.d("USER_ID", "User ID: " + userId);

                    // Redirigir a HomeActivity y pasar el UID si es necesario
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("USER_ID", userId); // Pasar el UID a la siguiente actividad

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "El email o la contraseña no es correcta", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.d("CAMPO", "email: " + email);
        Log.d("CAMPO", "password: " + password);
    }


}