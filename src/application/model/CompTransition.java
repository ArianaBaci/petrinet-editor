package application.model;

public class CompTransition {
	private Transition t;
	private Computation comp;
	
	CompTransition (Transition t, Computation comp) {
		this.t = t;
		this.comp = comp;
	}
	
	public Transition getTransition() {
		return t;
	}
	
	public Computation getComputation() {
		return comp;
	}
}
