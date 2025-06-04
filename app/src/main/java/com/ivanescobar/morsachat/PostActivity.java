package com.ivanescobar.morsachat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.ivanescobar.morsachat.models.Post;
import com.ivanescobar.morsachat.providers.ImageProvider;
import com.ivanescobar.morsachat.providers.PostProvider;
import com.ivanescobar.morsachat.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {


    CircleImageView mCircleImageBack;
    PostProvider mPostProvider;
    ImageView mImageViewPost1;

    ImageView mImageViewPost2;
    File mImageFile;
    File mImageFile2;

    Button mButtonPost;

    ImageProvider mImageProvider;

    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;

    String mTitle = "";

    String mDescription = "";



    private final int GALLERY_REQUEST_CODE = 1;

    private final int GALLERY_REQUEST_CODE_2 = 2;


    private FirebaseAuth mAuth;

    private LoadingDialog loadingDialog;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);

        // Inicializar el LoadingDialog
        loadingDialog = new LoadingDialog(this);


        mAuth = FirebaseAuth.getInstance();

    mPostProvider = new PostProvider();

    mImageProvider = new ImageProvider();

    mCircleImageBack = findViewById(R.id.circleImageBack);

    mCircleImageBack.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });



    mImageViewPost1 = findViewById(R.id.imageViewPost1);

    mImageViewPost2 = findViewById(R.id.imageViewPost2);

    mButtonPost = findViewById(R.id.btnPost);

    mTextInputTitle = findViewById(R.id.textInputTitle);

    mTextInputDescription = findViewById(R.id.textInputDescription);

    mButtonPost.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickPost();
        }
    });

    mImageViewPost1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery(GALLERY_REQUEST_CODE);

        }
    });

    mImageViewPost2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openGallery(GALLERY_REQUEST_CODE_2);
        }
    });





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }





    private void clickPost() {
        mTitle = mTextInputTitle.getText().toString();
        mDescription = mTextInputDescription.getText().toString();
        if (!mTitle.isEmpty() && !mDescription.isEmpty()) {
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

        mImageProvider.save(PostActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("POST_DEBUG", "Imagen subida correctamente"); // Verificar subida de imagen
                    mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                           final String url = uri.toString();
                            Log.d("POST_DEBUG", "URL de la imagen: " + url); // Verificar URL de la imagen

                            mImageProvider.save(PostActivity.this, mImageFile2).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> taskImage2) {
                                    if(taskImage2.isSuccessful()){
                                        mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri2) {
                                                String url2 = uri2.toString();

                                                Post post = new Post();
                                                post.setImage1(url);
                                                post.setImage2(url2);
                                                post.setTitle(mTitle);
                                                post.setDescription(mDescription);
                                                post.setIdUser(userId);
                                                post.setTimestamp(new Date().getTime());

                                                mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> taskSave) {

                                                        loadingDialog.dismissDialog();

                                                        if (taskSave.isSuccessful()) {
                                                            clearForm();
                                                            Log.d("POST_DEBUG", "Publicación guardada en Firestore"); // Verificar guardado en Firestore
                                                            Toast.makeText(PostActivity.this, "La información se almacenó correctamente", Toast.LENGTH_LONG).show();
                                                        } else {

                                                            Log.e("POST_DEBUG", "Error al guardar en Firestore: " + taskSave.getException()); // Capturar error
                                                            Toast.makeText(PostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }else{
                                        loadingDialog.dismissDialog();
                                        Toast.makeText(PostActivity.this, "La Imagen 2 no se pudo guardar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                        }
                    });
                } else {

                    loadingDialog.dismissDialog();

                    Log.e("POST_DEBUG", "Error al subir la imagen: " + task.getException()); // Capturar error
                    Toast.makeText(PostActivity.this, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearForm() {
        mTextInputTitle.setText("");
        mTextInputDescription.setText("");
        mImageViewPost1.setImageResource(R.drawable.ic_imagen);
        mImageViewPost2.setImageResource(R.drawable.ic_imagen);
        mTitle = "";
        mDescription = "";
        mImageFile = null;
        mImageFile2 = null;
    }

    private void openGallery(int requestCode){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, requestCode);
    }


                @Override
                protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                    super.onActivityResult(requestCode, resultCode, data);
                    if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
                        try {
                            mImageFile = FileUtil.from(this, data.getData());
                            mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                        }catch (Exception e){
                            Log.d("ERROR", "Se produjo un error " + e.getMessage());
                            Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (requestCode == GALLERY_REQUEST_CODE_2 && resultCode == RESULT_OK){
                        try {
                            mImageFile2 = FileUtil.from(this, data.getData());
                            mImageViewPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
                        }catch (Exception e){
                            Log.d("ERROR", "Se produjo un error " + e.getMessage());
                            Toast.makeText(this, "Se produjo un error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }





//    private void openGallery(int requestCode) {
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        galleryLauncher.launch(galleryIntent);
//       // startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
//    }



