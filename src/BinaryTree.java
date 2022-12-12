import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BinaryTree implements Comparable<BinaryTree> {
    private final Node root;

    public BinaryTree(Node node) {
        root = node;
    }

    public BinaryTree(BinaryTree t1, BinaryTree t2) {
        Node root1 = t1.getRoot();
        Node root2 = t2.getRoot();
        int totalFreq = root1.getFrequency() + root2.getFrequency();
        root = new Node(totalFreq, root1, root2);
    }

    public Node getRoot() {
        return root;
    }

    @Override
    public int compareTo(BinaryTree tree) {
        return Integer.compare(this.root.getFrequency(), tree.root.getFrequency());
    }

    public void printMap(String name) throws IOException {
        File file = new File("C://Users/al-alamia/Downloads/" + name);
        FileWriter writer = new FileWriter(file);
        HashMap<Long, String> map = getEncoding();
        for(Map.Entry<Long, String> entry : map.entrySet()) {
            writer.write(entry.getKey() + " -> " + entry.getValue());
        }
        writer.close();
    }

    public HashMap<Long, String> getEncoding() {
        HashMap<Long, String> encodeMap = new HashMap<>();
        dfs(root, new StringBuilder(), encodeMap);
        return encodeMap;
    }

    private void dfs(Node node, StringBuilder code, HashMap<Long, String> encodeMap) {
        if(node.isLeafNode()) encodeMap.put(node.getValue(), code.toString());
        else {
            dfs(node.getLeftChild(), code.append('0'), encodeMap);
            code.deleteCharAt(code.length()-1);
            dfs(node.getRightChild(), code.append('1'), encodeMap);
            code.deleteCharAt(code.length()-1);
        }
    }
}
