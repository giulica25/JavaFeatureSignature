package org.example;

import com.github.javaparser.TokenTypes;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.Optional;
import java.util.stream.Stream;

public class ModVisitor extends VoidVisitorAdapter {
    private int[] versions;
    public ModVisitor() {
        //Version array, for convenience this is one size to small so index 23 will directly
        //reference version 23
        this.versions = new int[26];
        for (int i : versions) {
            versions[i] = 0;
        }
    }

    @Override
    public void visit(SwitchStmt n, Object arg) {
        super.visit(n, arg);
        versions[12] += 1;
        for (SwitchEntry entry : n.getEntries()) {
            if (entry.getGuard().isPresent()) {
                versions[19] += 1;
            }
            for (Expression e : entry.getLabels()) {
                if (e.isPatternExpr()) {
                    versions[17] += 1;
                    if (e.asPatternExpr().getType().isPrimitiveType()) {
                        versions[23] += 1;
                    }
                }
            }
        }
    }

    @Override
    public void visit(TryStmt n, Object arg) {
        super.visit(n, arg);
        if (!n.getResources().isEmpty()) {
            versions[7] += 1;
            for (Expression ex : n.getResources()) {
                if (!ex.isVariableDeclarationExpr()) {
                    versions[9] += 1;
                }
                ;
            }
        }
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.isPrivate() && !n.getAnnotationByName("SafeVarargs").equals(Optional.empty())) {
            versions[9] += 1;
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.isInterface()) {
            for(MethodDeclaration method : n.getMethods()) {
                if (method.isPrivate()) {
                    versions[9] += 1;
                }
                if (method.isStatic() || method.isDefault()) {
                    versions[8] += 1;
                }
            }
        }
        if (n.getModifiers().contains(Modifier.sealedModifier())) {
            versions[15] += 1;
        }
    }

    @Override
    public void visit(VarType n, Object arg) {
        super.visit(n, arg);
        versions[10] += 1;
    }

    @Override
    public void visit(LambdaExpr n, Object arg) {
        super.visit(n, arg);
        versions[8] += 1;
        for (Parameter par : n.getParameters()) {
            if (par.getType().isVarType()) {
                //TODO: reduce version 10?
                versions[11] += 1;
            }
        }
    }

    @Override
    public void visit(TextBlockLiteralExpr n, Object arg) {
        super.visit(n, arg);
        versions[13] += 1;
        for (String line : n.stripIndentOfLines().toList()) {
            if (line.endsWith("\\") || line.endsWith("\\s")) {
                versions[14] += 1;
            }
        }
    }

    @Override
    public void visit(YieldStmt n, Object arg) {
        super.visit(n, arg);
        versions[13] += 1;
    }

    @Override
    public void visit(SwitchExpr n, Object arg) {
        super.visit(n, arg);
        versions[12] += 1;
        for (SwitchEntry entry : n.getEntries()) {
            if (entry.getGuard().isPresent()) {
                versions[19] += 1;
            }
            for (Expression e : entry.getLabels()) {
                if (e.isPatternExpr()) {
                    versions[17] += 1;
                    if (e.asPatternExpr().getType().isPrimitiveType()) {
                        versions[23] += 1;
                    }
                }
            }
        }
    }

    @Override
    public void visit(InstanceOfExpr n, Object arg) {
        super.visit(n, arg);
        if (n.getPattern().isPresent()) {
            versions[14] += 1;
            if (n.getPattern().get().getType().isPrimitiveType()) {
                versions[23] += 1;
            }
        }
    }

    @Override
    public void visit(RecordDeclaration n, Object arg) {
        super.visit(n, arg);
        versions[14] += 1;
        if (n.isLocalRecordDeclaration()) {
            versions[15] += 1;
        }
    }

    @Override
    public void visit(RecordPatternExpr n, Object arg) {
        versions[19] += 1;
    }

    @Override
    public void visit(ForEachStmt n, Object arg) {
        super.visit(n, arg);
        versions[5] += 1;
        if (n.getIterable().isPatternExpr()) {
            if (n.getIterable().asPatternExpr().isRecordPatternExpr()){
                versions[20] += 1;
            }
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
                    versions[22] += 1;
                }
            }
        }
    }

    @Override
    public void visit(SimpleName n, Object arg) {
        super.visit(n, arg);
        if (n.toString().equals("_")) {
            versions[21] += 1;
        }
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
        super.visit(n, arg);
        if (n.isStatic()) {
            versions[5] += 1;
        }
        if (n.isModule()) {
            versions[23] += 1;
        }
    }

    @Override
    public void visit(SwitchEntry n, Object arg) {
        super.visit(n, arg);
        if (n.getType().equals(SwitchEntry.Type.EXPRESSION)) {
            versions[13] += 1;
        }
    }

    @Override
    public void visit(MethodReferenceExpr n, Object arg) {
        super.visit(n, arg);
        versions[8] += 1;
    }

    @Override
    public void visit(VariableDeclarationExpr n, Object arg) {
        super.visit(n, arg);
        //Kinda awful but works in a pinch
        if (n.toString().startsWith("Optional")) {
            versions[8] += 1;
        }
    }

    @Override
    public void visit(AnnotationDeclaration n, Object arg) {
        super.visit(n, arg);
        versions[5] += 1;
    }

    @Override
    public void visit(AssertStmt n, Object arg) {
        super.visit(n, arg);
        versions[4] += 1;
    }

    @Override
    public void visit(IntegerLiteralExpr n, Object arg) {
        super.visit(n, arg);
        if(n.toString().startsWith("0b")) {
            versions[7] += 1;
        }
    }

    @Override
    public void visit(BooleanLiteralExpr n, Object arg) {
        super.visit(n, arg);
        if(n.toString().startsWith("0b")) {
            versions[7] += 1;
        }
    }

    @Override
    public void visit(LongLiteralExpr n, Object arg) {
        super.visit(n, arg);
        if(n.toString().startsWith("0b")) {
            versions[7] += 1;
        }
    }

    @Override
    public void visit(DoubleLiteralExpr n, Object arg) {
        super.visit(n, arg);
        if(n.toString().startsWith("0b")) {
            versions[7] += 1;
        }
    }

    @Override
    public void visit(ClassOrInterfaceType n, Object arg) {
        super.visit(n, arg);
        if (n.getTypeArguments().isPresent()) {
            versions[5] += 1;
        }
    }

    public int[] getVersions() {
        return versions;
    }
}
