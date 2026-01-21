package train;

/**
 * Représentation de la position d'un train dans le circuit. Une position
 * est caractérisée par deux valeurs :
 * <ol>
 *   <li>
 *     L'élément où se positionne le train : une gare (classe  {@link Station})
 *     ou une section de voie ferrée (classe {@link Section}).
 *   </li>
 *   <li>
 *     La direction qu'il prend (enumération {@link Direction}) : de gauche à
 *     droite ou de droite à gauche.
 *   </li>
 * </ol>
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr> Modifié par Mayte
 *         Segarra 
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 *         
 * @version 0.3
 */
public class Position implements Cloneable {
	private Direction direction;
	private Element pos;

	public Position(Element elt, Direction d) {
		if (elt == null || d == null)
			throw new NullPointerException();

		this.pos = elt;
		this.direction = d;
	}

	@Override
	public Position clone() {
		try {
			return (Position) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Element getEle() {
		return pos;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(this.pos.toString());
		result.append(" going ");
		result.append(this.direction);
		return result.toString();
	}

	public void setPos(Element pos) {
		this.pos = pos;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Moves the train to the next position on the railway
	 * @param railway the railway to move on
	 * @param trainName the name of the train moving
	 * @return a message describing the movement
	 */
	public synchronized String move(Railway railway, String trainName) {
		if(railway.isAtBoundary(pos, direction)) {
			// Release current element before changing direction
			pos.release(trainName);
			direction = (direction == Direction.LR) ? Direction.RL : Direction.LR;
			// Re-occupy with new direction
			pos.occupy(trainName, direction);
			return "changed direction to " + direction;
		} else {
			Element nextElement = railway.getNextElement(pos, direction);
			// Occupy next element before moving (will wait if occupied in opposite direction)
			nextElement.occupy(trainName, direction);
			// Release current element
			pos.release(trainName);
			Element previousPos = pos;
			pos = nextElement;
			return "moved from " + previousPos.toString() + " to " + pos.toString();
		}
	}

	
}
