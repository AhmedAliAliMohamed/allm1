package com.example.allm1.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.allm1.Activitys.Home;
import com.example.allm1.R;
import com.example.allm1.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static java.lang.Thread.sleep;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding ;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS = "sharedprefe";
    private static final String TEXT1 = "text1";
    private static final String TEXT2 = "text2";
    private String getText1;
    private String getText2;
    private int progress = 0;
    private final int barMax = 30;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARED_PREFS ,MODE_PRIVATE);
        getText1 = sharedPreferences.getString(TEXT1,"");
        getText2 = sharedPreferences.getString(TEXT2,"");
        loadData();



    }


    private  void loadData(){
        mainBinding.progressBar.setMax(barMax);
        if (getText1.isEmpty()){
          new Thread(new Runnable() {
                   @Override
                   public void run() {
                       while (progress < barMax){
                           progress++;
                           android.os.SystemClock.sleep(60);
                           handler.post(new Runnable() {
                               @Override
                               public void run() {
                                   mainBinding.progressBar.setMax(progress);
                                   Intent intent = new Intent(MainActivity.this ,Login.class);
                                   startActivity(intent);
                               }
                           });
                       }

                   }
        }).start();}
        else {checkLogin();}



    }


    private void checkLogin(){
            auth.signInWithEmailAndPassword(getText1,getText2)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent = new Intent(MainActivity.this ,Home.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                        }
                    });


    }






}
