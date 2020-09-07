package com.example.favouritevehiclelistwithdatabasebackups;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkingManager {

    Context context;
    JSONArray vehicleJSONData;

    //i'll try to use this instead of thread.
    final ExecutorService networkExecutor = Executors.newFixedThreadPool(4);

    //***********interface code******
    JsonDataListener jsonDataListener;

    public interface JsonDataListener {
        void returnApiData(JSONArray vehicleJSONData, ArrayList<Vehicle> existingVehicleList);
    }

    //***********interface code******

    NetworkingManager(JsonDataListener listener, Context context){
        this.jsonDataListener = listener;
        this.context = context;
    }

    void getVehicleJSonData(){
        final String url = "https://ambrotus.github.io/Vehicles.json";
        connectToUrl(url);
    }

    void connectToUrl(final String url){

        networkExecutor.execute(()->{
            //get the json file from the url, and then just parse it in the jsonmanager file
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream;
            BufferedReader inputReader = null;
            try{
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod("GET");
                inputStream = httpURLConnection.getInputStream();
                inputReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = inputReader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //testing output

                }
                vehicleJSONData = new JSONArray(buffer.toString());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        //do stuff in main activity
                        ArrayList<Vehicle> thisIsEmpty = new ArrayList<>();
                        jsonDataListener.returnApiData(vehicleJSONData, thisIsEmpty);
                    }
                });

            }catch (Exception e){

            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                try {
                    if (inputReader != null) {
                        inputReader.close();
                    }
                } catch (Exception e) {

                }
                networkExecutor.shutdown();
            }

        });

    }

}
