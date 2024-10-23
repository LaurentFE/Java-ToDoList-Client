package fr.LaurentFE.todolistclient;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class MainWindow extends JFrame {

    private MainWindow() {
        super("Todo lists manager");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(1080, 768);
        this.setLocationRelativeTo(null);

        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(this.createToolBar(), BorderLayout.NORTH);
        contentPane.add(this.createUserPanel(), BorderLayout.WEST);
        contentPane.add(this.createToDoListDisplay(), BorderLayout.CENTER);
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton button1 = new JButton(new ImageIcon("src/main/resources/new.png"));
        toolBar.add(button1);
        JButton button2 = new JButton("Button 2");
        button2.setPreferredSize(new Dimension(100, 30));
        toolBar.add(button2);
        JCheckBox checkBox1 = new JCheckBox("CheckBox 1");
        checkBox1.setPreferredSize(new Dimension(100, 30));
        toolBar.add(checkBox1);
        JTextField textField1 = new JTextField("TextField 1");
        textField1.setPreferredSize(new Dimension(200, 30));
        toolBar.add(textField1);

        return toolBar;
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton button1 = new JButton("User 1");
        button1.setPreferredSize(new Dimension(100, 30));
        panel.add(button1);
        JButton button2 = new JButton("User 2");
        button2.setPreferredSize(new Dimension(100, 30));
        panel.add(button2);

        return panel;
    }

    private JPanel createToDoListDisplay() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JTextArea textArea = new JTextArea("Edit me");
        textArea.setPreferredSize(new Dimension(100,100));
        panel.add(textArea);
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Nimbus Look and feel not supported");
        }

        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }
}