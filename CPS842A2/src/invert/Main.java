package invert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Main {
	public static void main(String[] args) {
		//create arraylists from cacm.all and stopwords.txt files
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> cacmList = fileHandler.generateArrayFromFile("src/invert/input/cacm.all");
		ArrayList<String> stopwordsList = fileHandler.generateArrayFromFile("src/invert/input/stopwords.txt");
		
		ArrayList<Document> documentsList = new ArrayList<Document>();
		for(String line : cacmList) {
			Document doc = new Document();
			if(line.startsWith(".I")) {
				doc.setId(line.substring(3,line.length()));
			}
			else if(line.startsWith(".T"))
		}
		
		//create and write dictionary and postings files
		String dictionaryContent = "dictionary filler";
		String postingsContent = "postings filler";
		fileHandler.printFile("dictionary.txt", dictionaryContent);
		fileHandler.printFile("postings.txt", postingsContent);
		
	}
}