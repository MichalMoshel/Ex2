import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Ex2SheetTest {

    @Test
    public void testInitializationWithDimensions() {
        // Creating a 5x5 sheet to test initialization logic
        // This size is chosen to validate proper handling of smaller tables.
        // It ensures the implementation handles boundary conditions and typical small-scale scenarios correctly.
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        assertEquals(5, sheet.width());
        assertEquals(5, sheet.height());

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                assertEquals("", sheet.value(x, y));
            }
        }
    }

    @Test
    public void testSetAndGetCell() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(1, 1, "42");
        assertEquals("42", sheet.value(1, 1));

        sheet.set(0, 0, "Hello");
        assertEquals("Hello", sheet.value(0, 0));
    }

    @Test
    public void testInvalidCellAccess() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        assertNull(sheet.get(3, 3));
        assertNull(sheet.get(-1, 0));
    }

    @Test
    public void testCellFormulaEvaluation() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=5+3");
        sheet.set(0, 1, "=A0*2");
        sheet.eval();

        assertEquals("8.0", sheet.value(0, 0));
        assertEquals("16.0", sheet.value(0, 1));
    }

    @Test
    public void testCircularDependency() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=A1");
        sheet.set(0, 1, "=A0");
        sheet.eval();

        assertEquals("ERR_CYCLE", sheet.value(0, 0));
        assertEquals("ERR_CYCLE", sheet.value(0, 1));
    }

    @Test
    public void testOutOfBoundsHandling() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=A4");
        sheet.eval();
        assertEquals("ERR_FORM", sheet.value(0, 0));
    }

    @Test
    public void testFileLoadingAndSaving() throws Exception {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=5+5");
        sheet.set(1, 1, "Hello");

        String tempFile = "test_sheet.csv";
        // This tests the file-saving functionality and its integration with the loading mechanism.
        sheet.save(tempFile);

        Ex2Sheet loadedSheet = new Ex2Sheet(3, 3);
        loadedSheet.load(tempFile);

        assertEquals(sheet.value(0, 0), loadedSheet.value(0, 0));
        assertEquals(sheet.value(1, 1), loadedSheet.value(1, 1));
    }

    @Test
    public void testDepthCalculation() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);
        sheet.set(0, 0, "=B1+C2");
        sheet.set(1, 1, "5");
        sheet.set(2, 2, "10");
        int[][] depth = sheet.depth();

        // This test checks the computation of cell dependencies and their respective depth values.
        assertEquals(0, depth[1][1]);
        assertEquals(0, depth[2][2]);
        assertTrue(depth[0][0] > 0); // Formula cell
    }
}
