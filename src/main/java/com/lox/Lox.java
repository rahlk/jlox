package com.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.lox.errors.ErrorHandler;
import com.lox.scanner.Scanner;
import com.lox.tokenizer.Token;

public class Lox {

    public static void main(String... args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage:  jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (ErrorHandler.hadError) {
            System.exit(65);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            ErrorHandler.hadError = false;
        }
    }

    private static void run(String source) {

        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

}
