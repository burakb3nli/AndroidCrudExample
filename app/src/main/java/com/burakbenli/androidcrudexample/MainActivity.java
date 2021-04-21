package com.burakbenli.androidcrudexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    EditText inputAd,inputEmail;
    ListView lstData;
    ProgressBar circularProgressBar;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference listeVeri;

    private List<Kullanici> listKullanicilar=new ArrayList<>();
    private Kullanici seciliKullanici; //Listview'de bir kayıda tıkladığımda, kaydı tutacak.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.menuToolBar);
        toolbar.setTitle("Crud Example");
        setSupportActionBar(toolbar);

        inputAd=findViewById(R.id.name);
        inputEmail=findViewById(R.id.email);
        lstData=findViewById(R.id.listData);
        circularProgressBar=findViewById(R.id.progress_circular);

        // Firebase kodlarım
        FirebaseApp.initializeApp(this);
        firebaseDatabase=firebaseDatabase.getInstance();
        listeVeri=firebaseDatabase.getReference();

        //Progress - Görünürlüğünü ayarladım
        circularProgressBar.setVisibility(View.VISIBLE);
        lstData.setVisibility(View.INVISIBLE);

        lstData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Kullanici listedekiKullanici =(Kullanici)parent.getItemAtPosition(position);
                seciliKullanici=listedekiKullanici;
                inputAd.setText(listedekiKullanici.getAd());
                inputEmail.setText(listedekiKullanici.getEmail());
            }
        });

        //Firebase Listener
        listeVeri.child("Kullanicilar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listKullanicilar.size()>0)
                    listKullanicilar.clear();
                for (DataSnapshot postSnapshot:snapshot.getChildren())
                {
                Kullanici kullanici=postSnapshot.getValue(Kullanici.class);
                listKullanicilar.add(kullanici);
                }

                ListViewAdapter adapter = new ListViewAdapter(MainActivity.this,listKullanicilar);

                lstData.setAdapter(adapter);

                circularProgressBar.setVisibility(View.INVISIBLE);
                lstData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuEkle)
        {
            kullaniciEkle();
        }

        else if (item.getItemId() == R.id.menuGuncelle)
        {
            kullaniciGuncelle();
        }

        else if (item.getItemId() == R.id.menuSil)
        {
            kullaniciSil(seciliKullanici);
        }
        return true;
    }

    private void kullaniciSil(Kullanici seciliKullanici) {
        //Kayı silme kodlarım burada
        listeVeri.child("Kullanicilar").child(seciliKullanici.getUid()).removeValue();
        kontrolTemizle();
    }

    private void kullaniciGuncelle() {
        //Kayıt güncelleme kodlarım burada
        Kullanici kullanici=new Kullanici(seciliKullanici.getUid(),inputAd.getText().toString(),inputEmail.getText().toString());
        listeVeri.child("Kullanicilar").child(kullanici.getUid()).child("ad").setValue(kullanici.getAd());
        listeVeri.child("Kullanicilar").child(kullanici.getUid()).child("email").setValue(kullanici.getEmail());
        kontrolTemizle();
    }

    private void kullaniciEkle() {
        //Kullanıcı ekleme kodlarını buraya yazıyorum
        Kullanici kullanici = new Kullanici(UUID.randomUUID().toString(),
                inputAd.getText().toString(),
                inputEmail.getText().toString());
        //Veri tabanına buradan gönderiyorum.
        listeVeri.child("Kullanicilar").child(kullanici.getUid()).setValue(kullanici);
        kontrolTemizle();
    }

    private void kontrolTemizle() {
        inputAd.setText("");
        inputEmail.setText("");
    }
}