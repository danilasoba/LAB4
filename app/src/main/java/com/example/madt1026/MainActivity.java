package com.example.madt1026;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int DELETE_NOTE_REQUEST_CODE = 1;
    ArrayList<String> listNoteItems = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView lvNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the listNoteItems
        this.listNoteItems = new ArrayList<>();

        // Find the ListView and set up the adapter
        this.lvNotes = findViewById(R.id.lvNotes);
        this.adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, this.listNoteItems);
        this.lvNotes.setAdapter(adapter);

        // Refresh the list when MainActivity is created
        updateListFromSharedPreferences();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notes_options_menu, menu);
        //inflater.inflate(R.menu.secondary_options_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Deprecated
        //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //Current
        //Location of file: /data/data/com.example.madt1026
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.NOTES_FILE, this.MODE_PRIVATE);
        String lastSavedNote = sharedPref.getString(Constants.NOTE_KEY, "NA");
        String lastSavedNoteDate = sharedPref.getString(Constants.NOTE_KEY_DATE, "1900-01-01");
        Set<String> savedSet = sharedPref.getStringSet(Constants.NOTES_ARRAY_KEY, null);

        if(savedSet != null) {
            this.listNoteItems.clear();
            this.listNoteItems.addAll(savedSet);
            this.adapter.notifyDataSetChanged();
        }

        Snackbar.make(lvNotes, String.format("%s: %s", getString(R.string.msg_last_saved_note), lastSavedNote), Snackbar.LENGTH_LONG).show();
        Toast.makeText(this, lastSavedNoteDate, Toast.LENGTH_LONG).show();

        //In case You will need to append/remove values from array:
        //https://stackoverflow.com/questions/9648236/android-listview-not-updating-after-a-call-to-notifydatasetchanged
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Refresh the list when returning to MainActivity
        updateListFromSharedPreferences();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DELETE_NOTE_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> modifiedNoteList = data.getStringArrayListExtra("modifiedNoteList");
            if (modifiedNoteList != null) {
                // Update the list in MainActivity with the modified list
                updateList(modifiedNoteList);
            } else {
                Log.e("MainActivity", "modifiedNoteList is null");
            }
        }
    }

    private void updateList(ArrayList<String> modifiedNoteList) {
        // Clear the existing listNoteItems and add all items from the modified list
        listNoteItems.clear();
        listNoteItems.addAll(modifiedNoteList);

        // Notify the adapter that the data set has changed
        adapter.notifyDataSetChanged();
    }



    private void updateListFromSharedPreferences() {
        // Retrieve the list from SharedPreferences
        SharedPreferences sharedPref = this.getSharedPreferences(Constants.NOTES_FILE, this.MODE_PRIVATE);
        Set<String> savedSet = sharedPref.getStringSet(Constants.NOTES_ARRAY_KEY, null);

        if (savedSet != null) {
            // Clear the existing listNoteItems and add all items from SharedPreferences
            listNoteItems.clear();
            listNoteItems.addAll(savedSet);

            // Notify the adapter that the data set has changed
            adapter.notifyDataSetChanged();

            // Set the adapter to the ListView (if not already set)
            if (lvNotes.getAdapter() == null) {
                lvNotes.setAdapter(adapter);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_note:
                Intent i = new Intent(this, AddNoteActivity.class);
                startActivity(i);
                return true;
            case R.id.remove_note:
                Intent deleteIntent = new Intent(this, DeleteNoteActivity.class);
                deleteIntent.putStringArrayListExtra("noteList", listNoteItems);
                startActivityForResult(deleteIntent, DELETE_NOTE_REQUEST_CODE); // Use DELETE_NOTE_REQUEST_CODE here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

