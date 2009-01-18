package org.basex.query.xquery.func;

import static org.basex.util.Token.*;
import org.basex.BaseX;
import org.basex.query.xquery.XQException;
import org.basex.query.xquery.XQContext;
import org.basex.query.xquery.expr.Expr;
import org.basex.query.xquery.item.Dbl;
import org.basex.query.xquery.item.Item;
import org.basex.query.xquery.item.Itr;
import org.basex.query.xquery.item.QNm;
import org.basex.query.xquery.item.Str;
import org.basex.query.xquery.item.Type;
import org.basex.query.xquery.iter.Iter;

/**
 * Accessor functions.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Christian Gruen
 */
final class FNAcc extends Fun {
  @Override
  public Iter iter(final XQContext ctx, final Iter[] arg) throws XQException {
    final Iter iter = arg.length == 0 ? checkCtx(ctx) : arg[0];
 
    switch(func) {
      case POS:
        return Itr.get(ctx.pos).iter();
      case LAST:
        return Itr.get(ctx.size).iter();
      case STRING:
        Item it = iter.atomic(this, true);
        return it == null ? Str.ZERO.iter() : it.s() && !it.u() ? it.iter() :
          Str.get(it.str()).iter();
      case NUMBER:
        it = iter.next();
        return (it == null || iter.next() != null ? Dbl.NAN :
          number(it)).iter();
      case URIQNAME:
        it = iter.atomic(this, true);
        if(it == null) return Iter.EMPTY;
        return ((QNm) check(it, Type.QNM)).uri.iter();
      case STRLEN:
        return Itr.get(len(checkStr(iter))).iter();
      case NORM:
        return Str.get(norm(checkStr(iter))).iter();
      default:
        BaseX.notexpected(func); return null;
    }
  }

  @Override
  public Expr c(final XQContext ctx) {
    if(args.length == 0) return this;
    final Item it = args[0].i() ? (Item) args[0] : null;
    
    switch(func) {
      case STRING:
        return it != null && it.s() && !it.u() ? it : this;
      case NUMBER:
        return args[0].e() ? Dbl.NAN : it != null ? number(it) : this;
      default:
        return this;
    }
  }
  
  /**
   * Converts the specified item to a double.
   * @param it input item
   * @return double iterator
   */
  private Item number(final Item it) {
    if(it.type == Type.DBL) return it;

    try {
      if(it.type != Type.URI && (it.s() || it.n() || it.u()))
        return Dbl.get(it.dbl());
    } catch(final XQException e) { }
    return Dbl.get(Double.NaN);
  }

  @Override
  public boolean usesPos(final XQContext ctx) {
    return func == FunDef.POS || func == FunDef.LAST || super.usesPos(ctx);
  }
}
