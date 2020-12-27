package com.unascribed.blockrenderer.client.varia;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptContext;
import java.util.Collections;
import java.util.List;

public interface Nashorn {

    static NashornScriptEngine getEngine(List<String> filters) {
        final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        final NashornScriptEngine engine = getEngine(factory, filters);
        final ScriptContext context = engine.getContext();

        /* Remove Standard Globals */
        remove(context, "load");
        remove(context, "quit");
        remove(context, "loadWithNewGlobal");
        remove(context, "exit");

        /* Provide Context for Script */
        engine.put("filters", Collections.unmodifiableList(filters));

        return engine;
    }

    static NashornScriptEngine getEngine(NashornScriptEngineFactory factory, List<String> filters) {
        /* Allow Nashorn's few ES6 features. N.B. This is more complete in Java 9 */
        String[] args = new String[]{"--language=es6"};

        /* Get the current ClassLoader */
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) loader = Nashorn.class.getClassLoader();

        /* Filter based on pass in classes */
        ClassFilter classFilter = s -> filters.stream().anyMatch(s::matches);

        return (NashornScriptEngine) factory.getScriptEngine(args, loader, classFilter);
    }

    static void remove(ScriptContext context, String name) {
        context.removeAttribute(name, context.getAttributesScope(name));
    }

}
