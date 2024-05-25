package com.COMPFIZ.underscore;
import com.COMPFIZ.core.EngineManager;
import com.COMPFIZ.core.WindowManager;
import org.lwjgl.Version;
import java.net.http.HttpClient;


public class Launcher {
    private static WindowManager winMan;
    private static EngineManager enGMan;
    public static void main(String[] args){
        System.out.println(Version.getVersion());
        winMan = new WindowManager("_UNDERSCORE", 1600, 900, false);
        enGMan = new EngineManager();
        try{
            enGMan.start();
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    public static WindowManager getWinMan(){
        return winMan;
    }
}
