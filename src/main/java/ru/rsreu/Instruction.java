package ru.rsreu;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Instruction {
    private GenerationInstructionsEnum manual; // Тип инструкции
    private Token resToken; // Результирующий токен
    private List<Token> tokens; // Список токенов-операндов
    private static SymbolTable symbolTable;

    // Конструктор
    public Instruction(Token manual, Token res, List<Token> tokens, SymbolTable symbolTable) {
        this.manual = GenerationInstructionsFromTokenType.toGetInstruction(manual); // Преобразование типа
        this.resToken = res;
        this.tokens = tokens;
        this.symbolTable = symbolTable;
    }

    // Геттеры и сеттеры (если необходимы)
    public GenerationInstructionsEnum getManual() {
        return manual;
    }

    public void setManual(GenerationInstructionsEnum manual) {
        this.manual = manual;
    }

    public Token getResToken() {
        return resToken;
    }

    public void setResToken(Token resToken) {
        this.resToken = resToken;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

    private String getSymbolId(Token token) {
        //System.out.println("provGEt " + token.toString());
        //System.out.println("type =  " + token.getType());
        if (/*token.getType() == TokenType.ID*/ token.toString().contains("#T")) {
            //System.out.println("getsid "+ token.getValue().toString());
            Symbol symbol = (Symbol) symbolTable.getSymbol(token.getId());
            for (Map.Entry<Integer, Symbol> entry : symbolTable.getSymbols().entrySet()) {
                if (entry.getValue().equals(symbol)) {
                    return String.format("<id,%d>", entry.getKey());
                }
            }
        }
        return token.toString(); // Для констант возвращаем оригинальное значение
    }

    // Метод сериализации
    public void serialize(DataOutputStream writer) throws IOException {
        System.out.println("wr");
        writer.writeInt(manual.ordinal()); // Записываем числовое представление enum
        System.out.println("rt");
        resToken.serialize(writer);       // Сериализация resToken
        System.out.println("wrt");
        writer.writeInt(tokens.size());   // Записываем размер списка tokens
        for (Token token : tokens) {
            System.out.println("tser");
            token.serialize(writer);      // Сериализация каждого токена
            System.out.println("tAftser");
        }
    }

    // Метод десериализации
    public static Instruction deserialize(DataInputStream reader, SymbolTable symbolTable) throws IOException {
        int typeRead = reader.readInt();
        GenerationInstructionsEnum manual = GenerationInstructionsEnum.values()[typeRead];
        Token resToken = Token.deserialize(reader, symbolTable); // Десериализация resToken

        int tokenCount = reader.readInt();          // Чтение количества токенов
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < tokenCount; i++) {
            tokens.add(Token.deserialize(reader, symbolTable));  // Десериализация каждого токена
        }

        return new Instruction(
                GenerationInstructionsFromTokenType.toSetInstruction(manual),
                resToken,
                tokens,
                symbolTable
        );
    }


    @Override
    public String toString() {
        String res = getSymbolId(resToken);
        //String res = resToken.getValue()/*.split(" - ")[0]*/;
        //System.out.println("toS INs" + res);
        //String tok0 = "";
        String tok0 = null;
        if (tokens.get(0).getType() == TokenType.ID){
            tok0 = getSymbolId(tokens.get(0));
        } else {
            tok0 = tokens.get(0).getValue().toString();
        }
        //String tok0 = getSymbolId(tokens.get(0));
        /*if (tokens.get(0).getType() == TokenType.ID) {
            //System.out.println("prov0 "+ tokens.get(0).toString());
            if ((tokens.get(0).toString()).contains("#T")) {
                tok0 = getSymbolId(tokens.get(0));
            } else {
                tok0 = tokens.get(0).toString().split(" - ")[0];
            }
            //tok0 = getSymbolId(tokens.get(0));
        } else {
            tok0 = tokens.get(0).toString().split(" - ")[0];
        }
        /*if ((tokens.get(0).toString()).contains("#T")) {
            tok0 = getSymbolId(tokens.get(0));
        } else {
            tok0 = tokens.get(0).toString().split(" - ")[0];
        }*/
        if (tokens.size() == 1) {
            return String.format("%s %s %s", manual, res, tok0);
        } else if (tokens.size() == 2  && tokens.get(1) != null) {
            String tok1 = getSymbolId(tokens.get(1));
            //String tok1 = "";
            /*if (tokens.get(1).getType() == TokenType.ID) {
                //System.out.println("prov1 "+ tokens.get(1).toString());
                if ((tokens.get(1).toString()).contains("#T")) {
                    tok1 = getSymbolId(tokens.get(1));
                } else {
                    tok1 = tokens.get(1).toString().split(" - ")[0];
                }
            } else {
                tok1 = tokens.get(1).toString().split(" - ")[0];
            }*/
            /*if ((tokens.get(1).toString()).contains("#T")) {
                tok0 = getSymbolId(tokens.get(0));
            } else {
                tok0 = tokens.get(1).toString().split(" - ")[0];
            }*/
            return String.format("%s %s %s %s", manual, res, tok0, tok1);
        }
        return String.format("%s %s %s", manual, res, tok0);
    }
}
