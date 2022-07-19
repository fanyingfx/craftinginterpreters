package com.craftinginterpreters.lox;


import java.util.*;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * Uses to scan the text and translating to tokens.
 */
public class Scanner {
    /**
     * Every token start position, update with {@code current} after add previous token.
     */
    private int start = 0;
    /**
     * Current character position.
     */
    private int current = 0;
    /**
     * Current number, used for debugging.
     */
    private int line = 1;
    /**
     * The source text string.
     */
    private final String source;
    /**
     * Saves parsed token from source.
     */
    private final List<Token> tokens;

    public Scanner(String source) {
        this.source = source;
        tokens=new ArrayList<>();

    }

    /**
     * Scan source from begin to end and add tokens
     * @return The token list
     */
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    /**
     * From left to right scanning by character and add the character(s) between *start* to token list.
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
                // Equal Operators
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
        // Ignore Comment

            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
               // Ignore whitespace

            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"':string();break;
            default:
                if (isDigit(c)) {
                   number(); 
                }else{
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            while (isDigit(peek()))advance();
        }
        addToken(NUMBER,Double.parseDouble(source.substring(start,current)));
    }

    private char peekNext() {
        //TODO 验证这段逻辑是否等价于 isAtEnd()
//        if(current+1>=source.length()) return '\0';
        if(isAtEnd()) return '\0';
        return source.charAt(current + 1);

    }

    static  private boolean isDigit(char c) {
        return c >= '0' && c<='9';
    }

    /**
     * Adds the string literal, support the multiple-line strings
     */
    private void string() {
        while (peek()!='"' && !isAtEnd()){
            if (peek() == '\n') {
                line += 1;
            }
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line,"Unterminated string.");
            return;
        }
        // The closing ".
        advance();
        // now the start is at the open double-quote and current at end double-quote, string literal don't need the quote.
        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING,value);

    }

    /**
     * If not the end return current character.
     * @return Current Character, if is end return '\0'
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Checks the current character whether matched, then move forward
     * @param expected current character
     * @return Matched return true, In the end or unmatched return false.
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }


    /**
     * For adding the simple tokens
     * @param type TokenType
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * For adding Literal token
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    /**
     * Return current char and move forward.
     */
    private char advance() {
        return source.charAt(current++);
    }


    /**
     * Whether scan to the end.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }
}
