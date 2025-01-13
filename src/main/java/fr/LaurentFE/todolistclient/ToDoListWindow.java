package fr.LaurentFE.todolistclient;

import com.google.gson.Gson;
import fr.LaurentFE.todolistclient.config.ServerManager;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

public class ToDoListWindow extends JInternalFrame {

    private ToDoList toDoList;
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    static final int baseHeight = 70;
    static final int heightIncrement = 50;
    private final String userName;
    private final MainWindow mainWindowRef;

    public ToDoListWindow(MainWindow mainWindowRef, ToDoList list, String userName) {
        super(list.getLabel(), true, true, false, false);
        this.mainWindowRef = mainWindowRef;
        openFrameCount++;
        toDoList = list;
        this.userName = userName;

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                openFrameCount--;
                dispose();
            }
        });
        this.setSize(400, baseHeight);
        this.setLocation(xOffset * openFrameCount, yOffset * openFrameCount);

        refreshContentPane();
    }

    private void refreshContentPane() {
        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.removeAll();

        this.setTitle(toDoList.getLabel());

        contentPane.setLayout(new BorderLayout());

        if (toDoList.getItems() != null) {
            this.reshape(this.getX(),
                    this.getY(),
                    this.getWidth(),
                    baseHeight + heightIncrement * toDoList.getItems().size());
        }
        contentPane.add(this.createToolBar(), BorderLayout.NORTH);
        contentPane.add(this.createItemsDisplay(), BorderLayout.CENTER);
        contentPane.revalidate();
        contentPane.repaint();
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(actEditListName);
        toolBar.add(actAddItem);

        return toolBar;
    }

    private JScrollPane createItemsDisplay() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        if (toDoList.getItems() != null) {
            for (ListItem item : toDoList.getItems()) {
                panel.add(createListItemDisplay(item));
            }
        }

        return new JScrollPane(panel);
    }

    private JPanel createListItemDisplay(ListItem item) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
        JCheckBox checkBox = new JCheckBox("", item.isChecked());
        checkBox.setName(item.getLabel());
        checkBox.addItemListener(this::checkStateChanged);
        JLabel label = new JLabel(item.getLabel());

        JPanel editPanel = new JPanel();
        JButton editItem = new JButton(actEditItemName);
        editItem.setName(item.getLabel());

        listPanel.add(checkBox);
        listPanel.add(label);
        editPanel.add(editItem);

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.EAST);

        return panel;
    }

    void checkStateChanged(ItemEvent e) {
        JCheckBox checkBox = (JCheckBox) e.getSource();
        int itemId = -1;
        for (ListItem item: toDoList.getItems()) {
            if (item.getLabel().equals(checkBox.getName())) {
                itemId = item.getItemId();
            }
        }
        String endpoint = ServerManager.getInstance()
                .getServerConfig().getServer_url() + "rest/items/"+itemId;

        String requestBody = "{ \"label\": \"" + checkBox.getName() + "\"," +
                " \"checked\": " + checkBox.isSelected() + " }";
        ServerManager.sendPatchRequest(endpoint, requestBody);
        refreshToDoList();
        refreshContentPane();
        mainWindowRef.refreshListContentPane();
    }

    private void refreshToDoList() {
        String endpoint = ServerManager.getInstance()
                .getServerConfig().getServer_url() + "rest/toDoLists/"+toDoList.getListId();

        String response = ServerManager.sendGetRequest(endpoint);
        Gson gson = new Gson();
        this.toDoList = gson.fromJson(response, ToDoList.class);
    }

    public static void resetOpenFrameCount() {
        openFrameCount = 0;
    }

    private final AbstractAction actAddItem = new AbstractAction() {
        {
            putValue(Action.SHORT_DESCRIPTION, "Add Item");
            putValue(Action.SMALL_ICON, new ImageIcon("src/main/resources/add-item.png"));
        }
        @Override
        public void actionPerformed(ActionEvent evt) {
            String itemName = JOptionPane.showInputDialog(
                    null,
                    "Enter new list item label",
                    "Add item",
                    JOptionPane.QUESTION_MESSAGE);
            if (itemName != null) {
                itemName = itemName.replace("\"", "");
                if (itemName.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "An item name must be composed of at least one character, different from the " +
                                    "character \".",
                            "Error trying to create new item",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String endpoint = ServerManager.getInstance()
                        .getServerConfig().getServer_url() + "rest/items";
                String requestBody = "{ \"listId\": " + toDoList.getListId() + "," +
                        "\"label\": \"" + itemName + "\"," +
                        "\"checked\": false }";
                ServerManager.sendPostRequest(endpoint, requestBody);
                refreshToDoList();
                refreshContentPane();
                mainWindowRef.refreshListContentPane();
            }
        }
    };

    private final AbstractAction actEditListName = new AbstractAction() {
        {
            putValue(Action.NAME, "Edit List");
            putValue(Action.SHORT_DESCRIPTION, "Edit List");
            putValue(Action.SMALL_ICON, new ImageIcon("src/main/resources/edit-list.png"));
        }
        @Override
        public void actionPerformed(ActionEvent evt) {
            Object option = JOptionPane.showInputDialog(
                    null,
                    "Enter new list name",
                    "Edit list name",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    toDoList.getLabel());
            if (option != null) {
                String newListName = option.toString().replace("\"", "");
                if (mainWindowRef.listNameAlreadyExists(newListName, toDoList.getListId())) {
                    JOptionPane.showMessageDialog(null,
                            "This user already has a list with this name",
                            "Error trying to create new todo list",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (newListName.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "A list name must be composed of at least one character, different from the " +
                                    "character \".",
                            "Error trying to create new todo list",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String endpoint = ServerManager.getInstance()
                        .getServerConfig().getServer_url() + "rest/toDoLists/"+toDoList.getListId();
                String requestBody = "{ \"userId\": " + mainWindowRef.getUserId(userName) + "," +
                        "\"label\": \"" + newListName + "\" }";
                System.out.println(requestBody);
                ServerManager.sendPatchRequest(endpoint, requestBody);
                toDoList.setLabel(newListName);
                refreshToDoList();
                refreshContentPane();
                mainWindowRef.refreshListContentPane();
            }
        }
    };

    private final AbstractAction actEditItemName = new AbstractAction() {
        {
            putValue(Action.NAME, "Edit Item Name");
            putValue(Action.SHORT_DESCRIPTION, "Edit Item Name");
            putValue(Action.SMALL_ICON, new ImageIcon("src/main/resources/edit-item.png"));
        }
        @Override
        public void actionPerformed(ActionEvent evt) {
            JButton button = (JButton) evt.getSource();
            String itemName = button.getName();
            Object option = JOptionPane.showInputDialog(
                    null,
                    "Enter new item name",
                    "Edit item name",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    itemName);
            if (option != null) {
                String newItemName = option.toString().replace("\"", "");
                 if (newItemName.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "A list name must be composed of at least one character, different from the " +
                                    "character \".",
                            "Error trying to edit a todo list name",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                boolean checked = false;
                int itemId = -1;
                for (ListItem item: toDoList.getItems()) {
                    if (itemName.equals(item.getLabel())) {
                        checked = item.isChecked();
                        itemId = item.getItemId();
                    }
                }
                String endpoint = ServerManager.getInstance()
                        .getServerConfig().getServer_url() + "rest/items/" + itemId;
                String requestBody = "{ \"label\": \"" + newItemName + "\"," +
                        "\"checked\": " + checked + " }";
                ServerManager.sendPatchRequest(endpoint, requestBody);
                for (ListItem item : toDoList.getItems()) {
                    if (item.getLabel().equals(itemName)) {
                        item.setLabel(newItemName);
                        break;
                    }
                }
                refreshToDoList();
                refreshContentPane();
                mainWindowRef.refreshListContentPane();
            }
        }
    };
}
