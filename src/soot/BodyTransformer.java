package soot;

import java.util.*;

public abstract class BodyTransformer
{
    /** Calls internalTransform with the optionsString properly set up.
     *  That is, the options in optionsString override those in the Scene. */
    public final void transform(Body b, String phaseName, String optionsString)
    {
        Map options = Scene.v().computePhaseOptions(phaseName, optionsString);
        internalTransform(b, options);
    }

    public final void transform(Body b)
    {
        internalTransform(b, new HashMap());
    }

    public final void transform(Body b, String phaseName)
    {
        internalTransform(b, Scene.v().getPhaseOptions(phaseName));
    }

    protected abstract void internalTransform(Body b, Map options);
}
