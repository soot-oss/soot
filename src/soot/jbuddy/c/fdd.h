/*========================================================================
               Copyright (C) 1996-2002 by Jorn Lind-Nielsen
                            All rights reserved

    Permission is hereby granted, without written agreement and without
    license or royalty fees, to use, reproduce, prepare derivative
    works, distribute, and display this software and its documentation
    for any purpose, provided that (1) the above copyright notice and
    the following two paragraphs appear in all copies of the source code
    and (2) redistributions, including without limitation binaries,
    reproduce these notices in the supporting documentation. Substantial
    modifications to this software may be copyrighted by their authors
    and need not follow the licensing terms described here, provided
    that the new terms are clearly indicated in all files where they apply.

    IN NO EVENT SHALL JORN LIND-NIELSEN, OR DISTRIBUTORS OF THIS
    SOFTWARE BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT, SPECIAL,
    INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OF THIS
    SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE AUTHORS OR ANY OF THE
    ABOVE PARTIES HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

    JORN LIND-NIELSEN SPECIFICALLY DISCLAIM ANY WARRANTIES, INCLUDING,
    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
    FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE PROVIDED HEREUNDER IS
    ON AN "AS IS" BASIS, AND THE AUTHORS AND DISTRIBUTORS HAVE NO
    OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
    MODIFICATIONS.
========================================================================*/

/*************************************************************************
  $Header: /home/jln/phd/bdd/src/RCS/fdd.h,v 1.1 2000/05/17 20:00:05 jln Exp jln $
  FILE:  fdd.h
  DESCR: Finite domain data with BDDs
  AUTH:  Jorn Lind
  DATE:  (C) february 1999
*************************************************************************/

#ifndef _FDD_H
#define _FDD_H

#include "bdd.h"


#ifdef CPLUSPLUS
extern "C" {
#endif

/* In file fdd.c */
   
typedef struct s_Domain
{
   int realsize;   /* The specified domain (0...N-1) */
   int binsize;    /* The number of BDD variables representing the domain */
   int *ivar;      /* Variable indeces for the variable set */
   BDD var;        /* The BDD variable set */
} Domain;

extern Domain* domain;

extern int  fdd_extdomain(int*INOUT, int);
extern int  fdd_overlapdomain(int, int);
extern void fdd_clearall(void);
extern int  fdd_domainnum(void);
extern int  fdd_domainsize(int);
extern int  fdd_varnum(int);
extern int* fdd_vars(int);
extern BDD  fdd_ithvar(int, int);
extern int  fdd_scanvar(BDD, int);
extern int* fdd_scanallvar(BDD);
extern BDD  fdd_ithset(int);
extern BDD  fdd_domain(int);
extern BDD  fdd_equals(int, int);
extern bddfilehandler fdd_file_hook(bddfilehandler);
#ifdef CPLUSPLUS
extern bddstrmhandler fdd_strm_hook(bddstrmhandler);
#endif
extern void fdd_printset(BDD);
extern void fdd_fprintset(FILE*, BDD);
extern int  fdd_scanset(BDD, int**, int*);
extern BDD  fdd_makeset(int*, int);
extern int  fdd_intaddvarblock(int, int, int);
extern int  fdd_setpair(bddPair*, int, int);
extern int  fdd_setpairs(bddPair*, int*, int*, int);

#ifdef CPLUSPLUS
}
#endif

/*************************************************************************
   If this file is included from a C++ compiler then the following
   classes, wrappers and hacks are supplied.
*************************************************************************/
#ifdef CPLUSPLUS

   /* FDD extensions */

inline bdd fdd_ithvarpp(int var, int val)
{ return fdd_ithvar(var, val); }

inline bdd fdd_ithsetpp(int var)
{ return fdd_ithset(var); }

inline bdd fdd_domainpp(int var)
{ return fdd_domain(var); }

inline int fdd_scanvar(const bdd &r, int var)
{ return fdd_scanvar(r.root, var); }

inline int* fdd_scanallvar(const bdd &r)
{ return fdd_scanallvar(r.root); }

inline bdd fdd_equalspp(int left, int right)
{ return fdd_equals(left, right); }

inline void fdd_printset(const bdd &r)
{ fdd_printset(r.root); }

inline void fdd_fprintset(FILE* ofile, const bdd &r)
{ fdd_fprintset(ofile, r.root); }

inline int fdd_scanset(const bdd &r, int *&v, int &n)
{ return fdd_scanset(r.root, &v, &n); }

inline bdd fdd_makesetpp(int *v, int n)
{ return fdd_makeset(v,n); }

#if 0
inline bdd* fdd_conpp(int bitnum, int var)
{ return fdd_transfer( bitnum, fdd_con(bitnum, var) ); }

inline bdd* fdd_varpp(int bitnum, int var)
{ return fdd_transfer( bitnum, fdd_var(bitnum, var) ); }

extern int fdd_isconst(int bitnum, bdd *e);
extern int fdd_val(int bitnum, bdd *e);

inline bdd* fdd_add(int bitnum, bdd *left, bdd *right)
{ return fdd_termopr(bitnum, left, right,bdd::fddAdd); }

inline bdd* fdd_sub(int bitnum, bdd *left, bdd *right)
{ return fdd_termopr(bitnum, left, right,bdd::fddSub); }

inline bdd* fdd_shl(int bitnum, bdd *expr, bdd c)
{ return fdd_shift(bitnum, expr, c, bdd::fddShl); }

inline bdd* fdd_shr(int bitnum, bdd *expr, bdd c)
{ return fdd_shift(bitnum, expr, c, bdd::fddShr); }

inline bdd fdd_lth(int bitnum, bdd *left, bdd *right)
{ return fdd_relopr(bitnum, left, right, bdd::fddLth); }

inline bdd fdd_lte(int bitnum, bdd *left, bdd *right)
{ return fdd_relopr(bitnum, left, right, bdd::fddLte); }

inline bdd fdd_gth(int bitnum, bdd *left, bdd *right)
{ return fdd_relopr(bitnum, left, right, bdd::fddGth); }

inline bdd fdd_gte(int bitnum, bdd *left, bdd *right)
{ return fdd_relopr(bitnum, left, right, bdd::fddGte); }

inline bdd fdd_equ(int bitnum, bdd *left, bdd *right)
{ return fdd_relopr(bitnum, left, right, bdd::fddEqu); }

inline bdd fdd_neq(int bitnum, bdd *left, bdd *right)
{ return fdd_relopr(bitnum, left, right, bdd::fddNeq); }
#endif

   /* Hacks to allow for overloading of return-types only */
#define fdd_ithvar fdd_ithvarpp
#define fdd_ithset fdd_ithsetpp
#define fdd_domain fdd_domainpp
#define fdd_equals fdd_equalspp
#define fdd_makeset fdd_makesetpp
#define fdd_con fdd_conpp
#define fdd_var fdd_varpp


#endif /* CPLUSPLUS */

#endif /* _FDD_H */


/* EOF */
