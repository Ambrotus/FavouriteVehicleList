package com.example.favouritevehiclelistwithdatabasebackups;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Vehicle.class},version = 1, exportSchema = false)
public abstract class LocalVehicleListDatabase extends RoomDatabase{
        public abstract LocalVehicleListDao LocalVehicleListDao();
}
