package search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.Collectors;

import invert.Document;

public class QueryHandler {
	private int topK = 100;
	
	
	Random rand = new Random(10);
	public QueryHandler() {
	}
	public void parseQuery(ArrayList<Document> list, ArrayList<Document> modifiedList, String[] query) {
		if(topK > modifiedList.size()) {
			topK = modifiedList.size();
		}
		ArrayList<String> similarityList = new ArrayList<String>();
		for(int i = 0; i < modifiedList.size(); i++) {
			//calculate similarity and add that to a list, as well as the doc's title and author names
			double similarity = calculateCosineSimilarity(modifiedList.get(i),query);
			similarityList.add(String.format("Sim: %.3f, T: %s, A: %s", similarity, list.get(i).getTitle(), list.get(i).getAuthors()));
		}
		//sort the array based on similarity values, since it is the first variable in each string
		similarityList = (ArrayList<String>) similarityList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
		for(int i = 0; i < topK; i++) {
			//add rank to every entry in similarityList
			similarityList.set(i, "Rank: "+(i+1)+", "+similarityList.get(i));
			System.out.println(similarityList.get(i));
		}
	}
	public double calculateCosineSimilarity(Document document, String[] query) {
		double value = 0;
		
		value = rand.nextDouble();
		return value;
	}
}
