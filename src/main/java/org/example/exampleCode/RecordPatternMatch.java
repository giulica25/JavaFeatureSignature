package org.example.exampleCode;

public class RecordPatternMatch {
    record Point(double x, double y) {}

    static void printAngleFromXAxis(Object obj) {
        if (obj instanceof Point(_, double y)) {
            System.out.println("matched");
        }
    }
}
