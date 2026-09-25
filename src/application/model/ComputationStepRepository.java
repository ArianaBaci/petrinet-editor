package application.model;

import java.util.ArrayList;
import java.util.List;

public class ComputationStepRepository {
    private final DatabaseManager dbManager;
    private List<ComputationStep> computationStepsCache;


    public ComputationStepRepository(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        computationStepsCache = dbManager.getComputationSteps();

        System.out.println(computationStepsCache);
        int newCount = 0;
        for (ComputationStep cs : computationStepsCache) {
            if (cs.getID() > newCount) {
                newCount = cs.getID();
            }
        }
       ComputationStep.setCount(newCount);
       Debugger.log("Db computation steps count: " + ComputationStep.getCount());
    }

    public void saveComputationStep(ComputationStep cs) {
        boolean success = dbManager.saveComputationStep(cs);
        if (success)
            computationStepsCache.add(cs);
    }

    public String history(int computationID, ComputationRepository computationRepository){
        String ret = "";
        Computation c = computationRepository.getComputationByID(computationID);
        String user = c.getUser();
        PetriNet pn = c.getPetriNet();

        for (ComputationStep cs : computationStepsCache) {
            if (cs.getComputationID() == computationID) {
                if(cs.getTransitionID() == 0);
                else
                    ret = ret + cs.getTimestamp() +  "\tcomputation ID: " + computationID + ", user: " + user + ", fired transition: " + pn.getTransitionName((cs.getTransitionID())) + ", marking: " + cs.getMarkingData() + "\n";
            }
        }
        return ret;
    }
    public String singleStepString(int computationID, ComputationRepository computationRepository){
        Computation c = computationRepository.getComputationByID(computationID);
        String user = c.getUser();
        PetriNet pn = c.getPetriNet();
        ComputationStep cs=computationStepsCache.getLast();
        return "Transition "+pn.getTransitionName((cs.getTransitionID())) + "Fired in date: " +cs.getTimestamp();
    }

    public List<ComputationStep> getComputationSteps() {
    return computationStepsCache;
}

    public boolean deleteComputationStep(ComputationStep c) {
        boolean success = dbManager.deleteComputationStep(c);
        if (success)
            computationStepsCache.remove(c);
        if (!success) {
            System.out.println("Error deleting computation");
        } else if (success) {
            System.out.println("Computation deleted");
        }
        return success;
    }


}

