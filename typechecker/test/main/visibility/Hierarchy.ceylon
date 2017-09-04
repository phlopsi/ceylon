class Outer() {

    shared abstract class X() {
        shared formal String hello;
    }

    class Y() extends X() {
        shared actual String hello = "hello";
    }
    
    shared X create() {
        return Y();
    }

    $error shared class Z() extends Y() {}
    
}

void testHello() {
    String s1 = Outer().create().hello;
    $error String s2 = Outer().Z().hello;
    Outer.X x = Outer().Z();
    String s3 = x.hello;
}
