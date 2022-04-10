package com.parmjeet.gurujisong;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class OflineAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
     Context context;
    private static final String TAG = "MyTag";

    ArrayList list =new ArrayList();
     OflineAdapter(Context context,ArrayList list){
         this.context=context;
         this.list=list;
     }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ofline_list, parent, false);
        CustomAdapter.MyViewHolder vh = new CustomAdapter.MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
      final String message = (String) list.get(position);

         String songName=message;
        if((songName.length())>90 && !songName.equals("YOU DID NOT DOWNLOAD ANY SHABAD STILL :FOR DOWNLOAD SHABAD CLICK ON CIRCLE ICON RIGHT SIDE OF EACH SHABAD IN ONLINE ACTIVITY"))
        {
            songName=songName.substring(0,55)+".mp3";
        }
        //     Toast.makeText(context,message.getUri()+"123412341234",Toast.LENGTH_SHORT).show();
            holder.name.setText(songName);

        if(!songName.equals("YOU DID NOT DOWNLOAD ANY SHABAD STILL :FOR DOWNLOAD SHABAD CLICK ON CIRCLE ICON RIGHT SIDE OF EACH SHABAD IN ONLINE ACTIVITY")) {
            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ExoPlayer.class);
                    intent.putExtra("posstion", position);
                    intent.putStringArrayListExtra("arrray", list);
                    //     Toast.makeText(context,list.size()+"   is size",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: size is @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + list.size());
                    context.startActivity(intent);
                    //   context.startActivity(new Intent(context, ExoPlayer.class));

                    // display a toast with person name on item click
                    //         Toast.makeText(context, songlist.get(position), Toast.LENGTH_SHORT).show();
                }
            });
        }
        holder.name.setLongClickable(true);
        holder.name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("DO YOU WANT TO REMOVE SONG FROM SAVE LIST ")
                        .setTitle("DELETE FILE");
                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        File file = new File(context.getFilesDir(), message);
                        file.delete();
                       // holder.name.setText("file remove");
                       list.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, list.size());
                        holder.itemView.setVisibility(View.GONE);

                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                  builder.show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;// init the item view's
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.songs);
        }
    }

}
