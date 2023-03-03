package id.go.kebumenkab.eletterkebumen.activity;

import android.content.Intent;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.helper.PrefManager;

public class ProfilActivity extends AppCompatActivity {

    private TextView nama;
    private TextView jabatan;
    private TextView instansi;

    private ImageView node2;

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

       // getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header_gradient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_profil));

        prefManager = new PrefManager(ProfilActivity.this);

        nama    = (TextView)findViewById(R.id.nama);
        jabatan = (TextView)findViewById(R.id.text_jabatan);
        instansi= (TextView)findViewById(R.id.text_instansi);
        node2 = (ImageView)findViewById(R.id.node_2);
        node2.setVisibility(View.INVISIBLE);
        instansi.setVisibility(View.INVISIBLE);

        nama.setText(prefManager.getSessionNama());
        jabatan.setText("Jabatan : "+ prefManager.getJabatan());
        if(prefManager.getIdPerangkat().length()==0){
            instansi.setText(prefManager.getSessionUnit());
            instansi.setVisibility(View.VISIBLE);
            node2.setVisibility(View.VISIBLE);
        }



    }

    public void logout(View v){
        prefManager.clearSession();
        prefManager.setIsLoggedIn(false);
        prefManager.setFirstTimeLaunch(false);
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        startActivity(intent);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }else{
            ActivityCompat.finishAffinity(this);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
