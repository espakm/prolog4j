package org.prolog4j;

public class Compound {

	private String functor;
	private Object[] args;
	
	public Compound(String functor, Object... args) {
		this.functor = functor;
		this.args = args;
	}
	
	public String getFunctor() {
		return functor;
	}

	public int getArity() {
		return args.length;
	}
	
	public Object[] getArgs() {
		return args.clone();
	}
	
	public Object getArg(int index) {
		return args[index];
	}
}
