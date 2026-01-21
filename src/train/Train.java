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
		if (!(p.getEle() instanceof Station))
			throw new BadPositionForTrainException(name);

		this.name = name;
		this.pos = p.clone();
		this.rail = rail;
		// Occupy initial position
		this.pos.getEle().occupy(name, this.pos.getDirection());
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
		String message = pos.move(rail, name);
		System.out.println("Train " + name + " " + message);
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
