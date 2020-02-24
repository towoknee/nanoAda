procedure PRIMES is 
	I, J : INTEGER;
	COUNTER : INTEGER;
	MODRESULT : INTEGER;
	begin 
	I := 1;
	while I <= 100 loop
		COUNTER := 0;
		J := 1;
		while J <= I loop
			MODRESULT := I mod J; 
			if MODRESULT = 0 then
				COUNTER := COUNTER + 1;
			endif;
			J := J + 1;
		end loop;
		if I = 100 then 
			write("0");
		elseif COUNTER = 2 then 
			write("1,");
		else
			write("0,");
		endif;
		I := I + 1;
	end loop;
end; 
