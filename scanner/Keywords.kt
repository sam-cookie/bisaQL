package scanner

val keywords = mapOf(

    // var declaration
    "Paghimog" to TokenType.PAGHIMO,
    // "ug" to TokenType.UG,
    "bar" to TokenType.VAR,
    "nga" to TokenType.NGA,

    // re assignment
    "Usbi" to TokenType.USBI,
    "ang" to TokenType.ANG,
    "himuag" to TokenType.HIMUAG,

    //printing
    "Ipakita" to TokenType.PRINT,

    //if statement, do 
    "Kung" to TokenType.IF,
    "buhata" to TokenType.BUHATA,
    "Ugdi" to TokenType.ELSE,    

    //while
    "Samtang" to TokenType.WHILE,
    
    //function
    "Buhatag" to TokenType.FUNCTION,
    "balik" to TokenType.RETURN,
    "Tawagi" to TokenType.CALL,
    "kay" to TokenType.KAY,

    "undangi" to TokenType.BREAK,
    "padayoni" to TokenType.CONTINUE,
    "tinuod" to TokenType.TRUE,
    "atik" to TokenType.FALSE,
    "wala" to TokenType.NULL,
    "sumpayig" to TokenType.SUMPAY,
    "Sugod" to TokenType.SUGOD,
    "Tapos" to TokenType.TAPOS,

    //arithmetic
    "plusig" to TokenType.PLUS,
    "minusig" to TokenType.MINUS,
    "timesig" to TokenType.TIMES,
    "dividig" to TokenType.DIVIDE,
    // "nabilins" to TokenType.MODULO

    // assignment arithmetic
    "imodulog" to TokenType.MODULO,
    "dugangig" to TokenType.ADD_ASSIGN,
    "kuhaag" to TokenType.MINUS_ASSIGN,
    "piluag" to TokenType.TIMES_ASSIGN,
    "bahinag" to TokenType.DIVIDED_ASSIGN,

    // > < >= <=
    "mas" to TokenType.MAS,
    "dakos" to TokenType.DAKOS,
    "gamays" to TokenType.GAMAYS,
    "tupongs" to TokenType.TUPONG,
    // "or" to TokenType.OR,

    // ==, !=, =
    "mahimong" to TokenType.EQUALS,
    "parehas" to TokenType.EQUALTO,
    "dili" to TokenType.NOT,

    //logical and nd or
    "ug" to TokenType.AND,
    "o" to TokenType.OR

)
