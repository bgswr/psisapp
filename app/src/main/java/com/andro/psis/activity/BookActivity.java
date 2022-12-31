package com.andro.psis.activity;


import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.andro.psis.R;
import com.andro.psis.database.DatabaseHelper;
import com.andro.psis.session.SessionManager;

import java.util.HashMap;

public class BookActivity extends AppCompatActivity {

    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Spinner spinTanding, spinTribun, spinKursi;
    SessionManager session;
    String email;
    int id_book;
    public String sTanding, sTribun, sKursi;
    int jmlDewasa;
    int hargaDewasa;
    int hargaTotalDewasa, hargaTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        dbHelper = new DatabaseHelper(BookActivity.this);
        db = dbHelper.getReadableDatabase();

        final String[] tanding = {"20-01-23 v PERSIB", "28-01-23 v PERSEBAYA", "02-02-23 v DEWA UNITED", "10-02-23 v PERSIS", "23-02-23 v PERSITA"};
        final String[] tribun = {"Barat", "Timur", "Utara", "Selatan"};
        final String[] kursi = {"1", "2", "3", "4", "5"};

        spinTanding = findViewById(R.id.tanding);
        spinTribun = findViewById(R.id.tribun);
        spinKursi = findViewById(R.id.kursi);

        ArrayAdapter<CharSequence> adapterTanding = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, tanding);
        adapterTanding.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTanding.setAdapter(adapterTanding);

        ArrayAdapter<CharSequence> adapterTribun = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, tribun);
        adapterTribun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTribun.setAdapter(adapterTribun);

        ArrayAdapter<CharSequence> adapterKursi = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, kursi);
        adapterKursi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinKursi.setAdapter(adapterKursi);

        spinTanding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sTanding = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinTribun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sTribun = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinKursi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sKursi = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnBook = findViewById(R.id.book);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perhitunganHarga();
                {
                        AlertDialog dialog = new AlertDialog.Builder(BookActivity.this)
                                .setTitle("Ingin pesan sekarang?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            db.execSQL("INSERT INTO TB_PESAN (tanding, tribun, kursi) VALUES ('" +
                                                    sTanding + "','" +
                                                    sTribun + "','" +
                                                    sKursi + "');");
                                            cursor = db.rawQuery("SELECT id_book FROM TB_PESAN ORDER BY id_book DESC", null);
                                            cursor.moveToLast();
                                            if (cursor.getCount() > 0) {
                                                cursor.moveToPosition(0);
                                                id_book = cursor.getInt(0);
                                            }
                                            db.execSQL("INSERT INTO TB_HARGA (username, id_book, harga_dewasa, harga_total) VALUES ('" +
                                                    email + "','" +
                                                    id_book + "','" +
                                                    hargaTotalDewasa + "','" +
                                                    hargaTotal + "');");
                                            Toast.makeText(BookActivity.this, "Pemesanan berhasil", Toast.LENGTH_LONG).show();
                                            finish();
                                        } catch (Exception e) {
                                            Toast.makeText(BookActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                })
                                .setNegativeButton("Tidak", null)
                                .create();
                        dialog.show();
                    }
            }
        });

        setupToolbar();

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbKrl);
        toolbar.setTitle("Form Pemesanan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void perhitunganHarga() {
        if (sTanding.equalsIgnoreCase("20-01-23 v PERSIB") && sTribun.equalsIgnoreCase("Barat")) {
            hargaDewasa = 200000;
        } else if (sTanding.equalsIgnoreCase("20-01-23 v PERSIB") && sTribun.equalsIgnoreCase("Timur")) {
            hargaDewasa = 120000;
        } else if (sTanding.equalsIgnoreCase("20-01-23 v PERSIB") && sTribun.equalsIgnoreCase("Utara")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("20-01-23 v PERSIB") && sTribun.equalsIgnoreCase("Selatan")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("28-01-23 v PERSEBAYA") && sTribun.equalsIgnoreCase("Barat")) {
            hargaDewasa = 200000;
        } else if (sTanding.equalsIgnoreCase("28-01-23 v PERSEBAYA") && sTribun.equalsIgnoreCase("Timur")) {
            hargaDewasa = 120000;
        } else if (sTanding.equalsIgnoreCase("28-01-23 v PERSEBAYA") && sTribun.equalsIgnoreCase("Utara")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("28-01-23 v PERSEBAYA") && sTribun.equalsIgnoreCase("Selatan")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("23-02-23 v PERSITA") && sTribun.equalsIgnoreCase("Barat")) {
            hargaDewasa = 200000;
        } else if (sTanding.equalsIgnoreCase("23-02-23 v PERSITA") && sTribun.equalsIgnoreCase("Timur")) {
            hargaDewasa = 120000;
        } else if (sTanding.equalsIgnoreCase("23-02-23 v PERSITA") && sTribun.equalsIgnoreCase("Utara")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("23-02-23 v PERSITA") && sTribun.equalsIgnoreCase("Selatan")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("02-02-23 v DEWA UNITED") && sTribun.equalsIgnoreCase("Barat")) {
            hargaDewasa = 200000;
        } else if (sTanding.equalsIgnoreCase("02-02-23 v DEWA UNITED") && sTribun.equalsIgnoreCase("Timur")) {
            hargaDewasa = 120000;
        } else if (sTanding.equalsIgnoreCase("02-02-23 v DEWA UNITED") && sTribun.equalsIgnoreCase("Utara")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("02-02-23 v DEWA UNITED") && sTribun.equalsIgnoreCase("Selatan")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("10-02-23 v PERSIS") && sTribun.equalsIgnoreCase("Barat")) {
            hargaDewasa = 200000;
        } else if (sTanding.equalsIgnoreCase("10-02-23 v PERSIS") && sTribun.equalsIgnoreCase("Timur")) {
            hargaDewasa = 120000;
        } else if (sTanding.equalsIgnoreCase("10-02-23 v PERSIS") && sTribun.equalsIgnoreCase("Utara")) {
            hargaDewasa = 75000;
        } else if (sTanding.equalsIgnoreCase("10-02-23 v PERSIS") && sTribun.equalsIgnoreCase("Selatan")) {
            hargaDewasa = 75000;
        }

        jmlDewasa = Integer.parseInt(sKursi);

        hargaTotalDewasa = jmlDewasa * hargaDewasa;
        hargaTotal = hargaTotalDewasa;
    }

}