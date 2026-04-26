%{
#include <stdio.h>

#define key 1
#define ID 2
#define num 3
#define symbol 4
%}

%%

(int|char|void)              { return key; }
[a-zA-Z_][a-zA-Z0-9_]*      { return ID; }
[0-9]+(\.[0-9]+)?           { return num; }
[=><;,"'\|?/]+              { return symbol; }
[ \t\n]+                    ;   /* ignore spaces */

%%

int main() {
    int ch;

    while ((ch = yylex())) {

        switch (ch) {

            case key:
                printf("%s : keyword\n", yytext);
                break;

            case ID:
                printf("%s : identifier\n", yytext);
                break;

            case num:
                printf("%s : number\n", yytext);
                break;

            case symbol:
                printf("%s : symbol\n", yytext);
                break;

            default:
                printf("%s : unknown\n", yytext);
        }
    }

    return 0;
}

int yywrap() {
    return 1;
}