package reddit_collect_pascage;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * Упрощенный агрегатор результатов парсинга.
 * Содержит только поля, необходимые для системы сбора.
 */
//!!!!!
//Todo: сделать более умное поглощение url
public class LightJsonParsingResult {

    // ==================== SETS ====================
    public final Set<String> notedRedditUrls = new HashSet<>();
    public final Set<String> notedPostUrls = new HashSet<>();
    public final Set<String> notedCommentsUrls = new HashSet<>();
    public final Set<String> notedUsernames = new HashSet<>();
    public final Set<String> notedSubredditsNames = new HashSet<>();

    // ==================== LISTS ====================
    public final List<PostBody> readedPosts = new ArrayList<>();
    public final List<CommentBody> readedComments = new ArrayList<>();
    public final List<ListingBody> readedListings = new ArrayList<>(); // исправлена опечатка ТЗ: readedListigs

    // ==================== MAPS ====================
    public final Map<String, PostBody> idToPostBody = new HashMap<>();
    public final Map<String, CommentBody> idToCommentBody = new HashMap<>();
    public final Map<String, PostBody> urlToPostBody = new HashMap<>();
    public final Map<String, CommentBody> urlToCommentBody = new HashMap<>();

    /** Добавляет URL в общий пул. */
    public void consumeUrl(String url) {
        if (url != null) {
            notedRedditUrls.add(url);
            
        }
    }

    /** Агрегирует пост. Поддерживает инвариант notedRedditUrls ⊇ notedPostUrls ∪ notedCommentsUrls */
    public void consumePost(PostBody post) {
        if (post == null) return;
        String url = post.url();
        String id = post.stringId();

        if (url != null) {
            notedPostUrls.add(url);
            consumeUrl(url); // инвариант
            urlToPostBody.put(url, post);
        }
        if (id != null) idToPostBody.put(id, post);
        
        readedPosts.add(post);
        if (post.getUsername() != null) notedUsernames.add(post.getUsername());
        if (post.subredditName() != null) notedSubredditsNames.add(post.subredditName());
    }

    /** Агрегирует комментарий. Поддерживает инвариант. */
    public void consumeComment(CommentBody comment) {
        if (comment == null) return;
        String url = comment.url();
        String id = comment.stringId();

        if (url != null) {
            notedCommentsUrls.add(url);
            consumeUrl(url); // инвариант
            urlToCommentBody.put(url, comment);
        }
        if (id != null) idToCommentBody.put(id, comment);
        
        readedComments.add(comment);
        if (comment.getUsername() != null) notedUsernames.add(comment.getUsername());
        if (comment.subredditName() != null) notedSubredditsNames.add(comment.subredditName());
    }

    /** Агрегирует листинг. */
    public void consumeListing(ListingBody listing) {
        if (listing != null) readedListings.add(listing);
    }

    /**
     * Рекурсивный обход JSON в глубину.
     * При обнаружении поля kind делегирует классификацию в ListingBody.
     */
    public static void readJson(JsonNode json, LightJsonParsingResult result) {
        if (json == null || result == null) return;

        if (json.has("kind")) {
            JsonBody classified = ListingBody.classificate(json);
            
            if (classified instanceof PostBody p) {
                result.consumePost(p);
            } else if (classified instanceof CommentBody c) {
                result.consumeComment(c);
            } else if (classified instanceof ListingBody l) {
                result.consumeListing(l);
                // Рекурсивно проходим по детям листинга
                for (JsonBody child : l.getChilds()) {
                    readJson(child.getBody(), result);
                }
                return; // Дети уже обработаны, выходим из текущей ветки
            }
        }

        // Глубокий обход для объектов без kind или JsonHolder
        if (json.isArray()) {
            for (JsonNode child : json) readJson(child, result);
        } else if (json.isObject()) {
            json.fields().forEachRemaining(entry -> readJson(entry.getValue(), result));
        }
    }
}