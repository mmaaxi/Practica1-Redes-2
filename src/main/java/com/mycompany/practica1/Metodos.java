package com.mycompany.practica1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Metodos {
                            /* PARA CREAR ZIP CARPETA */
                            
    public static void zipDirectory(File directory, String zipFileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFileName);
        ZipOutputStream zos = new ZipOutputStream(fos);
        zip(directory, directory.getName(), zos);
        zos.close();
        fos.close();
    }

    public static void zip(File directory, String baseName, ZipOutputStream zos) throws IOException {
        File[] files = directory.listFiles();
        byte[] buffer = new byte[1024];
        int length;
        for (File file : files) {
            if (file.isDirectory()) {
                zip(file, baseName + "/" + file.getName(), zos);
            } else {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry ze = new ZipEntry(baseName + "/" + file.getName());
                zos.putNextEntry(ze);
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                fis.close();
            }
        }
    }

                            /* METODOS LOCAL */

                            /* METODOS REMOTA */
}
