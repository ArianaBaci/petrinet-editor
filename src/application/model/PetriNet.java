package application.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PetriNet {
    private static int count = 0; //contatore per generare ID
    private int ID;
    private String name;
    private int initialPlace;
    private int finalPlace;

    private Set<Place> places;
    private Set<Transition> transitions;
    private Set<Node> nodes;
    private Set<Arc> arcs;
    private String admin;// (creator)
    private final LocalDate dateCreated;
    private boolean published;

    //COSTRUTTORE GENERICO per ricostruire la rete da DB
    
    public PetriNet(int ID, String name, String admin, int initialPlace, int finalPlace, LocalDate dateCreated, boolean pub) {
    	this.ID = ID;
    	this.name = name;
    	this.admin = admin;
    	this.initialPlace = initialPlace;
    	this.finalPlace = finalPlace;
    	this.dateCreated = dateCreated;
    	this.places = new HashSet<Place>();
    	this.transitions = new HashSet<Transition>();
    	this.arcs = new HashSet<Arc>();
    	this.nodes = new HashSet<Node>();
    	this.published = pub;
    }

    //COSTRUTTORI (con nome scelto dall'admin della rete o con nome messo di default)

    public PetriNet(String name, String admin) {
        count++;
        this.ID = count;
        this.name = name;
        this.admin = admin;
        Place InitialPlace = new Place("Initial", this.ID);
        Place FinalPlace = new Place("Final", this.ID);
        this.initialPlace = InitialPlace.getID();

        this.places = new HashSet<>();
        places.add(InitialPlace);
        places.add(FinalPlace);
        Transition default_transition = new Transition("default", this.ID, UserType.ADMIN);
        transitions = new HashSet<>();
        transitions.add(default_transition);
        this.nodes = new HashSet<>();
        nodes.addAll(transitions);
        nodes.addAll(places);
        Arc arcInitial = new Arc(this.ID, InitialPlace, default_transition);
        Arc arcFinal = new Arc(this.ID, default_transition, FinalPlace);
        arcs = new HashSet<>();
        arcs.add(arcInitial);
        arcs.add(arcFinal);
        this.dateCreated = LocalDate.now(); //verificare sia ok
    }
    public PetriNet (String admin){
        count++;
        this.ID = count;
        this.admin = admin;
        this.name = "PetriNet" + this.ID;
        Place InitialPlace = new Place("Initial", this.ID);
        Place FinalPlace = new Place("Final", this.ID);
        this.initialPlace = InitialPlace.getID();

        this.places = new HashSet<>();
        places.add(InitialPlace);
        places.add(FinalPlace);
        Transition default_transition = new Transition("default", this.ID, UserType.ADMIN);
        transitions = new HashSet<>();
        transitions.add(default_transition);
        this.nodes = new HashSet<>();
        nodes.addAll(transitions);
        nodes.addAll(places);
        Arc arcInitial = new Arc(this.ID, InitialPlace, default_transition);
        Arc arcFinal = new Arc(this.ID, default_transition, FinalPlace);
        arcs = new HashSet<>();
        arcs.add(arcInitial);
        arcs.add(arcFinal);
        this.dateCreated = LocalDate.now();
    }


    //  METODI ACCESSORI
    public static int getCount() {
        return count;
    }
    public int getID() {
        return ID;
    }
    public String getName() {
        return name;
    }
    public int getInitialPlace() {
        return initialPlace;
    }
    public String getInitialPlaceName() {
        return getNode(getInitialPlace()).name;
    }
    public Set<Place> getPlaces() {
        return places;
    }
    public Set<Arc> getArcs() {
        return arcs;
    }
    public Set<Transition> getTransitions() {
        return transitions;
    }
    public Set<Node> getNodes() { return nodes; }
    public LocalDate getDateCreated() { return dateCreated; }

    //ottieni name del Node a partire dall'ID
    public String getNodeName(int ID) {
        if(getNode(ID)!=null && getNode(ID) instanceof Transition) {
            Transition t = (Transition) getNode(ID);
            return t.getName();
        }
        if(getNode(ID)!=null && getNode(ID) instanceof Place) {
            Place p = (Place) getNode(ID);
            return p.getName();
        }
        else
            throw new NodeNotFoundException("No node with ID " + ID + " found");
    }

    public String getTransitionName(int ID) {
        for(Transition t : transitions) {
            if(t.getID() == ID) {
                return t.getName();
            }
        }
        return null;
    }

    ///////////  METODI SET NAME  /////////////

    //imposta il nome della petri
    public void setName(String name) {
        this.name=name;
    }

    //imposta nome delle componenti controllando che non siano già usati
    // (non consente ambiguità place/transition)

    public void setNodeName(String node, String name) {
        if(getNode(node)==null) throw new NodeNotFoundException("Node not found");
        
        if (name.equals("node")) {
        	throw new NodeNameInvalidException("You can't use this name");
        }
        
        for (Node n : getNodes()) {
        	if (n.getName().equals(name)) {
        		throw new NodeNameInvalidException("The name "+name+" is already in use");
        	}
        }
        getNode(node).name = name;
        for (Arc arc : getArcs()) {
            if (arc.getSourceName().equals(node)) {
                arc.SourceName=name;
            } else if (arc.getTargetName().equals(node)) {
                arc.TargetName=name;
            }
        }
    }

    /// /metodo per aggiornare nodes quando si fa una modifica a places o transitions
    ///
    public int updateNodes(){
        nodes.clear();
        nodes.addAll(transitions);
        nodes.addAll(places);
        return nodes.size();
    }

    ///////////////      RICERCA NODO/ARCO    //////////////

    //ricerca Node per name e restituisce il nodo
    public Node getNode(String name) {
        for(Node node : nodes){
            if(node.getName().equals(name) && node instanceof Transition){
                return (Transition)node;
            }
            if(node.getName().equals(name) && node instanceof Place){
                return (Place)node;
            }
        }
        return null;
    }

    //ricerca Node per ID e restituisce il nodo trovato
    public Node getNode(int ID) {
        for(Node node : nodes){
            if(node.getID() == ID){
                return node;
            }
        }
        return null;
    }

    ////ricerca Arc per (source,target) e restituisce l'arco
    public Arc getArc(Node source, Node target) {
        if (source instanceof Place && target instanceof Place || source instanceof Transition && target instanceof Transition)
            throw new SourceTypeNotValidException();
        else {
            for (Arc arc : arcs) {
                if (arc.getSourceID() == source.ID && arc.getTargetID() == target.ID) {
                    return arc;
                }
            }
        }
        return null;
    }

    ////////////         AGGIUNTA NODI E ARCHI    ///////////////

    //creazione Place a partire da Transition
    public int addPlace(String name, Transition source) {

        for (Place place : places) {
            if (place.getName().equals(name)) throw new NameOrArcAlreadyInUseException();
        }
        for (Transition transition : transitions) {
            if (transition.getName().equals(name)) throw new NameOrArcAlreadyInUseException();
        }
        Place newPlace = new Place(name, this.ID);
        places.add(newPlace);
        addArc(source, newPlace);
        if(updateNodes() == (transitions.size() + places.size()))
            return 0;
        else
            return -1;
    }

    public int addPlace(String name, String source) {
        if (getNode(source) == null || !(getNode(source) instanceof Transition)) throw new SourceTypeNotValidException();

        return addPlace(name, (Transition) getNode(source));
    }

    //creazione Transition a partire da Place
    public int addTransition(String name, Place source, UserType type) {;
        for (Transition transition : transitions) {
            if (transition.getName().equals(name)) throw new NameOrArcAlreadyInUseException();
        }
        for (Place place : places) {
            if (place.getName().equals(name)) throw new NameOrArcAlreadyInUseException();
        }
        Transition newTransition = new Transition(name, this.ID, type);
        transitions.add(newTransition);
        addArc(source, newTransition);
        if(updateNodes() == (transitions.size() + places.size()))
            return 0;
        else
            throw new RuntimeException("Generic error adding transition");
    }

    public int addTransition(String name, String source, UserType type) {
        if (getNode(source) == null || !(getNode(source) instanceof Place)) throw new SourceTypeNotValidException();

        return addTransition(name, (Place) getNode(source), type);
    }

    //creazione Arc che unisce nodi già esistenti
    public int addArc(Node source, Node target) {
        if(source instanceof Place && target instanceof Place || target instanceof Transition && source instanceof Transition){
            System.out.println("Place-to-Place or Transition-to-Transition arc not permitted");
            throw new SourceTypeNotValidException();
        }
        if (getArc(source, target)!=null || getArc(target, source)!=null) throw new NameOrArcAlreadyInUseException();
        else{
            if (source instanceof Place && places.contains(source)) {
                Arc newArc = new Arc(this.ID, source, target);
                arcs.add(newArc);
                return 0;
            }
            if (source instanceof Transition && transitions.contains(source)) {
                Arc newArc = new Arc(this.ID, source, target);
                this.arcs.add(newArc);
                return 0;
            }
        }
        return -1;
    }
    public int addArc(String source, String target) {
        if (getNode(source)==null|| getNode(target)==null)
            throw new NodeNotFoundException("Source or target node not found");
        Node source_node = getNode(source);
        Node target_node = getNode(target);
        return addArc(source_node,target_node);
    }


    /////////////           RIMOZIONE NODI E ARCHI     ////////////////////////
    public int removeArc(Node source, Node target) {
        if (getArc(source,target)==null) throw new NodeNotFoundException("Arc doesn't exists");

        Arc arc = getArc(source, target);
                arcs.remove(arc);
                return 0;
    }

    public int removeArc(String source, String target) {
        if (getNode(source)==null || getNode(target)==null)
            throw new NodeNotFoundException("Node doesn't exist");
        Node source_node = getNode(source);
        Node target_node = getNode(target);
        return removeArc(source_node,target_node);
    }

    public void removeNode(int ID) {
        if (getNode(ID) == null) throw new NodeNotFoundException("Node with ID " + ID + " not found");
        if (getInitialPlace() == ID) throw new ElementCantBeRemovedException();
        int check = 0; //serve per verificare se gli archi uscenti dal nodo da cancellare lasciano orfani
        Iterator<Arc> iterator = arcs.iterator();
        Arc arc = null;
        while (iterator.hasNext()) {
            arc = iterator.next();
            if (arc.getSourceID() == ID){
                check = -1;
                for(Arc a:arcs) {
                    if(a.getTargetID() == arc.getTargetID() && a.getID()!=arc.getID()) {
                        check = 0;
                        break;
                    }
                }
            }
            if(check == -1) throw new ElementCantBeRemovedException();
        }

        //rimuovo gli archi entranti e uscenti dal nodo
        iterator = arcs.iterator();
        while (iterator.hasNext()) {
            arc = iterator.next();
            if (arc.getTargetID() == ID || arc.getSourceID() == ID)
            iterator.remove();  // rimuove in modo sicuro l'elemento corrente
        }
        //rimuovo il nodo
        if(getNode(ID) instanceof Transition)
            transitions.remove(getNode(ID));
        if(getNode(ID) instanceof Place)
            places.remove(getNode(ID));
        if(updateNodes() == (transitions.size() + places.size()))
            return ;
        else throw new RuntimeException("Generic error removing node");
    }

    public void removeNode(String name) {
        removeNode(getNode(name).getID());
    }

    // metodo che restituisce subset di nodi eliminabili
    public Set<Node> getRemovableNodes() {
        Set<Node> removableNodes = new HashSet<>();
        int count = 0;
        for(Transition transition:transitions) {
            count = 0;
            for(Arc arc:arcs) {
                if(arc.getSourceID() == transition.getID())
                    count++;
            }
            if(count == 0) {
                removableNodes.add(transition);
            }
        }
        for(Place place:places) {
            count = 0;
            for(Arc arc:arcs) {
                if(arc.getSourceID() == place.getID())
                    count++;
            }
            if(count == 0)
                removableNodes.add(place);
        }

        return removableNodes;
    }

    //METODI VERIFICA VALIDITA' DELLA RETE
    public boolean checkPetriNet() {
        int countInitialPlace = 0;
        int countFinalPlace = 0;
        int countOrphanTransitions = 0;
        int countPlaceIsSource = 0;
        int countPlaceIsTarget = 0;
        int transitionHasSource = 0;
        int transitionHasTarget = 0;
        for (Place place : places) {
            for (Arc arc : arcs) {
                if (arc.getSourceID() == place.getID()) {
                    countPlaceIsSource++;
                }
            }
            if (countPlaceIsSource == 0)
                countFinalPlace++;
            for (Arc arc : arcs) {
                if (arc.getTargetID() == place.getID()) {
                    countPlaceIsTarget++;
                }
            }
            if (countPlaceIsTarget == 0)
                countInitialPlace++;
            countPlaceIsSource = 0;
            countPlaceIsTarget = 0;

        }
        for (Transition transition : transitions) {
            for (Arc arc : arcs) {
                if (arc.getSourceID() == transition.getID())
                    transitionHasTarget++;
                if (transition.getID() == arc.getTargetID())
                    transitionHasSource++;
            }
            if (transitionHasTarget == 0 || transitionHasSource == 0)
                countOrphanTransitions++;
            transitionHasSource = 0;
            transitionHasTarget = 0;
        }
        if (countInitialPlace == 1 && countFinalPlace == 1 && countOrphanTransitions == 0)
            return true;
        else {
            if (countInitialPlace == 0)
                throw new NodeNotFoundException("initial place missing");
            if (countOrphanTransitions != 0)
                throw new OrphanTransitionException();
            if (countFinalPlace != 1)
                throw new MoreThanOneFInalPlaceException();

            return false;
        }
    }
    /// metodo che dato un place restituisce true se il place è final
    /// (Sarà utile per capire quando una computazione è completata)
    public boolean checkFinalPlace(Place p) {
        if(! places.contains(p)) throw new NodeNotFoundException("Place not found");
        for (Arc arc : arcs) {
            if (arc.getSourceID() == p.getID()) {
                return false;
            }
        }
        return true;
    }
    
    /* 
     * METODI AGGIUNTI DA MASSIMO
     */
    public static void setCount(int count) {
    	PetriNet.count = count;
    }
    
    public void setInitialPlace(int id) {
    	this.initialPlace = id;
    }
    
    public void addPlace(Place p) {
    	this.places.add(p);
    }
    
    public void addTransition(Transition t) {
    	this.transitions.add(t);
    }

    public void addArc(Arc a) {
    	this.arcs.add(a);
    }
    
    public void makeNodesThroughUnion() {
    	this.nodes = new HashSet<Node>();
    	this.nodes.addAll(this.places);
    	this.nodes.addAll(this.transitions);
    }
    
    public String getAdminName() {
    	return this.admin;
    }
    
    public int getFinalPlace() {
    	return this.finalPlace;
    }
    
    public void setID(int id) {
    	this.ID = id;
    }
    
    public boolean getPublished() {
    	return published;
    }
    
    public void setPublished(boolean published) {
    	this.published = published;
    }
    /* 
     * FINE METODI AGGIUNTI DA MASSIMO
     */

    @Override
    public String toString() {         return "nome rete: " + name + ", admin: " + admin;     }



    // Iteratori per scorrere arcs, places, transitions
    public Iterator<Place> getPlaceIterator() {
        return places.iterator();
    }
    public Iterator<Transition> getTransitionIterator() {
        return transitions.iterator();
    }
    public Iterator<Arc> getArcIterator() {
        return arcs.iterator();
    }


    //Metodo per impostare Place come iniziale
    public void setInitialPlace(Place p) {
        if (this.places.contains(p) && checkInitialPlace(p) == true)
            this.initialPlace = p.getID();
    }

    public boolean checkInitialPlace(Place p) {
        if(! places.contains(p)) throw new NodeNotFoundException("Place not found");
        for (Arc arc : arcs) {
            if (getNode(arc.getSourceID()) instanceof Transition || arc.getTargetID() == (p.getID())) {
                return false;
            }
        }
        return true;
    }

    public boolean isRemovable(Node node){
        if(node.getID()==initialPlace)
            return false;
       return (getRemovableNodes().contains(node)? true:false);
    }
    public boolean checkFinalTransiitiom(Node node){
        for (Arc arc : arcs) {
            if(arc.getSourceID() == node.getID() && node instanceof Transition){
                return  false;
            }
        }
        return true;
    }

   public ObservableList<Integer> ObsNodesNumber = FXCollections.observableArrayList();


}