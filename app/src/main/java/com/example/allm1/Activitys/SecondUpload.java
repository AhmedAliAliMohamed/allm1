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

import com.example.allm1.Models.ModelOfInnerArabicAndEnglishPage;
import com.example.allm1.R;
import com.example.allm1.databinding.ActivitySecondUploadBinding;
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

import javax.xml.transform.Result;

public class SecondUpload extends AppCompatActivity {
    private ActivitySecondUploadBinding secondUploadBinding;
    private String pageName ;
    private String title ;
    private Bundle bundleValue;
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    private String innerTitle;
    private ModelOfInnerArabicAndEnglishPage model1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        secondUploadBinding = DataBindingUtil.setContentView(this, R.layout.activity_second_upload);
        bundleValue = getIntent().getExtras();
        pageName = bundleValue.getString("nameOfPage");
        title = bundleValue.getString("interTitle");
        storageReference = FirebaseStorage.getInstance().getReference(pageName);
        databaseReference = FirebaseDatabase.getInstance().getReference(pageName);
        getPhotoFromGallery();
        clickButtonForUpload();


    }


    // Get Photo From Gallery
    private void getPhotoFromGallery() {
        secondUploadBinding.imgSecondUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.PREVIEW_SDK_INT < 19) {
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

    //Show Image In Screen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                secondUploadBinding.imgSecondUpload.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    //Upload Image
    private void uploadImage() {
        if (filePath != null) {
            innerTitle = secondUploadBinding.txtSecondTitle.getEditText().getText().toString();
            storageReference.child(title).child(title).child(innerTitle).child(innerTitle).putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            downloadImageUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SecondUpload.this, "لم يتم رفع الملف تحقق من وجود الانترنت", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    // Download Image Url
    private void downloadImageUrl() {
        storageReference.child(title).child(title).child(innerTitle).child(innerTitle)
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String imageUrl = task.getResult().toString();
                    uploadOnRealTime(imageUrl);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SecondUpload.this, "لم يتم تنزيل الملف برجاء التحقق من جودة الشبكة", Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Upload To Realtime
    private void uploadOnRealTime(final String imageUrl) {
        model1 = new ModelOfInnerArabicAndEnglishPage(imageUrl, innerTitle);
        databaseReference.child(title).child(title).child(innerTitle).setValue(model1).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {



            }
        });
    }

    //Click For Upload
    private void clickButtonForUpload() {
        secondUploadBinding.btnUploadFirstTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath == null || secondUploadBinding.txtSecondTitle.equals("")) {
                    Toast.makeText(SecondUpload.this, "برجاء اختيار الصورة", Toast.LENGTH_SHORT).show();
                    secondUploadBinding.txtSecondTitle.setError("لايمكن تجاهل هذا الحقل");
                } else {
                    uploadImage();
                    if (pageName.equals("عربي")) {
                        Toast.makeText(SecondUpload.this, "تم رفع الملف", Toast.LENGTH_SHORT).show();
                        Intent toArabicPage = new Intent(SecondUpload.this, InerPages.class);
                        startActivity(toArabicPage);
                        finish();

                    } else if (pageName.equals("English")) {
                        Toast.makeText(SecondUpload.this, "تم رفع الملف", Toast.LENGTH_SHORT).show();
                        Intent toEnglishPage = new Intent(SecondUpload.this, InerPages.class);
                        startActivity(toEnglishPage);
                        finish();


                    }



                }
            }
        });
    }


}