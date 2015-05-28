package net.gegy1000.modcrafter.client.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.gegy1000.modcrafter.ModCrafter;
import net.gegy1000.modcrafter.ModCrafterAPI;
import net.gegy1000.modcrafter.json.JsonMod;
import net.gegy1000.modcrafter.mod.Mod;
import net.gegy1000.modcrafter.mod.ModSaveManager;
import net.gegy1000.modcrafter.mod.sprite.Sprite;
import net.gegy1000.modcrafter.script.Script;
import net.gegy1000.modcrafter.script.ScriptDefPrintConsole;
import net.gegy1000.modcrafter.script.parameter.IParameter;
import net.gegy1000.modcrafter.script.parameter.InputParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GuiModCrafterProject extends GuiScreen
{
    private GuiModCrafter modCrafterGui;

    private static final ResourceLocation background = new ResourceLocation("modcrafter:textures/gui/background.png");
    private static final ResourceLocation scriptTextures = new ResourceLocation("modcrafter:textures/gui/script/scripts.png");
    private static final ResourceLocation widgets = new ResourceLocation("modcrafter:textures/gui/widgets.png");
    
    private Mod loadedMod;

    private final int scriptHeight = 11;

    private Script holdingScript;

    private int heldOffsetX, heldOffsetY;

    private Script snapping;

    private Sprite selectedSprite;
    
    public GuiModCrafterProject(GuiModCrafter modCrafterGui, Mod loadedMod)
    {
        this.modCrafterGui = modCrafterGui;
        this.loadedMod = loadedMod;
    }

    public void initGui()
    {
        int i = this.height / 4 + 48;

        selectedSprite = loadedMod.getSprite(0);
        
        heldOffsetX = 0;
        heldOffsetY = 0;

        this.buttonList.add(new GuiModCrafterButton(0, this.width - 80, this.height - 10 - 20, 72, 20, I18n.format("gui.done", new Object[0])));
    }

    public void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            try
            {
                ModSaveManager.saveMod(loadedMod);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // TODO ask whether to save
            this.mc.displayGuiScreen(modCrafterGui);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "ModCrafter - " + loadedMod.getName(), this.width / 2, 5, 0xFFFFFFFF);

        if (selectedSprite != null) // TODO selected sprite selection
        {
            for (Script script : selectedSprite.getScripts())
            {
                drawScript(script);
            }
        }
        
        int x = 0;
        int y = 0;
        
        int spriteWidth = 26;
        
        for (Sprite sprite : loadedMod.getSprites())
        {
            mc.getTextureManager().bindTexture(widgets);
            
            int drawY = height - (spriteWidth * 4) + y;
            
            drawTexturedModalRect(x, drawY, 0, 0, spriteWidth - 1, spriteWidth - 1);
            
            String name = sprite.getName();
            
            if(name.length() > 8)
            {
                name = name.substring(0, 7) + "..";
            }
            
            drawScaledString(mc, name, x + 1, drawY + 15, 0xFFFFFF, 0.5F);
            
            x += spriteWidth;
            
            if(x > spriteWidth * 3)
            {
                x = 0;
                y += spriteWidth;
            }
        }
        
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    /**
     * Draws the background (i is always 0 as of 1.2.2)
     */
    public void drawBackground(int p_146278_1_)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator tessellator = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(background);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        tessellator.startDrawingQuads();
        // tessellator.setColorOpaque_I(4210752);
        tessellator.addVertexWithUV(0.0D, (double) this.height, 0.0D, 0.0D, (double) ((float) this.height / f + (float) p_146278_1_));
        tessellator.addVertexWithUV((double) this.width, (double) this.height, 0.0D, (double) ((float) this.width / f), (double) ((float) this.height / f + (float) p_146278_1_));
        tessellator.addVertexWithUV((double) this.width, 0.0D, 0.0D, (double) ((float) this.width / f), (double) p_146278_1_);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, (double) p_146278_1_);
        tessellator.draw();
    }

    public void drawScript(Script script)
    {
        int xPosition = script.getX();
        int yPosition = script.getY();

        String displayName = script.getDisplayName();
        int width = getWidth(displayName);

        int colour = script.getScriptDef().getColor();

        float r = (colour & 0xFF0000) >> 16;
        float g = (colour & 0xFF00) >> 8;
        float b = (colour & 0xFF);

        GL11.glColor4f(r, g, b, 1.0F);

        mc.renderEngine.bindTexture(scriptTextures);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        drawTexturedModalRect(xPosition, yPosition, 0, 0, 7, 11);

        for (int i = 0; i < width; i++)
        {
            drawTexturedModalRect(xPosition + 7 + (i), yPosition, 7, 0, 1, 11);
        }

        drawTexturedModalRect(xPosition + 7 + width, yPosition, 9, 0, 1, 11);

        drawScaledString(mc, displayName, xPosition + 2, yPosition + 3, 0xFFFFFF, 0.5F);
    }

    private int getWidth(String displayName)
    {
        return displayName.length() * 3 - 17;
    }

    private void drawScaledString(Minecraft mc, String text, float x, float y, int color, float scale)
    {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(x / scale, y / scale, 0);
        drawString(mc.fontRenderer, text, 0, 0, color);
        GL11.glPopMatrix();
    }

    protected void mouseClickMove(int mouseX, int mouseY, int lastButtonClicked, long timeSinceMouseClick)
    {
        if (holdingScript != null)
        {
            int x = mouseX + heldOffsetX;
            int y = mouseY + heldOffsetY;

            snapping = null;

            for (Script script : selectedSprite.getScripts())
            {
                if(script != holdingScript)
                {
                    if (Math.abs(y - (script.getY() + scriptHeight)) <= 4)
                    {
                        int sWidth = getWidth(script.getDisplayName());
                        
                        if(x > script.getX() - 4 && x + sWidth < script.getX() + sWidth + 4)
                        {
                            x = script.getX();
                            y = script.getY() + scriptHeight - 1;
                            snapping = script;
                            break;
                        }
                    }
                }
            }

            moveChild(holdingScript, x, y);

            holdingScript.setPosition(x, y);
        }
    }

    private void moveChild(Script script, int x, int y)
    {
        if(script.getChild() != null)
        {
            y += scriptHeight - 1;

            script.getChild().setPosition(x, y );

            moveChild(script.getChild(), x, y);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);

        if (holdingScript == null)
        {
            if (selectedSprite != null)
            {
                for (Script script : selectedSprite.getScripts())
                {
                    int width = getWidth(script.getDisplayName());

                    int x = script.getX();
                    int y = script.getY();

                    if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + scriptHeight)
                    {
                        holdingScript = script;
                        
                        heldOffsetX = x - mouseX;
                        heldOffsetY = y - mouseY;

                        snapping = null;

                        break;
                    }
                }
            }
        }
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int event)
    {
        super.mouseMovedOrUp(mouseX, mouseY, event);

        if(holdingScript != null)
        {
            holdingScript.setParent(snapping);
        }

        holdingScript = null;
        heldOffsetX = 0;
        heldOffsetY = 0;
    }
}
