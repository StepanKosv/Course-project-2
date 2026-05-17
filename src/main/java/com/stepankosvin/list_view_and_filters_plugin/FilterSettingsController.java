package com.stepankosvin.list_view_and_filters_plugin;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.stepankosvin.app_interface.GraphViewController;
import com.stepankosvin.app_interface.ScopeSelectController;
import com.stepankosvin.db_integration.DBFasade;
import com.stepankosvin.plugin_fasade.IDataUser;
import com.stepankosvin.plugin_fasade.IGraphHolder;
import com.stepankosvin.plugin_fasade.IScopeHolder;

import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.scoring.ClosenessCentrality;
import edu.uci.ics.jung.algorithms.scoring.DegreeScorer;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.event.GraphEvent.Edge;
import edu.uci.ics.jung.graph.util.Graphs;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Node;
import model.Rel;
import model.RelPK;

public class FilterSettingsController implements IGraphHolder<Long,RelPK,Node,Rel>{
	//TODO пока не буду делать логику опциональности.
    @FXML private Button refreshButton;
    @FXML private CheckBox degreeCentralityCheckBox;
    @FXML private CheckBox betweennessCentralityCheckBox;
    @FXML private CheckBox closenessCentralityCheckBox;
    @FXML private TextField minBetw;
    @FXML private TextField minClos;
    @FXML private TextField minDeg;
    @FXML private TextField minBetwL;
    @FXML private TextField minClosL;
    @FXML private TextField minDegL;
    @FXML private TextField minBetwR;
    @FXML private TextField minClosR;
    @FXML private TextField minDegR;
    @FXML private TextField minBetwE;
    
    
    @FXML private Button openVertexListButton;
    @FXML private Button openEdgeListButton;
    @FXML private Button openGraphViewButton;
    
    private IGraphHolder<Long,RelPK,Node,Rel> holder;
    private Graph<Long,RelPK> no_multiegde_graph;
    private BetweennessCentrality<Long, RelPK> betweenness;
    private ClosenessCentrality<Long, RelPK> closeness;
    private DegreeScorer<Long> degreeScorer;
    private DijkstraShortestPath<Long, RelPK> dijkstra;
    private Set<IDataUser<Long, RelPK>> listeners=new HashSet<>();
    private Graph<Long,RelPK> filteredGraph;
    

    @FXML
    void handleRefresh(ActionEvent event) {
        // Логика обновления данных
    	var future = CompletableFuture.runAsync(
    			()->refresh()
    			);
    	
    }

    @FXML
    void handleOpenVertexList(ActionEvent event) {

		try {
			// Логика открытия списка вершин
			// 1. Создаем объект загрузчика
			FXMLLoader loader = new FXMLLoader(getClass().getResource("VertexList.fxml"));

			// 2. Загружаем иерархию компонентов
			Parent root = loader.load();

			// 3. Получаем ссылку на контроллер
			VertexListController controller = loader.getController();
			controller.setParent(this);

			// действия с интерфейсом
			javafx.application.Platform.runLater(() -> {
				
			});

			Scene scene = new Scene(root, 400, 400);
			// 4. Создаем новое окно (Stage)
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void handleOpenEdgeList(ActionEvent event) {
    	try {
			// Логика открытия списка реберы
			// 1. Создаем объект загрузчика
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EdgeList.fxml"));

			// 2. Загружаем иерархию компонентов
			Parent root = loader.load();

			// 3. Получаем ссылку на контроллер
			EdgeListController controller = loader.getController();
			controller.setParent(this);

			// действия с интерфейсом
			javafx.application.Platform.runLater(() -> {
				
			});

			Scene scene = new Scene(root, 400, 400);
			// 4. Создаем новое окно (Stage)
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void handleOpenGraphView(ActionEvent event) {
    	try {
			// Логика открытия списка реберы
			// 1. Создаем объект загрузчика
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/stepankosvin/app_interface/GraphView.fxml"));

			// 2. Загружаем иерархию компонентов
			Parent root = loader.load();

			// 3. Получаем ссылку на контроллер
			GraphViewController controller = loader.getController();
			controller.setHolder(this);
			controller.refresh();

			// действия с интерфейсом
			javafx.application.Platform.runLater(() -> {
			});

			Scene scene = new Scene(root, 400, 400);
			// 4. Создаем новое окно (Stage)
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    public void initialize() {
        // Инициализация при загрузке FXML
    }
    
//    public void setSourceGraph(Graph<Long, RelPK> g) {
//    	source_multigraph=g;
//    	refresh();
//    }
    //public Graph<Long, RelPK> getGraph(){return source_multigraph;}
    public Graph<Long, RelPK> getGraph(){
		return this.filteredGraph;
    }
    
    private boolean more(Double val, String check) {
		try{
			return Double.parseDouble(check)<=val;
		}catch(Exception e) {
			return true;
		}
	}
    private boolean more(Double val, TextField check) {
		return more(val,check.getText());
	}
    
    
    // Вложенные классы по ТЗ
    public class NodeAdapter {
        // Поля и методы для адаптера вершины
    	Node node;
    	public NodeAdapter(Node n) {
    		node=n;
    	}
    	public Node getData() {return node;};
    	public Double getBetweennessCentrality() {
    		return betweenness.getVertexScore(node.getId());
    	}
    	public Double getClosenessCentrality() {
    		return closeness.getVertexScore(node.getId());
    	}
    	public Integer getDegreeScorer() {
    		return degreeScorer.getVertexScore(node.getId());
    	}
    	public boolean check(TextField betw, TextField clos, TextField deg) {
    		return more(getBetweennessCentrality(),betw)
    				&&more(getClosenessCentrality(),clos)
    				&&more(getDegreeScorer().doubleValue(),deg);
    	}
    	public boolean check() {
    		return more(getBetweennessCentrality(),minBetw.getText())
    				&&more(getClosenessCentrality(),minClos.getText())
    				&&more(getDegreeScorer().doubleValue(),minDeg.getText());
    	}
    }

    public class EdgeAdapter {
        // Поля и методы для адаптера ребра
    	Rel egde;
    	public EdgeAdapter(Rel rel) {
    		egde=rel;
    	}
    	public Rel getData() {return egde;}
    	public Double getBetweennessCentrality() {
    		return betweenness.getEdgeScore(egde.getId());
    	}
    	public NodeAdapter left() {
    		return new NodeAdapter(holder.getNode(egde.getId().getLeftNodeFk()));}
    	public NodeAdapter right() {
    		return new NodeAdapter(holder.getNode(egde.getId().getRightNodeFk()));}
    	public boolean check() {
    		return more(getBetweennessCentrality(),minBetwE)
    				&&left().check(minBetwL,minClosL,minDegL)
    				&&right().check(minBetwR,minClosR,minDegR);
    	}
    }


	@Override
	public void refresh() {
		if(holder.getGraph()!=null) {
			no_multiegde_graph = Graphs.synchronizedGraph(new SparseGraph<>());
			dijkstra = new DijkstraShortestPath<>(holder.getGraph());
			betweenness = new BetweennessCentrality<Long, RelPK>(holder.getGraph());
			closeness = new ClosenessCentrality<>(holder.getGraph(),dijkstra);
			degreeScorer = new DegreeScorer<>(holder.getGraph());
			
			filteredGraph=Graphs.synchronizedGraph(new SparseMultigraph<>());
			for(var node:holder.getGraph().getVertices()) {
				if((new NodeAdapter(holder.getNode(node)).check())) {
					filteredGraph.addVertex(node);
				}
			}
			for(var egde:holder.getGraph().getEdges()) {
				if(filteredGraph.containsVertex(egde.getLeftNodeFk())&&
						filteredGraph.containsVertex(egde.getRightNodeFk())&&
						(new EdgeAdapter(holder.getEdge(egde))).check()) {
					filteredGraph.addEdge(egde, egde.getLeftNodeFk(), egde.getRightNodeFk());
				}
			}
		}
		IGraphHolder.super.refresh();
	}

	public IGraphHolder<Long,RelPK,Node,Rel> getHolder() {
		return holder;
	}

	public void setHolder(IGraphHolder<Long, RelPK, Node, Rel> holder2) {
		this.holder = holder2;
	}

	@Override
	public Node getNode(Long id) {
		return holder.getNode(id);
	}

	@Override
	public Rel getEdge(RelPK id) {
		return holder.getEdge(id);
	}

	@Override
	public void addListener(IDataUser<Long, RelPK> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(IDataUser<Long, RelPK> listener) {
		listeners.remove(listener);
	}

	@Override
	public Set<IDataUser<Long, RelPK>> getListeners() {
		return listeners;
	}

	@Override
	public void setListeners(Set<IDataUser<Long, RelPK>> l) {
		listeners=l;
	}
}
