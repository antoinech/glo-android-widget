package com.antoinedevblog.www.widget;

import android.content.SharedPreferences;

import java.util.ArrayList;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import static android.content.Context.MODE_PRIVATE;

public class Board {
    public String name;

    public String id;
    private SharedPreferences sharedPref;


    public Board(String name, String id) {

        this.name = name;

        this.id = id;

    }

    // Constructor to convert JSON object into a Java class instance

    public Board(JSONObject object){

        try {

            this.name = object.getString("name");

            this.id = object.getString("id");

        } catch (JSONException e) {

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
