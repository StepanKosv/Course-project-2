package app_interface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class CollectStatusController {

    @FXML private ListView<String> messagesList;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;
    @FXML private Button continueButton;
    @FXML private Button closeButton;
    private CollectSettingsController parent;

    @FXML
    public void initialize() {
        // Слушатель на случай изменения выделенного сообщения в списке (опционально)
        messagesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Выбрано сообщение: " + newVal);
        });
    }

    @FXML
    private void handleStop() {
        System.out.println("Нажата кнопка Stop");
    }

    @FXML
    private void handlePause() {
        System.out.println("Нажата кнопка Pause");
    }

    @FXML
    private void handleContinue() {
        System.out.println("Нажата кнопка Continue");
    }

    @FXML
    private void handleClose() {
        System.out.println("Нажата кнопка Close");
    }

	public void setParent(CollectSettingsController collectSettingsController) {
		parent=collectSettingsController;
	}
}
