package calculator.ast.operators;

import calculator.ast.AstNode;
import datastructures.interfaces.IDictionary;

/**
 * An interface for defining handlers for operator nodes. Applying the operator
 * manipulates the AST by returning a replacement AstNode for the original
 * operator AstNode.
 *
 * The operator handler has access to the Calculator's dictionary of variables,
 * and may mutate it to define or redefine variables.
 */
@FunctionalInterface
public interface ExpressionOperator {
    AstNode apply(AstNode node, IDictionary<String, AstNode> variables);
}
