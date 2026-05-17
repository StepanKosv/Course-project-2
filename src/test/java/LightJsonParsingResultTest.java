import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepankosvin.reddit_collect_pascage.CommentBody;
import com.stepankosvin.reddit_collect_pascage.LightJsonParsingResult;
import com.stepankosvin.reddit_collect_pascage.ListingBody;
import com.stepankosvin.reddit_collect_pascage.PostBody;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class LightJsonParsingResultTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private LightJsonParsingResult result;

    @BeforeEach
    void setUp() {
        result = new LightJsonParsingResult();
    }

    @Test
    void testConsumePost() throws Exception {
        String json = """
            {"kind":"t3","data":{"name":"t3_P1","url":"u/p1","author":"u1","subreddit":"r1","selftext":"t","selftext_html":"h"}}
            """;
        PostBody post = new PostBody(mapper.readTree(json));
        result.consumePost(post);

        assertEquals(1, result.readedPosts.size());
        assertTrue(result.notedPostUrls.contains("u/p1"));
        assertTrue(result.notedRedditUrls.contains("u/p1"));
        assertEquals(post, result.idToPostBody.get("t3_P1"));
        assertEquals(post, result.urlToPostBody.get("u/p1"));
        assertTrue(result.notedUsernames.contains("u1"));
        assertTrue(result.notedSubredditsNames.contains("r1"));
        // Проверяем, что пост НЕ попал в комментарии
        assertTrue(result.readedComments.isEmpty());
    }

    @Test
    void testConsumeComment() throws Exception {
        String json = """
            {"kind":"t1","data":{"name":"t1_C1","permalink":"u/c1","author":"u2","subreddit":"r1","body":"c","body_html":"h","parent_id":"t3_P1"}}
            """;
        CommentBody comment = new CommentBody(mapper.readTree(json));
        result.consumeComment(comment);

        assertEquals(1, result.readedComments.size());
        assertTrue(result.notedCommentsUrls.contains("u/c1"));
        assertTrue(result.notedRedditUrls.contains("u/c1"));
        assertEquals(comment, result.idToCommentBody.get("t1_C1"));
        assertEquals(comment, result.urlToCommentBody.get("u/c1"));
        assertTrue(result.notedUsernames.contains("u2"));
        assertTrue(result.notedSubredditsNames.contains("r1"));
        assertTrue(result.readedPosts.isEmpty());
    }

    @Test
    void testConsumeListing() throws Exception {
        String json = """
            {"kind":"Listing","data":{"children":[]}}
            """;
        ListingBody listing = new ListingBody(mapper.readTree(json));
        result.consumeListing(listing);

        assertEquals(1, result.readedListings.size());
        assertEquals(listing, result.readedListings.get(0));
        // Листинг не наполняет Set/Map напрямую
        assertTrue(result.notedRedditUrls.isEmpty());
        assertTrue(result.readedPosts.isEmpty());
        assertTrue(result.readedComments.isEmpty());
    }

    @Test
    void testConsumeUrlInvariant() {
        result.consumeUrl("https://reddit.com/r/test");
        result.consumeUrl("https://reddit.com/r/test2");
        
        // Инвариант: notedRedditUrls всегда содержит все элементы из notedPostUrls и notedCommentsUrls
        result.consumePost(new PostBody(mapper.createObjectNode().put("kind","t3").set("data", 
            mapper.createObjectNode().put("url","p1").put("name","id1").put("selftext","t").put("selftext_html","h").put("author","a").put("subreddit","s"))));
            
        result.consumeComment(new CommentBody(mapper.createObjectNode().put("kind","t1").set("data", 
            mapper.createObjectNode().put("url","c1").put("name","id2").put("body","t").put("body_html","h").put("author","a").put("subreddit","s").put("parent_id","p1"))));

        assertTrue(result.notedRedditUrls.containsAll(result.notedPostUrls));
        assertTrue(result.notedRedditUrls.containsAll(result.notedCommentsUrls));
        assertEquals(3, result.notedRedditUrls.size()); // "p1", "c1" + 2 из consumeUrl
    }

    @Test
    void testReadJsonDeepTraversal() throws Exception {
        // Структура: Listing -> [Post, Comment -> Replies(Listing) -> Comment]
        String nestedJson = """
            {
              "kind": "Listing",
              "data": {
                "children": [
                  {"kind": "t3", "data": {"name": "t3_P1", "url": "u/p1", "subreddit": "r1", "author": "u1", "selftext": "t", "selftext_html": "h"}},
                  {"kind": "t1", "data": {"name": "t1_C1", "permalink": "u/c1", "subreddit": "r1", "author": "u2", "body": "c", "body_html": "h", "parent_id": "t3_P1",
                    "replies": {"kind": "Listing", "data": {"children": [
                      {"kind": "t1", "data": {"name": "t1_C2", "permalink": "u/c2", "subreddit": "r1", "author": "u3", "body": "r", "body_html": "h", "parent_id": "t1_C1"}}
                    ]}}
                  }}
                ]
              }
            }
            """;
        JsonNode root = mapper.readTree(nestedJson);
        LightJsonParsingResult.readJson(root, result);

        // Проверка глубины обхода
        assertEquals(1, result.readedPosts.size());
        assertEquals(2, result.readedComments.size());
        assertEquals(2, result.readedListings.size()); // Корневой + replies

        // Проверка Set'ов
        assertTrue(result.notedRedditUrls.containsAll(Set.of("u/p1", "u/c1", "u/c2")));
        assertTrue(result.notedUsernames.containsAll(Set.of("u1", "u2", "u3")));
        assertTrue(result.notedSubredditsNames.contains("r1"));

        // Проверка Map связей
        assertEquals("t3_P1", result.idToPostBody.get("t3_P1").stringId());
        assertEquals("t1_C2", result.idToCommentBody.get("t1_C2").stringId());
        assertEquals("u/p1", result.urlToPostBody.get("u/p1").url());
        assertEquals("u/c2", result.urlToCommentBody.get("u/c2").url());

        // Инвариант сохраняется при автоматическом парсинге
        assertTrue(result.notedRedditUrls.containsAll(result.notedPostUrls));
        assertTrue(result.notedRedditUrls.containsAll(result.notedCommentsUrls));
    }
}