package com.example.favouritevehiclelistwithdatabasebackups;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DatabaseClient {

    static DatabaseClient vehicleDBInstance;
    private static VehicleDatabase dDatabase;


    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4); // created 4 threads for us to use. also to be used to do dataquerys in the background
    private Context m_context;

    static DatabaseClient getInstance(Context context){
        if(vehicleDBInstance == null){
            vehicleDBInstance = new DatabaseClient(context);
        }
        return vehicleDBInstance;
    }

    DatabaseClient(Context context){
        m_context = context;
        //Migration wasnt really needed until i made a major change to the database structure
        dDatabase = Room.databaseBuilder(context, VehicleDatabase.class, "VehicleDatabase").build();

    }

    static VehicleDatabase getDatabase(){
        return dDatabase;
    }

    //***** database interface ***
    public interface DataListener {
        void returnData(ArrayList<Vehicle> favouriteDatabase, String onlineStatus);

    }
    static DataListener dataListener;

    //*****firebase******
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static void getFirebaseData(DataListener listener){
        dataListener = listener;
        ArrayList<Vehicle> vehicleList = new ArrayList<>();

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        db.collection("Vehicles")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            //while a document is not a json, i think i'll parse it there too.
                                            //ArrayList<Vehicle> vehicleList = new ArrayList<>();
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                Log.d("data", document.getId() + " => " + document.getData());
                                                if(!document.getData().get("id").equals(null)) {
                                                    int id = Integer.parseInt(document.getData().get("id").toString());
                                                    String brand = document.getData().get("brand").toString();
                                                    String model = document.getData().get("model").toString();
                                                    int year = Integer.parseInt(document.getData().get("year").toString());
                                                    boolean favourite = (boolean)document.getData().get("favourite");
                                                    //vehicleList.add(new Vehicle((int)document.getData().get("id"),document.getData().get("CarModel1").toString(),document.getData().get("CarModel2").toString(),(int)document.getData().get("Year")));
                                                    vehicleList.add(new Vehicle(id, brand, model, year, favourite));
                                                    int i = 0;
                                                    Log.d("data", vehicleList.get(i).getBrand());
                                                    i++;
                                                }
                                            }
                                            //vehicleList = JsonManager.getDocumentData(task);
                                            //Log.d("data", "car"+vehicleList.get(0).isFavourite());
                                            dataListener.returnData(vehicleList,"online");

                                        } else {
                                            Log.d("Error", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
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

    static void insertToFirebaseDB(Context context, Vehicle vehicle){
        db.collection("Vehicles").document(""+vehicle.getId()).set(vehicle);
    }

    static void deletefromFirebaseDB(Context context, Vehicle vehicle){
        db.collection("Vehicles").document(""+vehicle.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting document", e);
                    }
                });

    }
    //***** end of firebase code******

    static void getAllVehicles(DataListener listener){
        dataListener = listener;

        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Vehicle> vehicleArrayList = new ArrayList<>(Arrays.asList((DatabaseClient.getDatabase().VehicleDao().getAllVehicles())));
                        dataListener.returnData(vehicleArrayList, "offline");
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

    static Future<ArrayList<Vehicle>> getAllVehiclesFuture2 = databaseWriteExecutor.submit(()->{
        System.out.println(String.format("starting expensive task thread %s", Thread.currentThread().getName()));

        ArrayList<Vehicle> vehicleArrayList = new ArrayList<>(Arrays.asList((DatabaseClient.getDatabase().VehicleDao().getAllVehicles())));
        return vehicleArrayList;
    });

    static void insertToDB(Context context, Vehicle vehicle){
        databaseWriteExecutor.execute(()->{
            getInstance(context).dDatabase.VehicleDao().insertVehicle(vehicle);
        });

    }

    static void deletefromDB(Context context, Vehicle vehicle){
        databaseWriteExecutor.execute(()->{
            getInstance(context).dDatabase.VehicleDao().deleteVehicle(vehicle);
        });

    }


}
