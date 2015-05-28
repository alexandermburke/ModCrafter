package net.gegy1000.modcrafter.json;

import java.util.ArrayList;
import java.util.List;

import net.gegy1000.modcrafter.mod.Mod;
import net.gegy1000.modcrafter.mod.sprite.Sprite;
import net.gegy1000.modcrafter.script.Script;
import net.gegy1000.modcrafter.script.parameter.IParameter;

public class JsonScript
{
    public int sprite;

    public String defId;

    public int parentId;
    public int childId;

    public int x;
    public int y;

    public List<Object> parameters = new ArrayList<Object>();

    public JsonScript(Script script)
    {
        Sprite sprite = script.getSprite();

        this.defId = script.getScriptDef().getId();
        this.parentId = sprite.getScriptId(script.getParent());
        this.childId = sprite.getScriptId(script.getChild());
        this.x = script.getX();
        this.y = script.getY();

        for (IParameter par : script.getParameters())
        {
            this.parameters.add(par.getData());
        }
    }

    public Script toScript(Sprite sprite)
    {
        return new Script(sprite, sprite.getMod(), this);
    }
}
