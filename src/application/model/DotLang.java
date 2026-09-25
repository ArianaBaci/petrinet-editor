package application.model;
/*
 * Classe statica con un solo metodo che prende un oggetto rete di Petri e lo traduce in DOT Language
 * A tempo debito avrebbe più senso mettere questo metodo all'interno dell oggetto PetriNet
 */
public class DotLang{
	private static final String placeColor = "lightskyblue";
	private static final String adminColor = "lightcoral";
	private static final String userColor = "limegreen";
	private static final int maxNameLenght = 10;

	private static final String sizeSetting = "fixedsize=true ";

	private static final String placeStyle =
	"node["+sizeSetting+"width=1.4 shape=ellipse style=filled fillcolor="+placeColor+"];\n";

	private static final String adminStyle =
	"node["+sizeSetting+"width=1.2 height=0.45 shape=box style=filled fillcolor="+adminColor+"];\n";

	private static final String userStyle =
	"node["+sizeSetting+"width=1.2 height=0.45 shape=box style=filled fillcolor="+userColor+"];\n";
	
    public static String translate(PetriNet net) {

        String res = "digraph G {\n\n";

        //Crea tutti i posti
        res += placeStyle;
        for (Place p : net.getPlaces()) { 
				res += translateNode(p) + "; ";
        }
        res += "\n\n";

        res += adminStyle;
        for (Transition t : net.getTransitions()) {
        	if (t.getUserType()==UserType.ADMIN) {
				res += translateNode(t) + "; ";
        	}
        }
        res += "\n\n";
        
        res += userStyle;
        for (Transition t : net.getTransitions()) {
        	if (t.getUserType()==UserType.ENDUSER) {
				res += translateNode(t) + "; ";
        	}
        }
        res += "\n\n";

        for (Arc a : net.getArcs()) {
            res += translateNode(net.getNode(a.getSourceID()));
            res += " -> ";
            res += translateNode(net.getNode(a.getTargetID()));
            res += ";\n";
        }

        res += "\n}";
       
        Debugger.log("printing net:\n"+res);

        return res;

    }

    private static String translateNode(Node n) {
    	String res = n.getName();
    	if (res.length() > maxNameLenght) {
    		res = res.substring(0, maxNameLenght-3);
    		res += "...";
    	}
    	res = "\"" + res;
    	res += "\"";
    	return res;
    }
}