package problems;

import datastructures.IntTree;
// import misc.exceptions.NotYetImplementedException;

// IntelliJ will complain that this is an unused import, but you should use IntTreeNode variables
// in your solution, and then this error should go away.
import datastructures.IntTree.IntTreeNode;

/**
 * Parts b.vi, through b.x should go here.
 *
 * (Implement depthSum, numberNodes, removeLeaves, tighten, and trim as described by the spec.
 * See the spec on the website for picture examples and more explanation!)
 *
 * Also note: you may want to use private helper methods to help you solve these problems.
 * YOU MUST MAKE THE PRIVATE HELPER METHODS STATIC, or else your code will not compile.
 * This happens for reasons that aren't the focus of this assignment and are mostly skimmed over in 142
 * and 143.  If you want to know more you can ask on Piazza or at office hours.
 *
 * REMEMBER THE FOLLOWING RESTRICTIONS:
 * - do not construct new IntTreeNode objects (though you may have as many IntTreeNode variables as you like).
 * - do not call any IntTree methods
 * - do not construct any external data structures like arrays, queues, lists, etc.
 * - do not mutate the .data field of any nodes (except for numberNodes),
 */

public class IntTreeProblems {

    public static int depthSum(IntTree tree) {
        return depthSum(tree.overallRoot, 1);
    }

    private static int depthSum(IntTreeNode root, int level){
        if (root != null){
            return level * root.data + depthSum(root.left, level + 1) + depthSum(root.right, level + 1);
        }
        return 0;
    }

    public static void removeLeaves(IntTree tree) {
        tree.overallRoot = removeLeaves(tree.overallRoot);
    }

    private static IntTreeNode removeLeaves(IntTreeNode root){
        if (root != null){
            if (root.left == null && root.right == null){
                return null;
            }
            root.left = removeLeaves(root.left);
            root.right = removeLeaves(root.right);
        }
        return root;
    }

    public static int numberNodes(IntTree tree) {
        return numberNodes(tree.overallRoot, 1);
    }

    private static int numberNodes(IntTreeNode root, int count){
        if (root == null){
            return 0;
        }
        root.data = count;
        int leftData = numberNodes(root.left, count + 1);
        int rightData = numberNodes(root.right, count + 1 + leftData);
        return 1 + leftData + rightData;
    }

    public static void tighten(IntTree tree) {
        tree.overallRoot = tighten(tree.overallRoot);
    }

    private static IntTreeNode tighten(IntTreeNode root){
        if (root != null){
            if (root.left != null && root.right == null){
                return tighten(root.left);
            } else if (root.left == null && root.right != null){
                return tighten(root.right);
            } else {
                root.left = tighten(root.left);
                root.right = tighten(root.right);
            }
        }
        return root;
    }

    public static void trim(IntTree tree, int min, int max) {
        tree.overallRoot = trim(tree.overallRoot, min, max);
    }

    private static IntTreeNode trim(IntTreeNode root, int min, int max){
        if (root != null){
            if (root.data < min) {
                return trim(root.right, min, max);
            } else if (root.data > max){
                return trim(root.left, min, max);
            } else {
                root.left = trim(root.left, min, max);
                root.right = trim(root.right, min, max);
            }
        }
        return root;
    }
}
