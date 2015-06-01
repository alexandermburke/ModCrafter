package net.gegy1000.modcrafter.color;

import org.lwjgl.opengl.GL11;

public class ColorHelper
{
    /**
     * @deprecated Use LLib ColorHelper
     */
    @Deprecated
    public static void setColorFromInt(int color, float alpha)
    {
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, alpha);
    }
}