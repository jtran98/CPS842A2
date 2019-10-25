//Jacky Tran 500766582
package eval;

import java.util.ArrayList;
import java.util.Scanner;

import invert.Inverter;
import invert.Document;
import invert.FileHandler;

public class Main {
	public static void main(String[] args) {
		FileHandler fileHandler = new FileHandler();
		Inverter inverter = new Inverter();
		EvalHandler evalHandler = new EvalHandler();
		evalHandler.Test();
	}
}
