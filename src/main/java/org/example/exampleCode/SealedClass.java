package org.example.exampleCode;

public sealed class SealedClass permits SealedClass.Test {

    public final class Test extends SealedClass {

    }
}
