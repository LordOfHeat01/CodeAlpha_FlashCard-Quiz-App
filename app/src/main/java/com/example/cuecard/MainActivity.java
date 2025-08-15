package com.example.cuecard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.cuecard.R;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvQuestion, tvAnswer, tvProgress;
    private Button btnShowAnswer, btnAdd, btnEdit, btnDelete;
    private ImageButton btnPrev, btnNext;

    private ArrayList<Flashcard> cards;
    private int index = 0;

    // Launcher to start Add/Edit activity and receive result
    private ActivityResultLauncher<Intent> addEditLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvAnswer = findViewById(R.id.tvAnswer);
        tvProgress = findViewById(R.id.tvProgress);

        btnShowAnswer = findViewById(R.id.btnShowAnswer);
        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);

        cards = FlashcardStorage.load(this);

        // If empty, add some sample cards (so app doesn't look empty)
        if (cards.isEmpty()) {
            cards.add(new Flashcard("What is the capital of France?", "Paris"));
            cards.add(new Flashcard("Who wrote 'Hamlet'?", "William Shakespeare"));
            cards.add(new Flashcard("What is 2 + 2?", "4"));
            FlashcardStorage.save(this, cards);
        }

        // Activity result launcher
        addEditLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean isEdit = result.getData().getBooleanExtra("isEdit", false);
                        String q = result.getData().getStringExtra("question");
                        String a = result.getData().getStringExtra("answer");
                        int pos = result.getData().getIntExtra("position", -1);

                        if (isEdit && pos >= 0) {
                            // update
                            cards.get(pos).setQuestion(q);
                            cards.get(pos).setAnswer(a);
                            index = pos;
                            Toast.makeText(this, "Card updated", Toast.LENGTH_SHORT).show();
                        } else {
                            // add
                            cards.add(new Flashcard(q, a));
                            index = cards.size() - 1;
                            Toast.makeText(this, "Card added", Toast.LENGTH_SHORT).show();
                        }
                        FlashcardStorage.save(this, cards);
                        showCard();
                    }
                }
        );

        btnShowAnswer.setOnClickListener(v -> toggleAnswer());

        btnPrev.setOnClickListener(v -> {
            if (cards.isEmpty()) return;
            index = (index - 1 + cards.size()) % cards.size();
            showCard();
        });

        btnNext.setOnClickListener(v -> {
            if (cards.isEmpty()) return;
            index = (index + 1) % cards.size();
            showCard();
        });

        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddEditActivity.class);
            i.putExtra("isEdit", false);
            addEditLauncher.launch(i);
        });

        btnEdit.setOnClickListener(v -> {
            if (cards.isEmpty()) return;
            Intent i = new Intent(this, AddEditActivity.class);
            i.putExtra("isEdit", true);
            i.putExtra("question", cards.get(index).getQuestion());
            i.putExtra("answer", cards.get(index).getAnswer());
            i.putExtra("position", index);
            addEditLauncher.launch(i);
        });

        btnDelete.setOnClickListener(v -> {
            if (cards.isEmpty()) return;
            new AlertDialog.Builder(this)
                    .setTitle("Delete card")
                    .setMessage("Are you sure you want to delete this card?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        cards.remove(index);
                        if (cards.isEmpty()) {
                            index = 0;
                        } else {
                            index = index % cards.size();
                        }
                        FlashcardStorage.save(this, cards);
                        showCard();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        showCard();
    }

    private void toggleAnswer() {
        if (tvAnswer.getVisibility() == View.VISIBLE) {
            tvAnswer.setVisibility(View.GONE);
            btnShowAnswer.setText("Show Answer");
        } else {
            tvAnswer.setVisibility(View.VISIBLE);
            btnShowAnswer.setText("Hide Answer");
        }
    }

    private void showCard() {
        if (cards.isEmpty()) {
            tvQuestion.setText("No cards. Add one!");
            tvAnswer.setText("");
            tvAnswer.setVisibility(View.GONE);
            tvProgress.setText("0/0");
            return;
        }
        Flashcard c = cards.get(index);
        tvQuestion.setText(c.getQuestion());
        tvAnswer.setText(c.getAnswer());
        tvAnswer.setVisibility(View.GONE);
        btnShowAnswer.setText("Show Answer");
        tvProgress.setText((index + 1) + "/" + cards.size());
    }
}