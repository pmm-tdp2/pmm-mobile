package com.uberpets.model;

public class PetSize {

    private static final int  LITTLE = 1;
    private static final int  MEDIUM = 2;
    private static final int  BIG = 3;
    private int sizePet;


    public PetSize() {
        sizePet = 0;
    }

    public void changeToLittlePet(){
        this.sizePet = LITTLE;
    }

    public void changeToMediumPet(){
        this.sizePet = MEDIUM;
    }

    public void changeToBigPet(){
        this.sizePet = BIG;
    }

    public int getLittlePet() {
        if(this.sizePet == LITTLE) {
            return 1;
        }
        return 0;
    }

    public int getMediumPet() {
        if(this.sizePet == MEDIUM) {
            return 1;
        }
        return 0;
    }

    public int getBigPet() {
        if(this.sizePet == BIG) {
            return 1;
        }
        return 0;
    }

}
