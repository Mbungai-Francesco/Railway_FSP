package train;

/**
 * Cette classe abstraite est la représentation générique d'un élément de base d'un
 * circuit, elle factorise les fonctionnalitÃ©s communes des deux sous-classes :
 * l'entrée d'un train, sa sortie et l'appartenance au circuit.<br/>
 * Les deux sous-classes sont :
 * <ol>
 *   <li>La représentation d'une gare : classe {@link Station}</li>
 *   <li>La représentation d'une section de voie ferrée : classe {@link Section}</li>
 * </ol>
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public abstract class Element {
	private final String name;
	protected Railway railway;
	private String occupyingTrain = null;
	private Direction occupyingDirection = null;
	private final Object elementLock = new Object();

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

	public synchronized void enter(Train trainName) {
		// To be implemented in subclasses
	}

	public synchronized void leave(Train trainName) {
		// To be implemented in subclasses
	}

	/**
	 * Attempts to occupy this element with a train moving in a given direction.
	 * Waits if the element is occupied by a train moving in the opposite direction.
	 * @param trainName the name of the train trying to occupy this element
	 * @param direction the direction the train is moving
	 */
	// public void occupy(String trainName, Direction direction) {
	// 	synchronized(elementLock) {
	// 		// Wait if occupied by another train moving in a different direction
	// 		while(occupyingTrain != null && 
	// 			  !occupyingTrain.equals(trainName) && 
	// 			  occupyingDirection != direction) {
	// 			try {
	// 				elementLock.wait();
	// 			} catch (InterruptedException e) {
	// 				Thread.currentThread().interrupt();
	// 			}
	// 		}
	// 		// Now occupy the element
	// 		occupyingTrain = trainName;
	// 		occupyingDirection = direction;
	// 	}
	// }

	/**
	 * Releases the occupation of this element by a train.
	 * @param trainName the name of the train releasing this element
	 */
	// public void release(String trainName) {
	// 	synchronized(elementLock) {
	// 		if(occupyingTrain != null && occupyingTrain.equals(trainName)) {
	// 			occupyingTrain = null;
	// 			occupyingDirection = null;
	// 			elementLock.notifyAll();
	// 		}
	// 	}
	// }

	/**
	 * Checks if this element is free or occupied by the given train
	 * @param trainName the name of the train to check
	 * @return true if free or occupied by the same train, false if occupied by another train
	 */
	public boolean isFree(String trainName) {
		synchronized(elementLock) {
			return occupyingTrain == null || occupyingTrain.equals(trainName);
		}
	}
}
