package edu.nju.ics.frontier.view;

import com.intellij.openapi.Disposable;
import edu.nju.ics.frontier.Recognition_module;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RecManager implements Disposable {
    private static RecManager instance;
    private Recognition_module rm=Recognition_module.getInstance();
    private Timer rectimer=new Timer();
    /**
     * get the single instance of the 'RecManager' class
     * @return the single instance of the 'RecManager' class
     */
    public static RecManager getInstance() {
        if (instance == null) {
            synchronized (RecManager.class) {
                if (instance == null) {
                    instance = new RecManager();
                }
            }
        }
        return instance;
    }

    private RecManager() {
        System.out.println("in RecManager constructor!!");
        rectimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("in New rectimer!!");
                try {
                    rm.getResult("1");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("after getresult!!");
            }
        },15*60000,15*60000);
    }

    @Override
    public void dispose() {
        rectimer.cancel();
    }
}
