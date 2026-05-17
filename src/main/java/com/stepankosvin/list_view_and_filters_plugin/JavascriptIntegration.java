//package com.stepankosvin.list_view_and_filters_plugin;
//
//import com.stepankosvin.plugin_fasade.IPluginFasade.IScopeHolder;
//
//import javafx.scene.web.WebEngine;
//import model.RelPK;
//import netscape.javascript.JSObject;
//import javafx.scene.web.WebEngine;
//
//public class JavascriptIntegration {
//	public Object intreprete(String userJsCode, WebEngine webEngine) {
//		return webEngine.executeScript(userJsCode);
//	}
//	public void setContextNode(WebEngine webEngine, Long nodeId, IScopeHolder holder) {
//		JSObject window = (JSObject) webEngine.executeScript("window");
//		window.setMember("node",new NodeJsWrapper(holder.getNode(nodeId),webEngine));
//	}
//	public void setContextEgde(WebEngine webEngine, RelPK pk, IScopeHolder holder) {
//		
//	}
//	
//}
