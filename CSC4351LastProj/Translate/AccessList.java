package Translate;

public class AccessList {
  public TranslateAccess head;
  public AccessList tail;
  
  AccessList(TranslateAccess h, AccessList t) {
    head = h;
    tail = t;
  }
}
