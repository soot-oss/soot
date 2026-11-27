/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Thomas Schmeiduch
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

using System;

namespace Soot.Dotnet.TestSnippets.Types
{
    /// <summary>
    ///     structs
    /// </summary>
    public class Struc
    {
        public static void DoSth(string str)
        {
            S s = default;
            var s2 = new S();
            var s3 = new S(5);
            DoSthStruct(ref s, s2);
            S s4;
            s4.Test = 2;
        }

        public static void DoSthStruct(ref S s, S s2)
        {
            Console.WriteLine(s);
            Console.WriteLine(s2);
        }

        private static void DoSth2()
        {
            Console.WriteLine("Hello World!");
            MyCoordinates s = default;
            s.X = 2;
        }

        public struct S
        {
            public S(int i)
            {
                Test = 0;
            }

            public int Test;

            public int Ten()
            {
                return 10;
            }
        }

        public struct MyCoordinates
        {
            public double X { get; set; }
            public double Y { get; set; }
        }
    }
}