package application.model;


public abstract class Node {
    protected String name;
    protected int ID;
    protected int PetriNetID;
    static int count = 0; //contatore per generare ID del Place

    public void changeName(String name){
        this.name = name;
    }

    //METODI ACCESSORI
    public int getID() {
        return ID;
    }
    public int getPetriNetID() {
        return PetriNetID;
    }
    public String getName() {
        return name;
    }
    public String toString() {
        return this.name;
        //return "\"" + this.name + "\"" + ", ID:" + this.ID + "\n"/*+ ", PetriNetID:"+ this.PetriNetID*/;
    }
    
    public static void setCount(int count) {
    	Node.count = count;
    }
}

