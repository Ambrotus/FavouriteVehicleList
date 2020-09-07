package com.example.favouritevehiclelistwithdatabasebackups;

//import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.MyViewHolder>  {//implements DatabaseClient.DataListener
    private View view;
    private Context context;
    private ArrayList<Vehicle> vehicleArray;
    //private ArrayList<Vehicle> favouritesDB;
    private LayoutInflater inflater;

//    @Override
//    public void returnData(ArrayList<Vehicle> favouriteDatabase) {
//        favouritesDB = favouriteDatabase;
//    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView brandName;
        TextView modelName;
        ImageView favouriteImage;

        public MyViewHolder(@NonNull View view) {
            super(view);
            brandName = (TextView) view.findViewById(R.id.brandName);
            modelName = (TextView) view.findViewById(R.id.modelName);
            favouriteImage = (ImageView) view.findViewById((R.id.favouriteImage));
            favouriteImage.setTag(R.drawable.ic_favorite_border_24px);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(favouriteImage.getTag().equals(R.drawable.ic_favorite_24px)){
                Log.d("favourite vehicleToSave",vehicleArray.get(getAdapterPosition()).getBrand());
                AlertDialog message = new AlertDialog.Builder(view.getContext())
                        .setTitle("Remove Favorite Vehicle?")
                        .setMessage("Are you sure you want to remove "+ vehicleArray.get(getAdapterPosition()).getModel()+" from favourites?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("vehicle to remove",vehicleArray.get(getAdapterPosition()).getBrand());

                                DatabaseClient.deletefromFirebaseDB(context, vehicleArray.get(getAdapterPosition()));
                                DatabaseClient.deletefromDB(context, vehicleArray.get(getAdapterPosition()));

                                vehicleArray.get(getAdapterPosition()).setFavourite(false);
                                favouriteImage.setImageResource(R.drawable.ic_favorite_border_24px);
                                favouriteImage.setTag(R.drawable.ic_favorite_border_24px);


                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            } else {
                AlertDialog message = new AlertDialog.Builder(view.getContext())
                        .setTitle("Favorite Vehicle?")
                        .setMessage("Are you sure you want to add " + vehicleArray.get(getAdapterPosition()).getModel() + " to favourites?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("vehicle to save", vehicleArray.get(getAdapterPosition()).getBrand());

                                if(!vehicleArray.get(getAdapterPosition()).isFavourite()){
                                    vehicleArray.get(getAdapterPosition()).setFavourite(true);

                                    DatabaseClient.insertToDB(context, vehicleArray.get(getAdapterPosition()));
                                    DatabaseClient.insertToFirebaseDB(context, vehicleArray.get(getAdapterPosition()));

                                    favouriteImage.setImageResource(R.drawable.ic_favorite_24px);
                                    favouriteImage.setTag(R.drawable.ic_favorite_24px);
                                }

                                else {
                                    Log.d("vehicle already saved", vehicleArray.get(getAdapterPosition()).getBrand());
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            //update local favourite array with new database although i dont think this is needed as we dont pull from it here.
            //DatabaseClient.getAllVehicles((DatabaseClient.DataListener) view.getContext());

        }
    }

    @NonNull
    @Override
    public VehicleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)  {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_layout,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleAdapter.MyViewHolder holder, int position) {
        holder.brandName.setText(String.valueOf(vehicleArray.get(position).getBrand()));
        holder.modelName.setText(String.valueOf(vehicleArray.get(position).getModel()));
        if (vehicleArray.get(position).isFavourite()) {
                holder.favouriteImage.setImageResource(R.drawable.ic_favorite_24px);
                holder.favouriteImage.setTag(R.drawable.ic_favorite_24px);
        }

    }

    @Override
    public int getItemCount() {
        return vehicleArray.size();
    }

    public VehicleAdapter(Context appContext, ArrayList<Vehicle> list){//,ArrayList<Vehicle> favourites
        this.context = appContext;
        this.vehicleArray = list;
        //favouritesDB = favourites;
        inflater = LayoutInflater.from(appContext);
    }

}
