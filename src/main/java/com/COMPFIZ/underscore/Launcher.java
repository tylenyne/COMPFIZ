package com.COMPFIZ.underscore;

import com.COMPFIZ.core.EngineManager;
import com.COMPFIZ.core.WindowManager;
import com.COMPFIZ.core.utils.Constants;
import org.lwjgl.Version;
import java.util.*;

public class Launcher {//idea- make buffers that only allow inputs every other frame or every 3 frames so that the game feels better
    private static WindowManager winMan;
    private static examGame thisGame;//should be able to switch class to switch game

    public static void main(String[] args) {
        System.out.println(Version.getVersion());
        winMan = new WindowManager(Constants.TITLE, 1600, 900, false);
        thisGame = new examGame();
        EngineManager enGMan = new EngineManager();
        try {
            enGMan.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static WindowManager getWinMan() {
        return winMan;
    }

    public static examGame getThisGame() {
        return thisGame;
    }
}
