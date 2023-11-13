package com.example.madt1026;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class AddNoteActivity extends AppCompatActivity {

    EditText edNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        this.edNote = findViewById(R.id.edNote);
    }

    // About SharedPreferences: https://developer.android.com/training/data-storage/shared-preferences
    public void onBtnSaveAndCloseClick(View view) {
        String noteToAdd = this.edNote.getText().toString().trim();

        // Check if the entered text is empty
        if (noteToAdd.isEmpty()) {
            // Show a Toast warning
            Toast.makeText(this, "Please enter a non-empty note", Toast.LENGTH_SHORT).show();
            return; // Stop further execution if the text is empty
        }

        Log.d("AddNoteActivity", "Note to add: " + noteToAdd);

        Date c = Calendar.getInstance().getTime();
        Log.d("AddNoteActivity", "Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        Log.d("AddNoteActivity", "Formatted date: " + formattedDate);

        // Deprecated
        // SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // Current
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.NOTES_FILE, this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Set<String> savedSet = sharedPref.getStringSet(Constants.NOTES_ARRAY_KEY, null);
        Set<String> newSet = new HashSet<>();
        if (savedSet != null) {
            newSet.addAll(savedSet);
        }
        newSet.add(noteToAdd);

        editor.putString(Constants.NOTE_KEY, noteToAdd);
        editor.putString(Constants.NOTE_KEY_DATE, formattedDate);
        editor.putStringSet(Constants.NOTES_ARRAY_KEY, newSet);
        editor.apply();

        Log.d("AddNoteActivity", "Note saved successfully");
        finish();
    }

}