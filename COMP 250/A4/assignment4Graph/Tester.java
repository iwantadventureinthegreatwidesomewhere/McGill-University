package assignment4Graph;

public class Tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Graph g1 = new Graph(6);
		
		// TEST ADDEDGE
		
		g1.addEdge(0, 1);
		g1.addEdge(1, 2);
		g1.addEdge(1, 3);
		g1.addEdge(2, 3);
		g1.addEdge(2, 4);
		g1.addEdge(3, 4);
		if (g1.adjacency[2][3] && ! g1.adjacency[2][5]){
			System.out.println("addEdge seems to work, test it further though, this is testing only 1/18  of a single example !");
		}
		else{
			System.out.println("addEdge does not work.");
		}
		
		// TEST REMOVEEDGE
		
		Graph g2 = new Graph(6);
		g2.adjacency = new boolean[][]{new boolean[]{false, true, false, false, false, true},
			new boolean[]{true, false, true, true, false, false},
			new boolean[]{false, true, false, true, true, true},
			new boolean[]{false, true, true, false, true, false},
			new boolean[]{false, false, true, true, false, false},
			new boolean[]{true, false, true, false, false, false}};
		g2.removeEdge(0, 5);
		g2.removeEdge(2, 5);
		boolean areEqual = true;
		for (int i = 0; i< 6; i++){
			for (int j = 0; j < 6; j++){
				areEqual = areEqual && (g1.adjacency[i][j] == g2.adjacency[i][j]);
			}
		}
		if (areEqual){
			System.out.println("removeEdge seems to work, test it further though.");
		}
		else{
			System.out.println("removeEdge does not work.");
		}
		
		// TEST NBEDGES
		
		g1.adjacency = new boolean[][]{new boolean[]{false, true, false, false, false, false},
			new boolean[]{true, false, true, true, false, false},
			new boolean[]{false, true, false, true, true, false},
			new boolean[]{false, true, true, false, true, false},
			new boolean[]{false, false, true, true, false, false},
			new boolean[]{false, false, false, false, false, false}};
		if (g1.nbEdges() == 6){
			System.out.println("nbEdges seems to work, test it further though.");
		}
		else{
			System.out.println("nbEdges does not work.");
		}
		
		// TEST CYCLE
		
		if (!g1.cycle(0) && g1.cycle(2)){
			System.out.println("cycle seems to work, test it further though.");
		}
		else{
			System.out.println("cycle does not work.");
		}
		
		// TEST SHORTESTPATH
		
		if (g1.shortestPath(0, 4) == 3 && g1.shortestPath(0, 5) == 7 && g1.shortestPath(2, 4) == 1){
			System.out.println("shortestPath seems to work, test it further though.");
		}
		else{
			System.out.println("shortestPath does not work.");
		}
	}

}
