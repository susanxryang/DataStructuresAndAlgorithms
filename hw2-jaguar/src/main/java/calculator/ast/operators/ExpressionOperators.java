package calculator.ast.operators;

import calculator.ast.AstNode;
import calculator.errors.EvaluationError;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

public class ExpressionOperators {

    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new number node representing a
     * simplified version of the AstNode 'inner'.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     *
     * Postconditions:
     *
     * - Returns a number AstNode containing the computed double.
     *
     * There are no other side effects for the inputs.
     *
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     * 
     * This method is required to handle the following binary operations:
     *      +, -, *, /, ^
     *  (addition, subtraction, multiplication, division, and exponentiation, respectively)
     * and the following unary operations:
     *      negate, sin, cos
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode handleToDouble(AstNode node, IDictionary<String, AstNode> variables) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the locations specified by "your code here"
        // in the 'toDoubleHelper' method.
        //
        // If you're not sure why we have a public method calling a private
        // recursive helper method, review your notes from CSE 143 (or the
        // equivalent class you took) about the 'public-private pair' pattern.

        AstNode.assertOperatorValid("toDouble", 1, node);
        AstNode exprToConvert = node.getChildren().get(0);
        return new AstNode(toDoubleHelper(exprToConvert, variables));
    }

    // This method has default (package-private) access so that it can be used in handlePlot.
    static double toDoubleHelper(AstNode node, IDictionary<String, AstNode> variables) {
        // There are three types of nodes, so we have three cases.
        if (node.isNumber()) {
            return node.getNumericValue();
        } else if (node.isVariable()) {
            if (variables.containsKey(node.getName())){
                return toDoubleHelper(variables.get(node.getName()), variables);
            } else {
                throw new EvaluationError("undefined variable");
            }
        } else {
            // You may assume the expression node has the correct number of children.
            // If you wish to make your code more robust, you can also use the provided
            // "assertNodeMatches" method to verify the input is valid.
            String name = node.getName();

            if (name.equals("+")) {
                double sumL = toDoubleHelper(node.getChildren().get(0), variables);
                double sumR = toDoubleHelper(node.getChildren().get(1), variables);
                return sumL + sumR;
            } else if (name.equals("-")) {
                double subL = toDoubleHelper(node.getChildren().get(0), variables);
                double subR = toDoubleHelper(node.getChildren().get(1), variables);
                return subL - subR;
            } else if (name.equals("*")) {
                double multiL = toDoubleHelper(node.getChildren().get(0), variables);
                double multiR = toDoubleHelper(node.getChildren().get(1), variables);
                return multiL * multiR;
            } else if (name.equals("/")) {
                double divL = toDoubleHelper(node.getChildren().get(0), variables);
                double divR = toDoubleHelper(node.getChildren().get(1), variables);
                return divL / divR;
            } else if (name.equals("^")) {
                double num = toDoubleHelper(node.getChildren().get(0), variables);
                double exp = toDoubleHelper(node.getChildren().get(1), variables);
                return Math.pow(num, exp);
            } else if (name.equals("negate")) {
                double negNum = toDoubleHelper(node.getChildren().get(0), variables);
                return (-1) * (negNum);
            } else if (name.equals("sin")) {
                double sinNum = toDoubleHelper(node.getChildren().get(0), variables);
                return Math.sin(sinNum);
            } else if (name.equals("cos")) {
                double cosNum = toDoubleHelper(node.getChildren().get(0), variables);
                return Math.cos(cosNum);
            } else {
                throw new EvaluationError("An unknown operation");
            }
        }
    }

    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing a simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     *
     * Postconditions:
     *
     * - Returns an AstNode containing the simplified inner parameter.
     *
     * There are no other side effects for the inputs.
     *
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     *
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     *
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(AstNode node, IDictionary<String, AstNode> variables) {
        // Try writing this one on your own!
        // Hint 1: Your code will likely be structured roughly similarly
        //         to your "handleToDouble" method
        // Hint 2: When you're implementing constant folding, you may want
        //         to call your "handleToDouble" method in some way
        // Hint 3: When implementing your private pair, think carefully about
        //         when you should recurse. Do you recurse after simplifying
        //         the current level? Or before?

        AstNode.assertOperatorValid("simplify", 1, node);
        AstNode exprToConvert = node.getChildren().get(0);
        return simplifyHelper(exprToConvert, variables);
    }

    private static AstNode simplifyHelper(AstNode node, IDictionary<String, AstNode> variables) {
        if (node.isNumber()){
            return node;
        } else if (node.isVariable()) { // if variable
            String name = node.getName();
            if (variables.containsKey(name)) {
                node = variables.get(name);
                return simplifyHelper(node, variables);
            } else {
                return node;
            }
        } else { // if operator
            String name = node.getName();
            IList newChildren = new DoubleLinkedList<AstNode>();
            if (node.getChildren().size() == 2 && !name.equals("/")){
                if (node.getChildren().get(0).isNumber() && node.getChildren().get(1).isNumber()){
                    // 2 children, both are number, and the expression is not division
                    return new AstNode(toDoubleHelper(node, variables));
                }

                // if not both are number, process each
                for (AstNode child : node.getChildren()){
                    newChildren.add(simplifyHelper(child, variables));
                }

                AstNode newNode = new AstNode(name, newChildren);
                if (newNode.getChildren().get(0).isNumber() && newNode.getChildren().get(1).isNumber()) {
                    return new AstNode(toDoubleHelper(newNode, variables));
                }
                return newNode;

            } else { // one children, not both are number, or the expression is division
                for (AstNode child : node.getChildren()){
                    newChildren.add(simplifyHelper(child, variables));
                }
                return new AstNode(name, newChildren);
            }
        }
    }
}
