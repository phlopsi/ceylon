package com.redhat.ceylon.compiler.java.language;

import ceylon.language.Array;

import com.redhat.ceylon.compiler.java.Util;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Class;
import com.redhat.ceylon.compiler.java.metadata.Defaulted;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import com.redhat.ceylon.compiler.java.metadata.ValueType;
import com.redhat.ceylon.compiler.java.runtime.model.ReifiedType;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;

/**
 * A Ceylon schema for a Java <code>byte[]</code>.
 *
 * This type is never instantiated, it is completely erased to 
 * <code>byte[]</code>.
 * 
 * The {@link #get(int)}, {@link #set(int,byte)}, {@link #length size} 
 * methods and the constructor are also completely erased to Java array 
 * operators, or {@link Util#fillArray(byte[],byte)} in the case that an
 * initial element is specified.
 * 
 * Only the value type static methods are really invoked.
 *
 * @author Stéphane Épardaud <stef@epardaud.fr>
 */
@Ceylon(major = 7)
@Class
@ValueType
public final class ByteArray implements ReifiedType {
    
    @Ignore
    public final static TypeDescriptor $TypeDescriptor$ = TypeDescriptor.klass(ByteArray.class);
    
    public ByteArray(@Name("size") int size, @TypeInfo("ceylon.language::Integer") @Defaulted @Name("element") byte element){
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public ByteArray(@Name("size") int size){
        throw Util.makeJavaArrayWrapperException();
    }

    public byte get(int index) {
        throw Util.makeJavaArrayWrapperException();
    }

    public void set(int index, byte element) {
        throw Util.makeJavaArrayWrapperException();
    }

    @Name("size")
    public final int length = 0;

    @Ignore
    public static ceylon.language.Array<java.lang.Byte> getArray(byte[] array){
        return Array.instance(array);
    }

    @TypeInfo("ceylon.language::Array<java.lang::Byte>")
    public ceylon.language.Array<java.lang.Byte> getArray(){
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public static void copyTo(byte[] array, byte[] destination){
        System.arraycopy(array, 0, destination, 0, array.length);
    }

    @Ignore
    public static void copyTo(byte[] array, byte[] destination, int sourcePosition){
        System.arraycopy(array, sourcePosition, destination, 0, array.length-sourcePosition);
    }

    @Ignore
    public static void copyTo(byte[] array, byte[] destination, int sourcePosition, int destinationPosition){
        System.arraycopy(array, sourcePosition, destination, destinationPosition, array.length-sourcePosition);
    }

    @Ignore
    public static void copyTo(byte[] array, byte[] destination, int sourcePosition, int destinationPosition, int length){
        System.arraycopy(array, sourcePosition, destination, destinationPosition, length);
    }

    @Ignore
    public int copyTo$sourcePosition(byte[] destination){
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public int copyTo$destinationPosition(byte[] destination, int sourcePosition){
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public int copyTo$length(byte[] destination, int sourcePosition, int destinationPosition){
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public void copyTo(byte[] destination){
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public void copyTo(byte[] destination, 
                       int sourcePosition){
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public void copyTo(byte[] destination, 
                       int sourcePosition, 
                       int destinationPosition){
        throw Util.makeJavaArrayWrapperException();
    }

    public void copyTo(@Name("destination") byte[] destination, 
                       @Name("sourcePosition") @Defaulted int sourcePosition, 
                       @Name("destinationPosition") @Defaulted int destinationPosition, 
                       @Name("length") @Defaulted int length){
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    @Override
    public TypeDescriptor $getType$() {
        throw Util.makeJavaArrayWrapperException();
    }

    @Override
    public boolean equals(@Name("that") java.lang.Object that) {
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public static boolean equals(byte[] value, java.lang.Object that) {
        return value.equals(that);
    }

    @Override
    public int hashCode() {
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public static int hashCode(byte[] value) {
        return value.hashCode();
    }

    @Override
    public java.lang.String toString() {
        throw Util.makeJavaArrayWrapperException();
    }

    @Ignore
    public static java.lang.String toString(byte[] value) {
        return value.toString();
    }
}
