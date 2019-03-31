package com.antoinedevblog.www.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Content extends AppCompatActivity {
    //Put your credentials here
    private String clientID = "YOUR--CLIENT--ID";
    private String clientSECRET = "YOUR--CLIENT--SECRET";
    private String gloOAUTHurl = "https://app.gitkraken.com/oauth/authorize";
    private String access_token;
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("GitKraken Glo Widget");
        setContentView(R.layout.activity_content);
        sharedPref = getSharedPreferences("glo-app", MODE_PRIVATE);
        sharedPref.edit().clear().commit();
        final Button button = (Button) findViewById(R.id.auth_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(gloOAUTHurl +"?response_type=code&scope=board:read&state=myrandomstring&client_id=" + clientID));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Uri uri = getIntent().getData();

        //if you don't have a token yet

        String out = sharedPref.getString("token","null");
        if( out.equals("null") ) {
            if (uri != null) {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

                String code = uri.getQueryParameter("code");

                GetToken(code);
            }
        }else{
            openBoardSelector();
        }
    }

    private void GetToken(final String token){
        String url = "https://api.gitkraken.com/oauth/access_token";
        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", clientID);
        params.put("client_secret", clientSECRET);
        params.put("code",token);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            access_token = response.getString("access_token");
                            //We got an access token !!
                            sendTokenToWidget();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("JSONPost", "Error: " + error.getMessage());
                //pDialog.hide();
            }
        });

        queue.add(jsonObjReq);
    }

    private void sendTokenToWidget(){
        Toast.makeText(this,"Token Recieved",Toast.LENGTH_SHORT).show();
        // save your string in SharedPreferences
        sharedPref.edit().putString("token", access_token).commit();
        openBoardSelector();

    }

    private void openBoardSelector(){
        //Open Board selector
        Intent intent = new Intent(this, BoardActivity.class);
        startActivity(intent);
    }
}
