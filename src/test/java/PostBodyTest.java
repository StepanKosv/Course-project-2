import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepankosvin.reddit_collect_pascage.PostBody;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostBodyTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testPostBodyExtraction() throws Exception {
        String jsonStr = """
        		{
        		"kind": "t3",
        		"data": {
        		"approved_at_utc": null,
        		"subreddit": "scifi",
        		"selftext": "I’m thinking about starting the Foundation series. I know nothing about the books or the plot.  If you could go back, would you start with the prequels or would you start with Foundation (Book 3 of 7)?  Huge thanks in advance!",
        		"user_reports": [],
        		"saved": false,
        		"mod_reason_title": null,
        		"gilded": 0,
        		"clicked": false,
        		"title": "Best way to read the foundation series by Isaac Asimov",
        		"link_flair_richtext": [],
        		"subreddit_name_prefixed": "r/scifi",
        		"hidden": false,
        		"pwls": 6,
        		"link_flair_css_class": null,
        		"downs": 0,
        		"thumbnail_height": null,
        		"top_awarded_type": null,
        		"hide_score": false,
        		"name": "t3_17hsxyu",
        		"quarantine": false,
        		"link_flair_text_color": "dark",
        		"upvote_ratio": 0.57,
        		"author_flair_background_color": null,
        		"subreddit_type": "public",
        		"ups": 3,
        		"total_awards_received": 0,
        		"media_embed": {},
        		"thumbnail_width": null,
        		"author_flair_template_id": null,
        		"is_original_content": false,
        		"author_fullname": "t2_62kfwrcq",
        		"secure_media": null,
        		"is_reddit_media_domain": false,
        		"is_meta": false,
        		"category": null,
        		"secure_media_embed": {
        		},
        		"link_flair_text": null,
        		"can_mod_post": false,
        		"score": 3,
        		"approved_by": null,
        		"is_created_from_ads_ui": false,
        		"author_premium": false,
        		"thumbnail": "self",
        		"edited": false,
        		"author_flair_css_class": null,
        		"author_flair_richtext": [],
        		"gildings": {},
        		"content_categories": null,
        		"is_self": true,
        		"mod_note": null,
        		"created": 1698428205,
        		"link_flair_type": "text",
        		"wls": 6,
        		"removed_by_category": null,
        		"banned_by": null,
        		"author_flair_type": "text",
        		"domain": "self.scifi",
        		"allow_live_comments": false,
        		"selftext_html": "&lt;!-- SC_OFF --&gt;&lt;div class=\\\"md\\\"&gt;&lt;p&gt;I’m thinking about starting the Foundation series. I know nothing about the books or the plot.  If you could go back, would you start with the prequels or would you start with Foundation (Book 3 of 7)?  Huge thanks in advance!&lt;/p&gt;\\n&lt;/div&gt;&lt;!-- SC_ON --&gt;",
        		"likes": null,
        		"suggested_sort": null,
        		"banned_at_utc": null,
        		"view_count": null,
        		"archived": true,
        		"no_follow": false,
        		"is_crosspostable": true,
        		"pinned": false,
        		"over_18": false,
        		"all_awardings": [],
        		"awarders": [],
        		"media_only": false,
        		"can_gild": false,
        		"spoiler": false,
        		"locked": false,
        		"author_flair_text": null,
        		"treatment_tags": [],
        		"visited": false,
        		"removed_by": null,
        		"num_reports": null,
        		"distinguished": null,
        		"subreddit_id": "t5_2qh2z",
        		"author_is_blocked": false,
        		"mod_reason_by": null,
        		"removal_reason": null,
        		"link_flair_background_color": "",
        		"id": "17hsxyu",
        		"is_robot_indexable": true,
        		"num_duplicates": 0,
        		"report_reasons": null,
        		"author": "VegasBH",
        		"discussion_type": null,
        		"num_comments": 21,
        		"send_replies": true,
        		"media": null,
        		"contest_mode": false,
        		"author_patreon_flair": false,
        		"author_flair_text_color": null,
        		"permalink": "/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/",
        		"stickied": false,
        		"url": "https://www.reddit.com/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/",
        		"subreddit_subscribers": 4465332,
        		"created_utc": 1698428205,
        		"num_crossposts": 0,
        		"mod_reports": [],
        		"is_video": false
        		}
        		}
            """;
        System.out.println(jsonStr);
        JsonNode node = mapper.readTree(jsonStr);
        PostBody post = new PostBody(node);

        assertEquals("postBody", post.type());
        assertEquals("I’m thinking about starting the Foundation series. I know nothing about the books or the plot.  If you could go back, would you start with the prequels or would you start with Foundation (Book 3 of 7)?  Huge thanks in advance!"

, post.text());
        assertEquals("VegasBH", post.getUsername());
        assertEquals("t2_62kfwrcq", post.getUserIdStr());
        assertEquals("https://www.reddit.com/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/", post.url());
        assertEquals("t3_17hsxyu", post.stringId());
        assertEquals("&lt;!-- SC_OFF --&gt;&lt;div class=\"md\"&gt;&lt;p&gt;I’m thinking about starting the Foundation series. I know nothing about the books or the plot.  If you could go back, would you start with the prequels or would you start with Foundation (Book 3 of 7)?  Huge thanks in advance!&lt;/p&gt;\n&lt;/div&gt;&lt;!-- SC_ON --&gt;",
        		post.html());
        assertNull(post.parentId());
        assertNull(post.parentUrl());
        assertEquals("scifi", post.subredditName());
        assertEquals("t5_2qh2z", post.subredditIdString());
        
        // Проверка default infoString()
        //assertEquals("postBody | https://www.reddit.com/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/ | I’m thinking about starting the Foundation series.", post.infoString());
    }
}