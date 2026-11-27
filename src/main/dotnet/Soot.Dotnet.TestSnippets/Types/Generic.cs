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

using System.Collections.Generic;

namespace Soot.Dotnet.TestSnippets.Types
{
    public class Generic
    {
        public static void DoSth(GenericList<string> list)
        {
            // Declare a list of type int.
            var list1 = new GenericList<int>();
            list1.Add(1);

            // Declare a list of type string.
            var list2 = new GenericList<string>();
            list2.Add("");

            // Declare a list of type ExampleClass.
            var list3 = new GenericList<ExampleClass>();
            list3.Add(new ExampleClass());
        }

        public static void DoGen(GenericList<int> genericList)
        {
            var x = 0;
        }

        public static void DoGen(GenericList<string> genericList)
        {
            var x = 1;
        }

        private class ExampleClass
        {
        }
    }

    // Declare the generic class.
    public class GenericList<T>
    {
        private readonly List<T> _lst = new List<T>();

        public void Add(T input)
        {
            _lst.Add(input);
        }

        public T Get()
        {
            return _lst[0];
        }

        public List<T> GetAll()
        {
            return _lst;
        }
    }
}