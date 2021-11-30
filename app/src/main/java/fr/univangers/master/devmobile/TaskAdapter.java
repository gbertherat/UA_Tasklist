package fr.univangers.master.devmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.Task_ViewHolder>{
    private Context context;
    private TaskDbHelper helper;
    private ArrayList<TaskItem> data;

    public TaskAdapter(Context context, TaskDbHelper helper){
        this.context = context;
        this.helper = helper;
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

    public void getDataFromDatabase(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] projection = {
                TaskContract.TaskEntry._ID,
                TaskContract.TaskEntry.COLUMN_WEIGHT,
                TaskContract.TaskEntry.COLUMN_LABEL,
        };

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()){
            TaskItem item = new TaskItem();
            item.id = cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID));
            item.weight = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_WEIGHT)));
            item.label = cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_LABEL));
            add(item);
        }
        cursor.close();
    }

    // Item déjà créé = Pas besoin d'ajouter en BDD (possède déjà un ID)
    public void add(TaskItem item){
        data.add(item);
        notifyItemInserted(data.size()-1);
    }

    // Item sans ID, donc on le créé en BDD.
    public void add(String label, String weight){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_WEIGHT, weight);
        values.put(TaskContract.TaskEntry.COLUMN_LABEL, label);
        long id = db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
        db.close();

        TaskItem item = new TaskItem();
        item.label = label;
        item.weight = weight;
        item.id = (int) id;

        data.add(item);
        notifyItemInserted(data.size()-1);
    }

    // Suppression de l'item de la liste et en BDD en utilisant son ID.
    public void remove(int position){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE_NAME, TaskContract.TaskEntry._ID + "=?", new String[]{String.valueOf(data.get(position).id)});
        db.close();
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class Task_ViewHolder extends RecyclerView.ViewHolder{
        private final TextView taskLabel;
        private final View taskWeight;

        public Task_ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskLabel = itemView.findViewById(R.id.taskLabel);
            taskWeight = itemView.findViewById(R.id.weightColoredSquare);
        }
    }

    public static class TaskItem implements Parcelable {
        int id;
        String label;
        String weight;

        public TaskItem(){}

        protected TaskItem(Parcel in) {
            id = in.readInt();
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
            parcel.writeInt(id);
            parcel.writeString(label);
            parcel.writeString(weight);
        }
    }
}
