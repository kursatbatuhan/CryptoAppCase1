package com.example.cryptoappcase1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText searchEditText;
    private RecyclerView currenciesRV;
    private ProgressBar loadingPB;
    private ArrayList<CurrencyRVModal> currencyRVModalArrayList;
    private CurrencyRVAdapter currencyRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchEditText = findViewById(R.id.idEditSearch);
        currenciesRV = findViewById(R.id.idRVCurrencies);
        loadingPB = findViewById(R.id.idPBLoading);
        currencyRVModalArrayList = new ArrayList<>();
        currencyRVAdapter = new CurrencyRVAdapter(currencyRVModalArrayList,this);
        currenciesRV.setLayoutManager(new LinearLayoutManager(this));
        currenciesRV.setAdapter(currencyRVAdapter);
        getCurrencyData();




        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterCurrencies(editable.toString());
            }
        });

    }


    private void filterCurrencies(String currency){
        ArrayList<CurrencyRVModal> filteredList = new ArrayList<>();
        for (CurrencyRVModal item : currencyRVModalArrayList){
            if (item.getName().toLowerCase().contains(currency.toLowerCase())){
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(this , "No currency found for searched query" , Toast.LENGTH_SHORT).show();
        }else {
            currencyRVAdapter.filterList(filteredList);
        }
    }

    private void getCurrencyData(){
        loadingPB.setVisibility(View.VISIBLE);
        String url = "https://api.coinstats.app/public/v1/coins";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                try {
                    JSONArray dataArray = response.getJSONArray("coins");
                    for (int i = 0 ; i<dataArray.length();i++){
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        String symbol = dataObj.getString("symbol");
                        double price = dataObj.getDouble("price");
                        currencyRVModalArrayList.add(new CurrencyRVModal(name,symbol,price));

                    }
                    currencyRVAdapter.notifyDataSetChanged();
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this , "Fail to exrract json.." , Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingPB.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"Fail to get data..", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}