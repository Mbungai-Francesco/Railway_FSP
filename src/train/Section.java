package train;

/**
 * Représentation d'une section de voie ferrée. C'est une sous-classe de la
 * classe {@link Element}.
 *
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Section extends Element {
	private Train occupyingTrain = null;

	public Section(String name) {
		super(name);
	}

	/**
	 * A train enters the section (max 1 train per section)
	 * Waits if the section is already occupied
	 * @param train the train object
	 */
	@Override
	public synchronized void enter(Train train) {
		// Wait while section is occupied
		while(occupyingTrain != null || !train.sameDirection()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		// Now occupy the section
		occupyingTrain = train;
	}

	/**
	 * A train leaves the section
	 * @param train the train object
	 */
	@Override
	public synchronized void leave(Train train) {
		if(occupyingTrain != null && occupyingTrain.equals(train)) {
			occupyingTrain = null;
			this.notifyAll();
		}
	}

	/**
	 * Check if the section is occupied
	 * @return true if the section has a train
	 */
	public synchronized boolean isOccupied() {
		return occupyingTrain != null;
	}
}