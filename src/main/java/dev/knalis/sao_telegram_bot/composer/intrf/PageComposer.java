package dev.knalis.sao_telegram_bot.composer.intrf;

import dev.knalis.sao_telegram_bot.context.ComposerContext;
import dev.knalis.sao_telegram_bot.context.ContextKey;
import dev.knalis.sao_telegram_bot.dto.telegram.Button;
import dev.knalis.sao_telegram_bot.dto.telegram.ButtonRow;
import dev.knalis.sao_telegram_bot.util.PageSlice;

import java.util.List;

public interface PageComposer extends Composer {
    
    default ButtonRow generateFooter(String callBackUrl, int page, int totalPage) {
        if (totalPage <= 1) return new ButtonRow();

        if (!callBackUrl.endsWith("/")) {
            callBackUrl += "/";
        }

        int prevPage2 = Math.max(1, page - 2);
        int prevPage = Math.max(1, page - 1);
        int nextPage = Math.min(totalPage, page + 1);
        int nextPage2 = Math.min(totalPage, page + 2);

        Button indicator = Button.builder()
                .text(page + "/" + totalPage)
                .callbackData("noop")
                .build();
        
        var row = new ButtonRow();

        if (page > 1) {
            row.add(Button.builder().text("◀️◀️").callbackData(callBackUrl + prevPage2).build());
            row.add(Button.builder().text("◀️").callbackData(callBackUrl + prevPage).build());
        }
        
        row.add(indicator);

        if (page < totalPage) {
            row.add(Button.builder().text("▶️").callbackData(callBackUrl + nextPage).build());
            row.add(Button.builder().text("▶️▶️").callbackData(callBackUrl + nextPage2).build());
        }
        
        return row;
    }
    
    default PageState buildState(ComposerContext context, List list) {
        int page = context.get(ContextKey.PAGE);
        long userId = context.get(ContextKey.USER_ID);
        
        return new PageState(page, PageSlice.totalPages(list.size(), getPageSize()), PageSlice.slice(list, page, getPageSize()), userId);
    }
    
    record PageState(int page, int totalPages, List items, long userId) { }
    
    int getPageSize();

}
