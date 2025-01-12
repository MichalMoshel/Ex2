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
        this.x = x; // Assign the column index
        this.y = y; // Assign the row index
    }

    /**
     * Constructor to create a CellEntry from a string index (e.g., "A0", "B1").
     *
     * @param index The string representation of the cell, e.g., "A0", "B1".
     *              The format should be one uppercase letter followed by a number.
     */
    public CellEntry(String index) {
        if (index != null && index.length() >= 2) {
            // Ensure the input is not null and has at least two characters
            this.x = index.toUpperCase().charAt(0) - 'A';
            // Convert the first character (column letter) to a 0-based index
            try {
                this.y = Integer.parseInt(index.substring(1));
                // Extract the numeric part (row index) and parse it
            } catch (NumberFormatException e) {
                this.x = -1; // Invalid row number, set as invalid indices
                this.y = -1;
            }
        } else {
            this.x = -1; // Invalid format, set as invalid indices
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
        return x >= 0 && x < 26 && y >= 0 && y < 100;
        // Check that the column index is between A (0) and Z (25)
        // and the row index is within 0 to 99 (adjust range as necessary)
    }

    /**
     * Gets the column index (0-based).
     *
     * @return The column index.
     */
    @Override
    public int getX() {
        return x; // Return the column index
    }

    /**
     * Gets the row index (0-based).
     *
     * @return The row index.
     */
    @Override
    public int getY() {
        return y; // Return the row index
    }

    /**
     * Converts the CellEntry to a string representation, e.g., "A0", "B1".
     *
     * @return The string representation of the cell.
     */
    @Override
    public String toString() {
        char col = (char) ('A' + x);
        // Convert the column index back to its corresponding letter
        return col + String.valueOf(y);
        // Combine the column letter with the row index as a string
    }
}
