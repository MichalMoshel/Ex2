/**
 * Represents a cell entry in a table with indices like A0, A1, B0, etc.
 * Implements the Index2D interface for compatibility.
 */
public class CellEntry implements Index2D {
    private int x; // Column index (e.g., 0 for 'A', 1 for 'B')
    private int y; // Row index (e.g., 0 for '0', 1 for '1')

    /**
     * Constructor to create a CellEntry using x (column) and y (row) indices.
     *
     * @param x Column index (0-based, e.g., 0 for 'A', 1 for 'B').
     * @param y Row index (0-based, e.g., 0 for '0', 1 for '1').
     */
    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor to create a CellEntry from a string index (e.g., "A0", "B1").
     *
     * @param index The string representation of the cell, e.g., "A0", "B1".
     *              The format should be one uppercase letter followed by a number.
     */
    public CellEntry(String index) {
        if (index != null && index.length() >= 2) {
            this.x = index.toUpperCase().charAt(0) - 'A'; // Convert column letter to index
            try {
                this.y = Integer.parseInt(index.substring(1)); // Convert row part to integer
            } catch (NumberFormatException e) {
                this.x = -1; // Invalid row number
                this.y = -1;
            }
        } else {
            this.x = -1; // Invalid format
            this.y = -1;
        }
    }

    /**
     * Checks if the cell indices are valid.
     *
     * @return True if the cell is within the valid table range, false otherwise.
     */
    @Override
    public boolean isValid() {
        return x >= 0 && x < 26 && y >= 0 && y < 100; // Adjust range as needed
    }

    /**
     * Gets the column index (0-based).
     *
     * @return The column index.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Gets the row index (0-based).
     *
     * @return The row index.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Converts the CellEntry to a string representation, e.g., "A0", "B1".
     *
     * @return The string representation of the cell.
     */
    @Override
    public String toString() {
        char col = (char) ('A' + x); // Convert column index to letter
        return col + String.valueOf(y); // Row index is already 0-based
    }
}
