package com.app.chatx;




import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
    ArrayList<Messages> list;
    private String currentUid;
    public ChatAdapter(ArrayList<Messages> list,String currentUid){
        this.list=list;
        this.currentUid=currentUid;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg=itemView.findViewById(R.id.messageText);
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
        holder.msg.setText(msg.message);
        if(msg.senderId.equals(currentUid)){
            holder.msg.setBackgroundResource(R.drawable.sender_bubble);
            ((LinearLayout) holder.msg.getParent()) .setGravity(Gravity.END);
        }
        else{
            holder.msg.setBackgroundResource(R.drawable.reciever_bubble);
            ((LinearLayout) holder.msg.getParent()).setGravity(Gravity.START);
        }
    }
    @Override
    public int getItemCount(){
        return list.size();
    }
}