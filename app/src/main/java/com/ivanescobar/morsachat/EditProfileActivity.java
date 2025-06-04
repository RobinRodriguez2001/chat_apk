package com.ivanescobar.morsachat;

import static com.ivanescobar.morsachat.R.*;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Date;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.ivanescobar.morsachat.R;
import com.ivanescobar.morsachat.models.Post;
import com.ivanescobar.morsachat.models.User;
import com.ivanescobar.morsachat.providers.ImageProvider;
import com.ivanescobar.morsachat.providers.UsersProvider;
import com.ivanescobar.morsachat.utils.FileUtil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    CircleImageView mCircleImageViewBack;

    CircleImageView mCircleImageViewProfile;

    ImageView mImageViewCover;

    TextInputEditText mTextInputUsername;

    TextInputEditText mTextInputPhone;
    File mImageFile;
    File mImageFile2;

    String mUsername ="";

    ImageProvider mImageProvider;
    String mPhone = "";

    String mImageProfile = "";

    String mImageCover = "";

    ImageView mImageViewPost1;

    ImageView mImageViewPost2;

    private final int GALLERY_REQUEST_CODE_PROFILE = 1;

    private final int GALLERY_REQUEST_CODE_COVER = 2;

    private LoadingDialog loadingDialog;

    private FirebaseAuth mAuth;

    Button mButtonEditProfile;

    UsersProvider mUsersProvider;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Inicializar el LoadingDialog
        loadingDialog = new LoadingDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mImageProvider = new ImageProvider();

        mUsersProvider = new UsersProvider();

//        mImageViewPost1 = findViewById(R.id.imageViewPost1);
//
//        mImageViewPost2 = findViewById(R.id.imageViewPost2);

        mCircleImageViewBack = findViewById(R.id.circleImageBack);

        mCircleImageViewProfile = findViewById(R.id.circleImageProfile);

        mImageViewCover = findViewById(R.id.imageViewCover);

        mTextInputUsername = findViewById(R.id.textInputUserName);

        mTextInputPhone = findViewById(R.id.textInputPhone);

        mButtonEditProfile = findViewById(id.btnEditProfile);

        mButtonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEditProfile();
            }
        });

        mCircleImageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(GALLERY_REQUEST_CODE_PROFILE);
            }
        });

        mImageViewCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(GALLERY_REQUEST_CODE_COVER);
            }
        });

        mCircleImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUser();




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getUser() {

        String userId = mAuth.getCurrentUser().getUid();
        mUsersProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        mUsername = documentSnapshot.getString("username");
                        mTextInputUsername.setText(mUsername);
                    }
                    if(documentSnapshot.contains("phone")){
                        mPhone = documentSnapshot.getString("phone");
                        mTextInputPhone.setText(mPhone);
                    }
                    if(documentSnapshot.contains("image_profile")){
                        mImageProfile = documentSnapshot.getString("image_profile");
                        if(mImageProfile != null){
                            if(!mImageProfile.isEmpty()){
                                Picasso.get().load(mImageProfile).into(mCircleImageViewProfile);
                            }
                        }

                    }
                    if(documentSnapshot.contains("image_cover")){
                        mImageCover = documentSnapshot.getString("image_cover");
                        if(mImageCover != null){
                            if(!mImageCover.isEmpty()){
                                Picasso.get().load(mImageCover).into(mImageViewCover);
                            }

                        }

                    }

//                    String imageProfile = documentSnapshot.getString("image_profile");
//                    String imageCover = documentSnapshot.getString("image_cover");
                }
            }
        });
    }

    private void clickEditProfile() {
        mUsername = mTextInputUsername.getText().toString();
        mPhone = mTextInputPhone.getText().toString();
        if(!mUsername.isEmpty() && !mPhone.isEmpty()){
            clickPost();
        }else {
            Toast.makeText(this, "Ingrese el nombre de usuario y telefono", Toast.LENGTH_SHORT).show();
        }

    }

    private void clickPost() {
        mUsername = mTextInputUsername.getText().toString();
        mPhone = mTextInputPhone.getText().toString();
        if (!mUsername.isEmpty() && !mPhone.isEmpty()) {
            if (mImageFile != null && mImageFile2 != null) {
                // Obtener el UID del usuario actual
                String userId = mAuth.getCurrentUser().getUid();
                Log.d("POST_DEBUG", "User ID: " + userId); // Verificar el UID
                if (userId != null) {
                    saveImage(userId);
                } else {
                    Toast.makeText(this, "No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Debes seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Completa los campos restantes", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage(String userId) {

        loadingDialog.startLoadingAlertDialog();

        mImageProvider.save(EditProfileActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("POST_DEBUG", "Imagen subida correctamente"); // Verificar subida de imagen
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String urlProfile = uri.toString();
                            Log.d("POST_DEBUG", "URL de la imagen: " + urlProfile); // Verificar URL de la imagen

                            mImageProvider.save(EditProfileActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String urlCover = uri2.toString();


                                                User user = new User();
                                                user.setUsername(mUsername);
                                                user.setPhone(mPhone);
                                                user.setImageProfile(urlProfile);
                                                user.setImageCover(urlCover);
                                                //post.setIdUser(userId);
                                                user.setId(userId);

                                                mUsersProvider.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        if(task.isSuccessful()){
                                                            loadingDialog.dismissDialog();
                                                            Toast.makeText(EditProfileActivity.this, "la informacion se actualizo crrectamente", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            loadingDialog.dismissDialog();
                                                            Toast.makeText(EditProfileActivity.this, "La informacion no se pudo actualizar", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });





                                            }
                                        });
                                    }else{
                                        loadingDialog.dismissDialog();
                                        Toast.makeText(EditProfileActivity.this, "La Imagen 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                } else {

                    loadingDialog.dismissDialog();

                    Log.e("POST_DEBUG", "Error al subir la imagen: " + task.getException()); // Capturar error
                    Toast.makeText(EditProfileActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void openGallery(int requestCode){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK){
            try {
                mImageFile = FileUtil.from(this, data.getData());
                mCircleImageViewProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE_COVER && resultCode == RESULT_OK){
            try {
                mImageFile2 = FileUtil.from(this, data.getData());
                mImageViewCover.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR", "Se produjo un error " + e.getMessage());
                Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}