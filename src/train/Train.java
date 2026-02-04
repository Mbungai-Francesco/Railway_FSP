package train;

/**
 * Representation of a train. A train is characterized by:
 * <ol>
 *   <li>Its name for display purposes</li>
 *   <li>The position it occupies in the circuit (an element with a direction): {@link Position}</li>
 * </ol>
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Mayte Segarra <mt.segarra@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 * @version 0.3
 */
public class Train implements Runnable {
	private final String name;
	private Position pos;
	private Railway rail;
	private boolean turned = true;

	public Train(String name, Position p, Railway rail) throws BadPositionForTrainException {
		if (name == null || p == null)
			throw new NullPointerException();

		// A train must initially be in a station
		if (!(p.getEle() instanceof Station))
			throw new BadPositionForTrainException(name);

		this.name = name;
		this.pos = p.clone();
		this.rail = rail;
		
		// Enter the initial station
		Station station = (Station) this.pos.getEle();
		station.enter(name);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Train[");
		result.append(this.name);
		result.append("]");
		result.append(" is on ");
		result.append(this.pos);
		return result.toString();
	}

	/**
	 * Moves the train to the next position
	 */
	public void move() {
		pos.move(rail, name);
	}

	/**
	 * Handles the train's current location logic
	 * If at a station and not yet turned, reverse direction
	 * Otherwise, move to the next element
	 */
	public void currentLocation(){
		if(pos.getEle() instanceof Station && !turned) {
			pos.reverseDirection();
			turned = true;
		}
		else {
			move();
			turned = false;
		}
	}

	/**
	 * Main execution loop for the train thread
	 * Continuously moves the train with a 1.5 second delay between movements
	 */
	@Override
	public void run() {
		while(true) {
			currentLocation();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
