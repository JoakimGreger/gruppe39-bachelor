package com.example.joakim.ceapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.HashMap;

/**
 * Created by bim on 03.05.2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    public static final String DATABASE_NAME = "CEMLocate.db";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //db = this.getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS OfflineLeaderboard (_id INTEGER PRIMARY KEY NOT NULL, " +
                        "username VARCHAR(60), " +
                        "answers INTEGER, " +
                        "score INTEGER)"
        );
        populateLeaderboard(db);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS OfflineLeaderboard");
        onCreate(db);
    }

    public Cursor getOfflineLeaderboard(SQLiteDatabase db, String orderBy) {

        //Henter tidliger score hvis den finnes
        SharedPreferences startscore = context.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        int score = startscore.getInt("qScore", 0);

        //Henter antall besvareler
        SharedPreferences answers = context.getSharedPreferences("qAnswer", Context.MODE_PRIVATE);
        int answerAmount = answers.getInt("qAnswer", 0);

        String update = "UPDATE OfflineLeaderboard SET answers = " + answerAmount + ", score = " + score + " WHERE username = 'You'";
        db.execSQL(update);

        if (orderBy.equals("Score") || orderBy.equals("Answers")) {
            String sql = "SELECT * FROM OfflineLeaderboard ORDER BY " + orderBy + " DESC";

            return db.rawQuery(sql, null);
        } else {
            String sql = "SELECT * FROM OfflineLeaderboard";
            return db.rawQuery(sql, null);
        }
    }


    private void populateLeaderboard(SQLiteDatabase db) {
        //Henter tidliger score hvis den finnes
        SharedPreferences startscore = context.getSharedPreferences("qScore", Context.MODE_PRIVATE);
        int score = startscore.getInt("qScore", 0);

        //Henter antall besvareler
        SharedPreferences answers = context.getSharedPreferences("qAnswer", Context.MODE_PRIVATE);
        int answerAmount = answers.getInt("qAnswer", 0);

        db.execSQL("INSERT INTO OfflineLeaderboard(username, answers, score) VALUES" +
                "('John', 5, 530), " +
                "('Thea', 11, 1030), " +
                "('Fredrik', 22, 3070), " +
                "('Nora', 24, 3050), " +
                "('Thomas', 30, 5590), " +
                "('Oda', 40, 8430), " +
                "('Theo', 65, 10530)," +
                "('You', " + answerAmount + ", " + score + ")");
    }
}
