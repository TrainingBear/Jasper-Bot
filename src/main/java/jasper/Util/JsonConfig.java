package jasper.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jasper.Main;

/**
 * the fork of {@link Configurator} for json, using {@code Jackson} API
 * <p>
 * <b>NOTE</b> when passing file statName or json file statName, can or cannot
 * include
 * <code>.json</code> suffix. And case sensitive.
 * <hr>
 * How To Use This Configurator
 * <p>
 * Struktur json
 * <p>
 * .json itu terdiri dari beberapa elemen/tipe objek:
 * <ul>
 * <li>tipe primitif dan {@link String}, primitif kyk int double, char, boolean
 * <li>{@link JsonNode elemen}, seperti class interface/abstract,
 * <b>semua</b> elemen dalam json adalah turunannya dari {@link JsonNode}
 * <li>{@link ArrayNode Array}, kayak [ , , ,]
 * <li>{@link ObjectNode Object}, kayak {} atau badannya json. Bisa
 * diisi dengan key: value seperti contoh {"statName":"JsonConfig"},
 * {@code "statName"} adalah keynya {@code "JsonConfig"} adalah valuenya yang
 * bertipe data {@link}. {@link ObjectNode Object} value dari keynya bisa diisi
 * dengan berbagai jenis seperti {@link ObjectNode Object} itu sendiri dan
 * {@link ArrayNode}, contohnya {"array" : [ , , ,] , "object" : {...}}
 * <li>{@code ,} dan tanda , atau koma adalah pemisah antar elemen dalam json
 * </ul>
 * <p>
 * 
 * 
 * @author Drexyz1945
 */
public final class JsonConfig {
    // ===========================|| Editable Zone ||========================
    public static File DEFAULT_PATH;
    {
        try {
            DEFAULT_PATH = new File(
                    Main.class.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()).getParentFile();
            printLog("[JsonConfig] Set json location file save at: " + DEFAULT_PATH.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printLog(final String msg) {
        Main.sendLog("[JsonConfig] " + msg);
    }
    // ======================================================================

    public static final ObjectMapper jsonHandler = new ObjectMapper();
    private static final DefaultPrettyPrinter prettyWriter = new DefaultPrettyPrinter();
    {
        DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("  ", "\n");
        prettyWriter.indentObjectsWith(indenter);
        prettyWriter.indentArraysWith(indenter);
    }

    private final File parent;
    private Map<String, ObjectNode> jsons = new HashMap<>();
    private Map<String, JsonConfig> compounds = new HashMap<>();
    private final boolean isPrettyPrint;

    public boolean has(String name) {
        return this.jsons.containsKey(name);
    }

    /**
     * create json config instance
     * 
     * @param path        the path for folder to be place, can be null. If null it
     *                    will place to folder where the plugin placed
     * @param folderName  the folder statName that contain the .json files
     * @param prettyPrint whether is pretty print or no/compact
     * @param autoLoad    whether to {@link #load()} all the .json files inside the
     *                    folder when
     *                    construct
     */
    public JsonConfig(@Nullable String path, @Nullable String folderName, boolean prettyPrint, boolean autoLoad) {
        path = path == null ? DEFAULT_PATH.getAbsolutePath() : path;
        this.parent = folderName != null ? new File(path, folderName) : new File(path);
        this.isPrettyPrint = prettyPrint;
        if (!this.parent.exists())
            this.parent.mkdirs();
        if (autoLoad)
            this.load();
    }

    /**
     * create json config instance with default path to plugin folder
     * 
     * @param folderName  the folder statName that contain the .json files
     * @param prettyPrint whether is pretty print or no/compact
     * @param autoLoad    whether to {@link #load()} all the .json files inside the
     *                    folder when construct
     */
    public JsonConfig(@NotNull String folderName, boolean prettyPrint, boolean autoLoad) {
        this(null, folderName, prettyPrint, autoLoad);
    }

    /**
     * create json config instance with default path to plugin folder
     * 
     * @param folder      {@link File} folder that will contain the {@code .json}
     *                    file(s)
     * @param prettyPrint whether is pretty print or no/compact
     * @param autoLoad    whether to {@link #load()} all the .json files inside the
     *                    folder when construct
     */
    public JsonConfig(@NotNull File folder, boolean prettyPrint, boolean autoLoad) {
        this(folder.getAbsolutePath(), null, prettyPrint, autoLoad);
    }

    /**
     * The most essential and crucial, call it bro when run
     * <p>
     * Load all <code>.json</code> files inside the folder config
     * <hr>
     * <b>NOTE</b> Not support if inside the folder hasAbility a folder, the author
     * is lazy
     * ZzzzzZ
     */
    public void load() { // NOT SUPPORT IF INSIDE THE FOLDER HAS A FOLDER, cuz the author is lazy zzZzz
        final File[] faiels = this.parent.listFiles();
        if (faiels == null)
            return;
        try {
            for (final File fil : faiels)
                if (fil.isDirectory()) {
                    JsonConfig config = new JsonConfig(fil, this.isPrettyPrint, true);
                    config.load();
                    this.compounds.put(fil.getName(),
                            config);
                } else if (fil.getName().endsWith(".json"))
                    this.jsons.put(//
                            fil.getName(),
                            (ObjectNode) jsonHandler.readTree(fil)//
                    );
            printLog("Loaded " + this.parent.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * get a compound/.json file from another folder in it
     * <hr>
     * <b>NOTE</b> may <code>null</code>, use the {@link #makeOrGetCompound(String)}
     * instead for safety
     * 
     * @param name the folder statName
     * @return a {@link JsonConfig} that correspond to the folder
     * @deprecated use {@link #getCompound(String)} instead, this method just for
     *             shorten purpose. And there is no intend to remove this method,
     *             just for marks to not recommend use this method for safety
     *             purpose
     */
    @Deprecated
    public @Nullable JsonConfig getCompound(String name) {
        if (name.isEmpty())
            throw new IllegalArgumentException("The compound cannot be empty!");
        return compounds.get(name);
    }

    /**
     * create or get existing <code>Compound</code>/folder
     * <hr>
     * <b>NOTE</b> it auto load if the <code>Compound</code> doesn't exist
     * 
     * @param name the folder/compound statName
     * @return a {@link JsonConfig} that correspond to the folder
     */
    public JsonConfig makeOrGetCompound(String name) {
        JsonConfig comp = this.getCompound(name);
        if (comp != null)
            return comp;
        comp = new JsonConfig(this.parent.getAbsolutePath(), name, this.isPrettyPrint, true);
        this.compounds.put(name, comp);
        return comp;
    }

    /**
     * delete a compound/{@link File folder} inside this {@link JsonConfig}
     * <hr>
     * <b>NOTE</b> This is a dangerous method to use, use it wisely and careful
     * 
     * @param name compound/{@link File folder} to be removed
     * @return this {@link JsonConfig instance} after removing the compound
     */
    public JsonConfig deleteCompound(String name) {
        if (name.isEmpty())
            throw new IllegalArgumentException("The compound cannot be empty!");
        final File folder = new File(this.parent, name);
        if (folder.exists())
            folder.delete();
        this.compounds.remove(name);
        return this;
    }

    /**
     * get the coumpound/folders {@link HashMap}
     * 
     * @return {@link Map}<{@link String folder statName}, {@link JsonConfig}>
     */
    public Map<String, JsonConfig> entry() {
        return this.compounds;
    }

    /**
     * create or get existing <code>.json</code> file
     * <hr>
     * <b>NOTE</b> call {@link #load()} first. If the file doesn't exist then the
     * {@link FileWriter} cannot write the default "{}" it'll return
     * <i><b>null</i></b>
     * 
     * @param nameFile the <code>.json</code> file statName
     * @return {@link ObjectNode}, auto convert. if the file doesn't exist then
     *         {@link FileWriter} cannot write the default "{}" it'll return
     *         <i><b>null</i></b>
     * 
     */
    public ObjectNode createOrGet(String nameFile) {
        if (nameFile.isEmpty())
            throw new IllegalArgumentException("The .json cannot be empty!");
        nameFile = nameFile.endsWith(".json") ? nameFile : nameFile + ".json";
        final File file = new File(this.parent, nameFile);
        if (file.exists())
            return this.get(nameFile);
        ObjectNode obj = null;
        try {
            file.createNewFile();
            try (FileWriter writ = new FileWriter(file)) {
                writ.write("{}");
            }
            obj = (ObjectNode) jsonHandler.readTree(file);
            printLog("Success create json file " + nameFile);
        } catch (Exception e) {
            printLog(nameFile + " cant create: " + e.getMessage());
        }
        this.jsons.put(nameFile, obj);
        return obj;
    }

    /**
     * delete single file <code>.json</code>
     * 
     * @param filename    file statName to delete
     * @param deleteOnMap whether to delete the file from the {@link Map}
     * @return the removed {@link ObjectNode} if param deleteOnMap is true
     */
    public ObjectNode deleteFile(String filename, boolean deleteOnMap) {
        if (filename.isEmpty())
            throw new IllegalArgumentException("The .json cannot be empty!");
        filename = filename.endsWith(".json") ? filename : filename + ".json";
        final File file = new File(this.parent, filename);
        if (file.exists())
            file.delete();
        return deleteOnMap ? this.jsons.remove(filename) : null;
    }

    /**
     * save single file
     * <p>
     * {@link #saveAll()} to save all
     * 
     * @param jsonName the <code>.json</code> file to save
     * @throws IOException             if there is an error with the
     *                                 {@link JsonMapper#writeValueAsString(Object)}
     * @throws JsonProcessingException if there is an error with the
     *                                 {@link JsonMapper#writeValueAsString(Object)}
     */
    public void save(String jsonName) {
        if (jsonName.isEmpty())
            throw new IllegalArgumentException("The .json cannot be empty!");
        final String name = jsonName.endsWith(".json") ? jsonName : jsonName + ".json";
        final ObjectNode obj = this.jsons.get(name);
        if (obj != null)
            try {
                if (this.isPrettyPrint)
                    jsonHandler.writer(prettyWriter).writeValue(new File(this.parent, name), obj);
                else
                    jsonHandler.writeValue(new File(this.parent, name), obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * Save all json files
     * <p>
     * {@link #save(String)} to save a single file
     * <hr>
     * <b>NOTE</b> Only saves json file within parent folder, not another
     * folder/root/branch
     * 
     * @throws IOException             if there is an error with the
     *                                 {@link JsonMapper#writeValueAsString(Object)}
     * @throws JsonProcessingException if there is an error with the
     *                                 {@link ObjectMapper#writeValueAsString(Object)}
     */
    public void saveAll() {
        for (Entry<String, ObjectNode> set : this.jsons.entrySet())
            if (set.getValue() != null)
                try {
                    if (this.isPrettyPrint)
                        jsonHandler.writer(prettyWriter).writeValue(
                                new File(this.parent, set.getKey()), set.getValue());
                    else
                        jsonHandler.writeValue(new File(this.parent, set.getKey()), set.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
    }

    /**
     * get {@link ObjectNode} by the file <code>.json</code> statName
     * <hr>
     * <b>NOTE</b> Do not pass suffix <code>.json</code> after statName
     * 
     * @param jsonName the file statName
     * @return {@link ObjectNode}, make sure call {@link #load()} first
     */
    public ObjectNode get(String jsonName) {
        if (jsonName.isEmpty())
            throw new IllegalArgumentException("The .json statName cannot be empty!");
        jsonName = jsonName.endsWith(".json") ? jsonName : jsonName + ".json";
        return this.jsons.get(jsonName);
    }

    /**
     * get all jsons file
     * 
     * @return the {@link Map}<{@link String}, {@link ObjectNode}> of jsons
     */
    public Map<String, ObjectNode> getJsonsMap() {
        return this.jsons;
    }

    /**
     * editing the json
     * <hr>
     * <b>NOTE</b> make sure the referred file exists, call
     * {@link #createOrGet(String)} as failsafe
     * 
     * @param filename the file statName
     * @param autoSave whether to save the file after editing
     * @param editor   the editor
     * @throws NullPointerException if the file does not exist
     */
    public void edit(String filename, boolean autoSave, Consumer<ObjectNode> editor) {
        ObjectNode obj = this.get(filename);
        if (obj != null) {
            editor.accept(obj);
            if (autoSave)
                this.save(filename);
        } else
            throw new NullPointerException("File " + filename + " does not exist");
    }

    // *****************************************************************************************//
    // --------------------------------Beyond_this,is_the_Util----------------------------------//
    // *****************************************************************************************//

    /**
     * 
     * @param toParse the {@link String} to be parse
     * @return {@link ObjectNode}, may <b>null</b> if exception was thrown or
     *         invalid {@link String} json syntax'es
     */
    public static @Nullable ObjectNode parseFromString(@NotNull final String toParse) {
        ObjectNode obj = null;
        try {
            obj = (ObjectNode) jsonHandler.readTree(toParse);
        } catch (Exception e) {
            printLog("Cant parse");
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * get {@link ArrayNode Array} value (key:[value]) from {@link ObjectNode
     * Compound} also, create
     * the Array if it doesnt exist then return
     * 
     * @param obj the {@link ObjectNode Compound}
     * @param key the {@link String key} to get/create
     * @return the {@link ArrayNode Compound} that inputed, create the
     *         key : {@link ArrayNode compound} inside if there is not exist,
     *         otherwise it'll return the existing {@link ArrayNode Compound}
     */
    public static ArrayNode getOrMakeArray(@NotNull ObjectNode obj, String key) {
        final JsonNode node;
        if (obj.has(key) && (node = obj.get(key)).isArray())
            return (ArrayNode) node;
        ArrayNode array = createArray();
        obj.set(key, array);
        return array;
    }

    /**
     * get {@link ObjectNode Compound} value (key:{value}) from {@link ObjectNode
     * Compound} also, create
     * the Compound if it doesnt exist then return
     * 
     * @param obj the {@link ObjectNode Compound}
     * @param key the {@link String key} to get/create
     * @return the {@link ObjectNode Compound} that inputed, create the
     *         key : {@link ObjectNode compound} inside if there is not exist,
     *         otherwise it'll return the existing {@link ObjectNode Compound}
     */
    public static ObjectNode getOrMakeObject(@NotNull ObjectNode obj, String key) {
        JsonNode node;
        if (obj.has(key) && (node = obj.get(key)).isObject())
            return (ObjectNode) node;
        ObjectNode objek = createObject();
        obj.set(key, objek);
        return objek;
    }

    /**
     * @param array the {@link ArrayNode} to get the {@link String} inside
     * @return the {@link String} array inside the passed param
     */
    public static String[] getStringArray(@NotNull ArrayNode array) {
        String[] arr = new String[array.size()];
        for (int i = 0; i < array.size(); i++)
            arr[i] = array.get(i).asText();
        return arr;
    }

    /**
     * @param array the {@link ArrayNode} to get the int inside
     * @return the int array inside the passed param
     */
    public static int[] getIntArray(@NotNull ArrayNode array) {
        int[] arr = new int[array.size()];
        for (int i = 0; i < array.size(); i++)
            arr[i] = array.get(i).asInt();
        return arr;
    }

    /**
     * @param array the {@link ArrayNode} to get the double inside
     * @return the double array inside the passed param
     */
    public static double[] getDoubleArray(@NotNull ArrayNode array) {
        double[] arr = new double[array.size()];
        for (int i = 0; i < array.size(); i++)
            arr[i] = array.get(i).asDouble();
        return arr;
    }

    /**
     * remove <b>all</b> string that equal passed param
     * 
     * @param array the {@link ArrayNode} to remove the string inside
     * @return the string array inside the passed param
     */
    public static ArrayNode delStrArray(@NotNull ArrayNode array, String strToRemove) {
        ArrayNode arre = jsonHandler.createArrayNode();
        for (JsonNode element : array)
            if (!element.toString().equals(strToRemove))
                arre.add(element);
        return arre;
    }

    /**
     * Creates an {@link ArrayNode}
     * 
     * @return {@link ArrayNode}
     */
    public static ArrayNode createArray() {
        return jsonHandler.createArrayNode();
    }

    /**
     * Creates an {@link ObjectNode}
     * 
     * @return {@link ObjectNode}
     */
    public static ObjectNode createObject() {
        return jsonHandler.createObjectNode();
    }
}
