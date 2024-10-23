package fr.LaurentFE.todolistclient;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

public class MainWindow extends JFrame {

    private MainWindow() {
        super("Todo lists manager");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(740, 800);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon("src/main/resources/icon.png").getImage());
        this.setJMenuBar(this.createMenuBar());

        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(this.createToolBar(), BorderLayout.NORTH);
        contentPane.add(this.createMainDisplay(), BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');

        JMenuItem addUser = new JMenuItem("Add User");
        addUser.setIcon(new ImageIcon("src/main/resources/add-user.png"));
        menuFile.add(addUser);

        JMenuItem addToDoList = new JMenuItem("Add Todo List");
        addToDoList.setIcon(new ImageIcon("src/main/resources/add-todo-list.png"));
        menuFile.add(addToDoList);

        menuFile.addSeparator();

        JMenuItem openAllLists = new JMenuItem("Open All Todo Lists");
        openAllLists.setIcon(new ImageIcon("src/main/resources/open-todo-lists.png"));
        menuFile.add(openAllLists);

        menuFile.addSeparator();

        JMenuItem mnuExit = new JMenuItem("Exit");
        mnuExit.setIcon(new ImageIcon("src/main/resources/exit.png"));
        menuFile.add(mnuExit);

        menuBar.add(menuFile);
        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        /*
            When creating a new user : should reload UserPanel
         */
        JButton addUser = new JButton(new ImageIcon("src/main/resources/add-user.png"));
        toolBar.add(addUser);
        JButton addList = new JButton(new ImageIcon("src/main/resources/add-todo-list.png"));
        toolBar.add(addList);
        JButton openAllLists = new JButton(new ImageIcon("src/main/resources/open-todo-lists.png"));
        toolBar.add(openAllLists);

        return toolBar;
    }

    private JSplitPane createMainDisplay() {
        JScrollPane leftPanel = createUserPanel();
        JScrollPane rightPanel = createListPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setResizeWeight(0.5);

        return splitPane;
    }

    /*
        Should fetch all users from DB
     */
    private JScrollPane createUserPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton user1 = new JButton("User 1");
        panel.add(user1);
        JButton user2 = new JButton("123456789012345678901234567890123456789012345");
        panel.add(user2);

        return new JScrollPane(panel);
    }

    /*
        When a user is selected, should fetch all user's lists from DB
    */
    private JScrollPane createListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton openAll = new JButton("Open all lists");
        openAll.setIcon(new ImageIcon("src/main/resources/open-todo-lists.png"));
        panel.add(openAll);
        JButton list1 = new JButton("List 1");
        panel.add(list1);
        JButton list2 = new JButton("123456789012345678901234567890123456789012345");
        panel.add(list2);

        return new JScrollPane(panel);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Nimbus Look and feel not supported");
        }

        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);

        /*
            Temporary : this will be called upon selecting a todolist for a given user
         */
        ToDoListWindow todo = new ToDoListWindow();
        todo.setVisible(true);

    }
}