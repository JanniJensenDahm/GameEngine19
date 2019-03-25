package dk.kea.class19.JanniJD.gameengine19;

public class TouchEvent
{
    public enum TouchEventType
    {
        Down,
        Up,
        Dragged

    }

    public TouchEventType type;  //The type of event
    public int x;                //The x-coordinate of the event
    public int y;                //The y-coordinate of the event
    public int pointer;          //The pointer id (from the android system)
}
