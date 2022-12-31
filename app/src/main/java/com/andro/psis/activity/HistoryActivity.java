package com.andro.psis.activity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.andro.psis.R;
import com.andro.psis.model.HistoryModel;
import com.andro.psis.adapter.HistoryAdapter;
import com.andro.psis.database.DatabaseHelper;
import com.andro.psis.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity {

    protected Cursor cursor;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    SessionManager session;
    String id_book = "", tanding, tribun, kursi, riwayat, total;
    String email;
    TextView tvNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        tvNotFound = findViewById(R.id.noHistory);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        email = user.get(SessionManager.KEY_EMAIL);

        refreshList();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.tbHistory);
        toolbar.setTitle("Riwayat Pemesanan");
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

    public void refreshList() {
        final ArrayList<HistoryModel> hasil = new ArrayList<>();
        cursor = db.rawQuery("SELECT * FROM TB_PESAN, TB_HARGA WHERE TB_PESAN.id_book = TB_HARGA.id_book AND username='" + email + "'", null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            id_book = cursor.getString(0);
            tanding = cursor.getString(1);
            tribun = cursor.getString(2);
            kursi = cursor.getString(3);
            total = cursor.getString(7);
            riwayat = "Berhasil melakukan pemesanan tiket pada tanggal " + tanding + " di tribun " + tribun + ". " +
                    "Jumlah pembelian tiket sejumlah " + kursi + ".";
            hasil.add(new HistoryModel(id_book, riwayat, total, R.drawable.profile));
        }

        ListView listBook = findViewById(R.id.list_booking);
        HistoryAdapter arrayAdapter = new HistoryAdapter(this, hasil);
        listBook.setAdapter(arrayAdapter);

        //delete data
        listBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String selection = hasil.get(i).getIdBook();
                final CharSequence[] dialogitem = {"Hapus Data"};
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setTitle("Pilihan");
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        try {
                            db.execSQL("DELETE FROM TB_PESAN where id_book = " + selection + "");
                            id_book = "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        refreshList();
                    }
                });
                builder.create().show();
            }
        });

        if (id_book.equals("")) {
            tvNotFound.setVisibility(View.VISIBLE);
            listBook.setVisibility(View.GONE);
        } else {
            tvNotFound.setVisibility(View.GONE);
            listBook.setVisibility(View.VISIBLE);
        }

    }
}
