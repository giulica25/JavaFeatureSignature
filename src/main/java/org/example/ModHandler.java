package org.example;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;

public class ModHandler {
    FeatureUsageMap featureUsageMap;
    public ModHandler() {
        this.featureUsageMap = new FeatureUsageMap();
    }

    public FeatureUsageMap handle(File f) {
        if (f.isDirectory()) {
            this.handleDirectory(f);
        }
        if (f.isFile() && f.getName().endsWith(".java")) {
            handleFile(f);
        }
        return featureUsageMap;
    }

    public void handleDirectory(File f) {
        for (File subF : f.listFiles()) {
            handle(subF);
        }
    }

    public void handleFile(File f) throws ParseProblemException {
//        System.out.println("Analyzing file " + f.getPath());
        try {
            StaticJavaParser.getParserConfiguration().setLanguageLevel(
                    ParserConfiguration.LanguageLevel.JAVA_25);
            CompilationUnit cu = StaticJavaParser.parse(f);
            ModVisitor visitor = new ModVisitor();
            visitor.visit(cu, null);
            featureUsageMap.add(visitor.getFeatureUsageMap());
            //System.out.println(Arrays.toString(visitor.getVersions()));
        } catch (ParseProblemException e) {
            System.out.println("Encountered parsing error in file " + f.getPath());
            System.out.println("Error message: " + e.getMessage());
            throw e;
        } catch (FileNotFoundException ignored) {
        }
    }
}
