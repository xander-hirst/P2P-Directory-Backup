package fileSync;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

/**
 * A class to scan a directory and all of its sub-directories for the files contained within them, then print that information to a txt file
 * @author Xander Hirst
 */
public class DirectoryScanner {
		private static FileWriter fWriter;
		private static BufferedWriter bWriter;
		/**
		 * Method to initialize static writers for use in directoryScan
		 * @param outputFile The File object that contains the location of the desired output txt file
		 * @precondition The path used to initialize the outputFile File object must end in a txt file
		 */
		public static void initializeWriter(File outputFile) {
			try {
				fWriter = new FileWriter(outputFile.getAbsoluteFile());
				bWriter = new BufferedWriter(fWriter);
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		/**
		 * Method to scan a directory and print all of its files and sub-directories to the output txt file
		 * @param depthOfFile The level of folder currently being scanned (a folder within a directory will have a depth of 1 and so on)
		 * @param primaryDirectory The initial directory File object to be scanned
		 * @param directory An array containing all of files and folders in any given depth 
		 * @precondition initializeWriter must be executed before directoryScan
		 * @precondition depthOfFile should be 0 for properly formatted output
		 * @postcondition The contents of the directory will not be altered at all
		 */
		public static void directoryScan(int depthOfFile, File primaryDirectory, File[] directory) {
			
			try {
				if(depthOfFile == 0) {
					bWriter.write(primaryDirectory.getAbsolutePath());
					bWriter.newLine();
					bWriter.write("--------------------------------------------------------------");	
					bWriter.newLine();
				}
				
				for (File currentFile : directory) {
					for (int index = 0; index < depthOfFile; index++) {
						bWriter.write("\t");
					}
					if (currentFile.isFile()) {
						bWriter.write(currentFile.toString());
						bWriter.newLine();
					}
					else if (currentFile.isDirectory()) {
						bWriter.write("{" + currentFile.getName() + "}");
						bWriter.newLine();
						directoryScan(++depthOfFile, primaryDirectory, currentFile.listFiles());
						depthOfFile--;
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		/**
		 * Method to close the BufferedWriter
		 * @precondition initliazeWriter has been executed 
		 */
		public static void closeWriter() {
			try {
				bWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
}
