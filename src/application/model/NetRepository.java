package application.model;


import javafx.scene.image.Image;

import java.util.*;
import java.time.LocalDate;



public class NetRepository {

	private final DatabaseManager dbManager;
	private List<PetriNet> netCache;
	private Map<PetriNet, Image> imageCache;

	public NetRepository(DatabaseManager dbManager) {
		this.dbManager = dbManager;
		netCache = dbManager.getNets();
		
		System.out.println(netCache);
		int newCount = 0;
		for (PetriNet n: netCache) {
			if (n.getID()>newCount) {
				newCount = n.getID();
			}
		}
		PetriNet.setCount(newCount);
		Debugger.log("Db net count: " + PetriNet.getCount());
		imageCache = new HashMap<PetriNet, Image>();
		for (PetriNet n: netCache) {
			System.out.println("creating image for net "+ n.getName());
			imageCache.put(n, NetImage.createImage(n));
		}
	}
	
	public boolean saveNet(PetriNet n) {
		boolean success = dbManager.saveNet(n);
		
		if (success) {
			if (!netCache.contains(n)) {
			netCache.add(n);
			}
			imageCache.put(n, NetImage.createImage(n));
		}
		return success;
	}

	public boolean deleteNet(PetriNet n) {
		boolean success = dbManager.deleteNet(n);
		
		if (success) {
			imageCache.remove(n);
		}
		return success;
	}
	
	private List<PetriNet> getNets() {
		return netCache;
	}
	
	public PetriNet createDefaultNet(User creator) {
		String admin = creator.getName();
        PetriNet.setCount(PetriNet.getCount()+1);
        PetriNet n = new PetriNet(PetriNet.getCount(), "PetriNet"+PetriNet.getCount(), admin, 0, 0, LocalDate.now(), false);
        Place InitialPlace = new Place("a", n.getID());
        Place FinalPlace = new Place("b", n.getID());
        n.setInitialPlace(InitialPlace.getID());
        
        n.addPlace(InitialPlace);
        n.addPlace(FinalPlace);
        
        Transition default_transition = new Transition("x", n.getID(), UserType.ADMIN);
        n.addTransition(default_transition);
        n.makeNodesThroughUnion();
        
        Arc arcInitial = new Arc(n.getID(), InitialPlace, default_transition);
        Arc arcFinal = new Arc(n.getID(), default_transition, FinalPlace);
        n.addArc(arcInitial);
        n.addArc(arcFinal);
        
        return n;
	}

	public PetriNet getNet(int id)
	{
		for( PetriNet pn : netCache)
			if(pn.getID() == id)
				return pn;
		return null;
	}

	public Map<PetriNet, Image> getImages() {
		return imageCache;
	}
	
	public List<PetriNet> getPublishedNets() {
		List<PetriNet> res = new ArrayList<>();
		for (PetriNet n: netCache) {
			if (n.getPublished()) {
				res.add(n);
			}
		}
		return res;
	}

	public List<PetriNet> getDraftNets() {
		List<PetriNet> res = new ArrayList<>();
		for (PetriNet n: netCache) {
			if (!n.getPublished()) {
				res.add(n);
			}
		}
		return res;
	}
	
}
