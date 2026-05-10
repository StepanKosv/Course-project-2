package reddit_collect_pascage;


public interface ICollectState {
	public static enum ProcessState{
		Active,
		Finished,
		Failed,
		Ready,
		Other
	}
	public static enum ErrorState{
		Normal,
		Warning,
		Error
	}
	boolean isCriticalError();
	boolean isErrors();
	boolean isWarnings();
	String getErrorString();
	String getWarningString();
	String getMessagesString();
	float getCollectPercentage();
	ProcessState getProcessState();
	String getStateString();
}
