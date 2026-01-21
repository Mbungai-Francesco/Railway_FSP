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
}
