package fr.LaurentFE.todolistclient;

import com.google.gson.Gson;
import fr.LaurentFE.todolistclient.config.ConfigurationManager;
import fr.LaurentFE.todolistclient.config.ServerConfig;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class MainWindow extends JFrame {

    private UserList userList;
    private ToDoListList listList;
    private final ServerConfig conf;
    private String current_user;

    private MainWindow() {
        super("Todo lists manager");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(740, 800);
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon("src/main/resources/icon.png").getImage());
        this.setJMenuBar(this.createMenuBar());

        ConfigurationManager.getInstance().loadServerConfig();
        conf = ConfigurationManager.getInstance().getServerConfig();

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

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');

        JMenuItem addUser = new JMenuItem("Add User");
        addUser.setAction(actAddUser);
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

        toolBar.add(actAddUser);
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

    private JScrollPane createUserPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        getUsers();

        for (User user : this.userList.getUsers()) {
            JButton button = new JButton(user.getUser_name());
            button.addActionListener(getLists);
            panel.add(button);
            // TODO : add Action to button
        }

        return new JScrollPane(panel);
    }

    private JScrollPane createListPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton openAll = new JButton("Open all lists");
        openAll.setIcon(new ImageIcon("src/main/resources/open-todo-lists.png"));
        panel.add(openAll);

        this.getLists();

        if (this.listList != null) {
            for (ToDoList list : this.listList.getLists()) {
                JButton button = new JButton(list.getLabel());
                panel.add(button);
                // TODO : add Action to button
            }
        }

        return new JScrollPane(panel);
    }

    private void getUsers() {
        String endpoint = this.conf.getServer_url() + "/rest/Users";
            try (HttpClient httpClient = HttpClient.newHttpClient()) {
                HttpRequest getRequest = HttpRequest.newBuilder()
                        .uri(new URI(endpoint))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

                if(response.body().contains("user_id")) {
                    Gson gson = new Gson();
                    String parsableJson = "{ \"users\":" + response.body() + "}";
                    this.userList = gson.fromJson(parsableJson, UserList.class);
                }
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
    }

    private void getLists() {
        String endpoint = this.conf.getServer_url() + "/rest/ToDoLists?user_name=";
        if (this.current_user != null) {
            String userName = escapeLabelForAPI(this.current_user);
            endpoint += userName;
            System.out.println(endpoint);
            try(HttpClient httpClient = HttpClient.newHttpClient()) {
                HttpRequest getRequest = HttpRequest.newBuilder()
                        .uri(new URI(endpoint))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

                if(response.body().contains("list_id")) {
                    Gson gson = new Gson();
                    String parsableJson = "{ \"lists\":" + response.body() + "}";
                    this.listList = gson.fromJson(parsableJson, ToDoListList.class);
                } else {
                    listList = null;
                }
            } catch (URISyntaxException | InterruptedException | IOException e) {
                throw new RuntimeException(e);
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
                String escapedUserName = escapeLabelForAPI(userName);
                String endpoint = conf.getServer_url() + "/rest/User?user_name=" + escapedUserName;
                System.out.println(endpoint);
                try (HttpClient httpClient = HttpClient.newHttpClient()) {
                    HttpRequest postRequest = HttpRequest.newBuilder()
                            .uri(new URI(endpoint))
                            .POST(HttpRequest.BodyPublishers.ofString(""))
                            .build();
                    httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
                    current_user = userName;
                    refreshContentPane();
                } catch (IOException | InterruptedException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    };

    private final AbstractAction getLists = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            JButton userButton = (JButton) evt.getSource();
            current_user = userButton.getText();
            refreshContentPane();
        }
    };

    private String escapeLabelForAPI(String label) {
        return URLEncoder.encode(label, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%21", "!")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%7E", "~");
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
        //ToDoListWindow todo = new ToDoListWindow();
        //todo.setVisible(true);
    }
}