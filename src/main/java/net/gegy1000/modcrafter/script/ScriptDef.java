package net.gegy1000.modcrafter.script;

import net.gegy1000.modcrafter.mod.sprite.Sprite;

public abstract class ScriptDef
{
    public abstract void execute(Script script);

    public abstract String getId();

    public abstract Object[] getName();

    public abstract int getColor();

    public abstract boolean isAllowedFor(Sprite sprite);
}
