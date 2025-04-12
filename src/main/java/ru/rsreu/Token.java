package ru.rsreu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Token {
    private TokenType type;
    private final Object value;
    private final int id;

    public Token(TokenType type, Object value, int id) {
        this.type = type;
        this.value = value;
        this.id = id;
    }

    public TokenType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (type == TokenType.ID) {
            return "<id," + value + ">";
        }
        return "<" + value + ">";
    }

    // Метод для сериализации в бинарный формат
    public void serialize(DataOutputStream writer) throws IOException {
        System.out.println("TOKwr");
        writer.writeInt(type.ordinal());       // Записываем порядковый номер enum
        System.out.println("TOKwrVAl");
        if (type == TokenType.INT || type == TokenType.FLOAT) {
            writeValue(writer, String.valueOf(value));
        }
        //writeValue(writer, id);            // Записываем значение
        System.out.println("TOKid");
        writer.writeInt(id);        // Записываем identifierID
    }

    // Метод для десериализации из бинарного формата
    public static Token deserialize(DataInputStream reader, SymbolTable symbolTable) throws IOException {
        int typeRead = reader.readInt();
        System.out.println(Arrays.toString(TokenType.values()));

        TokenType type = TokenType.values()[typeRead]; // Читаем тип
        if (type == TokenType.INT || type == TokenType.FLOAT) {
            Object value = readValue(reader);                                    // Читаем значение
            int identifierID = reader.readInt();                                // Читаем identifierID
            return new Token(type, value, identifierID);
        } else {
            int identifierID = reader.readInt();
            System.out.println(identifierID);
            //Object value = symbolTable.getSymbol(identifierID).getValue();
            Object value = identifierID;
            return new Token(type, value, identifierID);
        }
        //Object value = readValue(reader);                                    // Читаем значение
        //int identifierID = reader.readInt();                                // Читаем identifierID
        //return new Token(type, null, identifierID);
    }

    // Метод для записи значения (динамического типа)
    private static void writeValue(DataOutputStream writer, Object value) throws IOException {
        if (value instanceof Integer) {
            writer.writeByte(0); // Признак int
            writer.writeInt((Integer) value);
        } else if (value instanceof Float) {
            writer.writeByte(1); // Признак float
            writer.writeFloat((Float) value);
        } else if (value instanceof String) {
            writer.writeByte(2); // Признак string
            writer.writeUTF((String) value);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
        }
    }

    // Метод для чтения значения (динамического типа)
    private static Object readValue(DataInputStream reader) throws IOException {
        byte typeIndicator = reader.readByte(); // Читаем признак типа

        switch (typeIndicator) {
            case 0:
                return reader.readInt(); // Читаем int
            case 1:
                return reader.readFloat(); // Читаем float
            case 2:
                return reader.readUTF(); // Читаем string
            default:
                throw new IllegalArgumentException("Unsupported value type indicator: " + typeIndicator);
        }
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public int getId() {
        return this.id;
    }
}
