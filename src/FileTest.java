import java.beans.FeatureDescriptor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FileTest {
	ArrayList<Integer> finalList = new ArrayList<Integer>();
	ArrayList<Integer> list = new ArrayList<Integer>();
	ArrayList<Integer> listFile1 = new ArrayList<Integer>();
	Set<Integer> duplicateSet = new HashSet<Integer>();

	public FileTest() {

	}

	public void createFile(String enterNoOfCS) {

		int userInput = Integer.parseInt(enterNoOfCS);
		String targetDirectory = System.getProperty("user.dir");

		// String targetDirectory = "C:\\Users\\Saswat\\Desktop\\AOSInput";

		File directory = new File(targetDirectory);
		File[] files = directory.listFiles();
		int fileSize = files.length;
		// System.out.println(fileSize);
		int correct = userInput * fileSize;
		int count = 0;
		for (File f : files) {

			/*
			 * if( (f.getName().equals("result.txt")) ||
			 * f.getName().equals("FileTest.java") ||
			 * f.getName().equals("FileTest.class")) {
			 * //System.out.println("file name  " + f.getName()); continue; }
			 */
			if (!f.getName().contains("output"))
				continue;

			count++;
			if (f.isFile()) {
				BufferedReader buffer = null;
				try {
					buffer = new BufferedReader(new FileReader(f));
					String line = buffer.readLine() + ",";
					String[] array = line.split(",");
					for (String values : array) {
						list.add(Integer.parseInt(values));

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		correct = userInput * count;
		/*
		 * for (Integer w : listFile1) { System.out.println(w); }
		 */

		finalList.addAll(list);
		finalList.addAll(listFile1);
		Collections.sort(finalList);
		duplicateSet = new HashSet<Integer>(finalList);
		int numDuplicates = finalList.size() - duplicateSet.size();
		// System.out.println(numDuplicates);
		if (numDuplicates != 0) {
			System.out.println("Duplicates are there");
		}

		int size = finalList.size();

		System.out.println("Size : " + size + " Correct : " + correct);
		if (size == correct && numDuplicates == 0) {
			System.out.println("Successfully done");
		} else if (size < correct) {
			System.out.println("Number of Critical Section is less than size");
		} else if (size > correct) {
			System.out
					.println("Number of Critical Section is greater than size");
		} else if (size == correct) {
			System.out.println("Number of Critical Section is equal to size");
		}

	}

	public void writeToFile() {

		File file1 = new File("result.txt");
		File file2 = null;
		try {
			if (file1.exists()) {
				file1.delete();
				file2 = new File("result.txt");
				// System.out.println("Making a new file");
				file2.createNewFile();
			} else {
				file2 = new File("result.txt");
				// System.out.println("Making a new file");
				file2.createNewFile();
			}
			PrintWriter printWriter = new PrintWriter(new FileWriter(file2,
					false));
			for (int i = 0; i < finalList.size(); i++) {
				printWriter.write(finalList.get(i) + ",");

			}
			printWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void checkSortedList()

	{
		String targetDirectory1 = System.getProperty("user.dir");
		String[] sortedArray = null;
		File directory = new File(targetDirectory1);
		File[] files = directory.listFiles();
		int fileSize = files.length;
		for (int i = 0; i < fileSize; i++) {

			BufferedReader buffer1 = null;
			try {
				buffer1 = new BufferedReader(new FileReader(files[i]));
				String line1 = buffer1.readLine() + ",";
				sortedArray = line1.split(",");
				int comp = 0;
				boolean sorted = false;
				for (int p = 0; p < sortedArray.length - 1; p++) {
					comp = Integer.parseInt(sortedArray[p]);
					if (comp > (Integer.parseInt(sortedArray[p + 1]))) {
						sorted = false;
						System.out.println("Not in sorted Order");
						break;
					} else {
						sorted = true;
						// System.out.println("File already in order");
					}

				}
				if (sorted) {
					System.out.println("File already in order");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println(listHere.get(0));
		}
	}

	public static void main(String args[]) {

		FileTest filetest = new FileTest();
		if (!(args.length == 0)) {
			filetest.createFile(args[0]);
		}
		filetest.writeToFile();
		filetest.checkSortedList();
	}

}
