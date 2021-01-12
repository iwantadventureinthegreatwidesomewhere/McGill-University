package assignment4Graph;

public class Graph {
	
	boolean[][] adjacency;
	int nbNodes;
	
	public Graph (int nb){
		this.nbNodes = nb;
		this.adjacency = new boolean [nb][nb];
		for (int i = 0; i < nb; i++){
			for (int j = 0; j < nb; j++){
				this.adjacency[i][j] = false;
			}
		}
	}
	
	public void addEdge (int i, int j){
		adjacency[i][j] = true;
		adjacency[j][i] = true;
	}
	
	public void removeEdge (int i, int j){
		adjacency[i][j] = false;
		adjacency[j][i] = false;
	}
	
	public int nbEdges(){
		int count = 0;
		for(int i = 0; i < adjacency.length; i++){
			for(int j = 0; j <= i; j++){
				if(adjacency[i][j] == true){
					count++;
				}	
			}
		}
		return count;
	}
	
	public boolean cycle(int start){
		boolean[][] visited = new boolean[adjacency.length][adjacency[0].length]; 
		boolean r = dfs_cycle(start, start, visited);
		return r;
	}
	
	public int shortestPath(int start, int end){
		if(start > end){
			int temp = start;
			start = end;
			end = temp;
		}
		boolean[][] visited = new boolean[adjacency.length][adjacency[0].length]; 
		int best = Integer.MAX_VALUE;
		int count = 0;
		int r = dfs_shortestPath(start, end, visited, best, count);
		if(r != Integer.MAX_VALUE){
			return r;
		}
		return this.nbNodes+1;
	}
	
	private boolean dfs_cycle(int i, int target, boolean[][] visited){
		boolean r = false;
		for(int j = 0; j < adjacency[i].length; j++){
			if(!visited[i][j] && i != j){
				if(adjacency[i][j]){
					if(j == target){
						return true;
					}
					visited[i][j] = true;
					visited[j][i] = true;
					r = dfs_cycle(j, target, visited);
					if(r){
						break;
					}
				}
			}	
		}
		return r;
	}
	
	private int dfs_shortestPath(int start, int end, boolean[][] visited, int best, int count){
		for(int j = 0; j < adjacency[start].length; j++){
			if(!visited[start][j] && start != j){
				if(adjacency[start][j]){
					count++;
					if(j == end){
						return count;
					}
					visited[start][j] = true;
					visited[j][start] = true;
					int r = dfs_shortestPath(j, end, visited, best, count);
					count--;
					if(r<best){
						best = r;
					}
					visited[start][j] = false;
					visited[j][start] = false;
				}
			}
		}
		return best;
	}
}
