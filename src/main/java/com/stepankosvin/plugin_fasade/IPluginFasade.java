package com.stepankosvin.plugin_fasade;
import java.util.Map;
import java.util.Set;

import com.stepankosvin.db_integration.DBFasade;
import com.stepankosvin.reddit_collect_pascage.ICollectResult;
import com.stepankosvin.reddit_collect_pascage.ICollectState;
import com.stepankosvin.reddit_collect_pascage.ICollector;
import com.stepankosvin.reddit_collect_pascage.ICollectState.ProcessState;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import model.Node;
import model.Rel;
import model.RelPK;

public interface IPluginFasade {
	public interface ButtonClick{
		public void consume(ActionEvent e, Scene scene);
	}
	public interface CollectProcess{
		public Scene getScene();
		public ICollector<? extends ICollectState, ? extends ICollectResult> getCollector();
		public void appendMessage(String message);
		public void updateState();
	}
	public interface GraphActionButton{
		public void consume(IGraphHolder<Long,RelPK,Node,Rel> holder, ActionEvent e, Scene scene);
	}
	public void setHolder(IScopeHolder holder);
	public IScopeHolder getScopeHolder();
	public void addAction(String name, ButtonClick action);
	public void removeAction(String name);
	public DBFasade getDBFasade();
	public Parent createCollectProcess(
			ICollector<? extends ICollectState, ? extends ICollectResult> collector);
	public void addGraphAction(String name, GraphActionButton action);
	public void removeGraphAction(String name);
	public Map<String,GraphActionButton> getGraphActions();
}
