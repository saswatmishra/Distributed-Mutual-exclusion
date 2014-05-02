import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;

public class SCTPClient extends Thread {

	public static boolean runningClient = true;
	public int sendingExitMessage = 7;

	// private SctpChannel[] ClientSock=new SctpChannel[10];

	public static void sendMessage(SctpChannel clientSock, String Message)
			throws IOException {
		// System.out.println("Hey Im inside Send Messgae");
		// prepare byte buffer to send massage
		ByteBuffer sendBuffer = ByteBuffer.allocate(512);
		sendBuffer.clear();
		// Reset a pointer to point to the start of buffer
		sendBuffer.put(Message.getBytes());
		sendBuffer.flip();
		System.out.println("Sending to... " + clientSock.getRemoteAddresses()
				+ " " + Message);
		try {
			// Send a message in the channel
			MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
			clientSock.send(sendBuffer, messageInfo);
			sendBuffer.clear();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void sendMessage2(SctpChannel clientSock, String Message)
			throws IOException {
		// System.out.println("Hey Im inside Send Messgae");
		// prepare byte buffer to send massage
		ByteBuffer sendBuffer = ByteBuffer.allocate(512);
		sendBuffer.clear();
		// Reset a pointer to point to the start of buffer
		sendBuffer.put(Message.getBytes());
		sendBuffer.flip();
		System.out.println("Sending2 to... " + clientSock.getRemoteAddresses()
				+ " " + Message);
		try {
			// Send a message in the channel
			MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
			clientSock.send(sendBuffer, messageInfo);
			sendBuffer.clear();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void receiveMessage(SctpChannel clientSock) {
		ByteBuffer byteBuffer;
		byteBuffer = ByteBuffer.allocate(512);
		try {
			MessageInfo messageInfo = clientSock
					.receive(byteBuffer, null, null);
			String message = byteToString(byteBuffer);
			// System.out.println("Received Message from Server:");
			// System.out.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String byteToString(ByteBuffer byteBuffer) {
		byteBuffer.position(0);
		byteBuffer.limit(512);
		byte[] bufArr = new byte[byteBuffer.remaining()];
		byteBuffer.get(bufArr);
		return new String(bufArr);
	}

	public void run() {
		try {
			for (int i = 0; i < MainClass.connectedTo.size(); i++) {
				System.out.println("In Client");
				InetSocketAddress serverAddr = new InetSocketAddress(
						MainClass.connectedTo.get(i).ipAddress,
						MainClass.connectedTo.get(i).portNo);
				MainClass.ClientSocket.put(
						MainClass.connectedTo.get(i).hostName,
						SctpChannel.open());
				MainClass.ClientSocket.get(
						MainClass.connectedTo.get(i).hostName).connect(
						serverAddr, 0, 0);
				String message = "Hello From Client with node number:"
						+ MainClass.connectedTo.get(i).hostName;
				sendMessage(MainClass.ClientSocket.get(MainClass.connectedTo
						.get(i).hostName), message);

			}
			while (true) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// while(runningClient){
		// VectorClockClass.globalCounter++;
		// for(int i=0;i<VectorClockClass.hostNames.size();i++)
		// {
		// //System.out.println("Host Names Size in Sending="+VectorClockClass.hostNames.size());
		// String message=null;
		// //System.out.println("Thread going to sleep");
		// //System.out.println("Thread is up");
		// if(!VectorClockClass.myHostAddress.equals(VectorClockClass.hostNames.get(i))
		// &&
		// VectorClockClass.myPortNumber!=VectorClockClass.portNumbers.get(i)){
		// if(VectorClockClass.sentMessageCounter<sendingExitMessage){
		// message="Hello From Client with node number:"+VectorClockClass.myNodeNumber+" Message Number:"+VectorClockClass.globalCounter+",";
		//
		// }
		// else{
		// message="END,";
		// }
		//
		// VectorClockClass.setInternalClock(1);
		// VectorClockClass.vector.put(VectorClockClass.myNodeNumber,VectorClockClass.internalClock);
		// // for(int j=0;j<=VectorClockClass.hostNames.size();j++)
		// // {
		// // message+=VectorClockClass.vector.get(j+1)+":";
		// // }
		// Iterator it = VectorClockClass.vector.entrySet().iterator();
		// while (it.hasNext()) {
		// Map.Entry pairs = (Map.Entry)it.next();
		// message+=pairs.getValue()+":";
		// //it.remove(); // avoids a ConcurrentModificationException
		// }
		//
		// try {
		// //System.out.println("Sending..."+message);
		// sendMessage(ClientSocket.get(VectorClockClass.nodeNumbers.get(i)),
		// message);
		// if(VectorClockClass.sentMessageCounter>=sendingExitMessage){
		// System.out.println("Vector After Sending exit message:"+VectorClockClass.vector);
		// runningClient=false;
		// //System.out.println(runningClient);
		// }
		// Thread.sleep(500*(VectorClockClass.myNodeNumber));
		// } catch (CharacterCodingException | InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		// }
		// VectorClockClass.sentMessageCounter++;
		// }
	}
}
