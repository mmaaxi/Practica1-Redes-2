/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.practica1;

import java.net.*;
import java.util.List;
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
        
        
        while (true) {
            int opcion = in.readInt();
            System.out.println("Opcion recibida por el servidor:"+opcion);
            switch (opcion) {
                    
                case 2:
                    // Listar contenido de carpeta remota 
                    /*En esta parte el servidor manda su informacion de la carpeta remota al cliente y este la muestra */
                    /*manda la ruta de la carpeta remota */
                    
                    List<String> nombresArchivos = Metodos.obtenerArchivos(folderRemoto,0);
                    ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
                    oos.writeObject(nombresArchivos);
                    oos.flush();
                    break;
                case 4:
                    // Crear Carpeta remota
                    String reference = in.readUTF();
                    Integer respuesta = Metodos.crearCarpeta(folderRemoto, reference);
                    out.writeInt(respuesta);
                    break;
                case 6:
                    // Borrar archivo/carpeta remota
                    List<String> listaArchivos = Metodos.obtenerArchivos(folderRemoto,0);
                    ObjectOutputStream archivosDisponibles = new ObjectOutputStream(server.getOutputStream());
                    archivosDisponibles.writeObject(listaArchivos);
                    archivosDisponibles.flush();

                    String nombreArchivoABorrar = in.readUTF(); // Recibir el nombre del archivo a borrar
                    String rutaArchivo = folderRemoto+"\\"+nombreArchivoABorrar;
                    File archivoDelete = new File(rutaArchivo);
                    Integer res = Metodos.borrarArchivoCarpetaRemota(archivoDelete);
                    out.writeInt(res);
                    break;
                case 8:
                    // Modificar ruta carpeta remota
                    String nuevaRuta = in.readUTF(); // Recibir la nueva ruta desde el cliente
                    folderRemoto = nuevaRuta; // Actualizar la ruta de la carpeta remota
                    System.out.println("Ruta de carpeta remota modificada con éxito a: " + folderRemoto);
                    break;
                case 9:
                    Metodos.recibirArchivoCarpeta(server, folderRemoto);// Para recibir archivos y carpetas
                    break;
                case 10:
                    // Listar contenido de carpeta remota 
                    /*En esta parte el servidor manda su informacion de la carpeta remota al cliente y este la muestra */
                    /*manda la ruta de la carpeta remota */
                    
                    List<String> nombresArchivosDescargar = Metodos.obtenerArchivos(folderRemoto,0);
                    ObjectOutputStream oos3 = new ObjectOutputStream(server.getOutputStream());
                    oos3.writeObject(nombresArchivosDescargar);
                    oos3.flush();
                    
                    String nombreArchivoDescargar = in.readUTF(); // Recibir el nombre del archivo a descargar
                    String rutaArchivoDescargar = folderRemoto+"\\"+nombreArchivoDescargar;
                    File archivoDescargar = new File(rutaArchivoDescargar);

                    if (archivoDescargar.exists()) {
                        if (archivoDescargar.isDirectory()) {
                            String zipFileName = archivoDescargar.getAbsolutePath() + ".zip";
                            try {
                                
                                Metodos.zipDirectory(archivoDescargar, zipFileName);
                                System.out.println("Carpeta comprimida correctamente en: " + zipFileName);
                                File zip = new File(zipFileName);
                                
                                enviarArchivoRL(server, folderRemoto, zip);
                                zip.delete();
                                
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }else{
                            enviarArchivoRL(server, rutaArchivoDescargar, archivoDescargar);
                        }
                    } else{
                        out.writeInt(-1);
                    }
                
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

    public static void enviarArchivoRL(Socket socket, String folderLocal, File archivo){
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
}