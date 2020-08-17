package com.example.allm1.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allm1.Adapters.AdapterForInnerPages;
import com.example.allm1.Models.ModelOfInnerArabicAndEnglishPage;
import com.example.allm1.R;
import com.example.allm1.databinding.ActivityInerPagesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

public class InerPages extends AppCompatActivity {
    private ActivityInerPagesBinding inerPagesBinding;
    private String title;
    private String nameOfPage;
    private Bundle getVal;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DrawerLayout drawer;
    private List<ModelOfInnerArabicAndEnglishPage> model = new ArrayList<>();
    private AdapterForInnerPages adapterForInnerPages;

    private static final String SHARED_PREFS = "sharedprefe";
    private static final String NAME_OF_PAGE = "nameOfPage";
    private static final String TITLE = "title";
    private static final String ADMIN = "Admin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inerPagesBinding = DataBindingUtil.setContentView(this, R.layout.activity_iner_pages);
        inerPagesBinding.recyInerPage.setLayoutManager(new GridLayoutManager(this, 2));
        getVal = getIntent().getExtras();
        checkStringValue();
        showDrawer();
        buttonAction();
        getShaerdPreferences();
        logOut();










    }


    // Show Drawer
    private void showDrawer() {
        Toolbar toolbar = inerPagesBinding.drawerToolbar;
        setSupportActionBar(toolbar);
        drawer = inerPagesBinding.drawerLayout;

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        downloadImage();
        readName();


    }


    // Back pressed if show drawer
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //Dowmload Image User
    private void downloadImage() {
        storageReference.child("Users").child(mAuth.getCurrentUser().getUid())
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String imageURL = task.getResult().toString();
                    CircleImageView circleImageView = inerPagesBinding.navView.getHeaderView(0).findViewById(R.id.profile_image_Drawers);
                    Picasso.with(InerPages.this).load(imageURL).into(circleImageView);
                }

            }
        });
    }


    //Get Name
    private void readName() {

        final String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = dataBase.getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    TextView view = inerPagesBinding.navView.getHeaderView(0).findViewById(R.id.txt_nav_user_name);
                    view.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // BUTTON FOR SECOND UPLOAD
    private void buttonAction() {
        inerPagesBinding.inerToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSecondUpload = new Intent(InerPages.this, SecondUpload.class);
                toSecondUpload.putExtra("nameOfPage", nameOfPage);
                toSecondUpload.putExtra("interTitle", title);
                startActivity(toSecondUpload);
                finish();
            }
        });
    }


    //check String Value For Upload
    private void checkStringValue() {
        if (getVal != null) {
            if (getVal.containsKey("nameOfPageArabic")) {
                nameOfPage = getVal.getString("nameOfPageArabic");
                title = getIntent().getStringExtra("titles");

            } else if (getVal.containsKey("nameOfPageEnglish")) {
                nameOfPage = getVal.getString("nameOfPageEnglish");
                title = getIntent().getStringExtra("titles");

            }
            setSharedPreferences();
            getData(nameOfPage, title);


        }


    }

    //GET DATA FROM FIREBASE
    private void getData(final String nameOfPage, final String title) {

        DatabaseReference reference = dataBase.getReference(nameOfPage);
        reference.child(title).child(title).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                model.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelOfInnerArabicAndEnglishPage innerArabicAndEnglishPage = new ModelOfInnerArabicAndEnglishPage();
                    innerArabicAndEnglishPage.getModel();
                    innerArabicAndEnglishPage = ds.getValue(ModelOfInnerArabicAndEnglishPage.class);
                    model.add(innerArabicAndEnglishPage);
                }
                adapterForInnerPages = new AdapterForInnerPages(getApplicationContext(), model,nameOfPage,title,sharedPreferences);
                inerPagesBinding.recyInerPage.setAdapter(adapterForInnerPages);
                adapterForInnerPages.notifyDataSetChanged();
                adapterForInnerPages.setOnItemClickListener(new AdapterForInnerPages.setOnItemClickListener() {
                    @Override
                    public void onItemClickListener(int position) {

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
        editor = sharedPreferences.edit();
        if (sharedPreferences.getString(ADMIN, "").isEmpty()) {
            inerPagesBinding.inerToUpload.setVisibility(View.GONE);
            editor.putString(NAME_OF_PAGE, nameOfPage);
            editor.putString(TITLE, title);
            editor.apply();
        } else {
            editor.putString(NAME_OF_PAGE, nameOfPage);
            editor.putString(TITLE, title);
            editor.apply();
        }
    }

    //GET SHARED PREFERENCE
    private void getShaerdPreferences() {
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        nameOfPage = sharedPreferences.getString(NAME_OF_PAGE, "");
        title = sharedPreferences.getString(TITLE, "");
        getData(nameOfPage, title);

    }

    private void logOut(){
        inerPagesBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_logout:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                        Intent toLogin = new Intent(InerPages.this, Login.class);
                        startActivity(toLogin);
                }
                return true;
            }
        });
    }


}