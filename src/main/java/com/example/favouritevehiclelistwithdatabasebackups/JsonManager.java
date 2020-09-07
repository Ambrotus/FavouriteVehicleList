package com.example.favouritevehiclelistwithdatabasebackups;

import org.json.JSONArray;

import java.util.ArrayList;

public class JsonManager {
    //grabs the json data and creates a list of initialized vehicles
    static ArrayList<Vehicle> getVehicleData(JSONArray vehicleJSONData){
        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        try {
            for (int i = 0; i < vehicleJSONData.length(); i++) {
                if(vehicleJSONData.getJSONObject(i).has("id"))
                vehicleList.add(new Vehicle(vehicleJSONData.getJSONObject(i).getInt("id"),vehicleJSONData.getJSONObject(i).getString("CarModel1"),vehicleJSONData.getJSONObject(i).getString("CarModel2"),vehicleJSONData.getJSONObject(i).getInt("Year"), false));

            }
        }catch (Exception e){
            //silent fail
        }
        return vehicleList;
    }


}
