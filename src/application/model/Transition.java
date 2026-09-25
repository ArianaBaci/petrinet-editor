package application.model;

public class Transition extends Node {
    private UserType type; //enum per le tipologie Admin o End-User

    public Transition(int ID, String name, int PetriNetID, UserType type) {
        this.PetriNetID = PetriNetID;
        this.name = name;
        this.type = type;
        this.ID = ID;
    }

    public Transition(String name, int PetriNetID, UserType type) {
        this.PetriNetID = PetriNetID;
        this.name = name;
        this.type = type;
        count++;
        this.ID = count;
    }

    public void setUserType(UserType type) {
        this.type = type;
    }
    public String toString(){
        return this.name;
    }
    /*
    public String toString() {
        return (this.type.toString() + "-transition, transition name: " + super.toString());
    }
  */
   public boolean equals(Object other){
          return other instanceof Transition && this.ID == ((Transition) other).ID;
    }

   public UserType getUserType() {
	   return type;
   }
}
