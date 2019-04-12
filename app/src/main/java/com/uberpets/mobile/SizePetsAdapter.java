package com.uberpets.mobile;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uberpets.model.PetSize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SizePetsAdapter extends RecyclerView.Adapter<SizePetsHolder> {


    private final List<PetSize> pets;
    static int id;
    private static final int minNumbItems = 1;
    private static final int maxNumbItems = 3;
    //private Map<Integer,SizePetsHolder> mapHolder;

    public SizePetsAdapter(ArrayList pets){
        this.pets = pets;
        //mapHolder = new HashMap<>();
        this.id = 0;
    }


    public SizePetsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SizePetsHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.size_pets_options, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final SizePetsHolder holder, final int position) {

        holder.getLittlePet().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLittlePetInHolder(holder);
                pets.get(position).changeToLittlePet();
                /*if(!mapHolder.containsKey(position))
                    mapHolder.put(position,holder);*/
            }
        });
        holder.getMediumPet().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMediumPetInHolder(holder);
                pets.get(position).changeToMediumPet();
                /*if(!mapHolder.containsKey(position))
                    mapHolder.put(position,holder);*/
            }
        });
        holder.getBigPet().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBigPetInHolder(holder);
                pets.get(position).changeToBigPet();
                /*if(!mapHolder.containsKey(position))
                    mapHolder.put(position,holder);*/
            }
        });
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getItemCount() > minNumbItems){
                    pets.remove(position);
                    //notifyItemRemoved(position);
                    notifyItemRangeChanged(position, pets.size());
                    //updateHolders();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return pets != null ? pets.size() : 0;
    }


    public void updateList() {
        if(getItemCount() < maxNumbItems) {
            this.pets.add(new PetSize());
            notifyItemInserted(getItemCount());
        }
    }

    public int getAllLittlePets() {
        int number=0;
        for (PetSize pet:pets) {
            number+= pet.getLittlePet();
        }
        return number;
    }

    public int getAllMediumPets() {
        int number=0;
        for (PetSize pet:pets) {
            number+= pet.getMediumPet();
        }
        return number;
    }

    public int getAllBigPets() {
        int number=0;
        for (PetSize pet:pets) {
            number+= pet.getBigPet();
        }
        return number;
    }


    public void setLittlePetInHolder(SizePetsHolder holder){
        holder.getLittlePet().setBackgroundResource(R.drawable.layout_selection);
        holder.getBigPet().setBackgroundColor(Color.TRANSPARENT);
        holder.getMediumPet().setBackgroundColor(Color.TRANSPARENT);
    }

    public void setMediumPetInHolder(SizePetsHolder holder){
        holder.getLittlePet().setBackgroundColor(Color.TRANSPARENT);
        holder.getMediumPet().setBackgroundResource(R.drawable.layout_selection);
        holder.getBigPet().setBackgroundColor(Color.TRANSPARENT);
    }

    public void setBigPetInHolder(SizePetsHolder holder){
        holder.getLittlePet().setBackgroundColor(Color.TRANSPARENT);
        holder.getMediumPet().setBackgroundColor(Color.TRANSPARENT);
        holder.getBigPet().setBackgroundResource(R.drawable.layout_selection);
    }

    /*public void updateHolders() {
        int total = pets.size();
        for(int i=0;i<total;i++){
            if(pets.get(i).getLittlePet() ==1)
                setLittlePetInHolder(mapHolder.get(i));
            else if (pets.get(i).getMediumPet() ==1)
                setMediumPetInHolder(mapHolder.get(i));
            else
                setBigPetInHolder(mapHolder.get(i));
        }
    }*/
}
