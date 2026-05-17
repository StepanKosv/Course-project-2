package com.stepankosvin.plugin_fasade;

import com.stepankosvin.db_integration.DBFasade;

import edu.uci.ics.jung.graph.Graph;
import model.HashToNodePK;
import model.HashToRelPK;
import model.Node;
import model.Rel;
import model.RelPK;

public interface IScopeHolder extends 
	IDataHolder<Long, model.RelPK, model.Node, model.Rel, model.HashToNodePK, model.HashToRelPK>,
	IGraphHolder<Long, model.RelPK, model.Node, model.Rel>{

	String getScopeName();

	void setDbFasade(DBFasade dbFasade);

	DBFasade getDbFasade();

	void reloadFromDB();

}