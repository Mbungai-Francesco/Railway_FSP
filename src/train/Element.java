package train;

/**
 * Abstract class representing a generic element of a railway circuit.
 * It provides common functionalities for the two subclasses:
 * train entry, train exit, and belonging to a circuit.
 * <br/>
 * The two subclasses are:
 * <ol>
 *   <li>Station representation: {@link Station}</li>
 *   <li>Railway section representation: {@link Section}</li>
 * </ol>
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public abstract class Element {
	private final String name;
	protected Railway railway;

	protected Element(String name) {
		if(name == null)
			throw new NullPointerException();
		
		this.name = name;
	}

	public void setRailway(Railway r) {
		if(r == null)
			throw new NullPointerException();
		
		this.railway = r;
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * A train enters this element
	 * @param trainName the name of the train entering
	 */
	public abstract void enter(String trainName);

	/**
	 * A train leaves this element
	 * @param trainName the name of the train leaving
	 */
	public abstract void leave(String trainName);
}
