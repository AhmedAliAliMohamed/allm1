package com.example.allm1.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;


import com.example.allm1.Adapters.AdapterForInnerPages;
import com.example.allm1.Adapters.AdapterForLearnPage;
import com.example.allm1.Models.ModelOfInnerArabicAndEnglishPage;
import com.example.allm1.Models.ModelOfLearnPage;
import com.example.allm1.R;
import com.example.allm1.databinding.ActivityLearnPageBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class LearnPage extends AppCompatActivity {
    private ActivityLearnPageBinding learnPageBinding;
    private FirebaseDatabase dataBase = FirebaseDatabase.getInstance();
    private SharedPreferences sharedPreferences ;
    private SharedPreferences.Editor editor;
    private static final String SHARED_PREFS = "sharedprefe";
    private static final String ADMIN = "Admin";
    String names;
    String titles;
    String item;
    Bundle bundle;
    private static final String NAME_OF_PAGE = "nameOfPage1";
    private static final String TITLE = "title1";
    private static final String ITEM = "items";
    private List<ModelOfLearnPage> modelOfLearnPageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        learnPageBinding = DataBindingUtil.setContentView(this, R.layout.activity_learn_page);
        checkStrings();
        buttonAction();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        learnPageBinding.RecLearnPage.setLayoutManager(layoutManager);
        sharedPreferences =getSharedPreferences(SHARED_PREFS ,MODE_PRIVATE);
        if (sharedPreferences.getString(ADMIN,"").isEmpty()){
            learnPageBinding.inerToUpload.setVisibility(View.GONE);

        }


    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        getSharedPreference();

    }
    private void buttonAction() {
        learnPageBinding.inerToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toThirdUpload = new Intent(LearnPage.this, Third_Upload.class);
                toThirdUpload.putExtra("names", names);
                toThirdUpload.putExtra("title", titles);
                toThirdUpload.putExtra("item", item);
                startActivity(toThirdUpload);
            }
        });
    }


    private void getData(final String nameOfPage, final String title) {
        DatabaseReference reference = dataBase.getReference(nameOfPage);
        reference.child(title).child(title).child(item).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelOfLearnPageList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot dss : ds.getChildren()) {
                        ModelOfLearnPage model = new ModelOfLearnPage();
                        model = dss.getValue(ModelOfLearnPage.class);
                        model.getModelOfLearnPages();
                        modelOfLearnPageList.add(model);
                    }


                }

                AdapterForLearnPage adapterForLearnPage = new AdapterForLearnPage(getApplicationContext(), modelOfLearnPageList);
                learnPageBinding.RecLearnPage.setAdapter(adapterForLearnPage);
                adapterForLearnPage.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void setSharedPreferences(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Bundle bundle = getIntent().getExtras();
        names = bundle.getString("nameOfPage");
        titles = bundle.getString("title");
        item = bundle.getString("items");
        Toast.makeText(this, ""+names+"  "+titles+"  "+item, Toast.LENGTH_SHORT).show();
        editor.putString(NAME_OF_PAGE, names);
        editor.putString(TITLE, titles);
        editor.putString(ITEM,item);
        editor.apply();
        getData(names,titles);

    }


    private void getSharedPreference(){
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        names = sharedPreferences.getString(NAME_OF_PAGE, "");
        titles = sharedPreferences.getString(TITLE, "");
        item = sharedPreferences.getString(ITEM,"");

    }


    private  void checkStrings(){
        bundle = getIntent().getExtras();
        names = bundle.getString("nameOfPage");
        titles = bundle.getString("title");
        item = bundle.getString("items");
        getData(names, titles);
        setSharedPreferences();
    }

}