package com.lox.scanner;

import com.lox.errors.ErrorHandler;
import com.lox.tokenizer.Token;
import com.lox.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    private final String source;

    private int start = 0;
    private int current = 0;
    private int line = 1;

    private final List<Token> tokens = new ArrayList<Token>();

    private static final Map<String, TokenType> keywords = new HashMap<String, TokenType>();

    static {
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("fun", TokenType.FUN);
        keywords.put("for", TokenType.FOR);
        keywords.put("is", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }
    public Scanner(String source) {
        this.source = source;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = nextChar();

        switch (c) {
            case '(':
                addToken(TokenType.LEFT_PARAN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PARAN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            //  Handles cases like !=, ==, >=, <=, etc.
            case '!':
                addToken(isNextChar('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(isNextChar('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '>':
                addToken(isNextChar('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '<':
                addToken(isNextChar('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '/':
                // If the next character is also a '/', then this whole line is a comment.
                if (isNextChar('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        nextChar();
                    }
                } else {
                        addToken(TokenType.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // We ignore the spaces, but also, we break so we can start scanning the next lexeme anew.
                break;
            case '\n':
                // Same as above, but also increment the line number.
                line += 1;
                break;
            // Literals
            case '"':
                processStringLiteral();
                break;
            default:
                if (isDigit(c)) {
                    processNumericLiteral();
                }
                else if (isAlpha(c)) {
                    processIdentifier();
                } else {
                    ErrorHandler.error(line, "Undefined character '" + c + "' found.");
                }
        }
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private void processIdentifier() {
        while (isAlpha(peek()) || isDigit(peek())) {
            nextChar();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        type = type == null ? TokenType.IDENTIFIER : type;
        addToken(type);
    }

    private void processNumericLiteral() {
        while(isDigit(peek())) {
            nextChar();
        }

        if (peek()=='.' && isDigit(peekNext())) {
            nextChar();

            while (isDigit(peek())) {
                nextChar();
            }
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current+1);
    }

    private void processStringLiteral() {
        while (peek()!='"' && !isAtEnd()) {
            if (peek()=='\n') {
                line += 1;
            }
            nextChar();
        }

        if (isAtEnd()) {
            ErrorHandler.error(line, "Unterminated string.");
        }

        // The last '"' character.
        nextChar();

        String value = source.substring(start+1, current-1);

        addToken(TokenType.STRING, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    private char nextChar() {
        return source.charAt(current++);
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private boolean isNextChar(char match) {
        if (isAtEnd()) {
            return false;
        } else if (source.charAt(current) != match) {
            return false;
        } else {
            current += 1;
            return true;
        }
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
