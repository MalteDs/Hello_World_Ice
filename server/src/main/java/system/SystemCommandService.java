package system;

import java.io.*;

public class SystemCommandService {
    public String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}
