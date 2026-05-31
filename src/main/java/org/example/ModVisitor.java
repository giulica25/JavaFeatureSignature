package org.example;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Optional;

public class ModVisitor extends VoidVisitorAdapter {
    private FeatureUsageMap featureUsageMap;

    public ModVisitor() {
        featureUsageMap = new FeatureUsageMap();
    }

    @Override
    public void visit(SwitchStmt n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.SWITCH_STATEMENT);
        for (SwitchEntry entry : n.getEntries()) {
            if (entry.getGuard().isPresent()) {
                featureUsageMap.increment(Feature.SWITCH_STATEMENT_GUARD);
            }
            for (var label: entry.getLabels()) {
                if (label.isStringLiteralExpr()) {
                    featureUsageMap.increment(Feature.STRING_SWITCH);
                }
                if (label.isPatternExpr()) {
                    featureUsageMap.increment(Feature.SWITCH_STATEMENT_PATTERN_MATCHING);
                    if (label.asPatternExpr().getType().isPrimitiveType()) {
                        featureUsageMap.increment(Feature.SWITCH_STATEMENT_PRIMITIVE_TYPE_PATTERN_EXPRESSION);
                    }
                    if (label.asPatternExpr().isRecordPatternExpr()) {
                        featureUsageMap.increment(Feature.SWITCH_STATEMENT_RECORD_PATTERN_EXPRESSION);
                    }
                }
            }
        }
    }

    @Override
    public void visit(TryStmt n, Object arg) {
        super.visit(n, arg);
        if (!n.getResources().isEmpty()) {
            featureUsageMap.increment(Feature.TRY_WITH_RESOURCES);
            for (Expression ex : n.getResources()) {
                if (!ex.isVariableDeclarationExpr()) {
                    featureUsageMap.increment(Feature.TRY_WITH_RESOURCES_FINAL_VARIABLE);
                }
            }
        }
    }

    public void visit(Type n, Object arg) {
        if (n.getAnnotations().isNonEmpty()) {
            featureUsageMap.increment(Feature.TYPE_ANNOTATION);
        }
        if ("VarHandle".equals(n.asString())) {
            featureUsageMap.increment(Feature.VARHANDLE);
        }
        if ("StableValue".equals(n.asString())) {
            featureUsageMap.increment(Feature.STABLE_VALUE);
        }
    }

    @Override
    public void visit(Modifier n, Object arg) {
        super.visit(n, arg);
        if (n.getKeyword() == Modifier.Keyword.STRICTFP) {
            featureUsageMap.increment(Feature.STRICTFP);
        }
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.isPrivate() && !n.getAnnotationByName("SafeVarargs").equals(Optional.empty())) {
            featureUsageMap.increment(Feature.SAFE_VARARGS);
        }
        if (n.isGeneric()) {
            featureUsageMap.increment(Feature.GENERIC_METHOD);
        }
        if (n.getParentNode().isPresent()) {
            if (!(n.getParentNode().get() instanceof ClassOrInterfaceDeclaration)) {
                featureUsageMap.increment(Feature.UNNAMED_CLASS);
            }
        }
        if ("main".equals(n.getName().asString())) {
            if (!n.isStatic()) {
                featureUsageMap.increment(Feature.INSTANCE_MAIN_METHOD);
            }
            if (!n.isPublic()) {
                featureUsageMap.increment(Feature.NON_PUBLIC_MAIN_METHOD);
            }
        }
    }

    @Override
    public void visit(MethodCallExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getTypeArguments().isPresent()) {
            featureUsageMap.increment(Feature.DIAMOND_OPERATOR);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.isInterface()) {
            for(MethodDeclaration method : n.getMethods()) {
                if (method.isPrivate()) {
                    featureUsageMap.increment(Feature.INTERFACE_PRIVATE_METHOD);
                }
                if (method.isStatic()) {
                    featureUsageMap.increment(Feature.INTERFACE_STATIC_METHOD);
                }
                if (method.isDefault()) {
                    featureUsageMap.increment(Feature.INTERFACE_DEFAULT_METHOD);
                }
            }
        }
        for (var member : n.getMembers()) {
            if (member.isClassOrInterfaceDeclaration()) {
                featureUsageMap.increment(Feature.INNER_CLASS);
            }
        }

        if (n.isEnumDeclaration()) {
            featureUsageMap.increment(Feature.ENUM);
        }
        if (n.getModifiers().contains(Modifier.sealedModifier())) {
            featureUsageMap.increment(Feature.SEALED_MODIFIER);
        }
    }

    @Override
    public void visit(VarType n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.VAR_TYPE);
    }

    @Override
    public void visit(LambdaExpr n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.LAMBDA_EXPRESSION);
        for (Parameter par : n.getParameters()) {
            if (par.getType().isVarType()) {
                //TODO: reduce version 10?
                featureUsageMap.increment(Feature.LAMBDA_VAR_TYPE);
            }
        }
    }

    @Override
    public void visit(Parameter n, Object args) {
        super.visit(n, args);
        if (n.isVarArgs()) {
            featureUsageMap.increment(Feature.VARARGS);
        }
    }

    @Override
    public void visit(TextBlockLiteralExpr n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.TEXT_BLOCK_LITERAL_EXPRESSION);
        for (String line : n.stripIndentOfLines().toList()) {
            if (line.endsWith("\\")) {
                featureUsageMap.increment(Feature.TEXT_BLOCK_ESCAPE_NEWLINE);
            }
            if (line.endsWith("\\s")) {
                featureUsageMap.increment(Feature.TEXT_BLOCK_ESCAPE_SPACE);
            }
        }
    }

    @Override
    public void visit(YieldStmt n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.YIELD);
    }

    @Override
    public void visit(SwitchExpr n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.SWITCH_EXPRESSION);;
        for (SwitchEntry entry : n.getEntries()) {
            if (entry.getGuard().isPresent()) {
                featureUsageMap.increment(Feature.SWITCH_EXPRESSION_GUARD);
            }
            for (Expression e : entry.getLabels()) {
                if (e.isPatternExpr()) {
                    featureUsageMap.increment(Feature.SWITCH_EXPRESSION_PATTERN_MATCHING);
                    if (e.asPatternExpr().getType().isPrimitiveType()) {
                        featureUsageMap.increment(Feature.SWITCH_EXPRESSION_PRIMITIVE_TYPE_PATTERN_EXPRESSION);
                    }
                    if (e.asPatternExpr().isRecordPatternExpr()) {
                        featureUsageMap.increment(Feature.SWITCH_EXPRESSION_RECORD_PATTERN_EXPRESSION);
                    }
                }
            }
        }
    }

    @Override
    public void visit(InstanceOfExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getPattern().isPresent()) {
            featureUsageMap.increment(Feature.INSTANCE_OF_PATTERN);
            if (n.getPattern().get().getType().isPrimitiveType()) {
                featureUsageMap.increment(Feature.INSTANCE_OF_PATTERN_PRIMITIVE_TYPE);
            }
        }
        if (n.isRecordPatternExpr()) {
            featureUsageMap.increment(Feature.INSTANCEOF_RECORD_PATTERN_EXPRESSION);
        }
    }

    @Override
    public void visit(RecordDeclaration n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.RECORD);
        if (n.isLocalRecordDeclaration()) {
            featureUsageMap.increment(Feature.LOCAL_RECORD);
        }
    }

    @Override
    public void visit(ForEachStmt n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.FOR_EACH);
        if (n.getIterable().isPatternExpr()) {
            if (n.getIterable().asPatternExpr().isRecordPatternExpr()){
                featureUsageMap.increment(Feature.FOR_EACH_RECORD_PATTERN_EXPRESSION);
            }
        }
    }

    @Override
    public void visit(CatchClause n, Object arg) {
        super.visit(n, arg);
        if (n.getParameter().getType().isUnionType()) {
            featureUsageMap.increment(Feature.CATCH_MULTIPLE_EXCEPTION_TYPES);
        }
    }

    @Override
    public void visit(ConstructorDeclaration n, Object arg) {
        super.visit(n, arg);
        //Ugly but works so meh, Happens cuz isSuper() does not exist for some reason
        NodeList<Statement> stats = n.getBody().getStatements();
        for (Statement s : stats) {
            if (s.isExplicitConstructorInvocationStmt()) {
                if (!stats.get(0).equals(s)) {
                    featureUsageMap.increment(Feature.FLEXIBLE_CONSTRUCTOR_BODIES);
                }
            }
        }
    }

    @Override
    public void visit(SimpleName n, Object arg) {
        super.visit(n, arg);
        if (n.toString().equals("_")) {
            featureUsageMap.increment(Feature.UNDERSCORE);
        }
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.isStatic()) {
            featureUsageMap.increment(Feature.STATIC_IMPORT_DECLARATION);
        }
        if (n.isModule()) {
            featureUsageMap.increment(Feature.MODULE_IMPORT_DECLARATION);
        }
    }

    @Override
    public void visit(SwitchEntry n, Object arg) {
        super.visit(n, arg);
        if (n.getLabels().size() > 1) {
            featureUsageMap.increment(Feature.SWITCH_ENTRY_MULTIPLE_LABELS);
        }
        if (n.getType().equals(SwitchEntry.Type.EXPRESSION)) {
            featureUsageMap.increment(Feature.SWITCH_ENTRY_TYPE_EXPRESSION);
        }
    }

    @Override
    public void visit(MethodReferenceExpr n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.METHOD_REFERENCE_EXPRESSION);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Object arg) {
        super.visit(n, arg);
        //Kinda awful but works in a pinch
        if (n.toString().startsWith("Optional")) {
            featureUsageMap.increment(Feature.OPTIONAL_VARIABLE_DECLARATION);
        }
    }

    @Override
    public void visit(AnnotationDeclaration n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.ANNOTATION);
        for (var annotation: n.getAnnotations()) {
            if ("Repeatable".equals(annotation.getName().asString())) {
                featureUsageMap.increment(Feature.REPEATABLE_ANNOTATION);
            }
        }
    }

    @Override
    public void visit(AssertStmt n, Object arg) {
        super.visit(n, arg);
        featureUsageMap.increment(Feature.ASSERT);
    }

    @Override
    public void visit(IntegerLiteralExpr n, Object arg) {
        super.visit(n, arg);
        if(n.toString().startsWith("0b")) {
            featureUsageMap.increment(Feature.BINARY_INTEGER_LITERAL_EXPRESSION);
        }

        if(n.toString().contains("_")) {
            featureUsageMap.increment(Feature.UNDERSCORE_IN_INTEGER_LITERAL);
        }
    }

    @Override
    public void visit(BooleanLiteralExpr n, Object arg) {
        super.visit(n, arg);
        if(n.toString().startsWith("0b")) {
            featureUsageMap.increment(Feature.BINARY_BOOLEAN_LITERAL_EXPRESSION);
        }
    }

    @Override
    public void visit(LongLiteralExpr n, Object arg) {
        super.visit(n, arg);
        if(n.toString().startsWith("0b")) {
            featureUsageMap.increment(Feature.BINARY_LONG_LITERAL_EXPRESSION);
        }

        if(n.toString().contains("_")) {
            featureUsageMap.increment(Feature.UNDERSCORE_IN_LONG_LITERAL);
        }
    }

    @Override
    public void visit(DoubleLiteralExpr n, Object arg) {
        super.visit(n, arg);
        if(n.toString().startsWith("0b")) {
            featureUsageMap.increment(Feature.BINARY_DOUBLE_LITERAL_EXPRESSION);
        }

        if(n.toString().contains("_")) {
            featureUsageMap.increment(Feature.UNDERSCORE_IN_DOUBLE_LITERAL);
        }
    }

    @Override
    public void visit(ClassOrInterfaceType n, Object arg) {
        super.visit(n, arg);
        if (n.getTypeArguments().isPresent()) {
            featureUsageMap.increment(Feature.GENERIC_CLASS);
        }
    }

    public FeatureUsageMap getFeatureUsageMap() {
        return featureUsageMap;
    }
}
