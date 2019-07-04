package com.zzw.persist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zzw.tools.io.OkTextReader;
import com.zzw.tools.io.OkTextWriter;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonIO {
    public static List<Input> readInputData(String path) {
        return readData(path);
    }

    public static void writeInputData(String path, List<Input> data) {
        writeData(path, data);
    }

    public static List<Development> readDevelopmentData(String path) {
        return readData(path);
    }

    public static void writeDevelopmentData(String path, List<Development> data) {
        writeData(path, data);
    }

    public static List<Performance> readPerformanceData(String path) {
        return readData(path);
    }

    public static void writePerformanceData(String path, List<Performance> data) {
        writeData(path, data);
    }

    private static <T> List<T> readData(String path) {
        if (path == null) {
            return null;
        }
        File file = new File(path);
        if (!(file.exists() && file.isFile())) {
            return null;
        }

        OkTextReader reader = new OkTextReader();
        reader.open(file);
        String json = reader.readLine();
        reader.close();

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<T>>(){}.getType();
        List<T> data = gson.fromJson(json, type);
        return (data == null || data.isEmpty()) ? null : data;
    }

    private static <T> void writeData(String path, List<T> data) {
        if (path == null || data == null || data.isEmpty()) {
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(data);

        OkTextWriter writer = new OkTextWriter();
        writer.open(path);
        writer.println(json);
        writer.close();
    }
}
