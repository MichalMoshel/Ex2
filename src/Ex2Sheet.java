import java.io.*;
import java.util.HashMap;

/**
 * Represents a 2D spreadsheet where cells can store text, numbers, or formulas.
 * Provides methods for cell manipulation, formula evaluation, saving and loading data.
 */
public class Ex2Sheet implements Sheet {
    private Cell[][] table;

    /**
     * Constructs a new Ex2Sheet with the specified dimensions (x by y) and initializes all cells.
     * @param x The number of rows in the sheet.
     * @param y The number of columns in the sheet.
     */
    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    /**
     * Constructs a new Ex2Sheet with default dimensions (width and height defined in Ex2Utils).
     */
    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    /**
     * Returns the value of a cell at the specified coordinates.
     * @param x The row index.
     * @param y The column index.
     * @return The value of the cell, or an empty cell if out of bounds or uninitialized.
     */
    @Override
    public String value(int x, int y) {
        if (!isIn(x, y)) return Ex2Utils.EMPTY_CELL;
        Cell c = get(x, y);
        if (c == null) return Ex2Utils.EMPTY_CELL;

        // Check the type of the cell and handle accordingly
        switch (c.getType()) {
            case Ex2Utils.TEXT:
            case Ex2Utils.NUMBER:
                return c.getData(); // Return the raw data for TEXT and NUMBER types.

            case Ex2Utils.FORM:
                try {
                    // Evaluate the formula using SCell's evaluateFormula
                    SCell sCell = (SCell) c;
                    return sCell.evaluateFormula(this);
                } catch (StackOverflowError | CycleException e) {
                    c.setType(Ex2Utils.ERR_CYCLE_FORM);
                    return Ex2Utils.ERR_CYCLE;
                } catch (IllegalArgumentException e) {
                    c.setType(Ex2Utils.ERR_FORM_FORMAT);
                    return Ex2Utils.ERR_FORM;
                }

            case Ex2Utils.ERR_CYCLE_FORM:
                return Ex2Utils.ERR_CYCLE;

            case Ex2Utils.ERR_FORM_FORMAT:
                return Ex2Utils.ERR_FORM;

            default:
                return Ex2Utils.EMPTY_CELL;
        }
    }

    /**
     * Returns the cell at the specified coordinates.
     * @param x The row index.
     * @param y The column index.
     * @return The cell at the given coordinates, or null if out of bounds.
     */
    @Override
    public Cell get(int x, int y) {
        return isIn(x, y) ? table[x][y] : null;
    }

    /**
     * Returns the cell corresponding to the specified string coordinate.
     * @param cords The coordinate in string format (e.g., "A1").
     * @return The cell at the given coordinate, or null if invalid.
     */
    @Override
    public Cell get(String cords) {
        CellEntry entry = CellEntry.fromString(cords);
        if (entry == null || !entry.isValid()) return null;
        return get(entry.getX(), entry.getY());
    }

    /**
     * Finds the coordinates of a given cell in the sheet.
     * @param cell The cell whose coordinates need to be found.
     * @return The coordinates of the cell as a string, or null if the cell is not found.
     */
    public String findCoordinates(Cell cell) {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell c = get(x, y);
                if (c != null && c.equals(cell)) {
                    return new CellEntry(x, y).toString();
                }
            }
        }
        return null; // Cell not found in the sheet
    }

    /**
     * Returns the number of rows in the sheet.
     * @return The number of rows.
     */
    @Override
    public int width() {
        return table.length;
    }

    /**
     * Returns the number of columns in the sheet.
     * @return The number of columns.
     */
    @Override
    public int height() {
        return table[0].length;
    }

    /**
     * Sets the value of a cell at the specified coordinates.
     * @param x The row index.
     * @param y The column index.
     * @param s The string value to set for the cell.
     */
    @Override
    public void set(int x, int y, String s) {
        if (!isIn(x, y)) return;
        table[x][y] = new SCell(s);
    }

    /**
     * Evaluates all formulas in the sheet, updating cells with results and handling errors.
     */
    @Override
    public void eval() {
        // Resolve all formulas, update orders, and detect errors
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell cell = get(x, y);
                if (cell != null && cell.getType() == Ex2Utils.FORM) {
                    try {
                        cell.setData(value(x, y));
                        cell.setType(Ex2Utils.NUMBER); // Assume evaluation results in a number
                    } catch (StackOverflowError | CycleException e) {
                        cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                    } catch (Exception e)  {
                        cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                    }
                }
            }
        }
    }

    /**
     * Checks if the specified coordinates are within the bounds of the sheet.
     * @param xx The row index.
     * @param yy The column index.
     * @return True if the coordinates are within bounds, false otherwise.
     */
    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && yy >= 0 && xx < width() && yy < height();
    }

    /**
     * Returns a 2D array representing the order of the cells in the sheet.
     * @return A 2D array where each cell contains the order of the corresponding cell.
     */
    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Cell cell = get(x, y);
                if (cell != null) {
                    ans[x][y] = cell.getOrder();
                }
            }
        }
        return ans;
    }

    /**
     * Loads the sheet data from a CSV file.
     * @param fileName The name of the file to load data from.
     * @throws IOException If an error occurs while reading the file.
     */
    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;
            while ((line = br.readLine()) != null && row < height()) {
                String[] cells = line.split(",");
                for (int col = 0; col < cells.length && col < width(); col++) {
                    set(row, col, cells[col]);
                }
                row++;
            }
        }
    }

    /**
     * Saves the sheet data to a CSV file.
     * @param fileName The name of the file to save data to.
     * @throws IOException If an error occurs while writing to the file.
     */
    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (int x = 0; x < width(); x++) {
                for (int y = 0; y < height(); y++) {
                    bw.write(get(x, y).getData());
                    if (y < height() - 1) bw.write(",");
                }
                bw.newLine();
            }
        }
    }

    /**
     * Evaluates the value of the cell at the specified coordinates.
     * @param x The row index.
     * @param y The column index.
     * @return The evaluated value of the cell at the given coordinates.
     */
    @Override
    public String eval(int x, int y) {
        return value(x, y);
    }
}
