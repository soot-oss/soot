namespace Soot.SystemTest.Dotnet.Referenced
{
    /// <summary>
    /// Lives in a separate assembly to exercise cross-assembly type references of the .NET frontend.
    /// </summary>
    public class Registry
    {
        public Members CreateMembers()
        {
            return new Members();
        }

        public string DescribeShape(Square square)
        {
            return square.Name + ":" + square.Area();
        }

        public int UseConstant()
        {
            return Members.Constant;
        }

        public class Entry
        {
            public int Id;

            public int Next()
            {
                return Id + 1;
            }
        }
    }
}
