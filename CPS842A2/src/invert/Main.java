//Jacky Tran 500766582
package invert;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		//create arraylists from cacm.all and stopwords.txt files
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> cacmList = fileHandler.generateArrayFromFile("src/invert/input/cacm.all");
		Scanner scan = new Scanner(System.in);
		System.out.println("Would you like to enable stopword filtering? Y/N");
		String filterInput = scan.nextLine();
		while(!filterInput.equalsIgnoreCase("Y") && !filterInput.equalsIgnoreCase("N")) {
			System.out.println("Invalid input. Please try again.");
			filterInput = scan.nextLine();
		}
		System.out.println("Would you like to enable stemming? Y/N");
		String stemInput = scan.nextLine();
		while(!stemInput.equalsIgnoreCase("Y") && !stemInput.equalsIgnoreCase("N")) {
			System.out.println("Invalid input. Please try again.");
			stemInput = scan.nextLine();
		}
		scan.close();
		DataParser parser = new DataParser();
		//ArrayList<Document> docList = parser.createDocumentArray(cacmList);
		ArrayList<Document> modifiedDocList = parser.createDocumentArray(cacmList);
		if(filterInput.equalsIgnoreCase("Y")) {
			parser.applyStopwordFilter(modifiedDocList);
		}
		if(stemInput.equalsIgnoreCase("Y")) {
			parser.applyStemming(modifiedDocList);
		}
		
		parser.generateDictionaryFile(modifiedDocList);
		System.out.println("dictionary.txt created");
		parser.generatePostingsFile(modifiedDocList);
		System.out.println("postings.txt created");
	}
}