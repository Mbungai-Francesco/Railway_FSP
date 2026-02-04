package train;

/**
 * Representation of a train's position in the railway circuit.
 * A position is characterized by two values:
 * <ol>
 *   <li>
 *     The element where the train is positioned: a station ({@link Station})
 *     or a railway section ({@link Section}).
 *   </li>
 *   <li>
 *     The direction it is taking ({@link Direction}): from left to right
 *     or from right to left.
 *   </li>
 * </ol>
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Mayte Segarra
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
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

	public String reverseDirection() {
		if(this.direction == Direction.LR) {
			this.direction = Direction.RL;
		} else {
			this.direction = Direction.LR;
		}
		return "reversed direction to " + this.direction;
	}

	/**
	 * Moves the train to the next position on the railway.
	 * 
	 * Q3.4 & Q3.5: Implements railway locking mechanism to:
	 * - Prevent trains from leaving stations when another train is moving
	 * - Ensure only one train moves at a time (guarantees max 1 train per section)
	 * - Avoid deadlocks by acquiring lock before leaving station and releasing upon arrival
	 * 
	 * @param railway the railway to move on
	 * @param trainName the name of the train moving
	 * @return a message describing the movement
	 */
	public synchronized String move(Railway railway, String trainName) {
		// Get next element in the current direction
		Element nextElement = railway.getNextElement(pos, direction);
		Element previousPos = pos;
		
		// Check if we can move (boundary check)
		if(nextElement == null) {
			return "cannot move, at boundary";
		}
		
		// Q3.4: If leaving a station, acquire railway lock first
		// This prevents other trains from leaving stations while this train is moving
		if(previousPos instanceof Station && nextElement instanceof Section) {
			if(!railway.acquireRailwayLock(direction)) {
				return "cannot acquire railway lock";
			}
		}
		
		// Enter the next element
		if(nextElement instanceof Section) {
			// Use direction-aware entry for sections
			((Section)nextElement).enter(trainName, direction);
		} else {
			// For stations, use basic entry
			nextElement.enter(trainName);
		}
		
		// Leave the previous element
		previousPos.leave(trainName);
		pos = nextElement;
		
		// Build status message
		String message;
		if(nextElement instanceof Station) {
			direction = (direction == Direction.LR) ? Direction.RL : Direction.LR;
			message = "moved from " + previousPos.toString() + " to " + nextElement.toString() 
			          + " and changed direction to " + direction;
		} else {
			message = "moved from " + previousPos.toString() + " to " + nextElement.toString();
		}
		
		// Print message BEFORE releasing lock to ensure correct message ordering
		System.out.println("Train[" + trainName + "] " + message);
		
		// Q3.5: Release railway lock when arriving at a station
		// This allows other trains to start moving
		if(nextElement instanceof Station) {
			railway.releaseRailwayLock();
		}
		
		return message;
	}
}
