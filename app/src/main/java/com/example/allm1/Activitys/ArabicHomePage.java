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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.allm1.Adapters.AdapterForArabicHome;
import com.example.allm1.Models.ModelOfArabicAndEnglishPage;
import com.example.allm1.R;
import com.example.allm1.databinding.ActivityArabicHomePageBinding;
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

public class ArabicHomePage extends AppCompatActivity  {

    private DrawerLayout drawer;
    private ActivityArabicHomePageBinding activityArabicHomePageBinding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private List<ModelOfArabicAndEnglishPage> modelOfArabicAndEnglishPageList = new ArrayList<>();
    private String nameOfPage = "عربي";
    private AdapterForArabicHome adapterArabicHomeHolder;
    private SharedPreferences sharedPreferences ;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS = "sharedprefe";
    private static final String ADMIN = "Admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityArabicHomePageBinding = DataBindingUtil.setContentView(this, R.layout.activity_arabic_home_page);
        sharedPreferences =getSharedPreferences(SHARED_PREFS ,MODE_PRIVATE);
        activityArabicHomePageBinding.recyArabicHomePage.setLayoutManager(new GridLayoutManager(this, 2));
        Bundle bundle = new Bundle();
        bundle.putString("inner",nameOfPage);
        showDrawer();
        actionButton();
        getItemforArabicPage();
        logOut();

        if (sharedPreferences.getString(ADMIN,"").isEmpty()){
            activityArabicHomePageBinding.fabToUpload.setVisibility(View.GONE);

        }





    }


    // Show Drawer
    private void showDrawer() {
        Toolbar toolbar = activityArabicHomePageBinding.drawerToolbar;
        setSupportActionBar(toolbar);
        drawer = activityArabicHomePageBinding.drawerLayout;

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


    //Dowmload Image User
    private void downloadImage() {
        storageReference.child("Users").child(mAuth.getCurrentUser().getUid())
                .getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String imageURL = task.getResult().toString();
                    CircleImageView circleImageView = activityArabicHomePageBinding.navView.getHeaderView(0).findViewById(R.id.profile_image_Drawers);
                    Picasso.with(ArabicHomePage.this).load(imageURL).into(circleImageView);
                }

            }
        });
    }

    // Add Item For Arabic Page
    private void actionButton() {
        activityArabicHomePageBinding.fabToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUpload = new Intent(ArabicHomePage.this, FirstUpload.class);
                Bundle arabicPage = new Bundle();
                arabicPage.putString("arabic", nameOfPage);
                toUpload.putExtras(arabicPage);
                startActivity(toUpload);


            }
        });
    }


    //Get Name
    private void readName() {

        final String uid = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = database.getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.getKey();
                    TextView view = activityArabicHomePageBinding.navView.getHeaderView(0).findViewById(R.id.txt_nav_user_name);
                    view.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // Get Data for page
    private void getItemforArabicPage() {
        DatabaseReference reference = database.getReference("عربي");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelOfArabicAndEnglishPageList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelOfArabicAndEnglishPage model = ds.getValue(ModelOfArabicAndEnglishPage.class);
                    modelOfArabicAndEnglishPageList.add(model);
                }
                adapterArabicHomeHolder = new AdapterForArabicHome(getApplicationContext(), modelOfArabicAndEnglishPageList,sharedPreferences);
                activityArabicHomePageBinding.recyArabicHomePage.setAdapter(adapterArabicHomeHolder);
                adapterArabicHomeHolder.notifyDataSetChanged();
                adapterArabicHomeHolder.setOnItemClickListener(new AdapterForArabicHome.onItemClickListener() {
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



    private void logOut (){
        activityArabicHomePageBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_logout:
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        finish();
                        Intent toLogin = new Intent(ArabicHomePage.this, Login.class);
                        startActivity(toLogin);
                }


                return true;
            }
        });
    }


}
