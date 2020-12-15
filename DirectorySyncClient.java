package fileSync;
import java.util.Scanner;
import java.io.File;

/**
 * Driver class to run DirectoryUnpacker and FileTransferClient
 * @author Xander Hirst
 */
public class DirectorySyncClient {

	public static void main(String[] args) {	
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter IP Address of server: ");
		String ip = scan.nextLine();
		System.out.print("Enter port number: ");
		int port = scan.nextInt();
		//get OS to format paths correctly
		System.out.print("Enter operating system (w for Windows, l for Linux): ");
		String os = scan.next();
		
		FileTransferClient fileClient = new FileTransferClient();
        fileClient.createSocket(ip, port);
        //find clients home directory
        String userHomeFolder = System.getProperty("user.home");
        File directoryManifest = new File(userHomeFolder, "dir_manifest.txt");
        fileClient.receiveFile(directoryManifest.toString());
        //create all the sub directories in the home directory
        String homeDirectory = DirectoryUnpacker.createFileStructure(directoryManifest);
        System.out.println("Directory structure replicated");
        //convert file paths from those listed in manifest to the client's file path structure
        String filePaths[] = DirectoryUnpacker.convertFilePaths(DirectoryUnpacker.getFilePaths(directoryManifest), userHomeFolder, homeDirectory, os);
        //receive each file from server
        for (int i = 0; i < filePaths.length; i++) {
        	fileClient.receiveFile(filePaths[i]);
        }
        System.out.println("Files received");
        fileClient.closeSocket();
        directoryManifest.delete();
        scan.close();
        System.out.println("Backup complete");
	}

}
