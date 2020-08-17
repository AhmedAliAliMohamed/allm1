package com.example.allm1.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.example.allm1.Adapters.AdapterForEngliashHome;
import com.example.allm1.Models.ModelOfArabicAndEnglishPage;
import com.example.allm1.R;

import com.example.allm1.databinding.ActivityEnglishHomePageBinding;
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

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EnglishHomePage extends AppCompatActivity {

    private DrawerLayout drawer;
    private ActivityEnglishHomePageBinding activityEnglishHomePageBinding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private List<ModelOfArabicAndEnglishPage> modelOfEnglishPageList = new ArrayList<>();
    private AdapterForEngliashHome adapterForEngliashHome;
    private SharedPreferences sharedPreferences ;
    private static final String SHARED_PREFS = "sharedprefe";
    private static final String ADMIN = "Admin";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEnglishHomePageBinding = DataBindingUtil.setContentView(this, R.layout.activity_english_home_page);
        activityEnglishHomePageBinding.recyEnglishHomePage.setLayoutManager(new GridLayoutManager(this,2));
        showDrawer();
        actionButton();
        getData();
       setSharedPreferences();
       logOut();




    }


    // Show Drawer
    private void showDrawer() {
        Toolbar toolbar = activityEnglishHomePageBinding.drawerToolbar;
        setSupportActionBar(toolbar);
        drawer = activityEnglishHomePageBinding.drawerLayout;
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
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent homePage = new Intent(this, Home.class);
            startActivity(homePage);
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
                    CircleImageView circleImageView = activityEnglishHomePageBinding.navView.getHeaderView(0).findViewById(R.id.profile_image_Drawers);
                    Picasso.with(EnglishHomePage.this).load(imageURL).into(circleImageView);
                }

            }
        });
    }


    private void actionButton(){
        activityEnglishHomePageBinding.fabToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUpload = new Intent(EnglishHomePage.this,FirstUpload.class);
                Bundle englishPage = new Bundle();
                englishPage.putString("english","English");
                toUpload.putExtras(englishPage);
                startActivity(toUpload);
            }
        });
    }

    //Get Name
    private void readName() {

        final String uid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("UserName").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getValue(String.class);
                    TextView view = activityEnglishHomePageBinding.navView.getHeaderView(0).findViewById(R.id.txt_nav_user_name);
                    view.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Get Data
    private void getData(){

        DatabaseReference reference = database.getReference("English");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelOfEnglishPageList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelOfArabicAndEnglishPage model = ds.getValue(ModelOfArabicAndEnglishPage.class);
                    modelOfEnglishPageList.add(model);
                }
                adapterForEngliashHome = new AdapterForEngliashHome(getApplicationContext(),modelOfEnglishPageList,sharedPreferences);
                activityEnglishHomePageBinding.recyEnglishHomePage.setAdapter(adapterForEngliashHome);
                adapterForEngliashHome.notifyDataSetChanged();
                adapterForEngliashHome.setOnItemClickListener(new AdapterForEngliashHome.onItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    
    //SET SHARED PREFERENCE
    private void setSharedPreferences() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
       
        if (sharedPreferences.getString(ADMIN, "").isEmpty()) {
            activityEnglishHomePageBinding.fabToUpload.setVisibility(View.GONE);
           
        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    private void logOut(){
        activityEnglishHomePageBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_logout:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                        Intent toLogin = new Intent(EnglishHomePage.this, Login.class);
                        startActivity(toLogin);
                }
                return true;
            }
        });
    }

}
