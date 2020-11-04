import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Этот класс расширяет класс Thread, чтобы мы могли получать и отправлять сообщения одновременно
 */
public class TCPServer extends Thread {

    public static final int SERVERPORT = 4444;
    private boolean running = false;
    private PrintWriter mOut;
    private OnMessageReceived messageListener;

    public static void main(String[] args) {

        //открывает окно, в котором будут приниматься и отправляться сообщения
        ServerBoard frame = new ServerBoard();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * Конструктор класса
     * @param messageListener прослушивает сообщения
     */
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Метод отправки сообщений с сервера на клиент
     * @param message сообщение, отправленное сервером
     */
    public void sendMessage(String message){
        if (mOut != null && !mOut.checkError()) {
            mOut.println(message);
            mOut.flush();
        }
    }

    @Override
    public void run() {
        super.run();

        running = true;

        try {
            System.out.println("S: Connecting...");

            //создание сокета сервера. Сокет сервера ожидает поступления запросов по сети.
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);

            //создание клиентского сокета... метод accept() прослушивает соединение, которое должно быть сделано с этим сокетом, и принимает его.
            Socket client = serverSocket.accept();
            System.out.println("S: Receiving...");

            try {

                //отправляет сообщение клиенту
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

                //читает сообщение, полученное от клиента
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                //здесь мы ждем получения сообщений от клиента (это бесконечный цикл)
                //этот вайл выступает в роли слушателя сообщений
                while (running) {
                    String message = in.readLine();

                    if (message != null && messageListener != null) {
                        //вызывает метод messageReceived из класса ServerBoard
                        messageListener.messageReceived(message);
                    }
                }

            } catch (Exception e) {
                System.out.println("S: Error");
                e.printStackTrace();
            } finally {
                client.close();
                System.out.println("S: Done.");
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }

    }

    //Объявление интерфейса. Метод messageReceived(строковое сообщение) должен быть реализован в ServerBoard
    //класс в StartServer, отвечающий за кнопку
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}
