package com.example.cuecard;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FlashcardStorage {
    private static final String PREFS = "flashcards_prefs";
    private static final String KEY = "flashcards_key";

    public static ArrayList<Flashcard> load(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = sp.getString(KEY, null);
        if (json == null) return new ArrayList<>();
        Type listType = new TypeToken<ArrayList<Flashcard>>(){}.getType();
        return new Gson().fromJson(json, listType);
    }

    public static void save(Context ctx, ArrayList<Flashcard> list) {
        SharedPreferences sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putString(KEY, new Gson().toJson(list)).apply();
    }
}