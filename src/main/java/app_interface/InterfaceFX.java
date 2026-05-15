package app_interface;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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

import java.awt.Dimension;

import javax.swing.SwingUtilities;

public class InterfaceFX extends Application {
	public static void main(String[] args) {
        launch(args);
    }
	@Override
	public void start(Stage stage) throws Exception {
		
		// 1. Создаем объект загрузчика
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CollectSettings.fxml"));
        
        // 2. Загружаем иерархию компонентов
        Parent root = loader.load();
        
        // 3. Получаем ссылку на контроллер
        CollectSettingsController controller = loader.getController();
        javafx.application.Platform.runLater(()->{
        	
        });
        
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("JUNG в JavaFX");
        stage.setScene(scene);
        stage.show();
	}
	
	private void testGraphView(Stage stage) throws Exception {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("reddit-collector");
		ScopeHolder holder = new ScopeHolder("test display scope", -1L, emf);
		var db=holder.dbFasade;
		holder.getGraph().addVertex(1l);
		holder.getGraph().addVertex(2l);
		holder.getGraph().addVertex(3l);
		model.RelPK e12=new model.RelPK();
		e12.setLeftNodeFk(1l);
		e12.setRightNodeFk(2l);
		e12.setRelId(-1l);
		holder.getGraph().addEdge(e12, 1l, 2l);
		int n=15;
		for(int i=0; i<n; i++) {
			holder.getGraph().addVertex((long)i);
		}
		for(int i=0; i<n; i++) {
			for(int j=i; j<n; j++) {
				model.RelPK e=new model.RelPK();
				e.setLeftNodeFk((long)i);
				e.setRightNodeFk((long)j);
				e.setRelId((long)i+j);
				holder.getGraph().addEdge(e,(long)i, (long)j);
			}
		}
//		Long node=db.transact(em->
//		{return db.createOrFindNode(em, "1", "asdf", holder.getScopeName());});
//		holder.newNode(node, new model.Node());
		
		
		// 1. Создаем объект загрузчика
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GraphView.fxml"));
        
        // 2. Загружаем иерархию компонентов
        Parent root = loader.load();
        
        // 3. Получаем ссылку на контроллер
        GraphViewController controller = loader.getController();
        javafx.application.Platform.runLater(()->{
        	controller.setHolder(holder);
        	controller.refresh();
        });
        
        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("JUNG в JavaFX");
        stage.setScene(scene);
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
