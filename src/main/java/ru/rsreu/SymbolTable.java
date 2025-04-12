package ru.rsreu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<Integer, Symbol> symbols = new HashMap<>();
    //private final Map<Integer, String> types = new HashMap<>(); // Новый Map для хранения типов
    //private int nextId = 1;

    public int addSymbol(String name, String type/*, Object value*/) {
        /*for (var entry : symbols.entrySet()) {
            if (entry.getValue().equals(name)) {
                int id = entry.getKey();
                String existingType = types.get(id);

                // Проверяем, не изменился ли тип переменной
                if (!existingType.equals(type)) {
                    throw new IllegalArgumentException("Лексическая ошибка! Переменной '" + name + "' присваивается разный тип: " +
                            existingType + " и " + type);
                }
                return id; // Возвращаем существующий ID
            }

        }*/
        //System.out.println("ya zdes4 " + type);
        if (containsSymbolByName(name, type)) {
            int id = getSymbolByName(name);
            return id;
        } /*else {
            int id = getSymbolByName(name);
            return id;
        }*/
        //System.out.println("ya zdes7 " + type);
        Symbol symbol = new Symbol(name, getType(type)/*, value*/);
        int id = symbols.size() + 1;
        symbols.put(id, symbol);
        return id;
    }

    /**
     * Метод проверки существования символа по наименованию
     *
     * @param name наименование переменной.
     * @return true, если переменная с подобным названием есть в таблице, false в обратном случае.
     */
    public boolean containsSymbolByName(String name, String type) {
        //System.out.println("ya zdes5 " + type);
        for (var symbol : symbols.entrySet()) {
            //System.out.println("ya zdes6 " + type);
            if (symbol.getValue().getName().equals(name)){
                int id = symbol.getKey();
                //System.out.println(symbol.getValue().getType().toString());
                //System.out.println(type);
                if (!symbol.getValue().getType().toString().equals(type)) {
                    throw new IllegalArgumentException("Лексическая ошибка! Переменной '" + name + "' присваивается разный тип: " +
                            symbol.getValue().getType().toString() + " и " + type);
                }
                //Symbol symbol = symbols.get(id);
                /*if (symbol.getValue().getType() == VariableType.INT){
                    if (type == "вещественный") {
                        throw new IllegalArgumentException("Лексическая ошибка! Переменной '" + name + "' присваивается разный тип: " +
                                symbol.getValue().toString() + " и " + type);
                    }
                } else {
                    if (type == "целый") {
                        throw new IllegalArgumentException("Лексическая ошибка! Переменной '" + name + "' присваивается разный тип: " +
                                symbol.getValue().toString() + " и " + type);
                    }
                }*/
                return true;
            }
        }
        return false;
    }

    /**
     * Метод получения переменной по наименованию.
     *
     * @param name наименование переменой.
     * @return символ таблицы символов.
     */
    public Integer getSymbolByName(String name) {
        for (var symbol : symbols.entrySet()) {
            if (symbol.getValue().getName().equals(name)) {
                return symbol.getKey();
            }
        }
        return null;
    }

    // Получение типа символа по id
    /*public String getType(int id) {
        return types.get(id);
    }*/
    /**
     * Метод для получения типа переменной из строкового представления.
     *
     * @param typeString строковое представление типа.
     * @return тип переменной.
     */
    public VariableType getType(String typeString) {
        return switch (typeString) {
            case "целый", "" -> VariableType.INT;
            case "вещественный" -> VariableType.FLOAT;
            default -> null;
        };
    }

    public Symbol getSymbol(int id) {return symbols.get(id);}

    public Map<Integer, Symbol> getSymbols() {
        return symbols;
    }


    // Сериализация
    public void serialize(DataOutputStream writer) throws IOException {
        writer.writeInt(symbols.size());
        for (Map.Entry<Integer, Symbol> entry : symbols.entrySet()) {
            writer.writeInt(entry.getKey());
            entry.getValue().serialize(writer);
        }
    }

    // Десериализация
    public static SymbolTable deserialize(DataInputStream reader) throws IOException {
        SymbolTable symbolTable = new SymbolTable();
        int count = reader.readInt();

        for (int i = 0; i < count; i++) {
            int key = reader.readInt();
            Symbol item = Symbol.deserialize(reader);
            symbolTable.symbols.put(key, item);
        }

        return symbolTable;
    }
    /*public Map<Integer, String> getSymbols() {
        return symbols;
    }*/

    //public String getType(int id) {return types.get(id);}

    /*public Map<Integer, String> getTypes() {
        return types;
    }*/
}

