package net.vatov.math.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.RealMatrix;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;

public class MatlabIO {

    public static void writeMatFile(String filePath, Map<String, RealMatrix> matrices) {
        List<MLArray> mats = new ArrayList<MLArray>(matrices.size());
        for (Entry<String, RealMatrix> m : matrices.entrySet()) {
            mats.add(new MLDouble(m.getKey(), m.getValue().getData()));
        }
        writeMatFile(mats, filePath);
    }

    public static void writeMatFile(String filePath, final String matName, final RealMatrix matrix) {
        writeMatFile(new ArrayList<MLArray>() {
            {
                add(new MLDouble(matName, matrix.getData()));
            }
        }, filePath);
    }

    private static void writeMatFile(List<MLArray> matrices, String filePath) {
        MatFileWriter writer = new MatFileWriter();
        try {
            writer.write(filePath, matrices);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
