//Jacky Tran 500766582
package eval;

import java.util.ArrayList;
import java.util.Scanner;

import invert.Document;
import invert.Inverter;
import tools.FileHandler;

public class Main {
	public static void main(String[] args) {
		EvalHandler evalHandler = new EvalHandler();
		Inverter inverter = new Inverter();
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> cacmList = fileHandler.generateArrayFromFile("src/invert/input/cacm.all");
		ArrayList<Document> docList = inverter.createDocumentArray(cacmList);
		ArrayList<Document> modifiedDocList = inverter.createDocumentArray(cacmList);
		
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
		ArrayList<Query> queryList = new ArrayList<Query>(evalHandler.getQueryArray());
		ArrayList<Qrel> qrelList = new ArrayList<Qrel>(evalHandler.getQrelArray());
		if(filterInput.equalsIgnoreCase("Y")) {
			evalHandler.applyStopwordFilter(queryList);
			inverter.applyStopwordFilter(modifiedDocList);
		}
		if(stemInput.equalsIgnoreCase("Y")) {
			evalHandler.applyStemming(queryList);
			inverter.applyStemming(modifiedDocList);
		}
		evalHandler.writeMeanAveragePrecision(queryList, qrelList, docList, modifiedDocList);
	}
}
