import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class CollectorCommandsTest {
    private RedditCollectorVer2 collector;
    private RedditCollectorVer2.Collector collectorCore;

    @BeforeEach
    void setUp() {
        collector = new RedditCollectorVer2("logs.txt", "state.txt", "result.txt", "urls.txt");
        collectorCore = collector.new Collector();
        collectorCore.maxIter = 10; // Для быстрых тестов
    }

    @Test
    void testFactories() {
        // fromPostUrl
    	LoadPageCmd postCmd = LoadPageCmd.fromPostUrl(collector, "r/scifi/comments/123/test");
        assertNotNull(postCmd);
        assertTrue(postCmd.url.contains("/.json"));

        // fromSubredditName
        LoadPageCmd subCmd = LoadPageCmd.fromSubredditName(collector, "math");
        assertEquals("https://www.reddit.com/r/math/.json", subCmd.url);

        // fromUserName -> 3 команды
        var userCmds = LoadPageCmd.fromUserName(collector, "dev_user");
        assertEquals(3, userCmds.size());
        assertTrue(userCmds.get(0).url.contains("/.json"));
        assertTrue(userCmds.get(1).url.contains("/submitted/.json"));
        assertTrue(userCmds.get(2).url.contains("/comments/.json"));
    }

    @Test
    void testLoadPageCmdDeprecatedAndExecutable() {
        String url = "https://www.reddit.com/r/test/.json";
        LoadPageCmd cmd = new LoadPageCmd(collector, url);

        assertTrue(cmd.isExecutable());
        assertFalse(cmd.isDeprecated());

        collector.loadedPages.add(url);
        assertTrue(cmd.isDeprecated());
    }

    @Test
    void testLoadCommentCmdLogic() {
        String commentUrl = "https://www.reddit.com/r/scifi/comments/abc123/title/xyz789/.json";
        String postUrl = "https://www.reddit.com/r/scifi/comments/abc123/.json";
        
        LoadCommentCmd cmd = new LoadCommentCmd(collector, commentUrl);
        assertTrue(cmd.isExecutable()); // Пост ещё не загружен
        assertFalse(cmd.isDeprecated());

        collector.loadedPages.add(postUrl);
        assertFalse(cmd.isExecutable()); // Пост загружен -> нет смысла грузить коммент отдельно
        assertTrue(cmd.isDeprecated());
    }

    @Test
    void testCollectorRunLoopAndStop() {
        collector.addCmd(new LoadPageCmd(collector, "https://mock.reddit.com/.json"));
        
        assertEquals(ICollectState.ProcessState.Ready, collector.state.getProcessState());
        
        collectorCore.run();
        
        // После run состояние должно стать Finished (очередь обработана или лимит)
        assertEquals(ICollectState.ProcessState.Finished, collector.state.getProcessState());
        assertTrue(collector.state.getMessagesString().contains("Collector loop finished"));
    }

    @Test
    void testCollectorStop() {
        collector.state.state = ICollectState.ProcessState.Active;
        collectorCore.stop();
        assertEquals(ICollectState.ProcessState.Finished, collector.state.getProcessState());
        assertTrue(collector.state.getMessagesString().contains("Collector stopped manually"));
    }
}