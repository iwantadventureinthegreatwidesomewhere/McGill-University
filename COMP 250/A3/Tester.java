package assignment3;

import java.util.Arrays;

public class Tester {

	public static void main(String[] args) {
		
		System.out.println("DISCLAIMER : Please check your code on additional examples that you design yourself as this tester only tests your code on very few examples !");
		System.out.println();
		
		Building b1 = new Building(new OneBuilding("Building 1", 1987, 50, 2025, 35));
		OneBuilding ob2 = new OneBuilding("Building 2", 1988, 60, 2023, 50);
		OneBuilding ob3 = new OneBuilding("Building 3", 1987, 55, 2024, 23);
		OneBuilding ob4 = new OneBuilding("Building 4", 1985, 50, 2022, 5);
		OneBuilding ob5 = new OneBuilding("Building 5", 1985, 45, 2021, 42);
		Building b6 = new Building(new OneBuilding("Building 6", 1985, 60, 2024, 26));
		OneBuilding ob7 = new OneBuilding("Building 7", 1986, 55, 2024, 11);
		OneBuilding ob8 = new OneBuilding("Building 8", 1995, 55, 2019, 46);
		
		// TESTING ADDBUILDING
		b1 = b1.addBuilding(ob2);
		b1 = b1.addBuilding(ob3);
		b1 = b1.addBuilding(ob4);
		b1 = b1.addBuilding(ob5);
		b6 = b6.addBuilding(ob7);
		b6 = b6.addBuilding(ob8);
		
		String sol1 = "Building 3(1987 , 55)\n"+"older than Building 3(1987 , 55) :\n"+"Building 4(1985 , 50)\n";
		sol1 += "same age as Building 4(1985 , 50) :\n"+"Building 5(1985 , 45)\n"+"same age as Building 3(1987 , 55) :\n";
		sol1 += "Building 1(1987 , 50)\n"+"younger than Building 3(1987 , 55) :\n"+"Building 2(1988 , 60)\n";
		String sol2 = "Building 6(1985 , 60)\n"+"younger than Building 6(1985 , 60) :\n"+"Building 7(1986 , 55)\n";
		sol2 += "younger than Building 7(1986 , 55) :\n"+"Building 8(1995 , 55)\n";

		if (sol1.equals(b1.toString()) && sol2.equals(b6.toString())){
			System.out.println("AddBuilding seems to work, check it on more examples though");
		}
		else{
			System.out.println("AddBuilding does not work");
		}
		
		// TESTING ADDBUILDINGS
		b1 = b1.addBuildings(b6);
		
		String sol3 = "Building 3(1987 , 55)\n"+"older than Building 3(1987 , 55) :\n"+"Building 6(1985 , 60)\n";
		sol3 += "same age as Building 6(1985 , 60) :\n"+"Building 4(1985 , 50)\n"+"same age as Building 4(1985 , 50) :\n"+"Building 5(1985 , 45)\n";
		sol3 += "younger than Building 6(1985 , 60) :\n"+"Building 7(1986 , 55)\n"+"same age as Building 3(1987 , 55) :\n"+"Building 1(1987 , 50)\n";
		sol3 += "younger than Building 3(1987 , 55) :\n"+"Building 2(1988 , 60)\n"+"younger than Building 2(1988 , 60) :\n"+"Building 8(1995 , 55)\n";
		
		if (sol3.equals(b1.toString())){
			System.out.println("AddBuildings seems to work, check it on more examples though");
		}
		else{
			System.out.println("AddBuildings does not work");
		}
		
		// TESTING REMOVEBUILDING
		b1 = b1.removeBuilding(ob5);
		b1 = b1.removeBuilding(new OneBuilding("Building 3", 1987, 55, 2024, 23));
		b1 = b1.removeBuilding(new OneBuilding("Building 4", 1985, 50, 2024, 23)); // this OneBuilding cannot be removed as it is not in the tree (the fields do not correspond to ob4 !)
		
		String sol4 = "Building 1(1987 , 50)\n"+"older than Building 1(1987 , 50) :\n"+"Building 6(1985 , 60)\n";
		sol4 += "same age as Building 6(1985 , 60) :\n"+"Building 4(1985 , 50)\n"+"younger than Building 6(1985 , 60) :\n"+"Building 7(1986 , 55)\n";
		sol4 += "younger than Building 1(1987 , 50) :\n"+"Building 2(1988 , 60)\n"+"younger than Building 2(1988 , 60) :\n"+"Building 8(1995 , 55)\n";
		if (sol4.equals(b1.toString())){
			System.out.println("RemoveBuilding seems to work, check it on more examples though");
		}
		else{
			System.out.println("RemoveBuilding does not work");
		}
		
		// TESTING OLDEST
		
		b1 = b1.addBuilding(ob3);
		b1 = b1.addBuilding(ob5);
		
		if (b1.oldest() == 1985 && b1.younger.oldest() == 1988){
			System.out.println("oldest seems to work, check it on more examples though");
		}
		else{
			System.out.println("oldest does not work");
		}
		
		// TESTING HIGHEST
		
		if (b1.highest() == 60 && b1.older.same.highest() == 50){
			System.out.println("highest seems to work, check it on more examples though");
		}
		else{
			System.out.println("highest does not work");
		}
		
		// TESTING HIGHESTFROMYEAR
		
		if (b1.highestFromYear(1989) == null && b1.highestFromYear(1986).equals(ob7) && b1.highestFromYear(1985).name.equals("Building 6")){
			System.out.println("highestFromYear seems to work, check it on more examples though");
		}
		else{
			System.out.println("highestFromYear does not work");
		}
		
		// TESTING NUMBERFROMYEARS
		
		if (b1.numberFromYears(1986, 1989) == 4 && b1.numberFromYears(1985, 1985) == 3 && b1.numberFromYears(1987, 1985) == 0){
			System.out.println("numberFromYears seems to work, check it on more examples though");
		}
		else{
			System.out.println("numberFromYears does not work");
		}
		
		// TESTING COSTPLANNING
		
		int[] costs = b1.costPlanning(7);
		int[] costsol = new int[]{0, 46, 0, 42, 5, 50, 60};
		if (costs.length != 7){
			System.out.println("CostPlanning : incorrect size of the array");
		}
		else{
			if (Arrays.equals(costs, costsol)){
				System.out.println("costPlanning seems to work, check it on more examples though");
			}
			else{
				System.out.println("costPlanning does not work");
			}
		}
		System.out.println();
		System.out.println("DISCLAIMER : if the tester says that all your methods work, congrats ! \nPlease continue checking your code on examples that you design yourself as this tester only tests your code on very few examples !");
		
	}

}
