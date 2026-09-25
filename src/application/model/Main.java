package application.model;

import java.util.Iterator;

public class Main {
    public static void main(String[] args) {

        PetriNet pn = new PetriNet("admin");
        pn.addTransition("T0", (Place) pn.getNode(pn.getInitialPlace()),UserType.ENDUSER);
        pn.addPlace("P1",(Transition) pn.getNode("T0"));
        pn.addPlace("P2",(Transition) pn.getNode("T0"));
        pn.addPlace("END", (Transition) pn.getNode("default"));
        pn.addTransition("T1", (Place) pn.getNode("P1"),UserType.ENDUSER);
        pn.addArc(pn.getNode("T1"), pn.getNode("Final") );
        pn.addArc(pn.getNode("T0"), pn.getNode("END") );
        pn.addArc(pn.getNode("P2"), pn.getNode("T1") );
        pn.addArc(pn.getNode("END"), pn.getNode("T1") );
        pn.addTransition("FinalTransition", (Place) pn.getNode("Final"),UserType.ADMIN);
        pn.addPlace("finalissimo",(Transition) pn.getNode("FinalTransition"));


        DotLang.translate(pn);

        /*

        PetriNet pn = new PetriNet("admin");

        pn.addTransition("T0", (Place) pn.getNode(pn.getInitialPlace()),UserType.ENDUSER);
        pn.addPlace("P1",(Transition) pn.getNode("T0"));
        pn.addPlace("P2",(Transition) pn.getNode("T0"));
        pn.addPlace("END", (Transition) pn.getNode("default"));
        pn.addTransition("T1", (Place) pn.getNode("P1"),UserType.ENDUSER);
        pn.addArc(pn.getNode("T1"), pn.getNode("Final") );
        pn.addArc(pn.getNode("T0"), pn.getNode("END") ); //aggiunto per completare la computation
        pn.addArc(pn.getNode("P2"), pn.getNode("T1") );
        pn.addArc("END","T1");

        System.out.println(pn.getNode("T0").toString());
        System.out.println(pn.getNode(pn.getInitialPlace()).toString());
        System.out.println(pn.getArc(pn.getNode("P2"), pn.getNode("T1") ).toString());


        System.out.println(pn.getNodeName(1));
        System.out.println(pn.getNodeName(2));
        System.out.println(pn.getNodeName(3));
        System.out.println(pn.getNodeName(4));


        ///// prove sulle computation
        Computation c = new Computation(pn, "user");
        System.out.println(c.enabledTransitions());
        System.out.println(c.enduserEnabledTransitions());
        System.out.println(c.adminEnabledTransitions());
        c.fireTransition((Transition) pn.getNode("T0"));
        c.fireTransition((Transition) pn.getNode("T1"));
        System.out.println(c.enabledTransitions());

*/
        //System.out.println(c.getStatus());
        //System.out.println(c.getEnabledTransitions());
        //c.fireTransition((Transition) pn.getNode("T1"));
        //System.out.println(c.getEnabledTransitions());
        //System.out.println(c.getSteps());
        //System.out.println(c.getStatus());
    }
}
