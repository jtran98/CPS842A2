//Jacky Tran 500766582
package eval;

import java.util.ArrayList;

import invert.FileHandler;

public class EvalHandler {
	public EvalHandler() {
		
	}
	public void Test() {
		FileHandler fileHandler = new FileHandler();
		ArrayList<String> queryList = fileHandler.generateArrayFromFile("src/eval/input/query.txt");
		ArrayList<String> qrelsList = fileHandler.generateArrayFromFile("src/eval/input/qrels.txt");
		for(String str : queryList) {
			System.out.println(str);
		}
	}
}
