package app_interface;

import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;


public class CollectSettingsController {

    @FXML private Label scopeNameLabel;
    @FXML private Spinner<Integer> pagesSpinner;
    @FXML private ListView<String> initialPagesList;
    @FXML private TextField pageInputField;
    @FXML private Button closeButton;
    @FXML private Button startButton;
    public ScopeHolder dataHolder;
    private CollectStatusController processStatus;

    @FXML
    public void initialize() {
        // Настройка спиннера для ввода количества страниц
        pagesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 1));
        pagesSpinner.valueProperty().addListener((obs, oldValue, newValue) -> handlePagesAmountChanged(newValue));

        // Отслеживание изменений в текстовом поле ввода страниц
        pageInputField.textProperty().addListener((obs, oldValue, newValue) -> handlePageInputChanged(newValue));
        
        // Добавление элемента в список по нажатию Enter в текстовом поле
        pageInputField.setOnAction(event -> {
            String text = pageInputField.getText().trim();
            if (!text.isEmpty()) {
                initialPagesList.getItems().add(text);
                pageInputField.clear();
            }
        });
     // СЛУШАТЕЛЬ УДАЛЕНИЯ: Удаление выбранного элемента по нажатию клавиши DELETE или BACKSPACE
        initialPagesList.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case KeyCode.DELETE:
                case KeyCode.BACK_SPACE:
                {
                    // Получаем индекс выбранного элемента
                    int selectedIndex = 
                    	initialPagesList.getSelectionModel().getSelectedIndex();
                    deleteItem(selectedIndex);
                    break;
                }
                default:
                    break;
            }
        });
    }
	private void deleteItem(int selectedIndex) {
		// Если элемент действительно выбран (индекс не равен -1)
		if (selectedIndex >= 0) {
		    // Удаляем элемент из модели данных списка
		    String removedItem = initialPagesList.getItems().remove(selectedIndex);
		    System.out.println("Удален элемент: " + removedItem);
		}
	}
 // Этот метод мы вызовем ВРУЧНУЮ для передачи данных
    public void initData(ScopeHolder holder, List<String> defaultPages) {
    	this.dataHolder = holder;
        // Устанавливаем текст в Label
        scopeNameLabel.setText(holder.getScopeName());
        
        // Заполняем список начальными страницами
        initialPagesList.getItems().addAll(defaultPages);
        
        System.out.println("Данные успешно переданы в контроллер!");
    }

    private void handlePagesAmountChanged(Integer newValue) {
        System.out.println("Количество страниц изменено на: " + newValue);
    }

    private void handlePageInputChanged(String newValue) {
        System.out.println("Текст ввода страницы изменен: " + newValue);
    }

    @FXML
    private void handleClose() {
        System.out.println("Нажата кнопка Close");
    }

    @FXML
    private void handleStart() {
    	System.out.println("CollectSettingsController.handleStart");
    	try {
    		if(processStatus==null&&initialPagesList.getItems().size()>0) {
    		
    			// 1. Создаем объект загрузчика
    			FXMLLoader loader = 
    					new FXMLLoader(
    							getClass().getResource("CollectStatus.fxml"));
            
    			// 2. Загружаем иерархию компонентов
    			Parent root = loader.load();
            
    			// 3. Получаем ссылку на контроллер
    			CollectStatusController controller = loader.getController();
    			controller.setParent(this);
    			this.processStatus = controller;
    			
    			 // 3. Создаем новое окно (Stage)
                Stage stage = new Stage();
                stage.setTitle("Дочернее окно");
                stage.setScene(new Scene(root));

                // 5. Показываем окно
                stage.show(); 
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}
