namespace Soot.SystemTest.Dotnet
{
    /// <summary>
    /// ArrayInitializer: a static array whose element data the C# compiler stores in the PE file (<c>&lt;PrivateImplementationDetails&gt;</c>),
    /// and a method reading from it.
    /// </summary>
    public class ArrayInitializer
    {
        public static int[] Numbers = { 1, 2, 3, 4 };

        public static int Third()
        {
            return Numbers[2];
        }
    }
}
