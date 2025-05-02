package Semant;

import Symbol.Symbol;
import Translate.Exp;
import Translate.Frag;
import Translate.Level;
import Translate.Translate;
import Types.NAME;
import Types.RECORD;
import Types.Type;



public class Semant {
    Env env;
    Translate translate;
    Level level;
    public Semant(Translate translate, ErrorMsg.ErrorMsg err) {
      this(new Env(err), translate, new Level(translate.frame));
    }
    Semant(Env e, Translate t, Level l) {
      env = e;
      translate = t;
      level = l;
    }

    public Semant(ErrorMsg.ErrorMsg err) {
        env = new Env(err);
    }
  
    public Frag transProg(Absyn.Exp exp) {
    //   new FindEscape.FindEscape(exp);
      level = new Level(level, Symbol.symbol("main"), null);
      ExpTy body = transExp(exp);
      translate.procEntryExit(level, body.exp);
      return translate.getResult();
    }
  
    private void error(int pos, String msg) {
      env.errorMsg.error(pos, msg);
    }

    static final Types.VOID VOID = new Types.VOID();
    static final Types.INT INT = new Types.INT();
    static final Types.STRING STRING = new Types.STRING();
    static final Types.NIL NIL = new Types.NIL();

    private Exp checkInt(ExpTy et, int pos) {
        if (!INT.coerceTo(et.ty)) {
            error(pos, "integer required");
            return null;
        }
        return et.exp;
    }

    private Exp checkComparable(ExpTy et, int pos){
        Type exp = et.ty.actual();
        if (!(exp instanceof Types.INT || exp instanceof Types.STRING || exp instanceof Types.NIL)) {
            error(pos, "CompError");
        }

        return et.exp;
    }

    private Exp checkOrderable(ExpTy et, int pos) {
        Type exp = et.ty.actual();
        if(!(exp instanceof Types.INT || exp instanceof Types.STRING)){
            error(pos,"OrderError");
        }
        return et.exp;
    }

    public ExpTy transExp(Absyn.Exp e) {
        ExpTy result;
        System.out.println(e == null ? "null" : e.getClass());
        if (e instanceof Absyn.ArrayExp arrayExp) {
          result = transExp(arrayExp);
      } else if (e instanceof Absyn.AssignExp assignExp) {
          result = transExp(assignExp);
      } else if (e instanceof Absyn.CallExp callExp) {
          result = transExp(callExp);
      } else if (e instanceof Absyn.CommaExpression commaExpression) {
          result = transExp(commaExpression);
      } else if (e instanceof Absyn.EmptyExpression emptyExpression) {
          result = transExp(emptyExpression);
      } else if (e instanceof Absyn.ForExp forExp) {
          result = transExp(forExp);
      } else if (e instanceof Absyn.FuncExpression funcExpression) {
          result = transExp(funcExpression);
      } else if (e instanceof Absyn.IdExp idExp) {
          result = transExp(idExp);
      } else if (e instanceof Absyn.IfExp ifExp) {
          result = transExp(ifExp);
      } else if (e instanceof Absyn.IntExp intExp) {
          result = transExp(intExp);
      } else if (e instanceof Absyn.LetExp letExp) {
          result = transExp(letExp);
      } else if (e instanceof Absyn.MinusMinusExpression minusMinusExpression) {
          result = transExp(minusMinusExpression);
      } else if (e instanceof Absyn.OpExp opExp) {
          result = transExp(opExp);
      } else if (e instanceof Absyn.ParenExpression parenExpression) {
          result = transExp(parenExpression);
      } else if (e instanceof Absyn.PlusPlusExpression plusPlusExpression) {
          result = transExp(plusPlusExpression);
      } else if (e instanceof Absyn.RecordExp recordExp) {
          result = transExp(recordExp);
      } else if (e instanceof Absyn.SeqExp seqExp) {
          result = transExp(seqExp);
      } else if (e instanceof Absyn.SizeofExpression sizeofExpression) {
          result = transExp(sizeofExpression);
      } else if (e instanceof Absyn.StringLit stringLit) {
          result = transExp(stringLit);
      } else if (e instanceof Absyn.UnaryExpression unaryExpression) {
          result = transExp(unaryExpression);
      } else if (e instanceof Absyn.VarExp varExp) {
          result = transExp(varExp);
      } else if (e instanceof Absyn.WhileExp whileExp) {
          result = transExp(whileExp);
      } else {
          result = null;
          System.out.println("Error :(");
      }
        return result;
    }

    public ExpTy transExp(Absyn.ArrayExp e) {
        Types.Type arrayType = (Types.Type) env.tenv.get(e.typ);

        if (arrayType == null || !(arrayType instanceof Types.ARRAY)) {
            error(e.pos, "Undefined or invalid array type: " + e.typ);
            return new ExpTy(null, NIL);
        }

        ExpTy sizeExp = transExp(e.size);
        checkInt(sizeExp, e.size.pos);

        ExpTy initExp = transExp(e.init);
        Types.Type elementType = ((Types.ARRAY) arrayType).element;
        if (!initExp.ty.coerceTo(elementType)) {
            error(e.init.pos, "Array initialization type mismatch");
        }

        return new ExpTy(null, arrayType);
    }

    public ExpTy transExp(Absyn.AssignExp e) {
        ExpTy varExp = transVar(e.var);
        ExpTy exp = transExp(e.exp);

        if (!exp.ty.coerceTo(varExp.ty)) {
            error(e.pos, "Type mismatch in assignment");
        }

        return new ExpTy(null, VOID);
    }

    public ExpTy transExp(Absyn.CallExp e) {
        Entry funcall = (Entry) env.venv.get(((Absyn.IdExp) e.func).name);
        if (funcall instanceof FunEntry) {
            FunEntry f = (FunEntry)funcall;
            Absyn.Exp arg = e.args.get(0);
            int count = 0;
            while (arg != null) {
                ExpTy result = transExp(arg);
//                if (!(result.ty.coerceTo())) no arg list in funentry how do i get the args?
                count++;
                arg = e.args.get(count);
            }
            return new ExpTy(null, f.result);
        }
        error(0, "fun undeclared");
        return new ExpTy(null, VOID);
    }

    public ExpTy transExp(Absyn.CommaExpression e) {
        ExpTy result = null;
        for (Absyn.Exp exp : e.expressions) {
            result = transExp(exp);
        }
        return result;
    }

    public ExpTy transExp(Absyn.EmptyExpression e) {
        return new ExpTy(null, NIL);
    }

    public ExpTy transExp(Absyn.ForExp e) {
        ExpTy lowerExp = transExp(e.lo);
        if(checkInt(lowerExp, e.lo.pos) == null) {
            error(e.pos, "Lower bound not an integer");
            return new ExpTy(null, VOID);
        }

        ExpTy higherExp = transExp(e.hi);
        if(checkInt(lowerExp, e.hi.pos) == null) {
            error(e.pos, "Lower bound not an integer");
            return new ExpTy(null, VOID);
        }

        env.venv.beginScope();

        // Frame.Access loopVarAccess = root_level.allocLocal(true);
        // env.venv.put(e.var, new VarEntry(INT, loopVarAccess));

        ExpTy bodyExp = transExp(e.body);

        env.venv.endScope();

        return new ExpTy(null, VOID);
    }

    public ExpTy transExp(Absyn.FuncExpression e) {
        return null;
    }

    public ExpTy intExp(Absyn.IntExp e) {
        return new ExpTy(null, INT);
    }

    public ExpTy transExp(Absyn.MinusMinusExpression e) {
        ExpTy exp = transExp(e.exp);
        if (!INT.coerceTo(exp.ty)) {
            error(e.pos, "Operand of '--' must be an integer");
            return new ExpTy(null, VOID);
        }
        return new ExpTy(null, INT);
    }

    public ExpTy transExp(Absyn.StringLit e) {
        return new ExpTy(null, STRING);
    }

    public ExpTy transExp(Absyn.OpExp e) {
        ExpTy left = transExp(e.left);
        ExpTy right = transExp(e.right);
        if (e.oper >= 0 && e.oper <= 3) {
          checkInt(left, e.left.pos);
          checkInt(right, e.right.pos);
          return new ExpTy(null, INT);
      } else if (e.oper == 4 || e.oper == 5) {
          checkComparable(left, e.left.pos);
          checkComparable(right, e.right.pos);
          if (!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty)) {
              error(0, "Types are not the same, can't be compared");
          }
          return new ExpTy(null, INT);
      } else if (e.oper >= 6 && e.oper <= 9) {
          checkOrderable(left, e.left.pos);
          checkOrderable(right, e.right.pos);
          if (!left.ty.coerceTo(right.ty) && !right.ty.coerceTo(left.ty)) {
              error(0, "Types are not the same, can't be ordered");
          }
          return new ExpTy(null, INT);
      }
        System.err.println("Something went wrong in OpExp switch statement");
        return null;
    }

    public ExpTy transExp(Absyn.ParenExpression e) {
        return transExp(e.exp);
    }

    public ExpTy transExp(Absyn.PlusPlusExpression e) {
        ExpTy exp = transExp(e.exp);
        if (!INT.coerceTo(exp.ty)) {
            error(e.pos, "Operand of '++' must be an integer");
            return new ExpTy(null, VOID);
        }
        return new ExpTy(null, INT);
    }

    public ExpTy transExp(Absyn.RecordExp e) {
         NAME n = (NAME)env.tenv.get(e.typ);
         Type recordty = n.actual();

         if (recordty instanceof RECORD) {
            Absyn.Efield field = e.fields.get(0);
             int count = 0;
             while (field != null) {
                ExpTy result = transExp(field.exp);
                if (!(result.ty.coerceTo(recordty))) {
                    error(0, "field didnt match record type");
                }
                count++;
                field = e.fields.get(count);
             }
             return new ExpTy(null, recordty);
         }
         error(e.pos, "soemthing went wrong with records");
         return new ExpTy(null, recordty);
    }

    public ExpTy transExp(Absyn.SeqExp e) {
        ExpTy result = null;

        for (Absyn.Exp exp : e.list) {
            result = transExp(exp);
        }

        return result != null ? result : new ExpTy(null, VOID);
    }

    public ExpTy transExp(Absyn.SizeofExpression e) {
        ExpTy exp = transExp(e.exp);
        if (exp == null || exp.ty == null) {
            error(e.pos, "Invalid expression for sizeof");
            return new ExpTy(null, INT);
        }

        return new ExpTy(null, INT);
    }

    public ExpTy transExp(Absyn.UnaryExpression e) {
        ExpTy operand = transExp(e.exp);

        if (e.op == Absyn.UnaryExpression.PLUS || e.op == Absyn.UnaryExpression.MINUS) {
          if (!INT.coerceTo(operand.ty)) {
              error(e.pos, "Operand must be an integer");
              return new ExpTy(null, VOID);
          }
          return new ExpTy(null, INT);
      } else if (e.op == Absyn.UnaryExpression.NOT || e.op == Absyn.UnaryExpression.COMPLEMENT) {
          if (!INT.coerceTo(operand.ty)) {
              error(e.pos, "Operand must be an integer");
              return new ExpTy(null, VOID);
          }
          return new ExpTy(null, INT);
      } else if (e.op == Absyn.UnaryExpression.SIZEOF) {
          return new ExpTy(null, INT);
      } else if (e.op == Absyn.UnaryExpression.ADDRESS_OF) {
          return new ExpTy(null, VOID);
      } else {
          error(e.pos, "Unknown unary operator");
          return new ExpTy(null, VOID);
      }
    }

    public ExpTy transExp(Absyn.WhileExp e) {
        ExpTy condition = transExp(e.test);
        checkInt(condition, e.test.pos);

        env.venv.beginScope();
        ExpTy body = transExp(e.body);
        env.venv.endScope();

        return new ExpTy(null, VOID);
    }

    public ExpTy transExp(Absyn.VarExp e) {
        return transVar(e.var);
    }

    public ExpTy transDec(Absyn.Decl d) {
        ExpTy result;
        if (d instanceof Absyn.FunctionDeclaration functionDeclaration) {
          result = transDec(functionDeclaration);
      } else if (d instanceof Absyn.StructDeclaration structDeclaration) {
          result = transDec(structDeclaration);
      } else if (d instanceof Absyn.VarDeclaration varDeclaration) {
          result = transDec(varDeclaration);
      } else if (d instanceof Absyn.EnumDeclaration enumDeclaration) {
          result = transDec(enumDeclaration);
      } else if (d instanceof Absyn.EmptyDeclaration emptyDeclaration) {
          result = transDec(emptyDeclaration);
      } else {
          result = null;
          System.err.println("transDec error;" + (d == null ? "null" : d.getClass()));
      }
        return result;
    }

    public ExpTy transDec(Absyn.FunctionDeclaration d) {
//        Types.Type result = transTy(d.result);
//        Types.RECORD formals = transTypeFields(d.params);
//        env.venv.put(d.name, new FunEntry(formals.result));
//        env.venv.beginScope();
//        for(p=dec.params; p!=null; p=p.tail){
//            env.venv.put(p.name, new VarEntry((Types.Type)env.tenv.get(p.typ)));
//        }
//        transExp(d.body);
//        env.venv.endScope();

        return null;
    }

    public ExpTy transDec(Absyn.StructDeclaration d) {
        //env.tenv.put(d.name, transTy(d.ty));
        return null;
    }

    public ExpTy transDec(Absyn.VarDeclaration d) {
        ExpTy temp = transExp(d.init);
        Type type = null;

        return null;
    }

    public ExpTy transDec(Absyn.EnumDeclaration d) {
        int val = 0;

        for (Absyn.Enumerator enumerator : d.body) {
            Symbol constant = Symbol.symbol(enumerator.name);

            if (env.venv.get(constant) != null) {
                error(d.pos, "Duplicate enum constant: " + constant);
            } else {
                // env.venv.put(constant, new VarEntry(INT, root_level.allocLocal(false)));
            }

            val++;
        }

        return new ExpTy(null, VOID);
    }

    public ExpTy transDec(Absyn.EmptyDeclaration d) {
        return new ExpTy(null, VOID);
    }

    public ExpTy transVar(Absyn.Var e) {
        ExpTy result;
        if (e instanceof Absyn.FieldVar fieldVar) {
          result = transVar(fieldVar);
      } else if (e instanceof Absyn.SimpleVar simpleVar) {
          result = transVar(simpleVar);
      } else if (e instanceof Absyn.SubscriptVar subscriptVar) {
          result = transVar(subscriptVar);
      } else {
          result = null;
          System.err.println("transVar error;" + (e == null ? "null" : e.getClass()));
      }
        return result;
    }

    public ExpTy transVar(Absyn.FieldVar e) {
        ExpTy varExp = transVar(e.var);

        if(!(varExp.ty.actual() instanceof Types.RECORD)) {
            error(e.pos, "Variable is not a record; transVar(FieldVar)");
            return new ExpTy(null, VOID);
        }

        Types.RECORD recordType = (Types.RECORD) varExp.ty.actual();

        for (Types.RECORD field = recordType; field != null; field = field.tail) {
            if (field.fieldName.equals(e.field)) {
                return new ExpTy(null, field.fieldType);
            }
        }

        error(e.pos, "Field not found; " + e.field);
        return new ExpTy(null, VOID);
    }



    public ExpTy transVar(Absyn.SimpleVar e) {
        Entry x = (Entry) env.venv.get(e.name);
        if (x instanceof VarEntry) {
            // VarEntry ent = (VarEntry)x;
            // return new ExpTy(null, ent.type);
        }
        error(0, "simplevar messed up");
        return new ExpTy(null, INT);
    }

    public ExpTy transVar(Absyn.SubscriptVar e) {
        ExpTy varExp = transVar(e.var);

        if(!(varExp.ty.actual() instanceof Types.ARRAY)) {
            error(e.pos, "Variable is not an array; transVar(SubscriptVar)");
            return new ExpTy(null, VOID);
        }

        ExpTy indExp = transExp(e.index);

        if(checkInt(indExp, e.index.pos) == null) {
            return new ExpTy(null, VOID);
        }

        return new ExpTy(null, (Types.ARRAY) varExp.ty.actual());
    }



}