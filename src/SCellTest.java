//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class SCellTest {
//
//    @Test
//    public void testDetermineType() {
//        SCell numberCell = new SCell("123");
//        assertEquals(Ex2Utils.NUMBER, numberCell.getType());
//
//        SCell formulaCell = new SCell("=(A1+A2)");
//        assertEquals(Ex2Utils.FORM, formulaCell.getType());
//
//        SCell textCell = new SCell("Hello");
//        assertEquals(Ex2Utils.TEXT, textCell.getType());
//    }
//
//    @Test
//    public void testSetAndGetData() {
//        SCell cell = new SCell("Test");
//        assertEquals("Test", cell.getData());
//
//        cell.setData("123");
//        assertEquals("Test", cell.getData());
//    }
//
//    @Test
//    public void testIsNumber() {
//        SCell cell = new SCell("123");
//        assertTrue(cell.isNumber("123"));
//        assertFalse(cell.isNumber("ABC"));
//    }
//
//    @Test
//    public void testIsText() {
//        SCell cell = new SCell("Hello");
//        assertTrue(cell.isText("Hello"));
//        assertFalse(cell.isText("=A1+A2"));
//        assertFalse(cell.isText("123"));
//    }
//
//    @Test
//    public void testIsForm() {
//        SCell cell = new SCell("=(A1+A2)");
//        assertTrue(cell.isForm("=(A1+A2)"));
//        assertFalse(cell.isForm("123"));
//        assertFalse(cell.isForm("Hello"));
//    }
//
//    @Test
//    public void testEvaluateFormulaSimple() {
//        Ex2Sheet sheet = new Ex2Sheet();
//        sheet.set(0, 0, "1"); // A0
//        sheet.set(0, 1, "2"); // A1
//
//        SCell formulaCell = new SCell("=(A0+A1)");
//        assertEquals("3.0", formulaCell.evaluateFormula(sheet));
//    }
//
//    @Test
//    public void testEvaluateFormulaWithParentheses() {
//        Ex2Sheet sheet = new Ex2Sheet();
//        sheet.set(0, 0, "1"); // A0
//        sheet.set(0, 1, "2"); // A1
//        sheet.set(0,3,"=((A0)+(A1))");
//        SCell formulaCell = new SCell("=((A0)+(A1))");
//        assertEquals("3.0", formulaCell.evaluateFormula(sheet));
//    }
//
//    @Test
//    public void testEvaluateFormulaWithInvalidReference() {
//        Ex2Sheet sheet = new Ex2Sheet();
//        SCell formulaCell = new SCell("=(A99)");
//        assertThrows(FormulaException.class, () -> {
//            formulaCell.evaluateFormula(sheet, null);
//        });
//
//    }
//
//    @Test
//    public void testEvaluateFormulaDivisionByZero() {
//        Ex2Sheet sheet = new Ex2Sheet();
//        sheet.set(0, 0, "1"); // A0
//        sheet.set(0, 1, "0"); // A1
//
//        SCell formulaCell = new SCell("=(A0/A1)");
//        assertThrows(IllegalArgumentException.class, () -> {
//            formulaCell.evaluateFormula(sheet, null);
//        });
//    }
//
//    @Test
//    public void testEvaluateFormulaWithCycles2() {
//        Ex2Sheet sheet = new Ex2Sheet();
//        sheet.set(0, 0, "100"); // A0
//        sheet.set(0, 1, "=(A0)"); // A1
//        sheet.set(0, 0, "=(A1)"); // A0
//        assertEquals(Ex2Utils.ERR_CYCLE, sheet.value(0,0));
//        assertEquals(Ex2Utils.ERR_CYCLE, ((SCell)sheet.get(0,0)).getShownValue());
//
//    }
//
//    @Test
//    public void testComputeOrder() {
//        Ex2Sheet sheet = new Ex2Sheet();
//        sheet.set(0, 0, "1"); // A0
//        sheet.set(0, 1, "2"); // A1
//        SCell formulaCell = new SCell("=(A0+A1)");
//        formulaCell.evaluateFormula(sheet);
//        assertEquals(1, formulaCell.getOrder());
//    }
//
//    @Test
//    public void testTokenizeFormula() {
//        SCell cell = new SCell("=A1 + (A2 * 3)");
//        String[] tokens = cell.tokenizeFormula("A1 + (A2 * 3)");
//        assertArrayEquals(new String[]{"A1", "+", "(", "A2", "*", "3", ")"}, tokens);
//    }
//
//    @Test
//    public void testIsNumericLiteral() {
//        SCell cell = new SCell("123");
//        assertTrue(cell.isNumericLiteral("123"));
//        assertFalse(cell.isNumericLiteral("ABC"));
//    }
//
//    @Test
//    public void testIsCellReference() {
//        SCell cell = new SCell("=A1");
//        assertTrue(cell.isCellReference("A1"));
//        assertFalse(cell.isCellReference("123"));
//        assertFalse(cell.isCellReference("Hello"));
//    }
//
//    @Test
//    public void testIsEnclosedInParentheses() {
//        SCell cell = new SCell("=(A1+A2)");
//        assertTrue(cell.isEnclosedInParentheses("(A1+A2)"));
//        assertFalse(cell.isEnclosedInParentheses("A1+A2"));
//    }
//
//    @Test
//    public void testFindMainOperator() {
//        SCell cell = new SCell("=A1 + (A2 * A3)");
//        assertEquals(3, cell.findMainOperator("A1 + (A2 * A3)", "+"));
//    }
//}
