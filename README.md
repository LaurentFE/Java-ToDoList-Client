# To Do List - Client

The main branch of the project is made to be interfaced with the Java ToDo List Server.

If you desire to use the newer ToDo List Server, made on the Spring Boot framework, please check the appropriate branch
of the repository : `compatible-with-Spring-Boot-server-version`.

## Usage

Configure the API Server address in src/main/resources/target-server.json to allow this client to send REST API calls 
to the java-ToDoList-Server (https://github.com/LaurentFE/java-ToDoList-Server) set up on your web server.

Use Maven to compile & build.

Launch the resulting Java application to open the GUI. 
GUI will not start (or rather, will crash with a Runtime Error, crude and unrefined) if it fails to connect properly to the ToDoList-Server.

## What

This Java application provides the user with a GUI to interface with the Java To Do List Server
(https://github.com/LaurentFE/java-ToDoList-Server) managing the MySQL DB keeping the todo lists stored.

On the leftmost panel are displayed the current known users of the system. You can select a user by clicking on the 
corresponding button, or create a new one. 

To do so, click on the "Add User" button in the toolbar, the "File>Add User" item in the menu bar, or press `CTRL+U`.

When a user is selected, in the middle panel will appear all known todo lists used by the selected user. You can select
 a todo list by clicking on the corresponding button or create a new one. 

To do so, click on the "Add Todo List" button in the toolbar, the "File>Add Todo List" item in the menu bar, or press 
`CTRL+T`. 

You can also open all of this user's todo lists by using the "Open All Lists" button in the toolbar, the "File>Open All 
Lists" item in the menu bar, the button in the displayed list of todo lists, or press `CTRL+O`.

Opening one (or many) todo lists will show the todo list and its items in the bigger, rightmost panel. 

In this panel, the toolbar provides buttons for editing the list's name, or add a new item to the list. 

Each item is prefixed with a checkbox, to check or uncheck any item of the list.

On the far end of each item is a button to edit the item's label.

This application only encodes the API requests in UTF-8 and doesn't handle all special characters.

This application also does not enforce any check on the user's inputs.

Please mind that on the default SQL scripts provided for the server in the server repo, all VARCHAR columns are limited
to 45 chars, and this client application doesn't check that you don't exceed this length.


## How

Programmed in Java using IDE IntelliJ, GUI developed with Swing.

Built with Maven.

All icons come from https://www.untitledui.com/free-icons/
