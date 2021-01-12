package assignment2;

public class Shelf {
	
	protected int height;
	protected int availableLength;
	protected int totalLength;
	protected Box firstBox;
	protected Box lastBox;

	public Shelf(int height, int totalLength){
		this.height = height;
		this.availableLength = totalLength;
		this.totalLength = totalLength;
		this.firstBox = null;
		this.lastBox = null;
	}
	
	protected void clear(){
		availableLength = totalLength;
		firstBox = null;
		lastBox = null;
	}
	
	public String print(){
		String result = "( " + height + " - " + availableLength + " ) : ";
		Box b = firstBox;
		while(b != null){
			result += b.id + ", ";
			b = b.next;
		}
		result += "\n";
		return result;
	}
	
	/**
	 * Adds a box on the shelf. Here we assume that the box fits in height and length on the shelf.
	 * @param b
	 */
	public void addBox(Box b){
		if(firstBox == null && lastBox == null){
			firstBox = b;
			lastBox = b;
			b.previous = null;
			b.next = null;
		}else{
			lastBox.next = b;
			b.previous = lastBox;
			b.next = null;
			lastBox = b;
		}
		availableLength -= b.length;
	}
	
	/**
	 * If the box with the identifier is on the shelf, remove the box from the shelf and return that box.
	 * If not, do not do anything to the Shelf and return null.
	 * @param identifier
	 * @return
	 */
	public Box removeBox(String identifier){
		Box box = firstBox;
		while(box != null){
			if(box.id.equals(identifier)){
				if(box.previous == null && box.next == null){
					firstBox = null;
					lastBox = null;
				}else if(box.previous == null && box.next != null){
					firstBox = box.next;
					firstBox.previous = null;
				}else if(box.previous != null && box.next == null){
					lastBox = box.previous;
					lastBox.next = null;
				}else{
					box.previous.next = box.next;
					box.next.previous = box.previous;
				}
				box.previous = null;
				box.next = null;
				availableLength += box.length;
				return box;
			}
			box = box.next;
		}
		return null;
	}
}
