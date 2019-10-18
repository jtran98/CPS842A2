package invert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Test {
	public static void main(String[] args) {
		ArrayList<Document> docList = new ArrayList<Document>();
		
		docList.add(new Document("1","term1 term2 term1 term1 term3","term4","date1","authors1"));
		docList.add(new Document("2","","term1 term2","date2","authors2"));
		docList.add(new Document("3","term3 term4 term2","","date3","authors3"));
		docList.add(new Document("4","term3 term2","term1 term5 term5 ","date4","authors4"));
		
		for(Document doc : docList) {
			doc.autoGenerateContent();
		}
		
		FileHandler fileHandler = new FileHandler();
		fileHandler.printFile("testing.txt", "bobba\nhome\njohn\n");
		fileHandler.appendFile("testing.txt", "fourthline");
	}
}
