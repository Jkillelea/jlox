package com.craftinginterpreters.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    // final static String TAB = "\t";   // actual tab character
    // final static String TAB = "  "; // 2 spaces
    final static String TAB = "    "; // 4 spaces

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: generate_ast <output_dir>");
            System.exit(1);
        }

        String outputDir = args[0];

        /* java metaprogramming */
        // expressions 
        defineAst(outputDir, "Expr", Arrays.asList(
            "Assign   : Token name, Expr value",
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Unary    : Token operator, Expr right",
            "Variable : Token name"
        ));

        // statments
        defineAst(outputDir, "Stmt", Arrays.asList(
            "Block      : List<Stmt> statements",
            "Expression : Expr expression",
            "Print      : Expr expression",
            "Var        : Token name, Expr initializer"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types)
    throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
     
        // base class Expr
        writer.println("/* autogenerated by com.craftinginterpreters.tool.GenerateAst");
        writer.println(" * Defines a base class, type classes, and a visitor");
        writer.println(" * interface for an abstract syntax tree");
        writer.println(" */");
        writer.println("package com.craftinginterpreters.lox;");
        writer.println("");
        writer.println("import java.util.List;");
        writer.println("");
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        // abstract accept() method
        writer.println("");
        writer.println(indent() + "abstract <R> R accept(Visitor<R> vistor);");

        // each expression type extends Expr
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields    = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }    

        writer.println("}"); // end base class
        writer.close();
    }

    // iterate through subclasses and declare a visit method for each one
    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println(indent() + "interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();

            // R visitBinaryExpr(Binary expr);
            writer.println(indent(2) + "R visit" + typeName + baseName + 
                    "(" + typeName + " " + baseName.toLowerCase() + ");"); 
        }
        writer.println(indent() + "}");
    }

    private static void defineType(PrintWriter writer, 
                                    String baseName, 
                                    String className, 
                                    String fieldList) {
        // begin class
        writer.println();
        writer.println(indent() + "static class " + className + " extends " + baseName + " {");
        
        // Fields.
        String[] fields = fieldList.split(", ");
        for (String field : fields) {
            writer.println(indent("final " + field + ";", 2));
        }

        // visitor pattern
        writer.println();
        writer.println(indent(2) + "<R> R accept(Visitor<R> visitor) {");
        writer.println(indent(3) + "return visitor.visit" + className + baseName + "(this);");
        writer.println(indent(2) + "}");
        writer.println();

        // Constructor.
        writer.println(indent(2) + className + "(" + fieldList + ") {");
        // Store parameters in fields.
        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.println(indent(3) + "this." + name + " = " + name + ";");
        }
        writer.println(indent(2) + "}"); // end constructor

        writer.println(indent() + "}"); // end class
    }
    
    private static String indent(int times) {
        return indent("", times);
    }
    
    private static String indent() {
        return indent("", 1);
    }
    
    private static String indent(String text, int numTabs) {
        for (int i = 0; i < numTabs; i++)
        text = TAB + text;
        return text;
    }
}
