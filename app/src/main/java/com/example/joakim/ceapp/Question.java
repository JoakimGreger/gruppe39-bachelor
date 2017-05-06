package com.example.joakim.ceapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bim on 05.05.2017.
 */

public class Question {
    private String question;
    private String type;
    private ArrayList<String> answers = new ArrayList<String>();

    public Question(JSONObject json) {
        try {
            this.question = json.getString("question");
            this.type = json.getString("type");

            if (type.equals("flervalg")) {
                JSONArray choices = json.getJSONArray("choices");
                for (int i = 0; i < choices.length(); i++) {
                    answers.add(choices.getJSONObject(i).getString("answer"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }
}
