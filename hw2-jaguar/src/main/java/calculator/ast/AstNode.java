package calculator.ast;

import calculator.errors.EvaluationError;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.ReadOnlyList;
import datastructures.concrete.dictionaries.ArrayDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

import java.util.Iterator;

/**
 * Represents a single node in an abstract syntax tree (AST).
 *
 * See spec for more details on what an AST is. Note that nodes are immutable, so you have to
 * create a new node to change the AST.
 */
public final class AstNode {
    private static final int STRONGEST_PRECEDENCE = 0;
    private static final int WEAKEST_PRECEDENCE = Integer.MAX_VALUE;
    private static IDictionary<String, Integer> precedenceMap;

    // This code runs once when the class is loaded to initialize the precedenceMap.
    static {
        precedenceMap = new ArrayDictionary<>();
        precedenceMap.put("^", 1);
        precedenceMap.put("negate", 2);
        precedenceMap.put("*", 3);
        precedenceMap.put("/", 3);
        precedenceMap.put("+", 4);
        precedenceMap.put("-", 4);
    }

    private final String name;
    private final IList<AstNode> children;
    private final ExprType type;

    /**
     * Creates a leaf node representing a single number.
     */
    public AstNode(double number) {
        this("" + number, new DoubleLinkedList<>(), ExprType.NUMBER);
    }

    /**
     * Creates a leaf node representing a variable.
     */
    public AstNode(String name) {
        this(name, new DoubleLinkedList<>(), ExprType.VARIABLE);
    }

    /**
     * Creates a node representing some operation.
     *
     * An "operation" is any kind of node that represents a function call or
     * combines one or more AST nodes.
     *
     * For example, the expression "3 + 2" can be represented as the '+'
     * operation with two children:
     *
     * new AstNode("+", [new AstNode(3.0), new AstNode(2.0)])
     *
     * As another example, the expression "sin(x)" can be represented as the
     * 'sin' operation with one child:
     *
     * new AstNode("sin", [new AstNode("x")])
     *
     * Note that the list of children may be empty: this represents calling a
     * function with no arguments.
     */
    public AstNode(String name, IList<AstNode> children) {
        this(name, children, ExprType.OPERATION);
    }

    private AstNode(String name, IList<AstNode> children, ExprType type) {
        this.name = name;
        this.children = new ReadOnlyList<>(children);
        this.type = type;
    }

    /**
     * Checks to make sure that the given node is an operation AstNode with the expected
     * name and number of children. Throws an EvaluationError otherwise.
     */
    public static void assertOperatorValid(String name, int numChildren, AstNode node) {
        boolean isValid = node.isOperation()
                && node.getName().equals(name)
                && node.getChildren().size() == numChildren;
        if (!isValid) {
            String msg = String.format(
                    "Node ('%s' w/ %d children) does not match expected ('%s' w/ %d children)",
                    node.getName(),
                    node.getChildren().size(),
                    name,
                    numChildren);

            throw new EvaluationError(msg);
        }
    }

    /**
     * Checks to make sure that the given node is an operation AstNode with the expected
     * name. Throws an EvaluationError otherwise. (Ignores number of children.)
     */
    public static void assertOperatorValid(String name, AstNode node) {
        boolean isValid = node.isOperation()
                && node.getName().equals(name);
        if (!isValid) {
            String msg = String.format(
                    "Node ('%s') does not match expected ('%s')",
                    node.getName(),
                    name);

            throw new EvaluationError(msg);
        }
    }

    /**
     * Returns 'true' if this node represents a number, and 'false' otherwise.
     */
    public boolean isNumber() {
        return this.type == ExprType.NUMBER;
    }

    /**
     * Returns 'true' if this node represents a variable, and 'false' otherwise.
     */
    public boolean isVariable() {
        return this.type == ExprType.VARIABLE;
    }

    /**
     * Returns 'true' if this node represents an operation or function call,
     * and 'false' otherwise.
     */
    public boolean isOperation() {
        return this.type == ExprType.OPERATION;
    }

    /**
     * Returns the variable or operation name.
     *
     * @throws EvaluationError if this node is a number
     */
    public String getName() {
        if (this.isNumber()) {
            throw new EvaluationError("Attempted to call 'getName()' on a number AstNode");
        }
        return this.name;
    }

    /**
     * Returns the numeric value of this node.
     *
     * @throws EvaluationError if this node does not represent a number
     */
    public double getNumericValue() {
        if (!this.isNumber()) {
            throw new EvaluationError("Attempted to call 'getNumericValue()' on a variable or operation AstNode");
        }
        return Double.parseDouble(this.name);
    }

    @Override
    public String toString() {
        return this.toString(WEAKEST_PRECEDENCE);
    }

    private String toString(int parentPrecedenceLevel) {
        if (this.isNumber()) {
            double val = this.getNumericValue();
            if (val == (long) val) {
                return String.format("%d", (long) val);
            } else {
                return String.format("%s", val);
            }
        } else if (this.isVariable()) {
            return this.getName();
        } else {
            boolean hasPrecedence = precedenceMap.containsKey(this.name);
            int currPrecedenceLevel = hasPrecedence ? precedenceMap.get(this.name) : STRONGEST_PRECEDENCE;
            int childPrecedenceLevel = hasPrecedence ? currPrecedenceLevel : WEAKEST_PRECEDENCE;

            IList<String> childrenStrings = new DoubleLinkedList<>();
            for (AstNode child : this.getChildren()) {
                childrenStrings.add(child.toString(childPrecedenceLevel));
            }

            String out;
            if ("-+*/^".contains(this.name)) {
                out = this.join(" " + this.name + " ", childrenStrings);
            } else if ("negate".equals(this.name)) {
                out = "-" + childrenStrings.get(0);
            } else {
                out = this.name + "(" + this.join(", ", childrenStrings) + ")";
            }

            if (currPrecedenceLevel > parentPrecedenceLevel) {
                out = "(" + out + ")";
            }

            return out;
        }
    }

    private String join(String connector, IList<String> items) {
        String out = "";
        if (!items.isEmpty()) {
            Iterator<String> iter = items.iterator();
            out = iter.next();
            while (iter.hasNext()) {
                out += connector + iter.next();
            }
        }
        return out;
    }

    /**
     * Returns this node's children.
     *
     * The list of children is read-only. To change the children, create a new node instead. If
     * this node represents a number or variable, the returned list is guaranteed to be empty.
     */
    public IList<AstNode> getChildren() {
        return this.children;
    }

    private enum ExprType {
        NUMBER,
        VARIABLE,
        OPERATION,
    }
}
