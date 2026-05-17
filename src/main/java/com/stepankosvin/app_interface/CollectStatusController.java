package com.stepankosvin.app_interface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import com.stepankosvin.plugin_fasade.IPluginFasade.CollectProcess;
import com.stepankosvin.reddit_collect_pascage.ICollectResult;
import com.stepankosvin.reddit_collect_pascage.ICollectState;
import com.stepankosvin.reddit_collect_pascage.ICollector;
import com.stepankosvin.reddit_collect_pascage.LoadPageCmd;
import com.stepankosvin.reddit_collect_pascage.RedditCollectorVer2;
import com.stepankosvin.reddit_collect_pascage.ICollectState.ProcessState;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class CollectStatusController implements CollectProcess{

    @FXML private ListView<String> messagesList;
    @FXML private Button stopButton;
    @FXML private Button pauseButton;
    @FXML private Button continueButton;
    @FXML private Button closeButton;
    @FXML private Label collectState;
    //private CollectSettingsController parent;
    private ICollector<? extends ICollectState, ? extends ICollectResult> collector;
    //private RedditCollectorVer2 RC;
    CompletableFuture<Void> future;

    @FXML
    public void initialize() {
        // Слушатель на случай изменения выделенного сообщения в списке (опционально)
        messagesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Выбрано сообщение: " + newVal);
        });
    }

    @FXML
    private void handleStop() {
        System.out.println("Нажата кнопка Stop");
        if(collector!=null) {
        	collector.stop();
        	if(future!=null) {
        		future.join();
        		future=null;
        	}
        }
    }

    @FXML
    private void handlePause() {
        System.out.println("Нажата кнопка Pause");
        if(collector!=null) {
        	collector.getState().setState(ProcessState.Ready);
        	if(future!=null) {
        		future.join();
        		future=null;
        	}
        }
    }

    @FXML
    private void handleContinue() {
        System.out.println("Нажата кнопка Continue");
        if(collector!=null&&collector.getState().getProcessState()==ProcessState.Ready) {
        	assert(future==null);
        	future = CompletableFuture.runAsync(()->collector.run());
        }
    }

    @FXML
    private void handleClose() {
    	if(collector!=null) {
        	collector.stop();
        	if(future!=null) {
        		future.join();
        		future=null;
        	}
        }
    }

	public void setParentAndInit(CollectSettingsController parent) {
		System.out.println("parser start");
		// Получаем текущую дату и время
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH_mm_ss");
		String nowstr = now.format(formatter);
		//файлы
		String pref="log_files_"+nowstr+"/";
		Path p=Path.of(pref);
		try {
			Files.createDirectory(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//rc
		RedditCollectorVer2 RC=new RedditCollectorVer2(
				pref+"logsFile.log", pref+"stateFile.txt", pref+"resultFile.txt", pref+"urlFile.txt",
				(new com.stepankosvin.db_integration.RedditCollectWritingVer2(parent.dataHolder)
						)::addMessageTransact //resultWriter db integration
				);
		//listeners to state
		RC.state = RC.new State(RC.logsFile, RC.stateFile) {
//			public void appendToLog(String entry) {
//				super.appendToLog(entry);
//				javafx.application.Platform.runLater(()->
//					appendMessage(entry));
//			}
//			public void setState(ProcessState state) {
//				super.setState(state);
//				javafx.application.Platform.runLater(()->
//					updateState());
//			}
		};
		//random
		int seed = 553;
		RC.random = new Random(seed);
		RC.state.putMess("random seed: " + seed);
		//cmd
		for(String url:parent.initialPagesList.getItems()) {
			RC.addCmd(new LoadPageCmd(RC,url));
		}
		//collect
		var c = RC.new Collector(
				parent.pagesSpinner.getValue() //count pages
				);
		setCollectorAndInit(c);
	}
	public void setCollectorAndInit(ICollector<? extends ICollectState, ? extends ICollectResult> c) {
		collector=c;
		collector.getState().addMessagesListener(this::appendMessage);
		collector.getState().addUpdateListener(this::updateState);
		future = CompletableFuture.runAsync(()->collector.run());
	}

	@Override
	public Scene getScene() {
		return messagesList.getScene();
	}

	@Override
	public ICollector<? extends ICollectState, ? extends ICollectResult> getCollector() {
		return collector;
	}

	@Override
	public void appendMessage(String message) {
		javafx.application.Platform.runLater(()->
		messagesList.getItems().add(message));
	}

	@Override
	public void updateState() {
		javafx.application.Platform.runLater(()->
		collectState.setText(collector.getState().getStateString()));
	}
}
