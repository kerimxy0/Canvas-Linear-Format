package io.canvasmc.canvas.region;

import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.chunk.storage.RegionFileVersion;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Factory for creating AbstractRegionFile instances.
 */
public class AbstractRegionFileFactory {

    public static AbstractRegionFile getAbstractRegionFile(
            net.minecraft.world.level.chunk.storage.RegionStorageInfo info, int linearCompression, Path file,
            Path directory,
            boolean dsync) throws IOException {
        return getAbstractRegionFile(info, linearCompression, file, directory, RegionFileVersion.VERSION_DEFLATE,
                dsync);
    }

    public static AbstractRegionFile getAbstractRegionFile(
            net.minecraft.world.level.chunk.storage.RegionStorageInfo info, int linearCompression, Path file,
            Path directory,
            boolean dsync, boolean canRecalcHeader) throws IOException {
        return getAbstractRegionFile(info, linearCompression, file, directory, RegionFileVersion.VERSION_DEFLATE,
                dsync);
    }

    public static AbstractRegionFile getAbstractRegionFile(
            net.minecraft.world.level.chunk.storage.RegionStorageInfo info, int linearCompression, Path file,
            Path directory,
            RegionFileVersion version, boolean dsync) throws IOException {
        if (file.toString().endsWith(".linear")) {
            return new LinearRegionFile(file, linearCompression);
        } else {
            return new RegionFile(info, file, directory, version, dsync);
        }
    }
}
