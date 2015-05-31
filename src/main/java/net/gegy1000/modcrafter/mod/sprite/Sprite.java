package net.gegy1000.modcrafter.mod.sprite;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.gegy1000.modcrafter.ModCrafterAPI;
import net.gegy1000.modcrafter.json.JsonScript;
import net.gegy1000.modcrafter.json.JsonSprite;
import net.gegy1000.modcrafter.mod.Mod;
import net.gegy1000.modcrafter.script.Script;
import net.gegy1000.modcrafter.script.ScriptDefHat;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Sprite
{
    private String name;
    private final Mod mod;

    private Map<Integer, Script> scripts = Maps.newHashMap();
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

        for (Entry<Integer, JsonScript> entry : jsonSprite.scripts.entrySet())
        {
            JsonScript script = entry.getValue();
            
            Script newScript = script.toScript(this);

            scripts.put(entry.getKey(), newScript);

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

    public Map<Integer, Script> getScripts()
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
            
            Integer id = 0;
            
            while(this.scripts.containsKey(id))
            {
                id++;
            }
            
            this.scripts.put(id, script);
        }
    }

    public void removeScript(Script script)
    {
        if(script != null)
        {
            scripts.remove(getScriptId(script));
            
            removeScript(script.getChild());
        }
    }

    public Script getScript(int id)
    {
        return id >= 0 ? scripts.get(id) : null;
    }

    public int getScriptId(Script script)
    {
        for (Entry<Integer, Script> entry : scripts.entrySet())
        {
            if(entry.getValue().equals(script))
            {
                return entry.getKey();
            }
        }
        
        return -1;
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
