package train;


/**
 * Représentation d'un circuit constitué d'éléments de voie ferrée : gare ou
 * section de voie
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 */
public class Railway {
	private Element[] elements;
	private final Object railwayLock = new Object();
	private Direction currentDirection = null;
	private int trainsMoving = 0;

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
	 * Checks if the current element is at a boundary
	 * @param current the current element
	 * @param direction the direction of movement
	 * @return true if at a boundary, false otherwise
	 */
	public boolean isAtBoundary(Element current, Direction direction) {
		int index = indexOf(current);
		if(index == -1) return false;
		
		if(direction == Direction.LR) {
			return index == elements.length - 1;
		} else {
			return index == 0;
		}
	}

	/**
	 * Checks if all sections from current position to the next station are free
	 * @param current the current element (should be a station)
	 * @param direction the direction of movement
	 * @param trainName the name of the train requesting to move
	 * @return true if all sections are free, false otherwise
	 */
	public boolean areAllSectionsFree(Element current, Direction direction, String trainName) {
		int index = indexOf(current);
		if(index == -1) return false;
		
		// Move in the given direction until we hit the next station
		if(direction == Direction.LR) {
			for(int i = index + 1; i < elements.length; i++) {
				Element elem = elements[i];
				if(elem instanceof Station) {
					return true; // Found next station with all sections free
				}
				// Check if section is free or occupied by same train
				if(!elem.isFree(trainName)) {
					return false;
				}
			}
		} else {
			for(int i = index - 1; i >= 0; i--) {
				Element elem = elements[i];
				if(elem instanceof Station) {
					return true; // Found next station with all sections free
				}
				// Check if section is free or occupied by same train
				if(!elem.isFree(trainName)) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Atomically checks if all sections from current position to the next station are free
	 * @param current the current element (should be a station)
	 * @param direction the direction of movement
	 * @param trainName the name of the train requesting to move
	 * @return true if all sections were successfully locked, false otherwise
	 */
	public boolean checkAndLockAllSections(Element current, Direction direction, String trainName) {
		synchronized(railwayLock) {
			// Wait if traffic is moving in opposite direction
			while(currentDirection != null && currentDirection != direction) {
				try {
					railwayLock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			
			// Check if all sections are free
			if(!areAllSectionsFree(current, direction, trainName)) {
				return false;
			}
			
			// Set the railway direction and increment train counter
			currentDirection = direction;
			trainsMoving++;
			
			// Lock all sections atomically
			lockAllSections(current, direction, trainName);
			return true;
		}
	}

	/**
	 * Releases the railway directional lock when a train finishes its journey
	 */
	public void releaseRailwayLock() {
		synchronized(railwayLock) {
			trainsMoving--;
			if(trainsMoving == 0) {
				currentDirection = null;
				railwayLock.notifyAll();
			}
		}
	}

	/**
	 * Locks all sections from current position to the next station
	 * @param current the current element (should be a station)
	 * @param direction the direction of movement
	 * @param trainName the name of the train
	 */
	public void lockAllSections(Element current, Direction direction, String trainName) {
		int index = indexOf(current);
		if(index == -1) return;
		
		if(direction == Direction.LR) {
			for(int i = index + 1; i < elements.length; i++) {
				Element elem = elements[i];
				if(elem instanceof Station) {
					return;
				}
				elem.occupy(trainName, direction);
			}
		} else {
			for(int i = index - 1; i >= 0; i--) {
				Element elem = elements[i];
				if(elem instanceof Station) {
					return;
				}
				elem.occupy(trainName, direction);
			}
		}
	}

	/**
	 * Releases all sections from current position to the next station
	 * @param current the current element (should be a station)
	 * @param direction the direction of movement
	 * @param trainName the name of the train
	 */
	public void releaseAllSections(Element current, Direction direction, String trainName) {
		int index = indexOf(current);
		if(index == -1) return;
		
		if(direction == Direction.LR) {
			for(int i = index + 1; i < elements.length; i++) {
				Element elem = elements[i];
				if(elem instanceof Station) {
					return;
				}
				elem.release(trainName);
			}
		} else {
			for(int i = index - 1; i >= 0; i--) {
				Element elem = elements[i];
				if(elem instanceof Station) {
					return;
				}
				elem.release(trainName);
			}
		}
	}
}
