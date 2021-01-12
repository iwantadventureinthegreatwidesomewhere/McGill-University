package assignment3;

public class Building {

	OneBuilding data;
	Building older;
	Building same;
	Building younger;
	
	public Building(OneBuilding data){
		this.data = data;
		this.older = null;
		this.same = null;
		this.younger = null;
	}
	
	public String toString(){
		String result = this.data.toString() + "\n";
		if (this.older != null){
			result += "older than " + this.data.toString() + " :\n";
			result += this.older.toString();
		}
		if (this.same != null){
			result += "same age as " + this.data.toString() + " :\n";
			result += this.same.toString();
		}
		if (this.younger != null){
			result += "younger than " + this.data.toString() + " :\n";
			result += this.younger.toString();
		}
		return result;
	}
	
	public Building addBuilding (OneBuilding b){
		if(b.yearOfConstruction < this.data.yearOfConstruction){
			if(this.older != null){
				this.older.addBuilding(b);
			}else{
				Building new_building = new Building(b);
				this.older = new_building;
			}
		}else if(b.yearOfConstruction > this.data.yearOfConstruction){
			if(this.younger != null){
				this.younger.addBuilding(b);
			}else{
				Building new_building = new Building(b);
				this.younger = new_building;
			}
		}else if(b.yearOfConstruction == this.data.yearOfConstruction){
			if(b.height > this.data.height){
				OneBuilding temp = this.data;
				this.data = b;
				b = temp;
			}
			if(this.same != null){
				this.same.addBuilding(b);
			}else{
				Building new_building = new Building(b);
				this.same = new_building;
			}
		}
		return this;
	}
	
	public Building addBuildings (Building b){
		this.addBuilding(b.data);
		if(b.older != null){
			this.addBuildings(b.older);
		}
		if(b.same != null){	
			this.addBuildings(b.same);
		}
		if(b.younger != null){
			this.addBuildings(b.younger);
		}
		return this;
	}
	
	public Building removeBuilding (OneBuilding b){
		if(this.older != null){
			Building r = this.older.removeBuilding(b);
			if(r == null){
				this.older = null;
			}
		}
		if(this.same != null){	
			Building r = this.same.removeBuilding(b);
			if(r == null){
				this.same = null;
			}
		}
		if(this.younger != null){
			Building r = this.younger.removeBuilding(b);
			if(r == null){
				this.younger = null;
			}
		}
		if(this.data.equals(b)){
			Building temp_o, temp_s, temp_y, temp_other;
			if(this.same != null){
				this.data = this.same.data;
				this.same = this.same.same;
			}else if(this.older != null){
				this.data = this.older.data;
				temp_o = this.older.older;
				temp_s = this.older.same;
				temp_y = this.older.younger;
				temp_other = this.younger;
				this.older = null;
				this.younger = null;
				if(temp_o != null){
					this.addBuildings(temp_o);
				}
				if(temp_s != null){
					this.addBuildings(temp_s);
				}
				if(temp_y != null){
					this.addBuildings(temp_y);
				}
				if(temp_other != null){
					this.addBuildings(temp_other);
				}
			}else if(this.younger != null){
				this.data = this.younger.data;
				temp_o = this.younger.older;
				temp_s = this.younger.same;
				temp_y = this.younger.younger;
				this.younger = null;
				if(temp_o != null){
					this.addBuildings(temp_o);
				}
				if(temp_s != null){
					this.addBuildings(temp_s);
				}
				if(temp_y != null){
					this.addBuildings(temp_y);
				}
			}else{
				return null;
			}
		}
		return this;
	}
	
	public int oldest(){
		int year = this.data.yearOfConstruction;
		if(this.older != null){
			year = this.older.oldest();
		}
		return year;
	}
	
	public int highest(){
		int height = this.data.height;
		if(this.older != null){
			int r = this.older.highest();
			if(r > height){
				height = r;
			}
		}else if(this.same != null){
			int r = this.same.highest();
			if(r > height){
				height = r;
			}
		}else if(this.younger != null){
			int r = this.younger.highest();
			if(r > height){
				height = r;
			}
		}
		return height;
	}
	
	public OneBuilding highestFromYear (int year){
		OneBuilding r = null;
		if(this.data.yearOfConstruction == year){
			r = new OneBuilding(this.data.name, this.data.yearOfConstruction, this.data.height, this.data.yearForRepair, this.data.costForRepair);
			return r;
		}else{
			if(this.older != null){
				r = this.older.highestFromYear(year);
				if(r != null){
					return r;
				}
			}
			if(this.same != null){
				r = this.same.highestFromYear(year);
				if(r != null){
					return r;
				}
			}
			if(this.younger != null){
				r = this.younger.highestFromYear(year);
				if(r != null){
					return r;
				}
			}
		} 
		return r;
	}
	
	public int numberFromYears (int yearMin, int yearMax){
		int r = 0;
		if(yearMin > yearMax){
			return 0;
		}
		if(this.data.yearOfConstruction >= yearMin && this.data.yearOfConstruction <= yearMax){
			r++;
		}
		if(this.older != null){
			r += this.older.numberFromYears(yearMin, yearMax);
		}
		if(this.same != null){
			r += this.same.numberFromYears(yearMin, yearMax);
		}
		if(this.younger != null){
			r += this.younger.numberFromYears(yearMin, yearMax);
		}
		return r;
	}
	
	public int[] costPlanning (int nbYears){
		int[] a = new int[nbYears];
		for(int i = 0; i < a.length; i++){
			if(this.data.yearForRepair == 2018+i){
				a[i] += this.data.costForRepair;
			}
		}
		if(this.older != null){
			int[] r = this.older.costPlanning(nbYears);
			for(int i = 0; i < a.length; i++){
				a[i] += r[i];
			}
		}
		if(this.same != null){
			int[] r = this.same.costPlanning(nbYears);
			for(int i = 0; i < a.length; i++){
				a[i] += r[i];
			}
		}
		if(this.younger != null){
			int[] r = this.younger.costPlanning(nbYears);
			for(int i = 0; i < a.length; i++){
				a[i] += r[i];
			}
		}
		return a;
	} 
}
