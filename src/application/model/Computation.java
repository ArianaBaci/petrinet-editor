package application.model;

import application.Main;
import application.model.PetriNet;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Computation {
    private static int count = 0;
    private int ID;
    private PetriNet petriNet;
    private int petriNetID;
    private String user;
    private ComputationStatus status = ComputationStatus.ACTIVE;
    private LocalDate start_date;
    private LocalDate end_date;
    private ArrayList<ComputationStep> steps;


    /// costruisce la computazione base con il primo computation step
    public Computation(PetriNet petriNet, String user) {
        if (user.equals(petriNet.getAdminName())) throw new InvalidUserException();
        count++;
        this.ID = count;
        this.petriNet = petriNet;
        this.petriNetID = petriNet.getID();
        this.user = user;
        this.start_date = LocalDate.now();

        Marking markingData = new Marking();
        for (Place p : getPetriNet().getPlaces()) {
            markingData.getMarking().put(p.getID(), 0); // metto tutto a 0
        }
        //modificato Ari preplace-> put
        markingData.getMarking().put(getPetriNet().getInitialPlace(), 1);   // metto a 1 il posto iniziale

        ComputationStep compStep = new ComputationStep(this.ID, markingData, 0, petriNet.getInitialPlaceName());
        this.steps = new ArrayList<ComputationStep>();
        steps.add(compStep);

    }

    /// / costruttore per db
    public Computation(int id, int petriNetID, String user, LocalDate start_date, LocalDate end_date, ArrayList<ComputationStep> steps, ComputationStatus status) {
        this.ID = id;
        this.petriNetID = petriNetID;
        this.user = user;
        this.start_date = LocalDate.now();
        this.steps = steps;
        this.status = status;
    }

    public Computation(int id, PetriNet petriNet, String user, LocalDate start_date, LocalDate end_date, ArrayList<ComputationStep> steps, ComputationStatus status) {
        this.ID = id;
        this.petriNet = petriNet;
        this.user = user;
        this.start_date = LocalDate.now();
        this.steps = steps;
        this.status = status;
    }

    /// / metodi accessori

    public PetriNet getPetriNet() {
        return petriNet;
    }

    public int getPetriNetID() {
        return petriNetID;
    }

    public int getID() {
        return ID;
    }

    public String getUser() {
        return user;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public static int getCount() {
        return count;
    }

    public ComputationStatus getStatus() {
        return status;
    }

    public ArrayList<ComputationStep> getSteps() {
        return steps;
    }

    /// metodi set
    public static void setCount(int count) {
        Computation.count = count;
    }

    public void setComputationSteps(ArrayList<ComputationStep> steps) {
        this.steps = steps;
    }

    public void setNet(PetriNet petriNet) {
        this.petriNet = petriNet;
    }


    /// / verifica che la transizione sia attivabile
    public boolean transitionEnabled(Transition t) {
        if (!petriNet.getTransitions().contains(t)) throw new RuntimeException("Invalid transition");
        HashSet<Arc> arcsToTransition = new HashSet<>(); // sottoinsieme degli archi diretti verso t
        for (Arc a : petriNet.getArcs()) {
            if (a.getTargetID() == t.getID())
                arcsToTransition.add(a);
        }
        //aggiungo controllo user/admin transition
        for (Arc a : arcsToTransition) {

            for (Place p : petriNet.getPlaces()) {
                if( getLastStep().getMarkingData().getMarking().get(p.getID())==null){
                    getLastStep().getMarkingData().getMarking().put(p.getID(), 0);
                    getLastStep().getMarkingData().getMarking().put(petriNet.getInitialPlace(), 1);
                }
            }

            if (getLastStep().getMarkingData().getMarking().get(a.getSourceID()) == 0)
                return false; //se almeno uno dei place che va in t ha 0 token, allora t non è attivabile
        }
        return true;
    }

    /// restituisce lista di transizioni attivabili
    public ArrayList<Transition> enabledTransitions() {
        ArrayList<Transition> res = new ArrayList<>();
        for (Transition t : getPetriNet().getTransitions()) {
            if (transitionEnabled(t))
                res.add(t);
        }
        return res;
    }

    public ArrayList<Transition> adminEnabledTransitions() {
        ArrayList<Transition> res = new ArrayList<>();
        for (Transition t : enabledTransitions())
            if (t.getUserType().equals(UserType.ADMIN))
                res.add(t);
        return res;
    }

    public ArrayList<Transition> enduserEnabledTransitions() {
        ArrayList<Transition> res = new ArrayList<>();
        for (Transition t : enabledTransitions())
            if (t.getUserType().equals(UserType.ENDUSER))
                res.add(t);
        return res;
    }

    /// / attiva transizione e aggiorna steps con il nuovo Computation Step
    public int fireTransition(Transition t) {
        if (!petriNet.getTransitions().contains(t)) throw new RuntimeException("Invalid transition");
        if (!transitionEnabled(t)) throw new RuntimeException("Transition is not enabled");

        Marking newMarkingData = new Marking();
        newMarkingData.getMarking().putAll(getLastStep().getMarkingData().getMarking());

        // per ogmi arco
        for (Arc a : petriNet.getArcs()) {
            //se t è il suo terget
            if (a.getTargetID() == t.getID()) {
                //prendo il marking della source
                int placeID = a.getSourceID();
                int old = newMarkingData.getMarking().get(placeID);
                // gli sottraggo un token
                newMarkingData.getMarking().put(placeID, old - 1);
            }
        }

        // come sopra ma sommando 1 alla destinazione
        for (Arc a : petriNet.getArcs()) {
            if (a.getSourceID() == t.getID()) {
                int placeID = a.getTargetID();
                int old = newMarkingData.getMarking().get(placeID);
                newMarkingData.getMarking().put(placeID, old + 1);
            }
        }

        ComputationStep newStep = new ComputationStep(this.getID(), newMarkingData, t.getID(), t.getName());
        steps.add(newStep);

        if (isComplete())
            this.status = ComputationStatus.COMPLETED;

        return 0;
    }



    /// /verifica se la computazione è terminata
    public boolean isComplete() {
        for (Place p : petriNet.getPlaces()) {

            //aggiunta ari
                if(getLastStep().getMarkingData().getMarking().get(p.getID())==null) {
                    getLastStep().getMarkingData().getMarking().put(p.getID(), 0);
                    getLastStep().getMarkingData().getMarking().put(petriNet.getInitialPlace(), 1);
                }
            if (petriNet.checkFinalPlace(p) && getLastStep().getMarkingData().getMarking().get(p.getID()) > 0) {
                this.end_date = LocalDate.now();
                return true;
            }// metodo applicabile solo a rete valida, deve esserci un solo final place
        }
        return false;
    }

    public ComputationStep getLastStep() {
        if (steps.isEmpty()) return null;
        return steps.get(steps.size() - 1);
    }

    public String toString() {
        return "Enduser: " + user.toString() + " Petrinet: " + petriNet.getName();
    }
    
    //DEBUG
    public void setStatus() {
        if (this.status.equals(ComputationStatus.COMPLETED)) {
            status = ComputationStatus.ACTIVE;
        }
       else if (this.status.equals(ComputationStatus.ACTIVE)) {
                status = ComputationStatus.COMPLETED;
        }
    }
}