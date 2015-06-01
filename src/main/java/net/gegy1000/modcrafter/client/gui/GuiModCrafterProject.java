package net.gegy1000.modcrafter.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.gegy1000.modcrafter.ModCrafterAPI;
import net.gegy1000.modcrafter.client.gui.element.Element;
import net.gegy1000.modcrafter.client.gui.element.ElementSidebar;
import net.gegy1000.modcrafter.client.gui.element.ElementSprites;
import net.gegy1000.modcrafter.client.gui.element.ElementTopBar;
import net.gegy1000.modcrafter.color.ColorHelper;
import net.gegy1000.modcrafter.mod.Mod;
import net.gegy1000.modcrafter.mod.ModSaveManager;
import net.gegy1000.modcrafter.mod.sprite.Sprite;
import net.gegy1000.modcrafter.script.Script;
import net.gegy1000.modcrafter.script.ScriptDef;
import net.gegy1000.modcrafter.script.ScriptDefHat;
import net.gegy1000.modcrafter.script.parameter.DataType;
import net.gegy1000.modcrafter.script.parameter.IParameter;
import net.gegy1000.modcrafter.script.parameter.InputParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

public class GuiModCrafterProject extends GuiScreen
{
	public GuiModCrafter modCrafterGui;
	public ArrayList<Element> elements = Lists.newArrayList();
	public int backgroundColor = 0x212121;
	public int elementColor = 0x141414;

    public static final ResourceLocation scriptTextures = new ResourceLocation("modcrafter:textures/gui/script/scripts.png");
    public static final ResourceLocation widgets = new ResourceLocation("modcrafter:textures/gui/widgets.png");
    
    public Mod loadedMod;

    public final int scriptHeight = 11;
    public final int spriteWidth = 21;

    public Script holdingScript;

    public int heldOffsetX, heldOffsetY;

    public Script snapping;
    
    public Sprite selectedSprite;
    
    public ElementSidebar elementScriptSidebar;
    public ElementSprites elementSprites;
    public ElementTopBar elementTopBar;
    
    public TextBox textBox;

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
        
        this.elements.clear();
        this.elements.add(this.elementScriptSidebar = new ElementSidebar(this, 0, 0, elementScriptSidebar == null ? 85 : elementScriptSidebar.width, height));
        this.elements.add(this.elementSprites = new ElementSprites(this, 0, elementSprites == null ? height - 66 : elementSprites.yPosition, elementScriptSidebar.width, elementSprites == null ? height - (height - 66) : elementSprites.height));
        this.elements.add(this.elementTopBar = new ElementTopBar(this, elementScriptSidebar.width, 0, width - elementScriptSidebar.width, 10));
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
    
    public void updateScreen()
    {
    	super.updateScreen();
    	
    	if (textBox != null)
    	{
    		textBox.updateScreen();
    	}
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
    	GL11.glDisable(GL11.GL_TEXTURE_2D);
    	ColorHelper.setColorFromInt(backgroundColor, 1.0F);
        drawTexturedModalRect(0, 0, 0, 0, width, height);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        for (Element element : elements)
        {
        	element.drawScreen(mouseX, mouseY, partialTicks);
        }
        
        if (selectedSprite != null)
        {
            for (Entry<Integer, Script> script : selectedSprite.getScripts().entrySet())
            {
                drawScript(script.getValue());
            }
        }
        
        if (textBox != null)
        {
            textBox.drawScreen(mouseX, mouseY, partialTicks);
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawScript(Script script)
    {
        int x = script.getX();

        float alpha = 1.0F;

        if((script.equals(holdingScript) && snapping != null) || x < 0)
        {
            alpha = 0.8F;
        }

        drawScript(script.getScriptDef(), x + elementScriptSidebar.width, script.getY() + elementTopBar.height, script.getName(), script.getDisplayName(), alpha);
    }

    public void drawScript(ScriptDef def, int xPosition, int yPosition, Object[] name, String displayName, float alpha)
    {
        int width = getScriptDrawWidth(displayName);

        int colour = def.getColor();

        float r = (colour & 0xFF0000) >> 16;
        float g = (colour & 0xFF00) >> 8;
        float b = (colour & 0xFF);

        GL11.glColor4f(r, g, b, alpha);

        mc.renderEngine.bindTexture(scriptTextures);

        if (def instanceof ScriptDefHat)
        {
            drawTexturedModalRect(xPosition, yPosition, 12, 0, 7, 12);

            for (int i = 0; i < 5; i++)
            {
                drawTexturedModalRect(xPosition + 7 + i, yPosition, 19, 0, 1, 12);
            }

            drawTexturedModalRect(xPosition + 12, yPosition, 20, 0, 1, 12);

            for (int i = 5; i < width; i++)
            {
                drawTexturedModalRect(xPosition + 8 + (i), yPosition, 21, 0, 1, 12);
            }

            drawTexturedModalRect(xPosition + 8 + width, yPosition, 22, 0, 1, 12);

            yPosition++;
        }
        else
        {
            drawTexturedModalRect(xPosition, yPosition, 0, 0, 7, 12);

            for (int i = 0; i < width; i++)
            {
                drawTexturedModalRect(xPosition + 7 + (i), yPosition, 7, 0, 1, 12);
            }

            drawTexturedModalRect(xPosition + 7 + width, yPosition, 9, 0, 1, 12);
        }

        int x = xPosition + 2;

        int parameter = 0;
        
        for (Object object : name)
        {
            if(object instanceof InputParameter)
            {
                InputParameter inputParameter = (InputParameter) object;
                
                String string = inputParameter.getData().toString();

                int textWidth = getScaledStringWidth(string + " ", 0.5F);

                if(inputParameter.getDataType() == DataType.TEXT)
                {
                    drawRect(x - 1, yPosition + 2, textWidth + 1, 6, 1F, 1F, 1F, 0.7F * alpha);
                }

                drawScaledString(mc, string, x, yPosition + 3, 0xCCCCCC, 0.5F);

                x += textWidth;
                
                parameter++;
            }
            else
            {
                drawScaledString(mc, object.toString(), x, yPosition + 3, 0xFFFFFF, 0.5F);

                x += getScaledStringWidth(object.toString() + " ", 0.5F);
            }
        }
    }

    private int getScriptDrawWidth(String displayName)
    {
        return (int) ((float) fontRendererObj.getStringWidth(displayName) * 0.5F) - 6;
    }

    private int getScriptWidth(String displayName)
    {
        return (int) ((float) fontRendererObj.getStringWidth(displayName) * 0.5F);
    }

    public int getScaledStringWidth(String displayName, float scale)
    {
        return (int) ((float) fontRendererObj.getStringWidth(displayName) * (float) scale);
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
    	for (Element element : elements)
        {
    		element.mouseClickMove(mouseX, mouseY, lastButtonClicked, timeSinceMouseClick);
        }
    	
        if (holdingScript != null && textBox == null)
        {
            dragScripts(mouseX, mouseY);
        }
    }

    private void dragScripts(int mouseX, int mouseY)
    {
        int x = mouseX + heldOffsetX;
        int y = mouseY + heldOffsetY;

        snapping = null;

        for (Entry<Integer, Script> entry : selectedSprite.getScripts().entrySet())
        {
            Script script = entry.getValue();

            if (script != holdingScript && holdingScript.getScriptDef().canAttachTo(script) && (script.getChild() == null || script.getChild() == holdingScript))
            {
                int yDiff = Math.abs(y - (script.getY() + scriptHeight));

                if (yDiff <= 4)
                {
                    int sWidth = getScriptWidth(script.getDisplayName());
                    
                    if (x > script.getX() - 4 && x + sWidth < script.getX() + sWidth + 4)
                    {
                        x = script.getX();
                        y = script.getY() + scriptHeight - 1;

                        snapping = script;

                        break;
                    }
                }
            }
        }

        if(y < 0)
            y = 0;
        
        moveChild(holdingScript, x, y);

        holdingScript.setPosition(x, y);
    }

    private void moveChild(Script script, int x, int y)
    {
        if (script.getChild() != null)
        {
            y += scriptHeight - 1;

            script.getChild().setPosition(x, y);

            moveChild(script.getChild(), x, y);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int button)
    {
        super.mouseClicked(mouseX, mouseY, button);
        
        for (Element element : elements)
        {
        	element.mouseClicked(mouseX, mouseY, button);
        }
        
        if (textBox != null)
    	{
        	textBox.mouseClicked(mouseX, mouseY, button);
    	}

        if (holdingScript == null)
        {
            if (selectedSprite != null)
            {
                for (Entry<Integer, Script> entry : selectedSprite.getScripts().entrySet())
                {
                    Script script = entry.getValue();

                    int width = getScriptWidth(script.getDisplayName());

                    int x = script.getX() + elementScriptSidebar.width;
                    int y = script.getY() + elementTopBar.height;

                    if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + scriptHeight)
                    {
                        holdingScript = script;

                        heldOffsetX = x - elementScriptSidebar.width - mouseX;
                        heldOffsetY = y - elementTopBar.height - mouseY;

                        snapping = null;

                        break;
                    }
                }

                if(holdingScript == null)
                {
                    int y = 12;

                    for (Entry<String, ScriptDef> entry : ModCrafterAPI.getScriptDefs().entrySet())
                    {
                        ScriptDef def = entry.getValue();

                        int width = getScriptWidth(def.getDefualtDisplayName());

                        if (mouseX >= 2 && mouseX <= width && mouseY >= y && mouseY <= y + scriptHeight)
                        {
                            holdingScript = new Script(selectedSprite, def, null);

                            heldOffsetX = 2 - mouseX - elementScriptSidebar.width;
                            heldOffsetY = y - mouseY - elementTopBar.height;

                            holdingScript.setPosition(mouseX + heldOffsetX, mouseY + heldOffsetY);

                            selectedSprite.addScript(holdingScript); //TODO add script inside constructor

                            snapping = null;

                            break;
                        }

                        y += scriptHeight + 2;
                    }
                }
                
                if (holdingScript != null)
                {
                    dragScripts(mouseX, mouseY);
                    Script script = holdingScript;
                    int xPos = script.getX() + elementScriptSidebar.width;
                    int yPos = script.getY() + elementTopBar.height;
                    int x = xPos + 2;
                    
                    int par = 0;
                    
                    for (Object object : script.getName())
                    {
						if (object instanceof InputParameter)
                        {
                            InputParameter inputParameter = (InputParameter) script.getParameter(par);
                            String string = inputParameter.getData().toString();
                            int textWidth = getScaledStringWidth(string + " ", 0.5F);
                            
                            if (inputParameter.getDataType() == DataType.TEXT)
                            {
                            	if (xPos >= elementScriptSidebar.width && mouseX > x - 1 && mouseX <= x - 1 + textWidth + 1)
                                {
                                	if (mouseY > yPos && mouseY <= yPos + scriptHeight && textBox == null)
                                	{
                                		textBox = new TextBox(this, x - 2, yPos - 17, textWidth * 2 + 4, 12, inputParameter);
                                		textBox.text = string;
                                	}
                                }
                            }
                            
                            par++;
                            
                            x += textWidth;
                        }
                        else
                        {
                            x += getScaledStringWidth(object.toString() + " ", 0.5F);
                        }
                    }
                }
            }
        }
    }
    
    protected void keyTyped(char c, int key)
    {
    	super.keyTyped(c, key);
    	
    	if (textBox != null)
    	{
    		textBox.keyTyped(c, key);
    	}
    }

    private void drawRect(int x, int y, int sizeX, int sizeY, float r, float g, float b, float a)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(r, g, b, a);
        drawTexturedModalRect(x, y, 0, 0, sizeX, sizeY);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    protected void mouseMovedOrUp(int mouseX, int mouseY, int event)
    {
        super.mouseMovedOrUp(mouseX, mouseY, event);
        
        for (Element element : elements)
        {
        	element.mouseMovedOrUp(mouseX, mouseY, event);
        }

        if (holdingScript != null)
        {
            holdingScript.setParent(snapping);

            if(holdingScript.getX() < 0)
            {
                selectedSprite.removeScript(holdingScript);
            }
        }

        holdingScript = null;
        heldOffsetX = 0;
        heldOffsetY = 0;
    }
}
