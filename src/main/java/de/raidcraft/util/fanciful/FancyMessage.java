package de.raidcraft.util.fanciful;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import de.raidcraft.util.ArrayWrapper;
import de.raidcraft.util.Reflection;
import net.md_5.bungee.api.chat.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Represents a formattable message. Such messages can use elements such as colors, formatting codes, hover and click data, and other features provided by the vanilla Minecraft <a href="http://minecraft.gamepedia.com/Tellraw#Raw_JSON_Text">JSON message formatter</a>.
 * This class allows plugins to emulate the functionality of the vanilla Minecraft <a href="http://minecraft.gamepedia.com/Commands#tellraw">tellraw command</a>.
 * <p>
 * This class follows the builder pattern, allowing for method chaining.
 * It is set up such that invocations of property-setting methods will affect the current editing component,
 * and a call to {@link #then()} or {@link #then(String)} will append a new editing component to the end of the message,
 * optionally initializing it with withText. Further property-setting method calls will affect that editing component.
 * </p>
 */
public class FancyMessage implements JsonRepresentedObject, Cloneable, Iterable<MessagePart>, ConfigurationSerializable {

    static {
        ConfigurationSerialization.registerClass(FancyMessage.class);
    }

    private final ComponentBuilder builder;

    private List<MessagePart> messageParts;
    private String jsonString;
    private boolean dirty;

    private static Constructor<?> nmsPacketPlayOutChatConstructor;

    @Override
    public FancyMessage clone() throws CloneNotSupportedException {
        FancyMessage instance = (FancyMessage) super.clone();
        instance.messageParts = new ArrayList<MessagePart>(messageParts.size());
        for (int i = 0; i < messageParts.size(); i++) {
            instance.messageParts.add(i, messageParts.get(i).clone());
        }
        instance.dirty = false;
        instance.jsonString = null;
        return instance;
    }

    public ComponentBuilder spigot() {
        return this.builder;
    }

    /**
	 * Creates a JSON message with withText.
     *
	 * @param firstPartText The existing withText in the message.
     */
    public FancyMessage(final String firstPartText) {
        this.builder = new ComponentBuilder(firstPartText);
    }

    /**
	 * Creates a JSON message without withText.
     */
    public FancyMessage() {
        this.builder = new ComponentBuilder("");
    }

    /**
	 * Sets the withText of the current editing component to a value.
     *
	 * @param text The new withText of the current editing component.
     * @return This builder instance.
     */
    public FancyMessage text(String text) {
        builder.append(text);
        return this;
    }

    public FancyMessage color(final net.md_5.bungee.api.ChatColor color) {
        this.builder.color(color);
        return this;
    }

    public FancyMessage style(final net.md_5.bungee.api.ChatColor... styles) {
        for (final net.md_5.bungee.api.ChatColor style : styles) {
            switch (style) {
                case STRIKETHROUGH:
                    this.builder.strikethrough(true);
                    break;
                case BOLD:
                    this.builder.bold(true);
                    break;
                case ITALIC:
                    this.builder.italic(true);
                    break;
                case UNDERLINE:
                    this.builder.underlined(true);
                    break;
                case RESET:
                    this.builder.reset();
                    break;
            }
        }
        return this;
    }

    public FancyMessage append(FancyMessage message) {
        this.builder.append(message.create());
        return this;
    }

    public FancyMessage then(FancyMessage message) {
        return this.append(message);
    }

    /**
     * Sets the color of the current editing component to a value.
     *
     * @param color The new color of the current editing component.
     * @return This builder instance.
     * @throws IllegalArgumentException If the specified {@code ChatColor} enumeration value is not a color (but a format value).
     */
    public FancyMessage color(final ChatColor color) {
        if (!color.isColor()) {
            throw new IllegalArgumentException(color.name() + " is not a color");
        }
        this.builder.color(color.asBungee());
        return this;
    }

    /**
     * Sets the stylization of the current editing component.
     *
     * @param styles The array of styles to apply to the editing component.
     * @return This builder instance.
     * @throws IllegalArgumentException If any of the enumeration values in the array do not represent formatters.
     */
    public FancyMessage style(ChatColor... styles) {
        for (final ChatColor style : styles) {
            switch (style) {
                case STRIKETHROUGH:
                    this.builder.strikethrough(true);
                    break;
                case BOLD:
                    this.builder.bold(true);
                    break;
                case ITALIC:
                    this.builder.italic(true);
                    break;
                case UNDERLINE:
                    this.builder.underlined(true);
                    break;
                case RESET:
                    this.builder.reset();
                    break;
            }
        }
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to open a file on the client side filesystem when the currently edited part of the {@code FancyMessage} is clicked.
     *
     * @param path The path of the file on the client filesystem.
     * @return This builder instance.
     */
    public FancyMessage file(final String path) {
        this.builder.event(new ClickEvent(ClickEvent.Action.OPEN_FILE, path));
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to open a webpage in the client's web browser when the currently edited part of the {@code FancyMessage} is clicked.
     *
     * @param url The URL of the page to open when the link is clicked.
     * @return This builder instance.
     */
    public FancyMessage link(final String url) {
        this.builder.event(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to replace the chat input box content with the specified string when the currently edited part of the {@code FancyMessage} is clicked.
     * The client will not immediately send the command to the server to be executed unless the client player submits the command/chat message, usually with the enter key.
     *
	 * @param command The withText to display in the chat bar of the client.
     * @return This builder instance.
     */
    public FancyMessage suggest(final String command) {
        this.builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to append the chat input box content with the specified string when the currently edited part of the {@code FancyMessage} is SHIFT-CLICKED.
     * The client will not immediately send the command to the server to be executed unless the client player submits the command/chat message, usually with the enter key.
     *
	 * @param command The withText to append to the chat bar of the client.
     * @return This builder instance.
     */
    public FancyMessage insert(final String command) {
        this.builder.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        return this;
    }

    /**
     * Set the behavior of the current editing component to instruct the client to send the specified string to the server as a chat message when the currently edited part of the {@code FancyMessage} is clicked.
     * The client <b>will</b> immediately send the command to the server to be executed when the editing component is clicked.
     *
	 * @param command The withText to display in the chat bar of the client.
     * @return This builder instance.
     */
    public FancyMessage command(final String command) {
        this.builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }


    public FancyMessage newLine() {
        this.builder.append("\n");
        return this;
    }

    public FancyMessage lineBreak() {
        return newLine().newLine();
    }

    /**
	 * Set the behavior of the current editing component to display information about an item when the client hovers over the withText.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param itemJSON A string representing the JSON-serialized NBT data tag of an {@link ItemStack}.
     * @return This builder instance.
     */
    public FancyMessage itemTooltip(final String itemJSON) {
        this.builder.event(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponent[]{new TextComponent(itemJSON)}));
        return this;
    }

    /**
	 * Set the behavior of the current editing component to display information about an item when the client hovers over the withText.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
     * @param itemStack The stack for which to display information.
     * @return This builder instance.
     */
    public FancyMessage itemTooltip(final ItemStack itemStack) {
        try {
            Object nmsItem = Reflection.getMethod(Reflection.getOBCClass("inventory.CraftItemStack"), "asNMSCopy", ItemStack.class).invoke(null, itemStack);
            return itemTooltip(Reflection.getMethod(Reflection.getNMSClass("ItemStack"), "save", Reflection.getNMSClass("NBTTagCompound")).invoke(nmsItem, Reflection.getNMSClass("NBTTagCompound").newInstance()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            return this;
        }
    }

    /**
	 * Set the behavior of the current editing component to display raw withText when the client hovers over the withText.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
	 * @param text The withText, which supports newlines, which will be displayed to the client upon hovering.
     * @return This builder instance.
     */
    public FancyMessage tooltip(final String text) {
        this.builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(text)}));
        return this;
    }

    /**
	 * Set the behavior of the current editing component to display raw withText when the client hovers over the withText.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
	 * @param lines The lines of withText which will be displayed to the client upon hovering. The iteration order of this object will be the order in which the lines of the tooltip are created.
     * @return This builder instance.
     */
    public FancyMessage tooltip(final Iterable<String> lines) {
        tooltip(ArrayWrapper.toArray(lines, String.class));
        return this;
    }

    /**
	 * Set the behavior of the current editing component to display raw withText when the client hovers over the withText.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
	 * @param lines The lines of withText which will be displayed to the client upon hovering.
     * @return This builder instance.
     */
    public FancyMessage tooltip(final String... lines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            builder.append(lines[i]);
            if (i != lines.length - 1) {
                builder.append('\n');
            }
        }
        tooltip(builder.toString());
        return this;
    }

    /**
	 * Set the behavior of the current editing component to display formatted withText when the client hovers over the withText.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
	 * @param text The formatted withText which will be displayed to the client upon hovering.
     * @return This builder instance.
     */
    public FancyMessage formattedTooltip(FancyMessage text) {
        this.builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.spigot().create()));
        return this;
    }

    /**
	 * Set the behavior of the current editing component to display the specified lines of formatted withText when the client hovers over the withText.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
	 * @param lines The lines of formatted withText which will be displayed to the client upon hovering.
     * @return This builder instance.
     */
    public FancyMessage formattedTooltip(FancyMessage... lines) {

        ComponentBuilder componentBuilder = new ComponentBuilder("");

        for (FancyMessage line : lines) {
            componentBuilder.append(line.create());
        }

        builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
        return this;
    }

    /**
	 * Set the behavior of the current editing component to display the specified lines of formatted withText when the client hovers over the withText.
     * <p>Tooltips do not inherit display characteristics, such as color and styles, from the message component on which they are applied.</p>
     *
	 * @param lines The lines of withText which will be displayed to the client upon hovering. The iteration order of this object will be the order in which the lines of the tooltip are created.
     * @return This builder instance.
     */
    public FancyMessage formattedTooltip(final Iterable<FancyMessage> lines) {
        return formattedTooltip(ArrayWrapper.toArray(lines, FancyMessage.class));
    }

    /**
     * Terminate construction of the current editing component, and begin construction of a new message component.
     * After a successful call to this method, all setter methods will refer to a new message component, created as a result of the call to this method.
     *
	 * @param text The withText which will populate the new message component.
     * @return This builder instance.
     */
    public FancyMessage then(final String text) {
        this.builder.append(text);
        return this;
    }

    /**
     * Terminate construction of the current editing component, and begin construction of a new message component.
     * After a successful call to this method, all setter methods will refer to a new message component, created as a result of the call to this method.
     *
     * @return This builder instance.
     */
    public FancyMessage then() {
        if (!latest().hasText()) {
            throw new IllegalStateException("previous message part has no text");
        }
        this.builder.append(new BaseComponent[]{});
        return this;
    }

    @Override
    public void writeJson(JsonWriter writer) throws IOException {
        if (messageParts.size() == 1) {
            latest().writeJson(writer);
        } else {
            writer.beginObject().name("text").value("").name("extra").beginArray();
            for (final MessagePart part : this) {
                part.writeJson(writer);
            }
            writer.endArray().endObject();
        }
    }

    /**
     * Serialize this fancy message, converting it into syntactically-valid JSON using a {@link JsonWriter}.
     * This JSON should be compatible with vanilla formatter commands such as {@code /tellraw}.
     *
     * @return The JSON string representing this object.
     */
    public String toJSONString() {
        if (!dirty && jsonString != null) {
            return jsonString;
        }
        StringWriter string = new StringWriter();
        JsonWriter json = new JsonWriter(string);
        try {
            writeJson(json);
            json.close();
        } catch (IOException e) {
            throw new RuntimeException("invalid message");
        }
        jsonString = string.toString();
        dirty = false;
        return jsonString;
    }

    public BaseComponent[] create() {
        return this.builder.create();
    }

    /**
     * Sends this message to a player. The player will receive the fully-fledged formatted display of this message.
     *
     * @param player The player who will receive the message.
     */
    public void send(Player player) {
        player.spigot().sendMessage(builder.create());
    }

    /**
     * Sends this message to a command sender.
     * If the sender is a player, they will receive the fully-fledged formatted display of this message.
     * Otherwise, they will receive a version of this message with less formatting.
     *
     * @param sender The command sender who will receive the message.
     */
    public void send(CommandSender sender) {
        sender.spigot().sendMessage(create());
    }

    /**
     * Sends this message to multiple command senders.
     *
     * @param senders The command senders who will receive the message.
     * @see #send(CommandSender)
     *
     * @deprecated Use {@link Player.Spigot#sendMessage(BaseComponent...)} after creating your ChatMessage with {@link #create()}.
     */
    @Deprecated
    public void send(final Iterable<? extends CommandSender> senders) {
        BaseComponent[] components = create();
        for (CommandSender sender : senders) {
            sender.spigot().sendMessage(components);
        }
    }

    private MessagePart latest() {
        return messageParts.get(messageParts.size() - 1);
    }

    // Doc copied from interface
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("messageParts", messageParts);
        map.put("JSON", toJSONString());
        return map;
    }

    /**
     * Deserializes a JSON-represented message from a mapping of key-value pairs.
     * This is called by the Bukkit serialization API.
     * It is not intended for direct public API consumption.
     *
     * @param serialized The key-value mapping which represents a fancy message.
     */
    @SuppressWarnings("unchecked")
    public static FancyMessage deserialize(Map<String, Object> serialized) {
        FancyMessage msg = new FancyMessage();
        msg.messageParts = (List<MessagePart>) serialized.get("messageParts");
        msg.jsonString = serialized.containsKey("JSON") ? serialized.get("JSON").toString() : null;
        msg.dirty = !serialized.containsKey("JSON");
        return msg;
    }

    /**
     * <b>Internally called method. Not for API consumption.</b>
     */
    public Iterator<MessagePart> iterator() {
        return messageParts.iterator();
    }

    private static JsonParser _stringParser = new JsonParser();

    /**
     * Deserializes a fancy message from its JSON representation. This JSON representation is of the format of
     * that returned by {@link #toJSONString()}, and is compatible with vanilla inputs.
     *
     * @param json The JSON string which represents a fancy message.
     * @return A {@code FancyMessage} representing the parameterized JSON message.
     */
    public static FancyMessage deserialize(String json) {
        JsonObject serialized = _stringParser.parse(json).getAsJsonObject();
        JsonArray extra = serialized.getAsJsonArray("extra"); // Get the extra component
        FancyMessage returnVal = new FancyMessage();
        returnVal.messageParts.clear();
        for (JsonElement mPrt : extra) {
            MessagePart component = new MessagePart();
            JsonObject messagePart = mPrt.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : messagePart.entrySet()) {
				// Deserialize withText
                if (TextualComponent.isTextKey(entry.getKey())) {
                    // The map mimics the YAML serialization, which has a "key" field and one or more "value" fields
                    Map<String, Object> serializedMapForm = new HashMap<String, Object>(); // Must be object due to Bukkit serializer API compliance
                    serializedMapForm.put("key", entry.getKey());
                    if (entry.getValue().isJsonPrimitive()) {
                        // Assume string
                        serializedMapForm.put("value", entry.getValue().getAsString());
                    } else {
                        // Composite object, but we assume each element is a string
                        for (Map.Entry<String, JsonElement> compositeNestedElement : entry.getValue().getAsJsonObject().entrySet()) {
                            serializedMapForm.put("value." + compositeNestedElement.getKey(), compositeNestedElement.getValue().getAsString());
                        }
                    }
                    component.text = TextualComponent.deserialize(serializedMapForm);
                } else if (MessagePart.stylesToNames.inverse().containsKey(entry.getKey())) {
                    if (entry.getValue().getAsBoolean()) {
                        component.styles.add(MessagePart.stylesToNames.inverse().get(entry.getKey()));
                    }
                } else if (entry.getKey().equals("color")) {
                    component.color = ChatColor.valueOf(entry.getValue().getAsString().toUpperCase());
                } else if (entry.getKey().equals("clickEvent")) {
                    JsonObject object = entry.getValue().getAsJsonObject();
					component.clickActionName = object.get("withAction").getAsString();
                    component.clickActionData = object.get("value").getAsString();
                } else if (entry.getKey().equals("hoverEvent")) {
                    JsonObject object = entry.getValue().getAsJsonObject();
					component.hoverActionName = object.get("withAction").getAsString();
                    if (object.get("value").isJsonPrimitive()) {
                        // Assume string
                        component.hoverActionData = new JsonString(object.get("value").getAsString());
                    } else {
                        // Assume composite type
                        // The only composite type we currently store is another FancyMessage
                        // Therefore, recursion time!
                        component.hoverActionData = deserialize(object.get("value").toString() /* This should properly serialize the JSON object as a JSON string */);
                    }
                } else if (entry.getKey().equals("insertion")) {
                    component.insertionData = entry.getValue().getAsString();
                } else if (entry.getKey().equals("with")) {
                    for (JsonElement object : entry.getValue().getAsJsonArray()) {
                        if (object.isJsonPrimitive()) {
                            component.translationReplacements.add(new JsonString(object.getAsString()));
                        } else {
                            // Only composite type stored in this array is - again - FancyMessages
                            // Recurse within this function to parse this as a translation replacement
                            component.translationReplacements.add(deserialize(object.toString()));
                        }
                    }
                }
            }
            returnVal.messageParts.add(component);
        }
        return returnVal;
    }

}
