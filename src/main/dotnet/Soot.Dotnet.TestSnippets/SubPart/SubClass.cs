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
using System.Runtime.InteropServices;
using Soot.Dotnet.TestSnippets.Types;

namespace Soot.Dotnet.TestSnippets.SubPart
{
    public class SubClass
    {
        private int _i;

        private NestedClass _l;

        public string EinString;

        public SubClass()
        {
            _i = 0;
        }

        public string EinStringParam { get; set; }

        public int Z { get; set; }

        public void Con()
        {
            _i = 0;
        }

        /*public static void BinStat()
        {
            Console.WriteLine("haha statisch");
        }*/

        public int GetI()
        {
            Typ.StatischesInt = 234;
            return _i;
        }

        public void Yo()
        {
            var l = new NestedClass(5, 90);
            _i = l.Calcen(1, 2);
            Z = NestedClass.CalcenStatic(16);
            _l = new NestedClass(7, 4);
            _i = _l.Calc();
            _l.Calc();

            StaticConstr.MyFunc();
        }

        [StructLayout(LayoutKind.Sequential)]
        private class NestedClass
        {
            private readonly int _b;

            private int _a;

            static NestedClass()
            {
                Console.WriteLine("Hi static cctor");
            }

            public NestedClass(int a, int b)
            {
                _a = a;
                _b = b;
            }

            public int Calc()
            {
                var c = _a + _b;
                c += 40;
                return c;
            }

            public int Calcen(int zahl1, int zahl2)
            {
                return zahl1 + zahl2 + 10;
            }

            public static int CalcenStatic(int zahl3)
            {
                return zahl3 / 4;
            }

            [Obsolete(error: true, message: "ist halt so")]
            public void Alt(int z)
            {
                _a = z;
            }
        }

        public class StaticConstr
        {
            static StaticConstr()
            {
                Console.WriteLine("Hi static cctor");
            }

            public static void MyFunc()
            {
                Console.WriteLine("Yo Function");
            }
        }
    }
}