package pw.fusionmine.fusionblocks.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class FileChecker {

    HashMap<String, Object> objectHashMap = new HashMap<>();
    HashMap<String, List<String>> commentsMap = new HashMap<>();
    File f;
    YamlConfiguration config;
    String filedir;
    String header;

    public FileChecker(String filedir) {
        this.filedir = filedir;
        loadFile(filedir);
        checkStorage();
    }

    public FileChecker(String filedir, String header) {
        this(filedir);
        this.header = header;
    }

    public void loadFile(String filedir) {
        this.f = new File(filedir);
        if (!this.f.exists()) {
            if (!this.f.getParentFile().exists()) {
                this.f.getParentFile().mkdirs();
            }
            try {
                this.f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.f);

        if (this.header != null) {
            this.config.options().header(this.header);
            try {
                this.config.save(this.f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public abstract void needle();

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(this.f);
        checkStorage();
    }


    public void addParam(String key, Object object) {
        this.objectHashMap.put(key, object);
    }

    public void addParam(String key, Object object, String... comments) {
        this.objectHashMap.put(key, object);
        this.commentsMap.put(key, Arrays.asList(comments));
    }

    public boolean setParam(String key, Object object) throws IOException {
        if (this.objectHashMap.containsKey(key)) {
            this.config.set(key, object);
            this.objectHashMap.put(key, object);
            this.config.save(this.f);
            return true;
        }
        return false;
    }

    public double getDouble(String key) {
        return ((Double) this.objectHashMap.get(key)).doubleValue();
    }

    public String getString(String key) {
        Object object = this.objectHashMap.get(key);

        if (object instanceof String) {
            String string = (String) object;
            string = MinecraftColor.format(string);
            return string;
        }
        return null;
    }


    private String applyFormatting(String input, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value != null) {
                input = input.replace(key, value);
            }
        }
        return MinecraftColor.format(input);
    }

    public List<?> getParamList(String key) {
        Object object = this.objectHashMap.get(key);
        List<String> strings = new ArrayList<>();
        if (object instanceof List) {
            List<?> list = (List) object;
            for (int i = 0; i < list.size(); i++) {
                String string = (String) list.get(i);
                string = MinecraftColor.format(string);
                strings.add(i, string);
            }
            return strings;
        }

        return null;
    }

    public <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType) {
        Object obj = this.objectHashMap.get(key);
        Map<K, V> map = getKvMap(keyType, valueType, obj);

        for (Map.Entry<K, V> item : map.entrySet()) {
            if (item.getValue() instanceof String) {
                String string = (String) item.getValue();
                item.setValue((V) MinecraftColor.format(string));
            }
            if (item.getValue() instanceof List) {
                List<String> strings = new ArrayList<>();
                List<?> list = (List) item.getValue();
                for (int i = 0; i < list.size(); i++) {
                    String string = (String) list.get(i);
                    string = MinecraftColor.format(string);
                    strings.add(i, string);
                }
                item.setValue((V) strings);
            }
        }
        return map;
    }

    private <K, V> Map<K, V> getKvMap(Class<K> keyType, Class<V> valueType, Object obj) {
        Map<K, V> map = new HashMap<>();
        if (obj instanceof ConfigurationSection) {
            ConfigurationSection section = (ConfigurationSection) obj;
            for (String subKey : section.getKeys(false)) {
                Object value = section.get(subKey);

                K keyCasted = keyType.cast(subKey);

                V valueCasted = valueType.cast(value);
                map.put(keyCasted, valueCasted);
            }
        }
        return map;
    }

    public boolean getBool(String key) {
        boolean bool = ((Boolean) this.objectHashMap.get(key)).booleanValue();
        return bool;
    }

    public int getInt(String key) {
        int intvalue = ((Integer) this.objectHashMap.get(key)).intValue();
        return intvalue;
    }


    public String toString() {
        String string = "";
        for (Map.Entry<String, Object> entry : this.objectHashMap.entrySet()) {
            string = string + string + " " + (String) entry.getKey() + "\n";
        }
        return string;
    }


    public void checkStorage() {
        needle();
        boolean changed = false;
        for (Map.Entry<String, Object> entry : this.objectHashMap.entrySet()) {
            if (this.config.contains(entry.getKey())) {
                Object object = this.config.get(entry.getKey());

                this.objectHashMap.put(entry.getKey(), object);
                continue;
            }

            changed = true;

            this.config.set(entry.getKey(), entry.getValue());
        }


        for (Map.Entry<String, List<String>> entry : this.commentsMap.entrySet()) {
            try {
                Method getCommentsMethod = this.config.getClass().getMethod("getComments", new Class[]{String.class});


                List<String> comments = (List<String>) getCommentsMethod.invoke(this.config, new Object[]{entry.getKey()});


                if (comments.isEmpty()) {
                    Method setCommentsMethod = this.config.getClass().getMethod("setComments", new Class[]{String.class, List.class});


                    setCommentsMethod.invoke(this.config, new Object[]{entry.getKey(), entry.getValue()});
                    changed = true;
                }

            } catch (Exception e) {
                throw new RuntimeException("Ошибка при установке комментария в конфигурацию.", e);
            }
        }

        if (changed) {
            try {
                this.config.save(this.f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getFiledir() {
        return this.filedir;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

}