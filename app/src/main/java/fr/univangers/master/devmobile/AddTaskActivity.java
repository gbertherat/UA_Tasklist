package fr.univangers.master.devmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        TextInputEditText input = findViewById(R.id.newTaskInput);
        RadioGroup group = findViewById(R.id.taskWeightRadioGroup);
        Button button = findViewById(R.id.addTaskButton);
        button.setOnClickListener(view -> {
            if(input.getText() != null && input.getText().length() == 0){
                return;
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("taskLabel", input.getText().toString());

            int radioId = group.getCheckedRadioButtonId();
            RadioButton rButton = findViewById(radioId);
            String value = rButton.getText().toString();

            String weight;
            switch(value){
                case "Moyenne":
                    weight = "2";
                    break;
                case "Haute":
                    weight = "1";
                    break;
                default:
                    weight = "3";
                    break;
            }

            intent.putExtra("taskWeight", weight);
            setResult(RESULT_OK, intent);
            finish();
        });
    }


}