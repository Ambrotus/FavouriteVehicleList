package com.example.favouritevehiclelistwithdatabasebackups;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NetworkingManager.JsonDataListener, DatabaseClient.DataListener, LocalDatabaseClient.DataListener{

    private RecyclerView vehicleListView;
    private ArrayList<Vehicle> vehicleListFromJSON;
    private ArrayList<Vehicle> vehicleListFromFirebase;
    private ArrayList<Vehicle> favouritesDB;
    private VehicleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ConnectivityManager connectivityManager;
    boolean connected;

    DatabaseClient dbClient;
    LocalDatabaseClient localDbClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbClient = DatabaseClient.getInstance(this); //this line of code will create the database
        localDbClient = LocalDatabaseClient.getInstance(this);

        vehicleListView = (RecyclerView) findViewById(R.id.carList);
        layoutManager = new LinearLayoutManager(this);
        vehicleListView.setLayoutManager(layoutManager);
        vehicleListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        NetworkingManager networkingManager = new NetworkingManager(this, getApplicationContext());

        //check for internet connection: https://stackoverflow.com/questions/5474089/how-to-check-currently-internet-connection-is-available-or-not-in-android
        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
        if(connected) {
            //we are connected to a network
            //connected = true;

            //requires internet to do this/ fetches data from my github api
            //otherwise the list doesnt load and will be pulled from local database
            networkingManager.getVehicleJSonData();

        }
        else {
            //connected = false;

            LocalDatabaseClient.getLocalVehicleList(this);

        }

        //requires internet to do this/ fetches data from my github api
        //otherwise the list doesnt load any ways, thus needs to be reworked to store the list to a database to pull from while offline
        //networkingManager.getVehicleJSonData();

    }


    @Override //gets the json vehicle list and the favourites database and sets up adapters/view
    public void returnApiData(JSONArray vehicleJSONData, ArrayList<Vehicle> existingVehicleList) {

        if(existingVehicleList.isEmpty()) {
            vehicleListFromJSON = JsonManager.getVehicleData(vehicleJSONData);
        } else vehicleListFromJSON = existingVehicleList;

        //this should update the local database list to the most updated api version
        if(existingVehicleList.isEmpty()&& vehicleListFromJSON != null){
            //if existing is empty, meaning we pulled from online, update via insertAll
            Vehicle[] tempVehicleArray = new Vehicle[vehicleListFromJSON.size()];
            vehicleListFromJSON.toArray(tempVehicleArray);
            LocalDatabaseClient.insertAllToDB(this,tempVehicleArray);

        }

        //DatabaseClient.insertToDB(context, vehicleArray.get(getAdapterPosition()));

//        favouritesDB = new ArrayList<>();
        // get local favourites DB and set the isFavourite file
        //DatabaseClient.getAllVehicles(this);

        //get online stored favourites and update local database, otherwise get list from local database

//        connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
//        if(connected){
        DatabaseClient.getFirebaseData(this);
//
//        }else{
        DatabaseClient.getAllVehicles(this);
//
//        }

    }

    @Override //gets favourites for the database
    public void returnData(ArrayList<Vehicle> favouriteDatabase,String onlineStatus) {
        if(onlineStatus.equals("online")){
            vehicleListFromFirebase = new ArrayList<>();
            if(favouriteDatabase.isEmpty()) {
                vehicleListFromFirebase = favouriteDatabase;
            }
        }else {

            favouritesDB = new ArrayList<>();
            favouritesDB = favouriteDatabase;

            //loop once here rather than in the adapters to improve performance
            //a hashmap instead of arraylists would be even better in the future
            for (int i = 0; i < favouritesDB.size(); i++) {
                for (int o = 0; o < vehicleListFromJSON.size(); o++) {
                    if (vehicleListFromJSON.get(o).getId() == (favouritesDB.get(i).getId())) {
                        vehicleListFromJSON.get(o).setFavourite(true);
                        Log.d("setting to fav", "returnApiData: ");
                    }
                }
            }

            //if( vehicleListFromFirebase!= null &&  (!favouritesDB.isEmpty())){
                //if(!vehicleListFromFirebase.containsAll(favouritesDB)){
                    for(int i = 0; i < favouritesDB.size(); i++){
                        DatabaseClient.insertToFirebaseDB(this,favouritesDB.get(i));
                    }
               // }
           // }

            adapter = new VehicleAdapter(getApplicationContext(), vehicleListFromJSON);//,favouritesDB
            vehicleListView.setAdapter(adapter);
        }
    }
}
