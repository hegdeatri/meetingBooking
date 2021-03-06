package Register;

import DBUtil.DBConnection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterController {
    @FXML
    private Label Heading;
    @FXML
    private Label errorLabel;
    @FXML
    private Label backgroundLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;

    //This will take the following as parameters to add their details into the database. The ID column will be automatically incremented
    public boolean registerLogic(String Username, String Firstname, String Lastname, String Password, String Email) throws SQLException {
        String sql = "INSERT INTO Users(Username, Firstname, Lastname, Password, Email, Account) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection con = DBConnection.getConnection();
            assert con != null;
            PreparedStatement statement = con.prepareStatement(sql);

            statement.setString(1, Username);
            statement.setString(2, Firstname);
            statement.setString(3, Lastname);
            statement.setString(4, Password);
            statement.setString(5, Email);
            statement.setString(6, "Customer");

            statement.execute();
            con.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Error!\n" + e);
            return false;
        }
    }

    //This will make sure that the required format is entered returning true/false. For this project only verification of email is required.
    public boolean checkFormat() {
        String regex = "^[\\\\w!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(emailField.getText());
        return matcher.matches();
    }

    //This will take you back to the login page.
    public void backToLogin() {
        try {
            //We need to get the old stage, so we can close it before we go back to the login page
            Stage old = (Stage) registerButton.getScene().getWindow();
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("/Login/LoginFXML.fxml").openStream());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Stylesheets/Login.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Register Page");
            stage.setResizable(false);
            old.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void registerUser() throws SQLException {
        try {

            if (usernameField.getText() != null
                    && firstNameField.getText() != null
                    && lastNameField.getText() != null
                    && emailField.getText() != null
                    && passwordField.getText() != null) {
                if (checkFormat()) {
                    if (registerLogic(usernameField.getText(), firstNameField.getText(), lastNameField.getText(), passwordField.getText(), emailField.getText())) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Account created!");

                        alert.showAndWait().ifPresent((btnType) -> {
                            if (btnType == ButtonType.OK) {
                                backToLogin();
                            }
                        });
                    }else{
                        errorLabel.setText("Account cannot be created with current details");
                    }

                } else {
                    errorLabel.setText("Invalid email address!");
                }
            }else{
                errorLabel.setText("Missing information!");

            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
        }
    }
}
