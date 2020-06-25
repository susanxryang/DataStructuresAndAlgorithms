package calculator.ast.operators;

import calculator.ast.AstNode;
import calculator.gui.ImageDrawer;
import datastructures.interfaces.IDictionary;

/**
 * An interface for defining handlers for operator nodes that interact with the GUI.
 *
 * The handler method has access to the Calculator's ImageDrawer in addition to its
 * dictionary of variables.
 */
@FunctionalInterface
public interface GuiOperator {
    AstNode apply(AstNode node, IDictionary<String, AstNode> variables, ImageDrawer imageDrawer);
}
