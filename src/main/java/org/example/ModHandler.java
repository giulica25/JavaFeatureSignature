package org.example;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class ModHandler {
    private int[] versionsTot;
    public ModHandler() {
        versionsTot = new int[26];
        for (int i = 0; i < 26; i++) {
            versionsTot[i] = 0;
        }
    }

    public int[] handle(File f) {
        if (f.isDirectory()) {
            this.handleDirectory(f);
        }
        if (f.isFile() && f.getName().endsWith(".java")) {
            handleFile(f);
        }
        return versionsTot;
    }

    public void handleDirectory(File f) {
        for (File subF : f.listFiles()) {
            handle(subF);
        }
    }

    public void handleFile(File f) {
        //System.out.println(f.getName());
        CompilationUnit cu = null;
        try {
            StaticJavaParser.getParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.JAVA_25);
            cu = StaticJavaParser.parse(f);
            ModVisitor visitor = new ModVisitor();
            visitor.visit(cu, null);
            addToTotal(visitor.getVersions());
            //System.out.println(Arrays.toString(visitor.getVersions()));
        } catch (Exception e) {
            System.out.println("Encountered problem while running: " + e);
        }
    }

    public void addToTotal(int[] versions) {
        for (int i = 0; i < 26; i++) {
            versionsTot[i] += versions[i];
        }
    }
}
