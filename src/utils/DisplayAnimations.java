package utils;

import main.Main;

import java.util.Arrays;

public class DisplayAnimations {

    public void BootAnimation(Main main) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (main.booting) {
                        main.displayText(1, "Booting");
                        Thread.sleep(150);
                        if (!main.booting) break;
                        main.displayText(1, "Booting.");
                        Thread.sleep(150);
                        if (!main.booting) break;
                        main.displayText(1, "Booting..");
                        Thread.sleep(150);
                        if (!main.booting) break;
                        main.displayText(1, "Booting...");
                        Thread.sleep(150);
                    }
                } catch (Exception e) {
                    Logger.error(e.getMessage());
                    Logger.error(Arrays.toString(e.getStackTrace()));
                }
            }
        });
        thread.start();
    }
}
