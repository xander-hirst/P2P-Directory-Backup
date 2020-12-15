package fileSync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * A class to decode manifest file and replicate a directory through the paths found in the manifest
 * @author Xander Hirst
 */
public class DirectoryUnpacker {
	/**
	 * A method to read the manifest to find all of the folders inside, then create all of the folders in the backup directory
	 * @param manifest the file object associated with the manifest
	 * @return the name of the home folder that is being backed up
	 * @precondition the manifest file has been received from the server
	 * @postcondition the backup directory will contain all of the proper sub directories
	 */
	public static String createFileStructure(File manifest) {
		String homeDirectoryName = null;
		try {
			//initialize writer
			BufferedReader bReader = new BufferedReader(new FileReader(manifest));
			homeDirectoryName = bReader.readLine();
			//get just the name of the home directory being backed up
			homeDirectoryName = getEndOfPath(homeDirectoryName);
			File baseDirectory = new File((System.getProperty("user.home")),(homeDirectoryName + "-backup"));
			baseDirectory.mkdir();
			System.out.println("Storing backup in: " + baseDirectory.getPath());
			String line;
			//read the manifest and store each line that contains a folder into an ArrayList of their names
			ArrayList<String> subDirectoryNames = new ArrayList<String>();
			while ((line = bReader.readLine()) != null) {
				if (line.contains("{")) {
					line = line.replace("{" , "");
					line = line.replace("}" , "");
					subDirectoryNames.add(line);
				}
			}
			//create a folder for each folder name read
			File subDirectories[] = new File[subDirectoryNames.size()];
			for(int i = 0; i < subDirectoryNames.size(); i++) {
				//if the folder is not nested inside of any other folders
				if (!subDirectoryNames.get(i).contains("\t")) {
					subDirectories[i] = new File(baseDirectory, subDirectoryNames.get(i));
					subDirectories[i].mkdir();
				}
				else {
					//if the folder before it is not nested inside of any folders
					//make the current folder inside of the folder above it
					if (!subDirectoryNames.get(i-1).contains("\t")) {
						subDirectories[i] = new File(subDirectories[i-1].getPath(), subDirectoryNames.get(i).replaceAll("\t", ""));
						subDirectories[i].mkdir();
					}
					else {
						//if the folder above it has less tabs then place folder inside of that folder
						if (getTabNum(subDirectoryNames.get(i)) > getTabNum(subDirectoryNames.get(i-1))) {
							subDirectories[i] = new File(subDirectories[i-1].getPath(), subDirectoryNames.get(i).replaceAll("\t", ""));
							subDirectories[i].mkdir();
						}
						//if the folder above it has the same number of tabs then place the folder inside the parent folder of the folder above it
						else if (getTabNum(subDirectoryNames.get(i)) == getTabNum(subDirectoryNames.get(i-1))) {
							subDirectories[i] = new File(subDirectories[i-1].getParentFile().getPath(), subDirectoryNames.get(i).replaceAll("\t", ""));
							subDirectories[i].mkdir();
						}
						//if the folder above it has more tabs then run backwards down the folder structure until a folder with same number of tabs is found
						//put folder inside of the parent folder of the folder found
						else if (getTabNum(subDirectoryNames.get(i)) < getTabNum(subDirectoryNames.get(i-1))) {
							for (int j = i-1; j > 0; j--) {
								if (getTabNum(subDirectoryNames.get(j)) == getTabNum(subDirectoryNames.get(i))) {
									subDirectories[i] = new File(subDirectories[j].getParentFile().getPath(), subDirectoryNames.get(i).replaceAll("\t", ""));
									subDirectories[i].mkdir();
									break;
								}
							}
						}
					}
				}
				bReader.close();
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return homeDirectoryName;
	}
	/**
	 * A method to read the manifest file and return an array of paths to each file
	 * @param manifest, the file object associated with the manifest
	 * @return an array containing all of the paths to each file
	 * @precondition the manifest file has been received from the server
	 */
	public static String[] getFilePaths(File manifest) {
		ArrayList<String> fileNames = new ArrayList<String>();
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(manifest));
			bReader.readLine();
			bReader.readLine();
			String line;
			while ((line = bReader.readLine()) != null) {
				//if the line is not a folder then add the line to the array
				if(!line.contains("{")){
					fileNames.add(line);
				}
			}
			bReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//convert the ArrayList to a regular String array
		return fileNames.toArray(new String[fileNames.size()]);
	}
	
	/**
	 * A method to convert the file paths found in the manifest to the client computer's file path structure
	 * @param filePaths an array containing all of the paths to files found in the manifest
	 * @param homePath the home directory of the clients computer
	 * @param baseFolder the name of the directory being backed up 
	 * @param os the operating system of the client computer
	 * @return a String array containing the modified paths to each file found in the manifest
	 * @precondition the manifest file has been received from the server
	 */
	public static String[] convertFilePaths (String[] filePaths, String homePath, String baseFolder, String os){
		for (int i = 0; i < filePaths.length; i++) {
			String path[] = getPathComponents(filePaths[i]);
			for (int j = 0; j < path.length; j++) {
				if (path[j].equals(baseFolder)) {
					if (os.equals("w")) {
						filePaths[i] = (homePath + "\\" + baseFolder + "-backup");
						for (int k = j+1; k < path.length; k++) {
							filePaths[i] = (filePaths[i] + "\\" + path[k]);
						}
						break;
					}
					else if (os.equals("l")) {
						filePaths[i] = (homePath + "/" + baseFolder + "-backup");
						for (int k = j+1; k < path.length; k++) {
							filePaths[i] = (filePaths[i] + "/" + path[k]);	
						}
						break;
					}
				}
			}
		}
		return filePaths;
	}
	
	/**
	 * Helper method to read the number of tabs on any given line in the manifest
	 * @param str a line from the manifest file
	 * @return the number of tabs on the given str
	 */
	private static int getTabNum (String str) {
		return (str.length() - str.replaceAll("\t", "").length());
	}
	
	/**
	 * Helper method to return the end of the path (the final folder or file in a path)
	 * @param path a path string
	 * @return the final folder or file in the given path
	 */
	private static String getEndOfPath (String path) {
		//split the path by the "/"
		String pathArr[] = path.replaceAll(Pattern.quote("\\"), "/").split("/");
		return  pathArr[pathArr.length - 1];
	}
	/**
	 * Helper method to return an array containing each individual member of a path
	 * @param path a path string
	 * @return a String array containing each individual member of a path
	 */
	private static String[] getPathComponents (String path) {
		//split a path by the "/"
		String pathArr[] = path.replaceAll(Pattern.quote("\\"), "/").split("/");
		return  pathArr;
	}
}
