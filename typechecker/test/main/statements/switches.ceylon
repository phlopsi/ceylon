void testSwitch(Integer i, String s, Character c, String? ss) {
    switch (i)
    case (-1) {}
    case (0) {}
    case (1) {}
    else {}
    
    switch (s)
    case ("") {}
    else {}
    
    switch (ss)
    case ("") {}
    case (null) {}
    else {}
    
    switch (c)
    case (' ') {}
    case ('\n') {}
    else {}
    
    $error switch (i)
    case (-1) {}
    case (0) {}
    case (-1) {}
    else {}
    
    $error switch (i)
    case (1) {}
    case (0) {}
    case (1) {}
    else {}
    
    $error switch (i)
    case (1k) {}
    case (0) {}
    case (1000) {}
    else {}
    
    $error switch (i)
    case (-1) {}
    case (0) {}
    case (1) {}
    
    $error switch (s)
    case ("") {}
    
    $error switch (s)
    case ("") {}
    case ("") {}
    else {}
    
    $error switch (ss)
    case ("") {}
    case (null) {}
    
    $error switch (c)
    case (' ') {}
    case ('\n') {}
    
    $error switch (c)
    case (' ') {}
    case ('\{#0020}') {}
    else {}
    
    $error switch (c)
    case ('\\') {}
    case ('\{#005C}') {}
    else {}
    
}

void fun0<X>(X x) {
    switch (x)
    case (null) {
        print(x);
    }
    else {
        print(x.string);
    }
}

void fun1<X>(X&Boolean x) {
    switch (x)
    //$error case (null) {
    //    print(x);
    //}
    case (true) {}
    case (false) {}
    else {
        $error print(x.string);
    }
}

void fun2<X>(X x) {
    switch (x)
    case (true) {
        print("T");
    }
    case (false) {
        print("F");
    }
    else {
        $error print(x.string);
    }
}

shared void switchOnvariable() {
    variable value int = 1;
    switch (int)
    case (1) {
        int = 2;
    }
    else {
        int = 3;
    }
}

void coverageWithTypeParameter<E>(String|Set<E> ss) {
    switch (ss)
    case (is String) {}
    case (is Set<E>) {}
}
