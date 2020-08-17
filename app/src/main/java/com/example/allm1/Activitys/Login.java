    package com.example.allm1.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.allm1.R;


import com.example.allm1.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS = "sharedprefe";
    private static final String TEXT1 = "text1";
    private static final String TEXT2 = "text2";
    private static final String ADMIN = "Admin";
    private  String email;
    private  String password;

    private ActivityLoginBinding loginBinding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" + ".{6,20}");






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        login();
        gotoRegist();
        toForgGetPassword();

    }


    // onclick Login
    private void login() {
        loginBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    validatePassword();
                    validateEmail();


            }
        });

    }


    // validateEmail
    private boolean validateEmail() {
        String emailInput = loginBinding.txtEmail.getEditText().getText().toString();

        if (emailInput.isEmpty()) {
            loginBinding.txtEmail.setError("لا يمكن تجاهل هذا لحقل");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            loginBinding.txtEmail.setError("برجاء ادخل البريد بشكل صحيح");
            return false;

        } else {
            loginBinding.txtEmail.setError(null);
            return true;
        }


    }


    // validatePassword
    private boolean validatePassword() {
        String passwordInput = loginBinding.txtPassword.getEditText().getText().toString();

        if (passwordInput.isEmpty()) {
            loginBinding.txtPassword.setError("لا يمكن تجاهل هذا لحقل");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            loginBinding.txtPassword.setError("برجاء ادخل كلة السر بشكل صحيح");
            return false;

        } else {
            validateEmail();
            checkEmail();
            loginBinding.btnLogin.setVisibility(View.GONE);
            loginBinding.progLogin.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(loginBinding.txtEmail.getEditText().getText().toString(),
                    loginBinding.txtPassword.getEditText().getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                                rememberDialog();




                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loginBinding.btnLogin.setVisibility(View.VISIBLE);
                            loginBinding.progLogin.setVisibility(View.GONE);
                            loginBinding.txtPassword.setError("تحقق من كلمة السر");
                            Toast.makeText(Login.this, "برجاء التحقق من وجود الانترنت ", Toast.LENGTH_SHORT).show();

                        }
                    });

            loginBinding.txtEmail.setError(null);
            return true;
        }


    }


    // check Email
    private void checkEmail() {
        String checkEmail = loginBinding.txtEmail.getEditText().getText().toString().trim();
        auth.fetchSignInMethodsForEmail(checkEmail)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        try {
                            if (task.getResult().getSignInMethods().size() == 0) {
                                loginBinding.txtEmail.setError("هذا البريد غير موجود ");
                            } else {

                            }
                        } catch (Exception e) {

                        }

                    }
                });
    }



    // Go To Registration
    private void gotoRegist() {
        loginBinding.txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegistration = new Intent(Login.this, Register.class);
                startActivity(toRegistration);
            }
        });
    }



    private void toForgGetPassword(){
        loginBinding.txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toForget = new Intent(Login.this,ForgetPassword.class);
                startActivity(toForget);
            }
        });
    }








    //Remeber
    private void rememberDialog() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (sharedPreferences.getBoolean("Set", true)) {
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Remember")
                    .setMessage("Save your Account for login")
                    .setPositiveButton("OK", null)
                    .setNegativeButton("Cancel", null)
                    .show();
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email = loginBinding.txtEmail.getEditText().getText().toString();
                    password =loginBinding.txtPassword.getEditText().getText().toString();
                    editor = sharedPreferences.edit();
                    editor.putString(TEXT1, email);
                    editor.putString(TEXT2, password);
                    editor.putBoolean("Set", false);
                    editor.apply();
                    finish();

                    if (email.equals("Ahmed44@yahoo.com")) {
                        editor.putString(ADMIN, "Admin");
                        editor.apply();
                        finish();

                    }
                    Intent toHomepage = new Intent(Login.this, Home.class);
                    toHomepage.putExtra("Email",loginBinding.txtEmail.getEditText().getText().toString());
                    toHomepage.putExtra("Password",loginBinding.txtPassword.getEditText().getText().toString());
                    startActivity(toHomepage);
                    finish();
                    dialog.cancel();
                    dialog.dismiss();

                }
            });
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent toHomepage = new Intent(Login.this, Home.class);
                    toHomepage.putExtra("Email",loginBinding.txtEmail.getEditText().getText().toString());
                    toHomepage.putExtra("Password",loginBinding.txtPassword.getEditText().getText().toString());
                    startActivity(toHomepage);
                    finish();
                    dialog.dismiss();


                }
            });
        }
    }



}
