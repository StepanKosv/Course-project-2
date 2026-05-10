import org.junit.jupiter.api.Test;

import reddit_collect_pascage.RedditUrlUtilities;

import static org.junit.jupiter.api.Assertions.*;

class RedditUrlUtilitiesTest {

    @Test
    void testExtractAddress() {
        // Примеры из ТЗ
        assertEquals("u/ABOBA", RedditUrlUtilities.extractAddress("u/ABOBA"));
        assertEquals("user/ABOBA", RedditUrlUtilities.extractAddress("reddit.com/user/ABOBA/.json"));
        assertEquals("r/math", RedditUrlUtilities.extractAddress("www.reddit.com/r/math"));
        assertEquals("r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac",
                     RedditUrlUtilities.extractAddress("https://www.reddit.com/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/?tl=ru"));

        // Граничные случаи
        assertNull(RedditUrlUtilities.extractAddress(null));
        assertEquals(null, RedditUrlUtilities.extractAddress(""));
        assertEquals("", RedditUrlUtilities.extractAddress("/"));
        assertEquals("", RedditUrlUtilities.extractAddress("https://www.reddit.com"));
        assertEquals("r/news", RedditUrlUtilities.extractAddress("old.reddit.com/r/news"));
        assertEquals("r/javahelp", RedditUrlUtilities.extractAddress("https://np.reddit.com/r/javahelp/"));
    }

    @Test
    void testToJsonUrl() {
        assertEquals("https://www.reddit.com/user/ABOBA/.json",
                     RedditUrlUtilities.toJsonUrl("reddit.com/user/ABOBA/.json"));
        assertEquals("https://www.reddit.com/r/math/.json",
                     RedditUrlUtilities.toJsonUrl("www.reddit.com/r/math"));
        assertEquals("https://www.reddit.com/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/.json",
                     RedditUrlUtilities.toJsonUrl("https://www.reddit.com/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/?tl=ru"));
        assertNull(RedditUrlUtilities.toJsonUrl(null));
    }

    @Test
    void testDetermineType() {
        assertEquals(RedditUrlUtilities.PageType.USER, 
                     RedditUrlUtilities.determineType("u/ABOBA"));
        assertEquals(RedditUrlUtilities.PageType.USER, 
                     RedditUrlUtilities.determineType("reddit.com/user/ABOBA/.json"));
        assertEquals(RedditUrlUtilities.PageType.SUBREDDIT, 
                     RedditUrlUtilities.determineType("www.reddit.com/r/math"));
        assertEquals(RedditUrlUtilities.PageType.POST, 
                     RedditUrlUtilities.determineType("https://www.reddit.com/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/"));
        assertEquals(RedditUrlUtilities.PageType.COMMENT, 
                     RedditUrlUtilities.determineType("https://www.reddit.com/r/scifi/comments/17hsxyu/title/k6pk6ux/"));
        assertEquals(RedditUrlUtilities.PageType.UNKNOWN, 
                     RedditUrlUtilities.determineType("https://google.com"));
        assertEquals(RedditUrlUtilities.PageType.UNKNOWN, 
                     RedditUrlUtilities.determineType(null));
    }
}