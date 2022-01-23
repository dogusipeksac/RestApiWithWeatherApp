package com.example.weatherapiapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button id_button,weatherCityId_button,CityName_button;
    private EditText searchEt;
    private ListView listview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        clickListener();
    }

    private void init(){
        id_button=findViewById(R.id.get_city_id_button);
        weatherCityId_button=findViewById(R.id.get_weather_by_CityId_button);
        CityName_button=findViewById(R.id.get_weather_CityName_button);
        searchEt=findViewById(R.id.search_edittext);
        listview=findViewById(R.id.listview);


    }
    private void clickListener(){
        WeatherDataService weatherDataService=new WeatherDataService(MainActivity.this);
        id_button.setOnClickListener(view -> {

            //this didnt return anything
            weatherDataService.getCityId(searchEt.getText().toString(), new WeatherDataService.VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, "Something wrong:"+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String cityId) {
                    Toast.makeText(MainActivity.this, "Returned an id of :"+cityId, Toast.LENGTH_SHORT).show();
                }
            });


        });
        weatherCityId_button.setOnClickListener(view ->{

            weatherDataService.getCityForecastById(searchEt.getText().toString(), new WeatherDataService.ForeCastByIDResponse() {
                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, "Something wrong:"+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(List<WeatherReportModel> weatherReportModels) {
                    ArrayAdapter arrayAdapter=new ArrayAdapter(
                            MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            weatherReportModels);
                    listview.setAdapter(arrayAdapter);
                }
            });

        });
        CityName_button.setOnClickListener(view ->{

            weatherDataService.getCityForecastByName(searchEt.getText().toString(), new WeatherDataService.GetCityNameForecastCallBack() {
                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, "Something wrong:"+message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(List<WeatherReportModel> weatherReportModels) {
                    ArrayAdapter arrayAdapter=new ArrayAdapter(
                            MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            weatherReportModels);
                    listview.setAdapter(arrayAdapter);
                }
            });
        });
    }

}