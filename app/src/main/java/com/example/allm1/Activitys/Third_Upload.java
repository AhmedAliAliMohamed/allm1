package com.example.allm1.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.allm1.Models.ModelOfLearnPage;
import com.example.allm1.R;
import com.example.allm1.databinding.ActivityThirdUploadBinding;
import com.google.android.gms.common.util.DataUtils;
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

public class Third_Upload extends AppCompatActivity {
    private ActivityThirdUploadBinding thirdUploadBinding;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private ModelOfLearnPage  modelOfLearnPage;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS = "sharedprefe";
    private static final String NAME_OF_PAGE = "nameOfPage1";
    private static final String TITLE = "title1";
    private static final String ITEM = "items";
    private static final String LETTERS = "letters";
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    String names;
    String titles;
    String item;
    String letters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       thirdUploadBinding = DataBindingUtil.setContentView(this,R.layout.activity_third__upload);



        setSharedPreferences();


        goToGallery();
        clickButtonForUpload();
        clickButtonForFinish();
    }



    private void goToGallery(){
        thirdUploadBinding.imgThirdUpload.setOnClickListener(new View.OnClickListener() {
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
                thirdUploadBinding.imgThirdUpload.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }



    private void uploadImages(String names , String titles , String item ){
        if (filePath != null) {
            letters = thirdUploadBinding.txtThirdTitle.getEditText().getText().toString();
            storageReference =FirebaseStorage.getInstance().getReference(names);
            storageReference.child(titles).child(titles).child(item).child(item).child(letters).child(letters).putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dowLoadImage();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Third_Upload.this, "File not uploaded Check for internet presence", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }



    private void dowLoadImage(){
        storageReference =FirebaseStorage.getInstance().getReference(names);
        storageReference.child(titles).child(titles).child(item).child(item).child(letters).child(letters)
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
                Toast.makeText(Third_Upload.this, "The file has not been downloaded. Please check the network quality", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void uploadOnRealTime(final String imageUrl){
        reference =FirebaseDatabase.getInstance().getReference(names);
        modelOfLearnPage =  new ModelOfLearnPage(imageUrl,letters);
        reference.child(titles).child(titles).child(item).child(item).child(letters).setValue(modelOfLearnPage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    private void clickButtonForUpload(){
        thirdUploadBinding.btnUploadThirdTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath == null || thirdUploadBinding.txtThirdTitle.equals("")) {
                    Toast.makeText(Third_Upload.this, "برجاء اختيار الصورة", Toast.LENGTH_SHORT).show();
                    thirdUploadBinding.txtThirdTitle.setError("لايمكن تجاهل هذا الحقل");
                } else {
                    uploadImages(names,titles,item);
                    if (names.equals("عربي")) {
                        Toast.makeText(Third_Upload.this, "تم رفع الملف", Toast.LENGTH_SHORT).show();
                        thirdUploadBinding.imgThirdUpload.setImageResource(R.drawable.image_for_upload);
                        thirdUploadBinding.txtThirdTitle.getEditText().setText("");

                    } else if (names.equals("English")) {
                        Toast.makeText(Third_Upload.this, "تم رفع الملف", Toast.LENGTH_SHORT).show();
                        thirdUploadBinding.imgThirdUpload.setImageResource(0);
                        thirdUploadBinding.txtThirdTitle.getEditText().setText("");

                    }

                }

            }
        });


    }



    private void clickButtonForFinish(){
        thirdUploadBinding.btnUploadThirdTitleFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSharedPreference();
                onBackPressed();

            }
        });

    }


    private void setSharedPreferences(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Bundle bundle = getIntent().getExtras();
        names = bundle.getString("names");
        titles = bundle.getString("title");
        item = bundle.getString("item");
        Toast.makeText(this, ""+names+"  "+titles+"  "+item, Toast.LENGTH_SHORT).show();
        editor.putString(NAME_OF_PAGE, names);
        editor.putString(TITLE, titles);
        editor.putString(ITEM,item);
        editor.putString(LETTERS,letters);
        editor.apply();

    }


    private void getSharedPreference(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        names = sharedPreferences.getString(NAME_OF_PAGE, "");
        titles = sharedPreferences.getString(TITLE, "");
        item = sharedPreferences.getString(ITEM,"");
        letters =sharedPreferences.getString(LETTERS,"");
    }


}