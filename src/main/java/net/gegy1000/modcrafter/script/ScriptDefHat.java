package net.gegy1000.modcrafter.script;

import net.gegy1000.modcrafter.json.JsonScript;
import net.gegy1000.modcrafter.mod.Mod;
import net.gegy1000.modcrafter.mod.sprite.Sprite;

public abstract class ScriptDefHat extends ScriptDef
{
    @Override
    public void execute(Script script)
    {
    }
    
    @Override
    public boolean canAttachTo(Script script)
    {
        return false;
    }
}
