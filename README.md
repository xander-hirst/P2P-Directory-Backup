# P2P-Directory-Backup
A java project that creates a backup of a directory on a separate computer by replicating the file structure of the host and sending files using TCP

Run by executing DirectorySyncHost on the host computer and DirectorySyncClient on the target backup computer. 
Creates a manifest file that stores information about the structure of the all the files and nested subdirectories found within the target directory.
Transfers all of the files found from the Host to the Client over a TCP socket. On client machine, manifest file is downloaded first and the subdirectory structure is replicated on the client machine. All files are placed in the correct subdirectory, then manifest file is deleted and socket is closed.
