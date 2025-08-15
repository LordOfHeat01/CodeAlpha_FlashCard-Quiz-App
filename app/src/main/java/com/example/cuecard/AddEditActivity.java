package com.example.cuecard;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cuecard.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddEditActivity extends AppCompatActivity {

    private EditText etQuestion, etAnswer;
    private Button btnSave, btnCancel;
    private boolean isEdit;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        Intent i = getIntent();
        isEdit = i.getBooleanExtra("isEdit", false);
        if (isEdit) {
            etQuestion.setText(i.getStringExtra("question"));
            etAnswer.setText(i.getStringExtra("answer"));
            position = i.getIntExtra("position", -1);
        }

        btnSave.setOnClickListener(v -> {
            String q = etQuestion.getText().toString().trim();
            String a = etAnswer.getText().toString().trim();

            if (q.isEmpty() || a.isEmpty()) {
                Toast.makeText(this, "Please enter both question and answer", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent resultIntent = new Intent();
            resultIntent.putExtra("question", q);
            resultIntent.putExtra("answer", a);
            resultIntent.putExtra("isEdit", isEdit);
            resultIntent.putExtra("position", position);

            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}

