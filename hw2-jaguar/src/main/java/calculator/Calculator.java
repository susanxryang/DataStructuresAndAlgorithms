package calculator;

import calculator.ast.operators.ExpressionOperator;
import calculator.ast.AstNode;
import calculator.ast.operators.ControlOperator;
import calculator.ast.operators.ControlOperators;
import calculator.ast.operators.ExpressionOperators;
import calculator.ast.operators.GuiOperator;
import calculator.ast.operators.GuiOperators;
import calculator.gui.ImageDrawer;
import calculator.parser.Parser;
import datastructures.concrete.dictionaries.ArrayDictionary;
import datastructures.interfaces.IDictionary;

/**
 * A Calculator class that can parse and evaluate strings.
 */
public class Calculator {
    // Components used by the calculator
    private Parser parser;
    private Interpreter interpreter;

    public Calculator() {
        this(null);
    }

    public Calculator(ImageDrawer imageDrawer) {
        this.parser = new Parser();

        // Create dictionaries of handler methods for the calculator's operators
        IDictionary<String, ExpressionOperator> regularOperators = new ArrayDictionary<>();
        IDictionary<String, ControlOperator> controlOperators = new ArrayDictionary<>();
        IDictionary<String, GuiOperator> guiOperators = new ArrayDictionary<>();

        // Regular operators are operators that only have access to the Calculator's
        // variables (and may read and write to them).
        regularOperators.put("simplify", ExpressionOperators::handleSimplify);
        regularOperators.put("toDouble", ExpressionOperators::handleToDouble);

        // GUI operators also have access to the Calculator's ImageDrawer for
        // plotting capabilities
        guiOperators.put("plot", GuiOperators::handlePlot);
        guiOperators.put("clear", GuiOperators::handleClear);

        // Control operators have access to the Interpreter in addition to the
        // variables, and may use the Interpreter to control evaluation of their
        // children.
        controlOperators.put("block", ControlOperators::handleBlock);
        controlOperators.put("assign", ControlOperators::handleAssign);
        controlOperators.put("quit", ControlOperators::handleQuit);
        controlOperators.put("exit", ControlOperators::handleQuit);

        this.interpreter = new Interpreter(regularOperators, guiOperators, controlOperators);
        this.interpreter.setImageDrawer(imageDrawer);
    }

    /**
     * Sets the ImageDrawer used to draw plots.
     */
    public void setImageDrawer(ImageDrawer imageDrawer) {
        this.interpreter.setImageDrawer(imageDrawer);
    }

    /**
     * Evaluates the input string, and returns the output as a string.
     */
    public String evaluate(String input) {
        if (input.trim().equals("")) {
            return "";
        }
        // Parse the input string into an AST
        AstNode ast = this.parser.parse(input + "\n");
        // Evaluate the AST and get the output
        AstNode output = interpreter.evaluate(ast);
        // Return the output as a string
        return output.toString();
    }

}
