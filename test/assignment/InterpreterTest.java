package assignment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import assignment.*;

/* 
 * Any comments and methods here are purely descriptions or suggestions.
 * This is your test file. Feel free to change this as much as you want.
 */

public class InterpreterTest {

    private static Interpreter interpreter;
    private TestCritter critter;

    // This will run ONCE before all other tests. It can be useful to setup up
    // global variables and anything needed for all of the tests.
    @BeforeAll
    static void setupAll() {
        interpreter = new Interpreter();
    }

    // This will run before EACH test.
    @BeforeEach
    void setupEach() {
        critter = new TestCritter();
    }

    // You can test execute critter here. You may want to make additional tests and
    // your own testing harness. See spec section 2.5 for more details.
    @Test
    void testExecuteCritter() {
        List<String> instructions = new ArrayList<>();
        instructions.add("hop");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);

        assertEquals("hop", critter.getLastAction());
        assertEquals(2, critter.getNextCodeLine());

    }

    // Test load species. You may want to make more tests for different cases here.
    @Test
    void testLoadSpecies() throws IOException {
        CritterSpecies species = interpreter.loadSpecies("species/Rover.cri");
        assertNotNull(species);
        assertEquals("Rover", species.getName());
        assertFalse(species.getCode().isEmpty());
    }

    // this will test to ensure that load species stops at a blank line and ignores
    // comments after
    @Test
    void testLoadSpeciesTerminates() throws IOException {
        File tempFile = File.createTempFile("test_comments", ".cri");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("CommentCritter\n");
            writer.write("hop\n");
            writer.write("left\n");
            writer.write("\n");
            writer.write("This comment should be ignored!!\n");
        }

        CritterSpecies species = interpreter.loadSpecies(tempFile.getPath());
        assertNotNull(species);
        assertEquals("CommentCritter", species.getName());
        assertEquals(2, species.getCode().size());
    }

    @Test
    void testLoadSpeciesTrimExtraSpace() throws IOException {
        File testFile = new File("testfile.cri");
        try {
            FileWriter writer = new FileWriter(testFile);
            writer.write("  PaddedCritter  \n");
            writer.write("   right   \n");
            writer.write("hop\n");
            writer.close();

            CritterSpecies species = interpreter.loadSpecies(testFile.getPath());

            assertNotNull(species);
            assertEquals("PaddedCritter", species.getName().trim());
            assertEquals(2, species.getCode().size());
            assertEquals("right", species.getCode().get(0).toString().trim());
            assertEquals("hop", species.getCode().get(1).toString().trim());
        } finally {
            if (testFile.exists()) {
                testFile.delete();
            }
        }
    }

    @Test
    void testActionLeftAndRight() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("left");
        instructions.add("right");
        critter.setCodeList(instructions);

        critter.setNextCodeLine(1);
        interpreter.executeCritter(critter);
        assertEquals("left", critter.getLastAction());
        assertEquals(2, critter.getNextCodeLine());

        interpreter.executeCritter(critter);
        assertEquals("right", critter.getLastAction());
        assertEquals(3, critter.getNextCodeLine());
    }

    @Test
    void testActionEat() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("eat");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("eat", critter.getLastAction());
        assertEquals(2, critter.getNextCodeLine());
    }

    @Test
    void testActionInfectWithoutParameter() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("infect");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("infect", critter.getLastAction());
        assertEquals(0, critter.getLastInfectTarget());
        assertEquals(2, critter.getNextCodeLine());
    }

    @Test
    void testActionInfectWithParameter() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("infect 7");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("infect", critter.getLastAction());
        assertEquals(7, critter.getLastInfectTarget());
        assertEquals(2, critter.getNextCodeLine());
    }

    @Test
    void testGo() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("go 3");
        instructions.add("left");
        instructions.add("hop");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("hop", critter.getLastAction());
        assertEquals(4, critter.getNextCodeLine());
    }

    @Test
    void testGoIncrement() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("go +2");
        instructions.add("left");
        instructions.add("right");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("right", critter.getLastAction());
        assertEquals(4, critter.getNextCodeLine());
    }

    @Test
    void testGoDecrement() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("go 3");
        instructions.add("hop");
        instructions.add("go -1");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("hop", critter.getLastAction());
        assertEquals(3, critter.getNextCodeLine());
    }

    @Test
    void testGoRegister() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("write r1 4");
        instructions.add("go r1");
        instructions.add("left");
        instructions.add("eat");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("eat", critter.getLastAction());
        assertEquals(5, critter.getNextCodeLine());
    }

    @Test
    void testRegisterWriteIncDec() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("write r1 5");
        instructions.add("inc r1");
        instructions.add("write r2 10");
        instructions.add("dec r2");
        instructions.add("hop");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("hop", critter.getLastAction());
        assertEquals(6, critter.getReg(1));
        assertEquals(9, critter.getReg(2));
    }

    @Test
    void testRegisterAddSub() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("write r1 20");
        instructions.add("write r2 8");
        instructions.add("add r1 r2");
        instructions.add("sub r2 r1");
        instructions.add("hop");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("hop", critter.getLastAction());
        assertEquals(28, critter.getReg(1));
        assertEquals(-20, critter.getReg(2));
    }

    @Test
    void testIfLtCondition() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("write r1 2");
        instructions.add("write r2 5");
        instructions.add("iflt r1 r2 5");
        instructions.add("left");
        instructions.add("right");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("right", critter.getLastAction());
        assertEquals(6, critter.getNextCodeLine());
    }

    @Test
    void testIfEqCondition() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("write r1 7");
        instructions.add("write r2 7");
        instructions.add("ifeq r1 r2 5");
        instructions.add("left");
        instructions.add("hop");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("hop", critter.getLastAction());
        assertEquals(6, critter.getNextCodeLine());
    }

    @Test
    void testIfGtCondition() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("write r1 4");
        instructions.add("write r2 9");
        instructions.add("ifgt r1 r2 5");
        instructions.add("eat");
        instructions.add("left");
        critter.setCodeList(instructions);

        interpreter.executeCritter(critter);
        assertEquals("eat", critter.getLastAction());
        assertEquals(5, critter.getNextCodeLine());
    }

    @Test
    void testIfEmpty() {
        ArrayList<String> instructions = new ArrayList<>();
        instructions.add("ifempty 0 4");
        instructions.add("left");
        instructions.add("go 5");
        instructions.add("hop");
        instructions.add("right");
        critter.setCodeList(instructions);

        // if it's empty ahead
        critter.setCell(0, Critter.EMPTY);
        interpreter.executeCritter(critter);
        assertEquals("hop", critter.getLastAction());

        // enemy ahead
        critter.setNextCodeLine(1);
        critter.setCell(0, Critter.ENEMY);
        interpreter.executeCritter(critter);
        assertEquals("left", critter.getLastAction());
    }

    static class TestCritter implements Critter {
        private List<String> code = new ArrayList<>();
        private int nextCodeLine = 1;
        private final int[] registers = new int[11]; // 1-indexed for r1-r10
        private HungerLevel hungerLevel = HungerLevel.SATISFIED;
        private final Map<Integer, Integer> surroundings = new HashMap<>();
        private final Map<Integer, Integer> offAngles = new HashMap<>();

        private String lastAction = null;
        private int lastInfectTarget = -1;

        public TestCritter() {
            int[] bearings = { 0, 45, 90, 135, 180, 225, 270, 315 };
            for (int b : bearings) {
                surroundings.put(b, Critter.EMPTY);
                offAngles.put(b, 0);
            }
        }

        public void setCodeList(List<String> instructions) {
            this.code = new ArrayList<>(instructions);
        }

        public void setCell(int bearing, int content) {
            surroundings.put(bearing, content);
        }

        public void setOffAngle(int bearing, int angle) {
            offAngles.put(bearing, angle);
        }

        public void setHunger(HungerLevel level) {
            this.hungerLevel = level;
        }

        public String getLastAction() {
            return lastAction;
        }

        public int getLastInfectTarget() {
            return lastInfectTarget;
        }

        @Override
        public List<String> getCode() {
            return code;
        }

        @Override
        public int getNextCodeLine() {
            return nextCodeLine;
        }

        @Override
        public void setNextCodeLine(int line) {
            this.nextCodeLine = line;
        }

        @Override
        public int getReg(int regNum) {
            return registers[regNum];
        }

        @Override
        public void setReg(int regNum, int val) {
            registers[regNum] = val;
        }

        @Override
        public int getCellContent(int bearing) {
            return surroundings.getOrDefault(bearing, Critter.EMPTY);
        }

        @Override
        public int getOffAngle(int bearing) {
            return offAngles.getOrDefault(bearing, 0);
        }

        @Override
        public HungerLevel getHungerLevel() {
            return hungerLevel;
        }

        @Override
        public void hop() {
            lastAction = "hop";
        }

        @Override
        public void left() {
            lastAction = "left";
        }

        @Override
        public void right() {
            lastAction = "right";
        }

        @Override
        public void eat() {
            lastAction = "eat";
        }

        @Override
        public void infect() {
            lastAction = "infect";
            lastInfectTarget = 0;
        }

        @Override
        public void infect(int lineNum) {
            lastAction = "infect";
            lastInfectTarget = lineNum;
        }

        @Override
        public boolean ifRandom() {
            return Math.random() < 0.5;
        }
    }

}
