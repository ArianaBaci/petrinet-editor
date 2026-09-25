package application.model;

import java.util.List;
import java.util.Set;

public class Place extends Node{

    public Place(int ID, String name, int PetriNetID) {
        this.name = name;
        this.PetriNetID = PetriNetID;
        this.ID = ID;
    }

    public Place(String name, int PetriNetID) {
        this.name = name;
        this.PetriNetID = PetriNetID;
        count++;
        this.ID = count;
    }

    public String toString(){
        return this.name;
    }

}
