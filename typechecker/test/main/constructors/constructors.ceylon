$error class WithBothInitAndDefaultConst() {
    shared new withBothInitAndDefaultConst() {}
}

class WithNeitherInitNorConst() {} //TODO: should be an error

class WithConst extends Basic {
    shared new const() {}
}

$error class WithNoConst extends Basic() {}
$error class WithInit() extends Basic {}

$error class WithConstAndParams() {
    new const() {}
}

class WithDefaultConst {
    shared new () {}
}

class ExtendsWithDefaultConstBroken extends WithDefaultConst {
    $error shared new () {}
}

class ExtendsWithDefaultConstOk extends WithDefaultConst {
    shared new () extends WithDefaultConst() {}
}

class ExtendsWithConstBroken extends WithConst {
    $error shared new () {}
}

class ExtendsWithConstOk extends WithConst {
    shared new () extends const() {}
}

class WithConstAndDefaultConst {
    shared new () {}
    new const() {}
}

class WithAttributes {
    String name = "Trompon";
    Integer init;
    variable Integer count;
    print(name);
    shared new () {
        count = 0;
        init = count;
    }
    new constWithParameter(Integer initial) {
        count = initial;
        init = initial;
    }
    void inc() {
        count++;
    }
    void reset() {
        count = init;
    }
}

class WithSharedAttributes {
    shared String name = "Trompon";
    shared Integer init;
    shared variable Integer count;
    print(name);
    shared new () {
        count = 0;
        init = count;
    }
    new constWithParameter(Integer initial) {
        count = initial;
        init = initial;
    }
    shared void inc() {
        count++;
    }
    shared void reset() {
        count = init;
    }
}

class BrokenWithAttributes {
    String name;
    variable Integer count;
    Integer init;
    shared new () {
        init = 0;
    }
    new constWithParameter(Integer initial) {
        count = initial;
    }
    void inc() {
        $error count++;
    }
    void reset() {
        $error count = init;
    }
}

class BrokenWithSharedAttributes {
    $error shared String name;
    $error shared variable Integer count;
    $error shared Integer init;
    shared new () {
        init = 0;
    }
    new constWithParameter(Integer initial) {
        count = initial;
    }
    shared void inc() {
        $error count++;
    }
    shared void reset() {
        $error count = init;
    }
}

class WithAttributesAndMisplacedStatement {
    String name = "Trompon";
    Integer init;
    variable Integer count;
    shared new () {
        count = 0;
        init = count;
    }
    new constWithParameter(Integer initial) {
        count = initial;
        init = initial;
    }
    print(name);
    void inc() {
        count++;
    }
    void reset() {
        count = init;
    }
}

abstract class WithSplitInitializer {
    shared String name;
    shared Float x;
    shared new() { x=0.0; }
    shared new create() { x=1.0; }
    name = "Gavin";
    Integer count = 1;
}

class WithAttributesAndMispacedUsage {
    String name = "Trompon";
    Integer init;
    variable Integer count;
    print(name);
    $error print(init);
    shared new () {
        $error print(count);
        count = 0;
        init = count;
    }
    new constWithParameter(Integer initial) {
        $error print(init);
        count = initial;
        init = initial;
    }
    void inc() {
        count++;
    }
    void reset() {
        count = init;
    }
}

class Alias1() => WithDefaultConst();
class Alias2() => WithConst.const();
$error class BrokenAlias1() => WithConst();
$error class BrokenAlias2() => WithConst;
$error class AliasWithNoParams => WithNeitherInitNorConst();

class Super {
    shared new create() {}
}

class Broken extends Super {
    $error shared new () extends Super() {}
}

class MoreBroken extends Super {
    $error new broken() extends Basic() {}
    shared new screate() extends create() {}
}

class Sub1 extends Super {
    new create() extends Super.create() {}
    shared new screate() extends create() {}
}
class BrokenSub1 extends Super {
    $error new create() extends Super.create {}
    shared new screate() extends create() {}
}

class Sub2() extends Super.create() {}
$error class BrokenSub2() extends Super.create {}

class Alias() => Super.create();

class Sub3() extends Alias() {}
class Sub4 extends Alias {
    shared new () extends Alias() {}
}

class Silly extends Basic {
    shared new () extends Basic() {}
}

class Unshared() { }

shared class SharedWithConstructor {
    shared new ($error Unshared bar) { }
}

abstract class AbstractWithConstructor {
    shared new constructorForAbstract() {}
}

class InheritsAbstractWithConstructor1() 
        extends AbstractWithConstructor.constructorForAbstract() {
}

class InheritsAbstractWithConstructor2 
        extends AbstractWithConstructor {
    shared new constructor() extends constructorForAbstract() {}
}

void testRefs<T>() {
    $error value val1 = WithConst;
    $error value val2 = T;
    $error value val3 = Identifiable;
    $error value val4 = AbstractWithConstructor.constructorForAbstract;
    
    $type:"InheritsAbstractWithConstructor1" 
    value new1 = InheritsAbstractWithConstructor1();
    
    $type:"InheritsAbstractWithConstructor2" 
    value new2 = InheritsAbstractWithConstructor2.constructor();
}

shared class C<out X> {
    shared new ({X*} items) {}
    shared class B<out Y> {
        shared new ($error [X*] items) {}
        shared new other([Y*] items) {}
    }
}

shared class Supertype(Integer x) {
    shared Integer zsub = 0;
}

shared class Subtype extends Supertype {
    Integer xsub = 1;
    shared Integer ysub = 1;
    
    $error shared new subtype() extends Supertype(xsub) {}
    $error shared new create() extends Supertype(ysub) {}
    $error shared new extra() extends Supertype(ysub) {}
}

class WithDupeConstructor {
    new dupe() {}
    $error new dupe(String string) {}
    shared new create() {}
}
class WithDupeDefaultConstructor {
    $error shared new () {}
    $error new (String string) {}
}

class WithUnsharedDefaultConstructor {
    new () {}
    shared new create() {}
}

shared class Thing {
    String name;
    
    sealed shared new(String name) {
        this.name = name;
    }
    
    sealed shared new another(String name) {
        this.name = name;
    }
}

shared class WithPartialConstructor {
    Float length;
    String name;
    abstract new withLength(Float x, Float y) {
        length = (x^2+y^2)^0.5;
    }
    shared new withNameAndCoords(String name, Float x, Float y) 
            extends withLength(x, y) {
        this.name = name;
    }
    string = "``name``:``length``";
}

shared class WithNonPartialConstructorDelegation {
    Float length;
    String name;
    new withLengthAndName(Float x, Float y, String name) {
        length = (x^2+y^2)^0.5;
        this.name = name;
    }
    shared new withNameAndCoords(String name, Float x, Float y) 
            extends withLengthAndName(x, y, name) {}
    string = "``name``:``length``";
}

shared class WithDefaultConstructorDelegation {
    Float length;
    String name;
    shared new (Float x, Float y, String name) {
        length = (x^2+y^2)^0.5;
        this.name = name;
    }
    shared new withNameAndCoords(String name, Float x, Float y) 
            extends WithDefaultConstructorDelegation(x, y, name) {}
    string = "``name``:``length``";
}

shared class WithBrokenPartialConstructor {
    Float length;
    String name;
    abstract new withLength(Float x, Float y) {
        //length = (x^2+y^2)^0.5;
    }
    $error shared abstract new withBrokenLength(Float x, Float y) {
        length = (x^2+y^2)^0.5;
    }
    shared new withNameAndCoords(String name, Float x, Float y) 
            extends withLength(x, y) {
        this.name = name;
    }
    $error string = "``name``:``length``";
    $error print(withLength(1.0, 2.0));
}

class WithBrokenDelegation<Element> {
    
    shared new(){}
    
    $error shared new foo(Element f) 
            extends WithBrokenDelegation<Integer>(){} 
    
    shared new foo0(Element f) 
            extends WithBrokenDelegation<Element>(){} 
    
    shared new baz(){}
    
    $error shared new bar(Element f) 
            extends WithBrokenDelegation<Integer>.baz(){}
    
    shared new bar0(Element f) 
            extends WithBrokenDelegation<Element>.baz(){} 
}

class WithInnerClassExtendingPartialConstructor {
    abstract new partial() {}   
    $error shared class Inner() extends partial() {}
    shared new create() {}
}

class Foobar {
    abstract new partial() {}
    shared new create() {}
    $error class First() => Foobar.partial();
    shared class Second() => Foobar.create();
    $error shared class Third() => Foobar.none();
}

class WithMethod {
    shared void accept(Float x);
    shared new new1() {
        accept(Float x) => print(x);
    }
    shared new new2(void accept(Float x)) {
        this.accept = accept;
    }
}

void testWithMethod() {
    WithMethod.new1()
            .accept(1.0);
    WithMethod.new2((x) => print("hello " + x.string))
            .accept(3.0);
}

$error shared new(){}

class WithMemberRefInDelegation {
    shared new create(String s) {}
    String hello = "hello";
    $error shared new broken1 extends create(hello) {}
    function str(Integer j) => j.string;
    $error shared new broken2(Integer i) extends create(str(i)) {} 
}

class WithNoSharedConstructor {
    new instance {}
    new create() {}
}

class WithReturnInConstructor {
    shared new () {
        return;
    }
    shared new baz() {}
    print("done");
}


class WithConstructorWithInheritedMethodName 
        satisfies InterfaceWithMethod {
    shared new () {}
    $error shared new create() {}
}

interface InterfaceWithMethod {
    shared Integer create() => 0;
}

class WithDuplicateDefaultConstructor {
    $error shared new() {}
    $error shared new(String name) {}
}

shared class WithIllegalDefaultArg {
    Integer b = 1;
    shared new($error Integer a = b) { // error
        print(a);
    }
}


void notinherited() {
    
    class A {
        shared new () {}
        shared new create() {}
        shared new instance {}
        shared new overload() {}
    }
    
    class B extends A {
        shared new overload(String s) extends A() {}
    }
    
    void runAB() {
        value ai = A.instance;
        $error value bi = B.instance;
        value ac = A.create();
        $error value bc = B.create();
        
        $error value over1 = B.overload();
        value over2 = B.overload("");
    }
    
    class C1 {
        shared new create() {}
    }
    
    class C2 extends C1 {
        shared new create(String s = "") extends C1.create() {}
    }
    
    C2 c2 = C2.create();

}


class WithAssertion {
    assert (exists arg = process.arguments[0]);
    String stuff;
    shared new (String greeting) {
        print(greeting + arg);
        stuff = arg;
    }
    shared new withGreeting(String greeting) {
        print(greeting + arg);
        stuff = arg;
    }
    
    shared void do(String greeting) {
        print(greeting + arg);
    }
}


shared void testWithAssertion() {
    WithAssertion("Hello").do("Hello");
    WithAssertion.withGreeting("Hello").do("Hello");
}

class BadNew {
    $error shared new construct() extends BadNew() {}
    $error shared new () extends create() {}
    $error shared new create() extends make() {}
    shared new make() {}
}
class GoodNew {
    shared new make() {
        GoodNew n = create();
    }
    shared new create() extends make() {
        GoodNew n = GoodNew();
    }
    shared new () extends create() {
        GoodNew n = construct();
    }
    shared new construct() extends GoodNew() {}
}


class Stuff {
    shared new ofStuff() {
        Stuff thing1 = ofSize();
        Stuff thing2 = Stuff.ofSize();
    }
    shared new ofSize() {
        value stuff = ofStuff();
    }
}

class WithPrivateDefConstructor {
    new () {}
    shared new create() extends WithPrivateDefConstructor() {}
    shared class Inner() extends WithPrivateDefConstructor() {
        WithPrivateDefConstructor();
    }
}

class OkExtendsWithPrivateDefConstructor() extends WithPrivateDefConstructor.create() {}
$error class BadExtendsWithPrivateDefConstructor() extends WithPrivateDefConstructor() {}

void testWithPrivateDefConstructor() {
    WithPrivateDefConstructor.create().Inner();
    $error value foo = WithPrivateDefConstructor();     
    $error value makeF = WithPrivateDefConstructor;
    WithPrivateDefConstructor bar = WithPrivateDefConstructor.create();
    $error WithPrivateDefConstructor();
    $error `WithPrivateDefConstructor`();
}

class Order {
    shared static class Status 
            of completed
             | active
             | inactive {
        shared new completed {}
        shared new active {}
        shared new inactive {}
    }
    new () {}
}

void testOrderStatus(Order.Status status) {
    switch (status)
    case (Order.Status.completed) {}
    case (Order.Status.active) {}
    case (Order.Status.inactive) {}
}

