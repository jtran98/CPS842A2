//Jacky Tran 500766582
package eval;

import tools.FileHandler;
import tools.Stemmer;

import java.util.ArrayList;

public class EvalHandler {
	public EvalHandler() {
		
	}
	public void test() {
		ArrayList<Query> queryList = new ArrayList<Query>(getQueryArray());
		ArrayList<Qrel> qrelList = new ArrayList<Qrel>(getQrelArray());
	}
	//convert the read file of query.txt into an arraylist of query objects
	public ArrayList<Query> getQueryArray() {
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> queryList = fileHandler.generateArrayFromFile("src/eval/input/query.txt");
		ArrayList<Query> parsedQueries = new ArrayList<Query>();
		Query query = new Query();
		for(int i = 0; i < queryList.size(); i++) {
			if(queryList.get(i).startsWith(".I")&& queryList.get(i).substring(3,4).matches("\\d+")) {
				if(!query.getId().equals("")) {
					parsedQueries.add(query);
					query = new Query();
				}
				query.setId(queryList.get(i).substring(3));
			}
			else if(queryList.get(i).startsWith(".W")) {
				String queryString = "";
				for(int j = i+1; i <queryList.size(); j++) {
					queryString += queryList.get(j)+" ";
					if(queryList.get(j+1).startsWith(".") && queryList.get(j+1).length() == 2) {
						break;
					}
				}
				String[] queryArr = queryString.replaceAll("\\p{Punct}", "").toLowerCase().split("\\s+");
				String temp = "";
				for(int j = 0; j < queryArr.length; j++) {
					temp += queryArr[j]+" ";
				}
				query.setQuery(temp.substring(0,temp.length()-1));
			}
		}
		//no need to add the final query unlike in Inverter, due to the final query being non existent
		return parsedQueries;
	}
	//convert the read file of qrels.txt into an arraylist of qrel objects
	public ArrayList<Qrel> getQrelArray(){
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> qrelsList = fileHandler.generateArrayFromFile("src/eval/input/qrels.txt");
		ArrayList<Qrel> parsedQrels = new ArrayList<Qrel>();
		
		Qrel qrel = new Qrel();
		//hardcoded the first instance, currentQueryID is required because qrel trims leading 0s
		String currentQueryID = "01";
		qrel.setId("1");
		for(int i = 0; i < qrelsList.size(); i++) {
			String line  = qrelsList.get(i);
			if(!line.substring(0,2).equals(currentQueryID)) {
				parsedQrels.add(qrel);
				qrel = new Qrel();
				//removes leading 0s for the id
				String tempId = qrelsList.get(i).substring(0,2);
				while(tempId.startsWith("0")) {
					tempId = tempId.substring(1,tempId.length());
				}
				qrel.setId(tempId);
				currentQueryID = qrelsList.get(i).substring(0,2);
			}
			//removes leading 0s for the docid, so comparison with document ids is easiler later on
			String tempDocId = qrelsList.get(i).substring(3,7);
			while(tempDocId.startsWith("0")) {
				tempDocId = tempDocId.substring(1,tempDocId.length());
			}
			qrel.getDocIDs().add(tempDocId);
		}
		parsedQrels.add(qrel);
		return parsedQrels;
	}
	//apply stopword filter to queries
	public void applyStopwordFilter(ArrayList<Query> list) {
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> stopwordsList = fileHandler.generateArrayFromFile("src/invert/input/stopwords.txt");
		for(int i = 0; i < list.size(); i++) {
			//for every line in the list, check through every filter word and remove all instances of the filter word if they appear
			for(String filter: stopwordsList) {
				String[] filteredQuery = list.get(i).getQuery().replaceAll("\\b"+filter+"\\b", "").split("\\s+");
				String temp = "";
				for(int j = 0; j < filteredQuery.length; j++) {
					temp += filteredQuery[j]+" ";
				}
				list.get(i).setQuery(temp.substring(0,temp.length()-1));
			}
			//remove characters of whitespace at the beginning
			while(list.get(i).getQuery().startsWith(" ")){
				list.get(i).setQuery(list.get(i).getQuery().substring(1));
			}
		}
	}
	//Stem queries
	public void applyStemming(ArrayList<Query> list) {
		for(int i = 0; i < list.size(); i++) {
			//break down each line into individual words, stem each word, then remake the new line with the stemmed words
			Stemmer queryStemmer = new Stemmer();
			String[] originalQuery = list.get(i).getQuery().split("\\s+");
			String stemmedQuery = "";
			for(int j = 0; j < originalQuery.length; j++) {
				queryStemmer.add(originalQuery[j].toCharArray(), originalQuery[j].length());
				queryStemmer.stem();
				//append each stemmed word to the new stemmed line, and add a space between each word
				stemmedQuery += queryStemmer.toString()+" ";
			}
			//replace the old lines with the newly stemmed lines, and remove the final whitespace
			list.get(i).setQuery(stemmedQuery.substring(0,stemmedQuery.length()-1));
			
			//remove characters of whitespace at the beginning
			while(list.get(i).getQuery().startsWith(" ")){
				list.get(i).setQuery(list.get(i).getQuery().substring(1));
			}
		}
	}
}
