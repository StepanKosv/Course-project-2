import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepankosvin.reddit_collect_pascage.CommentBody;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommentBodyTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void testCommentBodyExtraction() throws Exception {
        String jsonStr = """
            {
         "kind": "t1",
         "data": {
           "subreddit_id": "t5_2qh2z",
           "approved_at_utc": null,
           "author_is_blocked": false,
           "comment_type": null,
           "awarders": [],
           "mod_reason_by": null,
           "banned_by": null,
           "author_flair_type": "text",
           "total_awards_received": 0,
           "subreddit": "scifi",
           "author_flair_template_id": null,
           "likes": null,
           "replies": {
             "kind": "Listing",
             "data": {
               "after": null,
               "dist": null,
               "modhash": "80sauiipi3ebc02bfeac823ffff28e07ae0114c94c710d00ba",
               "geo_filter": "",
               "children": [
                 {
                   "kind": "t1",
                   "data": {
                     "subreddit_id": "t5_2qh2z",
                     "approved_at_utc": null,
                     "author_is_blocked": false,
                     "comment_type": null,
                     "awarders": [],
                     "mod_reason_by": null,
                     "banned_by": null,
                     "author_flair_type": "text",
                     "total_awards_received": 0,
                     "subreddit": "scifi",
                     "author_flair_template_id": null,
                     "likes": null,
                     "replies": "",
                     "user_reports": [],
                     "saved": false,
                     "id": "k6pk6ux",
                     "banned_at_utc": null,
                     "mod_reason_title": null,
                     "gilded": 0,
                     "archived": true,
                     "collapsed_reason_code": null,
                     "no_follow": false,
                     "author": "Texas_Sam2002",
                     "can_mod_post": false,
                     "created_utc": 1698428483,
                     "send_replies": true,
                     "parent_id": "t1_k6pjodm",
                     "score": 12,
                     "author_fullname": "t2_nb135ld5",
                     "removal_reason": null,
                     "approved_by": null,
                     "mod_note": null,
                     "all_awardings": [],
                     "body": "This.  They were written in very different times, so reading in order written is the best way.  Original trilogy first, of course.\\n\\nI have to admit that I didn't care for the direction that Asimov took the later books, and my sense is that it was really just about cashing in, but that was his prerogative.",
                     "edited": false,
                     "top_awarded_type": null,
                     "author_flair_css_class": null,
                     "name": "t1_k6pk6ux",
                     "is_submitter": false,
                     "downs": 0,
                     "author_flair_richtext": [],
                     "author_patreon_flair": false,
                     "body_html": "&lt;div class=\\\"md\\\"&gt;&lt;p&gt;This.  They were written in very different times, so reading in order written is the best way.  Original trilogy first, of course.&lt;/p&gt;\\n\\n&lt;p&gt;I have to admit that I didn&amp;#39;t care for the direction that Asimov took the later books, and my sense is that it was really just about cashing in, but that was his prerogative.&lt;/p&gt;\\n&lt;/div&gt;",
                     "gildings": {


                     },
                     "collapsed_reason": null,
                     "distinguished": null,
                     "associated_award": null,
                     "stickied": false,
                     "author_premium": false,
                     "can_gild": false,
                     "link_id": "t3_17hsxyu",
                     "unrepliable_reason": null,
                     "author_flair_text_color": null,
                     "score_hidden": false,
                     "permalink": "/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/k6pk6ux/",
                     "subreddit_type": "public",
                     "locked": false,
                     "report_reasons": null,
                     "created": 1698428483,
                     "author_flair_text": null,
                     "treatment_tags": [],
                     "collapsed": false,
                     "subreddit_name_prefixed": "r/scifi",
                     "controversiality": 0,
                     "depth": 1,
                     "author_flair_background_color": null,
                     "collapsed_because_crowd_control": null,
                     "mod_reports": [],
                     "num_reports": null,
                     "ups": 12
                   }
                 }
               ],
               "before": null
             }
           },
           "user_reports": [],
           "saved": false,
           "id": "k6pjodm",
           "banned_at_utc": null,
           "mod_reason_title": null,
           "gilded": 0,
           "archived": true,
           "collapsed_reason_code": null,
           "no_follow": false,
           "author": "ISmellElderberries",
           "can_mod_post": false,
           "created_utc": 1698428295,
           "send_replies": true,
           "parent_id": "t3_17hsxyu",
           "score": 36,
           "author_fullname": "t2_ey83273g",
           "approved_by": null,
           "mod_note": null,
           "all_awardings": [],
           "collapsed": false,
           "body": "I always go in the order in which the author wrote and/or published the books.",
           "edited": false,
           "top_awarded_type": null,
           "author_flair_css_class": null,
           "name": "t1_k6pjodm",
           "is_submitter": false,
           "downs": 0,
           "author_flair_richtext": [],
           "author_patreon_flair": false,
           "body_html": "&lt;div class=\\\"md\\\"&gt;&lt;p&gt;I always go in the order in which the author wrote and/or published the books.&lt;/p&gt;\\n&lt;/div&gt;",
           "removal_reason": null,
           "collapsed_reason": null,
           "distinguished": null,
           "associated_award": null,
           "stickied": false,
           "author_premium": false,
           "can_gild": false,
           "gildings": {


           },
           "unrepliable_reason": null,
           "author_flair_text_color": null,
           "score_hidden": false,
           "permalink": "/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/k6pjodm/",
           "subreddit_type": "public",
           "locked": false,
           "report_reasons": null,
           "created": 1698428295,
           "author_flair_text": null,
           "treatment_tags": [],
           "link_id": "t3_17hsxyu",
           "subreddit_name_prefixed": "r/scifi",
           "controversiality": 0,
           "depth": 0,
           "author_flair_background_color": null,
           "collapsed_because_crowd_control": null,
           "mod_reports": [],
           "num_reports": null,
           "ups": 36
         }
       }

            """;
        JsonNode node = mapper.readTree(jsonStr);
        CommentBody comment = new CommentBody(node);

        assertEquals("commentBody", comment.type());
        assertEquals("I always go in the order in which the author wrote and/or published the books.", comment.text());
        assertEquals("ISmellElderberries", comment.getUsername());
        assertEquals("t2_ey83273g", comment.getUserIdStr());
        assertEquals("/r/scifi/comments/17hsxyu/best_way_to_read_the_foundation_series_by_isaac/k6pjodm/", comment.url());
        assertEquals("t1_k6pjodm", comment.stringId());
        assertEquals("t3_17hsxyu", comment.parentId());
        assertNull(comment.parentUrl());
        assertEquals("scifi", comment.subredditName());
        assertEquals("t5_2qh2z", comment.subredditIdString());
        assertNotNull(comment.getReplies());
    }
}