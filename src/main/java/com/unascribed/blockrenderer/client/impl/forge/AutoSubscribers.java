package com.unascribed.blockrenderer.client.impl.forge;

import com.unascribed.blockrenderer.client.api.forge.AutoSubscriber;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.unascribed.blockrenderer.Reference.MOD_ID;

@EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class AutoSubscribers {

    private static final Type AUTO_SUBSCRIBER = Type.getType(AutoSubscriber.class);

    @SubscribeEvent
    public static void onModLoadingComplete(FMLLoadCompleteEvent event) {
        subscribers = getAutoSubscriberAnnotations()
                .map(asName)
                .map(forNameOrNull)
                .filter(Objects::nonNull)
                .distinct()
                .map(instanceOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static Stream<AnnotationData> getAutoSubscriberAnnotations() {
        return ModList
                .get()
                .getAllScanData()
                .stream()
                .flatMap(data -> data.getAnnotations().stream())
                .filter(isAutoSubscriber);
    }

    private static final Predicate<AnnotationData> isAutoSubscriber = annotation -> AUTO_SUBSCRIBER.equals(annotation.getAnnotationType());
    private static final Function<AnnotationData, String> asName = annotation -> annotation.getClassType().getClassName();

    private static final Function<String, Class<?>> forNameOrNull = clazzName -> {
        try {
            return Class.forName(clazzName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    };

    private static final Function<Class<?>, Object> instanceOrNull = clazz -> {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    };

    @Nullable
    public static List<Object> subscribers = null;

}
