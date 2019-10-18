//Jacky Tran 500766582
package invert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class DataParser {
	final int INTERVAL_BETWEEN_WRITES = 50;
	public DataParser(){
	}
	public ArrayList<Document> createDocumentArray(ArrayList<String> contentList){
		ArrayList<Document> docList = new ArrayList<Document>();
		Document doc = new Document();
		for(int i = 0; i < contentList.size(); i++) {
			//get id
			if(contentList.get(i).startsWith(".I") && contentList.get(i).substring(3,4).matches("\\d+")) {
				if(!doc.getId().equals("")) {
					docList.add(doc);
					doc = new Document();
				}
				doc.setId(contentList.get(i).substring(3));
			}
			//get title
			else if(contentList.get(i).equals(".T")) {
				String title = "";
				for(int j = i+1; j < contentList.size(); j++) {
					if(contentList.get(j).startsWith(".") && contentList.get(j).length() == 2) {
						break;
					}
					title += contentList.get(j)+" ";
				}
				String[] titleArr = title.replaceAll("\\p{Punct}", "").toLowerCase().split("\\s+");
				String temp = "";
				for(int j = 0; j < titleArr.length; j++) {
					temp += titleArr[j]+" ";
				}
				doc.setTitle(temp.substring(0,temp.length()-1));
			}
			//get abstract
			else if(contentList.get(i).equals(".W")) {
				String docAbstract = "";
				for(int j = i+1; j < contentList.size(); j++) {
					docAbstract += contentList.get(j)+" ";
					if(contentList.get(j+1).startsWith(".") && contentList.get(j+1).length() == 2) {
						break;
					}
				}
				String[] abstractArr = docAbstract.replaceAll("\\p{Punct}", "").toLowerCase().split("\\s+");
				String temp = "";
				for(int j = 0; j < abstractArr.length; j++) {
					temp += abstractArr[j]+" ";
				}
				doc.setAbstract(temp.substring(0,temp.length()-1));
			}
			//get publication date
			else if(contentList.get(i).equals(".B")) {
				doc.setDate(contentList.get(i+1));
			}
			//get author list
			else if(contentList.get(i).equals(".A")) {
				String authors = "";
				for(int j = i+1; j < contentList.size(); j++) {
					authors += contentList.get(j)+" ";
					if(contentList.get(j+1).startsWith(".") && contentList.get(j+1).length() == 2) {
						break;
					}
				}
				doc.setAuthors(authors.substring(0,authors.length()-1));
			}
			doc.setContent(doc.getTitle()+" "+doc.getAbstract());
		}
		//adds final document to document array
		docList.add(doc);
		return docList;
	}
	//stems all the words in the document array's title and abstract fields, as well as updates the content fields
	public void applyStemming(ArrayList<Document> list){
		for(int i = 0; i < list.size(); i++) {
			//break down each line into individual words, stem each word, then remake the new line with the stemmed words
			Stemmer titleStemmer = new Stemmer();
			Stemmer abstractStemmer = new Stemmer();
			String[] originalTitle = list.get(i).getTitle().split("\\s+");
			String[] originalAbstract = list.get(i).getAbstract().split("\\s+");
			String stemmedTitle = "";
			String stemmedAbstract = "";
			for(int j = 0; j < originalTitle.length; j++) {
				titleStemmer.add(originalTitle[j].toCharArray(), originalTitle[j].length());
				titleStemmer.stem();
				//append each stemmed word to the new stemmed line, and add a space between each word
				stemmedTitle += titleStemmer.toString()+" ";
			}
			for(int j = 0; j < originalAbstract.length; j++) {
				abstractStemmer.add(originalAbstract[j].toCharArray(), originalAbstract[j].length());
				abstractStemmer.stem();
				//append each stemmed word to the new stemmed line, and add a space between each word
				stemmedAbstract += abstractStemmer.toString()+" ";
			}
			//replace the old lines with the newly stemmed lines, and remove the final whitespace
			list.get(i).setTitle(stemmedTitle.substring(0,stemmedTitle.length()-1));
			list.get(i).setAbstract(stemmedAbstract.substring(0,stemmedAbstract.length()-1));
			
			//remove characters of whitespace at the beginning
			while(list.get(i).getTitle().startsWith(" ")){
				list.get(i).setTitle(list.get(i).getTitle().substring(1));
			}
			while(list.get(i).getAbstract().startsWith(" ")){
				list.get(i).setAbstract(list.get(i).getAbstract().substring(1));
			}
			list.get(i).setContent(list.get(i).getTitle()+" "+list.get(i).getAbstract());
		}
	}
	//filters out all the stopwords in all the titles and abstracts of the document array, as well as updating the content fields
	public void applyStopwordFilter(ArrayList<Document> list){
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> stopwordsList = fileHandler.generateArrayFromFile("src/invert/input/stopwords.txt");
		for(int i = 0; i < list.size(); i++) {
			//for every line in the list, check through every filter word and remove all instances of the filter word if they appear
			for(String filter: stopwordsList) {
				String[] filteredTitle = list.get(i).getTitle().replaceAll("\\b"+filter+"\\b", "").split("\\s+");
				String[] filteredAbstract = list.get(i).getAbstract().replaceAll("\\b"+filter+"\\b", "").split("\\s+");
				String temp = "";
				for(int j = 0; j < filteredTitle.length; j++) {
					temp += filteredTitle[j]+" ";
				}
				list.get(i).setTitle(temp.substring(0,temp.length()-1));
				temp = "";
				for(int j = 0; j < filteredAbstract.length; j++) {
					temp += filteredAbstract[j]+" ";
				}
				list.get(i).setAbstract(temp.substring(0,temp.length()-1));
			}
			//remove characters of whitespace at the beginning
			while(list.get(i).getTitle().startsWith(" ")){
				list.get(i).setTitle(list.get(i).getTitle().substring(1));
			}
			while(list.get(i).getAbstract().startsWith(" ")){
				list.get(i).setAbstract(list.get(i).getAbstract().substring(1));
			}
			list.get(i).setContent(list.get(i).getTitle()+" "+list.get(i).getAbstract());
		}
	}
	//generates the appropriate string for the dictionary file (alphabetically sorted terms as keys, and values being their document frequency)
	public void generateDictionaryFile(ArrayList<Document> list) {
		FileHandler fileHandler = new FileHandler();
		fileHandler.printFile("dictionary.txt", "");
		HashMap<String, Integer> termFrequencyMap = new HashMap<String, Integer>();
		ArrayList<String> allTerms = new ArrayList<String>();
		//for every document, break down the content into a string array and find first occurrence of every term
		for(Document doc : list) {
			List<String> terms = Arrays.asList(doc.toArray());
			HashMap<String, Boolean> temp = new HashMap<String,Boolean>();
			//for every term in the content variable of the document
			for(int i = 0; i < terms.size(); i++) {
				//add every term to allTerms, will be sorted later to contain only distinct terms
				allTerms.add(terms.get(i));
				//if statement makes sure only the first instance of each term in a doc is recorded
				if(temp.get(terms.get(i))==null) {
					temp.put(terms.get(i),true);
					//increase count everytime a term appears (only once per document)
					int count = termFrequencyMap.containsKey(terms.get(i)) ? termFrequencyMap.get(terms.get(i)) : 0;
					termFrequencyMap.put(terms.get(i), count+1);
				}
			}
			
		}
		//removes empty indicies
		termFrequencyMap.remove("");
		//sorts map
		Map<String, Integer> sortedDocFrequencyMap = new TreeMap<String, Integer>(termFrequencyMap);
		
		//sorts all terms alphabetically
		allTerms = (ArrayList<String>) allTerms.stream().distinct().sorted().collect(Collectors.toList());
		ArrayList<String> newAllTerms = new ArrayList<String>();
		for(int i = 0; i < allTerms.size(); i++) {
			if(!allTerms.get(i).equals("")) {
				newAllTerms.add(allTerms.get(i));
			}
		}
		String result = "";
		int index = 0;
		for(String term : newAllTerms) {
			result +="Term: "+term+"\nDF: "+sortedDocFrequencyMap.get(term)+"\n";
			if(index == INTERVAL_BETWEEN_WRITES) {
				fileHandler.appendFile("dictionary.txt", result);
				index = 0;
				result = "";
			}
			index++;
		}
		fileHandler.appendFile("dictionary.txt", result);
	}
	
	/**generates string for postings file, contains every term, which documents they show up in (using sorted document ids), how
	many times they occur in each document and in which positions
	*/
	public void generatePostingsFile(ArrayList<Document> list) {
		FileHandler fileHandler = new FileHandler();
		fileHandler.printFile("postings.txt", "");
		ArrayList<String> allTerms = new ArrayList<String>();
		//create arraylist containing every unique term in all documents
		for(Document doc : list) {
			List<String> terms = Arrays.asList(doc.toArray()).stream().sorted().collect(Collectors.toList());
			for(int i = 0; i < terms.size(); i++) {
				if(terms.get(i).equals("")) {
					terms.remove(i);
					continue;
				}
				allTerms.add(terms.get(i));
			}			
		}
		//sorting and removing empty indices
		allTerms = (ArrayList<String>) allTerms.stream().distinct().sorted().collect(Collectors.toList());
		ArrayList<String> newAllTerms = new ArrayList<String>();
		for(int i = 0; i < allTerms.size(); i++) {
			if(!allTerms.get(i).equals("")) {
				newAllTerms.add(allTerms.get(i));
			}
		}
		int index = 0;
		String result = "";
		for(String term : newAllTerms) {
			result += "Term: "+term+"\n";
			for(Document doc : list) {
				if(doc.getContent().contains(term)) {
					if(doc.getTermPositions(term).size()>0) {
						result+="Doc #"+doc.getId()+"\n";
						result+="Freq: "+doc.getTermPositions(term).size()+"\n";
						result+="Pos: ";
						ArrayList<Integer> positions = doc.getTermPositions(term);
						for(int i = 0; i < positions.size(); i++) {
							result +=positions.get(i)+1+",";
						}
						//remove final comma
						result = result.substring(0,result.length()-1);
						result+="\n";
					}
				}
			}
			index++;
			if(index == INTERVAL_BETWEEN_WRITES) {
				fileHandler.appendFile("postings.txt", result);
				index = 0;
				result = "";
			}
		}
		fileHandler.appendFile("postings.txt", result);
	}
}