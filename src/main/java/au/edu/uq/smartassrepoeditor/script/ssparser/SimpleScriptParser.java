/* Generated By:JavaCC: Do not edit this line. SimpleScriptParser.java */
package au.edu.uq.smartassrepoeditor.script.ssparser;

import java.util.Vector;

@SuppressWarnings("unchecked")
public class SimpleScriptParser implements SimpleScriptParserConstants {
        Vector<ScriptLine> lines = new Vector<ScriptLine>();
        VarCreation tmp_var;
        Vector<VarArg> tmp_args;

        String tmp_section_name;
        String tmp_var_type;
        String tmp_file_path;

        public class ScriptLine {}

        public class VarArg {}
        
        public class StrArg extends VarArg {public String str;}

        public class DSArg extends VarArg {public String dsname; public int fieldno = 0;}

        public class VarCreation extends ScriptLine {
                public String moduleName, varName;
                public Vector<VarArg> args = new Vector<VarArg>();
        }

        public class SectionAccess extends ScriptLine {
                public String varName;
                public String sectionName;
        }

        public class DSCreation extends ScriptLine {
                final public static int SEQUENTIAL = 0;
                final public static int RANDOM = 1;
                final public static int RANDOM_UNIQUE = 2;
                final public static int BYKEY = 3;
                final public static int BYLINENO = 4;

                public String name;
                public String type;
                public String path;
                public int kind;
                public String key;
                public int num;
        }


  public Vector<ScriptLine> parse() throws ParseException {
	  Script();
	  return lines;
  }

  final public void Script() throws ParseException {
    if (jj_2_1(3)) {
      jj_consume_token(SPACES);
    } else {
      ;
    }
    Line();
    if (jj_2_2(3)) {
      jj_consume_token(SPACES);
    } else {
      ;
    }
    label_1:
    while (true) {
      if (jj_2_3(3)) {
        ;
      } else {
        break label_1;
      }
      jj_consume_token(LINEDELIMITER);
      if (jj_2_4(3)) {
        jj_consume_token(SPACES);
      } else {
        ;
      }
      Line();
      if (jj_2_5(3)) {
        jj_consume_token(SPACES);
      } else {
        ;
      }
    }
    if (jj_2_6(3)) {
      jj_consume_token(LINEDELIMITER);
    } else {
      ;
    }
    if (jj_2_7(3)) {
      jj_consume_token(SPACES);
    } else {
      ;
    }
    jj_consume_token(0);
  }

  final public void Line() throws ParseException {
               ScriptLine line;
    if (jj_2_8(3)) {
      line = VarCreation();
    } else if (jj_2_9(3)) {
      line = VarAccessSection();
    } else if (jj_2_10(3)) {
      line = DSCreation();
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
         lines.add(line);
  }

  final public ScriptLine VarCreation() throws ParseException {
                            VarCreation line; tmp_args = new Vector<VarArg>();
    line = VarDecl();
    if (jj_2_11(3)) {
      VarArgsCreation();
    } else {
      ;
    }
                                                 {if (true) return line;}
    throw new Error("Missing return statement in function");
  }

  final public ScriptLine VarAccessSection() throws ParseException {
                                 Token var, section;
    var = jj_consume_token(NAME);
    jj_consume_token(1);
    section = jj_consume_token(NAME);
         SectionAccess line = new SectionAccess();
        line.varName = var.image;
        line.sectionName = section.image;
        {if (true) return line;}
    throw new Error("Missing return statement in function");
  }

  final public ScriptLine DSCreation() throws ParseException {
                           Token dstype, dsname, dspath, kind = null, subkind = null, num = null, key=null;
    jj_consume_token(DSLANGLE);
    dstype = jj_consume_token(NAME);
    jj_consume_token(DSRANGLE);
    jj_consume_token(SPACES);
    jj_consume_token(DSLANGLE);
    dsname = jj_consume_token(NAME);
    jj_consume_token(DSRANGLE);
    if (jj_2_12(3)) {
      jj_consume_token(SPACES);
    } else {
      ;
    }
    jj_consume_token(OPENDSBRACKET);
    jj_consume_token(DEFQUOTE);
    dspath = jj_consume_token(DEFNOQUOTES);
    jj_consume_token(DEFENDQUOTE);
    if (jj_2_23(3)) {
      jj_consume_token(2);
      if (jj_2_13(3)) {
        jj_consume_token(SPACES);
      } else {
        ;
      }
      if (jj_2_19(3)) {
        kind = jj_consume_token(SEQUENTIAL);
      } else if (jj_2_20(3)) {
        kind = jj_consume_token(RANDOM);
        if (jj_2_14(3)) {
          jj_consume_token(SPACES);
          subkind = jj_consume_token(UNIQUE);
        } else {
          ;
        }
      } else if (jj_2_21(3)) {
        kind = jj_consume_token(KEY);
        if (jj_2_15(3)) {
          jj_consume_token(SPACES);
        } else {
          ;
        }
        jj_consume_token(2);
        if (jj_2_16(3)) {
          jj_consume_token(SPACES);
        } else {
          ;
        }
        num = jj_consume_token(NUMBERS);
        if (jj_2_17(3)) {
          jj_consume_token(SPACES);
        } else {
          ;
        }
        jj_consume_token(2);
        if (jj_2_18(3)) {
          jj_consume_token(SPACES);
        } else {
          ;
        }
        jj_consume_token(DEFQUOTE);
        key = jj_consume_token(DEFNOQUOTES);
        jj_consume_token(DEFENDQUOTE);
      } else if (jj_2_22(3)) {
        kind = jj_consume_token(NUMBERS);
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    } else {
      ;
    }
    if (jj_2_24(3)) {
      jj_consume_token(SPACES);
    } else {
      ;
    }
    jj_consume_token(3);
                 DSCreation line = new DSCreation();
                        line.name = dsname.image;
                        line.type = dstype.image;
                        line.path = dspath.image;
                        if(kind==null || kind.image.equals("RANDOM")) {
                                if(kind==null || subkind==null)
                                        line.kind = DSCreation.RANDOM;
                                else
                                        line.kind = DSCreation.RANDOM_UNIQUE;
                        } else if(kind.image.equals("SEQUENTIAL"))
                                line.kind = DSCreation.SEQUENTIAL;
                        else if(kind.image.equals("KEY")) {
                                line.kind = DSCreation.BYKEY;
                                line.num = Integer.parseInt(num.image);
                                line.key = key.image;
                        } else {
                                line.kind = DSCreation.BYLINENO;
                                line.num = Integer.parseInt(kind.image);
                        }
                        {if (true) return line;}
    throw new Error("Missing return statement in function");
  }

  final public VarCreation VarDecl() throws ParseException {
                         Token type, name;
    type = jj_consume_token(NAME);
    jj_consume_token(SPACES);
    name = jj_consume_token(NAME);
         tmp_var = new VarCreation();
        tmp_var.moduleName = type.image;
        tmp_var.varName = name.image;
        {if (true) return tmp_var;}
    throw new Error("Missing return statement in function");
  }

  final public void VarArgsCreation() throws ParseException {
    jj_consume_token(OPENBRACKET);
    if (jj_2_25(3)) {
      jj_consume_token(SPACES);
    } else {
      ;
    }
    Arg();
    if (jj_2_26(3)) {
      jj_consume_token(SPACES);
    } else {
      ;
    }
    label_2:
    while (true) {
      if (jj_2_27(3)) {
        ;
      } else {
        break label_2;
      }
      jj_consume_token(COMMA);
      if (jj_2_28(3)) {
        jj_consume_token(SPACES);
      } else {
        ;
      }
      Arg();
      if (jj_2_29(3)) {
        jj_consume_token(SPACES);
      } else {
        ;
      }
    }
    jj_consume_token(CLOSEBRACKET);

  }

  final public void Arg() throws ParseException {
    if (jj_2_30(3)) {
      StringArg();
    } else if (jj_2_31(3)) {
      DSArg();
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void StringArg() throws ParseException {
                    Token str;
    if (jj_2_32(3)) {
      jj_consume_token(QUOTE);
      str = jj_consume_token(NOQUOTES);
      jj_consume_token(ENDQUOTE);
    } else if (jj_2_33(3)) {
      str = jj_consume_token(NOSTRING);
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
          StrArg arg = new StrArg();
        arg.str = str.image;
        tmp_var.args.add(arg);
  }

  final public void DSArg() throws ParseException {
                Token ds, fieldno = null;
    jj_consume_token(PARAMDSLANGLE);
    ds = jj_consume_token(NAME);
    if (jj_2_34(3)) {
      jj_consume_token(DSSHARP);
      fieldno = jj_consume_token(NUMBERS);
    } else {
      ;
    }
    jj_consume_token(PARAMDSRANGLE);
        DSArg arg = new DSArg();
        arg.dsname = ds.image;
        if(fieldno!=null)
                arg.fieldno = Integer.parseInt(fieldno.image);
        tmp_var.args.add(arg);
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  final private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  final private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  final private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  final private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  final private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  final private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  final private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  final private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  final private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  final private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_13(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  final private boolean jj_2_14(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_14(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(13, xla); }
  }

  final private boolean jj_2_15(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_15(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(14, xla); }
  }

  final private boolean jj_2_16(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_16(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(15, xla); }
  }

  final private boolean jj_2_17(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_17(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(16, xla); }
  }

  final private boolean jj_2_18(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_18(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(17, xla); }
  }

  final private boolean jj_2_19(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_19(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(18, xla); }
  }

  final private boolean jj_2_20(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_20(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(19, xla); }
  }

  final private boolean jj_2_21(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_21(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(20, xla); }
  }

  final private boolean jj_2_22(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_22(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(21, xla); }
  }

  final private boolean jj_2_23(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_23(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(22, xla); }
  }

  final private boolean jj_2_24(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_24(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(23, xla); }
  }

  final private boolean jj_2_25(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_25(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(24, xla); }
  }

  final private boolean jj_2_26(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_26(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(25, xla); }
  }

  final private boolean jj_2_27(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_27(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(26, xla); }
  }

  final private boolean jj_2_28(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_28(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(27, xla); }
  }

  final private boolean jj_2_29(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_29(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(28, xla); }
  }

  final private boolean jj_2_30(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_30(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(29, xla); }
  }

  final private boolean jj_2_31(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_31(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(30, xla); }
  }

  final private boolean jj_2_32(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_32(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(31, xla); }
  }

  final private boolean jj_2_33(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_33(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(32, xla); }
  }

  final private boolean jj_2_34(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_34(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(33, xla); }
  }

  final private boolean jj_3_18() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_26() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_6() {
    if (jj_scan_token(LINEDELIMITER)) return true;
    return false;
  }

  final private boolean jj_3R_4() {
    if (jj_3R_11()) return true;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_scan_token(LINEDELIMITER)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_4()) jj_scanpos = xsp;
    if (jj_3R_3()) return true;
    return false;
  }

  final private boolean jj_3R_10() {
    if (jj_scan_token(PARAMDSLANGLE)) return true;
    if (jj_scan_token(NAME)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_34()) jj_scanpos = xsp;
    if (jj_scan_token(PARAMDSRANGLE)) return true;
    return false;
  }

  final private boolean jj_3_17() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_25() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_12() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3R_11() {
    if (jj_scan_token(NAME)) return true;
    if (jj_scan_token(SPACES)) return true;
    if (jj_scan_token(NAME)) return true;
    return false;
  }

  final private boolean jj_3_32() {
    if (jj_scan_token(QUOTE)) return true;
    if (jj_scan_token(NOQUOTES)) return true;
    if (jj_scan_token(ENDQUOTE)) return true;
    return false;
  }

  final private boolean jj_3R_9() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_32()) {
    jj_scanpos = xsp;
    if (jj_3_33()) return true;
    }
    return false;
  }

  final private boolean jj_3_31() {
    if (jj_3R_10()) return true;
    return false;
  }

  final private boolean jj_3_10() {
    if (jj_3R_6()) return true;
    return false;
  }

  final private boolean jj_3R_7() {
    if (jj_scan_token(OPENBRACKET)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_25()) jj_scanpos = xsp;
    if (jj_3R_8()) return true;
    xsp = jj_scanpos;
    if (jj_3_26()) jj_scanpos = xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_27()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(CLOSEBRACKET)) return true;
    return false;
  }

  final private boolean jj_3_16() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3R_8() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_30()) {
    jj_scanpos = xsp;
    if (jj_3_31()) return true;
    }
    return false;
  }

  final private boolean jj_3_30() {
    if (jj_3R_9()) return true;
    return false;
  }

  final private boolean jj_3_14() {
    if (jj_scan_token(SPACES)) return true;
    if (jj_scan_token(UNIQUE)) return true;
    return false;
  }

  final private boolean jj_3R_5() {
    if (jj_scan_token(NAME)) return true;
    if (jj_scan_token(1)) return true;
    if (jj_scan_token(NAME)) return true;
    return false;
  }

  final private boolean jj_3_15() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_9() {
    if (jj_3R_5()) return true;
    return false;
  }

  final private boolean jj_3_29() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_5() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_22() {
    if (jj_scan_token(NUMBERS)) return true;
    return false;
  }

  final private boolean jj_3_21() {
    if (jj_scan_token(KEY)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_15()) jj_scanpos = xsp;
    if (jj_scan_token(2)) return true;
    xsp = jj_scanpos;
    if (jj_3_16()) jj_scanpos = xsp;
    if (jj_scan_token(NUMBERS)) return true;
    return false;
  }

  final private boolean jj_3_20() {
    if (jj_scan_token(RANDOM)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_14()) jj_scanpos = xsp;
    return false;
  }

  final private boolean jj_3_34() {
    if (jj_scan_token(DSSHARP)) return true;
    if (jj_scan_token(NUMBERS)) return true;
    return false;
  }

  final private boolean jj_3_19() {
    if (jj_scan_token(SEQUENTIAL)) return true;
    return false;
  }

  final private boolean jj_3_28() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_13() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_33() {
    if (jj_scan_token(NOSTRING)) return true;
    return false;
  }

  final private boolean jj_3_24() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_8() {
    if (jj_3R_4()) return true;
    return false;
  }

  final private boolean jj_3R_3() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_8()) {
    jj_scanpos = xsp;
    if (jj_3_9()) {
    jj_scanpos = xsp;
    if (jj_3_10()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3_23() {
    if (jj_scan_token(2)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_13()) jj_scanpos = xsp;
    xsp = jj_scanpos;
    if (jj_3_19()) {
    jj_scanpos = xsp;
    if (jj_3_20()) {
    jj_scanpos = xsp;
    if (jj_3_21()) {
    jj_scanpos = xsp;
    if (jj_3_22()) return true;
    }
    }
    }
    return false;
  }

  final private boolean jj_3_11() {
    if (jj_3R_7()) return true;
    return false;
  }

  final private boolean jj_3_7() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3_27() {
    if (jj_scan_token(COMMA)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_28()) jj_scanpos = xsp;
    if (jj_3R_8()) return true;
    xsp = jj_scanpos;
    if (jj_3_29()) jj_scanpos = xsp;
    return false;
  }

  final private boolean jj_3_4() {
    if (jj_scan_token(SPACES)) return true;
    return false;
  }

  final private boolean jj_3R_6() {
    if (jj_scan_token(DSLANGLE)) return true;
    if (jj_scan_token(NAME)) return true;
    if (jj_scan_token(DSRANGLE)) return true;
    return false;
  }

  public SimpleScriptParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[0];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_0();
      jj_la1_1();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {};
   }
   private static void jj_la1_1() {
      jj_la1_1 = new int[] {};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[34];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public SimpleScriptParser(java.io.InputStream stream) {
     this(stream, null);
  }
  public SimpleScriptParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SimpleScriptParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public SimpleScriptParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SimpleScriptParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public SimpleScriptParser(SimpleScriptParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(SimpleScriptParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[33];
    for (int i = 0; i < 33; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 0; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 33; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 34; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
            case 10: jj_3_11(); break;
            case 11: jj_3_12(); break;
            case 12: jj_3_13(); break;
            case 13: jj_3_14(); break;
            case 14: jj_3_15(); break;
            case 15: jj_3_16(); break;
            case 16: jj_3_17(); break;
            case 17: jj_3_18(); break;
            case 18: jj_3_19(); break;
            case 19: jj_3_20(); break;
            case 20: jj_3_21(); break;
            case 21: jj_3_22(); break;
            case 22: jj_3_23(); break;
            case 23: jj_3_24(); break;
            case 24: jj_3_25(); break;
            case 25: jj_3_26(); break;
            case 26: jj_3_27(); break;
            case 27: jj_3_28(); break;
            case 28: jj_3_29(); break;
            case 29: jj_3_30(); break;
            case 30: jj_3_31(); break;
            case 31: jj_3_32(); break;
            case 32: jj_3_33(); break;
            case 33: jj_3_34(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}