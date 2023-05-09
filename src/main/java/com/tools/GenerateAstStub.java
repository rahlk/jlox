package com.tools;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAstStub {
    public static void main(String... args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage:  gen_ast_stub <output_directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary    : Expr left, Token operator, Expr right",
                "Grouping  : Expr expression",
                "Literal   : Object value",
                "Unary     : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException{
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package com.lox.parser;\n");
        writer.println("import java.util.List;");
        writer.println("import com.lox.tokenizer.Token;\n");
        writer.println("abstract class " + baseName + " {");

        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineTypes(writer, baseName, className, fields, "\t");
        }

        writer.println("}");
        writer.close();

    }

    private static void defineTypes(PrintWriter writer, String baseName, String className, String fields, String tab) {
        writer.println(tab + "static class " + className + " extends " + baseName + " {");

        String[] field = fields.split(", ");

        // Define fields
        for (String f : field) {
            writer.println(tab+tab+"final "+f+";");
        }

        // Constructor
        writer.println(tab+tab+className+"("+fields+") {");
        for (String f : field) {
            // Store parameters in fields
            String name = f.split(" ")[1];
            writer.println(tab+tab+tab+"this." + name + " = " + name + ";");
        }
        writer.println();
        writer.println(tab+tab+"}");
        writer.println(tab+"}");
    }
}
