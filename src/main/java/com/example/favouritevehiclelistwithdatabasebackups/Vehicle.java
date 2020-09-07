package com.example.favouritevehiclelistwithdatabasebackups;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Vehicle {
    @PrimaryKey(autoGenerate = false)
    private int id;

    private String brand;
    private String model;
    private int year;
    private boolean isFavourite;

//    public Vehicle(){
//        brand = "";
//        model = "";
//        year = 0;
//        isFavourite = false;
//    }

//    @Ignore
//    public Vehicle(int id, String brand, String model, int year) {
//        this.id = id;
//        this.brand = brand;
//        this.model = model;
//        this.year = year;
//        this.isFavourite = false;
//    }


    public Vehicle(int id, String brand, String model, int year, boolean favourite) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.isFavourite = favourite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

}
