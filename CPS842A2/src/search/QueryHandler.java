//Jacky Tran 500766582
package search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;
import invert.Document;
import tools.FileHandler;

public class QueryHandler {
	private final double SIMILARITY_THRESHOLD = 0;
	private final int NUMBER_OF_ENTRIES = 1000;
	private final String DICTIONARY_FILE_PATH = "src/invert/output/dictionary.txt";
	public QueryHandler() {
	}
	public void parseQuery(ArrayList<Document> list, ArrayList<Document> modifiedList, ArrayList<String> query) {
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> docFreqArr = fileHandler.generateArrayFromFile(DICTIONARY_FILE_PATH);
		HashMap<String, Integer> docFreqMap = new HashMap<String, Integer>();
		for(int i = 0; i < docFreqArr.size(); i+=2) {
			docFreqMap.put(docFreqArr.get(i).substring(6,docFreqArr.get(i).length()), Integer.parseInt(docFreqArr.get(i+1).substring(4,docFreqArr.get(i+1).length())));
		}
		ArrayList<String> similarityList = getSimilarityList(list, modifiedList, query, docFreqMap);
		if(similarityList.size() > NUMBER_OF_ENTRIES) {
			for(int i = 0; i < NUMBER_OF_ENTRIES; i++) {
				//add rank to every entry in similarityList
				similarityList.set(i, "Rank: "+(i+1)+", "+similarityList.get(i));
				System.out.println(similarityList.get(i));
			}
		}
		else if(similarityList.size() > 0){
			for(int i = 0; i < similarityList.size(); i++) {
				//add rank to every entry in similarityList
				similarityList.set(i, "Rank: "+(i+1)+", "+similarityList.get(i));
				System.out.println(similarityList.get(i));
			}
		}
		else {
			System.out.println("No relevant documents.");
		}
	}
	public ArrayList<String> getSimilarityList(ArrayList<Document> list, ArrayList<Document> modifiedList, ArrayList<String> query, HashMap<String, Integer> docFreqMap) {
		ArrayList<String> similarityList = new ArrayList<String>();
		//calculate queryAsDoc once since there's no point doing it for every doc in the collection for the same query
		Document queryAsDoc = new Document();
		String queryString = "";
		for(String str : query) {
			queryString += str+" ";
		}
		queryAsDoc.setContent(queryString);
		
		for(int i = 0; i < modifiedList.size(); i++) {
			//calculate similarity and add that to a list, as well as the doc's title and author names
			double similarity = calculateCosineSimilarity(modifiedList, modifiedList.get(i), queryAsDoc, query, docFreqMap);
			if(similarity > SIMILARITY_THRESHOLD) {
				similarityList.add(String.format("Sim: %.3f, T: %s, A: %s, ID: %s", similarity, list.get(i).getTitle(), list.get(i).getAuthors(), list.get(i).getId()));
			}
		}
		//sort the array based on similarity values, since it is the first variable in each string
		similarityList = (ArrayList<String>) similarityList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		return similarityList;
	}
	//Calculates cosine similarity between a document and a query
	public double calculateCosineSimilarity(ArrayList<Document> modifiedList, Document document, Document queryAsDoc, ArrayList<String> query, HashMap<String, Integer> docFreqMap) {
		ArrayList<Double> docVector = new ArrayList<Double>();
		ArrayList<Double> queryVector = new ArrayList<Double>();
		//creating the term vector that will be iterated through, contains all terms found in both the document and query
		//ignores all other terms, since weight=TF*IDF and TF being 0 would make terms not found in either document/query adds unnecessary summing of 0s
		//for the magnitude/dot product
		ArrayList<String> terms = new ArrayList<String>(query);
		for(String str : document.toArray()) {
			terms.add(str);
		}
		
		terms = (ArrayList<String>) terms.stream().distinct().sorted().collect(Collectors.toList());
		//filling the doc/query vectors with weights for each term
		for(String term : terms) {
			int docFreq = document.getFrequencyOfTerm(term);
			int queryFreq = queryAsDoc.getFrequencyOfTerm(term);
			int termCount = 0;
			if(docFreqMap.containsKey(term)) {
				termCount = docFreqMap.get(term);
			}
			//increments if term appears in query
			if(queryAsDoc.getContent().contains(term)) {
				termCount++;
			}
			double idf = Math.log10(modifiedList.size()/termCount);
			double docTF = 0;
			double queryTF = 0;
			if(docFreq > 0) {
				docTF = 1+Math.log10(docFreq);
			}
			if(queryFreq > 0) {
				queryTF = 1+Math.log10(queryFreq);
			}
			//add the calculated weighting of tf*idf to both vectors
			docVector.add(docTF*idf);
			queryVector.add(queryTF*idf);
		}
		double docMagnitude = 0;
		double queryMagnitude = 0;
		double numerator = 0;
		for(int i = 0; i < terms.size(); i++) {
			docMagnitude += Math.pow(docVector.get(i),2);
			queryMagnitude += Math.pow(queryVector.get(i), 2);
			numerator += (docVector.get(i)*queryVector.get(i));
		}
		docMagnitude = Math.sqrt(docMagnitude);
		queryMagnitude = Math.sqrt(queryMagnitude);
		Double result = (numerator/(docMagnitude*queryMagnitude));
		if(result.isNaN()) {
			result = 0.0;
		}
		return result;
	}
}
