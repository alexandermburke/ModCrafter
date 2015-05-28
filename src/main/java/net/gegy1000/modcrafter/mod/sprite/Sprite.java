package net.gegy1000.modcrafter.mod.sprite;

import java.util.List;

import net.gegy1000.modcrafter.ModCrafterAPI;
import net.gegy1000.modcrafter.json.JsonScript;
import net.gegy1000.modcrafter.json.JsonSprite;
import net.gegy1000.modcrafter.mod.Mod;
import net.gegy1000.modcrafter.script.Script;
import net.gegy1000.modcrafter.script.ScriptDef;
import net.gegy1000.modcrafter.script.ScriptDefHat;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.Lists;

public class Sprite
{
    private String name;
    private final Mod mod;

    private List<Script> scripts = Lists.newArrayList();
    private List<Script> hatScripts = Lists.newArrayList();

    private SpriteDef def;

    public Sprite(SpriteDef def, Mod mod, String name)
    {
        this.mod = mod;
        this.name = name;
        this.def = def;
    }

    public Sprite(Mod mod, JsonSprite jsonSprite)
    {
        this.mod = mod;

        this.def = ModCrafterAPI.getSpriteById(jsonSprite.type);
        this.name = jsonSprite.name;

        for (JsonScript script : jsonSprite.scripts)
        {
            Script newScript = script.toScript(this);

            scripts.add(newScript);

            if (newScript.getScriptDef() instanceof ScriptDefHat)
            {
                hatScripts.add(newScript);
            }
        }
    }

    public SpriteDef getSpriteDef()
    {
        return def;
    }

    public List<Script> getScripts()
    {
        return scripts;
    }

    public List<Script> getHatScripts()
    {
        return hatScripts;
    }

    public void addScript(Script script)
    {
        if (script != null)
        {
            if (script.getScriptDef() instanceof ScriptDefHat)
            {
                this.hatScripts.add(script);
            }

            this.scripts.add(script);
        }
    }

    public void removeScript(int id)
    {
        scripts.remove(id);
    }

    public Script getScript(int id)
    {
        return id >= 0 ? scripts.get(id) : null;
    }

    public int getScriptId(Script script)
    {
        return scripts.indexOf(script);
    }

    public Mod getMod()
    {
        return mod;
    }

    public String getName()
    {
        if (name == null)
        {
            name = RandomStringUtils.randomAscii(10);
        }

        return name;
    }

    public void rename(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Sprite)
        {
            return ((Sprite) obj).getName().equals(getName());
        }

        return false;
    }
}
