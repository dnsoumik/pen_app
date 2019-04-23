package player.com.p2p;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CreateFile {
    String FILE_PATH;
    File file;

    public CreateFile(String filePath){
        this.FILE_PATH = filePath;
        this.file = new File(FILE_PATH);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File writeByte(byte[] bytes){
        try {
            // Initialize a pointer
            // in file using OutputStream
            OutputStream os = new FileOutputStream(this.file);

            // Starts writing the bytes in it
            os.write(bytes);
            System.out.println("Successfully bytes inserted");

            // Close the file
            os.close();

            return this.file;
        }catch (Exception e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
            return null;
        }
    }
}
