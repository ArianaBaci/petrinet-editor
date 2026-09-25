package application.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ComputationStep {
    private static int count = 0;
    private int ID;
    private int computationID;
    private int transitionID;
    private String transitionName;
    private Marking markingData;  // chiave intera = ID del place, valore intero = numero di token nel place
    private LocalDateTime timestamp;


    /// costruttore di computation step risultato dall'attivazione di una transition
    public ComputationStep(int computationID, Marking markingData, int transitionID, String transitionName) {
        count++;
        this.ID = count;
        this.computationID = computationID;
        this.transitionID = transitionID;
        this.transitionName = transitionName;
        this.markingData = markingData;
        this.timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    /// costruttore per dbmanager
    public ComputationStep(int ID, int computationID, int transitionID, String transitionName, Marking markingData, LocalDateTime timestamp) {
        this.ID = ID;
        this.computationID = computationID;
        this.transitionID = transitionID;
        this.transitionName = transitionName;
        this.markingData = markingData;
        this.timestamp = timestamp;
    }

    /// metodi accessori
    public Marking getMarkingData() {
        return markingData; }
    public int getID() {
        return ID;
    }
    public int getComputationID() {
        return computationID;
    }
    public int getTransitionID() {
        return transitionID;
    }
    public String getTransitionName() {return transitionName;}
    public static int getCount() { return ComputationStep.count; }
    public LocalDateTime getTimestamp() { return timestamp; }

    /// metodi set
    public void setID(int ID) {
        this.ID = ID;
    }
    public void setMarking(Marking markingData) {
        this.markingData = markingData;
    }
    public static void setCount(int count) { ComputationStep.count = count; }

    public String toString() {
        if(transitionName.equals("Initial")) {
         return"";
        }
        return transitionName +" Fired in date: " + timestamp.toString().replace("T", " ");
    }

    public String history() {
        return timestamp.toString() + "\t fired transition ID:" + transitionName + ", marking: " + markingData + "\n";
    }

}