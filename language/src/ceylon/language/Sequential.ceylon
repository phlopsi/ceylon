"A possibly-empty, immutable sequence of values. The type 
 `Sequential<Element>` may be abbreviated `[Element*]` or 
 `Element[]`. 
 
 `Sequential` has two enumerated subtypes:
 
 - `Empty`, abbreviated `[]`, represents an empty sequence, 
    and
 - `Sequence<Element>`, abbreviated `[Element+]` represents 
    a non-empty sequence, and has the very important 
    subclass [[Tuple]]."
see (`class Tuple`)
shared interface Sequential<out Element>
        of []|[Element+]
        satisfies List<Element> & 
                  Ranged<Integer,Element,Element[]> {
    
    "Reverse this sequence, returning a new sequence."
    shared actual formal Element[] reversed;
    
    "This sequence."
    shared actual default Element[] sequence => this;
    
    "The rest of the sequence, without the first 
     element."
    shared actual formal Element[] rest;
    
    "Returns a sequence formed by repeating the elements of 
     this sequence the given number of times, or an empty 
     sequence if `times<=0`."
    shared actual default Element[] repeat(Integer times)
            => [*cycle(times)];
    
    "Select the first elements of this sequence, returning 
     a sequence no longer than the given length. If this 
     sequence is shorter than the given length, return this 
     sequence. Otherwise return a sequence of the given 
     length."
    shared actual default Element[] initial(Integer length)
            => this[0:length];
    
    "Select the last elements of the sequence, returning a 
     sequence no longer than the given length. If this 
     sequence is shorter than the given length, return this 
     sequence. Otherwise return a sequence of the given 
     length."
    shared actual default Element[] terminal(Integer length) {
        if (exists l = lastIndex, length>0) {
            return this[l-length+1..l];
        }
        else {
            return [];
        }
    }
    
    "Trim the elements satisfying the given predicate
     function from the start and end of this sequence, 
     returning a sequence no longer than this sequence."
    shared actual default Element[] trim(Boolean trimming(Element elem))
            => [*super.trim(trimming)]; //TODO: inefficient?
    
    "Trim the elements satisfying the given predicate
     function from the start of this sequence, returning 
     a sequence no longer than this sequence."
    shared actual default Element[] trimLeading(Boolean trimming(Element elem))
            => [*super.trimLeading(trimming)]; //TODO: inefficient?
    
    "Trim the elements satisfying the given predicate
     function from the end of this sequence, returning a 
     sequence no longer than this sequence."
    shared actual default Element[] trimTrailing(Boolean trimming(Element elem))
            => [*super.trimTrailing(trimming)]; //TODO: inefficient?
    
    "Produces a sequence with a given [[initial element|head]], 
     followed by the elements of this sequence."
    shared actual formal [Other,Element*] following<Other>(Other head);
    
    "This sequence."
    shared actual default Element[] clone() => this;
    
    "A string of form `\"[ x, y, z ]\"` where `x`, `y`, and 
     `z` are the `string` representations of the elements of 
     this collection, as produced by the iterator of the 
     collection, or the string `\"{}\"` if this collection 
     is empty. If the collection iterator produces the value 
     `null`, the string representation contains the string 
     `\"null\"`."
    shared actual default String string => 
            empty then "[]" else "[``commaList(this)``]";
    
}

"Return a [[Sequential]] containing the given [[elements]], 
 iterating the given [[Iterable]] only once."
Element[] sequential<Element>({Element*} elements) {
    if (is Element[] elements) {
        return elements;
    }
    if (is Array<Element> elements) {
        return elements.sequence;
    }
    value iterator = elements.iterator();
    variable value elem = iterator.next();
    if (is Finished x = elem) {
        return [];
    }
    assert (is Element x = elem);
    variable Array<Element> array = arrayOfSize<Element>(5, x);
    variable Integer index = 0;
    while (!is Finished y=elem) {
        if (index >= array.size) {
            value newSize = array.size + 
                    array.size.rightLogicalShift(1);
            value newarray = arrayOfSize<Element>(newSize, x);
            array.copyTo(newarray);
            array = newarray;
        }
        array.set(index, y);
        index++;
        elem = iterator.next();
    }
    return array.take(index).sequence;
}

