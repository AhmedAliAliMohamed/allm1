package com.example.allm1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allm1.Activitys.InerPages;
import com.example.allm1.Activitys.LearnPage;
import com.example.allm1.Models.ModelOfInnerArabicAndEnglishPage;
import com.example.allm1.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterForInnerPages extends RecyclerView.Adapter<AdapterForInnerPages.AdapterForInnerPagesHolder> {

    private Context context;
    private String removePart = null;
    private List<ModelOfInnerArabicAndEnglishPage> model ;
    private setOnItemClickListener listener;
    private int position;
    private String adapterNameOfPage;
    private String adapterTitle;
    private String AdapterItem;

    private SharedPreferences sharedPreferences ;
    private static final String SHARED_PREFS = "sharedprefe";
    private static final String ADMIN = "Admin";

    public interface setOnItemClickListener{
         void onItemClickListener(int position);

    }

    public void setOnItemClickListener(setOnItemClickListener clickListener){
        listener = clickListener;

    }



    public AdapterForInnerPages(Context context1 , List<ModelOfInnerArabicAndEnglishPage> model1,String nameOfPages,String titles,SharedPreferences preferences) {
        this.context = context1;
        this.model = model1;
        this.sharedPreferences = preferences;
        this.adapterNameOfPage = nameOfPages;
        this.adapterTitle = titles;
    }

    @NonNull
    @Override
    public AdapterForInnerPagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_arabic_and_english,parent,false);
        return new AdapterForInnerPagesHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterForInnerPagesHolder holder, int position) {
        ModelOfInnerArabicAndEnglishPage innerArabicAndEnglishPage = model.get(position);
        Picasso.with(context).load(innerArabicAndEnglishPage.getImage()).into(holder.imageView);
        holder.textView.setText(innerArabicAndEnglishPage.getTitle());

    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class AdapterForInnerPagesHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private FloatingActionButton actionButton;
        public AdapterForInnerPagesHolder(@NonNull final View itemView, final setOnItemClickListener onItemClickListener) {
            super(itemView);
            imageView =itemView.findViewById(R.id.image_item_arabic_english);
            textView =itemView.findViewById(R.id.text_arabic_english_item);
            actionButton = itemView.findViewById(R.id.fab_to_remove_item);
            if (sharedPreferences.getString(ADMIN, "").isEmpty()) {
                actionButton.setVisibility(View.GONE);

            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener !=null){
                        position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClickListener(position);
                            Intent toLearnPage = new Intent(itemView.getContext(), LearnPage.class);

                            toLearnPage.putExtra("nameOfPage",adapterNameOfPage);
                            toLearnPage.putExtra("title",adapterTitle);
                            toLearnPage.putExtra("items", model.get(getAdapterPosition()).getTitle());
                            itemView.getContext().startActivity(toLearnPage);

                        }
                    }


                }
            });
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePart = model.get(getAdapterPosition()).getTitle();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(adapterNameOfPage);
                    reference.child(adapterTitle).child(adapterTitle).child(removePart).setValue(null);
                    removePhoto(removePart);


                }
            });

        }
    }
    // Remove Photo
    private void removePhoto(String names) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference(adapterNameOfPage);
        reference.child(adapterNameOfPage).child(adapterNameOfPage).child(names).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }
    }

