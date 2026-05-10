package reddit_collect_pascage;


public interface ICollector<S extends ICollectState, R extends ICollectResult> {
	S getState();
	R getResult();
	void run();
	void stop();
}
