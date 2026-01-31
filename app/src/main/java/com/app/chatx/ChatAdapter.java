package com.app.chatx;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    ArrayList<Messages> list;
    private String currentUid;
    Context context;
    public ChatAdapter(ArrayList<Messages> list,String currentUid){
        this.list=list;
        this.currentUid=currentUid;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView msg;
        ImageView imageMessage, videoIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg=itemView.findViewById(R.id.messageText);
            imageMessage=itemView.findViewById(R.id.imageMessage);
            videoIcon=itemView.findViewById(R.id.videoIcon);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Messages msg=list.get(position);
        holder.msg.setVisibility(View.GONE);
        holder.imageMessage.setVisibility(View.GONE);
        holder.videoIcon.setVisibility(View.GONE);
        holder.msg.setText(msg.message);
        LinearLayout layout=(LinearLayout) holder.itemView;

        if(msg.senderId.equals(currentUid)){
            holder.msg.setBackgroundResource(R.drawable.sender_bubble);
            ((LinearLayout) holder.msg.getParent()) .setGravity(Gravity.END);
        }
        else{
            holder.msg.setBackgroundResource(R.drawable.reciever_bubble);
            ((LinearLayout) holder.msg.getParent()).setGravity(Gravity.START);
        }
        if("text".equals(msg.type)){
            holder.msg.setVisibility(View.VISIBLE);
            holder.msg.setText(msg.message);
        }
        else if("image".equals(msg.type)){
            holder.imageMessage.setVisibility(View.VISIBLE);
            Glide.with(context).load(msg.mediaUrl).into(holder.imageMessage);

        } else if ("video".equals(msg.type)) {
            holder.imageMessage.setVisibility(View.VISIBLE);
            holder.videoIcon.setVisibility(View.VISIBLE);
            Glide.with(context).load(msg.mediaUrl).into(holder.imageMessage);
            holder.imageMessage.setOnClickListener(v->{
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(msg.mediaUrl),"video/*");
                context.startActivity(intent);
            });

        }
    }
    @Override
    public int getItemCount(){
        return list.size();
    }
}