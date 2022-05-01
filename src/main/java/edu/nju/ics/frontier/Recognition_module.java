package edu.nju.ics.frontier;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


//单例模式
public class Recognition_module {
    private static final Recognition_module recModule=new Recognition_module();
    private LinkedList<String> dataBuffer;      //30min的数据缓冲区
    private ArrayList<RecResult> resultLog;     //识别结果的记录
    private Recognition_module() {
        dataBuffer=new LinkedList<String>();
        resultLog=new ArrayList<RecResult>();

        //调试仿真
        String userHome = System.getProperty("user.home");
        String rootpath = userHome  + "/interaction_traces/";
        try {
            LoadBufferFromFile(rootpath+"src/temp.json");
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public static Recognition_module getInstance()
    {
        return recModule;
    }
    //维护缓冲区
    private void update(long curtime) {
        if(dataBuffer.isEmpty())
            return;

        while(!dataBuffer.isEmpty()&&curtime-Long.parseLong(JSONObject.parseObject(dataBuffer.getFirst()).getString("time"))/1000>1800)
        {
            dataBuffer.removeFirst();
        }
    }
    //添加数据
    public void onData(String data) {
        dataBuffer.addLast(data);

        JSONObject curop = JSONObject.parseObject(data);
        String s=curop.getString("time");
        //System.out.println(s);
        long newtime= Long.parseLong(s)/1000;
        update(newtime);
    }
    //获取当前识别结果
    public int getResult(String alg) throws IOException {
        System.out.println("current buffer size:  "+dataBuffer.size());
        String userHome = System.getProperty("user.home");
        String rootpath = userHome  + "/interaction_traces/";
        FileOutputStream fos = new FileOutputStream(rootpath+"src/piece.json",false);
        for(String s:dataBuffer)
        {
            s+='\n';
            byte[] bytes = s.getBytes("UTF-8");
            fos.write(bytes);
        }
        fos.close();
        RunPython.runfun(rootpath+"src/Data_Process.py","process",rootpath+"src/piece.json",rootpath+"src/feature.json");
        RunPython.runfun(rootpath+"src/Data_Process.py","abstract",rootpath+"src/piece.json",rootpath+"src/abstract.json");
        RunPython.runfun(rootpath+"src/Data_Process.py","getfeature",rootpath+"src/abstract.json",rootpath+"src/feature.json");
        File file = new File(rootpath+"src/piece.json");
        file.delete();
        file = new File(rootpath+"src/abstract.json");
        file.delete();
        //file = new File("src/piece.json");
        //file.delete();

        //RunPython.runfun("src/Random_forest.py","recognize","","");

        int returnval=RunPython.run(rootpath+"src/Random_forest.py",rootpath+"src/feature.json");
        System.out.println(returnval);
        Date date=new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime=formater.format(date);
        System.out.println("return value:"+returnval);
        if(returnval==101) {
            resultLog.add(new RecResult(currentTime,1));
            System.out.println("Engaged");
            return 1;
        }
        else if(returnval==100) {
            resultLog.add(new RecResult(currentTime,0));
            System.out.println("Not Engaged");
            return 0;
        }
        else
            System.out.println("Error!");
        return -1;
    }
    //获得目前所有的识别结果
    public void getLog() throws IOException {
        String userHome = System.getProperty("user.home");
        String rootpath = userHome  + "/interaction_traces/";
        FileOutputStream fos = new FileOutputStream(rootpath+"src/Log.txt",false);
        for(RecResult res:resultLog)
        {
            String s=res.time+":     "+(res.result==1?"engaged":"not engaged");
            s+='\n';
            byte[] bytes = s.getBytes("UTF-8");
            fos.write(bytes);
        }
        Date date=new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime=formater.format(date);
        String s1="\n========================\nLogged in: "+currentTime;
        byte[] bytes = s1.getBytes("UTF-8");
        fos.write(bytes);
        fos.close();
    }

    public void LoadBufferFromFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        String line;
        while((line= reader.readLine())!=null)
        {
            onData(line);
            //dataBuffer.add(line);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");
        //RunPython.run("src/Random_forest.py");
        Recognition_module rm=new Recognition_module();
        rm.LoadBufferFromFile("/src/temp.json");
        System.out.println(rm.dataBuffer.size());
        rm.getResult("1");
        rm.getResult("1");
        rm.getLog();

    }
}
