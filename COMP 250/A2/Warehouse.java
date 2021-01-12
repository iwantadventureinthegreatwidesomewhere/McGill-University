package assignment2;

public class Warehouse{

	protected Shelf[] storage;
	protected int nbShelves;
	public Box toShip;
	public UrgentBox toShipUrgently;
	static String problem = "problem encountered while performing the operation";
	static String noProblem = "operation was successfully carried out";
	
	public Warehouse(int n, int[] heights, int[] lengths){
		this.nbShelves = n;
		this.storage = new Shelf[n];
		for (int i = 0; i < n; i++){
			this.storage[i]= new Shelf(heights[i], lengths[i]);
		}
		this.toShip = null;
		this.toShipUrgently = null;
	}
	
	public String printShipping(){
		Box b = toShip;
		String result = "not urgent : ";
		while(b != null){
			result += b.id + ", ";
			b = b.next;
		}
		result += "\n" + "should be already gone : ";
		b = toShipUrgently;
		while(b != null){
			result += b.id + ", ";
			b = b.next;
		}
		result += "\n";
		return result;
	}
	
 	public String print(){
 		String result = "";
		for (int i = 0; i < nbShelves; i++){
			result += i + "-th shelf " + storage[i].print();
		}
		return result;
	}
	
 	public void clear(){
 		toShip = null;
 		toShipUrgently = null;
 		for (int i = 0; i < nbShelves ; i++){
 			storage[i].clear();
 		}
 	}
 	
 	/**
 	 * initiate the merge sort algorithm
 	 */
	public void sort(){
		mergeSort(0, nbShelves -1);
	}
	
	/**
	 * performs the induction step of the merge sort algorithm
	 * @param start
	 * @param end
	 */
	protected void mergeSort(int start, int end){
		if(start<end){
			int mid = (start+end)/2;
			mergeSort(start, mid);
			mergeSort(mid+1, end);
			merge(start, mid, end);
		}
		
		/*for(int i = 0; i < storage.length; i++){
			System.out.print(storage[i].height + " ");
		}
		System.out.print("\n");*/
	}
	
	/**
	 * performs the merge part of the merge sort algorithm
	 * @param start
	 * @param mid
	 * @param end
	 */
	protected void merge(int start, int mid, int end){
		Shelf[] left = new Shelf[mid-start+1];
		Shelf[] right = new Shelf[end-mid];
		int x = start;
		for(int i = 0; i < left.length; i++){
			left[i] = storage[x];
			x++;
		}
		x = mid+1;
		for(int i = 0; i < right.length; i++){
			right[i] = storage[x];
			x++;
		}
		
		/*System.out.print("left: ");
		for(int i = 0; i < left.length; i++){
			System.out.print(left[i].height + " ");
		}
		System.out.print("\n");
		System.out.print("right: ");
		for(int i = 0; i < right.length; i++){
			System.out.print(right[i].height + " ");
		}
		System.out.print("\n");*/
		
		int j = 0;
		int k = 0;
		for(int count = start; count <= end; count++){
			if( j < left.length && k < right.length){
				if(left[j].height <= right[k].height){
					storage[count] = left[j];
					j++;
				} else {
					storage[count] = right[k];
					k++;
				}
			}else if(j < left.length && k >= right.length){
				storage[count] = left[j];
				j++;
			}else if(j >= left.length && k < right.length){
				storage[count] = right[k];
				k++;
			}		
		}
	}
	
	/**
	 * Adds a box is the smallest possible shelf where there is room available.
	 * Here we assume that there is at least one shelf (i.e. nbShelves >0)
	 * @param b
	 * @return problem or noProblem
	 */
	public String addBox (Box b){
		for(int i = 0; i < storage.length; i++){
			if(b.height <= storage[i].height && b.length <= storage[i].availableLength){
				storage[i].addBox(b);
				return noProblem;
			}
		}
		return problem;
	}
	
	/**
	 * Adds a box to its corresponding shipping list and updates all the fields
	 * @param b
	 * @return problem or noProblem
	 */
	public String addToShip (Box b){
		try{
			if(b instanceof UrgentBox){
				if(toShipUrgently != null){
					toShipUrgently.previous = b;
					b.next = toShipUrgently;
					b.previous = null;
					toShipUrgently = (UrgentBox) b;
				}else{
					toShipUrgently = (UrgentBox) b;
				}
				return noProblem;
			} else {
				if(toShip != null){
					toShip.previous = b;
					b.next = toShip;
					b.previous = null;
					toShip = b;
				}else{
					toShip = b;
				}
				return noProblem;
			}
		}catch(Exception e){
			return problem;
		}
	}
	
	/**
	 * Find a box with the identifier (if it exists)
	 * Remove the box from its corresponding shelf
	 * Add it to its corresponding shipping list
	 * @param identifier
	 * @return problem or noProblem
	 */
	public String shipBox (String identifier){
		
		/*for(Shelf shelf : storage){
			System.out.println(shelf.firstBox + " " + shelf.lastBox);
		}*/
		
		for(Shelf shelf : storage){
			Box box = shelf.removeBox(identifier);
			if(box != null){
				addToShip(box);
				return noProblem;
			}
		}
		return problem;
	}
	
	/**
	 * if there is a better shelf for the box, moves the box to the optimal shelf.
	 * If there are none, do not do anything
	 * @param b
	 * @param position
	 */
	public void moveOneBox (Box b, int position){
		storage[position].removeBox(b.id);
		addBox(b);
	}
	
	/**
	 * reorganize the entire warehouse : start with smaller shelves and first box on each shelf.
	 */
	public void reorganize (){
		for(int i = 0; i < storage.length; i++){
			Box box = storage[i].firstBox;
			while(box != null){
				Box next = box.next;
				moveOneBox(box, i);
				box = next;
			}
		}
	}
}