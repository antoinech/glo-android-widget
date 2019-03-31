package com.antoinedevblog.www.widget;


import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

public class BoardActivity extends AppCompatActivity {

    ArrayList<Board> arrayOfBoards;
    private String PAT_key = " ";
    private SharedPreferences sharedPref;
    private String jsonString;
    private JSONArray jsonArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        // Construct the data source
        arrayOfBoards = new ArrayList<Board>();

        arrayOfBoards.add(new Board("Lol","id"));
        updateJSONString();

    }



    private void populateListView(){

        arrayOfBoards = Board.fromJson(jsonArray);

        // Create the adapter to convert the array to views
        BoardAdapter adapter = new BoardAdapter(this, arrayOfBoards);

        ListView listView = (ListView) findViewById(R.id.boardlist);

        listView.setAdapter(adapter);
    }

    private void updateJSONString(){
        // get or create SharedPreferences
        sharedPref = getSharedPreferences("glo-app", MODE_PRIVATE);
        String token = sharedPref.getString("token", "null");
        if(token == "null") {
            return;
        }else{
            PAT_key = token;
        }

        String url = "https://gloapi.gitkraken.com/v1/glo/boards";
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        jsonString = response;
                        getJSONDataFromString();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error","Error with Json");
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String ACCESS_TOKEN = PAT_key;
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", ACCESS_TOKEN);
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getJSONDataFromString(){
        if(jsonString != null){
            try {
                Log.e("error",jsonString);
                jsonArray = new JSONArray(jsonString);
                populateListView();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
