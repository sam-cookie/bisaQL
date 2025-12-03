# Bisaya Quality Language or BisaQL

### Creator  
Samantha Lodenn D. Lansoy  
Chrystie Rae A. Sajorne  

### Language Overview     
Bisaya Quality Language or BisaQL is a  beginner-friendly programming language that is purely in Bisaya. The type of Bisaya this language uses is “Davao Bisaya,” making every line sound like how a Dabawenyo would talk. It’s designed for everyone who wants to learn Bisaya and those who already speak Bisaya. It helps programming become a lot more understandable and familiar as it uses words that are used by most target users.

### Main Characteristics: 
Lower the barrier to learning programming by using local language keywords
Simple syntax
Supports the usual building blocks such as variables, functions, basic control flow
Dynamically typed 

## BisaQL Syntax Guide
1. Variables  
To create a new variable, use the _Paghimog_ keyword.  
* Syntax: Paghimog bar [name] nga [value].  
* Example:  
  Paghimog bar edad nga 21.  
  Paghimog bar ngalan nga "Juan". 

**Re-assignment**  
To update or change the value of an existing variable, use _Usbi_.  
* Syntax: usbi ang [name] himuag [value].  
* Example:  
  Usbi ang edad himuag 22.  
  Usbi ang ngalan himuag "Pedro".  

2. Output  
To print a value to the screen, use _Ipakita_.  
* Syntax: ipakita ang [expression].   
* Example:  
  Ipakita ang "Maayong Buntag!".  
  Ipakita ang edad.  

3. Arithmetic   
HiliSaya uses descriptive words instead of symbols for basic math.  

| Operation | Keyword | Example | Result |
|---|---|---|---|
| Addition | plusig | 5 plusig 5 | 10 |
| Subtraction | minusig | 10 minusig 2 | 8 |
| Multiplication | timesig | 4 timesig 2 | 8 |
| Division | dibayig | 10 dibayig 2 | 5 |
| Modulo | imodulog |10 imodulog 3 | 1 |
| Combine Strings | sumpayig | "Hi" sumpayig " Po" | "Hi Po" |  

**Shortcut Math (Compound Assignment)**  
Quickly update a variable's value.  
| Keyword | Keyword | Example |
|---|---|---|
| dugangig | += | Usbi ang x dugangig 5. |
| kuhaag | -= | Usbi ang x kuhaag 2. |
| piluag | *= | Usbi ang x piluag 3. |
| bahinag | /= | Usbi ang x bahinag 2. |

4. Comparisons 
Used to compare two values. These return tinuod (true) or atik (false). 

| Keyword | Keyword | Example |  
|---|---|---|
| > | mas dakos | Ipakita ang 10 mas dakos 5. |
| < | mas gamays | Ipakita ang 5 mas gamays 10. |
| >= | mas dakos tupongs | Ipakita ang edad mas dakos tupongs 18. |
| <= | mas gamays tupongs | Ipakita ang presyo mas gamays tupongs 100. |
| == | parehas | Ipakita ang x parehas y. |
| != | dili parehas | Ipakita ang x dili parehas y. |  

5. Logical Operators   
Used to combine multiple conditions.  
* AND (ug)
  Example: Ipakita ang x parehas 1 ug y parehas 2.
  (Both must be true)
* OR (o)
  Example: Ipakita ang  x parehas 1 o x parehas 5.
  (At least one must be true)
* NOT (dili)
  Example: Ipakita ang dili tinuod.
  (Reverses the value)


If / Else (Kung / Ugdi)
Syntax:
Kung ang [condition] buhata
	[code to run if true]
ugdi
	[code to run if false]
tapos.
Example:
kung marka mas dakos tupongs 75 buhata
	ipakita ang "Pasar ka!".
ugdi
	ipakita ang "Hagbong.".
tapos.
While Loop (Samtang)
Syntax:
Samtang ang  [condition] buhata
	[code to loop]
tapos.

7. Data Types  
* Boolean: tinuod (true), atik (false)  
* Null: wala  

### Literals
*Number Literals*   
- Represent the whole numbers or decimals
	Example:  
	42  
	-2.1  

*String Literals*  
- Enclosed in double quotes (“ ”). Strings can contain letters, numbers, spaces, and symbols  
  Example: 
         “Hello, world!”  

*Boolean Literals*  
- Represent true values using local words  
  - tuod = true  
  - atik = false  

*Null Literals*  
- waay represents no value

*Array Literals*   
- Arrays are written using square brackets [] and the values inside it are separated by commas  
  Example:   
		[1, 2, 3, 4]  

### Identifiers
*Rules for Valid Identifiers*
- Identifiers (variable names, function names, etc) must only begin with either a lowercase(a-z) or uppercase letter(A-Z). It can contain numbers or underscores.
- Identifiers cannot use reserved key words
- Special characters (@,$,#, etc) are not allowed within identifiers, except for underscore(_).
- Identifiers cannot contain whitespace

### Comments
- Line and block comments are supported
- Syntax for line comments : // comment 
- Syntax for block comments: /// comment ///

### Syntax Style
- The way the code is structured is like how you would hear a Bisaya person talk
- Whitespace is not important, except that newlines terminates statements
- Blocks are created using sugod and tapos keyword
- Parentheses are used for grouping expressions and function calls 
- Brackets are used for arrays 

## Example code:

_Creating variables_  

	Paghimog bar ngalan nga "Juan".  
	Paghimog bar ihap nga 1.  

_Printing Strings/ Statements_  

	Ipakita ang "Maayong Buntag, " sumpayig ngalan.    
	Ipakita ang "Magsugod na ta og ihap:".   

_While Statement_  

	Samtang ang ihap mas gamays tupongs 5 buhata,    
		Ipakita ang "Numero: " plusig ihap.  

		kung ang ihap parehas 3 buhata  
			ipakita ang "   --> Naa na ta sa tunga!".  
		ugdi  
			ipakita ang "   Padayon...".  
		tapos.  

		usbi ang ihap dugangig 1.  
	tapos.  
	
	Ipakita ang "Nahuman na ang pag-ihap!".   


### Design Rationale
*Familiar vocabulary:* using local-language keywords (like gawas and balik) reduces difficulty from understanding and helps target users learn it faster  
*Simplicity:* small set of keywords to make the language simple and use of newline and indention-based blocks to make programming clean   
*Readability:* allow optional semicolons and line-based statement termination so beginners are not tripped up by punctuation rules.  
*Extensibility:* core syntax is minimal so future features (modules, types, standard library) can be added without breaking existing code.  
