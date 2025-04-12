package ru.rsreu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Класс, представляющий запись в таблице символов
 */
public class Symbol {

    /**
     * Имя переменной.
     */
    private final String name;

    /**
     * Тип переменной.
     */
    private VariableType type;

    private Object value;

    /**
     * Конструктор символа таблицы символов на основе названия.
     *
     * @param name название переменной.
     */
    public Symbol(String name) {
        this.name = name;
        this.type = VariableType.INT;
        this.value = 0;
    }

    /**
     * Конструктор символа таблицы символов на основе названия и типа.
     *
     * @param name название переменной.
     * @param type тип пемеренной.
     */
    public Symbol(String name, VariableType type/*, Object value*/) {
        this.name = name;
        this.type = Objects.requireNonNullElse(type, VariableType.INT);
        //this.value = value;\
        this.value = 0;
    }

    public void setType(VariableType type) {
        this.type = type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    /**
     * Метод доступа к наименованию переменной.
     *
     * @return наименование переменной.
     */
    public String getName() {
        return name;
    }

    /**
     * Метод доступа к типу переменной
     *
     * @return тип переменной.
     */
    public VariableType getType() {
        return type;
    }

    // Сериализация
    public void serialize(DataOutputStream writer) throws IOException {
        writer.writeUTF(name);
        writer.writeInt(type.ordinal()); // Записываем числовое представление enum
        //writeValue(writer, value, type);

        /*if (value == null) {
            System.out.println("Warning: Serializing null value for symbol " + name);
            writer.writeUTF("null");
        } else {
            writeValue(writer, value, type);
        }*/
    }

    // Десериализация
    public static Symbol deserialize(DataInputStream reader) throws IOException {
        String name = reader.readUTF();
        VariableType type = VariableType.values()[reader.readInt()];
        //readValue(reader);
        return new Symbol(name, type);
    }

    private static void writeValue(DataOutputStream writer, Object valuePar, VariableType type) throws IOException {
        if (type == VariableType.INT) {
            writer.writeByte(0); // Признак int
            writer.writeInt((Integer) valuePar);
        } else if (type == VariableType.FLOAT) {
            writer.writeByte(1); // Признак float
            writer.writeFloat((Float) valuePar);
        } else {
            writer.writeByte(2); // Признак string
            writer.writeUTF((String) valuePar);
        }
        /*} else {
            throw new IllegalArgumentException("Unsupported value type: " + valuePar.getClass().getName());
        }*/
    }

    // Метод для чтения значения (динамического типа)
    private static Object readValue(DataInputStream reader) throws IOException {
        byte typeIndicator = reader.readByte(); // Читаем признак типа

        return switch (typeIndicator) {
            case 0 -> reader.readInt(); // Читаем int
            case 1 -> reader.readFloat(); // Читаем float
            case 2 -> reader.readUTF(); // Читаем string
            default -> throw new IllegalArgumentException("Unsupported value type indicator: " + typeIndicator);
        };
    }

    /**
     * Метод для получения строкового представления символа.
     *
     * @return строковое представление символа.
     */
    @Override
    public String toString() {
        return name + " ["+ type.toString() +"]";
    }
}

