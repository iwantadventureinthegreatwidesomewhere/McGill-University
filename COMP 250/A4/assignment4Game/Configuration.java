package assignment4Game;

public class Configuration {
	
	public int[][] board;
	public int[] available;
	boolean spaceLeft;
	
	public Configuration(){
		board = new int[7][6];
		available = new int[7];
		spaceLeft = true;
	}
	
	public void print(){
		System.out.println("| 0 | 1 | 2 | 3 | 4 | 5 | 6 |");
		System.out.println("+---+---+---+---+---+---+---+");
		for (int i = 0; i < 6; i++){
			System.out.print("|");
			for (int j = 0; j < 7; j++){
				if (board[j][5-i] == 0){
					System.out.print("   |");
				}
				else{
					System.out.print(" "+ board[j][5-i]+" |");
				}
			}
			System.out.println();
		}
	}
	
	public void addDisk (int index, int player){
		board[index][available[index]] = player;
		available[index] += 1;
		spaceLeft = false;
		for(int col = 0; col < 7; col++){
			for(int row = 0; row < 6; row++){
				if(board[col][row] == 0){
					spaceLeft = true;
				}
			}
		}
	}
	
	public boolean isWinning (int lastColumnPlayed, int player){
		int lastRowPlayed;
		if(available[lastColumnPlayed] != 0){
			lastRowPlayed = available[lastColumnPlayed]-1;
		}else{
			lastRowPlayed = available[lastColumnPlayed];
		}
		//vertical
		int countVertical = 0;
		for(int i = 0; i < 4; i++){
			if(lastRowPlayed-i > -1){
				if(board[lastColumnPlayed][lastRowPlayed-i] == player){
					countVertical++;
				}
			}
			if(countVertical == 4){
				return true;
			}
		}
		//horizontal
		int countHorizontal = 0;
		for(int i = 0; i < 2; i++){
			int count = lastColumnPlayed;
			while(count > -1 && count < 7 && board[count][lastRowPlayed] == player){
				countHorizontal++;
				count = count + 1 + -2*i;
			}
		}
		if(countHorizontal-1 >= 4){
			return true;
		}
		//diagonal(positive slope)
		int countDiagonal = 0;
		for(int i = 0; i < 2; i++){
			countVertical = lastRowPlayed;
			countHorizontal = lastColumnPlayed;
			while(countVertical > -1 && countVertical < 6 && countHorizontal > -1 && countHorizontal < 7 && board[countHorizontal][countVertical] == player){
				countDiagonal++;
				countVertical = countVertical + 1 + -2*i;
				countHorizontal = countHorizontal + 1 + -2*i; 
			}
		}
		if(countDiagonal-1 >= 4){
			return true;
		}
		//diagonal(negative slope)
		countDiagonal = 0;
		for(int i = 0; i < 2; i++){
			countVertical = lastRowPlayed;
			countHorizontal = lastColumnPlayed;
			while(countVertical > -1 && countVertical < 6 && countHorizontal > -1 && countHorizontal < 7 && board[countHorizontal][countVertical] == player){
				countDiagonal++;
				countVertical = countVertical + 1 + -2*i;
				countHorizontal = countHorizontal - 1 + +2*i; 
			}
		}
		if(countDiagonal-1 >= 4){
			return true;
		}
		return false;
	}
	
	public int canWinNextRound (int player){
		for(int col = 0; col < 7; col++){
			Configuration temp = new Configuration();
			for(int i = 0; i < 7; i++){
				for(int j = 0; j < 6; j++){
					temp.board[i][j] = this.board[i][j];
				}
				temp.available[i] = this.available[i];
				
			}
			temp.spaceLeft = this.spaceLeft;
			if(this.available[col] < 6){
				temp.addDisk(col, player);
				if(temp.isWinning(col, player)){
					return col;
				}
			}
		}
		return -1;
	}
	
	public int canWinTwoTurns (int player){
		int otherPlayer;
		if(player == 1){
			otherPlayer = 2;
		}else{
			otherPlayer = 1;
		}
		for(int col = 0; col < 7; col++){
			Configuration temp = new Configuration();
			for(int i = 0; i < 7; i++){
				for(int j = 0; j < 6; j++){
					temp.board[i][j] = this.board[i][j];
				}
				temp.available[i] = this.available[i];
				
			}
			temp.spaceLeft = this.spaceLeft;
			if(this.available[col] < 6){
				temp.addDisk(col, player);
				if(temp.canWinNextRound(player) != -1 && temp.canWinNextRound(otherPlayer) == -1){
					temp.addDisk(temp.canWinNextRound(player), otherPlayer);
					if(temp.canWinNextRound(player) != -1){
						return col;
					}
				}
			}
			
		}
		return -1;
	}
	
}
