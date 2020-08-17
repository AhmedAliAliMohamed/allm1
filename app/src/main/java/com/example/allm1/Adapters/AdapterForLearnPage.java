package com.example.allm1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allm1.Models.ModelOfLearnPage;
import com.example.allm1.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterForLearnPage extends RecyclerView.Adapter<AdapterForLearnPage.AdapterForLearnPages> {

    private Context context;
    private List<ModelOfLearnPage>learnPagesList;


    public AdapterForLearnPage(Context context, List<ModelOfLearnPage> learnPages) {
        this.context = context;
        this.learnPagesList = learnPages;
    }


    @NonNull
    @Override
    public AdapterForLearnPage.AdapterForLearnPages onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_for_learnp_page,parent,false);
        return new AdapterForLearnPages(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterForLearnPage.AdapterForLearnPages holder, int position) {
        ModelOfLearnPage modelOfLearnPage = learnPagesList.get(position);
        Picasso.with(context).load(modelOfLearnPage.getImageForLearn()).into(holder.imageView);
        holder.textView.setText(modelOfLearnPage.getTextsForLearn());

    }

    @Override
    public int getItemCount() {
        return learnPagesList.size() ;
    }

    public class AdapterForLearnPages extends RecyclerView.ViewHolder {
        ImageView imageView ;
        TextView textView ;
        public AdapterForLearnPages(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_for_learn_page_items);
            textView = itemView.findViewById(R.id.txt_letters);
        }
    }
}
