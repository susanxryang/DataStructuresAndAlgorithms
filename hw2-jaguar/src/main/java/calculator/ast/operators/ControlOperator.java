package calculator.ast.operators;

import calculator.Interpreter;
import calculator.ast.AstNode;
import datastructures.interfaces.IDictionary;

/**
 * An interface for special operators that have control over the evaluation of their
 * own children, or otherwise affect the execution of the Calculator.
 *
 * The operator handler has access to the Calculator's Interpreter, in addition to its
 * dictionary of variables.
 *
 * Additionally, children of operators registered as control operators are not automatically
 * evaluated; the handler must call Interpreter.evaluate to do so explicitly.
 */
@FunctionalInterface
public interface ControlOperator {
    AstNode apply(AstNode node, IDictionary<String, AstNode> variables, Interpreter interpreter);
}
