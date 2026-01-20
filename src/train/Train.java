package train;

/**
 * Représentation d'un train. Un train est caractérisé par deux valeurs :
 * <ol>
 *   <li>
 *     Son nom pour l'affichage.
 *   </li>
 *   <li>
 *     La position qu'il occupe dans le circuit (un élément avec une direction) : classe {@link Position}.
 *   </li>
 * </ol>
 * 
 * @author Fabien Dagnat <fabien.dagnat@imt-atlantique.fr>
 * @author Mayte segarra <mt.segarra@imt-atlantique.fr>
 * Test if the first element of a train is a station
 * @author Philippe Tanguy <philippe.tanguy@imt-atlantique.fr>
 * @version 0.3
 */
public class Train implements Runnable {
	private final String name;
	private Position pos;
	private Railway rail;

	public Train(String name, Position p, Railway rail) throws BadPositionForTrainException {
		if (name == null || p == null)
			throw new NullPointerException();

		// A train should be first be in a station
		if (!(p.getPos() instanceof Station))
			throw new BadPositionForTrainException(name);

		this.name = name;
		this.pos = p.clone();
		this.rail = rail;
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

	public void move(){
		Element[] elements = rail.getEl();
		int index = -1;
		for(int i=0; i<elements.length; i++) {
			if(elements[i] == pos.getPos()) {
				index = i;
				break;
			}
		}
		if(pos.getDirection() == Direction.LR) {
			if(index == elements.length - 1) {
				pos.setDirection(Direction.RL);
				System.out.println("Train " + name + " changed direction to " + pos.getDirection());
			} else {
				pos.setPos(elements[index + 1]);
				System.out.println("Train " + name + " moved from " + elements[index].toString() + " to " + pos.getPos().toString());
			}
		} else {
			if(index != 0) {
				pos.setPos(elements[index - 1]);
				System.out.println("Train " + name + " moved from " + elements[index].toString() + " to " + pos.getPos().toString());
			}else{
				pos.setDirection(Direction.LR);
				System.out.println("Train " + name + " changed direction to " + pos.getDirection());
			}
		}
	}

	@Override
	public void run() {
		while(true) {
			move();
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
