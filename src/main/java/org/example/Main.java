package org.example;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Main {
    
    public static void main(String[] args) {
        String SOURCE_PATH = "src/main/java/org/example/sourceCode";
        File f2 = new File(SOURCE_PATH);
        for (File f : Objects.requireNonNull(f2.listFiles())) {
            ModHandler handler = new ModHandler();
            System.out.println(f.getName());
            System.out.println(Arrays.toString(handler.handle(f)));
        }
    }
}