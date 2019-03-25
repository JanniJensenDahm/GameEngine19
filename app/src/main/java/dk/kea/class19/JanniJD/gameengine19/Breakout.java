package dk.kea.class19.JanniJD.gameengine19;

public class Breakout extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        return new MainMenuScreen(this);
    }
}
