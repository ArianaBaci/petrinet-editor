package application.model;

import application.view.ViewNavigator;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;


public class ComputationRepository {

    private final DatabaseManager dbManager;
    private final List<Computation> computationCache;

    public ComputationRepository(DatabaseManager dbManager, NetRepository netRepository) {
        this.dbManager = dbManager;
        computationCache = new ArrayList<>();
        ArrayList<Computation> computationCacheProv = new ArrayList<>(dbManager.getComputations());

        // aggiungo le petrinet a ciascuna computation costruita dal dbmanager
        for (Computation c : computationCacheProv) {
            int id = c.getPetriNetID();
            c.setNet(netRepository.getNet(c.getPetriNetID()));
            computationCache.add(c);
        }

        int newCount = 0;
        for (Computation c : computationCache) {
            if (c.getID() > newCount) {
                newCount = c.getID();
            }
        }
        Computation.setCount(newCount);
        Debugger.log("Db computation count: " + Computation.getCount());

        System.out.println(computationCache);
    }

    public boolean checkUser(Computation c) {
        for (Computation c2 : computationCache) {
            if (c2.getPetriNet().getID() == c.getPetriNet().getID() && c2.getUser().equals(c.getUser()) && c2.getStatus().equals(ComputationStatus.ACTIVE))
                return false;
        }
        return true;
    }

    public Computation getComputationByID(int id) {
        for (Computation c : computationCache) {
            if(c.getID() == id)
                return c;
        }
        return null;
    }

    public boolean saveComputation(Computation c, ComputationStepRepository computationStepRepository) {
        boolean success = dbManager.saveComputation(c);
        if (success) {
            computationCache.add(c);
            computationStepRepository.saveComputationStep(c.getLastStep());
        }
        if (!success) {
            System.out.println("Error saving computation");
        } else if (success) {
            System.out.println("Computation saved");
        }
        return success;
    }

    public boolean deleteComputation(Computation c) {
        boolean success=false;
        if (c != null) {
             success = dbManager.deleteComputation(c);
            if (success)
                computationCache.remove(c);
            if (!success) {
                System.out.println("Error deleting computation");
            } else if (success) {
                System.out.println("Computation deleted");
            }
        }
            return success;
    }

    public List<Computation> getComputations() {
        return computationCache;
    }

    public ArrayList<Transition> getEnabledTransitions() {
        ArrayList<Transition> ret = new ArrayList<>();
        for (Computation c : computationCache) {
            ret.addAll(c.enabledTransitions());
        }
        return ret;
    }

    public List<CompTransition> getEnabledUserTransitions(String endUser) {
        List<CompTransition> ret = new ArrayList<>();
        List<Computation> endUserComputations = new ArrayList<>();

        for (Computation c : computationCache) {
            if (c.getUser().equals(endUser)) {
                endUserComputations.add(c);
            }
        }
        for (Computation c : endUserComputations) {
            for (Transition t : c.enabledTransitions())
                if (t.getUserType().equals(UserType.ENDUSER))
                    ret.add(new CompTransition(t, c));
        }
        return ret;
    }

    public List<CompTransition> getEnabledAdminTransitions(String admin) {
        List<CompTransition> ret = new ArrayList<>();
        ArrayList<Computation> adminComputations = new ArrayList<>();
        for (Computation c : computationCache) {
            if (c.getPetriNet().getAdminName().equals(admin)) {
                adminComputations.add(c);
            }
        }
        for (Computation c : adminComputations) {
            for (Transition t : c.enabledTransitions())
                if (t.getUserType().equals(UserType.ADMIN))
                    ret.add(new CompTransition(t, c));
        }
        return ret;
    }

    public boolean subscribe(PetriNet petriNet, String user) {
        Computation c = new Computation(petriNet, user);
        boolean success=false;
        try {
             success = dbManager.saveComputation(c);
            if (success) {
                computationCache.add(c);
                dbManager.saveComputationStep(c.getLastStep());
                return true;
            } else {
                System.out.println("Error saving computation");
                return false;
            }
        } catch (Exception e) {
            ViewNavigator.navigateToMyNets();
            System.out.println("Error saving computation");
        }
      return success;
    }

    public String history(int computationID) {
        String ret = "";
        for (Computation c : computationCache) {
            if(c.getID() == computationID)
                for(ComputationStep step : c.getSteps())
                    ret = ret + "\n" + step.history();
        }
        return ret;
    }

}
