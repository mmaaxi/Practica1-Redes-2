
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
        //String folderRemoto = "C:\\Users\\Max\\remota";
        PrintWriter pw = new PrintWriter(server.getOutputStream(), true);
        
        server.setReuseAddress(true);
       
          
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

                    String nombreArchivoABorrar = in.readUTF();
                    // Recibir la selección del cliente

                    String rutaArchivo = folderRemoto +"\\"+nombreArchivoABorrar;
                    File archivoDelete = new File(rutaArchivo);

                    Integer res = borrarArchivoCarpeta(archivoDelete);
                    out.writeInt(res);
                    break;
                case 8:
                    // Modificar ruta carpeta remota
                    String nuevaRuta = in.readUTF(); // Recibir la nueva ruta desde el cliente
                    folderRemoto = nuevaRuta; // Actualizar la ruta de la carpeta remota
                    System.out.println("Ruta de carpeta remota modificada con éxito a: " + folderRemoto);
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

    public static Integer borrarArchivoCarpeta(File archivo){

        int response = 0;
      
        if (archivo.exists()) {

            if (archivo.isDirectory()) {
        
                File[] archivos = archivo.listFiles();
                if (archivos != null) {
                    for (File archivoActual : archivos) {
                        borrarArchivoCarpeta(archivoActual);
                    }
                }
            }

            if (archivo.delete()) {
                System.out.println("Se borró el archivo/carpeta: " + archivo.getAbsolutePath());
                response = 1;
            } else {
                response = 0 ;
                System.out.println("No se borró el archivo/carpeta: " + archivo.getAbsolutePath());
            }
        } else {
            response = -1;
            System.out.println("El archivo/carpeta no existe: " + archivo.getAbsolutePath());
        }

        if(response == 0)
            return 0;
        else if (response == 1)
            return 1;
        else
            return -1;
        
        }
}