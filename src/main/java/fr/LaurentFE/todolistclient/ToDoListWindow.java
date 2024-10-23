package fr.LaurentFE.todolistclient;

import javax.swing.*;
import java.awt.*;

public class ToDoListWindow extends JFrame {
    /*
        Will accept a ToDoList object
     */
    public ToDoListWindow() {
        super("Todo list name");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 300);
        /*
            center of the screen, + offset for every todolist open, to cascade properly
         */
        this.setLocationRelativeTo(null);

        this.setIconImage(new ImageIcon("src/main/resources/todolist.png").getImage());
        this.setJMenuBar(this.createMenuBar());

        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(this.createToolBar(), BorderLayout.NORTH);
        contentPane.add(this.createItemsDisplay(), BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic('F');

        JMenuItem addItem = new JMenuItem("Add Item");
        addItem.setIcon(new ImageIcon("src/main/resources/add-item.png"));
        menuFile.add(addItem);

        menuFile.addSeparator();

        JMenuItem editListName = new JMenuItem("Edit List Name");
        editListName.setIcon(new ImageIcon("src/main/resources/edit-list.png"));
        menuFile.add(editListName);

        menuFile.addSeparator();

        JMenuItem closeList = new JMenuItem("Close List");
        closeList.setIcon(new ImageIcon("src/main/resources/exit.png"));
        menuFile.add(closeList);

        menuBar.add(menuFile);
        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addItem = new JButton(new ImageIcon("src/main/resources/add-item.png"));
        toolBar.add(addItem);
        JButton editListName = new JButton(new ImageIcon("src/main/resources/edit-list.png"));
        toolBar.add(editListName);

        return toolBar;
    }

    private JPanel createItemsDisplay() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        /*
            For each list item
         */
        panel.add(createListItemDisplay());
        panel.add(createListItemDisplay());
        panel.add(createListItemDisplay());
        panel.add(createListItemDisplay());

        return panel;
    }

    private JPanel createListItemDisplay() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
        JCheckBox checkBox = new JCheckBox();
        JLabel label = new JLabel("item_name");

        JPanel editPanel = new JPanel();
        JButton editItem = new JButton(new ImageIcon("src/main/resources/edit-item.png"));

        listPanel.add(checkBox);
        listPanel.add(label);
        editPanel.add(editItem);

        panel.add(listPanel, BorderLayout.CENTER);
        panel.add(editPanel, BorderLayout.EAST);

        return panel;
    }
}
