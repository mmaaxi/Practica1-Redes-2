package com.mycompany.practica1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
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

    public static void mostrarCarpeta(String folderLocal, int nivel) {

        File dir = new File(folderLocal);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                // Agregamos espacios de indentación según el nivel
                for (int i = 0; i < nivel; i++) {
                    System.out.print("    ");
                }
                if (file.isDirectory()) {
                    // Si es una carpeta, llamamos recursivamente a mostrarCarpeta con un nivel más profundo
                    System.out.println("\\" + file.getName());
                    mostrarCarpeta(file.getAbsolutePath(), nivel + 1);
                } else {
                    // Si es un archivo, lo mostramos
                    System.out.println(file.getName());
                }
            }
        } else {
            System.out.println("La carpeta está vacía o no existe");
        }
    }

    public static boolean validaRuta(String nuevaRuta){
        File directorio = new File(nuevaRuta);
    
    // Verificar si la ruta corresponde a un directorio existente
    if (!directorio.isDirectory()) {
        System.out.println("La ruta no corresponde a un directorio existente.");
        return false;
    }
    
    // Verificar si la ruta es absoluta
    if (!directorio.isAbsolute()) {
        System.out.println("La ruta no es una ruta absoluta.");
        return false;
    }
    return true;

    }

                            /* METODOS REMOTA */

    public static List<String> obtenerArchivos(String folderLocal, int nivel) {
        List<String> nombresArchivos = new ArrayList<>();
        File dir = new File(folderLocal);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Si es una carpeta, llamamos recursivamente a obtenerContenidoCarpeta con un nivel más profundo
                    nombresArchivos.add(generarEspacios(nivel) + "\\" + file.getName());
                    nombresArchivos.addAll(obtenerArchivos(file.getAbsolutePath(), nivel + 1));
                } else {
                    // Si es un archivo, lo agregamos a la lista
                    nombresArchivos.add(generarEspacios(nivel) + file.getName());
                }
            }
        }
        return nombresArchivos;
    }

    public static String generarEspacios(int nivel) {
        StringBuilder espacios = new StringBuilder();
        for (int i = 0; i < nivel; i++) {
            espacios.append("    ");
        }
        return espacios.toString();
    }

    public static Integer crearCarpeta(String folderRemoto, String nombre){

        String carpetaNueva = folderRemoto+"\\"+nombre;
        File carpeta = new File(carpetaNueva);

        // Verificar si la carpeta ya existe
        if (!carpeta.exists()) {
            // Intentar crear la carpeta
            if (carpeta.mkdir()) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    public static Integer borrarArchivoCarpetaRemota(File archivo){
        if (archivo.exists()) {
            if (archivo.isDirectory()) {
                File[] archivos = archivo.listFiles();
                if (archivos != null) {
                    for (File archivoActual : archivos) {
                        borrarArchivoCarpetaRemota(archivoActual);
                    }
                }
            }
            if (archivo.delete()) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

                                /* ENVIO DE ARCHIVOS */

    public static void enviarArchivoCarpeta(Socket socket, String folderLocal, File archivo){
        try {
            String nombre = archivo.getName();
            String path = archivo.getAbsolutePath();
            long tam = archivo.length();
            System.out.println("Preparándose para enviar archivo " + path + " de " + tam + " bytes\n\n");
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(new FileInputStream(path));
            dos.writeUTF(nombre);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
            long enviados = 0;
            int l=0,porcentaje=0;
            while(enviados<tam){
                byte[] b = new byte[1500];
                l=dis.read(b);
                System.out.println("enviados: "+l);
                dos.write(b,0,l);

                enviados = enviados + l;
                porcentaje = (int)((enviados*100)/tam);
                System.out.print("\rEnviado el "+porcentaje+" % del archivo\n");
            }//while
            System.out.println("\nArchivo enviado..");

            dis.close();
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recibirArchivoCarpeta(Socket server, String folderRemoto){
        try {
            /*setReuseAddress para cuando se pierda la conexion use la misma direccion */
            server.setReuseAddress(true);
            System.out.println("Servidor iniciado esperando por archivos..");
            File f = new File(folderRemoto);
            String ruta = f.getAbsolutePath();
            String carpeta="archivos";
            String ruta_archivos = ruta+"\\"+carpeta+"\\";
            System.out.println("ruta:"+ruta_archivos);
            File f2 = new File(ruta_archivos);
            f2.mkdirs();
            f2.setWritable(true);

            DataInputStream dis = new DataInputStream(server.getInputStream());
            String nombre = dis.readUTF();
            long tam = dis.readLong();
            System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n\n");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));
            long recibidos=0;
            int l=0, porcentaje=0;
            while(recibidos<tam){
                byte[] b = new byte[1500];
                l = dis.read(b);
                System.out.println("leidos: "+l);
                dos.write(b,0,l);
                dos.flush();
                recibidos = recibidos + l;
                porcentaje = (int)((recibidos*100)/tam);
                System.out.print("\rRecibido el "+ porcentaje +" % del archivo ");
            }//while
            System.out.println("Archivo recibido..");
            

            if(nombre.endsWith(".zip")){

                ZipInputStream zis = new ZipInputStream(new FileInputStream(ruta_archivos + nombre));
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    String entryName = entry.getName();
                    File entryFile = new File(ruta_archivos + entryName);
                    if (entry.isDirectory()) {
                        entryFile.mkdirs();
                    } else {
                        entryFile.getParentFile().mkdirs();
                        try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                        }
                    }
                }
                zis.close();
                dos.close();
                File zip = new File(ruta_archivos + nombre);
                zip.delete();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
