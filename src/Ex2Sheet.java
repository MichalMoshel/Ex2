import java.io.*;
// Add your documentation below:

public class Ex2Sheet implements Sheet {
    private Cell[][] table;


    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i = i + 1) {
            for (int j = 0; j < y; j = j + 1) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;

        SCell c = (SCell) get(x, y);
        ans = switch (c.getType()) {
            case Ex2Utils.NUMBER, Ex2Utils.TEXT -> c.toString();
            case Ex2Utils.FORM -> c.getComputed();
            case Ex2Utils.ERR_FORM_FORMAT -> "ERR_FORM";
            case Ex2Utils.ERR_CYCLE_FORM -> "ERR_CYCLE";
            default -> ans;
        };
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public Cell get(String cords) {
        Cell ans = null;
        Index2D c = new CellEntry(cords);
        if (c.isValid() && isIn(c.getX(), c.getY())) {
            ans = get(c.getX(), c.getY());
        }
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        table[x][y] = c;
    }

    @Override
    public void eval() {
        int[][] dd = depth(); // get the depth of each cell

        int depth = 0; // the current depth
        int count = 0; // the number of cells that have been computed
        int max = width() * height(); // the maximum number of cells
        boolean changed = true; // if there was a change in the last iteration
        while (count < max && changed) {
            changed = false;
            for (int x = 0; x < width(); x = x + 1) {
                for (int y = 0; y < height(); y = y + 1) {
                    SCell c = (SCell) get(x, y);
                    if(dd[x][y] == -1){ // means that the cell is cyclic
                        c.setType(Ex2Utils.ERR_CYCLE_FORM);
                        changed = true;
                        count = count + 1;
                    }else
                    if (dd[x][y] == depth) { // if the cell is at the current depth, meaning that it can be computed
                        String res = eval(x, y);
                        c.setComputed(res);
                        changed = true;
                        count = count + 1;
                    }
                }
            }
            depth = depth + 1;
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && yy >= 0 && xx < width() && yy < height();
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()]; // matrix to store the depth of each cell
        for (int x = 0; x < width(); x = x + 1) {
            for (int y = 0; y < height(); y = y + 1) {
                Cell c = get(x, y);
                if (c.getType() == Ex2Utils.NUMBER || c.getType() == Ex2Utils.TEXT) { //the depth of a number or text cell is 0
                    table[x][y].setOrder(0);
                    ans[x][y] = 0;
                } else {
                    if (c.getType() == Ex2Utils.ERR_FORM_FORMAT) { // if the cell is an error form, reset it
                        table[x][y].setData(table[x][y].toString());
                    }
                    ans[x][y] = -1;
                    table[x][y].setOrder(-1);
                }
            }
        }

        int count = 0; // The number of cells that have been computed
        int depth = 0; // The current depth
        int max = width() * height(); // The maximum number of cells
        boolean changed = true; // If there was a change in the last iteration

        while (count < max && changed){
            changed = false;
            for (int x = 0; x < width(); x = x + 1) {
                for (int y = 0; y < height(); y = y + 1) {
                    if(ans[x][y] != -1){ // If the cell has already been computed,
                        continue;
                    }
                    // Check if the cell can be computed
                    Cell c = get(x, y);
                    boolean bool = true;
                    for (String str : ((SCell) c).getDependencies()) { // Check if all the dependencies have been computed
                        Index2D cord = new CellEntry(str);
                        if(!isIn(cord.getX(), cord.getY())){ // If the cell is out of bounds
                            c.setType(Ex2Utils.ERR_FORM_FORMAT);
                            c.setOrder(0);
                            ans[x][y] = 0;
                            continue;
                        } else
                        if (ans[cord.getX()][cord.getY()] == -1) { // If the dependency has not been computed, this cell cannot be computed
                            bool = false;
                        }
                    }
                    if (bool) { // If all the dependencies have been computed
                        c.setOrder(depth);
                        ans[x][y] = depth;
                        count = count + 1;
                        changed = true;
                    }
                }
            }
            depth = depth + 1;
        }
        return ans;
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            // Clear the existing table
            for (int i = 0; i < table.length; i++) {
                for (int j = 0; j < table[i].length; j++) {
                    table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
                }
            }

            // Read the header line and ignore it
            reader.readLine();

            // Read the remaining lines
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by commas
                String[] parts = line.split(",", 3);

                // Validate the format: must have at least 3 parts
                if (parts.length < 3) continue;

                try {
                    int x = Integer.parseInt(parts[0].trim());
                    int y = Integer.parseInt(parts[1].trim());
                    String content = parts[2].trim();

                    // Ensure indices are within bounds
                    if (x >= 0 && x < table.length && y >= 0 && y < table[0].length) {
                        set(x, y, content);
                    }
                } catch (NumberFormatException e) {
                    // Ignore lines with invalid number formats
                }
            }
        }

        // Re-evaluate the sheet after loading
        eval();
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the header line
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment" +
                    "\n");

            // Iterate over all cells and write non-empty cells
            for (int i = 0; i < table.length; i++) {
                for (int j = 0; j < table[i].length; j++) {
                    String content = table[i][j].toString();
                    if (!content.equals(Ex2Utils.EMPTY_CELL)) {
                        writer.write(String.format("%d,%d,%s\n", i, j, content));
                    }
                }
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        String ans = null;
        if (get(x, y) != null) {
            ans = get(x, y).toString();
        }

        if(get(x, y).getType() == Ex2Utils.NUMBER || get(x, y).getType() == Ex2Utils.TEXT){
            return ans;
        }

        else{
            assert ans != null;
            ans = ans.replaceAll(" ", "");
            double res = evalForm(ans);
            if(Double.isNaN(res)){
                get(x, y).setType(Ex2Utils.ERR_FORM_FORMAT);
                return "ERR_FORM";
            }
            return "" + res;
        }
    }

    private double evalForm(String ans) {
        return evalFormRec(ans.substring(1));
    }


    private double evalFormRec(String substring) {
        if (isNumber(substring)) {
            return Double.parseDouble(substring);
        }

        if(substring.charAt(0) == '(' && substring.charAt(substring.length()-1) == ')'){
            return evalFormRec(substring.substring(1, substring.length()-1));
        }

        // If it is a cell
        if(substring.length() >=2 && substring.length() <=3 && Character.isLetter(substring.charAt(0))){
            Index2D c = new CellEntry(substring);
            int x = c.getX();
            int y = c.getY();
            if(isIn(x, y)){
                Cell cell = get(x, y);
                if(cell.getType() == Ex2Utils.NUMBER){
                    return Double.parseDouble(cell.toString());
                }
                if(cell.getType() == Ex2Utils.TEXT || cell.getType() == Ex2Utils.ERR_FORM_FORMAT){
                    return Double.NaN;
                }
                else {
                    return evalForm(cell.toString());
                }
            }
        }

        // Recursively evaluate the expression
        int mainOperatorIndex = findMainOperator(substring);
        if (mainOperatorIndex == -1) {
            return Double.NaN;
        }
        char operator = substring.charAt(mainOperatorIndex);
        String left = substring.substring(0, mainOperatorIndex);
        String right = substring.substring(mainOperatorIndex + 1);

        return switch (operator) {
            case '+' -> evalFormRec(left) + evalFormRec(right);
            case '-' -> evalFormRec(left) - evalFormRec(right);
            case '*' -> evalFormRec(left) * evalFormRec(right);
            case '/' -> evalFormRec(left) / evalFormRec(right);
            default -> Double.NaN;
        };
    }

    private static int findMainOperator(String substring) {
        int index = -1;
        int parenthesesDepth = 0;
        double lowestPrecedence = Double.MAX_VALUE;

        for (int i = 0; i < substring.length(); i++) {
            char ch = substring.charAt(i);

            if (ch == '(') {
                parenthesesDepth++;
            } else if (ch == ')') {
                parenthesesDepth--;
            } else if (isOperator(ch)) {
                double precedence = getPrecedence(ch) + parenthesesDepth;
                if (precedence <= lowestPrecedence) {
                    lowestPrecedence = precedence;
                    index = i;
                }
            }
        }
        return index;
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private static double getPrecedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 0.25;
            case '*', '/' -> 0.5;
            default -> Double.MAX_VALUE;
        };
    }

    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}