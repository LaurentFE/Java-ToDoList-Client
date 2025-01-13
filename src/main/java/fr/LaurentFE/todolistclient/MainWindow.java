package fr.LaurentFE.todolistclient;

import com.google.gson.Gson;
import fr.LaurentFE.todolistclient.config.ServerManager;
import fr.LaurentFE.todolistclient.config.ServerConfig;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

public class MainWindow extends JFrame {

    private UserList userList;
    private ToDoListList listList;
    private final ServerConfig conf;
    private String currentUser;
    private JDesktopPane desktopPane;
    private JSplitPane listSplitPane;

    private MainWindow() {
        super("Todo lists manager");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                confirmClose();
            }
        });
        this.setSize(1400, 700);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon("src/main/resources/icon.png").getImage());
        this.setJMenuBar(this.createMenuBar());

        ServerManager.getInstance().loadServerConfig();
        conf = ServerManager.getInstance().getServerConfig();

        this.userList = new UserList(new ArrayList<>());

        refreshContentPane();
    }

    private void refreshContentPane() {
        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.removeAll();

        contentPane.setLayout(new BorderLayout());

        contentPane.add(this.createToolBar(), BorderLayout.NORTH);
        contentPane.add(this.createMainDisplay(), BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();
    }

    public void refreshListContentPane() {
        listSplitPane.setLeftComponent(createListPane());
        listSplitPane.getLeftComponent().revalidate();
        listSplitPane.getLeftComponent().repaint();
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');

        JMenuItem addUser = new JMenuItem(actAddUser);
        menuFile.add(addUser);

        JMenuItem addToDoList = new JMenuItem(actAddNewToDoList);
        menuFile.add(addToDoList);

        menuFile.addSeparator();

        JMenuItem openAllLists = new JMenuItem(actOpenAllLists);
        menuFile.add(openAllLists);

        menuFile.addSeparator();

        JMenuItem mnuExit = new JMenuItem(actExit);
        menuFile.add(mnuExit);

        menuBar.add(menuFile);
        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(actAddUser);
        toolBar.add(actAddNewToDoList);
        toolBar.add(actOpenAllLists);

        return toolBar;
    }

    private JSplitPane createMainDisplay() {
        JScrollPane leftPanel = createUserPanel();
        JScrollPane rightPanel = createListScrollPane();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setResizeWeight(0);

        return splitPane;
    }

    private JScrollPane createUserPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;

        getUsers();
        if (this.userList != null) {
            for (int i = 0; i < this.userList.getUsers().size(); i++) {
                JButton button = new JButton(this.userList.getUsers().get(i).getUserName());
                button.addActionListener(actGetLists);
                c.gridx = 0;
                c.gridy = i + 1;
                if (i == this.userList.getUsers().size() - 1) {
                    c.weighty = 1;
                }
                panel.add(button, c);
            }
        }

        return new JScrollPane(panel);
    }

    private JScrollPane createListScrollPane() {

        listSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        desktopPane = new JDesktopPane();

        listSplitPane.setLeftComponent(new JScrollPane(createListPane()));
        listSplitPane.setRightComponent(desktopPane);
        listSplitPane.setResizeWeight(0);

        return new JScrollPane(listSplitPane);
    }

    private JPanel createListPane() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;

        this.getLists();

        if (this.listList != null) {
            JButton openAll = new JButton("Open all lists");
            openAll.addActionListener(actOpenAllLists);
            c.gridx = 0;
            c.gridy = 0;
            c.weighty = 0;
            panel.add(openAll, c);
            for (int i=0; i<this.listList.getLists().size(); i++) {
                JButton button = new JButton(this.listList.getLists().get(i).getLabel());
                button.addActionListener(actOpenList);
                c.gridx = 0;
                c.gridy = i+1;
                if (i==this.listList.getLists().size()-1) {
                    c.weighty = 1;
                }
                panel.add(button, c);
            }
        }

        return panel;
    }

    public int getUserId(String userName) {
        int userId=-1;
        for (User user: userList.getUsers()) {
            if (user.getUserName().equals(userName)) {
                userId = user.getUserId();
            }
        }
        return userId;
    }

    private void getUsers() {
        String endpoint = this.conf.getServer_url() + "rest/users";
        String response = ServerManager.sendGetRequest(endpoint);
        if(response.contains("userId")) {
            Gson gson = new Gson();
            String parsableJson = "{ \"users\":" + response + "}";
            this.userList = gson.fromJson(parsableJson, UserList.class);
        }
    }

    private void getLists() {
        int currentUserId = getUserId(currentUser);
        String endpoint = this.conf.getServer_url() + "rest/toDoLists?userId="+currentUserId;
        if (this.currentUser != null) {
            String response = ServerManager.sendGetRequest(endpoint);
            if(response.contains("listId")) {
                Gson gson = new Gson();
                String parsableJson = "{ \"lists\":" + response + "}";
                this.listList = gson.fromJson(parsableJson, ToDoListList.class);
            } else {
                listList = null;
            }
        }
    }

    private final AbstractAction actAddUser = new AbstractAction() {
        {
            putValue(Action.NAME, "Add User");
            putValue(Action.SHORT_DESCRIPTION, "Add User");
            putValue(Action.SMALL_ICON, new ImageIcon("src/main/resources/add-user.png"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
            putValue(Action.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent evt) {
            String userName = JOptionPane.showInputDialog(
                    null,
                    "Enter new user name",
                    "Add user",
                    JOptionPane.QUESTION_MESSAGE);
            if (userName != null) {
                userName = userName.replace("\"","");
                if (userList!= null && userNameAlreadyExists(userName)){
                    JOptionPane.showMessageDialog(null,
                            "A user with this name already exists",
                            "Error trying to create new user",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (userName.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "A user name must be composed of at least one character, different from the character \".",
                            "Error trying to create new user",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String endpoint = conf.getServer_url() + "rest/users";
                String requestBody = "{ \"userName\": \""+userName+"\" }";
                ServerManager.sendPostRequest(endpoint, requestBody);
                currentUser = userName;
                refreshContentPane();
            }
        }
    };

    private final AbstractAction actGetLists = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            JButton userButton = (JButton) evt.getSource();
            currentUser = userButton.getText();
            refreshContentPane();
            ToDoListWindow.resetOpenFrameCount();
        }
    };

    private final AbstractAction actOpenList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            JButton listButton = (JButton) evt.getSource();
            String chosenList = listButton.getText();
            for(ToDoList list : listList.getLists()) {
                if (chosenList.equals(list.getLabel())) {
                    openListIfNotOpened(list);
                }
            }
        }
    };

    private final AbstractAction actOpenAllLists = new AbstractAction() {
        {
            putValue(Action.NAME, "Open All Lists");
            putValue(Action.SHORT_DESCRIPTION, "Open All Lists");
            putValue(Action.SMALL_ICON, new ImageIcon("src/main/resources/open-todo-lists.png"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
            putValue(Action.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (listList != null) {
                for (ToDoList list : listList.getLists()) {
                    openListIfNotOpened(list);
                }
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "No User has been selected",
                        "Error trying to open all lists",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private final AbstractAction actAddNewToDoList = new AbstractAction() {
        {
            putValue(Action.NAME, "Add Todo List");
            putValue(Action.SHORT_DESCRIPTION, "Add Todo List");
            putValue(Action.SMALL_ICON, new ImageIcon("src/main/resources/add-todo-list.png"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
            putValue(Action.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentUser != null) {
                String listName = JOptionPane.showInputDialog(
                        null,
                        "Enter new todo list name",
                        "Add todo list",
                        JOptionPane.QUESTION_MESSAGE);
                if (listName != null) {
                    listName = listName.replace("\"", "");
                    if (listList!= null && listNameAlreadyExists(listName, 0)){
                        JOptionPane.showMessageDialog(null,
                                "This user already has a list with this name",
                                "Error trying to create new todo list",
                                JOptionPane.ERROR_MESSAGE);
                                return;
                    } else if (listName.isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                                "A list name must be composed of at least one character, different from the " +
                                        "character \".",
                                "Error trying to create new todo list",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    int userId = getUserId(currentUser);
                    String endpoint = conf.getServer_url()
                            + "rest/toDoLists";
                    String requestBody = "{ \"userId\": " + userId + ", \"label\": \"" + listName + "\" }";
                    String response = ServerManager.sendPostRequest(endpoint, requestBody);
                    refreshListContentPane();
                    Gson gson = new Gson();
                    ToDoList toDoList = gson.fromJson(response, ToDoList.class);
                    openListIfNotOpened(toDoList);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "No User has been selected",
                        "Error trying to create new todo list",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    private final AbstractAction actExit = new AbstractAction() {
        {
            putValue(Action.NAME, "Exit");
            putValue(Action.SHORT_DESCRIPTION, "Exit");
            putValue(Action.SMALL_ICON, new ImageIcon("src/main/resources/exit.png"));
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
            putValue(Action.ACCELERATOR_KEY,
                    KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK));
        }
        @Override
        public void actionPerformed(ActionEvent evt) {
            confirmClose();
        }
    };

    private void openListIfNotOpened(ToDoList list) {
        JInternalFrame[] openedLists = desktopPane.getAllFrames();
        if (openedLists.length != 0) {
            ArrayList<String> openedListNames = new ArrayList<>();
            for (JInternalFrame frame : openedLists) {
                openedListNames.add(frame.getTitle());
            }
            if (!openedListNames.contains(list.getLabel())) {
                openList(list);
            } else {
                for (JInternalFrame frame : openedLists) {
                    if (frame.getTitle().equals(list.getLabel())) {
                        try {
                            frame.setSelected(true);
                        } catch (PropertyVetoException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } else {
            openList(list);
        }
    }

    private void openList(ToDoList list) {
        ToDoListWindow todo = new ToDoListWindow(this, list, currentUser);
        todo.setVisible(true);
        desktopPane.add(todo);
        todo.moveToFront();
        try {
            todo.setSelected(true);
        } catch (PropertyVetoException e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmClose() {
        int exitValue = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to exit ?",
                "Exit",
                JOptionPane.YES_NO_OPTION);
        if (exitValue == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    public boolean userNameAlreadyExists(String userName) {
        for (User user : userList.getUsers()) {
            if (user.getUserName().equals(userName)){
                return true;
            }
        }
        return false;
    }

    public boolean listNameAlreadyExists(String listName, Integer disregardId) {
        for (ToDoList todo : listList.getLists()) {
            if (todo.getLabel().equals(listName) && !todo.getListId().equals(disregardId)){
                return true;
            }
        }
        return false;
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