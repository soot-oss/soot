package ca.mcgill.sable.soot.jimple.parser.parser;

import ca.mcgill.sable.soot.jimple.parser.node.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;

class TokenIndex extends AnalysisAdapter
{
    int index;

    public void caseTPublic(TPublic node)
    {
        index = 0;
    }

    public void caseTProtected(TProtected node)
    {
        index = 1;
    }

    public void caseTPrivate(TPrivate node)
    {
        index = 2;
    }

    public void caseTStatic(TStatic node)
    {
        index = 3;
    }

    public void caseTFinal(TFinal node)
    {
        index = 4;
    }

    public void caseTAbstract(TAbstract node)
    {
        index = 5;
    }

    public void caseTSynchronized(TSynchronized node)
    {
        index = 6;
    }

    public void caseTTransient(TTransient node)
    {
        index = 7;
    }

    public void caseTVolatile(TVolatile node)
    {
        index = 8;
    }

    public void caseTClass(TClass node)
    {
        index = 9;
    }

    public void caseTInterface(TInterface node)
    {
        index = 10;
    }

    public void caseTVoid(TVoid node)
    {
        index = 11;
    }

    public void caseTBoolean(TBoolean node)
    {
        index = 12;
    }

    public void caseTByte(TByte node)
    {
        index = 13;
    }

    public void caseTShort(TShort node)
    {
        index = 14;
    }

    public void caseTChar(TChar node)
    {
        index = 15;
    }

    public void caseTInt(TInt node)
    {
        index = 16;
    }

    public void caseTLong(TLong node)
    {
        index = 17;
    }

    public void caseTFloat(TFloat node)
    {
        index = 18;
    }

    public void caseTDouble(TDouble node)
    {
        index = 19;
    }

    public void caseTUnknown(TUnknown node)
    {
        index = 20;
    }

    public void caseTExtends(TExtends node)
    {
        index = 21;
    }

    public void caseTImplements(TImplements node)
    {
        index = 22;
    }

    public void caseTBreakpoint(TBreakpoint node)
    {
        index = 23;
    }

    public void caseTNew(TNew node)
    {
        index = 24;
    }

    public void caseTCase(TCase node)
    {
        index = 25;
    }

    public void caseTCatch(TCatch node)
    {
        index = 26;
    }

    public void caseTCmp(TCmp node)
    {
        index = 27;
    }

    public void caseTCmpg(TCmpg node)
    {
        index = 28;
    }

    public void caseTCmpl(TCmpl node)
    {
        index = 29;
    }

    public void caseTDefault(TDefault node)
    {
        index = 30;
    }

    public void caseTEntermonitor(TEntermonitor node)
    {
        index = 31;
    }

    public void caseTExitmonitor(TExitmonitor node)
    {
        index = 32;
    }

    public void caseTFrom(TFrom node)
    {
        index = 33;
    }

    public void caseTGoto(TGoto node)
    {
        index = 34;
    }

    public void caseTIf(TIf node)
    {
        index = 35;
    }

    public void caseTInstanceof(TInstanceof node)
    {
        index = 36;
    }

    public void caseTInterfaceinvoke(TInterfaceinvoke node)
    {
        index = 37;
    }

    public void caseTLookupswitch(TLookupswitch node)
    {
        index = 38;
    }

    public void caseTNop(TNop node)
    {
        index = 39;
    }

    public void caseTRet(TRet node)
    {
        index = 40;
    }

    public void caseTReturn(TReturn node)
    {
        index = 41;
    }

    public void caseTSpecialinvoke(TSpecialinvoke node)
    {
        index = 42;
    }

    public void caseTStaticinvoke(TStaticinvoke node)
    {
        index = 43;
    }

    public void caseTTableswitch(TTableswitch node)
    {
        index = 44;
    }

    public void caseTThrow(TThrow node)
    {
        index = 45;
    }

    public void caseTTo(TTo node)
    {
        index = 46;
    }

    public void caseTVirtualinvoke(TVirtualinvoke node)
    {
        index = 47;
    }

    public void caseTWith(TWith node)
    {
        index = 48;
    }

    public void caseTComma(TComma node)
    {
        index = 49;
    }

    public void caseTLBrace(TLBrace node)
    {
        index = 50;
    }

    public void caseTRBrace(TRBrace node)
    {
        index = 51;
    }

    public void caseTSemicolon(TSemicolon node)
    {
        index = 52;
    }

    public void caseTLBracket(TLBracket node)
    {
        index = 53;
    }

    public void caseTRBracket(TRBracket node)
    {
        index = 54;
    }

    public void caseTLAngledBracket(TLAngledBracket node)
    {
        index = 55;
    }

    public void caseTRAngledBracket(TRAngledBracket node)
    {
        index = 56;
    }

    public void caseTLParen(TLParen node)
    {
        index = 57;
    }

    public void caseTRParen(TRParen node)
    {
        index = 58;
    }

    public void caseTColon(TColon node)
    {
        index = 59;
    }

    public void caseTDot(TDot node)
    {
        index = 60;
    }

    public void caseTQuote(TQuote node)
    {
        index = 61;
    }

    public void caseTColonEquals(TColonEquals node)
    {
        index = 62;
    }

    public void caseTEquals(TEquals node)
    {
        index = 63;
    }

    public void caseTAnd(TAnd node)
    {
        index = 64;
    }

    public void caseTOr(TOr node)
    {
        index = 65;
    }

    public void caseTXor(TXor node)
    {
        index = 66;
    }

    public void caseTMod(TMod node)
    {
        index = 67;
    }

    public void caseTCmpeq(TCmpeq node)
    {
        index = 68;
    }

    public void caseTCmpne(TCmpne node)
    {
        index = 69;
    }

    public void caseTCmpgt(TCmpgt node)
    {
        index = 70;
    }

    public void caseTCmpge(TCmpge node)
    {
        index = 71;
    }

    public void caseTCmplt(TCmplt node)
    {
        index = 72;
    }

    public void caseTCmple(TCmple node)
    {
        index = 73;
    }

    public void caseTShl(TShl node)
    {
        index = 74;
    }

    public void caseTShr(TShr node)
    {
        index = 75;
    }

    public void caseTUshr(TUshr node)
    {
        index = 76;
    }

    public void caseTPlus(TPlus node)
    {
        index = 77;
    }

    public void caseTMinus(TMinus node)
    {
        index = 78;
    }

    public void caseTMult(TMult node)
    {
        index = 79;
    }

    public void caseTDiv(TDiv node)
    {
        index = 80;
    }

    public void caseTClassIdentifier(TClassIdentifier node)
    {
        index = 81;
    }

    public void caseTSimpleIdentifier(TSimpleIdentifier node)
    {
        index = 82;
    }

    public void caseTAtIdentifier(TAtIdentifier node)
    {
        index = 83;
    }

    public void caseTBoolConstant(TBoolConstant node)
    {
        index = 84;
    }

    public void caseTIntegerConstant(TIntegerConstant node)
    {
        index = 85;
    }

    public void caseTStringConstant(TStringConstant node)
    {
        index = 86;
    }

    public void caseEOF(EOF node)
    {
        index = 87;
    }
}
