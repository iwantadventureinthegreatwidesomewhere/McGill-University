package assignment1;

public class Tester {

	public static void main(String[] args) {
		//Testing makeValid
		String originalMessage = "It's been snowing, now there's ice everywhere.";
		Message M1 = new Message(originalMessage);
		if (M1.equals(new Message("itsbeensnowingnowtheresiceeverywhere"))){
			System.out.println("makeValid seems correct.");
		}
		else{
			System.out.println("makeValid is incorrect.");
		}
		
		//Testing Caesar cipher
		originalMessage = "Are you going to get the first one ?";
		M1 = new Message(originalMessage);
		int key1 = 5;
		M1.caesarCipher(key1);
		if (M1.equals(new Message ("fwjdtzltnslytljyymjknwxytsj"))){
			System.out.println("Caesar cipher seems correct.");
		}
		else{
			System.out.println("Caesar cipher is incorrect.");
		}
		
		//Testing Caesar decipher
		M1 = new Message ("fwjdtzltnslytljyymjknwxytsj");
		M1.caesarDecipher(key1);
		if (M1.equals(new Message (originalMessage))){
			System.out.println("You paid attention to the range of caesarCipher");
		}
		else{
			System.out.println("the range of caesarCipher is wider than what you coded");
		}
		
		//Testing Caesar analysis
		originalMessage = "Attenborough is widely considered a national treasure in Britain, although he himself does not like the term.[6][7][8] In 2002 he was named among the 100 Greatest Britons following a UK-wide poll for the BBC.[9] He is the younger brother of the late director, producer and actor Richard Attenborough,[10] and older brother of the late motor executive John Attenborough.";
		String codedMessage = "fyyjsgtwtzlmnxbnijqdhtsxnijwjifsfyntsfqywjfxzwjnsgwnyfnsfqymtzlmmjmnrxjqkitjxstyqnpjymjyjwrnsmjbfxsfrjifrtslymjlwjfyjxygwnytsxktqqtbnslfzpbnijutqqktwymjgghmjnxymjdtzsljwgwtymjwtkymjqfyjinwjhytwuwtizhjwfsifhytwwnhmfwifyyjsgtwtzlmfsitqijwgwtymjwtkymjqfyjrtytwjcjhzynajotmsfyyjsgtwtzlm" ;
		Message M2 = new Message(codedMessage);
		M2.caesarAnalysis();
		if (M2.equals(new Message (originalMessage))){
			System.out.println("Caesar analysis seems correct.");
		}
		else{
			System.out.println("Caesar analysis is incorrect.");
		}
		
		//Testing Vigenere Cipher
		originalMessage = "Harder now : go for the Vigenere cipher";
		Message M3 = new Message(originalMessage);
		int[] key3 = {1,2,1,0};
		M3.vigenereCipher(key3);
		if (M3.equals(new Message("icsdftooxipfptuhfxjgfpfrfejpigs"))){
			System.out.println("Vigenere cipher seems correct.");
		}
		else{
			System.out.println("Vigenere cipher is incorrect.");
		}
		
		//Testing Vigenere Decipher
		M3 = new Message("icsdftooxipfptuhfxjgfpfrfejpigs");
		M3.vigenereDecipher(key3);
		if (M3.equals(new Message(originalMessage))){
			System.out.println("Vigenere decipher seems correct.");
		}
		else{
			System.out.println("Vigenere decipher is incorrect.");
		}
		
		//Testing transposition cipher
		originalMessage = "Last one to code and you are good for this assignment !! :)";
		Message M4 = new Message(originalMessage);
		int key4 = 6;
		M4.transpositionCipher(key4);
		if (M4.equals(new Message ("leeuohitataadig*sonrfsn*tcdeoam*ooygrse*ndootsn*", false))){
			System.out.println("Transposition cipher seems correct.");
		}
		else{
			System.out.println("Transposition cipher is incorrect.");
		}
		
		//Testing transposition decipher
		M4 = new Message("leeuohitataadig*sonrfsn*tcdeoam*ooygrse*ndootsn*", false);
		M4.transpositionDecipher(key4);
		if (M4.equals(new Message(originalMessage))){
			System.out.println("Transposition decipher seems correct.");
		}
		else{
			System.out.println("Transposition decipher is incorrect.");
		}
		
	}

}
