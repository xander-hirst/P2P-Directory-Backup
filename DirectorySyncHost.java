package fileSync;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
/**
 * Driver class to run DirectoryScanner, FileTransferServer, and DirectoryUnpacker
 * @author Xander Hirst
 */
public class DirectorySyncHost {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter the path of the directory you would like to scan: ");
		String dirToScanPath = scan.nextLine();
		//get the server's home directory and store the manifest file there temporarily
		String userHomeFolder = System.getProperty("user.home");
		File primaryDirectory = new File(dirToScanPath);
		//File directoryManifest = new File(userHomeFolder, "dir_manifest.txt");
		File directoryManifest = new File("D:\\dir_manifest.txt");
		if (directoryManifest.exists()) {
			directoryManifest.delete();
		}
		else {
			try {
				directoryManifest.createNewFile();
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
		//initialize the BufferedWriter
		DirectoryScanner.initializeWriter(directoryManifest);
		File filesInDirectory[] = null;
		if (primaryDirectory.exists()) {
			//get an initial list of files in the home directory
			filesInDirectory = primaryDirectory.listFiles();
			//populate manifest file with directory files and sub directories
			DirectoryScanner.directoryScan(0, primaryDirectory, filesInDirectory);
			System.out.println("Directory scan was successful");
		}
		else {
			System.out.println("Invalid directory path");
		}
		DirectoryScanner.closeWriter();	
		
		System.out.print("Enter port number: ");
		int portNumber = scan.nextInt();
		FileTransferServer fileServer = new FileTransferServer();
		fileServer.createSocket(portNumber);
		fileServer.sendFile(directoryManifest.toString());
		//read file paths from the manifest and send each file
		String filePaths[] = DirectoryUnpacker.getFilePaths(directoryManifest);
		for (int i = 0; i < filePaths.length; i++) {
			fileServer.sendFile(filePaths[i].replaceAll("\t", ""));
		}
		System.out.println("Files sent");
		fileServer.closeSocket();
		directoryManifest.delete();
		scan.close();
		System.out.println("Backup Complete");
	}

}
