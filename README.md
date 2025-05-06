# Project 5+6 😭😭😭

## [Final Demo Video](https://www.youtube.com/watch?v=-F_QB__Oo40)

### Group Members:  
- Darrius Anthony  
- Asher Borderlon  
- Jack Corson  
- Gustavo Coloma

### Contributions:  
- Darrius
  - Non-tested test.c, 
  - Worked on codegen
  - Regalloc
  - Parse
  - Debugging as many errors as possible
- Asher
  - Debugging, lots of troubleshooting
  - Grm.cup fixes and (attempts) at compilation of it
  - Compatibility in Semant.java
- Jack
  - Worked on translate file
  - Set up code environment for the group
  - Tried debugging Semant.Semant and getting all errors to go away and align with that we had in previous projects.
- Gustavo
  - Transfigured unCx and unEx to C compiler
  - Mass Troubleshooting errors/Debugging
  - Worked on IfThenElseExp


# Details  

- *(See video link at top of README, email jcorso1@lsu.edu if having troubles accessing)*
- Had issues with previous projects
- These issues compacted, making subsequent projects more difficult
- Culminated in spending more time fixing previous issues than working on the actual project
  - See video for more details :)
- Error below with our Grm.cup file that made us pivot to studying for the exam
  - We were unable to fix the issues with recompiling this file, so we were unable to debug our actual codebase further

### Error that shed tears 

```
Stack trace:
java.lang.VerifyError: Bad type on operand stack
Exception Details:
  Location:
    Parse/CUP$Grm$actions.CUP$Grm$do_action(ILjava_cup/runtime/lr_parser;Ljava/util/Stack;I)Ljava_cup/runtime/Symbol; @4190: invokespecial
  Reason:
    Type 'Absyn/Type' (current frame, stack[3]) is not assignable to 'Absyn/Decl'
  Current Frame:
    bci: @4190
    flags: { }
    locals: { 'Parse/CUP$Grm$actions', integer, 'java_cup/runtime/lr_parser', 'java/util/Stack', integer, top, null, integer, integer, 'Absyn/Type' }
    stack: { uninitialized 4183, uninitialized 4183, integer, 'Absyn/Type' }
```

#### *re:Have pity on our souls, please* 🙏