

import java.net.*;
import java.io.*;

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
        String folderRemoto = "C:\\Users\\maxar\\Desktop\\carpetaRemota";
        PrintWriter pw = new PrintWriter(server.getOutputStream(), true);
        
        
        /*setReuseAddress para cuando se pierda la conexion use la misma direccion */
          server.setReuseAddress(true);
        //   System.out.println("Servidor iniciado esperando por archivos..");
        //   File f = new File("");
        //   String ruta = f.getAbsolutePath();
        //   String carpeta="archivos";
        //   String ruta_archivos = ruta+"\\"+carpeta+"\\";
        //   System.out.println("ruta:"+ruta_archivos);
        //   File f2 = new File(ruta_archivos);
        //   f2.mkdirs();
        //   f2.setWritable(true);
          
        while (true) {
            int opcion = in.readInt();
            System.out.println("Opcion recibida por el servidor:"+opcion);
            switch (opcion) {
                    
                case 2:
                    // Listar contenido de carpeta remota 
                    /*En esta parte el servidor manda su informacion de la carpeta remota al cliente y este la muestra */
                    /*manda la ruta de la carpeta remota */
                    
                    pw.println(folderRemoto); 
                    break;
                case 4:
                    // Crear Carpeta remota
                    pw.println(folderRemoto);
                    break;
                case 6:
                    // Crear Carpeta remota
                    pw.println(folderRemoto);
                    break;
               
                case 11:
                    // System.out.println("Cliente conectado desde "+server.getInetAddress()+":"+server.getPort());
                    // DataInputStream dis = new DataInputStream(server.getInputStream());
                    // String nombre = dis.readUTF();
                    // long tam = dis.readLong();
                    // System.out.println("Comienza descarga del archivo "+nombre+" de "+tam+" bytes\n\n");
                    // DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos+nombre));
                    // long recibidos=0;
                    // int l=0, porcentaje=0;
                    // while(recibidos<tam){
                    //     byte[] b = new byte[1500];
                    //     l = dis.read(b);
                    //     System.out.println("leidos: "+l);
                    //     dos.write(b,0,l);
                    //     dos.flush();
                    //     recibidos = recibidos + l;
                    //     porcentaje = (int)((recibidos*100)/tam);
                    //     System.out.print("\rRecibido el "+ porcentaje +" % del archivo");
                    // }//while
                    // System.out.println("Archivo recibido..");
                    // dos.close();
                    // dis.close();
                    // server.close();
                    // break;
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

    
}