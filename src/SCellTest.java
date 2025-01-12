import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class SCellTest {

    @Test
    public void testNumericCellInitialization() {
        // Test that a cell containing a number is properly initialized
        SCell cell = new SCell("42");
        assertEquals("42.0", cell.getData()); // Check the value is stored as a formatted number
        assertEquals(Ex2Utils.NUMBER, cell.getType()); // Ensure the cell is identified as numeric
    }

    @Test
    public void testTextCellInitialization() {
        // Test that a cell containing plain text is properly initialized
        SCell cell = new SCell("Hello");
        assertEquals("Hello", cell.getData()); // Verify the text content
        assertEquals(Ex2Utils.TEXT, cell.getType()); // Ensure the cell is identified as text
    }

    @Test
    public void testFormulaCellInitialization() {
        // Test that a cell containing a formula is properly initialized
        SCell cell = new SCell("=A1+B2");
        assertEquals("=A1+B2", cell.getData()); // Ensure the formula is stored correctly
        assertEquals(Ex2Utils.FORM, cell.getType()); // Check the cell type is identified as a formula

        // Verify dependencies were correctly parsed
        ArrayList<String> dependencies = cell.getDependencies();
        assertTrue(dependencies.contains("A1"));
        assertTrue(dependencies.contains("B2"));
        assertEquals(2, dependencies.size());
    }

    @Test
    public void testInvalidFormula() {
        // Test that an invalid formula is properly flagged
        SCell cell = new SCell("=A1+");
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, cell.getType()); // Ensure the cell is marked as having a format error
        assertEquals("=A1+", cell.getData()); // The formula should remain as is
    }

    @Test
    public void testDependencyDetection() {
        // Test that dependencies are detected in a complex formula
        SCell cell = new SCell("=A1+B2/C3-D4");
        ArrayList<String> dependencies = cell.getDependencies();
        assertTrue(dependencies.contains("A1"));
        assertTrue(dependencies.contains("B2"));
        assertTrue(dependencies.contains("C3"));
        assertTrue(dependencies.contains("D4"));
        assertEquals(4, dependencies.size());
    }

    @Test
    public void testSetDataUpdatesType() {
        // Test that setting new data updates the cell type
        SCell cell = new SCell("42");
        assertEquals(Ex2Utils.NUMBER, cell.getType()); // Initially numeric

        cell.setData("Hello");
        assertEquals(Ex2Utils.TEXT, cell.getType()); // Updated to text

        cell.setData("=A1+B2");
        assertEquals(Ex2Utils.FORM, cell.getType()); // Updated to formula
    }

    @Test
    public void testIsValidForm() {
        // Test the validity of various formula strings
        assertTrue(SCell.isValidForm("A1+B2"));
        assertTrue(SCell.isValidForm("(A1+B2)*C3"));
        assertFalse(SCell.isValidForm("A1++B2")); // Invalid operator sequence
        assertFalse(SCell.isValidForm("A1+")); // Missing operand
        assertFalse(SCell.isValidForm("A1+B2#")); // Invalid character
    }
}
