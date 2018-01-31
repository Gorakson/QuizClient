/**
 * Hochschule München, Fakultät 07. 
 * Softwareentwicklung II Praktikum, IF2A, SS2016
 * Lösung der 2. Aufgabe
 * Oracle Java SE 8u77
 * OS Win7 64, RAM 8GB, CPU 4x2.5GHz x64
 */

package edu.hm.cs.game.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Client application for question game.
 */
@SuppressWarnings("serial")
public class QuizClient extends JFrame implements TextLog {

    /** first label for any message to the client. */
    private JLabel messageLabel;
    /** second label for any message to the client. */
    private JLabel protocolLabel;
    /** panel to show current question. */
    private JEditorPane questionPanel;
    /** panel to show current state of the game. */
    private JEditorPane protocolPanel;
    /** button to send a yes answer. */
    private JButton yesButton;
    /** button to send a no answer. */
    private JButton noButton;
    /** Connection to the server. */
    private final ServerConnection serverConnection;

    /**
     * Initialize the frame.
     * @throws IOException 
     */
    public QuizClient() throws IOException {
        initGui();
        final InetAddress host = InetAddress.getByName(null);
        final int port = 1337;

        serverConnection = new ServerConnection(new Socket(host, port), this);
        new Thread(serverConnection).start();
    }

    /**
     * Initialize frame.
     */
    public void initGui() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final int x = 100;
        final int y = 100;
        final int width = 800;
        final int height = 300;
        setBounds(x, y, width, height);
        final JPanel contentPane = new JPanel();
        final int border = 5;
        contentPane.setBorder(new EmptyBorder(border, border, border, border));
        setContentPane(contentPane);
        contentPane.setLayout(new GridLayout(0, 2, 0, 0));
        contentPane.add(initLeftPanel());
        contentPane.add(initRightPanel());
        messageLabel.setText("message");
        protocolLabel.setText("protocol");

        yesButton.addActionListener(al -> {
            serverConnection.postAnswer("Ja");
            yesButton.setEnabled(false);
            noButton.setEnabled(false);
        });
        noButton.addActionListener(al -> {
            serverConnection.postAnswer("Nein");
            yesButton.setEnabled(false);
            noButton.setEnabled(false);
        });
    }

    /**
     * Create left panel.
     * 
     * @return left panel with elements
     */
    public JPanel initLeftPanel() {
        final JPanel leftPanel = new JPanel();

        leftPanel.setLayout(new BorderLayout(0, 0));

        final JPanel buttonPanel = new JPanel();
        leftPanel.add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setLayout(new GridLayout(0, 2, 0, 0));

        yesButton = new JButton("Ja");
        buttonPanel.add(yesButton);

        noButton = new JButton("Nein");
        buttonPanel.add(noButton);

        final JPanel messagePanel = new JPanel();
        leftPanel.add(messagePanel, BorderLayout.NORTH);

        messageLabel = new JLabel();
        messagePanel.add(messageLabel);

        final JScrollPane scrollPane1 = new JScrollPane();
        leftPanel.add(scrollPane1, BorderLayout.CENTER);

        questionPanel = new JEditorPane();
        questionPanel.setEditable(false);
        scrollPane1.setViewportView(questionPanel);
        return leftPanel;
    }

    /**
     * Create right panel.
     * 
     * @return right panel with elements
     */
    public JPanel initRightPanel() {
        final JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(0, 0));

        final JScrollPane scrollPane = new JScrollPane();
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        protocolPanel = new JEditorPane();
        protocolPanel.setEditable(false);
        scrollPane.setViewportView(protocolPanel);

        final JPanel messagePanel2 = new JPanel();
        rightPanel.add(messagePanel2, BorderLayout.NORTH);

        protocolLabel = new JLabel();
        messagePanel2.add(protocolLabel);
        return rightPanel;
    }

    @Override
    public void log(LogType type, String text) {
        try {
            final Document doc;
            final JEditorPane pane;

            if (type == LogType.Protocol) {
                doc = protocolPanel.getDocument();
                pane = protocolPanel;
            } else { // Question
                yesButton.setEnabled(true);
                noButton.setEnabled(true);
                doc = questionPanel.getDocument();
                pane = questionPanel;
            }

            pane.setText(doc.getText(0, doc.getLength()) + text + System.lineSeparator());
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launch the application.
     * 
     * @param args
     *            values to start application
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                final QuizClient frame;
                try {
                    frame = new QuizClient();
                    frame.setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}

