package id.go.kebumenkab.eletterkebumen.ajudan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import id.go.kebumenkab.eletterkebumen.R;
import id.go.kebumenkab.eletterkebumen.activity.PengaturanActivity;
import id.go.kebumenkab.eletterkebumen.adapter.ViewPagerAdapter;

public class Dashboard extends AppCompatActivity implements
        SuratMasukFragment.OnFragmentInteractionListener,
    SuratKonsepFragment.OnFragmentInteractionListener{

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private ViewPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private ViewPagerAdapter adapter;
    private  int pagePosition;

    private int[] imageResId = {
            R.drawable.ic_concept_white,
            R.drawable.ic_surat_white };

    public static  TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header_gradient));


        setPage(0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < imageResId.length; i++) {
//            tabLayout.getTabAt(i).setIcon(imageResId[i]);
//            tabLayout.getTabAt(i).setCustomView(R.layout.badged_tab);

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setIcon(imageResId[i]);
            tab.setCustomView(R.layout.badged_tab);
            tab.getCustomView().findViewById(android.R.id.text1).setVisibility(View.GONE);


        }
        /** Tidak dipakai karena akan menggunakan fitur trigger notifikasi Firebase **/
        // startService(new Intent(this, ServiceCheckBadge.class));

       // BadgeUtils.setBadge(Dashboard.this, 1);

//        ContentValues cv = new ContentValues();
//        cv.put("package", getPackageName());
//        cv.put("class", "id.go.kebumenkab.eletter.activity.Dashboard");
//        cv.put("badgecount", 1); // integer count you want to display
//
//// Execute insert
//        getContentResolver().insert(Uri.parse("content://com.sec.badge/apps"), cv);


    }

    public static void setBadge(int position, String number){
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if(tab != null && tab.getCustomView() != null) {
            TextView b = (TextView) tab.getCustomView().findViewById(R.id.badge);

            if(b != null) {
                b.setText(number);
            }

            View v = tab.getCustomView().findViewById(R.id.badgeCotainer);
            if(v != null && !number.equalsIgnoreCase("0")) {
                v.setVisibility(View.VISIBLE);
            }else{
                v.setVisibility(View.GONE);
            }
        }


    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SuratKonsepFragment(),"KONSEP");
        adapter.addFrag(new SuratMasukFragment(), "MASUK");

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("SCROLL", String.valueOf(positionOffset));
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("Page", String.valueOf(position));
                setPage(position);
                invalidateFragmentMenus(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(adapter);
    }


    private void invalidateFragmentMenus(int position){
        for(int i = 0; i < adapter.getCount(); i++){
            adapter.getItem(i).setHasOptionsMenu(i == position);
        }
        invalidateOptionsMenu(); //or respectively its support method.
    }

    private void setPage(int position){
        pagePosition = position;
        if(position == 0) {
            getSupportActionBar().setTitle("Konsep");

        }
        if(position == 1) {
            getSupportActionBar().setTitle("Masuk");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(Dashboard.this, PengaturanActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        /** Tidak dipakai karena akan menggunakan fitur trigger notifikasi Firebase **/
        // stopService(new Intent(this, ServiceCheckBadge.class));
        super.onDestroy();
    }
}
