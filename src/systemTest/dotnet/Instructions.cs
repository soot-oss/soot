namespace Soot.SystemTest.Dotnet
{
    /// <summary>
    /// CIL instructions that need translation: boxing, casts, arrays, null checks, literals and call variants.
    /// </summary>
    public class Instructions
    {
        public static object Box(int value)
        {
            return value;
        }

        public static int Unbox(object value)
        {
            return (int)value;
        }

        public static string Cast(object value)
        {
            return (string)value;
        }

        public static bool IsString(object value)
        {
            return value is string;
        }

        public static string AsString(object value)
        {
            return value as string;
        }

        public static object Null()
        {
            return null;
        }

        public static string Literal()
        {
            return "soot";
        }

        public static int[] NewArray(int size)
        {
            return new int[size];
        }

        public static int ArrayLength(int[] values)
        {
            return values.Length;
        }

        public static int ArrayElement(int[] values, int index)
        {
            return values[index];
        }

        public static void StoreArrayElement(long[] values, int index, long value)
        {
            values[index] = value;
        }

        public static int Sum(params int[] values)
        {
            int sum = 0;
            foreach (int value in values)
            {
                sum += value;
            }

            return sum;
        }

        public static string Concat(string left, string right)
        {
            return left + right;
        }

        public static long LongArithmetic(long a, long b)
        {
            return a * b - a / b + (a % b);
        }

        public static double DoubleArithmetic(double a, int b)
        {
            return a / b;
        }

        public static int Compare(int a, int b)
        {
            return a < b ? -1 : (a > b ? 1 : 0);
        }

        public static bool Logic(bool a, bool b)
        {
            return a && !b;
        }

        public static int Ternary(int value)
        {
            return value == 0 ? 1 : 2;
        }
    }
}
