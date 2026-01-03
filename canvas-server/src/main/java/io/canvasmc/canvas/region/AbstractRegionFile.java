package io.canvasmc.canvas.region;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract interface for region file implementations.
 * Supports both standard Anvil format and Linear format.
 */
public interface AbstractRegionFile extends ca.spottedleaf.moonrise.patches.chunk_system.storage.ChunkSystemRegionFile {
    void flush() throws IOException;

    void clear(ChunkPos pos) throws IOException;

    void close() throws IOException;

    void setStatus(int x, int z, ChunkStatus status);

    void setOversized(int x, int z, boolean oversized) throws IOException;

    boolean hasChunk(ChunkPos pos);

    boolean doesChunkExist(ChunkPos pos) throws Exception;

    boolean isOversized(int x, int z);

    boolean recalculateHeader() throws IOException;

    DataOutputStream getChunkDataOutputStream(ChunkPos pos) throws IOException;

    DataInputStream getChunkDataInputStream(ChunkPos pos) throws IOException;

    CompoundTag getOversizedData(int x, int z) throws IOException;

    ChunkStatus getStatusIfCached(int x, int z);

    int getRecalculateCount();

    ReentrantLock getFileLock();

    Path getRegionFile();
}
