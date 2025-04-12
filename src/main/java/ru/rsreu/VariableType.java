package ru.rsreu;

/**
 * Перечисление для типов переменных.
 */
public enum VariableType {
    /**
     * Вещественный тип переменной.
     */
    FLOAT,
    /**
     * Целочисленный тип переменной.
     */
    INT;

    /**
     * Метод для получения строкового представления типа переменной.
     *
     * @return строковое представление типа переменной.
     */
    @Override
    public String toString() {
        return switch (this) {
            case INT -> "целый";
            case FLOAT -> "вещественный";
            default -> "неизвестный тип";
        };
    }
}
