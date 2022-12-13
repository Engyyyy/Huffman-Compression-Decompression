import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class WeightedActivitySelector {

    private static class Activity {
        public int start;
        public int end;
        public int weight;
        
        public Activity(int s, int e, int w) {
            start = s;
            end = e;
            weight = w;
        }

        public void print() {
            System.out.println(start + " " + end + " " + weight);
        }
        
    }

    public static int selectOptimalActivities(Activity[] activities) {
        Arrays.sort(activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity a1, Activity a2) {
                if(a1.end < a2.end) return -1;
                else if(a1.end == a2.end) return 0;
                else return 1;
            }
        });

        int[] dp = new int[activities.length];
        for(int i = 0; i < activities.length; i++) dp[i] = -1;
        return selectAux(activities, activities.length-1, dp);
    }

    public static int binarySearch(int l, int r, Activity[] activities, int ans, int i) {
        if(l > r) return ans;
        int mid = (l + r) / 2;
        if(activities[mid].end > activities[i].start) return binarySearch(l, mid-1, activities, ans, i);
        else return binarySearch(mid+1, r, activities, mid, i);
    }

    public static int selectAux(Activity[] activities, int i, int[] dp) {
        if(i == -1) return 0;
        else if(dp[i] != -1) return dp[i];
        else {
            int j = binarySearch(0, i-1, activities, -1, i);
            dp[i] = Math.max(activities[i].weight + selectAux(activities, j, dp), selectAux(activities, i-1, dp));
            return dp[i];
        }
    }

    public static Activity[] ReadInput(String inputPath) throws FileNotFoundException {
        File inputFile = new File(inputPath);
        Scanner inputScanner = new Scanner(inputFile);
        int n = inputScanner.nextInt();
        Activity[] activities = new Activity[n];
        int countActivities = 0;
        for(int i = 0; i < n; i++) {
            int s = inputScanner.nextInt();
            int e = inputScanner.nextInt();
            int w = inputScanner.nextInt();
            activities[countActivities++] = new Activity(s, e, w);
        }
        return activities;
    }
    
    public static void writeOutput(int output, String inputPath) throws IOException {
        File inputFile = new File(inputPath);
        String inputName = inputFile.getName();
        int extensionIndex = inputName.lastIndexOf('.');
        if(extensionIndex != -1) inputName = inputName.substring(0, extensionIndex);
        File parentDirectory = inputFile.getParentFile();
        File outputFile = new File(parentDirectory.getAbsolutePath() + "/" + inputName + "_19015478.out");
        outputFile.createNewFile();
        FileWriter writer = new FileWriter(outputFile);
        writer.write(output + "");
        writer.close();
        System.out.println("wrote successfully to file!");
    }

    public static void main(String[] args) {
        String inputPath = "C://Users/al-alamia/Downloads/test.txt";
        try {
            Activity[] a = WeightedActivitySelector.ReadInput(inputPath);
            int maxWeight = WeightedActivitySelector.selectOptimalActivities(a);
            WeightedActivitySelector.writeOutput(maxWeight, inputPath);
        } catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }

}
