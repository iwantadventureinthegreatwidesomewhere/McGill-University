package assignment4Game;

import java.io.*;

public class Game {
	
	public static int play(InputStreamReader input){
		BufferedReader keyboard = new BufferedReader(input);
		Configuration c = new Configuration();
		int columnPlayed = 3; int player;
		
		// first move for player 1 (played by computer) : in the middle of the grid
		c.addDisk(firstMovePlayer1(), 1);
		int nbTurn = 1;
		
		while (nbTurn < 42){ // maximum of turns allowed by the size of the grid
			player = nbTurn %2 + 1;
			if (player == 2){
				columnPlayed = getNextMove(keyboard, c, 2);
			}
			if (player == 1){
				columnPlayed = movePlayer1(columnPlayed, c);
			}
			System.out.println(columnPlayed);
			c.addDisk(columnPlayed, player);
			if (c.isWinning(columnPlayed, player)){
				c.print();
				System.out.println("Congrats to player " + player + " !");
				return(player);
			}
			nbTurn++;
		}
		return -1;
	}
	
	public static int getNextMove(BufferedReader keyboard, Configuration c, int player){
		c.print();
		int col = -1;
		int otherPlayer = 0;
		if(player == 1){
			otherPlayer = 2;
		}else{
			otherPlayer = 1;
		}
		if(c.canWinNextRound(otherPlayer) != -1){
			System.out.println("*note that opponent has a winning move*");
		}
		while(true){
			System.out.print("select a column to add your dish: ");
			String s = null;
			try {
				s = keyboard.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			col = Integer.parseInt(s);
			if(col > -1 && col < 7 && c.available[col] < 6){
				break;
			}
		}
		return col;
	}
	
	public static int firstMovePlayer1 (){
		return 3;
	}
	
	public static int movePlayer1 (int columnPlayed2, Configuration c){
		if(c.canWinNextRound(1) != -1){
			return c.canWinNextRound(1);
		}else if(c.canWinTwoTurns(1) != -1){
			return c.canWinTwoTurns(1);
		}else{
			for(int i = 0; i < 7; i++){
				if(columnPlayed2-i > -1 && c.available[columnPlayed2-i] < 6){
					return columnPlayed2-i;
				}else if(columnPlayed2+i < 7 && c.available[columnPlayed2+i] < 6){
					return columnPlayed2+i;
				}
			}
			return -1;
		}
	}
	
}
