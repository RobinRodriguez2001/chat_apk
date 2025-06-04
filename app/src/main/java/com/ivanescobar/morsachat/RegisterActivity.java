package com.ivanescobar.morsachat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;

    TextInputEditText mTextInputUserName;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    TextInputEditText mTextInputConfirmPassword;

    TextInputEditText mTextInputPhone;

    Button mButtonRegister;

    FirebaseAuth mAuth;

    FirebaseFirestore mFirestore;

    private LoadingDialog loadingDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Inicializar el LoadingDialog
        loadingDialog = new LoadingDialog(this);

        mCircleImageViewBack = findViewById(R.id.circleImageBack);

        mTextInputEmail = findViewById(R.id.textInputEmail);

        mTextInputUserName = findViewById(R.id.textInputUserName);

        mTextInputPassword = findViewById(R.id.textInputPassword);

        mTextInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);

        mTextInputPhone = findViewById(R.id.textInputPhone);

        mButtonRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        mFirestore = FirebaseFirestore.getInstance();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void register(){
        String username = mTextInputUserName.getText().toString();
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();
        String confirmPassword = mTextInputConfirmPassword.getText().toString();
        String phone = mTextInputPhone.getText().toString();



        if(!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !phone.isEmpty()){
            if (isEmailValid(email)){
                if(password.equals(confirmPassword)){
                    if(password.length() >=6){
                        createUser(username, email, password, phone);
                    }else{
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, "Insertaste todos los campos y el email es válido", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Insertaste todos los campos pero el email es inv alido", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "Has insertado todos los campos", Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(this, "Para continuar inserta todos los campos", Toast.LENGTH_SHORT).show();

        }
    }


    private void createUser(String username, final String email, final String password, final String phone) {
        // Mostrar el cuadro de carga
        loadingDialog.startLoadingAlertDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = mAuth.getCurrentUser().getUid(); // Obtener el ID del usuario
                    Map<String, Object> map = new HashMap<>();
                    map.put("idUser", id); // Guardar el ID del usuario como un campo
                    map.put("email", email);
                    map.put("username", username);
                    map.put("password", password); // Opcional: No es recomendable almacenar contraseñas en Firestore
                    map.put("phone", phone);
                    map.put("timestamp", new Date().getTime());

                    mFirestore.collection("Users").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Ocultar el cuadro de carga cuando se complete la autenticación
                            loadingDialog.dismissDialog();

                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "¡El usuario se creó correctamente!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                // Limpiar pantalla
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterActivity.this, "No se pudo almacenar el usuario en la base de datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Ocultar el cuadro de carga cuando se complete la autenticación
                    loadingDialog.dismissDialog();
                    Toast.makeText(RegisterActivity.this, "No fue posible registrar al usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




//    Verificar si el email es valido
    public static boolean isEmailValid(String email){
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}