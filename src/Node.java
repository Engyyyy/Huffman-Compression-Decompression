public class Node {
    private int frequency;
    private long value = -1;
    private boolean leaf;
    private Node leftChild;
    private Node rightChild;

    public Node(int freq, long val) {
        frequency = freq;
        value = val;
        leaf = true;
        leftChild = null;
        rightChild = null;
    }

    public Node(int freq, Node left, Node right) {
        frequency = freq;
        leaf = false;
        leftChild = left;
        rightChild = right;
    }

    public Node() {

    }

    public int getFrequency() {
        return frequency;
    }

    public boolean isLeafNode() {
        return leaf;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public long getValue() {
        return value;
    }
    public void makeInternalNode(Node left, Node right) {
        leaf = false;
        leftChild = left;
        rightChild = right;
    }
    public void makeLeafNode(long val) {
        leaf = true;
        leftChild = null;
        rightChild = null;
        value = val;
    }
}
