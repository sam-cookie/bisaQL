package scanner

enum class TokenType {
    // single token and multicharacter tokens 
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    PLUS, MINUS, TIMES, DIVIDE, MODULO, EQUALS,
    COMMA, COLON, NOT, AND, OR,
    LESS_THAN_EQUAL, GREATER_THAN_EQUAL, LESS_THAN, GREATER_THAN, EQUALTO, NOT_EQUAL, DOUBLE_QUOTE,
    ADD_ASSIGN, MINUS_ASSIGN, TIMES_ASSIGN, DIVIDED_ASSIGN, MODULO_ASSIGN, LOGICAL_AND, LOGICAL_OR, PERIOD,

    // block identifiers 
    SUGOD, TAPOS,  

    // literals 
    IDENTIFIER, NUMBER, STRING,  

    MAS, DAKOS, GAMAYS, TUPONG,

    // keywords / sentence words
    PAGHIMO, BAR, NGA,           // var declaration
    USBI, ANG, HIMUAG,               // assignment
    IPAKITA,                          // print
    KUNG, BUHATA,                     // if
    UGDI,                             // else
    SAMTANG,                           // while
    GAMITON,                           // function
    BALIK,                             // return
    CALL, 

    VAR, FUNCTION, RETURN, PRINT, IF, ELSE, WHILE, BREAK, CONTINUE, TRUE, FALSE, NULL, SUMPAY, KAY,

    EOF
}
