.model small
.stack
.data

MSG1 DB 10,13,"Hello World$"
MSG2 DB 10,13,"Welcome$"

.code
.startup
MOV AH,07H
LEA DX,MSG1
INT 21H
MOV AH,07H
LEA DX,MSG2
INT 21H
.exit
end
