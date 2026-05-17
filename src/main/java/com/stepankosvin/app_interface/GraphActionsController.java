package com.stepankosvin.app_interface;

import java.util.LinkedHashMap;
import java.util.Map;

import com.stepankosvin.plugin_fasade.IGraphHolder;
import com.stepankosvin.plugin_fasade.IPluginFasade;
import com.stepankosvin.plugin_fasade.IPluginFasade.ButtonClick;
import com.stepankosvin.plugin_fasade.IPluginFasade.GraphActionButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import model.Node;
import model.Rel;
import model.RelPK;

public class GraphActionsController {
	@FXML private TitledPane actionsPane;
    @FXML private TextField searchField;
    @FXML private VBox buttonsContainer;
    @FXML private Button refreshButton;
    // Хранилище оригинальных кнопок для возможности их фильтрации
    private final Map<String, Button> actionsMap = new LinkedHashMap<>();
    
    private IPluginFasade fasade;
    private IGraphHolder<Long,RelPK,Node,Rel> holder;
    
    @FXML
	public void initialize() {
		// Инициализация логики при загрузке FXML
		// Слушатель изменений в текстовом поле для мгновенного поиска
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterButtons(newValue);
        });
	}
    public void setData(IPluginFasade f, IGraphHolder<Long,RelPK,Node,Rel> h) {
    	fasade=f;
    	holder=h;
    	refresh();
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
    public void addAction(String name, GraphActionButton action) {
//		if (actionsMap.containsKey(name)) {
//            return; // Или обновите существующую, если это необходимо
//        }

        Button newButton = new Button(name);
        newButton.setMaxWidth(Double.MAX_VALUE); // Кнопка занимает всю ширину
        
        // Привязка логики клика
        newButton.setOnAction(e -> {
            action.consume(holder,e, actionsPane.getScene());
        });

        actionsMap.put(name, newButton);
        
        // Отображаем кнопку, если она проходит текущий фильтр поиска
        if (name.toLowerCase().contains(searchField.getText().toLowerCase())) {
            buttonsContainer.getChildren().add(newButton);
        }
	}
    @FXML
	void handleRefresh(ActionEvent event) {
		refresh();
	}
	public void refresh() {
		fasade.getGraphActions().forEach(this::addAction);
	}
}
