import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import com.sun.nio.sctp.SctpChannel;

public class MainClass {

	public static String myHostAddress;
	public static int myPortNumber;
	public static int myNodeNumber;
	public static Map<Integer, SctpChannel> ClientSocket = new HashMap<Integer, SctpChannel>();
	public static ArrayList<TripletData> connectedTo = new ArrayList<>();
	public static ArrayList<TripletData> nodeList = new ArrayList<>();
	public static Map<Integer, Integer> lud = new HashMap<>();

	// static int criticalCount=10;

	public static void readConnectedTo(String filePath) throws IOException {
		BufferedReader br = null;
		String sCurrentLine;
		String[] output;
		ArrayList<TripletData> result = new ArrayList<>();
		br = new BufferedReader(new FileReader(filePath));

		while (!(sCurrentLine = br.readLine()).equals("END")) {
		}
		int counter = 1;
		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println("Hello");
			output = sCurrentLine.split("\\s+");
			if (counter == myNodeNumber) {
				for (int i = 0; i < output.length; i++) {
					if (output[i].equals("1")) {
						System.out.println("Hey");
						for (int j = 0; j < nodeList.size(); j++) {
							if (nodeList.get(j).hostName == i + 1)
								connectedTo.add(nodeList.get(j));
						}
					}
				}

			}
			counter++;
		}
		br.close();
	}

	public static void readNodeList(String filePath) throws IOException {
		BufferedReader br = null;
		String sCurrentLine;
		String[] output;
		ArrayList<TripletData> result = new ArrayList<>();
		br = new BufferedReader(new FileReader(filePath));
		System.out.println("here");
		while (!(sCurrentLine = br.readLine()).equals("END")) {
			TripletData newEntry = new TripletData();
			if (sCurrentLine.equals("")) {
			} else {
				output = sCurrentLine.split("\\s+");
				if (Integer.parseInt(output[0]) == myNodeNumber) {
					myHostAddress = output[1];
					myPortNumber = Integer.parseInt(output[2]);
					System.out.println("Hostname:" + myHostAddress + ";"
							+ "myPortNumber:" + myPortNumber);
					// lud.put(myNodeNumber,-1);
				} else {
					newEntry.hostName = Integer.parseInt(output[0]);
					newEntry.ipAddress = output[1];
					newEntry.portNo = Integer.parseInt(output[2]);
					nodeList.add(newEntry);
					// lud.put(newEntry.hostName,-1);

				}
			}
		}
		br.close();

	}

	public static void main(String args[]) throws IOException, Exception {

		System.out.println("Enter Node Number");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));
		myNodeNumber = Integer.parseInt(bufferRead.readLine());
		System.out.println("myNodeNumber:" + myNodeNumber);
		readNodeList("topo2.txt");
		readConnectedTo("topo2.txt");
		for (int i = 0; i < MainClass.connectedTo.size(); i++) {
			System.out.println("Connected To : "
					+ MainClass.connectedTo.get(i).hostName + ","
					+ MainClass.connectedTo.get(i).ipAddress + ","
					+ MainClass.connectedTo.get(i).portNo);
		}
		for (int i = 0; i < MainClass.nodeList.size(); i++) {
			System.out.println("Node List : "
					+ MainClass.nodeList.get(i).hostName + ","
					+ MainClass.nodeList.get(i).ipAddress + ","
					+ MainClass.nodeList.get(i).portNo);
		}

		if (MainClass.myNodeNumber == 1)
			CriticalSection.setTokenHere(true);

		for (int i = 0; i <= MainClass.nodeList.size(); i++) {
			System.out.println("Initialized");
			LinkedList<ReqID> d = new LinkedList<>();
			CriticalSection.reqArray.add(i, d);
		}
		Thread server = new SCTPServer();
		server.setName("Server Thread");
		server.start();

		Thread.sleep(10000);
		Thread client = new SCTPClient();
		client.start();
		// Thread cs= new CriticalSection(criticalCount);
		Thread.sleep(10000);
		// for(int i=0;i<criticalCount;i++){
		// int randomNum = 500 + (int)(Math.random()*10000);
		// Thread.sleep(randomNum);
		// CriticalSection.enterCS();
		// }

		// cs.start();

		// for(int i=0;i<criticalCount;i++){
		// int randomNum = 500 + (int)(Math.random()*10000);
		// Thread.sleep(randomNum);

		// for(int i=0;i<criticalCount;i++){
		// int randomNum = 1000 + (int)(Math.random()*3000);
		// while(!CriticalSection.getInCS()){
		//
		// }
		// Thread.sleep(randomNum);
		// CriticalSection.exitCS();
		//
		// }

		// }

		Thread.sleep(600000);

		// if(myNodeNumber==1){
		// Thread.sleep(30000);
		// CriticalSection.exitCS();
		// }
		// else
		// CriticalSection.exitCS();

		System.out.println("Waiting in main");
		// cs.join();
		// System.out.println("Waiting in main after cs.join()");

		server.join();
		client.join();
		System.out.println("Out OF MAIN");
	}

}
