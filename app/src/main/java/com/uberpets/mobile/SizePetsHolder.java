package com.uberpets.mobile;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

class SizePetsHolder extends RecyclerView.ViewHolder {
    private ImageButton littlePet;
    private ImageButton bigPet;
    private ImageButton deleteButton;
    private ImageButton mediumPet;

    ImageButton getDeleteButton() {
        return deleteButton;
    }

    ImageButton getLittlePet() {
        return littlePet;
    }

    ImageButton getMediumPet() {
        return mediumPet;
    }

    ImageButton getBigPet() {
        return bigPet;
    }

    SizePetsHolder(View itemView){
        super(itemView);
        littlePet = itemView.findViewById(R.id.little_pet);
        mediumPet = itemView.findViewById(R.id.medium_pet);
        bigPet = itemView.findViewById(R.id.big_pet);
        deleteButton = itemView.findViewById(R.id.button_delete);
    }

}
