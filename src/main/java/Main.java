import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application
{
    private TextArea txtArea;
    private static final String titleTxt = "Confluence updater";
    private File inputFile;
    private List<String> permissions;


    public void log(String text)
    {
        txtArea.setText(txtArea.getText() + text + "\n---------------------------------------------------\n");
    }


    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle(titleTxt);

        // Window label
        Label label = new Label("Confluence updater");
        label.setTextFill(Color.DARKBLUE);
        label.setFont(Font.font("Calibri", FontWeight.BOLD, 36));
        HBox labelHb = new HBox();
        labelHb.setAlignment(Pos.CENTER);
        labelHb.getChildren().add(label);

        Label label1 = new Label("URL: ");
        Label label2 = new Label("Username: ");
        Label label3 = new Label("Password: ");
        TextField urlText = new TextField();
        urlText.setMinWidth(500);
        urlText.setText("http://yandex.ru/confluence/rpc/json-rpc/confluenceservice-v2/addPermissionsToSpace");
        TextField usernameText = new TextField();
        TextField passwordText = new TextField();

        GridPane grid = new GridPane();
        //        grid.setMinWidth(400);
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(label1, 1, 1);
        grid.add(urlText, 2, 1);

        grid.add(label2, 1, 2);
        grid.add(usernameText, 2, 2);
        grid.add(label3, 1, 3);
        grid.add(passwordText, 2, 3);

        // Text area in a scrollpane and label
        Label txtAreaLabel = new Label("Log:");
        txtAreaLabel.setFont(Font.font("Calibri", FontWeight.NORMAL, 20));
        txtArea = new TextArea();
        log("Application started");
        //        txtArea.setWrapText(true);
        //        txtArea.setDisable(true);

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(txtArea);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        scroll.setPrefHeight(300);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        VBox txtAreaVbox = new VBox(5);
        txtAreaVbox.setPadding(new Insets(5, 5, 5, 5));
        txtAreaVbox.getChildren().addAll(txtAreaLabel, scroll);

        // Button
        Button readFileButton = new Button("Read permissions from file");
        readFileButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load input file");
            inputFile = fileChooser.showOpenDialog(primaryStage);
            if (inputFile != null)
            {
                Predicate<String> blank = String::isEmpty;
                permissions = readFile(inputFile);
                permissions = permissions.stream().filter(blank.negate())
                    .collect(Collectors.toList());
                //                txtArea.setText(txtArea.getText() + String.join("\n", permissions));
                log("Read " + permissions.size() + " permissions");
            }

        });

        // Button
        Button authBtn = new Button("Authenticate");
        authBtn.setOnAction(event -> {
            String url = urlText.getText();
            String username = usernameText.getText();
            String password = usernameText.getText();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            if (url.isEmpty())
            {
                alert.setContentText("Url is empty or incorrect");
                alert.showAndWait();
                return;

            }
            else if (username.isEmpty())
            {
                alert.setContentText("Username is empty or incorrect");
                alert.showAndWait();
                return;
            }
            else if (password.isEmpty())
            {
                alert.setContentText("Password is empty or incorrect");
                alert.showAndWait();
                return;
            }
            else if (permissions == null)
            {
                alert.setContentText("Not loaded permissions");
                alert.showAndWait();
                return;
            }

            String result = RequestSender.addPermissions(url, username, password, permissions);
            log(result);


        });

        HBox buttonHb1 = new HBox(10);
        buttonHb1.setAlignment(Pos.BASELINE_LEFT);
        buttonHb1.getChildren().

            addAll(readFileButton, authBtn);

        // Vbox
        VBox vbox = new VBox(30);
        vbox.setPadding(new
            Insets(25, 25, 25, 25));
        vbox.getChildren().
            addAll(labelHb, grid, buttonHb1, txtAreaVbox);

        // Scene
        Scene scene = new Scene(vbox, 1000, 600); // w x h
        primaryStage.setScene(scene);

        //        savedStage = primaryStage;

        primaryStage.show();
    }


    public static void main(String[] args)
    {
        launch(args);
    }


    public class Params
    {
        String url;
        String username;
        String password;


        public Params(String url, String username, String password)
        {
            this.url = url;
            this.username = username;
            this.password = password;
        }


        @Override
        public String toString()
        {
            return "Params{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
        }


    }


    private Optional<Params> showDialog()
    {
        Dialog<Params> dialog = new Dialog<>();
        dialog.setTitle("Enter parameters for connection");
        dialog.setHeaderText("Enter parameters for connection to jira \n" +
            "press Okay (or click title bar 'X' for cancel).");
        dialog.setResizable(true);

        Label label1 = new Label("URL: ");
        Label label2 = new Label("Username: ");
        Label label3 = new Label("Password: ");
        TextField text1 = new TextField();
        TextField text2 = new TextField();
        TextField text3 = new TextField();

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);

        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        grid.add(label3, 1, 3);
        grid.add(text3, 2, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        dialog.setResultConverter(b -> {

            if (b == buttonTypeOk)
            {

                return new Params(text1.getText(), text2.getText(), text3.getText());
            }

            return null;
        });

        return dialog.showAndWait();
    }


    private List<String> readFile(File file)
    {
        List<String> stringList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line = br.readLine();
            while (line != null)
            {
                stringList.add(line);
                line = br.readLine();
            }
            //            actionStatus.setText("File " +
            //                inputFile.toString() + " successfully read.\nCurrent output file:  " + outputFile.getAbsolutePath());

        }
        catch (IOException e)
        {
            //            actionStatus.setText("An ERROR occurred while opening input file!" +
            //                inputFile.toString() + "\nCurrent output file:  " + outputFile.getAbsolutePath());
        }
        return stringList;
    }
}
