package ru.rsreu;

import java.util.Arrays;

public class Validator {
    public Validator(){}
    private String[] bannedSymbols = {"<", ">", ":", "/", "|", "?", "*", "\\", " ", "&", "#", "~"};
    public String validString(String fileName){
        if (fileName.length() > 4){
            if (fileName.endsWith(".TXT")){
                if (Arrays.stream(bannedSymbols).anyMatch(fileName::contains)){
                    return "Ошибка: в названии недопустимы символы: <, >, :, /, \\, |, ?, *, 'пробел', &, #, ~";
                }
            } else {
                return "Ошибка: допустимо только расширение '.txt'";
            }
        } else {
            return "Ошибка: название файла должно состоять хотя бы из 1 символа. В конце обязательно должно быть расширение '.txt'. В названии запрещено использовать: <, >, :, /, \\, |, ?, *, 'пробел'";
        }
        return  "valid";
    }
}
