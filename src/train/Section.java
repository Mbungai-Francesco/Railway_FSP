package train;

/**
 * Representation of a railway section. This is a subclass of {@link Element}.
 * A section can contain at most one train at a time.
 *
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Section extends Element {
	private String occupyingTrain = null;
	private Direction trainDirection = null;  // Q3.1: Direction of the occupying train

	public Section(String name) {
		super(name);
	}

	/**
	 * A train enters the section with a specific direction.
	 * Q3.2: Waits if the section is occupied by another train.
	 * Ensures maximum one train per section.
	 * 
	 * @param trainName the name of the train entering
	 * @param direction the direction of movement
	 */
	public synchronized void enter(String trainName, Direction direction) {
		// Wait while section is occupied by a different train
		while(occupyingTrain != null && !occupyingTrain.equals(trainName)) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		// Occupy the section with direction
		occupyingTrain = trainName;
		trainDirection = direction;
	}

	/**
	 * A train enters the section (backward compatible method without direction)
	 * 
	 * @param trainName the name of the train entering
	 */
	@Override
	public synchronized void enter(String trainName) {
		// Wait while section is occupied by another train
		while(occupyingTrain != null && !occupyingTrain.equals(trainName)) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		// Occupy the section
		occupyingTrain = trainName;
	}

	/**
	 * A train leaves the section, freeing it for other trains
	 * 
	 * @param trainName the name of the train leaving
	 */
	@Override
	public synchronized void leave(String trainName) {
		if(occupyingTrain != null && occupyingTrain.equals(trainName)) {
			occupyingTrain = null;
			trainDirection = null;
			this.notifyAll();
		}
	}

	/**
	 * Checks if the section is currently occupied
	 * 
	 * @return true if a train is in the section, false otherwise
	 */
	public synchronized boolean isOccupied() {
		return occupyingTrain != null;
	}

	/**
	 * Gets the direction of the train currently occupying the section
	 * 
	 * @return the direction or null if the section is empty
	 */
	public synchronized Direction getTrainDirection() {
		return trainDirection;
	}
}