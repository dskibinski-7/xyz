package com.example.skybapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.AuthFailureError;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import org.json.JSONObject;


public class LedsActivity extends AppCompatActivity {

    String url = "http://192.168.56.16/ANDROID/leds.php";



    EditText getKolor;
    EditText getWiersz;
    EditText getKolumna;
    Button SendDataBtn;
    Button GoToTextBtn;

    String wiersz;
    String kolumna;
    String kolor;

    String tablica_json;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leds);

        SendDataBtn = (Button)findViewById(R.id.zapal);
        GoToTextBtn = (Button)findViewById(R.id.go_to_text_btn);
        getKolor = (EditText)findViewById(R.id.kolor);
        getWiersz = (EditText)findViewById(R.id.wiersz);
        getKolumna = (EditText)findViewById(R.id.kolumna);

        SendDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pobranie danych
                wiersz = getWiersz.getText().toString();
                kolumna = getKolumna.getText().toString();
                kolor = getKolor.getText().toString();

               /* if (kolor.equals("red"))
                {
                    kolor = "1";
                }
                else if (kolor.equals("green"))
                {
                    kolor = "2";
                }
                else if (kolor.equals("blue"))
                {
                    kolor = "3";
                }
                else
                {
                    kolor = "0";
                }*/



                postRequest();



            }
        });

        GoToTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToTextFnct();
            }
        });


    }

    private void postRequest() {
        RequestQueue requestQueue=Volley.newRequestQueue(LedsActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("kolor",kolor);
                params.put("wiersz",wiersz);
                params.put("kolumna",kolumna);
                return params;
            }

            @Override
            public Map<String,String> getHeaders() throws AuthFailureError{
                Map<String,String> params=new HashMap<String, String>();
                params.put("Content-Type","source");
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }



    private void GoToTextFnct() {
        Intent openConfigIntent = new Intent(this, LedTextActivity.class);
        startActivityForResult(openConfigIntent, COMMON.REQUEST_CODE_CONFIG);
    }



}
