package ca.mcgill.sable.soot.jimple.parser.lexer;

import java.io.*;
import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.jimple.parser.node.*;

public class Lexer
{
    protected Token token;
    protected State state = State.INITIAL;

    private PushbackReader in;
    private int line;
    private int pos;
    private boolean cr;
    private boolean eof;
    private final StringBuffer text = new StringBuffer();

    protected void filter() throws LexerException, IOException
    {
    }

    public Lexer(PushbackReader in)
    {
        this.in = in;
    }

    public Token peek() throws LexerException, IOException
    {
        while(token == null)
        {
            token = getToken();
            filter();
        }

        return token;
    }

    public Token next() throws LexerException, IOException
    {
        while(token == null)
        {
            token = getToken();
            filter();
        }

        Token result = token;
        token = null;
        return result;
    }

    private Token getToken() throws IOException, LexerException
    {
        int dfa_state = 0;

        int start_pos = pos;
        int start_line = line;

        int accept_state = -1;
        int accept_token = -1;
        int accept_length = -1;
        int accept_pos = -1;
        int accept_line = -1;

        text.setLength(0);

        while(true)
        {
            int c = getChar();

            if(c != -1)
            {
                switch(c)
                {
                case 10:
                    if(cr)
                    {
                        cr = false;
                    }
                    else
                    {
                        line++;
                        pos = 0;
                    }
                    break;
                case 13:
                    line++;
                    pos = 0;
                    cr = true;
                    break;
                default:
                    pos++;
                    cr = false;
                    break;
                };

                text.append((char) c);

                do
                {
                    int oldState = (dfa_state < -1) ? (-2 -dfa_state) : dfa_state;

                    dfa_state = -1;

                    int low = 0;
                    int high = gotoTable[oldState].length - 1;

                    while(low <= high)
                    {
                        int middle = (low + high) / 2;

                        if(c < gotoTable[oldState][middle][0])
                        {
                            high = middle - 1;
                        }
                        else if(c > gotoTable[oldState][middle][1])
                        {
                            low = middle + 1;
                        }
                        else
                        {
                            dfa_state = gotoTable[oldState][middle][2];
                            break;
                        }
                    }
                }while(dfa_state < -1);
            }
            else
            {
                dfa_state = -1;
            }

            if(dfa_state >= 0)
            {
                if(accept[state.id()][dfa_state] != -1)
                {
                    accept_state = dfa_state;
                    accept_token = accept[state.id()][dfa_state];
                    accept_length = text.length();
                    accept_pos = pos;
                    accept_line = line;
                }
            }
            else
            {
                if(accept_state != -1)
                {
                    switch(accept_token)
                    {
                    case 0:
                        {
                            Token token = new0(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 1:
                        {
                            Token token = new1(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 2:
                        {
                            Token token = new2(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 3:
                        {
                            Token token = new3(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 4:
                        {
                            Token token = new4(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 5:
                        {
                            Token token = new5(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 6:
                        {
                            Token token = new6(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 7:
                        {
                            Token token = new7(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 8:
                        {
                            Token token = new8(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 9:
                        {
                            Token token = new9(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 10:
                        {
                            Token token = new10(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 11:
                        {
                            Token token = new11(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 12:
                        {
                            Token token = new12(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 13:
                        {
                            Token token = new13(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 14:
                        {
                            Token token = new14(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 15:
                        {
                            Token token = new15(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 16:
                        {
                            Token token = new16(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 17:
                        {
                            Token token = new17(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 18:
                        {
                            Token token = new18(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 19:
                        {
                            Token token = new19(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 20:
                        {
                            Token token = new20(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 21:
                        {
                            Token token = new21(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 22:
                        {
                            Token token = new22(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 23:
                        {
                            Token token = new23(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 24:
                        {
                            Token token = new24(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 25:
                        {
                            Token token = new25(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 26:
                        {
                            Token token = new26(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 27:
                        {
                            Token token = new27(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 28:
                        {
                            Token token = new28(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 29:
                        {
                            Token token = new29(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 30:
                        {
                            Token token = new30(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 31:
                        {
                            Token token = new31(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 32:
                        {
                            Token token = new32(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 33:
                        {
                            Token token = new33(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 34:
                        {
                            Token token = new34(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 35:
                        {
                            Token token = new35(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 36:
                        {
                            Token token = new36(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 37:
                        {
                            Token token = new37(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 38:
                        {
                            Token token = new38(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 39:
                        {
                            Token token = new39(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 40:
                        {
                            Token token = new40(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 41:
                        {
                            Token token = new41(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 42:
                        {
                            Token token = new42(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 43:
                        {
                            Token token = new43(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 44:
                        {
                            Token token = new44(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 45:
                        {
                            Token token = new45(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 46:
                        {
                            Token token = new46(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 47:
                        {
                            Token token = new47(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 48:
                        {
                            Token token = new48(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 49:
                        {
                            Token token = new49(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 50:
                        {
                            Token token = new50(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 51:
                        {
                            Token token = new51(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 52:
                        {
                            Token token = new52(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 53:
                        {
                            Token token = new53(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 54:
                        {
                            Token token = new54(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 55:
                        {
                            Token token = new55(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 56:
                        {
                            Token token = new56(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 57:
                        {
                            Token token = new57(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 58:
                        {
                            Token token = new58(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 59:
                        {
                            Token token = new59(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 60:
                        {
                            Token token = new60(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 61:
                        {
                            Token token = new61(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 62:
                        {
                            Token token = new62(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 63:
                        {
                            Token token = new63(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 64:
                        {
                            Token token = new64(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 65:
                        {
                            Token token = new65(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 66:
                        {
                            Token token = new66(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 67:
                        {
                            Token token = new67(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 68:
                        {
                            Token token = new68(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 69:
                        {
                            Token token = new69(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 70:
                        {
                            Token token = new70(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 71:
                        {
                            Token token = new71(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 72:
                        {
                            Token token = new72(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 73:
                        {
                            Token token = new73(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 74:
                        {
                            Token token = new74(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 75:
                        {
                            Token token = new75(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 76:
                        {
                            Token token = new76(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 77:
                        {
                            Token token = new77(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 78:
                        {
                            Token token = new78(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 79:
                        {
                            Token token = new79(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 80:
                        {
                            Token token = new80(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 81:
                        {
                            Token token = new81(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 82:
                        {
                            Token token = new82(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 83:
                        {
                            Token token = new83(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 84:
                        {
                            Token token = new84(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 85:
                        {
                            Token token = new85(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 86:
                        {
                            Token token = new86(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 87:
                        {
                            Token token = new87(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 88:
                        {
                            Token token = new88(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 89:
                        {
                            Token token = new89(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 90:
                        {
                            Token token = new90(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 91:
                        {
                            Token token = new91(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 92:
                        {
                            Token token = new92(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    }
                }
                else
                {
                    if(text.length() > 0)
                    {
                        throw new LexerException(
                            "[" + (start_line + 1) + "," + (start_pos + 1) + "]" +
                            " Unknown token: " + text);
                    }
                    else
                    {
                        EOF token = new EOF(
                            start_line + 1,
                            start_pos + 1);
                        return token;
                    }
                }
            }
        }
    }

    Token new0(int line, int pos) { return new TAbstract(line, pos); }
    Token new1(int line, int pos) { return new TFinal(line, pos); }
    Token new2(int line, int pos) { return new TNative(line, pos); }
    Token new3(int line, int pos) { return new TPublic(line, pos); }
    Token new4(int line, int pos) { return new TProtected(line, pos); }
    Token new5(int line, int pos) { return new TPrivate(line, pos); }
    Token new6(int line, int pos) { return new TStatic(line, pos); }
    Token new7(int line, int pos) { return new TSynchronized(line, pos); }
    Token new8(int line, int pos) { return new TTransient(line, pos); }
    Token new9(int line, int pos) { return new TVolatile(line, pos); }
    Token new10(int line, int pos) { return new TClass(line, pos); }
    Token new11(int line, int pos) { return new TInterface(line, pos); }
    Token new12(int line, int pos) { return new TVoid(line, pos); }
    Token new13(int line, int pos) { return new TBoolean(line, pos); }
    Token new14(int line, int pos) { return new TByte(line, pos); }
    Token new15(int line, int pos) { return new TShort(line, pos); }
    Token new16(int line, int pos) { return new TChar(line, pos); }
    Token new17(int line, int pos) { return new TInt(line, pos); }
    Token new18(int line, int pos) { return new TLong(line, pos); }
    Token new19(int line, int pos) { return new TFloat(line, pos); }
    Token new20(int line, int pos) { return new TDouble(line, pos); }
    Token new21(int line, int pos) { return new TUnknown(line, pos); }
    Token new22(int line, int pos) { return new TExtends(line, pos); }
    Token new23(int line, int pos) { return new TImplements(line, pos); }
    Token new24(int line, int pos) { return new TBreakpoint(line, pos); }
    Token new25(int line, int pos) { return new TCase(line, pos); }
    Token new26(int line, int pos) { return new TCatch(line, pos); }
    Token new27(int line, int pos) { return new TCmp(line, pos); }
    Token new28(int line, int pos) { return new TCmpg(line, pos); }
    Token new29(int line, int pos) { return new TCmpl(line, pos); }
    Token new30(int line, int pos) { return new TDefault(line, pos); }
    Token new31(int line, int pos) { return new TEntermonitor(line, pos); }
    Token new32(int line, int pos) { return new TExitmonitor(line, pos); }
    Token new33(int line, int pos) { return new TFrom(line, pos); }
    Token new34(int line, int pos) { return new TGoto(line, pos); }
    Token new35(int line, int pos) { return new TIf(line, pos); }
    Token new36(int line, int pos) { return new TInstanceof(line, pos); }
    Token new37(int line, int pos) { return new TInterfaceinvoke(line, pos); }
    Token new38(int line, int pos) { return new TLengthof(line, pos); }
    Token new39(int line, int pos) { return new TLookupswitch(line, pos); }
    Token new40(int line, int pos) { return new TNew(line, pos); }
    Token new41(int line, int pos) { return new TNewarray(line, pos); }
    Token new42(int line, int pos) { return new TNewmultiarray(line, pos); }
    Token new43(int line, int pos) { return new TNop(line, pos); }
    Token new44(int line, int pos) { return new TRet(line, pos); }
    Token new45(int line, int pos) { return new TReturn(line, pos); }
    Token new46(int line, int pos) { return new TSpecialinvoke(line, pos); }
    Token new47(int line, int pos) { return new TStaticinvoke(line, pos); }
    Token new48(int line, int pos) { return new TTableswitch(line, pos); }
    Token new49(int line, int pos) { return new TThrow(line, pos); }
    Token new50(int line, int pos) { return new TThrows(line, pos); }
    Token new51(int line, int pos) { return new TTo(line, pos); }
    Token new52(int line, int pos) { return new TVirtualinvoke(line, pos); }
    Token new53(int line, int pos) { return new TWith(line, pos); }
    Token new54(int line, int pos) { return new TComma(line, pos); }
    Token new55(int line, int pos) { return new TLBrace(line, pos); }
    Token new56(int line, int pos) { return new TRBrace(line, pos); }
    Token new57(int line, int pos) { return new TSemicolon(line, pos); }
    Token new58(int line, int pos) { return new TLBracket(line, pos); }
    Token new59(int line, int pos) { return new TRBracket(line, pos); }
    Token new60(int line, int pos) { return new TLParen(line, pos); }
    Token new61(int line, int pos) { return new TRParen(line, pos); }
    Token new62(int line, int pos) { return new TColon(line, pos); }
    Token new63(int line, int pos) { return new TDot(line, pos); }
    Token new64(int line, int pos) { return new TQuote(line, pos); }
    Token new65(int line, int pos) { return new TColonEquals(line, pos); }
    Token new66(int line, int pos) { return new TEquals(line, pos); }
    Token new67(int line, int pos) { return new TAnd(line, pos); }
    Token new68(int line, int pos) { return new TOr(line, pos); }
    Token new69(int line, int pos) { return new TXor(line, pos); }
    Token new70(int line, int pos) { return new TMod(line, pos); }
    Token new71(int line, int pos) { return new TCmpeq(line, pos); }
    Token new72(int line, int pos) { return new TCmpne(line, pos); }
    Token new73(int line, int pos) { return new TCmpgt(line, pos); }
    Token new74(int line, int pos) { return new TCmpge(line, pos); }
    Token new75(int line, int pos) { return new TCmplt(line, pos); }
    Token new76(int line, int pos) { return new TCmple(line, pos); }
    Token new77(int line, int pos) { return new TShl(line, pos); }
    Token new78(int line, int pos) { return new TShr(line, pos); }
    Token new79(int line, int pos) { return new TUshr(line, pos); }
    Token new80(int line, int pos) { return new TPlus(line, pos); }
    Token new81(int line, int pos) { return new TMinus(line, pos); }
    Token new82(int line, int pos) { return new TMult(line, pos); }
    Token new83(int line, int pos) { return new TDiv(line, pos); }
    Token new84(String text, int line, int pos) { return new TName(text, line, pos); }
    Token new85(String text, int line, int pos) { return new TIdentifier(text, line, pos); }
    Token new86(String text, int line, int pos) { return new TAtIdentifier(text, line, pos); }
    Token new87(String text, int line, int pos) { return new TBoolConstant(text, line, pos); }
    Token new88(String text, int line, int pos) { return new TIntegerConstant(text, line, pos); }
    Token new89(String text, int line, int pos) { return new TFloatConstant(text, line, pos); }
    Token new90(String text, int line, int pos) { return new TStringConstant(text, line, pos); }
    Token new91(String text, int line, int pos) { return new TBlank(text, line, pos); }
    Token new92(String text, int line, int pos) { return new TComment(text, line, pos); }

    private int getChar() throws IOException
    {
        if(eof)
        {
            return -1;
        }

        int result = in.read();

        if(result == -1)
        {
            eof = true;
        }

        return result;
    }

    private void pushBack(int acceptLength) throws IOException
    {
        int length = text.length();
        for(int i = length - 1; i >= acceptLength; i--)
        {
            eof = false;

            in.unread(text.charAt(i));
        }
    }

    private String getText(int acceptLength)
    {
        StringBuffer s = new StringBuffer(acceptLength);
        for(int i = 0; i < acceptLength; i++)
        {
            s.append(text.charAt(i));
        }

        return s.toString();
    }

    private static int[][][] gotoTable =
    {
		{{9, 9, 1}, {10, 10, 2}, {13, 13, 3}, {32, 32, 4}, {33, 33, 5}, {34, 34, 6}, {36, 36, 7}, {37, 37, 8}, {38, 38, 9}, {39, 39, 10}, {40, 40, 11}, {41, 41, 12}, {42, 42, 13}, {43, 43, 14}, {44, 44, 15}, {45, 45, 16}, {46, 46, 17}, {47, 47, 18}, {48, 48, 19}, {49, 57, 20}, {58, 58, 21}, {59, 59, 22}, {60, 60, 23}, {61, 61, 24}, {62, 62, 25}, {64, 64, 26}, {65, 90, 27}, {91, 91, 28}, {93, 93, 29}, {94, 94, 30}, {95, 95, 31}, {97, 97, 32}, {98, 98, 33}, {99, 99, 34}, {100, 100, 35}, {101, 101, 36}, {102, 102, 37}, {103, 103, 38}, {104, 104, 39}, {105, 105, 40}, {106, 107, 39}, {108, 108, 41}, {109, 109, 39}, {110, 110, 42}, {111, 111, 39}, {112, 112, 43}, {113, 113, 39}, {114, 114, 44}, {115, 115, 45}, {116, 116, 46}, {117, 117, 47}, {118, 118, 48}, {119, 119, 49}, {120, 122, 39}, {123, 123, 50}, {124, 124, 51}, {125, 125, 52}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{61, 61, 53}, },
		{{0, 33, 54}, {34, 34, 55}, {35, 65535, 54}, },
		{{36, 36, 56}, {48, 57, 57}, {65, 90, 58}, {95, 95, 59}, {97, 122, 60}, },
		{},
		{},
		{{0, 9, 61}, {11, 12, 61}, {14, 38, 61}, {40, 65535, 61}, },
		{},
		{},
		{},
		{},
		{},
		{{48, 57, -2}, },
		{},
		{{42, 42, 62}, {47, 47, 63}, },
		{{46, 46, 64}, {48, 55, 65}, {56, 57, 20}, {76, 76, 66}, {88, 88, 67}, {120, 120, 68}, },
		{{46, 46, 64}, {48, 57, 20}, {76, 76, 66}, },
		{{61, 61, 69}, },
		{},
		{{60, 60, 70}, {61, 61, 71}, },
		{{61, 61, 72}, },
		{{61, 61, 73}, {62, 62, 74}, },
		{{99, 99, 75}, {112, 112, 76}, {116, 116, 77}, },
		{{36, 122, -9}, },
		{},
		{},
		{},
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 60}, {98, 98, 78}, {99, 122, 60}, },
		{{36, 95, -9}, {97, 110, 60}, {111, 111, 79}, {112, 113, 60}, {114, 114, 80}, {115, 120, 60}, {121, 121, 81}, {122, 122, 60}, },
		{{36, 95, -9}, {97, 97, 82}, {98, 103, 60}, {104, 104, 83}, {105, 107, 60}, {108, 108, 84}, {109, 109, 85}, {110, 122, 60}, },
		{{36, 95, -9}, {97, 100, 60}, {101, 101, 86}, {102, 110, 60}, {111, 111, 87}, {112, 122, 60}, },
		{{36, 95, -9}, {97, 109, 60}, {110, 110, 88}, {111, 119, 60}, {120, 120, 89}, {121, 122, 60}, },
		{{36, 95, -9}, {97, 97, 90}, {98, 104, 60}, {105, 105, 91}, {106, 107, 60}, {108, 108, 92}, {109, 113, 60}, {114, 114, 93}, {115, 122, 60}, },
		{{36, 110, -35}, {111, 111, 94}, {112, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 101, 60}, {102, 102, 95}, {103, 108, 60}, {109, 109, 96}, {110, 110, 97}, {111, 122, 60}, },
		{{36, 100, -37}, {101, 101, 98}, {102, 110, 60}, {111, 111, 99}, {112, 122, 60}, },
		{{36, 95, -9}, {97, 97, 100}, {98, 100, 60}, {101, 101, 101}, {102, 110, 60}, {111, 111, 102}, {112, 122, 60}, },
		{{36, 95, -9}, {97, 113, 60}, {114, 114, 103}, {115, 116, 60}, {117, 117, 104}, {118, 122, 60}, },
		{{36, 100, -37}, {101, 101, 105}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 103, 60}, {104, 104, 106}, {105, 111, 60}, {112, 112, 107}, {113, 115, 60}, {116, 116, 108}, {117, 120, 60}, {121, 121, 109}, {122, 122, 60}, },
		{{36, 95, -9}, {97, 97, 110}, {98, 103, 60}, {104, 104, 111}, {105, 110, 60}, {111, 111, 112}, {112, 113, 60}, {114, 114, 113}, {115, 122, 60}, },
		{{36, 109, -38}, {110, 110, 114}, {111, 122, 60}, },
		{{36, 95, -9}, {97, 104, 60}, {105, 105, 115}, {106, 110, 60}, {111, 111, 116}, {112, 122, 60}, },
		{{36, 104, -50}, {105, 105, 117}, {106, 122, 60}, },
		{},
		{},
		{},
		{},
		{{0, 65535, -8}, },
		{},
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{0, 38, -12}, {39, 39, 118}, {40, 65535, 61}, },
		{{0, 41, 119}, {42, 42, 120}, {43, 65535, 119}, },
		{{0, 9, 121}, {11, 12, 121}, {14, 65535, 121}, },
		{{48, 57, 122}, },
		{{46, 76, -21}, },
		{},
		{{48, 57, 123}, {65, 70, 124}, {97, 102, 125}, },
		{{48, 102, -69}, },
		{},
		{},
		{},
		{},
		{},
		{{62, 62, 126}, },
		{{97, 97, 127}, },
		{{97, 97, 128}, },
		{{104, 104, 129}, },
		{{36, 95, -9}, {97, 114, 60}, {115, 115, 130}, {116, 122, 60}, },
		{{36, 110, -35}, {111, 111, 131}, {112, 122, 60}, },
		{{36, 100, -37}, {101, 101, 132}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 115, 60}, {116, 116, 133}, {117, 122, 60}, },
		{{36, 114, -80}, {115, 115, 134}, {116, 116, 135}, {117, 122, 60}, },
		{{36, 95, -9}, {97, 97, 136}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 97, 137}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 111, 60}, {112, 112, 138}, {113, 122, 60}, },
		{{36, 101, -42}, {102, 102, 139}, {103, 122, 60}, },
		{{36, 95, -9}, {97, 116, 60}, {117, 117, 140}, {118, 122, 60}, },
		{{36, 115, -83}, {116, 116, 141}, {117, 122, 60}, },
		{{36, 104, -50}, {105, 105, 142}, {106, 115, 60}, {116, 116, 143}, {117, 122, 60}, },
		{{36, 95, -9}, {97, 107, 60}, {108, 108, 144}, {109, 122, 60}, },
		{{36, 109, -38}, {110, 110, 145}, {111, 122, 60}, },
		{{36, 110, -35}, {111, 111, 146}, {112, 122, 60}, },
		{{36, 110, -35}, {111, 111, 147}, {112, 122, 60}, },
		{{36, 115, -83}, {116, 116, 148}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 111, -87}, {112, 112, 149}, {113, 122, 60}, },
		{{36, 114, -80}, {115, 115, 150}, {116, 116, 151}, {117, 122, 60}, },
		{{36, 109, -38}, {110, 110, 152}, {111, 122, 60}, },
		{{36, 109, -38}, {110, 110, 153}, {111, 111, 154}, {112, 122, 60}, },
		{{36, 115, -83}, {116, 116, 155}, {117, 122, 60}, },
		{{36, 95, -9}, {97, 118, 60}, {119, 119, 156}, {120, 122, 60}, },
		{{36, 111, -87}, {112, 112, 157}, {113, 122, 60}, },
		{{36, 104, -50}, {105, 105, 158}, {106, 110, 60}, {111, 111, 159}, {112, 122, 60}, },
		{{36, 97, -34}, {98, 98, 160}, {99, 122, 60}, },
		{{36, 115, -83}, {116, 116, 161}, {117, 122, 60}, },
		{{36, 110, -35}, {111, 111, 162}, {112, 122, 60}, },
		{{36, 100, -37}, {101, 101, 163}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 97, 164}, {98, 122, 60}, },
		{{36, 109, -38}, {110, 110, 165}, {111, 122, 60}, },
		{{36, 97, -34}, {98, 98, 166}, {99, 122, 60}, },
		{{36, 113, -45}, {114, 114, 167}, {115, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 168}, {98, 116, 60}, {117, 117, 169}, {118, 122, 60}, },
		{{36, 95, -9}, {97, 106, 60}, {107, 107, 170}, {108, 122, 60}, },
		{{36, 113, -45}, {114, 114, 171}, {115, 122, 60}, },
		{{36, 104, -50}, {105, 105, 172}, {106, 107, 60}, {108, 108, 173}, {109, 122, 60}, },
		{{36, 115, -83}, {116, 116, 174}, {117, 122, 60}, },
		{},
		{{0, 65535, -64}, },
		{{0, 41, 175}, {42, 42, 120}, {43, 46, 175}, {47, 47, 176}, {48, 65535, 175}, },
		{{0, 65535, -65}, },
		{{48, 57, 122}, {69, 69, 177}, {70, 70, 178}, {101, 101, 179}, {102, 102, 180}, },
		{{48, 70, -69}, {76, 76, 66}, {97, 102, 125}, },
		{{48, 102, -125}, },
		{{48, 102, -125}, },
		{},
		{{117, 117, 181}, },
		{{114, 114, 182}, },
		{{105, 105, 183}, },
		{{36, 115, -83}, {116, 116, 184}, {117, 122, 60}, },
		{{36, 107, -92}, {108, 108, 185}, {109, 122, 60}, },
		{{36, 95, -9}, {97, 97, 186}, {98, 122, 60}, },
		{{36, 100, -37}, {101, 101, 187}, {102, 122, 60}, },
		{{36, 100, -37}, {101, 101, 188}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 98, 60}, {99, 99, 189}, {100, 122, 60}, },
		{{36, 113, -45}, {114, 114, 190}, {115, 122, 60}, },
		{{36, 114, -80}, {115, 115, 191}, {116, 122, 60}, },
		{{36, 95, -9}, {97, 102, 60}, {103, 103, 192}, {104, 107, 60}, {108, 108, 193}, {109, 122, 60}, },
		{{36, 95, -9}, {97, 97, 194}, {98, 122, 60}, },
		{{36, 97, -34}, {98, 98, 195}, {99, 122, 60}, },
		{{36, 100, -37}, {101, 101, 196}, {102, 122, 60}, },
		{{36, 115, -83}, {116, 116, 197}, {117, 122, 60}, },
		{{36, 100, -37}, {101, 101, 198}, {102, 122, 60}, },
		{{36, 114, -80}, {115, 115, 199}, {116, 122, 60}, },
		{{36, 95, -9}, {97, 97, 200}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 97, 201}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 108, 60}, {109, 109, 202}, {110, 122, 60}, },
		{{36, 110, -35}, {111, 111, 203}, {112, 122, 60}, },
		{{36, 107, -92}, {108, 108, 204}, {109, 122, 60}, },
		{{36, 115, -83}, {116, 116, 205}, {117, 122, 60}, },
		{{36, 100, -37}, {101, 101, 206}, {102, 122, 60}, },
		{{36, 102, -140}, {103, 103, 207}, {104, 122, 60}, },
		{{36, 102, -140}, {103, 103, 208}, {104, 122, 60}, },
		{{36, 106, -116}, {107, 107, 209}, {108, 122, 60}, },
		{{36, 104, -50}, {105, 105, 210}, {106, 122, 60}, },
		{{36, 95, -9}, {97, 97, 211}, {98, 108, 60}, {109, 109, 212}, {110, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 117, 60}, {118, 118, 213}, {119, 122, 60}, },
		{{36, 115, -83}, {116, 116, 214}, {117, 122, 60}, },
		{{36, 107, -92}, {108, 108, 215}, {109, 122, 60}, },
		{{36, 116, -89}, {117, 117, 216}, {118, 122, 60}, },
		{{36, 113, -45}, {114, 114, 217}, {115, 122, 60}, },
		{{36, 98, -137}, {99, 99, 218}, {100, 122, 60}, },
		{{36, 115, -83}, {116, 116, 219}, {117, 122, 60}, },
		{{36, 98, -137}, {99, 99, 220}, {100, 122, 60}, },
		{{36, 107, -92}, {108, 108, 221}, {109, 122, 60}, },
		{{36, 110, -35}, {111, 111, 222}, {112, 122, 60}, },
		{{36, 109, -38}, {110, 110, 223}, {111, 122, 60}, },
		{{36, 100, -37}, {101, 101, 224}, {102, 122, 60}, },
		{{36, 109, -38}, {110, 110, 225}, {111, 122, 60}, },
		{{36, 115, -83}, {116, 116, 226}, {117, 122, 60}, },
		{{36, 95, -9}, {97, 99, 60}, {100, 100, 227}, {101, 122, 60}, },
		{{36, 95, -9}, {97, 97, 228}, {98, 122, 60}, },
		{{36, 103, -47}, {104, 104, 229}, {105, 122, 60}, },
		{{0, 41, 230}, {42, 42, 231}, {43, 65535, 230}, },
		{},
		{{43, 43, 232}, {45, 45, 233}, {48, 57, 234}, },
		{},
		{{43, 57, -179}, },
		{},
		{{103, 103, 235}, },
		{{97, 97, 236}, },
		{{115, 115, 237}, },
		{{36, 113, -45}, {114, 114, 238}, {115, 122, 60}, },
		{{36, 100, -37}, {101, 101, 239}, {102, 122, 60}, },
		{{36, 106, -116}, {107, 107, 240}, {108, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 103, -47}, {104, 104, 241}, {105, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 114, -80}, {115, 115, 242}, {116, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 116, -89}, {117, 117, 243}, {118, 122, 60}, },
		{{36, 107, -92}, {108, 108, 244}, {109, 122, 60}, },
		{{36, 113, -45}, {114, 114, 245}, {115, 122, 60}, },
		{{36, 108, -149}, {109, 109, 246}, {110, 122, 60}, },
		{{36, 109, -38}, {110, 110, 247}, {111, 122, 60}, },
		{{36, 100, -37}, {101, 101, 248}, {102, 122, 60}, },
		{{36, 107, -92}, {108, 108, 249}, {109, 122, 60}, },
		{{36, 115, -83}, {116, 116, 250}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 100, -37}, {101, 101, 251}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 97, 252}, {98, 122, 60}, },
		{{36, 113, -45}, {114, 114, 253}, {115, 122, 60}, },
		{{36, 115, -83}, {116, 116, 254}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 116, -89}, {117, 117, 255}, {118, 122, 60}, },
		{{36, 117, -160}, {118, 118, 256}, {119, 122, 60}, },
		{{36, 113, -45}, {114, 114, 257}, {115, 122, 60}, },
		{{36, 116, -89}, {117, 117, 258}, {118, 122, 60}, },
		{{36, 95, -9}, {97, 97, 259}, {98, 122, 60}, },
		{{36, 100, -37}, {101, 101, 260}, {102, 122, 60}, },
		{{36, 104, -50}, {105, 105, 261}, {106, 122, 60}, },
		{{36, 113, -45}, {114, 114, 262}, {115, 122, 60}, },
		{{36, 115, -83}, {116, 116, 263}, {117, 122, 60}, },
		{{36, 104, -50}, {105, 105, 264}, {106, 122, 60}, },
		{{36, 104, -50}, {105, 105, 265}, {106, 122, 60}, },
		{{36, 103, -47}, {104, 104, 266}, {105, 122, 60}, },
		{{36, 100, -37}, {101, 101, 267}, {102, 122, 60}, },
		{{36, 118, -103}, {119, 119, 268}, {120, 122, 60}, },
		{{36, 114, -80}, {115, 115, 269}, {116, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 110, -35}, {111, 111, 270}, {112, 122, 60}, },
		{{36, 116, -89}, {117, 117, 271}, {118, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 115, -83}, {116, 116, 272}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{0, 65535, -177}, },
		{{0, 41, 175}, {42, 42, 231}, {43, 65535, -122}, },
		{{48, 57, 234}, },
		{{48, 57, 234}, },
		{{48, 57, 234}, {70, 70, 178}, {102, 102, 180}, },
		{{104, 104, 273}, },
		{{109, 109, 274}, },
		{},
		{{36, 95, -9}, {97, 97, 275}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 97, 276}, {98, 122, 60}, },
		{{36, 111, -87}, {112, 112, 277}, {113, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 107, -92}, {108, 108, 278}, {109, 122, 60}, },
		{{36, 100, -37}, {101, 101, 279}, {102, 122, 60}, },
		{{36, 108, -149}, {109, 109, 280}, {110, 122, 60}, },
		{{36, 110, -35}, {111, 111, 281}, {112, 122, 60}, },
		{{36, 99, -174}, {100, 100, 282}, {101, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 108, -149}, {109, 109, 283}, {110, 122, 60}, },
		{{36, 109, -38}, {110, 110, 284}, {111, 122, 60}, },
		{{36, 101, -42}, {102, 102, 285}, {103, 122, 60}, },
		{{36, 103, -47}, {104, 104, 286}, {105, 122, 60}, },
		{{36, 111, -87}, {112, 112, 287}, {113, 122, 60}, },
		{{36, 100, -37}, {101, 101, 288}, {102, 122, 60}, },
		{{36, 113, -45}, {114, 114, 289}, {115, 122, 60}, },
		{{36, 107, -92}, {108, 108, 290}, {109, 122, 60}, },
		{{36, 115, -83}, {116, 116, 291}, {117, 122, 60}, },
		{{36, 98, -137}, {99, 99, 292}, {100, 122, 60}, },
		{{36, 98, -137}, {99, 99, 293}, {100, 122, 60}, },
		{{36, 109, -38}, {110, 110, 294}, {111, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 295}, {98, 122, 60}, },
		{{36, 98, -137}, {99, 99, 296}, {100, 122, 60}, },
		{{36, 113, -45}, {114, 114, 297}, {115, 122, 60}, },
		{{36, 114, -80}, {115, 115, 298}, {116, 122, 60}, },
		{{36, 114, -80}, {115, 115, 299}, {116, 122, 60}, },
		{{36, 104, -50}, {105, 105, 300}, {106, 122, 60}, },
		{{36, 118, -103}, {119, 119, 301}, {120, 122, 60}, },
		{{36, 95, -9}, {97, 97, 302}, {98, 122, 60}, },
		{{36, 104, -50}, {105, 105, 303}, {106, 122, 60}, },
		{{116, 116, 304}, },
		{{101, 101, 305}, },
		{{36, 98, -137}, {99, 99, 306}, {100, 122, 60}, },
		{{36, 109, -38}, {110, 110, 307}, {111, 122, 60}, },
		{{36, 110, -35}, {111, 111, 308}, {112, 122, 60}, },
		{{36, 115, -83}, {116, 116, 309}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 110, -35}, {111, 111, 310}, {112, 122, 60}, },
		{{36, 109, -38}, {110, 110, 311}, {111, 122, 60}, },
		{{36, 114, -80}, {115, 115, 312}, {116, 122, 60}, },
		{{36, 100, -37}, {101, 101, 313}, {102, 122, 60}, },
		{{36, 98, -137}, {99, 99, 314}, {100, 122, 60}, },
		{{36, 95, -9}, {97, 97, 315}, {98, 122, 60}, },
		{{36, 110, -35}, {111, 111, 316}, {112, 122, 60}, },
		{{36, 114, -80}, {115, 115, 317}, {116, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 318}, {98, 122, 60}, },
		{{36, 115, -83}, {116, 116, 319}, {117, 122, 60}, },
		{{36, 100, -37}, {101, 101, 320}, {102, 122, 60}, },
		{{36, 115, -83}, {116, 116, 321}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 107, -92}, {108, 108, 322}, {109, 122, 60}, },
		{{36, 104, -50}, {105, 105, 323}, {106, 122, 60}, },
		{{36, 110, -35}, {111, 111, 324}, {112, 122, 60}, },
		{{36, 118, -103}, {119, 119, 325}, {120, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 100, -37}, {101, 101, 326}, {102, 122, 60}, },
		{{36, 109, -38}, {110, 110, 327}, {111, 122, 60}, },
		{{36, 107, -92}, {108, 108, 328}, {109, 122, 60}, },
		{{36, 107, -92}, {108, 108, 329}, {109, 122, 60}, },
		{{101, 101, 330}, },
		{{116, 116, 331}, },
		{{36, 115, -83}, {116, 116, 332}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 104, -50}, {105, 105, 333}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 109, -38}, {110, 110, 334}, {111, 122, 60}, },
		{{36, 104, -50}, {105, 105, 335}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 109, -38}, {110, 110, 336}, {111, 122, 60}, },
		{{36, 100, -37}, {101, 101, 337}, {102, 122, 60}, },
		{{36, 98, -137}, {99, 99, 338}, {100, 122, 60}, },
		{{36, 101, -42}, {102, 102, 339}, {103, 122, 60}, },
		{{36, 118, -103}, {119, 119, 340}, {120, 122, 60}, },
		{{36, 95, -9}, {97, 120, 60}, {121, 121, 341}, {122, 122, 60}, },
		{{36, 104, -50}, {105, 105, 342}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 100, -37}, {101, 101, 343}, {102, 122, 60}, },
		{{36, 104, -50}, {105, 105, 344}, {106, 122, 60}, },
		{{36, 109, -38}, {110, 110, 345}, {111, 122, 60}, },
		{{36, 109, -38}, {110, 110, 346}, {111, 122, 60}, },
		{{36, 104, -50}, {105, 105, 347}, {106, 122, 60}, },
		{{36, 109, -38}, {110, 110, 348}, {111, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 104, -50}, {105, 105, 349}, {106, 122, 60}, },
		{{36, 100, -37}, {101, 101, 350}, {102, 122, 60}, },
		{{120, 120, 351}, },
		{{101, 101, 352}, },
		{{36, 122, -9}, },
		{{36, 109, -38}, {110, 110, 353}, {111, 122, 60}, },
		{{36, 104, -50}, {105, 105, 354}, {106, 122, 60}, },
		{{36, 115, -83}, {116, 116, 355}, {117, 122, 60}, },
		{{36, 115, -83}, {116, 116, 356}, {117, 122, 60}, },
		{{36, 110, -35}, {111, 111, 357}, {112, 122, 60}, },
		{{36, 100, -37}, {101, 101, 358}, {102, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 104, -50}, {105, 105, 359}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 360}, {98, 122, 60}, },
		{{36, 99, -174}, {100, 100, 361}, {101, 122, 60}, },
		{{36, 109, -38}, {110, 110, 362}, {111, 122, 60}, },
		{{36, 117, -160}, {118, 118, 363}, {119, 122, 60}, },
		{{36, 104, -50}, {105, 105, 364}, {106, 122, 60}, },
		{{36, 115, -83}, {116, 116, 365}, {117, 122, 60}, },
		{{36, 115, -83}, {116, 116, 366}, {117, 122, 60}, },
		{{36, 109, -38}, {110, 110, 367}, {111, 122, 60}, },
		{{36, 122, -9}, },
		{{99, 99, 368}, },
		{{114, 114, 369}, },
		{{36, 115, -83}, {116, 116, 370}, {117, 122, 60}, },
		{{36, 115, -83}, {116, 116, 371}, {117, 122, 60}, },
		{{36, 110, -35}, {111, 111, 372}, {112, 122, 60}, },
		{{36, 114, -80}, {115, 115, 373}, {116, 122, 60}, },
		{{36, 101, -42}, {102, 102, 374}, {103, 122, 60}, },
		{{36, 104, -50}, {105, 105, 375}, {106, 122, 60}, },
		{{36, 115, -83}, {116, 116, 376}, {117, 122, 60}, },
		{{36, 113, -45}, {114, 114, 377}, {115, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 117, -160}, {118, 118, 378}, {119, 122, 60}, },
		{{36, 110, -35}, {111, 111, 379}, {112, 122, 60}, },
		{{36, 95, -9}, {97, 121, 60}, {122, 122, 380}, },
		{{36, 98, -137}, {99, 99, 381}, {100, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 117, -160}, {118, 118, 382}, {119, 122, 60}, },
		{{101, 101, 383}, },
		{{48, 57, 384}, },
		{{36, 122, -9}, },
		{{36, 110, -35}, {111, 111, 385}, {112, 122, 60}, },
		{{36, 113, -45}, {114, 114, 386}, {115, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 109, -38}, {110, 110, 387}, {111, 122, 60}, },
		{{36, 98, -137}, {99, 99, 388}, {100, 122, 60}, },
		{{36, 113, -45}, {114, 114, 389}, {115, 122, 60}, },
		{{36, 110, -35}, {111, 111, 390}, {112, 122, 60}, },
		{{36, 106, -116}, {107, 107, 391}, {108, 122, 60}, },
		{{36, 100, -37}, {101, 101, 392}, {102, 122, 60}, },
		{{36, 103, -47}, {104, 104, 393}, {105, 122, 60}, },
		{{36, 110, -35}, {111, 111, 394}, {112, 122, 60}, },
		{{112, 112, 395}, },
		{{48, 57, 384}, },
		{{36, 113, -45}, {114, 114, 396}, {115, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 117, -160}, {118, 118, 397}, {119, 122, 60}, },
		{{36, 103, -47}, {104, 104, 398}, {105, 122, 60}, },
		{{36, 95, -9}, {97, 97, 399}, {98, 122, 60}, },
		{{36, 106, -116}, {107, 107, 400}, {108, 122, 60}, },
		{{36, 100, -37}, {101, 101, 401}, {102, 122, 60}, },
		{{36, 99, -174}, {100, 100, 402}, {101, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 106, -116}, {107, 107, 403}, {108, 122, 60}, },
		{{116, 116, 404}, },
		{{36, 122, -9}, },
		{{36, 110, -35}, {111, 111, 405}, {112, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 120, -320}, {121, 121, 406}, {122, 122, 60}, },
		{{36, 100, -37}, {101, 101, 407}, {102, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 100, -37}, {101, 101, 408}, {102, 122, 60}, },
		{{105, 105, 409}, },
		{{36, 106, -116}, {107, 107, 410}, {108, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{111, 111, 411}, },
		{{36, 100, -37}, {101, 101, 412}, {102, 122, 60}, },
		{{110, 110, 413}, },
		{{36, 122, -9}, },
		{},
    };

    private static int[][] accept =
    {
		{-1, 91, 91, 91, 91, -1, -1, 85, 70, 67, 64, 60, 61, 82, 80, 54, 81, 63, 83, 88, 88, 62, 57, 75, 66, 73, -1, 85, 58, 59, 69, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 55, 68, 56, 72, -1, 90, 85, 85, 85, 85, 85, -1, -1, 92, -1, 88, 88, -1, -1, 65, 77, 76, 71, 74, 78, -1, -1, -1, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 35, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 51, 85, 85, 85, 85, 85, 84, -1, -1, 92, 89, 88, 88, 88, 79, -1, -1, -1, 85, 85, 85, 85, 85, 85, 85, 85, 27, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 17, 85, 85, 85, 85, 40, 43, 85, 85, 85, 44, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, -1, 92, -1, 89, -1, 89, -1, -1, -1, 85, 85, 85, 14, 25, 85, 16, 85, 28, 29, 85, 85, 85, 85, 85, 85, 85, 85, 33, 34, 85, 85, 85, 85, 18, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 12, 85, 53, -1, -1, -1, -1, 89, -1, -1, 86, 85, 85, 85, 26, 10, 85, 85, 85, 85, 85, 85, 1, 19, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 85, 15, 85, 85, 85, 85, 49, 85, 85, 85, 85, -1, -1, 85, 85, 85, 85, 20, 85, 85, 85, 85, 85, 85, 85, 85, 2, 85, 85, 85, 85, 3, 45, 85, 6, 85, 85, 50, 85, 85, 85, 85, -1, -1, 85, 13, 85, 30, 85, 85, 22, 85, 85, 85, 85, 85, 85, 85, 5, 85, 85, 85, 85, 85, 85, 21, 85, 85, -1, -1, 0, 85, 85, 85, 85, 85, 85, 38, 85, 41, 85, 85, 85, 85, 85, 85, 85, 85, 9, -1, -1, 85, 85, 85, 85, 85, 11, 85, 85, 4, 85, 85, 85, 85, 8, 85, -1, -1, 24, 85, 85, 23, 36, 85, 85, 85, 85, 85, 85, 85, 85, -1, 86, 85, 32, 85, 85, 85, 85, 85, 85, 48, 85, -1, 31, 85, 39, 85, 85, 47, 7, 85, -1, 85, 42, 46, 52, -1, 85, -1, 37, 86, },

    };

    public static class State
    {
        public final static State INITIAL = new State(0);

        private int id;

        private State(int id)
        {
            this.id = id;
        }

        public int id()
        {
            return id;
        }
    }
}
