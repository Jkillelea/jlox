/* autogenerated by com.craftinginterpreters.tool.GenerateAst
 * Defines a base class, type classes, and a visitor
 * interface for an abstract syntax tree
 */
package com.craftinginterpreters.lox;

import java.util.List;

abstract class Stmt {
    interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
    }

    abstract <R> R accept(Visitor<R> vistor);

    static class Block extends Stmt {
        final List<Stmt> statements;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        Block(List<Stmt> statements) {
            this.statements = statements;
        }
    }

    static class Expression extends Stmt {
        final Expr expression;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        Expression(Expr expression) {
            this.expression = expression;
        }
    }

    static class Print extends Stmt {
        final Expr expression;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        Print(Expr expression) {
            this.expression = expression;
        }
    }

    static class Var extends Stmt {
        final Token name;
        final Expr initializer;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }
    }
}
