package assignment;

import java.io.IOException;
import java.util.*;

/**
 * Responsible for loading critter species from text files and interpreting the
 * simple Critter language.
 * 
 * For more information on the purpose of the below two methods, see the
 * included API/ folder and the project description.
 */
public class Interpreter implements CritterInterpreter {

	public void executeCritter(Critter c) {
		// loop through the ArrayList using .getNextCodeLine()
		// check if currentLine is out of bounds
		// break up each String line to a String array (to separate the instruction from
		// the line number)
		// hella if-else statements?:
		// ACTIONS
		// if parts[0] = hop: critter.hop(); crotter.setNextCodeLine(currentLine+1);
		// return;
		// left
		// right
		// eat
		// infect - maybe do a nested if else here - if length of the array is 2, call
		// lineJump

		// JUMPS
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

		// REGISTERS
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
		String name;
		ArrayList<String> code;
		// read the file using BufferedReader .readLine()
		// first line is species name
		// iterate through all instructions, save them to an ArrayList<String>
		// exit at the blank line

		return new CritterSpecies(name, code);
	}

	// this is a helper method that will translate from the given target line
	// notation for go to the line number
	public int lineJump(String target, int currentLine, Critter cri) {
		int nextLine;
		// 4 cases:
		// n is preceeded by +
		// n is preceeded by -
		// n has no prefix: absolute jump to n
		// n is a register number (preceded by "r"): absolute jump to the register
		// number
		return nextLine;
	}
}
