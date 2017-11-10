import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TCPServer {
static File[] filesList;

static boolean initiateFilesList(){
	String relPath = null;
	try {
		 relPath = new File(".").getCanonicalPath();
	} catch (IOException ex) {
		System.out.println("ERROR : Cannot find files ! ");
		return false;
	}
	String dirPathname = relPath +  "\\Files";
	File directory = new File(dirPathname);
	if(!directory.isDirectory())
	{
		System.out.println(dirPathname + " is not directory");
		return false;
	}
	filesList = directory.listFiles();
	return true;
}
static String listFiles(){
	
	//LIST - wyswietla pliki w katalogu serwera
	//GET <nazwa> - pobiera plik opodanej nazwie
	// SHOW <nazwa> - wyswietla zawartosc pliki z serwera
	// QUIT - zamyka klienta
	// SHUTDOWN - zamyka server
	
	String filesString = "";
	for (File file : filesList)
	{
		if(file.isFile())
		{
			System.out.println(file.getName());
			filesString += file.getName() + " ";
		}
	}
	return filesString;
}

static String getFileContent (String clientCommand, DataOutputStream outToClient) throws IOException{
	String buffer = "";
	boolean fileNotFound = false;
	if(filesList.length != 0) 
	{
		String fileName = clientCommand.substring(5);
		for (File file : filesList)
		{
			if(file.isFile())
			{
				if (file.getName().equals(fileName))
				{
					FileReader handle = new FileReader(file);
					BufferedReader bufferedReader = new BufferedReader(handle);
					Stream<String> lines = bufferedReader.lines();
					List<String> list = new ArrayList<>(lines.collect(Collectors.toList()));
					for (String string : list) {
						buffer += string + " ";
					}
					bufferedReader.close();	
					fileNotFound = false;
					break;
				}
				else 
				{
					fileNotFound = true;
				}
			}
		}
		if (fileNotFound) 
			return "File not found !";
	}
	else 
		return "There is no files on server !";
	return buffer;
}

 public static void main(String argv[]) throws Exception {
	 boolean serverHaveFile =  initiateFilesList(); 
	 String clientCommand;
	 boolean shutdown = false;
	 ServerSocket welcomeSocket = new ServerSocket(7);
	 System.out.println("Starting server");
	  while (true) 
	  {
	   Socket connectionSocket = welcomeSocket.accept();
	   System.out.println("Waiting for message from client...");
	   BufferedReader inFromClient =
	    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	   
	   // From Client data reader
	   DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	   
	   // Getting message from Client
	   clientCommand = inFromClient.readLine();
	   System.out.println("Received: " + clientCommand);
	   
	   switch (clientCommand)
	   {
	   case "LIST":
		   if (serverHaveFile)
			   outToClient.writeBytes(listFiles() + '\n');
		   break;
	   case "SHUTDOWN":
		   shutdown = true;
		   break;
	   default:
		   if (serverHaveFile) {
			   if (clientCommand.matches("SHOW.*")){
				   if (clientCommand.length() > 4){
					   outToClient.writeBytes(getFileContent(clientCommand, outToClient) + "\n");
				   }else {
					   outToClient.writeBytes("Bad file name !\n");
				   }
			   } else
				   outToClient.writeBytes("Not recognized command \n");
			   break;
		   }
	   }
	   
	   System.out.println("Reply sent to client.");
	   if (shutdown)
	   {
		   break;
	   }
	  }
	  System.out.println("Server closing...");
	  welcomeSocket.close();
	 }
}