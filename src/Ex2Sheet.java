import java.io.*;

/**
 * Represents a spreadsheet implementation with cells that can contain text, numbers, or formulas.
 */
public class Ex2Sheet implements Sheet {
    private Cell[][] table; // 2D array to store the cells of the spreadsheet

    /**
     * Constructor to initialize a spreadsheet with specified dimensions.
     *
     * @param x Number of rows.
     * @param y Number of columns.
     */
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y]; // Initialize a 2D array of SCell
        for (int i = 0; i < x; i++) { // Iterate through rows
            for (int j = 0; j < y; j++) { // Iterate through columns
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL); // Initialize all cells as empty
            }
        }
        eval(); // Evaluate all cells initially
    }

    /**
     * Default constructor that initializes the spreadsheet with predefined dimensions.
     */
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT); // Use constants for default dimensions
    }

    /**
     * Returns the value of the cell at the specified coordinates.
     *
     * Handles various cell types (number, text, formula, error).
     *
     * @param x Row index.
     * @param y Column index.
     * @return Value of the cell as a string.
     */
    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL; // Default value for empty cells
        SCell c = (SCell) get(x, y); // Get the cell at (x, y)

        // Determine the type of the cell and return its appropriate value
        ans = switch (c.getType()) {
            case Ex2Utils.NUMBER, Ex2Utils.TEXT -> c.toString(); // Number or text is returned as-is
            case Ex2Utils.FORM -> c.getComputed(); // Return computed value for formulas
            case Ex2Utils.ERR_FORM_FORMAT -> "ERR_FORM"; // Error for invalid formula format
            case Ex2Utils.ERR_CYCLE_FORM -> "ERR_CYCLE"; // Error for cyclic dependencies
            default -> ans; // Default empty cell value
        };
        return ans;
    }

    @Override
    public String eval(int x, int y) {
        return "";
    }

    /**
     * Returns the cell at the specified coordinates.
     *
     * @param x Row index.
     * @param y Column index.
     * @return The cell object.
     */
    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    /**
     * Returns the cell based on a string coordinate (e.g., "A1").
     *
     * @param cords String representation of cell coordinates.
     * @return The cell object, or null if invalid.
     */
    @Override
    public Cell get(String cords) {
        Cell ans = null;
        Index2D c = new CellEntry(cords); // Parse the coordinates into indices
        if (c.isValid() && isIn(c.getX(), c.getY())) { // Validate the indices
            ans = get(c.getX(), c.getY());
        }
        return ans;
    }

    /**
     * Returns the width (number of rows) of the spreadsheet.
     *
     * @return Number of rows in the spreadsheet.
     */
    @Override
    public int width() {
        return table.length;
    }

    /**
     * Returns the height (number of columns) of the spreadsheet.
     *
     * @return Number of columns in the spreadsheet.
     */
    @Override
    public int height() {
        return table[0].length;
    }

    /**
     * Sets the content of the cell at the specified coordinates.
     *
     * @param x Row index.
     * @param y Column index.
     * @param s Content to set in the cell.
     */
    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s); // Create a new cell with the given content
        table[x][y] = c; // Replace the cell in the table
    }

    /**
     * Evaluates all the cells in the spreadsheet.
     *
     * This ensures formulas are computed and dependencies are resolved.
     */
    @Override
    public void eval() {
        int[][] dd = depth(); // Get the depth of each cell
        int depth = 0; // Current depth being processed
        int count = 0; // Number of cells evaluated
        int max = width() * height(); // Total number of cells
        boolean changed = true; // Track if any cells were updated

        // Process cells layer by layer based on their depth
        while (count < max && changed) {
            changed = false;
            for (int x = 0; x < width(); x++) { // Iterate through rows
                for (int y = 0; y < height(); y++) { // Iterate through columns
                    SCell c = (SCell) get(x, y);
                    if (dd[x][y] == -1) { // Cyclic dependency
                        c.setType(Ex2Utils.ERR_CYCLE_FORM);
                        changed = true;
                        count++;
                    } else if (dd[x][y] == depth) { // Compute cells at the current depth
                        String res = eval(x, y); // Evaluate the cell
                        c.setComputed(res); // Set the computed value
                        changed = true;
                        count++;
                    }
                }
            }
            depth++;
        }
    }

    /**
     * Checks if the specified coordinates are within bounds.
     *
     * @param xx Row index.
     * @param yy Column index.
     * @return True if within bounds, false otherwise.
     */
    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && yy >= 0 && xx < width() && yy < height();
    }

    /**
     * Calculates the depth of each cell for evaluation.
     *
     * Depth is determined by dependencies, ensuring no cell is evaluated before its dependencies.
     *
     * @return A 2D matrix representing the depth of each cell.
     */
    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()]; // Matrix to store the depth of each cell

        // Initialize depths based on cell types
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell c = get(x, y);
                if (c.getType() == Ex2Utils.NUMBER || c.getType() == Ex2Utils.TEXT) {
                    table[x][y].setOrder(0); // Numbers and text have depth 0
                    ans[x][y] = 0;
                } else {
                    if (c.getType() == Ex2Utils.ERR_FORM_FORMAT) {
                        table[x][y].setData(table[x][y].toString()); // Reset invalid formulas
                    }
                    ans[x][y] = -1; // Mark other cells as uncomputed
                    table[x][y].setOrder(-1);
                }
            }
        }

        // Compute depths iteratively
        int count = 0;
        int depth = 0;
        int max = width() * height();
        boolean changed = true;

        while (count < max && changed) {
            changed = false;
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    if (ans[x][y] != -1) { // Skip already computed cells
                        continue;
                    }

                    Cell c = get(x, y);
                    boolean canCompute = true;
                    for (String str : ((SCell) c).getDependencies()) { // Check dependencies
                        Index2D cord = new CellEntry(str);
                        if (!isIn(cord.getX(), cord.getY())) {
                            c.setType(Ex2Utils.ERR_FORM_FORMAT); // Mark invalid references
                            c.setOrder(0);
                            ans[x][y] = 0;
                            continue;
                        } else if (ans[cord.getX()][cord.getY()] == -1) {
                            canCompute = false; // Dependencies not ready
                        }
                    }

                    if (canCompute) {
                        c.setOrder(depth); // Assign depth if dependencies are computed
                        ans[x][y] = depth;
                        count++;
                        changed = true;
                    }
                }
            }
            depth++;
        }
        return ans;
    }

    @Override
    public void save(String fileName) throws IOException {

    }

    @Override
    public void load(String fileName) throws IOException {

    }
}
