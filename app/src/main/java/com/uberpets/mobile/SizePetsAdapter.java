package com.uberpets.mobile;

import android.graphics.Color;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uberpets.model.PetSize;

import java.util.ArrayList;
import java.util.List;

public class SizePetsAdapter extends RecyclerView.Adapter<SizePetsHolder> {


    private final List<PetSize> pets;
    private static final int minNumbItems = 1;
    private static final int maxNumbItems = 3;
    private FloatingActionButton addPetButton;

    public SizePetsAdapter(ArrayList<PetSize> pets, FloatingActionButton addPetButton){
        this.pets = pets;
        this.addPetButton = addPetButton;
    }

    @NonNull
    public SizePetsHolder onCreateViewHolder
            (@NonNull ViewGroup viewGroup, int i) {
        return new SizePetsHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.size_pets_options, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final SizePetsHolder holder,
                                 final int position) {

        updateHolder(holder,position);

        holder.getLittlePet().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLittlePetInHolder(holder);
                pets.get(position).changeToLittlePet();
            }
        });
        holder.getMediumPet().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMediumPetInHolder(holder);
                pets.get(position).changeToMediumPet();
            }
        });
        holder.getBigPet().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBigPetInHolder(holder);
                pets.get(position).changeToBigPet();
            }
        });
        holder.getDeleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getItemCount() > minNumbItems){
                    pets.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();
                    //mostrar el fab button
                    addPetButton.show();
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
        if (getItemCount() == maxNumbItems){
            addPetButton.hide();
        }
    }

    public int getAllLittlePets() {
        int number=0;
        for (PetSize pet:pets) {
            if(pet.isLittlePet())
                number++;
        }
        return number;
    }

    public int getAllMediumPets() {
        int number=0;
        for (PetSize pet:pets) {
            if(pet.isMediumPet())
                number++;
        }
        return number;
    }

    public int getAllBigPets() {
        int number=0;
        for (PetSize pet:pets) {
            if(pet.isBigPet())
                number++;
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
    public void setBlankAllPetsInHolder(SizePetsHolder holder){
        holder.getLittlePet().setBackgroundColor(Color.TRANSPARENT);
        holder.getMediumPet().setBackgroundColor(Color.TRANSPARENT);
        holder.getBigPet().setBackgroundColor(Color.TRANSPARENT);
    }


    public void updateHolder(SizePetsHolder holder, int position) {
        PetSize petSize = pets.get(position);
        if(petSize.isLittlePet())
            setLittlePetInHolder(holder);
        else if(petSize.isMediumPet())
            setMediumPetInHolder(holder);
        else if(petSize.isBigPet())
            setBigPetInHolder(holder);
        else
            setBlankAllPetsInHolder(holder);
    }
}
