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

        void onResponse(WeatherReportModel weatherReportModel);
    }

    public void getCityForecastById(String cityId,ForeCastByIDResponse foreCastByIDResponse){
        List<WeatherReportModel> report=new ArrayList<>();
        String url=QUERY_FOR_CITY_WEATHER_BY_ID+cityId;
        // get the json object
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray consolidated_weather=response.getJSONArray("consolidated_weather");
                    //get the first item in the array
                    WeatherReportModel first_day=new WeatherReportModel();

                    JSONObject first_day_from_api= (JSONObject) consolidated_weather.get(0);
                    first_day.setId(first_day_from_api.getInt("id"));
                    first_day.setWeather_state_name("weather_state_name");
                    first_day.setWeather_state_abbr("weather_state_abbr");
                    first_day.setWind_direction_compass("wind_direction_compass");
                    first_day.setCreated("created");
                    first_day.setApplicable_date("applicable_date");
                    first_day.setMin_temp(first_day_from_api.getLong("min_temp"));
                    first_day.setMax_temp(first_day_from_api.getLong("max_temp"));
                    first_day.setThe_temp(first_day_from_api.getLong("the_temp"));
                    first_day.setWind_speed(first_day_from_api.getLong("wind_speed"));
                    first_day.setWind_direction(first_day_from_api.getLong("wind_direction"));
                    first_day.setAir_pressure(first_day_from_api.getLong("air_pressure"));
                    first_day.setHumidity(first_day_from_api.getInt("humidity"));
                    first_day.setVisibility(first_day_from_api.getLong("visibility"));
                    first_day.setPredictability(first_day_from_api.getInt("predictability"));

                    foreCastByIDResponse.onResponse(first_day);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        //get the property called "concolodated_weather" which is an array


        //get each item in the array and assign it to a new weatherreportmodel object

        MySingleton.getInstance(context).addToRequestQueue(request);

    }

    /*public List<WeaterReportModel> getCityForecastByName(String cityName){

    }*/
}
