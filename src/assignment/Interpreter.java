package assignment;

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

	// max number of non-action instructions a critter can run in one turn
	private static final int MAX_STEPS = 1000;

	public void executeCritter(Critter c) {
		List<String> codeList = c.getCode();
		int currentLine = c.getNextCodeLine();
		int steps = 0;

		while (true) {
			// infinite loop guard: if no action has been taken after MAX_STEPS
			// instructions, save our place and end the turn so the simulator
			// doesn't freeze. A slow but valid program resumes here next turn.
			if (++steps > MAX_STEPS) {
				c.setNextCodeLine(currentLine);
				return;
			}
			if(currentLine < 1 || currentLine > codeList.size()) {
				return;
			}
			try {
				String[] parts = codeList.get(currentLine - 1).trim().split("\\s+");
				String action = parts[0];

				if (action.equals("hop")) {
					c.hop();
					c.setNextCodeLine(currentLine + 1);
					return;
				} else if (action.equals("left")) {
					c.left();
					c.setNextCodeLine(currentLine + 1);
					return;
				} else if (action.equals("right")) {
					c.right();
					c.setNextCodeLine(currentLine + 1);
					return;
				} else if (action.equals("eat")) {
					c.eat();
					c.setNextCodeLine(currentLine + 1);
					return;
				}

				// line jumps
				else if (action.equals("go")) {
					currentLine = lineJump(parts[1], currentLine, c);
				} else if (action.equals("ifempty")) {
					int bearing = Integer.parseInt(parts[1]);
					if (c.getCellContent(bearing) == Critter.EMPTY) {
						currentLine = lineJump(parts[2], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifhungry")) {
					if (c.getHungerLevel() == Critter.HungerLevel.HUNGRY
							|| c.getHungerLevel() == Critter.HungerLevel.STARVING) {
						currentLine = lineJump(parts[1], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifstarving")) {
					if (c.getHungerLevel() == Critter.HungerLevel.STARVING) {
						currentLine = lineJump(parts[1], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifrandom")) {
					if (c.ifRandom()) {
						currentLine = lineJump(parts[1], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifangle")) {
					int b1 = Integer.parseInt(parts[1]);
					int b2 = Integer.parseInt(parts[2]);

					if (c.getOffAngle(b1) == b2) {
						currentLine = lineJump(parts[3], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifwall")) {
					int bearing = Integer.parseInt(parts[1]);
					if (c.getCellContent(bearing) == Critter.WALL) {
						currentLine = lineJump(parts[2], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifenemy")) {
					int bearing = Integer.parseInt(parts[1]);
					if (c.getCellContent(bearing) == Critter.ENEMY) {
						currentLine = lineJump(parts[2], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifally")) {
					int bearing = Integer.parseInt(parts[1]);
					if (c.getCellContent(bearing) == Critter.ALLY) {
						currentLine = lineJump(parts[2], currentLine, c);
					} else {
						currentLine++;
					}
				}
				// Register methods start here
				else if (action.equals("infect")) {
					if (parts.length == 1) {
						c.infect();                                  // plain "infect"
					} else {
						try {
							c.infect(Integer.parseInt(parts[1]));    // "infect 5"
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException("infect expects a line number, got '" + parts[1] + "'");
						}
					}
					c.setNextCodeLine(currentLine + 1);              // action: save place
					return;                                          // and end the turn
				} else if (action.equals("write")) {
					int reg = parseReg(parts[1]);
					int value = Integer.parseInt(parts[2]);
					c.setReg(reg, value);
					currentLine++;
				} else if (action.equals("add")) {
					int r1 = parseReg(parts[1]);
					int r2 = parseReg(parts[2]);
					c.setReg(r1, c.getReg(r1) + c.getReg(r2));
					currentLine++;
				} else if (action.equals("sub")) {
					int r1 = parseReg(parts[1]);
					int r2 = parseReg(parts[2]);
					c.setReg(r1, c.getReg(r1) - c.getReg(r2));
					currentLine++;

				} else if (action.equals("inc")) {
					int r1 = parseReg(parts[1]);
					c.setReg(r1, c.getReg(r1) + 1);
					currentLine++;
				} else if (action.equals("dec")) {
					int r1 = parseReg(parts[1]);
					c.setReg(r1, c.getReg(r1) - 1);
					currentLine++;
				} else if (action.equals("iflt")) {
					int r1 = parseReg(parts[1]);
					int r2 = parseReg(parts[2]);
					if (c.getReg(r1) < c.getReg(r2)) {
						currentLine = lineJump(parts[3], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifeq")) {
					int r1 = parseReg(parts[1]);
					int r2 = parseReg(parts[2]);

					if (c.getReg(r1) == c.getReg(r2)) {
						currentLine = lineJump(parts[3], currentLine, c);
					} else {
						currentLine++;
					}
				} else if (action.equals("ifgt")) {
					int r1 = parseReg(parts[1]);
					int r2 = parseReg(parts[2]);
					if (c.getReg(r1) > c.getReg(r2)) {
						currentLine = lineJump(parts[3], currentLine, c);
					} else {
						currentLine++;
					}
				} else {
					throw new IllegalArgumentException("unknown instruction '" + action + "'");
				}
			} catch(IllegalArgumentException | IndexOutOfBoundsException e) {
				System.err.println("Line " + currentLine + ": " + e.getMessage());
				return;
			}
		}

	}


	public CritterSpecies loadSpecies(String filename) throws IOException {
		String name;
		// Check preconditions for user input
		ArrayList<String> code = new ArrayList<>();

		// read the file using BufferedReader .readLine()
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			name = reader.readLine();
			if(name == null || name.isBlank()) {
				return null;
			}
			String line;
			// iterate through all instructions, save them to an ArrayList<String>
			// exit at the blank line
			while ((line = reader.readLine()) != null && !line.isBlank()) {
				code.add(line.trim());
			}
		}
		if(code.isEmpty()) {
			return null;
		}
		return new CritterSpecies(name.trim(), code);
	}

	// this is a helper method that will translate from the given target line
	// notation for go to the line number
	public int lineJump(String target, int currentLine, Critter cri) {
		try {
			if (target.charAt(0) == 'r') {
				return cri.getReg(parseReg(target));
			} else if (target.charAt(0) == '+' || target.charAt(0) == '-') {
				return currentLine + Integer.parseInt(target);
			} else {
				return Integer.parseInt(target);
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("bad jump target '" + target + "'");
		}
	}

	private int parseReg(String s) {
		if (!s.startsWith("r")) {
			throw new IllegalArgumentException("Expected a register, got '" + s + "'");
		}
		int n = Integer.parseInt(s.substring(1));
		if (n < 1 || n > Critter.REGISTERS) {
			throw new IllegalArgumentException("Register is out of range 1 to 10");
		}
		return n;
	}

}
