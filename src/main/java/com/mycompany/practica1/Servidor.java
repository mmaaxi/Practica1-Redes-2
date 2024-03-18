/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.practica1;

import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.*;

/**
 *
 * @author Max
 */
public class Servidor {

    public static void main(String[] args) throws IOException {
        try{
            /*Aceptando la comunicacion del cliente */
            int port = 1234;
        ServerSocket serverSocket = new ServerSocket(port);
        Socket server = serverSocket.accept();
        System.out.println("Servidor iniciado en el puerto "+port+" .. esperando cliente..");
        /*Establecemos los flujos de entrada y salida */
        DataInputStream in = new DataInputStream(server.getInputStream());
        DataOutputStream out = new DataOutputStream(server.getOutputStream());
        //String folderRemoto = "C:\\Users\\maxar\\Desktop\\carpetaRemota";
        //String folderRemoto = "C:\\Users\\Max\\remota";
        String folderRemoto = "C:\\Users\\mreye\\Downloads\\Redes\\Remota";
        PrintWriter pw = new PrintWriter(server.getOutputStream(), true);
        
        
        while (true) {
            int opcion = in.readInt();
            System.out.println("Opcion recibida por el servidor:"+opcion);
            switch (opcion) {
                    
                case 2:
                    // Listar contenido de carpeta remota 
                    /*En esta parte el servidor manda su informacion de la carpeta remota al cliente y este la muestra */
                    /*manda la ruta de la carpeta remota */
                    
                    List<String> nombresArchivos = obtenerArchivos(folderRemoto,0);
                    ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
                    oos.writeObject(nombresArchivos);
                    oos.flush();
                    break;
                case 4:
                    // Crear Carpeta remota
                    String reference = in.readUTF();
                    Integer respuesta = crearCarpeta(folderRemoto, reference);
                    out.writeInt(respuesta);
                    break;
                case 6:
                    // Borrar archivo/carpeta remota
                    List<String> listaArchivos = obtenerArchivos(folderRemoto,0);
                    ObjectOutputStream archivosDisponibles = new ObjectOutputStream(server.getOutputStream());
                    archivosDisponibles.writeObject(listaArchivos);
                    archivosDisponibles.flush();

                    String nombreArchivoABorrar = in.readUTF(); // Recibir la selección del cliente
                    Integer res = borrarArchivoCarpeta(folderRemoto, nombreArchivoABorrar);
                    out.writeInt(res);
                    break;
                case 8:
                    // Modificar ruta carpeta remota
                    String nuevaRuta = in.readUTF(); // Recibir la nueva ruta desde el cliente
                    folderRemoto = nuevaRuta; // Actualizar la ruta de la carpeta remota
                    System.out.println("Ruta de carpeta remota modificada con éxito a: " + folderRemoto);
                    break;
                case 9:
                case 10:
                    recibirArchivoLR(server, folderRemoto); // Para recibir archivos y carpetas
                    break;
                case 12:
                    server.close();
                    return;
                default:
                    System.out.println("\nOpcion no valida para el servidor");
                    break;
            }
        }
        }catch(Exception e) {
            // Se imprime la traza de la excepción en caso de producirse un error
            e.printStackTrace();
        }
        
    }

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

    // Método para generar espacios de indentación según el nivel
    private static String generarEspacios(int nivel) {
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

    public static Integer borrarArchivoCarpeta(String folderRemoto, String nombre){
        String rutaArchivo = folderRemoto+"\\"+nombre;
        File archivo = new File(rutaArchivo);
        if(archivo.exists()){
            if (archivo.delete()) {
                return 1;
            } else {
                return 0;
            }}else{return -1;}
    }

    public static void recibirArchivoLR(Socket server, String folderRemoto){
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

            System.out.println("Cliente conectado desde "+server.getInetAddress()+":"+server.getPort());
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
                System.out.println("Extrayendo zip...");

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