//Jacky Tran 500766582
package search;

import java.util.ArrayList;
import java.util.Scanner;

import invert.Inverter;
import invert.Document;
import invert.FileHandler;

public class Main {
	public static void main(String[] args) {
		//create arraylists from cacm.all and stopwords.txt files
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> cacmList = fileHandler.generateArrayFromFile("src/invert/input/cacm.all");
		//ArrayList<String> cacmList = fileHandler.generateArrayFromFile("src/invert/input/test.txt");
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
		Inverter inverter = new Inverter();
		ArrayList<Document> docList = inverter.createDocumentArray(cacmList);
		ArrayList<Document> modifiedDocList = inverter.createDocumentArray(cacmList);
		if(filterInput.equalsIgnoreCase("Y")) {
			inverter.applyStopwordFilter(modifiedDocList);
		}
		if(stemInput.equalsIgnoreCase("Y")) {
			inverter.applyStemming(modifiedDocList);
		}
		
		System.out.println("Please input your first query.");
		String userInput = scan.nextLine();
		QueryHandler queryHandler = new QueryHandler();
		while(!userInput.equalsIgnoreCase("ZZEND")) {
			//turn the user's input into a document object's abstract value, since stemming/stopword filter works specifically for document arraylists
			ArrayList<Document> temp = new ArrayList<Document>();
			temp.add(new Document());
			temp.get(0).setContent(userInput);
			if(filterInput.equalsIgnoreCase("Y")) {
				inverter.applyStopwordFilter(temp);
			}
			if(stemInput.equalsIgnoreCase("Y")) {
				inverter.applyStemming(temp);
			}
			String[] inputArray = temp.get(0).toArray();
			ArrayList<String> newInput = new ArrayList<String>();
			for(int i = 0; i < inputArray.length; i++) {
				newInput.add(inputArray[i].toLowerCase());
			}
			
			queryHandler.parseQuery(docList, modifiedDocList, newInput);
			
			System.out.println("Please input your next query, or 'ZZEND' to finish.");
			userInput = scan.nextLine();
		}
		scan.close();
	}
}