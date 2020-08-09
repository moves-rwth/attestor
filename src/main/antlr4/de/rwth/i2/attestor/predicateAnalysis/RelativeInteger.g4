grammar RelativeInteger;

expr
    : '(' Expr = expr ')'
    | Op = MINUS Expr = expr
    | Left = expr Op = PLUS Right = expr
    | Left = expr Op = MINUS Right = expr
    | INTEGER
    | VAR
    | INDEX
    | ASSIGN '[' Idx = INTEGER ']'
    ;

PLUS    : '+' ;
MINUS   : '-' ;
INTEGER : [0-9]+ ;
VAR     : '$VAR' ;
INDEX   : '$INDEX' ;
ASSIGN  : '$ASSIGN' ;
WS : [ \t\r\n]+ -> skip ;