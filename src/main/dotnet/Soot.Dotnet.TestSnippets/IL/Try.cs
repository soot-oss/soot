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
using System.Collections.Generic;
using System.IO;
using System.Runtime.InteropServices;

namespace Soot.Dotnet.TestSnippets.IL
{
    public class Try
    {
        public void TryCatchEasy()
        {
            try
            {
                Console.WriteLine("Hello");
                var x = 5;
                var z = x + 10;
            }
            catch (Exception e)
            {
            }
        }

        public int TryCatch(int[] a, bool b, string c)
        {
            try
            {
                Console.WriteLine("Alles gut");
                return 123;
            }
            catch (ArgumentNullException e)
            {
                Console.WriteLine("Arg Null Fehler");
            }
            catch (ArgumentException e)
            {
                // Console.WriteLine(e);
                Console.WriteLine("Argument Fehler");
                // throw new AggregateException();
                throw new Exception();
            }
            catch (Exception e)
            {
                Console.WriteLine("Ein Fehler e");
                // rethrow
                throw;
            }
            finally
            {
                Console.WriteLine("final countdown");
            }

            return 0;
        }

        public void TryFilter()
        {
            try
            {
                Console.WriteLine("Yey");
            }
            catch (COMException ex) when ((uint)ex.ErrorCode == 0x80070005)
            {
                Console.WriteLine("Zugang verboten!");
            }
        }

        public void TryFilterExtended()
        {
            try
            {
                Console.WriteLine("Yey");
            }
            catch (ArgumentException e)
            {
                Console.WriteLine("Arg Fehler");
            }
            catch (COMException ex) when ((uint)ex.ErrorCode == 0x80070005)
            {
                Console.WriteLine("Zugang verboten!");
            }
            catch (Exception e)
            {
                Console.WriteLine("Sonstige Fehler" + e.Message);
            }
        }

        // ("ConsoleApp1InnerClass.Try+<TryFault>d__0");
        /// ("MoveNext");
        private static IEnumerable<string> TryFault(string fileName)
        {
            using (var file = File.OpenText(fileName))
            {
                string s;
                while ((s = file.ReadLine()) != null) yield return s;
            }
        }

        public void TryCatch(int i)
        {
            try
            {
                i = 2;
            }
            catch (SystemException e)
            {
                i = 3;
            }
            finally
            {
                i = 4;
            }
        }
    }
}