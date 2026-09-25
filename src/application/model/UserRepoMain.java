package application.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class UserRepoMain {

	public static void main(String[] args) {
		DatabaseManager dbManager = new DatabaseManager();
		UserRepository userRepository = new UserRepository(dbManager);
		NetRepository netRepository = new NetRepository(dbManager);
		ComputationRepository computationRepository = new ComputationRepository(dbManager, netRepository);
		ComputationStepRepository computationStepRepository = new ComputationStepRepository(dbManager);




		/*
		System.out.println(c.getPetriNet().getName() + c.getUser() + c.getID() + "\n" + c.getPetriNet().getTransitions());
		c.fireTransition(c.enabledTransitions().getFirst());

		if(c.adminEnabledTransitions()!=null)
			c.fireTransition(c.enabledTransitions().getLast());

		computationRepository.saveComputation(c);
		*/

/*



		Computation c = new Computation(netRepository.getNet(9),"pippo");
		computationRepository.saveComputation(c);
		c.fireTransition(c.enabledTransitions().getFirst());
		computationRepository.saveComputation(c);

		Computation c1 = new Computation(netRepository.getNet(6),"pippo");
		System.out.println(computationRepository.checkUser(c1));

		Computation c2 = new Computation(netRepository.getNet(6),"noemi");
		System.out.println(computationRepository.checkUser(c2));

		System.out.println(computationRepository.checkUser(c));
		Computation c1 = new Computation(netRepository.getNet(5),"pippo");
		System.out.println(computationRepository.checkUser(c1));
		computationRepository.saveComputation(c1);

		 */



		//userRepository.registerUser("pippo", "pippo");


		//Computation c = new Computation(netRepository.getNet(4),"noemi");
		//computationRepository.saveComputation(c);
		//c.fireTransition(c.enduserEnabledTransitions().getFirst());
		//computationRepository.saveComputation(c);
		//computationStepRepository.saveComputationStep(c.getLastStep());








/*
		Scanner s = new Scanner(System.in);
		while (userRepository.loggedUser == null) {
			System.out.println("Inserire username:");
			String name = s.nextLine();
			System.out.println("Inserire password:");
			String password = s.nextLine();
			userRepository.loginUser(name, password);
		}
		s.close();

		PetriNet pn = netRepository.createDefaultNet(userRepository.loggedUser);

		netRepository.saveNet(pn);


		/// prove computation

		Computation c = new Computation(pn,"admin");
		computationRepository.saveComputation(c);
		ComputationStep cs = c.getLastStep();
		computationStepRepository.saveComputationStep(cs);
		c.fireTransition(c.enabledTransitions().getFirst());
		cs = c.getLastStep();
		computationStepRepository.saveComputationStep(cs);
		c.fireTransition(c.enabledTransitions().getFirst());
		cs = c.getLastStep();
		computationStepRepository.saveComputationStep(cs);


		System.out.println("stampo tot transiz attivabili:\n" + computationRepository.getEnabledTransitions());
		System.out.println("stampo transiz attivabili dall'end user pippo:\n" + computationRepository.getEnabledUserTransitions("pippo"));
		System.out.println("stampo transiz attivabili dall'end user noemi:\n" + computationRepository.getEnabledUserTransitions("noemi"));
		System.out.println("stampo transiz attivabili dall'admin admin:\n" + computationRepository.getEnabledAdminTransitions("admin"));
		System.out.println("stampo transiz attivabili dall'admin pippo:\n" + computationRepository.getEnabledAdminTransitions("pippo"));
 */








		/*
			RETI PER FARE PROVE

		PetriNet pn = new PetriNet("rete bella","pippo");

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
		pn.addPlace("Pn",(Transition) pn.getNode("T1"));
		pn.addArc(pn.getNode("Pn"), pn.getNode("FinalTransition") );

		PetriNet pn = new PetriNet("noemi");

		pn.addTransition("T0", (Place) pn.getNode(pn.getInitialPlace()),UserType.ENDUSER);
		pn.addPlace("P1",(Transition) pn.getNode("T0"));
		pn.addPlace("P2",(Transition) pn.getNode("T0"));
		pn.addPlace("END", (Transition) pn.getNode("default"));
		pn.addTransition("T1", (Place) pn.getNode("P1"),UserType.ENDUSER);
		pn.addArc(pn.getNode("T1"), pn.getNode("Final") );
		pn.addArc(pn.getNode("T0"), pn.getNode("END") ); //aggiunto per completare la computation
		pn.addArc(pn.getNode("P2"), pn.getNode("T1") );
		pn.addArc("END","T1");

		pn.setName("PetriNet x");
		pn.addTransition("Tx", (Place) pn.getNode(pn.getInitialPlace()),UserType.ENDUSER);
		pn.addPlace("Px",(Transition) pn.getNode("Tx"));
		pn.addPlace("P2",(Transition) pn.getNode("Tx"));
		pn.addPlace("END", (Transition) pn.getNode("x"));
		pn.addTransition("T1", (Place) pn.getNode("Px"),UserType.ENDUSER);
		pn.addArc(pn.getNode("T1"), pn.getNode("b") );
		pn.addArc(pn.getNode("P2"), pn.getNode("T1") );


		pn.setName("PetriNet simpatica");
		pn.addPlace("p", (Transition) pn.getNode("x"));
		pn.addTransition("t", (Place) pn.getNode("p"),UserType.ENDUSER);
		pn.addArc(pn.getNode("t"), pn.getNode("b") );

		 */
	}
}