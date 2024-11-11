package shadows.apotheosis.adventure.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

public class GhostVertexBuilder implements VertexConsumer {

    private final VertexConsumer wrapped;
    private final int alpha;

    public GhostVertexBuilder(VertexConsumer wrapped, int alpha) {
        this.wrapped = wrapped;
        this.alpha = alpha;
    }

    @Override
    public @NotNull VertexConsumer vertex(double x, double y, double z) {
        return this.wrapped.vertex(x, y, z);
    }

    @Override
    public @NotNull VertexConsumer color(int red, int green, int blue, int alpha) {
        return this.wrapped.color(red, green, blue, alpha * this.alpha / 0xFF);
    }

    @Override
    public @NotNull VertexConsumer uv(float u, float v) {
        return this.wrapped.uv(u, v);
    }

    @Override
    public @NotNull VertexConsumer overlayCoords(int u, int v) {
        return this.wrapped.overlayCoords(u, v);
    }

    @Override
    public @NotNull VertexConsumer uv2(int u, int v) {
        return this.wrapped.uv2(u, v);
    }

    @Override
    public @NotNull VertexConsumer normal(float x, float y, float z) {
        return this.wrapped.normal(x, y, z);
    }

    @Override
    public void endVertex() {
        this.wrapped.endVertex();
    }

    @Override
    public void defaultColor(int pRed, int pGreen, int pBlue, int pAlpha) {

    }

    @Override
    public void unsetDefaultColor() {

    }

    public static class GhostBufferSource implements MultiBufferSource {

        private final MultiBufferSource wrapped;

        public GhostBufferSource(MultiBufferSource wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public @NotNull VertexConsumer getBuffer(RenderType type) {
            return new GhostVertexBuilder(this.wrapped.getBuffer(type), 0x99);
        }

    }
}
