package dk.kea.class19.JanniJD.gameengine19;

/* What a basic screen should be able to do
 basic behavior from a screen.*/
public abstract class Screen
{
    protected final GameEngine gameEngine;

    public Screen(GameEngine gameEngine)
    {
        this.gameEngine = gameEngine;
    }

    public abstract void update(float deltaTime);
    public abstract void pause();
    public abstract void resume();
    public abstract void dispose();
}
