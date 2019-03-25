package dk.kea.class19.JanniJD.gameengine19;

public interface TouchHandler
{
    public boolean isTouchDown(int pointers);
    public int getTouchX(int pointer);
    public int getTouchY(int pointer);

}
