package reddit_collect_pascage;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * Агрегатор результатов парсинга JSON.
 * Содержит 25 полей для сбора всех значимых сущностей, связей и метаданных.
 */
/**
 * @deprecated Этот класс невозможно отладить из-за количества полей. 
 * Используйте {@link LightJsonParsingResult} вместо него.
 */
@Deprecated
public class JsonParsingResult {

    // ==================== SETS ====================
    public final Set<String> notedRedditUrls = new HashSet<>();
    public final Set<String> notedPostUrls = new HashSet<>();
    public final Set<String> notedCommentsUrls = new HashSet<>();
    public void AddPostUrl(String url) {
    	addIfNotNull(notedPostUrls,url);
    	addIfNotNull(notedRedditUrls,url);
    }
    public void AddCommentUrl(String url) {
    	addIfNotNull(notedCommentsUrls,url);
    	addIfNotNull(notedRedditUrls,url);
    }
//    public final Set<String> notedIdStrings = new HashSet<>();
//    public final Set<String> notedUserIdStrings = new HashSet<>();
//    public final Set<String> notedPostIdStrings = new HashSet<>();
//    public final Set<String> notedCommentIdStrings = new HashSet<>();
//    public final Set<String> notedSubredditIdStrings = new HashSet<>();
    public final Set<String> notedUsernames = new HashSet<>();
    public final Set<String> notedSubredditsNames = new HashSet<>();

    // ==================== MAPS (Relations) ====================
//    public final Map<String, String> subredditToId = new HashMap<>();
//    public final Map<String, String> idToSubreddit = new HashMap<>();
//    public final Map<String, String> userToId = new HashMap<>();
//    public final Map<String, String> idToUser = new HashMap<>();
//    public final Map<String, String> postToId = new HashMap<>();
//    public final Map<String, String> idToPost = new HashMap<>();
//    public final Map<String, String> commentToId = new HashMap<>();
//    public final Map<String, String> idToComment = new HashMap<>();

    // ==================== LISTS ====================
    public final List<PostBody> readedPosts = new ArrayList<>();
    public final List<CommentBody> readedComments = new ArrayList<>();
    public final List<ListingBody> readedListings = new ArrayList<>();

    // ==================== MAPS (Bodies) ====================
    public final Map<String, JsonBody> idToPostBody = new HashMap<>();
    public final Map<String, JsonBody> idToCommentBody = new HashMap<>();
    public final Map<String, JsonBody> urlToPostBody = new HashMap<>();
    public final Map<String, JsonBody> urlToCommentBody = new HashMap<>();

    // ==================== HELPER METHODS ====================
    private void addIfNotNull(Set<String> set, String val) {
        if (val != null) set.add(val);
    }
    private void putIfNotNull(Map<String, String> map, String k, String v) {
        if (k != null && v != null) map.put(k, v);
    }
    private void putIfNotNullBody(Map<String, JsonBody> map, String k, JsonBody v) {
        if (k != null && v != null) map.put(k, v);
    }

    // ==================== CONSUME METHODS ====================

    /**
     * Агрегирует данные из поста.
     */
    public void consumePost(PostBody post) {
        if (post == null) return;
        // 1. notedRedditUrls
        addIfNotNull(notedRedditUrls, post.url());
        addIfNotNull(notedRedditUrls,post.parentUrl());
        // 2. notedPostUrls
        addIfNotNull(notedPostUrls, post.url());
        // 3. notedIdStrings
//        addIfNotNull(notedIdStrings, post.stringId());
        // 4. notedPostIdStrings
//        addIfNotNull(notedPostIdStrings, post.stringId());
        // 5. notedUsernames
        addIfNotNull(notedUsernames, post.getUsername());
        // 6. notedSubredditsNames
        addIfNotNull(notedSubredditsNames, post.subredditName());
        // 7. notedSubredditIdStrings
//        addIfNotNull(notedSubredditIdStrings, post.subredditIdString());
        // 8. subredditToId
//        putIfNotNull(subredditToId, post.subredditName(), post.subredditIdString());
        // 9. idToSubreddit
//        putIfNotNull(idToSubreddit, post.subredditIdString(), post.subredditName());
        // 10. userToId
//        putIfNotNull(userToId, post.getUsername(), post.getUserIdStr());
        // 11. idToUser
//        putIfNotNull(idToUser, post.getUserIdStr(), post.getUsername());
        // 12. postToId
//        putIfNotNull(postToId, post.url(), post.stringId());
        // 13. idToPost
//        putIfNotNull(idToPost, post.stringId(), post.url());
        // 14. readedPosts
        readedPosts.add(post);
        // 15. idToPostBody
        putIfNotNullBody(idToPostBody, post.stringId(), post);
        // 16. urlToPostBody
        putIfNotNullBody(urlToPostBody, post.url(), post);
        
        // НЕ обновляются: notedCommentsUrls, notedCommentIdStrings, commentToId, idToComment, 
        // readedComments, idToCommentBody, urlToCommentBody, readedListings
    }

    /**
     * Агрегирует данные из комментария.
     */
    public void consumeComment(CommentBody comment) {
        if (comment == null) return;
        // 1. notedRedditUrls
        addIfNotNull(notedRedditUrls, comment.url());
        addIfNotNull(notedRedditUrls,comment.parentUrl());
        // 2. notedCommentsUrls
        addIfNotNull(notedCommentsUrls, comment.url());
        // 3. notedIdStrings
//        addIfNotNull(notedIdStrings, comment.stringId());
        // 4. notedCommentIdStrings
//        addIfNotNull(notedCommentIdStrings, comment.stringId());
        // 5. notedUsernames
        addIfNotNull(notedUsernames, comment.getUsername());
        // 6. notedSubredditsNames
        addIfNotNull(notedSubredditsNames, comment.subredditName());
        // 7. notedSubredditIdStrings
//        addIfNotNull(notedSubredditIdStrings, comment.subredditIdString());
        // 8. subredditToId
//        putIfNotNull(subredditToId, comment.subredditName(), comment.subredditIdString());
        // 9. idToSubreddit
//        putIfNotNull(idToSubreddit, comment.subredditIdString(), comment.subredditName());
        // 10. userToId
//        putIfNotNull(userToId, comment.getUsername(), comment.getUserIdStr());
        // 11. idToUser
//        putIfNotNull(idToUser, comment.getUserIdStr(), comment.getUsername());
        // 12. commentToId
//        putIfNotNull(commentToId, comment.url(), comment.stringId());
        // 13. idToComment
//        putIfNotNull(idToComment, comment.stringId(), comment.url());
        // 14. readedComments
        readedComments.add(comment);
        // 15. idToCommentBody
        putIfNotNullBody(idToCommentBody, comment.stringId(), comment);
        // 16. urlToCommentBody
        putIfNotNullBody(urlToCommentBody, comment.url(), comment);

        // НЕ обновляются: notedPostUrls, notedPostIdStrings, postToId, idToPost, 
        // readedPosts, idToPostBody, urlToPostBody, readedListings
    }

    /**
     * Агрегирует данные из листинга.
     */
    public void consumeListing(ListingBody listing) {
        if (listing == null) return;
        // 1. readedListings
        readedListings.add(listing);
        
        // НЕ обновляются: все Set/Map поля, readedPosts, readedComments, 
        // idToPostBody, idToCommentBody, urlToPostBody, urlToCommentBody
        // (Дочерние элементы обрабатываются отдельно через readJson)
    }

    // ==================== STATIC PARSER ====================

    /**
     * Рекурсивно проходит по JSON в глубину.
     * Если встречается поле kind, использует ListingBody::classificate 
     * и записывает найденные связи в result.
     */
    public static void readJson(JsonNode json, JsonParsingResult result) {
        if (json == null || result == null) return;

        // 1. Попытка классификации узла по полю kind
        if (json.has("kind")) {
            JsonBody classified = ListingBody.classificate(json);
            
            if (classified instanceof PostBody post) {
                result.consumePost(post);
            } else if (classified instanceof CommentBody comment) {
                result.consumeComment(comment);
            } else if (classified instanceof ListingBody listing) {
                result.consumeListing(listing);
                // Рекурсивно обрабатываем детей листинга
                for (JsonBody child : listing.getChilds()) {
                    readJson(child.getBody(), result);
                }
                return; // Дети уже обработаны, дальнейший обход не нужен
            }
            // JsonHolder игнорируется для потребления, но требует обхода ниже
        }

        // 2. Глубокий обход для узлов без kind или JsonHolder
        if (json.isArray()) {
            for (JsonNode child : json) {
                readJson(child, result);
            }
        } else if (json.isObject()) {
            json.fields().forEachRemaining(entry -> readJson(entry.getValue(), result));
        }
    }
}