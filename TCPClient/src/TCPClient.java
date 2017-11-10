import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TCPClient {
 static String command = "NO_COMMAND" ;
 
 static boolean loopCondtion() throws IOException
	{
	 	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Insert command:");
		return !(command = inFromUser.readLine()).equals("END");
	}
 
 public static void main(String argv[]) throws Exception {
  
  String replyFromSerwer;
  Socket clientSocket = null;
  System.out.println("Starting client...\n");
  while (loopCondtion())
  {
	  clientSocket = new Socket("localhost", 7);
	  System.out.println("Insert command:");
	  //command = inFromUser.readLine();
	  
	  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	  outToServer.writeBytes(command + '\n');
	  System.out.println("Command: " + command + " --- > Server.");
	  
	  BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	  
	  replyFromSerwer = inFromServer.readLine();         
          
	  System.out.println("FROM SERVER: " + replyFromSerwer);
  }  
  clientSocket.close();
 }
}

 