package dev.knalis.sao_telegram_bot.dto.telegram;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ButtonRow {
    private final List<Button> buttons;
    
    public ButtonRow(List<Button> buttons) {
        this.buttons = buttons;
    }
    
    public ButtonRow() {
        this.buttons = new ArrayList<>();
    }
    
    public ButtonRow(Button... buttons) {
        this.buttons = new ArrayList<>(List.of(buttons));
    }
    
    public static ButtonRow of(Button ... buttons) {
        return new ButtonRow(List.of(buttons));
    }
    
    public List<Button> getButtons() {
        return List.of((Button) buttons);
    }
    
    public void add(Button button) {
        this.buttons.add(button);
    }
    
    public void addAll(List<Button> buttons) {
        this.buttons.addAll(buttons);
    }
    
    public void addAll(Button... buttons) {
        this.buttons.addAll(List.of(buttons));
    }
    
    public List<InlineKeyboardButton> toInlineKeyboardButtons() {
        List<InlineKeyboardButton> inlineButtons = new ArrayList<>();
        for (Button button : buttons) {
            inlineButtons.add(button.toInlineButton());
        }
        return inlineButtons;
    }
    
    public int size() {
        return buttons.size();
    }
    
    public boolean isEmpty() {
        return buttons.isEmpty();
    }
    
    public void clear() {
        buttons.clear();
    }
}
