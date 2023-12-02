package me.rodrigo.velocitystaffchat.network;

import java.io.*;
import java.net.URL;

public class FileDownloader {

    public static void downloadFile(String fileUrl, String destinationPath) throws IOException {
        URL url = new URL(fileUrl);

        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destinationPath)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }



}
