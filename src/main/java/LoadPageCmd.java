
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoadPageCmd implements ICollectCommand {
    protected String url;
    protected RedditCollectorVer2 collector;

    public LoadPageCmd(RedditCollectorVer2 collector, String url) {
        this.collector = collector;
        this.url = url;
    }

    @Override
    public void wait_timeout() {
        try {
            TimeUnit.SECONDS.sleep(collector.sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        try {
        	collector.state.putMess("LoadPageCmd.run on "+url);
            // 1. Перенос из finded в loaded
            collector.findedPages.remove(url);
            collector.loadedPages.add(url);

            // 2. HTTP запрос
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            HttpResponse<String> response = collector.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 200) {
            	collector.state.putMess("LoadPageCmd.run succesfull loaded (200) "+url);
                // 3. Парсинг JSON
                JsonNode json = new ObjectMapper().readTree(response.body());
                collector.urlToJson.put(url, json);
                try {
                    Files.writeString(Path.of(collector.urlFile), url+" -> "+json + System.lineSeparator(),
                            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    // В production здесь должен быть fallback в System.err или консоль
                    System.err.println("Url write failed: " + e.getMessage());
                    collector.state.putErr("Url write failed: " + e.getMessage());
                }
                LightJsonParsingResult localResult = new LightJsonParsingResult();
                LightJsonParsingResult.readJson(json, localResult);
                
                // 4. Поглощение в общий Result
                collector.result.consume(localResult);

                // 5. Генерация новых команд
                enqueueFromResult(localResult);
            } else if (status >= 300 && status < 400) {
            	collector.state.putWarn("LoadPageCmd.run redirect status "+status);
                // Редирект
                response.headers().firstValue("Location").ifPresent(loc -> {
                    String newUrl = RedditUrlUtilities.toJsonUrl(loc);
                    collector.state.putWarn("LoadPageCmd.run redirected on "+newUrl);
                    if (newUrl != null) {
                        collector.putRedirect(url, newUrl);
                        collector.addCmd(new LoadPageCmd(collector, newUrl));
                    }
                });
            } else if (status == 404) {
                collector.state.putWarn("404 Not Found: " + url);
            } else {
                collector.state.putCritical("HTTP " + status + " for " + url);
            }
        } catch (Exception e) {
            if (collector.isRelease) {
                collector.state.putErr("Runtime error on " + url + ": " + e.getMessage());
            } else {
                collector.state.putCritical("Runtime error on " + url + ": " + e.getMessage());
            }
        }
    }

    @Override public boolean isDeprecated() { return collector.loadedPages.contains(url); }
    @Override public boolean isExecutable() { return true; }
    
    @Override public String toString() {
    	return "["+super.toString()+", url:"+url+"]";
    }

    // === FACTORIES ===
    //пока не используется
    public static LoadPageCmd fromPostUrl(RedditCollectorVer2 c, String url) {
        return new LoadPageCmd(c, RedditUrlUtilities.toJsonUrl(url));
    }

    public static LoadPageCmd fromSubredditName(RedditCollectorVer2 c, String name) {
        String url = "https://www.reddit.com/r/" + name + "/.json";
        return new LoadPageCmd(c, url);
    }

    public static List<LoadPageCmd> fromUserName(RedditCollectorVer2 c, String name) {
        String base = "https://www.reddit.com/user/" + name;
        List<LoadPageCmd> cmds = new ArrayList<>();
        cmds.add(new LoadPageCmd(c, base + "/.json"));
        cmds.add(new LoadPageCmd(c, base + "/submitted/.json"));
        cmds.add(new LoadPageCmd(c, base + "/comments/.json"));
        return cmds;
    }
    //пока не используется
    public static LoadPageCmd postFromComment(RedditCollectorVer2 c, String commentUrl) {
        String postUrl = commentUrl.replaceAll("/comments/[^/]+/[^/]+/[^/]+$", "/comments/[^/]+/[^/]+$")
                                   .replaceFirst("/[^/]+/.json$", "/.json");
        // Упрощённая логика обрезки: берём всё до 3-го сегмента после /comments/
        String[] parts = RedditUrlUtilities.extractAddress(commentUrl).split("/");
        if (parts.length >= 4 && "comments".equals(parts[2])) {
            String safeUrl = "https://www.reddit.com/r/" + parts[1] + "/comments/" + parts[3] + "/.json";
            return new LoadPageCmd(c, safeUrl);
        }
        return fromPostUrl(c, commentUrl);
    }
    //пока нигде не используется
    public static LoadCommentCmd fromCommentUrl(RedditCollectorVer2 c, String url) {
        return new LoadCommentCmd(c, RedditUrlUtilities.toJsonUrl(url));
    }

    private void enqueueFromResult(LightJsonParsingResult res) {
    	//пока оставил пустую логику по url без дифференциации для постов
    	res.notedRedditUrls.forEach(u -> {
            String j = RedditUrlUtilities.toJsonUrl(u);
            if (j != null && !collector.loadedPages.contains(j)) { 
                collector.addCmd(new LoadPageCmd(collector, j));
            }
        });
//        // Добавляем посты
//        res.notedPostUrls.forEach(u -> {
//            String j = RedditUrlUtilities.toJsonUrl(u);
//            if (j != null && !collector.loadedPages.contains(j)) 
//                collector.cmdSheldue.add(new LoadPageCmd(collector, j));
//        });
//        // Добавляем комменты (как посты, т.к. комментарии в Reddit API доступны через пост)
//        res.notedCommentsUrls.forEach(u -> {
//            String j = RedditUrlUtilities.toJsonUrl(u);
//            if (j != null && !collector.loadedPages.contains(j))
//                collector.cmdSheldue.add(LoadPageCmd.fromCommentUrl(collector, j));
//        });
        // Добавляем юзеров и сабреддиты
        res.notedUsernames.forEach(u -> 
        	LoadPageCmd.fromUserName(collector, u).forEach(cmd->collector.addCmd(cmd)));
            //collector.cmdSheldue.addAll(LoadPageCmd.fromUserName(collector, u)));
        res.notedSubredditsNames.forEach(s -> 
            collector.addCmd(LoadPageCmd.fromSubredditName(collector, s)));
    }
}