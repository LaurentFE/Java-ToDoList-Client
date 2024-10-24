package fr.LaurentFE.todolistclient;

import com.google.gson.Gson;
import fr.LaurentFE.todolistclient.config.ServerManager;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ToDoListWindow extends JInternalFrame {

    private ToDoList toDoList;
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    static final int baseHeight = 70;
    static final int heightIncrement = 50;
    private final String userName;

    public ToDoListWindow(ToDoList list, String userName) {
        super(list.getLabel(), true, true, false, false);
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

        JButton editListName = new JButton(new ImageIcon("src/main/resources/edit-list.png"));
        toolBar.add(editListName);
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
        JCheckBox checkBox = new JCheckBox("",item.isChecked());
        JLabel label = new JLabel(item.getLabel());

        JPanel editPanel = new JPanel();
        JButton editItem = new JButton(new ImageIcon("src/main/resources/edit-item.png"));

        listPanel.add(checkBox);
        listPanel.add(label);
        editPanel.add(editItem);

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.EAST);

        return panel;
    }

    private void refreshToDoList() {
        String escapedUserName = ServerManager.escapeLabelForAPI(userName);
        String escapedListName = ServerManager.escapeLabelForAPI(toDoList.getLabel());
        String endpoint = ServerManager.getInstance()
                .getServerConfig().getServer_url() + "/rest/ToDoList?user_name=" + escapedUserName
                + "&list_name=" + escapedListName;

        String response = ServerManager.sendGetRequest(endpoint);
        Gson gson = new Gson();
        this.toDoList = gson.fromJson(response, ToDoList.class);
    }

    public static void resetOpenFrameCount() {
        openFrameCount = 0;
    }

    private final AbstractAction actAddItem = new AbstractAction() {
        {
            putValue(Action.NAME, "Add Item");
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
                String escapedUserName = ServerManager.escapeLabelForAPI(userName);
                String escapedListName = ServerManager.escapeLabelForAPI(toDoList.getLabel());
                String escapedItemName = ServerManager.escapeLabelForAPI(itemName);
                String endpoint = ServerManager.getInstance()
                        .getServerConfig().getServer_url() + "/rest/ListItem?user_name=" + escapedUserName
                        + "&list_name=" + escapedListName
                        + "&item_name=" + escapedItemName;
                ServerManager.sendPostRequest(endpoint);
                refreshToDoList();
                refreshContentPane();
            }
        }
    };
}
