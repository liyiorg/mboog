package mboog.generator.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * @author LiYi
 */
public abstract class MBGFileUtil {

    private static String ROOT_PATH = System.getProperty("user.dir");

    public static File getFile(String filePath) {
        return getFile("/src/main/java/", filePath);
    }

    public static File getResourcesFile(String filePath) {
        return getFile("/src/main/resources/", filePath);
    }

    public static File getFile(String projectDir, String filePath) {
        return new File(ROOT_PATH + projectDir + filePath);
    }

    public static boolean createFile(File file, String contents) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream in = new ByteArrayInputStream(contents.getBytes());
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            fileOutputStream = new FileOutputStream(file);
            while ((bytesRead = in.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static String copyToString(InputStream in, Charset charset) throws IOException {
        if (in == null) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(in, charset);
        char[] buffer = new char[4096];
        int bytesRead = -1;
        while ((bytesRead = reader.read(buffer)) != -1) {
            out.append(buffer, 0, bytesRead);
        }
        return out.toString();
    }

    public static void copy(String in, Charset charset, OutputStream out) throws IOException {
        Writer writer = new OutputStreamWriter(out, charset);
        writer.write(in);
        writer.flush();
    }

}
