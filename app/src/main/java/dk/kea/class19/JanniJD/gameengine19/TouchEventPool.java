package dk.kea.class19.JanniJD.gameengine19;

import java.util.ArrayList;
import java.util.List;

public class TouchEventPool extends Pool<TouchEvent>
{
@Override
    protected TouchEvent newItem()
{
    return new TouchEvent();
}
}
