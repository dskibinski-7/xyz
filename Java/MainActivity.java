package com.example.skybapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Double.isNaN;

public class MainActivity extends AppCompatActivity {


    /* BEGIN config data */
    private String ipAddress = COMMON.DEFAULT_IP_ADDRESS;
    private int sampleTime = COMMON.DEFAULT_SAMPLE_TIME;
    /* END config data */

    /* BEGIN widgets */
    private GraphView dataGraph_t;
    private GraphView dataGraph_p;
    private GraphView dataGraph_h;

    private LineGraphSeries<DataPoint> dataSeries_t;
    private LineGraphSeries<DataPoint> dataSeries_p;
    private LineGraphSeries<DataPoint> dataSeries_h;

    //czy te smieszne ustawienia moga byc wspolne - oraz co wgl robia
    private final int dataGraphMaxDataPointsNumber = 1000;
    private final double dataGraphMaxX = 10.0d;
    private final double dataGraphMinX =  0.0d;
    private final double dataGraphMaxY =  1.0d;
    private final double dataGraphMinY = -1.0d;
    private AlertDialog.Builder configAlterDialog;

    /* BEGIN request timer */
    private RequestQueue queue;
    private Timer requestTimer;
    private long requestTimerTimeStamp = 0;
    private long requestTimerPreviousTime = -1;
    private boolean requestTimerFirstRequest = true;
    private boolean requestTimerFirstRequestAfterStop;
    private TimerTask requestTimerTask;
    private final Handler handler = new Handler();
    /* END request timer */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    /**
     * @brief Main activity button onClick procedure - common for all upper menu buttons
     * @param v the View (Button) that was clicked
     */

    public void btns_onClick_main_menu(View v) {
        switch (v.getId()) {
            case R.id.pogoda: {
                openCharts();
                break;
            }
            case R.id.go_to_led: {
                openLeds();
                break;
            }
            default: {
                // do nothing
            }
        }
    }

    private void openCharts() {
        Intent openConfigIntent = new Intent(this, ChartsActivity.class);
        startActivityForResult(openConfigIntent, COMMON.REQUEST_CODE_CONFIG);
    }
    private void openLeds() {
        Intent openConfigIntent = new Intent(this, LedsActivity.class);
        startActivityForResult(openConfigIntent, COMMON.REQUEST_CODE_CONFIG);
    }






}
