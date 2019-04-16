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

    public boolean isLittlePet() {
        if(this.sizePet == LITTLE) {
            return true;
        }
        return false;
    }

    public boolean isMediumPet() {
        if(this.sizePet == MEDIUM) {
            return true;
        }
        return false;
    }

    public boolean isBigPet() {
        if(this.sizePet == BIG) {
            return true;
        }
        return false;
    }

}
