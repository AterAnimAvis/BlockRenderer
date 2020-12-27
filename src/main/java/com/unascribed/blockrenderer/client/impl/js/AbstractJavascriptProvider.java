package com.unascribed.blockrenderer.client.impl.js;

import com.unascribed.blockrenderer.client.varia.Nashorn;
import com.unascribed.blockrenderer.client.varia.logging.Log;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.apache.logging.log4j.Marker;

import javax.annotation.Nullable;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.unascribed.blockrenderer.client.varia.Files.DEFAULT_FOLDER;

public abstract class AbstractJavascriptProvider<T> {

    public static final String SCRIPTS_FOLDER = "scripts";

    private final List<T> PROVIDERS = new ArrayList<>();

    abstract Marker marker();

    abstract String type();

    abstract String fileType();

    public void reload() {
        removeProviders(PROVIDERS);

        PROVIDERS.clear();

        File folder = new File(DEFAULT_FOLDER, SCRIPTS_FOLDER);
        try {
            Files
                    .walk(folder.toPath())
                    .filter(path -> {
                        Log.debug(marker(), "Trying to load {}", path);
                        return true;
                    })
                    .filter(path -> path.toString().endsWith(fileType()))
                    .forEach(path -> {
                        try (FileReader reader = new FileReader(path.toFile())) {
                            T provider = load(reader);

                            Log.debug(marker(), "Loaded {} {}", path, provider);

                            if (provider == null) {
                                Log.error(marker(), "Can't cast to {}: {}", type(), path);
                                return;
                            }

                            PROVIDERS.add(provider);
                        } catch (ScriptException | IOException e) {
                            Log.error(marker(), "Error loading script: " + path, e);
                        }
                    });
        } catch (IOException e) {
            Log.error(marker(), "Error walking script folder", e);
        }

        addProviders(PROVIDERS);
    }

    @Nullable
    private T load(Reader script) throws ScriptException {
        NashornScriptEngine engine = Nashorn.getEngine(ALLOWED_CLASSES);
        engine.eval(PRELOAD);
        engine.eval(script);
        return cast(engine);
    }

    @Nullable
    abstract T cast(NashornScriptEngine engine);

    abstract void removeProviders(List<T> providers);

    abstract void addProviders(List<T> providers);

    private static final String API_PACKAGE = "com.unascribed.blockrenderer.client.api";
    private static final String IMPL_PACKAGE = "com.unascribed.blockrenderer.client.impl";

    private static final String PRELOAD
            = "let API = Java.type('" + IMPL_PACKAGE + ".js.API');"
            + "let Bounds = Java.type('" + API_PACKAGE + ".Bounds');"
            + "let BoundsProvider = Java.type('" + API_PACKAGE + ".BoundsProvider');"
            + "let DefaultState = Java.type('" + API_PACKAGE + ".DefaultState');"
            + "let DefaultStateProvider = Java.type('" + API_PACKAGE + ".DefaultStateProvider');"
            + "let Vector3d = Java.type('" + API_PACKAGE + ".vendor.joml.Vector3d');"
            + "let Vector3dc = Java.type('" + API_PACKAGE + ".vendor.joml.Vector3dc');"
            + "let Identifier = Java.type('net.minecraft.util.ResourceLocation');";

    private static final List<String> ALLOWED_CLASSES = Arrays.asList(
            API_PACKAGE + ".Bounds",
            API_PACKAGE + ".BoundsProvider",
            API_PACKAGE + ".DefaultState",
            API_PACKAGE + ".DefaultStateProvider",
            API_PACKAGE + ".vendor.joml.Vector3d",
            API_PACKAGE + ".vendor.joml.Vector3dc",
            IMPL_PACKAGE + ".js.API",
            "net.minecraft.util.ResourceLocation"

            // NBT
            // TODO:
            // "net.minecraft.nbt."
      /*
        ByteArrayNBT
        ByteNBT
        CollectionNBT
        CompoundNBT
        DoubleNBT
        EndNBT
        FloatNBT
        INBT
        INBTType
        IntArrayNBT
        ListNBT
        LongArrayNBT
        LongNBT
        NBTDynamicOps
        NBTTypes
        NBTUtil
        NumberNBT
        ShortNBT
        StringNBT

        JsonToNBT
       */
    );

}
