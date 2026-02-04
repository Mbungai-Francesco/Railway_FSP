package train;

/**
 * Representation of a railway circuit consisting of railway elements: 
 * stations and sections.
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Railway {
	private Element[] elements;
	private Direction currentDirection = null;  // Direction of trains currently moving
	private int trainsMoving = 0;  // Number of trains currently in motion

	public Railway(Element[] elements) {
		if(elements == null)
			throw new NullPointerException();
		
		this.elements = elements;
		for (Element e : elements)
			e.setRailway(this);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Element e : this.elements) {
			if (first)
				first = false;
			else
				result.append("--");
			result.append(e);
		}
		return result.toString();
	}

	public Element[] getEl() {
		return elements;
	}

	/**
	 * Finds the index of a given element in the railway
	 * 
	 * @param element the element to find
	 * @return the index of the element, or -1 if not found
	 */
	public int indexOf(Element element) {
		for(int i = 0; i < elements.length; i++) {
			if(elements[i] == element) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the next element based on current position and direction
	 * 
	 * @param current the current element
	 * @param direction the direction of movement
	 * @return the next element or null if at boundary
	 */
	public Element getNextElement(Element current, Direction direction) {
		int index = indexOf(current);
		if(index == -1) return null;
		
		if(direction == Direction.LR) {
			if(index == elements.length - 1) return null;
			return elements[index + 1];
		} else {
			if(index == 0) return null;
			return elements[index - 1];
		}
	}

	/**
	 * Q3.4: Acquires the railway lock for a train leaving a station.
	 * Only ONE train can move at a time to ensure:
	 * - Maximum one train per section
	 * - No trains can leave stations if a train is moving in opposite direction
	 * 
	 * @param direction the direction the train wants to go
	 * @return true if lock acquired, false otherwise
	 */
	public synchronized boolean acquireRailwayLock(Direction direction) {
		// Wait while any train is moving (only one train at a time)
		while(trainsMoving > 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		
		// Set direction and mark one train as moving
		currentDirection = direction;
		trainsMoving = 1;
		return true;
	}

	/**
	 * Q3.5: Releases the railway lock when a train arrives at a station.
	 * This allows other trains to start moving.
	 */
	public synchronized void releaseRailwayLock() {
		trainsMoving--;
		if(trainsMoving == 0) {
			currentDirection = null;
			this.notifyAll();
		}
	}
}
