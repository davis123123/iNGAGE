package ingage.ingage.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;

import ingage.ingage.R;
import ingage.ingage.adapters.FaqsAdapter;
import ingage.ingage.handlers.FaqsHandler;
import ingage.ingage.helpers.FaqsHelper;

public class FaqsActivity extends AppCompatActivity implements FaqsHandler.AsyncInterface{

    public static ArrayList<FaqsHelper> faqs;
    RecyclerView rvFaqs;
    FaqsAdapter adapter;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FAQs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        rvFaqs = (RecyclerView)findViewById(R.id.rvFaqs);
        adapter = new FaqsAdapter(this);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvFaqs.setLayoutManager(layoutManager);
        rvFaqs.setAdapter(adapter);

        faqs  = new ArrayList<>();

        loadingDialog();
        FaqsHandler faqshandler = new FaqsHandler(this);
        faqshandler.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadingDialog(){
        pd = new ProgressDialog(this);
        pd.setTitle("Loading");
        pd.setMessage("Please wait...");
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pd.show();
    }

    @Override
    public void response(String response) {
        Gson gson = new Gson();
        FaqsHelper[] arr = gson.fromJson(response, FaqsHelper[].class);

        for(int i=0; i<arr.length;i++){
            adapter.add(arr[i]);
        }
        adapter.notifyDataSetChanged();

        pd.dismiss();
    }

}
