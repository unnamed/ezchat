package me.fixeddev.ezchat.format;

import me.fixeddev.ezchat.format.part.ChatPart;
import me.fixeddev.ezchat.format.part.EasyChatPart;

import java.util.ArrayList;
import java.util.List;

public class FormatConverter {
    public static NewChatFormat convertFormat(ChatFormat chatFormat) {
        List<ChatPart<?>> chatPartList = new ArrayList<>();

        chatPartList.add(new EasyChatPart(chatFormat.getPrefix(), chatFormat.getPrefixClickAction(), chatFormat.getPrefixClickActionContent(), chatFormat.getPrefixTooltip()));
        chatPartList.add(new EasyChatPart(chatFormat.getPlayerName(), chatFormat.getPlayerNameClickAction(), chatFormat.getPlayerNameClickActionContent(), chatFormat.getPlayerNameTooltip()));
        chatPartList.add(new EasyChatPart(chatFormat.getSuffix(), chatFormat.getSuffixClickAction(), chatFormat.getSuffixClickActionContent(), chatFormat.getSuffixTooltip()));

        return new NewChatFormat(chatFormat.getFormatName(), chatFormat.getPriority(), chatFormat.getChatColor(), chatFormat.getPermission(), chatFormat.isUsePlaceholderApi(), chatPartList);
    }

}
