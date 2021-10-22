package fr.univangers.master.devmobile;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
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

    public ArrayList<TaskItem> getData() {
        return data;
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
        holder.itemView.setTag(position);
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

    static class TaskItem implements Parcelable {
        String label;
        String weight;

        public TaskItem(){}

        protected TaskItem(Parcel in) {
            label = in.readString();
            weight = in.readString();
        }

        public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
            @Override
            public TaskItem createFromParcel(Parcel in) {
                return new TaskItem(in);
            }

            @Override
            public TaskItem[] newArray(int size) {
                return new TaskItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(label);
            parcel.writeString(weight);
        }
    }

    public void add(String label, String weight){
        TaskItem item = new TaskItem();
        item.label = label;
        item.weight = weight;
        data.add(item);
        notifyItemInserted(data.size()-1);
    }

    public void add(TaskItem item){
        data.add(item);
        notifyItemInserted(data.size()-1);
    }

    public void remove(int position){
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }
}
