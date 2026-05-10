package reddit_collect_pascage;
import com.fasterxml.jackson.databind.JsonNode;

import db_integration.RedditCollectResultWritingVer1;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class RedditCollectorVer2 {
    
    // ==================== FIELDS ====================
    public int sleepTime = 10;
    public Logger logger = Logger.getLogger(RedditCollectorVer2.class.getName());
    public HttpClient httpClient = HttpClient.newHttpClient();
    public boolean isRelease = false;
    
    public String logsFile = "logs.txt";
    public String stateFile = "state.json";
    public String resultFile = "result.txt";
    public String urlFile = "urls.txt";
    
    public Random random = new Random();
    public Set<String> loadedPages = new HashSet<>();
    public ArraySet<String> findedPages = new ArraySet<>();
    private ArraySet<ICollectCommand> cmdSheldue = new ArraySet<>();
    
    public Map<String, JsonNode> urlToJson = new HashMap<>();
    public Map<String, String> urlToRedirect = new HashMap<>();
    
    public Result result;
    public State state;
    
    //write to db
    public RedditCollectResultWritingVer1 resultWriter;

    // ==================== CONSTRUCTOR ====================
    public RedditCollectorVer2(String logsFile, String stateFile, String resultFile, String urlFile, 
    		RedditCollectResultWritingVer1 _resultWriter) {
        this.logsFile = logsFile;
        this.stateFile = stateFile;
        this.resultFile = resultFile;
        this.urlFile = urlFile;
        
        this.state = new State(this.logsFile, this.stateFile);
        this.result = new Result();
        this.state.putMess("Collector initialized");
        this.resultWriter = _resultWriter;
    }
    
    //инкапсулирую логику редиректов для отладки
    public void putRedirect(String oldUrl, String newUrl) {
    	if(urlToRedirect.containsKey(oldUrl)&&urlToRedirect.get(oldUrl)!=newUrl) {
    		state.putWarn("unexpected overwrited redirection from "+
    				oldUrl+" old: "+urlToRedirect.get(oldUrl)+" new: "+newUrl);
    	}
    	if(urlToRedirect.getOrDefault(oldUrl, null)!=newUrl) {
    		try {
                Files.writeString(Path.of(urlFile), oldUrl+" -> "+newUrl + System.lineSeparator(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                // В production здесь должен быть fallback в System.err или консоль
                System.err.println("Url write failed: " + e.getMessage());
                state.putErr("Url write failed: " + e.getMessage());
            }
    	}
    	urlToRedirect.put(oldUrl, newUrl);
    }
    //инкапсулирую логику добавления команд для отладки
    public void addCmd(ICollectCommand cmd) {
    	state.putMess("add cmd: "+cmd);
    	this.cmdSheldue.add(cmd);
    }

    // ==================== NESTED CLASS: State ====================
    public class State implements ICollectState {
        public ProcessState state = ProcessState.Ready;
        public List<String> errors = new ArrayList<>();
        public List<String> warns = new ArrayList<>();
        public List<String> messages = new ArrayList<>();
        
        private final String logsFile;
        private final String stateFile;

        public State(String logsFile, String stateFile) {
            this.logsFile = logsFile;
            this.stateFile = stateFile;
        }

        @Override public ProcessState getProcessState() { return state; }
        @Override public String getStateString() { return state.name(); }
        @Override public boolean isCriticalError() { return state == ProcessState.Failed; }
        @Override public boolean isErrors() { return !errors.isEmpty(); }
        @Override public boolean isWarnings() { return !warns.isEmpty(); }
        @Override public String getErrorString() { return String.join(" | ", errors); }
        @Override public String getWarningString() { return String.join(" | ", warns); }
        @Override public String getMessagesString() { return String.join(" | ", messages); }
        
        @Override public float getCollectPercentage() {
            // Заглушка: расчёт будет добавлен при реализации Collector
            return 0.0f; 
        }

        public void putMess(String mess) {
        	RedditCollectorVer2.this.logger.info(mess);
            messages.add(mess);
            appendToLog("[INFO] " + mess);
        }

        public void putWarn(String mess) {
        	RedditCollectorVer2.this.logger.warning(mess);
            warns.add(mess);
            appendToLog("[WARN] " + mess);
        }

        public void putErr(String mess) {
        	RedditCollectorVer2.this.logger.severe(mess);
            errors.add(mess);
            appendToLog("[ERR] " + mess);
        }

        public void putCritical(String err) {
            putErr(err);
            state = ProcessState.Failed;
        }

        public String fullString() {
            return String.format("STATE: %s\nMESSAGES: %d\nWARNINGS: %d\nERRORS: %d",
                    state, messages.size(), warns.size(), errors.size());
        }

        public void saveState() {
            try {
                Files.writeString(Path.of(stateFile), fullString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                
            } catch (IOException e) {
                logger.warning("Failed to save state file: " + e.getMessage());
            }
        }

        private void appendToLog(String entry) {
            try {
                Files.writeString(Path.of(logsFile), entry + System.lineSeparator(),
                        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                // В production здесь должен быть fallback в System.err или консоль
                System.err.println("Log write failed: " + e.getMessage());
            }
        }
    }

    public class Result extends LightJsonParsingResult implements ICollectResult {

        /**
         * Поглощает другой результат парсинга.
         * Сливает все агрегированные коллекции и обрабатывает ссылки согласно ТЗ:
         * конвертирует в JSON-формат и добавляет в findedPages, пропуская уже загруженные.
         */
//        public void consume(LightJsonParsingResult other) {
//            if (other == null) return;
//
//            // 1. Слияние агрегированных данных
//            notedRedditUrls.addAll(other.notedRedditUrls);
//            notedPostUrls.addAll(other.notedPostUrls);
//            notedCommentsUrls.addAll(other.notedCommentsUrls);
//            notedUsernames.addAll(other.notedUsernames);
//            notedSubredditsNames.addAll(other.notedSubredditsNames);
//
//            readedPosts.addAll(other.readedPosts);
//            readedComments.addAll(other.readedComments);
//            readedListings.addAll(other.readedListings);
//
//            idToPostBody.putAll(other.idToPostBody);
//            idToCommentBody.putAll(other.idToCommentBody);
//            urlToPostBody.putAll(other.urlToPostBody);
//            urlToCommentBody.putAll(other.urlToCommentBody);
//
//            // 2. Обработка ссылок согласно ТЗ
//            for (String url : other.notedRedditUrls) {
//                String jsonUrl = RedditUrlUtilities.toJsonUrl(url);
//                if (jsonUrl != null) {
//                    // Инвариант: не добавляем в очередь, если страница уже загружена
//                    if (!loadedPages.contains(jsonUrl)) {
//                        findedPages.add(jsonUrl);
//                    }
//                }
//            }
//        }
    	public void consume(LightJsonParsingResult other) {
    	    if (other == null) return;

    	    // 1. Слияние агрегированных данных с подсчётом новых элементов
    	    int newUrls = 0, newUsernames = 0, newSubreddits = 0;
    	    Set<String> freshUrls = new HashSet<>();
    	    Set<String> freshUsernames = new HashSet<>();
    	    Set<String> freshSubreddits = new HashSet<>();
    	    
    	    for (String url : other.notedRedditUrls) {
    	        if (notedRedditUrls.add(url)) {
    	            newUrls++;
    	            freshUrls.add(url);
    	        }
    	    }
    	    notedPostUrls.addAll(other.notedPostUrls);
    	    notedCommentsUrls.addAll(other.notedCommentsUrls);
    	    
    	    for (String user : other.notedUsernames) {
    	        if (notedUsernames.add(user)) {
    	            newUsernames++;
    	            freshUsernames.add(user);
    	        }
    	    }
    	    for (String sub : other.notedSubredditsNames) {
    	        if (notedSubredditsNames.add(sub)) {
    	            newSubreddits++;
    	            freshSubreddits.add(sub);
    	        }
    	    }
    	    other.readedPosts.forEach(resultWriter::addMessageTransact); //save posts
    	    readedPosts.addAll(other.readedPosts);
    	    other.readedComments.forEach(resultWriter::addMessageTransact); //save comments
    	    readedComments.addAll(other.readedComments);
    	    readedListings.addAll(other.readedListings);

    	    idToPostBody.putAll(other.idToPostBody);
    	    idToCommentBody.putAll(other.idToCommentBody);
    	    urlToPostBody.putAll(other.urlToPostBody);
    	    urlToCommentBody.putAll(other.urlToCommentBody);

    	    // 2. Логирование находок с выводом самих списков
    	    if (!freshUrls.isEmpty()) {
    	        RedditCollectorVer2.this.state.putMess("Found " + newUrls + " new Reddit URLs: " + 
    	            String.join(", ", freshUrls));
    	    }
    	    if (!freshUsernames.isEmpty()) {
    	        RedditCollectorVer2.this.state.putMess("Found " + newUsernames + " new usernames: " + 
    	            String.join(", ", freshUsernames));
    	    }
    	    if (!freshSubreddits.isEmpty()) {
    	        RedditCollectorVer2.this.state.putMess("Found " + newSubreddits + " new subreddits: " + 
    	            String.join(", ", freshSubreddits));
    	    }

    	    // 3. Обработка ссылок согласно ТЗ
    	    int queued = 0;
    	    Set<String> queuedUrls = new HashSet<>();
    	    for (String url : other.notedRedditUrls) {
    	        String jsonUrl = RedditUrlUtilities.toJsonUrl(url);
    	        if (jsonUrl != null && !loadedPages.contains(jsonUrl)) {
    	            if (findedPages.add(jsonUrl)) {
    	                queued++;
    	                queuedUrls.add(jsonUrl);
    	            }
    	        }
    	    }
    	    if (!queuedUrls.isEmpty()) {
    	        RedditCollectorVer2.this.state.putMess("Queued " + queued + " new pages: " + 
    	            String.join(", ", queuedUrls));
    	    }
    	}

        /** Выводит сводку всех собранных данных */
        public String fullString() {
            return String.join("\n",
                "=== COLLECTOR RESULT ===",
                "Reddit URLs: " + notedRedditUrls.size()+" "+notedRedditUrls,
                "Posts: " + readedPosts.size()+" "+readedPosts,
                "Comments: " + readedComments.size()+" "+readedComments,
                "Listings: " + readedListings.size()+" "+readedListings,
                "Usernames: " + notedUsernames.size()+" "+notedUsernames,
                "Subreddits: " + notedSubredditsNames.size()+" "+notedSubredditsNames,
                "Loaded Pages: " + loadedPages.size()+" "+loadedPages,
                "Pending (finded): " + findedPages.size()+" "+findedPages,
                "Mapped Posts (ID): " + idToPostBody.size()+" "+idToPostBody,
                "Mapped Comments (ID): " + idToCommentBody.size()+" "+idToCommentBody,
                "========================"
            );
        }

        /** Сохраняет сводку в resultFile */
        public void saveResult() {
            try {
                Files.writeString(Path.of(resultFile), fullString(),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                logger.warning("Failed to save result to " + resultFile + ": " + e.getMessage());
            }
        }
    }
    public class Collector implements ICollector<State, Result> {
        public int maxIter = 500;
        //public ExecutorService executor;
//        public Collector(ExecutorService e) {
//        	executor=e;
//        }

        public Collector(int maxIt) {
        	//this.executor=e;
        	this.maxIter=maxIt;
		}
		@Override public State getState() { return state; }
        @Override public Result getResult() { return result; }

        @Override
        public void run() {
            state.state = ICollectState.ProcessState.Active;
            int iter = 0;

            while (state.state == ICollectState.ProcessState.Active && iter < maxIter) {
                ICollectCommand cmd = cmdSheldue.getRandom(random);
                iter++;
                if (cmd == null) break; // Очередь пуста
                state.putMess("get cmd: "+cmd);
                if (cmd.isExecutable()) {
                	state.putMess("run cmd: "+cmd);
                    cmd.wait_timeout();
                    cmd.run();
                }

                if (cmd.isDeprecated()) {
                	state.putMess("remove cmd: "+cmd);
                    cmdSheldue.remove(cmd);
                }
            }

            if (state.state == ICollectState.ProcessState.Active) {
                state.putMess("Collector loop finished. Iterations: " + iter);
                state.state = ICollectState.ProcessState.Finished;
            }
            //executor.shutdown();
            getResult().saveResult();
        }

        @Override
        public void stop() {
            state.state = ICollectState.ProcessState.Finished;
            state.putMess("Collector stopped manually");
        }
    }
}