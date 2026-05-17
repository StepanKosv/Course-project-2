import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepankosvin.reddit_collect_pascage.CommentBody;
import com.stepankosvin.reddit_collect_pascage.JsonBody;
import com.stepankosvin.reddit_collect_pascage.ListingBody;
import com.stepankosvin.reddit_collect_pascage.PostBody;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ListingBodyTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testClassificateAndChilds() throws Exception {
        String listingJson = """
            {
              "kind": "Listing",
              "data": {
                "after": null,
                "before": null,
                "children": [
                  { "kind": "t3", "data": { "name": "post1", "selftext": "p", "url": "u", "subreddit": "s", "subreddit_id": "sid" } },
                  { "kind": "t1", "data": { "name": "com1", "body": "c", "permalink": "u", "parent_id": "p1", "subreddit": "s", "subreddit_id": "sid", "author": "a", "author_fullname": "aid" } },
                  { "kind": "unknown_kind", "data": { "foo": "bar" } },
                  {}
                ]
              }
            }
            """;
        JsonNode node = mapper.readTree(listingJson);
        ListingBody listing = new ListingBody(node);

        assertEquals("ListingBody | elements: 4", listing.infoString());
        
        List<JsonBody> children = listing.getChilds();
        assertEquals(4, children.size());
        assertTrue(children.get(0) instanceof PostBody);
        assertTrue(children.get(1) instanceof CommentBody);
        assertTrue(children.get(2) instanceof ListingBody.JsonHolder);

        // Тест статического classificate
        JsonNode unknownNode = mapper.readTree("{\"kind\": \"t88\", \"data\": {}}");
        assertTrue(ListingBody.classificate(unknownNode) instanceof ListingBody.JsonHolder);
    }
}