package io.canvasmc.canvas.path;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Represents a path that may not be processed yet.
 * Ported from Kaiiju/Petal.
 */
public class AsyncPath extends Path {
    private volatile boolean processed = false;
    private final List<Runnable> postProcessing = new ArrayList<>(0);
    private final Set<BlockPos> positions;
    private final Supplier<Path> pathSupplier;
    private final List<Node> nodes;

    public AsyncPath(@NotNull List<Node> emptyNodeList, @NotNull Set<BlockPos> positions,
            @NotNull Supplier<Path> pathSupplier) {
        super(emptyNodeList, BlockPos.ZERO, false); // Use BlockPos.ZERO as target instead of null
        this.nodes = emptyNodeList;
        this.positions = positions;
        this.pathSupplier = pathSupplier;
        AsyncPathProcessor.queue(this);
    }

    @Override
    public boolean isProcessed() {
        return this.processed;
    }

    public synchronized void postProcessing(@NotNull Runnable runnable) {
        if (this.processed)
            runnable.run();
        else
            this.postProcessing.add(runnable);
    }

    public boolean hasSameProcessingPositions(final Set<BlockPos> positions) {
        if (this.positions.size() != positions.size())
            return false;
        return this.positions.containsAll(positions);
    }

    public synchronized void process() {
        if (this.processed)
            return;
        final Path bestPath = this.pathSupplier.get();
        if (bestPath != null) {
            this.nodes.addAll(bestPath.nodes);
            this.target = bestPath.getTarget();
            this.distToTarget = bestPath.getDistToTarget();
            this.reached = bestPath.canReach();
        } else {
            this.reached = false;
        }
        this.processed = true;
        for (Runnable runnable : this.postProcessing)
            runnable.run();
    }

    private void checkProcessed() {
        if (!this.processed)
            this.process();
    }

    @Override
    public @NotNull BlockPos getTarget() {
        checkProcessed();
        return super.getTarget();
    }

    @Override
    public float getDistToTarget() {
        checkProcessed();
        return super.getDistToTarget();
    }

    @Override
    public boolean canReach() {
        checkProcessed();
        return super.canReach();
    }

    @Override
    public boolean isDone() {
        return this.isProcessed() && super.isDone();
    }

    @Override
    public void advance() {
        checkProcessed();
        super.advance();
    }

    @Override
    public boolean notStarted() {
        checkProcessed();
        return super.notStarted();
    }

    @Nullable
    @Override
    public Node getEndNode() {
        checkProcessed();
        return super.getEndNode();
    }

    @Override
    public Node getNode(int index) {
        checkProcessed();
        return super.getNode(index);
    }

    @Override
    public void truncateNodes(int length) {
        checkProcessed();
        super.truncateNodes(length);
    }

    @Override
    public void replaceNode(int index, Node node) {
        checkProcessed();
        super.replaceNode(index, node);
    }

    @Override
    public int getNodeCount() {
        checkProcessed();
        return super.getNodeCount();
    }

    @Override
    public int getNextNodeIndex() {
        checkProcessed();
        return super.getNextNodeIndex();
    }

    @Override
    public void setNextNodeIndex(int nodeIndex) {
        checkProcessed();
        super.setNextNodeIndex(nodeIndex);
    }

    @Override
    public Vec3 getEntityPosAtNode(Entity entity, int index) {
        checkProcessed();
        return super.getEntityPosAtNode(entity, index);
    }

    @Override
    public BlockPos getNodePos(int index) {
        checkProcessed();
        return super.getNodePos(index);
    }

    @Override
    public Vec3 getNextEntityPos(Entity entity) {
        checkProcessed();
        return super.getNextEntityPos(entity);
    }

    @Override
    public BlockPos getNextNodePos() {
        checkProcessed();
        return super.getNextNodePos();
    }

    @Override
    public Node getNextNode() {
        checkProcessed();
        return super.getNextNode();
    }

    @Nullable
    @Override
    public Node getPreviousNode() {
        checkProcessed();
        return super.getPreviousNode();
    }

}
