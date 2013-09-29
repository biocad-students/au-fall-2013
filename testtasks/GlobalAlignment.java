import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GlobalAlignment {
	static int GAP = -5;
	
	public static void main(String[] args) throws FileNotFoundException {
		if (args.length == 0){
			System.out.println("Enter an argument: filename of blosum");
			return;
		}

		// for test
		String s1 = "PLEASANTLY";
		String s2 = "MEANLY";
		
		/*System.out.println("Enter string");
		Scanner scanner = new Scanner(System.in);
		String s1 = scanner.next();
		System.out.println("Enter string");
		String s2 = scanner.next();
		scanner.close();*/
		
		String fpath = args[0]; // path of file with blosum62
		int[][] blosum = getBlosum(fpath);
		int[][] matrix = align(blosum, s1, s2);
		System.out.printf("Alignment score = %d\n", getScore(matrix));
		String trace = backtracing(matrix);
		System.out.println(trace);
		alignStrings(trace, s1, s2);
	}
	
	// parse blosum from file
	static int[][] getBlosum(String fpath) throws FileNotFoundException {
		Scanner fscanner = new Scanner(new File(fpath));
		fscanner.nextLine(); // line with letters
		String s;
		int[][] blosum  = new int[20][20];
		
		for (int i = 0; i < 20; i++) {
			s = fscanner.nextLine();
			String[] arr = s.split("\\s+");
			
			try{
				for (int j = 0; j < 20; j++) 
					blosum[i][j] = Integer.parseInt(arr[j+1]);
			}
			catch (IndexOutOfBoundsException ex){
				System.out.printf("Parsing: string of file %d: %s", i, ex);
				fscanner.close();
				return null;
			}
		}
		fscanner.close();
		return blosum;
	}
	
	static int[][] align(int[][] blosum, String s1, String s2) {
		int len1 = s1.length();
		int len2 = s2.length();
		int[][] matrix = new int[len1 + 1][len2 + 1]; // scoring matrix
		
		matrix[0][0] = 0;
		for (int i = 1; i < len1 + 1; i++)
			matrix[i][0] = matrix[i - 1][0] + GAP;
		for (int j = 1; j < len2 + 1; j++)
			matrix[0][j] = matrix[0][j - 1] + GAP;
		
		int[] map = {0,-1,1,2,3,4,5,6,7,-1,8,9,10,11,-1,12,13,14,15,16,-1,17,18,-1,19,-1};
		
		int a, b;
		int indel, mismatch;
		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				int ch1 = Character.codePointAt(s1, i);
				int ch2 = Character.codePointAt(s2, j);
								
				// get index of character in blosum
				if (ch1 >= 65 ) a = ch1 - 65;
				else a = ch1 - 97;
				if (ch2 >= 65 ) b = ch2 - 65;
				else b = ch2 - 97;
				a = map[a];
				b = map[b];
				
				// maximum is insert or delete?
				if (matrix[i][j + 1] > matrix[i + 1][j])
					indel = matrix[i][j + 1] + GAP;
				else 
					indel = matrix[i + 1][j] + GAP;
				
				// compare insert-delete and mismatch
				mismatch = blosum[a][b] + matrix[i][j];
				if (indel > mismatch)
					matrix[i + 1][j + 1] = indel;
				else matrix[i + 1][j + 1] = mismatch;
			}
		}
		return matrix;
	}
	
	static int getScore(int[][] matrix) {
		int len1 = matrix.length;
		int len2 = matrix[0].length;
		return matrix[len1 - 1][len2 - 1];
	}
	
	static String backtracing(int[][] matrix) {
		StringBuilder sb = new StringBuilder();
		int i = matrix.length - 1;
		int j = matrix[0].length - 1;
		while (i > 0 && j > 0) {
			if (matrix[i][j-1] > matrix[i-1][j]) {
				if (matrix[i][j-1] > matrix[i-1][j-1]) {
					sb.append("I");
					--j; // to left
				}
				else {
					sb.append("M");
					--i; // diagonally
					--j;
				}
			}
			else {
				if (matrix[i-1][j] > matrix[i-1][j-1]){
					sb.append("D");
					--i; // up
				}
				else {
					sb.append("M");
					--i; // diagonally
					--j;
				}
			}
		}
		return sb.toString();
	}
	
	static void alignStrings(String trace, String s1, String s2){
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		int c1 = 0; // counter of s1
		int c2 = 0; // counter of s2
		for (char ch : trace.toCharArray()){
			switch (ch) {
			case 'I':
				sb1.append("-"); // insert
				sb2.append(s2.charAt(c2++));
				break;
			case 'M':
				sb1.append(s1.charAt(c1++)); // match / mismatch
				sb2.append(s2.charAt(c2++));
				break;
			case 'D':
				sb1.append(s1.charAt(c1++));
				sb2.append("-"); // delete
				break;
			default:
				break;
			}
		}
		System.out.println(sb1.toString());
		System.out.println(sb2.toString());
	}
}
