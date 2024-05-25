package com.COMPFIZ.underscore;
import com.COMPFIZ.core.EngineManager;
import com.COMPFIZ.core.WindowManager;
import com.COMPFIZ.core.utils.Constants;
import org.lwjgl.Version;
import java.net.http.HttpClient;


public class Launcher {
    private static WindowManager winMan;
    private static examGame thisGame;//should be able to switch class to switch game
    public static void main(String[] args){
        System.out.println(Version.getVersion());
        winMan = new WindowManager(Constants.TITLE, 1600, 900, true);
        thisGame = new examGame();
        EngineManager enGMan = new EngineManager();
        try{
            enGMan.start();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public static WindowManager getWinMan(){
        return winMan;
    }

    public static examGame getThisGame() {
        return thisGame;
    }
}
