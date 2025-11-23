HiliSaya Programming Language

Creator
Samantha Lodenn D. Lansoy
Chrystie Rae A. Sajorne

Language Overview
HiliSaya is a  beginner-friendly programming language that is a mix of both Hiligaynon and Bisaya languages. It’s designed for students, teachers, and professionals who speak both languages. It helps programming a lot more understandable and familiar as it uses words that are used most by the target users. 

Main Characteristics: 
Lower the barrier to learning programming by using local language keywords
Simple syntax
Supports the usual building blocks such as variables, functions, classes, basic control flow, and 
Dynamically typed 

Keywords
Variable (var) : declare a variable 
Function (func): define a function
Return (balik): return from a function
Print (gawas): printing output to console
If(kung): if condition
Else(ugdi): else condition
Elif(kungdi): elif condition
While(samtang): while loop
Break(untat): break a loop
Continue(padayon): continue to next iteration
True / false (tuod / atik) - Boolean literals
Null (waay) - no value
Sugod and Tapos - To start multiline 
End (humana) - end of line indicator


Operators
Arithmetic Operators
+ : Add 
- : Subtract
* : Multiply
/ : Divide
% : Modulo

Comparison Operators
> : Greater than
>= : Greater than or equal to	
< : Less than 
<= : Less than or equal to
= : Equals
== : Equal to
!= Not equal to

Logical Operators
&& : logical and
|| : logical or
! : logical not

Assignment Operators
= : assign
+= : add and assign
-= : subtract and assign
*= : multiply and assign
/= : divide and assign
%= : module and assign 








Literals
Number Literals
Represent the whole numbers or decimals
Example: 
42
-2.1
String Literals
Enclosed in double quotes (“ ”). Strings can contain letters, numbers, spaces, and symbols
	Example: 
		“Hello, world!”
Boolean Literals
Represent true values using local words
tuod = true
atik = false
Null Literals 
waay represents no value 
Array Literals 
Arrays are written using square brackets [] and the values inside it are separated by commas
	Example: 
		[1, 2, 3, 4]

Identifiers
Rules for Valid Identifiers
Identifiers (variable names, function names, etc) must only begin with either a lowercase(a-z) or uppercase letter(A-Z). It can contain numbers or underscores.
Identifiers cannot use reserved key words
Special characters (@,$,#, etc) are not allowed within identifiers, except for underscore(_).
Identifiers cannot contain whitespace

Comments
Line and block comments are supported
Syntax for line comments : // comment 
Syntax for block comments: /// comment ///
Syntax Style
Whitespace is not important, except that newlines terminates statements
Compound statements end “humana” is explicitly said
Blocks are created using indentation
Parentheses are used for grouping expressions and function calls 
Brackets are used for arrays 



// variable declaration
bar edad = 20.
bar pangalan = “sir ren”.

// function
func greet (name)
    gawas “hello, ” + name.

greet (“sir ren”).

// with return value
func add (a, b)
        balik a + b.

bar suma = add 6, 7.
gawas (suma).

// conditional
bar edad = 20.

sugod 
kung edad == 20
    gawas “welcome to your 20s!”.
kungdi edad >= 30
    gawas “pangasawa na oy”.
ugdi
    gawas “bata pa ka chuy”.
tapos.

// arrays and indexing
bar lista = [1, 2, 3, 4]
gawas lista[0]  // 1

// sample program
bar minimumAge = 18.

sugod
func userGreeting (name)
    gawas “hello, ” + name + “!”.
    checkValidity(18).
     tapos.

sugod
func checkValidity (userAge)
     sugod
    kung userAge >= 18
        gawas “pwede na sa dating app!”
    ugdi
        gawas “hawa diri sa dating app!”
        tapos.
tapos.
userGreeting (“sir ren”).


Design Rationale
Familiar vocabulary: using local-language keywords (like gawas and balik) reduces difficulty from understanding  and helps target users learn it faster
Simplicity: small set of keywords to make the language simple and use of newline and indention-based blocks to make programming clean 
Readability: allow optional semicolons and line-based statement termination so beginners are not tripped up by punctuation rules.
Extensibility: core syntax is minimal so future features (modules, types, standard library) can be added without breaking existing code.