package fr.LaurentFE.todolistclient;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class ToDoListWindow extends JInternalFrame {

    private final ToDoList toDoList;
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;

    public ToDoListWindow(ToDoList list) {
        super(list.getLabel(), true, true, false, false);
        openFrameCount++;

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameClosing(InternalFrameEvent e) {
                openFrameCount--;
                dispose();
            }
        });
        this.setSize(400, 300);

        this.setLocation(xOffset * openFrameCount, yOffset * openFrameCount);

        //this.setIconImage(new ImageIcon("src/main/resources/todolist.png").getImage());
        this.setJMenuBar(this.createMenuBar());

        toDoList = list;

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

        for (ListItem item : toDoList.getItems()) {
            panel.add(createListItemDisplay(item));
        }

        return panel;
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

    public static void resetOpenFrameCount() {
        openFrameCount = 0;
    }
}
