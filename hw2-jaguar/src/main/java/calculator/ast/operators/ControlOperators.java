package calculator.ast.operators;

import calculator.ast.AstNode;
import calculator.errors.EvaluationError;
import calculator.errors.QuitError;
import calculator.Interpreter;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

public class ControlOperators {
    /**
     * This function is responsible for handling the `quit()` operation node.
     *
     * It makes the calculator program end when invoked.
     */
    public static AstNode handleQuit(AstNode node, IDictionary<String, AstNode> vars, Interpreter interpreter) {
        // This exception is caught and handled within one of the calculator-related classes.
        throw new QuitError();
    }

    /**
     * This function is responsible for handling the `block(a, b, c, ..., z)` node.
     *
     * It invokes child a, then child b, then child c and so forth until all children
     * are evaluated.
     */
    public static AstNode handleBlock(AstNode node, IDictionary<String, AstNode> vars, Interpreter interpreter) {
        // We check to make sure the signature is ok -- this is strictly optional.
        AstNode.assertOperatorValid("block", node);

        // Next, we construct a dummy return value node.
        AstNode out = new AstNode(1);

        // We then evaluate each child, one by one.
        for (AstNode child : node.getChildren()) {
            out = interpreter.evaluate(child);
        }

        // Then return value is same as last item in block,
        // or is equal to 1 if the block, for whatever reason, is empty
        return out;
    }

    public static AstNode handleAssign(AstNode node, IDictionary<String, AstNode> vars, Interpreter interpreter) {
        // Same thing: optional sanity check
        AstNode.assertOperatorValid("assign", 2, node);


        // Parse children
        IList<AstNode> children = node.getChildren();
        AstNode var = children.get(0);
        AstNode expr = interpreter.evaluate(children.get(1));

        // Some sanity checking
        if (!var.isVariable()) {
            throw new EvaluationError(String.format(
                    "LHS of assignment must be a variable. Encountered %s instead.",
                    var.isNumber() ? var.getNumericValue() : var.getName()));
        }

        // Record and return result
        vars.put(var.getName(), expr);
        return expr;
    }
}

