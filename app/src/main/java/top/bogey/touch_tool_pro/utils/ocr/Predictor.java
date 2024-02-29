package top.bogey.touch_tool_pro.utils.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import top.bogey.touch_tool_pro.MainApplication;
import top.bogey.touch_tool_pro.utils.SettingSave;

public class Predictor {
    private final static String KEYS = "keys.txt";
    private final static String DET = "det.nb";
    private final static String CLS = "cls.nb";
    private final static String REC = "rec.nb";

    private static final ArrayList<String> labels = new ArrayList<>();
    private static long nativePointer;

    public static void tryInitOcr() {
        if (SettingSave.getInstance().isUseOcr()) initOcr();
    }

    public static boolean initOcr() {
        String[] fileNames = new String[]{KEYS, DET, CLS, REC};
        for (String fileName : fileNames) {
            File file = getFile(fileName);
            if (!file.exists()) return false;
        }
        return loadLabels() && loadModel();
    }

    public static boolean ocrReady() {
        return nativePointer != 0;
    }

    private static boolean loadLabels() {
        labels.clear();
        labels.add("black");

        try (InputStream inputStream = new FileInputStream(getFile(KEYS))) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        labels.add(" ");
        return true;
    }

    private static boolean loadModel() {
        Context context = MainApplication.getInstance();
        String modelDir = context.getFilesDir() + File.separator + "ocr" + File.separator;
        nativePointer = init(modelDir + DET, modelDir + REC, modelDir + CLS, 0, 8, "LITE_POWER_HIGH");
        return nativePointer != 0;
    }

    private static File getFile(String fileName) {
        Context context = MainApplication.getInstance();
        return new File(context.getFilesDir(), File.separator + "ocr" + File.separator + fileName);
    }

    public static boolean importModel(File zipFile) {
        Context context = MainApplication.getInstance();
        String modelDir = context.getFilesDir() + File.separator + "ocr";
        File dirFile = new File(modelDir);
        if (!dirFile.exists()) if (!dirFile.mkdirs()) return false;

        try {
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                File file = new File(modelDir, zipEntry.getName());
                if (file.isDirectory()) continue;

                FileOutputStream outputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = zipInputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, len);
                }
                outputStream.close();
            }
            zipInputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<OcrResult> runOcr(Bitmap image) {
        ArrayList<OcrResult> results = new ArrayList<>();
        if (!ocrReady() || image == null) return results;

        float[] floats = forward(nativePointer, image, 960, 1, 0, 1);

        int begin = 0;
        while (begin < floats.length) {
            int pointNum = Math.round(floats[begin]);
            int wordNum = Math.round(floats[begin + 1]);
            int similar = Math.round(floats[begin + 2] * 100);

            int current = begin + 3;
            Rect rect = new Rect();
            boolean init = false;
            for (int i = 0; i < pointNum; i++) {
                int x = Math.round(floats[current + i * 2]);
                int y = Math.round(floats[current + i * 2 + 1]);
                if (init) {
                    rect.left = Math.min(x, rect.left);
                    rect.right = Math.max(x, rect.right);
                    rect.top = Math.min(y, rect.top);
                    rect.bottom = Math.max(y, rect.bottom);
                } else {
                    rect.set(x, y, x, y);
                    init = true;
                }
            }

            StringBuilder builder = new StringBuilder();
            current += (pointNum * 2);
            for (int i = 0; i < wordNum; i++) {
                int index = Math.round(floats[current + i]);
                builder.append(labels.get(index));
            }

            results.add(new OcrResult(rect, builder.toString(), similar));

            begin += (3 + pointNum * 2 + wordNum + 2);
        }

        results.sort((o1, o2) -> {
            int topOffset = -(o1.getArea().top - o2.getArea().top);
            if (Math.abs(topOffset) <= 10) {
                return -(o1.getArea().left - o2.getArea().left);
            } else {
                return topOffset;
            }
        });

        return results;
    }

    public static void destroy() {
        if (ocrReady()) release(nativePointer);
        nativePointer = 0;
    }


    private static native long init(String detModelPath, String recModelPath, String clsModelPath, int useOpencl, int threadNum, String cpuMode);

    private static native float[] forward(long pointer, Bitmap originalImage, int max_size_len, int run_det, int run_cls, int run_rec);

    private static native void release(long pointer);
}
