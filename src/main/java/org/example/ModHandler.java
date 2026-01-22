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
        handleDirectory(f);
        return versionsTot;
    }

    public void handleDirectory(File f) {
        for (File subF : f.listFiles()) {
            if (subF.isDirectory()) {
                this.handleDirectory(subF);
            }
            if (subF.isFile() && subF.getName().endsWith(".java")) {
                handleFile(subF);
            }
        }
    }

    public void handleFile(File f) {
        //System.out.println(f.getName());
        CompilationUnit cu = null;
        try {
            StaticJavaParser.getParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.JAVA_24);
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
