package launch;

import link.ConManager;
import theNewConnection.ConnectManager;

public class Launcher {

    public static void main(String[] args){

//        String urlstr = "http://store.steampowered.com/app/637650";
        int appid = 637650;
        //524220
        //374320
        //668630
        //570
        //292030
        long time1 = System.currentTimeMillis();
//        new ConManager().getPic(appid);
        ConnectManager.sentGet(appid);
        long time2 = System.currentTimeMillis();
        System.out.printf((time2-time1)+"ms");

    }
}
