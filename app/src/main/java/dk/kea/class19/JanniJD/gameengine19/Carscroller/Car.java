package dk.kea.class19.JanniJD.gameengine19.Carscroller;

public class Car
{
    public static final int WIDTH = 64;
    public  static final int HEIGHT = 32;
    public int x = 0;
    public  int y = 0;

    public Car()
    {
        x = 15;
        y = 160 - HEIGHT / 2;
    }
}
