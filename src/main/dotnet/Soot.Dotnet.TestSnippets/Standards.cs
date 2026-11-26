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

namespace Soot.Dotnet.TestSnippets
{
    /// <summary>
    ///     Standard stuff such as "if"
    /// </summary>
    public class Standards
    {
        public static int MyProp { get; set; }

        public int PropWithBody
        {
            set => MyProp = value * 2;
            get => MyProp / 2;
        }

        private static void If(int x)
        {
            var number = 0;
            if (x == 3)
                number = 5;
            else
                number = 10;
        }
    }
}