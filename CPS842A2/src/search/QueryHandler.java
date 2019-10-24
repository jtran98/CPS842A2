//Jacky Tran 500766582
package search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

import invert.DataParser;
import invert.Document;

public class QueryHandler {
	private int topK = 100;
	private final double THRESHOLD = 0.00;
	
	Random rand = new Random(10);
	public QueryHandler() {
	}
	public void parseQuery(ArrayList<Document> list, ArrayList<Document> modifiedList, ArrayList<String> query) {
		if(topK > modifiedList.size()) {
			topK = modifiedList.size();
		}
		ArrayList<String> similarityList = new ArrayList<String>();
		
		for(int i = 0; i < modifiedList.size(); i++) {
			//calculate similarity and add that to a list, as well as the doc's title and author names
			double similarity = calculateCosineSimilarity(modifiedList, modifiedList.get(i), query);
			similarityList.add(String.format("Sim: %.5f, T: %s, A: %s", similarity, list.get(i).getTitle(), list.get(i).getAuthors()));
		}
		//sort the array based on similarity values, since it is the first variable in each string
		similarityList = (ArrayList<String>) similarityList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		for(int i = 0; i < topK; i++) {
			//add rank to every entry in similarityList
			similarityList.set(i, "Rank: "+(i+1)+", "+similarityList.get(i));
			System.out.println(similarityList.get(i));
		}
	}
	//Calculates cosine similarity between a document and a query
	public double calculateCosineSimilarity(ArrayList<Document> modifiedList, Document document, ArrayList<String> query) {
		DataParser parser = new DataParser();
		ArrayList<String> allTerms = parser.getAllTerms(modifiedList);
		ArrayList<Double> docVector = new ArrayList<Double>();
		ArrayList<Double> queryVector = new ArrayList<Double>();
		String queryString = "";
		for(String str : query) {
			queryString += str+" ";
		}
		Document queryAsDoc = new Document();
		queryAsDoc.setContent(queryString);
		//filling the doc/query vectors with weights for each term
		//note that since the iteration is through allTerms which is calculated through cacm.all, any terms in the query not found in that file will be ignored
		//since the idf would not be able to be calculated, as idf would be log(# of docs/0)
		for(String term : allTerms) {
			int docFreq = document.getFrequencyOfTerm(term);
			int queryFreq = queryAsDoc.getFrequencyOfTerm(term);
			int termCount = 0;
			//change this to read from the dictionary file instead of calculating document frequency again
			for(Document doc : modifiedList) {
				if(doc.getContent().contains(term)) {
					termCount++;
				}
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
		for(int i = 0; i < allTerms.size(); i++) {
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
