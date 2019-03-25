package dk.kea.class19.JanniJD.gameengine19;

public class TestGame extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        return new TestScreen(this);
    }

}
