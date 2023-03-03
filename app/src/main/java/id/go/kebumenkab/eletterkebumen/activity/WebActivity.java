package id.go.kebumenkab.eletterkebumen.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import id.go.kebumenkab.eletterkebumen.R;

public class WebActivity extends AppCompatActivity{

    private WebView webview;
    private EditText find;
    private ImageButton next, closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view_tanpainternet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String htmlAsString = getString(R.string.html_info);

        webview         = (WebView)findViewById(R.id.webview);
        find            = (EditText) findViewById(R.id.find);
        next            = (ImageButton) findViewById(R.id.btn_next);
        closeButton     = (ImageButton) findViewById(R.id.btn_close);

        find.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        closeButton.setVisibility(View.GONE);

        if(getIntent().hasExtra("faq")){
            getSupportActionBar().setTitle("FAQ");
            htmlAsString = getString(R.string.faq);
            find.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
        }

        webview.loadDataWithBaseURL(null, htmlAsString, "text/html", "utf-8", null);


        find.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                webview.findAllAsync(find.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.findNext(true);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.clearMatches();
                find.setText("");
                hideKeyboardFrom(getApplicationContext(), v);
            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}



