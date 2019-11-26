//Jacky Tran 500766582
package search;

import java.util.ArrayList;
import java.util.Scanner;

import invert.Inverter;
import tools.FileHandler;
import invert.Document;

public class Main {
	public static void main(String[] args) {
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> cacmList = fileHandler.generateArrayFromFile("src/invert/input/cacm.all");
		Scanner scan = new Scanner(System.in);
		System.out.println("Please enter a coefficient value for Cosine Similarity Score (W1)");
		String w1 = scan.nextLine();
		System.out.println("Please enter a coefficient value for Page Rank (W2)");
		String w2 = scan.nextLine();
		double d1 = Double.parseDouble(w1);
		double d2 = Double.parseDouble(w2);
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
		ArrayList<Document> docList = new ArrayList<Document>(inverter.createDocumentArray(cacmList));
		ArrayList<Document> modifiedDocList = new ArrayList<Document>(inverter.createDocumentArray(cacmList));
		if(filterInput.equalsIgnoreCase("Y")) {
			System.out.println("Filtering");
			inverter.applyStopwordFilter(modifiedDocList);
		}
		if(stemInput.equalsIgnoreCase("Y")) {
			System.out.println("Stemming");
			inverter.applyStemming(modifiedDocList);
		}
		
		System.out.println("Please input your first query.");
		String userInput = scan.nextLine();
		QueryHandler queryHandler = new QueryHandler(d1,d2);
		while(!userInput.equalsIgnoreCase("ZZEND")) {
			//turn the user's input into a document object's abstract value, since stemming/stopword filter works specifically for document arraylists
			ArrayList<Document> temp = new ArrayList<Document>();
			temp.add(new Document());
			//this must be setAbstract (or setTitle presumably) to work, setContent breaks it.
			temp.get(0).setAbstract(userInput);
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