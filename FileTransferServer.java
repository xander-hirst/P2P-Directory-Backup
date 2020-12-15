package fileSync;
import java.io.*;
import java.io.ObjectOutputStream.PutField;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class FileTransferServer {
	private ServerSocket serverSocket = null;
    private Socket socket = null;
    private DataInputStream inStream = null;
    private DataOutputStream outStream = null;

    
    public void createSocket(int port) 
    {
        try 
        {
        	//create Server and start listening
            serverSocket = new ServerSocket(port);
            //accept the connection
            socket = serverSocket.accept();
            //fetch the streams
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to client");
        }
        catch (IOException io) 
        {
            io.printStackTrace();
        }
    }
    
    public void sendFile(String filePath)
    {
    	final int MAX_BUFFER = 1000;
    	byte [] data = null;
    	int bufferSize = 0;
    	try
    	{
    		File file = new File(filePath);
    		FileInputStream fileInput = new FileInputStream(file);
    		//get the file length
    		long fileSize = file.length();
    		//first send the size of the file to the client
    		outStream.writeLong(fileSize);
    		outStream.flush();

    		//Now send the file contents
    		if(fileSize > MAX_BUFFER)
    			bufferSize = MAX_BUFFER;
    		else 
    			bufferSize = (int)fileSize;
    		
    		data = new byte[bufferSize];
    		
    		long totalBytesRead = 0;
    		while(true)
    		{
    			//read upto MAX_BUFFER number of bytes from file
    			int readBytes = fileInput.read(data);
    			//send readBytes number of bytes to the client
        		outStream.write(data);
        		outStream.flush();

        		//stop if EOF
    			if(readBytes == -1)//EOF
    				break;
    			
    			totalBytesRead = totalBytesRead + readBytes;
    			
    			//stop if fileLength number of bytes are read
    			if(totalBytesRead == fileSize)
    				break;
    			
    			////update fileSize for the last remaining block of data
    			if((fileSize-totalBytesRead) < MAX_BUFFER)
    				bufferSize = (int) (fileSize-totalBytesRead);
    			
    			//reinitialize the data buffer
    			data = new byte[bufferSize];
    		}
    		
    		fileInput.close();
    		
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    public void closeSocket() {
    	try {
    		serverSocket.close();
    		socket.close();
    		
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
