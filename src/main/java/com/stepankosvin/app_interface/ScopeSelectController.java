package com.stepankosvin.app_interface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.stepankosvin.db_integration.DBFasade;
import com.stepankosvin.list_view_and_filters_plugin.FilterSettingsController;
import com.stepankosvin.list_view_and_filters_plugin.VertexListController;
import com.stepankosvin.plugin_fasade.IPlugin;
import com.stepankosvin.plugin_fasade.IPluginFasade;
import com.stepankosvin.plugin_fasade.IPluginFasade.ButtonClick;
import com.stepankosvin.plugin_fasade.ScannerUtilite;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.ScopeInfo;

public class ScopeSelectController {

    @FXML private ComboBox<String> scopeNameComboBox;
    @FXML private Button loadButton;
    @FXML private Button loadOptionsButton;
    @FXML private Button loadPluginsButton;
    
    private List<IPlugin> plugins=new ArrayList<IPlugin>();
    
    private DBFasade fasade;
    

    // Внедрение контроллера вложенного виджета ScopeView
    @FXML private ScopeViewController scopeViewController;

    @FXML
    public void initialize() {
    }
    
    public void initData(DBFasade _f) {
    	fasade = _f;
    }
    
    @FXML
    void handleLoadPlugins(ActionEvent event) {
    	loadPlugins();
    	Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Плагины");
		alert.setHeaderText("загружены плагины");
		alert.setContentText(String.join(";\n",plugins.stream().map(p->p.name()).toList()));

		// Показываем окно и ждем, пока пользователь его закроет
		alert.showAndWait();
    }

	public void loadPlugins() {
		plugins.clear();
    	
    	String currentDir = Path.of("plugins").toString();
    	System.out.println("plugins dir : "+currentDir);
    	try {
			Files.createDirectory(Path.of("plugins"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	var classes = ScannerUtilite.scanFolder(currentDir, IPlugin.class);
    	var plugins_list = ScannerUtilite.getObjects(classes);
    	for(var p : plugins_list) {
    		if(p instanceof IPlugin ip) {
    			plugins.add(ip);
    		}else {
    			Alert alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Ошибка");
    			alert.setHeaderText("Произошла ошибка при выполнении handleLoadPlugins");
    			alert.setContentText("не плагин : "+p.toString());

    			// Показываем окно и ждем, пока пользователь его закроет
    			alert.showAndWait();
    		}
    	}
		plugins.addAll(LikePlugins.plugins);
		
	}

    @FXML
    void handleScopeSelectionChanged(ActionEvent event) {
        // Логика, срабатывающая сразу при выборе элемента в списке
    }
    
    @FXML
    void handleLoadOptions(ActionEvent event) {
    	assert(fasade!=null);
    	scopeNameComboBox.getItems().clear();
    	List<String> list=
    			fasade.transact(
    					em->{return em.createNamedQuery("ScopeInfo.findAll", ScopeInfo.class)
    				        .getResultList().stream().map(scope->scope.getScopeName())
    				        .collect(java.util.stream.Collectors.toList());}
    					);

    	scopeNameComboBox.getItems().addAll(list);
    }

    @FXML
    void handleLoadScope(ActionEvent event) {
        String selectedScope = scopeNameComboBox.getValue();
        if (selectedScope != null) {
        	ScopeHolder h= new ScopeHolder(selectedScope, -1L, fasade);
            scopeViewController.setHolder(h);
            for(var p:plugins) {
            	p.consume(scopeViewController);
            }
        }
    }
    
}
