package calculator.ast.operators;

import calculator.ast.AstNode;
import calculator.errors.EvaluationError;
import calculator.gui.ImageDrawer;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import static calculator.ast.operators.ExpressionOperators.toDoubleHelper;
// import misc.exceptions.NotYetImplementedException;
// import static calculator.ast.operators.ExpressionOperators.*;

public class GuiOperators {
    /**
     * This function is responsible for handling the `clear()` operation node.
     *
     * It clears the plotting window when invoked.
     */
    public static AstNode handleClear(AstNode wrapper, IDictionary<String, AstNode> variables, ImageDrawer drawer) {
        AstNode.assertOperatorValid("clear", wrapper);

        drawer.getGraphics().clearRect(0, 0, drawer.getWidth(), drawer.getHeight());

        return wrapper;
    }

    /**
     * Takes as input a 'plot(exprToPlot, var, varMin, varMax, step)' AstNode,
     * the dictionary of variables, and an ImageDrawer, and generates the
     * corresponding plot on the ImageDrawer. Returns some arbitrary AstNode.
     *
     * There are no other side effects for the inputs.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the child expressions other than 'var' contains an undefined variable
     * @throws EvaluationError  if 'var' contains a defined variable or is not a variable
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'step' is zero or negative
     */

    public static AstNode handlePlot(AstNode node, IDictionary<String, AstNode> variables, ImageDrawer drawer) {
        AstNode.assertOperatorValid("plot", 5, node);
        // plot(exprToPlot, var, varMin, varMax, step)
        IList<AstNode> children = node.getChildren();
        AstNode exprToPlot = children.get(0);
        AstNode var = children.get(1);
        if (variables.containsKey(var.getName())) {
            throw new EvaluationError("the variable is defined, cannot be plotted");
        }

        double min = toDoubleHelper(children.get(2), variables);
        double max = toDoubleHelper(children.get(3), variables);
        double step = toDoubleHelper(children.get(4), variables);
        IList xInput = new DoubleLinkedList();
        IList yInput = new DoubleLinkedList();

        if (min > max){
            throw new EvaluationError("given min is greater than max");
        }
        if (step <= 0) {
            throw new EvaluationError("step must be greater than 0");
        }
        else {
            for (double i = min; i <= max; i += step){
                xInput.add(i);
                variables.put(var.getName(), new AstNode(i));
                yInput.add(toDoubleHelper(exprToPlot, variables));
            }
            variables.remove(var.getName());
            drawer.drawScatterPlot("plot", var.getName(), "output", xInput, yInput);
        }

        // return handleSimplify(node, variables);
        // Note: every single function we add MUST return an
        // AST node that your "simplify" function is capable of handling.
        // However, your "simplify" function doesn't really know what to do
        // with "plot" functions (and what is the "plot" function supposed to
        // evaluate to anyways?) so we'll settle for just returning an
        // arbitrary number.
        //
        // When working on this method, you should uncomment the following line:

        return new AstNode(1);
    }
}

