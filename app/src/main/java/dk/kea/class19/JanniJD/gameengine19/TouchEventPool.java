package dk.kea.class19.JanniJD.gameengine19;

public class TouchEventPool extends Pool<TouchEvent>
{
    @Override
    protected TouchEvent newItem()
    {
        return new TouchEvent();
    }

}
