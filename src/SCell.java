// Add your documentation below:

import java.util.ArrayList;

public class SCell implements Cell {
    private String line;
    private int type;
    private int order;
    private String computed;
    private ArrayList<String> dependencies = new ArrayList<>();

    public SCell(String s) {
        setData(s);
    }

    public boolean isForm(String s) {
        if(s.isEmpty()){
            return false;
        }

        if(s.charAt(0) == '='){
            if(s.length() == 1){
                type = Ex2Utils.ERR_FORM_FORMAT;
                return false;
            }
            s = s.replaceAll(" ", "");
            boolean res = isValidForm(s.substring(1));
            if(!res){
                type = Ex2Utils.ERR_FORM_FORMAT;
            }
            return res;
        }
        return false;
    }



    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }


    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public int getOrder() {
        return order;
    }

    //@Override
    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        line = s;
        if(isNumber(s)){
            type = Ex2Utils.NUMBER;
            double d = Double.parseDouble(s);
            line = "" + d;
        }
        else if(isForm(s)){
            type = Ex2Utils.FORM;
            findDependencies();
        }
        else {
            if (type != Ex2Utils.ERR_FORM_FORMAT) {
                type = Ex2Utils.TEXT;
            }
        }
    }
    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        order = t;
    }

    public String getComputed() {
        return computed;
    }

    public void setComputed(String t) {
        computed = t;
    }

    public void findDependencies(){
        if(line == null){
            return;
        }
        String s = line.substring(1);
        s = s.replaceAll(" ", "");
        String[] words = s.split("[+\\-*/() ]");  // split by operators
        for (String word : words) {
            if (!word.isEmpty() && word.substring(0, 1).matches("[A-Za-z]")){ // for reference to another cell like A1
                if(word.length() <= 3) {
                    Index2D c = new CellEntry(word);
                    if (c.isValid()) {
                        dependencies.add(word);
                    }
                }
            }
        }
    }

    public ArrayList<String> getDependencies() {
        return dependencies;
    }

    public static boolean isValidForm(String s) {
        if (s.isEmpty()) {
            return false;
        }

        // Ensure the string contains only valid characters
        boolean onlyAlphaNumeric = s.matches("^[a-zA-Z0-9+\\-*/().]+$");
        if (!onlyAlphaNumeric) {
            return false;
        }

        final int NUM = 0, OP = 1, OPEN = 2, CLOSE = 3, DOT = 4;
        int prev = -1;
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
