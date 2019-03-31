package com.antoinedevblog.www.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.content.SharedPreferences;


import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class BoardAdapter extends ArrayAdapter<Board> {

    private SharedPreferences sharedPref;
    public BoardAdapter(Context context, ArrayList<Board> users) {

        super(context, 0, users);

    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position

        Board board = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_layout, parent, false);

        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Access the row position here to get the correct data item
                Board board = getItem(position);
                selectBoard(board.id);
            }
        });


        // Lookup view for data population

        TextView title = (TextView) convertView.findViewById(R.id.tx_title);
        TextView id = (TextView) convertView.findViewById(R.id.tx_desc);
        // Populate the data into the template view using the data object

        title.setText(board.name);
        id.setText(board.id);

        // Return the completed view to render on screen

        return convertView;

    }

    private void selectBoard(String id){

        sharedPref = this.getContext().getSharedPreferences("glo-app", MODE_PRIVATE);
        // save your string in SharedPreferences
        sharedPref.edit().putString("board", id).commit();
    }

}

