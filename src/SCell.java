import java.util.*;

/**
 * Represents a cell in a spreadsheet. It can contain text, numbers, or formulas.
 * Provides methods to determine the cell's type, evaluate formulas, and manage dependencies.
 */
public class SCell implements Cell {
    private String line;
    private int type;
    private int order;

    /**
     * Constructs an SCell with the specified data.
     * @param s The data to initialize the cell.
     */
    public SCell(String s) {
        setData(s);
    }

    /**
     * Determines the type of the cell based on its data.
     * @param data The raw string data of the cell.
     * @return The type of the cell as defined in Ex2Utils (FORM, NUMBER, or TEXT).
     */
    private int determineType(String data) {
        if(this.isForm(data)){
            return Ex2Utils.FORM;
        } else if (this.isNumber(data)) {
            return Ex2Utils.NUMBER;
        } else {
            return Ex2Utils.TEXT;
        }
    }

    /**
     * Gets the order of evaluation for the cell.
     * @return The order of the cell.
     */
    @Override
    public int getOrder() {
        return order;
    }

    /**
     * Compares the current cell to another object for equality.
     * @param obj The object to compare to.
     * @return true if the cells are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Cell)){
            return false;
        }
        return this.toString().equals(obj.toString());
    }

    /**
     * Returns the string representation of the cell data.
     * @return The data of the cell as a string.
     */
    @Override
    public String toString() {
        return getData();
    }

    /**
     * Sets the data of the cell and determines its type.
     * @param s The new data to set in the cell.
     */
    @Override
    public void setData(String s) {
        line = s;
        setType(determineType(s));
    }

    /**
     * Gets the data of the cell.
     * @return The data of the cell.
     */
    @Override
    public String getData() {
        return line;
    }

    /**
     * Gets the type of the cell.
     * @return The type of the cell (e.g., FORM, NUMBER, or TEXT).
     */
    @Override
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the cell.
     * @param t The new type for the cell.
     */
    @Override
    public void setType(int t) {
        type = t;
    }

    /**
     * Sets the order of evaluation for the cell.
     * @param t The new order of the cell.
     */
    @Override
    public void setOrder(int t) {
        this.order = t;
    }

    /**
     * Checks if the text represents a valid number.
     * @param text The text to check.
     * @return true if the text is a valid number, false otherwise.
     */
    public boolean isNumber(String text){
        try{
            Double d = Double.parseDouble(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if the text represents valid text (not a number or formula).
     * @param text The text to check.
     * @return true if the text is valid text, false otherwise.
     */
    public boolean isText(String text){
        return !isNumber(text) && !isForm(text);
    }

    /**
     * Checks if the text represents a formula (starts with "=").
     * @param text The text to check.
     * @return true if the text is a formula, false otherwise.
     */
    public boolean isForm(String text){
        return text != null && text.startsWith("=") && text.length() > 1;
    }

    /**
     * Evaluates the formula in the cell, considering possible dependencies.
     * @param sheet The sheet containing the cell references.
     * @return The evaluated result of the formula as a string.
     */
    public String evaluateFormula(Ex2Sheet sheet) {
        return evaluateFormula(sheet, new HashSet<>());
    }

    /**
     * Evaluates the formula in the cell, considering possible dependencies and cycles.
     * @param sheet The sheet containing the cell references.
     * @param visited A set of visited cell references to detect cycles.
     * @return The evaluated result of the formula as a string.
     */
    public String evaluateFormula(Ex2Sheet sheet, Set<String> visited) {
        if (type != Ex2Utils.FORM) {
            return line; // Only evaluate formulas
        }

        if (visited == null) {
            visited = new HashSet<>();
        }

        // Use the current cell as the entry point
        CellEntry thisCell = CellEntry.fromString(sheet.findCoordinates(this));
        if (thisCell != null && visited.contains(thisCell.toString())) {
            type = Ex2Utils.ERR_CYCLE_FORM; // Cycle detected
            throw new CycleException("Cycle detected in formula for cell: " + thisCell);
        }
        if(thisCell != null){
            visited.add(thisCell.toString());
        }

        // Track visited cells to detect cycles
        String formula = line.substring(1); // Remove "="
        String result;

        result = String.valueOf(parseFormula(formula, sheet, visited));
        type = Ex2Utils.NUMBER; // Successfully evaluated to a number
        order = computeOrder(formula, sheet); // Update order based on dependencies

        if(thisCell != null){
            visited.remove(thisCell.toString()); // Remove from visited after evaluation
        }

        line = result; // Update cell data with the evaluated result
        return result;
    }

    /**
     * Parses a formula and computes its value recursively.
     * @param formula The formula to evaluate.
     * @param sheet The sheet containing the cell references.
     * @param visited A set of visited cells to detect cycles.
     * @return The result of the formula as a double.
     */
    private double parseFormula(String formula, Ex2Sheet sheet, Set<String> visited) {
        formula = formula.trim();

        // Base cases: number or cell reference
        if (isNumericLiteral(formula)) { // Numeric literal
            return Double.parseDouble(formula);
        }
        if (isCellReference(formula)) { // Cell reference
            CellEntry ref = CellEntry.fromString(formula);
            if (ref == null || !sheet.isIn(ref.getX(), ref.getY())) {
                throw new FormulaException("Invalid cell reference: " + formula);
            }

            Cell refCell = sheet.get(ref.getX(), ref.getY());
            if (refCell.getType() == Ex2Utils.NUMBER) {
                return Double.parseDouble(refCell.getData());
            } else if (refCell.getType() == Ex2Utils.FORM) {
                return Double.parseDouble(((SCell) refCell).evaluateFormula(sheet, visited));
            } else {
                throw new FormulaException("Invalid or unresolved cell reference: " + formula);
            }
        }

        // Recursive parsing: parentheses
        if (isEnclosedInParentheses(formula) && hasMatchingParentheses(formula.substring(1, formula.length() - 1))) {
            return parseFormula(formula.substring(1, formula.length() - 1), sheet, visited);
        }

        // Recursive parsing: binary operations
        String[] operators = Ex2Utils.M_OPS;
        for (String op : operators) {
            int index = findMainOperator(formula, op);
            if (index != -1) {
                String left = formula.substring(0, index).trim();
                String right = formula.substring(index + 1).trim();
                double leftVal = parseFormula(left, sheet, visited);
                double rightVal = parseFormula(right, sheet, visited);
                switch (op) {
                    case "+":
                        return leftVal + rightVal;
                    case "-":
                        return leftVal - rightVal;
                    case "*":
                        return leftVal * rightVal;
                    case "/":
                        if (rightVal == 0) {
                            throw new IllegalArgumentException("Division by zero");
                        }
                        return leftVal / rightVal;
                }
            }
        }

        throw new IllegalArgumentException("Invalid formula syntax: " + formula);
    }

    /**
     * Helper function to check if a string is a numeric literal.
     * @param str The string to check.
     * @return true if the string is a valid numeric literal, false otherwise.
     */
    boolean isNumericLiteral(String str) {
        if (str == null || str.isEmpty()) return false;
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Helper function to check if a string is a valid cell reference.
     * @param str The string to check.
     * @return true if the string is a valid cell reference, false otherwise.
     */
    boolean isCellReference(String str) {
        if (str == null || str.isEmpty()) return false;
        char firstChar = str.charAt(0);
        if (!Character.isLetter(firstChar)) return false;

        for (int i = 1; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

    /**
     * Helper function to check if a string is enclosed in parentheses.
     * @param str The string to check.
     * @return true if the string is enclosed in parentheses, false otherwise.
     */
    boolean isEnclosedInParentheses(String str) {
        return str.startsWith("(") && str.endsWith(")");
    }

    /**
     * Checks if parentheses are balanced in a string.
     * @param str The string to check.
     * @return true if parentheses are balanced, false otherwise.
     */
    boolean hasMatchingParentheses(String str) {
        int balance = 0;
        for (char c : str.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false; // Mismatched closing parenthesis
        }
        return balance == 0; // True if parentheses are balanced
    }

    /**
     * Finds the main operator in a formula considering parentheses.
     * @param formula The formula string.
     * @param operator The operator to search for.
     * @return The index of the main operator in the formula.
     */
    int findMainOperator(String formula, String operator) {
        int level = 0;
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if (c == '(') level++;
            if (c == ')') level--;
            if (level == 0 && formula.startsWith(operator, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Computes the order of a formula based on the maximum dependency order.
     * @param formula The formula to evaluate.
     * @param sheet The sheet containing the referenced cells.
     * @return The computed order for the formula.
     */
    private int computeOrder(String formula, Ex2Sheet sheet) {
        int maxOrder = 0;

        // Extract potential cell references
        String[] tokens = tokenizeFormula(formula);
        for (String token : tokens) {
            if (isCellReference(token)) {
                CellEntry ref = CellEntry.fromString(token);
                if (sheet.isIn(ref.getX(), ref.getY())) {
                    Cell refCell = sheet.get(ref.getX(), ref.getY());
                    maxOrder = Math.max(maxOrder, refCell.getOrder());
                }
            }
        }

        return maxOrder + 1; // Order is 1 + max dependency order
    }

    /**
     * Tokenizes the formula into components (e.g., operands, operators).
     * @param formula The formula to tokenize.
     * @return An array of strings representing the components of the formula.
     */
    String[] tokenizeFormula(String formula) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (char c : formula.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                current.append(c);
            } else {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                if (!Character.isWhitespace(c)) {
                    tokens.add(String.valueOf(c));
                }
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }

        return tokens.toArray(new String[0]);
    }
}
