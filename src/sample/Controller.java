package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;



public class Controller extends View{

    @FXML private TextField tf1;

    private Model model = new Model();

    @FXML public void onActionBtnOk() { this.ok();}

    @FXML public void onActionBtnClear() { this.clear();}


    private void clear(){
        tf1.clear();
        displayLabel("");
    }

    private void ok(){

        String expression = tf1.getText();

        displayLabel(model.calculate(expression));


    }



}
