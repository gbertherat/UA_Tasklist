package fr.univangers.master.devmobile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.Task_ViewHolder> {
    private Context context;
    private ArrayList<TaskItem> data;

    public TaskAdapter(Context context){
        this.context= context;
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public Task_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.line, parent, false);
        return new Task_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Task_ViewHolder holder, int position) {
        TaskItem item = data.get(position);
        holder.taskLabel.setText(item.label);
        switch(item.weight){
            case "2":
                holder.taskWeight.setBackgroundColor(Color.parseColor("#80FFAA00"));
                break;
            case "1":
                holder.taskWeight.setBackgroundColor(Color.parseColor("#80FF0000"));
                break;
            default:
                holder.taskWeight.setBackgroundColor(Color.parseColor("#8000FF19"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class Task_ViewHolder extends RecyclerView.ViewHolder{
        private TextView taskLabel;
        private View taskWeight;

        public Task_ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskLabel = itemView.findViewById(R.id.taskLabel);
            taskWeight = itemView.findViewById(R.id.weightColoredSquare);
        }
    }

    static class TaskItem{
        String label;
        String weight;
    }

    public void add(String label, String weight){
        TaskItem item = new TaskItem();
        item.label = label;
        item.weight = weight;
        data.add(item);
        notifyItemInserted(data.size()-1);
    }
}
