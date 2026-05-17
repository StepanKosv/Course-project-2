package com.stepankosvin.plugin_fasade;

import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

public interface IGraphHolder<NodeId, EdgeId,NodeData,EdgeData> 
	extends IDataNode<NodeId, EdgeId, NodeData, EdgeData> {
	Graph<NodeId, EdgeId> getGraph();
}
