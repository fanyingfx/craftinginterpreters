package com.craftinginterpreters.lox;

/**
 * @author fan
 * 7/20/22
 */
public class AstRPNPrinter implements Expr.Visitor<String>{
    public String print(Expr expr){
        return expr.accept(this);
    }
    public String toRPN(String name,Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        for (Expr expr :
                exprs) {
            builder.append(expr.accept(this)).append(" ");
        }
        builder.append(name);
        return builder.toString();
    }
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {

        return toRPN(expr.operator.lexeme,expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return null;
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr==null) return  "nil";
        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return null;
    }

    public static void main(String[] args) {
        Expr expression =
                new Expr.Binary(
                    new Expr.Binary(
                    new Expr.Literal(1),
                    new Token(TokenType.PLUS,"+",null,1),
                    new Expr.Literal(2)
            ),
                        new Token(TokenType.STAR,"*",null,1),

                        new Expr.Binary(
                                new Expr.Literal(4),
                                new Token(TokenType.MINUS,"-",null,1),
                                new Expr.Literal(3)

                        )
                );
        System.out.println(new AstRPNPrinter().print(expression));

    }
}
