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
		String filterInput = scan.next();
		while(!filterInput.equalsIgnoreCase("Y") && !filterInput.equalsIgnoreCase("N")) {
			System.out.println("Invalid input. Please try again.");
			filterInput = scan.next();
		}
		System.out.println("Would you like to enable stemming? Y/N");
		String stemInput = scan.next();
		while(!stemInput.equalsIgnoreCase("Y") && !stemInput.equalsIgnoreCase("N")) {
			System.out.println("Invalid input. Please try again.");
			stemInput = scan.next();
		}
		DataParser parser = new DataParser();
		ArrayList<Document> docList = parser.createDocumentArray(cacmList);
		ArrayList<Document> modifiedDocList = parser.createDocumentArray(cacmList);
		if(filterInput.equalsIgnoreCase("Y")) {
			parser.applyStopwordFilter(modifiedDocList);
		}
		if(stemInput.equalsIgnoreCase("Y")) {
			parser.applyStemming(modifiedDocList);
		}
		
		
		for(Document doc: modifiedDocList) {
			System.out.println("ID: "+doc.getId());
			System.out.println("Title: "+doc.getTitle());
			System.out.println("Abstract: "+doc.getAbstract());
			System.out.println("Date: "+doc.getDate());
			System.out.println("Authors: "+doc.getAuthors());
			System.out.println("Content: "+doc.getContent());
		}
		
		
		//TODO: grab the required information to create proper dictionary/postings files
		//create and write dictionary and postings files
		String dictionaryContent = "dictionary filler";
		String postingsContent = "postings filler";
		fileHandler.printFile("dictionary.txt", dictionaryContent);
		fileHandler.printFile("postings.txt", postingsContent);
	}
}