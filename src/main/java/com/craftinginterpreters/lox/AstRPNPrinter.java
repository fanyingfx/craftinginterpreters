package com.craftinginterpreters.lox;

/**
 * @author fan
 * 7/20/22
 */
public class AstRPNPrinter implements Expr.Visitor<String>{
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return null;
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return null;
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return null;
    }
}
