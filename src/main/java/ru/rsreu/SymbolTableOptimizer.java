package ru.rsreu;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SymbolTableOptimizer {

    public SymbolTableOptimizer() {
    }

    public static SymbolTable optimize(SymbolTable symbolTable, List<Instruction> instructions) {
        // Получаем карту символов
        Map<Integer, Symbol> symbols = symbolTable.getSymbols();

        // Используем итератор для удаления элементов из Map
        Iterator<Map.Entry<Integer, Symbol>> iterator = symbols.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Symbol> entry = iterator.next();
            Symbol symbol = entry.getValue();

            // Проверяем, начинается ли имя символа с "#T"
            if (!symbol.getName().contains("#T")) {
                boolean used = false;

                // Проверяем использование символа в инструкциях
                for (Instruction instruction : instructions) {
                    //System.out.println("instr: " + instruction.getResToken().getValue());
                    //System.out.println("symbol: " + symbol.getName());
                    if (instruction.getResToken().getValue().equals(symbol.getName())) {
                        used = true;
                        //break;
                    }

                    for (Token operand : instruction.getTokens()) {
                        //System.out.println("opetType: " + operand.getType());
                        //System.out.println("ID: " + operand.getId());
                        //System.out.println("operVal: " + symbolTable.getSymbol(operand.getId()).getName());
                        //System.out.println("symbol: " + symbol.getName());
                        if (operand.getType() == TokenType.ID &&
                                symbolTable.getSymbol(operand.getId()).getName().equals(symbol.getName())) {
                            used = true;
                            //break;
                        }
                    }
                }

                // Если символ не используется, удаляем его
                if (!used) {
                    iterator.remove(); // Удаляем через итератор, чтобы избежать ConcurrentModificationException
                }
            }
        }

        return symbolTable;
    }
}
