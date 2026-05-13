package app_interface;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import javafx.embed.swing.SwingNode;
import javafx.scene.layout.StackPane;
import javax.swing.SwingUtilities;

public class InterfaceFX extends Application {
	public static void main(String[] args) {
        launch(args);
    }
	@Override
	public void start(Stage stage) throws Exception {
//		Parent root = FXMLLoader.load(getClass().getResource("InterfaceFX.fxml")); 
//        Scene scene = new Scene(root);
//         
//        stage.setScene(scene);
         
//        stage.show();
		// 1. Создаем SwingNode для встраивания Swing-контента
        final SwingNode swingNode = new SwingNode();

        // 2. Создаем контент JGraphX в потоке Swing (EDT)
        createSwingContent(swingNode);

        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);

        stage.setScene(new Scene(pane, 800, 600));
        stage.setTitle("JGraphX inside JavaFX");
        stage.show();
	}
	
	private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            // Создаем стандартный граф JGraphX
            mxGraph graph = new mxGraph();
            Object parent = graph.getDefaultParent();

            graph.getModel().beginUpdate();
            try {
                // Добавляем вершины и ребра
                Object v1 = graph.insertVertex(parent, null, "Привет", 20, 20, 80, 30);
                Object v2 = graph.insertVertex(parent, null, "Мир!", 240, 150, 80, 30);
                graph.insertEdge(parent, null, "Связь", v1, v2);
            } finally {
                graph.getModel().endUpdate();
            }

            // Оборачиваем в mxGraphComponent (это JComponent) и передаем в SwingNode
            mxGraphComponent graphComponent = new mxGraphComponent(graph);
            swingNode.setContent(graphComponent);
        });
    }
}
