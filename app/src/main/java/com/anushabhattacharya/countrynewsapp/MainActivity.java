package com.anushabhattacharya.countrynewsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.anushabhattacharya.countrynewsapp.api.ApiClient;
import com.anushabhattacharya.countrynewsapp.api.ApiInterfaces;
import com.anushabhattacharya.countrynewsapp.models.Articles;
import com.anushabhattacharya.countrynewsapp.models.News;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String API_KEY="37917dd5f50643038326ff619e98f37d";
    private RecyclerView recyclerView;
    private List<Articles> articles=new ArrayList<>();
    private Adapter adapter;
    private String TAG=MainActivity.class.getSimpleName();
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        String countryCode=getCountry();
        Log.d("countryCode",countryCode);
        loadArticles(countryCode);


    }


    public void loadArticles(String countryCode)
    {
        ApiInterfaces apiInterfaces= ApiClient.getApiClient().create(ApiInterfaces.class);

        Call<News> call=apiInterfaces.getNews(countryCode,API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful() && response.body()!=null)
                {
                    if(!articles.isEmpty()){
                        articles.clear();
                    }
                    articles=response.body().getArticle();
                    adapter=new Adapter(articles,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Failed to load data!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to load news! Check your internet", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getCountry()
    {
        //String locale = this.getResources().getConfiguration().locale.getCountry();
        //return locale.toLowerCase();
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();
        return countryCode;
    }
}