package com.ctgu.api.growthparameter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WriteToFile {
    // 辅助方法：写入CSV文件
    public static void writeToFile(String fileName, List<String> lines) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/" + fileName))) {
            writer.write("a,b,c,isValid,isTriangle");
            writer.newLine();
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
