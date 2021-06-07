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

public class ChartsActivity extends AppCompatActivity {



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
    private final double dataGraphMaxX = 45.0d;
    private final double dataGraphMinX = 0.0d;
    private final double dataGraphMaxY_t = 100.0d;
    private final double dataGraphMinY_t = -30.0d;

    private final double dataGraphMaxY_h = 100.0d;
    private final double dataGraphMinY_h = 0.0d;

    private final double dataGraphMaxY_p = 1200.0d;
    private final double dataGraphMinY_p = 200.0d;
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
        setContentView(R.layout.activity_charts);

        Intent intent = getIntent();
        Bundle configBundle = intent.getExtras();

        /* BEGIN initialize GraphView */
        // https://github.com/jjoe64/GraphView/wiki
        //temperature
        dataGraph_t = (GraphView) findViewById(R.id.dataGraph_t);
        dataSeries_t = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph_t.addSeries(dataSeries_t);
        dataGraph_t.getViewport().setXAxisBoundsManual(true);
        dataGraph_t.getViewport().setMinX(dataGraphMinX);
        dataGraph_t.getViewport().setMaxX(dataGraphMaxX);
        dataGraph_t.getViewport().setYAxisBoundsManual(true);
        dataGraph_t.getViewport().setMinY(dataGraphMinY_t);
        dataGraph_t.getViewport().setMaxY(dataGraphMaxY_t);

        //pressure
        dataGraph_p = (GraphView) findViewById(R.id.dataGraph_p);
        dataSeries_p = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph_p.addSeries(dataSeries_p);
        dataGraph_p.getViewport().setXAxisBoundsManual(true);
        dataGraph_p.getViewport().setMinX(dataGraphMinX);
        dataGraph_p.getViewport().setMaxX(dataGraphMaxX);
        dataGraph_p.getViewport().setYAxisBoundsManual(true);
        dataGraph_p.getViewport().setMinY(dataGraphMinY_p);
        dataGraph_p.getViewport().setMaxY(dataGraphMaxY_p);

        //humidity
        dataGraph_h = (GraphView) findViewById(R.id.dataGraph_h);
        dataSeries_h = new LineGraphSeries<>(new DataPoint[]{});
        dataGraph_h.addSeries(dataSeries_h);
        dataGraph_h.getViewport().setXAxisBoundsManual(true);
        dataGraph_h.getViewport().setMinX(dataGraphMinX);
        dataGraph_h.getViewport().setMaxX(dataGraphMaxX);
        dataGraph_h.getViewport().setYAxisBoundsManual(true);
        dataGraph_h.getViewport().setMinY(dataGraphMinY_h);
        dataGraph_h.getViewport().setMaxY(dataGraphMaxY_h);

        /* BEGIN config alter dialog */
        configAlterDialog = new AlertDialog.Builder(ChartsActivity.this);
        configAlterDialog.setTitle("This will STOP data acquisition. Proceed?");
        configAlterDialog.setIcon(android.R.drawable.ic_dialog_alert);
        configAlterDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                stopRequestTimerTask();
                openConfig();
            }
        });
        configAlterDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        /* END initialize GraphView */
        queue = Volley.newRequestQueue(ChartsActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        if ((requestCode == COMMON.REQUEST_CODE_CONFIG) && (resultCode == RESULT_OK)) {

            // IoT server IP address
            ipAddress = dataIntent.getStringExtra(COMMON.CONFIG_IP_ADDRESS);
            //textViewIP.setText(getIpAddressDisplayText(ipAddress));

            // Sample time (ms)
            String sampleTimeText = dataIntent.getStringExtra(COMMON.CONFIG_SAMPLE_TIME);
            sampleTime = Integer.parseInt(sampleTimeText);
            //textViewSampleTime.setText(getSampleTimeDisplayText(sampleTimeText));
        }
    }

    /**
     * @param v the View (Button) that was clicked
     * @brief Main activity button onClick procedure - common for all upper menu buttons
     */
    public void btns_onClick(View v) {
        switch (v.getId()) {
            case R.id.ust_wykres: {
                if (requestTimer != null)
                    configAlterDialog.show();
                else
                    openConfig();
                break;
            }
            case R.id.start_wykres: {
                startRequestTimer();
                break;
            }
            case R.id.stop_wykres: {
                stopRequestTimerTask();
                break;
            }
            default: {
                // do nothing
            }
        }
    }




    private String getURL(String ip) {
        return ("http://" + ip + "/" + COMMON.FILE_NAME);
    }


    private void openConfig() {
        Intent openConfigIntent = new Intent(this, ChartsSettingsActivity.class);
        Bundle configBundle = new Bundle();
        configBundle.putString(COMMON.CONFIG_IP_ADDRESS, ipAddress);
        configBundle.putInt(COMMON.CONFIG_SAMPLE_TIME, sampleTime);
        openConfigIntent.putExtras(configBundle);
        startActivityForResult(openConfigIntent, COMMON.REQUEST_CODE_CONFIG);
    }

    /**
     * @param response IoT server JSON response as string
     * @brief Reading raw chart data from JSON response.
     * @retval new chart data
     */
    //temperature
    private double getRawDataFromResponse_t(String response) {
        JSONObject jObject;
        double x = Double.NaN;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = (double) jObject.get("temperature");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }

    private double getRawDataFromResponse_p(String response) {
        JSONObject jObject;
        double x = Double.NaN;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = (double) jObject.get("pressure");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }

    private double getRawDataFromResponse_h(String response) {
        JSONObject jObject;
        double x = Double.NaN;

        // Create generic JSON object form string
        try {
            jObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
            return x;
        }

        // Read chart data form JSON object
        try {
            x = (double) jObject.get("humidity");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return x;
    }


    /**
     * @brief Starts new 'Timer' (if currently not exist) and schedules periodic task.
     */
    private void startRequestTimer() {
        if (requestTimer == null) {
            // set a new Timer
            requestTimer = new Timer();

            // initialize the TimerTask's job
            initializeRequestTimerTask();
            requestTimer.schedule(requestTimerTask, 0, sampleTime);

            // clear error message
            //textViewError.setText("");
        }
    }

    /**
     * @brief Stops request timer (if currently exist)
     * and sets 'requestTimerFirstRequestAfterStop' flag.
     */
    private void stopRequestTimerTask() {
        // stop the timer, if it's not already null
        if (requestTimer != null) {
            requestTimer.cancel();
            requestTimer = null;
            requestTimerFirstRequestAfterStop = true;
        }
    }

    /**
     * @brief Initialize request timer period task with 'Handler' post method as 'sendGetRequest'.
     */
    private void initializeRequestTimerTask() {
        requestTimerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        sendGetRequest();
                    }
                });
            }
        };
    }


    /**
     * @brief Sending GET request to IoT server using 'Volley'.
     */
    private void sendGetRequest()
    {
        // Instantiate the RequestQueue with Volley
        // https://javadoc.io/doc/com.android.volley/volley/1.1.0-rc2/index.html
        String url = getURL(ipAddress);

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { responseHandling(response); }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { errorHandling(COMMON.ERROR_RESPONSE); }
                });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private void errorHandling(int errorCode) {

    }

    /**
     * @brief GET response handling - chart data series updated with IoT server data.
     */
    private void responseHandling(String response) {
        if (requestTimer != null) {
            // get time stamp with SystemClock
            long requestTimerCurrentTime = SystemClock.uptimeMillis(); // current time
            requestTimerTimeStamp += getValidTimeStampIncrease(requestTimerCurrentTime);

            // get raw data from JSON response
            double rawData_t = getRawDataFromResponse_t(response);
            double rawData_h = getRawDataFromResponse_h(response);
            double rawData_p = getRawDataFromResponse_p(response);

            // update chart
            if (isNaN(rawData_t)) {
                //errorHandling(COMMON.ERROR_NAN_DATA);

            } else {

                // update plot series
                double timeStamp = requestTimerTimeStamp / 1000.0; // [sec]
                boolean scrollGraph = (timeStamp > dataGraphMaxX);
                dataSeries_t.appendData(new DataPoint(timeStamp, rawData_t), scrollGraph, dataGraphMaxDataPointsNumber);
                dataSeries_h.appendData(new DataPoint(timeStamp, rawData_h), scrollGraph, dataGraphMaxDataPointsNumber);
                dataSeries_p.appendData(new DataPoint(timeStamp, rawData_p), scrollGraph, dataGraphMaxDataPointsNumber);

                // refresh chart
                dataGraph_t.onDataChanged(true, true);
                dataGraph_p.onDataChanged(true, true);
                dataGraph_h.onDataChanged(true, true);
            }

            // remember previous time stamp
            requestTimerPreviousTime = requestTimerCurrentTime;
        }
    }


    /**
     * @brief Validation of client-side time stamp based on 'SystemClock'.
     */
    private long getValidTimeStampIncrease(long currentTime) {
        // Right after start remember current time and return 0
        if (requestTimerFirstRequest) {
            requestTimerPreviousTime = currentTime;
            requestTimerFirstRequest = false;
            return 0;
        }

        // After each stop return value not greater than sample time
        // to avoid "holes" in the plot
        if (requestTimerFirstRequestAfterStop) {
            if ((currentTime - requestTimerPreviousTime) > sampleTime)
                requestTimerPreviousTime = currentTime - sampleTime;

            requestTimerFirstRequestAfterStop = false;
        }

        // If time difference is equal zero after start
        // return sample time
        if ((currentTime - requestTimerPreviousTime) == 0)
            return sampleTime;

        // Return time difference between current and previous request
        return (currentTime - requestTimerPreviousTime);
    }

}
