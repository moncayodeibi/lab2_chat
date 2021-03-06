import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class ChatServer {
     private static final int PORT = 9001; //declaramos una variable para defiir el puerto de eschucha

    private static HashSet<String> names = new HashSet<String>(); //instanciamos una array tipo String para colocar el texto escrito
    
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();//y de igual manera un array para imprimir el texto escrito
   
    public static void main(String[] args) throws Exception {
        //Especificamos que el servidor está corriendo
	System.out.println("The chat server is running.");
	//ponemos en escucha el puerto 9001    
        ServerSocket listener = new ServerSocket(PORT);
        
	//mintras esto este escuchando mientras este corriendo    
	try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }
    
    /**
     Clase handler
     */
    private static class Handler extends Thread {
	 //declaramos algunas variables que nos van yudar a manejar el chat
        private String name; //variable del nombre del usuario del chat
        private Socket socket;//variable socket para la conexión
        private BufferedReader in; //variable de lectura
        private PrintWriter out; // variable de impresión en pantalla

        /**
         Constructor de inicialización del socket
         */
        public Handler(Socket socket) {
            this.socket = socket;
        }
        /**
        Método para correr la apliacación 
         */
        public void run() {
            try {

                // Instanciamos una variable de escritura
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
		     // Instanciamos una variable de impresión
                out = new PrintWriter(socket.getOutputStream(), true);

                // código para recibir el nombre del cliente
                while (true) {
		   //imprimios nombre guardado a lo escrito en el cliente al recibor el nombre 	
                    out.println("SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
		//sincronizamos nombres cuando se sigan añadiendo clientes a la aplicación
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        }
                    }
                }

                // Con los nombres sicronizados y acepatamos inciamos el socket de comunicación
                out.println("NAMEACCEPTED");
                writers.add(out);

                // Aceptamos los mensajes de todas las personas con nombre correcto
                while (true) {
			//mientras los clientes escriben se lee lo que se escribe y se coloca en la variable input
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
			//cuando el cliente envie el mensaje se coloca el nombre escojigo más lo que el cliente escribió en el mensaje	
                    for (PrintWriter writer : writers) {
                        writer.println("MESSAGE " + name + ": " + input);
                    }
                }
            } catch (IOException e) {
		    //variable de excepción en el caso de algún error
                System.out.println(e);
            } finally {
                //si algo sale mal se se quita el nombre 
                if (name != null) {
                    names.remove(name);
                }
                if (out != null) {
			//quiamamos el nombre y la escrtitura para que quede libre
                    writers.remove(out);
                }
                try {
			//cerramos el socket de la comunicación
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
