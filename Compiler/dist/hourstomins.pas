PROGRAM HMINSEC
VAR 
  hours, convert_to , mins, secs, sixity
BEGIN 
READ(hours);
READ(convert_to); 
mins:= hours * sixity;
secs:= mins*sixity; 
WRITE(mins,secs)
END. 
 
