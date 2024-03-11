/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.practica1;

import java.net.*;
import java.io.*;

/**
 *
 * @author Max
 */
public class Servidor {

    public static void main(String[] args) throws IOException {
        try{
            ServerSocket serverSocket = new ServerSocket(1234);
        Socket server = serverSocket.accept();

        DataInputStream in = new DataInputStream(server.getInputStream());
        DataOutputStream out = new DataOutputStream(server.getOutputStream());

        String opcion;
        
          server.setReuseAddress(true);
          System.out.println("Servidor iniciado esperando por archivos..");
          File f = new File("");
          String ruta = f.getAbsolutePath();
          String carpeta="archivos";
          String ruta_archivos = ruta+"\\"+carpeta+"\\";
          System.out.println("ruta:"+ruta_archivos);
          File f2 = new File(ruta_archivos);
          f2.mkdirs();
          f2.setWritable(true);
          
        while (true) {
            opcion = in.readUTF();
            switch (opcion) {
                case "1":
                    
                case "2":
                    // Crear carpetas remotamente
                    break;
                case "3":
                    // Borrar archivos de la carpeta remota
                    break;
                case "4":
                    // Cambiar ruta del directorio remoto
                    break;
                case "5":
                    // Salir
                    serverSocket.close();
                    return;
                case "11":
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
                        System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
                    }//while
                    System.out.println("Archivo recibido..");
                    dos.close();
                    dis.close();
                    server.close();
                    break;
                default:
                    out.writeUTF("Opción no válida");
                    break;
            }
        }
        }catch(Exception e) {
            // Se imprime la traza de la excepción en caso de producirse un error
            e.printStackTrace();
        }
        
    }
}
