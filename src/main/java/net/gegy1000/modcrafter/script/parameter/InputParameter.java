package net.gegy1000.modcrafter.script.parameter;

public class InputParameter implements IParameter
{
    private Object data;
    
    public InputParameter(Object data)
    {
        this.data = data;
    }
    
    public void setData(Object data)
    {
        this.data = data;
    }
    
    public Object getData()
    {
        return data;
    }
}
