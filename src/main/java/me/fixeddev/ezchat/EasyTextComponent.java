package me.fixeddev.ezchat;

import me.fixeddev.ezchat.util.ReflectionUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EasyTextComponent {

    private static final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    private BaseComponent builder;
    private BaseComponent pointer;

    public EasyTextComponent() {
        this(new TextComponent(""));
    }

    private EasyTextComponent(@NotNull BaseComponent component) {
        this.builder = component;
        pointer = builder;
    }

    @NotNull
    public EasyTextComponent appendWithNewLine(@NotNull String content) {
        return appendWithNewLine(appendAll(fromLegacyText(content, ChatColor.WHITE)));
    }

    @NotNull
    public EasyTextComponent appendWithNewLine(@NotNull BaseComponent component) {
        return append(component).addNewLine();
    }

    @NotNull
    public EasyTextComponent append(@NotNull String content) {
        return append(appendAll(fromLegacyText(content, ChatColor.WHITE)));
    }

    @NotNull
    public EasyTextComponent append(@NotNull BaseComponent component) {
        pointer.addExtra(component);
        pointer = component;

        return this;
    }

    @NotNull
    public EasyTextComponent append(@NotNull EasyTextComponent easyComponent) {
        builder.addExtra(easyComponent.builder);

        return this;
    }


    @NotNull
    public EasyTextComponent addNewLine() {
        return append("\n");
    }

    @NotNull
    public EasyTextComponent setHoverShowText(@NotNull String content) {
        return setHoverShowText(fromLegacyText(content, ChatColor.WHITE));
    }

    @NotNull
    public EasyTextComponent setHoverShowText(@NotNull EasyTextComponent component) {
        return setHoverShowText(new BaseComponent[]{component.builder});
    }

    @NotNull
    public EasyTextComponent setHoverShowText(@NotNull BaseComponent[] component) {
        return setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, component));
    }

    @NotNull
    public EasyTextComponent setHoverShowItem(@NotNull ItemStack item) {
        return setHoverShowItem(appendAll(ComponentSerializer.parse(convertItemStackToJson(item))));
    }

    @NotNull
    public EasyTextComponent setHoverShowItem(@NotNull EasyTextComponent component) {
        return setHoverShowItem(component.builder);
    }

    @NotNull
    public EasyTextComponent setHoverShowItem(@NotNull BaseComponent component) {
        return setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{component}));
    }

    @NotNull
    public EasyTextComponent setHoverEvent(@NotNull HoverEvent event) {
        builder.setHoverEvent(event);

        return this;
    }

    @NotNull
    public EasyTextComponent setClickRunCommand(@NotNull String command) {
        return setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    @NotNull
    public EasyTextComponent setClickSuggestCommand(@NotNull String command) {
        return setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
    }

    @NotNull
    public EasyTextComponent setClickOpenUrl(@NotNull String url) {
        return setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }

    @NotNull
    public EasyTextComponent setClickEvent(@NotNull ClickEvent event) {
        builder.setClickEvent(event);

        return this;
    }


    public BaseComponent build() {
        return builder;
    }

    /**
     * Created by sainttx
     * Taken from here: https://www.spigotmc.org/threads/tut-item-tooltips-with-the-chatcomponent-api.65964/
     *
     * @param itemStack
     * @return
     */
    private String convertItemStackToJson(ItemStack itemStack) {
        // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
        Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
        Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
        Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
        Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
        Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
        Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
        Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
            return null;
        }

        // Return a string representation of the serialized object
        return itemAsJsonObject.toString();
    }

    @NotNull
    public static BaseComponent appendAll(BaseComponent[] components) {
        if (components.length == 0) {
            throw new IllegalArgumentException("Appending 0 components is not allowed!");
        }

        BaseComponent root = null;
        BaseComponent parent = null;

        for (BaseComponent component : components) {
            if (parent == null) {
                parent = component;
                root = parent;

                continue;
            }

            parent.addExtra(component);
            parent = component;
        }

        return root;
    }

    /**
     * Converts the old formatting system that used
     * {@link net.md_5.bungee.api.ChatColor#COLOR_CHAR} into the new json based
     * system.
     *
     * @param message      the text to convert
     * @param defaultColor color to use when no formatting is to be applied
     *                     (i.e. after ChatColor.RESET).
     * @return the components needed to print the message to the client
     */
    public static BaseComponent[] fromLegacyText(String message, ChatColor defaultColor) {
        List<BaseComponent> components = new ArrayList<BaseComponent>();
        StringBuilder builder = new StringBuilder();
        TextComponent component = new TextComponent();
        Matcher matcher = url.matcher(message);

        boolean reset = false;

        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == ChatColor.COLOR_CHAR) {
                if (++i >= message.length()) {
                    break;
                }
                c = message.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    c += 32;
                }
                ChatColor format;
                if (c == 'x' && i + 12 < message.length()) {
                    StringBuilder hex = new StringBuilder("#");
                    for (int j = 0; j < 6; j++) {
                        hex.append(message.charAt(i + 2 + (j * 2)));
                    }
                    try {
                        format = ChatColor.of(hex.toString());
                    } catch (IllegalArgumentException | NoSuchMethodError ex) {
                        format = null;
                    }

                    i += 12;
                } else {
                    format = ChatColor.getByChar(c);
                }
                if (format == null) {
                    continue;
                }

                if (builder.length() > 0) {
                    TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }

                if(reset){
                    component.setBold(false);
                    component.setItalic(false);
                    component.setUnderlined(false);
                    component.setStrikethrough(false);
                    component.setObfuscated(false);
                }

                if (format == ChatColor.BOLD) {
                    component.setBold(true);
                } else if (format == ChatColor.ITALIC) {
                    component.setItalic(true);
                } else if (format == ChatColor.UNDERLINE) {
                    component.setUnderlined(true);
                } else if (format == ChatColor.STRIKETHROUGH) {
                    component.setStrikethrough(true);
                } else if (format == ChatColor.MAGIC) {
                    component.setObfuscated(true);
                } else if (format == ChatColor.RESET) {
                    format = defaultColor;

                    component = new TextComponent();
                    component.setBold(false);
                    component.setItalic(false);
                    component.setUnderlined(false);
                    component.setStrikethrough(false);
                    component.setObfuscated(false);
                    reset = true;

                    component.setColor(format);
                } else {
                    component = new TextComponent();
                    component.setColor(format);
                }
                continue;
            }
            int pos = message.indexOf(' ', i);
            if (pos == -1) {
                pos = message.length();
            }
            if (matcher.region(i, pos).find()) { //Web link handling

                if (builder.length() > 0) {
                    TextComponent old = component;
                    component = new TextComponent(old);
                    old.setText(builder.toString());
                    builder = new StringBuilder();
                    components.add(old);
                }

                TextComponent old = component;
                component = new TextComponent(old);
                String urlString = message.substring(i, pos);
                component.setText(urlString);
                component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        urlString.startsWith("http") ? urlString : "http://" + urlString));
                components.add(component);
                i += pos - i - 1;
                component = old;
                continue;
            }
            builder.append(c);
        }

        component.setText(builder.toString());
        components.add(component);

        return components.toArray(new BaseComponent[0]);
    }
}
