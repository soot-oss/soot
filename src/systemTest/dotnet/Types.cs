namespace Soot.SystemTest.Dotnet
{
    public interface IShape
    {
        double Area();

        string Name { get; }
    }

    public abstract class Shape : IShape
    {
        public abstract double Area();

        public abstract string Name { get; }

        public override string ToString()
        {
            return Name + ":" + Area();
        }
    }

    public class Square : Shape
    {
        private readonly double side;

        public Square(double side)
        {
            this.side = side;
        }

        public override double Area()
        {
            return side * side;
        }

        public override string Name
        {
            get { return "square"; }
        }
    }

    public class Circle : Shape
    {
        private readonly double radius;

        public Circle(double radius)
        {
            this.radius = radius;
        }

        public override double Area()
        {
            return 3.14159 * radius * radius;
        }

        public override string Name
        {
            get { return "circle"; }
        }
    }

    public struct Point
    {
        public int X;
        public int Y;

        public Point(int x, int y)
        {
            X = x;
            Y = y;
        }

        public int Manhattan()
        {
            return (X < 0 ? -X : X) + (Y < 0 ? -Y : Y);
        }
    }

    public enum Color
    {
        Red = 1,
        Green = 2,
        Blue = 4
    }

    public class Container<T>
    {
        private T value;

        public Container(T value)
        {
            this.value = value;
        }

        public T Value
        {
            get { return value; }
        }

        public TResult Map<TResult>(System.Func<T, TResult> mapper)
        {
            return mapper(value);
        }

        public class Nested
        {
            public int Depth()
            {
                return 1;
            }
        }
    }
}
