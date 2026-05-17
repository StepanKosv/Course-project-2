package com.stepankosvin.clusters_plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.stepankosvin.app_interface.GraphActionsController;
import com.stepankosvin.app_interface.ScopeSelectController;
import com.stepankosvin.plugin_fasade.IDataUser;
import com.stepankosvin.plugin_fasade.IGraphHolder;
import com.stepankosvin.plugin_fasade.IPluginFasade;
import com.stepankosvin.plugin_fasade.IScopeHolder;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Graphs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import model.Node;
import model.Rel;
import model.RelPK;

public class EdgeBetweennessClustererController implements IDataUser<Long,RelPK>{
	private IGraphHolder<Long,RelPK,Node,Rel> holder;
	private IScopeHolder scope;
	private IPluginFasade fasade;
	private List<IGraphHolder<Long,RelPK,Node,Rel>> clusterHolders=new ArrayList<>();
	@FXML 
	public Spinner<Integer> egdesCount;
	@FXML
	private Button refreshButton;
	@FXML
	private VBox clustersParent;
	private class ClusterHolder implements IGraphHolder<Long,RelPK,Node,Rel>{
		private Graph<Long,RelPK> clust;
		private Set<IDataUser<Long, RelPK>> listeners;
		public ClusterHolder(Graph<Long,RelPK> c) {clust=c;}
		@Override
		public Node getNode(Long id) {
			// TODO Auto-generated method stub
			return scope.getNode(id);
		}

		@Override
		public Rel getEdge(RelPK id) {
			// TODO Auto-generated method stub
			return scope.getEdge(id);
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

		@Override
		public Graph<Long, RelPK> getGraph() {
			return clust;
		}
	}
	@FXML
	void handleRefresh(ActionEvent event) {
		var future = CompletableFuture.runAsync(()->
			refresh()
		);
	}
	public void refresh() {
		clusterHolders.forEach(c->c.close());
		clusterHolders.clear();
		EdgeBetweennessClusterer<Long,RelPK> clusterer = 
				new EdgeBetweennessClusterer<>(egdesCount.getValue());

		// 2. Extract clusters (returns a Set of node sets, where each set is a cluster)
		Set<Set<Long>> clusterSet = clusterer.apply(holder.getGraph());
		Set<RelPK> removed = Set.copyOf(clusterer.getEdgesRemoved());
		
		for(var cluster:clusterSet) {
			Graph<Long,RelPK> g=Graphs.synchronizedGraph(new SparseMultigraph<>());
			for(var v:cluster) {
				g.addVertex(v);
			}
			for(var v:cluster) {
				for(var e:holder.getGraph().getIncidentEdges(v)) {
					if((!removed.contains(e))&&(!g.containsEdge(e))) {
						g.addEdge(e, e.getLeftNodeFk(),e.getRightNodeFk());
					}
				}
			}
			ClusterHolder ch=new ClusterHolder(g);
			this.clusterHolders.add(ch);
		}
		javafx.application.Platform.runLater(() -> {
			this.clustersParent.getChildren().clear();
			this.clusterHolders.forEach(ch->
					{
						try {
						// 1. Создаем объект загрузчика
				        FXMLLoader loader = new FXMLLoader(getClass().getResource(
				        		"/com/stepankosvin/app_interface/GraphActions.fxml"));
				        
				        // 2. Загружаем иерархию компонентов
				        Parent root = loader.load();
				        
				        // 3. Получаем ссылку на контроллер
				        GraphActionsController controller = loader.getController();
				        controller.setData(fasade, ch);
				        clustersParent.getChildren().add(root);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					);
		});
	}
	@Override
	public void update(List<RelPK> edgeKeyList, List<Long> nodeKeyList) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
