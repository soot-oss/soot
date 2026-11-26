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

using NLog;
using NLog.Config;
using NLog.Layouts;
using NLog.Targets;

namespace Soot.Dotnet.Decompiler.Helper
{
    public static class LoggerUtils
    {
        /// <summary>
        ///     Initialize NLog with our configuration of console logging
        ///     It is called one-time at AssemblyAnalyzer
        /// </summary>
        public static void InitNlog()
        {
            var config = new LoggingConfiguration();

            // Targets where to log to: Console
            var logConsole = new ConsoleTarget("logconsole")
            {
                Layout = Layout.FromString("[Dotnet] " +
                                           "${level:uppercase=true:padding=-5} ${logger} - " +
                                           "${message}${literal:text=\n:when='${exception}'!=''} " +
                                           "${exception:format=ToString}${newline} " +
                                           "${literal:text=(Method <:when='${event-properties:item=TypeName}'!=''}"
                                           + "${event-properties:item=TypeName}"
                                           + "${literal:text=\\::when='${event-properties:item=Method}'!=''}"
                                           + "${event-properties:item=Method}" +
                                           "${literal:text=>):when='${event-properties:item=TypeName}'!=''}")
            };

            // Rules for mapping loggers to targets
            config.AddRule(LogLevel.Info, LogLevel.Fatal, logConsole);
            // Apply config
            LogManager.Configuration = config;
            var logger = LogManager.GetCurrentClassLogger();
            logger.Info(".NET Decompiler: Initialized logger; running using runtime " + System.Runtime.InteropServices.RuntimeInformation.FrameworkDescription);
            var stopper = false;
            while(stopper) {
                System.Threading.Thread.Sleep(5000);
                logger.Info("Step12");
            }
        }
    }
}