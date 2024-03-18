/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.practica1;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Max
 */
public class Cliente {
    public static void main(String[] args){
        try{
            // Se establece la conexión con el servidor en el localhost (127.0.0.1) y el puerto 1234
            Socket socket = new Socket("127.0.0.1",1234);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            //String LOCAL_FOLDER_PATH = "C:\\Users\\maxar\\Desktop\\carpetaLocal";
            //String LOCAL_FOLDER_PATH = "C:\\Users\\Max\\yo";
            String LOCAL_FOLDER_PATH = "C:\\Users\\mreye\\Downloads\\Redes\\Local";

            
            while(true){
                    System.out.println("\n\n\n");
                    System.out.println("1. Mostrar contenido de la carpeta local");
                    System.out.println("2. Mostrar contenido de la carpeta remota");
                    System.out.println("3. Crear carpetas localmente");
                    System.out.println("4. Crear carpetas remota");
                    System.out.println("5. Borrar archivos/carpeta de la carpeta local");
                    System.out.println("6. Borrar archivos/carpeta de la carpeta remota");
                    System.out.println("7. Cambiar ruta del directorio local");
                    System.out.println("8. Cambiar ruta del directorio remoto");
                    System.out.println("9. Enviar archivos/carpeta desde la carpeta local hacia la remota");

                    /*En esta parte del codigo tendremos que recibir los archivos del servidor al cliente */
                    System.out.println("10. Enviar archivos/carpeta desde la carpeta remota hacia la local");  
                    
                    System.out.println("11. Salir");
                    System.out.println("Ingresa una opcion:\n");
                    /*Se mapea la opcion para el servidor */
                    int opcion = scanner.nextInt();
                    out.writeInt(opcion);

                    switch (opcion) {
                        case 1:
                        System.out.println("**MOSTRAR CONTENIDO DE CARPETA LOCAL\n");
                            // Mostrar contenido de la carpeta local
                            Metodos.mostrarCarpeta(LOCAL_FOLDER_PATH,0);
                            break;
                            
                        case 2:/*Mostrar carpeta remota*/
                            System.out.println("\n**MOSTRAR CONTENIDO DE CARPETA REMOTA\n");
                            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                            List<String> nombresArchivos = (List<String>) ois.readObject();
                            

                            // Iteramos sobre la lista para mostrar el contenido
                            for (String nombreArchivo : nombresArchivos) {
                                System.out.println(nombreArchivo);
                            }
                            break;
                        case 3:
                        System.out.println("\n**CREAR CARPETA LOCAL**\n");
                            /*Crear carpeta Localmente */
                            Metodos.crearCarpeta(LOCAL_FOLDER_PATH);
                            break;
                        case 4:
                            /*Crear carpeta Remota*/
                            System.out.println("\n**CREAR CARPETA REMOTA**\n");
                            System.out.println("Ingrese el nombre de la carpeta a crear:");
                            String carpetaNueva =scanner.next();
                            out.writeUTF(carpetaNueva);
                            Integer respuesta = in.readInt();
                            if (respuesta==1) {
                                System.out.println("Se creo la nueva carpeta.");
                            } else if (respuesta==-1) {
                                System.out.println("No se creo la carpeta.");
                            } else if (respuesta==0) {
                                System.out.println("La carpeta ya existe.");
                            }
                            break;
                        case 5:
                            /*Borrar archivo local */
                            System.out.println("\n**BORRAR ARCHIVO/CARPETA DE LA CARPETA LOCAL**\n");
                            System.out.println("Archivos disponibles en la carpeta local:\n");
                            Metodos.mostrarCarpeta(LOCAL_FOLDER_PATH, 0);
                            /*Parte para borrar el archivo */

                            Scanner scannerr = new Scanner(System.in);
                            System.out.print("Introduce el nombre del archivo/carpeta a borrar: ");
                            String rutaArchivo = LOCAL_FOLDER_PATH+"\\"+scannerr.nextLine();
                            File archivoDelete = new File(rutaArchivo);

                            Metodos.borrarArchivoCarpeta(archivoDelete);
                            break;
                        case 6:
                            System.out.println("\n** BORRAR ARCHIVOS/CARPETA DE LA CARPETA REMOTA **\n");
                            // Mostrar lista de archivos y carpetas en la carpeta remota
                            ObjectInputStream ois2 = new ObjectInputStream(in);
                            List<String> archivosDisponibles = (List<String>) ois2.readObject();
                            System.out.println("Archivos disponibles en la carpeta remota:\n");
                            for (String archivo : archivosDisponibles) {
                                System.out.println(archivo);
                            }

                            System.out.print("Introduce el nombre del archivo/carpeta a borrar: ");
                            String archivoBorrar = scanner.next();
                            out.writeUTF(archivoBorrar);

                            int res = in.readInt();
                            if (res == 1) {
                                System.out.println("Se borró el archivo/carpeta.");
                            } else if (res == -1) {
                                System.out.println("El archivo/carpeta no existe.");
                            } else if (res == 0) {
                                System.out.println("No se pudo borrar el archivo/carpeta.");
                            }
                            break;
                        case 7:
                            /*Cambiar ruta del directorio local */
                            System.out.println("\n**ACTUALIZAR RUTA DEL DIRECTORIO LOCAL\n");
                            System.out.println("Ingresa la nueva ruta local: ");
                            scanner.nextLine();
                            String nuevaRutaLocal = scanner.nextLine();
                            /*Validamos si la ruta si existe*/
                            if (Metodos.validaRuta(nuevaRutaLocal)) {
                                LOCAL_FOLDER_PATH = nuevaRutaLocal;
                            } else {
                                System.out.println("La ruta ingresada no es válida. No se actualizará la ruta del directorio local.");
                            }
                            break;
                        case 8:
                            /*Cambiar ruta del directorio remoto */
                            System.out.println("\n**CAMBIAR RUTA CARPETA REMOTA**\n");
                            System.out.println("Ingresa la nueva ruta remota: ");
                            scanner.nextLine();
                            String nuevaRutaRemota= scanner.nextLine();
                            if(Metodos.validaRuta(nuevaRutaRemota)){
                                out.writeUTF(nuevaRutaRemota);
                                
                            }else{
                                System.out.println("La ruta ingresada no es válida. No se actualizará la ruta del directorio remoto.");
                            }
                            break;
                        case 9:
                            /* Enviar archivos de la carpeta local a la remota*/
                            JFileChooser jf = new JFileChooser();
                            //jf.setMultiSelectionEnabled(true);
                            jf.setCurrentDirectory(new File(LOCAL_FOLDER_PATH));
                            jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                            int r = jf.showOpenDialog(null);

                            if(r==JFileChooser.APPROVE_OPTION){

                                if (jf.getSelectedFile().isDirectory()) {
                                    /* Se selecciono una carpeta y hay que comprimirla */
                                    File selectedFolder = jf.getSelectedFile();
                                    String zipFileName = selectedFolder.getAbsolutePath() + ".zip";
                                    try {
                                        Metodos.zipDirectory(selectedFolder, zipFileName);
                                        System.out.println("Carpeta comprimida correctamente en: " + zipFileName);
                                        File zip = new File(zipFileName);
                                        
                                        Metodos.enviarArchivoCarpeta(socket, LOCAL_FOLDER_PATH, zip);
                                        zip.delete();
                                        
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }else{
                                    /* Se selecciono un archivo y hay que enviarlo normal */
                                    Metodos.enviarArchivoCarpeta(socket, LOCAL_FOLDER_PATH, jf.getSelectedFile());
                                    
                                }
                            }
                            break;
                        case 10:/*Enviar archivos/carpetas desde el remoto al local */
                            ObjectInputStream ois3= new ObjectInputStream(socket.getInputStream());
                            List<String> nombresArchivosBorrar = (List<String>) ois3.readObject();
                            

                            // Iteramos sobre la lista para mostrar el contenido
                            for (String nombreArchivo : nombresArchivosBorrar) {
                                System.out.println(nombreArchivo);
                            }

                            System.out.print("Introduce el nombre del archivo/carpeta a descargar: ");
                            String archivoDescargar = scanner.next();
                            out.writeUTF(archivoDescargar);

                            int res2 = in.readInt();
                            if (res2==-1) {
                                System.out.println("El archivo/carpeta no existe");
                            }else{
                                Metodos.recibirArchivoCarpeta(socket, LOCAL_FOLDER_PATH);
                            }
                            break;
                        case 11:/* Salir */
                            socket.close();
                            return;
                        default:
                            System.out.println("Opcion no valida");
                            break;
                    }
            }
            

        } catch(Exception e) {
            // Se imprime la traza de la excepción en caso de producirse un error
            e.printStackTrace();
        }
    }//main
}