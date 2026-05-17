package com.stepankosvin.list_view_and_filters_plugin;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.stepankosvin.list_view_and_filters_plugin.FilterSettingsController.NodeAdapter;
import com.stepankosvin.plugin_fasade.IDataUser;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.RelPK;

public class VertexListController implements IDataUser<Long, RelPK>{

    @FXML private Button refreshButton;
    @FXML private TableView<NodeAdapter> vertexTableView; // Замените Object на ваш класс модели вершины
    @FXML private TableColumn<NodeAdapter, Number> idColumn;
    @FXML private TableColumn<NodeAdapter, String> labelColumn;
    @FXML private TableColumn<NodeAdapter, Number> closenessCentralityColumn;
    @FXML private TableColumn<NodeAdapter, Number> betweennessCentralityColumn;
    @FXML private TableColumn<NodeAdapter, Number> degreeColumn;
	private FilterSettingsController parent;
    

    @FXML
    void handleRefresh(ActionEvent event) {
        // Логика обновления таблицы вершин
    	var future = CompletableFuture.runAsync(
    			()->refresh()
    			);
    }

    @FXML
    public void initialize() {
        // Настройка фабрик ячеек (CellValueFactory) для колонок таблицы
    	idColumn.setCellValueFactory(node->new SimpleLongProperty(node.getValue().getData().getId()));
    	labelColumn.setCellValueFactory(node->new SimpleStringProperty(
    			node.getValue().getData().getDisplayText()));
    	closenessCentralityColumn.setCellValueFactory(node->new SimpleDoubleProperty(
    			node.getValue().getClosenessCentrality()
    			));
    	betweennessCentralityColumn.setCellValueFactory(node->new SimpleDoubleProperty(
    			node.getValue().getBetweennessCentrality()
    			));
    	degreeColumn.setCellValueFactory(node->new SimpleIntegerProperty(
    			node.getValue().getDegreeScorer()
    			));
    }
    
    public void setParent(FilterSettingsController p) {
    	parent=p;
    	parent.addListener(this);
    	refresh();
    }

	@Override
	public void update(List<RelPK> edgeKeyList, List<Long> nodeKeyList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		System.out.println("VertexListController.refresh");
		vertexTableView.getItems().clear();
		if(parent!=null) {
			if(parent.getGraph()!=null) {
				System.out.println("done");
				for(var node:parent.getGraph().getVertices()) {
					vertexTableView.getItems().add(
							parent.new NodeAdapter(parent.getHolder().getNode(node))
							);
				}
			}
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
