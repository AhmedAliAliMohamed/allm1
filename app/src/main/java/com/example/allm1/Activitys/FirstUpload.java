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
import android.view.View;
import android.widget.Toast;

import com.example.allm1.Models.ModelOfArabicAndEnglishPage;
import com.example.allm1.R;
import com.example.allm1.databinding.ActivityFirstUploadBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class FirstUpload extends AppCompatActivity {

    private ActivityFirstUploadBinding activityFirstUploadBinding;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private Bundle getVal;
    private String pageName = null;
    private String title = null;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private ModelOfArabicAndEnglishPage model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFirstUploadBinding = DataBindingUtil.setContentView(this, R.layout.activity_first_upload);
        intentTogallary();
        checkValueStringPage();
        uploadButtonFirstTitle();


    }


    // Intent For Get Iamge
    private void intentTogallary() {
        activityFirstUploadBinding.imgFirstUpload.setOnClickListener(new View.OnClickListener() {
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
                activityFirstUploadBinding.imgFirstUpload.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    //check String Value For Upload
    private void checkValueStringPage() {
        getVal = getIntent().getExtras();
        if (getVal.containsKey("arabic")) {
            pageName = getVal.getString("arabic");
            Toast.makeText(this, "" + pageName, Toast.LENGTH_SHORT).show();

        } else if (getVal.containsKey("english")) {
            pageName = getVal.getString("english");
            Toast.makeText(this, "" + pageName, Toast.LENGTH_SHORT).show();

        }


    }


    // Upload Over Firebase Storage and realtime
    private void uploadIamge() {
        title = activityFirstUploadBinding.txtFirstTitle.getEditText().getText().toString();
        if (filePath != null) {
            storageReference.child(pageName + "/").child(title + "/").child(title).putFile(filePath)

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadImage();
                            if (pageName.equals("عربي")) {
                                Toast.makeText(FirstUpload.this, "تم رفع الملف", Toast.LENGTH_SHORT).show();
                                Intent toArabicPage = new Intent(FirstUpload.this, ArabicHomePage.class);
                                startActivity(toArabicPage);
                                finish();
                            } else if (pageName.equals("English")) {
                                Toast.makeText(FirstUpload.this, "تم رفع الملف", Toast.LENGTH_SHORT).show();
                                Intent toEnglishPage = new Intent(FirstUpload.this, EnglishHomePage.class);
                                startActivity(toEnglishPage);
                                finish();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(FirstUpload.this, "لم يتم رفع الملف", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "برجاء اختيار صوره اولاً", Toast.LENGTH_SHORT).show();
        }
    }

    // Click Button For Upload
    private void uploadButtonFirstTitle() {
        activityFirstUploadBinding.btnUploadFirstTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityFirstUploadBinding.txtFirstTitle.getEditText().getText() == null) {


                }

                uploadIamge();


            }
        });


    }


    // Dowload Image Link
    private void downloadImage() {
        storageReference.child(pageName).child(title).child(title)
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
        if (title.equals("")) {
            activityFirstUploadBinding.txtFirstUpload.setError("لا يمكن تجاهل هذا الحقل");
        } else {
            model = new ModelOfArabicAndEnglishPage(title, fileImage);
            databaseReference.child(pageName).child(title).setValue(model);
        }
    }


}