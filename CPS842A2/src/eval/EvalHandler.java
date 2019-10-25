//Jacky Tran 500766582
package eval;

import tools.FileHandler;
import tools.Stemmer;

import java.util.ArrayList;
import java.util.HashMap;

import invert.Document;
import search.QueryHandler;

public class EvalHandler {
	private final String QUERY_FILE_PATH = "src/eval/input/query.txt";
	private final String QRELS_FILE_PATH = "src/eval/input/qrels.txt";
	private final String STOPWORDS_FILE_PATH = "src/invert/input/stopwords.txt";
	private final String DICTIONARY_FILE_PATH = "src/invert/output/dictionary.txt";
	private final String WRITE_FILE_PATH = "src/eval/output/";
	private final String R_PRECISION_FILE_NAME = "r_precision_values.txt";
	private FileHandler fileHandler = new FileHandler();
	public EvalHandler() {
		
	}
	public void writeMeanAveragePrecision(ArrayList<Query> queryList, ArrayList<Qrel> qrelList, ArrayList<Document> list, ArrayList<Document> modifiedList) {
		double meanAP = 0;
		//required code to get similarityList
		QueryHandler queryHandler = new QueryHandler();
		fileHandler.printFile(R_PRECISION_FILE_NAME, "", WRITE_FILE_PATH);
		ArrayList<String> docFreqArr = fileHandler.generateArrayFromFile(DICTIONARY_FILE_PATH);
		HashMap<String, Integer> docFreqMap = new HashMap<String, Integer>();
		for(int i = 0; i < docFreqArr.size(); i+=2) {
			docFreqMap.put(docFreqArr.get(i).substring(6,docFreqArr.get(i).length()), Integer.parseInt(docFreqArr.get(i+1).substring(4,docFreqArr.get(i+1).length())));
		}
		//calculate AP sum
		//this counter is required due to qrels missing relevant doc ids for some queries
		int numberOfAPsCalculated = 0;
		for(int i = 0; i < queryList.size(); i++) {
			String[] queryAsArray = queryList.get(i).getQuery().split("\\s+");
			ArrayList<String> queryAsArrayList = new ArrayList<String>();
			for(String str : queryAsArray) {
				queryAsArrayList.add(str);
			}
			ArrayList<String> similarityList = queryHandler.getSimilarityList(list, modifiedList, queryAsArrayList, docFreqMap);
			//2nd loop required since qrels has missing relevant doc ids, would cause a mismatch otherwise
			for(int j = 0; j < qrelList.size(); j++) {
				if(queryList.get(i).getId().equals(qrelList.get(j).getId())) {
					meanAP += getAveragePrecision(queryList.get(i),qrelList.get(j), similarityList);
					numberOfAPsCalculated ++;
				}
			}
		}
		//divide to get MAP
		meanAP /= numberOfAPsCalculated;
		String fileContent = "Mean Average Precision: "+meanAP;
		fileHandler.printFile("mean_average_precision.txt", fileContent, WRITE_FILE_PATH);
	}
	public double getAveragePrecision(Query query, Qrel qrel, ArrayList<String> similarityList) {
		double result = 0;
		//-1 since qrels start at 1 but queries start at 0
		ArrayList<String> relevantDocs = new ArrayList<String>(qrel.getDocIDs());
		ArrayList<String> retrievedDocs = new ArrayList<String>();
		for(String str : similarityList) {
			String temp = str;
			//trim similarityList entries until you get to the id
			while(!temp.startsWith("ID: ")) {
				temp = temp.substring(1, temp.length());
			}
			//get the id
			temp = temp.substring(4, temp.length());
			retrievedDocs.add(temp);
		}
		//compare results between the actual relevant docs retrieved from qrels and the calculated cossim ones from assumedRelevantDocs
		ArrayList<Integer> indexOfMatches = new ArrayList<Integer>();
		//iterate through all retrieved documents and see if they match the relevant documents
		for(int i = 0; i < retrievedDocs.size(); i++) {
			//if all relevant documents are found, finish
			if(indexOfMatches.size() == relevantDocs.size()) {
				break;
			}
			else {
				//if a docID from the relevant list matches a doc id from the retrieved list, increment numberOfMatches
				for(String relevantDocId : relevantDocs) {
					if(retrievedDocs.get(i).equals(relevantDocId)) {
						//break since there are no repeating relevant doc ids in the same query
						indexOfMatches.add((i+1));
						break;
					}
				}
			}
		}
		//calculate R-Precision
		ArrayList<Double> precisionValues = new ArrayList<Double>(getRPrecision(indexOfMatches));
		String printString = "R-Values for Query #"+query.getId()+"\n";
		for(double value : precisionValues) {
			String temp = String.format("%.3f, ", value);
			printString += temp;
		}
		printString = printString.substring(0,printString.length()-2)+"\n";
		fileHandler.appendFile(R_PRECISION_FILE_NAME, printString, WRITE_FILE_PATH);
		double numerator = 0;
		for(double precision : precisionValues) {
			numerator += precision;
		}
		result = numerator/relevantDocs.size();
		return result;
	}
	//finds recall values uninterpolated
	public ArrayList<Double> getRPrecision(ArrayList<Integer> indexOfMatches){
		ArrayList<Double> precisionValues = new ArrayList<Double>();
		for(int i = 0; i < indexOfMatches.size(); i++) {
			precisionValues.add((1.0/(double)indexOfMatches.get(i)));
		}
		return precisionValues;
	}
	//convert the read file of query.txt into an arraylist of query objects
	public ArrayList<Query> getQueryArray() {
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> queryList = fileHandler.generateArrayFromFile(QUERY_FILE_PATH);
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
		ArrayList<String> qrelsList = fileHandler.generateArrayFromFile(QRELS_FILE_PATH);
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
		ArrayList<String> stopwordsList = fileHandler.generateArrayFromFile(STOPWORDS_FILE_PATH);
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
