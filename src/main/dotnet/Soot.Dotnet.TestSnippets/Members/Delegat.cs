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

namespace Soot.Dotnet.TestSnippets.Members
{
    public delegate void MyDelegate(string s);

    /// <summary>
    ///     Produce IL Code with delegates
    /// </summary>
    public class MyDelegateInvoke
    {
        public static void DoSth()
        {
            var md = new MyDelegate(MyFunction);
            md("Test");
        }

        public static void MyFunction(string s)
        {
            Console.WriteLine(s);
        }
    }
}