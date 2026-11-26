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

namespace Soot.Dotnet.TestSnippets.IL
{
    /// <summary>
    ///     Switch Instructions
    /// </summary>
    public class Switcher
    {
        public enum En
        {
            Dies,
            Das
        }

        public void DoSw(string s)
        {
            var lel = true;
            if (lel)
            {
                var x = 0;
                Console.WriteLine(x);
            }

            switch (s)
            {
                case "lel":
                    Console.WriteLine("jo1");
                    break;
                case "lol":
                    Console.WriteLine("lol");
                    break;
                default:
                    Console.WriteLine("no");
                    break;
            }
        }

        public void SwInt(int i)
        {
            bool? lol;
            switch (i)
            {
                case 11:
                    Console.WriteLine(10.0);
                    // Initobj
                    lol = null;
                    break;
                case 21:
                    Console.WriteLine("line");
                    // ldloca
                    lol = true;
                    if (lol.Value)
                        Console.WriteLine("");
                    break;
            }

            float x = i;
        }

        public void SwIntE(En e)
        {
            switch (e)
            {
                case En.Das:
                    var x = 0;
                    break;
                case En.Dies:
                    var y = 1;
                    break;
            }
        }

        // SwitchInstruction OpCode
        public void SwInstOpCode(int x)
        {
            switch (x)
            {
                case 0:
                    Console.WriteLine("line 0");
                    return;
                case 1:
                {
                    Console.WriteLine("line 1");
                    return;
                }
                /*case 2:
                {
                    Console.WriteLine("line 2");
                    return;
                }*/
                case 3:
                {
                    Console.WriteLine("line 3");
                    return;
                }
                default:
                {
                    Console.WriteLine("line d");
                    return;
                }
            }
        }
    }
}