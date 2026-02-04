package train;

/**
 * Représentation d'une gare. C'est une sous-classe de la classe {@link Element}.
 * Une gare est caractérisée par un nom et un nombre de quais (donc de trains
 * qu'elle est susceptible d'accueillir à un instant donné).
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Station extends Element {
	private final int size;
	private int trainsInside = 0;

	public Station(String name, int size) {
		super(name);
		if(name == null || size <=0)
			throw new NullPointerException();
		this.size = size;
	}

	/**
	 * A train enters the station
	 * Waits if the station is full (trainsInside >= size)
	 * @param trainName the name of the train
	 */
	@Override
	public synchronized void enter(String trainName) {
		// Wait while station is full
		while(trainsInside >= size) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		// Now enter the station
		trainsInside++;
	}

	/**
	 * A train leaves the station
	 * @param trainName the name of the train
	 */
	@Override
	public synchronized void leave(String trainName) {
		trainsInside--;
		this.notifyAll();
	}

	/**
	 * Check if the station is full
	 * @return true if the station is at capacity
	 */
	public synchronized boolean isFull() {
		return trainsInside >= size;
	}
}
