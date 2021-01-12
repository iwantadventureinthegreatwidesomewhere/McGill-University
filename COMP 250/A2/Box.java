package assignment2;

public class Box {

	protected int height;
	protected int length;
	public String id;
	protected Box next;
	protected Box previous;
	
	public Box (int height, int length, String id){
		this.height = height;
		this.length = length;
		this.id = id;
		this.next = null;
		this.previous = null;
	}
	
}
