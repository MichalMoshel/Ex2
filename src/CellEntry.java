/**
 * Represents a cell's coordinates within a 2D grid with indices for rows (x) and columns (y).
 * This class also provides utilities for converting cell indices to a string representation and vice versa.
 * The cell index follows the convention of using column letters (A-Z) for the row index (x)
 * and 0-based numbering for the column index (y).
 */
public class CellEntry implements Index2D {
    private int x; // Row index (0-based, 'A' corresponds to 0)
    private int y; // Column index (0-based, where 0 is the first column)

    /**
     * Constructs a CellEntry with the given row and column indices.
     *
     * @param x The row index (0-based, where 0 is the first row).
     * @param y The column index (0-based, where 0 is the first column).
     */
    public CellEntry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Compares this CellEntry to another object for equality.
     * Two CellEntry objects are considered equal if they represent the same cell,
     * which is determined by comparing their string representations.
     *
     * @param o The object to compare.
     * @return True if the objects are equal, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CellEntry)) {
            return false;
        }
        if (o == this) {
            return true;
        }

        return this.toString().equals(o.toString());
    }

    /**
     * Returns a string representation of the CellEntry in the format "A0", where
     * 'A' is the column letter (for the row index) and '0' is the row number
     * (0-based for columns).
     *
     * For example:
     * - (0, 0) will return "A0"
     * - (1, 0) will return "B0"
     *
     * @return The string representation of the cell's coordinates.
     */
    @Override
    public String toString() {
        char column = (char) ('A' + x); // Convert row index to letter (e.g., 0 -> A)
        return column + String.valueOf(y); // Convert column index to 0-based and append
    }

    /**
     * Checks whether the current CellEntry has valid coordinates. A cell is valid if:
     * - The row index (x) is between 0 and 99 (inclusive).
     * - The column index (y) is between 0 and 25 (inclusive, corresponding to A-Z).
     *
     * @return True if the cell coordinates are valid, otherwise false.
     */
    @Override
    public boolean isValid() {
        return isValid(x, y);
    }

    /**
     * Helper method to check whether the given coordinates are valid.
     *
     * @param x The row index (0-based).
     * @param y The column index (0-based).
     * @return True if the coordinates are valid, otherwise false.
     */
    private static boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && y < 26 && x < 100; // Valid range: X in [0-99], Y in [0-25] (A-Z)
    }

    /**
     * Gets the row index (0-based) of this cell.
     *
     * @return The row index.
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * Gets the column index (0-based) of this cell.
     *
     * @return The column index.
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * Parses a string representation of a cell index (e.g., "A0") into a CellEntry object.
     * The format is expected to be a letter (A-Z) followed by a number (0-99).
     *
     * For example:
     * - "A0" will be parsed as row 0, column 0.
     * - "B3" will be parsed as row 1, column 3.
     *
     * @param index The string representation of the index.
     * @return A CellEntry object corresponding to the parsed coordinates, or null if invalid.
     */
    public static CellEntry fromString(String index) {
        if (index == null || index.length() < 2) return null; // Invalid string

        char rowChar = index.charAt(0); // Column letter (e.g., 'A')
        int column;
        try {
            column = Integer.parseInt(index.substring(1)); // Convert to 0-based (e.g., '0' -> 0)
        } catch (NumberFormatException e) {
            return null; // Invalid number format
        }
        int row = Character.toUpperCase(rowChar) - 'A'; // Convert letter to row index (e.g., 'A' -> 0)
        if (!isValid(row, column)) return null; // Validate the coordinates
        return new CellEntry(row, column);
    }
}
