grammar SeparationLogic;

/** parser rules **/

// symbolic heap
heap : heapHead RULEDELIM heapBody;

// declaration of the free variables for a symbolic heap
heapHead : predicateSymbol '(' (variableDeclaration (',' variableDeclaration)* )? ')';

// the actual symbolic heap
heapBody : (variableDeclaration+ '|')? atom (SEP atom)*;

// system of inductive definitions
sid : sidRule+;

// rule for a single predicate definition
sidRule : sidRuleHead (RULEDELIM sidRuleBody)+ ';';

// left-hand side of a rule containing the predicate symbol and it's parameter declarations
sidRuleHead : predicateSymbol '(' freeVariableDeclaration (',' freeVariableDeclaration)* ')';

// right-hand side of a rule determining the formula and declaration of existential variables
sidRuleBody : (variableDeclaration '|')? spatial (SEP spatial)*;

// declaration of typed variables
freeVariableDeclaration : REDUCTION? variableDeclaration;
variableDeclaration : variable '{' type '}';
variable : VARPREFIX? ID (ID|NUM|US)* VARSUFFIX?;
type : (ID|NUM)+;

// formulas allowed in predicate definitions
spatial : pointer | predicateCall;

// formulas allowed in general symbolic heaps
atom : spatial | EMP | pure;

// points-to assertions
pointer : variable FIELDACCESS selector PTO (variable|NULL);
selector : (ID|NUM|US)+;

// pure formulas
pure : (variable|constant) EQ (variable|constant);
constant : NULL | NUM;

// predicate calls
predicateCall : predicateSymbol '(' parameter (',' parameter)* ')';
parameter : variable | NULL;
predicateSymbol : (ID|NUM)+;




/** lexer rules **/
NULL : 'null';
EMP : 'emp';
PTO : '->';
SEP : '*';
EQ : '=';
FIELDACCESS : '.';
RULEDELIM : '<=';
US : '_';
REDUCTION : '!';
VARPREFIX : '$' | '%' | '@';
VARSUFFIX : ':';
ID : [a-zA-Z]+;
NUM : [0-9]+;
WS : [ \t\r\n]+ -> skip;