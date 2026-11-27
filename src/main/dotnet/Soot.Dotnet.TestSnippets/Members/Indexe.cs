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
    /// <summary>
    ///     indexer
    /// </summary>
    public class Indexe
    {
        public static void DoSth()
        {
            var stringCollection = new SampleCollection<string>();
            stringCollection[0] = "Hello, World";
            Console.WriteLine(stringCollection[0]);

            var stringCollection2 = new MyIndexer
            {
                [0] = "Hello, World"
            };
            Console.WriteLine(stringCollection2[0]);
        }
    }

    internal class SampleCollection<T>
    {
        // Declare an array to store the data elements.
        private readonly T[] arr = new T[100];

        // Define the indexer to allow client code to use [] notation.
        public T this[int i]
        {
            get => arr[i];
            set => arr[i] = value;
        }
    }

    internal class MyIndexer
    {
        // Declare an array to store the data elements.
        private readonly string[] arr = new string[100];

        // Define the indexer to allow client code to use [] notation.
        public string this[int i]
        {
            get => arr[i];
            set => arr[i] = value;
        }
    }
}