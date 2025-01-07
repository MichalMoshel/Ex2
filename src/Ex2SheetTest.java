import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.Arrays;

public class Ex2SheetTest {

    private Ex2Sheet sheet;

    @BeforeEach
    public void setUp() {
        sheet = new Ex2Sheet(5, 5);  // Creates a 5x5 sheet
    }

    @Test
    public void testInitialSheetEmptyCells() {
        // Check if all cells are initialized to an empty cell
        for (int i = 0; i < sheet.width(); i++) {
            for (int j = 0; j < sheet.height(); j++) {
                assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(i, j));
            }
        }
    }

    @Test
    public void testSetValue() {
        sheet.set(1, 1, "Hello");
        assertEquals("Hello", sheet.value(1, 1));

        sheet.set(2, 2, "123");
        assertEquals("123", sheet.value(2, 2));
    }

    @Test
    public void testGetCell() {
        sheet.set(1, 1, "Test");
        assertNotNull(sheet.get(1, 1));
        assertEquals("Test", sheet.get(1, 1).getData());

        assertNull(sheet.get(10, 10));  // Invalid coordinates
    }

    @Test
    public void testInvalidCoordinates() {
        assertNull(sheet.get("Z100"));  // Invalid string coordinates
    }

    @Test
    public void testFindCoordinates() {
        sheet.set(2, 2, "TestCell");
        String coordinates = sheet.findCoordinates(sheet.get(2, 2));
        assertNotNull(coordinates);
        assertEquals("C2", coordinates);  // Assuming A1 starts at (0,0)
    }

    @Test
    public void testEval() {
        // Set up a formula or any cell that should be evaluated
        sheet.set(0, 1, "5");
        sheet.set(2, 2, "=(A1+5)");

        // Evaluate formula and check if the result is correct
        sheet.eval();
        assertEquals("10.0", sheet.value(2, 2));
    }

    @Test
    public void testLoadAndSave() throws IOException {
        // Save sheet to file
        String fileName = "test.csv";
        sheet.set(0, 0, "A1");
        sheet.set(1, 1, "B2");
        sheet.save(fileName);

        // Create a new sheet and load the data from the file
        Ex2Sheet newSheet = new Ex2Sheet(5, 5);
        newSheet.load(fileName);

        // Check if the values were correctly loaded
        assertEquals("A1", newSheet.value(0, 0));
        assertEquals("B2", newSheet.value(1, 1));

        // Clean up the file after test
        new File(fileName).delete();
    }


    @Test
    public void testFormulaParsingError() {
        // Set a cell with a malformed formula
        sheet.set(0, 0, "=A1+");
        sheet.eval();

        // Check for formula parsing error
        assertEquals(Ex2Utils.ERR_FORM, sheet.value(0, 0));
    }

    @Test
    public void testEmptyCellHandling() {
        sheet.set(0, 0, Ex2Utils.EMPTY_CELL);
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(0, 0));
    }

    @Test
    public void testIsInBounds() {
        // Check for valid coordinates
        assertTrue(sheet.isIn(0, 0));
        assertTrue(sheet.isIn(4, 4));  // Bottom-right corner

        // Check for invalid coordinates
        assertFalse(sheet.isIn(5, 5));
        assertFalse(sheet.isIn(-1, 0));
        assertFalse(sheet.isIn(0, -1));
    }
}
