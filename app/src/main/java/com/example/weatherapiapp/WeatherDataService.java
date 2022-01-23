package com.example.weatherapiapp;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {
//https://www.metaweather.com/api/location/44418/
    public static final String QUERY_FOR_CITY_ID = "https://www.metaweather.com/api/location/search/?query=";
    public static final String QUERY_FOR_CITY_WEATHER_BY_ID  = "https://www.metaweather.com/api/location/";
    Context context;
    String cityId;

    public WeatherDataService(Context context) {
        this.context = context;
    }


    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityId);
    }

    public void getCityId(String cityName,VolleyResponseListener volleyResponseListener){
        String url = QUERY_FOR_CITY_ID +cityName;
        // Request a string response from the provided URL.

        JsonArrayRequest request=new JsonArrayRequest(Request.Method.GET,url,null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cityId="";
                try {
                    JSONObject cityInfo=response.getJSONObject(0);
                    cityId=cityInfo.getString("woeid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //this worked. but it didint return the id number to mainactivity
                //Toast.makeText(context,"City id:"+cityId, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(cityId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(context, "That didn't work!"+error.toString(), Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("That didn't work!"+error.toString());
            }
        });
        MySingleton.getInstance(context).addToRequestQueue(request);
        //returned a null.
        //return cityId;
    }
    public interface ForeCastByIDResponse {
        void onError(String message);

        void onResponse(List<WeatherReportModel> weatherReportModels);
    }

    public void getCityForecastById(String cityId,ForeCastByIDResponse foreCastByIDResponse){
        List<WeatherReportModel> reportModelList=new ArrayList<>();
        String url=QUERY_FOR_CITY_WEATHER_BY_ID+cityId;
        // get the json object
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray consolidated_weather_list=response.getJSONArray("consolidated_weather");
                //get the first item in the array

                for(int i=0;i<consolidated_weather_list.length();i++){
                WeatherReportModel one_day_weather=new WeatherReportModel();
                JSONObject first_day_from_api= (JSONObject) consolidated_weather_list.get(i);
                one_day_weather.setId(first_day_from_api.getInt("id"));
                one_day_weather.setWeather_state_name(first_day_from_api.getString("weather_state_name"));
                one_day_weather.setWeather_state_abbr(first_day_from_api.getString("weather_state_abbr"));
                one_day_weather.setWind_direction_compass(first_day_from_api.getString("wind_direction_compass"));
                one_day_weather.setCreated(first_day_from_api.getString("created"));
                one_day_weather.setApplicable_date(first_day_from_api.getString("applicable_date"));
                one_day_weather.setMin_temp(first_day_from_api.getLong("min_temp"));
                one_day_weather.setMax_temp(first_day_from_api.getLong("max_temp"));
                one_day_weather.setThe_temp(first_day_from_api.getLong("the_temp"));
                one_day_weather.setWind_speed(first_day_from_api.getLong("wind_speed"));
                one_day_weather.setWind_direction(first_day_from_api.getLong("wind_direction"));
                one_day_weather.setAir_pressure(first_day_from_api.getLong("air_pressure"));
                one_day_weather.setHumidity(first_day_from_api.getInt("humidity"));
                one_day_weather.setVisibility(first_day_from_api.getLong("visibility"));
                one_day_weather.setPredictability(first_day_from_api.getInt("predictability"));
                reportModelList.add(one_day_weather);

                }
                foreCastByIDResponse.onResponse(reportModelList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Something wrong:"+error, Toast.LENGTH_SHORT).show();
            }
        });


        //get the property called "concolodated_weather" which is an array


        //get each item in the array and assign it to a new weatherreportmodel object

        MySingleton.getInstance(context).addToRequestQueue(request);

    }
    public interface GetCityNameForecastCallBack{
        void onError(String message);
        void onResponse(List<WeatherReportModel> weatherReportModels);
    }
    public void getCityForecastByName(String cityName,GetCityNameForecastCallBack getCityForecastCallBack){
        //fetch the city id given the city name

        //fetch the city forecast
        getCityId(cityName, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }

            @Override
            public void onResponse(String cityId) {
                getCityForecastById(cityId, new ForeCastByIDResponse() {
                    @Override
                    public void onError(String message) {

                    }

                    @Override
                    public void onResponse(List<WeatherReportModel> weatherReportModels) {
                    //we have the weather report
                        getCityForecastCallBack.onResponse(weatherReportModels);
                    }
                });
            }
        });
    }
}
