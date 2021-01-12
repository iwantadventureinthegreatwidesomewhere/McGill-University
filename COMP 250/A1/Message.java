package assignment1;

public class Message {
	
	public String message;
	public int lengthOfMessage;

	public Message (String m){
		message = m;
		lengthOfMessage = m.length();
		this.makeValid();
	}
	
	public Message (String m, boolean b){
		message = m;
		lengthOfMessage = m.length();
	}
	
	/**
	 * makeValid modifies message to remove any character that is not a letter and turn Upper Case into Lower Case
	 */
	public void makeValid(){
		//INSERT YOUR CODE HERE
		String temp = "";
		int count = 0;
		for(int i = 0; i < lengthOfMessage; i++) {
			if(message.charAt(i) >= 'a' && message.charAt(i) <= 'z') {
				temp += "" + message.charAt(i);
				count++;
			} else if (message.charAt(i) >= 'A' && message.charAt(i) < 'Z'){
				temp += ("" + message.charAt(i)).toLowerCase();	
				count++;
			}
		}
		message = temp;
		lengthOfMessage = count;
	}
	
	/**
	 * prints the string message
	 */
	public void print(){
		System.out.println(message);
	}
	
	/**
	 * tests if two Messages are equal
	 */
	public boolean equals(Message m){
		if (message.equals(m.message) && lengthOfMessage == m.lengthOfMessage){
			return true;
		}
		return false;
	}
	
	/**
	 * caesarCipher implements the Caesar cipher : it shifts all letter by the number 'key' given as a parameter.
	 * @param key
	 */
	public void caesarCipher(int key){
		// INSERT YOUR CODE HERE
		String temp = "";
		for(int i = 0; i < lengthOfMessage; i++) {
			char c = (char) (message.charAt(i) + key);
			int dif = 0;
			if(c < 'a') {
				dif = c - 'a';
				c = (char) (123 + dif);
			} else if (c > 'z') {
				dif = c - 'z';
				c = (char) (96 + dif);
			}
			temp += "" + c;
		}
		message = temp;
	}
	
	public void caesarDecipher(int key){
		this.caesarCipher(- key);
	} 
	
	/**
	 * caesarAnalysis breaks the Caesar cipher
	 * you will implement the following algorithm :
	 * - compute how often each letter appear in the message
	 * - compute a shift (key) such that the letter that happens the most was originally an 'e'
	 * - decipher the message using the key you have just computed
	 */
	public void caesarAnalysis(){
		// INSERT YOUR CODE HERE
		int charFrequency = 0;
		char charHighest = '\u0000';
		
		for(char i = 97; i < 123; i++) {
			int count = 0;
			for(int j = 0; j < lengthOfMessage; j++) {
				if(message.charAt(j) == (char) i) {
					count++;
				}
			}
			if(count > charFrequency) {
				charFrequency = count;
				charHighest = (char) i;
			}
		}
		
		int key = charHighest - 'e';
		caesarDecipher(key);
	
	}
	
	/**
	 * vigenereCipher implements the Vigenere Cipher : it shifts all letter from message by the corresponding shift in the 'key'
	 * @param key
	 */
	public void vigenereCipher (int[] key){
		// INSERT YOUR CODE HERE
		String temp = "";
		for(int i = 0; i < lengthOfMessage; i++) {
			int extra = 0;
			if (key.length < lengthOfMessage && i > key.length -1) {
				extra = key.length;
			}
			char c = '\u0000';
			if(extra > 0) {
				c = (char) (message.charAt(i) + key[i - extra*(i/extra)]);
			} else {
				c = (char) (message.charAt(i) + key[i - extra]);
			}
			
			int dif = 0;
			if(c < 'a') {
				dif = c - 'a';
				c = (char) (123 + dif);
			} else if (c > 'z') {
				dif = c - 'z';
				c = (char) (96 + dif);
			}
			temp += "" + c;
		}
		message = temp;
	}

	/**
	 * vigenereDecipher deciphers the message given the 'key' according to the Vigenere Cipher
	 * @param key
	 */
	public void vigenereDecipher (int[] key){
		// INSERT YOUR CODE HERE
		int[] decipherKey = new int[key.length];
		for(int i = 0; i < key.length; i++) {
			decipherKey[i] = -key[i];
		}
		vigenereCipher(decipherKey);
	}
	
	/**
	 * transpositionCipher performs the transition cipher on the message by reorganizing the letters and eventually adding characters
	 * @param key
	 */
	public void transpositionCipher (int key){
		// INSERT YOUR CODE HERE
		int count = 0;
		char[][] array = new char[(lengthOfMessage/key)+1][key];
		for(int i = 0; i < (lengthOfMessage/key)+1; i++){
			for(int j = 0; j < key; j++) {
				if(count < lengthOfMessage) {
					array[i][j] = message.charAt(count);
					count++;
				} else {
					array[i][j] = '*';
					count++;
				}
			}
		}
		String temp = "";
		for(int j = 0; j < key; j++) {
			for(int i = 0; i < (lengthOfMessage/key)+1; i++){
				temp += array[i][j];
			}
			
		}
		message = temp;
		lengthOfMessage = key*((lengthOfMessage/key)+1);

	}
	
	/**
	 * transpositionDecipher deciphers the message given the 'key'  according to the transition cipher.
	 * @param key
	 */
	public void transpositionDecipher (int key){
		// INSERT YOUR CODE HERE
		int count = 0;
		char[][] array = new char[lengthOfMessage/key][key];
		for(int j = 0; j < key; j++){
			for(int i = 0; i < lengthOfMessage/key; i++) {
				array[i][j] = message.charAt(count);
				count++;
			}
		}
		String temp = "";
		for(int i = 0; i < lengthOfMessage/key; i++) {
			for(int j = 0; j < key; j++){
				if(array[i][j] != '*') {
					temp += array[i][j];
				}	
			}
			
		}
		message = temp;
		lengthOfMessage = temp.length();
	}
}