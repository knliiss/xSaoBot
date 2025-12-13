package dev.knalis.sao_telegram_bot.util;

public final class TextLayout {
    
    static final int STANDARD_LINES = 10;
    
    private TextLayout() {
    }
    
    public static String formatWithFiller(String rawText) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("<b>──────────────────────────────────</b>\n");
        sb.append(rawText).append("\n");
        
        int lines = rawText.split("\n").length;
        int missing = Math.max(0, STANDARD_LINES - lines);
        
        for (int i = 0; i < missing; i++) {
            sb.append("\n");
        }
        
        sb.append("<b>──────────────────────────────────</b>\n");
        return sb.toString();
    }
}
