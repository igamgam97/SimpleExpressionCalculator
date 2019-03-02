package sample;

/*
 * это представление: то, что выводится на форму
 * */

import javafx.fxml.FXML;
import javafx.scene.control.Label;

class View {

    // поле вывода
    @FXML
    private Label LabelResult;

    // выводит текст в LabelResult
    void displayLabel(String s) {
        LabelResult.setText(s);
    }
}
