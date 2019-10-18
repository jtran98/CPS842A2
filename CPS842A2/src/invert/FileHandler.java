//Jacky Tran 500766582
package invert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

//handles files
public class FileHandler {
	public FileHandler() {
	}
	//Creates and returns an arraylist from a given file, each arraylist element being a line from the file.
	public ArrayList<String> generateArrayFromFile(String pathName){
		ArrayList<String> array = new ArrayList<String>();
		try {
			Stream<String> stream = Files.lines(Paths.get(pathName));
			stream.forEach(x -> array.add(x));
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: File not found");
		}
		return array;
	}
	//Prints a file into the output folder, requires a file name and the file's content as a string
	public void printFile(String fileName, String fileContent) {
		byte[] bytesArray = fileContent.getBytes();
		try {
			File file = new File("src/invert/output/"+fileName);
			FileOutputStream outputStream = new FileOutputStream(file);
			file.createNewFile();
			outputStream.write(bytesArray);
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error: Could not create output stream");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: Could not create file");
		}
		
	}
	public void appendFile(String fileName, String fileContent) {
		byte[] bytesArray = fileContent.getBytes();
		try {
			File file = new File("src/invert/output/"+fileName);
			FileOutputStream outputStream = new FileOutputStream(file,true);
			outputStream.write(bytesArray);
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: Could not write to file");
		}
	}
}