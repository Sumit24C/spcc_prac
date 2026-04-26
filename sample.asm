.model small
.stack
.data

MSG1 DB 10,13,"Hello World$"
MSG2 DB 10,13,"Welcome$"

.code
DISP MACRO XX
MOV AH,07H
LEA DX,XX
INT 21H
ENDM
.startup
DISP MSG1
DISP MSG2
.exit
end