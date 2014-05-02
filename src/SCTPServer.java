import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.Scanner;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;

public class SCTPServer extends Thread {

	private SctpServerChannel serverSocket;
	private SctpChannel clientSocket;

	public SCTPServer() {
		try {
			serverSocket = SctpServerChannel.open();
			InetSocketAddress serverAddr = new InetSocketAddress(
					MainClass.myHostAddress, MainClass.myPortNumber);
			serverSocket.bind(serverAddr);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendMessage(SctpChannel clientSock, String Message)
			throws CharacterCodingException {
		// prepare byte buffer to send massage
		ByteBuffer sendBuffer = ByteBuffer.allocate(512);
		sendBuffer.clear();
		// Reset a pointer to point to the start of buffer
		sendBuffer.put(Message.getBytes());
		sendBuffer.flip();

		try {
			// Send a message in the channel
			MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
			clientSock.send(sendBuffer, messageInfo);
		} catch (IOException ex) {
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

	// public static void main(String args[]){
	// SCTPServer s=new SCTPServer();
	// }
	public void run() {
		while (true) {
			RecieveMsg rcvmsg;
			System.out.println("Server is ready to accept");
			// Receive a connection from client and accept it
			try {
				clientSocket = serverSocket.accept();
				rcvmsg = new RecieveMsg(clientSocket);
				Thread t = new Thread(rcvmsg);
				t.setName("Receive Msg Thread");
				t.start();
				// String Message = "WELCOME MESSAGE FROM SERVER";
				// Thread.sleep(10000);
				// sendMessage(clientSocket, Message);
				// receiveMessage(clientSocket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// MessageReceiver messagereceiver = new
			// MessageReceiver(clientSocket);

		}
	}
}
