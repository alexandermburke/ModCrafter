package net.gegy1000.modcrafter.script;

import java.util.ArrayList;
import java.util.List;

import net.gegy1000.modcrafter.ModCrafterAPI;
import net.gegy1000.modcrafter.json.JsonScript;
import net.gegy1000.modcrafter.mod.Mod;
import net.gegy1000.modcrafter.mod.sprite.Sprite;
import net.gegy1000.modcrafter.script.parameter.IParameter;
import net.gegy1000.modcrafter.script.parameter.InputParameter;

public class Script
{
    private ScriptDef def;

    private int parent;
    private int child = -1;

    private Object[] name;

    private Sprite sprite;

    private int x;
    private int y;

    private Mod mod;

    public Script(Sprite sprite, ScriptDef def, Script parent)
    {
        this.sprite = sprite;
        this.def = def;
        this.parent = sprite.getScriptId(parent);
        this.mod = sprite.getMod();
        this.name = def.getName();
    }

    public void execute()
    {
        def.execute(this);

        Script child = getChild();

        if (child != null)
        {
            child.execute();
        }
    }

    public Script(Sprite sprite, Mod mod, JsonScript jsonScript)
    {
        this.def = ModCrafterAPI.getScriptById(jsonScript.defId);
        this.parent = jsonScript.parentId;
        this.child = jsonScript.childId;
        this.sprite = sprite;
        this.name = def.getName();

        if (jsonScript.parameters != null)
        {
            int parameterId = 0;

            for (int i = 0; i < name.length; i++)
            {
                if (name[i] instanceof IParameter)
                {
                    name[i] = new InputParameter(jsonScript.parameters.get(parameterId)); // TODO save type of par? or override with hat script?

                    parameterId++;
                }
            }
        }

        this.setPosition(jsonScript.x, jsonScript.y);
        this.mod = mod;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public void setChild(Script child)
    {
        int oldChildId = this.child;

        this.child = getSprite().getScriptId(child);

        if (oldChildId != -1)
        {
            Script oldChild = getSprite().getScript(oldChildId);

            if (oldChild.getParent() != null && oldChild != child)
            {
                oldChild.setParent(null);
            }
        }

        if (child != null)
        {
            if (getSprite().getScriptId(this) != child.parent)
            {
                child.setParent(this);
            }
        }
    }

    public final Script getChild()
    {
        return child != -1 ? getSprite().getScript(child) : null;
    }

    public Script getParent()
    {
        return parent != -1 ? getSprite().getScript(parent) : null;
    }

    public void setParent(Script parent)
    {
        int oldParentId = this.parent;

        this.parent = getSprite().getScriptId(parent);

        if (oldParentId != -1)
        {
            Script oldParent = getSprite().getScript(oldParentId);

            if (oldParent.getChild() != null && oldParent != parent)
            {
                oldParent.setChild(null);
            }
        }

        if (parent != null)
        {
            if (getSprite().getScriptId(this) != parent.child)
            {
                parent.setChild(this);
            }
        }
    }

    public IParameter getParameter(int index)
    {
        int parIndex = 0;

        for (Object namePart : name)
        {
            if (namePart instanceof IParameter)
            {
                if (parIndex == index)
                {
                    return (IParameter) namePart;
                }

                parIndex++;
            }
        }

        return null;
    }

    public String getDisplayName()
    {
        String displayName = "";

        for (Object namePart : name)
        {
            if (namePart instanceof IParameter)
                displayName += ((IParameter) namePart).getData();
            else
                displayName += namePart;

            displayName += " ";
        }

        return displayName;
    }

    public ScriptDef getScriptDef()
    {
        return def;
    }

    public Sprite getSprite()
    {
        return sprite;
    }

    public Object[] getName()
    {
        return name;
    }
}
