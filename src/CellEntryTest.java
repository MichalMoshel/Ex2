import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CellEntryTest {

    @Test
    public void testConstructorWithValidIndices() {
        CellEntry cell = new CellEntry(1, 2); // Corresponds to "B2"
        assertEquals(1, cell.getX());
        assertEquals(2, cell.getY());
        assertEquals("B2", cell.toString());
        assertTrue(cell.isValid());
    }

    @Test
    public void testConstructorWithValidString() {
        CellEntry cell = new CellEntry("C3");
        assertEquals(2, cell.getX()); // 'C' -> 2
        assertEquals(3, cell.getY());
        assertEquals("C3", cell.toString());
        assertTrue(cell.isValid());
    }

    @Test
    public void testConstructorWithInvalidString() {
        CellEntry cell = new CellEntry("Z99"); // Edge of the valid range
        assertEquals(25, cell.getX());
        assertEquals(99, cell.getY());
        assertEquals("Z99", cell.toString());
        assertTrue(cell.isValid());

        CellEntry invalidCell = new CellEntry("Z100"); // Out of valid range
        assertFalse(invalidCell.isValid());
    }

    @Test
    public void testConstructorWithInvalidFormat() {
        CellEntry cell = new CellEntry("12"); // Invalid format
        assertEquals(-1, cell.getX());
        assertEquals(-1, cell.getY());
        assertFalse(cell.isValid());

        CellEntry anotherInvalidCell = new CellEntry(null); // Null input
        assertEquals(-1, anotherInvalidCell.getX());
        assertEquals(-1, anotherInvalidCell.getY());
        assertFalse(anotherInvalidCell.isValid());
    }

    @Test
    public void testEndCaseBoundary() {
        CellEntry validLowerBound = new CellEntry("A0"); // Lower bound
        assertEquals(0, validLowerBound.getX());
        assertEquals(0, validLowerBound.getY());
        assertTrue(validLowerBound.isValid());
        assertEquals("A0", validLowerBound.toString());

        CellEntry validUpperBound = new CellEntry("Z99"); // Upper bound
        assertEquals(25, validUpperBound.getX());
        assertEquals(99, validUpperBound.getY());
        assertTrue(validUpperBound.isValid());
        assertEquals("Z99", validUpperBound.toString());

        CellEntry invalidUpperBound = new CellEntry("Z100"); // Beyond upper bound
        assertFalse(invalidUpperBound.isValid());
    }

    @Test
    public void testIsValid() {
        CellEntry validCell = new CellEntry("B5");
        assertTrue(validCell.isValid());

        CellEntry invalidX = new CellEntry("[0"); // Invalid column character
        assertFalse(invalidX.isValid());

        CellEntry invalidY = new CellEntry("A-1"); // Negative row index
        assertFalse(invalidY.isValid());
    }
}
