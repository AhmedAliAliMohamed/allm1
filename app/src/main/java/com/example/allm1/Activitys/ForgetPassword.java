package com.example.allm1.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.allm1.R;
import com.example.allm1.databinding.ActivityForgetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    private ActivityForgetPasswordBinding activityForgetPasswordBinding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityForgetPasswordBinding = DataBindingUtil.setContentView(this,R.layout.activity_forget_password);
        forgGetPassowrd();



    }

    private void forgGetPassowrd(){
        activityForgetPasswordBinding.btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.sendPasswordResetEmail(activityForgetPasswordBinding.txtForgetPassword.getEditText().getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    dialogs();

                                }
                            }
                        });
            }
        });
    }

    private void dialogs(){
        final AlertDialog dialog = new AlertDialog.Builder(this)
                 .setTitle("Forget Password")
                .setMessage("Check your E-Mail Please")
                .setPositiveButton("OK", null)
                .show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(ForgetPassword.this, Login.class);
                startActivity(toLogin);

            }
        });
    }
}