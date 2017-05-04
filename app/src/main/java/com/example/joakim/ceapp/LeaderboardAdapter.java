package com.example.joakim.ceapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bim on 03.05.2017.
 */

public class LeaderboardAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private String type;

    public LeaderboardAdapter(Context context, Cursor cursor, int flags, String type) {
        super(context, cursor, flags);
        this.type = type;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item, null);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView item = (TextView)view.findViewById(R.id.itemText);
        item.setText(cursor.getString(1));
        TextView score = (TextView) view.findViewById(R.id.itemScore);

        if (type.equals("Answers")) {
            Integer num = cursor.getInt(2);
            score.setText(num.toString());
        } else {
            Integer num = cursor.getInt(3);
            score.setText(num.toString());
        }

        if (cursor.getString(1).equals("You")) {
            item.setTextColor(Color.BLUE);
            score.setTextColor(Color.BLUE);
        } else {
            item.setTextColor(Color.BLACK);
            score.setTextColor(Color.BLACK);
        }

    }
}




