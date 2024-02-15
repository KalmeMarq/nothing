package me.kalmemarq.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

public class ServerConsoleGui {
    public JTextArea textArea;
    public JTextField inputField;

    public Runnable onClose = () -> {
    };
    public Consumer<String> onSend = (msg) -> {
    };
    public JFrame frame;

    public ServerConsoleGui() {
        this.frame = new JFrame("Server");
        this.frame.setPreferredSize(new Dimension(400, 400));
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.setLayout(new BorderLayout());
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        this.frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ServerConsoleGui.this.onClose.run();
            }
        });

        this.textArea = new JTextArea();
        this.textArea.setEditable(false);
        contentPane.add(this.textArea, "Center");

        this.inputField = new JTextField();
        contentPane.add(this.inputField, "South");


        this.inputField.addActionListener(l -> {
            if (this.inputField.getText().trim().isEmpty()) return;
            this.onSend.accept(this.inputField.getText().trim());
            this.inputField.setText("");
        });

        this.frame.setContentPane(contentPane);

        this.frame.pack();
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setOnSend(Consumer<String> onSend) {
        this.onSend = onSend;
    }
}
