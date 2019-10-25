package eval;

import java.util.ArrayList;
import java.util.Scanner;

import invert.Inverter;
import invert.Document;
import invert.FileHandler;

public class Main {
	public static void main(String[] args) {
		//create arraylists from cacm.all and stopwords.txt files
		FileHandler fileHandler = new FileHandler();
		Inverter inverter = new Inverter();
		ArrayList<String> cacmList = fileHandler.generateArrayFromFile("src/invert/input/cacm.all");
		ArrayList<Document> modifiedDocList = inverter.createDocumentArray(cacmList);
		
	}
}
