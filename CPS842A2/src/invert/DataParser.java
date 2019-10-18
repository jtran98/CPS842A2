//Jacky Tran 500766582
package invert;

import java.util.ArrayList;

public class DataParser {
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
				doc.setTitle(title.substring(0,title.length()));
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
				doc.setAbstract(docAbstract.substring(0,docAbstract.length()-1));
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
		for(Document doc : list) {
			doc.setTitle(doc.getTitle().toLowerCase());
			doc.setAbstract(doc.getAbstract().toLowerCase());
		}
		for(int i = 0; i < list.size(); i++) {
			//first, remove all punctuation
			list.get(i).setTitle(list.get(i).getTitle().replaceAll("\\p{Punct}", ""));
			list.get(i).setAbstract(list.get(i).getAbstract().replaceAll("\\p{Punct}", ""));
			
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
		for(Document doc : list) {
			doc.setTitle(doc.getTitle().toLowerCase());
			doc.setAbstract(doc.getAbstract().toLowerCase());
		}
		for(int i = 0; i < list.size(); i++) {
			//first, remove all punctuation
			list.get(i).setTitle(list.get(i).getTitle().replaceAll("\\p{Punct}", ""));
			list.get(i).setAbstract(list.get(i).getAbstract().replaceAll("\\p{Punct}", ""));
			//for every line in the list, check through every filter word and remove all instances of the filter word if they appear
			for(String filter: stopwordsList) {
				list.get(i).setTitle(list.get(i).getTitle().replaceAll("\\b"+filter+"\\b", ""));
				list.get(i).setAbstract(list.get(i).getAbstract().replaceAll("\\b"+filter+"\\b", ""));
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
}
