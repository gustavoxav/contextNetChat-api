package br.cefet.segaudit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageApp {
    private Comunicador comunicador;
    private JTextArea messageArea;
    private JTextField inputField;

    public MessageApp(Comunicador comunicador) {
        this.comunicador = comunicador;
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Encrypted Message APP - ContextNet");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 18));
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setPreferredSize(new Dimension(400, 70));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        inputField = new JTextField(25);
        inputField.setFont(new Font("Arial", Font.PLAIN, 22));
        inputField.setPreferredSize(new Dimension(250, 60));
        inputPanel.add(inputField);

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.PLAIN, 22));
        sendButton.setPreferredSize(new Dimension(100, 60));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                comunicador.sendMessage(message);
                messageArea.append("Voce: " + message + "\n");
                inputField.setText("");
            }
        });

        inputPanel.add(sendButton);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.NORTH);

        frame.setVisible(true);
    }

    public void displayReceivedMessage(String message) {
        messageArea.append("Destinatario: " + message + "\n");
    }
}
