package application.model;


public class Arc {
    protected int ID;
    protected int sourceID; // (Place or Transition)
    protected int targetID; // (Transition or Place)
    protected int petriNetID;
    protected static int count = 0; //contatore per generare ID
    public static String SourceName;
    public static String TargetName;

    public Arc(int ID, int sourceID, int targetID, int netId) {
    	this.ID = ID;
    	this.sourceID = sourceID;
    	this.targetID = targetID;
    	this.petriNetID = netId;
    }

    public Arc(int netId, Node source, Node target) {
    	count++;
    	this.ID = count;
    	this.sourceID = source.getID();
    	this.targetID = target.getID();
    	this.petriNetID = netId;
        SourceName = source.getName();
        TargetName = target.getName();

    }
    //METODI ACCESSORI
    public int getID() {
        return ID;
    }
    public int getPetriNetID() {
        return petriNetID;
    }
    public int getSourceID() {
        return sourceID;
    }
    public int getTargetID() {
        return targetID;
    }
    public String getSourceName() {
        return SourceName;
}
    public String getTargetName() {
        return TargetName;
}

    public String toString() {
        //return /*"Arc ID:" + this.ID + */"source ID: " +this.petriNetID + ", target ID:" + this.targetID + "\n"/*+ ", PetriNetID:"+ this.PetriNetID*/;
        return " "+ "From " + getSourceName() + " to " + getTargetName();
    }
    public boolean equals(Object other){
        return other instanceof Arc && this.ID == ((Arc) other).ID;
    }
    
    public static void setCount(int count) {
    	Arc.count = count;
    }
}