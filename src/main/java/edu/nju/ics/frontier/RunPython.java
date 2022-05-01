package edu.nju.ics.frontier;

import org.python.core.PyFunction;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunPython {
    public static int run(String filename,String arg)
    {
        Process proc;
        try {
            System.out.println("python "+filename+" "+arg);
            proc = Runtime.getRuntime().exec("python "+filename+" "+arg);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
            return proc.exitValue();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void runfun(String filename,String funcName,String arg1,String arg2)
    {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(filename);

// 第一个参数为期望获得的函数（变量）的名字，第二个参数为期望返回的对象类型
        PyFunction pyFunction = interpreter.get(funcName, PyFunction.class);
//调用函数，如果函数需要参数，在Java中必须先将参数转化为对应的“Python类型”
        //PyObject pyobj = pyFunction.__call__(new PyInteger(a), new PyInteger(b));
        pyFunction.__call__(new PyString(arg1), new PyString(arg2));

    }
}
