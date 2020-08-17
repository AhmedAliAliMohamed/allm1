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

import static android.content.Context.MODE_PRIVATE;


public class AdapterForArabicHome extends RecyclerView.Adapter<AdapterForArabicHome.AdapterArabicHomeHolder> {
    private Context contextAdapter;
    private List<ModelOfArabicAndEnglishPage> modelOfArabicAndEnglishPages1;
    private onItemClickListener mListener;
    private String removePart = null;
    private  int position;
    private String nameOfPage = "عربي";
    private SharedPreferences sharedPreferences ;
    private static final String SHARED_PREFS = "sharedprefe";
    private static final String ADMIN = "Admin";

    // InterFace For onClickItem
    public interface onItemClickListener {
        void onItemClick(int position);
    }

    // Method setOnClick
    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    // Constructor
    public AdapterForArabicHome(Context context, List<ModelOfArabicAndEnglishPage> modelOfArabicAndEnglishPages,SharedPreferences sharedPreferences1) {
        contextAdapter = context;
        modelOfArabicAndEnglishPages1 = modelOfArabicAndEnglishPages;
        sharedPreferences =sharedPreferences1;
    }


    @NonNull
    @Override
    public AdapterArabicHomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_arabic_and_english, parent, false);
        return new AdapterArabicHomeHolder(view, mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterArabicHomeHolder holder, int position) {
        ModelOfArabicAndEnglishPage model = modelOfArabicAndEnglishPages1.get(position);
        Picasso.with(contextAdapter).load(model.getImageOfAll()).into(holder.imageForArabicPage);
        holder.nameOftitel.setText(model.getNameOfTitle());
    }


    @Override
    public int getItemCount() {
        return modelOfArabicAndEnglishPages1.size();
    }


    // Iner Class For Holder Item View
    public class AdapterArabicHomeHolder extends RecyclerView.ViewHolder {
        private ImageView imageForArabicPage;
        private TextView nameOftitel;
        private FloatingActionButton fb;

        // Method For Holder
        public AdapterArabicHomeHolder(@NonNull final View itemView, final onItemClickListener listener) {
            super(itemView);
            imageForArabicPage = itemView.findViewById(R.id.image_item_arabic_english);
            nameOftitel = itemView.findViewById(R.id.text_arabic_english_item);
            fb = itemView.findViewById(R.id.fab_to_remove_item);
            if (sharedPreferences.getString(ADMIN,"").isEmpty()){
                fb.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                       position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            Intent toInerPages = new Intent(itemView.getContext(), InerPages.class);
                            toInerPages.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            toInerPages.putExtra("titles", modelOfArabicAndEnglishPages1.get(getAdapterPosition()).getNameOfTitle());
                            toInerPages.putExtra("nameOfPageArabic",nameOfPage);
                            itemView.getContext().startActivity(toInerPages);
                        }
                    }
                }
            });
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePart = modelOfArabicAndEnglishPages1.get(getAdapterPosition()).getNameOfTitle();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("عربي");
                    reference.child(removePart).setValue(null);
                    removePhoto();


                }
            });
        }

    }

    // Remove Photo
    private void removePhoto() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("عربي");
        reference.child(removePart).child(removePart).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }



}
