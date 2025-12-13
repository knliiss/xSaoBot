package dev.knalis.sao_telegram_bot.util;

import java.util.List;

public final class PageSlice {
    
    public static <T> List<T> slice(List<T> list, int page, int pageSize) {
        int from = Math.max(0, (page - 1) * pageSize);
        int to = Math.min(list.size(), from + pageSize);
        return from < to ? list.subList(from, to) : List.of();
    }
    
    public static int totalPages(int total, int pageSize) {
        return Math.max(1, (int) Math.ceil((double) total / pageSize));
    }
}
