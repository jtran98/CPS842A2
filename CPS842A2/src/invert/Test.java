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
		
		docList.add(new Document("1","term1 term2 term3","term4","date1","authors1"));
		docList.add(new Document("2","","term1 term2","date2","authors2"));
		docList.add(new Document("3","term3 term4 term2","","date3","authors3"));
		docList.add(new Document("4","term3 term2","term1 term5 term5 ","date4","authors4"));
		
		for(Document doc : docList) {
			doc.autoGenerateContent();
		}
		
		
		/**
		HashMap<String, Integer> docFrequencyMap = new HashMap<String, Integer>();
		HashMap<String, Integer> termPositionMap = new HashMap<String, Integer>();
		for(Document doc : docList) {
			String[] terms = doc.getContent().split("\\s+");
			for(int i = 0; i < terms.length; i++) {
				int count = docFrequencyMap.containsKey(terms[i]) ? docFrequencyMap.get(terms[i]) : 0;
				docFrequencyMap.put(terms[i], count+1);
				termPositionMap.put(terms[i], i+1);
			}
		}
		docFrequencyMap.remove("");
		termPositionMap.remove("");
		Map<String, Integer> sortedDocFrequencyMap = new TreeMap<String, Integer>(docFrequencyMap);
		Map<String, Integer> sortedTermPositionMap = new TreeMap<String, Integer>(termPositionMap);
		
		sortedTermPositionMap.forEach((key, value) -> System.out.println("Term: "+key+"\nPosition: "+value));
		*/
		/**
		 *  [1, 1, 1, 1]
			[1, 1]
			[1, 1, 1]
			[1, 1, 1, 1]
			[term1, term2, term3, term4]
			[term1, term2]
			[term2, term3, term4]
			[term1, term2, term3, term5]
		 */
		
		
		/**postings: every term has:
			Term: Bob
			Appears in document (id): 3 position: 7
			Appears in document (id): 5 position: 2,6,4,8
			Appears in document (id): 8 position: 72
			Appears in document (id): 34 position: 17
		*/
	}
}
