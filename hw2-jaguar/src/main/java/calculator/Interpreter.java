package calculator;

import calculator.ast.operators.ExpressionOperator;
import calculator.ast.AstNode;
import calculator.ast.operators.ControlOperator;
import calculator.ast.operators.GuiOperator;
import calculator.gui.ImageDrawer;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ArrayDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

/**
 * This class handles all the evaluation of ASTs and maintains the state of the Calculator
 * (i.e., the values of variables).
 */
public class Interpreter {
    private final IDictionary<String, ExpressionOperator> regularOperators;
    private final IDictionary<String, GuiOperator> guiOperators;
    private final IDictionary<String, ControlOperator> controlOperators;
    private final IDictionary<String, AstNode> variables;
    private ImageDrawer imageDrawer;

    Interpreter(IDictionary<String, ExpressionOperator> regularOperators,
                       IDictionary<String, GuiOperator> guiOperators,
                       IDictionary<String, ControlOperator> controlOperators) {
        this.regularOperators = regularOperators;
        this.guiOperators = guiOperators;
        this.controlOperators = controlOperators;
        this.variables = new ArrayDictionary<>();
        this.imageDrawer = null;
    }

    /**
     * Sets the ImageDrawer used to draw plots.
     */
    void setImageDrawer(ImageDrawer imageDrawer) {
        this.imageDrawer = imageDrawer;
    }

    /**
     * Evaluates and simplifies the input AST.
     *
     * Simplification involves wrapping the input AST in a simplify node.
     *
     * Evaluation involves calling the operator handler methods stored in this Interpreter to
     * replace each operator node with a new AstNode. (The aforementioned simplify node is
     * also evaluated in order to simplify the AST.)
     */
    public AstNode evaluate(AstNode node) {
        return evaluateHelper(wrapNodeWithSimplify(node));
    }

    /**
     * Recursively iterates though the input AST, applying the respective operator handler
     * functions when encountering operator nodes.
     */
    private AstNode evaluateHelper(AstNode node) {
        if (node.isNumber()) {
            // Nothing to left to do for this subtree
            return node;
        } else if (node.isVariable()) {
            // Nothing to left to do for this subtree
            return node;
        } else if (node.isOperation()) {
            String nodeName = node.getName();

            if (this.controlOperators.containsKey(nodeName)) {
                // Control operators handle evaluation of their own children,
                // so all we need to do is call their handler methods.
                ControlOperator operatorHandler = this.controlOperators.get(nodeName);
                return operatorHandler.apply(node, this.variables, this);
            } else {
                // Other operators require recursive evaluation.
                // We first evaluate the children before handing control back
                // to the operator's handler method (if one exists).

                // Create a new node with the evaluated children.
                IList<AstNode> children = new DoubleLinkedList<>();
                for (AstNode oldChild : node.getChildren()) {
                    children.add(evaluateHelper(oldChild));
                }
                AstNode output = new AstNode(node.getName(), children);

                // Apply operators handlers, if defined.
                if (this.regularOperators.containsKey(nodeName)) {
                    ExpressionOperator expressionOperatorHandler = this.regularOperators.get(nodeName);
                    output = expressionOperatorHandler.apply(output, this.variables);
                } else if (this.guiOperators.containsKey(nodeName)) {
                    GuiOperator operatorHandler = this.guiOperators.get(nodeName);
                    output = operatorHandler.apply(output, this.variables, this.imageDrawer);
                }
                return output;
            }
        } else {
            throw new AssertionError("Invalid AstNode type encountered during evaluation.");
        }
    }

    /**
     * Returns the input node wrapped in a simplify node (unless the input node is already
     * a simplify node, in which case it returns the input as is).
     */
    private static AstNode wrapNodeWithSimplify(AstNode input) {
        if (input.isOperation() && input.getName().equals("simplify")) {
            return input;
        } else {
            IList<AstNode> children = new DoubleLinkedList<>();
            children.add(input);
            return new AstNode("simplify", children);
        }
    }
}
