grammar RelativeInteger;

expr
    : '(' Exp = expr ')'
    | INTEGER
    | INDEX
    | ASSIGN
    | Left = expr Op = PLUS Right = expr
    | Left = expr Op = MINUS Right = expr
    | expr MINUS Exp = expr
    ;

PLUS    : '+' ;
MINUS   : '-' ;
DIGIT   : [0-9] ;
INTEGER : DIGIT+ ;
INDEX   : '$INDEX' ;
ASSIGN  : '$ASSIGN' '[' INTEGER ']' ;
WS      : [ \t\r\n]+ -> skip ;