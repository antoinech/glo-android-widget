package com.antoinedevblog.www.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class GloWidgetService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new GloWidgetCardFactory(getApplicationContext(),intent);
    }

    class GloWidgetCardFactory implements RemoteViewsFactory{

        private Context context;
        private int widgetId;
        private String PAT_key = "";
        private String BOARD_key = " ";
        private String jsonString = "[{\"name\": \"Cards Not Loaded\",\"description\": \"\"}]";
        JSONArray jsonArray;
        SharedPreferences sharedPref;

        GloWidgetCardFactory(Context context, Intent intent){
            this.context = context;
            this.widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            //Get the data from datasource
            getJSONDataFromString();
        }

        @Override
        public void onDataSetChanged() {
            //Update the jsonString variable
            updateJSONString();

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return  jsonArray.length();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.row_layout);
            JSONObject JO = new JSONObject();
            try {
                JO = jsonArray.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String title = "", desc = "";
            try {
                title = JO.getString("name");
                JSONObject descObj = JO.getJSONObject("description");
                String description = ((JSONObject) descObj).getString("text");
                //Shortens the description if too long
                if(description.length() >= 14)
                {
                    description = description.substring(1, 125);
                    description +="...";
                }

                desc =description;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            views.setTextViewText(R.id.tx_title,title);
            views.setTextViewText(R.id.tx_desc,desc);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public void getJSONDataFromString(){
            if(jsonString != null){
                try {
                    Log.e("error",jsonString);
                    jsonArray = new JSONArray(jsonString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public void updateJSONString(){
            // get or create SharedPreferences
            sharedPref = context.getSharedPreferences("glo-app", MODE_PRIVATE);
            String token = sharedPref.getString("token", "null");
            if(token == "null") {

                return;
            }else{
                PAT_key = token;
            }

            String board = sharedPref.getString("board", "null");
            if(token == "null") {

                return;
            }else{
                BOARD_key = board;
            }

            String url = "https://gloapi.gitkraken.com/v1/glo/boards/" + BOARD_key + "/cards?fields=name,description";
            RequestQueue queue = Volley.newRequestQueue(context);

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
    }
}
