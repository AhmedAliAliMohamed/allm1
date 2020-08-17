package com.example.allm1.Activitys;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;


import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.allm1.R;


import com.example.allm1.databinding.ActivityHomeBinding;


import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Home extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawer;
    private ActivityHomeBinding activityHomeBinding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS = "sharedprefe";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        activityHomeBinding.cardItemRecylArabic.setOnClickListener(this);
        activityHomeBinding.cardItemRecylEnglish.setOnClickListener(this);
        showDrawer();

        logOut();



    }

    // Show Drawer
    private void showDrawer() {
        Toolbar toolbar = activityHomeBinding.drawerToolbar;
        setSupportActionBar(toolbar);
        drawer = activityHomeBinding.drawerLayout;

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        downloadImage();
        readName();


    }


    // Backpressed if show drawer
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
            Intent homPhone = new Intent(Intent.ACTION_MAIN);
            homPhone.addCategory(Intent.CATEGORY_HOME);
            homPhone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homPhone);
        }
    }


    // Intent for pages
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.card_item_recyl_arabic) {
            Intent toArabicIntent = new Intent(this, ArabicHomePage.class);
            startActivity(toArabicIntent);

        } else if (v.getId() == R.id.card_item_recyl_english) {
            Intent toEnglishIntent = new Intent(this, EnglishHomePage.class);
            startActivity(toEnglishIntent);
        }
    }


    //Dowmload Image
    private void downloadImage() {
        storageReference.child("Users").child(mAuth.getCurrentUser().getUid())
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String imageURL = task.getResult().toString();
                    CircleImageView circleImageView = activityHomeBinding.navView.getHeaderView(0).findViewById(R.id.profile_image_Drawers);
                    Picasso.with(Home.this).load(imageURL).into(circleImageView);
                }

            }
        });
    }


    //Get Name
    private void readName() {

        final String uid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    TextView view = activityHomeBinding.navView.getHeaderView(0).findViewById(R.id.txt_nav_user_name);
                    view.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //LogOut
    private void logOut (){
        activityHomeBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_logout:
                        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                        Intent toLogin = new Intent(Home.this, Login.class);
                        startActivity(toLogin);
                }
                return true;
            }
        });
    }


}
