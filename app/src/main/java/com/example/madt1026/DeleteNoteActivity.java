package com.example.madt1026;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.madt1026.Constants;
import com.example.madt1026.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeleteNoteActivity extends AppCompatActivity {

    private ArrayList<String> listNoteItems;
    private ArrayAdapter<String> adapter;
    private ListView lvNotes;
    private Button btnSaveChanges;
    private ArrayList<String> notesToDelete = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_node);

        listNoteItems = getIntent().getStringArrayListExtra("noteList");

        // Ensure listNoteItems is not null, initialize if necessary
        if (listNoteItems == null) {
            listNoteItems = new ArrayList<>();
        }

        lvNotes = findViewById(R.id.lvNotesToDelete);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, listNoteItems);
        lvNotes.setAdapter(adapter);
        lvNotes.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toggleSelection(position);
            }
        });

        // Initialize btnSaveChanges
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Check for null before setting click listener
        if (btnSaveChanges != null) {
            btnSaveChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveChanges();
                }
            });
        } else {
            Log.e("DeleteNoteActivity", "btnSaveChanges is null");
        }
    }

    private void toggleSelection(int position) {
        String selectedNote = listNoteItems.get(position);
        if (notesToDelete.contains(selectedNote)) {
            notesToDelete.remove(selectedNote);
        } else {
            notesToDelete.add(selectedNote);
        }
    }

    private void saveChanges() {
        // Create a new list with the notes you want to keep
        ArrayList<String> modifiedNoteList = new ArrayList<>(listNoteItems);

        // Remove deleted notes from modifiedNoteList based on your logic
        for (String noteToDelete : notesToDelete) {
            modifiedNoteList.remove(noteToDelete);
        }

        // Save the modified list back to SharedPreferences
        saveListToSharedPreferences(modifiedNoteList);

        // Finish the activity
        finish();
    }

    private void saveListToSharedPreferences(ArrayList<String> noteList) {
        // Current
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.NOTES_FILE, this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        Set<String> newSet = new HashSet<>(noteList);
        editor.putStringSet(Constants.NOTES_ARRAY_KEY, newSet);
        editor.apply();

        Log.d("DeleteNoteActivity", "List saved successfully");
    }
}
