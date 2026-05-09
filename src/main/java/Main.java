

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;

public class Main {

	public static void main(String[] args) {
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
		RedditCollectorVer2 RC=new RedditCollectorVer2(
				pref+"logsFile.log", pref+"stateFile.txt", pref+"resultFile.txt", pref+"urlFile.txt"
				);
		//random
		int seed=249;
		RC.random=new Random(seed);
		RC.state.putMess("random seed: "+seed);
		//cmd
		RC.addCmd(new LoadPageCmd(RC,"https://www.reddit.com/r/mathmemes/comments/1sgzvxx/the_1phone/.json"));
		//collector
		RedditCollectorVer2.Collector collector=RC.new Collector(
				//Executors.newSingleThreadExecutor(),
				3 //iter
				);
		//run
		collector.run();

		System.out.println("parser stop");
//		RedditCollectorVer1 RC=new RedditCollectorVer1();
//		HashSet<String> startUrls=new HashSet<>();
//		startUrls.add("https://www.reddit.com/r/mathmemes/comments/1sgzvxx/the_1phone/.json");
//		startUrls.add(
//				"https://www.reddit.com/r/eclipse/comments/1expxew/a_package_name_must_be_specified_for_a_module/.json");
//		startUrls.add("https://www.reddit.com/r/desmos/comments/1snoas5/is_this_a_bug/");
//		// Получаем текущую дату и время
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String nowstr = now.format(formatter);
//        int seed=126;
//        
//		RedditCollectorVer1.Collector collector=RC.new Collector(
//				10,
//				50,
//				"stateFile_"+nowstr+".txt",
//				"resultFile_"+nowstr+".txt",
//				"nodesFile_"+nowstr+".txt",
//				"RedditCollectorVer1_logs_"+nowstr+".log",
//				startUrls,
//				new Random(seed)
//				);
//		collector.run();
	}

}
