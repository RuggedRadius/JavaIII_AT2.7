package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.util.List;

public class Controller
{
    // Attributes
    public ObservableList<FileTransfer> filesIncoming;
    public ObservableList<FileTransfer> filesOutgoing;

    public Server server;

    // FX Controls
    public TableView tblIncoming;
    public TableView tblOutgoing;

    public Label lblStatus;

    public TextField txtPortNumber;

    public void initialize() {
        initIncomingTable();
        initOutgoingTable();

        try
        {
            // Create server
            int port = Integer.parseInt(txtPortNumber.getText());
            server = new Server(port, this);

            // Launch server
            lblStatus.setText("Launching server...");
            server.launchServer();
            lblStatus.setText("Sever running");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initIncomingTable() {

        // Set table data
        filesIncoming = FXCollections.observableArrayList();
        tblIncoming.setItems(filesIncoming);

        // Create columns
        TableColumn<String, FileTransfer> colFileName = new TableColumn("File Name");
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colFileName.setPrefWidth(500);

        TableColumn<String, FileTransfer> colProgress = new TableColumn("Progress");
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));

        TableColumn<String, FileTransfer> colStatus = new TableColumn("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add column to TableView
        tblIncoming.getColumns().addAll(colFileName, colStatus, colProgress);
    }
    private void initOutgoingTable() {

        // Set table data
        filesOutgoing = FXCollections.observableArrayList();
        tblOutgoing.setItems(filesOutgoing);

        // Create columns
        TableColumn<String, FileTransfer> colFileName = new TableColumn("File Name");
        colFileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colFileName.setPrefWidth(250);

        TableColumn<String, FileTransfer> colProgress = new TableColumn("Progress");
        colProgress.setCellValueFactory(new PropertyValueFactory<>("progress"));

        TableColumn<String, FileTransfer> colStatus = new TableColumn("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add column to TableView
        tblOutgoing.getColumns().addAll(colFileName, colStatus, colProgress);
    }

    @FXML public void sendSingleFile() {
        File fileToSend = getLocalFile();
        if (fileToSend != null) {
            sendFile(getLocalFile());
        }
    }
    @FXML public void sendMultipleFiles() {
        List<File> filesToSend = getLocalFiles();
        if (filesToSend != null && filesToSend.size() > 0) {
            for (File file : filesToSend)
                sendFile(file);
        }
    }

    public File getLocalFile() {

        FileChooser mrChoosey = new FileChooser();
        return  mrChoosey.showOpenDialog(null);
    }
    public List<File> getLocalFiles() {
        FileChooser mrChoosey = new FileChooser();
        return  mrChoosey.showOpenMultipleDialog(null);
    }

    public void sendFile(File file) {

        if (server != null)
        {
            // Create File transfer for table
            FileTransfer newTransfer = new FileTransfer(file);
            newTransfer.status = "Completed";
            newTransfer.progress = 0.0;
            filesOutgoing.add(newTransfer);

            // Add to outgoing queue for processing
            server.outgoingFiles.add(file);

            // Log to user
            System.out.println("File " + file.getName() + " added to outgoing queue.");
        }
        else
        {
            System.out.println("Error: No server found!");
            JOptionPane.showMessageDialog(
                    null,
                    "No server found.",
                    "Server Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @FXML public void exitApplication() {
        System.exit(0);
    }

}
