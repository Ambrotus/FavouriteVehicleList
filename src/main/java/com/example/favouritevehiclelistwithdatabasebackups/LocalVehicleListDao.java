package com.example.favouritevehiclelistwithdatabasebackups;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
@Dao
public interface LocalVehicleListDao {

    @Query("DELETE FROM Vehicle")
    void deleteAllForTesting();

    @Insert
    void insertVehicle(Vehicle vehicle);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insertAllVehicles(Vehicle[] vehicles);

    @Delete
    void deleteVehicle(Vehicle vehicle);

    @Update
    void updateVehicle(Vehicle vehicle);

    @Transaction
    @Query("SELECT * FROM Vehicle")
    Vehicle[] getAllVehicles();

}
