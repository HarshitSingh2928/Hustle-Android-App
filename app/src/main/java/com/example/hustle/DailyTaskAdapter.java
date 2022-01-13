package com.example.hustle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DailyTaskAdapter extends RecyclerView.Adapter<DailyTaskAdapter.ViewHolder> {

    private static final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();
    //ArrayList<TaskModelClass> taskModelClassArrayList;
    ArrayList<String> taskModelClassArrayList;
    Spannable spannables;
    Context context;
    //onItemClick onItemClick;


    public DailyTaskAdapter(Context context,ArrayList<String> taskModelClassArrayList){//onItemClick onItemClick) {
        this.context = context;
        this.taskModelClassArrayList=taskModelClassArrayList;
        //this.onItemClick=onItemClick;
    }

    @NonNull
    @Override
    public DailyTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.task_layout,parent,false);
        return new DailyTaskAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyTaskAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            //TaskModelClass taskModelClass =taskModelClassArrayList.get(position);
            holder.itemView.setLongClickable(true);
            //holder.textView.setText(taskModelClass.getTask(),TextView.BufferType.SPANNABLE);
                holder.textView.setText(taskModelClassArrayList.get(position),TextView.BufferType.SPANNABLE);

//                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        onItemClick.onClick(position,v);
//                        return true;
//                    }
//                });
                holder.checkBox.setOnCheckedChangeListener(null);
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    spannables= (Spannable) holder.textView.getText();
                    if(holder.checkBox.isChecked()){
                        holder.textView.setTextColor(Color.parseColor("#808080"));
                        //spannables.setSpan(STRIKE_THROUGH_SPAN, 0, taskModelClass.getTask().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spannables.setSpan(STRIKE_THROUGH_SPAN, 0, taskModelClassArrayList.get(position).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if(!holder.checkBox.isChecked()){
                        holder.textView.setTextColor(Color.parseColor("#000000"));
                        //holder.textView.setText(taskModelClass.getTask(),TextView.BufferType.SPANNABLE);
                        holder.textView.setText(taskModelClassArrayList.get(position),TextView.BufferType.SPANNABLE);
                    }

                        //onItemClick.onClick(position);

                    }
                });


    }

    @Override
    public int getItemCount() {
        if(taskModelClassArrayList==null){
            return 0;
        }
        return taskModelClassArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        CheckBox checkBox;
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox=itemView.findViewById(R.id.task_layout_check);
            textView=itemView.findViewById(R.id.task_layout_text);

            itemView.setOnClickListener(this::onClick);
            itemView.setOnLongClickListener(this::onLongClick);
        }

        @Override
        public void onClick(View v) {

        }

        boolean onLongClick(View v){
            //Toast.makeText(context,taskModelClassArrayList.get(getAdapterPosition()).getTask(),Toast.LENGTH_LONG).show();
            return  true;
        }
    }

//    public interface onItemClick{
//        public void onClick(int position,View v);
//    }
}
