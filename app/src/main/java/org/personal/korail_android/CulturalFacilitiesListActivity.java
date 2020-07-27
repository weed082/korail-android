package org.personal.korail_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.personal.korail_android.adapter.FacilitiesAdapter;
import org.personal.korail_android.background.OkHttp;
import org.personal.korail_android.item.FacilitiesItem;

import java.util.ArrayList;

public class CulturalFacilitiesListActivity extends AppCompatActivity {

    ArrayList<FacilitiesItem> facilitiesItemArrayList;
    FacilitiesAdapter facilitiesAdapter;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    String result;
    Handler handler;
    TextView resultTV;
    EditText searchET;
    ImageButton searchIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cultural_facilities_list);

        recyclerView = findViewById(R.id.facilitiesRecyclerview);
        resultTV = findViewById(R.id.resultCountTV);
        searchET = findViewById(R.id.searchET);
        searchIB = findViewById(R.id.searchIB);

        handler = new Handler();
        facilitiesItemArrayList = new ArrayList<>();
        facilitiesAdapter = new FacilitiesAdapter(facilitiesItemArrayList, this);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(facilitiesAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttp okHttpThread = new OkHttp();
                result = okHttpThread.getData("http://52.79.146.35/cultural_facilities");
                displayFacilities(result,"");
            }
        }).start();

        searchIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        OkHttp okHttpThread = new OkHttp();
                        result = okHttpThread.getData("http://52.79.146.35/cultural_facilities/?station_name=" + searchET.getText());
                        String searchStation= String.valueOf(searchET.getText());
                        displayFacilities(result,searchStation);
                    }
                }).start();
            }
        });

    }

    public void displayFacilities(final String result, final String searchStation) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = null;
                facilitiesItemArrayList.clear();
                facilitiesAdapter.notifyDataSetChanged();
                try {
                    jsonObject = new JSONObject(result);
                    //그중에서 data를 키값으로 갖는 jsonarray를 가져옴
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if(!searchStation.equals("")){
                        resultTV.setText(searchStation+" 검색결과 총 "+jsonArray.length()+"건");
                        resultTV.setVisibility(View.VISIBLE);
                    }
                    //데이터를 jsonarray길이만큼 반복하여 name값을 가져옴
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject searchItem = jsonArray.getJSONObject(i);
                        String id = searchItem.getString("id");
                        String lineName = searchItem.getString("line_name");
                        String stationName = searchItem.getString("station_name");
                        String floor = searchItem.getString("floor");
                        String location = searchItem.getString("location");
                        String equipment = searchItem.getString("equipment");
                        FacilitiesItem facilitiesItem = new FacilitiesItem(id, lineName, stationName, floor, location, equipment);
                        facilitiesItemArrayList.add(facilitiesItem);
                        facilitiesAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}