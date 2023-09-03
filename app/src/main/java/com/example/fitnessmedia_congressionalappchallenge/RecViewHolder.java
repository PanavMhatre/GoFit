package com.example.fitnessmedia_congressionalappchallenge;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profile;
    TextView name,email;
    Button button;

    public RecViewHolder(@NonNull View itemView) {
        super(itemView);

        profile = itemView.findViewById(R.id.profileImageRec);
        name = itemView.findViewById(R.id.nameRecView);
        email = itemView.findViewById(R.id.emailRecView);
        button = itemView.findViewById(R.id.addBtn);

    }
}
