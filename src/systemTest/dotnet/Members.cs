namespace Soot.SystemTest.Dotnet
{
    /// <summary>
    /// Members: fields, properties, indexers, events, delegates and constants.
    /// </summary>
    public class Members
    {
        public const int Constant = 42;

        public static int StaticField;
        private int instanceField;
        protected string name;

        public Members()
        {
            instanceField = 1;
        }

        public int InstanceField
        {
            get { return instanceField; }
            set { instanceField = value; }
        }

        public static int StaticProperty { get; set; }

        public string Name
        {
            get { return name; }
            set { name = value ?? string.Empty; }
        }

        public int this[int index]
        {
            get { return instanceField + index; }
            set { instanceField = value - index; }
        }

        public event System.EventHandler Changed;

        public void RaiseChanged()
        {
            Changed?.Invoke(this, System.EventArgs.Empty);
        }

        public int Add(int a, int b)
        {
            return a + b;
        }

        public static string Describe(int value)
        {
            return value.ToString();
        }

        public void SetNameViaDelegate(System.Action<string> action)
        {
            action(name);
        }
    }

    public class DerivedMembers : Members
    {
        public int Additional;

        public override string ToString()
        {
            return base.ToString();
        }
    }
}
