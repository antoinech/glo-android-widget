package com.antoinedevblog.www.widget;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import static android.content.Context.MODE_PRIVATE;

public class Board {
    public String name;

    public String id;

    public JSONArray columns;
    private SharedPreferences sharedPref;


    public Board(String name, String id) {

        this.name = name;

        this.id = id;

        this.columns = new JSONArray();
    }

    // Constructor to convert JSON object into a Java class instance

    public Board(JSONObject object){

        this.columns = new JSONArray();
        try {

            this.name = object.getString("name");

            this.id = object.getString("id");


            JSONArray columnsArray = object.getJSONArray("columns");

            Log.e("MyFilter",Integer.toString(columnsArray.length()));
            String tempColumnStr = "";
            for(int i = 0; i< columnsArray.length();i++){
                JSONObject column = columnsArray.getJSONObject(i);

                JSONObject out = new JSONObject();

                tempColumnStr = column.getString("name");
                out.put("name",tempColumnStr);

                tempColumnStr = column.getString("id");
                out.put("id",column.getString("id"));
                this.columns.put(out);
            }


        } catch (JSONException e) {
            Log.e("MyFilter","Holy shit, it's not even fetching new boards");
            e.printStackTrace();

        }

    }



    // Factory method to convert an array of JSON objects into a list of objects

    // Board.fromJson(jsonArray);

    public static ArrayList<Board> fromJson(JSONArray jsonObjects) {

        ArrayList<Board> boards = new ArrayList<Board>();

        for (int i = 0; i < jsonObjects.length(); i++) {

            try {

                boards.add(new Board(jsonObjects.getJSONObject(i)));

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }

        return boards;

    }
    
}
