package com.example.meditationapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meditationapp.ModelClasses.NatureData;
import com.example.meditationapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NatureAdapter extends RecyclerView.Adapter<NatureAdapter.itemHolder> {
    Context context;
    List<NatureData> nature;

    public NatureAdapter(Context context, List<NatureData> nature) {
        this.context = context;
        this.nature = nature;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nature_recycleritem, parent, false);
        return new itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        Picasso.get().load(nature.get(position).getImages()).into(holder.image);
        Log.e("nature", String.valueOf(nature.size()));
    }

    @Override
    public int getItemCount() {
        return nature.size();
    }

    public class itemHolder extends RecyclerView.ViewHolder {

        AppCompatImageView image;

        public itemHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.nature_lib_drop);
        }
    }
}