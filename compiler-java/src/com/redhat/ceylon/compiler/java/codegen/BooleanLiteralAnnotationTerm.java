package com.redhat.ceylon.compiler.java.codegen;

import com.redhat.ceylon.langtools.tools.javac.tree.JCTree.JCAnnotation;
import com.redhat.ceylon.langtools.tools.javac.tree.JCTree.JCExpression;

public class BooleanLiteralAnnotationTerm extends LiteralAnnotationTerm {
    
    /** 
     * Sometimes we need an instance just for calling 
     * {@link #makeAtValue(ExpressionTransformer, String, JCExpression)} on.
     */
    public static final LiteralAnnotationTerm FACTORY = new BooleanLiteralAnnotationTerm(false);
    
    final boolean value;
    public BooleanLiteralAnnotationTerm(boolean value) {
        super();
        this.value = value;
    }
    @Override
    public com.redhat.ceylon.langtools.tools.javac.util.List<JCAnnotation> makeAtValue(
            ExpressionTransformer exprGen, String name, JCExpression value) {
        return exprGen.makeAtBooleanValue(name, value);
    }
    @Override
    public JCExpression makeLiteral(ExpressionTransformer exprGen) {
        return exprGen.make().Literal(value);
    }
    @Override
    public com.redhat.ceylon.langtools.tools.javac.util.List<JCAnnotation> makeExprs(ExpressionTransformer exprGen, com.redhat.ceylon.langtools.tools.javac.util.List<JCAnnotation> value) {
        return exprGen.makeAtBooleanExprs(exprGen.make().NewArray(null,  null, AbstractTransformer.upcastExprList(value)));
    }
    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
