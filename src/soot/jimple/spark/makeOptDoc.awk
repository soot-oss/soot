#!/usr/bin/awk -f
#/* Soot - a J*va Optimization Framework
# * Copyright (C) 2002 Ondrej Lhotak
# *
# * This library is free software; you can redistribute it and/or
# * modify it under the terms of the GNU Lesser General Public
# * License as published by the Free Software Foundation; either
# * version 2.1 of the License, or (at your option) any later version.
# *
# * This library is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# * Lesser General Public License for more details.
# *
# * You should have received a copy of the GNU Lesser General Public
# * License along with this library; if not, write to the
# * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
# * Boston, MA 02111-1307, USA.
# */

BEGIN {
    print "\\documentclass{article}"
    print "\\title{Spark Options}"
    print "\\author{Ond\\v{r}ej Lhot\'ak}"
    print "\\begin{document}"
    print "\\maketitle"
}
END {
    print "\\end{document}"
}
/^SECTION/ {
    gsub("^SECTION ","");
    print "\\section{"$0"}";
}
/^BOPT/ {
    option = $2;
    getline default;
    allowed = " true false";
    print "\\subsection{Option \\tt "option"}";
    print "\\begin{itemize}";
    print "\\item Allowed values: {\\tt "allowed"}";
    print "\\item Default value: {\\tt "default"}";
    print "\\end{itemize}";
    while( 1 ) {
        getline comment;
        if( comment == "END" ) break;
        print comment;
    }
}

/^MOPT/ {
    option = $2;
    i = 3;
    allowed = "";
    while( $i != "" ) {
        allowed = allowed " " $i;
        i++;
    }
    getline default;
    print "\\subsection{Option \\tt "option"}";
    print "\\begin{itemize}";
    print "\\item Allowed values: {\\tt "allowed"}";
    print "\\item Default value: {\\tt "default"}";
    print "\\end{itemize}";
    while( 1 ) {
        getline comment;
        if( comment == "END" ) break;
        print comment;
    }
}

