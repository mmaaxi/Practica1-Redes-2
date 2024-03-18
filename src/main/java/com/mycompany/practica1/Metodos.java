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

    public static void respuestaServidor(Integer a){
        if (a==1) {
            System.out.println("Se creo la nueva carpeta.");
        } else if (a==-1) {
            System.out.println("No se creo la carpeta.");
        } else if (a==0) {
            System.out.println("La carpeta ya existe.");
        }
    }
                            /* PARA CREAR ZIP CARPETA */

    public static void zipDirectory(File directory, String zipFileName) throws IOException {
        /* FLujo de salida para escribir en un archivo zip  */
        FileOutputStream fos = new FileOutputStream(zipFileName); 
        /* Archivo zip resultante */
        ZipOutputStream zos = new ZipOutputStream(fos);
        zip(directory, directory.getName(), zos);
        zos.close();
        fos.close();
    }

    public static void zip(File directory, String baseName, ZipOutputStream zos) throws IOException {
        /* Obtenemos la lista de archivos y las guardamos en un arreglo */
        File[] files = directory.listFiles();
        /* Buffer para leer en los archivos */
        byte[] buffer = new byte[1024];
        int length;
        for (File file : files) {
            if (file.isDirectory()) {
                /* Si es un directorio se llama recursivamente pero con la nueva ubicacion */
                zip(file, baseName + "/" + file.getName(), zos);
            } else {
                /* Se crea un flujo de entrada para leer el contenido del archivo de la iteracion actual */
                FileInputStream fis = new FileInputStream(file);
                ZipEntry ze = new ZipEntry(baseName + "/" + file.getName());
                zos.putNextEntry(ze);
                /* Lee el contenido del archivo en bloques de 1024 bytes y lo almacena en el buffer. */
                while ((length = fis.read(buffer)) > 0) {
                    /* escribe el contenido del buffer en el archivo zip de salida */
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
        /* StringBuilder para manipular dinamicamente un string */
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
            return 0; // La carpeta ya existe
        }
    }

    public static Integer borrarArchivoCarpetaRemota(File archivo){
        if (archivo.exists()) {
            if (archivo.isDirectory()) {
                /* Creamos un arreglo con los archivos de la carpeta y llamamos
                    recursivamente a la funcion 
                 */
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
            /* Obtenemos los datos del arreglo */
            String nombre = archivo.getName();
            String path = archivo.getAbsolutePath();
            long tam = archivo.length();
            System.out.println("Preparándose para enviar archivo " + path + " de " + tam + " bytes\n\n");
            /* Flujos de entrada y salida */
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(new FileInputStream(path));
            /* Escribimos el nombre y tamaño del arreglo */
            dos.writeUTF(nombre);
            dos.flush();
            dos.writeLong(tam);
            dos.flush();
            long enviados = 0;
            int l=0,porcentaje=0;
            /* Mientras enviados < tam significa que aun hay datos por mandar */
            while(enviados<tam){
                /* buffer para almacenar temporalmente los datos leidos */
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
            /* Creamos un nuevo directorio que representa la carpeta destino*/
            File f = new File(folderRemoto);
            /* Obtenermos los datos del archivo */
            String ruta = f.getAbsolutePath();
            /* Creamos la carpeta donde se guardaran los archios */
            String carpeta="archivos";
            String ruta_archivos = ruta+"\\"+carpeta+"\\";
            System.out.println("ruta:"+ruta_archivos);
            File f2 = new File(ruta_archivos);
            f2.mkdirs();
            f2.setWritable(true);

            /* Flujo de entrada para recibir los datos */
            DataInputStream dis = new DataInputStream(server.getInputStream());
            String nombre = dis.readUTF();
            long tam = dis.readLong();
            System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n\n");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));
            long recibidos=0;
            int l=0, porcentaje=0;
            /* Mientras enviados < tam significa que aun hay datos por mandar */
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
            
            /* Si es un archivo zip tenemos que descomprimirlo */
            if(nombre.endsWith(".zip")){
                /* Flujo de entrada zip  para leer el archivo zip */
                ZipInputStream zis = new ZipInputStream(new FileInputStream(ruta_archivos + nombre));
                /* entrada para cada archio dentro del zip */
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    /* iteramos sobre todas las entradas del zip */
                    String entryName = entry.getName();
                    File entryFile = new File(ruta_archivos + entryName);
                    if (entry.isDirectory()) {
                        entryFile.mkdirs();
                    } else {
                        /* se crea el directorio padre del archio */
                        entryFile.getParentFile().mkdirs();
                        /* flujo de salida para escribir los datos */
                        try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                            /* buffer para almacenar temporalmente los datos leidos */
                            byte[] buffer = new byte[1024];
                            int len;
                            /* mientras haya datos disponibles para leer del flujo de entrada del archivo zip  */
                            while ((len = zis.read(buffer)) > 0) {
                                /* escribe los datos leídos del archivo zip en el flujo de salida */
                                fos.write(buffer, 0, len);
                            }
                            fos.close();
                        }
                    }
                }
                zis.close();
                dos.close();
                /* Borramos el zip despues de descomprimirlo */
                File zip = new File(ruta_archivos + nombre);
                zip.delete();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
