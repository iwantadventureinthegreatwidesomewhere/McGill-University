package assignment3;

public class OneBuilding {

	final String name;
	final int yearOfConstruction;
	final int height;
	final int yearForRepair;
	final int costForRepair;
	
	public OneBuilding(String name, int yearOfConstruction, int height, int yearForRepair, int costForRepair){
		this.name = name;
		this.yearOfConstruction = yearOfConstruction;
		this.height = height;
		this.yearForRepair = yearForRepair;
		this.costForRepair = costForRepair;
	}
	
	public String toString(){
		String result = this.name + "(" + this.yearOfConstruction + " , " + this.height + ")";
		return result;
	}
	
	public boolean equals(OneBuilding b){
		boolean temp = this.name.equals(b.name);
		temp = temp && this.yearForRepair == b.yearForRepair;
		temp = temp && this.yearOfConstruction == b.yearOfConstruction;
		temp = temp && this.height == b.height;
		temp = temp && this.costForRepair == b.costForRepair;
		return temp;
	}
	
}
