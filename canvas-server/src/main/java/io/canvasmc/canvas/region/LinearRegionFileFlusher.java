package io.canvasmc.canvas.region;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.canvasmc.canvas.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * Handles asynchronous flushing of Linear region files.
 */
public class LinearRegionFileFlusher {

    private static final Logger LOGGER = LoggerFactory.getLogger("Canvas-Linear");
    private final Queue<LinearRegionFile> savingQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduler;
    private final ExecutorService executor;

    public LinearRegionFileFlusher() {
        int flushThreads = Config.INSTANCE != null ? Config.INSTANCE.linearFormat.flushMaxThreads : 1;
        int flushFrequency = Config.INSTANCE != null ? Config.INSTANCE.linearFormat.flushFrequency : 10;

        if (flushThreads < 0)
            flushThreads = Math.max(Runtime.getRuntime().availableProcessors() + flushThreads, 1);
        else
            flushThreads = Math.max(flushThreads, 1);

        final int finalFlushThreads = flushThreads;

        this.scheduler = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder().setNameFormat("canvas-linear-flush-scheduler").build());
        this.executor = Executors.newFixedThreadPool(finalFlushThreads,
                new ThreadFactoryBuilder().setNameFormat("canvas-linear-flusher-%d").build());

        LOGGER.info("Using {} threads for linear region flushing", finalFlushThreads);
        scheduler.scheduleAtFixedRate(this::pollAndFlush, 0L, flushFrequency, TimeUnit.SECONDS);
    }

    public void scheduleSave(LinearRegionFile regionFile) {
        if (savingQueue.contains(regionFile))
            return;
        savingQueue.add(regionFile);
    }

    private void pollAndFlush() {
        while (!savingQueue.isEmpty()) {
            LinearRegionFile regionFile = savingQueue.poll();
            if (regionFile != null && !regionFile.closed && regionFile.isMarkedToSave()) {
                executor.execute(regionFile::flushWrapper);
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
        scheduler.shutdown();
    }
}
