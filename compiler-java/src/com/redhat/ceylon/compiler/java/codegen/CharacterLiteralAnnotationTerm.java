package com.redhat.ceylon.compiler.java.codegen;

import com.redhat.ceylon.langtools.tools.javac.tree.JCTree.JCAnnotation;
import com.redhat.ceylon.langtools.tools.javac.tree.JCTree.JCExpression;

public class CharacterLiteralAnnotationTerm extends LiteralAnnotationTerm {
    
    /** 
     * Sometimes we need an instance just for calling 
     * {@link #makeAtValue(ExpressionTransformer, String, JCExpression)} on.
     */
    public static final LiteralAnnotationTerm FACTORY = new CharacterLiteralAnnotationTerm(0);
    
    final int value;
    
    public CharacterLiteralAnnotationTerm(int value) {
        super();
        this.value = value;
    }
    @Override
    public com.redhat.ceylon.langtools.tools.javac.util.List<JCAnnotation> makeAtValue(
            ExpressionTransformer exprGen, String name, JCExpression value) {
        return exprGen.makeAtCharacterValue(name, value);
    }
    @Override
    public JCExpression makeLiteral(ExpressionTransformer exprGen) {
        return exprGen.make().Literal(value);
    }
    @Override
    public com.redhat.ceylon.langtools.tools.javac.util.List<JCAnnotation> makeExprs(ExpressionTransformer exprGen, com.redhat.ceylon.langtools.tools.javac.util.List<JCAnnotation> value) {
        return exprGen.makeAtCharacterExprs(exprGen.make().NewArray(null,  null, AbstractTransformer.upcastExprList(value)));
    }
    @Override
    public String toString() {
        return "'" + new String(Character.toChars(value)) + "'";
    }
}
