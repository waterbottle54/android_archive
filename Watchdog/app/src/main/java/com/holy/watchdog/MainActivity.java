package com.holy.watchdog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.holy.watchdog.adapters.CriminalAdapter;
import com.holy.watchdog.helpers.SQLiteHelper;
import com.holy.watchdog.models.Criminal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener, CriminalAdapter.OnItemClickListener, PopupMenu.OnMenuItemClickListener {

    // list and adapter of criminals
    private List<Criminal> mCriminalList;
    private CriminalAdapter mCriminalAdapter;

    // Position of selected criminal
    private int mSelectedCriminalPosition = -1;

    // Watch service
    private WatchService mWatcherService;
    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set button listener
        FloatingActionButton addCriminalButton = findViewById(R.id.btn_add_criminal);
        addCriminalButton.setOnClickListener(this);

        // Build criminal recycler view
        buildCriminalRecycler();
        updateCriminalRecycler();

        // Start watch service
        Intent serviceIntent = new Intent(this, WatchService.class);
        startForegroundService(serviceIntent);

        // Define connection with watch service
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                WatchService.LocalBinder binder = (WatchService.LocalBinder) service;
                mWatcherService = binder.getService();
                mWatcherService.setCriminalList(mCriminalList);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };
    }

    @Override
    protected void onDestroy() {
        /*
        // Stop watch service
        Intent serviceIntent = new Intent(this, WatchService.class);
        stopService(serviceIntent);
        */
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Bind with watch service
        Intent serviceIntent = new Intent(this, WatchService.class);
        bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {

        // Unbind with watch service
        Intent serviceIntent = new Intent(this, WatchService.class);
        unbindService(mConnection);

        super.onStop();
    }

    // Build criminal recycler view

    private void buildCriminalRecycler() {

        RecyclerView recyclerView = findViewById(R.id.recycler_criminal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mCriminalList = new ArrayList<>();
        mCriminalAdapter = new CriminalAdapter(mCriminalList);
        recyclerView.setAdapter(mCriminalAdapter);

        // Set item view click listener
        mCriminalAdapter.setOnItemClickListener(this);
    }

    // Update criminal recycler view based on SQLite DB

    private void updateCriminalRecycler() {

        mCriminalList.clear();
        mCriminalList.addAll(SQLiteHelper.getInstance(this).getAllCriminals());
        mCriminalAdapter.notifyDataSetChanged();
    }

    // Handle button click

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_add_criminal) {

            // Show dialog that adds criminal info
            showAddDialog();
        }
    }

    // Handle criminal item click

    @Override
    public void onItemClick(int position, View itemView) {

        PopupMenu popupMenu = new PopupMenu(this, itemView);
        popupMenu.inflate(R.menu.menu_edit);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();

        mSelectedCriminalPosition = position;
    }

    // Handle popup menu click

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_modify:
                // Show dialog that modifies criminal info
                showModifyDialog();
                break;
            case R.id.item_delete:
                // Show dialog that asks whether to delete criminal info
                askWhetherToDeleteCriminal();
                break;
        }

        return false;
    }

    // Show dialog that adds criminal info

    private void showAddDialog() {

        View addView = View.inflate(this, R.layout.view_edit_criminal, null);
        EditText nicknameEdit = addView.findViewById(R.id.edit_nickname);

        new AlertDialog.Builder(this)
                .setTitle(R.string.add)
                .setView(addView)
                .setPositiveButton(R.string.add, (dialog, which) -> {
                    // Get criminal info user entered
                    String strNickname = nicknameEdit.getText().toString().trim();
                    if (strNickname.isEmpty()) {
                        Toast.makeText(this,
                                "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Add new criminal to SQLite DB and update recycler view
                    Criminal newCriminal = new Criminal(strNickname);
                    SQLiteHelper.getInstance(MainActivity.this).addCriminal(newCriminal);
                    updateCriminalRecycler();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // Show dialog that modifies criminal info

    private void showModifyDialog() {

        View modifyView = View.inflate(this, R.layout.view_edit_criminal, null);
        EditText nicknameEdit = modifyView.findViewById(R.id.edit_nickname);

        Criminal criminal = mCriminalList.get(mSelectedCriminalPosition);
        nicknameEdit.setText(criminal.getNickname());

        new AlertDialog.Builder(this)
                .setTitle(R.string.modify)
                .setView(modifyView)
                .setPositiveButton(R.string.modify, (dialog, which) -> {
                    // Get criminal info user entered
                    String strNickname = nicknameEdit.getText().toString().trim();
                    if (strNickname.isEmpty()) {
                        Toast.makeText(this,
                                "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Update criminal in SQLite DB and update recycler view
                    Criminal newCriminal = new Criminal(strNickname);
                    SQLiteHelper.getInstance(MainActivity.this)
                            .updateCriminal(criminal.getId(), newCriminal);
                    updateCriminalRecycler();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // Show dialog that asks whether to delete criminal info

    private void askWhetherToDeleteCriminal() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage("범죄자 정보를 삭제하시겠습니까?")
                .setPositiveButton("네", (dialog, which) -> {
                    // Delete criminal from SQLite DB and update recycler view
                    int id = mCriminalList.get(mSelectedCriminalPosition).getId();
                    SQLiteHelper.getInstance(MainActivity.this).deleteCriminal(id);
                    updateCriminalRecycler();
                })
                .setNegativeButton("아니오", null)
                .show();
    }

}