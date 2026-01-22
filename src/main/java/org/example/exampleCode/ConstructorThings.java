package org.example.exampleCode;

public class ConstructorThings {
    public ConstructorThings(int j) {
        int x = j;
    }

    public class TestConstr extends ConstructorThings{
        public TestConstr() {
            System.out.println("Test");
            super(2);
        }
    }

    public class TestCon2 extends ConstructorThings{
        public TestCon2() {
            super(1);
        }
    }
}
