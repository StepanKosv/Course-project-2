package com.stepankosvin.list_view_and_filters_plugin;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.stepankosvin.list_view_and_filters_plugin.FilterSettingsController.EdgeAdapter;
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

public class EdgeListController  implements IDataUser<Long, RelPK>{

    @FXML private Button refreshButton;
    @FXML private TableView<EdgeAdapter> edgeTableView; 
    
    private FilterSettingsController parent;
    
    @FXML private TableColumn<EdgeAdapter, Number> idColumn;
    @FXML private TableColumn<EdgeAdapter, String> labelColumn;
    @FXML private TableColumn<EdgeAdapter, Number> betweennessCentralityColumn;
    @FXML private TableColumn<EdgeAdapter, Number> idColumnL;
    @FXML private TableColumn<EdgeAdapter, String> labelColumnL;
    @FXML private TableColumn<EdgeAdapter, Number> closenessCentralityColumnL;
    @FXML private TableColumn<EdgeAdapter, Number> betweennessCentralityColumnL;
    @FXML private TableColumn<EdgeAdapter, Number> degreeColumnL;
    @FXML private TableColumn<EdgeAdapter, Number> idColumnR;
    @FXML private TableColumn<EdgeAdapter, String> labelColumnR;
    @FXML private TableColumn<EdgeAdapter, Number> closenessCentralityColumnR;
    @FXML private TableColumn<EdgeAdapter, Number> betweennessCentralityColumnR;
    @FXML private TableColumn<EdgeAdapter, Number> degreeColumnR;
    
    public void setParent(FilterSettingsController p) {
    	parent=p;
    	parent.addListener(this);
    	refresh();
    }

    @FXML
    void handleRefresh(ActionEvent event) {
        // Логика обновления таблицы ребер
    	var future = CompletableFuture.runAsync(
    			()->refresh()
    			);
    }

    @FXML
    public void initialize() {
        // Настройка фабрик ячеек (CellValueFactory) для колонок таблицы
    	idColumn.setCellValueFactory(egde->new SimpleLongProperty(
    			egde.getValue().getData().getId().getRelId()));
    	labelColumn.setCellValueFactory(egde->new SimpleStringProperty(
    			egde.getValue().getData().getDisplayText()));
    	betweennessCentralityColumn.setCellValueFactory(egde->new SimpleDoubleProperty(
    			egde.getValue().getBetweennessCentrality()
    			));
    	idColumnL.setCellValueFactory(egde->new SimpleLongProperty(
    			egde.getValue().left().getData().getId()));
    	labelColumnL.setCellValueFactory(egde->new SimpleStringProperty(
    			egde.getValue().left().getData().getDisplayText()));
    	closenessCentralityColumnL.setCellValueFactory(egde->new SimpleDoubleProperty(
    			egde.getValue().left().getClosenessCentrality()
    			));
    	betweennessCentralityColumnL.setCellValueFactory(egde->new SimpleDoubleProperty(
    			egde.getValue().left().getBetweennessCentrality()
    			));
    	degreeColumnL.setCellValueFactory(egde->new SimpleIntegerProperty(
    			egde.getValue().left().getDegreeScorer()
    			));
    	idColumnR.setCellValueFactory(egde->new SimpleLongProperty(
    			egde.getValue().right().getData().getId()));
    	labelColumnR.setCellValueFactory(egde->new SimpleStringProperty(
    			egde.getValue().right().getData().getDisplayText()));
    	closenessCentralityColumnR.setCellValueFactory(egde->new SimpleDoubleProperty(
    			egde.getValue().right().getClosenessCentrality()
    			));
    	betweennessCentralityColumnR.setCellValueFactory(egde->new SimpleDoubleProperty(
    			egde.getValue().right().getBetweennessCentrality()
    			));
    	degreeColumnR.setCellValueFactory(egde->new SimpleIntegerProperty(
    			egde.getValue().right().getDegreeScorer()
    			));
    }

	@Override
	public void update(List<RelPK> edgeKeyList, List<Long> nodeKeyList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh() {
		System.out.println("EdgeListController.refresh");
		edgeTableView.getItems().clear();
		if(parent!=null) {
			if(parent.getGraph()!=null) {
				System.out.println("done");
				for(var egde:parent.getGraph().getEdges()) {
					edgeTableView.getItems().add(
							parent.new EdgeAdapter(parent.getHolder().getEdge(egde))
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
