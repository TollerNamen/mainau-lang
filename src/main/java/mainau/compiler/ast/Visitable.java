package mainau.compiler.ast;

import mainau.compiler.visitor.ASTVisitor;

public interface Visitable {

    <R, P> R accept(ASTVisitor<? extends R, P> visitor, P param);
}