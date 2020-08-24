package com.example.allm1.Activitys;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.text.Editable;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.allm1.R;


import com.example.allm1.databinding.ActivityRegisterBinding;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class Register extends AppCompatActivity {
    private ActivityRegisterBinding registerBinding;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private String userId = null;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" + ".{6,20}");


    // On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        btnRegistration();
        intentForgetImage();


    }


    // onClick For Registration
    private void btnRegistration() {
        registerBinding.btnRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkImage();

            }
        });
    }


    // Register Firebase
    private void fireBaseRegistration(Editable email, Editable password) {
        registerBinding.btnRegist.setVisibility(View.GONE);
        registerBinding.progRegister.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            userId = task.getResult().getUser().getUid();
                            uploadIamge();
                            Intent toLogin = new Intent(Register.this, Login.class);
                            startActivity(toLogin);

                        } else {
                            Toast.makeText(Register.this, "Failed to register, please check the data", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        registerBinding.txtRegEmail1.setError("Please enter the information correctly");
                    }
                });

    }


    // Intent For Get Iamge
    private void intentForgetImage() {
        registerBinding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 19) {
                    Intent uploadImage = new Intent();
                    uploadImage.setType("image/*");
                    uploadImage.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(uploadImage, "Select Image from here..."), PICK_IMAGE_REQUEST);
                } else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                }

            }
        });
    }


    //Show Image on Screen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null)) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                registerBinding.profileImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    // Upload Over Firebase Storage and realtime
    private void uploadIamge() {
        if (filePath != null) {
            storageReference.child("Users").child(userId).putFile(filePath)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadImage();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Register.this, "Failed to register, please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    // Dowload Image Link
    private void downloadImage() {
        storageReference.child("Users").child(userId)
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String imageURL = task.getResult().toString();
                    uploadImageRealtime(imageURL);
                }

            }
        });
    }


    //Method For UploadImage and Name On Realtime
    private void uploadImageRealtime(final String fileImage) {
        reference.child("Users").child(userId).child(registerBinding.txtRegUserName1.getEditText().getText().toString()).setValue(fileImage);
    }


    // validateEmail
    private boolean validateEmail() {
        String emailInput = registerBinding.txtRegEmail1.getEditText().getText().toString();

        if (emailInput.isEmpty()) {
            registerBinding.txtRegEmail1.setError("This cannot be ignored for a field");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            registerBinding.txtRegEmail1.setError("Please enter the email correctly");
            return false;

        } else {

            registerBinding.txtRegEmail1.setError(null);
            return true;
        }

    }


    //validatePassword
    private boolean validatePassword() {
        String passwordInput = registerBinding.txtRegPassword1.getEditText().getText().toString();
        if (passwordInput.isEmpty()) {
            registerBinding.txtRegPassword1.setError("This cannot be ignored for a field");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            registerBinding.txtRegPassword1.setError("The total password you entered is less than 6");
            return false;

        }
        else {
            registerBinding.txtRegPassword1.setError(null);

            return true;
        }


    }


    // validateUser
    private boolean validateUser() {
        String userNameinput = registerBinding.txtRegUserName1.getEditText().getText().toString();

        if (userNameinput.isEmpty()) {
            registerBinding.txtRegUserName1.setError("This cannot be ignored for a field");
            return false;
        } else {
            registerBinding.txtRegUserName1.setError(null);
            return true;
        }

    }


    //checkImage
    private void checkImage() {
        if (filePath == null) {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
        } else {
            validateEmail();
            validatePassword();
            validateUser();
            try {
                fireBaseRegistration(registerBinding.txtRegEmail1.getEditText().getText(), registerBinding.txtRegPassword.getText());
            }
            catch (Exception e){
                Toast.makeText(this, "You cannot register before the rest of the fields are finished", Toast.LENGTH_SHORT).show();
            }

        }

    }




}



