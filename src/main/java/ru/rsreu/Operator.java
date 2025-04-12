package ru.rsreu;

public enum Operator {
    PLUS('+', "плюс"),
    MINUS('-', "минус"),
    MULTIPLY('*', "умножить на"),
    DIVIDE('/', "делить на");

    private final char symbol;
    private final String word;

    Operator(char symbol, String word) {
        this.symbol = symbol;
        this.word = word;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getWord() {
        return word;
    }

    public static Operator fromSymbol(char symbol) {
        for (Operator op : Operator.values()) {
            if (op.getSymbol() == symbol) {
                return op;
            }
        }
        return null;
    }
}
