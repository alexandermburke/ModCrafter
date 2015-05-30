package net.gegy1000.modcrafter.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.gegy1000.modcrafter.mod.Mod;
import net.gegy1000.modcrafter.mod.sprite.Sprite;
import net.gegy1000.modcrafter.script.Script;

public class JsonSprite
{
    public String name;

    public Map<Integer, JsonScript> scripts;

    public String type;

    public JsonSprite(Sprite sprite)
    {
        this.name = sprite.getName();

        this.scripts = new HashMap<Integer, JsonScript>();

        this.type = sprite.getSpriteDef().getId();

        for (Entry<Integer, Script> script : sprite.getScripts().entrySet())
        {
            scripts.put(script.getKey(), new JsonScript(script.getValue()));
        }
    }

    public Sprite toSprite(Mod mod)
    {
        return new Sprite(mod, this);
    }
}
