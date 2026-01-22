package org.example.exampleCode;

public class InstanceOf {

    static void main() {
        String test = "test";

        if (test instanceof String) {

        }
        if (test instanceof String s) {
            System.out.println(s);
        }
    }
}
