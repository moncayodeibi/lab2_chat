/*ActionEvent nos muestra si existe un evento y qu elo ha producido*/
import java.awt.event.ActionEvent;
/*ActionListener nos ayuda a manejar los eventos en acci[on*/
import java.awt.event.ActionListener;
import java.io.BufferedReader;
/*blibliotecas de manejo de excepciones*/
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/*las siguientes importaciones son las necesarias para 
que se muestre todo en interfaz gráfica, nos ayuda a configurar 
el frame o patallas*/

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient {

    /**/
     /*Declaramos las variables de uso para leer e imprimir los mensajes*/
    BufferedReader in;
    PrintWriter out;
    /**/
    JFrame frame = new JFrame("Chat múltiples clientes");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    
    /*constructor de la clase ChatClient donde inicializamos las variables antes definidas*/
    public ChatClient() {

        // a continiación se implementa el códido de la inferfaz gráfico
        textField.setEditable(false);   //la caja de exto se encuentra deshabilitada
        messageArea.setEditable(false); //el área de texto se nuestras deshabilitada
        frame.getContentPane().add(textField, "North");//lugar donde va a reflejarse el texto escrito
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        // añadimos la clase espera o escuha para la espera de un texto dentro del chat
        textField.addActionListener(new ActionListener() {
           //metodo que ayuda al manejo del texto ingresado
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());//se imprime el texto ingresado
                textField.setText(""); //lueve el texto a ser vacío
            }
        });
    }
    
    /**
     método de ingreso de la ip del servidor del chat
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Ingresa la IP del servidor",
            "Bienvenido al chat",
            JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * método para escojer un nombre dentro del chat o nickname
     */
    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    private void run() throws IOException {

        // colocamos dentro de la variable serverAddress la ip del servidor por el método get
        String serverAddress = getServerAddress();
        //instanciamos de la clase Socket con la ip del sevidor y con la puerta de enlace 9001
        Socket socket = new Socket(serverAddress, 9001);
        //dentro de la variable in colocamos lo que ingresamos
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        //dentro de la variable out colocamos lo que deseamos imprimir
        out = new PrintWriter(socket.getOutputStream(), true);

        // entramos dentro de un lazo  para seguir enviando mensajes en el chat mientras se mantenga en verdadero
        while (true) {
            //declaramos una variable string line para colocar lo que escribarmos 
            String line = in.readLine();
            //si el nombre es correcto lo imprime
            if (line.startsWith("SUBMITNAME")) {
                out.println(getName());
                //si el nombre es correcto lo setea
            } else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
                //si el texto ingresado es un mensaje lo imprime saltando una línea
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            }
        }
    }
    
    /**
     * la clase aplicación para poder enviar el mensaje, es la clase principal de ejecución
     */
    public static void main(String[] args) throws Exception {
        //instanciamos la clase ChatClient
        ChatClient client = new ChatClient();
        //corremos la pantalla creada en la clase ChatClient
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //hacemos visible la pantalla
        client.frame.setVisible(true);
        client.run(); //corrremos la apliación del chat
    }
    
}
