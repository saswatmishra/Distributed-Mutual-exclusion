import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class CheckFilesSort {

	ArrayList<ArrayList<Integer>> checkSorted = new ArrayList<ArrayList<Integer>>();
	ArrayList<Integer> fileLists = new ArrayList<Integer>();
	ArrayList<Integer> listHere = new ArrayList<Integer>();

	public void checkSortedList()

	{
		String targetDirectory = System.getProperty("user.dir");
		String[] sortedArray = null;
		File directory = new File(targetDirectory);
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

	public static void main(String[] args) {
		CheckFilesSort check = new CheckFilesSort();
		check.checkSortedList();
	}

}
