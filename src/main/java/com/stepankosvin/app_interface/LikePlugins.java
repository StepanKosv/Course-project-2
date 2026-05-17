package com.stepankosvin.app_interface;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.stepankosvin.list_view_and_filters_plugin.FilterSettingsController;
import com.stepankosvin.plugin_fasade.IPlugin;
import com.stepankosvin.plugin_fasade.IPluginFasade;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LikePlugins {
	public static List<IPlugin> plugins = List.of(new FilterPlugin(),new GraphViewPlugin());
	
	public static class FilterPlugin implements IPlugin{
		
		@Override
		public void consume(IPluginFasade fasade) {
			fasade.addGraphAction("new filter", (holder,a,s)->{
				try {
					// Логика открытия списка вершин
					// 1. Создаем объект загрузчика
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stepankosvin/list_view_and_filters_plugin/FilterSettings.fxml"));

					// 2. Загружаем иерархию компонентов
					Parent root = loader.load();

					// 3. Получаем ссылку на контроллер
					FilterSettingsController controller = loader.getController();
					var future = CompletableFuture.runAsync(() -> {
						controller.setHolder(holder);
						controller.refresh();
					});

					// действия с интерфейсом
					javafx.application.Platform.runLater(() -> {
						
					});

					Scene scene = new Scene(root, 400, 400);
					// 4. Создаем новое окно (Stage)
					Stage stage = new Stage();
					stage.setScene(scene);
					stage.show();
				} catch (IOException err) {
					// TODO Auto-generated catch block
					err.printStackTrace();
				}
			});
		}

		@Override
		public String name() {
			return "graph filter";
		}
	}
	public static class GraphViewPlugin implements IPlugin{
		@Override
		public void consume(IPluginFasade fasade) {
			fasade.addGraphAction("new graph view", (holder,a,s)->{
				try {
					// Логика открытия списка вершин
					// 1. Создаем объект загрузчика
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stepankosvin/app_interface/GraphView.fxml"));

					// 2. Загружаем иерархию компонентов
					Parent root = loader.load();

					// 3. Получаем ссылку на контроллер
					GraphViewController controller = loader.getController();
					var future = CompletableFuture.runAsync(() -> {
						controller.setHolder(holder);
						controller.refresh();
					});

					// действия с интерфейсом
					javafx.application.Platform.runLater(() -> {
						
					});

					Scene scene = new Scene(root, 400, 400);
					// 4. Создаем новое окно (Stage)
					Stage stage = new Stage();
					stage.setScene(scene);
					stage.show();
				} catch (IOException err) {
					// TODO Auto-generated catch block
					err.printStackTrace();
				}
			});
		}

		@Override
		public String name() {
			return "graph view";
		}
	}
	
}
