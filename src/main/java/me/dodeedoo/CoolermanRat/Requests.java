package me.dodeedoo.CoolermanRat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class Requests {

    URL url;
    byte[] payload;
    Integer responseCode;

    public void refrencedontexecute() throws Exception {
        URL url = new URL("");
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        byte[] out = "{\"username\":\"root\",\"password\":\"password\"}" .getBytes(StandardCharsets.UTF_8);
        int length = out.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
    }

    public Requests(String url, String payload) throws IOException {
        this.url = new URL(url);
        this.payload = payload.getBytes(StandardCharsets.UTF_8);
    }

    public void execute() throws IOException {
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        int length = payload.length;
        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(payload);
        }
        this.responseCode = http.getResponseCode();
    }

    public Integer getResponseCode() {
        return this.responseCode;
    }
}
