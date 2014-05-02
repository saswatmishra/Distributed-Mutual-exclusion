import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class CriticalSection extends Thread {

	static volatile boolean tokenHere = false;
	static volatile boolean tokenForMe = false;
	static volatile boolean inCS = false;
	static volatile int myTokenID = -1;
	static int logicalClock = 0;
	int executionTime = 0;
	static volatile int globalCounter = 0;
	File yourFile;

	static synchronized int getGlobalCounter() {
		return globalCounter;
	}

	static synchronized void incrementGlobalCounter() {
		globalCounter++;
	}

	static void setGlobalCounter(int counter) {
		globalCounter = Math.max(counter, globalCounter);
	}

	static volatile ArrayList<LinkedList<ReqID>> reqArray = new ArrayList<LinkedList<ReqID>>();

	static synchronized boolean getInCS() {
		return inCS;
	}

	static synchronized void setInCS(boolean cs) {
		inCS = cs;
		System.out.println("INCS made: " + cs);
	}

	static synchronized boolean getTokenHere() {
		return tokenHere;
	}

	static synchronized void setTokenHere(boolean th) {
		tokenHere = th;
		System.out.println("tokenHere made: " + th);
	}

	static synchronized int getMyTokenID() {
		return myTokenID;
	}

	static synchronized void setMyTokenID(int tID) {
		myTokenID = tID;
		System.out.println("MyTokenID made: " + tID);
	}

	static synchronized boolean getTokenForMe() {
		return tokenForMe;
	}

	static synchronized void setTokenForMe(boolean th) {
		tokenForMe = th;
		System.out.println("tokenForMe made: " + th);
	}

	static synchronized boolean checkDuplicate(int reqOrigin, int reqTime) {
		for (int i = 0; i < reqArray.size(); i++) {
			if (!reqArray.get(i).isEmpty()) {
				for (int j = 0; j < reqArray.get(i).size(); j++) {
					if (reqArray.get(i).get(j).reqOrigin == reqOrigin) {
						if (reqArray.get(i).get(j).reqTime >= reqTime) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	// static boolean tokenHere;
	// static boolean inCS;
	// static int logicalClock;
	// static ArrayList<LinkedList<ReqID>> reqArray;

	CriticalSection() throws IOException {
		// tokenHere=false;
		// inCS=false;
		// logicalClock=0;
		// reqArray= new ArrayList<LinkedList<ReqID>>();
		yourFile = new File("output" + MainClass.myNodeNumber + ".txt");
		if (!yourFile.exists()) {
			yourFile.createNewFile();
		}
	}

	CriticalSection(int times) throws IOException {
		executionTime = times;
		File yourFile = new File("output" + MainClass.myNodeNumber + ".txt");
		if (!yourFile.exists()) {
			yourFile.createNewFile();
		}
	}

	public void run() {
		try {
			System.out.println("In Here run()");

			for (int i = 0; i < executionTime; i++) {
				System.out.println("Execution Time:" + i);
				int randomNum = 1000 + (int) (Math.random() * 3000);
				Thread.sleep(randomNum);
				enterCS();

			}
			while (true) {
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String serializeReq() {
		String alreadySeen = "";
		for (int i = 0; i < MainClass.connectedTo.size(); i++) {
			alreadySeen = alreadySeen + MainClass.connectedTo.get(i).hostName
					+ ",";
		}
		// add itself as already seen
		alreadySeen = alreadySeen + MainClass.myNodeNumber + ",";

		alreadySeen = alreadySeen + "0,";
		// delete the last ","
		alreadySeen = alreadySeen.substring(0, alreadySeen.length() - 1);

		String req = "RR" + MainClass.myNodeNumber + ";" + logicalClock + ";"
				+ MainClass.myNodeNumber + ";" + alreadySeen;

		return req;
	}

	public static synchronized void delOldRequest(int reqO, int reqT) {
		for (int i = 0; i < reqArray.size(); i++) {
			if (!reqArray.get(i).isEmpty()) {
				for (int j = 0; j < reqArray.get(i).size(); j++) {
					if (reqArray.get(i).get(j).reqOrigin == reqO
							&& reqArray.get(i).get(j).reqTime < reqT) {
						reqArray.get(i).remove(j);
						System.out.println("Request Deleted");
					}
				}
			}
		}
	}

	static synchronized void writeAndIncrementCounter(File file)
			throws IOException {
		incrementGlobalCounter();
		FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write("" + getGlobalCounter() + ",");
		System.out.println("Global Counter:" + getGlobalCounter());
		bw.close();
		System.out.println("In Critical");
	}

	public static ArrayList<Integer> createNewAlreadySeen(
			ArrayList<Integer> alreadySeenOld) {
		ArrayList<Integer> alreadySeenNew = new ArrayList<>();
		for (int i = 0; i < alreadySeenOld.size(); i++)
			alreadySeenNew.add(alreadySeenOld.get(i));

		for (int i = 0; i < MainClass.connectedTo.size(); i++) {
			if (!alreadySeenOld.contains(MainClass.connectedTo.get(i).hostName))
				alreadySeenNew.add(MainClass.connectedTo.get(i).hostName);
		}

		return alreadySeenNew;
	}

	public static String constructNewReq(int reqOrigin, int reqTime,
			int myNodeNumber, ArrayList<Integer> alreadySeenNew) {
		String req = "RR";

		req = req + reqOrigin + ";" + reqTime + ";" + myNodeNumber + ";";
		for (int i = 0; i < alreadySeenNew.size(); i++) {
			req = req + alreadySeenNew.get(i) + ",";
		}
		req = req + "0,";
		req = req.substring(0, req.length() - 1);

		return req;
	}

	public static synchronized void manipulateLUD() {
		MainClass.lud.put(MainClass.myNodeNumber, logicalClock);
	}

	public static synchronized boolean checkLUD(int timeStamp, int Origin) {
		int ludValue = MainClass.lud.get(Origin);

		if (ludValue < timeStamp)
			return true;

		return false;
	}

	public static synchronized void addToReqArray(int sender, ReqID id) {
		reqArray.get(sender - 1).add(id);
	}

	public static synchronized void receiveRequest(String req)
			throws IOException {
		System.out.println("receiveRequest : " + req);
		req = req.replaceAll("\\n", "");
		req = req.replaceAll(" ", "");
		boolean checkTimeFlag = false;
		ArrayList<Integer> alreadySeenOld = new ArrayList<>();
		ArrayList<Integer> alreadySeenNew = new ArrayList<>();
		// Deserialization of input String
		String[] output = req.split(";");

		int reqOrigin = Integer.parseInt(output[0]);
		int reqTime = Integer.parseInt(output[1]);
		int sender = Integer.parseInt(output[2]);
		output[3] = output[3].replaceAll("\\n", "");
		output[3] = output[3].replaceAll(" ", "");
		String[] outputAlreadySeen = output[3].split(",");

		// ---- System.out.print("Already Seen:");
		// ---- for(int i=0;i<outputAlreadySeen.length-1;i++)
		// --- System.out.print(" "+outputAlreadySeen[i]);

		for (int i = 0; i < outputAlreadySeen.length - 1; i++) {
			alreadySeenOld.add(Integer.parseInt(outputAlreadySeen[i]));// (Character.getNumericValue(outputAlreadySeen[i].charAt(0)));
		}
		delOldRequest(reqOrigin, reqTime);

		checkTimeFlag = checkDuplicate(reqOrigin, reqTime);

		if (!checkTimeFlag) {
			logicalClock = Math.max(logicalClock, reqTime) + 1;
			ReqID id = new ReqID();
			id.reqOrigin = reqOrigin;
			id.reqTime = reqTime;
			System.out.println("senderId " + (sender));
			addToReqArray(sender, id);
			System.out.println("Added to reqArray:"
					+ reqArray.get(sender - 1).getLast().reqOrigin + ","
					+ reqArray.get(sender - 1).getLast().reqTime);

			alreadySeenNew = createNewAlreadySeen(alreadySeenOld);

			req = constructNewReq(reqOrigin, reqTime, MainClass.myNodeNumber,
					alreadySeenNew);

			for (int i = 0; i < MainClass.connectedTo.size(); i++) {

				if (!alreadySeenOld
						.contains(MainClass.connectedTo.get(i).hostName)) {
					SCTPClient.sendMessage(MainClass.ClientSocket
							.get(MainClass.connectedTo.get(i).hostName), req);
				}
			}
			if (getTokenHere() == true && !getInCS() && !getTokenForMe()) {
				// ----System.out.println("tokenHere: "+tokenHere
				// +" && "+"!inCS "+!inCS);
				setTokenHere(false);
				transmitToken();
			}
		}
	}

	public static synchronized void upDateLUD(String ludString) {
		// ---System.out.println("LUD String:"+ludString);

		String[] output = ludString.split(",");

		for (int i = 0; i < output.length; i++) {
			String[] result = output[i].split(":");
			MainClass.lud.put(Integer.parseInt(result[0]),
					Integer.parseInt(result[1]));
		}
	}

	public static synchronized String copyLUD(String token) {
		for (Map.Entry entry : MainClass.lud.entrySet()) {
			// --- System.out.print("key,val: ");
			// ---- System.out.println(entry.getKey() + "," + entry.getValue());
			token = token + entry.getKey() + ":" + entry.getValue() + ",";
		}
		return token;
	}

	public static synchronized void transmitTokenToDest(int elec,
			String newToken) throws IOException {
		for (int i = 0; i < reqArray.size(); i++) {
			for (int j = 0; j < reqArray.get(i).size(); j++) {
				// if(reqArray.get(i).get(j).reqOrigin==elec &&
				// getTokenHere()==true){
				if (reqArray.get(i).get(j).reqOrigin == elec) {
					SCTPClient.sendMessage(MainClass.ClientSocket.get((i + 1)),
							newToken);
					// setTokenHere(false);
				}
			}
		}
	}

	public static synchronized void removeFromReqArray(int indexI, int indexJ) {

		if ((reqArray.get(indexI).size() - 1) >= indexJ) {
			// if(reqArray.get(indexI).get(indexJ)!=null){
			System.out.println("removed "
					+ reqArray.get(indexI).get(indexJ).reqOrigin + ","
					+ reqArray.get(indexI).get(indexJ).reqTime);
			reqArray.get(indexI).remove(indexJ);
		} else {
			System.out.println("Null encountered");
		}
	}

	public static void receiveToken(String token) throws IOException,
			InterruptedException {
		// setTokenHere(true);
		System.out.println("In receiveToken(String token)");
		String[] output = token.split(";");
		String newToken = "TT";

		upDateLUD(output[0]);
		String[] output1 = output[1].split(":");
		int elec = Integer.parseInt(output1[0]);// Character.getNumericValue(output[1].charAt(0));
		setGlobalCounter(Integer.parseInt(output1[1]));

		if (elec == MainClass.myNodeNumber) {
			System.out.println("My Token");
			setTokenForMe(true);
			setTokenHere(true);
			setMyTokenID(MainClass.myNodeNumber);
			setInCS(true);// inCS=true;
			// setInCS(true);
		} else {
			System.out.println("Not my Token");
			newToken = copyLUD(newToken);
			newToken = newToken.substring(0, newToken.length() - 1);
			newToken += ";";
			newToken += elec + ":" + getGlobalCounter() + ":0";
			transmitTokenToDest(elec, newToken);
		}
	}

	public static void enterCS() throws IOException, InterruptedException {
		CriticalSection cs = new CriticalSection();
		System.out.println("enterCS()" + " INCS:" + getInCS() + " Token:"
				+ getTokenHere());
		if (getTokenHere() == true) {
			setInCS(true);// inCS=true;
			System.out.println("INCS made" + getInCS() + "using token:");
		} else {
			String req = serializeReq();
			for (int i = 0; i < MainClass.connectedTo.size(); i++) {
				SCTPClient.sendMessage(MainClass.ClientSocket
						.get(MainClass.connectedTo.get(i).hostName), req);
			}
		}

		while (getTokenHere() == false
				&& getMyTokenID() != MainClass.myNodeNumber) {
		}
		// while(getInCS()){
		// if(counter==0){
		// entering CS
		// File file = new File("output"+MainClass.myNodeNumber+".txt");
		// if (!file.exists()) {
		// file.createNewFile();
		// // }
		// writeAndIncrementCounter(file);
		// counter++;

		// System.out.println("In Critical");
		int counter = 0;
		while (getInCS() && getMyTokenID() == MainClass.myNodeNumber) {
			if (counter == 0) {
				writeAndIncrementCounter(cs.yourFile);
				counter++;
			}
		}
		System.out.println("Not in Critical Anymore");
		// exitCS();
		// }
	}

	public static synchronized ArrayList<QuadData> addToSendList(
			ArrayList<QuadData> X) {
		for (int i = 0; i < reqArray.size(); i++) {
			if (!reqArray.get(i).isEmpty()) {
				for (int j = 0; j < reqArray.get(i).size(); j++) {
					// ----
					// System.out.println("I did enter here:.."+"with reqOrigin:"+reqArray.get(i).get(j).reqOrigin+" reqTime:"+reqArray.get(i).get(j).reqTime);
					if (checkLUD(reqArray.get(i).get(j).reqTime, reqArray
							.get(i).get(j).reqOrigin)) {
						QuadData d = new QuadData(
								reqArray.get(i).get(j).reqOrigin, reqArray.get(
										i).get(j).reqTime, i, j);
						// indexI=i;
						// indexJ=j;
						X.add(d);

					}
				}
			}
		}
		return X;
	}

	public static void transmitToken() throws IOException {
		int indexI = -1;
		int indexJ = -1;
		ArrayList<QuadData> X = new ArrayList<>();
		int timeStamp = 40000;
		int elec = -1;
		String token = "TT";

		X = addToSendList(X);

		timeStamp = 40000;
		for (int i = 0; i < X.size(); i++) {
			if (X.get(i).timeStamp < timeStamp) {
				indexI = X.get(i).indexI;
				indexJ = X.get(i).indexJ;
				elec = X.get(i).elec;
				timeStamp = X.get(i).timeStamp;
			}
		}

		if (!X.isEmpty()) {
			// ----System.out.println("Removed from reArray : "+reqArray.get(indexI).get(indexJ).reqOrigin+" , "+reqArray.get(indexI).get(indexJ).reqTime);
			// setTokenHere(false);
			manipulateLUD();
			logicalClock += 1;
			removeFromReqArray(indexI, indexJ);

			// Iterator it = MainClass.lud.entrySet().iterator();
			// while (it.hasNext()) {
			// Map.Entry pairs = (Map.Entry)it.next();
			// token=token+pairs.getKey()+":"+pairs.getValue()+",";
			// it.remove(); // avoids a ConcurrentModificationException
			// }

			token = copyLUD(token);
			token = token.substring(0, token.length() - 1);
			token += ";";
			token += elec + ":" + getGlobalCounter() + ":0";
			System.out.println("IndexI:" + (indexI + 1));
			SCTPClient.sendMessage(MainClass.ClientSocket.get((indexI + 1)),
					token);
		} else {
			System.out.println("oops...index -1");
		}

	}

	public static void exitCS() throws IOException, InterruptedException {
		System.out.println("Entered exitCS()" + " INCS:" + getInCS()
				+ " Token:" + getTokenHere());
		// if(getInCS()==true){

		setInCS(false);// inCS=false;
		setTokenForMe(false);
		setTokenHere(false);
		setMyTokenID(-1);
		transmitToken();
		// }
		System.out.println("Exited exitCS()" + " INCS:" + getInCS() + " Token:"
				+ getTokenHere());
	}

}
