package com.stepankosvin.app_interface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import model.Node;
import model.Rel;
import model.RelPK;

import java.awt.Dimension;
import java.util.List;

import javax.swing.SwingUtilities;

import com.stepankosvin.plugin_fasade.IDataUser;
import com.stepankosvin.plugin_fasade.IGraphHolder;
import com.stepankosvin.plugin_fasade.IScopeHolder;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;

public class GraphViewController implements IDataUser<Long, model.RelPK> {
	private IGraphHolder<Long,RelPK,Node,Rel> dataHolder;

	@FXML
	private Button refreshButton;

	@FXML
	private Button closeButton;

	@FXML
	private StackPane graphContainer;

	@FXML
	private SwingNode swingNode;

	public void setHolder(IGraphHolder<Long,RelPK,Node,Rel> h) {
		this.dataHolder = h;
		this.dataHolder.addListener(this);
	}

	@FXML
	private void refresh(ActionEvent event) {
		// Логика обновления JUNG графа
		this.refresh();
	}

	@FXML
	private void close(ActionEvent event) {
		// Логика закрытия панели или приложения
		this.close();
	}

	@Override
	public void update(List<RelPK> edgeKeyList, List<Long> nodeKeyList) {
		// TODO Auto-generated method stub
		this.refresh();
	}

	@Override
	public void refresh() {
		SwingUtilities.invokeLater(() -> updateView());
		System.out.println("GraphViewController.refresh");
	}

	private void updateView() {
		// 2. Настраиваем layout и визуализацию JUNG
		Layout<Long, model.RelPK> layout = new KKLayout<>(this.dataHolder.getGraph());
		//edu.uci.ics.jung.algorithms.layout.KKLayout<V, E>
		layout.setSize(new Dimension(400, 400));
		VisualizationViewer<Long, model.RelPK> vv = new VisualizationViewer<>(layout);
		vv.setPreferredSize(
				new Dimension((int)graphContainer.getWidth(), (int)graphContainer.getHeight()));

		// Слушатель для адаптации размеров ПЕРЕД отрисовкой
		graphContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
			double width = newVal.doubleValue();
			double height = graphContainer.getHeight();
			if (width > 0 && height > 0) {
				SwingUtilities.invokeLater(() -> updateGraphSize(vv, layout, width, height));
			}
		});

		graphContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
			double height = newVal.doubleValue();
			double width = graphContainer.getWidth();
			if (width > 0 && height > 0) {
				SwingUtilities.invokeLater(() -> updateGraphSize(vv, layout, width, height));
			}
		});

		// Добавляем интерактивность (мышь)
		DefaultModalGraphMouse<String, Integer> graphMouse = new DefaultModalGraphMouse<>();
		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(graphMouse);

		// отображение вершин
		// pass
		// отображение ребер
		// pass

		// Установка контента в SwingNode
		javafx.application.Platform.runLater(() -> {
			swingNode.setContent(vv);
		});
	}

	private void updateGraphSize(VisualizationViewer<Long, RelPK> vv, Layout<Long, RelPK> layout, double width,
			double height) {
		java.awt.Dimension size = new java.awt.Dimension((int) width, (int) height);
		layout.setSize(size);
		vv.setPreferredSize(size);
		vv.setSize(size);
		vv.setGraphLayout(layout); // Пересчет позиций вершин под новый размер
	}

	@Override
	public void close() {
		// TODO добавить логику закрытия окна
		System.out.println("GraphViewController.close");
	}
	
}
