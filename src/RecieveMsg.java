import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class RecieveMsg implements Runnable {
	public static boolean running = true;
	SctpChannel clientSocket;

	RecieveMsg(SctpChannel clientSocket) {
		this.clientSocket = clientSocket;
	}

	public static void receiveMessage(SctpChannel clientSock)
			throws InterruptedException {
		String[] output;
		String[] outputVector;
		ByteBuffer byteBuffer;
		byteBuffer = ByteBuffer.allocate(512);
		String messageType = "";
		try {
			if (running = true) {
				MessageInfo messageInfo = clientSock.receive(byteBuffer, null,
						null);
				String message = byteToString(byteBuffer);
				System.out.println("Received Message from Server: "
						+ clientSock.getRemoteAddresses() + " " + message);
				messageType = message.substring(0, 2);
				message = message.substring(2, message.length());

			}
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				receiveMessage(clientSocket);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
