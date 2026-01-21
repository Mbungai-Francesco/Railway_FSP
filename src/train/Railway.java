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

	public void nextPos(Position p, String name){
		// Section[] sections = elements.
		int index = -1;

		// ! gets the current position of the train relative to the railway elements
		for(int i=0; i<elements.length; i++) {
			if(elements[i] == p.getEle()) {
				index = i;
				break;
			}
		}

		// ! updates the position of the train based on its current direction
		if(p.getDirection() == Direction.LR) {
			if(index == elements.length - 1) {
				p.setDirection(Direction.RL);
				System.out.println("Train " + name + " changed direction to " + p.getDirection());
			} else {
				p.setEle(elements[index + 1]);
				System.out.println("Train " + name + " moved from " + elements[index].toString() + " to " + p.getEle().toString());
			}
		} else {
			if(index != 0) {
				p.setEle(elements[index - 1]);
				System.out.println("Train " + name + " moved from " + elements[index].toString() + " to " + p.getEle().toString());
			}else{
				p.setDirection(Direction.LR);
				System.out.println("Train " + name + " changed direction to " + p.getDirection());
			}
		}
	}
}
