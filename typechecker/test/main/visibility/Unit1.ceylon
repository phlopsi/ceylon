import visibility.pack { $error Unit3, Unit4, Unit5 }

class Unit1() {
    
    void print(String s) {}
    
    shared String hello = "Hello";
    String goodbye = "Goodbye";
    
    shared class Inner() {
        shared String hello = "Hello";
        String goodbye = "Goodbye";
        print(Inner().goodbye);
        void method() {
            say(outer.hello);
            say(outer.goodbye);
            say(hello);
            say(goodbye);
        }
        void say(String s) {}
    }
    
    print(Inner().hello);
    $error print(Inner().goodbye);
    Unit1 u1 = Unit1();
    u1.print(u1.goodbye);
    
    $error print(Unit2().Inner().hello);
    $error print(Unit2().Inner().goodbye); 
    
    $error class Subclass1() extends Unit3() {}
    class Subclass2() extends Unit4() {}
    
    $error Unit3 u3 = Unit3();
    Unit4 u4 = Unit4();
    
    $error Unit3();
    
    $error String h = u3.hello;
    $error String s = Unit5().hello;
    
}