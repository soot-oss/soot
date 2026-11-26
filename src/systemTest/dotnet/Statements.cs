namespace Soot.SystemTest.Dotnet
{
    /// <summary>
    /// Branching and exception handling constructs. Each method maps to a distinct set of Jimple statements.
    /// </summary>
    public class Statements
    {
        public static int SideEffect;

        public static int Branch(int x)
        {
            if (x > 0)
            {
                return 1;
            }

            return -1;
        }

        public static int Switch(int x)
        {
            switch (x)
            {
                case 1:
                    return 10;
                case 2:
                    return 20;
                default:
                    return 0;
            }
        }

        public static int Loop(int n)
        {
            int sum = 0;
            while (n > 0)
            {
                sum += n;
                n--;
            }

            return sum;
        }

        public static int DoWhile(int n)
        {
            int sum = 0;
            do
            {
                sum += n;
                n--;
            } while (n > 0);

            return sum;
        }

        public static int TryCatchFinally(int x)
        {
            try
            {
                return 100 / x;
            }
            catch (System.DivideByZeroException)
            {
                return 0;
            }
            finally
            {
                SideEffect++;
            }
        }

        public static int TryCatchMultiple(int x)
        {
            try
            {
                return int.Parse(x.ToString());
            }
            catch (System.FormatException)
            {
                return 1;
            }
            catch (System.OverflowException)
            {
                return 2;
            }
        }

        public static void Throw()
        {
            throw new System.InvalidOperationException("boom");
        }
    }
}
