package org.example.exampleCode;

public class SwitchWithGuards {

    public static void main(String[] args) {
        int x = 1;
        int b = 2;

        switch(b) {
            case 2 -> b = 3;
            default -> b = 4;
        }

        switch(b) {
            case 3:
                b = 4;
                break;
            default:
                b = 5;
        }
    }

    static void test(Object obj) {
        switch (obj) {
            case String s when s.length() == 1 -> System.out.println("Short: " + s);
            case String s                      -> System.out.println(s);
            default                            -> System.out.println("Not a string");
        }
    }
}
