package com.example.kryptozad2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainController {

    @FXML
    VBox mainBorder;

    @FXML
    AnchorPane mainBackground;

    @FXML
    TextField publicKeyP;

    @FXML
    TextField publicKeyG;

    @FXML
    TextField publicKeyH;

    @FXML
    TextField privateKey;

    @FXML
    Button generateKeysButton;

    @FXML
    TextArea textToEncrypt;

    @FXML
    TextArea textToDecrypt;

    @FXML
    TextArea textToDecrypt2;

    @FXML
    RadioButton radioFile;

    @FXML
    RadioButton radioWindow;

    @FXML
    Button encryptButton;

    @FXML
    Button decryptButton;

    @FXML
    Button saveButton;

    @FXML
    Button setFile;

    @FXML
    TextField filePath;

    @FXML
    Label infoLabel;



    byte[] content;

    MainController mainController;

    Stage stage = new Stage();

    File file;

    BigInteger p;
    BigInteger h;
    BigInteger g;
    BigInteger pk;


    public void showStage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 850, 600);
        mainController = fxmlLoader.getController();
        mainController.defaultSettings();
        stage.setTitle("ElGamal encryption program");
        stage.setScene(scene);
        stage.show();
    }

    public void defaultSettings() {
        ToggleGroup toggleGroup = new ToggleGroup();
        radioFile.setToggleGroup(toggleGroup);
        radioWindow.setToggleGroup(toggleGroup);
        radioFile.setSelected(true);
        radioFile.setOnAction(a -> radioButtonsBehaviour());
        radioWindow.setOnAction(a -> radioButtonsBehaviour());
        saveButton.setOnAction(a -> saveFile());
        saveButton.setVisible(false);
        encryptButton.setOnAction(a -> encryptMessage());
        decryptButton.setOnAction(a -> decryptMessage());
        setFile.setOnAction(a -> loadFile());
        generateKeysButton.setOnAction(a -> generateKeys());
        setDefaultBorders();

    }

    public void generateKeys() {
        ElGamal elGamal = new ElGamal();
        elGamal.generateKeys();
        publicKeyP.setText(HexFormat.of().formatHex( convertHexStringToBigInt(elGamal.getPrivateKey()).toByteArray()));

        publicKeyP.setText(elGamal.getpKey());
        publicKeyH.setText(elGamal.gethKey());
        publicKeyG.setText(elGamal.getgKey());
        privateKey.setText(elGamal.getPrivateKey());
    }

    public void setRedKeyBorders() {
        publicKeyG.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        publicKeyH.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        publicKeyP.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        privateKey.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
    }

    public boolean verifyKeys() {
        if(publicKeyP.getText() == "" || publicKeyH.getText() == "" || publicKeyG.getText() == "" || privateKey.getText() =="") {
            setRedKeyBorders();
            infoLabel.setText("Zadne pole nie moze byc puste");
            return false;
        }
        ElGamal elGamal = new ElGamal();
        elGamal.setP(convertHexStringToBigInt(publicKeyP.getText()));
        elGamal.setH(convertHexStringToBigInt(publicKeyH.getText()));
        elGamal.setPrivateKey(convertHexStringToBigInt(privateKey.getText()));
        elGamal.setG(convertHexStringToBigInt(publicKeyG.getText()));
        if(!elGamal.verifyKeys()){//Tu sie da weryfikacja w algorytmie
            setRedKeyBorders();
            infoLabel.setText("Niepoprawne klucze");
            return false;
        }
        return true;
    }
    public void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Proszę wybrać plik");
        File file2 = fileChooser.showOpenDialog(stage);
        if(file2!=null && file2.getPath()!=null) {
            infoLabel.setText("Plik wybrany pomyślnie");
            filePath.setText(file2.getPath());

        }
    }

    public void radioButtonsBehaviour() {
        if(radioFile.isSelected()) {
            textToDecrypt.setText("");
            textToEncrypt.setText("");
        } else if(radioWindow.isSelected()) {
            filePath.setText("");
        }
        infoLabel.setText("");
        saveButton.setVisible(false);
        setDefaultBorders();
    }


    public void encryptMessage() {
        setDefaultBorders();
        if(!verifyKeys()) {
            return;
        }
        if(radioWindow.isSelected()) {
            if(textToEncrypt.getText() == "") {
                textToEncrypt.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
                infoLabel.setText("Pole jest puste");
                return;
            }
            content = textToEncrypt.getText().getBytes(StandardCharsets.UTF_8);
        } else if (radioFile.isSelected()) {
            try {
                content = Files.readAllBytes(Paths.get(filePath.getText()));
            } catch (IOException e) {
                filePath.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
                infoLabel.setText("Błąd przy odczycie pliku");
                return;
            }
        }
        ElGamal elGamal = new ElGamal();
        elGamal.setP(new BigInteger(HexFormat.of().parseHex(publicKeyP.getText())));
        elGamal.setH(new BigInteger(HexFormat.of().parseHex(publicKeyH.getText())));
        elGamal.setG(new BigInteger(HexFormat.of().parseHex(publicKeyG.getText())));
        String[] parts = elGamal.encryptMessage(content);
        String partsCombined = parts[0] + "\n" + parts[1];
        if(radioWindow.isSelected()) {
            textToDecrypt.setText(partsCombined);
        } else if(radioFile.isSelected()) {
            content = partsCombined.getBytes(StandardCharsets.UTF_8);
            saveButton.setVisible(true);
            saveButton.setText("Zapisz zaszyfrowany");
        }

        infoLabel.setText("Zaszyfrowano pomyślnie");

    }

    public void decryptMessage() {
        setDefaultBorders();
        if(!verifyKeys()) {
            return;
        }

        if(radioWindow.isSelected()) {
            if(textToDecrypt.getText() == "") {
                textToDecrypt.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
                infoLabel.setText("Pole jest puste");
                return;
            }
            content = textToDecrypt.getText().getBytes();
        } else if (radioFile.isSelected()) {
            try {
                content = Files.readAllBytes(Paths.get(filePath.getText()));
            } catch (IOException e) {
                filePath.setBorder(new Border(new BorderStroke(Color.RED,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
                infoLabel.setText("Błąd przy odczycie pliku");
                return;
            }

        }
        ElGamal elGamal = new ElGamal();
        elGamal.setP(new BigInteger(HexFormat.of().parseHex(publicKeyP.getText())));
        elGamal.setPrivateKey(new BigInteger(HexFormat.of().parseHex(privateKey.getText())));
        if(radioWindow.isSelected()) {
            String[] parts = textToDecrypt.getText().split("\n");
            elGamal.setC1(new BigInteger(parts[0]));
            elGamal.setC2(new BigInteger(parts[1]));

            textToEncrypt.setText(new String(elGamal.decryptMessage()));
        } else if(radioFile.isSelected()){
            String[] parts = new String(content).split("\n");
            elGamal.setC1(new BigInteger(parts[0]));
            elGamal.setC2(new BigInteger(parts[1]));
            content = elGamal.decryptMessage();
            saveButton.setVisible(true);
            saveButton.setText("Zapisz odszyfrowany");
        }

        infoLabel.setText("Odszyfrowano pomyślnie");

    }

    public void setDefaultBorders() {
        mainBorder.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,null,null)));
        mainBackground.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,null,null)));
        publicKeyG.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        publicKeyH.setBorder(new Border(new BorderStroke(Color.GREEN,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        publicKeyP.setBorder(new Border(new BorderStroke(Color.GREEN,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        privateKey.setBorder(new Border(new BorderStroke(Color.GREEN,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        textToEncrypt.setBorder(new Border(new BorderStroke(Color.GREEN,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        textToDecrypt.setBorder(new Border(new BorderStroke(Color.GREEN,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
        filePath.setBorder(new Border(new BorderStroke(Color.GREEN,BorderStrokeStyle.SOLID,new CornerRadii(3),new BorderWidths(2))));
    }
    public void saveFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(pdfFilter);
        FileChooser.ExtensionFilter binFilter = new FileChooser.ExtensionFilter("Binary Files (*.bin)", "*.bin");
        fileChooser.getExtensionFilters().add(binFilter);
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(pngFilter);
        fileChooser.setTitle("Zapisz plik");
        file = fileChooser.showSaveDialog(stage);
        try(FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public BigInteger convertHexStringToBigInt(String text){
        return new BigInteger(text,16);
    }
}