package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

/**
 * @author fan
 * 7/21/22
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

//    Expr parse() {
//        try {
//            return expression();
//        } catch (ParserError error) {
//            return null;
//        }
//    }
    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
        
    }

    private Stmt declaration() {
        try{
            if(match(VAR)){
                return varDeclaration();
            }
            return statement();
        }catch (ParserError error){
            synchronize();
            return null;
        }
    }
    private Stmt varDeclaration(){
        Token name = consume(IDENTIFIER, "Expect variable name.");
        Expr initializer = null;
        if(match(EQUAL)){
            initializer=expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() {
        if(match(PRINT)) return printStatement();
        if(match(LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE)&&!isAtEnd()) {
            statements.add(declaration());

        }
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Except ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();
        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }


//    private Expr comma(){
//        Expr expr = ternary();
//        while (match(COMMA)){
//            Token operator = previous();
//            Expr right = ternary();
//            expr = new Expr.Binary(expr, operator, right);
//        }
//        return  expr;
//
//    }

//    private Expr ternary() {
//        Expr expr =equality();
//        while (match(QUESTION)) {
//            Token operator1 = previous();
//            Expr middle =ternary();
//            if(match(COLON)){
//                Token operator2 = previous();
//                Expr end =ternary();
//                expr= new Expr.Binary(expr,operator1,new Expr.Binary(middle,operator2,end));
//            }else
//                throw error(peek(), "Missing right colon expression.");
//        }
//        return expr;
//    }

    private Expr equality() {
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right =comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() {
        Expr expr = term();
        while (match(GREATER, GREATER, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;

    }

    private Expr term() {
        Expr expr = factor();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * factory -> unary ( ("/" | "*") unary )*;
     *
     * @return
     */
    private Expr factor() {
        Expr expr = unary();
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }



    private Expr unary() {
        if (match(BANG,MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        if (match(NUMBER, STRING)) return new Expr.Literal(previous().literal);
        if (match(IDENTIFIER)) return new Expr.Variable(previous());

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        } else if (match(COMMA, QUESTION, BANG,
                BANG_EQUAL, EQUAL, EQUAL_EQUAL,
                GREATER, GREATER_EQUAL, LESS, LESS_EQUAL,
                PLUS,MINUS,STAR,SLASH
                )) {
            throw error(previous(), "Missing the left hand operand.");
        }

        throw error(peek(), "Expression.");


    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }

        }


        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }
        return peek().type == type;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {

        return peek().type == EOF;

    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }


    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    /**
     * Logging the error and return the ParserError, to let the calling method decide
     * whether to unwind or not.
     *
     * @param token
     * @param message
     * @return
     */
    private ParserError error(Token token, String message) {
        Lox.error(token, message);
        return new ParserError();

    }


    private static class ParserError extends RuntimeException {
    }

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;

            }
            advance();
        }
    }
}
