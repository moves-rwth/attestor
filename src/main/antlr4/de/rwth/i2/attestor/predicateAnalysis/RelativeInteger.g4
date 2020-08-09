grammar RelativeInteger;

expr
    : '(' Expr = expr ')'
    | Op = MINUS Expr = expr
    | Left = expr Op = PLUS Right = expr
    | Left = expr Op = MINUS Right = expr
    | Left = expr Op = LUB Right = expr
    | TOP
    | BOTTOM
    | INTEGER
    | VAR
    | INDEX
    | ASSIGN '[' Idx = INTEGER ']'
    ;

LUB     : 'U' ;
PLUS    : '+' ;
MINUS   : '-' ;
TOP     : '$TOP' ;
BOTTOM  : '$BOTTOM' ;
VAR     : '$VAR' ;
INDEX   : '$INDEX' ;
ASSIGN  : '$ASSIGN' ;
INTEGER : [0-9]+ ;
WS : [ \t\r\n]+ -> skip ;