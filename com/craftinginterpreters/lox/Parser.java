package com.craftinginterpreters.lox;

import java.util.List;
import static com.craftinginterpreters.lox.TokenType.*;
import static com.craftinginterpreters.lox.Expr.*;

class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return equality();
    }

    // equality -> comparison (("!=" | "==") comparison)*;
    private Expr equality() {
        Expr expr = comparison(); // first thing to compare

        while (match(BANG_EQUAL, EQUAL_EQUAL)) { // some number of other
            Token operator = previous();         // things to compare
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // comparison -> addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ;
    private Expr comparison() {
        Expr expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // ((a times/div b)*) +/- ((c times/div d)*)
    private Expr addition() {
        Expr expr = multiplication(); // left side

        while (match(MINUS, PLUS)) {
            Token operator = previous(); // plus or minus
            Expr right = multiplication(); // right side
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // (!a or -a) times/div (!b or -b)
    private Expr multiplication() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    // !a or -a
    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous(); // bang or minus
            Expr right = unary(); // value
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    // primary -> NUMBER | STRING | "false" | "true" | "nil" | "(" expression ")" ;
    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
    }

    // check if the current token is one of the types
    private boolean match(TokenType... types) {
        for(TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    // try and consume a closing parenthesis or fail with a message
    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();

        throw error(peek(), message);
    }

    // return true if current token is of given type. Doesn't consume it.
    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    // create a parse error
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }
    
}