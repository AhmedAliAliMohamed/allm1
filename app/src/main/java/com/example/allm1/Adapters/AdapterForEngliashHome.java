package com.example.allm1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allm1.Activitys.InerPages;
import com.example.allm1.Models.ModelOfArabicAndEnglishPage;
import com.example.allm1.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterForEngliashHome extends RecyclerView.Adapter<AdapterForEngliashHome.AdapterEnglishHomeHolder> {
     private Context contextAdapter;
     private   List<ModelOfArabicAndEnglishPage> modelOfEnglishPage;
     private onItemClickListener mListener;
     private int position;
     private String removePart = null;
     private String nameOfPage ="English";
    private SharedPreferences sharedPreferences ;
    private static final String ADMIN = "Admin";


    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener =listener;

    }

    public AdapterForEngliashHome(Context context, List<ModelOfArabicAndEnglishPage> modelOfArabicAndEnglishPages,SharedPreferences preferences) {
        contextAdapter = context;
        modelOfEnglishPage = modelOfArabicAndEnglishPages;
        this.sharedPreferences =preferences;
    }

    @NonNull
    @Override
    public AdapterEnglishHomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_arabic_and_english,parent, false);
        return new AdapterEnglishHomeHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEnglishHomeHolder holder, int position) {
        ModelOfArabicAndEnglishPage model = modelOfEnglishPage.get(position);
        Picasso.with(contextAdapter).load(model.getImageOfAll()).into(holder.imageForEnglishPage);
        holder.nameOfTitle.setText(model.getNameOfTitle());



    }


    @Override
    public int getItemCount() {
        return modelOfEnglishPage.size();
    }



    public class AdapterEnglishHomeHolder extends RecyclerView.ViewHolder {
        private ImageView imageForEnglishPage;
        private TextView nameOfTitle;
        private FloatingActionButton fb;

        public AdapterEnglishHomeHolder(@NonNull final View itemView , final onItemClickListener listener) {
            super(itemView);
            imageForEnglishPage = itemView.findViewById(R.id.image_item_arabic_english);
            nameOfTitle = itemView.findViewById(R.id.text_arabic_english_item);
            fb = itemView.findViewById(R.id.fab_to_remove_item);
            if (sharedPreferences.getString(ADMIN,"").isEmpty()){
                fb.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                            Intent toInerPages = new Intent(itemView.getContext(), InerPages.class);
                            toInerPages.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            toInerPages.putExtra("titles", modelOfEnglishPage.get(getAdapterPosition()).getNameOfTitle());
                            toInerPages.putExtra("nameOfPageEnglish",nameOfPage);
                            itemView.getContext().startActivity(toInerPages);
                        }
                    }

                }
            });
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePart = modelOfEnglishPage.get(getAdapterPosition()).getNameOfTitle();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("English");
                    reference.child(removePart).setValue(null);
                    removePhoto();
                }
            });
        }
    }

    // Remove Photo
    private void removePhoto() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("English");
        reference.child(removePart).child(removePart).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Ahmed", "YESSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");


            }
        });
    }
}
