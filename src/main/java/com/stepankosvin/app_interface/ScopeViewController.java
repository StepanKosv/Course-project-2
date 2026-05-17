package com.stepankosvin.app_interface;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stepankosvin.db_integration.DBFasade;
import com.stepankosvin.plugin_fasade.IPluginFasade;
import com.stepankosvin.plugin_fasade.IScopeHolder;
import com.stepankosvin.reddit_collect_pascage.ICollectResult;
import com.stepankosvin.reddit_collect_pascage.ICollectState;
import com.stepankosvin.reddit_collect_pascage.ICollector;

import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ScopeViewController implements IPluginFasade{
	@FXML private TitledPane actionsPane;
    @FXML private TextField searchField;
    @FXML private VBox buttonsContainer;
    // Хранилище оригинальных кнопок для возможности их фильтрации
    private final Map<String, Button> actionsMap = new LinkedHashMap<>();

	@FXML
	private Label currentScopeNameLabel;
	@FXML
	private Button refreshButton;
	@FXML
	private Button reloadFromDbButton;
	@FXML
	private Button newGraphViewButton;
	@FXML
	private Button newVertexListButton;
	@FXML
	private Button newRelationsListButton;
	@FXML
	private Button newFilterViewButton;
	
	private IScopeHolder holder;
	
	private Map<String,GraphActionButton> graphActions=new HashMap<>();

	// Вложенные контроллеры (JavaFX внедряет их автоматически по ID + суффикс
	// Controller)
	@FXML
	private GraphViewController graphViewController;
	@FXML
	private GraphActionsController graphActionsPanelController;
	// @FXML private Object vertexListController;
	// @FXML private Object relationsListController;

	@FXML
	public void initialize() {
		// Инициализация логики при загрузке FXML
		// Слушатель изменений в текстовом поле для мгновенного поиска
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterButtons(newValue);
        });
	}
	
	private void changeOrSetDate(IScopeHolder h) {
		closeHolder();
		setData(h);
		refresh();
	}
	
	public void closeHolder() {
		if(holder!=null) this.holder.close();
	}

	private void setData(IScopeHolder h) {
		holder=h;
		javafx.application.Platform.runLater(() -> {
			currentScopeNameLabel.setText("Current Scope: " + h.getScopeName());
		});
		javafx.application.Platform.runLater(() -> {
			graphViewController.setHolder(holder);
			this.refresh();
		});
		holder.reloadFromDB();
		this.refresh();
	}

	@FXML
	void handleRefresh(ActionEvent event) {
		refresh();
	}
	void refresh() {
		if (holder != null)
			holder.refresh();
		this.graphActionsPanelController.setData(this, holder);
	}

	@FXML
	void handleReloadFromDb(ActionEvent event) {
		if (holder != null) {
			holder.reloadFromDB();
			holder.refresh();
		}
	}

	@FXML
	void handleNewGraphView(ActionEvent event) {
		// Действие для создания нового окна/вкладки графа
	}

	@FXML
	void handleNewVertexList(ActionEvent event) {
		// Действие для создания нового списка вершин
	}

	@FXML
	void handleNewRelationsList(ActionEvent event) {
		// Действие для создания нового списка связей
	}

	@FXML
	void handleNewFilterView(ActionEvent event) {
		// Действие для открытия панели фильтров
	}
	@FXML
	void handleCollectButton(ActionEvent event) {
		if(holder!=null) {
			try {
				// 1. Создаем объект загрузчика
				FXMLLoader loader = new FXMLLoader(getClass().getResource("CollectSettings.fxml"));

				// 2. Загружаем иерархию компонентов
				Parent root = loader.load();

				// 3. Получаем ссылку на контроллер
				CollectSettingsController controller = loader.getController();
				
				// зачем-то дефолтный элемент
				controller.initData(holder,
						List.of("https://www.reddit.com/r/mathmemes/comments/1sgzvxx/the_1phone/.json"));
				
				javafx.application.Platform.runLater(() -> {
					
				});

				Scene scene = new Scene(root, 400, 400);

				// show
				Stage stage = new Stage();
				stage.setTitle("Collect in " + holder.getScopeName());
				stage.setScene(scene);
				stage.show();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setHolder(IScopeHolder h) {
		changeOrSetDate(h);
	}

	@Override
	public IScopeHolder getScopeHolder() {
		return holder;
	}

	@Override
	public void addAction(String name, ButtonClick action) {
//		if (actionsMap.containsKey(name)) {
//            return; // Или обновите существующую, если это необходимо
//        }

        Button newButton = new Button(name);
        newButton.setMaxWidth(Double.MAX_VALUE); // Кнопка занимает всю ширину
        
        // Привязка логики клика
        newButton.setOnAction(e -> {
            action.consume(e, actionsPane.getScene());
        });

        actionsMap.put(name, newButton);
        
        // Отображаем кнопку, если она проходит текущий фильтр поиска
        if (name.toLowerCase().contains(searchField.getText().toLowerCase())) {
            buttonsContainer.getChildren().add(newButton);
        }
	}

	@Override
	public void removeAction(String name) {
		Button removedButton = actionsMap.remove(name);
        if (removedButton != null) {
            buttonsContainer.getChildren().remove(removedButton);
        }
	}

	@Override
	public DBFasade getDBFasade() {
		// TODO Auto-generated method stub
		return holder.getDbFasade();
	}
	/**
	 * Обязательно в потоке javafx.application.Platform.runLater
	 */
	@Override
	public Parent createCollectProcess(
			ICollector<? extends ICollectState, ? extends ICollectResult> collector) {
		try {
			// 1. Создаем объект загрузчика
			FXMLLoader loader = new FXMLLoader(getClass().getResource("CollectStatus.fxml"));

			// 2. Загружаем иерархию компонентов
			Parent root = loader.load();

			// 3. Получаем ссылку на контроллер
			CollectStatusController controller = loader.getController();
			controller.setCollectorAndInit(collector);

			// 3. Создаем новое окно (Stage)
			Stage stage = new Stage();
			stage.setScene(new Scene(root));

			// 5. Показываем окно
			stage.show();
			return root;
		}catch(Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Ошибка");
			alert.setHeaderText("Произошла ошибка при выполнении createCollectProcess");
			alert.setContentText(e.getMessage());

			// Показываем окно и ждем, пока пользователь его закроет
			alert.showAndWait();

			e.printStackTrace();
			return null;
		}
	}
	private void filterButtons(String query) {
        buttonsContainer.getChildren().clear();
        
        if (query == null || query.isEmpty()) {
            buttonsContainer.getChildren().addAll(actionsMap.values());
            return;
        }

        String lowerCaseQuery = query.toLowerCase();
        actionsMap.forEach((name, button) -> {
            if (name.toLowerCase().contains(lowerCaseQuery)) {
                buttonsContainer.getChildren().add(button);
            }
        });
    }

	@Override
	public void addGraphAction(String name, GraphActionButton action) {
		this.graphActions.put(name, action);
	}

	@Override
	public void removeGraphAction(String name) {
		this.graphActions.remove(name);
	}

	@Override
	public Map<String,GraphActionButton> getGraphActions() {
		return this.graphActions;
	}
}
