package com.stepankosvin.reddit_collect_pascage;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилиты для работы с URL Reddit.
 * Согласно ТЗ: static class, содержит extractAddress, toJsonUrl, determineType.
 */
public final class RedditUrlUtilities {

    private RedditUrlUtilities() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Типы страниц Reddit, определяемые по ссылке
     */
    public enum PageType {
        USER, SUBREDDIT, POST, COMMENT, UNKNOWN
    }

    /**
     * Выделяет из URL внутренний адрес Reddit.
     * Алгоритм: разбивает строку по "/", отбрасывает протокол, домен, параметры и ".json".
     */
    public static String extractAddress(String url) {
        if (url == null || url.trim().isEmpty()) {
            return null;
        }

        // Убираем GET-параметры
        String withoutQuery = url.split("\\?")[0];
        String[] parts = withoutQuery.split("/");
        List<String> relevantParts = new ArrayList<>();

        for (String part : parts) {
            String p = part.trim();
            if (p.isEmpty()) continue;
            if (p.matches("https?:")) continue; // протокол
            if (p.toLowerCase().contains("reddit.com")) continue; // домен (включая www., old., np.)
            if (p.toLowerCase().endsWith(".json")) continue; // суффикс json

            relevantParts.add(p);
        }

        return String.join("/", relevantParts);
    }

    /**
     * Преобразует обычный URL в формат JSON API Reddit.
     * Формат: www.reddit.com/{extracted_address}/.json
     */
    public static String toJsonUrl(String url) {
        String address = extractAddress(url);
        if (address == null) return null;
        // Reddit API требует суффикс .json в конце пути
        return "https://www.reddit.com/" + address + "/.json";
    }

    /**
     * Пытается определить тип страницы реддита по виду ссылки.
     */
    public static PageType determineType(String url) {
        String address = extractAddress(url);
        if (address == null) return PageType.UNKNOWN;

        String[] parts = address.split("/");
        if (parts.length < 2) return PageType.UNKNOWN;

        String first = parts[0].toLowerCase();

        // Пользователи: u/ или user/
        if (first.equals("u") || first.equals("user")) {
            return PageType.USER;
        }

        // Сабреддиты и обсуждения: r/
        if (first.equals("r")) {
            // Формат поста: r/sub/comments/post_id/title
            // Формат комментария: r/sub/comments/post_id/title/comment_id
            if (parts.length >= 4 && parts[2].equalsIgnoreCase("comments")) {
                return parts.length > 5 ? PageType.COMMENT : PageType.POST;
            }
            return PageType.SUBREDDIT;
        }

        return PageType.UNKNOWN;
    }
}