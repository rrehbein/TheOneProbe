package mcjty.theoneprobe.rendering;

import mcjty.theoneprobe.apiimpl.ProbeInfo;
import mcjty.theoneprobe.apiimpl.elements.Cursor;
import mcjty.theoneprobe.apiimpl.elements.Element;
import mcjty.theoneprobe.network.PacketGetInfo;
import mcjty.theoneprobe.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class OverlayRenderer {

    private static Map<Pair<Integer,BlockPos>, Pair<Long, ProbeInfo>> cachedInfo = new HashMap<>();
    private static long lastCleanupTime = 0;

    public static void registerProbeInfo(int dim, BlockPos pos, ProbeInfo probeInfo) {
        long time = System.currentTimeMillis();
        cachedInfo.put(Pair.of(dim, pos), Pair.of(time, probeInfo));
    }

    public static void renderHUD() {
        RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (mouseOver == null) {
            return;
        }
        BlockPos blockPos = mouseOver.getBlockPos();
        if (blockPos == null) {
            return;
        }
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player.worldObj.isAirBlock(blockPos)) {
            return;
        }

        long time = System.currentTimeMillis();

        Pair<Long, ProbeInfo> pair = cachedInfo.get(Pair.of(player.worldObj.provider.getDimension(), blockPos));
        if (pair == null) {
            PacketHandler.INSTANCE.sendToServer(new PacketGetInfo(player.worldObj.provider.getDimension(), blockPos));
        } else {
            if (time > pair.getLeft() + 200) {
                // This info is slightly old. Update it
                PacketHandler.INSTANCE.sendToServer(new PacketGetInfo(player.worldObj.provider.getDimension(), blockPos));
            }
            renderElements(pair.getRight());
        }

        if (time > lastCleanupTime + 5000) {
            // It has been a while. Time to clean up unused cached pairs.
            Map<Pair<Integer,BlockPos>, Pair<Long, ProbeInfo>> newCachedInfo = new HashMap<>();
            for (Map.Entry<Pair<Integer, BlockPos>, Pair<Long, ProbeInfo>> entry : cachedInfo.entrySet()) {
                long t = entry.getValue().getLeft();
                if (time < t + 200 * 2) {
                    newCachedInfo.put(entry.getKey(), entry.getValue());
                }
            }
            cachedInfo = newCachedInfo;
            lastCleanupTime = time;
        }
    }

    private static void renderElements(ProbeInfo probeInfo) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();

        Cursor cursor = new Cursor(20, 20, 20, 20);

        for (Element element : probeInfo.getElements()) {
            element.render(cursor);
        }
    }
}