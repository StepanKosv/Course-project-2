package com.stepankosvin.plugin_fasade;

public interface IPlugin {
	public void consume(IPluginFasade fasade);
	public String name();
}
