import java.io.*;
import java.util.*;

public class FileCompressor {
    public File inputFile;
    public File outputFile;
    int n;
    public HashMap<Long, Integer> freqMap;
    private long residualVal;
    private int residualSize;
    BinaryTree huffmanTree;

    public FileCompressor(String inPath, int n) {
        inputFile = new File(inPath);
        this.n = n;
        File parentDir = inputFile.getParentFile();
        String outPath = parentDir.getAbsolutePath() + "/19015478." + n + "." + inputFile.getName() + ".hc";
        outputFile = new File(outPath);
        freqMap = new HashMap<>();
    }

    private void processFile() throws IOException {
        long nBytes = 0;
        int count = 0;
        int currByte;
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(inputFile));
        while((currByte = inStream.read()) != -1) {
            nBytes = (nBytes << 8) + currByte;
            count++;
            if(count == n) {
                if(freqMap.containsKey(nBytes)) freqMap.put(nBytes, freqMap.get(nBytes) + 1);
                else freqMap.put(nBytes, 1);
                nBytes = 0;
                count = 0;
            }
        }
        residualSize = count;
        residualVal = nBytes;
        inStream.close();
    }

    private void generateHuffmanTree() {
        PriorityQueue<BinaryTree> minHeap = new PriorityQueue<>();
        for(long key : freqMap.keySet()) {
            Node root = new Node(freqMap.get(key), key);
            minHeap.add(new BinaryTree(root));
        }
        if(residualSize > 0) minHeap.add(new BinaryTree(new Node(1, -residualVal)));
        while(minHeap.size() > 1) {
            BinaryTree min1 = minHeap.remove();
            BinaryTree min2 = minHeap.remove();
            minHeap.add(new BinaryTree(min1, min2));
        }
        huffmanTree = minHeap.peek();
    }

    private void writeHeader(BufferedOutputStream outStream) throws IOException {
        outStream.write(n);
        Queue<Node> q = new LinkedList<>();
        q.add(huffmanTree.getRoot());
        int count = 0;
        while(!q.isEmpty()) {
            Node node = q.poll();
            if(node.isLeafNode()) {
                outStream.write(1 << (7 - count));
                count = 0;
                int valBytes;
                long val;
                if(node.getValue() < 0) {
                    valBytes = residualSize;
                    val = residualVal;
                }
                else {
                    valBytes = n;
                    val = node.getValue();
                }
                outStream.write(valBytes);
                while(valBytes > 0) {
                    outStream.write(ReadByte.getIthByte(val, valBytes-1));
                    valBytes--;
                }
            }
            else {
                count++;
                if(count == 8) {
                    outStream.write(0);
                    count = 0;
                }
                q.add(node.getLeftChild());
                q.add(node.getRightChild());
            }
        }
    }

    private void writeFile(BufferedOutputStream outStream) throws IOException {
        BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(inputFile));
        HashMap<Long, String> codeMap = huffmanTree.getEncoding();
        int currByte;
        long nBytes = 0;
        int count_read = 0;
        int byteToWrite = 0;
        int count_write = 0;
        while((currByte = inStream.read()) != -1) {
            nBytes = (nBytes << 8) + currByte;
            count_read++;
            if(count_read == n) {
                String code = codeMap.get(nBytes);
                for(int i = 0; i < code.length(); i++) {
                    if(code.charAt(i) == '0') byteToWrite = (byteToWrite << 1);
                    else byteToWrite = (byteToWrite << 1) + 1;
                    count_write++;
                    if(count_write == 8) {
                        outStream.write(byteToWrite);
                        byteToWrite = 0;
                        count_write = 0;
                    }
                }
                nBytes = 0;
                count_read = 0;
            }
        }
        inStream.close();
        if(residualSize > 0) {
            String code = codeMap.get(-residualVal);
            for(int i = 0; i < code.length(); i++) {
                if(code.charAt(i) == '0') byteToWrite = (byteToWrite << 1);
                else byteToWrite = (byteToWrite << 1) + 1;
                count_write++;
                if(count_write == 8) {
                    outStream.write(byteToWrite);
                    byteToWrite = 0;
                    count_write = 0;
                }
            }
        }
        if(count_write > 0) {
            byteToWrite = (byteToWrite << (8 - count_write));
            outStream.write(byteToWrite);
        }
        outStream.write(count_write);
    }

    public void compress() throws IOException {
        BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(outputFile));
        processFile();
        generateHuffmanTree();
        writeHeader(outStream);
        writeFile(outStream);
        outStream.close();
    }

    public static void main(String[] args) {
        String inPath = "C://Users/al-alamia/Downloads/Hunter.mp4";
        int n = 1;
        try {
            FileCompressor compressor = new FileCompressor(inPath, n);
            long start = System.currentTimeMillis();
            compressor.compress();
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        }
        catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
