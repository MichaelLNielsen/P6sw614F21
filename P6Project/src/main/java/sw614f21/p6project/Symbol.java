package sw614f21.p6project;

public abstract class Symbol {
    public EventType SymbolID;
    public String EventID;
    
    public void SetSymbolID(EventType symbol){
        SymbolID = symbol;
        EventID = symbol.toString();
    }
}
