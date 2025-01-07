import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class CellEntryTest {

    @Test
    void testFromString() {
        String str = "A1";
        CellEntry cell = CellEntry.fromString(str);
        assertEquals(0, cell.getX());
        assertEquals(1, cell.getY());
    }

    @Test
    void testGetX() {
        CellEntry cell = new CellEntry(3, 5);
        assertEquals(3, cell.getX());
    }

    @Test
    void testGetY() {
        CellEntry cell = new CellEntry(3, 5);
        assertEquals(5, cell.getY());
    }

    @Test
    void testEquals() {
        CellEntry cell1 = new CellEntry(3, 5);
        CellEntry cell2 = new CellEntry(3, 5);
        CellEntry cell3 = new CellEntry(1, 2);

        assertEquals(cell1, cell2);
        assertNotEquals(cell1, cell3);
    }
}
