package com.parmjeet.gurujisong;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



class CustomAdapter extends  RecyclerView.Adapter<CustomAdapter.MyViewHolder>   {
    public interface OnItemClickListener{
        void onItemClick(int position) throws IOException;
    }
    private OnItemClickListener mOnItemClickListener;


    //  songList songLists;
 public static  ArrayList<songList> soList;
    Context context;

    public CustomAdapter(Context context, ArrayList<songList> soList,OnItemClickListener listener) {
        this.context = context;
        mOnItemClickListener = listener;
        this.soList = new ArrayList<songList>(soList);
    }
    @NonNull
    @Override
    public CustomAdapter.MyViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        MyViewHolder  vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        songList message = (songList) soList.get(position);
        int repeat=message.getNo_of_time();
        String songName=message.getSonglastsegmant();
        if((songName.length())>60)
        {
            songName=songName.substring(0,50)+".mp3  ";
        }
   //     Toast.makeText(context,message.getUri()+"123412341234",Toast.LENGTH_SHORT).show();
        holder.name.setText(songName);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,ExoPlayer.class);
                 intent.putExtra("link", position);
                 intent.putExtra("arrray",soList);
                 context.startActivity(intent);
             //   context.startActivity(new Intent(context, ExoPlayer.class));

                // display a toast with person name on item click
       //         Toast.makeText(context, songlist.get(position), Toast.LENGTH_SHORT).show();
            }
        });
      //  holder.imageViewIcon.setImageResource(getImageId(context));
        holder.imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mOnItemClickListener.onItemClick(position);
     //        Toast.makeText(context, "adapter work", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return soList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        ImageButton imageViewIcon;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.songs);
            imageViewIcon=(ImageButton) itemView.findViewById(R.id.imageView);
        }
    }


}
