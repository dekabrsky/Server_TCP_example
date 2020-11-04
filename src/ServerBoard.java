import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerBoard extends JFrame {
    private JTextArea messagesArea;
    private JButton sendButton;
    private JTextField message;
    private JButton startServer;
    private TCPServer mServer;

    public ServerBoard() {

        super("ServerBoard");

        JPanel panelFields = new JPanel();
        panelFields.setLayout(new BoxLayout(panelFields,BoxLayout.X_AXIS));

        JPanel panelFields2 = new JPanel();
        panelFields2.setLayout(new BoxLayout(panelFields2,BoxLayout.X_AXIS));

        //здесь у нас будет экран текстовых сообщений
        messagesArea = new JTextArea();
        messagesArea.setColumns(30);
        messagesArea.setRows(10);
        messagesArea.setEditable(false);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // получить сообщение из текствьюшки
                String messageText = message.getText();
                // добавляет сообщение в область area
                messagesArea.append("\n" + messageText);
                // отправляет сообщение клиенту
                mServer.sendMessage(messageText);
                // очищает сообщение
                message.setText("");
            }
        });

        startServer = new JButton("Start");
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // отключает кнопку пуска
                startServer.setEnabled(false);

                //создает объект OnMessageReceived, запрошенный конструктором TCPServer
                mServer = new TCPServer(new TCPServer.OnMessageReceived() {
                    @Override
                    //этот метод объявленный в интерфейсе из класса TCPServer реализован здесь
                    //этот метод на самом деле является методом обратного вызова, потому что он будет
                    //выполняться каждый раз, когда он будет вызван из класса TCPServer (в while)
                    public void messageReceived(String message) {
                        messagesArea.append("\n "+message);
                    }
                });
                mServer.start();

            }
        });

        //поле, в которое пользователь вводит текст (EditText вызывается в Android)
        message = new JTextField();
        message.setSize(200, 20);

        //добавляем кнопки и текстовые поля на панель
        panelFields.add(messagesArea);
        panelFields.add(startServer);

        panelFields2.add(message);
        panelFields2.add(sendButton);

        getContentPane().add(panelFields);
        getContentPane().add(panelFields2);

        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

        setSize(300, 170);
        setVisible(true);
    }
}