/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.practica1;

import java.net.*;
import java.io.*;
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
            String LOCAL_FOLDER_PATH = "C:\\Users\\Max\\yo";
            
            while(true){
                    System.out.println("\n\n\n");
                    System.out.println("1. Mostrar contenido de la carpeta local");
                    System.out.println("2. Mostrar contenido de la carpeta remota");
                    System.out.println("3. Crear carpetas localmente");
                    System.out.println("4. Crear carpetas remota");
                    System.out.println("5. Borrar archivos de la carpeta local");
                    System.out.println("6. Borrar archivos de la carpeta remota");
                    System.out.println("7. Borrar carpetas localmente");
                    System.out.println("8. Borrar carpetas remotamente");
                    System.out.println("9. Cambiar ruta del directorio local");
                    System.out.println("10. Cambiar ruta del directorio remoto");
                    System.out.println("11. Enviar archivos desde la carpeta local hacia la remota");
                    System.out.println("12. Enviar carpetas desde la carpeta local hacia la remota"); 
                    
                    System.out.println("14. Salir");
                    System.out.println("Ingresa una opcion:\n");
                    int opcion = scanner.nextInt();
                    out.writeUTF(Integer.toString(opcion));

                    switch (opcion) {
                        case 1:
                            // Mostrar contenido de la carpeta local
                            File dir = new File(LOCAL_FOLDER_PATH); 
                            File[] files = dir.listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    System.out.println(file.getName());
                                }
                            } else {
                                System.out.println("La carpeta está vacia o no existe");
                            }
                            break;
                            
                        case 2:

                            break;
                        case 3:
                            // Crear carpeta localmente
                            System.out.println("Ingrese el nombre de la carpeta a crear localmente:");
                            String localFolderName = scanner.next();
                            File localFolder = new File(LOCAL_FOLDER_PATH + File.separator + localFolderName);
                            if (localFolder.mkdir()) {
                                System.out.println("Carpeta creada localmente con éxito");
                            } else {
                                System.out.println("Error al crear la carpeta localmente");
                            }
                            break;
                        case 5:
                            System.out.print("Introduce el nombre del archivo a borrar: ");
                            String nombreArchivo = scanner.nextLine();
                            File archivo = new File(LOCAL_FOLDER_PATH + File.separator + nombreArchivo);
                            if (archivo.delete()) {
                                System.out.println("El archivo fue borrado exitosamente");
                            } else {
                                System.out.println("No se pudo borrar el archivo");
                            }
                            break;
                        case 11:
                            JFileChooser jf = new JFileChooser();
                            //jf.setMultiSelectionEnabled(true);
                            int r = jf.showOpenDialog(null);
                            if(r==JFileChooser.APPROVE_OPTION){
                                File f = jf.getSelectedFile();
                                String nombre = f.getName();
                                String path = f.getAbsolutePath();
                                long tam = f.length();
                                System.out.println("Preparandose pare enviar archivo "+path+" de "+tam+" bytes\n\n");
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
                                    dos.flush();
                                    enviados = enviados + l;
                                    porcentaje = (int)((enviados*100)/tam);
                                    System.out.print("\rEnviado el "+porcentaje+" % del archivo");
                                }//while
                                System.out.println("\nArchivo enviado..");
                                dis.close();
                                dos.close();
                                socket.close();
                            }//if
                            break;
                        case 14:
                            // Salir
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
