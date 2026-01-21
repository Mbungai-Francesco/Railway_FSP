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
	public String move(Railway railway, String trainName) {
		// If currently at a station, we need to lock all sections before moving
		if(pos instanceof Station) {
			// Atomically check if we can lock all sections to the next station
			while(!railway.checkAndLockAllSections(pos, direction, trainName)) {
				try {
					// Wait a bit before retrying
					Thread.sleep(500);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			// Now move to next element
			Element nextElement = railway.getNextElement(pos, direction);
			Element previousPos = pos;
			pos = nextElement;
			return "moved from " + previousPos.toString() + " to " + pos.toString();
		} else {
			// Moving through a section (already locked)
			Element nextElement = railway.getNextElement(pos, direction);
			Element previousPos = pos;
			pos = nextElement;
			
			// If we reached a station, release all sections and prepare to change direction
			if(pos instanceof Station) {
				railway.releaseAllSections(previousPos, direction, trainName);
				// Release the railway directional lock since journey is complete
				railway.releaseRailwayLock();
				direction = (direction == Direction.LR) ? Direction.RL : Direction.LR;
				return "moved from " + previousPos.toString() + " to " + pos.toString() + " and changed direction to " + direction;
			}
			
			return "moved from " + previousPos.toString() + " to " + pos.toString();
		}
	}
}
