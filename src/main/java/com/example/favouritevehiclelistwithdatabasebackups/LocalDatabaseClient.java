package com.example.favouritevehiclelistwithdatabasebackups;

import android.content.Context;
import android.net.sip.SipSession;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Room;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalDatabaseClient {
    static LocalDatabaseClient localVehicleDBInstance;
    private static LocalVehicleListDatabase localDatabase;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4); // created 4 threads for us to use. also to be used to do dataquerys in the background
    private Context m_context;

    static LocalDatabaseClient getInstance(Context context){
        if(localVehicleDBInstance == null){
            localVehicleDBInstance = new LocalDatabaseClient(context);
        }
        return localVehicleDBInstance;
    }

    LocalDatabaseClient(Context context){
        m_context = context;
        //Migration wasnt really needed until i made a major change to the database structure
        localDatabase = Room.databaseBuilder(context, LocalVehicleListDatabase.class, "LocalVehicleListDatabase").build();
    }

    static LocalVehicleListDatabase getDatabase(){
        return localDatabase;
    }


    //***** database interface ***
    public interface DataListener {
        //void returnData(ArrayList<Vehicle> favouriteDatabase);
        void returnApiData(JSONArray vehicleJSONData, ArrayList<Vehicle> existingVehicleList);
    }

    static DataListener dataListener;


    static void insertAllToDB(Context context, Vehicle[] vehicles){
//        databaseWriteExecutor.execute(()->{
//            getInstance(context).localDatabase.LocalVehicleListDao().insertAllVehicles(vehicles);
//        });

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getInstance(context).localDatabase.LocalVehicleListDao().insertAllVehicles(vehicles);
                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();
                    }
                }, 100);
                Looper.loop();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch(Exception e) {
        }
    }


    static void getLocalVehicleList(LocalDatabaseClient.DataListener listener){
        dataListener = listener;
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Vehicle> vehicleArrayList = new ArrayList<>(Arrays.asList((LocalDatabaseClient.getDatabase().LocalVehicleListDao().getAllVehicles())));
                        JSONArray thisIsEmpty = new JSONArray();
                        dataListener.returnApiData(thisIsEmpty, vehicleArrayList);
                        handler.removeCallbacks(this);
                        Looper.myLooper().quit();
                    }
                }, 100);
                Looper.loop();
            }
        };
        thread.start();
        try {
            thread.join();
        } catch(Exception e) {
        }
    }



}
