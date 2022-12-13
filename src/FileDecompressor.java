import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class FileDecompressor {
    File inputFile;
    File outputFile;
    BinaryTree huffmanTree;
    int n;
    int residualSize;

    public FileDecompressor(String inPath) {
        inputFile = new File(inPath);
        File parentDir = inputFile.getParentFile();
        int extensionIndex = inputFile.getName().lastIndexOf('.');
        String inNameWithoutExtension = inputFile.getName().substring(0, extensionIndex);
        String outPath = parentDir.getAbsolutePath() + "/extracted." + inNameWithoutExtension;
        outputFile = new File(outPath);
    }

    private ReadByte readBit(ReadByte readByte, BufferedInputStream inStream) throws IOException {
        if(readByte != null && readByte.hasRemainingBits()) return readByte;
        else return new ReadByte(inStream.read());
    }

    private void readHuffmanTree(BufferedInputStream inStream) throws IOException {
        n = inStream.read();
        huffmanTree = new BinaryTree(new Node());
        Queue<Node> q = new LinkedList<>();
        ReadByte readByte = null;
        q.add(huffmanTree.getRoot());
        while(!q.isEmpty()) {
            Node node = q.poll();
            readByte = readBit(readByte, inStream);
            int bit = readByte.getNextBit();
            if(bit == 0) {
                Node left = new Node();
                Node right = new Node();
                node.makeInternalNode(left, right);
                q.add(left);
                q.add(right);
            }
            else if(bit == 1) {
                int valBytes = inStream.read();
                int num = valBytes;
                long val = 0;
                while(num > 0) {
                    int currByte = inStream.read();
                    val = (val << 8) + currByte;
                    num--;
                }
                if(valBytes < n) {
                    val = -val;
                    residualSize = valBytes;
                }
                node.makeLeafNode(val);
                readByte = null;
            }
        }
    }

    private void writeFile(BufferedInputStream inStream) throws IOException {
        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        int currByte = inStream.read(), nextByte = inStream.read(), nextNextByte = inStream.read();
        Node node = huffmanTree.getRoot();
        ReadByte readByte;
        while(nextNextByte != -1) {
            readByte = new ReadByte(currByte);
            while(readByte.hasRemainingBits()) {
                if(node.isLeafNode()) {
                    long val = node.getValue();
                    int valBytes = n;
                    if(val < 0) {
                        val = -val;
                        valBytes = residualSize;
                    }
                    while(valBytes > 0) {
                        outStream.write(ReadByte.getIthByte(val, valBytes-1));
                        valBytes--;
                    }
                    node = huffmanTree.getRoot();
                }
                int bit = readByte.getNextBit();
                if(bit == 0) node = node.getLeftChild();
                else node = node.getRightChild();
            }
            currByte = nextByte;
            nextByte = nextNextByte;
            nextNextByte = inStream.read();
        }
        readByte = new ReadByte(currByte);
        while(nextByte > 0) {
            if(node.isLeafNode()) {
                long val = node.getValue();
                int valBytes = n;
                if(val < 0) {
                    val = -val;
                    valBytes = residualSize;
                }
                while(valBytes > 0) {
                    int b = ReadByte.getIthByte(val, valBytes-1);
                    outStream.write(b);
                    valBytes--;
                }
                node = huffmanTree.getRoot();
            }
            int bit = readByte.getNextBit();
            if(bit == 0) node = node.getLeftChild();
            else node = node.getRightChild();
            nextByte--;
        }
        if(node.isLeafNode()) {
            long val = node.getValue();
            int valBytes = n;
            if(val < 0) {
                val = -val;
                valBytes = residualSize;
            }
            while(valBytes > 0) {
                int b = ReadByte.getIthByte(val, valBytes-1);
                outStream.write(b);
                valBytes--;
            }
        }
        outStream.close();
    }

    public void decompress() throws IOException {
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(inputFile));
        readHuffmanTree(inStream);
        writeFile(inStream);
        inStream.close();
    }

    public static void main(String[] args) {
        String inPath = "C://Users/al-alamia/Downloads/19015478.1.Hunter.mp4.hc";
        try {
            FileDecompressor decompressor = new FileDecompressor(inPath);
            long start = System.currentTimeMillis();
            decompressor.decompress();
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        }
        catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
