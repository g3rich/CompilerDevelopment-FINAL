package ru.rsreu;

public enum NumberWord {
    ZERO(0, "ноль"),
    ONE(1, "один"),
    TWO(2, "два"),
    THREE(3, "три"),
    FOUR(4, "четыре"),
    FIVE(5, "пять"),
    SIX(6, "шесть"),
    SEVEN(7, "семь"),
    EIGHT(8, "восемь"),
    NINE(9, "девять");

    private final int number;
    private final String word;

    NumberWord(int number, String word) {
        this.number = number;
        this.word = word;
    }

    public int getNumber() {
        return number;
    }

    public String getWord() {
        return word;
    }

    public static String fromNumber(int number) {
        for (NumberWord nw : NumberWord.values()) {
            if (nw.getNumber() == number) {
                return nw.getWord();
            }
        }
        return null;
    }
}
