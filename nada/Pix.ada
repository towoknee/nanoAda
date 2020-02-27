procedure Pix is 
procedure Pi(X : INTEGER) is 
  I, J : INTEGER;
  COUNTER : INTEGER; 
  MODRESULT : INTEGER;
  begin

  I := 1;

  while I <= X loop 
    COUNTER := 0;
    J := 1; 
    while J <= I loop 
      MODRESULT := I mod J;
      if MODRESULT = 0 then
        COUNTER := COUNTER + 1;
      endif;
      J := J + 1; 
    end loop; 

    if COUNTER = 2 then
      if I = 2 then
        write("2");
      else
        write(",");
        write(I);
      endif;
    endif;
    
    I := I + 1;
  end loop;

  end;

begin
write("\r");
Pi(100);
write("\r");
end; 
