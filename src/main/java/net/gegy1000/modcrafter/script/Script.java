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

    private List<IParameter> parameters;
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

        loadParameters(def);
    }

    public void execute()
    {
        def.execute(this);

        Script child = getChild();

        if(child != null)
        {
            child.execute();
        }
    }

    private void loadParameters(ScriptDef def)
    {
        this.name = def.getName();

        parameters = new ArrayList<IParameter>();

        for (Object arg : name)
        {
            if (arg instanceof IParameter)
            {
                parameters.add((IParameter) arg);
            }
        }
    }

    public Script(Sprite sprite, Mod mod, JsonScript jsonScript)
    {
        this.def = ModCrafterAPI.getScriptById(jsonScript.defId);
        this.parent = jsonScript.parentId;
        this.child = jsonScript.childId;
        this.sprite = sprite;
        this.name = def.getName();
        this.parameters = new ArrayList<IParameter>();
        
        if(jsonScript.parameters != null)
        {
            for (Object par : jsonScript.parameters)
            {
                parameters.add(new InputParameter(par)); //TODO save type of par? or override with hat script?
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

        if(child != null)
        {
            if(getSprite().getScriptId(this) != child.parent)
            {
                child.setParent(this);
            }
        }
        
        if(oldChildId != -1)
        {
            Script oldChild = getSprite().getScript(oldChildId);

            if(oldChild.getParent() != null)
            {
                oldChild.setParent(null);
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

        if(parent != null)
        {
            if(getSprite().getScriptId(this) != parent.child)
            {
                parent.setChild(this);
            }
        }
        
        if(oldParentId != -1)
        {
            Script oldParent = getSprite().getScript(oldParentId);

            if(oldParent.getChild() != null)
            {
                oldParent.setChild(null);
            }
        }
    }

    public IParameter getParameter(int index)
    {
        return parameters.get(index);
    }

    public String getDisplayName()
    {
        // TODO cache display name? remove this?

        String displayName = "";

        if(parameters.size() > 0)
        {
            int parIndex = 0;

            for (Object namePart : name)
            {
                if (namePart instanceof IParameter)
                {
                    displayName += getParameter(parIndex).getData();
                    parIndex++;
                }
                else
                {
                    displayName += namePart;
                }

                displayName += " ";
            }
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

    public List<IParameter> getParameters()
    {
        return parameters;
    }

    public Object[] getName()
    {
        return name;
    }
}
