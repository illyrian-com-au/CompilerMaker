package test;

public class IntValue {
    public int intValue = 10;
    
    public void setInt(int value) {
        intValue = value;
    }
    
    public int getInt() {
        return intValue;
    }
    
    public String toString() {
        return Integer.toString(intValue);
    }
}
