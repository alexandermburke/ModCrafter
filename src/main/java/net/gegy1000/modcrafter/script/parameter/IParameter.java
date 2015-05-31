package net.gegy1000.modcrafter.script.parameter;

public interface IParameter
{
    void setData(Object data);

    Object getData();
    
    DataType getDataType();
}
