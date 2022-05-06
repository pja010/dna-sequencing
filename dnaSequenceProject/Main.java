import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Stream;

//TODO if time - refactoring
class Main {

    public static Hashtable<String, String> data = new Hashtable<String, String>();

    /**
     * The user menu interface that mainly drives the program
     */
    private static void userMenu() {

        Scanner sc = new Scanner(System.in);
        String fileSequences = "DNA_sequences.txt";
        String fileQuery = "DNA_query.txt";
        Hashtable sampleData;
        String targetData;
        
          // try {
            System.out.println("Enter the name of the file with the query: ");
            fileQuery = sc.nextLine();

            System.out.println("Enter the name of the file with the sequences to be searched: ");
            fileSequences = sc.nextLine();
            sampleData = readFile(fileSequences);
            targetData = readFile_o(fileQuery);
          // } catch (IOException e) {
          //   System.out.println("Enter a valid file.");
          // }  
          
        System.out.println("**********************");
        System.out.println("1. Longest subsequence\n" +
                "2. Longest substring\n" +
                "3. Edit distance\n" +
                "4. Needleman Wunsch Algorithm\n");
     
        int i = 0 ;
        
        while (i >= 0 && i <= 4) {
          
          System.out.println("Input the number of the algorithm you want to use, or \"-1\" to exit: ");

          try {
            i = Integer.parseInt(sc.nextLine());
             FindMostSimilarSeq(targetData, sampleData, i);
          } catch (NumberFormatException e) {
            System.out.println("Enter a valid integer.");
          }  
                    
        }
        System.out.println("Exiting");
        return;
    }


  // "Top-level" function that compares input string with table of strings to compute similarity
    private static void FindMostSimilarSeq(String t, Hashtable<String, String> map, int algorithmChoice) {

        int bestSimilarity = 0;

        if (algorithmChoice  == 3) {
          bestSimilarity = Integer.MAX_VALUE;
        }

        int similarity = 0;
        String bestSequence = "";

        String longestCommonSS = "";
        String bestSS = "";
        String seeString = "";
        
        for (Map.Entry<String, String> entry : map.entrySet()) {

          String sequence = entry.getValue();
    
          switch (algorithmChoice) {
            case 1:
              longestCommonSS = longestCommonSubSeq(sequence, t);
              similarity = longestCommonSS.length();

              if (similarity > bestSimilarity) {
              bestSimilarity = similarity;
              bestSequence = entry.getKey();
              bestSS = longestCommonSS;
              }
              break;
            case 2:
              longestCommonSS = longestCommonSubString(sequence, t);
              similarity = longestCommonSS.length();
              
              if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestSequence = entry.getKey();
                bestSS = longestCommonSS;
              }
              break;
              
            case 3:
              similarity = EditDistance(sequence, t);
              if (similarity < bestSimilarity) {
                bestSimilarity = similarity;
                bestSequence = entry.getKey();
              }
              break;
            case 4:
              similarity = NW_algorithm(sequence, t);
              if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestSequence = entry.getKey();
              }
              break;

            default:
              return;
          }
        }


  System.out.println("Best matching sequence: " + bestSequence);

  if (algorithmChoice != 3) {     
    System.out.println("Similarity score: " + bestSimilarity);
  } else {
    System.out.println("Edit distance score: " + bestSimilarity);
  }
  
  

    Scanner sc = new Scanner(System.in);
    switch (algorithmChoice) {
      case 1:
        System.out.println("Would you like to see the longest common subsequence? [y/n]");
        seeString = sc.nextLine();
        if (seeString.equals("y")) {
            System.out.println("The longest common subsequence is " + bestSS);
          } 
        break;
      case 2:
        System.out.println("Would you like to see the longest common substring? [y/n]");
        seeString = sc.nextLine();
        if (seeString.equals("y")) {
         System.out.println("The longest common substring is " + bestSS);
       }
       break; 
  }
  }


/**
*ALGORITHM 4
*/
  public static int NW_algorithm(String a, String b) {
        /* length of the first string */
        int m = a.length();
        /* length of the second string */
        int n = b.length();
        /* the grid used for scoring */
        int[][] grid = new int[m+2][n+2];
        /* match value, could be 1 or -1 */
        int match;
        /* indel value, which represents two gap penalties from the top or the left */
        int left_indel;
        int right_indel;
        /* contains the max for match, and two indel values */
        int score;

        /* Initializing the grid */
        grid[0][0] = 0;
        for (int i = 1; i < m+2; i++) grid[i][0] = -i;
        for (int j = 1; j < n+2; j++) grid[0][j] = -j;
        /**
         * Going through the grid that if there is a match, add one point; minus one point for a mismatch
         * Also minus one point for indel
         */
        for (int i = 2; i < m+2; i++) {
            for (int j = 2; j < n+2; j++) {
                if (Character.toLowerCase(a.charAt(i - 2)) == Character.toLowerCase(b.charAt(j - 2))) match = grid[i-1][j-1] + 1;
                else match = grid[i-1][j-1] - 1;
                left_indel = grid[i-1][j] - 1;
                right_indel = grid[i][j-1] - 1;
                score = Stream.of(match, left_indel, right_indel).max(Comparator.comparing(d->d)).get();
                grid[i][j] = score;
            }
        }
        return grid[m+1][n+1];
    }


/*
ALGORITHM 3
Implementation based on the Wagner–Fischer algorithm to find the "edit distance" between two strings by using a matrix to compute the distances between the prefixes of the first and second string
**/
  static int EditDistance(String s1, String s2) {
    
    int substitutionCost; 
    int[][] charDistance;

    charDistance = new int[s1.length()+1][s2.length()+1];
  
    for (int i = 1; i <= s1.length(); i++) {
      charDistance[i][0] = i;
    }  

    for (int j = 1; j <= s2.length(); j++) {
        charDistance[0][j] = j; 
    }

    for (int j = 1; j < s2.length(); j++) {
      for (int i = 1; i< s1.length(); i++) {

        if ( s1.charAt(i) == s2.charAt(j) )
          substitutionCost = 0;
        else {
          substitutionCost = 1;
        }

        // take min cost of delation, insertion and substitution
        charDistance[i][j] = MinOf3(
          charDistance[i-1][j] + 1,                 
          charDistance[i][j-1] + 1,                   
          charDistance[i-1][j-1] + substitutionCost 
        );
    
      }

    }

    // return charDistance[s1.length()][s2.length()];
    // return charDistance[s1.length()-1][s2.length()-1];
    if (s1.length() == 0 || s2.length() == 0) 
      return charDistance[s1.length()][s2.length()];
    else
    return charDistance[s1.length()-1][s2.length()-1];
  }

 
  private static int MinOf3(int a, int b, int c) {
    
    if (a <= b && a <= c)
      return a;
    else if (b <= a && b <= c)
      return b;
    else
      return c; 
  }

    /**
     * This function reads data from the file that is used as target to be compared to 
     * 
     * @param fileName - the file name of the file
     * @return - data in the file as a string
     */
    private static String readFile_o(String fileName) {
        StringBuilder sb = null;
        try (BufferedReader rd = new BufferedReader(new FileReader(fileName))) {
            sb = new StringBuilder();
            String line = rd.readLine();

            while ((line != null) && (line.length() > 0)) {
                line = line.toUpperCase();
                sb.append(line);
                line = rd.readLine();
            }
        } catch (IOException e) {
          System.out.println("Enter a valid file.");
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * This function reads data from the file that contains the data sets used to compare the DNAs
     *
     * @param fileName - is the file name of the file
     * @return a hashtable that contains the sets of data in the file
     */
    private static Hashtable readFile(String fileName) {
        try (BufferedReader rd = new BufferedReader(new FileReader(fileName))
        ) {
            StringBuilder sb = new StringBuilder();
            String temp = "";
            String line = rd.readLine();

            while ((line != null) && (line.length() > 0)) {
                if (line.charAt(0) == '>') {
                    if (sb.length() == 0) { // if it's a new entry
                        line.toUpperCase();
                        data.put(line, "");
                        temp = line;
                    } else {
                        data.replace(temp, sb.toString());
                        temp = line;
                        data.put(temp, "");
                        sb.setLength(0);
                    }
                } else {
                    sb.append(line);
                }
                line = rd.readLine();
            }
        } catch (IOException e) {
                    System.out.println("Enter a valid file.");

            e.printStackTrace();
        }
        System.out.println("Hashtable readin successful");
        return data;
    }

  /** 
  * ALGORITHM 1
  */
  public static String longestCommonSubSeq(String X, String Y) {
    int Xlen = X.length();
    int Ylen = Y.length();
    int[][] c = new int[Xlen + 1][Ylen + 1];
    char[][] b = new char[Xlen + 1][Ylen + 1];
    String longestSS = new String();

    for (int i = 1; i <= Xlen; i++) { // i = 1 to Xlen
      for (int j = 1; j <= Ylen; j++) { // i = 1 to Ylen
        if (X.charAt(i - 1) == Y.charAt(j - 1)) { // if Xi == Yj --> use charAt i - 1 because string is 0 indexed and array is 1 indexed
          c[i][j] = c[i-1][j-1] + 1; 
          b[i][j] = 'd'; // depends on the cell to the upper left
        } else if (c[i-1][j] > c[i][j-1]) { //compare to cell above to cell to the left
          c[i][j] = c[i-1][j];
          b[i][j] = 'l'; // depends on the cell to the left
        }
        else {
          c[i][j] = c[i][j-1];
          b[i][j] = 'u'; // depends on the cell above
        }
        //System.out.print(c[i][j] + " ");
      }
      //System.out.println();
    }
    //go back through to return the longest common subsequence
    int i = Xlen;
    int j = Ylen;
    while((i >= 0) && (j >= 0)) {
      if (b[i][j] == 'd') {
        longestSS = String.valueOf(X.charAt(i - 1)) + longestSS;
        i--;
        j--;
      } else if (b[i][j] == 'l') {
        i--;
      } else { // b[i][j] == 'u'
        j--;
      }
    }

    return longestSS;
  }

  /** 
  * ALGORITHM 2
  */
  public static String longestCommonSubString(String X, String Y) {
    int Xlen = X.length();
    int Ylen = Y.length();
    int[][] c = new int[Xlen + 1][Ylen + 1];

    int max = 0; //  max substring length
    int maxi = 0; // max substring location in table
    int maxj = 0;

    for (int i = 1; i <= Xlen; i++) { // i = 1 to Xlen
      for (int j = 1; j <= Ylen; j++) { // i = 1 to Ylen
        if ((X.charAt(i - 1)) == Y.charAt(j - 1)) { // if Xi == Yj --> use charAt i - 1 because string is 0 indexed and array is 1 indexed
          c[i][j] = c[i-1][j-1] + 1; 
          if (c[i][j] > max) {
              max = c[i][j];
              maxi = i;
              maxj = j;
          }
        }
        else {
          c[i][j] = 0;
        }
      }
    }
    //go find the longest sub string's last letter
    int i = maxi;
    int j = maxj;
    String longestSS = new String();

    // go back up the diagonal until the string is complete
    while(longestSS.length() < max) {
      longestSS = String.valueOf(X.charAt(i - 1)) + longestSS;
      i--;
      j--;
    }

    return longestSS;
  }

  public static void main(String[] args) {
    userMenu();
  }

}