package ProgramAssignment1;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.*;

public class JavaShell2 {

    private static String currentDirectory = System.getProperty("user.dir");

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String command;
        while ((command = reader.readLine()) != null) {
            String[] parts = command.split(" "); // Split the command into parts
            if (parts.length == 0) {
                continue; // Handle empty input gracefully
            }
            String mainCommand = parts[0];

            switch (mainCommand) {
                case "exit":
                    return;
                case "ls":
                    if (parts.length > 1 && parts[1].equals("-l")) {
                        listDirectoryLong();
                    } else {
                        listDirectory();
                    }
                    break;
                case "cd":
                    changeDirectory(parts);
                    break;
                case "mkdir":
                    mkdir(parts);
                    break;
                case "rmdir":
                    rmdir(parts);
                    break;
                case "cp":
                    cp(parts);
                    break;
                case "rm":
                    rm(parts);
                    break;
                default:
                    // Try to start a program with the given name.
                    runProgram(parts);
                    break;
            }
        }

    }

    private static void runProgram(String[] args) {
        try {
            // Create a ProcessBuilder with the command and its arguments
            ProcessBuilder processBuilder = new ProcessBuilder(args);

            // Redirect input, output, and error streams to the current process
            processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            // Start the external program
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Program executed successfully.");
            } else {
                System.err.println("Program exited with an error code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error running program: " + e.getMessage());
        }
    }

    private static void listDirectory() {
        File directory = new File(currentDirectory);
        String[] files = directory.list();
        for (String file : files) {
            System.out.println(file);
        }
    }

    private static void listDirectoryLong() {
        File directory = new File(currentDirectory);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    String name = file.getName();
                    String canonicalPath = file.getCanonicalPath();
                    String type = file.isDirectory() ? "Directory" : "File";
                    long size = file.length();
                    String permissions = file.canRead() ? "r" : "-";
                    permissions += file.canWrite() ? "w" : "-";
                    permissions += file.canExecute() ? "x" : "-";

                    System.out.printf("%s %s %d %s %s%n", permissions, type, size, name, canonicalPath);
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        } else {
            System.err.println("Error: Unable to list files in the directory.");
        }
    }

    private static void changeDirectory(String[] commandParts) {
        if (commandParts.length != 2) {
            System.err.println("Error: Invalid usage of 'cd' command.");
            return;
        }

        String newDirectory = commandParts[1];
        File directory = new File(currentDirectory + File.separator + newDirectory);
        if (directory.exists() && directory.isDirectory()) {
            currentDirectory = directory.getAbsolutePath();
        } else {
            System.err.println("Error: Directory does not exist.");
        }
    }

    private static void mkdir(String[] commandParts) {
        if (commandParts.length != 2) {
            System.err.println("Error: Invalid usage of 'mkdir' command.");
            return;
        }

        String directoryName = commandParts[1];
        File directory = new File(currentDirectory + File.separator + directoryName);
        if (!directory.exists()) {
            if (directory.mkdir()) {
                System.out.println("Directory created.");
            } else {
                System.err.println("Error: Failed to create directory.");
            }
        } else {
            System.err.println("Error: Directory already exists.");
        }
    }

    private static void rmdir(String[] commandParts) {
        if (commandParts.length != 2) {
            System.err.println("Error: Invalid usage of 'rmdir' command.");
            return;
        }

        String directoryName = commandParts[1];
        File directory = new File(currentDirectory + File.separator + directoryName);
        if (directory.exists() && directory.isDirectory()) {
            if (directory.delete()) {
                System.out.println("Directory deleted.");
            } else {
                System.err.println("Error: Failed to delete directory.");
            }
        } else {
            System.err.println("Error: Directory does not exist.");
        }
    }

    public static void cp(String[] commandParts) {

        if (commandParts.length != 3) {
            System.err.println("Invalid usage of cp");
            return;
        }

        String sourcePath = commandParts[1];
        String destPath = commandParts[2];

        File source = new File(sourcePath);
        File dest = new File(destPath);

        if (!source.exists()) {
            System.err.println("Source file does not exist");
            return;
        }

        try (InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            System.out.println("File copied successfully");

        } catch (IOException e) {
            System.err.println("Error copying file: " + e.getMessage());
        }
    }

    private static void rm(String[] commandParts) {
        if (commandParts.length != 2) {
            System.err.println("Error: Invalid usage of 'rm' command.");
            return;
        }

        String fileName = commandParts[1];
        File file = new File(currentDirectory + File.separator + fileName);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("File deleted.");
            } else {
                System.err.println("Error: Failed to delete file.");
            }
        } else {
            System.err.println("Error: File does not exist.");
        }
    }
}