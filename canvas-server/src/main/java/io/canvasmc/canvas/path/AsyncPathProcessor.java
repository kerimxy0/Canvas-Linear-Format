package io.canvasmc.canvas.path;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.canvasmc.canvas.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Handles the scheduling of async path processing.
 * Ported from Kaiiju/Petal.
 */
public class AsyncPathProcessor {
    private static ExecutorService pathProcessingExecutor;

    static {
        initializeExecutor();
    }

    private static void initializeExecutor() {
        int maxThreads = Config.INSTANCE != null ? Config.INSTANCE.asyncPathfinding.maxThreads : 0;
        int keepalive = Config.INSTANCE != null ? Config.INSTANCE.asyncPathfinding.keepalive : 60;

        if (maxThreads < 0)
            maxThreads = Math.max(Runtime.getRuntime().availableProcessors() + maxThreads, 1);
        else if (maxThreads == 0)
            maxThreads = Math.max(Runtime.getRuntime().availableProcessors() / 4, 1);

        pathProcessingExecutor = new ThreadPoolExecutor(1, maxThreads, keepalive, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("canvas-path-processor-%d")
                        .setPriority(Thread.NORM_PRIORITY - 2).build());
    }

    protected static CompletableFuture<Void> queue(@NotNull AsyncPath path) {
        return CompletableFuture.runAsync(path::process, pathProcessingExecutor);
    }

    public static void awaitProcessing(Entity entity, @Nullable Path path, Consumer<@Nullable Path> afterProcessing) {
        if (path != null && !path.isProcessed() && path instanceof AsyncPath asyncPath) {
            asyncPath.postProcessing(() -> entity.getBukkitEntity().taskScheduler
                    .schedule(nmsEntity -> afterProcessing.accept(path), null, 1));
        } else {
            afterProcessing.accept(path);
        }
    }

    public static boolean isEnabled() {
        return Config.INSTANCE != null && Config.INSTANCE.asyncPathfinding.enabled;
    }

    public static boolean isPathProcessorThread() {
        return Thread.currentThread().getName().contains("canvas-path-processor");
    }
}
