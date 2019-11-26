//Jacky Tran 500766582
package search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;
import invert.Document;
import invert.Inverter;
import invert.Link;
import tools.FileHandler;

public class QueryHandler {
	private final double SIMILARITY_THRESHOLD = 0;
	private final int NUMBER_OF_ENTRIES = 10;
	private final double DAMPING_FACTOR = 0.85;
	private final String DICTIONARY_FILE_PATH = "src/invert/output/dictionary.txt";
	private double cosSimCoefficient;
	private double pageRankCoefficient;
	
	public QueryHandler(double cosSim, double pageRank) {
		cosSimCoefficient = cosSim;
		pageRankCoefficient = pageRank;
	}
	public void parseQuery(ArrayList<Document> list, ArrayList<Document> modifiedList, ArrayList<String> query) {
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> docFreqArr = fileHandler.generateArrayFromFile(DICTIONARY_FILE_PATH);
		HashMap<String, Integer> docFreqMap = new HashMap<String, Integer>();
		for(int i = 0; i < docFreqArr.size(); i+=2) {
			docFreqMap.put(docFreqArr.get(i).substring(6,docFreqArr.get(i).length()), Integer.parseInt(docFreqArr.get(i+1).substring(4,docFreqArr.get(i+1).length())));
		}
		ArrayList<String> similarityList = new ArrayList<String>(getSimilarityList(list, modifiedList, query, docFreqMap));
		int size = 0;
		if(similarityList.size() > NUMBER_OF_ENTRIES) {
			size = NUMBER_OF_ENTRIES;
		}
		else if(similarityList.size() > 0){
			size = similarityList.size();
		}
		if(size > 0){
			for(int i = 0; i < size; i++) {
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
		Inverter inverter = new Inverter();
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> cacmList = fileHandler.generateArrayFromFile("src/invert/input/cacm.all");
		//doclist is created so the inverter's citations list can be generated
		//fix later so that creating the citations list isn't dependent on this
		ArrayList<Document> docList = new ArrayList<Document>(inverter.createDocumentArray(cacmList));
		ArrayList<Link> citationsList = new ArrayList<Link>(inverter.getCitations());
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
			String docId = Integer.toString(i+1);
			double pageRank =  calculatePageRank(citationsList,docId);
			double score = (cosSimCoefficient*similarity) + (pageRankCoefficient*pageRank);
			if(score > SIMILARITY_THRESHOLD) {
				similarityList.add(String.format("Score: %.3f, T: %s, A: %s, ID: %s", score, list.get(i).getTitle(), list.get(i).getAuthors(), list.get(i).getId()));
			}
		}
		//sort the array based on similarity values, since it is the first variable in each string
		similarityList = (ArrayList<String>) similarityList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		return similarityList;
	}
	//calculates pagerank for a document, assumes all pages have an initial PR of 0.01
	public double calculatePageRank(ArrayList<Link> citations, String docId) {
		//for every link, if a document X is connected to the doc with id docId:
		//find every document that X links to, and divide doc X's PR/# of linked docs
		//repeat for every doc that links to doc #docId, then multiply everything by damping factor
		ArrayList<Double> values = new ArrayList<Double>();
		for(Link link : citations) {
			if(link.getConnection().equals(docId)) {
				String connectionValue = link.getValue();
				double numberOfConnections = 0;
				for(Link link2 : citations) {
					if(link2.getValue().equals(connectionValue)) {
						numberOfConnections++;
					}
				}
				values.add((0.01/numberOfConnections));
			}
		}
		double sum = 0;
		for(Double num : values) {
			sum += num;
		}
		sum *= DAMPING_FACTOR;
		return sum;
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
