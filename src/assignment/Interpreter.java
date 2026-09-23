package assignment;

import java.io.IOException;
import java.util.*;
import java.io.*;

/**
 * Responsible for loading critter species from text files and interpreting the
 * simple Critter language.
 * 
 * For more information on the purpose of the below two methods, see the
 * included API/ folder and the project description.
 */
public class Interpreter implements CritterInterpreter {

	public void executeCritter(Critter c) {
		ArrayList<String> codeList = (ArrayList<String>) c.getCode();
		c.setNextCodeLine(0);


		while(true) {
			int currentLine = c.getNextCodeLine();
			String[] parts = codeList.get(codeLine).split(" ");
			String action = parts[0];
			int n = 0;
			if(action.equals("hop")) {
				c.hop();
				c.setNextCodeLine(currentLine + 1);
			}
			else if(action.equals("infect")) {
				if (parts.length == 1) {
					c.infect();
				} else if (parts.length > 1) {
					try {
						n = Integer.parseInt(parts[1]);
					} catch (NumberFormatException e) {
						System.err.println("Parameter for infect method was not an int");
					}
					c.infect(n);
				}
			}
			else if(action.equals("write")) {
				int reg = Integer.parseInt(parts[1].substring(1));
				int value = Integer.parseInt(parts[2]);
				c.setReg(reg, value);
				currentLine ++;
			}
			else if(action.equals("add")) {
				int r1 = Integer.parseInt(parts[1].substring(1));
				int r2 = Integer.parseInt(parts[2].substring(1));
				c.setReg(r1, c.getReg(r1) + c.getReg(r2));
				currentLine ++;
			}
			else if(action.equals("sub")) {
				int r1 = Integer.parseInt(parts[1].substring(1));
				int r2 = Integer.parseInt(parts[2].substring(1));
				c.setReg(r1, c.getReg(r1) - c.getReg(r2));
				currentLine ++;

				}
			else if(action.equals("inc")) {
				int r1 = Integer.parseInt(parts[1].substring(1));
				c.setReg(r1, c.getReg(r1) + 1);
				currentLine ++;
			}
			else if(action.equals("dec")) {
				int r1 = Integer.parseInt(parts[1].substring(1));
				c.setReg(r1, c.getReg(r1) - 1);
				currentLine ++;
			}
			else if(action.equals("iflt")) {
				int r1 = Integer.parseInt(parts[1].substring(1));
				int r2 = Integer.parseInt(parts[2].substring(1));
				int nth = Integer.parseInt((parts[3].substring(1)));
				if(c.getReg(r1) < c.getReg(r2)) {
					currentLine = nth;
				}
				else {
					currentLine++;
				}
			}
			else if(action.equals("ifeq")) {
				int r1 = Integer.parseInt(parts[1].substring(1));
				int r2 = Integer.parseInt(parts[2].substring(1));
				int nth = Integer.parseInt((parts[3].substring(1)));
				if (c.getReg(r1) == c.getReg(r2)) {
					currentLine = nth;
				} else {
					currentLine++;
				}
			}
			else if(action.equals("ifgt")) {
				int r1 = Integer.parseInt(parts[1].substring(1));
				int r2 = Integer.parseInt(parts[2].substring(1));
				int nth = Integer.parseInt((parts[3].substring(1)));
				if (c.getReg(r1) > c.getReg(r2)) {
					currentLine = nth;
				} else {
					currentLine++;
				}
			}











		}
		// loop through the ArrayList using .getNextCodeLine()
		// check if currentLine is out of bounds
		// break up each String line to a String array (to separate the instruction from
		// the line number)
		// hella if-else statements?:
		// ACTIONS
		// if parts[0] = hop: critter.hop(); critter.setNextCodeLine(currentLine+1);
		// return;
		// left
		// right
		// eat
		// infect - maybe do a nested if else here - if length of the array is 2, call
		// lineJump

		// JUMPS - HER
		// go
		// ifempty
		// ifhungry
		// ifstarving
		// ifrandom
		// ifangle
		// ifwall
		// ifenemy
		// ifally
		// ifempty

		// REGISTERS - ME
		// write
		// add
		// sub
		// inc
		// dec
		// iflt
		// ifeq
		// ifgt

		return;
	}

	public CritterSpecies loadSpecies(String filename) throws IOException {
		// Check preconditions for user input
		String name;
		ArrayList<String> code = new ArrayList<>();

		// read the file using BufferedReader .readLine()
		try (BufferedReader reader = new BufferedReader(new FileReader(filename)) ) {
			// first line is species name
			name = reader.readLine();
			if(name == null) {
				throw new IllegalArgumentException("File cannot be empty");
			}
			String line;

			// iterate through all instructions, save them to an ArrayList<String>
			// exit at the blank line
			while(!((line = reader.readLine()).isBlank())) {
				code.add(line);
			}
		}
		return new CritterSpecies(name, code);
	}

	// this is a helper method that will translate from the given target line
	// notation for go to the line number
	public int lineJump(String target, int currentLine, Critter cri) {

		if (target.charAt(0) == 'r') {
			String afterR = target.substring(1);
			int rNum = -1;
			try {
				rNum = Integer.parseInt(afterR);
			} catch (NumberFormatException e) {
				System.err.println("Text after 'r' was not an integer");
			}
			return cri.getReg(rNum);
		} else if (target.charAt(0) == '+' || target.charAt(0) == '-') {
			int relJump = 0;
			try {
				relJump = Integer.parseInt(target);
			} catch (NumberFormatException e) {
				System.err.println("Text after '+' or '-' was not an integer");
			}
			return (currentLine + relJump);

		} else {
			int line = currentLine;
			try {
				line = Integer.parseInt(target);
			} catch (NumberFormatException e) {
				System.err.println("Text after 'r' was not an integer");
			}
			return line;
		}

	}









		}
		}
		try {
			k = Integer.
		}
		// 4 cases:
		// n is preceeded by +
		// n is preceeded by -
		// n has no prefix: absolute jump to n
		// n is a register number (preceded by "r"): absolute jump to the register
		// number
		return nextLine;
	}
}
