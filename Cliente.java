import java.net.*;
import java.io.*; 
import java.util.Scanner;
import javax.swing.JFileChooser;


 import java.net.*;
 import java.io.*;
 import java.util.List;
 import java.util.Map;
 import java.util.Scanner;
 import javax.swing.JFileChooser;
 

 public class Cliente {
     public static void main(String[] args){
         try{
             // Se establece la conexión con el servidor en el localhost (127.0.0.1) y el puerto 1234
             Socket socket = new Socket("127.0.0.1",1234);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in);
             String LOCAL_FOLDER_PATH = "C:\\Users\\maxar\\Desktop\\carpetaLocal";
             //String LOCAL_FOLDER_PATH = "C:\\Users\\Max\\yo";
 
             
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
                     System.out.println("9. Enviar archivos desde la carpeta local hacia la remota");
                     System.out.println("10. Enviar carpetas desde la carpeta local hacia la remota");
 
                     /*En esta parte del codigo tendremos que recibir los archivos del servidor al cliente */
                     System.out.println("11. Enviar archivos desde la carpeta remota hacia la local");
                     System.out.println("12. Enviar carpetas desde la carpeta remota hacia la local");  
                     
                     System.out.println("13. Salir");
                     System.out.println("Ingresa una opcion:\n");
                     /*Se mapea la opcion para el servidor */
                     int opcion = scanner.nextInt();
                     out.writeInt(opcion);
 
                     switch (opcion) {
                         case 1:
                         System.out.println("**MOSTRAR CONTENIDO DE CARPETA LOCAL\n");
                             // Mostrar contenido de la carpeta local
                             mostrarCarpeta(LOCAL_FOLDER_PATH,0);
                             break;
                             
                         case 2:/*Mostrar carpeta remota*/
                             System.out.println("\n**MOSTRAR CONTENIDO DE CARPETA REMOTA\n");
                             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                             List<String> nombresArchivos = (List<String>) ois.readObject();
                             
 
                             // Iterar sobre el contenido de la carpeta para mostrarlo
                             for (String nombreArchivo : nombresArchivos) {
                                 System.out.println(nombreArchivo);
                             }
                             break;
                         case 3:
                         System.out.println("\n**CREAR CARPETA LOCAL**\n");
                             /*Crear carpeta Localmente */
                             crearCarpeta(LOCAL_FOLDER_PATH);
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
                             System.out.println("Archivos disponibles en la carpeta remota:\n");
                             mostrarCarpeta(LOCAL_FOLDER_PATH, 0);
                             /*Parte para borrar el archivo */

                             Scanner scannerr = new Scanner(System.in);
                             System.out.print("Introduce el nombre del archivo/carpeta a borrar: ");
                             String rutaArchivo = LOCAL_FOLDER_PATH+"\\"+scannerr.nextLine();
                             File archivoDelete = new File(rutaArchivo);

                             borrarArchivoCarpeta(archivoDelete);
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
 
                            /*Respuesta del servidor al cliente */
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
                             if (validaRuta(nuevaRutaLocal)) {
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
                             if(validaRuta(nuevaRutaRemota)){
                                 out.writeUTF(nuevaRutaRemota);
                                 
                             }else{
                                 System.out.println("La ruta ingresada no es válida. No se actualizará la ruta del directorio remoto.");
                             }
                             break;
                         case 9:
                             /* Enviar archivos de la carpeta local a la remota*/
                             break;
                         case 10:
                             /*Enviar carpetas de la carpeta local a la remota */
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
                         case 12:/*Enviar carpetas desde el local al remoto */
                         break;
                         case 13:/*Enviar archivos desde el remoto al local */
                         break;
                         case 14:/*Enviar carpetas desde el remoto al local */
                         break;
                         case 15:
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
 
     public static void crearCarpeta(String folderLocal){
         Scanner scanner = new Scanner(System.in);
         System.out.println("Ingrese el nombre de la carpeta a crear:");
 
         String carpetaNueva = folderLocal+"\\"+scanner.next();
 
         
         File carpeta = new File(carpetaNueva);
 
         // Verificar si la carpeta ya existe
         if (!carpeta.exists()) {
             // Intentar crear la carpeta
             if (carpeta.mkdir()) {
                 System.out.println("Se creo la nueva carpeta.");
             } else {
                 System.out.println("No se creo la carpeta.");
             }
         } else {
             System.out.println("La carpeta ya existe.");
         }
     }
 
     public static void borrarArchivoCarpeta(File archivo){
 
        
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
            } else {
                System.out.println("No se borró el archivo/carpeta: " + archivo.getAbsolutePath());
            }
        } else {
            System.out.println("El archivo/carpeta no existe: " + archivo.getAbsolutePath());
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
 }