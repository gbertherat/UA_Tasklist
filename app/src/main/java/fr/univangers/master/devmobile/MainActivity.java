package fr.univangers.master.devmobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private TaskAdapter adapter;
    private ArrayList<TaskAdapter.TaskItem> taskList = new ArrayList<>();
    private ActivityResultLauncher<Intent> launchAddTaskActivity;
    private TaskDbHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new TaskDbHelper(getApplicationContext());
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

        // Création du Recycler View
        adapter = new TaskAdapter(this, helper);
        RecyclerView recyclerView = findViewById(R.id.taskList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Récupération de l'instance si elle existe
        if(savedInstanceState != null){
            for(Parcelable parcelable : savedInstanceState.getParcelableArrayList("savedList")){
                adapter.add((TaskAdapter.TaskItem) parcelable);
            }
        } else {
            adapter.getDataFromDatabase();
        }

        // Action de mouvement sur la Recycler View, suppression de l'entrée en swipant
        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = (int) viewHolder.itemView.getTag();
                adapter.remove(position);
            }
        };
        new ItemTouchHelper(touchHelperCallback).attachToRecyclerView(recyclerView);

        // Accès à la fenêtre de création d'une tâche
        launchAddTaskActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intentData = result.getData();
                        String label = intentData.getStringExtra("taskLabel");
                        String weight = intentData.getStringExtra("taskWeight");

                        if(label != null && weight != null) {
                            adapter.add(label, weight);
                        }
                    }
                });

        taskList = adapter.getData();
    }

    // Affichage du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Ajout de l'activité au menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addTaskMenu) {
            Intent intent = new Intent(this, AddTaskActivity.class);
            launchAddTaskActivity.launch(intent);
        } else if(item.getItemId() == R.id.settingsMenu){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return true;
    }

    // Sauvegarde de l'instance
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("savedList", taskList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(getString(R.string.displayPriorities))){
            boolean value = sharedPreferences.getBoolean(s, false);
            View view = findViewById(R.id.weightColoredSquare);
            view.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        } else if(s.equals(getString(R.string.policeSize))){
            int value = Integer.parseInt(sharedPreferences.getString(s, "12"));
            TextView view = findViewById(R.id.taskLabel);
            view.setTextSize(value);
        }

    }

    // Lorsque l'application est killed
    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}