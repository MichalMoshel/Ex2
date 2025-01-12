import java.util.ArrayList;

// Class representing a specific type of cell (SCell), implementing the Cell interface
public class SCell implements Cell {
    private String line; // Raw data of the cell
    private int type; // Type of the cell (e.g., number, formula, text)
    private int order; // Order for computation or sorting
    private String computed; // Computed value of the cell after evaluation
    private ArrayList<String> dependencies = new ArrayList<>(); // List of dependent cell references

    // Constructor to initialize SCell with a string input
    public SCell(String s) {
        setData(s); // Set initial data and determine its type
    }

    // Check if the given string is a formula (starts with '=')
    public boolean isForm(String s) {
        if (s.isEmpty()) {
            return false; // Empty strings cannot be formulas
        }

        if (s.charAt(0) == '=') { // Formula must start with '='
            if (s.length() == 1) { // '=' alone is invalid
                type = Ex2Utils.ERR_FORM_FORMAT; // Set error type for invalid formula
                return false;
            }
            s = s.replaceAll(" ", ""); // Remove spaces for validation
            boolean res = isValidForm(s.substring(1)); // Check the formula's validity
            if (!res) {
                type = Ex2Utils.ERR_FORM_FORMAT; // Mark as error if invalid
            }
            return res;
        }
        return false; // Not a formula if it doesn't start with '='
    }

    // Helper function to check if a character is a valid operator
    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    // Check if the string is a valid number
    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s); // Attempt parsing the string to a double
            return true;
        } catch (NumberFormatException e) {
            return false; // Not a number if exception occurs
        }
    }

    // Getter for order
    @Override
    public int getOrder() {
        return order;
    }

    // Overriding toString method to return the raw data
    @Override
    public String toString() {
        return getData();
    }

    // Set the data for the cell and determine its type
    @Override
    public void setData(String s) {
        line = s; // Store raw input
        if (isNumber(s)) { // If input is a valid number
            type = Ex2Utils.NUMBER; // Mark type as number
            double d = Double.parseDouble(s);
            line = "" + d; // Normalize the number format
        } else if (isForm(s)) { // If input is a valid formula
            type = Ex2Utils.FORM; // Mark type as formula
            findDependencies(); // Identify cell references in the formula
        } else { // Otherwise, treat as text or invalid formula
            if (type != Ex2Utils.ERR_FORM_FORMAT) {
                type = Ex2Utils.TEXT; // Default to text type
            }
        }
    }

    // Getter for raw data
    @Override
    public String getData() {
        return line;
    }

    // Getter for cell type
    @Override
    public int getType() {
        return type;
    }

    // Setter for cell type
    @Override
    public void setType(int t) {
        type = t;
    }

    // Setter for computation order
    @Override
    public void setOrder(int t) {
        order = t;
    }

    // Getter for computed value
    public String getComputed() {
        return computed;
    }

    // Setter for computed value
    public void setComputed(String t) {
        computed = t;
    }

    // Extract dependencies (cell references) from the formula
    public void findDependencies() {
        if (line == null) {
            return;
        }
        String s = line.substring(1); // Remove '='
        s = s.replaceAll(" ", ""); // Remove spaces
        String[] words = s.split("[+\\-*/() ]"); // Split by operators
        for (String word : words) {
            if (!word.isEmpty() && word.substring(0, 1).matches("[A-Za-z]")) { // Check for cell reference
                if (word.length() <= 3) { // Assuming references like A1, B2
                    Index2D c = new CellEntry(word); // Validate cell reference
                    if (c.isValid()) {
                        dependencies.add(word); // Add valid reference to dependencies
                    }
                }
            }
        }
    }

    // Getter for dependencies
    public ArrayList<String> getDependencies() {
        return dependencies;
    }

    // Validate the syntax of the formula
    public static boolean isValidForm(String s) {
        if (s.isEmpty()) {
            return false; // Empty formula is invalid
        }

        // Ensure the string contains only valid characters
        boolean onlyAlphaNumeric = s.matches("^[a-zA-Z0-9+\\-*/().]+$");
        if (!onlyAlphaNumeric) {
            return false;
        }

        // FSM-like validation for formula structure
        final int NUM = 0, OP = 1, OPEN = 2, CLOSE = 3, DOT = 4;
        int prev = -1; // Tracks previous token type
        int countParentheses = 0;

        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            if (ch == '(') {
                if (prev == NUM || prev == CLOSE || prev == DOT) {
                    return false;
                }
                countParentheses++;
                prev = OPEN;
            } else if (ch == ')') {
                if (prev == OP || prev == OPEN || countParentheses == 0 || prev == DOT) {
                    return false;
                }
                countParentheses--;
                prev = CLOSE;
            } else if (isOperator(ch)) {
                if (prev == OP || prev == OPEN || prev == -1 || prev == DOT) {
                    return false;
                }
                prev = OP;
            } else if (Character.isDigit(ch)) {
                if (prev == CLOSE) {
                    return false;
                }
                prev = NUM;
            } else if (ch == '.') {
                if (prev != NUM) { // Ensure a dot only follows a digit
                    return false;
                }
                prev = DOT;
            } else if (Character.isLetter(ch)) {
                if (prev == NUM || prev == CLOSE || prev == DOT) {
                    return false;
                }
                prev = NUM; // Letters are treated like numbers (variables)
            } else {
                return false; // Invalid character
            }
        }

        // Ensure valid end state
        if (prev == OP || prev == OPEN || prev == DOT || countParentheses != 0) {
            return false;
        }

        return true;
    }
}
