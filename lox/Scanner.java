package lox;

import static lox.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();    
    private int start   = 0;
    private int current = 0;
    private int line    = 1;

    // define reserved words
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // beginning of next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
        // single chars
        case '(': addToken(LEFT_PAREN);  break;
        case ')': addToken(RIGHT_PAREN); break;
        case '{': addToken(LEFT_BRACE);  break;
        case '}': addToken(RIGHT_BRACE); break;
        case ',': addToken(COMMA);       break;
        case '.': addToken(DOT);         break;
        case '-': addToken(MINUS);       break;
        case '+': addToken(PLUS);        break;
        case ';': addToken(SEMICOLON);   break;
        case '*': addToken(STAR);        break;

        // two chars
        case '!': addToken(match('=') ? BANG_EQUAL : BANG);       break;
        case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL);     break;
        case '<': addToken(match('=') ? LESS_EQUAL : LESS);       break;
        case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

        // a slash might start a comment or might be something else
        case '/':
            if (match('/')) // comment
                while (peek() != '\n' && !isAtEnd()) // goes until end of line
                    advance();
            else
                addToken(SLASH);

        // ignore whitespace
        case ' ':
        case '\r':
        case '\t':
            break;

        case '\n':
            line++;
            break;

        // beginning a string
        case '"':
            string();
            break;

        // "or"
        case 'o':
            if (peek() == 'r')
                addToken(OR);
            break;

        default:
            if (isDigit(c))
                number();
            else if (isAlpha(c))
                identifier();
            else
                Lox.error(line, "Unexpected character: " + c);
            break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        // token a keyword?
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null)
            type = IDENTIFIER; // if not, it's an identifier

        addToken(type);
    }

    // read in a number
    private void number() {
        while (isDigit(peek()))
            advance();

        // look for fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            advance(); // consume the '.'

            while (isDigit(peek())) // after the decimal point
                advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    // read in a string
    private void string() {
        // read in chars
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }

        // not terminated
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        advance(); // the closing "
        
        // trim surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;

        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    // look ahead one
    private char peek() {
        if (isAtEnd())
            return '\0';

        return source.charAt(current);
    }

    // look ahead two
    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') 
            || (c >= 'A' && c <= 'Z') 
            || c == '_';
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }
}